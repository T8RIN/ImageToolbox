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
#include <string.h>
#include <math.h>
#include <assert.h>
#include "jp2k_encoder.h"
#include "j2kheader_manager.h"
#include "imgreg_manager.h"
#include "opj_inttypes.h"


#ifdef SERVER
#include "fcgi_stdio.h"
#define logstream FCGI_stdout
#else
#define FCGI_stdout stdout
#define FCGI_stderr stderr
#define logstream stderr
#endif /*SERVER*/


/**
 * search a message by class_id
 *
 * @param[in] class_id    class identifiers
 * @param[in] in_class_id in-class identifiers, -1 means any
 * @param[in] csn         codestream number
 * @param[in] msg         first message pointer of the searching list
 * @return                found message pointer
 */
message_param_t * search_message(Byte8_t class_id, Byte8_t in_class_id,
                                 Byte8_t csn, message_param_t *msg);

/**
 * reconstruct j2k codestream from JPT- (in future, JPP-) stream
 *
 * @param[in]  msgqueue   message queue pointer
 * @param[in]  jpipstream original JPT- JPP- stream
 * @param[in]  csn        codestream number
 * @param[in]  fw         reconstructing image frame width
 * @param[in]  fh         reconstructing image frame height
 * @param[out] codelen   codestream length
 * @return               generated reconstructed j2k codestream
 */
Byte_t * recons_codestream(msgqueue_param_t *msgqueue, Byte_t *jpipstream,
                           Byte8_t csn, int fw, int fh, Byte8_t *codelen);

Byte_t * recons_j2k(msgqueue_param_t *msgqueue, Byte_t *jpipstream, Byte8_t csn,
                    int fw, int fh, Byte8_t *j2klen)
{
    Byte_t *j2kstream = NULL;

    if (!msgqueue) {
        return NULL;
    }

    j2kstream = recons_codestream(msgqueue, jpipstream, csn, fw, fh, j2klen);

    return j2kstream;
}

Byte_t * add_emptyboxstream(placeholder_param_t *phld, Byte_t *jp2stream,
                            Byte8_t *jp2len);
Byte_t * add_msgstream(message_param_t *message, Byte_t *origstream,
                       Byte_t *j2kstream, Byte8_t *j2klen);

Byte_t * recons_jp2(msgqueue_param_t *msgqueue, Byte_t *jpipstream, Byte8_t csn,
                    Byte8_t *jp2len)
{
    message_param_t *ptr;
    Byte_t *jp2stream = NULL;
    Byte_t *codestream = NULL;
    Byte8_t codelen;
    Byte8_t jp2cDBoxOffset = 0, jp2cDBoxlen = 0;

    *jp2len = 0;

    if (!msgqueue) {
        return NULL;
    }

    ptr = msgqueue->first;
    while ((ptr = search_message(METADATA_MSG, (Byte8_t) - 1, csn, ptr)) != NULL) {
        if (ptr->phld) {
            if (strncmp((char *)ptr->phld->OrigBH + 4, "jp2c", 4) == 0) {
                jp2cDBoxOffset = *jp2len + ptr->phld->OrigBHlen;
                jp2stream = add_emptyboxstream(ptr->phld, jp2stream, jp2len);  /* header only */
                jp2cDBoxlen = *jp2len - jp2cDBoxOffset;
            } else {
                jp2stream = add_emptyboxstream(ptr->phld, jp2stream,
                                               jp2len);    /* header only */
            }
        }
        jp2stream = add_msgstream(ptr, jpipstream, jp2stream, jp2len);
        ptr = ptr->next;
    }

    codestream = recons_codestream(msgqueue, jpipstream, csn, 0, 0, &codelen);

    if (jp2cDBoxOffset != 0 && codelen <= jp2cDBoxlen) {
        memcpy(jp2stream + jp2cDBoxOffset, codestream, codelen);
    }

    opj_free(codestream);

    return jp2stream;
}

OPJ_BOOL isJPPstream(Byte8_t csn, msgqueue_param_t *msgqueue);

Byte_t * recons_codestream_from_JPTstream(msgqueue_param_t *msgqueue,
        Byte_t *jpipstream, Byte8_t csn, int fw, int fh,  Byte8_t *j2klen);
