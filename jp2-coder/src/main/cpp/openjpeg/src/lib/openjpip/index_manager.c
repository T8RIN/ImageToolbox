/*
 * $Id$
 *
 * Copyright (c) 2002-2014, Universite catholique de Louvain (UCL), Belgium
 * Copyright (c) 2002-2014, Professor Benoit Macq
 * Copyright (c) 2010-2011, Kaori Hagihara
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS `AS IS'
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <string.h>

#include "opj_inttypes.h"
#include "index_manager.h"
#include "box_manager.h"
#include "manfbox_manager.h"
#include "mhixbox_manager.h"
#include "codestream_manager.h"
#include "marker_manager.h"
#include "faixbox_manager.h"
#include "boxheader_manager.h"

#ifdef SERVER
#include "fcgi_stdio.h"
#define logstream FCGI_stdout
#else
#define FCGI_stdout stdout
#define FCGI_stderr stderr
#define logstream stderr
#endif /*SERVER*/

/**
 * chekc JP2 box indexing
 *
 * @param[in] toplev_boxlist top level box list
 * @return                   if correct (true) or wrong (false)
 */
OPJ_BOOL check_JP2boxidx(boxlist_param_t *toplev_boxlist);

/**
 * set code index parameters (parse cidx box)
 * Annex I
 *
 * @param[in]  cidx_box pointer to the reference cidx_box
 * @param[out] codeidx  pointer to index parameters
 * @return              if succeeded (true) or failed (false)
 */
OPJ_BOOL set_cidxdata(box_param_t *cidx_box, index_param_t *codeidx);

index_param_t * parse_jp2file(int fd)
{
    index_param_t *jp2idx;
    box_param_t *cidx;
    metadatalist_param_t *metadatalist;
    boxlist_param_t *toplev_boxlist;
    Byte8_t filesize;

    if (!(filesize = (Byte8_t)get_filesize(fd))) {
        return NULL;
    }

    if (!(toplev_boxlist = get_boxstructure(fd, 0, filesize))) {
        fprintf(FCGI_stderr, "Error: Not correctl JP2 format\n");
        return NULL;
    }

    if (!check_JP2boxidx(toplev_boxlist)) {
        fprintf(FCGI_stderr, "Index format not supported\n");
        delete_boxlist(&toplev_boxlist);
        return NULL;
    }

    if (!(cidx = search_box("cidx", toplev_boxlist))) {
        fprintf(FCGI_stderr, "Box cidx not found\n");
        delete_boxlist(&toplev_boxlist);
        return NULL;
    }

    jp2idx = (index_param_t *)opj_malloc(sizeof(index_param_t));

    if (!set_cidxdata(cidx, jp2idx)) {
        fprintf(FCGI_stderr, "Error: Not correctl format in cidx box\n");
        opj_free(jp2idx);
        delete_boxlist(&toplev_boxlist);
        return NULL;
    }
    delete_boxlist(&toplev_boxlist);

    metadatalist = const_metadatalist(fd);
    jp2idx->metadatalist = metadatalist;

#ifndef SERVER
    fprintf(logstream, "local log: code index created\n");
#endif

    return jp2idx;
}

void print_index(index_param_t index)
{
    int i;

    fprintf(logstream, "index info:\n");
    fprintf(logstream, "\tCodestream  Offset: %#" PRIx64 "\n", index.offset);
    fprintf(logstream, "\t            Length: %#" PRIx64 "\n", index.length);
    fprintf(logstream, "\tMain header Length: %#" PRIx64 "\n", index.mhead_length);

    print_SIZ(index.SIZ);
    print_COD(index.COD);

    fprintf(logstream, "Tile part information: \n");
    print_faixbox(index.tilepart);

    fprintf(logstream, "Tile header information: \n");
    for (i = 0; i < (int)(index.SIZ.XTnum * index.SIZ.YTnum); i++) {
        print_mhixbox(index.tileheader[i]);
    }

    fprintf(logstream, "Precinct packet information: \n");
    for (i = 0; i < index.SIZ.Csiz; i++) {
        fprintf(logstream, "Component %d\n", i);
        print_faixbox(index.precpacket[i]);
    }

    print_allmetadata(index.metadatalist);
}

