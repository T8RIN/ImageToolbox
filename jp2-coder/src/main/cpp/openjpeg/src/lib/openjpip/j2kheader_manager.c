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
#include "j2kheader_manager.h"

#ifdef SERVER
#include "fcgi_stdio.h"
#define logstream FCGI_stdout
#else
#define FCGI_stdout stdout
#define FCGI_stderr stderr
#define logstream stderr
#endif /*SERVER */


SIZmarker_param_t get_SIZmkrdata_from_j2kstream(Byte_t *SIZstream);
CODmarker_param_t get_CODmkrdata_from_j2kstream(Byte_t *CODstream);

OPJ_BOOL get_mainheader_from_j2kstream(Byte_t *j2kstream,
                                       SIZmarker_param_t *SIZ, CODmarker_param_t *COD)
{
    if (*j2kstream++ != 0xff || *j2kstream++ != 0x4f) {
        fprintf(FCGI_stderr, "Error, j2kstream is not starting with SOC marker\n");
        return OPJ_FALSE;
    }

    if (SIZ) {
        *SIZ = get_SIZmkrdata_from_j2kstream(j2kstream);
        if (SIZ->Lsiz == 0) {
            return OPJ_FALSE;
        }

        j2kstream += (SIZ->Lsiz + 2);
    }

    if (COD) {
        if (!SIZ) {
            j2kstream += (big2(j2kstream + 2) + 2);
        }

        *COD = get_CODmkrdata_from_j2kstream(j2kstream);
        if (COD->Lcod == 0) {
            return OPJ_FALSE;
        }
    }
    return OPJ_TRUE;
}

SIZmarker_param_t get_SIZmkrdata_from_j2kstream(Byte_t *SIZstream)
{
    SIZmarker_param_t SIZ;
    int i;

    if (*SIZstream++ != 0xff || *SIZstream++ != 0x51) {
        fprintf(FCGI_stderr,
                "Error, SIZ marker not found in the reconstructed j2kstream\n");
        memset(&SIZ, 0, sizeof(SIZ));
        return SIZ;
    }

    SIZ.Lsiz   = big2(SIZstream);
    SIZ.Rsiz   = big2(SIZstream + 2);
    SIZ.Xsiz   = big4(SIZstream + 4);
    SIZ.Ysiz   = big4(SIZstream + 8);
    SIZ.XOsiz  = big4(SIZstream + 12);
    SIZ.YOsiz  = big4(SIZstream + 16);
    SIZ.XTsiz  = big4(SIZstream + 20);
    SIZ.YTsiz  = big4(SIZstream + 24);
    SIZ.XTOsiz = big4(SIZstream + 28);
    SIZ.YTOsiz = big4(SIZstream + 32);
    SIZ.Csiz   = big2(SIZstream + 36);

    SIZ.XTnum  = (SIZ.Xsiz - SIZ.XTOsiz + SIZ.XTsiz - 1) / SIZ.XTsiz;
    SIZ.YTnum  = (SIZ.Ysiz - SIZ.YTOsiz + SIZ.YTsiz - 1) / SIZ.YTsiz;

    for (i = 0; i < (int)SIZ.Csiz; i++) {
        SIZ.Ssiz[i]  = *(SIZstream + (38 + i * 3));
        SIZ.XRsiz[i] = *(SIZstream + (39 + i * 3));
        SIZ.YRsiz[i] = *(SIZstream + (40 + i * 3));
    }

    return SIZ;
}

CODmarker_param_t get_CODmkrdata_from_j2kstream(Byte_t *CODstream)
{
    CODmarker_param_t COD;
    int i;

    if (*CODstream++ != 0xff || *CODstream++ != 0x52) {
        fprintf(FCGI_stderr,
                "Error, COD marker not found in the reconstructed j2kstream\n");
        memset(&COD, 0, sizeof(COD));
        return COD;
    }

    COD.Lcod = big2(CODstream);
    COD.Scod = *(CODstream + 2);
    COD.prog_order = *(CODstream + 3);
    COD.numOflayers = big2(CODstream + 4);
    COD.numOfdecomp = *(CODstream + 7);

    if (COD.Scod & 0x01) {
        COD.XPsiz = (Byte4_t *)opj_malloc((OPJ_SIZE_T)(COD.numOfdecomp + 1) * sizeof(
                                              Byte4_t));
        COD.YPsiz = (Byte4_t *)opj_malloc((OPJ_SIZE_T)(COD.numOfdecomp + 1) * sizeof(
                                              Byte4_t));

        for (i = 0; i <= COD.numOfdecomp; i++) {
            /*precinct size */
            COD.XPsiz[i] = (Byte4_t)pow(2, *(CODstream + 12 + i) & 0x0F);
            COD.YPsiz[i] = (Byte4_t)pow(2, (*(CODstream + 12 + i) & 0xF0) >> 4);
        }
    } else {
        COD.XPsiz = (Byte4_t *)opj_malloc(sizeof(Byte4_t));
        COD.YPsiz = (Byte4_t *)opj_malloc(sizeof(Byte4_t));
        COD.XPsiz[0] = COD.YPsiz[0] = 1 << 15; /*pow(2,15)*/
    }
    return COD;
}


