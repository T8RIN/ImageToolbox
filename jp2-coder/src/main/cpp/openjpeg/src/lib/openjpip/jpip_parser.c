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
#include <string.h>
#include <math.h>
#include "jpip_parser.h"
#include "channel_manager.h"
#include "imgreg_manager.h"

#ifdef SERVER
#include "fcgi_stdio.h"
#define logstream FCGI_stdout
#else
#define FCGI_stdout stdout
#define FCGI_stderr stderr
#define logstream stderr
#endif /*SERVER*/


OPJ_BOOL identify_target(query_param_t query_param,
                         targetlist_param_t *targetlist, target_param_t **target)
{
    if (query_param.tid) {
        if (strcmp(query_param.tid, "0") != 0) {
            if (query_param.cid[0] != '\0') {
                fprintf(FCGI_stdout,
                        "Reason: Target can not be specified both through tid and cid\r\n");
                fprintf(FCGI_stdout, "Status: 400\r\n");
                return OPJ_FALSE;
            }
            if ((*target = search_targetBytid(query_param.tid, targetlist))) {
                return OPJ_TRUE;
            }
        }
    }

    if (query_param.target)
        if (!(*target = search_target(query_param.target, targetlist)))
            if (!(*target = gene_target(targetlist, query_param.target))) {
                return OPJ_FALSE;
            }

    if (*target) {
        fprintf(FCGI_stdout, "JPIP-tid: %s\r\n", (*target)->tid);
        return OPJ_TRUE;
    } else {
        fprintf(FCGI_stdout, "Reason: target not found\r\n");
        fprintf(FCGI_stdout, "Status: 400\r\n");
        return OPJ_FALSE;
    }
}

OPJ_BOOL associate_channel(query_param_t    query_param,
                           sessionlist_param_t *sessionlist,
                           session_param_t **cursession,
                           channel_param_t **curchannel)
{
    if (search_session_and_channel(query_param.cid, sessionlist, cursession,
                                   curchannel)) {

        if (!query_param.cnew) {
            set_channel_variable_param(query_param, *curchannel);
        }
    } else {
        fprintf(FCGI_stderr, "Error: process canceled\n");
        return OPJ_FALSE;
    }
    return OPJ_TRUE;
}

OPJ_BOOL open_channel(query_param_t query_param,
                      sessionlist_param_t *sessionlist,
                      auxtrans_param_t auxtrans,
                      target_param_t *target,
                      session_param_t **cursession,
                      channel_param_t **curchannel)
{
    cachemodel_param_t *cachemodel = NULL;

    if (target) {
        if (!(*cursession)) {
            *cursession = gene_session(sessionlist);
        }
        if (!(cachemodel = search_cachemodel(target, (*cursession)->cachemodellist)))
            if (!(cachemodel = gene_cachemodel((*cursession)->cachemodellist, target,
                                               query_param.return_type == JPPstream))) {
                return OPJ_FALSE;
            }
    } else if (*curchannel) {
        cachemodel = (*curchannel)->cachemodel;
    }

    *curchannel = gene_channel(query_param, auxtrans, cachemodel,
                               (*cursession)->channellist);
    if (*curchannel == NULL) {
        return OPJ_FALSE;
    }

    return OPJ_TRUE;
}

