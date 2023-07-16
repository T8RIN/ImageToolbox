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

#ifndef     SOCK_MANAGER_H_
# define    SOCK_MANAGER_H_

#include "byte_manager.h"
#include "opj_stdint.h"

#ifdef _WIN32
#include <winsock.h>
#else
typedef int SOCKET;
#endif /*_WIN32*/

#define BUF_LEN 256

/**
 * open listening socket
 *
 * @param  port opening port number
 * @return      new socket
 */
SOCKET open_listeningsocket(uint16_t port);

/**
 * accept a new connection to the listenning socket
 *
 * @param listening_socket listenning socket
 * @return                 connected socket (-1 if error occurs)
 */
SOCKET accept_socket(SOCKET listening_socket);


/**
 * receive a string line (ending with '\n') from client
 *
 * @param [in]  connected_socket file descriptor of the connected socket
 * @param [out] buf              string to be stored
 * @return                       red size
 */
OPJ_SIZE_T receive_line(SOCKET connected_socket, char *buf);

/**
 * receive a string line (ending with '\n') from client, return malloc string
 *
 * @param [in]  connected_socket file descriptor of the connected socket
 * @return                       pointer to the string (memory allocated)
 */
char * receive_string(SOCKET connected_socket);

/**
 * receive data stream to client
 *
 * @param [in]  connected_socket file descriptor of the connected socket
 * @param [in]  length           length of the receiving stream
 * @return                       pointer to the data stream (memory allocated), NULL if failed
 */
void * receive_stream(SOCKET connected_socket, OPJ_SIZE_T length);

/**
 * send data stream to client
 *
 * @param [in]  connected_socket file descriptor of the connected socket
 * @param [in]  stream           data stream
 * @param [in]  length           length of data stream
 */
void send_stream(SOCKET connected_socket, const void *stream,
                 OPJ_SIZE_T length);

/**
 * close socket
 *
 * @param [in] sock closing socket
 * @return     0 if succeed, -1 if failed
 */
int close_socket(SOCKET sock);

#endif /* !SOCK_MANAGER_H_ */
