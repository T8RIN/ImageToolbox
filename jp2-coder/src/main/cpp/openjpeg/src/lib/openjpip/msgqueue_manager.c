/*
 * $Id$
 *
 * Copyright (c) 2002-2014, Universite catholique de Louvain (UCL), Belgium
 * Copyright (c) 2002-2014, Professor Benoit Macq
 * Copyright (c) 2010-2011, Kaori Hagihara
 * Copyright (c) 2011,      Lucian Corlaciu, GSoC
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
#include <sys/types.h>
#include <sys/stat.h>
#include <string.h>
#include <ctype.h>
#include <assert.h>
#include <limits.h>
#ifdef _WIN32
#include <io.h>
#else
#include <unistd.h>
#endif
#include "msgqueue_manager.h"
#include "metadata_manager.h"
#include "index_manager.h"
#include "opj_inttypes.h"

#ifdef SERVER
#include "fcgi_stdio.h"
#define logstream FCGI_stdout
#else
#define FCGI_stdout stdout
#define FCGI_stderr stderr
#define logstream stderr
#endif /*SERVER*/

msgqueue_param_t * gene_msgqueue(OPJ_BOOL stateless,
                                 cachemodel_param_t *cachemodel)
{
    msgqueue_param_t *msgqueue;

    msgqueue = (msgqueue_param_t *)opj_malloc(sizeof(msgqueue_param_t));

    msgqueue->first = NULL;
    msgqueue->last  = NULL;

    msgqueue->stateless = stateless;
    msgqueue->cachemodel = cachemodel;

    return msgqueue;
}

void delete_msgqueue(msgqueue_param_t **msgqueue)
{
    message_param_t *ptr, *next;

    if (!(*msgqueue)) {
        return;
    }

    ptr = (*msgqueue)->first;

    while (ptr) {
        next = ptr->next;
        opj_free(ptr);
        ptr = next;
    }
    if ((*msgqueue)->stateless && (*msgqueue)->cachemodel) {
        delete_cachemodel(&((*msgqueue)->cachemodel));
    }

    opj_free(*msgqueue);
}

void print_msgqueue(msgqueue_param_t *msgqueue)
{
    message_param_t *ptr;
    static const char *message_class[] = { "Precinct", "Ext-Prec", "TileHead", "non",
                                           "Tile", "Ext-Tile", "Main", "non", "Meta"
                                         };

    if (!msgqueue) {
        return;
    }

    fprintf(logstream, "message queue:\n");
    ptr = msgqueue->first;

    while (ptr) {
        fprintf(logstream, "\t class_id: %" PRId64 " %s\n", ptr->class_id,
                message_class[ptr->class_id]);
        fprintf(logstream, "\t in_class_id: %" PRId64 "\n", ptr->in_class_id);
        fprintf(logstream, "\t csn: %" PRId64 "\n", ptr->csn);
        fprintf(logstream, "\t bin_offset: %#" PRIx64 "\n", ptr->bin_offset);
        fprintf(logstream, "\t length: %#" PRIx64 "\n", ptr->length);
        if (ptr->class_id % 2) {
            fprintf(logstream, "\t aux: %" PRId64 "\n", ptr->aux);
        }
        fprintf(logstream, "\t last_byte: %d\n", ptr->last_byte);
        if (ptr->phld) {
            print_placeholder(ptr->phld);
        } else {
            fprintf(logstream, "\t res_offset: %#" PRIx64 "\n", ptr->res_offset);
        }
        fprintf(logstream, "\n");

        ptr = ptr->next;
    }
}

void enqueue_message(message_param_t *msg, msgqueue_param_t *msgqueue);

void enqueue_mainheader(msgqueue_param_t *msgqueue)
{
    cachemodel_param_t *cachemodel;
    target_param_t *target;
    index_param_t *codeidx;
    message_param_t *msg;

    cachemodel = msgqueue->cachemodel;
    target = cachemodel->target;
    codeidx = target->codeidx;

    msg = (message_param_t *)opj_malloc(sizeof(message_param_t));

    msg->last_byte = OPJ_TRUE;
    msg->in_class_id = 0;
    msg->class_id = MAINHEADER_MSG;
    assert(target->csn >= 0);
    msg->csn = (Byte8_t)target->csn;
    msg->bin_offset = 0;
    msg->length = codeidx->mhead_length;
    msg->aux = 0; /* non exist*/
    msg->res_offset = codeidx->offset;
    msg->phld = NULL;
    msg->next = NULL;

    enqueue_message(msg, msgqueue);

    cachemodel->mhead_model = OPJ_TRUE;
}

