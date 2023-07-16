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
#include "faixbox_manager.h"
#include "opj_inttypes.h"

#ifdef SERVER
#include "fcgi_stdio.h"
#define logstream FCGI_stdout
#else
#define FCGI_stdout stdout
#define FCGI_stderr stderr
#define logstream stderr
#endif /*SERVER*/

faixbox_param_t * gene_faixbox(box_param_t *box)
{
    faixbox_param_t *faix;
    size_t numOfelem;
    long pos = 0;

    faix = (faixbox_param_t *)malloc(sizeof(faixbox_param_t));

    faix->version = fetch_DBox1byte(box, (pos += 1) - 1);

    if (3 < faix->version) {
        fprintf(FCGI_stderr, "Error: version %d in faix box is reserved for ISO use.\n",
                faix->version);
        free(faix);
        return NULL;
    }

    if (faix->version % 2) {
        subfaixbox8_param_t *subfaixbox;
        size_t i;

        faix->subfaixbox.byte8_params = (subfaixbox8_param_t *)malloc(sizeof(
                                            subfaixbox8_param_t));

        subfaixbox = faix->subfaixbox.byte8_params;
        subfaixbox->nmax = fetch_DBox8bytebigendian(box, (pos += 8) - 8);
        subfaixbox->m    = fetch_DBox8bytebigendian(box, (pos += 8) - 8);

        numOfelem = subfaixbox->nmax * subfaixbox->m;

        subfaixbox->elem = (faixelem8_param_t *)malloc(numOfelem * sizeof(
                               faixelem8_param_t));

        if (faix->version == 3) {
            subfaixbox->aux = (Byte4_t *)malloc(numOfelem * sizeof(Byte4_t));
        }

        for (i = 0; i < numOfelem; i++) {
            subfaixbox->elem[i].off = fetch_DBox8bytebigendian(box, (pos += 8) - 8);
            subfaixbox->elem[i].len = fetch_DBox8bytebigendian(box, (pos += 8) - 8);
            if (faix->version == 3) {
                subfaixbox->aux[i] = fetch_DBox4bytebigendian(box, (pos += 4) - 4);
            }
        }
    } else {
        subfaixbox4_param_t *subfaixbox;
        size_t i;

        faix->subfaixbox.byte4_params = (subfaixbox4_param_t *)malloc(sizeof(
                                            subfaixbox4_param_t));

        subfaixbox = faix->subfaixbox.byte4_params;
        subfaixbox->nmax = fetch_DBox4bytebigendian(box, (pos += 4) - 4);
        subfaixbox->m    = fetch_DBox4bytebigendian(box, (pos += 4) - 4);

        numOfelem = subfaixbox->nmax * subfaixbox->m;

        subfaixbox->elem = (faixelem4_param_t *)malloc(numOfelem * sizeof(
                               faixelem4_param_t));

        if (faix->version == 2) {
            subfaixbox->aux = (Byte4_t *)malloc(numOfelem * sizeof(Byte4_t));
        }

        for (i = 0; i < numOfelem; i++) {
            subfaixbox->elem[i].off = fetch_DBox4bytebigendian(box, (pos += 4) - 4);
            subfaixbox->elem[i].len = fetch_DBox4bytebigendian(box, (pos += 4) - 4);
            if (faix->version == 2) {
                subfaixbox->aux[i] = fetch_DBox4bytebigendian(box, (pos += 4) - 4);
            }
        }
    }
    return faix;
}

void print_faixbox(faixbox_param_t *faix)
{
    Byte8_t i, j;

    fprintf(logstream, "faix box info\n");
    fprintf(logstream, "\tversion: %d\n", faix->version);

    fprintf(logstream, "\t nmax: %#" PRIx64 " = %" PRId64 "\n", get_nmax(faix),
            get_nmax(faix));
    fprintf(logstream, "\t m: %#" PRIx64 " = %" PRId64 "\n", get_m(faix),
            get_m(faix));

    for (i = 0; i < get_m(faix); i++) {
        for (j = 0; j < get_nmax(faix); j++) {
            fprintf(logstream, "\t off = %#" PRIx64 ", len = %#" PRIx64 "",
                    get_elemOff(faix, j, i), get_elemLen(faix, j, i));
            if (2 <= faix->version) {
                fprintf(logstream, ", aux = %#x", get_elemAux(faix, j, i));
            }
            fprintf(logstream, "\n");
        }
        fprintf(logstream, "\n");
    }
}

void delete_faixbox(faixbox_param_t **faix)
{
    if ((*faix)->version % 2) {
        free((*faix)->subfaixbox.byte8_params->elem);
        if ((*faix)->version == 3) {
            free((*faix)->subfaixbox.byte8_params->aux);
        }
        free((*faix)->subfaixbox.byte8_params);
    } else {
        free((*faix)->subfaixbox.byte4_params->elem);
        if ((*faix)->version == 2) {
            free((*faix)->subfaixbox.byte4_params->aux);
        }
        free((*faix)->subfaixbox.byte4_params);
    }
    free(*faix);
}

Byte8_t get_nmax(faixbox_param_t *faix)
{
    if (faix->version % 2) {
        return faix->subfaixbox.byte8_params->nmax;
    } else {
        return (Byte8_t)faix->subfaixbox.byte4_params->nmax;
    }
}

Byte8_t get_m(faixbox_param_t *faix)
{
    if (faix->version % 2) {
        return faix->subfaixbox.byte8_params->m;
    } else {
        return (Byte8_t)faix->subfaixbox.byte4_params->m;
    }
}

Byte8_t get_elemOff(faixbox_param_t *faix, Byte8_t elem_id, Byte8_t row_id)
{
    Byte8_t nmax = get_nmax(faix);
    if (faix->version % 2) {
        return faix->subfaixbox.byte8_params->elem[ row_id * nmax + elem_id].off;
    } else {
        return (Byte8_t)faix->subfaixbox.byte4_params->elem[ row_id * nmax +
                       elem_id].off;
    }
}

Byte8_t get_elemLen(faixbox_param_t *faix, Byte8_t elem_id, Byte8_t row_id)
{
    Byte8_t nmax = get_nmax(faix);
    if (faix->version % 2) {
        return faix->subfaixbox.byte8_params->elem[ row_id * nmax + elem_id].len;
    } else {
        return (Byte8_t)faix->subfaixbox.byte4_params->elem[ row_id * nmax +
                       elem_id].len;
    }
}

Byte4_t get_elemAux(faixbox_param_t *faix, Byte8_t elem_id, Byte8_t row_id)
{
    Byte8_t nmax;
    if (faix->version < 2) {
        return (Byte4_t) - 1;
    }

    nmax = get_nmax(faix);
    if (faix->version % 2) {
        return faix->subfaixbox.byte8_params->aux[ row_id * nmax + elem_id];
    } else {
        return faix->subfaixbox.byte4_params->aux[ row_id * nmax + elem_id];
    }
}
