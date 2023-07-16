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

#ifndef     JP2K_ENCODER_H_
# define    JP2K_ENCODER_H_

#include "byte_manager.h"
#include "msgqueue_manager.h"

/**
 * reconstruct j2k codestream from message queue
 *
 * @param[in]  msgqueue   message queue pointer
 * @param[in]  jpipstream original jpt- jpp- stream
 * @param[in]  csn        codestream number
 * @param[in]  fw         reconstructing image frame width
 * @param[in]  fh         reconstructing image frame height
 * @param[out] j2klen     pointer to the j2k codestream length
 * @return     generated  reconstructed j2k codestream
 */
Byte_t * recons_j2k(msgqueue_param_t *msgqueue, Byte_t *jpipstream, Byte8_t csn,
                    int fw, int fh, Byte8_t *j2klen);


/**
 * reconstruct jp2 file codestream from message queue
 *
 * @param[in]  msgqueue   message queue pointer
 * @param[in]  jpipstream original jpt- jpp- stream
 * @param[in]  csn        codestream number
 * @param[out] jp2len     pointer to the jp2 codestream length
 * @return     generated  reconstructed jp2 codestream
 */
Byte_t * recons_jp2(msgqueue_param_t *msgqueue, Byte_t *jpipstream, Byte8_t csn,
                    Byte8_t *jp2len);

/**
 * reconstruct j2k codestream of mainheader from message queue
 *
 * @param[in]  msgqueue   message queue pointer
 * @param[in]  jpipstream original jpt- jpp- stream
 * @param[in]  csn        codestream number
 * @param[out] j2klen     pointer to the j2k codestream length
 * @return     generated  reconstructed j2k codestream
 */
Byte_t * recons_j2kmainhead(msgqueue_param_t *msgqueue, Byte_t *jpipstream,
                            Byte8_t csn, Byte8_t *j2klen);

#endif      /* !JP2K_ENCODER_H_ */