void enqueue_tileheader(int tile_id, msgqueue_param_t *msgqueue)
{
    cachemodel_param_t *cachemodel;
    target_param_t *target;
    index_param_t *codeidx;
    message_param_t *msg;

    cachemodel = msgqueue->cachemodel;
    target = cachemodel->target;
    codeidx = target->codeidx;

    if (!cachemodel->th_model[ tile_id]) {
        msg = (message_param_t *)opj_malloc(sizeof(message_param_t));
        msg->last_byte = OPJ_TRUE;
        assert(tile_id >= 0);
        msg->in_class_id = (Byte8_t)tile_id;
        msg->class_id = TILE_HEADER_MSG;
        assert(target->csn >= 0);
        msg->csn = (Byte8_t)target->csn;
        msg->bin_offset = 0;
        msg->length = codeidx->tileheader[tile_id]->tlen -
                      2; /* SOT marker segment is removed*/
        msg->aux = 0; /* non exist*/
        msg->res_offset = codeidx->offset + (OPJ_OFF_T)get_elemOff(codeidx->tilepart, 0,
                          (Byte8_t)tile_id) + 2; /* skip SOT marker seg*/
        msg->phld = NULL;
        msg->next = NULL;

        enqueue_message(msg, msgqueue);
        cachemodel->th_model[ tile_id] = OPJ_TRUE;
    }
}

void enqueue_tile(Byte4_t tile_id, int level, msgqueue_param_t *msgqueue)
{
    cachemodel_param_t *cachemodel;
    target_param_t *target;
    OPJ_BOOL *tp_model;
    Byte8_t numOftparts; /* num of tile parts par tile*/
    Byte8_t numOftiles;
    index_param_t *codeidx;
    faixbox_param_t *tilepart;
    message_param_t *msg;
    Byte8_t binOffset, binLength, class_id;
    Byte8_t i;

    cachemodel = msgqueue->cachemodel;
    target = cachemodel->target;
    codeidx  = target->codeidx;
    tilepart = codeidx->tilepart;

    numOftparts = get_nmax(tilepart);
    numOftiles  = get_m(tilepart);

    class_id = (numOftparts == 1) ? TILE_MSG : EXT_TILE_MSG;

    if (/*tile_id < 0 ||*/ numOftiles <= (Byte8_t)tile_id) {
        fprintf(FCGI_stderr, "Error, Invalid tile-id %d\n", tile_id);
        return;
    }

    tp_model = &cachemodel->tp_model[ tile_id * numOftparts];

    binOffset = 0;
    for (i = 0; i < numOftparts - (Byte8_t)level; i++) {
        binLength = get_elemLen(tilepart, i, tile_id);

        if (!tp_model[i]) {
            msg = (message_param_t *)opj_malloc(sizeof(message_param_t));

            msg->last_byte = (i == numOftparts - 1);
            msg->in_class_id = tile_id;
            msg->class_id = class_id;
            assert(target->csn >= 0);
            msg->csn = (Byte8_t)target->csn;
            msg->bin_offset = binOffset;
            msg->length = binLength;
            msg->aux = numOftparts - i;
            msg->res_offset = codeidx->offset + (OPJ_OFF_T)get_elemOff(tilepart, i,
                              tile_id)/*-1*/;
            msg->phld = NULL;
            msg->next = NULL;

            enqueue_message(msg, msgqueue);

            tp_model[i] = OPJ_TRUE;
        }
        binOffset += binLength;
    }
}