OPJ_BOOL close_channel(query_param_t query_param,
                       sessionlist_param_t *sessionlist,
                       session_param_t **cursession,
                       channel_param_t **curchannel)
{
    char *cclose;
    int i;

    if (query_param.cclose[0] == '*') {
#ifndef SERVER
        fprintf(logstream, "local log: close all\n");
#endif
        /* all channels associatd with the session will be closed */
        if (!delete_session(cursession, sessionlist)) {
            return OPJ_FALSE;
        }
    } else {
        /* check if all entry belonging to the same session */

        for (i = 0, cclose = query_param.cclose; i < query_param.numOfcclose;
                i++, cclose += (strlen(cclose) + 1)) {

            /* In case of the first entry of close cid */
            if (*cursession == NULL) {
                if (!search_session_and_channel(cclose, sessionlist, cursession, curchannel)) {
                    return OPJ_FALSE;
                }
            } else /* second or more entry of close cid */
                if (!(*curchannel = search_channel(cclose, (*cursession)->channellist))) {
                    fprintf(FCGI_stdout, "Reason: Cclose id %s is from another session\r\n",
                            cclose);
                    return OPJ_FALSE;
                }
        }

        /* delete channels */
        for (i = 0, cclose = query_param.cclose; i < query_param.numOfcclose;
                i++, cclose += (strlen(cclose) + 1)) {
            *curchannel = search_channel(cclose, (*cursession)->channellist);
            delete_channel(curchannel, (*cursession)->channellist);
        }

        if ((*cursession)->channellist->first == NULL ||
                (*cursession)->channellist->last == NULL)
            /* In case of empty session */
        {
            delete_session(cursession, sessionlist);
        }
    }
    return OPJ_TRUE;
}


/**
 * enqueue tiles or precincts into the message queue
 *
 * @param[in] query_param structured query
 * @param[in] msgqueue    message queue pointer
 */
void enqueue_imagedata(query_param_t query_param, msgqueue_param_t *msgqueue);

/**
 * enqueue metadata bins into the message queue
 *
 * @param[in]     query_param  structured query
 * @param[in]     metadatalist pointer to metadata bin list
 * @param[in,out] msgqueue     message queue pointer
 * @return                     if succeeded (true) or failed (false)
 */
OPJ_BOOL enqueue_metabins(query_param_t query_param,
                          metadatalist_param_t *metadatalist, msgqueue_param_t *msgqueue);


OPJ_BOOL gene_JPIPstream(query_param_t query_param,
                         target_param_t *target,
                         session_param_t *cursession,
                         channel_param_t *curchannel,
                         msgqueue_param_t **msgqueue)
{
    index_param_t *codeidx;
    cachemodel_param_t *cachemodel;

    if (!cursession || !curchannel) { /* stateless */
        if (!target) {
            return OPJ_FALSE;
        }
        if (!(cachemodel = gene_cachemodel(NULL, target,
                                           query_param.return_type == JPPstream))) {
            return OPJ_FALSE;
        }
        *msgqueue = gene_msgqueue(OPJ_TRUE, cachemodel);
    } else { /* session */
        cachemodel  = curchannel->cachemodel;
        target = cachemodel->target;
        *msgqueue = gene_msgqueue(OPJ_FALSE, cachemodel);
    }

    codeidx = target->codeidx;

    if (cachemodel->jppstream) {
        fprintf(FCGI_stdout, "Content-type: image/jpp-stream\r\n");
    } else {
        fprintf(FCGI_stdout, "Content-type: image/jpt-stream\r\n");
    }

    if (query_param.layers != -1) {
        if (query_param.layers  > codeidx->COD.numOflayers) {
            fprintf(FCGI_stdout, "JPIP-layers: %d\r\n", codeidx->COD.numOflayers);
            query_param.layers = codeidx->COD.numOflayers;
        }
    }

    /*meta*/
    if (query_param.box_type[0][0] != 0  && query_param.len != 0)
        if (!enqueue_metabins(query_param, codeidx->metadatalist, *msgqueue)) {
            return OPJ_FALSE;
        }

    if (query_param.metadata_only) {
        return OPJ_TRUE;
    }

    /* main header */
    if (!cachemodel->mhead_model && query_param.len != 0) {
        enqueue_mainheader(*msgqueue);
    }

    /* image codestream */
    if ((query_param.fx > 0 && query_param.fy > 0)) {
        enqueue_imagedata(query_param, *msgqueue);
    }

    return OPJ_TRUE;
}


/**
 * enqueue precinct data-bins into the queue
 *
 * @param[in] xmin      min x coordinate in the tile at the decomposition level
 * @param[in] xmax      max x coordinate in the tile at the decomposition level
 * @param[in] ymin      min y coordinate in the tile at the decomposition level
 * @param[in] ymax      max y coordinate in the tile at the decomposition level
 * @param[in] tile_id   tile index
 * @param[in] level     decomposition level
 * @param[in] lastcomp  last component number
 * @param[in] comps     pointer to the array that stores the requested components
 * @param[in] layers    number of quality layers
 * @param[in] msgqueue  message queue
 * @return
 */
