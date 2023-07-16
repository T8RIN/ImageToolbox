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

#include "marker_manager.h"

#ifdef SERVER
#include "fcgi_stdio.h"
#define logstream FCGI_stdout
#else
#define FCGI_stdout stdout
#define FCGI_stderr stderr
#define logstream stderr
#endif /*SERVER */

marker_param_t set_marker(codestream_param_t cs, Byte2_t code, OPJ_OFF_T offset,
                          Byte2_t length)
{
    marker_param_t mkr;

    mkr.cs = cs;
    mkr.code = code;
    mkr.offset = offset;
    mkr.length = length;

    return mkr;
}


Byte_t fetch_marker1byte(marker_param_t marker, OPJ_OFF_T offset)
{
    return fetch_codestream1byte(&(marker.cs), marker.offset + offset);
}

Byte2_t fetch_marker2bytebigendian(marker_param_t marker, OPJ_OFF_T offset)
{
    return fetch_codestream2bytebigendian(&(marker.cs), marker.offset + offset);
}

Byte4_t fetch_marker4bytebigendian(marker_param_t marker, OPJ_OFF_T offset)
{
    return fetch_codestream4bytebigendian(&(marker.cs), marker.offset + offset);
}
