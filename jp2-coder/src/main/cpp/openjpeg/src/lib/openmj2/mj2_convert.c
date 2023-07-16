/*
* Copyright (c) 2002-2014, Universite catholique de Louvain (UCL), Belgium
* Copyright (c) 2002-2014, Professor Benoit Macq
* Copyright (c) 2003-2007, Francois-Olivier Devaux and Antonin Descampe
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

#include "opj_includes.h"
#include "mj2_convert.h"

/*  -----------------------       */
/*                    */
/*                    */
/*  Count the number of frames        */
/*  in a YUV file             */
/*                    */
/*  -----------------------       */

unsigned int OPJ_CALLCONV yuv_num_frames(mj2_tk_t * tk, char *infile)
{
    unsigned int prec_size;
    long end_of_f, frame_size;
    FILE *f;

    f = fopen(infile, "rb");
    if (!f) {
        fprintf(stderr, "failed to open %s for reading\n", infile);
        return 0;
    }
    prec_size = (tk->depth + 7) / 8; /* bytes of precision */

    frame_size = (long)(tk->w * tk->h * (1.0 + (double) 2 / (double)(
            tk->CbCr_subsampling_dx *
            tk->CbCr_subsampling_dy)));  /* Calculate frame size */
    frame_size *= prec_size;

    fseek(f, 0, SEEK_END);
    end_of_f = ftell(f);      /* Calculate file size */

    if (end_of_f < frame_size) {
        fprintf(stderr,
                "YUV does not contains any frame of %d x %d size\n", tk->w,
                tk->h);
        fclose(f);
        return 0;
    }
    fclose(f);

    return (unsigned int)(end_of_f / frame_size);
}

/*  ----------------------- */
/* */
/* */
/*  YUV to IMAGE */
/* */
/*  ----------------------- */

opj_image_t * OPJ_CALLCONV mj2_image_create(mj2_tk_t * tk,
        opj_cparameters_t *parameters)
{
    opj_image_cmptparm_t cmptparm[3];
    opj_image_t * img;
    int i;
    int numcomps = 3;
    int subsampling_dx = parameters->subsampling_dx;
    int subsampling_dy = parameters->subsampling_dy;

    /* initialize image components */
    memset(&cmptparm[0], 0, 3 * sizeof(opj_image_cmptparm_t));
    for (i = 0; i < numcomps; i++) {
        cmptparm[i].prec = tk->depth;
        cmptparm[i].bpp = tk->depth;
        cmptparm[i].sgnd = 0;
        cmptparm[i].dx = i ? subsampling_dx * tk->CbCr_subsampling_dx : subsampling_dx;
        cmptparm[i].dy = i ? subsampling_dy * tk->CbCr_subsampling_dy : subsampling_dy;
        cmptparm[i].w = tk->w;
        cmptparm[i].h = tk->h;
    }
    /* create the image */
    img = opj_image_create(numcomps, cmptparm, CLRSPC_SRGB);
    return img;
}

char OPJ_CALLCONV yuvtoimage(mj2_tk_t * tk, opj_image_t * img, int frame_num,
                             opj_cparameters_t *parameters, char* infile)
{
    int i, compno;
    int offset, size, max, prec_bytes, is_16, v;
    long end_of_f, position;
    int numcomps = 3;
    int subsampling_dx = parameters->subsampling_dx;
    int subsampling_dy = parameters->subsampling_dy;
    FILE *yuvfile;
    int *data;
    unsigned char uc;

    yuvfile = fopen(infile, "rb");
    if (!yuvfile) {
        fprintf(stderr, "failed to open %s for readings\n", parameters->infile);
        return 1;
    }
    is_16 = (tk->depth > 8);
    prec_bytes = (is_16 ? 2 : 1);

    offset = (int)((double)(frame_num * tk->w * tk->h) * (1.0 +
                   1.0 * (double) 2 / (double)(tk->CbCr_subsampling_dx *
                           tk->CbCr_subsampling_dy)));
    offset *= prec_bytes;

    fseek(yuvfile, 0, SEEK_END);
    end_of_f = ftell(yuvfile);
    fseek(yuvfile, sizeof(unsigned char) * offset, SEEK_SET);
    position = ftell(yuvfile);
    if (position >= end_of_f) {
        fprintf(stderr, "Cannot reach frame number %d in yuv file !!\n",
                frame_num);
        fclose(yuvfile);
        return 1;
    }

    img->x0 = tk->Dim[0];
    img->y0 = tk->Dim[1];
    img->x1 = !tk->Dim[0] ? (tk->w - 1) * subsampling_dx + 1 : tk->Dim[0] +
              (tk->w - 1) * subsampling_dx + 1;
    img->y1 = !tk->Dim[1] ? (tk->h - 1) * subsampling_dy + 1 : tk->Dim[1] +
              (tk->h - 1) * subsampling_dy + 1;

    size = tk->w * tk->h * prec_bytes;

    for (compno = 0; compno < numcomps; compno++) {
        max = size / (img->comps[compno].dx * img->comps[compno].dy);
        data = img->comps[compno].data;

        for (i = 0; i < max && !feof(yuvfile); i++) {
            v = 0;
            fread(&uc, 1, 1, yuvfile);
            v = uc;

            if (is_16) {
                fread(&uc, 1, 1, yuvfile);
                v |= (uc << 8);
            }
            *data++ = v;
        }
    }
    fclose(yuvfile);

    return 0;
}



