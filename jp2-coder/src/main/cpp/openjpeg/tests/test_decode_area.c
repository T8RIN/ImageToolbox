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

#include <string.h>
#include <stdlib.h>

#include "openjpeg.h"
#include "format_defs.h"


/* -------------------------------------------------------------------------- */
#define JP2_RFC3745_MAGIC "\x00\x00\x00\x0c\x6a\x50\x20\x20\x0d\x0a\x87\x0a"
#define JP2_MAGIC "\x0d\x0a\x87\x0a"
/* position 45: "\xff\x52" */
#define J2K_CODESTREAM_MAGIC "\xff\x4f\xff\x51"

static int infile_format(const char *fname)
{
    FILE *reader;
    unsigned char buf[12];
    unsigned int l_nb_read;

    reader = fopen(fname, "rb");

    if (reader == NULL) {
        return -1;
    }

    memset(buf, 0, 12);
    l_nb_read = (unsigned int)fread(buf, 1, 12, reader);
    fclose(reader);
    if (l_nb_read != 12) {
        return -1;
    }

    if (memcmp(buf, JP2_RFC3745_MAGIC, 12) == 0 || memcmp(buf, JP2_MAGIC, 4) == 0) {
        return JP2_CFMT;
    } else if (memcmp(buf, J2K_CODESTREAM_MAGIC, 4) == 0) {
        return J2K_CFMT;
    } else {
        return -1;
    }
}


/* -------------------------------------------------------------------------- */

/**
  sample error debug callback expecting no client object
 */
static void error_callback(const char *msg, void *client_data)
{
    (void)client_data;
    fprintf(stdout, "[ERROR] %s", msg);
}
/**
  sample warning debug callback expecting no client object
 */
static void warning_callback(const char *msg, void *client_data)
{
    (void)client_data;
    fprintf(stdout, "[WARNING] %s", msg);
}
/**
  sample debug callback expecting no client object
 */
static void info_callback(const char *msg, void *client_data)
{
    (void)client_data;
    (void)msg;
    /*fprintf(stdout, "[INFO] %s", msg);*/
}

static opj_codec_t* create_codec_and_stream(const char* input_file,
        opj_stream_t** pOutStream)
{
    opj_dparameters_t l_param;
    opj_codec_t * l_codec = NULL;
    opj_stream_t * l_stream = NULL;

    l_stream = opj_stream_create_default_file_stream(input_file, OPJ_TRUE);
    if (!l_stream) {
        fprintf(stderr, "ERROR -> failed to create the stream from the file\n");
        return NULL;
    }

    /* Set the default decoding parameters */
    opj_set_default_decoder_parameters(&l_param);

    /* */
    l_param.decod_format = infile_format(input_file);


    switch (l_param.decod_format) {
    case J2K_CFMT: { /* JPEG-2000 codestream */
        /* Get a decoder handle */
        l_codec = opj_create_decompress(OPJ_CODEC_J2K);
        break;
    }
    case JP2_CFMT: { /* JPEG 2000 compressed image data */
        /* Get a decoder handle */
        l_codec = opj_create_decompress(OPJ_CODEC_JP2);
        break;
    }
    default: {
        fprintf(stderr, "ERROR -> Not a valid JPEG2000 file!\n");
        opj_stream_destroy(l_stream);
        return NULL;
    }
    }

    /* catch events using our callbacks and give a local context */
    opj_set_info_handler(l_codec, info_callback, 00);
    opj_set_warning_handler(l_codec, warning_callback, 00);
    opj_set_error_handler(l_codec, error_callback, 00);

    /* Setup the decoder decoding parameters using user parameters */
    if (! opj_setup_decoder(l_codec, &l_param)) {
        fprintf(stderr, "ERROR ->failed to setup the decoder\n");
        opj_stream_destroy(l_stream);
        opj_destroy_codec(l_codec);
        return NULL;
    }

    *pOutStream = l_stream;
    return l_codec;
}


