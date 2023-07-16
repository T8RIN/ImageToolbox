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

#ifndef     BYTE_MANAGER_H_
#define     BYTE_MANAGER_H_

#include <stddef.h>
#include "openjpeg.h"
#include "opj_stdint.h"

typedef uint8_t Byte_t;
typedef uint16_t Byte2_t;
typedef uint32_t Byte4_t;
typedef uint64_t Byte8_t;

/**
 * fetch bytes of data in file stream
 *
 * @param[in] fd     file discriptor
 * @param[in] offset start Byte position
 * @param[in] size   Byte length
 * @return           pointer to the fetched data
 */
Byte_t *fetch_bytes(int fd, OPJ_OFF_T offset, OPJ_SIZE_T size);


/**
 * fetch a 1-byte Byte codes in file stream
 *
 * @param[in] fd     file discriptor
 * @param[in] offset start Byte position
 * @return           fetched codes
 */
Byte_t fetch_1byte(int fd, OPJ_OFF_T offset);

/**
 * fetch a 2-byte big endian Byte codes in file stream
 *
 * @param[in] fd     file discriptor
 * @param[in] offset start Byte position
 * @return           fetched codes
 */
Byte2_t fetch_2bytebigendian(int fd, OPJ_OFF_T offset);

/**
 * fetch a 4-byte big endian Byte codes in file stream
 *
 * @param[in] fd     file discriptor
 * @param[in] offset start Byte position
 * @return           fetched codes
 */
Byte4_t fetch_4bytebigendian(int fd, OPJ_OFF_T offset);

/**
 * fetch a 8-byte big endian Byte codes in file stream
 *
 * @param[in] fd     file discriptor
 * @param[in] offset start Byte position
 * @return           fetched codes
 */
Byte8_t fetch_8bytebigendian(int fd, OPJ_OFF_T offset);


/**
 * convert 2-byte big endian Byte codes to number
 *
 * @param[in] buf Byte codes
 * @return        resolved number
 */
Byte2_t big2(Byte_t *buf);

/**
 * convert 4-byte big endian Byte codes to number
 *
 * @param[in] buf Byte codes
 * @return        resolved number
 */
Byte4_t big4(Byte_t *buf);

/**
 * convert 8-byte big endian Byte codes to number
 *
 * @param[in] buf Byte codes
 * @return        resolved number
 */
Byte8_t big8(Byte_t *buf);

/**
 * modify 4Byte code in a codestream
 *
 * @param[in]  code code value
 * @param[out] stream modifying codestream
 */
void modify_4Bytecode(Byte4_t code, Byte_t *stream);

/**
 * Get file size
 *
 * @param[in] fd file discriptor
 * @return       file size
 */
OPJ_OFF_T get_filesize(int fd);

#endif      /* !BYTE_MANAGER_H_ */
