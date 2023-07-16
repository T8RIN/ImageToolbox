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
#include <string.h>
#include <stdlib.h>
#include "imgsock_manager.h"
#if _WIN32
#define strncasecmp _strnicmp
#endif

msgtype_t identify_clientmsg(SOCKET connected_socket)
{
    OPJ_SIZE_T receive_size;
    char buf[BUF_LEN];
    static const char *magicid[] = { "JPIP-stream", "PNM request", "XML request",
                                     "TID request", "CID request", "CID destroy", "SIZ request", "JP2 save",
                                     "QUIT"
                                   };
    int i;

    receive_size = receive_line(connected_socket, buf);

    if (receive_size == 0) {
        fprintf(stderr, "Error to receive the header of client message\n");
        return MSGERROR;
    }

    for (i = 0; i < NUM_OF_MSGTYPES; i++) {
        if (strncasecmp(magicid[i], buf, strlen(magicid[i])) == 0) {
            fprintf(stderr, "%s\n", magicid[i]);
            return i;
        }
    }

    fprintf(stderr, "Cannot identify client message type %s\n", buf);
    return MSGERROR;
}

Byte_t * receive_JPIPstream(SOCKET connected_socket, char **target, char **tid,
                            char **cid, OPJ_SIZE_T *streamlen)
{
    char buf[BUF_LEN];
    const char versionstring[] = "version 1.2";
    int idatalen;
    OPJ_SIZE_T linelen, datalen;
    Byte_t *jpipstream;

    *target = *cid = *tid = NULL;

    if ((linelen = receive_line(connected_socket, buf)) == 0) {
        return NULL;
    }
    if (strncmp(versionstring, buf, strlen(versionstring)) != 0) {
        fprintf(stderr, "Wrong format\n");
        return NULL;
    }

    if ((linelen = receive_line(connected_socket, buf)) == 0) {
        return NULL;
    }

    if (strstr(buf, "jp2")) {
        /* register cid option*/
        *target = strdup(buf);

        if ((linelen = receive_line(connected_socket, buf)) == 0) {
            return NULL;
        }
        if (strcmp(buf, "0") != 0) {
            *tid = strdup(buf);
        }

        if ((linelen = receive_line(connected_socket, buf)) == 0) {
            return NULL;
        }
        if (strcmp(buf, "0") != 0) {
            *cid = strdup(buf);
        }

        if ((linelen = receive_line(connected_socket, buf)) == 0) {
            return NULL;
        }
    }

    idatalen = atoi(buf);
    if (idatalen < 0) {
        fprintf(stderr, "Receive Data: %d Bytes\n", idatalen);
        return NULL;
    }
    datalen = (OPJ_SIZE_T)idatalen;
    fprintf(stdout, "Receive Data: %lu Bytes\n", datalen);

    jpipstream = receive_stream(connected_socket, datalen);

    /* check EOR*/
    if (jpipstream[datalen - 3] == 0x00 && (jpipstream[datalen - 2] == 0x01 ||
                                            jpipstream[datalen - 2] == 0x02)) {
        *streamlen = datalen - 3;
    } else {
        *streamlen = datalen;
    }

    return jpipstream;
}

void send_XMLstream(SOCKET connected_socket, Byte_t *xmlstream,
                    OPJ_SIZE_T length)
{
    Byte_t header[5];

    header[0] = 'X';
    header[1] = 'M';
    header[2] = 'L';
    header[3] = (Byte_t)((length >> 8) & 0xff);
    header[4] = (Byte_t)(length & 0xff);

    send_stream(connected_socket, header, 5);
    send_stream(connected_socket, xmlstream, length);
}

void send_IDstream(SOCKET connected_socket, const char *id, OPJ_SIZE_T idlen,
                   const char *label);

void send_CIDstream(SOCKET connected_socket, const char *cid, OPJ_SIZE_T cidlen)
{
    send_IDstream(connected_socket, cid, cidlen, "CID");
}

void send_TIDstream(SOCKET connected_socket, const char *tid, OPJ_SIZE_T tidlen)
{
    send_IDstream(connected_socket, tid, tidlen, "TID");
}

void send_IDstream(SOCKET connected_socket, const char *id, OPJ_SIZE_T idlen,
                   const char *label)
{
    char header[4];

    header[0] = label[0];
    header[1] = label[1];
    header[2] = label[2];
    header[3] = (char)(idlen & 0xff);

    send_stream(connected_socket, header, 4);
    send_stream(connected_socket, id, idlen);
}

void send_PNMstream(SOCKET connected_socket, Byte_t *pnmstream,
                    unsigned int width, unsigned int height, unsigned int numofcomp, Byte_t maxval)
{
    OPJ_SIZE_T pnmlen = 0;
    Byte_t header[7];

    pnmlen = width * height * numofcomp;

    header[0] = 'P';
    header[1] = numofcomp == 3 ? 6 : 5;
    header[2] = (width >> 8) & 0xff;
    header[3] = width & 0xff;
    header[4] = (height >> 8) & 0xff;
    header[5] = height & 0xff;
    header[6] = maxval;

    send_stream(connected_socket, header, 7);
    send_stream(connected_socket, pnmstream, pnmlen);
}

void send_SIZstream(SOCKET connected_socket, unsigned int width,
                    unsigned int height)
{
    Byte_t response[9];

    response[0] = 'S';
    response[1] = 'I';
    response[2] = 'Z';
    response[3] = (width >> 16) & 0xff;
    response[4] = (width >> 8) & 0xff;
    response[5] = width & 0xff;
    response[6] = (height >> 16) & 0xff;
    response[7] = (height >> 8) & 0xff;
    response[8] = height & 0xff;

    send_stream(connected_socket, response, 9);
}

void response_signal(SOCKET connected_socket, OPJ_BOOL succeed)
{
    Byte_t code;

    if (succeed) {
        code = 1;
    } else {
        code = 0;
    }

    send_stream(connected_socket, &code, 1);
}
