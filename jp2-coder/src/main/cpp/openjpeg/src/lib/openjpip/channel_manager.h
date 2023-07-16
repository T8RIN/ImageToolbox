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

#ifndef     CHANNEL_MANAGER_H_
# define    CHANNEL_MANAGER_H_

#include <time.h>
#include "query_parser.h"
#include "cachemodel_manager.h"
#include "auxtrans_manager.h"

/** maximum length of channel identifier*/
#define MAX_LENOFCID 30

/** Channel parameters*/
typedef struct channel_param {
    cachemodel_param_t *cachemodel; /**< reference pointer to the cache model*/
    char cid[MAX_LENOFCID];         /**< channel identifier*/
    cnew_transport_t aux;           /**< auxiliary transport*/
    /* - a record of the client's capabilities and preferences to the extent that the server queues requests*/
    time_t start_tm;                /**< starting time*/
    struct channel_param *next;     /**< pointer to the next channel*/
} channel_param_t;


/** Channel list parameters*/
typedef struct channellist_param {
    channel_param_t *first; /**< first channel pointer of the list*/
    channel_param_t *last;  /**< last  channel pointer of the list*/
} channellist_param_t;


/**
 * generate a channel list
 *
 * @return pointer to the generated channel list
 */
channellist_param_t *gene_channellist(void);


/**
 * generate a channel under the channel list
 *
 * @param[in] query_param query parameters
 * @param[in] auxtrans    auxiliary transport
 * @param[in] cachemodel  reference cachemodel
 * @param[in] channellist channel list pointer
 * @return                pointer to the generated channel
 */
channel_param_t *gene_channel(query_param_t query_param,
                              auxtrans_param_t auxtrans, cachemodel_param_t *cachemodel,
                              channellist_param_t *channellist);

/**
 * set channel variable parameters
 *
 * @param[in]     query_param query parameters
 * @param[in,out] channel     pointer to the modifying channel
 */
void set_channel_variable_param(query_param_t query_param,
                                channel_param_t *channel);

/**
 * delete a channel
 *
 * @param[in] channel address of the deleting channel pointer
 * @param[in,out] channellist channel list pointer
 */
void delete_channel(channel_param_t **channel,
                    channellist_param_t *channellist);


/**
 * delete channel list
 *
 * @param[in,out] channellist address of the channel list pointer
 */
void delete_channellist(channellist_param_t **channellist);


/**
 * print all channel parameters
 *
 * @param[in] channellist channel list pointer
 */
void print_allchannel(channellist_param_t *channellist);


/**
 * search a channel by channel ID
 *
 * @param[in] cid         channel identifier
 * @param[in] channellist channel list pointer
 * @return                found channel pointer
 */
channel_param_t *search_channel(const char cid[],
                                channellist_param_t *channellist);

#endif      /* !CHANNEL_MANAGER_H_ */
