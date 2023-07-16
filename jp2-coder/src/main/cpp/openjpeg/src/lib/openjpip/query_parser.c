/*
 * $Id$
 *
 * Copyright (c) 2002-2014, Universite catholique de Louvain (UCL), Belgium
 * Copyright (c) 2002-2014, Professor Benoit Macq
 * Copyright (c) 2010-2011, Kaori Hagihara
 * Copyright (c) 2011,      Lucian Corlaciu, GSoC
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


#ifdef _WIN32
#include <windows.h>
#define strcasecmp  _stricmp
#define strncasecmp _strnicmp
#else
#include <strings.h>
#endif

#include <stdio.h>
#include <assert.h>
#include <string.h>
#include <stdlib.h>
#include "query_parser.h"
#include "opj_stdint.h"

#ifdef SERVER
#include "fcgi_stdio.h"
#define logstream FCGI_stdout
#else
#define FCGI_stdout stdout
#define FCGI_stderr stderr
#define logstream stderr
#endif /*SERVER*/


/**
 * Get initialized query parameters
 *
 * @return initial query parameters
 */
query_param_t * get_initquery(void);

/*
 * get a pair of field name and value from the string starting fieldname=fieldval&... format
 *
 * @param[in] stringptr pointer to the beginning of the parsing string
 * @param[out] fieldname string to copy the field name, if not found, NULL
 * @param[out] fieldval string to copy the field value, if not found, NULL
 * @return pointer to the next field string, if there is none, NULL
 */
char * get_fieldparam(const char *stringptr, char *fieldname, char *fieldval);

void parse_cclose(char *src, query_param_t *query_param);
void parse_metareq(char *field, query_param_t *query_param);

/* parse the requested components (parses forms like:a; a,b; a-b; a-b,c;  a,b-c)*/
void parse_comps(char *field, query_param_t *query_param);


/** maximum length of field name*/
#define MAX_LENOFFIELDNAME 10

/** maximum length of field value*/
#define MAX_LENOFFIELDVAL 128

query_param_t * parse_query(const char *query_string)
{
    query_param_t *query_param;
    const char *pquery;
    char fieldname[MAX_LENOFFIELDNAME], fieldval[MAX_LENOFFIELDVAL];

    query_param = get_initquery();

    pquery = query_string;

    while (pquery != NULL) {

        pquery = get_fieldparam(pquery, fieldname, fieldval);

        if (fieldname[0] != '\0') {
            if (strcasecmp(fieldname, "target") == 0) {
                query_param->target = strdup(fieldval);
            }

            else if (strcasecmp(fieldname, "tid") == 0) {
                query_param->tid = strdup(fieldval);
            }

            else if (strcasecmp(fieldname, "fsiz") == 0) {
                sscanf(fieldval, "%d,%d", &query_param->fx, &query_param->fy);
            }

            else if (strcasecmp(fieldname, "roff") == 0) {
                sscanf(fieldval, "%d,%d", &query_param->rx, &query_param->ry);
            }

            else if (strcasecmp(fieldname, "rsiz") == 0) {
                sscanf(fieldval, "%d,%d", &query_param->rw, &query_param->rh);
            }

            else if (strcasecmp(fieldname, "layers") == 0) {
                sscanf(fieldval, "%d", &query_param->layers);
            }

            else if (strcasecmp(fieldname, "cid") == 0) {
                query_param->cid = strdup(fieldval);
            }

            else if (strcasecmp(fieldname, "cnew") == 0) {
                if (strncasecmp(fieldval, "http-tcp", 8) == 0) {
                    query_param->cnew = tcp;
                } else if (strncasecmp(fieldval, "http", 4) == 0) {
                    query_param->cnew = http;
                }
            }

            else if (strcasecmp(fieldname, "cclose") == 0) {
                parse_cclose(fieldval, query_param);
            }

            else if (strcasecmp(fieldname, "metareq") == 0) {
                parse_metareq(fieldval, query_param);
            }

            else if (strcasecmp(fieldname, "comps") == 0) {
                parse_comps(fieldval, query_param);
            }

            else if (strcasecmp(fieldname, "type") == 0) {
                if (strncasecmp(fieldval, "jpp-stream", 10) == 0) {
                    query_param->return_type = JPPstream;
                } else if (strncasecmp(fieldval, "jpt-stream", 10) == 0) {
                    query_param->return_type = JPTstream;
                }
            }

            else if (strcasecmp(fieldname, "len") == 0) {
                sscanf(fieldval, "%d", &query_param->len);
                if (query_param->len == 2000) { /* for kakadu client*/
                    strncpy(query_param->box_type[0], "ftyp", 4);
                }
            }
        }
    }
    return query_param;
}

