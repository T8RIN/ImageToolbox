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

#ifdef _WIN32
#include <io.h>
#else

#include <sys/types.h>
#include <unistd.h>

#endif

#include <stdlib.h>
#include <sys/stat.h>
#include "byte_manager.h"

#ifdef SERVER
#include "fcgi_stdio.h"
#define logstream FCGI_stdout
#else
#define FCGI_stdout stdout
#define FCGI_stderr stderr
#define logstream stderr
#endif /*SERVER*/


Byte_t *fetch_bytes(int fd, OPJ_OFF_T offset, OPJ_SIZE_T size) {
    Byte_t *data;

    if (lseek(fd, offset, SEEK_SET) == -1) {
        fprintf(FCGI_stdout, "Reason: Target broken (fseek error)\r\n");
        fprintf(FCGI_stderr, "Error: error in fetch_bytes( %d, %ld, %lu)\n", fd, offset,
                size);
        return NULL;
    }

    data = (Byte_t *) malloc(size);

    if ((OPJ_SIZE_T) read(fd, data, size) != size) {
        free(data);
        fprintf(FCGI_stdout, "Reason: Target broken (read error)\r\n");
        fprintf(FCGI_stderr, "Error: error in fetch_bytes( %d, %ld, %lu)\n", fd, offset,
                size);
        return NULL;
    }
    return data;
}

Byte_t fetch_1byte(int fd, OPJ_OFF_T offset) {
    Byte_t code;

    if (lseek(fd, offset, SEEK_SET) == -1) {
        fprintf(FCGI_stdout, "Reason: Target broken (seek error)\r\n");
        fprintf(FCGI_stderr, "Error: error in fetch_1byte( %d, %ld)\n", fd, offset);
        return 0;
    }

    if (read(fd, &code, 1) != 1) {
        fprintf(FCGI_stdout, "Reason: Target broken (read error)\r\n");
        fprintf(FCGI_stderr, "Error: error in fetch_bytes( %d, %ld)\n", fd, offset);
        return 0;
    }
    return code;
}

Byte2_t fetch_2bytebigendian(int fd, OPJ_OFF_T offset) {
    Byte_t *data;
    Byte2_t code;

    if (!(data = fetch_bytes(fd, offset, 2))) {
        fprintf(FCGI_stderr, "Error: error in fetch_2bytebigendian( %d, %ld)\n", fd,
                offset);
        return 0;
    }
    code = big2(data);
    free(data);

    return code;
}

Byte4_t fetch_4bytebigendian(int fd, OPJ_OFF_T offset) {
    Byte_t *data;
    Byte4_t code;

    if (!(data = fetch_bytes(fd, offset, 4))) {
        fprintf(FCGI_stderr, "Error: error in fetch_4bytebigendian( %d, %ld)\n", fd,
                offset);
        return 0;
    }
    code = big4(data);
    free(data);

    return code;
}

Byte8_t fetch_8bytebigendian(int fd, OPJ_OFF_T offset) {
    Byte_t *data;
    Byte8_t code;

    if (!(data = fetch_bytes(fd, offset, 8))) {
        fprintf(FCGI_stderr, "Error: error in fetch_8bytebigendian( %d, %ld)\n", fd,
                offset);
        return 0;
    }
    code = big8(data);
    free(data);

    return code;
}


Byte2_t big2(Byte_t *buf) {
    return (Byte2_t) ((((Byte2_t) buf[0]) << 8) + ((Byte2_t) buf[1]));
}

Byte4_t big4(Byte_t *buf) {
    return (((((((Byte4_t) buf[0]) << 8) + ((Byte4_t) buf[1])) << 8)
             + ((Byte4_t) buf[2])) << 8) + ((Byte4_t) buf[3]);
}

Byte8_t big8(Byte_t *buf) {
    return (((Byte8_t) big4(buf)) << 32)
           + ((Byte8_t) big4(buf + 4));
}

void modify_4Bytecode(Byte4_t code, Byte_t *stream) {
    *stream = (Byte_t) ((Byte4_t) (code & 0xff000000) >> 24);
    *(stream + 1) = (Byte_t) ((Byte4_t) (code & 0x00ff0000) >> 16);
    *(stream + 2) = (Byte_t) ((Byte4_t) (code & 0x0000ff00) >> 8);
    *(stream + 3) = (Byte_t) (code & 0x000000ff);
}

OPJ_OFF_T get_filesize(int fd) {
    struct stat sb;

    if (fstat(fd, &sb) == -1) {
        fprintf(FCGI_stdout, "Reason: Target broken (fstat error)\r\n");
        fprintf(FCGI_stderr, "Error: error in get_filesize( %d)\n", fd);
        return 0;
    }
    return sb.st_size;
}