Byte_t * recons_codestream_from_JPPstream(msgqueue_param_t *msgqueue,
        Byte_t *jpipstream, Byte8_t csn, int fw, int fh, Byte8_t *j2klen);

Byte_t * add_EOC(Byte_t *j2kstream, Byte8_t *j2klen);

Byte_t * recons_codestream(msgqueue_param_t *msgqueue, Byte_t *jpipstream,
                           Byte8_t csn, int fw, int fh, Byte8_t *codelen)
{
    if (isJPPstream(csn, msgqueue)) {
        return recons_codestream_from_JPPstream(msgqueue, jpipstream, csn, fw, fh,
                                                codelen);
    } else {
        return recons_codestream_from_JPTstream(msgqueue, jpipstream, csn, fw, fh,
                                                codelen);
    }
}

OPJ_BOOL isJPPstream(Byte8_t csn, msgqueue_param_t *msgqueue)
{
    message_param_t *msg;

    msg = msgqueue->first;
    while (msg) {
        if (msg->csn == csn) {
            if (msg->class_id <= 2) {
                return OPJ_TRUE;
            } else if (msg->class_id == 4 || msg->class_id == 5) {
                return OPJ_FALSE;
            }
        }
        msg = msg->next;
    }

    fprintf(FCGI_stderr, "Error, message of csn %" PRId64 " not found\n", csn);

    return OPJ_FALSE;
}

Byte_t * add_mainhead_msgstream(msgqueue_param_t *msgqueue, Byte_t *origstream,
                                Byte_t *j2kstream, Byte8_t csn, Byte8_t *j2klen);
Byte8_t get_last_tileID(msgqueue_param_t *msgqueue, Byte8_t csn,
                        OPJ_BOOL isJPPstream);
Byte_t * add_emptytilestream(const Byte8_t tileID, Byte_t *j2kstream,
                             Byte8_t *j2klen);

Byte_t * recons_codestream_from_JPTstream(msgqueue_param_t *msgqueue,
        Byte_t *jpipstream, Byte8_t csn, int fw, int fh,  Byte8_t *j2klen)
{
    Byte_t *j2kstream = NULL;
    Byte8_t last_tileID, tileID;
    OPJ_BOOL found;
    Byte8_t binOffset;
    message_param_t *ptr;
    SIZmarker_param_t SIZ;
    OPJ_SIZE_T mindeclev;

    *j2klen = 0;
    j2kstream = add_mainhead_msgstream(msgqueue, jpipstream, j2kstream, csn,
                                       j2klen);

    if (!get_mainheader_from_j2kstream(j2kstream, &SIZ, NULL)) {
        return j2kstream;
    }

    if (fw <= 0 || fh <= 0) {
        mindeclev = 0;
    } else {
        mindeclev = (OPJ_SIZE_T)comp_decomplev(fw, fh, (int)SIZ.Xsiz, (int)SIZ.Ysiz);
    }

    last_tileID = get_last_tileID(msgqueue, csn, OPJ_FALSE);

    for (tileID = 0; tileID <= last_tileID; tileID++) {
        found = OPJ_FALSE;
        binOffset = 0;

        ptr = msgqueue->first;
        while ((ptr = search_message(TILE_MSG, tileID, csn, ptr)) != NULL) {
            if (ptr->bin_offset == binOffset) {
                found = OPJ_TRUE;
                j2kstream = add_msgstream(ptr, jpipstream, j2kstream, j2klen);
                binOffset += ptr->length;
            }
            ptr = ptr->next;
        }
        ptr = msgqueue->first;
        while ((ptr = search_message(EXT_TILE_MSG, tileID, csn, ptr)) != NULL) {
            if (ptr->aux > mindeclev) { /* FIXME: pointer comparison ? */
                if (ptr->bin_offset == binOffset) {
                    found = OPJ_TRUE;
                    j2kstream = add_msgstream(ptr, jpipstream, j2kstream, j2klen);
                    binOffset += ptr->length;
                }
            }
            ptr = ptr->next;
        }
        if (!found) {
            j2kstream = add_emptytilestream(tileID, j2kstream, j2klen);
        }
    }

    j2kstream = add_EOC(j2kstream, j2klen);

    return j2kstream;
}