void enqueue_precincts(int xmin, int xmax, int ymin, int ymax, int tile_id,
                       int level, int lastcomp, OPJ_BOOL *comps, int layers,
                       msgqueue_param_t *msgqueue);

/**
 * enqueue all precincts inside a tile into the queue
 *
 * @param[in] tile_id   tile index
 * @param[in] level     decomposition level
 * @param[in] lastcomp  last component number
 * @param[in] comps     pointer to the array that stores the requested components
 * @param[in] layers    number of quality layers
 * @param[in] msgqueue  message queue
 * @return
 */
void enqueue_allprecincts(int tile_id, int level, int lastcomp, OPJ_BOOL *comps,
                          int layers, msgqueue_param_t *msgqueue);

void enqueue_imagedata(query_param_t query_param, msgqueue_param_t *msgqueue)
{
    index_param_t *codeidx;
    imgreg_param_t imgreg;
    range_param_t tile_Xrange, tile_Yrange;
    Byte4_t u, v, tile_id;
    int xmin, xmax, ymin, ymax;
    int numOfreslev;

    codeidx = msgqueue->cachemodel->target->codeidx;

    if (!(msgqueue->cachemodel->jppstream)  &&
            get_nmax(codeidx->tilepart) == 1) { /* normally not the case */
        numOfreslev = 1;
    } else {
        numOfreslev = codeidx->COD.numOfdecomp + 1;
    }

    imgreg  = map_viewin2imgreg(query_param.fx, query_param.fy,
                                query_param.rx, query_param.ry, query_param.rw, query_param.rh,
                                (int)codeidx->SIZ.XOsiz, (int)codeidx->SIZ.YOsiz, (int)codeidx->SIZ.Xsiz,
                                (int)codeidx->SIZ.Ysiz,
                                numOfreslev);

    if (query_param.len == 0) {
        return;
    }

    for (u = 0, tile_id = 0; u < codeidx->SIZ.YTnum; u++) {
        tile_Yrange = get_tile_Yrange(codeidx->SIZ, tile_id, imgreg.level);

        for (v = 0; v < codeidx->SIZ.XTnum; v++, tile_id++) {
            tile_Xrange = get_tile_Xrange(codeidx->SIZ, tile_id, imgreg.level);

            if (tile_Xrange.minvalue < tile_Xrange.maxvalue &&
                    tile_Yrange.minvalue < tile_Yrange.maxvalue) {
                if (tile_Xrange.maxvalue <= (Byte4_t)(imgreg.xosiz + imgreg.ox) ||
                        tile_Xrange.minvalue >= (Byte4_t)(imgreg.xosiz + imgreg.ox + imgreg.sx) ||
                        tile_Yrange.maxvalue <= (Byte4_t)(imgreg.yosiz + imgreg.oy) ||
                        tile_Yrange.minvalue >= (Byte4_t)(imgreg.yosiz + imgreg.oy + imgreg.sy)) {
                    /*printf("Tile completely excluded from view-window %d\n", tile_id);*/
                    /* Tile completely excluded from view-window */
                } else if (tile_Xrange.minvalue >= (Byte4_t)(imgreg.xosiz + imgreg.ox) &&
                           tile_Xrange.maxvalue <= (Byte4_t)(imgreg.xosiz + imgreg.ox + imgreg.sx) &&
                           tile_Yrange.minvalue >= (Byte4_t)(imgreg.yosiz + imgreg.oy) &&
                           tile_Yrange.maxvalue <= (Byte4_t)(imgreg.yosiz + imgreg.oy + imgreg.sy)) {
                    /* Tile completely contained within view-window */
                    /* high priority */
                    /*printf("Tile completely contained within view-window %d\n", tile_id);*/
                    if (msgqueue->cachemodel->jppstream) {
                        enqueue_tileheader((int)tile_id, msgqueue);
                        enqueue_allprecincts((int)tile_id, imgreg.level, query_param.lastcomp,
                                             query_param.comps, query_param.layers, msgqueue);
                    } else {
                        enqueue_tile(tile_id, imgreg.level, msgqueue);
                    }
                } else {
                    /* Tile partially overlaps view-window */
                    /* low priority */
                    /*printf("Tile partially overlaps view-window %d\n", tile_id);*/
                    if (msgqueue->cachemodel->jppstream) {
                        enqueue_tileheader((int)tile_id, msgqueue);

                        /* FIXME: The following code is suspicious it implicitly cast an unsigned int to int, which truncates values */
                        xmin = tile_Xrange.minvalue >= (Byte4_t)(imgreg.xosiz + imgreg.ox) ? 0 :
                               imgreg.xosiz + imgreg.ox - (int)tile_Xrange.minvalue;
                        xmax = tile_Xrange.maxvalue <= (Byte4_t)(imgreg.xosiz + imgreg.ox + imgreg.sx)
                               ? (int)(tile_Xrange.maxvalue - tile_Xrange.minvalue - 1) : (int)(
                                   imgreg.xosiz + imgreg.ox + imgreg.sx - (int)tile_Xrange.minvalue - 1);
                        ymin = tile_Yrange.minvalue >= (Byte4_t)(imgreg.yosiz + imgreg.oy) ? 0 :
                               imgreg.yosiz + imgreg.oy - (int)tile_Yrange.minvalue;
                        ymax = tile_Yrange.maxvalue <= (Byte4_t)(imgreg.yosiz + imgreg.oy + imgreg.sy)
                               ? (int)(tile_Yrange.maxvalue - tile_Yrange.minvalue - 1) : (int)(
                                   imgreg.yosiz + imgreg.oy + imgreg.sy - (int)tile_Yrange.minvalue - 1);
                        enqueue_precincts(xmin, xmax, ymin, ymax, (int)tile_id, imgreg.level,
                                          query_param.lastcomp, query_param.comps, query_param.layers, msgqueue);
                    } else {
                        enqueue_tile(tile_id, imgreg.level, msgqueue);
                    }
                }
            }
        }
    }
}


