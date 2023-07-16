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

#ifdef _WIN32
#include <windows.h>
#ifdef _MSC_VER
typedef SSIZE_T ssize_t;
#endif
#else
#include <sys/types.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <unistd.h>
#endif

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include "sock_manager.h"

#ifdef SERVER
#include "fcgi_stdio.h"
#define logstream FCGI_stdout
#else
#define FCGI_stdout stdout
#define FCGI_stderr stderr
#define logstream stderr
#endif /*SERVER*/

SOCKET open_listeningsocket(uint16_t port)
{
    SOCKET listening_socket;
    struct sockaddr_in sin;
    int sock_optval = 1;

    listening_socket = socket(AF_INET, SOCK_STREAM, 0);
    if (listening_socket == -1) {
        perror("socket");
        exit(1);
    }

    if (setsockopt(listening_socket, SOL_SOCKET, SO_REUSEADDR,
                   (const char *)&sock_optval, sizeof(sock_optval)) == -1) {
        perror("setsockopt");
        exit(1);
    }

    memset(&sin, 0, sizeof(sin));
    sin.sin_family = AF_INET;
    sin.sin_port = htons(port);
    sin.sin_addr.s_addr = htonl(INADDR_ANY);

    if (bind(listening_socket, (struct sockaddr *)&sin, sizeof(sin)) < 0) {
        perror("bind");
        close_socket(listening_socket);
        exit(1);
    }

    if (listen(listening_socket, SOMAXCONN) == -1) {
        perror("listen");
        close_socket(listening_socket);
        exit(1);
    }
    fprintf(FCGI_stderr, "port %d is listened\n", port);

    return listening_socket;
}

SOCKET accept_socket(SOCKET listening_socket)
{
    struct sockaddr_in peer_sin;
    unsigned int addrlen = sizeof(peer_sin);

    return accept(listening_socket, (struct sockaddr *)&peer_sin, &addrlen);
}

void send_stream(SOCKET connected_socket, const void *stream, OPJ_SIZE_T length)
{
    char *ptr = (char*)stream;
    OPJ_SIZE_T remlen = length;

    while (remlen > 0) {
        ssize_t sentlen = send(connected_socket, ptr, remlen, 0);
        if (sentlen == -1) {
            fprintf(FCGI_stderr, "sending stream error\n");
            break;
        }
        remlen = remlen - (OPJ_SIZE_T)sentlen;
        ptr = ptr + sentlen;
    }
}

void * receive_stream(SOCKET connected_socket, OPJ_SIZE_T length)
{
    char *stream, *ptr;
    OPJ_SIZE_T remlen;

    ptr = stream = malloc(length);
    remlen = length;

    while (remlen > 0) {
        ssize_t redlen = recv(connected_socket, ptr, remlen, 0);
        if (redlen == -1) {
            fprintf(FCGI_stderr, "receive stream error\n");
            free(stream);
            stream = NULL;
            break;
        }
        remlen -= (OPJ_SIZE_T)redlen;
        ptr = ptr + redlen;
    }
    return stream;
}

OPJ_SIZE_T receive_line(SOCKET connected_socket, char *p)
{
    OPJ_SIZE_T len = 0;
    while (1) {
        ssize_t ret;
        ret = recv(connected_socket, p, 1, 0);
        if (ret == -1) {
            perror("receive");
            exit(1);
        } else if (ret == 0) {
            break;
        }
        if (*p == '\n') {
            break;
        }
        p++;
        len++;
    }
    *p = '\0';

    if (len == 0) {
        fprintf(FCGI_stderr, "Header receive error\n");
    }

    return len;
}

char * receive_string(SOCKET connected_socket)
{
    char buf[BUF_LEN];

    /* MM FIXME: there is a nasty bug here, size of buf if BUF_LEN which is never
    indicated to downstream receive_line */
    receive_line(connected_socket, buf);

    return strdup(buf);
}

int close_socket(SOCKET sock)
{
#ifdef _WIN32
    return closesocket(sock);
#else
    return close(sock);
#endif
}