query_param_t * get_initquery(void)
{
    query_param_t *query;
    int i;

    query = (query_param_t *)opj_malloc(sizeof(query_param_t));

    query->target = NULL;
    query->tid = NULL;
    query->fx = -1;
    query->fy = -1;
    query->rx = -1;
    query->ry = -1;
    query->rw = -1;
    query->rh = -1;
    query->layers = -1;
    query->lastcomp = -1;
    query->comps = NULL;
    query->cid = NULL;
    query->cnew = non;
    query->cclose = NULL;
    query->numOfcclose = 0;
    memset(query->box_type, 0, MAX_NUMOFBOX * 4);
    memset(query->limit, 0, MAX_NUMOFBOX * sizeof(int));
    for (i = 0; i < MAX_NUMOFBOX; i++) {
        query->w[i] = OPJ_FALSE;
        query->s[i] = OPJ_FALSE;
        query->g[i] = OPJ_FALSE;
        query->a[i] = OPJ_FALSE;
        query->priority[i] = OPJ_FALSE;
    }
    query->root_bin = 0;
    query->max_depth = -1;
    query->metadata_only = OPJ_FALSE;
    query->return_type = UNKNOWN;
    query->len = -1;

    return query;
}


char * get_fieldparam(const char *stringptr, char *fieldname, char *fieldval)
{
    char *eqp, *andp, *nexfieldptr;

    if ((eqp = strchr(stringptr, '=')) == NULL) {
        fprintf(stderr, "= not found\n");
        strcpy(fieldname, "");
        strcpy(fieldval, "");
        return NULL;
    }
    if ((andp = strchr(stringptr, '&')) == NULL) {
        andp = strchr(stringptr, '\0');
        nexfieldptr = NULL;
    } else {
        nexfieldptr = andp + 1;
    }

    assert((size_t)(eqp - stringptr));
    strncpy(fieldname, stringptr, (size_t)(eqp - stringptr));
    fieldname[eqp - stringptr] = '\0';
    assert(andp - eqp - 1 >= 0);
    strncpy(fieldval, eqp + 1, (size_t)(andp - eqp - 1));
    fieldval[andp - eqp - 1] = '\0';

    return nexfieldptr;
}

void print_queryparam(query_param_t query_param)
{
    int i;
    char *cclose;

    fprintf(logstream, "query parameters:\n");

    if (query_param.target) {
        fprintf(logstream, "\t target: %s\n", query_param.target);
    }

    if (query_param.tid) {
        fprintf(logstream, "\t tid: %s\n", query_param.tid);
    }

    fprintf(logstream, "\t fx,fy: %d, %d\n", query_param.fx, query_param.fy);
    fprintf(logstream, "\t rx,ry: %d, %d \t rw,rh: %d, %d\n", query_param.rx,
            query_param.ry, query_param.rw, query_param.rh);
    fprintf(logstream, "\t layers: %d\n", query_param.layers);
    fprintf(logstream, "\t components: ");

    if (query_param.lastcomp == -1) {
        fprintf(logstream, "ALL\n");
    } else {
        for (i = 0; i <= query_param.lastcomp; i++)
            if (query_param.comps[i]) {
                fprintf(logstream, "%d ", i);
            }
        fprintf(logstream, "\n");
    }
    fprintf(logstream, "\t cnew: %d\n", query_param.cnew);

    if (query_param.cid) {
        fprintf(logstream, "\t cid: %s\n", query_param.cid);
    }

    if (query_param.cclose) {
        fprintf(logstream, "\t cclose: ");

        for (i = 0, cclose = query_param.cclose; i < query_param.numOfcclose; i++) {
            fprintf(logstream, "%s ", cclose);
            cclose += (strlen(cclose) + 1);
        }
        fprintf(logstream, "\n");
    }

    fprintf(logstream, "\t req-box-prop\n");
    for (i = 0; i < MAX_NUMOFBOX && query_param.box_type[i][0] != 0; i++) {
        fprintf(logstream,
                "\t\t box_type: %.4s limit: %d w:%d s:%d g:%d a:%d priority:%d\n",
                query_param.box_type[i], query_param.limit[i], query_param.w[i],
                query_param.s[i], query_param.g[i], query_param.a[i], query_param.priority[i]);
    }

    fprintf(logstream, "\t root-bin:  %d\n", query_param.root_bin);
    fprintf(logstream, "\t max-depth: %d\n", query_param.max_depth);
    fprintf(logstream, "\t metadata-only: %d\n", query_param.metadata_only);
    fprintf(logstream,
            "\t image return type: %d, [JPP-stream=0, JPT-stream=1, UNKNOWN=-1]\n",
            query_param.return_type);
    fprintf(logstream, "\t len:  %d\n", query_param.len);
}