opj_image_t* decode(
    OPJ_BOOL quiet,
    const char* input_file,
    OPJ_INT32 x0,
    OPJ_INT32 y0,
    OPJ_INT32 x1,
    OPJ_INT32 y1,
    OPJ_UINT32* ptilew,
    OPJ_UINT32* ptileh,
    OPJ_UINT32* pcblkw,
    OPJ_UINT32* pcblkh)
{
    opj_codec_t * l_codec = NULL;
    opj_image_t * l_image = NULL;
    opj_stream_t * l_stream = NULL;

    if (!quiet) {
        if (x0 != 0 || x1 != 0 || y0 != 0 || y1 != 0) {
            printf("Decoding %d,%d,%d,%d\n", x0, y0, x1, y1);
        } else {
            printf("Decoding full image\n");
        }
    }

    l_codec = create_codec_and_stream(input_file, &l_stream);
    if (l_codec == NULL) {
        return NULL;
    }

    /* Read the main header of the codestream and if necessary the JP2 boxes*/
    if (! opj_read_header(l_stream, l_codec, &l_image)) {
        fprintf(stderr, "ERROR -> failed to read the header\n");
        opj_stream_destroy(l_stream);
        opj_destroy_codec(l_codec);
        return NULL;
    }

    {
        opj_codestream_info_v2_t* pCodeStreamInfo = opj_get_cstr_info(l_codec);
        if (ptilew) {
            *ptilew = pCodeStreamInfo->tdx;
        }
        if (ptileh) {
            *ptileh = pCodeStreamInfo->tdy;
        }
        //int numResolutions = pCodeStreamInfo->m_default_tile_info.tccp_info[0].numresolutions;
        if (pcblkw) {
            *pcblkw = 1U << pCodeStreamInfo->m_default_tile_info.tccp_info[0].cblkw;
        }
        if (pcblkh) {
            *pcblkh = 1U << pCodeStreamInfo->m_default_tile_info.tccp_info[0].cblkh;
        }
        opj_destroy_cstr_info(&pCodeStreamInfo);
    }

    if (x0 != 0 || x1 != 0 || y0 != 0 || y1 != 0) {
        if (!opj_set_decode_area(l_codec, l_image, x0, y0, x1, y1)) {
            fprintf(stderr, "ERROR -> failed to set the decoded area\n");
            opj_stream_destroy(l_stream);
            opj_destroy_codec(l_codec);
            opj_image_destroy(l_image);
            return NULL;
        }
    }

    /* Get the decoded image */
    if (!(opj_decode(l_codec, l_stream, l_image))) {
        fprintf(stderr, "ERROR -> failed to decode image!\n");
        opj_stream_destroy(l_stream);
        opj_destroy_codec(l_codec);
        opj_image_destroy(l_image);
        return NULL;
    }

    if (! opj_end_decompress(l_codec, l_stream)) {
        opj_stream_destroy(l_stream);
        opj_destroy_codec(l_codec);
        opj_image_destroy(l_image);
        return NULL;
    }


    opj_stream_destroy(l_stream);
    opj_destroy_codec(l_codec);
    return l_image;
}