void print_SIZ(SIZmarker_param_t SIZ)
{
    int i;

    fprintf(logstream, "\tImage and Tile SIZ parameters\n");
    fprintf(logstream, "\t              Rsiz: %#x\n", SIZ.Rsiz);
    fprintf(logstream, "\t        Xsiz, Ysiz: (%d,%d) = (%#x, %#x)\n", SIZ.Xsiz,
            SIZ.Ysiz, SIZ.Xsiz, SIZ.Ysiz);
    fprintf(logstream, "\t      XOsiz, YOsiz: (%d,%d) = (%#x, %#x)\n", SIZ.XOsiz,
            SIZ.YOsiz, SIZ.XOsiz, SIZ.YOsiz);
    fprintf(logstream, "\t      XTsiz, YTsiz: (%d,%d) = (%#x, %#x)\n", SIZ.XTsiz,
            SIZ.YTsiz, SIZ.XTsiz, SIZ.YTsiz);
    fprintf(logstream, "\t    XTOsiz, YTOsiz: (%d,%d) = (%#x, %#x)\n", SIZ.XTOsiz,
            SIZ.YTOsiz, SIZ.XTOsiz, SIZ.YTOsiz);
    fprintf(logstream, "\t    XTnum, YTnum: (%d,%d)\n", SIZ.XTnum, SIZ.YTnum);
    fprintf(logstream, "\t Num of Components: %d\n", SIZ.Csiz);

    for (i = 0; i < SIZ.Csiz; i++) {
        fprintf(logstream,
                "\t[%d] (Ssiz, XRsiz, YRsiz): (%d, %d, %d) = (%#x, %#x, %#x)\n", i, SIZ.Ssiz[i],
                SIZ.XRsiz[i], SIZ.YRsiz[i], SIZ.Ssiz[i], SIZ.XRsiz[i], SIZ.YRsiz[i]);
    }
}

void print_COD(CODmarker_param_t COD)
{
    int i;

    fprintf(logstream, "\tCoding style default COD parameters\n");
    fprintf(logstream,
            "\t Progression order: %d [ LRCP=0, RLCP=1, RPCL=2, PCRL=3, CPRL=4]\n",
            COD.prog_order);
    fprintf(logstream, "\t     Num of layers: %d\n", COD.numOflayers);
    fprintf(logstream, "\t Decomposition lvl: %d\n", COD.numOfdecomp);

    for (i = 0; i <= ((COD.Scod & 0x01) ? COD.numOfdecomp : 0); i++) {
        fprintf(logstream, "\t  [%d] XPsiz, YPsiz: (%d,%d) = (%#x, %#x)\n", i,
                COD.XPsiz[i], COD.YPsiz[i], COD.XPsiz[i], COD.YPsiz[i]);
    }
}

void delete_index(index_param_t **index)
{
    int i;

    delete_metadatalist(&((*index)->metadatalist));

    delete_COD((*index)->COD);

    delete_faixbox(&((*index)->tilepart));

    for (i = 0; i < (int)((*index)->SIZ.XTnum * (*index)->SIZ.YTnum); i++) {
        delete_mhixbox(&((*index)->tileheader[i]));
    }
    opj_free((*index)->tileheader);

    for (i = 0; i < (*index)->SIZ.Csiz; i++) {
        delete_faixbox(&((*index)->precpacket[i]));
    }
    opj_free((*index)->precpacket);

    opj_free(*index);
}

void delete_COD(CODmarker_param_t COD)
{
    if (COD.XPsiz) {
        opj_free(COD.XPsiz);
    }
    if (COD.YPsiz) {
        opj_free(COD.YPsiz);
    }
}