/*  ----------------------- */
/* */
/* */
/*  IMAGE to YUV */
/* */
/*  ----------------------- */


opj_bool OPJ_CALLCONV imagetoyuv(opj_image_t * img, char *outfile)
{
    FILE *f;
    int *data;
    int i, v, is_16, prec_bytes;
    unsigned char buf[2];

    if (img->numcomps == 3) {
        if (img->comps[0].dx != img->comps[1].dx / 2
                || img->comps[1].dx != img->comps[2].dx) {
            fprintf(stderr,
                    "Error with the input image components size: cannot create yuv file)\n");
            return OPJ_FALSE;
        }
    } else if (!(img->numcomps == 1)) {
        fprintf(stderr,
                "Error with the number of image components(must be one or three)\n");
        return OPJ_FALSE;
    }

    f = fopen(outfile, "a+b");
    if (!f) {
        fprintf(stderr, "failed to open %s for writing\n", outfile);
        return OPJ_FALSE;
    }
    is_16 = (img->comps[0].prec > 8);
    prec_bytes = (is_16 ? 2 : 1);
    data = img->comps[0].data;

    for (i = 0; i < (img->comps[0].w * img->comps[0].h); i++) {
        v = *data++;
        buf[0] = (unsigned char)v;

        if (is_16) {
            buf[1] = (unsigned char)(v >> 8);
        }

        fwrite(buf, 1, prec_bytes, f);
    }


    if (img->numcomps == 3) {
        data = img->comps[1].data;

        for (i = 0; i < (img->comps[1].w * img->comps[1].h); i++) {
            v = *data++;
            buf[0] = (unsigned char)v;

            if (is_16) {
                buf[1] = (unsigned char)(v >> 8);
            }

            fwrite(buf, 1, prec_bytes, f);
        }
        data = img->comps[2].data;

        for (i = 0; i < (img->comps[2].w * img->comps[2].h); i++) {
            v = *data++;
            buf[0] = (unsigned char)v;

            if (is_16) {
                buf[1] = (unsigned char)(v >> 8);
            }

            fwrite(buf, 1, prec_bytes, f);
        }
    } else if (img->numcomps == 1) {
        /* fake CbCr values */
        if (is_16) {
            buf[0] = 255;
            if (img->comps[0].prec == 10) {
                buf[1] = 1;
            } else if (img->comps[0].prec == 12) {
                buf[1] = 3;
            } else {
                buf[1] = 125;
            }
        } else {
            buf[0] = 125;
        }

        for (i = 0; i < (img->comps[0].w * img->comps[0].h * 0.25); i++) {
            fwrite(buf, 1, prec_bytes, f);
        }


        for (i = 0; i < (img->comps[0].w * img->comps[0].h * 0.25); i++) {
            fwrite(buf, 1, prec_bytes, f);
        }
    }
    fclose(f);
    return OPJ_TRUE;
}

/*  ----------------------- */
/* */
/* */
/*  IMAGE to BMP */
/* */
/*  ----------------------- */