void enqueue_precinct(int seq_id, int tile_id, int comp_id, int layers,
                      msgqueue_param_t *msgqueue)
{
    cachemodel_param_t *cachemodel;
    index_param_t *codeidx;
    faixbox_param_t *precpacket;
    message_param_t *msg;
    Byte8_t nmax, binOffset, binLength;
    int layer_id, numOflayers;

    cachemodel = msgqueue->cachemodel;
    codeidx = cachemodel->target->codeidx;
    precpacket = codeidx->precpacket[ comp_id];
    numOflayers = codeidx->COD.numOflayers;

    nmax = get_nmax(precpacket);
    assert(nmax < INT_MAX);
    if (layers < 0) {
        layers = numOflayers;
    }
    assert(tile_id >= 0);

    binOffset = 0;
    for (layer_id = 0; layer_id < layers; layer_id++) {

        binLength = get_elemLen(precpacket, (Byte8_t)(seq_id * numOflayers + layer_id),
                                (Byte8_t)tile_id);

        if (!cachemodel->pp_model[comp_id][tile_id * (int)nmax + seq_id * numOflayers +
                                           layer_id]) {

            msg = (message_param_t *)opj_malloc(sizeof(message_param_t));
            msg->last_byte = (layer_id == (numOflayers - 1));
            msg->in_class_id = comp_precinct_id(tile_id, comp_id, seq_id, codeidx->SIZ.Csiz,
                                                (int)codeidx->SIZ.XTnum * (int) codeidx->SIZ.YTnum);
            msg->class_id = PRECINCT_MSG;
            msg->csn = (Byte8_t)cachemodel->target->csn;
            msg->bin_offset = binOffset;
            msg->length = binLength;
            msg->aux = 0;
            msg->res_offset = codeidx->offset + (OPJ_OFF_T)get_elemOff(precpacket,
                              (Byte8_t)(seq_id * numOflayers + layer_id), (Byte8_t)tile_id);
            msg->phld = NULL;
            msg->next = NULL;

            enqueue_message(msg, msgqueue);

            cachemodel->pp_model[comp_id][tile_id * (int)nmax + seq_id * numOflayers +
                                          layer_id] = OPJ_TRUE;
        }
        binOffset += binLength;
    }
}

/* MM FIXME: each params is coded on int, this is really not clear from the specs what it should be */
Byte8_t comp_precinct_id(int t, int c, int s, int num_components, int num_tiles)
{
    return (Byte8_t)(t + (c + s * num_components) * num_tiles);
}

void enqueue_box(Byte8_t meta_id, boxlist_param_t *boxlist,
                 msgqueue_param_t *msgqueue, Byte8_t *binOffset);
void enqueue_phld(Byte8_t meta_id, placeholderlist_param_t *phldlist,
                  msgqueue_param_t *msgqueue, Byte8_t *binOffset);
void enqueue_boxcontents(Byte8_t meta_id, boxcontents_param_t *boxcontents,
                         msgqueue_param_t *msgqueue, Byte8_t *binOffset);

void enqueue_metadata(Byte8_t meta_id, msgqueue_param_t *msgqueue)
{
    metadatalist_param_t *metadatalist;
    metadata_param_t *metadata;
    Byte8_t binOffset;

    metadatalist = msgqueue->cachemodel->target->codeidx->metadatalist;
    metadata = search_metadata(meta_id, metadatalist);

    if (!metadata) {
        fprintf(FCGI_stderr, "Error: metadata-bin %" PRIu64 " not found\n", meta_id);
        return;
    }
    binOffset = 0;

    if (metadata->boxlist) {
        enqueue_box(meta_id, metadata->boxlist, msgqueue, &binOffset);
    }

    if (metadata->placeholderlist) {
        enqueue_phld(meta_id, metadata->placeholderlist, msgqueue, &binOffset);
    }

    if (metadata->boxcontents) {
        enqueue_boxcontents(meta_id, metadata->boxcontents, msgqueue, &binOffset);
    }

    msgqueue->last->last_byte = OPJ_TRUE;
}

message_param_t * gene_metamsg(Byte8_t meta_id, Byte8_t binoffset,
                               Byte8_t length, OPJ_OFF_T res_offset, placeholder_param_t *phld, Byte8_t csn);

void enqueue_box(Byte8_t meta_id, boxlist_param_t *boxlist,
                 msgqueue_param_t *msgqueue, Byte8_t *binOffset)
{
    box_param_t *box;
    message_param_t *msg;

    box = boxlist->first;
    assert(msgqueue->cachemodel->target->csn >= 0);
    while (box) {
        msg = gene_metamsg(meta_id, *binOffset, box->length, box->offset, NULL,
                           (Byte8_t)msgqueue->cachemodel->target->csn);
        enqueue_message(msg, msgqueue);

        *binOffset += box->length;
        box = box->next;
    }
}

void enqueue_phld(Byte8_t meta_id, placeholderlist_param_t *phldlist,
                  msgqueue_param_t *msgqueue, Byte8_t *binOffset)
{
    placeholder_param_t *phld;
    message_param_t *msg;

    phld = phldlist->first;
    assert(msgqueue->cachemodel->target->csn >= 0);
    while (phld) {
        msg = gene_metamsg(meta_id, *binOffset, phld->LBox, 0, phld,
                           (Byte8_t)msgqueue->cachemodel->target->csn);
        enqueue_message(msg, msgqueue);

        *binOffset += phld->LBox;
        phld = phld->next;
    }
}

