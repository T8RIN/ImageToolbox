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

#ifndef     BOX_MANAGER_H_
# define    BOX_MANAGER_H_

#include "byte_manager.h"

/** box parameters*/
typedef struct box_param {
    int fd;                 /**< file descriptor*/
    OPJ_OFF_T offset;         /**< byte position of the whole Box (LBox) in the file*/
    Byte_t headlen;        /**< header length  8 or 16*/
    Byte8_t length;         /**< length of the whole Box*/
    char type[4];        /**< type of information in the DBox*/
    struct box_param *next; /**< pointer to the next box*/
} box_param_t;


/** Box list parameters*/
typedef struct boxlist_param {
    box_param_t *first; /**< first box pointer of the list*/
    box_param_t *last;  /**< last  box pointer of the list*/
} boxlist_param_t;


/**
 * generate a box list
 *
 * @return pointer to the generated box list
 */
boxlist_param_t *gene_boxlist(void);

/**
 * get box structure of JP2 file
 *
 * @param[in]  fd     file descriptor
 * @param[in]  offset offset of the decomposing region
 * @param[in]  length length of the decomposing region
 * @return            pointer to the generated boxlist
 */
boxlist_param_t *get_boxstructure(int fd, OPJ_OFF_T offset, OPJ_SIZE_T length);


/**
 * generate box from JP2 file at the given offset
 *
 * @param[in] fd     file discriptor of the JP2 file
 * @param[in] offset Box offset
 * @return           pointer to the structure of generate box parameters
 */
box_param_t *gene_boxbyOffset(int fd, OPJ_OFF_T offset);


/**
 * generate box from code stream (JPP or JPT stream) at the given offset
 *
 * @param[in] stream code stream of a box
 * @param[in] offset Box offset of the whole stream
 * @return           pointer to the structure of generate box parameters
 */
box_param_t *gene_boxbyOffinStream(Byte_t *stream, OPJ_OFF_T offset);

/**
 * generate(search) box from JP2 file
 *
 * @param[in] fd     file discriptor of the JP2 file
 * @param[in] offset start Byte position of the search
 * @param[in] length Byte length of the search, if 0, size to the end of file
 * @param[in] TBox   Box Type
 * @return           pointer to the structure of generate/found box parameters
 */
box_param_t *gene_boxbyType(int fd, OPJ_OFF_T offset, OPJ_SIZE_T length,
                            const char TBox[]);

/**
 * generate(search) box from code stream
 *
 * @param[in] stream code stream ( from the first byte)
 * @param[in] offset start Byte position of the search
 * @param[in] length Byte length of the search, if 0, size to the end of file
 * @param[in] TBox   Box Type
 * @return           pointer to the structure of generate/found box parameters
 */
box_param_t *gene_boxbyTypeinStream(Byte_t *stream, OPJ_OFF_T offset,
                                    OPJ_SIZE_T length, const char TBox[]);

/**
 * generate child box from JP2 file at the given offset
 *
 * @param[in] superbox super box pointer
 * @param[in] offset   offset from DBox first byte of superbox
 * @return             pointer to the structure of generate box parameters
 */
box_param_t *gene_childboxbyOffset(box_param_t *superbox, OPJ_OFF_T offset);

/**
 * generate(search) box from JP2 file
 *
 * @param[in] superbox super box pointer
 * @param[in] offset   offset from DBox first byte of superbox
 * @param[in] TBox     Box Type
 * @return             pointer to the structure of generate/found box parameters
 */
box_param_t *gene_childboxbyType(box_param_t *superbox, OPJ_OFF_T offset,
                                 const char TBox[]);

/**
 * get DBox offset
 *
 * @param[in] box box pointer
 * @return        DBox offset (byte position) in the file
 */
OPJ_OFF_T get_DBoxoff(box_param_t *box);


/**
 * get DBox length
 *
 * @param[in] box box pointer
 * @return        DBox length ( content length)
 */
OPJ_SIZE_T get_DBoxlen(box_param_t *box);


/**
 * fetch header bytes in file stream
 *
 * @param[in] box    box pointer
 * @return           pointer to the fetched bytes
 */
Byte_t *fetch_headbytes(box_param_t *box);


/**
 * fetch DBox (Box Contents) bytes of data in file stream
 *
 * @param[in] box    box pointer
 * @param[in] offset start Byte position in DBox
 * @param[in] size   Byte length
 * @return           pointer to the fetched data
 */
Byte_t *fetch_DBoxbytes(box_param_t *box, OPJ_OFF_T offset, OPJ_SIZE_T size);

/**
 * fetch DBox (Box Contents) 1-byte Byte codes in file stream
 *
 * @param[in] box    box pointer
 * @param[in] offset start Byte position in DBox
 * @return           fetched code
 */
Byte_t fetch_DBox1byte(box_param_t *box, OPJ_OFF_T offset);

/**
 * fetch DBox (Box Contents) 2-byte big endian Byte codes in file stream
 *
 * @param[in] box    box pointer
 * @param[in] offset start Byte position in DBox
 * @return           fetched code
 */
Byte2_t fetch_DBox2bytebigendian(box_param_t *box, OPJ_OFF_T offset);

/**
 * fetch DBox (Box Contents) 4-byte big endian Byte codes in file stream
 *
 * @param[in] box    box pointer
 * @param[in] offset start Byte position in DBox
 * @return           fetched code
 */
Byte4_t fetch_DBox4bytebigendian(box_param_t *box, OPJ_OFF_T offset);

/**
 * fetch DBox (Box Contents) 8-byte big endian Byte codes in file stream
 *
 * @param[in] box    box pointer
 * @param[in] offset start Byte position in DBox
 * @return           fetched code
 */
Byte8_t fetch_DBox8bytebigendian(box_param_t *box, OPJ_OFF_T offset);


/**
 * search a box by box type
 *
 * @param[in] type    box type
 * @param[in] boxlist box list pointer
 * @return            found box pointer
 */
box_param_t *search_box(const char type[], boxlist_param_t *boxlist);

/**
 * print box parameters
 *
 * @param[in] box box pointer
 */
void print_box(box_param_t *box);


/**
 * print all box parameters
 *
 * @param[in] boxlist box list pointer
 */
void print_allbox(boxlist_param_t *boxlist);

/**
 * delete a box in list
 *
 * @param[in,out] box     address of the deleting box pointer
 * @param[in]     boxlist box list pointer
 */
void delete_box_in_list(box_param_t **box, boxlist_param_t *boxlist);


/**
 * delete a box in list by Type
 *
 * @param[in,out] type    box type
 * @param[in]     boxlist box list pointer
 */
void delete_box_in_list_by_type(const char type[], boxlist_param_t *boxlist);


/**
 * delete box list
 *
 * @param[in,out] boxlist address of the box list pointer
 */
void delete_boxlist(boxlist_param_t **boxlist);


/**
 * insert a box into list
 *
 * @param[in] box     box pointer
 * @param[in] boxlist box list pointer
 */
void insert_box_into_list(box_param_t *box, boxlist_param_t *boxlist);

#endif      /* !BOX_MANAGER_H_ */
