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
#include <math.h>
#include <stdlib.h>
#include <assert.h>
#include "imgreg_manager.h"

#ifdef SERVER
#include "fcgi_stdio.h"
#define logstream FCGI_stdout
#else
#define FCGI_stdout stdout
#define FCGI_stderr stderr
#define logstream stderr
#endif /*SERVER*/

imgreg_param_t map_viewin2imgreg(const int fx,    const int fy,
                                 const int rx,    const int ry,
                                 const int rw,    const int rh,
                                 const int XOsiz, const int YOsiz,
                                 const int Xsiz,  const int Ysiz,
                                 const int numOfreslev)
{
    imgreg_param_t imgreg;
    int px, py;
    int xmax, ymax;

    imgreg.xosiz = XOsiz;
    imgreg.yosiz = YOsiz;
    imgreg.fx = fx;
    imgreg.fy = fy;
    imgreg.level = 0;
    xmax = Xsiz;
    ymax = Ysiz;

    find_level(numOfreslev, &imgreg.level, &imgreg.fx, &imgreg.fy, &imgreg.xosiz,
               &imgreg.yosiz, &xmax, &ymax);

    if (rx == -1 ||  ry == -1) {
        imgreg.ox = 0;
        imgreg.oy = 0;
    } else {
        imgreg.ox = rx * imgreg.fx / fx;
        imgreg.oy = ry * imgreg.fy / fy;
    }

    if (rw == -1 || rh == -1) {
        imgreg.sx = imgreg.fx;
        imgreg.sy = imgreg.fy;
    } else {
        px = (int)ceil((double)((rx + rw) * imgreg.fx) / (double)fx);
        py = (int)ceil((double)((ry + rh) * imgreg.fy) / (double)fy);

        if (imgreg.fx < px) {
            px = imgreg.fx;
        }
        if (imgreg.fy < py) {
            py = imgreg.fy;
        }

        imgreg.sx = px - imgreg.ox;
        imgreg.sy = py - imgreg.oy;
    }

    if (fx != imgreg.fx || fy != imgreg.fy) {
        fprintf(FCGI_stdout, "JPIP-fsiz: %d,%d\r\n", imgreg.fx, imgreg.fy);
    }

    if (rw != imgreg.sx || rh != imgreg.sy) {
        fprintf(FCGI_stdout, "JPIP-rsiz: %d,%d\r\n", imgreg.sx, imgreg.sy);
    }

    if (rx != imgreg.ox || ry != imgreg.oy) {
        fprintf(FCGI_stdout, "JPIP-roff: %d,%d\r\n", imgreg.ox, imgreg.oy);
    }

    return imgreg;
}

void find_level(int maxlev, int *lev, int *fx, int *fy, int *xmin, int *ymin,
                int *xmax, int *ymax)
{
    int xwidth = *xmax - *xmin;
    int ywidth = *ymax - *ymin;

    /* Find smaller frame size for now (i.e. assume "round-down"). */
    if ((*fx < 1 && xwidth != 0) || (*fy < 1 && ywidth != 0)) {
        fprintf(FCGI_stderr, "Frame size must be strictly positive");
        exit(-1);
    } else if (*lev < maxlev - 1 && (*fx < xwidth || *fy < ywidth)) {
        /* Simulate the ceil function. */
        *xmin = (int)ceil((double) * xmin / (double)2.0);
        *ymin = (int)ceil((double) * ymin / (double)2.0);
        *xmax = (int)ceil((double) * xmax / (double)2.0);
        *ymax = (int)ceil((double) * ymax / (double)2.0);

        (*lev) ++;
        find_level(maxlev, lev, fx, fy, xmin, ymin, xmax, ymax);
    } else {
        *fx = xwidth;
        *fy = ywidth;
    }
}

int comp_decomplev(int fw, int fh, int Xsiz, int Ysiz)
{
    int level;
    int xmin, xmax, ymin, ymax;

    level = 0;
    xmin = ymin = 0;
    xmax = Xsiz;
    ymax = Ysiz;

    find_level(1000, &level, &fw, &fh, &xmin, &ymin, &xmax, &ymax);

    assert(level >= 0);
    return level;
}

void print_imgreg(imgreg_param_t imgreg)
{
#ifndef SERVER
    fprintf(logstream, "codestream image region:\n");
    fprintf(logstream, "\t fsiz: %d, %d\n", imgreg.fx, imgreg.fy);
    fprintf(logstream, "\t roff: %d, %d\n", imgreg.ox, imgreg.oy);
    fprintf(logstream, "\t rsiz: %d, %d\n", imgreg.sx, imgreg.sy);
    fprintf(logstream, "\t level: %d\n", imgreg.level);
#else
    (void)imgreg;
#endif
}
