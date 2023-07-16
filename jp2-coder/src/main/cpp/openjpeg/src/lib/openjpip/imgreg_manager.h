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

#ifndef     IMGREG_MANAGER_H_
# define    IMGREG_MANAGER_H_

/** image region parameters */
typedef struct imgreg_param {
    int xosiz, yosiz; /** offset from the origin of the reference grid
            at the decomposition level */
    int fx, fy;       /** frame size (fsiz) */
    int ox, oy;       /** offset (roff) */
    int sx, sy;       /** region size (rsiz) */
    int level;        /** decomposition level */
} imgreg_param_t;


/**
 * map view-window requests to codestream image resolutions and regions
 *
 * @param[in] fx,fy       frame size
 * @param[in] rx,ry       offset of region
 * @param[in] rw,rh       size  of region
 * @param[in] XOsiz,YOsiz offset from the origin of the reference grid to the left side of the image area
 * @param[in] Xsiz,Ysiz   size of the reference grid
 * @param[in] numOfreslev number of resolution levels
 * @return                structure of image region parameters
 */
imgreg_param_t map_viewin2imgreg(const int fx,    const int fy,
                                 const int rx,    const int ry,
                                 const int rw,    const int rh,
                                 const int XOsiz, const int YOsiz,
                                 const int Xsiz,  const int Ysiz,
                                 const int numOfreslev);


/**
 * find deconposition level and its resolution size
 * C.4.1 Mapping view-window requests to codestream image resolution
 * and regions
 * Note: only round-down implemented
 *
 * @param[in]     maxlev maximum decomposition level
 * @param[in,out] lev    decomposition level pointer
 * @param[in,out] fx     horizontal frame size pointer
 * @param[in,out] fy     vertical   frame size pointer
 * @param[in,out] xmin   horizontal image offset pointer
 * @param[in,out] ymin   vertical   image offset pointer
 * @param[in,out] xmax   horizontal image size pointer
 * @param[in,out] ymax   vertical   image size pointer
 */
void find_level(int maxlev, int *lev, int *fx, int *fy, int *xmin, int *ymin,
                int *xmax, int *ymax);

/**
 * compute decomposition level (only to get the level
 *   use find_level for all parameters
 *
 * @param[in] fw   horizontal frame size
 * @param[in] fh   vertical   frame size
 * @param[in] Xsiz image width
 * @param[in] Ysiz image height
 * @return decomposition level
 */
int comp_decomplev(int fw, int fh, int Xsiz, int Ysiz);

/**
 * print image region parameters
 *
 * @param[in] imgreg image region structure of parameters
 */
void print_imgreg(imgreg_param_t imgreg);


#endif      /* !IMGREG_MANAGER_H_ */
