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

#ifndef     TARGET_MANAGER_H_
# define    TARGET_MANAGER_H_

#include "index_manager.h"

/** maximum length of target identifier*/
#define MAX_LENOFTID 30

/** target parameters*/
typedef struct target_param {
    char tid[MAX_LENOFTID];         /**< target identifier*/
    char *targetname;               /**< local file path or URL ( URL is supported only with SERVER mode)*/
    int fd;                         /**< file descriptor*/
#ifdef SERVER
    char *tmpfname;                 /**< temporal file name to download a remote target file*/
#endif
    int csn;                        /**< codestream number                                  */
    index_param_t
    *codeidx;         /**< index information of codestream                    */
    int num_of_use;                 /**< numbers of sessions referring to this target       */
    OPJ_BOOL jppstream;                 /**< if this target can return JPP-stream               */
    OPJ_BOOL jptstream;                 /**< if this target can return JPP-stream               */
    struct target_param
        *next;      /**< pointer to the next target                         */
} target_param_t;


/** Target list parameters*/
typedef struct targetlist_param {
    target_param_t *first; /**< first target pointer of the list*/
    target_param_t *last;  /**< last  target pointer of the list*/
} targetlist_param_t;



/**
 * generate a target list
 *
 * @return pointer to the generated target list
 */
targetlist_param_t * gene_targetlist(void);


/**
 * generate a target
 *
 * @param[in] targetlist target list to insert the generated target
 * @param[in] targetpath file path or URL of the target
 * @return               pointer to the generated target
 */
target_param_t * gene_target(targetlist_param_t *targetlist, char *targetpath);


/**
 * refer a target, used to make a new cache model
 *
 * @param[in]  reftarget reference target pointer
 * @param[out] ptr       address of feeding target pointer
 */
void refer_target(target_param_t *reftarget, target_param_t **ptr);


/**
 * refer a target, used to make a new cache model
 *
 * @param[in]  target reference pointer to the target
 */
void unrefer_target(target_param_t *target);

/**
 * delete a target
 *
 * @param[in,out] target address of the deleting target pointer
 */
void delete_target(target_param_t **target);


/**
 * delete a target in list
 *
 * @param[in,out] target     address of the deleting target pointer
 * @param[in] targetlist target list pointer
 */
void delete_target_in_list(target_param_t **target,
                           targetlist_param_t *targetlist);


/**
 * delete target list
 *
 * @param[in,out] targetlist address of the target list pointer
 */
void delete_targetlist(targetlist_param_t **targetlist);


/**
 * print target parameters
 *
 * @param[in] target target pointer
 */
void print_target(target_param_t *target);

/**
 * print all target parameters
 *
 * @param[in] targetlist target list pointer
 */
void print_alltarget(targetlist_param_t *targetlist);


/**
 * search a target by target name
 *
 * @param[in] targetname target name
 * @param[in] targetlist target list pointer
 * @return               found target pointer
 */
target_param_t * search_target(const char targetname[],
                               targetlist_param_t *targetlist);


/**
 * search a target by tid
 *
 * @param[in] tid        target identifier
 * @param[in] targetlist target list pointer
 * @return               found target pointer
 */
target_param_t * search_targetBytid(const char tid[],
                                    targetlist_param_t *targetlist);

#endif      /* !TARGET_MANAGER_H_ */

