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

#ifndef     CACHE_MANAGER_H_
# define    CACHE_MANAGER_H_

#include "metadata_manager.h"
#include "ihdrbox_manager.h"

/** cache parameters*/
typedef struct cache_param {
    char *filename;                     /**< file name*/
    char *tid;                          /**< taregt identifier*/
    int csn;                            /**< codestream number*/
    char **cid;                         /**< dynamic array of channel identifiers*/
    int numOfcid;                       /**< number of cids*/
    metadatalist_param_t *metadatalist; /**< metadata-bin list*/
    ihdrbox_param_t *ihdrbox;           /**< ihdrbox*/
    struct cache_param *next;           /**< pointer to the next cache*/
} cache_param_t;

/**< cache list parameters*/
typedef struct cachelist_param {
    cache_param_t *first; /**< first cache pointer of the list*/
    cache_param_t *last;  /**< last  cache pointer of the list*/
} cachelist_param_t;


/**
 * generate a cache list
 *
 * @return pointer to the generated cache list
 */
cachelist_param_t *gene_cachelist(void);

/**
 * delete cache list
 *
 * @param[in,out] cachelist address of the cache list pointer
 */
void delete_cachelist(cachelist_param_t **cachelist);

/**
 * generate a cache
 *
 * @param[in] targetname target file name
 * @param[in] csn        codestream number
 * @param[in] tid        target identifier
 * @param[in] cid        channel identifier
 * @return               pointer to the generated cache
 */
cache_param_t *gene_cache(const char *targetname, int csn, char *tid,
                          char *cid);

/**
 * delete a cache
 *
 * @param[in] cache address of the cache pointer
 */
void delete_cache(cache_param_t **cache);

/**
 * insert a cache into list
 *
 * @param[in] cache     cache pointer
 * @param[in] cachelist cache list pointer
 */
void insert_cache_into_list(cache_param_t *cache, cachelist_param_t *cachelist);


/**
 * search a cache by target name
 *
 * @param[in] targetname target filename
 * @param[in] cachelist  cache list pointer
 * @return               found cache pointer
 */
cache_param_t *search_cache(const char targetname[],
                            cachelist_param_t *cachelist);


/**
 * search a cache by csn
 *
 * @param[in] csn        codestream number
 * @param[in] cachelist  cache list pointer
 * @return               found cache pointer
 */
cache_param_t *search_cacheBycsn(int csn, cachelist_param_t *cachelist);


/**
 * search a cache by cid
 *
 * @param[in] cid        channel identifier
 * @param[in] cachelist  cache list pointer
 * @return               found cache pointer
 */
cache_param_t *search_cacheBycid(const char cid[],
                                 cachelist_param_t *cachelist);


/**
 * search a cache by tid
 *
 * @param[in] tid        target identifier
 * @param[in] cachelist  cache list pointer
 * @return               found cache pointer
 */
cache_param_t *search_cacheBytid(const char tid[],
                                 cachelist_param_t *cachelist);

/**
 * add cid into a cache
 *
 * @param[in] cid   channel identifier
 * @param[in] cache cache pointer
 */
void add_cachecid(const char *cid, cache_param_t *cache);


/**
 * update tid of a cache
 *
 * @param[in] tid   target identifier
 * @param[in] cache cache pointer
 */
void update_cachetid(const char *tid, cache_param_t *cache);


/**
 * remove cid in cache
 *
 * @param[in] cid       channel identifier
 * @param[in] cachelist cachelist pointer
 */
void remove_cachecid(const char *cid, cachelist_param_t *cachelist);


/**
 * print cache parameters
 *
 * @param[in] cache cache pointer
 */
void print_cache(cache_param_t *cache);

/**
 * print all cache parameters
 *
 * @param[in] cachelist cache list pointer
 */
void print_allcache(cachelist_param_t *cachelist);

#endif /* !CACHE_MANAGER_H_ */
