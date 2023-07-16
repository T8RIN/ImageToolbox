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

#ifndef     BOXHEADER_MANAGER_H_
# define    BOXHEADER_MANAGER_H_

#include "openjpeg.h"
#include "byte_manager.h"
#include "box_manager.h"

/** box header parameters*/
typedef struct boxheader_param {
    Byte_t headlen;              /**< header length  8 or 16*/
    Byte8_t length;               /**< length of the reference Box*/
    char type[4];              /**< type of information in the DBox*/
    struct boxheader_param *next; /**< pointer to the next header box*/
} boxheader_param_t;


/**
 * generate a box header at the given offset
 *
 * @param[in] fd     file discriptor of the JP2 file
 * @param[in] offset Box offset
 * @return           pointer to the structure of generate box header parameters
 */
boxheader_param_t *gene_boxheader(int fd, OPJ_OFF_T offset);

/**
 * generate a child box header at the given offset
 *
 * @param[in] superbox super box pointer
 * @param[in] offset   offset from DBox first byte of superbox
 * @return             pointer to the structure of generate box header parameters
 */
boxheader_param_t *gene_childboxheader(box_param_t *superbox,
                                       OPJ_OFF_T offset);

/**
 * print box header parameters
 *
 * @param[in] boxheader boxheader pointer
 */
void print_boxheader(boxheader_param_t *boxheader);

#endif      /* !BOXHEADER_MANAGER_H_ */
