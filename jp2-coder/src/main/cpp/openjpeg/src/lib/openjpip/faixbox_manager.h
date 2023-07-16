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

#ifndef     FAIXBOX_MANAGER_H_
# define    FAIXBOX_MANAGER_H_

#include "byte_manager.h"
#include "box_manager.h"

/** 4byte parameters of a faix element*/
typedef struct faixelem4_param {
    Byte4_t off; /**< offset*/
    Byte4_t len; /**< length*/
} faixelem4_param_t;

/** 8byte parameters of a faix element*/
typedef struct faixelem8_param {
    Byte8_t off; /**< offset*/
    Byte8_t len; /**< length*/
} faixelem8_param_t;

/** 4byte parameters of fragment array index box*/
typedef struct subfaixbox4_param {
    Byte4_t nmax;             /**< maximum number of valid elements in any row of the array*/
    Byte4_t m;                /**< number of raws of the array*/
    faixelem4_param_t *elem;  /**< dynamic array pointer of faix elements*/
    Byte4_t *aux;             /**< dynamic array pointer of auxiliary*/
    /**info in each element for version 2 or 3*/
} subfaixbox4_param_t;

/** 8byte parameters of fragment array index box*/
typedef struct subfaixbox8_param {
    Byte8_t nmax;             /**< maximum number of valid elements in any row of the array*/
    Byte8_t m;                /**< number of raws of the array*/
    faixelem8_param_t *elem;  /**< dynamic array pointer of faix elements*/
    Byte4_t *aux;             /**< dynamic array pointer of auxiliary*/
    /**info in each element for version 2 or 3*/
} subfaixbox8_param_t;

/** variable sized parameters in fragment array index box*/
typedef union subfaixbox_param {
    subfaixbox4_param_t
    *byte4_params; /**< parameters with 4byte codes for version 0 or 2*/
    subfaixbox8_param_t
    *byte8_params; /**< parameters with 8byte codes for version 1 or 3*/
} subfaixbox_param_t;

/** fragment array index box parameters*/
/** I.3.2.4.2 Fragment Array Index box*/
typedef struct faixbox_param {
    Byte_t version;                /**< Refer to the Table I.3 - Version values*/
    subfaixbox_param_t subfaixbox; /**< rest information in faixbox*/
} faixbox_param_t;


/**
 * generate faix box
 *
 * @param[in] box pointer to the reference faix_box
 * @return        generated faixbox
 */
faixbox_param_t * gene_faixbox(box_param_t *box);


/**
 * print faix box parameters
 *
 * @param[in] faix faix box pointer
 */
void print_faixbox(faixbox_param_t *faix);


/**
 * delete faix box
 *
 * @param[in,out] faix addressof the faixbox pointer
 */
void delete_faixbox(faixbox_param_t **faix);

/**
 * get nmax parameter value from faix box
 *
 * @param[in] faix faix box pointer
 */
Byte8_t get_nmax(faixbox_param_t *faix);

/**
 * get m parameter value from faix box
 *
 * @param[in] faix faix box pointer
 */
Byte8_t get_m(faixbox_param_t *faix);

/**
 * get offset of a element from faix box
 *
 * @param[in] faix    faix box pointer
 * @param[in] elem_id element id in a row (0<= <nmax)
 * @param[in] row_id  row id (0<= <m)
 */
Byte8_t get_elemOff(faixbox_param_t *faix, Byte8_t elem_id, Byte8_t row_id);

/**
 * get length of a element from faix box
 *
 * @param[in] faix    faix box pointer
 * @param[in] elem_id element id in a row (0<= <nmax)
 * @param[in] row_id  row id (0<= <m)
 */
Byte8_t get_elemLen(faixbox_param_t *faix, Byte8_t elem_id, Byte8_t row_id);

/**
 * get aux of a element from faix box
 *
 * @param[in] faix    faix box pointer
 * @param[in] elem_id element id in a row (0<= <nmax)
 * @param[in] row_id  row id (0<= <m)
 */
Byte4_t get_elemAux(faixbox_param_t *faix, Byte8_t elem_id, Byte8_t row_id);

#endif      /* !FAIXBOX_MANAGER_H_ */