Byte_t * add_SOTmkr(Byte_t *j2kstream, Byte8_t *j2klen);

Byte_t * recons_bitstream(msgqueue_param_t *msgqueue, Byte_t *jpipstream,
                          Byte_t *j2kstream, Byte8_t csn,
                          Byte8_t tileID, SIZmarker_param_t SIZ, CODmarker_param_t COD, int mindeclev,
                          int *max_reslev, Byte8_t *j2klen);

Byte_t * recons_codestream_from_JPPstream(msgqueue_param_t *msgqueue,
        Byte_t *jpipstream, Byte8_t csn, int fw, int fh, Byte8_t *j2klen)
{
    Byte_t *j2kstream = NULL;
    Byte8_t tileID, last_tileID;
    Byte8_t SOToffset;
    OPJ_BOOL foundTH;
    Byte8_t binOffset;
    message_param_t *ptr;
    SIZmarker_param_t SIZ;
    CODmarker_param_t COD;
    int max_reslev, mindeclev;

    *j2klen = 0;
    j2kstream = add_mainhead_msgstream(msgqueue, jpipstream, j2kstream, csn,
                                       j2klen);

    if (!get_mainheader_from_j2kstream(j2kstream, &SIZ, &COD)) {
        return j2kstream;
    }

    if (fw == 0 || fh == 0) {
        mindeclev = 0;
    } else {
        mindeclev = comp_decomplev(fw, fh, (int)SIZ.Xsiz, (int)SIZ.Ysiz);
    }

    max_reslev = -1;
    last_tileID = get_last_tileID(msgqueue, csn, OPJ_TRUE);

    for (tileID = 0; tileID <= last_tileID; tileID++) {

        ptr = msgqueue->first;
        binOffset = 0;
        foundTH = OPJ_FALSE;
        SOToffset = *j2klen;
        while ((ptr = search_message(TILE_HEADER_MSG, tileID, csn, ptr)) != NULL) {
            if (ptr->bin_offset == binOffset) {
                j2kstream = add_SOTmkr(j2kstream, j2klen);
                j2kstream = add_msgstream(ptr, jpipstream, j2kstream, j2klen);
                foundTH = OPJ_TRUE;
                binOffset += ptr->length;
            }
            ptr = ptr->next;
        }

        if (foundTH) {
            j2kstream = recons_bitstream(msgqueue, jpipstream, j2kstream, csn, tileID, SIZ,
                                         COD, mindeclev, &max_reslev, j2klen);
            modify_tileheader(j2kstream, SOToffset,
                              (max_reslev < COD.numOfdecomp ? max_reslev : -1), SIZ.Csiz, j2klen);
        } else {
            j2kstream = add_emptytilestream(tileID, j2kstream, j2klen);
        }
    }

    if (max_reslev < COD.numOfdecomp)
        if (!modify_mainheader(j2kstream, max_reslev, SIZ, COD, j2klen)) {
            delete_COD(COD);
            return j2kstream;
        }

    j2kstream = add_EOC(j2kstream, j2klen);
    delete_COD(COD);

    return j2kstream;
}

Byte_t * add_mainhead_msgstream(msgqueue_param_t *msgqueue, Byte_t *origstream,
                                Byte_t *j2kstream, Byte8_t csn, Byte8_t *j2klen)
{
    message_param_t *ptr;
    Byte8_t binOffset;

    ptr = msgqueue->first;
    binOffset = 0;

    while ((ptr = search_message(MAINHEADER_MSG, (Byte8_t) - 1, csn,
                                 ptr)) != NULL) {
        if (ptr->bin_offset == binOffset) {
            j2kstream = add_msgstream(ptr, origstream, j2kstream, j2klen);
            binOffset += ptr->length;
        }
        ptr = ptr->next;
    }
    return j2kstream;
}

Byte_t * add_SOTmkr(Byte_t *j2kstream, Byte8_t *j2klen)
{
    Byte_t *buf;
    const Byte2_t SOT = 0x90ff;

    buf = (Byte_t *)opj_malloc((*j2klen) + 2);

    memcpy(buf, j2kstream, *j2klen);
    memcpy(buf + (*j2klen), &SOT, 2);

    *j2klen += 2;

    if (j2kstream) {
        opj_free(j2kstream);
    }

    return buf;
}

