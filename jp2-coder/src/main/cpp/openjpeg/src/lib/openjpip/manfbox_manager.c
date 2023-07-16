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
#include <stdio.h>
#include <string.h>
#include "manfbox_manager.h"

#ifdef SERVER
#include "fcgi_stdio.h"
#define logstream FCGI_stdout
#else
#define FCGI_stdout stdout
#define FCGI_stderr stderr
#define logstream stderr
#endif /*SERVER */

manfbox_param_t * gene_manfbox(box_param_t *box)
{
    manfbox_param_t *manf;   /* manifest parameters */
    boxheader_param_t *bh;   /* current box pointer */
    boxheader_param_t *last; /* last boxheader pointer of the list */
    OPJ_OFF_T pos;                 /* current position in manf_box contents; */

    manf = (manfbox_param_t *)malloc(sizeof(manfbox_param_t));

    pos = 0;
    manf->first = last = NULL;

    while ((OPJ_SIZE_T)pos < get_DBoxlen(box)) {

        bh = gene_childboxheader(box, pos);
        pos += bh->headlen;

        /* insert into the list */
        if (manf->first) {
            last->next = bh;
        } else {
            manf->first = bh;
        }
        last = bh;
    }
    return manf;
}

void delete_manfbox(manfbox_param_t **manf)
{
    boxheader_param_t *bhPtr, *bhNext;

    bhPtr = (*manf)->first;
    while (bhPtr != NULL) {
        bhNext = bhPtr->next;
#ifndef SERVER
        /*      fprintf( logstream, "local log: boxheader %.4s deleted!\n", bhPtr->type); */
#endif
        free(bhPtr);
        bhPtr = bhNext;
    }
    free(*manf);
}

void print_manfbox(manfbox_param_t *manf)
{
    boxheader_param_t *ptr;

    ptr = manf->first;
    while (ptr != NULL) {
        print_boxheader(ptr);
        ptr = ptr->next;
    }
}

boxheader_param_t * search_boxheader(const char type[], manfbox_param_t *manf)
{
    boxheader_param_t *found;

    found = manf->first;

    while (found != NULL) {

        if (strncmp(type, found->type, 4) == 0) {
            return found;
        }

        found = found->next;
    }
    fprintf(FCGI_stderr, "Error: Boxheader %s not found\n", type);

    return NULL;
}
