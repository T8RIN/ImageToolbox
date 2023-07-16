/*
 * The copyright in this software is being made available under the 2-clauses
 * BSD License, included below. This software may be subject to other third
 * party and contributor rights, including patent rights, and no such rights
 * are granted under this license.
 *
 * Copyright (c) 2017, IntoPix SA <contact@intopix.com>
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

#include <stddef.h>
#include <stdint.h>
#include <string.h>
#include <limits.h>

#include "openjpeg.h"

extern "C" int LLVMFuzzerInitialize(int* argc, char*** argv);
extern "C" int LLVMFuzzerTestOneInput(const uint8_t *buf, size_t len);

typedef struct {
    const uint8_t* pabyData;
    size_t         nCurPos;
    size_t         nLength;
} MemFile;


static void ErrorCallback(const char * msg, void *)
{
    (void)msg;
    //fprintf(stderr, "%s\n", msg);
}


static void WarningCallback(const char *, void *)
{
}

static void InfoCallback(const char *, void *)
{
}

static OPJ_SIZE_T ReadCallback(void* pBuffer, OPJ_SIZE_T nBytes,
                               void *pUserData)
{
    MemFile* memFile = (MemFile*)pUserData;
    //printf("want to read %d bytes at %d\n", (int)memFile->nCurPos, (int)nBytes);
    if (memFile->nCurPos >= memFile->nLength) {
        return -1;
    }
    if (memFile->nCurPos + nBytes >= memFile->nLength) {
        size_t nToRead = memFile->nLength - memFile->nCurPos;
        memcpy(pBuffer, memFile->pabyData + memFile->nCurPos, nToRead);
        memFile->nCurPos = memFile->nLength;
        return nToRead;
    }
    if (nBytes == 0) {
        return -1;
    }
    memcpy(pBuffer, memFile->pabyData + memFile->nCurPos, nBytes);
    memFile->nCurPos += nBytes;
    return nBytes;
}

static OPJ_BOOL SeekCallback(OPJ_OFF_T nBytes, void * pUserData)
{
    MemFile* memFile = (MemFile*)pUserData;
    //printf("seek to %d\n", (int)nBytes);
    memFile->nCurPos = nBytes;
    return OPJ_TRUE;
}

static OPJ_OFF_T SkipCallback(OPJ_OFF_T nBytes, void * pUserData)
{
    MemFile* memFile = (MemFile*)pUserData;
    memFile->nCurPos += nBytes;
    return nBytes;
}


int LLVMFuzzerInitialize(int* /*argc*/, char*** argv)
{
    return 0;
}

static const unsigned char jpc_header[] = {0xff, 0x4f};
static const unsigned char jp2_box_jp[] = {0x6a, 0x50, 0x20, 0x20}; /* 'jP  ' */

int LLVMFuzzerTestOneInput(const uint8_t *buf, size_t len)
{

    OPJ_CODEC_FORMAT eCodecFormat;
    if (len >= sizeof(jpc_header) &&
            memcmp(buf, jpc_header, sizeof(jpc_header)) == 0) {
        eCodecFormat = OPJ_CODEC_J2K;
    } else if (len >= 4 + sizeof(jp2_box_jp) &&
               memcmp(buf + 4, jp2_box_jp, sizeof(jp2_box_jp)) == 0) {
        eCodecFormat = OPJ_CODEC_JP2;
    } else {
        return 0;
    }

    opj_codec_t* pCodec = opj_create_decompress(eCodecFormat);
    opj_set_info_handler(pCodec, InfoCallback, NULL);
    opj_set_warning_handler(pCodec, WarningCallback, NULL);
    opj_set_error_handler(pCodec, ErrorCallback, NULL);

    opj_dparameters_t parameters;
    opj_set_default_decoder_parameters(&parameters);

    opj_setup_decoder(pCodec, &parameters);

    opj_stream_t *pStream = opj_stream_create(1024, OPJ_TRUE);
    MemFile memFile;
    memFile.pabyData = buf;
    memFile.nLength = len;
    memFile.nCurPos = 0;
    opj_stream_set_user_data_length(pStream, len);
    opj_stream_set_read_function(pStream, ReadCallback);
    opj_stream_set_seek_function(pStream, SeekCallback);
    opj_stream_set_skip_function(pStream, SkipCallback);
    opj_stream_set_user_data(pStream, &memFile, NULL);

    opj_image_t * psImage = NULL;
    if (!opj_read_header(pStream, pCodec, &psImage)) {
        opj_destroy_codec(pCodec);
        opj_stream_destroy(pStream);
        opj_image_destroy(psImage);
        return 0;
    }

    OPJ_UINT32 width = psImage->x1 - psImage->x0;
    OPJ_UINT32 height = psImage->y1 - psImage->y0;

#if 0
    // Reject too big images since that will require allocating a lot of
    // memory
    if (width != 0 && psImage->numcomps != 0 &&
            (width > INT_MAX / psImage->numcomps ||
             height > INT_MAX / (width * psImage->numcomps * sizeof(OPJ_UINT32)))) {
        opj_stream_destroy(pStream);
        opj_destroy_codec(pCodec);
        opj_image_destroy(psImage);

        return 0;
    }

    // Also reject too big tiles.
    // TODO: remove this limitation when subtile decoding no longer imply
    // allocation memory for whole tile
    opj_codestream_info_v2_t* pCodeStreamInfo = opj_get_cstr_info(pCodec);
    OPJ_UINT32 nTileW, nTileH;
    nTileW = pCodeStreamInfo->tdx;
    nTileH = pCodeStreamInfo->tdy;
    opj_destroy_cstr_info(&pCodeStreamInfo);
    if (nTileW > 2048 || nTileH > 2048) {
        opj_stream_destroy(pStream);
        opj_destroy_codec(pCodec);
        opj_image_destroy(psImage);

        return 0;
    }
#endif

    OPJ_UINT32 width_to_read = width;
    if (width_to_read > 1024) {
        width_to_read = 1024;
    }
    OPJ_UINT32 height_to_read = height;
    if (height_to_read > 1024) {
        height_to_read = 1024;
    }

    if (opj_set_decode_area(pCodec, psImage,
                            psImage->x0, psImage->y0,
                            psImage->x0 + width_to_read,
                            psImage->y0 + height_to_read)) {
        if (opj_decode(pCodec, pStream, psImage)) {
            //printf("success\n");
        }
    }

    opj_end_decompress(pCodec, pStream);
    opj_stream_destroy(pStream);
    opj_destroy_codec(pCodec);
    opj_image_destroy(psImage);

    return 0;
}