void enqueue_boxcontents(Byte8_t meta_id, boxcontents_param_t *boxcontents,
                         msgqueue_param_t *msgqueue, Byte8_t *binOffset)
{
    message_param_t *msg;

    assert(msgqueue->cachemodel->target->csn >= 0);
    msg = gene_metamsg(meta_id, *binOffset, boxcontents->length,
                       boxcontents->offset, NULL, (Byte8_t)msgqueue->cachemodel->target->csn);
    enqueue_message(msg, msgqueue);

    *binOffset += boxcontents->length;
}

message_param_t * gene_metamsg(Byte8_t meta_id, Byte8_t binOffset,
                               Byte8_t length, OPJ_OFF_T res_offset, placeholder_param_t *phld, Byte8_t csn)
{
    message_param_t *msg;

    msg = (message_param_t *)opj_malloc(sizeof(message_param_t));

    msg->last_byte = OPJ_FALSE;
    msg->in_class_id = meta_id;
    msg->class_id = METADATA_MSG;
    msg->csn = csn;
    msg->bin_offset = binOffset;
    msg->length = length;
    msg->aux = 0; /* non exist*/
    msg->res_offset = res_offset;
    msg->phld = phld;
    msg->next = NULL;

    return msg;
}

void enqueue_message(message_param_t *msg, msgqueue_param_t *msgqueue)
{
    if (msgqueue->first) {
        msgqueue->last->next = msg;
    } else {
        msgqueue->first = msg;
    }

    msgqueue->last = msg;
}

void add_bin_id_vbas_stream(Byte_t bb, Byte_t c, Byte8_t in_class_id,
                            int tmpfd);
void add_vbas_stream(Byte8_t code, int tmpfd);
void add_body_stream(message_param_t *msg, int fd, int tmpfd);
void add_placeholder_stream(placeholder_param_t *phld, int tmpfd);

void recons_stream_from_msgqueue(msgqueue_param_t *msgqueue, int tmpfd)
{
    message_param_t *msg;
    Byte8_t class_id, csn;
    Byte_t bb, c;

    if (!(msgqueue)) {
        return;
    }

    msg = msgqueue->first;
    class_id = (Byte8_t) - 1;
    csn = (Byte8_t) - 1;
    while (msg) {
        if (msg->csn == csn) {
            if (msg->class_id == class_id) {
                bb = 1;
            } else {
                bb = 2;
                class_id = msg->class_id;
            }
        } else {
            bb = 3;
            class_id = msg->class_id;
            csn = msg->csn;
        }

        c = msg->last_byte ? 1 : 0;

        add_bin_id_vbas_stream(bb, c, msg->in_class_id, tmpfd);

        if (bb >= 2) {
            add_vbas_stream(class_id, tmpfd);
        }
        if (bb == 3) {
            add_vbas_stream(csn, tmpfd);
        }

        add_vbas_stream(msg->bin_offset, tmpfd);
        add_vbas_stream(msg->length, tmpfd);

        if (msg->class_id % 2) { /* Aux is present only if the id is odd*/
            add_vbas_stream(msg->aux, tmpfd);
        }

        if (msg->phld) {
            add_placeholder_stream(msg->phld, tmpfd);
        } else {
            add_body_stream(msg, msgqueue->cachemodel->target->fd, tmpfd);
        }

        msg = msg->next;
    }
}

void add_vbas_with_bytelen_stream(Byte8_t code, int bytelength, int tmpfd);
void print_binarycode(Byte8_t n, int segmentlen);

void add_bin_id_vbas_stream(Byte_t bb, Byte_t c, Byte8_t in_class_id, int tmpfd)
{
    int bytelength;
    Byte8_t tmp;

    /* A.2.3 In-class identifiers */
    /* 7k-3bits, where k is the number of bytes in the VBAS*/
    bytelength = 1;
    tmp = in_class_id >> 4;
    while (tmp) {
        bytelength ++;
        tmp >>= 7;
    }

    in_class_id |= (Byte8_t)((((bb & 3) << 5) | (c & 1) << 4) << ((
                                 bytelength - 1) * 7));

    add_vbas_with_bytelen_stream(in_class_id, bytelength, tmpfd);
}

void add_vbas_stream(Byte8_t code, int tmpfd)
{
    int bytelength;
    Byte8_t tmp;

    bytelength = 1;
    tmp = code;
    while (tmp >>= 7) {
        bytelength ++;
    }

    add_vbas_with_bytelen_stream(code, bytelength, tmpfd);
}

