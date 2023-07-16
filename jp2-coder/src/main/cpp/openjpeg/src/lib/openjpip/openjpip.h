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

#ifndef         OPENJPIP_H_
# define        OPENJPIP_H_

#include "session_manager.h"
#include "target_manager.h"
#include "query_parser.h"
#include "msgqueue_manager.h"
#include "sock_manager.h"
#include "auxtrans_manager.h"

#ifdef SERVER

#include "fcgi_stdio.h"
#define logstream FCGI_stdout

#else

#define FCGI_stdout stdout
#define FCGI_stderr stderr
#define logstream stderr

#include "cache_manager.h"
#include "byte_manager.h"
#include "imgsock_manager.h"

#include "metadata_manager.h"
#include "ihdrbox_manager.h"
#include "index_manager.h"

#endif /*SERVER*/

/*
 *==========================================================
 * JPIP server API
 *==========================================================
 */

#ifdef SERVER

/** Server static records*/
typedef struct server_record {
    sessionlist_param_t *sessionlist; /**< list of session records*/
    targetlist_param_t *targetlist;   /**< list of target records*/
    auxtrans_param_t auxtrans;
} server_record_t;

/** Query/response data for each client*/
typedef struct QR {
    query_param_t *query;             /**< query parameters*/
    msgqueue_param_t *msgqueue;       /**< message queue*/
    channel_param_t *channel;         /**< channel, (NULL if stateless)*/
} QR_t;

/**
 * Initialize the JPIP server
 *
 * @param[in] tcp_auxport opening tcp auxiliary port ( 0 not to open, valid No. 49152-65535)
 * @param[in] udp_auxport opening udp auxiliary port ( 0 not to open, valid No. 49152-65535)
 * @return                initialized server record pointer
 */
server_record_t * init_JPIPserver(int tcp_auxport, int udp_auxport);

/**
 * Terminate the JPIP server
 *
 * @param[in] rec address of deleting server static record pointer
 */
void terminate_JPIPserver(server_record_t **rec);

/**
 * 1st process per client request; parse query string
 *
 * @param[in]  query_string request query string
 * @return     initialized query/response data pointer
 */
QR_t * parse_querystring(const char *query_string);

/**
 * 2nd process; process JPIP request and construct message queue
 *
 * @param[in]  rec server static record pointer
 * @param[in]  qr  query/response data pointer
 * @return     true if succeed, otherwise false
 */
OPJ_BOOL process_JPIPrequest(server_record_t *rec, QR_t *qr);

/**
 * 3rd process; send response data JPT/JPP-stream
 *
 * @param[in]  rec server static record pointer
 * @param[in]  qr  query/response data pointer
 */
void send_responsedata(server_record_t *rec, QR_t *qr);

/**
 * 4th (last) process;
 *
 * @param[in]  rec server static record pinter
 * @param[in]  qr  address of query/response data pointer
 */
void end_QRprocess(server_record_t *rec, QR_t **qr);

/**
 * Option for local tests; print out parameter values to logstream (stderr)
 *
 * @param[in]  query    true if query parameters are to be printed out
 * @param[in]  messages true if queue of messages is to be printed out
 * @param[in]  sessions true if session list      is to be printed out
 * @param[in]  targets  true if target list       is  to be printed out
 * @param[in]  qr       query/response data pointer
 * @param[in]  rec      server static record pinter
 */
void local_log(OPJ_BOOL query, OPJ_BOOL messages, OPJ_BOOL sessions,
               OPJ_BOOL targets, QR_t *qr, server_record_t *rec);

#endif /*SERVER*/

/*
 *==========================================================
 *      JPIP decoding server API
 *==========================================================
 */

#ifndef SERVER

/** Decoding server static records*/
typedef struct dec_server_record {
    cachelist_param_t *cachelist; /**< cache list*/
    Byte_t *jpipstream;           /**< JPT/JPP stream*/
    OPJ_SIZE_T jpipstreamlen;            /**< length of jpipstream*/
    msgqueue_param_t *msgqueue;   /**< parsed message queue of jpipstream*/
    SOCKET listening_socket;      /**< listenning socket*/
} dec_server_record_t;


/** Client socket identifier*/
typedef SOCKET client_t;

/**
 * Initialize the image decoding server
 *
 * @param[in] port opening tcp port (valid No. 49152-65535)
 * @return         initialized decoding server record pointer
 */
OPJ_API dec_server_record_t * OPJ_CALLCONV init_dec_server(int port);

