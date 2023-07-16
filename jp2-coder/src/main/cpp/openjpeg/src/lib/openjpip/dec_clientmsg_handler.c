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
#include <string.h>
#include <assert.h>
#include <limits.h>
#include "dec_clientmsg_handler.h"
#include "ihdrbox_manager.h"
#include "jpipstream_manager.h"
#include "jp2k_encoder.h"
#include "opj_inttypes.h"

void handle_JPIPstreamMSG(SOCKET connected_socket, cachelist_param_t *cachelist,
                          Byte_t **jpipstream, OPJ_SIZE_T *streamlen, msgqueue_param_t *msgqueue)
{
    Byte_t *newjpipstream;
    OPJ_SIZE_T newstreamlen = 0;
    cache_param_t *cache;
    char *target, *tid, *cid;
    metadatalist_param_t *metadatalist;

    newjpipstream = receive_JPIPstream(connected_socket, &target, &tid, &cid,
                                       &newstreamlen);

    fprintf(stderr, "newjpipstream length: %" PRIu64 "\n", newstreamlen);

    parse_JPIPstream(newjpipstream, newstreamlen, (OPJ_OFF_T)*streamlen, msgqueue);

    *jpipstream = update_JPIPstream(newjpipstream, newstreamlen, *jpipstream,
                                    streamlen);
    opj_free(newjpipstream);

    metadatalist = gene_metadatalist();
    parse_metamsg(msgqueue, *jpipstream, *streamlen, metadatalist);

    assert(msgqueue->last);
    assert(msgqueue->last->csn < INT_MAX);
    /* cid registration*/
    if (target != NULL) {
        if ((cache = search_cache(target, cachelist))) {
            if (tid != NULL) {
                update_cachetid(tid, cache);
            }
            if (cid != NULL) {
                add_cachecid(cid, cache);
            }
        } else {
            cache = gene_cache(target, (int)msgqueue->last->csn, tid, cid);
            insert_cache_into_list(cache, cachelist);
        }
    } else {
        cache = search_cacheBycsn((int)msgqueue->last->csn, cachelist);
    }

    if (cache->metadatalist) {
        delete_metadatalist(&cache->metadatalist);
    }
    cache->metadatalist = metadatalist;

    if (target) {
        opj_free(target);
    }
    if (tid) {
        opj_free(tid);
    }
    if (cid) {
        opj_free(cid);
    }

    response_signal(connected_socket, OPJ_TRUE);
}

void handle_PNMreqMSG(SOCKET connected_socket, Byte_t *jpipstream,
                      msgqueue_param_t *msgqueue, cachelist_param_t *cachelist)
{
    Byte_t *pnmstream;
    ihdrbox_param_t *ihdrbox;
    char *CIDorTID, tmp[10];
    cache_param_t *cache;
    int fw, fh;
    int maxval;

    CIDorTID = receive_string(connected_socket);

    if (!(cache = search_cacheBycid(CIDorTID, cachelist)))
        if (!(cache = search_cacheBytid(CIDorTID, cachelist))) {
            opj_free(CIDorTID);
            return;
        }

    opj_free(CIDorTID);

    receive_line(connected_socket, tmp);
    fw = atoi(tmp);

    receive_line(connected_socket, tmp);
    fh = atoi(tmp);

    ihdrbox = NULL;
    assert(cache->csn >= 0);
    pnmstream = jpipstream_to_pnm(jpipstream, msgqueue, (Byte8_t)cache->csn, fw, fh,
                                  &ihdrbox);

    maxval = ihdrbox->bpc > 8 ? 255 : (1 << ihdrbox->bpc) - 1;
    send_PNMstream(connected_socket, pnmstream, ihdrbox->width, ihdrbox->height,
                   ihdrbox->nc, (Byte_t)maxval);

    opj_free(ihdrbox);
    opj_free(pnmstream);
}