OPJ_BOOL modify_SIZmkrstream(SIZmarker_param_t SIZ, int difOfdecomplev,
                             Byte_t *SIZstream);
Byte2_t modify_CODmkrstream(CODmarker_param_t COD, int numOfdecomp,
                            Byte_t *CODstream);

OPJ_BOOL modify_mainheader(Byte_t *j2kstream, int numOfdecomp,
                           SIZmarker_param_t SIZ, CODmarker_param_t COD, Byte8_t *j2klen)
{
    Byte2_t newLcod;

    if (*j2kstream++ != 0xff || *j2kstream++ != 0x4f) {
        fprintf(FCGI_stderr, "Error, j2kstream is not starting with SOC marker\n");
        return OPJ_FALSE;
    }

    if (!modify_SIZmkrstream(SIZ, COD.numOfdecomp - numOfdecomp, j2kstream)) {
        return OPJ_FALSE;
    }

    j2kstream += SIZ.Lsiz + 2;
    if (!(newLcod = modify_CODmkrstream(COD, numOfdecomp, j2kstream))) {
        return OPJ_FALSE;
    }

    memmove(j2kstream + 2 + newLcod, j2kstream + 2 + COD.Lcod,
            *j2klen - (Byte8_t)(SIZ.Lsiz + COD.Lcod + 6));
    *j2klen -= (Byte8_t)(COD.Lcod - newLcod);

    return OPJ_TRUE;
}

OPJ_BOOL modify_SIZmkrstream(SIZmarker_param_t SIZ, int difOfdecomplev,
                             Byte_t *SIZstream)
{
    int i;

    if (*SIZstream++ != 0xff || *SIZstream++ != 0x51) {
        fprintf(FCGI_stderr,
                "Error, SIZ marker not found in the reconstructed j2kstream\n");
        return OPJ_FALSE;
    }

    for (i = 0; i < difOfdecomplev; i++) {
        SIZ.Xsiz   = (Byte4_t)ceil((double)SIZ.Xsiz / 2.0);
        SIZ.Ysiz   = (Byte4_t)ceil((double)SIZ.Ysiz / 2.0);
        SIZ.XOsiz  = (Byte4_t)ceil((double)SIZ.XOsiz / 2.0);
        SIZ.YOsiz  = (Byte4_t)ceil((double)SIZ.YOsiz / 2.0);
        SIZ.XTsiz  = (Byte4_t)ceil((double)SIZ.XTsiz / 2.0);
        SIZ.YTsiz  = (Byte4_t)ceil((double)SIZ.YTsiz / 2.0);
        SIZ.XTOsiz = (Byte4_t)ceil((double)SIZ.XTOsiz / 2.0);
        SIZ.YTOsiz = (Byte4_t)ceil((double)SIZ.YTOsiz / 2.0);
    }

    SIZstream += 4; /* skip Lsiz + Rsiz */

    modify_4Bytecode(SIZ.Xsiz,   SIZstream);
    modify_4Bytecode(SIZ.Ysiz,   SIZstream + 4);
    modify_4Bytecode(SIZ.XOsiz,  SIZstream + 8);
    modify_4Bytecode(SIZ.YOsiz,  SIZstream + 12);
    modify_4Bytecode(SIZ.XTsiz,  SIZstream + 16);
    modify_4Bytecode(SIZ.YTsiz,  SIZstream + 20);
    modify_4Bytecode(SIZ.XTOsiz, SIZstream + 24);
    modify_4Bytecode(SIZ.YTOsiz, SIZstream + 28);

    return OPJ_TRUE;
}