void enqueue_precincts(int xmin, int xmax, int ymin, int ymax, int tile_id,
                       int level, int lastcomp, OPJ_BOOL *comps, int layers,
                       msgqueue_param_t *msgqueue)
{
    index_param_t *codeidx;
    int c, u, v, res_lev, dec_lev;
    int seq_id;
    Byte4_t XTsiz, YTsiz;
    Byte4_t XPsiz, YPsiz;
    Byte4_t xminP, xmaxP, yminP, ymaxP;

    codeidx  = msgqueue->cachemodel->target->codeidx;
    /* MM: shouldn't xmin/xmax be Byte4_t instead ? */
    if (xmin < 0 || xmax < 0 || ymin < 0 || ymax < 0) {
        return;
    }
    /* MM: I think the API should not really be int should it ? */
    if (tile_id < 0) {
        return;
    }

    for (c = 0; c < codeidx->SIZ.Csiz; c++)
        if (lastcomp == -1 /*all*/ || (c <= lastcomp && comps[c])) {
            seq_id = 0;
            for (res_lev = 0, dec_lev = codeidx->COD.numOfdecomp; dec_lev >= level;
                    res_lev++, dec_lev--) {

                XTsiz = get_tile_XSiz(codeidx->SIZ, (Byte4_t)tile_id, dec_lev);
                YTsiz = get_tile_YSiz(codeidx->SIZ, (Byte4_t)tile_id, dec_lev);

                XPsiz = (codeidx->COD.Scod & 0x01) ? codeidx->COD.XPsiz[ res_lev] : XTsiz;
                YPsiz = (codeidx->COD.Scod & 0x01) ? codeidx->COD.YPsiz[ res_lev] : YTsiz;

                for (u = 0; u < ceil((double)YTsiz / (double)YPsiz); u++) {
                    yminP = (Byte4_t)u * YPsiz;
                    ymaxP = (Byte4_t)(u + 1) * YPsiz - 1;
                    if (YTsiz <= ymaxP) {
                        ymaxP = YTsiz - 1;
                    }

                    for (v = 0; v < ceil((double)XTsiz / (double)XPsiz); v++, seq_id++) {
                        xminP = (Byte4_t)v * XPsiz;
                        xmaxP = (Byte4_t)(v + 1) * XPsiz - 1;
                        if (XTsiz <= xmaxP) {
                            xmaxP = XTsiz - 1;
                        }

                        if (xmaxP < (Byte4_t)xmin || xminP > (Byte4_t)xmax || ymaxP < (Byte4_t)ymin ||
                                yminP > (Byte4_t)ymax) {
                            /* Precinct completely excluded from view-window */
                        } else if (xminP >= (Byte4_t)xmin && xmaxP <= (Byte4_t)xmax &&
                                   yminP >= (Byte4_t)ymin && ymaxP <= (Byte4_t)ymax) {
                            /* Precinct completely contained within view-window
                             high priority */
                            enqueue_precinct(seq_id, tile_id, c, (dec_lev > level) ? -1 : layers, msgqueue);
                        } else {
                            /* Precinct partially overlaps view-window
                             low priority */
                            enqueue_precinct(seq_id, tile_id, c, (dec_lev > level) ? -1 : layers, msgqueue);
                        }
                    }
                }
            }
        }
}