void handle_XMLreqMSG(SOCKET connected_socket, Byte_t *jpipstream,
                      cachelist_param_t *cachelist)
{
    char *cid;
    cache_param_t *cache;
    boxcontents_param_t *boxcontents;
    Byte_t *xmlstream;

    cid = receive_string(connected_socket);

    if (!(cache = search_cacheBycid(cid, cachelist))) {
        opj_free(cid);
        return;
    }

    opj_free(cid);

    boxcontents = cache->metadatalist->last->boxcontents;
    xmlstream = (Byte_t *)opj_malloc(boxcontents->length);
    memcpy(xmlstream, jpipstream + boxcontents->offset, boxcontents->length);
    send_XMLstream(connected_socket, xmlstream, boxcontents->length);
    opj_free(xmlstream);
}

void handle_TIDreqMSG(SOCKET connected_socket, cachelist_param_t *cachelist)
{
    char *target, *tid = NULL;
    cache_param_t *cache;
    OPJ_SIZE_T tidlen = 0;

    target = receive_string(connected_socket);
    cache = search_cache(target, cachelist);

    opj_free(target);

    if (cache) {
        tid = cache->tid;
        tidlen = strlen(tid);
    }
    send_TIDstream(connected_socket, tid, tidlen);
}

void handle_CIDreqMSG(SOCKET connected_socket, cachelist_param_t *cachelist)
{
    char *target, *cid = NULL;
    cache_param_t *cache;
    OPJ_SIZE_T cidlen = 0;

    target = receive_string(connected_socket);
    cache = search_cache(target, cachelist);

    opj_free(target);

    if (cache) {
        if (cache->numOfcid > 0) {
            cid = cache->cid[ cache->numOfcid - 1];
            cidlen = strlen(cid);
        }
    }
    send_CIDstream(connected_socket, cid, cidlen);
}

void handle_dstCIDreqMSG(SOCKET connected_socket, cachelist_param_t *cachelist)
{
    char *cid;

    cid = receive_string(connected_socket);
    remove_cachecid(cid, cachelist);
    response_signal(connected_socket, OPJ_TRUE);

    opj_free(cid);
}

void handle_SIZreqMSG(SOCKET connected_socket, Byte_t *jpipstream,
                      msgqueue_param_t *msgqueue, cachelist_param_t *cachelist)
{
    char *tid, *cid;
    cache_param_t *cache;
    Byte4_t width, height;

    tid = receive_string(connected_socket);
    cid = receive_string(connected_socket);

    cache = NULL;

    if (tid[0] != '0') {
        cache = search_cacheBytid(tid, cachelist);
    }

    if (!cache && cid[0] != '0') {
        cache = search_cacheBycid(cid, cachelist);
    }

    opj_free(tid);
    opj_free(cid);

    width = height = 0;
    if (cache) {
        assert(cache->csn >= 0);
        if (!cache->ihdrbox) {
            cache->ihdrbox = get_SIZ_from_jpipstream(jpipstream, msgqueue,
                             (Byte8_t)cache->csn);
        }
        width  = cache->ihdrbox->width;
        height = cache->ihdrbox->height;
    }
    send_SIZstream(connected_socket, width, height);
}

void handle_JP2saveMSG(SOCKET connected_socket, cachelist_param_t *cachelist,
                       msgqueue_param_t *msgqueue, Byte_t *jpipstream)
{
    char *cid;
    cache_param_t *cache;
    Byte_t *jp2stream;
    Byte8_t jp2len;

    cid = receive_string(connected_socket);
    if (!(cache = search_cacheBycid(cid, cachelist))) {
        opj_free(cid);
        return;
    }

    opj_free(cid);

    assert(cache->csn >= 0);
    jp2stream = recons_jp2(msgqueue, jpipstream, (Byte8_t)cache->csn, &jp2len);

    if (jp2stream) {
        save_codestream(jp2stream, jp2len, "jp2");
        opj_free(jp2stream);
    }
}