OPJ_BOOL check_JP2boxidx(boxlist_param_t *toplev_boxlist)
{
    box_param_t *iptr, *fidx, *prxy;
    box_param_t *cidx, *jp2c;
    Byte8_t off;
    Byte8_t len;
    int pos;
    Byte8_t ooff;
    boxheader_param_t *obh;
    Byte_t ni;
    Byte8_t ioff;
    boxheader_param_t *ibh;

    iptr = search_box("iptr", toplev_boxlist);
    fidx = search_box("fidx", toplev_boxlist);
    cidx = search_box("cidx", toplev_boxlist);
    jp2c = search_box("jp2c", toplev_boxlist);
    prxy = gene_childboxbyType(fidx, 0, "prxy");

    off = fetch_DBox8bytebigendian(iptr, 0);
    if (off != (Byte8_t)fidx->offset) {
        fprintf(FCGI_stderr,
                "Reference File Index box offset in Index Finder box not correct\n");
    }

    len = fetch_DBox8bytebigendian(iptr, 8);
    if (len != fidx->length) {
        fprintf(FCGI_stderr,
                "Reference File Index box length in Index Finder box not correct\n");
    }

    pos = 0;
    ooff = fetch_DBox8bytebigendian(prxy, pos);
    if (ooff != (Byte8_t)jp2c->offset) {
        fprintf(FCGI_stderr, "Reference jp2c offset in prxy box not correct\n");
    }
    pos += 8;

    obh = gene_childboxheader(prxy, pos);
    if (obh->length != jp2c->length || strncmp(obh->type, "jp2c", 4) != 0) {
        fprintf(FCGI_stderr, "Reference jp2c header in prxy box not correct\n");
    }
    pos += obh->headlen;
    opj_free(obh);

    ni = fetch_DBox1byte(prxy, pos);
    if (ni != 1) {
        fprintf(FCGI_stderr, "Multiple indexes not supported\n");
        opj_free(prxy);
        return OPJ_FALSE;
    }
    pos += 1;

    ioff = fetch_DBox8bytebigendian(prxy, pos);
    if (ioff != (Byte8_t)cidx->offset) {
        fprintf(FCGI_stderr, "Reference cidx offset in prxy box not correct\n");
    }
    pos += 8;

    ibh = gene_childboxheader(prxy, pos);
    if (ibh->length != cidx->length || strncmp(ibh->type, "cidx", 4) != 0) {
        fprintf(FCGI_stderr, "Reference cidx header in prxy box not correct\n");
    }
    pos += ibh->headlen;
    opj_free(ibh);

    opj_free(prxy);

    return OPJ_TRUE;
}

/**
 * set code index parameters from cptr box
 * I.3.2.2 Codestream Finder box
 *
 * @param[in]  cidx_box pointer to the reference cidx_box
 * @param[out] jp2idx   pointer to index parameters
 * @return              if succeeded (true) or failed (false)
 */
OPJ_BOOL set_cptrdata(box_param_t *cidx_box, index_param_t *jp2idx);

/**
 * set code index parameters from mhix box for main header
 * I.3.2.4.3 Header Index Table box
 *
 * @param[in]  cidx_box   pointer to the reference cidx_box
 * @param[in]  codestream codestream parameters
 * @param[out] jp2idx     pointer to index parameters
 * @return                if succeeded (true) or failed (false)
 */
OPJ_BOOL set_mainmhixdata(box_param_t *cidx_box, codestream_param_t codestream,
                          index_param_t *jp2idx);

/**
 * set code index parameters from tpix box
 * I.3.2.4.4 Tile-part Index Table box
 *
 * @param[in]  cidx_box   pointer to the reference cidx_box
 * @param[out] jp2idx     pointer to index parameters
 * @return                if succeeded (true) or failed (false)
 */
