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

#ifndef     MHIXBOX_MANAGER_H_
# define    MHIXBOX_MANAGER_H_

#include "byte_manager.h"
#include "box_manager.h"

/** Marker index parameters*/
typedef struct markeridx_param {
    Byte2_t code;                 /**< marker code*/
    Byte2_t num_remain;           /**< remining number of the same marker index segments listed immediately*/
    OPJ_OFF_T offset;               /**< offset relative to the start of the*/
    /**codestream ( including the length*/
    /**parameter but not the marker itself)*/
    Byte2_t length;               /**< marker segment length*/
    struct markeridx_param *next; /**< pointer to the next markeridx*/
} markeridx_param_t;



/** header index table box parameters*/
/** I.3.2.4.3 Header Index Table box*/
typedef struct mhixbox_param {
    Byte8_t tlen;             /**< length ( total length of the main*/
    /**header or of the first tile-part header)*/
    markeridx_param_t *first; /**< first marker index pointer of the list*/
} mhixbox_param_t;



/**
 * generate mhix box
 *
 * @param[in] box pointer to the reference mhix box
 * @return        generated mhixbox pointer
 */
mhixbox_param_t * gene_mhixbox(box_param_t *box);


/**
 * search a marker index by marker code from mhix box
 *
 * @param[in] code marker code
 * @param[in] mhix mhix box pointer
 * @return         found marker index pointer
 */
markeridx_param_t * search_markeridx(Byte2_t code, mhixbox_param_t *mhix);


/**
 * print mhix box parameters
 *
 * @param[in] mhix mhix box pointer
 */
void print_mhixbox(mhixbox_param_t *mhix);


/**
 * print marker index parameters
 *
 * @param[in] markeridx marker index pointer
 */
void print_markeridx(markeridx_param_t *markeridx);


/**
 * delete mhix box
 *
 * @param[in,out] mhix address of the mhix box pointer
 */
void delete_mhixbox(mhixbox_param_t **mhix);

#endif      /* !MHIXBOX_MANAGER_H_ */
