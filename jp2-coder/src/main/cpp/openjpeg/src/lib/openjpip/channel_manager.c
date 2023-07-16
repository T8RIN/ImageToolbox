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

#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include "channel_manager.h"

#ifdef _WIN32
#define snprintf _snprintf /* Visual Studio */
#endif

#ifdef SERVER
#include "fcgi_stdio.h"
#define logstream FCGI_stdout
#else
#define FCGI_stdout stdout
#define FCGI_stderr stderr
#define logstream stderr
#endif /*SERVER */

channellist_param_t *gene_channellist(void) {
    channellist_param_t *channellist;

    channellist = (channellist_param_t *) opj_malloc(sizeof(channellist_param_t));

    channellist->first = NULL;
    channellist->last = NULL;

    return channellist;
}

channel_param_t *gene_channel(query_param_t query_param,
                              auxtrans_param_t auxtrans, cachemodel_param_t *cachemodel,
                              channellist_param_t *channellist) {
    channel_param_t *channel;
    const char transport[4][10] = {"non", "http", "http-tcp", "http-udp"};

    if (!cachemodel) {
        fprintf(FCGI_stdout, "Status: 404\r\n");
        fprintf(FCGI_stdout, "Reason: cnew cancelled\r\n");
        return NULL;
    }

    channel = (channel_param_t *) opj_malloc(sizeof(channel_param_t));
    channel->cachemodel = cachemodel;

    /* set channel ID and get present time */
    snprintf(channel->cid, MAX_LENOFCID, "%x%x",
             (unsigned int) time(&channel->start_tm), (unsigned int) rand());

    channel->aux = query_param.cnew;

    /* only tcp implemented for now */
    if (channel->aux == udp) {
        channel->aux = tcp;
    }

    channel->next = NULL;

    set_channel_variable_param(query_param, channel);

    if (channellist->first != NULL) {
        channellist->last->next = channel;
    } else {
        channellist->first = channel;
    }
    channellist->last = channel;

    fprintf(FCGI_stdout, "JPIP-cnew: cid=%s", channel->cid);
    fprintf(FCGI_stdout, ",transport=%s", transport[channel->aux]);

    if (channel->aux == tcp || channel->aux == udp) {
        fprintf(FCGI_stdout, ",auxport=%d",
                channel->aux == tcp ? auxtrans.tcpauxport : auxtrans.udpauxport);
    }

    fprintf(FCGI_stdout, "\r\n");

    return channel;
}


void set_channel_variable_param(query_param_t query_param,
                                channel_param_t *channel) {
    /* set roi information */
    (void) query_param;
    (void) channel;
}


void delete_channel(channel_param_t **channel, channellist_param_t *channellist) {
    channel_param_t *ptr;

    if (*channel == channellist->first) {
        channellist->first = (*channel)->next;
    } else {
        ptr = channellist->first;
        while (ptr->next != *channel) {
            ptr = ptr->next;
        }

        ptr->next = (*channel)->next;

        if (*channel == channellist->last) {
            channellist->last = ptr;
        }
    }
#ifndef SERVER
    fprintf(logstream, "local log: channel: %s deleted\n", (*channel)->cid);
#endif
    opj_free(*channel);
}

void delete_channellist(channellist_param_t **channellist) {
    channel_param_t *channelPtr, *channelNext;

    channelPtr = (*channellist)->first;
    while (channelPtr != NULL) {
        channelNext = channelPtr->next;
#ifndef SERVER
        fprintf(logstream, "local log: channel %s deleted!\n", channelPtr->cid);
#endif
        opj_free(channelPtr);
        channelPtr = channelNext;
    }
    opj_free(*channellist);
}

void print_allchannel(channellist_param_t *channellist) {
    channel_param_t *ptr;

    ptr = channellist->first;
    while (ptr != NULL) {
        fprintf(logstream, "channel-ID=%s \t target=%s\n", ptr->cid,
                ptr->cachemodel->target->targetname);
        ptr = ptr->next;
    }
}

channel_param_t *search_channel(const char cid[],
                                channellist_param_t *channellist) {
    channel_param_t *foundchannel;

    foundchannel = channellist->first;

    while (foundchannel != NULL) {

        if (strcmp(cid, foundchannel->cid) == 0) {
            return foundchannel;
        }

        foundchannel = foundchannel->next;
    }
    fprintf(FCGI_stdout, "Status: 503\r\n");
    fprintf(FCGI_stdout, "Reason: Channel %s not found in this session\r\n", cid);

    return NULL;
}
