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
#include <string.h>
#include <stdlib.h>
#include <ctype.h>
#include <assert.h>
#include "box_manager.h"
#include "opj_inttypes.h"

#ifdef SERVER
#include "fcgi_stdio.h"
#define logstream FCGI_stdout
#else
#define FCGI_stdout stdout
#define FCGI_stderr stderr
#define logstream stderr
#endif /*SERVER*/

boxlist_param_t *gene_boxlist(void) {
    boxlist_param_t *boxlist;

    boxlist = (boxlist_param_t *) malloc(sizeof(boxlist_param_t));

    boxlist->first = NULL;
    boxlist->last = NULL;

    return boxlist;
}

boxlist_param_t *get_boxstructure(int fd, OPJ_OFF_T offset, OPJ_SIZE_T length) {
    boxlist_param_t *boxlist;
    box_param_t *box;
    OPJ_OFF_T pos;

    boxlist = NULL;
    pos = offset;
    assert((OPJ_OFF_T) length >= 0);
    do {
        if (!(box = gene_boxbyOffset(fd, pos))) {
            break;
        }

        assert((OPJ_OFF_T) box->length >= 0);
        pos += (OPJ_OFF_T) box->length;

        if (!boxlist) {
            boxlist = gene_boxlist();
        }
        insert_box_into_list(box, boxlist);
    } while (pos < offset + (OPJ_OFF_T) length);

    return boxlist;
}

box_param_t *gene_boxbyOffset(int fd, OPJ_OFF_T offset) {
    Byte_t *data;
    Byte8_t boxlen;
    Byte_t headlen;
    char *boxtype;
    box_param_t *box;

    /* read LBox and TBox*/
    if (!(data = fetch_bytes(fd, offset, 8))) {
        fprintf(FCGI_stderr, "Error: error in gene_boxbyOffset( %d, %"
        PRId64
        ")\n", fd,
                offset);
        return NULL;
    }

    headlen = 8;
    boxlen = (Byte8_t) big4(data);
    boxtype = (char *) (data + 4);

    /* box type constraint*/
    if (!isalpha(boxtype[0]) || !isalpha(boxtype[1]) ||
        (!isalnum(boxtype[2]) && !isspace(boxtype[2])) ||
        (!isalpha(boxtype[3]) && !isspace(boxtype[3]))) {
        free(data);
        return NULL;
    }

    if (boxlen == 1) {
        Byte_t *data2;
        headlen = 16;
        /* read XLBox*/
        if ((data2 = fetch_bytes(fd, offset + 8, 8))) {
            boxlen = big8(data2);
            free(data2);
        } else {
            fprintf(FCGI_stderr, "Error: error in gene_boxbyOffset( %d, %"
            PRId64
            ")\n", fd,
                    offset);
            free(data);
            return NULL;
        }
    }
    box = (box_param_t *) malloc(sizeof(box_param_t));
    box->fd = fd;
    box->offset = offset;
    box->headlen = headlen;
    box->length = boxlen;
    strncpy(box->type, boxtype, 4);
    box->next = NULL;
    free(data);
    return box;
}

box_param_t *gene_boxbyOffinStream(Byte_t *stream, OPJ_OFF_T offset) {
    Byte8_t boxlen;
    Byte_t headlen;
    char *boxtype;
    box_param_t *box;

    /* read LBox and TBox*/
    headlen = 8;
    boxlen = (Byte8_t) big4(stream);
    boxtype = (char *) (stream + 4);

    /* box type constraint*/
    if (!isalpha(boxtype[0]) || !isalpha(boxtype[1]) ||
        (!isalnum(boxtype[2]) && !isspace(boxtype[2])) ||
        (!isalpha(boxtype[3]) && !isspace(boxtype[3]))) {
        return NULL;
    }

    if (boxlen == 1) {
        headlen = 16;
        boxlen = big8(stream + 8); /* read XLBox*/
    }
    box = (box_param_t *) malloc(sizeof(box_param_t));
    box->fd = -1;
    box->offset = offset;
    box->headlen = headlen;
    box->length = boxlen;
    strncpy(box->type, boxtype, 4);
    box->next = NULL;

    return box;
}