OPJ_BOOL set_tpixdata(box_param_t *cidx_box, index_param_t *jp2idx);

/**
 * set code index parameters from thix box
 * I.3.2.4.5 Tile Header Index Table box
 *
 * @param[in]  cidx_box   pointer to the reference cidx_box
 * @param[out] jp2idx     pointer to index parameters
 * @return                if succeeded (true) or failed (false)
 */
OPJ_BOOL set_thixdata(box_param_t *cidx_box, index_param_t *jp2idx);

/**
 * set code index parameters from ppix box
 * I.3.2.4.6 Precinct Packet Index Table box
 *
 * @param[in]  cidx_box   pointer to the reference cidx_box
 * @param[out] jp2idx     pointer to index parameters
 * @return                if succeeded (true) or failed (false)
 */
OPJ_BOOL set_ppixdata(box_param_t *cidx_box, index_param_t *jp2idx);

OPJ_BOOL set_cidxdata(box_param_t *cidx_box, index_param_t *jp2idx)
{
    box_param_t *manf_box;
    manfbox_param_t *manf;
    codestream_param_t codestream;

    set_cptrdata(cidx_box, jp2idx);

    codestream = set_codestream(cidx_box->fd, jp2idx->offset, jp2idx->length);

    manf_box = gene_boxbyType(cidx_box->fd, get_DBoxoff(cidx_box),
                              get_DBoxlen(cidx_box), "manf");
    manf = gene_manfbox(manf_box);

    if (!search_boxheader("mhix", manf)) {
        fprintf(FCGI_stderr, "Error: mhix box not present in manfbox\n");
        opj_free(jp2idx);
        delete_manfbox(&manf);
        return OPJ_FALSE;
    }
    set_mainmhixdata(cidx_box, codestream, jp2idx);

    if (!search_boxheader("tpix", manf)) {
        fprintf(FCGI_stderr, "Error: tpix box not present in manfbox\n");
        opj_free(jp2idx);
        delete_manfbox(&manf);
        return OPJ_FALSE;
    }
    set_tpixdata(cidx_box, jp2idx);

    if (!search_boxheader("thix", manf)) {
        fprintf(FCGI_stderr, "Error: thix box not present in manfbox\n");
        opj_free(jp2idx);
        delete_manfbox(&manf);
        return OPJ_FALSE;
    }
    set_thixdata(cidx_box, jp2idx);

    if (!search_boxheader("ppix", manf)) {
        fprintf(FCGI_stderr, "Error: ppix box not present in manfbox\n");
        opj_free(jp2idx);
        delete_manfbox(&manf);
        return OPJ_FALSE;
    }
    set_ppixdata(cidx_box, jp2idx);

    delete_manfbox(&manf);
    opj_free(manf_box);

    return OPJ_TRUE;
}

OPJ_BOOL set_cptrdata(box_param_t *cidx_box, index_param_t *jp2idx)
{
    box_param_t *box;   /**< cptr box*/
    Byte2_t dr, cont;

    if (!(box = gene_boxbyType(cidx_box->fd, get_DBoxoff(cidx_box),
                               get_DBoxlen(cidx_box), "cptr"))) {
        return OPJ_FALSE;
    }

    /* DR: Data Reference. */
    /* If 0, the codestream or its Fragment Table box exists in the current file*/
    if ((dr = fetch_DBox2bytebigendian(box, 0))) {
        fprintf(FCGI_stderr, "Error: Codestream not present in current file\n");
        opj_free(box);
        return OPJ_FALSE;
    }

    /* CONT: Container Type*/
    /* If 0, the entire codestream appears as a contiguous range of*/
    /* bytes within its file or resource.*/
    if ((cont = fetch_DBox2bytebigendian(box, 2))) {
        fprintf(FCGI_stderr, "Error: Can't cope with fragmented codestreams yet\n");
        opj_free(box);
        return OPJ_FALSE;
    }

    jp2idx->offset = (OPJ_OFF_T)fetch_DBox8bytebigendian(box, 4);
    jp2idx->length = fetch_DBox8bytebigendian(box, 12);

    opj_free(box);

    return OPJ_TRUE;
}