void enqueue_allprecincts(int tile_id, int level, int lastcomp, OPJ_BOOL *comps,
                          int layers, msgqueue_param_t *msgqueue)
{
    index_param_t *codeidx;
    int c, i, res_lev, dec_lev;
    int seq_id;
    Byte4_t XTsiz, YTsiz;
    Byte4_t XPsiz, YPsiz;

    codeidx  = msgqueue->cachemodel->target->codeidx;
    if (tile_id < 0) {
        return;
    }

    for (c = 0; c < codeidx->SIZ.Csiz; c++)
        if (lastcomp == -1 /*all*/ || (c <= lastcomp && comps[c])) {
            seq_id = 0;
            for (res_lev = 0, dec_lev = codeidx->COD.numOfdecomp; dec_lev >= level;
                    res_lev++, dec_lev--) {

                XTsiz = get_tile_XSiz(codeidx->SIZ, (Byte4_t)tile_id, dec_lev);
                YTsiz = get_tile_YSiz(codeidx->SIZ, (Byte4_t)tile_id, dec_lev);

                XPsiz = (codeidx->COD.Scod & 0x01) ? codeidx->COD.XPsiz[ res_lev] : XTsiz;
                YPsiz = (codeidx->COD.Scod & 0x01) ? codeidx->COD.YPsiz[ res_lev] : YTsiz;

                for (i = 0;
                        i < ceil((double)YTsiz / (double)YPsiz)*ceil((double)XTsiz / (double)XPsiz);
                        i++, seq_id++) {
                    enqueue_precinct(seq_id, tile_id, c, (dec_lev > level) ? -1 : layers,
                                     msgqueue);
                }
            }
        }
}

OPJ_BOOL enqueue_metabins(query_param_t query_param,
                          metadatalist_param_t *metadatalist, msgqueue_param_t *msgqueue)
{
    int i;
    for (i = 0; i < MAX_NUMOFBOX && query_param.box_type[i][0] != 0; i++) {
        if (query_param.box_type[i][0] == '*') {
            fprintf(FCGI_stdout, "Status: 501\r\n");
            fprintf(FCGI_stdout,
                    "Reason: metareq with all box-property * not implemented\r\n");
            return OPJ_FALSE;
        } else {
            Byte8_t idx = search_metadataidx(query_param.box_type[i], metadatalist);

            if (idx != (Byte8_t) - 1) {
                enqueue_metadata(idx, msgqueue);
            } else {
                fprintf(FCGI_stdout, "Status: 400\r\n");
                fprintf(FCGI_stdout, "Reason: box-type %.4s not found\r\n",
                        query_param.box_type[i]);
                return OPJ_FALSE;
            }
        }
    }
    return OPJ_TRUE;
}
