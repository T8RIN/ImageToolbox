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

#ifndef     IMGSOCK_MANAGER_H_
# define    IMGSOCK_MANAGER_H_

#include "byte_manager.h"
#include "sock_manager.h"
#include "opj_includes.h"

#define NUM_OF_MSGTYPES 9
typedef enum eMSGTYPE { JPIPSTREAM, PNMREQ, XMLREQ, TIDREQ, CIDREQ, CIDDST, SIZREQ, JP2SAVE, QUIT, MSGERROR} msgtype_t;

/**
 * indeitify client message type
 *
 * @param [in] connected_socket file descriptor of the connected socket
 * @return                      message type
 */
msgtype_t identify_clientmsg(SOCKET connected_socket);

/**
 * receive a JPT- JPP- stream from client
 *
 * @param [in]  connected_socket file descriptor of the connected socket
 * @param [out] target           address of received target file name string pointer ( malloced, if not received, NULL)
 * @param [out] tid              address of received target identifier string pointer ( malloced, if not received, null string)
 * @param [out] cid              address of received channel identifier string pointer ( malloced, if not received, null string)
 * @param [out] streamlen        length of the received codestream
 * @return                       JPT- JPP- codestream
 */
Byte_t * receive_JPIPstream(SOCKET connected_socket, char **target, char **tid,
                            char **cid, OPJ_SIZE_T *streamlen);

/**
 * send PGM/PPM image stream to the client
 *
 * @param [in]  connected_socket file descriptor of the connected socket
 * @param [in]  pnmstream        PGM/PPM image codestream
 * @param [in]  width            width  of the PGM/PPM image (different from the original image)
 * @param [in]  height           height of the PGM/PPM image
 * @param [in]  numofcomp        number of components of the image
 * @param [in]  maxval           maximum value of the image (only 255 supported)
 */
void send_PNMstream(SOCKET connected_socket, Byte_t *pnmstream,
                    unsigned int width, unsigned int height, unsigned int numofcomp, Byte_t maxval);

/**
 * send XML data stream to the client
 *
 * @param [in]  connected_socket file descriptor of the connected socket
 * @param [in]  xmlstream        xml data stream
 * @param [in]  length           legnth of the xml data stream
 */
void send_XMLstream(SOCKET connected_socket, Byte_t *xmlstream,
                    OPJ_SIZE_T length);

/**
 * send TID data stream to the client
 *
 * @param [in]  connected_socket file descriptor of the connected socket
 * @param [in]  tid              tid string
 * @param [in]  tidlen           legnth of the tid string
 */
void send_TIDstream(SOCKET connected_socket, const char *tid,
                    OPJ_SIZE_T tidlen);

/**
 * send CID data stream to the client
 *
 * @param [in]  connected_socket file descriptor of the connected socket
 * @param [in]  cid              cid string
 * @param [in]  cidlen           legnth of the cid string
 */
void send_CIDstream(SOCKET connected_socket, const char *cid,
                    OPJ_SIZE_T cidlen);

/**
 * send SIZ data stream to the client
 *
 * @param [in]  connected_socket file descriptor of the connected socket
 * @param [in]  width            original width  of the image
 * @param [in]  height           original height of the image
 */
void send_SIZstream(SOCKET connected_socket, unsigned int width,
                    unsigned int height);

/**
 * send response signal to the client
 *
 * @param [in]  connected_socket file descriptor of the connected socket
 * @param [in]  succeed          whether if the requested process succeeded
 */
void response_signal(SOCKET connected_socket, OPJ_BOOL succeed);

#endif /* !IMGSOCK_MANAGER_H_ */

/*! \file
 * PROTOCOL specification to communicate with opj_dec_server
 *
 *\section sec1 JPIP-stream
 * Cache JPT- JPP- stream in server
 *
 * client -> server: JPIP-stream\\n version 1.1\\n (optional for cid registration: targetnamestring\\n  tidstring\\n  cidstring\\n) bytelengthvalue\\n data \n
 * server -> client: 1 or 0 (of 1Byte response signal)
 *
 *\section sec2 PNM request
 * Get decoded PGM/PPM image
 *
 * client -> server: PNM request\\n [cid/tid]string\\n fw\\n fh\\n \n
 * server -> client: P6 or P5 (2Byte) width (2Byte Big endian) height (2Byte Big endian) maxval (1Byte) data
 *
 *\section sec3 XML request
 * Get XML data
 *
 * client -> server: XML request\\n \n
 * server -> client: XML (3Byte) length (2Byte Big endian) data
 *
 *\section sec4 TID request
 * Get target ID of target image
 *
 * client -> server: TID request\\n targetname\\n \n
 * server -> client: TID (3Byte) length (1Byte) tiddata
 *
 *\section sec5 CID request
 * Get Channel ID of identical target image
 *
 * client -> server: CID request\\n targetname\\n \n
 * server -> client: CID (3Byte) length (1Byte) ciddata
 *
 *\section sec6 CID destroy
 * Close Channel ID
 *
 * client -> server: CID destroy\\n ciddata \n
 * server -> client: 1 or 0 (of 1Byte response signal)
 *
 *\section sec7 SIZ request
 * Get original size of image
 *
 * client -> server: SIZ request\\n  tidstring\\n  cidstring\\n \n
 * server -> client: SIZ (3Byte) width (3Byte Big endian) height (3Byte Big endian)
 *
 *\section sec8 JP2 save
 * Save in JP2 file format
 *
 * client -> server: JP2 save\\n ciddata \n
 * server -> client: 1 or 0 (of 1Byte response signal)
 *
 *\section sec9 QUIT
 * Quit the opj_dec_server program
 *
 * client -> server: quit or QUIT
 */
