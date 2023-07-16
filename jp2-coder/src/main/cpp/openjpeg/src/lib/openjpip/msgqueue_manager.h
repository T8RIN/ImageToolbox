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

#ifndef     MSGQUEUE_MANAGER_H_
# define    MSGQUEUE_MANAGER_H_

#include "byte_manager.h"
#include "cachemodel_manager.h"
#include "placeholder_manager.h"

#define PRECINCT_MSG 0
#define EXT_PRECINCT_MSG 1
#define TILE_HEADER_MSG 2
#define TILE_MSG 4
#define EXT_TILE_MSG 5
#define MAINHEADER_MSG 6
#define METADATA_MSG 8

/** message parameters */
typedef struct message_param {
    OPJ_BOOL
    last_byte;          /**< if message contains the last byte of the data-bin*/
    Byte8_t in_class_id;        /**< in-class identifier A.2.3*/
    Byte8_t class_id;           /**< class identifiers */
    Byte8_t csn;                /**< index of the codestream*/
    Byte8_t bin_offset;         /**< offset of the data in this message from the start of the data-bin*/
    Byte8_t length;             /**< message byte length*/
    Byte8_t aux;                /**<*/
    OPJ_OFF_T res_offset;         /**< offset in the resource*/
    placeholder_param_t *phld;  /**< placeholder pointer in index*/
    struct message_param *next; /**< pointer to the next message*/
} message_param_t;

/** message queue parameters */
typedef struct msgqueue_param {
    message_param_t *first;         /**< first message pointer of the list*/
    message_param_t *last;          /**< last  message pointer of the list*/
    OPJ_BOOL stateless;                 /**< if this is a stateless message queue*/
    cachemodel_param_t *cachemodel; /**< reference cachemodel pointer*/
} msgqueue_param_t;

/**
 * generate message queue
 *
 * @param[in] stateless   if this is a stateless message queue
 * @param[in] cachemodel  cachemodel pointer
 * @return                generated message queue pointer
 */
msgqueue_param_t * gene_msgqueue(OPJ_BOOL stateless,
                                 cachemodel_param_t *cachemodel);

/**
 * delete message queue
 *
 * @param[in] msgqueue address of the message queue pointer
 */
void delete_msgqueue(msgqueue_param_t **msgqueue);

/**
 * delete a message in msgqueue
 *
 * @param[in] message  address of the deleting message pointer
 * @param[in] msgqueue message queue pointer
 */
void delete_message_in_msgqueue(message_param_t **message,
                                msgqueue_param_t *msgqueue);

/**
 * print message queue
 *
 * @param[in] msgqueue message queue pointer
 */
void print_msgqueue(msgqueue_param_t *msgqueue);


/**
 * enqueue main header data-bin into message queue
 *
 * @param[in,out] msgqueue message queue pointer
 */
void enqueue_mainheader(msgqueue_param_t *msgqueue);

/**
 * enqueue tile headers data-bin into message queue
 *
 * @param[in]     tile_id  tile id starting from 0
 * @param[in,out] msgqueue message queue pointer
 */
void enqueue_tileheader(int tile_id, msgqueue_param_t *msgqueue);

/**
 * enqueue tile data-bin into message queue
 *
 * @param[in]     tile_id  tile id starting from 0
 * @param[in]     level    decomposition level
 * @param[in,out] msgqueue message queue pointer
 */
void enqueue_tile(Byte4_t tile_id, int level, msgqueue_param_t *msgqueue);

/**
 * enqueue precinct data-bin into message queue
 *
 * @param[in]     seq_id   precinct sequence number within its tile
 * @param[in]     tile_id  tile index
 * @param[in]     comp_id  component number
 * @param[in]     layers   num of layers
 * @param[in,out] msgqueue message queue
 */
void enqueue_precinct(int seq_id, int tile_id, int comp_id, int layers,
                      msgqueue_param_t *msgqueue);


/**
 * enqueue Metadata-bin into message queue
 *
 * @param[in]     meta_id  metadata-bin id
 * @param[in,out] msgqueue message queue pointer
 */
void enqueue_metadata(Byte8_t meta_id, msgqueue_param_t *msgqueue);


/**
 * reconstruct JPT/JPP-stream from message queue
 *
 * @param[in] msgqueue message queue pointer
 * @param[in] tmpfd    file discriptor to write JPT/JPP-stream
 */
void recons_stream_from_msgqueue(msgqueue_param_t *msgqueue, int tmpfd);


/**
 * parse JPT- JPP- stream to message queue
 *
 * @param[in]     JPIPstream   JPT- JPP- stream data pointer
 * @param[in]     streamlen    JPIPstream length
 * @param[in]     offset       offset of the stream from the whole beginning
 * @param[in,out] msgqueue     adding message queue pointer
 */
void parse_JPIPstream(Byte_t *JPIPstream, Byte8_t streamlen, OPJ_OFF_T offset,
                      msgqueue_param_t *msgqueue);

/**
 * parse JPT- JPP- stream to message queue
 *
 * @param[in] msgqueue     reference message queue pointer
 * @param[in] stream       stream data pointer
 * @param[in] streamlen    stream length
 * @param[in] metadatalist adding metadata list pointer
 */
void parse_metamsg(msgqueue_param_t *msgqueue, Byte_t *stream,
                   Byte8_t streamlen, metadatalist_param_t *metadatalist);

/**
 * compute precinct ID A.3.2.1
 *
 * @param[in]  t                 tile index
 * @param[in]  c                 component index
 * @param[in]  s                 sequence number
 * @param[in]  num_components    total number of components
 * @param[in]  num_tiles         total number of tiles
 * @return                       precicnt id
 */
Byte8_t comp_precinct_id(int t, int c, int s, int num_components,
                         int num_tiles);

#endif      /* !MSGQUEUE_MANAGER_H_ */