/**
 * set SIZ marker information
 * A.5 Fixed information marker segment
 * A.5.1 Image and tile size (SIZ)
 *
 * @param[in]  sizmkidx   pointer to SIZ marker index in mhix box
 * @param[in]  codestream codestream parameters
 * @param[out] SIZ        SIZ marker parameters pointer
 * @return                if succeeded (true) or failed (false)
 */
OPJ_BOOL set_SIZmkrdata(markeridx_param_t *sizmkidx,
                        codestream_param_t codestream, SIZmarker_param_t *SIZ);

/**
 * set code index parameters from COD marker in codestream
 * A.6 Functional marker segments
 * A.6.1 Coding style default (COD)
 *
 * @param[in]  codmkidx   pointer to COD marker index in mhix box
 * @param[in]  codestream codestream parameters
 * @param[out] COD        COD marker parameters pointer
 * @return                if succeeded (true) or failed (false)
 */
OPJ_BOOL set_CODmkrdata(markeridx_param_t *codmkidx,
                        codestream_param_t codestream, CODmarker_param_t *COD);

OPJ_BOOL set_mainmhixdata(box_param_t *cidx_box, codestream_param_t codestream,
                          index_param_t *jp2idx)
{
    box_param_t *mhix_box;
    mhixbox_param_t *mhix;
    markeridx_param_t *sizmkidx;
    markeridx_param_t *codmkidx;

    if (!(mhix_box = gene_boxbyType(cidx_box->fd, get_DBoxoff(cidx_box),
                                    get_DBoxlen(cidx_box), "mhix"))) {
        return OPJ_FALSE;
    }

    jp2idx->mhead_length = fetch_DBox8bytebigendian(mhix_box, 0);

    mhix = gene_mhixbox(mhix_box);
    opj_free(mhix_box);

    sizmkidx = search_markeridx(0xff51, mhix);
    set_SIZmkrdata(sizmkidx, codestream, &(jp2idx->SIZ));

    codmkidx = search_markeridx(0xff52, mhix);
    set_CODmkrdata(codmkidx, codestream, &(jp2idx->COD));

    delete_mhixbox(&mhix);

    return OPJ_TRUE;
}

OPJ_BOOL set_tpixdata(box_param_t *cidx_box, index_param_t *jp2idx)
{
    box_param_t *tpix_box;   /**< tpix box*/
    box_param_t *faix_box;   /**< faix box*/

    if (!(tpix_box = gene_boxbyType(cidx_box->fd, get_DBoxoff(cidx_box),
                                    get_DBoxlen(cidx_box), "tpix"))) {
        fprintf(FCGI_stderr, "Error: tpix box not present in cidx box\n");
        return OPJ_FALSE;
    }

    if (!(faix_box = gene_boxbyType(tpix_box->fd, get_DBoxoff(tpix_box),
                                    get_DBoxlen(tpix_box), "faix"))) {
        fprintf(FCGI_stderr, "Error: faix box not present in tpix box\n");
        opj_free(tpix_box);
        return OPJ_FALSE;
    }

    jp2idx->tilepart = gene_faixbox(faix_box);

    opj_free(tpix_box);
    opj_free(faix_box);

    return OPJ_TRUE;
}

