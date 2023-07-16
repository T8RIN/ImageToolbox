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

#ifndef     INDEX_MANAGER_H_
# define    INDEX_MANAGER_H_

#include "opj_config.h"
#include "opj_includes.h"

#include "byte_manager.h"
#include "faixbox_manager.h"
#include "metadata_manager.h"
#include "mhixbox_manager.h"

/** progression order */
#if 0
typedef enum porder {
    PROG_UNKNOWN = -1,      /**< place-holder */
    LRCP = 0,               /**< layer-resolution-component-precinct order */
    RLCP = 1,               /**< resolution-layer-component-precinct order */
    RPCL = 2,               /**< resolution-precinct-component-layer order */
    PCRL = 3,               /**< precinct-component-resolution-layer order */
    CPRL = 4                /**< component-precinct-resolution-layer order */
} porder_t;
#endif

/** A.5.1 Image and tile size (SIZ)*/
typedef struct SIZmarker_param {
    Byte2_t Lsiz;              /**< length of marker segment excluding the marker*/
    Byte2_t Rsiz;              /**< capabilities that a decoder needs*/
    Byte4_t Xsiz;              /**< width of the reference grid*/
    Byte4_t Ysiz;              /**< height of the reference grid*/
    Byte4_t XOsiz;             /**< horizontal offset from the origin of the reference grid to the left side of the image area*/
    Byte4_t YOsiz;             /**< vertical offset from the origin of the reference grid to the top side of the image area*/
    Byte4_t XTsiz;             /**< width of one reference tile with respect to the reference grid*/
    Byte4_t YTsiz;             /**< height of one reference tile with respect to the reference grid*/
    Byte4_t XTOsiz;            /**< horizontal offset from the origin of the reference grid to the left side of the first tile*/
    Byte4_t YTOsiz;            /**< vertical offset from the origin of the reference grid to the top side of the first tile*/
    Byte4_t XTnum;             /**< number of tiles in horizontal direction*/
    Byte4_t YTnum;             /**< number of tiles in vertical direction*/
    Byte2_t Csiz;              /**< number of the components in the image*/
    Byte_t  Ssiz[3];           /**< precision (depth) in bits and sign of the component samples*/
    Byte_t  XRsiz[3];          /**< horizontal separation of a sample of component with respect to the reference grid*/
    Byte_t  YRsiz[3];          /**< vertical separation of a sample of component with respect to the reference grid*/
} SIZmarker_param_t;

/** A.6.1 Coding style default (COD)*/
typedef struct CODmarker_param {
    Byte2_t  Lcod;             /**< length of marker segment excluding the marker*/
    Byte_t   Scod;             /**< Coding style for all components*/
    OPJ_PROG_ORDER prog_order;       /**< progression order*/
    Byte2_t  numOflayers;      /**< number of layers*/
    Byte_t   numOfdecomp;      /**< number of decompositions levels*/
    Byte4_t  *XPsiz;           /**< dynamic array of precinct width  at successive resolution level in order*/
    Byte4_t  *YPsiz;           /**< dynamic array of precinct height at successive resolution level in order*/
} CODmarker_param_t;

/** index parameters*/
typedef struct index_param {
    metadatalist_param_t *metadatalist; /**< metadata-bin list*/
    OPJ_OFF_T offset;                     /**< codestream offset*/
    Byte8_t length;                     /**< codestream length */
    Byte8_t mhead_length;               /**< main header length  */
    SIZmarker_param_t SIZ;              /**< SIZ marker information*/
    CODmarker_param_t COD;              /**< COD marker information*/
    faixbox_param_t *tilepart;          /**< tile part information from tpix box*/
    mhixbox_param_t
    **tileheader;       /**< dynamic array of tile header information from thix box*/
    faixbox_param_t
    **precpacket;       /**< dynamic array of precint packet information from ppix box*/
} index_param_t;


/**
 * parse JP2 file
 * AnnexI: Indexing JPEG2000 files for JPIP
 *
 * @param[in] fd file descriptor of the JP2 file
 * @return       pointer to the generated structure of index parameters
 */
index_param_t * parse_jp2file(int fd);

/**
 * print index parameters
 *
 * @param[in] index index parameters
 */
void print_index(index_param_t index);

/**
 * print Image and Tile SIZ parameters
 *
 * @param[in] SIZ SIZ marker information
 */
void print_SIZ(SIZmarker_param_t SIZ);

/**
 * print Coding style default COD parameters
 *
 * @param[in] COD COD marker information
 */
void print_COD(CODmarker_param_t COD);

/**
 * delete index
 *
 * @param[in,out] index addressof the index pointer
 */
void delete_index(index_param_t **index);

/**
 * delete dynamic arrays in COD marker
 *
 * @param[in] COD COD marker information
 */
void delete_COD(CODmarker_param_t COD);


/** 1-dimensional range parameters*/
typedef struct range_param {
    Byte4_t minvalue; /**< minimal value*/
    Byte4_t maxvalue; /**< maximal value*/
} range_param_t;

/**
 * get horizontal range of the tile in reference grid
 *
 * @param[in] SIZ      SIZ marker information
 * @param[in] tile_id  tile id
 * @param[in] level    decomposition level
 * @return             structured range parameter
 */
range_param_t get_tile_Xrange(SIZmarker_param_t SIZ, Byte4_t tile_id,
                              int level);

/**
 * get vertical range of the tile in reference grid
 *
 * @param[in] SIZ      SIZ marker information
 * @param[in] tile_id  tile id
 * @param[in] level    decomposition level
 * @return             structured range parameter
 */
range_param_t get_tile_Yrange(SIZmarker_param_t SIZ, Byte4_t tile_id,
                              int level);


/**
 * get tile wdith at the decomposition level
 *
 * @param[in] SIZ      SIZ marker information
 * @param[in] tile_id  tile id
 * @param[in] level    decomposition level
 * @return             tile width
 */
Byte4_t get_tile_XSiz(SIZmarker_param_t SIZ, Byte4_t tile_id, int level);
Byte4_t get_tile_YSiz(SIZmarker_param_t SIZ, Byte4_t tile_id, int level);


/**
 * answers if the target is feasible to JPT-stream
 *
 * @param[in] index index parameters
 * @return    true if JPT-stream is feasible
 */
OPJ_BOOL isJPTfeasible(index_param_t index);

#endif      /* !INDEX_MANAGER_H_ */