void add_vbas_with_bytelen_stream(Byte8_t code, int bytelength, int tmpfd)
{
    int n;
    Byte8_t seg;

    n = bytelength - 1;
    while (n >= 0) {
        seg = (code >> (n * 7)) & 0x7f;
        if (n) {
            seg |= 0x80;
        }
        if (write(tmpfd, (Byte4_t *)&seg, 1) != 1) {
            fprintf(FCGI_stderr, "Error: failed to write vbas\n");
            return;
        }
        n--;
    }
}

void add_body_stream(message_param_t *msg, int fd, int tmpfd)
{
    Byte_t *data;

    if (!(data = fetch_bytes(fd, msg->res_offset, msg->length))) {
        fprintf(FCGI_stderr, "Error: fetch_bytes in add_body_stream()\n");
        return;
    }

    if (write(tmpfd, data, msg->length) < 1) {
        opj_free(data);
        fprintf(FCGI_stderr, "Error: fwrite in add_body_stream()\n");
        return;
    }
    opj_free(data);
}

void add_bigendian_bytestream(Byte8_t code, int bytelength, int tmpfd);

void add_placeholder_stream(placeholder_param_t *phld, int tmpfd)
{
    add_bigendian_bytestream(phld->LBox, 4, tmpfd);
    if (write(tmpfd, phld->TBox, 4) < 1) {
        fprintf(FCGI_stderr, "Error: fwrite in add_placeholder_stream()\n");
        return;
    }
    add_bigendian_bytestream(phld->Flags, 4, tmpfd);
    add_bigendian_bytestream(phld->OrigID, 8, tmpfd);

    if (write(tmpfd, phld->OrigBH, phld->OrigBHlen) < 1) {
        fprintf(FCGI_stderr, "Error: fwrite in add_placeholder_stream()\n");
        return;
    }
}

void add_bigendian_bytestream(Byte8_t code, int bytelength, int tmpfd)
{
    int n;
    Byte8_t seg;

    n = bytelength - 1;
    while (n >= 0) {
        seg = (code >> (n * 8)) & 0xff;
        if (write(tmpfd, (Byte4_t *)&seg, 1) != 1) {
            fprintf(FCGI_stderr, "ERROR: failed to write bigendian_bytestream\n");
            return;
        }
        n--;
    }
}

void print_binarycode(Byte8_t n, int segmentlen)
{
    char buf[256];
    int i = 0, j, k;

    do {
        buf[i++] = n % 2 ? '1' : '0';
    } while ((n = n / 2));

    for (j = segmentlen - 1; j >= i; j--) {
        putchar('0');
    }

    for (j = i - 1, k = 0; j >= 0; j--, k++) {
        putchar(buf[j]);
        if (!((k + 1) % segmentlen)) {
            printf(" ");
        }
    }
    printf("\n");
}

Byte_t * parse_bin_id_vbas(Byte_t *streamptr, Byte_t *bb, Byte_t *c,
                           Byte8_t *in_class_id);
Byte_t * parse_vbas(Byte_t *streamptr, Byte8_t *elem);

void parse_JPIPstream(Byte_t *JPIPstream, Byte8_t streamlen, OPJ_OFF_T offset,
                      msgqueue_param_t *msgqueue)
{
    Byte_t *ptr;  /* stream pointer*/
    message_param_t *msg;
    Byte_t bb, c;
    Byte8_t class_id, csn;

    class_id = (Byte8_t) - 1; /* dummy*/
    csn = (Byte8_t) - 1;
    ptr = JPIPstream;
    while ((Byte8_t)(ptr - JPIPstream) < streamlen) {
        msg = (message_param_t *)opj_malloc(sizeof(message_param_t));

        ptr = parse_bin_id_vbas(ptr, &bb, &c, &msg->in_class_id);

        msg->last_byte   = c == 1 ? OPJ_TRUE : OPJ_FALSE;

        if (bb >= 2) {
            ptr = parse_vbas(ptr, &class_id);
        }

        msg->class_id = class_id;

        if (bb == 3) {
            ptr = parse_vbas(ptr, &csn);
        }
        msg->csn = csn;

        ptr = parse_vbas(ptr, &msg->bin_offset);
        ptr = parse_vbas(ptr, &msg->length);

        if (msg->class_id % 2) { /* Aux is present only if the id is odd*/
            ptr = parse_vbas(ptr, &msg->aux);
        } else {
            msg->aux = 0;
        }

        msg->res_offset = ptr - JPIPstream + offset;
        msg->phld = NULL;
        msg->next = NULL;

        if (msgqueue->first) {
            msgqueue->last->next = msg;
        } else {
            msgqueue->first = msg;
        }
        msgqueue->last = msg;

        ptr += msg->length;
    }
}

