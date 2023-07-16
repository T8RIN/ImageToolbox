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

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "cache_manager.h"

cachelist_param_t *gene_cachelist(void) {
    cachelist_param_t *cachelist;

    cachelist = (cachelist_param_t *) malloc(sizeof(cachelist_param_t));

    cachelist->first = NULL;
    cachelist->last = NULL;

    return cachelist;
}

void delete_cachelist(cachelist_param_t **cachelist) {
    cache_param_t *cachePtr, *cacheNext;

    cachePtr = (*cachelist)->first;
    while (cachePtr != NULL) {
        cacheNext = cachePtr->next;
        delete_cache(&cachePtr);
        cachePtr = cacheNext;
    }
    free(*cachelist);
}

cache_param_t *gene_cache(const char *targetname, int csn, char *tid,
                          char *cid) {
    cache_param_t *cache;

    cache = (cache_param_t *) malloc(sizeof(cache_param_t));
    cache->filename = strdup(targetname);
    cache->tid = strdup(tid);
    cache->csn = csn;
    cache->cid = (char **) malloc(sizeof(char *));
    *cache->cid = strdup(cid);
    cache->numOfcid = 1;
#if 1
    cache->metadatalist = NULL;
#else
    cache->metadatalist = gene_metadatalist();
#endif
    cache->ihdrbox = NULL;
    cache->next = NULL;

    return cache;
}

void delete_cache(cache_param_t **cache) {
    int i;

    free((*cache)->filename);
    free((*cache)->tid);

    delete_metadatalist(&(*cache)->metadatalist);

    if ((*cache)->ihdrbox) {
        free((*cache)->ihdrbox);
    }
    for (i = 0; i < (*cache)->numOfcid; i++) {
        free((*cache)->cid[i]);
    }
    free((*cache)->cid);
    free(*cache);
}

void insert_cache_into_list(cache_param_t *cache, cachelist_param_t *cachelist) {
    if (cachelist->first) {
        cachelist->last->next = cache;
    } else {
        cachelist->first = cache;
    }
    cachelist->last = cache;
}

cache_param_t *search_cache(const char targetname[],
                            cachelist_param_t *cachelist) {
    cache_param_t *foundcache;

    if (!targetname) {
        return NULL;
    }

    foundcache = cachelist->first;

    while (foundcache != NULL) {

        if (strcmp(targetname, foundcache->filename) == 0) {
            return foundcache;
        }

        foundcache = foundcache->next;
    }
    return NULL;
}

cache_param_t *search_cacheBycsn(int csn, cachelist_param_t *cachelist) {
    cache_param_t *foundcache;

    foundcache = cachelist->first;

    while (foundcache != NULL) {

        if (csn == foundcache->csn) {
            return foundcache;
        }
        foundcache = foundcache->next;
    }
    return NULL;
}

cache_param_t *search_cacheBycid(const char cid[],
                                 cachelist_param_t *cachelist) {
    cache_param_t *foundcache;
    int i;

    if (!cid) {
        return NULL;
    }

    foundcache = cachelist->first;

    while (foundcache != NULL) {
        for (i = 0; i < foundcache->numOfcid; i++)
            if (strcmp(cid, foundcache->cid[i]) == 0) {
                return foundcache;
            }
        foundcache = foundcache->next;
    }
    return NULL;
}

cache_param_t *search_cacheBytid(const char tid[],
                                 cachelist_param_t *cachelist) {
    cache_param_t *foundcache;

    if (!tid) {
        return NULL;
    }

    foundcache = cachelist->first;

    while (foundcache != NULL) {
        if (strcmp(tid, foundcache->tid) == 0) {
            return foundcache;
        }
        foundcache = foundcache->next;
    }
    return NULL;
}

void add_cachecid(const char *cid, cache_param_t *cache) {
    if (!cid) {
        return;
    }

    if ((cache->cid = realloc(cache->cid,
                              (OPJ_SIZE_T)(cache->numOfcid + 1) * sizeof(char *))) == NULL) {
        fprintf(stderr, "failed to add new cid to cache table in add_cachecid()\n");
        return;
    }

    cache->cid[cache->numOfcid] = strdup(cid);

    cache->numOfcid++;
}

void update_cachetid(const char *tid, cache_param_t *cache) {
    if (!tid) {
        return;
    }

    if (tid[0] != '0' && strcmp(tid, cache->tid) != 0) {
        fprintf(stderr, "tid is updated to %s for %s\n", tid, cache->filename);
        free(cache->tid);
        cache->tid = strdup(tid);
    }
}

void remove_cidInCache(const char *cid, cache_param_t *cache);

void remove_cachecid(const char *cid, cachelist_param_t *cachelist) {
    cache_param_t *cache;

    cache = search_cacheBycid(cid, cachelist);
    remove_cidInCache(cid, cache);
}

void remove_cidInCache(const char *cid, cache_param_t *cache) {
    int idx = -1;
    char **tmp;
    int i, j;

    for (i = 0; i < cache->numOfcid; i++)
        if (strcmp(cid, cache->cid[i]) == 0) {
            idx = i;
            break;
        }

    if (idx == -1) {
        fprintf(stderr, "cid: %s not found\n", cid);
        return;
    }

    tmp = cache->cid;

    cache->cid = (char **) malloc((OPJ_SIZE_T)(cache->numOfcid - 1) * sizeof(
            char *));

    for (i = 0, j = 0; i < cache->numOfcid; i++) {
        if (i != idx) {
            cache->cid[j] = strdup(tmp[i]);
            j++;
        }
        free(tmp[i]);
    }
    free(tmp);

    cache->numOfcid--;
}

void print_cache(cache_param_t *cache) {
    int i;

    fprintf(stdout, "cache\n");
    fprintf(stdout, "\t filename: %s\n", cache->filename);
    fprintf(stdout, "\t tid: %s\n", cache->tid);
    fprintf(stdout, "\t csn: %d\n", cache->csn);
    fprintf(stdout, "\t cid:");

    for (i = 0; i < cache->numOfcid; i++) {
        fprintf(stdout, " %s", cache->cid[i]);
    }
    fprintf(stdout, "\n");
}

void print_allcache(cachelist_param_t *cachelist) {
    cache_param_t *ptr;

    fprintf(stdout, "cache list\n");

    ptr = cachelist->first;
    while (ptr != NULL) {
        print_cache(ptr);
        ptr = ptr->next;
    }
}
