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

#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <assert.h>
#ifdef _WIN32
#define snprintf _snprintf /* Visual Studio */
#include <io.h>
#else
#include <sys/types.h>
#include <unistd.h>
#endif
#include <sys/stat.h>
#include <fcntl.h>
#include <time.h>
#include "target_manager.h"

#ifdef SERVER
#include <curl/curl.h>
#include "fcgi_stdio.h"
#define logstream FCGI_stdout
#else
#define FCGI_stdout stdout
#define FCGI_stderr stderr
#define logstream stderr
#endif /*SERVER*/

targetlist_param_t * gene_targetlist(void)
{
    targetlist_param_t *targetlist;

    targetlist = (targetlist_param_t *)opj_malloc(sizeof(targetlist_param_t));

    targetlist->first = NULL;
    targetlist->last  = NULL;

    return targetlist;
}


/**
 * open jp2 format image file
 *
 * @param[in]  filepath file name (.jp2)
 * @param[out] tmpfname new file name if filepath is a URL
 * @return              file descriptor
 */
int open_jp2file(const char filepath[], char tmpfname[]);

target_param_t * gene_target(targetlist_param_t *targetlist, char *targetpath)
{
    target_param_t *target;
    int fd;
    index_param_t *jp2idx;
    char tmpfname[MAX_LENOFTID];
    static int last_csn = 0;

    if (targetpath[0] == '\0') {
        fprintf(FCGI_stderr, "Error: exception, no targetpath in gene_target()\n");
        return NULL;
    }

    if ((fd = open_jp2file(targetpath, tmpfname)) == -1) {
        fprintf(FCGI_stdout, "Status: 404\r\n");
        return NULL;
    }

    if (!(jp2idx = parse_jp2file(fd))) {
        fprintf(FCGI_stdout, "Status: 501\r\n");
        return NULL;
    }

    target = (target_param_t *)opj_malloc(sizeof(target_param_t));
    snprintf(target->tid, MAX_LENOFTID, "%x-%x", (unsigned int)time(NULL),
             (unsigned int)rand());
    target->targetname = strdup(targetpath);
    target->fd = fd;
#ifdef SERVER
    if (tmpfname[0]) {
        target->tmpfname = strdup(tmpfname);
    } else {
        target->tmpfname = NULL;
    }
#endif
    target->csn = last_csn++;
    target->codeidx = jp2idx;
    target->num_of_use = 0;
    target->jppstream = OPJ_TRUE;
    target->jptstream = isJPTfeasible(*jp2idx);
    target->next = NULL;

    if (targetlist->first) { /* there are one or more entries*/
        targetlist->last->next = target;
    } else {               /* first entry*/
        targetlist->first = target;
    }
    targetlist->last = target;

#ifndef SERVER
    fprintf(logstream, "local log: target %s generated\n", targetpath);
#endif

    return target;
}

void refer_target(target_param_t *reftarget, target_param_t **ptr)
{
    *ptr = reftarget;
    reftarget->num_of_use++;
}

void unrefer_target(target_param_t *target)
{
    target->num_of_use--;
}

void delete_target(target_param_t **target)
{
    close((*target)->fd);

#ifdef SERVER
    if ((*target)->tmpfname) {
        fprintf(FCGI_stderr, "Temporal file %s is deleted\n", (*target)->tmpfname);
        remove((*target)->tmpfname);
    }
#endif

    if ((*target)->codeidx) {
        delete_index(&(*target)->codeidx);
    }

#ifndef SERVER
    fprintf(logstream, "local log: target: %s deleted\n", (*target)->targetname);
#endif

    opj_free((*target)->targetname);

    opj_free(*target);
}

void delete_target_in_list(target_param_t **target,
                           targetlist_param_t *targetlist)
{
    target_param_t *ptr;

    if (*target == targetlist->first) {
        targetlist->first = (*target)->next;
    } else {
        ptr = targetlist->first;
        while (ptr->next != *target) {
            ptr = ptr->next;
        }

        ptr->next = (*target)->next;

        if (*target == targetlist->last) {
            targetlist->last = ptr;
        }
    }
    delete_target(target);
}

void delete_targetlist(targetlist_param_t **targetlist)
{
    target_param_t *targetPtr, *targetNext;

    targetPtr = (*targetlist)->first;
    while (targetPtr != NULL) {
        targetNext = targetPtr->next;
        delete_target(&targetPtr);
        targetPtr = targetNext;
    }
    opj_free(*targetlist);
}