int OPJ_CALLCONV imagetobmp(opj_image_t * img, char *outfile)
{
    int w, wr, h, hr, i, pad;
    FILE *f;

    if (img->numcomps == 3 && img->comps[0].dx == img->comps[1].dx
            && img->comps[1].dx == img->comps[2].dx
            && img->comps[0].dy == img->comps[1].dy
            && img->comps[1].dy == img->comps[2].dy
            && img->comps[0].prec == img->comps[1].prec
            && img->comps[1].prec == img->comps[2].prec) {
        /* -->> -->> -->> -->>

          24 bits color

        <<-- <<-- <<-- <<-- */

        f = fopen(outfile, "wb");
        if (!f) {
            fprintf(stderr, "failed to open %s for writing\n", outfile);
            return 1;
        }

        w = img->comps[0].w;
        wr = int_ceildivpow2(img->comps[0].w, img->comps[0].factor);

        h = img->comps[0].h;
        hr = int_ceildivpow2(img->comps[0].h, img->comps[0].factor);

        fprintf(f, "BM");

        /* FILE HEADER */
        /* ------------- */
        fprintf(f, "%c%c%c%c",
                (unsigned char)(hr * wr * 3 + 3 * hr * (wr % 2) +
                                54) & 0xff,
                (unsigned char)((hr * wr * 3 + 3 * hr * (wr % 2) + 54)
                                >> 8) & 0xff,
                (unsigned char)((hr * wr * 3 + 3 * hr * (wr % 2) + 54)
                                >> 16) & 0xff,
                (unsigned char)((hr * wr * 3 + 3 * hr * (wr % 2) + 54)
                                >> 24) & 0xff);
        fprintf(f, "%c%c%c%c", (0) & 0xff, ((0) >> 8) & 0xff,
                ((0) >> 16) & 0xff, ((0) >> 24) & 0xff);
        fprintf(f, "%c%c%c%c", (54) & 0xff, ((54) >> 8) & 0xff,
                ((54) >> 16) & 0xff, ((54) >> 24) & 0xff);

        /* INFO HEADER   */
        /* ------------- */
        fprintf(f, "%c%c%c%c", (40) & 0xff, ((40) >> 8) & 0xff,
                ((40) >> 16) & 0xff, ((40) >> 24) & 0xff);
        fprintf(f, "%c%c%c%c", (unsigned char)((wr) & 0xff),
                (unsigned char)((wr) >> 8) & 0xff,
                (unsigned char)((wr) >> 16) & 0xff,
                (unsigned char)((wr) >> 24) & 0xff);
        fprintf(f, "%c%c%c%c", (unsigned char)((hr) & 0xff),
                (unsigned char)((hr) >> 8) & 0xff,
                (unsigned char)((hr) >> 16) & 0xff,
                (unsigned char)((hr) >> 24) & 0xff);
        fprintf(f, "%c%c", (1) & 0xff, ((1) >> 8) & 0xff);
        fprintf(f, "%c%c", (24) & 0xff, ((24) >> 8) & 0xff);
        fprintf(f, "%c%c%c%c", (0) & 0xff, ((0) >> 8) & 0xff,
                ((0) >> 16) & 0xff, ((0) >> 24) & 0xff);
        fprintf(f, "%c%c%c%c",
                (unsigned char)(3 * hr * wr +
                                3 * hr * (wr % 2)) & 0xff,
                (unsigned char)((hr * wr * 3 + 3 * hr * (wr % 2)) >>
                                8) & 0xff,
                (unsigned char)((hr * wr * 3 + 3 * hr * (wr % 2)) >>
                                16) & 0xff,
                (unsigned char)((hr * wr * 3 + 3 * hr * (wr % 2)) >>
                                24) & 0xff);
        fprintf(f, "%c%c%c%c", (7834) & 0xff, ((7834) >> 8) & 0xff,
                ((7834) >> 16) & 0xff, ((7834) >> 24) & 0xff);
        fprintf(f, "%c%c%c%c", (7834) & 0xff, ((7834) >> 8) & 0xff,
                ((7834) >> 16) & 0xff, ((7834) >> 24) & 0xff);
        fprintf(f, "%c%c%c%c", (0) & 0xff, ((0) >> 8) & 0xff,
                ((0) >> 16) & 0xff, ((0) >> 24) & 0xff);
        fprintf(f, "%c%c%c%c", (0) & 0xff, ((0) >> 8) & 0xff,
                ((0) >> 16) & 0xff, ((0) >> 24) & 0xff);

        for (i = 0; i < wr * hr; i++) {
            unsigned char R, G, B;
            /* a modifier */
            /* R = img->comps[0].data[w * h - ((i) / (w) + 1) * w + (i) % (w)];*/
            R = img->comps[0].data[w * hr - ((i) / (wr) + 1) * w + (i) % (wr)];
            /* G = img->comps[1].data[w * h - ((i) / (w) + 1) * w + (i) % (w)];*/
            G = img->comps[1].data[w * hr - ((i) / (wr) + 1) * w + (i) % (wr)];
            /* B = img->comps[2].data[w * h - ((i) / (w) + 1) * w + (i) % (w)];*/
            B = img->comps[2].data[w * hr - ((i) / (wr) + 1) * w + (i) % (wr)];
            fprintf(f, "%c%c%c", B, G, R);

            if ((i + 1) % wr == 0) {
                for (pad = (3 * wr) % 4 ? 4 - (3 * wr) % 4 : 0; pad > 0; pad--) { /* ADD */
                    fprintf(f, "%c", 0);
                }
            }
        }
        fclose(f);
    }
    return 0;
}
