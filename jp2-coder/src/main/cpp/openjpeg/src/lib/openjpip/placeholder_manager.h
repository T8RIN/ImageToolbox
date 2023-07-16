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

#ifndef     PLACEHOLDER_MANAGER_H_
# define    PLACEHOLDER_MANAGER_H_

#include "byte_manager.h"
#include "box_manager.h"

/** A.3.6.3 Placeholder box format*/
/** placeholder box parameters*/
typedef struct placeholder_param {
    Byte4_t LBox;
    char TBox[4];
    Byte4_t Flags;
    Byte8_t OrigID;
    Byte_t *OrigBH;   /**< dynamic memory pointer*/
    Byte_t OrigBHlen; /**< length of OrigBH*/
#ifdef AAA
    Byte8_t EquivID;
    Byte_t *EquivBH;   /**< dynamic memory pointer*/
    Byte_t EquivBHlen; /**< length of EquivBH*/
    Byte8_t CSID;
    Byte4_t NCS;
#endif /*AAA*/
    struct placeholder_param *next; /**< pointer to the next placeholder*/
} placeholder_param_t;


/** placeholder box list parameters*/
typedef struct placeholderlist_param {
    placeholder_param_t *first; /**< first placeholder pointer of the list*/
    placeholder_param_t *last;  /**< last  placeholder pointer of the list*/
} placeholderlist_param_t;


/**
 * generate a placeholder list
 *
 * @return pointer to the generated placeholder list
 */
placeholderlist_param_t * gene_placeholderlist(void);


/**
 * delete placeholder list
 *
 * @param[in,out] list address of the placeholder list pointer
 */
void delete_placeholderlist(placeholderlist_param_t **list);


/**
 * generate a placeholder of a box
 *
 * @param[in] box    box pointer
 * @param[in] origID metadata-bin ID of the bin containing the contents of the original box
 * @return           pointer to the generated placeholder
 */
placeholder_param_t * gene_placeholder(box_param_t *box, Byte8_t origID);


/**
 * delete a placeholder
 *
 * @param[in,out] placeholder address of the placeholder pointer
 */
void delete_placeholder(placeholder_param_t **placeholder);

void insert_placeholder_into_list(placeholder_param_t *phld,
                                  placeholderlist_param_t *phldlist);


/**
 * print placeholder parameters
 *
 * @param[in] phld placeholder pointer
 */
void print_placeholder(placeholder_param_t *phld);


/**
 * print all placeholder parameters
 *
 * @param[in] list placeholder list pointer
 */
void print_allplaceholder(placeholderlist_param_t *list);

#endif      /* !PLACEHOLDER_MANAGER_H_ */
