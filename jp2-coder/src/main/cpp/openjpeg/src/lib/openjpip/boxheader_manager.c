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
#include <stdlib.h>
#include "boxheader_manager.h"
#include "opj_inttypes.h"

#ifdef SERVER
#include "fcgi_stdio.h"
#define logstream FCGI_stdout
#else
#define FCGI_stdout stdout
#define FCGI_stderr stderr
#define logstream stderr
#endif /*SERVER*/


boxheader_param_t *gene_boxheader(int fd, OPJ_OFF_T offset) {
    Byte8_t boxlen;
    Byte_t headlen;
    char *boxtype;
    boxheader_param_t *boxheader;

    boxlen = fetch_4bytebigendian(fd, offset);
    boxtype = (char *) fetch_bytes(fd, offset + 4, 4);
    headlen = 8;

    if (boxlen == 1) { /* read XLBox */
        boxlen = fetch_8bytebigendian(fd, offset + 8);
        headlen = 16;
    }

    boxheader = (boxheader_param_t *) malloc(sizeof(boxheader_param_t));
    boxheader->headlen = headlen;
    boxheader->length = boxlen;
    strncpy(boxheader->type, boxtype, 4);
    boxheader->next = NULL;

    free(boxtype);
    return boxheader;
}

boxheader_param_t *gene_childboxheader(box_param_t *superbox, OPJ_OFF_T offset) {
    return gene_boxheader(superbox->fd, get_DBoxoff(superbox) + offset);
}

void print_boxheader(boxheader_param_t *boxheader) {
    fprintf(logstream, "boxheader info:\n"
                       "\t type: %.4s\n"
                       "\t length:%"
    PRId64
    " %#"
    PRIx64
    "\n", boxheader->type, boxheader->length,
            boxheader->length);
}
