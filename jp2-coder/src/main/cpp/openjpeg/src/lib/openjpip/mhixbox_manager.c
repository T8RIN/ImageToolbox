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
#include "mhixbox_manager.h"
#include "opj_inttypes.h"

#ifdef SERVER
#include "fcgi_stdio.h"
#define logstream FCGI_stdout
#else
#define FCGI_stdout stdout
#define FCGI_stderr stderr
#define logstream stderr
#endif /*SERVER */


mhixbox_param_t * gene_mhixbox(box_param_t *box)
{
    mhixbox_param_t *mhix;
    markeridx_param_t  *mkridx, *lastmkidx;
    OPJ_OFF_T pos = 0;

    mhix = (mhixbox_param_t *)malloc(sizeof(mhixbox_param_t));

    mhix->tlen = fetch_DBox8bytebigendian(box, (pos += 8) - 8);

    mhix->first = lastmkidx = NULL;
    while ((OPJ_SIZE_T)pos < get_DBoxlen(box)) {

        mkridx = (markeridx_param_t *)malloc(sizeof(markeridx_param_t));
        mkridx->code       = fetch_DBox2bytebigendian(box, (pos += 2) - 2);
        mkridx->num_remain = fetch_DBox2bytebigendian(box, (pos += 2) - 2);
        mkridx->offset     = (OPJ_OFF_T)fetch_DBox8bytebigendian(box, (pos += 8) - 8);
        mkridx->length     = fetch_DBox2bytebigendian(box, (pos += 2) - 2);
        mkridx->next = NULL;

        if (mhix->first) {
            lastmkidx->next = mkridx;
        } else {
            mhix->first = mkridx;
        }
        lastmkidx = mkridx;
    }
    return mhix;
}


markeridx_param_t * search_markeridx(Byte2_t code, mhixbox_param_t *mhix)
{
    markeridx_param_t *found;

    found = mhix->first;

    while (found != NULL) {

        if (code == found->code) {
            return found;
        }

        found = found->next;
    }
    fprintf(FCGI_stderr, "Error: Marker index %#x not found\n", code);

    return NULL;
}


void print_mhixbox(mhixbox_param_t *mhix)
{
    markeridx_param_t *ptr;

    fprintf(logstream, "mhix box info:\n");
    fprintf(logstream, "\t tlen: %#" PRIx64 "\n", mhix->tlen);

    ptr = mhix->first;
    while (ptr != NULL) {
        fprintf(logstream, "marker index info:\n"
                "\t code: %#x\n"
                "\t num_remain: %#x\n"
                "\t offset: %#" PRIx64 "\n"
                "\t length: %#x\n", ptr->code, ptr->num_remain, ptr->offset, ptr->length);
        ptr = ptr->next;
    }
}


void print_markeridx(markeridx_param_t *markeridx)
{
    fprintf(logstream, "marker index info:\n"
            "\t code: %#x\n"
            "\t num_remain: %#x\n"
            "\t offset: %#" PRIx64 "\n"
            "\t length: %#x\n", markeridx->code, markeridx->num_remain, markeridx->offset,
            markeridx->length);
}


void delete_mhixbox(mhixbox_param_t **mhix)
{
    markeridx_param_t *mkPtr, *mkNext;

    mkPtr = (*mhix)->first;
    while (mkPtr != NULL) {
        mkNext = mkPtr->next;
#ifndef SERVER
        /*      fprintf( logstream, "local log: marker index %#x deleted!\n", mkPtr->code); */
#endif
        free(mkPtr);
        mkPtr = mkNext;
    }
    free(*mhix);
}