/**
 * Terminate the  image decoding server
 *
 * @param[in] rec address of deleting decoding server static record pointer
 */
OPJ_API void OPJ_CALLCONV terminate_dec_server(dec_server_record_t **rec);

/**
 * Accept client connection
 *
 * @param[in] rec decoding server static record pointer
 * @return        client socket ID, -1 if failed
 */
OPJ_API client_t OPJ_CALLCONV accept_connection(dec_server_record_t *rec);

/**
 * Handle client request
 *
 * @param[in] client client socket ID
 * @param[in] rec    decoding server static record pointer
 * @return           true if succeed
 */
OPJ_API OPJ_BOOL OPJ_CALLCONV handle_clientreq(client_t client,
        dec_server_record_t *rec);

#endif /*SERVER*/

/*
 *==========================================================
 *     JPIP tool API
 *==========================================================
 */

#ifndef SERVER

/*
 * jpip to JP2 or J2K
 */

/** JPIP decoding parameters*/
typedef struct jpip_dec_param {
    Byte_t *jpipstream;                 /**< JPT/JPP-stream*/
    Byte8_t jpiplen;                    /**< length of jpipstream*/
    msgqueue_param_t *msgqueue;         /**< message queue*/
    metadatalist_param_t *metadatalist; /**< metadata list going into JP2 file*/
    ihdrbox_param_t *ihdrbox;           /**< ihdr box going into JP2 file*/
    Byte_t *jp2kstream;                 /**< J2K codestream or JP2 file codestream*/
    Byte8_t jp2klen;                    /**< length of j2kstream or JP2 file*/
} jpip_dec_param_t;

/**
 * Initialize jpip decoder
 *
 * @param[in] jp2 true in case of jp2 file encoding, else j2k file encoding
 * @return        JPIP decoding parameters pointer
 */
OPJ_API jpip_dec_param_t * OPJ_CALLCONV init_jpipdecoder(OPJ_BOOL jp2);

/**
 * Destroy jpip decoding parameters
 *
 * @param[in]  dec  address of JPIP decoding parameters pointer
 */
OPJ_API void OPJ_CALLCONV destroy_jpipdecoder(jpip_dec_param_t **dec);

/**
 * Read jpip codestream from a file
 *
 * @param[in]  fname file name
 * @param[in]  dec   JPIP decoding parameters pointer
 * @return           true if succeed
 */
OPJ_API OPJ_BOOL OPJ_CALLCONV fread_jpip(const char fname[],
        jpip_dec_param_t *dec);

/**
 * Decode jpip codestream
 *
 * @param[in]  dec   JPIP decoding parameters pointer
 */
OPJ_API void OPJ_CALLCONV decode_jpip(jpip_dec_param_t *dec);

/**
 * Write J2K/JP2 codestream to a file
 *
 * @param[in]  fname file name
 * @param[in]  dec   JPIP decoding parameters pointer
 * @return           true if succeed
 */
OPJ_API OPJ_BOOL OPJ_CALLCONV fwrite_jp2k(const char fname[],
        jpip_dec_param_t *dec);

/**
 * Option; print out parameter values to stderr
 *
 * @param[in]  messages true if queue of messages is to be printed out
 * @param[in]  metadata true if metadata          is to be printed out
 * @param[in]  ihdrbox  true if image header data is to be printed out
 * @param[in]  dec   JPIP decoding parameters pointer
 */
OPJ_API void OPJ_CALLCONV output_log(OPJ_BOOL messages, OPJ_BOOL metadata,
                                     OPJ_BOOL ihdrbox, jpip_dec_param_t *dec);

/*
 *  test the format of index (cidx) box in JP2 file
 */

/** Redefinition of index parameters*/
typedef index_param_t index_t;

/**
 * Parse JP2 file and get index information from cidx box inside
 *
 * @param[in] fd file descriptor of the JP2 file
 * @return       pointer to the generated structure of index parameters
 */
OPJ_API index_t * OPJ_CALLCONV get_index_from_JP2file(int fd);

/**
 * Destroy index parameters
 *
 * @param[in,out] idx addressof the index pointer
 */
OPJ_API void OPJ_CALLCONV destroy_index(index_t **idx);


/**
 * print index parameters
 *
 * @param[in] index index parameters
 */
OPJ_API void OPJ_CALLCONV output_index(index_t *index);

#endif /*SERVER*/

#endif /* !OPENJPIP_H_ */