void parse_metadata(metadata_param_t *metadata, message_param_t *msg,
                    Byte_t *stream);

void parse_metamsg(msgqueue_param_t *msgqueue, Byte_t *stream,
                   Byte8_t streamlen, metadatalist_param_t *metadatalist)
{
    message_param_t *msg;
    (void)streamlen;

    if (metadatalist == NULL) {
        return;
    }

    msg = msgqueue->first;
    while (msg) {
        if (msg->class_id == METADATA_MSG) {
            metadata_param_t *metadata = gene_metadata(msg->in_class_id, NULL, NULL, NULL);
            insert_metadata_into_list(metadata, metadatalist);
            parse_metadata(metadata, msg, stream + msg->res_offset);
        }
        msg = msg->next;
    }
}

placeholder_param_t * parse_phld(Byte_t *datastream, Byte8_t metalength);

void parse_metadata(metadata_param_t *metadata, message_param_t *msg,
                    Byte_t *datastream)
{
    box_param_t *box;
    placeholder_param_t *phld;
    char *boxtype = (char *)(datastream + 4);

    msg->phld = NULL;

    if (strncmp(boxtype, "phld", 4) == 0) {
        if (!metadata->placeholderlist) {
            metadata->placeholderlist = gene_placeholderlist();
        }

        phld = parse_phld(datastream, msg->length);
        msg->phld = phld;
        insert_placeholder_into_list(phld, metadata->placeholderlist);
    } else if (isalpha(boxtype[0]) && isalpha(boxtype[1]) &&
               (isalnum(boxtype[2]) || isspace(boxtype[2])) &&
               (isalpha(boxtype[3]) || isspace(boxtype[3]))) {
        if (!metadata->boxlist) {
            metadata->boxlist = gene_boxlist();
        }

        box = gene_boxbyOffinStream(datastream, msg->res_offset);
        insert_box_into_list(box, metadata->boxlist);
    } else {
        metadata->boxcontents = gene_boxcontents(msg->res_offset, msg->length);
    }
}

placeholder_param_t * parse_phld(Byte_t *datastream, Byte8_t metalength)
{
    placeholder_param_t *phld;

    phld = (placeholder_param_t *)opj_malloc(sizeof(placeholder_param_t));

    phld->LBox = big4(datastream);
    strncpy(phld->TBox, "phld", 4);
    phld->Flags = big4(datastream + 8);
    phld->OrigID = big8(datastream + 12);
    phld->OrigBHlen = (Byte_t)(metalength - 20);
    phld->OrigBH = (Byte_t *)opj_malloc(phld->OrigBHlen);
    memcpy(phld->OrigBH, datastream + 20, phld->OrigBHlen);
    phld->next = NULL;

    return phld;
}

Byte_t * parse_bin_id_vbas(Byte_t *streamptr, Byte_t *bb, Byte_t *c,
                           Byte8_t *in_class_id)
{
    Byte_t code;
    Byte_t *ptr;

    ptr = streamptr;
    code = *(ptr++);

    *bb = (code >> 5) & 3;
    *c  = (code >> 4) & 1;

    *in_class_id = code & 15;

    while (code >> 7) {
        code = *(ptr++);
        *in_class_id = (*in_class_id << 7) | (code & 0x7f);
    }
    return ptr;
}

Byte_t * parse_vbas(Byte_t *streamptr, Byte8_t *elem)
{
    Byte_t code;
    Byte_t *ptr;

    *elem = 0;
    ptr = streamptr;
    do {
        code = *(ptr++);
        *elem = (*elem << 7) | (code & 0x7f);
    } while (code >> 7);

    return ptr;
}

void delete_message_in_msgqueue(message_param_t **msg,
                                msgqueue_param_t *msgqueue)
{
    message_param_t *ptr;

    if (!(*msg)) {
        return;
    }

    if (*msg == msgqueue->first) {
        msgqueue->first = (*msg)->next;
    } else {
        ptr = msgqueue->first;
        while (ptr->next != *msg) {
            ptr = ptr->next;
        }

        ptr->next = (*msg)->next;

        if (*msg == msgqueue->last) {
            msgqueue->last = ptr;
        }
    }
    opj_free(*msg);
}