int decode_by_strip(OPJ_BOOL quiet,
                    const char* input_file,
                    OPJ_UINT32 strip_height,
                    OPJ_INT32 da_x0,
                    OPJ_INT32 da_y0,
                    OPJ_INT32 da_x1,
                    OPJ_INT32 da_y1,
                    opj_image_t* full_image)
{
    /* OPJ_UINT32 tilew, tileh; */
    opj_codec_t * l_codec = NULL;
    opj_image_t * l_image = NULL;
    opj_stream_t * l_stream = NULL;
    OPJ_UINT32 x0, y0, x1, y1, y;
    OPJ_UINT32 full_x0, full_y0, full_x1, full_y1;

    l_codec = create_codec_and_stream(input_file, &l_stream);
    if (l_codec == NULL) {
        return 1;
    }

    /* Read the main header of the codestream and if necessary the JP2 boxes*/
    if (! opj_read_header(l_stream, l_codec, &l_image)) {
        fprintf(stderr, "ERROR -> failed to read the header\n");
        opj_stream_destroy(l_stream);
        opj_destroy_codec(l_codec);
        return 1;
    }

    full_x0 = l_image->x0;
    full_y0 = l_image->y0;
    full_x1 = l_image->x1;
    full_y1 = l_image->y1;

    if (da_x0 != 0 || da_y0 != 0 || da_x1 != 0 || da_y1 != 0) {
        x0 = (OPJ_UINT32)da_x0;
        y0 = (OPJ_UINT32)da_y0;
        x1 = (OPJ_UINT32)da_x1;
        y1 = (OPJ_UINT32)da_y1;
    } else {
        x0 = l_image->x0;
        y0 = l_image->y0;
        x1 = l_image->x1;
        y1 = l_image->y1;
    }
    for (y = y0; y < y1; y += strip_height) {
        OPJ_UINT32 h_req = strip_height;
        if (y + h_req > y1) {
            h_req = y1 - y;
        }
        if (!quiet) {
            printf("Decoding %u...%u\n", y, y + h_req);
        }
        if (!opj_set_decode_area(l_codec, l_image, (OPJ_INT32)x0, (OPJ_INT32)y,
                                 (OPJ_INT32)x1, (OPJ_INT32)(y + h_req))) {
            fprintf(stderr, "ERROR -> failed to set the decoded area\n");
            opj_stream_destroy(l_stream);
            opj_destroy_codec(l_codec);
            opj_image_destroy(l_image);
            return 1;
        }

        /* Get the decoded image */
        if (!(opj_decode(l_codec, l_stream, l_image))) {
            fprintf(stderr, "ERROR -> failed to decode image!\n");
            opj_stream_destroy(l_stream);
            opj_destroy_codec(l_codec);
            opj_image_destroy(l_image);
            return 1;
        }

        if (full_image) {
            OPJ_UINT32 y_check, x;
            OPJ_UINT32 compno;
            for (compno = 0; compno < l_image->numcomps; compno ++) {
                for (y_check = 0; y_check < h_req; y_check++) {
                    for (x = x0; x < x1; x++) {
                        OPJ_INT32 sub_image_val =
                            l_image->comps[compno].data[y_check * (x1 - x0) + (x - x0)];
                        OPJ_INT32 image_val =
                            full_image->comps[compno].data[(y + y_check) * (x1 - x0) + (x - x0)];
                        if (sub_image_val != image_val) {
                            fprintf(stderr,
                                    "Difference found at subimage pixel (%u,%u) "
                                    "of compno=%u: got %d, expected %d\n",
                                    x, y_check + y, compno, sub_image_val, image_val);
                            return 1;
                        }
                    }
                }
            }
        }

    }

    /* If image is small enough, try a final whole image read */
    if (full_x1 - full_x0 < 10000 && full_y1 - full_y0 < 10000) {
        if (!quiet) {
            printf("Decoding full image\n");
        }
        if (!opj_set_decode_area(l_codec, l_image,
                                 (OPJ_INT32)full_x0, (OPJ_INT32)full_y0,
                                 (OPJ_INT32)full_x1, (OPJ_INT32)full_y1)) {
            fprintf(stderr, "ERROR -> failed to set the decoded area\n");
            opj_stream_destroy(l_stream);
            opj_destroy_codec(l_codec);
            opj_image_destroy(l_image);
            return 1;
        }

        /* Get the decoded image */
        if (!(opj_decode(l_codec, l_stream, l_image))) {
            fprintf(stderr, "ERROR -> failed to decode image!\n");
            opj_stream_destroy(l_stream);
            opj_destroy_codec(l_codec);
            opj_image_destroy(l_image);
            return 1;
        }
    }

    if (! opj_end_decompress(l_codec, l_stream)) {
        opj_stream_destroy(l_stream);
        opj_destroy_codec(l_codec);
        opj_image_destroy(l_image);
        return 1;
    }


    opj_stream_destroy(l_stream);
    opj_destroy_codec(l_codec);
    opj_image_destroy(l_image);
    return 0;
}

