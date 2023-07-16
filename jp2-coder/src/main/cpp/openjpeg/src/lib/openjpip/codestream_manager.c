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
#include "codestream_manager.h"
#include "opj_inttypes.h"

#ifdef SERVER
#include "fcgi_stdio.h"
#define logstream FCGI_stdout
#else
#define FCGI_stdout stdout
#define FCGI_stderr stderr
#define logstream stderr
#endif /*SERVER */

codestream_param_t set_codestream(int fd, OPJ_OFF_T offset, OPJ_SIZE_T length)
{
    codestream_param_t cs;

    cs.fd = fd;
    cs.offset = offset;
    cs.length = length;

    return cs;
}

Byte_t * fetch_codestreambytes(codestream_param_t *cs, OPJ_OFF_T offset,
                               OPJ_SIZE_T size)
{
    return fetch_bytes(cs->fd, cs->offset + offset, size);
}

Byte_t fetch_codestream1byte(codestream_param_t *cs, OPJ_OFF_T offset)
{
    return fetch_1byte(cs->fd, cs->offset + offset);
}

Byte2_t fetch_codestream2bytebigendian(codestream_param_t *cs, OPJ_OFF_T offset)
{
    return fetch_2bytebigendian(cs->fd, cs->offset + offset);
}

Byte4_t fetch_codestream4bytebigendian(codestream_param_t *cs, OPJ_OFF_T offset)
{
    return fetch_4bytebigendian(cs->fd, cs->offset + offset);
}

void print_codestream(codestream_param_t cs)
{
    fprintf(logstream, "codestream info:\n"
            "\t fd: %d\n"
            "\t offset: %#" PRIx64 "\n"
            "\t length: %#" PRIx64 "\n", cs.fd, cs.offset, cs.length);
}
