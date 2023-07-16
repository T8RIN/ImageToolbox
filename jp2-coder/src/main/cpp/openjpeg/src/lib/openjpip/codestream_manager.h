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

#ifndef     CODESTREAM_MANAGER_H_
# define    CODESTREAM_MANAGER_H_

#include "byte_manager.h"

/** codestream parameters*/
typedef struct codestream_param {
    int fd;         /**< file descriptor*/
    OPJ_OFF_T offset; /**< byte position of DBox (Box Contents) in the file*/
    Byte8_t length; /**< content length*/
} codestream_param_t;


/**
 * set codestream parameters from inputs
 *
 * @param[in] fd     file descriptor
 * @param[in] offset offset in the file
 * @param[in] length codestream length
 * @return           structure of generated codestream parameters
 */
codestream_param_t set_codestream(int fd, OPJ_OFF_T offset, OPJ_SIZE_T length);


/**
 * fetch Codestream bytes of data in file stream
 *
 * @param[in] cs     codestream pointer
 * @param[in] offset start Byte position in codestream
 * @param[in] size   Byte length
 * @return           pointer to the fetched data
 */
Byte_t * fetch_codestreambytes(codestream_param_t *cs, OPJ_OFF_T offset,
                               OPJ_SIZE_T size);

/**
 * fetch Codestream 1-byte Byte code in file stream
 *
 * @param[in] cs     codestream pointer
 * @param[in] offset start Byte position in codestream
 * @return           fetched code
 */
Byte_t fetch_codestream1byte(codestream_param_t *cs, OPJ_OFF_T offset);

/**
 * fetch Codestream 2-byte big endian Byte codes in file stream
 *
 * @param[in] cs     codestream pointer
 * @param[in] offset start Byte position in codestream
 * @return           fetched code
 */
Byte2_t fetch_codestream2bytebigendian(codestream_param_t *cs,
                                       OPJ_OFF_T offset);

/**
 * fetch Codestream 4-byte big endian Byte codes in file stream
 *
 * @param[in] cs     codestream pointer
 * @param[in] offset start Byte position in codestream
 * @return           fetched code
 */
Byte4_t fetch_codestream4bytebigendian(codestream_param_t *cs,
                                       OPJ_OFF_T offset);


/**
 * print codestream parameters
 *
 * @param[in] cs codestream
 */
void print_codestream(codestream_param_t cs);


#endif      /* !CODESTREAM_MANAGER_H_ */