box_param_t *gene_boxbyType(int fd, OPJ_OFF_T offset, OPJ_SIZE_T length,
                            const char TBox[]) {
    OPJ_OFF_T pos;
    Byte_t *data;
    Byte8_t boxlen;
    Byte_t headlen;
    char *boxtype;
    box_param_t *foundbox;


    if (length == 0) { /* set the max length*/
        if (get_filesize(fd) <= offset) {
            return NULL;
        }
        assert(get_filesize(fd) > offset);
        assert(offset >= 0);
        length = (OPJ_SIZE_T)(get_filesize(fd) - offset);
    }

    pos = offset;
    assert(pos >= 0);
    assert((OPJ_OFF_T) length >= 0);
    while (pos < offset + (OPJ_OFF_T) length - 7) { /* LBox+TBox-1=7*/

        /* read LBox and TBox*/
        if ((data = fetch_bytes(fd, pos, 8))) {
            headlen = 8;
            boxlen = (Byte8_t) big4(data);
            boxtype = (char *) (data + 4);

            if (boxlen == 1) {
                Byte_t *data2;
                headlen = 16;
                /* read XLBox*/
                if ((data2 = fetch_bytes(fd, pos + 8, 8))) {
                    boxlen = big8(data2);
                    free(data2);
                } else {
                    fprintf(FCGI_stderr, "Error: error in gene_boxbyType( %d, %"
                    PRId64
                    ", %"
                    PRId64
                    ", %s)\n", fd, offset, length, TBox);
                    free(data);
                    return NULL;
                }
            }
            if (strncmp(boxtype, TBox, 4) == 0) {
                foundbox = (box_param_t *) malloc(sizeof(box_param_t));
                foundbox->fd = fd;
                foundbox->offset = pos;
                foundbox->headlen = headlen;
                foundbox->length = boxlen;
                strncpy(foundbox->type, TBox, 4);
                foundbox->next = NULL;
                free(data);
                return foundbox;
            }
            free(data);
        } else {
            fprintf(FCGI_stderr, "Error: error in gene_boxbyType( %d, %"
            PRId64
            ", %"
            PRId64
            ", %s)\n", fd, offset, length, TBox);
            return NULL;
        }
        assert(((Byte8_t) pos + boxlen) >= (Byte8_t) pos);
        pos += (OPJ_OFF_T) boxlen;
    }
    fprintf(FCGI_stderr, "Error: Box %s not found\n", TBox);

    return NULL;
}

box_param_t *gene_boxbyTypeinStream(Byte_t *stream, OPJ_OFF_T offset,
                                    OPJ_SIZE_T length, const char TBox[]) {
    OPJ_OFF_T pos;
    Byte_t *data;
    Byte8_t boxlen;
    Byte_t headlen;
    char *boxtype;
    box_param_t *foundbox;

    pos = offset;
    assert(pos >= 0);
    assert((OPJ_OFF_T) length >= 0);
    while (pos < offset + (OPJ_OFF_T)(length) - 7) { /* LBox+TBox-1=7*/

        /* read LBox and TBox*/
        data = stream + pos;
        headlen = 8;
        boxlen = (Byte8_t) big4(data);
        boxtype = (char *) (data + 4);

        if (boxlen == 1) {
            /* read XLBox*/
            headlen = 16;
            boxlen = big8(data + 8);
        }

        if (strncmp(boxtype, TBox, 4) == 0) {
            foundbox = (box_param_t *) malloc(sizeof(box_param_t));
            foundbox->fd = -1;
            foundbox->offset = pos;
            foundbox->headlen = headlen;
            foundbox->length = boxlen;
            strncpy(foundbox->type, TBox, 4);
            foundbox->next = NULL;
            return foundbox;
        }
        assert(((Byte8_t) pos + boxlen) >= (Byte8_t) pos);
        pos += (OPJ_OFF_T) boxlen;
    }
    fprintf(FCGI_stderr, "Error: Box %s not found\n", TBox);

    return NULL;
}

box_param_t *gene_childboxbyOffset(box_param_t *superbox, OPJ_OFF_T offset) {
    return gene_boxbyOffset(superbox->fd, get_DBoxoff(superbox) + offset);
}

