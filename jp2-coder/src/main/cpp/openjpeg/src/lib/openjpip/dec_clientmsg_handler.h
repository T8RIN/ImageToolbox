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

#ifndef     DEC_CLIENTMSG_HANDLER_H_
# define    DEC_CLIENTMSG_HANDLER_H_

#include "imgsock_manager.h"
#include "cache_manager.h"
#include "byte_manager.h"
#include "msgqueue_manager.h"

/**
 * handle JPT- JPP- stream message
 *
 * @param[in]     connected_socket socket descriptor
 * @param[in]     cachelist        cache list pointer
 * @param[in,out] jpipstream       address of JPT- JPP- stream pointer
 * @param[in,out] streamlen        address of stream length
 * @param[in,out] msgqueue         message queue pointer
 */
void handle_JPIPstreamMSG(SOCKET connected_socket, cachelist_param_t *cachelist,
                          Byte_t **jpipstream, OPJ_SIZE_T *streamlen, msgqueue_param_t *msgqueue);

/**
 * handle PNM request message
 *
 * @param[in] connected_socket socket descriptor
 * @param[in] jpipstream       jpipstream pointer
 * @param[in] msgqueue         message queue pointer
 * @param[in] cachelist        cache list pointer
 */
void handle_PNMreqMSG(SOCKET connected_socket, Byte_t *jpipstream,
                      msgqueue_param_t *msgqueue, cachelist_param_t *cachelist);

/**
 * handle XML request message
 *
 * @param[in] connected_socket socket descriptor
 * @param[in] jpipstream       address of caching jpipstream pointer
 * @param[in] cachelist        cache list pointer
 */
void handle_XMLreqMSG(SOCKET connected_socket, Byte_t *jpipstream,
                      cachelist_param_t *cachelist);

/**
 * handle TargetID request message
 *
 * @param[in] connected_socket socket descriptor
 * @param[in] cachelist        cache list pointer
 */
void handle_TIDreqMSG(SOCKET connected_socket, cachelist_param_t *cachelist);

/**
 * handle ChannelID request message
 *
 * @param[in] connected_socket socket descriptor
 * @param[in] cachelist        cache list pointer
 */
void handle_CIDreqMSG(SOCKET connected_socket, cachelist_param_t *cachelist);

/**
 * handle distroy ChannelID message
 *
 * @param[in]     connected_socket socket descriptor
 * @param[in,out] cachelist        cache list pointer
 */
void handle_dstCIDreqMSG(SOCKET connected_socket, cachelist_param_t *cachelist);

/**
 * handle SIZ request message
 *
 * @param[in]     connected_socket socket descriptor
 * @param[in] jpipstream       address of caching jpipstream pointer
 * @param[in] msgqueue         message queue pointer
 * @param[in,out] cachelist        cache list pointer
 */
void handle_SIZreqMSG(SOCKET connected_socket, Byte_t *jpipstream,
                      msgqueue_param_t *msgqueue, cachelist_param_t *cachelist);

/**
 * handle saving JP2 file request message
 *
 * @param[in] connected_socket socket descriptor
 * @param[in] cachelist        cache list pointer
 * @param[in] msgqueue         message queue pointer
 * @param[in] jpipstream       address of caching jpipstream pointer
 */
void handle_JP2saveMSG(SOCKET connected_socket, cachelist_param_t *cachelist,
                       msgqueue_param_t *msgqueue, Byte_t *jpipstream);


#endif      /* !DEC_CLIENTMSG_HANDLER_H_ */
