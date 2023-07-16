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
#include "placeholder_manager.h"
#include "opj_inttypes.h"

#ifdef SERVER
#include "fcgi_stdio.h"
#define logstream FCGI_stdout
#else
#define FCGI_stdout stdout
#define FCGI_stderr stderr
#define logstream stderr
#endif /*SERVER*/



placeholderlist_param_t * gene_placeholderlist(void)
{
    placeholderlist_param_t *list;

    list = (placeholderlist_param_t *)malloc(sizeof(placeholderlist_param_t));

    list->first = NULL;
    list->last  = NULL;

    return list;
}

void delete_placeholderlist(placeholderlist_param_t **list)
{
    placeholder_param_t *ptr, *next;

    if (!(*list)) {
        return;
    }

    ptr = (*list)->first;

    while (ptr) {
        next = ptr->next;
        delete_placeholder(&ptr);
        ptr = next;
    }
    free(*list);
}

placeholder_param_t * gene_placeholder(box_param_t *box, Byte8_t origID)
{
    placeholder_param_t *placeholder;

    placeholder = (placeholder_param_t *)malloc(sizeof(placeholder_param_t));

    strncpy(placeholder->TBox, "phld", 4);
    placeholder->Flags =
        1; /* only the access to the original contents of this box, for now */
    placeholder->OrigID = origID;
    placeholder->OrigBH = fetch_headbytes(box);
    placeholder->OrigBHlen = box->headlen;
    placeholder->LBox = 20 + (Byte4_t)box->headlen;
    placeholder->next = NULL;

    return placeholder;
}

void delete_placeholder(placeholder_param_t **placeholder)
{
    if ((*placeholder)->OrigBH) {
        free((*placeholder)->OrigBH);
    }
    free(*placeholder);
}

void insert_placeholder_into_list(placeholder_param_t *phld,
                                  placeholderlist_param_t *phldlist)
{
    if (phldlist->first) {
        phldlist->last->next = phld;
    } else {
        phldlist->first = phld;
    }
    phldlist->last = phld;
}

void print_placeholder(placeholder_param_t *phld)
{
    int i;

    fprintf(logstream, "placeholder info:\n");
    fprintf(logstream, "\t LBox: %d %#x\n", phld->LBox, phld->LBox);
    fprintf(logstream, "\t TBox: %.4s\n", phld->TBox);
    fprintf(logstream, "\t Flags: %#x %#x\n", phld->Flags, phld->Flags);
    fprintf(logstream, "\t OrigID: %" PRId64 "\n", phld->OrigID);
    fprintf(logstream, "\t OrigBH: ");

    for (i = 0; i < phld->OrigBHlen; i++) {
        fprintf(logstream, "%02x ", phld->OrigBH[i]);
    }
    fprintf(logstream, "\t");

    for (i = 0; i < phld->OrigBHlen; i++) {
        fprintf(logstream, "%c", phld->OrigBH[i]);
    }
    fprintf(logstream, "\n");
}

void print_allplaceholder(placeholderlist_param_t *list)
{
    placeholder_param_t *ptr;

    if (!list) {
        return;
    }

    fprintf(logstream, "all placeholder info: \n");
    ptr = list->first;
    while (ptr != NULL) {
        print_placeholder(ptr);
        ptr = ptr->next;
    }
}
