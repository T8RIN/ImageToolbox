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

#ifndef         AUXTRANS_MANAGER_H_
# define        AUXTRANS_MANAGER_H_

#include "sock_manager.h"
#include "opj_includes.h"

/** auxiliary transport setting parameters*/
typedef struct auxtrans_param {
    int tcpauxport;       /**< tcp port*/
    int udpauxport;       /**< udp port*/
    SOCKET tcplistensock; /**< listenning socket for aux tcp (-1 if not open)*/
    SOCKET udplistensock; /**< listenning socket for aux udp (-1 if not open)*/
} auxtrans_param_t;

/**
 * Initialize auxiliary transport server of JPIP server
 *
 * @param[in] tcp_auxport opening tcp auxiliary port ( 0 not to open, valid No. 49152-65535)
 * @param[in] udp_auxport opening udp auxiliary port ( 0 not to open, valid No. 49152-65535)
 * @return                initialized transport parameters
 */
auxtrans_param_t init_aux_transport(int tcp_auxport, int udp_auxport);

/**
 * Close auxiliary transport server of JPIP server
 *
 * @param[in] auxtrans closing transport server
 */
void close_aux_transport(auxtrans_param_t auxtrans);

/**
 * Send response data on aux transport
 *
 * @param[in] istcp          true if tcp, false if udp
 * @param[in] auxtrans       available transport parameters
 * @param[in] cid            channel ID
 * @param[in] data           sending data
 * @param[in] length         length of data
 * @param[in] maxlenPerFrame maximum data length to send per frame
 */
void send_responsedata_on_aux(OPJ_BOOL istcp, auxtrans_param_t auxtrans,
                              const char cid[], void *data, OPJ_SIZE_T length,
                              OPJ_SIZE_T maxlenPerFrame);

#endif /* !AUXTRANS_MANAGER_H_ */
