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
#include "session_manager.h"
#include "target_manager.h"

#ifdef SERVER
#include "fcgi_stdio.h"
#define logstream FCGI_stdout
#else
#define FCGI_stdout stdout
#define FCGI_stderr stderr
#define logstream stderr
#endif /*SERVER */


sessionlist_param_t * gene_sessionlist(void)
{
    sessionlist_param_t *sessionlist;

    sessionlist = (sessionlist_param_t *)opj_malloc(sizeof(sessionlist_param_t));

    sessionlist->first = NULL;
    sessionlist->last  = NULL;

    return sessionlist;
}

session_param_t * gene_session(sessionlist_param_t *sessionlist)
{
    session_param_t *session;

    session = (session_param_t *)opj_malloc(sizeof(session_param_t));

    session->channellist = gene_channellist();
    session->cachemodellist = gene_cachemodellist();

    session->next = NULL;

    if (sessionlist->first) { /* there are one or more entries */
        sessionlist->last->next = session;
    } else {               /* first entry */
        sessionlist->first = session;
    }
    sessionlist->last = session;

    return session;
}

OPJ_BOOL search_session_and_channel(char cid[],
                                    sessionlist_param_t *sessionlist,
                                    session_param_t **foundsession,
                                    channel_param_t **foundchannel)
{
    *foundsession = sessionlist->first;

    while (*foundsession != NULL) {

        *foundchannel = (*foundsession)->channellist->first;

        while (*foundchannel != NULL) {

            if (strcmp(cid, (*foundchannel)->cid) == 0) {
                return OPJ_TRUE;
            }

            *foundchannel = (*foundchannel)->next;
        }
        *foundsession = (*foundsession)->next;
    }

    fprintf(FCGI_stdout, "Status: 503\r\n");
    fprintf(FCGI_stdout, "Reason: Channel %s not found\r\n", cid);

    return OPJ_FALSE;
}

void insert_cachemodel_into_session(session_param_t *session,
                                    cachemodel_param_t *cachemodel)
{
    if (!cachemodel) {
        return;
    }

#ifndef SERVER
    fprintf(logstream, "local log: insert cachemodel into session\n");
#endif
    if (session->cachemodellist->first != NULL) {
        session->cachemodellist->last->next = cachemodel;
    } else {
        session->cachemodellist->first = cachemodel;
    }
    session->cachemodellist->last = cachemodel;
}

OPJ_BOOL delete_session(session_param_t **session,
                        sessionlist_param_t *sessionlist)
{
    session_param_t *ptr;

    if (*session == NULL) {
        return OPJ_FALSE;
    }


    if (*session == sessionlist->first) {
        sessionlist->first = (*session)->next;
    } else {
        ptr = sessionlist->first;
        while (ptr->next != *session) {
            ptr = ptr->next;
        }
        ptr->next = (*session)->next;

        if (*session == sessionlist->last) {
            sessionlist->last = ptr;
        }
    }

    delete_channellist(&((*session)->channellist));
    delete_cachemodellist(&((*session)->cachemodellist));

#ifndef SERVER
    fprintf(logstream, "local log: session: %p deleted!\n", (void *)(*session));
#endif
    opj_free(*session);

    return OPJ_TRUE;
}

void delete_sessionlist(sessionlist_param_t **sessionlist)
{
    session_param_t *sessionPtr, *sessionNext;

    sessionPtr = (*sessionlist)->first;
    while (sessionPtr != NULL) {
        sessionNext = sessionPtr->next;

        delete_channellist(&(sessionPtr->channellist));
        delete_cachemodellist(&(sessionPtr->cachemodellist));

#ifndef SERVER
        fprintf(logstream, "local log: session: %p deleted!\n", (void *)sessionPtr);
#endif
        opj_free(sessionPtr);

        sessionPtr = sessionNext;
    }

    (*sessionlist)->first = NULL;
    (*sessionlist)->last  = NULL;

    opj_free(*sessionlist);
}

void print_allsession(sessionlist_param_t *sessionlist)
{
    session_param_t *ptr;
    cachemodel_param_t *cachemodel;
    int i = 0;

    fprintf(logstream, "SESSIONS info:\n");

    ptr = sessionlist->first;
    while (ptr != NULL) {
        fprintf(logstream, "session No.%d\n", i++);
        print_allchannel(ptr->channellist);
        cachemodel = ptr->cachemodellist->first;
        while (cachemodel) {
            print_target(cachemodel->target);
            cachemodel = cachemodel->next;
        }
        ptr = ptr->next;
    }
}
