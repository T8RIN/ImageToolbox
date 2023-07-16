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

#ifndef     JPIP_PARSER_H_
# define    JPIP_PARSER_H_

#include "query_parser.h"
#include "session_manager.h"
#include "target_manager.h"
#include "msgqueue_manager.h"
#include "channel_manager.h"

/**
 * REQUEST: target identification by target or tid request
 *
 * @param[in]     query_param   structured query
 * @param[in]     targetlist    target list pointer
 * @param[out]    target        address of target pointer
 * @return                      if succeeded (true) or failed (false)
 */
OPJ_BOOL identify_target(query_param_t query_param,
                         targetlist_param_t *targetlist, target_param_t **target);

/**
 * REQUEST: channel association
 *          this must be processed before any process
 *
 * @param[in]     query_param   structured query
 * @param[in]     sessionlist   session list pointer
 * @param[out]    cursession    address of the associated session pointer
 * @param[out]    curchannel    address of the associated channel pointer
 * @return                      if succeeded (true) or failed (false)
 */
OPJ_BOOL associate_channel(query_param_t    query_param,
                           sessionlist_param_t *sessionlist,
                           session_param_t **cursession,
                           channel_param_t **curchannel);
/**
 * REQUEST: new channel (cnew) assignment
 *
 * @param[in]     query_param   structured query
 * @param[in]     sessionlist   session list pointer
 * @param[in]     auxtrans      auxiliary transport
 * @param[in]     target        requested target pointer
 * @param[in,out] cursession    address of the associated/opened session pointer
 * @param[in,out] curchannel    address of the associated/opened channel pointer
 * @return                      if succeeded (true) or failed (false)
 */
OPJ_BOOL open_channel(query_param_t query_param,
                      sessionlist_param_t *sessionlist,
                      auxtrans_param_t auxtrans,
                      target_param_t *target,
                      session_param_t **cursession,
                      channel_param_t **curchannel);

/**
 * REQUEST: channel close (cclose)
 *
 * @param[in]     query_param   structured query
 * @param[in]     sessionlist   session list pointer
 * @param[in,out] cursession    address of the session pointer of deleting channel
 * @param[in,out] curchannel    address of the deleting channel pointer
 * @return                      if succeeded (true) or failed (false)
 */
OPJ_BOOL close_channel(query_param_t query_param,
                       sessionlist_param_t *sessionlist,
                       session_param_t **cursession,
                       channel_param_t **curchannel);

/**
 * REQUEST: view-window (fsiz)
 *
 * @param[in]     query_param structured query
 * @param[in]     target      requested target pointer
 * @param[in,out] cursession  associated session pointer
 * @param[in,out] curchannel  associated channel pointer
 * @param[out]    msgqueue    address of the message queue pointer
 * @return                    if succeeded (true) or failed (false)
 */
OPJ_BOOL gene_JPIPstream(query_param_t query_param,
                         target_param_t *target,
                         session_param_t *cursession,
                         channel_param_t *curchannel,
                         msgqueue_param_t **msgqueue);

#endif      /* !JPIP_PARSER_H_ */