Byte_t * recons_LRCPbitstream(msgqueue_param_t *msgqueue, Byte_t *jpipstream,
                              Byte_t *j2kstream, Byte8_t csn,
                              Byte8_t tileID, SIZmarker_param_t SIZ, CODmarker_param_t COD, int mindeclev,
                              int *max_reslev, Byte8_t *j2klen);

Byte_t * recons_RLCPbitstream(msgqueue_param_t *msgqueue, Byte_t *jpipstream,
                              Byte_t *j2kstream, Byte8_t csn,
                              Byte8_t tileID, SIZmarker_param_t SIZ, CODmarker_param_t COD, int mindeclev,
                              int *max_reslev, Byte8_t *j2klen);

Byte_t * recons_RPCLbitstream(msgqueue_param_t *msgqueue, Byte_t *jpipstream,
                              Byte_t *j2kstream, Byte8_t csn,
                              Byte8_t tileID, SIZmarker_param_t SIZ, CODmarker_param_t COD, int mindeclev,
                              int *max_reslev, Byte8_t *j2klen);

Byte_t * recons_PCRLbitstream(msgqueue_param_t *msgqueue, Byte_t *jpipstream,
                              Byte_t *j2kstream, Byte8_t csn,
                              Byte8_t tileID, SIZmarker_param_t SIZ, CODmarker_param_t COD, int mindeclev,
                              int *max_reslev, Byte8_t *j2klen);

Byte_t * recons_CPRLbitstream(msgqueue_param_t *msgqueue, Byte_t *jpipstream,
                              Byte_t *j2kstream, Byte8_t csn,
                              Byte8_t tileID, SIZmarker_param_t SIZ, CODmarker_param_t COD, int mindeclev,
                              int *max_reslev, Byte8_t *j2klen);

Byte_t * recons_bitstream(msgqueue_param_t *msgqueue, Byte_t *jpipstream,
                          Byte_t *j2kstream, Byte8_t csn,
                          Byte8_t tileID, SIZmarker_param_t SIZ, CODmarker_param_t COD, int mindeclev,
                          int *max_reslev, Byte8_t *j2klen)
{
    switch (COD.prog_order) {
    case OPJ_LRCP:
        return recons_LRCPbitstream(msgqueue, jpipstream, j2kstream, csn, tileID, SIZ,
                                    COD, mindeclev, max_reslev, j2klen);
    case OPJ_RLCP:
        return recons_RLCPbitstream(msgqueue, jpipstream, j2kstream, csn, tileID, SIZ,
                                    COD, mindeclev, max_reslev, j2klen);
    case OPJ_RPCL:
        return recons_RPCLbitstream(msgqueue, jpipstream, j2kstream, csn, tileID, SIZ,
                                    COD, mindeclev, max_reslev, j2klen);
    case OPJ_PCRL:
        return recons_PCRLbitstream(msgqueue, jpipstream, j2kstream, csn, tileID, SIZ,
                                    COD, mindeclev, max_reslev, j2klen);
    case OPJ_CPRL:
        return recons_CPRLbitstream(msgqueue, jpipstream, j2kstream, csn, tileID, SIZ,
                                    COD, mindeclev, max_reslev, j2klen);
    default:
        fprintf(FCGI_stderr, "Error, progression order not supported\n");
    }
    return j2kstream;
}

int comp_numOfprcts(Byte8_t tileID, SIZmarker_param_t SIZ,
                    CODmarker_param_t COD, int r);
Byte8_t comp_seqID(Byte8_t tileID, SIZmarker_param_t SIZ, CODmarker_param_t COD,
                   int r, int p);

Byte_t * recons_packet(msgqueue_param_t *msgqueue, Byte_t *jpipstream,
                       Byte_t *j2kstream, Byte8_t csn,
                       Byte8_t tileID, SIZmarker_param_t SIZ, CODmarker_param_t COD, int *max_reslev,
                       int comp_idx, int res_idx, int prct_idx, int lay_idx, Byte8_t *j2klen);

