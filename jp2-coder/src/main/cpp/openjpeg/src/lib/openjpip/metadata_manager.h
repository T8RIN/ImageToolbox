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

#ifndef     METADATA_MANAGER_H_
#define     METADATA_MANAGER_H_

#include "box_manager.h"
#include "placeholder_manager.h"

typedef struct boxcontents_param {
    OPJ_OFF_T offset; /**< byte position of the box contents in the file*/
    Byte8_t length; /**< length of the box contents*/
} boxcontents_param_t;

/** metadata-bin parameters*/
typedef struct metadata_param {
    Byte8_t idx;                                  /**< index number*/
    boxlist_param_t *boxlist;                 /**< box list*/
    placeholderlist_param_t *placeholderlist; /**< placeholder box list*/
    boxcontents_param_t
    *boxcontents;         /**< box contens in case of no boxlist and placeholderlist*/
    struct metadata_param
        *next;              /**< pointer to the next metadata-bin*/
} metadata_param_t;

/** metadata-bin list parameters*/
typedef struct metadatalist_param {
    metadata_param_t *first; /**< first metadata-bin pointer of the list*/
    metadata_param_t *last;  /**< last metadata-bin pointer of the list*/
} metadatalist_param_t;


/**
 * generate a metadata list
 *
 * @return pointer to the generated metadata list
 */
metadatalist_param_t * gene_metadatalist(void);


/**
 * construct metadata-bin list of JP2 file
 *
 * @param[in] fd file descriptor
 * @return            pointer to the generated metadata-bin list
 */
metadatalist_param_t * const_metadatalist(int fd);


/**
 * delete metadata list
 *
 * @param[in,out] list address of the metadata list pointer
 */
void delete_metadatalist(metadatalist_param_t **list);


/**
 * generate a metadata bin
 *
 * @param[in] idx         metadata-bin index
 * @param[in] boxlist     box list pointer
 * @param[in] phldlist    placeholder list pointer
 * @param[in] boxcontents boxcontents pointer
 * @return                pointer to the generated metadata bin
 */
metadata_param_t * gene_metadata(Byte8_t idx, boxlist_param_t *boxlist,
                                 placeholderlist_param_t *phldlist, boxcontents_param_t *boxcontents);

/**
 * delete a metadata bin
 *
 * @param[in,out] metadata address of the deleting metadata bin pointer
 */
void delete_metadata(metadata_param_t **metadata);

/**
 * generate box contents
 *
 * @return pointer to the box contents
 */
boxcontents_param_t * gene_boxcontents(OPJ_OFF_T offset, OPJ_SIZE_T length);

/**
 * print metadata-bin parameters
 *
 * @param[in] metadata metadata-bin pointer
 */
void print_metadata(metadata_param_t *metadata);

/**
 * print all metadata parameters
 *
 * @param[in] list metadata list pointer
 */
void print_allmetadata(metadatalist_param_t *list);


/**
 * search a metadata bin by index
 *
 * @param[in] idx  index
 * @param[in] list metadata-bin list pointer
 * @return         found metadata-bin pointer
 */
metadata_param_t * search_metadata(Byte8_t idx, metadatalist_param_t *list);


/**
 * search a metadata index by box-type
 *
 * @param[in] boxtype box-type
 * @param[in] list    metadata-bin list pointer
 * @return            found metadata-bin index, if not found, -1
 */
Byte8_t search_metadataidx(char boxtype[4], metadatalist_param_t *list);


/**
 * insert a metadata-bin into list
 *
 * @param[in] metabin      metadata-bin pointer
 * @param[in] metadatalist metadata list pointer
 */
void insert_metadata_into_list(metadata_param_t *metabin,
                               metadatalist_param_t *metadatalist);

#endif      /* !METADATA_MANAGER_H_ */