OPJ_BOOL set_thixdata(box_param_t *cidx_box, index_param_t *jp2idx)
{
    box_param_t *thix_box, *manf_box, *mhix_box;
    manfbox_param_t *manf;
    boxheader_param_t *ptr;
    mhixbox_param_t *mhix;
    Byte8_t pos;
    OPJ_OFF_T mhixseqoff;
    Byte2_t tile_no;

    if (!(thix_box = gene_boxbyType(cidx_box->fd, get_DBoxoff(cidx_box),
                                    get_DBoxlen(cidx_box), "thix"))) {
        fprintf(FCGI_stderr, "Error: thix box not present in cidx box\n");
        return OPJ_FALSE;
    }

    if (!(manf_box = gene_boxbyType(thix_box->fd, get_DBoxoff(thix_box),
                                    get_DBoxlen(thix_box), "manf"))) {
        fprintf(FCGI_stderr, "Error: manf box not present in thix box\n");
        opj_free(thix_box);
        return OPJ_FALSE;
    }

    manf = gene_manfbox(manf_box);
    ptr = manf->first;
    mhixseqoff = manf_box->offset + (OPJ_OFF_T)manf_box->length;
    pos = 0;
    tile_no = 0;
    jp2idx->tileheader = (mhixbox_param_t **)opj_malloc(jp2idx->SIZ.XTnum *
                         jp2idx->SIZ.YTnum * sizeof(mhixbox_param_t *));

    while (ptr) {
        if (!(mhix_box = gene_boxbyType(thix_box->fd, mhixseqoff + (OPJ_OFF_T)pos,
                                        get_DBoxlen(thix_box) - manf_box->length - pos, "mhix"))) {
            fprintf(FCGI_stderr, "Error: mhix box not present in thix box\n");
            delete_manfbox(&manf);
            opj_free(manf_box);
            opj_free(thix_box);
            return OPJ_FALSE;
        }
        mhix = gene_mhixbox(mhix_box);

        pos += mhix_box->length;
        ptr = ptr->next;

        opj_free(mhix_box);
        jp2idx->tileheader[tile_no++] = mhix;
    }

    delete_manfbox(&manf);
    opj_free(manf_box);
    opj_free(thix_box);

    return OPJ_TRUE;
}

OPJ_BOOL set_ppixdata(box_param_t *cidx_box, index_param_t *jp2idx)
{
    box_param_t *ppix_box, *faix_box, *manf_box;
    manfbox_param_t *manf;     /**< manf*/
    boxheader_param_t *bh;     /**< box headers*/
    faixbox_param_t *faix;     /**< faix*/
    OPJ_OFF_T inbox_offset;
    int comp_idx;

    if (!(ppix_box = gene_boxbyType(cidx_box->fd, get_DBoxoff(cidx_box),
                                    get_DBoxlen(cidx_box), "ppix"))) {
        fprintf(FCGI_stderr, "Error: ppix box not present in cidx box\n");
        return OPJ_FALSE;
    }

    inbox_offset = get_DBoxoff(ppix_box);
    if (!(manf_box = gene_boxbyType(ppix_box->fd, inbox_offset,
                                    get_DBoxlen(ppix_box), "manf"))) {
        fprintf(FCGI_stderr, "Error: manf box not present in ppix box\n");
        opj_free(ppix_box);
        return OPJ_FALSE;
    }

    opj_free(ppix_box);

    manf = gene_manfbox(manf_box);
    bh = search_boxheader("faix", manf);
    inbox_offset = manf_box->offset + (OPJ_OFF_T)manf_box->length;

    opj_free(manf_box);

    jp2idx->precpacket = (faixbox_param_t **)opj_malloc(jp2idx->SIZ.Csiz * sizeof(
                             faixbox_param_t *));

    for (comp_idx = 0; bh != NULL; bh = bh->next, comp_idx++) {
        if (jp2idx->SIZ.Csiz <= comp_idx) {
            fprintf(FCGI_stderr,
                    "Error: num of faix boxes is not identical to num of components in ppix box\n");
            delete_manfbox(&manf);
            return OPJ_FALSE;
        }

        if (!(faix_box = gene_boxbyOffset(cidx_box->fd, inbox_offset))) {
            fprintf(FCGI_stderr, "Error: faix box not present in ppix box\n");
            delete_manfbox(&manf);
            return OPJ_FALSE;
        }

        faix = gene_faixbox(faix_box);
        jp2idx->precpacket[comp_idx] = faix;

        inbox_offset = faix_box->offset + (OPJ_OFF_T)faix_box->length;
        opj_free(faix_box);
    }

    delete_manfbox(&manf);

    return OPJ_TRUE;
}

