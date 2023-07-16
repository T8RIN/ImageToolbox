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
#include "cachemodel_manager.h"
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


cachemodellist_param_t *gene_cachemodellist(void) {
    cachemodellist_param_t *cachemodellist;

    cachemodellist = (cachemodellist_param_t *) opj_malloc(sizeof(
                                                                   cachemodellist_param_t));

    cachemodellist->first = NULL;
    cachemodellist->last = NULL;

    return cachemodellist;
}

cachemodel_param_t *gene_cachemodel(cachemodellist_param_t *cachemodellist,
                                    target_param_t *target, OPJ_BOOL reqJPP) {
    cachemodel_param_t *cachemodel;
    faixbox_param_t *tilepart;
    faixbox_param_t *precpacket;
    size_t numOfelem;
    Byte8_t numOftiles;
    int i;

    cachemodel = (cachemodel_param_t *) opj_malloc(sizeof(cachemodel_param_t));

    refer_target(target, &cachemodel->target);

    if (reqJPP) {
        if (target->jppstream) {
            cachemodel->jppstream = OPJ_TRUE;
        } else {
            cachemodel->jppstream = OPJ_FALSE;
        }
    } else { /* reqJPT */
        if (target->jptstream) {
            cachemodel->jppstream = OPJ_FALSE;
        } else {
            cachemodel->jppstream = OPJ_TRUE;
        }
    }

    cachemodel->mhead_model = OPJ_FALSE;

    tilepart = target->codeidx->tilepart;
    numOftiles = get_m(tilepart);
    numOfelem = get_nmax(tilepart) * numOftiles;
    cachemodel->tp_model = (OPJ_BOOL *) opj_calloc(1, numOfelem * sizeof(OPJ_BOOL));
    cachemodel->th_model = (OPJ_BOOL *) opj_calloc(1, numOftiles * sizeof(OPJ_BOOL));
    cachemodel->pp_model = (OPJ_BOOL **) opj_malloc(target->codeidx->SIZ.Csiz *
                                                    sizeof(OPJ_BOOL * ));
    for (i = 0; i < target->codeidx->SIZ.Csiz; i++) {
        precpacket = target->codeidx->precpacket[i];
        cachemodel->pp_model[i] = (OPJ_BOOL *) opj_calloc(1,
                                                          get_nmax(precpacket) * get_m(precpacket) *
                                                          sizeof(OPJ_BOOL));
    }
    cachemodel->next = NULL;

    if (cachemodellist) {
        if (cachemodellist->first) { /* there are one or more entries */
            cachemodellist->last->next = cachemodel;
        } else {               /* first entry */
            cachemodellist->first = cachemodel;
        }
        cachemodellist->last = cachemodel;
    }

#ifndef SERVER
    fprintf(logstream, "local log: cachemodel generated\n");
#endif

    return cachemodel;
}

void print_cachemodel(cachemodel_param_t cachemodel) {
    target_param_t *target;
    Byte8_t TPnum; /* num of tile parts in each tile */
    Byte8_t Pmax; /* max num of packets per tile */
    Byte8_t i, j, k;
    int n; /* FIXME: Is this large enough ? */

    target = cachemodel.target;

    fprintf(logstream, "target: %s\n", target->targetname);
    fprintf(logstream, "\t main header model: %d\n", cachemodel.mhead_model);

    fprintf(logstream, "\t tile part model:\n");
    TPnum = get_nmax(target->codeidx->tilepart);

    for (i = 0, n = 0; i < target->codeidx->SIZ.YTnum; i++) {
        for (j = 0; j < target->codeidx->SIZ.XTnum; j++) {
            for (k = 0; k < TPnum; k++) {
                fprintf(logstream, "%d", cachemodel.tp_model[n++]);
            }
            fprintf(logstream, " ");
        }
        fprintf(logstream, "\n");
    }

    fprintf(logstream, "\t tile header and precinct packet model:\n");
    for (i = 0; i < target->codeidx->SIZ.XTnum * target->codeidx->SIZ.YTnum; i++) {
        fprintf(logstream, "\t  tile.%"
        PRIu64
        "  %d\n", i, cachemodel.th_model[i]);
        for (j = 0; j < target->codeidx->SIZ.Csiz; j++) {
            fprintf(logstream, "\t   compo.%"
            PRIu64
            ": ", j);
            Pmax = get_nmax(target->codeidx->precpacket[j]);
            for (k = 0; k < Pmax; k++) {
                fprintf(logstream, "%d", cachemodel.pp_model[j][i * Pmax + k]);
            }
            fprintf(logstream, "\n");
        }
    }
}

cachemodel_param_t *search_cachemodel(target_param_t *target,
                                      cachemodellist_param_t *cachemodellist) {
    cachemodel_param_t *foundcachemodel;

    foundcachemodel = cachemodellist->first;

    while (foundcachemodel != NULL) {

        if (foundcachemodel->target == target) {
            return foundcachemodel;
        }

        foundcachemodel = foundcachemodel->next;
    }
    return NULL;
}

void delete_cachemodellist(cachemodellist_param_t **cachemodellist) {
    cachemodel_param_t *cachemodelPtr, *cachemodelNext;

    cachemodelPtr = (*cachemodellist)->first;
    while (cachemodelPtr != NULL) {
        cachemodelNext = cachemodelPtr->next;
        delete_cachemodel(&cachemodelPtr);
        cachemodelPtr = cachemodelNext;
    }
    opj_free(*cachemodellist);
}

void delete_cachemodel(cachemodel_param_t **cachemodel) {
    int i;

    unrefer_target((*cachemodel)->target);

    opj_free((*cachemodel)->tp_model);
    opj_free((*cachemodel)->th_model);

    for (i = 0; i < (*cachemodel)->target->codeidx->SIZ.Csiz; i++) {
        opj_free((*cachemodel)->pp_model[i]);
    }
    opj_free((*cachemodel)->pp_model);

#ifndef SERVER
    fprintf(logstream, "local log: cachemodel deleted\n");
#endif
    opj_free(*cachemodel);
}

OPJ_BOOL is_allsent(cachemodel_param_t cachemodel) {
    target_param_t *target;
    Byte8_t TPnum; /* num of tile parts in each tile */
    Byte8_t Pmax; /* max num of packets per tile */
    Byte8_t i, j, k;
    int n; /* FIXME: is this large enough ? */

    target = cachemodel.target;

    if (!cachemodel.mhead_model) {
        return OPJ_FALSE;
    }

    TPnum = get_nmax(target->codeidx->tilepart);

    if (cachemodel.jppstream) {
        for (i = 0; i < target->codeidx->SIZ.XTnum * target->codeidx->SIZ.YTnum; i++) {
            if (!cachemodel.th_model[i]) {
                return OPJ_FALSE;
            }

            for (j = 0; j < target->codeidx->SIZ.Csiz; j++) {
                Pmax = get_nmax(target->codeidx->precpacket[j]);
                for (k = 0; k < Pmax; k++)
                    if (!cachemodel.pp_model[j][i * Pmax + k]) {
                        return OPJ_FALSE;
                    }
            }
        }
        return OPJ_TRUE;
    } else {
        for (i = 0, n = 0; i < target->codeidx->SIZ.YTnum; i++)
            for (j = 0; j < target->codeidx->SIZ.XTnum; j++)
                for (k = 0; k < TPnum; k++)
                    if (!cachemodel.tp_model[n++]) {
                        return OPJ_FALSE;
                    }
        return OPJ_TRUE;
    }
}