box_param_t *gene_childboxbyType(box_param_t *superbox, OPJ_OFF_T offset,
                                 const char TBox[]) {
    OPJ_SIZE_T DBOXlen = get_DBoxlen(superbox);
    assert(offset >= 0);
    if (DBOXlen < (OPJ_SIZE_T) offset) {
        fprintf(FCGI_stderr, "Error: Impossible happen %lu < %ld\n", DBOXlen, offset);
        return NULL;
    }
    return gene_boxbyType(superbox->fd, get_DBoxoff(superbox) + offset,
                          DBOXlen - (OPJ_SIZE_T) offset, TBox);
}

OPJ_OFF_T get_DBoxoff(box_param_t *box) {
    return box->offset + box->headlen;
}

OPJ_SIZE_T get_DBoxlen(box_param_t *box) {
    return box->length - box->headlen;
}

Byte_t *fetch_headbytes(box_param_t *box) {
    return fetch_bytes(box->fd, box->offset, box->headlen);
}

Byte_t *fetch_DBoxbytes(box_param_t *box, OPJ_OFF_T offset, OPJ_SIZE_T size) {
    return fetch_bytes(box->fd, get_DBoxoff(box) + offset, size);
}

Byte_t fetch_DBox1byte(box_param_t *box, OPJ_OFF_T offset) {
    return fetch_1byte(box->fd, get_DBoxoff(box) + offset);
}

Byte2_t fetch_DBox2bytebigendian(box_param_t *box, OPJ_OFF_T offset) {
    return fetch_2bytebigendian(box->fd, get_DBoxoff(box) + offset);
}

Byte4_t fetch_DBox4bytebigendian(box_param_t *box, OPJ_OFF_T offset) {
    return fetch_4bytebigendian(box->fd, get_DBoxoff(box) + offset);
}

Byte8_t fetch_DBox8bytebigendian(box_param_t *box, OPJ_OFF_T offset) {
    return fetch_8bytebigendian(box->fd, get_DBoxoff(box) + offset);
}

box_param_t *search_box(const char type[], boxlist_param_t *boxlist) {
    box_param_t *foundbox;

    foundbox = boxlist->first;

    while (foundbox != NULL) {

        if (strncmp(type, foundbox->type, 4) == 0) {
            return foundbox;
        }

        foundbox = foundbox->next;
    }
    fprintf(FCGI_stderr, "Error: Box %s not found\n", type);

    return NULL;
}

void print_box(box_param_t *box) {
    fprintf(logstream, "box info:\n"
                       "\t type: %.4s\n"
                       "\t offset: %"
    PRId64
    " %#"
    PRIx64
    "\n"
    "\t header length: %d\n"
    "\t length: %"
    PRId64
    " %#"
    PRIx64
    "\n", box->type, box->offset,
            box->offset, box->headlen, box->length, box->length);
}

void print_allbox(boxlist_param_t *boxlist) {
    box_param_t *ptr;

    if (!boxlist) {
        return;
    }

    ptr = boxlist->first;
    if (!ptr) {
        fprintf(logstream, "no box\n");
    }

    fprintf(logstream, "all box info: \n");
    while (ptr != NULL) {
        print_box(ptr);
        ptr = ptr->next;
    }
}

void delete_box_in_list(box_param_t **box, boxlist_param_t *boxlist) {
    box_param_t *ptr;

    if (*box == boxlist->first) {
        boxlist->first = (*box)->next;
    } else {
        ptr = boxlist->first;
        while (ptr->next != *box) {
            ptr = ptr->next;
        }
        ptr->next = (*box)->next;

        if (*box == boxlist->last) {
            boxlist->last = ptr;
        }
    }
    free(*box);
}

void delete_box_in_list_by_type(const char type[], boxlist_param_t *boxlist) {
    box_param_t *box;

    box = search_box(type, boxlist);
    delete_box_in_list(&box, boxlist);
}

void delete_boxlist(boxlist_param_t **boxlist) {
    box_param_t *boxPtr, *boxNext;

    if (!(*boxlist)) {
        return;
    }

    boxPtr = (*boxlist)->first;
    while (boxPtr != NULL) {
        boxNext = boxPtr->next;
        free(boxPtr);
        boxPtr = boxNext;
    }
    free(*boxlist);
}

void insert_box_into_list(box_param_t *box, boxlist_param_t *boxlist) {
    if (boxlist->first) {
        boxlist->last->next = box;
    } else {
        boxlist->first = box;
    }
    boxlist->last = box;
}