OPJ_BOOL set_SIZmkrdata(markeridx_param_t *sizmkidx,
                        codestream_param_t codestream, SIZmarker_param_t *SIZ)
{
    marker_param_t sizmkr;
    int i;

    sizmkr = set_marker(codestream, sizmkidx->code, sizmkidx->offset,
                        sizmkidx->length);

    SIZ->Lsiz = fetch_marker2bytebigendian(sizmkr, 0);

    if (sizmkidx->length != SIZ->Lsiz) {
        fprintf(FCGI_stderr, "Error: marker %#x index is not correct\n",
                sizmkidx->code);
        return OPJ_FALSE;
    }

    SIZ->Rsiz   = fetch_marker2bytebigendian(sizmkr, 2);
    SIZ->Xsiz   = fetch_marker4bytebigendian(sizmkr, 4);
    SIZ->Ysiz   = fetch_marker4bytebigendian(sizmkr, 8);
    SIZ->XOsiz  = fetch_marker4bytebigendian(sizmkr, 12);
    SIZ->YOsiz  = fetch_marker4bytebigendian(sizmkr, 16);
    SIZ->XTsiz  = fetch_marker4bytebigendian(sizmkr, 20);
    SIZ->YTsiz  = fetch_marker4bytebigendian(sizmkr, 24);
    SIZ->XTOsiz = fetch_marker4bytebigendian(sizmkr, 28);
    SIZ->YTOsiz = fetch_marker4bytebigendian(sizmkr, 32);
    SIZ->Csiz   = fetch_marker2bytebigendian(sizmkr, 36);

    SIZ->XTnum  = (SIZ->Xsiz - SIZ->XTOsiz + SIZ->XTsiz - 1) / SIZ->XTsiz;
    SIZ->YTnum  = (SIZ->Ysiz - SIZ->YTOsiz + SIZ->YTsiz - 1) / SIZ->YTsiz;

    for (i = 0; i < (int)SIZ->Csiz; i++) {
        SIZ->Ssiz[i]  = fetch_marker1byte(sizmkr, 38 + i * 3);
        SIZ->XRsiz[i] = fetch_marker1byte(sizmkr, 39 + i * 3);
        SIZ->YRsiz[i] = fetch_marker1byte(sizmkr, 40 + i * 3);
    }
    return OPJ_TRUE;
}

OPJ_BOOL set_CODmkrdata(markeridx_param_t *codmkidx,
                        codestream_param_t codestream, CODmarker_param_t *COD)
{
    marker_param_t codmkr;
    int i;

    codmkr = set_marker(codestream, codmkidx->code, codmkidx->offset,
                        codmkidx->length);

    COD->Lcod = fetch_marker2bytebigendian(codmkr, 0);

    if (codmkidx->length != COD->Lcod) {
        fprintf(FCGI_stderr, "Error: marker %#x index is not correct\n",
                codmkidx->code);
        return OPJ_FALSE;
    }

    COD->Scod   = fetch_marker1byte(codmkr, 2);
    COD->prog_order  = fetch_marker1byte(codmkr, 3);
    COD->numOflayers = fetch_marker2bytebigendian(codmkr, 4);
    COD->numOfdecomp = fetch_marker1byte(codmkr, 7);

    if (COD->Scod & 0x01) {
        COD->XPsiz = (Byte4_t *)opj_malloc((OPJ_SIZE_T)(COD->numOfdecomp + 1) * sizeof(
                                               Byte4_t));
        COD->YPsiz = (Byte4_t *)opj_malloc((OPJ_SIZE_T)(COD->numOfdecomp + 1) * sizeof(
                                               Byte4_t));

        for (i = 0; i <= COD->numOfdecomp; i++) {
            /*precinct size*/
            COD->XPsiz[i] = (Byte2_t)pow(2, fetch_marker1byte(codmkr, 12 + i) & 0x0F);
            COD->YPsiz[i] = (Byte2_t)pow(2, (fetch_marker1byte(codmkr,
                                             12 + i) & 0xF0) >> 4);
        }
    } else {
        COD->XPsiz = (Byte4_t *)opj_malloc(sizeof(Byte4_t));
        COD->YPsiz = (Byte4_t *)opj_malloc(sizeof(Byte4_t));

        COD->XPsiz[0] = COD->YPsiz[0] = 1 << 15; /* pow(2,15); */
    }
    return OPJ_TRUE;
}


