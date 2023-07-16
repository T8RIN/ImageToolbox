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

#ifndef     MANFBOX_MANAGER_H_
# define    MANFBOX_MANAGER_H_

#include "byte_manager.h"
#include "box_manager.h"
#include "boxheader_manager.h"


/** manifest box parameters*/
/** I.3.2.3 Manifest box*/
typedef struct manfbox_param {
    boxheader_param_t *first; /**< top of the box header list*/
} manfbox_param_t;


/**
 * generate manifest box
 *
 * @param[in] box pointer to the reference manf box
 * @return        generated manfbox
 */
manfbox_param_t * gene_manfbox(box_param_t *box);


/**
 * delete manifest box
 *
 * @param[in,out] manf addressof the manfbox pointer
 */
void delete_manfbox(manfbox_param_t **manf);


/**
 * print manf box parameters
 *
 * @param[in] manf manf box pointer
 */
void print_manfbox(manfbox_param_t *manf);


/**
 * search a boxheader by box type from manifest box
 *
 * @param[in]     type box type
 * @param[in]     manf manf box pointer
 * @return             found box pointer
 */
boxheader_param_t * search_boxheader(const char type[], manfbox_param_t *manf);


#endif      /* !MANFBOX_MANAGER_H_ */