void print_target(target_param_t *target)
{
    fprintf(logstream, "target:\n");
    fprintf(logstream, "\t tid=%s\n", target->tid);
    fprintf(logstream, "\t csn=%d\n", target->csn);
    fprintf(logstream, "\t target=%s\n\n", target->targetname);
}

void print_alltarget(targetlist_param_t *targetlist)
{
    target_param_t *ptr;

    ptr = targetlist->first;
    while (ptr != NULL) {
        print_target(ptr);
        ptr = ptr->next;
    }
}

target_param_t * search_target(const char targetname[],
                               targetlist_param_t *targetlist)
{
    target_param_t *foundtarget;

    foundtarget = targetlist->first;

    while (foundtarget != NULL) {

        if (strcmp(targetname, foundtarget->targetname) == 0) {
            return foundtarget;
        }

        foundtarget = foundtarget->next;
    }
    return NULL;
}

target_param_t * search_targetBytid(const char tid[],
                                    targetlist_param_t *targetlist)
{
    target_param_t *foundtarget;

    foundtarget = targetlist->first;

    while (foundtarget != NULL) {

        if (strcmp(tid, foundtarget->tid) == 0) {
            return foundtarget;
        }

        foundtarget = foundtarget->next;
    }

    return NULL;
}

int open_remotefile(const char filepath[], char tmpfname[]);

int open_jp2file(const char filepath[], char tmpfname[])
{
    int fd;
    char *data;

    /* download remote target file to local storage*/
    if (strncmp(filepath, "http://", 7) == 0) {
        if ((fd = open_remotefile(filepath, tmpfname)) == -1) {
            return -1;
        }
    } else {
        tmpfname[0] = 0;
        if ((fd = open(filepath, O_RDONLY)) == -1) {
            fprintf(FCGI_stdout, "Reason: Target %s not found\r\n", filepath);
            return -1;
        }
    }
    /* Check resource is a JP family file.*/
    if (lseek(fd, 0, SEEK_SET) == -1) {
        close(fd);
        fprintf(FCGI_stdout, "Reason: Target %s broken (lseek error)\r\n", filepath);
        return -1;
    }

    data = (char *)opj_malloc(12);  /* size of header*/

    if (read(fd, data, 12) != 12) {
        opj_free(data);
        close(fd);
        fprintf(FCGI_stdout, "Reason: Target %s broken (read error)\r\n", filepath);
        return -1;
    }

    if (*data || *(data + 1) || *(data + 2) ||
            *(data + 3) != 12 || strncmp(data + 4, "jP  \r\n\x87\n", 8)) {
        opj_free(data);
        close(fd);
        fprintf(FCGI_stdout, "Reason: No JPEG 2000 Signature box in target %s\r\n",
                filepath);
        return -1;
    }

    opj_free(data);

    return fd;
}

#ifdef SERVER
static size_t write_data(void *ptr, size_t size, size_t nmemb, void *stream);
#endif

int open_remotefile(const char filepath[], char tmpfname[])
{
#ifndef SERVER
    (void)filepath;
    (void)tmpfname;
    fprintf(FCGI_stderr, "Remote file can not be opened in local mode\n");
    return -1;

#else

    CURL *curl_handle;
    int fd;

    curl_handle = curl_easy_init();
    curl_easy_setopt(curl_handle, CURLOPT_URL, filepath);
    curl_easy_setopt(curl_handle, CURLOPT_NOPROGRESS, 1L);
    curl_easy_setopt(curl_handle, CURLOPT_WRITEFUNCTION, write_data);

    snprintf(tmpfname, MAX_LENOFTID, "%x-%x.jp2", (unsigned int)time(NULL),
             (unsigned int)rand());
    fprintf(FCGI_stderr, "%s is downloaded to a temporal new file %s\n", filepath,
            tmpfname);
    if ((fd = open(tmpfname, O_RDWR | O_CREAT | O_EXCL, S_IRWXU)) == -1) {
        fprintf(FCGI_stdout, "Reason: File open error %s\r\n", tmpfname);
        curl_easy_cleanup(curl_handle);
        return -1;
    }
    curl_easy_setopt(curl_handle, CURLOPT_WRITEDATA, &fd);
    curl_easy_perform(curl_handle);
    curl_easy_cleanup(curl_handle);

    return fd;
#endif /*SERVER*/
}

#ifdef SERVER
static size_t write_data(void *ptr, size_t size, size_t nmemb, void *stream)
{
    int *fd = (int *)stream;
    ssize_t written = write(*fd, ptr, size * nmemb);
    assert(written >= 0);

    return (size_t)written;
}
#endif /*SERVER*/