OPJ_BOOL check_consistency(opj_image_t* p_image, opj_image_t* p_sub_image)
{
    OPJ_UINT32 compno;
    for (compno = 0; compno < p_image->numcomps; compno ++) {
        OPJ_UINT32 y;
        OPJ_UINT32 shift_y = p_sub_image->comps[compno].y0 - p_image->comps[compno].y0;
        OPJ_UINT32 shift_x = p_sub_image->comps[compno].x0 - p_image->comps[compno].x0;
        OPJ_UINT32 image_w = p_image->comps[compno].w;
        OPJ_UINT32 sub_image_w = p_sub_image->comps[compno].w;
        for (y = 0; y < p_sub_image->comps[compno].h; y++) {
            OPJ_UINT32 x;

            for (x = 0; x < sub_image_w; x++) {
                OPJ_INT32 sub_image_val =
                    p_sub_image->comps[compno].data[y * sub_image_w + x];
                OPJ_INT32 image_val =
                    p_image->comps[compno].data[(y + shift_y) * image_w + x + shift_x];
                if (sub_image_val != image_val) {
                    fprintf(stderr,
                            "Difference found at subimage pixel (%u,%u) "
                            "of compno=%u: got %d, expected %d\n",
                            x, y, compno, sub_image_val, image_val);
                    return OPJ_FALSE;
                }
            }
        }
    }
    return OPJ_TRUE;
}

static INLINE OPJ_UINT32 opj_uint_min(OPJ_UINT32  a, OPJ_UINT32  b)
{
    return (a < b) ? a : b;
}

