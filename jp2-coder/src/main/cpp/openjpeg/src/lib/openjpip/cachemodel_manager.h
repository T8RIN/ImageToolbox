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

#ifndef     CACHEMODEL_MANAGER_H_
# define    CACHEMODEL_MANAGER_H_

#include "target_manager.h"

/** Cache model parameters*/
typedef struct cachemodel_param {
    target_param_t *target;        /**< reference pointer to the target*/
    OPJ_BOOL jppstream;                /**< return type, true: JPP-stream, false: JPT-stream*/
    OPJ_BOOL mhead_model;              /**< main header model, if sent, 1, else 0*/
    OPJ_BOOL *tp_model;                /**< dynamic array pointer of tile part model, if sent, 1, else 0*/
    OPJ_BOOL *th_model;                /**< dynamic array pointer of tile header model*/
    OPJ_BOOL **pp_model;               /**< dynamic array pointer of precint packet model*/
    struct cachemodel_param *next; /**< pointer to the next cache model*/
} cachemodel_param_t;

/** Cache model list parameters*/
typedef struct cachemodellist_param {
    cachemodel_param_t *first; /**< first cache model pointer of the list*/
    cachemodel_param_t *last;  /**< last  cache model pointer of the list*/
} cachemodellist_param_t;


/**
 * generate a cache model list
 *
 * @return pointer to the generated cache model list
 */
cachemodellist_param_t *gene_cachemodellist(void);

/**
 * generate a cache model under a list
 *
 * @param[in] cachemodellist cachemodel list to insert the generated cache model, NULL for stateless
 * @param[in] target         pointer the reference target
 * @param[in] reqJPP         if JPP-stream is desired true, JPT-stream false
 * @return                   pointer to the generated cache model
 */
cachemodel_param_t *gene_cachemodel(cachemodellist_param_t *cachemodellist,
                                    target_param_t *target, OPJ_BOOL reqJPP);


/**
 * print cache model
 *
 * @param[in] cachemodel cache model
 */
void print_cachemodel(cachemodel_param_t cachemodel);


/**
 * search a cache model of a target
 *
 * @param[in] target         referring target
 * @param[in] cachemodellist cache model list
 * @return                   found cache model pointer
 */
cachemodel_param_t *search_cachemodel(target_param_t *target,
                                      cachemodellist_param_t *cachemodellist);


/**
 * check if all data has been sent
 *
 * @param[in] cachemodel cache model
 * @return               true if sent all, false otherwise
 */
OPJ_BOOL is_allsent(cachemodel_param_t cachemodel);


/**
 * delete a cache model
 *
 * @param[in] cachemodel     address of the cachemodel pointer
 */
void delete_cachemodel(cachemodel_param_t **cachemodel);

/**
 * delete cachemodel list
 *
 * @param[in,out] cachemodellist address of the cachemodel list pointer
 */
void delete_cachemodellist(cachemodellist_param_t **cachemodellist);


#endif      /* !CACHEMODEL_MANAGER_H_ */
