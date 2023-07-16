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
#include <time.h>
#include "jpipstream_manager.h"
#include "jp2k_encoder.h"
#include "jp2k_decoder.h"
#include "ihdrbox_manager.h"
#include "j2kheader_manager.h"

Byte_t * update_JPIPstream(Byte_t *newstream, OPJ_SIZE_T newstreamlen,
                           Byte_t *cache_stream, OPJ_SIZE_T *streamlen)
{
    Byte_t *stream = (Byte_t *)opj_malloc((*streamlen) + newstreamlen);
    if (*streamlen > 0) {
        memcpy(stream, cache_stream, *streamlen);
    }
    memcpy(stream + (*streamlen), newstream, newstreamlen);
    *streamlen += newstreamlen;

    if (cache_stream) {
        opj_free(cache_stream);
    }

    return stream;
}

void save_codestream(Byte_t *codestream, OPJ_SIZE_T streamlen, const char *fmt)
{
    time_t timer;
    struct tm *t_st;
    char filename[20];
    FILE *fp;

    time(&timer);
    t_st = localtime(&timer);

    sprintf(filename, "%4d%02d%02d%02d%02d%02d.%.3s", t_st->tm_year + 1900,
            t_st->tm_mon + 1, t_st->tm_mday, t_st->tm_hour, t_st->tm_min, t_st->tm_sec,
            fmt);

    fp = fopen(filename, "wb");
    if (fwrite(codestream, streamlen, 1, fp) != 1) {
        fprintf(stderr, "Error: failed to write codestream to file %s\n", filename);
    }
    fclose(fp);
}


Byte_t * jpipstream_to_pnm(Byte_t *jpipstream, msgqueue_param_t *msgqueue,
                           Byte8_t csn, int fw, int fh, ihdrbox_param_t **ihdrbox)
{
    Byte_t *pnmstream;
    Byte_t *j2kstream; /* j2k or jp2 codestream */
    Byte8_t j2klen;
    size_t retlen;
    FILE *fp;
    const char j2kfname[] = "tmp.j2k";

    fp = fopen(j2kfname, "w+b");
    if (!fp) {
        return NULL;
    }
    j2kstream = recons_j2k(msgqueue, jpipstream, csn, fw, fh, &j2klen);
    if (!j2kstream) {
        fclose(fp);
        remove(j2kfname);
        return NULL;
    }

    retlen = fwrite(j2kstream, 1, j2klen, fp);
    opj_free(j2kstream);
    fclose(fp);
    if (retlen != j2klen) {
        remove(j2kfname);
        return NULL;
    }

    pnmstream = j2k_to_pnm(j2kfname, ihdrbox);

    remove(j2kfname);

    return pnmstream;
}

ihdrbox_param_t * get_SIZ_from_jpipstream(Byte_t *jpipstream,
        msgqueue_param_t *msgqueue, Byte8_t csn)
{
    ihdrbox_param_t *ihdrbox;
    Byte_t *j2kstream;
    Byte8_t j2klen;
    SIZmarker_param_t SIZ;

    j2kstream = recons_j2kmainhead(msgqueue, jpipstream, csn, &j2klen);
    if (!get_mainheader_from_j2kstream(j2kstream, &SIZ, NULL)) {
        opj_free(j2kstream);
        return NULL;
    }

    ihdrbox = (ihdrbox_param_t *)opj_malloc(sizeof(ihdrbox_param_t));

    ihdrbox->width = SIZ.Xsiz;
    ihdrbox->height = SIZ.Ysiz;
    ihdrbox->nc = SIZ.Csiz;
    ihdrbox->bpc = SIZ.Ssiz[0];

    opj_free(j2kstream);

    return ihdrbox;
}
