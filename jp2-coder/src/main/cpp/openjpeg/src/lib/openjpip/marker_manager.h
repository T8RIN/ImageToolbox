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

#ifndef     MARKER_MANAGER_H_
# define    MARKER_MANAGER_H_

#include "codestream_manager.h"


/** Marker parameters*/
typedef struct marker_param {
    codestream_param_t cs; /**< corresponding codestream*/
    Byte2_t code;          /**< marker code*/
    OPJ_OFF_T offset;        /**< offset relative to the start of the codestream ( including the length parameter but not the marker itself)*/
    Byte2_t length;        /**< marker segment length*/
} marker_param_t;


/**
 * set marker parameters from inputs
 *
 * @param[in] cs     marker code
 * @param[in] code   marker code
 * @param[in] offset offset in the codestream
 * @param[in] length marker segment length
 * @return           structure of generated marker parameters
 */
marker_param_t set_marker(codestream_param_t cs, Byte2_t code, OPJ_OFF_T offset,
                          Byte2_t length);


/**
 * fetch marker content 1-bytes of data in file stream
 *
 * @param[in] marker marker structure
 * @param[in] offset start Byte position in marker
 * @return           fetched code
 */
Byte_t fetch_marker1byte(marker_param_t marker, OPJ_OFF_T offset);

/**
 * fetch marker content 2-byte big endian Byte codes in file stream
 *
 * @param[in] marker marker structure
 * @param[in] offset start Byte position in marker
 * @return           fetched code
 */
Byte2_t fetch_marker2bytebigendian(marker_param_t marker, OPJ_OFF_T offset);

/**
 * fetch marker content 4-byte big endian Byte codes in file stream
 *
 * @param[in] marker marker structure
 * @param[in] offset start Byte position in marker
 * @return           fetched code
 */
Byte4_t fetch_marker4bytebigendian(marker_param_t marker, OPJ_OFF_T offset);


#endif      /* !MARKER_MANAGER_H_ */