Byte_t * recons_LRCPbitstream(msgqueue_param_t *msgqueue, Byte_t *jpipstream,
                              Byte_t *j2kstream, Byte8_t csn,
                              Byte8_t tileID, SIZmarker_param_t SIZ, CODmarker_param_t COD, int mindeclev,
                              int *max_reslev, Byte8_t *j2klen)
{
    int r, p, c, l, numOfprcts;

    for (l = 0; l < COD.numOflayers; l++)
        for (r = 0; r <= (COD.numOfdecomp - mindeclev); r++) {
            if (COD.Scod & 0x01) {
                numOfprcts = comp_numOfprcts(tileID, SIZ, COD, r);
            } else {
                numOfprcts = 1;
            }

            for (c = 0; c < SIZ.Csiz; c++)
                for (p = 0; p < numOfprcts; p++) {
                    j2kstream = recons_packet(msgqueue, jpipstream, j2kstream, csn, tileID, SIZ,
                                              COD, max_reslev, c, r, p, l, j2klen);
                }
        }

    return j2kstream;
}

Byte_t * recons_RLCPbitstream(msgqueue_param_t *msgqueue, Byte_t *jpipstream,
                              Byte_t *j2kstream, Byte8_t csn,
                              Byte8_t tileID, SIZmarker_param_t SIZ, CODmarker_param_t COD, int mindeclev,
                              int *max_reslev, Byte8_t *j2klen)
{
    int r, p, c, l, numOfprcts;

    for (r = 0; r <= (COD.numOfdecomp - mindeclev); r++) {
        if (COD.Scod & 0x01) {
            numOfprcts = comp_numOfprcts(tileID, SIZ, COD, r);
        } else {
            numOfprcts = 1;
        }

        for (l = 0; l < COD.numOflayers; l++)
            for (c = 0; c < SIZ.Csiz; c++)
                for (p = 0; p < numOfprcts; p++) {
                    j2kstream = recons_packet(msgqueue, jpipstream, j2kstream, csn, tileID, SIZ,
                                              COD, max_reslev, c, r, p, l, j2klen);
                }
    }

    return j2kstream;
}

Byte_t * recons_precinct(msgqueue_param_t *msgqueue, Byte_t *jpipstream,
                         Byte_t *j2kstream, Byte8_t csn,
                         Byte8_t tileID, SIZmarker_param_t SIZ, CODmarker_param_t COD, int *max_reslev,
                         int comp_idx, int res_idx, Byte8_t seqID, Byte8_t *j2klen);

Byte_t * recons_RPCLbitstream(msgqueue_param_t *msgqueue, Byte_t *jpipstream,
                              Byte_t *j2kstream, Byte8_t csn,
                              Byte8_t tileID, SIZmarker_param_t SIZ, CODmarker_param_t COD, int mindeclev,
                              int *max_reslev, Byte8_t *j2klen)
{
    int r, p, c, numOfprcts;
    Byte8_t seqID;

    for (r = 0, seqID = 0; r <= (COD.numOfdecomp - mindeclev); r++) {

        if (COD.Scod & 0x01) {
            numOfprcts = comp_numOfprcts(tileID, SIZ, COD, r);
        } else {
            numOfprcts = 1;
        }

        for (p = 0; p < numOfprcts; p++, seqID++)
            for (c = 0; c < SIZ.Csiz; c++) {
                j2kstream = recons_precinct(msgqueue, jpipstream, j2kstream, csn, tileID, SIZ,
                                            COD, max_reslev, c, r, seqID, j2klen);
            }
    }

    return j2kstream;
}

Byte_t * recons_PCRLbitstream(msgqueue_param_t *msgqueue, Byte_t *jpipstream,
                              Byte_t *j2kstream, Byte8_t csn,
                              Byte8_t tileID, SIZmarker_param_t SIZ, CODmarker_param_t COD, int mindeclev,
                              int *max_reslev, Byte8_t *j2klen)
{
    int r, p, c, min_numOfprcts, numOfprcts, min_numOfres;
    Byte8_t seqID;

    min_numOfres = COD.numOfdecomp - mindeclev + 1;

    if (COD.Scod & 0x01) {
        min_numOfprcts = 0;
        for (r = 0; r < min_numOfres; r++) {
            numOfprcts = comp_numOfprcts(tileID, SIZ, COD, r);

            if (numOfprcts < min_numOfprcts || min_numOfprcts == 0) {
                min_numOfprcts = numOfprcts;
            }
        }
    } else {
        min_numOfprcts = 1;
    }

    for (p = 0; p < min_numOfprcts; p++)
        for (c = 0; c < SIZ.Csiz; c++)
            for (r = 0; r < min_numOfres; r++) {
                seqID = comp_seqID(tileID, SIZ, COD, r, p);
                j2kstream = recons_precinct(msgqueue, jpipstream, j2kstream, csn, tileID, SIZ,
                                            COD, max_reslev, c, r, seqID, j2klen);
            }

    return j2kstream;
}


