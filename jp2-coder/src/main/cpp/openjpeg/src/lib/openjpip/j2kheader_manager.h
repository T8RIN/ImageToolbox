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

#ifndef         J2KHEADER_MANAGER_H_
# define        J2KHEADER_MANAGER_H_

#include "byte_manager.h"
#include "index_manager.h"

/**
 * get main header information from j2k codestream
 *
 * @param[in]  j2kstream j2k codestream
 * @param[out] SIZ       SIZ marker pointer
 * @param[out] COD       COD marker pointer
 * @return               if succeeded (true) or failed (false)
 */
OPJ_BOOL get_mainheader_from_j2kstream(Byte_t *j2kstream,
                                       SIZmarker_param_t *SIZ, CODmarker_param_t *COD);

/**
 * modify main header in j2k codestream to fit with the new number of decompositions
 *
 * @param[in]  j2kstream   j2k codestream
 * @param[in]  numOfdecomp the New number of decompositions
 * @param[in]  SIZ         original SIZ marker information
 * @param[in]  COD         original COD marker information
 * @param[out] j2klen      pointer to the length of j2k code stream
 * @return                 if succeeded (true) or failed (false)
 */
OPJ_BOOL modify_mainheader(Byte_t *j2kstream, int numOfdecomp,
                           SIZmarker_param_t SIZ, CODmarker_param_t COD, Byte8_t *j2klen);

/**
 * modify tile header in j2k codestream to fit with the tile part length, and new number of decompositions for multi-componet images
 *
 * @param[in]  j2kstream   j2k codestream
 * @param[in]  SOToffset   offset of SOT marker from the beginning of j2kstream
 * @param[in]  numOfdecomp the New number of decompositions, -1 if the same as original
 * @param[in]  Csiz        number of components
 * @param[out] j2klen      pointer to the length of j2k code stream
 * @return                 if succeeded (true) or failed (false)
 */
OPJ_BOOL modify_tileheader(Byte_t *j2kstream, Byte8_t SOToffset,
                           int numOfdecomp, Byte2_t Csiz, Byte8_t *j2klen);

#endif      /* !J2KHEADER_MANAGER_H_ */