Byte2_t modify_CODmkrstream(CODmarker_param_t COD, int numOfdecomp,
                            Byte_t *CODstream)
{
    Byte2_t newLcod;

    assert(numOfdecomp >= 0 || numOfdecomp <= 255);
    if (*CODstream++ != 0xff || *CODstream++ != 0x52) {
        fprintf(FCGI_stderr,
                "Error, COD marker not found in the reconstructed j2kstream\n");
        return 0;
    }

    if (COD.Scod & 0x01) {
        newLcod  = (Byte2_t)(13 + numOfdecomp);

        *CODstream++ = (Byte_t)((Byte2_t)(newLcod & 0xff00) >> 8);
        *CODstream++ = (Byte_t)(newLcod & 0x00ff);
    } else {
        newLcod = COD.Lcod;
        CODstream += 2;
    }

    CODstream += 5; /* skip Scod & SGcod */

    /* SPcod */
    *CODstream++ = (Byte_t) numOfdecomp;

    return newLcod;
}

OPJ_BOOL modify_COCmkrstream(int numOfdecomp, Byte_t *COCstream, Byte2_t Csiz,
                             Byte2_t *oldLcoc, Byte2_t *newLcoc);

OPJ_BOOL modify_tileheader(Byte_t *j2kstream, Byte8_t SOToffset,
                           int numOfdecomp, Byte2_t Csiz, Byte8_t *j2klen)
{
    Byte4_t Psot; /* tile part length ref A.4.2 Start of tile-part SOT */
    Byte_t *thstream, *SOTstream, *Psot_stream;
    Byte2_t oldLcoc, newLcoc;

    SOTstream = thstream = j2kstream + SOToffset;

    if (*SOTstream++ != 0xff || *SOTstream++ != 0x90) {
        fprintf(FCGI_stderr, "Error, thstream is not starting with SOT marker\n");
        return OPJ_FALSE;
    }

    SOTstream += 4; /* skip Lsot & Isot */
    Psot = (Byte4_t)((SOTstream[0] << 24) + (SOTstream[1] << 16) +
                     (SOTstream[2] << 8) + (SOTstream[3]));
    Psot_stream = SOTstream;

    thstream += 12; /* move to next marker (SOT always 12bytes) */

    while (!(*thstream == 0xff && *(thstream + 1) == 0x93)) { /* search SOD */
        if (numOfdecomp != -1 && *thstream == 0xff &&
                *(thstream + 1) == 0x53) { /* COC */
            if (!modify_COCmkrstream(numOfdecomp, thstream, Csiz, &oldLcoc, &newLcoc)) {
                return OPJ_FALSE;
            }

            memmove(thstream + newLcoc + 2, thstream + oldLcoc + 2,
                    *j2klen - (Byte8_t)(thstream - j2kstream + oldLcoc + 2));
            *j2klen -= (Byte8_t)(oldLcoc - newLcoc);
        }
        thstream += 2;
        thstream += ((thstream[0] << 8) + (thstream[1])); /* marker length */
    }

    if ((*j2klen) - SOToffset != Psot) {
        Psot = (Byte4_t)((*j2klen) - SOToffset);
        modify_4Bytecode(Psot, Psot_stream);
    }
    return OPJ_TRUE;
}

OPJ_BOOL modify_COCmkrstream(int numOfdecomp, Byte_t *COCstream, Byte2_t Csiz,
                             Byte2_t *oldLcoc, Byte2_t *newLcoc)
{
    if (numOfdecomp < 0 || numOfdecomp > 255) {
        return OPJ_FALSE;
    }
    if (*COCstream++ != 0xff || *COCstream++ != 0x53) {
        fprintf(FCGI_stderr,
                "Error, COC marker not found in the reconstructed j2kstream\n");
        return OPJ_FALSE;
    }

    *oldLcoc = big2(COCstream);
    *newLcoc = (Byte2_t)((Csiz < 257 ? 10 : 11) + numOfdecomp);
    *COCstream++ = (Byte_t)((Byte2_t)((*newLcoc) & 0xff00) >> 8);
    *COCstream++ = (Byte_t)((*newLcoc) & 0x00ff);

    if (Csiz < 257) {
        COCstream += 2;    /* skip Ccoc & Scoc */
    } else {
        COCstream += 3;
    }

    *COCstream = (Byte_t)numOfdecomp;

    return OPJ_TRUE;
}