/* very very generic name see NOMINMAX */
#ifdef min
#undef min
#endif
#ifdef max
#undef max
#endif
Byte4_t max(Byte4_t n1, Byte4_t n2);
Byte4_t min(Byte4_t n1, Byte4_t n2);

range_param_t get_tile_range(Byte4_t Osiz, Byte4_t siz, Byte4_t TOsiz,
                             Byte4_t Tsiz, Byte4_t tile_XYid, int level);

range_param_t get_tile_Xrange(SIZmarker_param_t SIZ, Byte4_t tile_id, int level)
{
    return get_tile_range(SIZ.XOsiz, SIZ.Xsiz, SIZ.XTOsiz, SIZ.XTsiz,
                          tile_id % SIZ.XTnum, level);
}

range_param_t get_tile_Yrange(SIZmarker_param_t SIZ, Byte4_t tile_id, int level)
{
    return get_tile_range(SIZ.YOsiz, SIZ.Ysiz, SIZ.YTOsiz, SIZ.YTsiz,
                          tile_id / SIZ.XTnum, level);
}

range_param_t get_tile_range(Byte4_t Osiz, Byte4_t siz, Byte4_t TOsiz,
                             Byte4_t Tsiz, Byte4_t tile_XYid, int level)
{
    range_param_t range;
    int n;

    range.minvalue = max(Osiz, TOsiz + tile_XYid * Tsiz);
    range.maxvalue = min(siz,  TOsiz + (tile_XYid + 1) * Tsiz);

    for (n = 0; n < level; n++) {
        range.minvalue = (Byte4_t)ceil(range.minvalue / 2.0);
        range.maxvalue = (Byte4_t)ceil(range.maxvalue / 2.0);
    }
    return range;
}

Byte4_t get_tile_XSiz(SIZmarker_param_t SIZ, Byte4_t tile_id, int level)
{
    range_param_t tile_Xrange;

    tile_Xrange = get_tile_Xrange(SIZ, tile_id, level);
    return tile_Xrange.maxvalue - tile_Xrange.minvalue;
}

Byte4_t get_tile_YSiz(SIZmarker_param_t SIZ, Byte4_t tile_id, int level)
{
    range_param_t tile_Yrange;

    tile_Yrange = get_tile_Yrange(SIZ, tile_id, level);
    return tile_Yrange.maxvalue - tile_Yrange.minvalue;
}

/* TODO: what is this code doing ? will all compiler be able to optimize the following ? */
Byte4_t max(Byte4_t n1, Byte4_t n2)
{
    if (n1 < n2) {
        return n2;
    } else {
        return n1;
    }
}

Byte4_t min(Byte4_t n1, Byte4_t n2)
{
    if (n1 < n2) {
        return n1;
    } else {
        return n2;
    }
}

OPJ_BOOL isJPTfeasible(index_param_t index)
{
    if (1 < get_nmax(index.tilepart)) {
        return OPJ_TRUE;
    } else {
        return OPJ_FALSE;
    }
}