Byte_t * recons_CPRLbitstream(msgqueue_param_t *msgqueue, Byte_t *jpipstream,
                              Byte_t *j2kstream, Byte8_t csn,
                              Byte8_t tileID, SIZmarker_param_t SIZ, CODmarker_param_t COD, int mindeclev,
                              int *max_reslev, Byte8_t *j2klen)
{
    int r, p, c, min_numOfprcts, numOfprcts, min_numOfres;
    Byte8_t seqID;

    min_numOfres = COD.numOfdecomp - mindeclev + 1;

    if (COD.Scod & 0x01) {
        min_numOfprcts = 0;
        for (r = 0; r < min_numOfres; r++) {
            numOfprcts = comp_numOfprcts(tileID, SIZ, COD, r);

            if (numOfprcts < min_numOfprcts || min_numOfprcts == 0) {
                min_numOfprcts = numOfprcts;
            }
        }
    } else {
        min_numOfprcts = 1;
    }

    for (c = 0; c < SIZ.Csiz; c++)
        for (p = 0; p < min_numOfprcts; p++)
            for (r = 0; r < min_numOfres; r++) {
                seqID = comp_seqID(tileID, SIZ, COD, r, p);
                j2kstream = recons_precinct(msgqueue, jpipstream, j2kstream, csn, tileID, SIZ,
                                            COD, max_reslev, c, r, seqID, j2klen);
            }

    return j2kstream;
}

int comp_numOfprcts(Byte8_t tileID, SIZmarker_param_t SIZ,
                    CODmarker_param_t COD, int r)
{
    int ret;
    Byte4_t XTsiz, YTsiz;

    XTsiz = get_tile_XSiz(SIZ, (Byte4_t)tileID, COD.numOfdecomp - r);
    YTsiz = get_tile_YSiz(SIZ, (Byte4_t)tileID, COD.numOfdecomp - r);

    ret = (int)(ceil((double)XTsiz / (double)COD.XPsiz[r]) * ceil((double)YTsiz /
                (double)COD.YPsiz[r]));
    assert(ret >= 0);
    return ret;
}

Byte_t * add_padding(Byte8_t padding, Byte_t *j2kstream, Byte8_t *j2klen);

Byte_t * recons_packet(msgqueue_param_t *msgqueue, Byte_t *jpipstream,
                       Byte_t *j2kstream, Byte8_t csn,
                       Byte8_t tileID, SIZmarker_param_t SIZ, CODmarker_param_t COD, int *max_reslev,
                       int comp_idx, int res_idx, int prct_idx, int lay_idx, Byte8_t *j2klen)
{
    Byte8_t seqID, precID, binOffset;
    message_param_t *ptr;
    OPJ_BOOL foundPrec;
    int l;

    seqID = comp_seqID(tileID, SIZ, COD, res_idx, prct_idx);
    precID = comp_precinct_id((int)tileID, comp_idx, (int)seqID, (int)SIZ.Csiz,
                              (int)SIZ.XTnum * (int)SIZ.YTnum);

    ptr = msgqueue->first;
    binOffset = 0;
    foundPrec = OPJ_FALSE;
    l = 0;

    while ((ptr = search_message(PRECINCT_MSG, precID, csn, ptr)) != NULL) {
        if (ptr->bin_offset == binOffset) {
            if (lay_idx == l) {
                j2kstream = add_msgstream(ptr, jpipstream, j2kstream, j2klen);
                foundPrec = OPJ_TRUE;
                if (*max_reslev < res_idx) {
                    *max_reslev = res_idx;
                }

                break;
            }
            binOffset += ptr->length;
            l++;
        }
        ptr = ptr->next;
    }
    if (!foundPrec && COD.Scod & 0x01) {
        j2kstream = add_padding(1, j2kstream, j2klen);
    }

    return j2kstream;
}