int main(int argc, char** argv)
{
    opj_image_t * l_image = NULL;
    opj_image_t * l_sub_image = NULL;
    OPJ_INT32 da_x0 = 0, da_y0 = 0, da_x1 = 0, da_y1 = 0;
    const char* input_file = NULL;
    OPJ_UINT32 tilew, tileh, cblkw, cblkh;
    OPJ_UINT32 w, h;
    OPJ_UINT32 x, y;
    OPJ_UINT32 step_x, step_y;
    OPJ_BOOL quiet = OPJ_FALSE;
    OPJ_UINT32 nsteps = 100;
    OPJ_UINT32 strip_height = 0;
    OPJ_BOOL strip_check = OPJ_FALSE;

    if (argc < 2) {
        fprintf(stderr,
                "Usage: test_decode_area [-q] [-steps n] input_file_jp2_or_jk2 [x0 y0 x1 y1]\n"
                "or   : test_decode_area [-q] [-strip_height h] [-strip_check] input_file_jp2_or_jk2 [x0 y0 x1 y1]\n");
        return 1;
    }

    {
        int iarg;
        for (iarg = 1; iarg < argc; iarg++) {
            if (strcmp(argv[iarg], "-q") == 0) {
                quiet = OPJ_TRUE;
            } else if (strcmp(argv[iarg], "-steps") == 0 && iarg + 1 < argc) {
                nsteps = (OPJ_UINT32)atoi(argv[iarg + 1]);
                iarg ++;
            } else if (strcmp(argv[iarg], "-strip_height") == 0 && iarg + 1 < argc) {
                strip_height = (OPJ_UINT32)atoi(argv[iarg + 1]);
                iarg ++;
            } else if (strcmp(argv[iarg], "-strip_check") == 0) {
                strip_check = OPJ_TRUE;
            } else if (input_file == NULL) {
                input_file = argv[iarg];
            } else if (iarg + 3 < argc) {
                da_x0 = atoi(argv[iarg]);
                da_y0 = atoi(argv[iarg + 1]);
                da_x1 = atoi(argv[iarg + 2]);
                da_y1 = atoi(argv[iarg + 3]);
                if (da_x0 < 0 || da_y0 < 0 || da_x1 < 0 || da_y1 < 0) {
                    fprintf(stderr, "Wrong bounds\n");
                    return 1;
                }
                iarg += 3;
            }
        }
    }

    if (!strip_height || strip_check) {
        l_image = decode(quiet, input_file, 0, 0, 0, 0,
                         &tilew, &tileh, &cblkw, &cblkh);
        if (!l_image) {
            return 1;
        }
    }

    if (strip_height) {
        int ret = decode_by_strip(quiet, input_file, strip_height, da_x0, da_y0, da_x1,
                                  da_y1, l_image);
        if (l_image) {
            opj_image_destroy(l_image);
        }
        return ret;
    }

    if (da_x0 != 0 || da_x1 != 0 || da_y0 != 0 || da_y1 != 0) {
        l_sub_image = decode(quiet, input_file, da_x0, da_y0, da_x1, da_y1,
                             NULL, NULL, NULL, NULL);
        if (!l_sub_image) {
            fprintf(stderr, "decode failed for %d,%d,%d,%d\n",
                    da_x0, da_y0, da_x1, da_y1);
            opj_image_destroy(l_sub_image);
            opj_image_destroy(l_image);
            return 1;
        }

        if (!check_consistency(l_image, l_sub_image)) {
            fprintf(stderr, "Consistency checked failed for %d,%d,%d,%d\n",
                    da_x0, da_y0, da_x1, da_y1);
            opj_image_destroy(l_sub_image);
            opj_image_destroy(l_image);
            return 1;
        }
        opj_image_destroy(l_sub_image);
        opj_image_destroy(l_image);
        return 0;
    }

    w = l_image->x1 - l_image->x0;
    h = l_image->y1 - l_image->y0;
    step_x = w > nsteps ? w / nsteps : 1;
    step_y = h > nsteps ? h / nsteps : 1;
    for (y = 0; y < h; y += step_y) {
        for (x = 0; x < w; x += step_x) {
            da_x0 = (OPJ_INT32)(l_image->x0 + x);
            da_y0 = (OPJ_INT32)(l_image->y0 + y);
            da_x1 = (OPJ_INT32)opj_uint_min(l_image->x1, l_image->x0 + x + 1);
            da_y1 = (OPJ_INT32)opj_uint_min(l_image->y1, l_image->y0 + y + 1);
            l_sub_image = decode(quiet, input_file, da_x0, da_y0, da_x1, da_y1,
                                 NULL, NULL, NULL, NULL);
            if (!l_sub_image) {
                fprintf(stderr, "decode failed for %d,%d,%d,%d\n",
                        da_x0, da_y0, da_x1, da_y1);
                opj_image_destroy(l_sub_image);
                opj_image_destroy(l_image);
                return 1;
            }

            if (!check_consistency(l_image, l_sub_image)) {
                fprintf(stderr, "Consistency checked failed for %d,%d,%d,%d\n",
                        da_x0, da_y0, da_x1, da_y1);
                opj_image_destroy(l_sub_image);
                opj_image_destroy(l_image);
                return 1;
            }
            opj_image_destroy(l_sub_image);

            if (step_x > 1 || step_y > 1) {
                if (step_x > 1) {
                    da_x0 = (OPJ_INT32)opj_uint_min(l_image->x1, (OPJ_UINT32)da_x0 + 1);
                    da_x1 = (OPJ_INT32)opj_uint_min(l_image->x1, (OPJ_UINT32)da_x1 + 1);
                }
                if (step_y > 1) {
                    da_y0 = (OPJ_INT32)opj_uint_min(l_image->y1, (OPJ_UINT32)da_y0 + 1);
                    da_y1 = (OPJ_INT32)opj_uint_min(l_image->y1, (OPJ_UINT32)da_y1 + 1);
                }
                if (da_x0 < (OPJ_INT32)l_image->x1 && da_y0 < (OPJ_INT32)l_image->y1) {
                    l_sub_image = decode(quiet, input_file, da_x0, da_y0, da_x1, da_y1,
                                         NULL, NULL, NULL, NULL);
                    if (!l_sub_image) {
                        fprintf(stderr, "decode failed for %d,%d,%d,%d\n",
                                da_x0, da_y0, da_x1, da_y1);
                        opj_image_destroy(l_sub_image);
                        opj_image_destroy(l_image);
                        return 1;
                    }

                    if (!check_consistency(l_image, l_sub_image)) {
                        fprintf(stderr, "Consistency checked failed for %d,%d,%d,%d\n",
                                da_x0, da_y0, da_x1, da_y1);
                        opj_image_destroy(l_sub_image);
                        opj_image_destroy(l_image);
                        return 1;
                    }
                    opj_image_destroy(l_sub_image);
                }
            }
        }
    }

    opj_image_destroy(l_image);
    return 0;
}