void parse_cclose(char *src, query_param_t *query_param)
{
    size_t i;
    size_t len;

    len = strlen(src);
    query_param->cclose = strdup(src);

    for (i = 0; i < len; i++)
        if (query_param->cclose[i] == ',') {
            query_param->cclose[i] = '\0';
            query_param->numOfcclose ++;
        }

    query_param->numOfcclose ++;
}

void parse_req_box_prop(char *req_box_prop, int idx,
                        query_param_t *query_param);

void parse_metareq(char *field, query_param_t *query_param)
{
    char req_box_prop[20];
    char *ptr, *src;
    int numofboxreq = 0;

    memset(req_box_prop, 0, 20);

    /* req-box-prop*/
    ptr = strchr(field, '[');
    ptr++;
    src = ptr;
    while (*ptr != ']') {
        if (*ptr == ';') {
            assert(ptr - src >= 0);
            strncpy(req_box_prop, src, (size_t)(ptr - src));
            parse_req_box_prop(req_box_prop, numofboxreq++, query_param);
            ptr++;
            src = ptr;
            memset(req_box_prop, 0, 20);
        }
        ptr++;
    }
    assert(ptr - src >= 0);
    strncpy(req_box_prop, src, (size_t)(ptr - src));

    parse_req_box_prop(req_box_prop, numofboxreq++, query_param);

    if ((ptr = strchr(field, 'R'))) {
        sscanf(ptr + 1, "%d", &(query_param->root_bin));
    }

    if ((ptr = strchr(field, 'D'))) {
        sscanf(ptr + 1, "%d", &(query_param->max_depth));
    }

    if ((ptr = strstr(field, "!!"))) {
        query_param->metadata_only = OPJ_TRUE;
    }
}

void parse_req_box_prop(char *req_box_prop, int idx, query_param_t *query_param)
{
    char *ptr;

    if (*req_box_prop == '*') {
        query_param->box_type[idx][0] = '*';
    } else {
        strncpy(query_param->box_type[idx], req_box_prop, 4);
    }

    if ((ptr = strchr(req_box_prop, ':'))) {
        if (*(ptr + 1) == 'r') {
            query_param->limit[idx] = -1;
        } else {
            sscanf(ptr + 1, "%d", &(query_param->limit[idx]));
        }
    }

    if ((ptr = strchr(req_box_prop, '/'))) {
        ptr++;
        while (*ptr == 'w' || *ptr == 's' || *ptr == 'g' || *ptr == 'a') {
            switch (*ptr) {
            case 'w':
                query_param->w[idx] = OPJ_TRUE;
                break;
            case 's':
                query_param->s[idx] = OPJ_TRUE;
                break;
            case 'g':
                query_param->g[idx] = OPJ_TRUE;
                break;
            case 'a':
                query_param->a[idx] = OPJ_TRUE;
                break;
            }
            ptr++;
        }
    } else {
        query_param->g[idx] = OPJ_TRUE;
        query_param->s[idx] = OPJ_TRUE;
        query_param->w[idx] = OPJ_TRUE;
    }

    if ((ptr = strchr(req_box_prop, '!'))) {
        query_param->priority[idx] = OPJ_TRUE;
    }

    idx++;
}

void parse_comps(char *field, query_param_t *query_param)
{
    int i, start, stop, aux = -1;
    char *ptr1, *ptr2;

    ptr1 = strchr(field, '-');
    ptr2 = strchr(field, ',');

    if (ptr1 && ptr2)
        if (ptr1 > ptr2) {
            sscanf(field, "%d,%d-%d", &aux, &start, &stop);
        } else {
            sscanf(field, "%d-%d,%d", &start, &stop, &aux);
        } else if (ptr1) {
        sscanf(field, "%d-%d", &start, &stop);
    } else if (ptr2) {
        sscanf(field, "%d,%d", &start, &stop);
        aux = start;
        start = stop;
    } else {
        sscanf(field, "%d", &stop);
        start = stop;
    }

    query_param->lastcomp = stop > aux ? stop : aux;
    query_param->comps = (OPJ_BOOL *)opj_calloc(1,
                         (OPJ_SIZE_T)(query_param->lastcomp + 1) * sizeof(OPJ_BOOL));

    for (i = start; i <= stop; i++) {
        query_param->comps[i] = OPJ_TRUE;
    }

    if (aux != -1) {
        query_param->comps[aux] = OPJ_TRUE;
    }
}

void delete_query(query_param_t **query)
{
    if ((*query)->target) {
        opj_free((*query)->target);
    }

    if ((*query)->tid) {
        opj_free((*query)->tid);
    }

    if ((*query)->comps) {
        opj_free((*query)->comps);
    }

    if ((*query)->cid) {
        opj_free((*query)->cid);
    }

    if ((*query)->cclose) {
        opj_free((*query)->cclose);
    }

    opj_free(*query);
}