Byte_t * recons_precinct(msgqueue_param_t *msgqueue, Byte_t *jpipstream,
                         Byte_t *j2kstream, Byte8_t csn,
                         Byte8_t tileID, SIZmarker_param_t SIZ, CODmarker_param_t COD, int *max_reslev,
                         int comp_idx, int res_idx, Byte8_t seqID, Byte8_t *j2klen)
{
    Byte8_t precID, binOffset;
    message_param_t *ptr;
    OPJ_BOOL foundPrec;

    precID = comp_precinct_id((int)tileID, comp_idx, (int)seqID, (int)SIZ.Csiz,
                              (int)SIZ.XTnum * (int)SIZ.YTnum);

    ptr = msgqueue->first;
    binOffset = 0;
    foundPrec = OPJ_FALSE;

    while ((ptr = search_message(PRECINCT_MSG, precID, csn, ptr)) != NULL) {
        if (ptr->bin_offset == binOffset) {
            j2kstream = add_msgstream(ptr, jpipstream, j2kstream, j2klen);

            foundPrec = OPJ_TRUE;
            binOffset += ptr->length;
            if (*max_reslev < res_idx) {
                *max_reslev = res_idx;
            }

            if (ptr->last_byte) {
                break;
            }
        }
        ptr = ptr->next;
    }
    if (!foundPrec && COD.Scod & 0x01) {
        j2kstream = add_padding(COD.numOflayers, j2kstream, j2klen);
    }

    return j2kstream;
}

Byte8_t comp_seqID(Byte8_t tileID, SIZmarker_param_t SIZ, CODmarker_param_t COD,
                   int r, int p)
{
    Byte8_t seqID = 0;
    int rr;
    assert(p >= 0);
    assert(r >= 0);

    for (rr = 0; rr < r; rr++) {
        seqID += (Byte8_t)comp_numOfprcts(tileID, SIZ, COD, rr);
    }

    seqID += (Byte8_t)p;

    return seqID;
}

Byte8_t get_last_tileID(msgqueue_param_t *msgqueue, Byte8_t csn,
                        OPJ_BOOL isjppstream)
{
    Byte8_t last_tileID = 0;
    message_param_t *msg;

    msg = msgqueue->first;
    while (msg) {
        if (isjppstream) {
            if ((msg->class_id == TILE_HEADER_MSG) && msg->csn == csn &&
                    last_tileID < msg->in_class_id) {
                last_tileID = msg->in_class_id;
            }
        } else {
            if ((msg->class_id == TILE_MSG || msg->class_id == EXT_TILE_MSG) &&
                    msg->csn == csn && last_tileID < msg->in_class_id) {
                last_tileID = msg->in_class_id;
            }
        }
        msg = msg->next;
    }
    return last_tileID;
}


message_param_t * search_message(Byte8_t class_id, Byte8_t in_class_id,
                                 Byte8_t csn, message_param_t *msg)
{
    while (msg != NULL) {
        if (in_class_id == (Byte8_t) - 1) {
            if (msg->class_id == class_id && msg->csn == csn) {
                return msg;
            }
        } else {
            if (msg->class_id == class_id && msg->in_class_id == in_class_id &&
                    msg->csn == csn) {
                return msg;
            }
        }
        msg = msg->next;
    }
    return NULL;
}


Byte_t * gene_msgstream(message_param_t *message, Byte_t *stream,
                        Byte8_t *length);
Byte_t * gene_emptytilestream(const Byte8_t tileID, Byte8_t *length);

Byte_t * add_msgstream(message_param_t *message, Byte_t *origstream,
                       Byte_t *j2kstream, Byte8_t *j2klen)
{
    Byte_t *newstream;
    Byte8_t newlen;
    Byte_t *buf;

    if (!message) {
        return NULL;
    }

    newstream = gene_msgstream(message, origstream, &newlen);

    buf = (Byte_t *)opj_malloc((*j2klen) + newlen);

    memcpy(buf, j2kstream, *j2klen);
    memcpy(buf + (*j2klen), newstream, newlen);

    *j2klen += newlen;

    opj_free(newstream);
    if (j2kstream) {
        opj_free(j2kstream);
    }

    return buf;
}


Byte_t * add_emptyboxstream(placeholder_param_t *phld, Byte_t *jp2stream,
                            Byte8_t *jp2len)
{
    Byte_t *newstream;
    Byte8_t newlen;
    Byte_t *buf;

    if (phld->OrigBHlen == 8) {
        newlen = big4(phld->OrigBH);
    } else {
        newlen = big8(phld->OrigBH + 8);
    }

    newstream = (Byte_t *)opj_malloc(newlen);
    memset(newstream, 0, newlen);
    memcpy(newstream, phld->OrigBH, phld->OrigBHlen);

    buf = (Byte_t *)opj_malloc((*jp2len) + newlen);

    memcpy(buf, jp2stream, *jp2len);
    memcpy(buf + (*jp2len), newstream, newlen);

    *jp2len += newlen;

    opj_free(newstream);
    if (jp2stream) {
        opj_free(jp2stream);
    }

    return buf;
}

Byte_t * add_emptytilestream(const Byte8_t tileID, Byte_t *j2kstream,
                             Byte8_t *j2klen)
{
    Byte_t *newstream;
    Byte8_t newlen;
    Byte_t *buf;

    newstream = gene_emptytilestream(tileID, &newlen);

    buf = (Byte_t *)opj_malloc((*j2klen) + newlen);

    memcpy(buf, j2kstream, *j2klen);
    memcpy(buf + (*j2klen), newstream, newlen);

    *j2klen += newlen;

    opj_free(newstream);
    if (j2kstream) {
        opj_free(j2kstream);
    }

    return buf;
}

Byte_t * add_padding(Byte8_t padding, Byte_t *j2kstream, Byte8_t *j2klen)
{
    Byte_t *buf;

    buf = (Byte_t *)opj_malloc((*j2klen) + padding);

    memcpy(buf, j2kstream, *j2klen);
    memset(buf + (*j2klen), 0, padding);

    *j2klen += padding;

    if (j2kstream) {
        opj_free(j2kstream);
    }

    return buf;
}

Byte_t * add_EOC(Byte_t *j2kstream, Byte8_t *j2klen)
{
    Byte2_t EOC = 0xd9ff;

    Byte_t *buf;

    buf = (Byte_t *)opj_malloc((*j2klen) + 2);

    memcpy(buf, j2kstream, *j2klen);
    memcpy(buf + (*j2klen), &EOC, 2);

    *j2klen += 2;

    if (j2kstream) {
        opj_free(j2kstream);
    }

    return buf;
}

Byte_t * gene_msgstream(message_param_t *message, Byte_t *stream,
                        Byte8_t *length)
{
    Byte_t *buf;

    if (!message) {
        return NULL;
    }

    *length = message->length;
    buf = (Byte_t *)opj_malloc(*length);
    memcpy(buf, stream + message->res_offset,  *length);

    return buf;
}

Byte_t * gene_emptytilestream(const Byte8_t tileID, Byte8_t *length)
{
    Byte_t *buf;
    const Byte2_t SOT = 0x90ff;
    const Byte2_t Lsot = 0xa << 8;
    Byte2_t Isot;
    const Byte4_t Psot = 0xe << 24;
    const Byte_t TPsot = 0, TNsot = 1;
    const Byte2_t SOD = 0x93ff;

    *length = 14;
    buf = (Byte_t *)opj_malloc(*length);

    Isot = (Byte2_t)((((Byte2_t)tileID) << 8) | ((((Byte2_t)tileID) & 0xf0) >> 8));

    memcpy(buf, &SOT, 2);
    memcpy(buf + 2, &Lsot, 2);
    memcpy(buf + 4, &Isot, 2);
    memcpy(buf + 6, &Psot, 4);
    memcpy(buf + 10, &TPsot, 1);
    memcpy(buf + 11, &TNsot, 1);
    memcpy(buf + 12, &SOD, 2);

    return buf;
}

Byte_t * recons_j2kmainhead(msgqueue_param_t *msgqueue, Byte_t *jpipstream,
                            Byte8_t csn, Byte8_t *j2klen)
{
    *j2klen = 0;
    return add_mainhead_msgstream(msgqueue, jpipstream, NULL, csn, j2klen);
}
