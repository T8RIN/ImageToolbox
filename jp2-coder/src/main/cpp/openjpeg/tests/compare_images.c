/*
 * Copyright (c) 2011-2012, Centre National d'Etudes Spatiales (CNES), France
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

/*
 * compare_images.c
 *
 *  Created on: 8 juil. 2011
 *      Author: mickael
 */

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <string.h>
#include <ctype.h>
#include <assert.h>

#include "opj_apps_config.h"
#include "opj_getopt.h"

#include "openjpeg.h"
#include "format_defs.h"
#include "convert.h"

#ifdef OPJ_HAVE_LIBTIFF
#include <tiffio.h> /* TIFFSetWarningHandler */
#endif /* OPJ_HAVE_LIBTIFF */

/*******************************************************************************
 * Parse MSE and PEAK input values (
 * separator = ":"
 *******************************************************************************/
static double* parseToleranceValues(char* inArg, const int nbcomp)
{
    double* outArgs = malloc((size_t)nbcomp * sizeof(double));
    int it_comp = 0;
    const char delims[] = ":";
    char *result = strtok(inArg, delims);

    while ((result != NULL) && (it_comp < nbcomp)) {
        outArgs[it_comp] = atof(result);
        result = strtok(NULL, delims);
        it_comp++;
    }

    if (it_comp != nbcomp) {
        free(outArgs);
        return NULL;
    }
    /* else */
    return outArgs;
}

/*******************************************************************************
 * Command line help function
 *******************************************************************************/
static void compare_images_help_display(void)
{
    fprintf(stdout, "\nList of parameters for the compare_images function  \n");
    fprintf(stdout, "\n");
    fprintf(stdout,
            "  -b \t REQUIRED \t filename to the reference/baseline PGX/TIF/PNM image \n");
    fprintf(stdout, "  -t \t REQUIRED \t filename to the test PGX/TIF/PNM image\n");
    fprintf(stdout,
            "  -n \t REQUIRED \t number of component of the image (used to generate correct filename, not used when both input files are TIF)\n");
    fprintf(stdout,
            "  -m \t OPTIONAL \t list of MSE tolerances, separated by : (size must correspond to the number of component) of \n");
    fprintf(stdout,
            "  -p \t OPTIONAL \t list of PEAK tolerances, separated by : (size must correspond to the number of component) \n");
    fprintf(stdout,
            "  -s \t OPTIONAL \t 1 or 2 filename separator to take into account PGX/PNM image with different components, "
            "please indicate b or t before separator to indicate respectively the separator "
            "for ref/base file and for test file.  \n");
    fprintf(stdout,
            "  -d \t OPTIONAL \t indicate if you want to run this function as conformance test or as non regression test\n");
    fprintf(stdout,
            "  -i \t OPTIONAL \t list of features to ignore. Currently 'prec' only supported\n");
    fprintf(stdout, "\n");
}

static int get_decod_format_from_string(const char *filename)
{
    const int dot = '.';
    char * ext = strrchr(filename, dot);
    if (strcmp(ext, ".pgx") == 0) {
        return PGX_DFMT;
    }
    if (strcmp(ext, ".tif") == 0) {
        return TIF_DFMT;
    }
    if (strcmp(ext, ".ppm") == 0) {
        return PXM_DFMT;
    }
    return -1;
}


/*******************************************************************************
 * Create filenames from a filename using separator and nb components
 * (begin from 0)
 *******************************************************************************/
static char* createMultiComponentsFilename(const char* inFilename,
        const int indexF, const char* separator)
{
    char s[255];
    char *outFilename, *ptr;
    const char token = '.';
    size_t posToken = 0;
    int decod_format;

    /*printf("inFilename = %s\n", inFilename);*/
    if ((ptr = strrchr(inFilename, token)) != NULL) {
        posToken = strlen(inFilename) - strlen(ptr);
        /*printf("Position of %c character inside inFilename = %d\n", token, posToken);*/
    } else {
        /*printf("Token %c not found\n", token);*/
        outFilename = (char*)malloc(1);
        outFilename[0] = '\0';
        return outFilename;
    }

    outFilename = (char*)malloc((posToken + 7) * sizeof(char)); /*6*/

    strncpy(outFilename, inFilename, posToken);
    outFilename[posToken] = '\0';
    strcat(outFilename, separator);
    sprintf(s, "%i", indexF);
    strcat(outFilename, s);

    decod_format = get_decod_format_from_string(inFilename);
    if (decod_format == PGX_DFMT) {
        strcat(outFilename, ".pgx");
    } else if (decod_format == PXM_DFMT) {
        strcat(outFilename, ".pgm");
    }

    /*printf("outfilename: %s\n", outFilename);*/
    return outFilename;
}

/*******************************************************************************
 *
 *******************************************************************************/
static opj_image_t* readImageFromFilePPM(const char* filename,
        int nbFilenamePGX, const char *separator)
{
    int it_file;
    opj_image_t* image_read = NULL;
    opj_image_t* image = NULL;
    opj_cparameters_t parameters;
    opj_image_cmptparm_t* param_image_read;
    int** data;

    /* If separator is empty => nb file to read is equal to one*/
    if (strlen(separator) == 0) {
        nbFilenamePGX = 1;
    }

    /* set encoding parameters to default values */
    opj_set_default_encoder_parameters(&parameters);
    parameters.decod_format = PXM_DFMT;
    strcpy(parameters.infile, filename);

    /* Allocate memory*/
    param_image_read = malloc((size_t)nbFilenamePGX * sizeof(opj_image_cmptparm_t));
    data = malloc((size_t)nbFilenamePGX * sizeof(*data));

    for (it_file = 0; it_file < nbFilenamePGX; it_file++) {
        /* Create the right filename*/
        char *filenameComponentPGX;
        if (strlen(separator) == 0) {
            filenameComponentPGX = malloc((strlen(filename) + 1) * sizeof(
                                              *filenameComponentPGX));
            strcpy(filenameComponentPGX, filename);
        } else {
            filenameComponentPGX = createMultiComponentsFilename(filename, it_file,
                                   separator);
        }

        /* Read the tif file corresponding to the component */
        image_read = pnmtoimage(filenameComponentPGX, &parameters);
        if (!image_read) {
            int it_free_data;
            fprintf(stderr, "Unable to load ppm file: %s\n", filenameComponentPGX);

            free(param_image_read);

            for (it_free_data = 0; it_free_data < it_file; it_free_data++) {
                free(data[it_free_data]);
            }
            free(data);

            free(filenameComponentPGX);

            return NULL;
        }

        /* Set the image_read parameters*/
        param_image_read[it_file].x0 = 0;
        param_image_read[it_file].y0 = 0;
        param_image_read[it_file].dx = 0;
        param_image_read[it_file].dy = 0;
        param_image_read[it_file].h = image_read->comps->h;
        param_image_read[it_file].w = image_read->comps->w;
        param_image_read[it_file].bpp = image_read->comps->bpp;
        param_image_read[it_file].prec = image_read->comps->prec;
        param_image_read[it_file].sgnd = image_read->comps->sgnd;

        /* Copy data*/
        data[it_file] = malloc(param_image_read[it_file].h * param_image_read[it_file].w
                               * sizeof(int));
        memcpy(data[it_file], image_read->comps->data,
               image_read->comps->h * image_read->comps->w * sizeof(int));

        /* Free memory*/
        opj_image_destroy(image_read);
        free(filenameComponentPGX);
    }

    image = opj_image_create((OPJ_UINT32)nbFilenamePGX, param_image_read,
                             OPJ_CLRSPC_UNSPECIFIED);
    for (it_file = 0; it_file < nbFilenamePGX; it_file++) {
        /* Copy data into output image and free memory*/
        memcpy(image->comps[it_file].data, data[it_file],
               image->comps[it_file].h * image->comps[it_file].w * sizeof(int));
        free(data[it_file]);
    }

    /* Free memory*/
    free(param_image_read);
    free(data);

    return image;
}

static opj_image_t* readImageFromFileTIF(const char* filename,
        int nbFilenamePGX, const char *separator)
{
    opj_image_t* image_read = NULL;
    opj_cparameters_t parameters;
    (void)nbFilenamePGX;
    (void)separator;

    /* conformance test suite produce annoying warning/error:
     * TIFFReadDirectory: Warning, /.../data/baseline/conformance/jp2_1.tif: unknown field with tag 37724 (0x935c) encountered.
     * TIFFOpen: /.../data/baseline/nonregression/opj_jp2_1.tif: Cannot open.
     * On Win32 this open a message box by default, so remove it from the test suite:
     */
#ifdef OPJ_HAVE_LIBTIFF
    TIFFSetWarningHandler(NULL);
    TIFFSetErrorHandler(NULL);
#endif

    if (strlen(separator) != 0) {
        return NULL;
    }

    /* set encoding parameters to default values */
    opj_set_default_encoder_parameters(&parameters);
    parameters.decod_format = TIF_DFMT;
    strcpy(parameters.infile, filename);

    /* Read the tif file corresponding to the component */
#ifdef OPJ_HAVE_LIBTIFF
    image_read = tiftoimage(filename, &parameters);
#endif
    if (!image_read) {
        fprintf(stderr, "Unable to load TIF file\n");
        return NULL;
    }

    return image_read;
}

static opj_image_t* readImageFromFilePGX(const char* filename,
        int nbFilenamePGX, const char *separator)
{
    int it_file;
    opj_image_t* image_read = NULL;
    opj_image_t* image = NULL;
    opj_cparameters_t parameters;
    opj_image_cmptparm_t* param_image_read;
    int** data;

    /* If separator is empty => nb file to read is equal to one*/
    if (strlen(separator) == 0) {
        nbFilenamePGX = 1;
    }

    /* set encoding parameters to default values */
    opj_set_default_encoder_parameters(&parameters);
    parameters.decod_format = PGX_DFMT;
    strcpy(parameters.infile, filename);

    /* Allocate memory*/
    param_image_read = malloc((size_t)nbFilenamePGX * sizeof(opj_image_cmptparm_t));
    data = malloc((size_t)nbFilenamePGX * sizeof(*data));

    for (it_file = 0; it_file < nbFilenamePGX; it_file++) {
        /* Create the right filename*/
        char *filenameComponentPGX;
        if (strlen(separator) == 0) {
            filenameComponentPGX = malloc((strlen(filename) + 1) * sizeof(
                                              *filenameComponentPGX));
            strcpy(filenameComponentPGX, filename);
        } else {
            filenameComponentPGX = createMultiComponentsFilename(filename, it_file,
                                   separator);
        }

        /* Read the pgx file corresponding to the component */
        image_read = pgxtoimage(filenameComponentPGX, &parameters);
        if (!image_read) {
            int it_free_data;
            fprintf(stderr, "Unable to load pgx file\n");

            free(param_image_read);

            for (it_free_data = 0; it_free_data < it_file; it_free_data++) {
                free(data[it_free_data]);
            }
            free(data);

            free(filenameComponentPGX);

            return NULL;
        }

        /* Set the image_read parameters*/
        param_image_read[it_file].x0 = 0;
        param_image_read[it_file].y0 = 0;
        param_image_read[it_file].dx = 0;
        param_image_read[it_file].dy = 0;
        param_image_read[it_file].h = image_read->comps->h;
        param_image_read[it_file].w = image_read->comps->w;
        param_image_read[it_file].bpp = image_read->comps->bpp;
        param_image_read[it_file].prec = image_read->comps->prec;
        param_image_read[it_file].sgnd = image_read->comps->sgnd;

        /* Copy data*/
        data[it_file] = malloc(param_image_read[it_file].h * param_image_read[it_file].w
                               * sizeof(int));
        memcpy(data[it_file], image_read->comps->data,
               image_read->comps->h * image_read->comps->w * sizeof(int));

        /* Free memory*/
        opj_image_destroy(image_read);
        free(filenameComponentPGX);
    }

    image = opj_image_create((OPJ_UINT32)nbFilenamePGX, param_image_read,
                             OPJ_CLRSPC_UNSPECIFIED);
    for (it_file = 0; it_file < nbFilenamePGX; it_file++) {
        /* Copy data into output image and free memory*/
        memcpy(image->comps[it_file].data, data[it_file],
               image->comps[it_file].h * image->comps[it_file].w * sizeof(int));
        free(data[it_file]);
    }

    /* Free memory*/
    free(param_image_read);
    free(data);

    return image;
}

#if defined(OPJ_HAVE_LIBPNG) && 0 /* remove for now */
/*******************************************************************************
 *
 *******************************************************************************/
static int imageToPNG(const opj_image_t* image, const char* filename,
                      int num_comp_select)
{
    opj_image_cmptparm_t param_image_write;
    opj_image_t* image_write = NULL;

    param_image_write.x0 = 0;
    param_image_write.y0 = 0;
    param_image_write.dx = 0;
    param_image_write.dy = 0;
    param_image_write.h = image->comps[num_comp_select].h;
    param_image_write.w = image->comps[num_comp_select].w;
    param_image_write.bpp = image->comps[num_comp_select].bpp;
    param_image_write.prec = image->comps[num_comp_select].prec;
    param_image_write.sgnd = image->comps[num_comp_select].sgnd;

    image_write = opj_image_create(1u, &param_image_write, OPJ_CLRSPC_GRAY);
    memcpy(image_write->comps->data, image->comps[num_comp_select].data,
           param_image_write.h * param_image_write.w * sizeof(int));

    imagetopng(image_write, filename);

    opj_image_destroy(image_write);

    return EXIT_SUCCESS;
}
#endif

typedef struct test_cmp_parameters {
    /**  */
    char* base_filename;
    /**  */
    char* test_filename;
    /** Number of components */
    int nbcomp;
    /**  */
    double* tabMSEvalues;
    /**  */
    double* tabPEAKvalues;
    /**  */
    int nr_flag;
    /**  */
    char separator_base[2];
    /**  */
    char separator_test[2];
    /** whether to ignore prec differences */
    int ignore_prec;

} test_cmp_parameters;

/* return decode format PGX / TIF / PPM , return -1 on error */
static int get_decod_format(test_cmp_parameters* param)
{
    int base_format = get_decod_format_from_string(param->base_filename);
    int test_format = get_decod_format_from_string(param->test_filename);
    if (base_format != test_format) {
        return -1;
    }
    /* handle case -1: */
    return base_format;
}

/*******************************************************************************
 * Parse command line
 *******************************************************************************/
static int parse_cmdline_cmp(int argc, char **argv, test_cmp_parameters* param)
{
    char *MSElistvalues = NULL;
    char *PEAKlistvalues = NULL;
    char *separatorList = NULL;
    size_t sizemembasefile, sizememtestfile;
    int index, flagM = 0, flagP = 0;
    const char optlist[] = "b:t:n:m:p:s:di:";
    char* ignoreList = NULL;
    int c;

    /* Init parameters*/
    param->base_filename = NULL;
    param->test_filename = NULL;
    param->nbcomp = 0;
    param->tabMSEvalues = NULL;
    param->tabPEAKvalues = NULL;
    param->nr_flag = 0;
    param->separator_base[0] = 0;
    param->separator_test[0] = 0;
    param->ignore_prec = 0;

    opj_opterr = 0;

    while ((c = opj_getopt(argc, argv, optlist)) != -1)
        switch (c) {
        case 'b':
            sizemembasefile = strlen(opj_optarg) + 1;
            param->base_filename = (char*) malloc(sizemembasefile);
            strcpy(param->base_filename, opj_optarg);
            /*printf("param->base_filename = %s [%d / %d]\n", param->base_filename, strlen(param->base_filename), sizemembasefile );*/
            break;
        case 't':
            sizememtestfile = strlen(opj_optarg) + 1;
            param->test_filename = (char*) malloc(sizememtestfile);
            strcpy(param->test_filename, opj_optarg);
            /*printf("param->test_filename = %s [%d / %d]\n", param->test_filename, strlen(param->test_filename), sizememtestfile);*/
            break;
        case 'n':
            param->nbcomp = atoi(opj_optarg);
            break;
        case 'm':
            MSElistvalues = opj_optarg;
            flagM = 1;
            break;
        case 'p':
            PEAKlistvalues = opj_optarg;
            flagP = 1;
            break;
        case 'd':
            param->nr_flag = 1;
            break;
        case 's':
            separatorList = opj_optarg;
            break;
        case 'i':
            ignoreList = opj_optarg;
            break;
        case '?':
            if ((opj_optopt == 'b') || (opj_optopt == 't') || (opj_optopt == 'n') ||
                    (opj_optopt == 'p') || (opj_optopt == 'm') || (opj_optopt
                            == 's')) {
                fprintf(stderr, "Option -%c requires an argument.\n", opj_optopt);
            } else if (isprint(opj_optopt)) {
                fprintf(stderr, "Unknown option `-%c'.\n", opj_optopt);
            } else {
                fprintf(stderr, "Unknown option character `\\x%x'.\n", opj_optopt);
            }
            return 1;
        default:
            fprintf(stderr, "WARNING -> this option is not valid \"-%c %s\"\n", c,
                    opj_optarg);
            break;
        }

    if (opj_optind != argc) {
        for (index = opj_optind; index < argc; index++) {
            fprintf(stderr, "Non-option argument %s\n", argv[index]);
        }
        return 1;
    }

    if (param->nbcomp == 0) {
        fprintf(stderr, "Need to indicate the number of components !\n");
        return 1;
    }
    /* else */
    if (flagM && flagP) {
        param->tabMSEvalues = parseToleranceValues(MSElistvalues, param->nbcomp);
        param->tabPEAKvalues = parseToleranceValues(PEAKlistvalues, param->nbcomp);
        if ((param->tabMSEvalues == NULL) || (param->tabPEAKvalues == NULL)) {
            fprintf(stderr,
                    "MSE and PEAK values are not correct (respectively need %d values)\n",
                    param->nbcomp);
            return 1;
        }
    }

    /* Get separators after corresponding letter (b or t)*/
    if (separatorList != NULL) {
        if ((strlen(separatorList) == 2) || (strlen(separatorList) == 4)) {
            /* keep original string*/
            size_t sizeseplist = strlen(separatorList) + 1;
            char* separatorList2 = (char*)malloc(sizeseplist);
            strcpy(separatorList2, separatorList);
            /*printf("separatorList2 = %s [%d / %d]\n", separatorList2, strlen(separatorList2), sizeseplist);*/

            if (strlen(separatorList) == 2) { /* one separator behind b or t*/
                char *resultT = NULL;
                resultT = strtok(separatorList2, "t");
                if (strlen(resultT) == strlen(
                            separatorList)) { /* didn't find t character, try to find b*/
                    char *resultB = NULL;
                    resultB = strtok(resultT, "b");
                    if (strlen(resultB) == 1) {
                        param->separator_base[0] = separatorList[1];
                        param->separator_base[1] = 0;
                        param->separator_test[0] = 0;
                    } else { /* not found b*/
                        free(separatorList2);
                        return 1;
                    }
                } else { /* found t*/
                    param->separator_base[0] = 0;
                    param->separator_test[0] = separatorList[1];
                    param->separator_test[1] = 0;
                }
                /*printf("sep b = %s [%d] and sep t = %s [%d]\n",param->separator_base, strlen(param->separator_base), param->separator_test, strlen(param->separator_test) );*/
            } else { /* == 4 characters we must found t and b*/
                char *resultT = NULL;
                resultT = strtok(separatorList2, "t");
                if (strlen(resultT) == 3) { /* found t in first place*/
                    char *resultB = NULL;
                    resultB = strtok(resultT, "b");
                    if (strlen(resultB) == 1) { /* found b after t*/
                        param->separator_test[0] = separatorList[1];
                        param->separator_test[1] = 0;
                        param->separator_base[0] = separatorList[3];
                        param->separator_base[1] = 0;
                    } else { /* didn't find b after t*/
                        free(separatorList2);
                        return 1;
                    }
                } else { /* == 2, didn't find t in first place*/
                    char *resultB = NULL;
                    resultB = strtok(resultT, "b");
                    if (strlen(resultB) == 1) { /* found b in first place*/
                        param->separator_base[0] = separatorList[1];
                        param->separator_base[1] = 0;
                        param->separator_test[0] = separatorList[3];
                        param->separator_test[1] = 0;
                    } else { /* didn't found b in first place => problem*/
                        free(separatorList2);
                        return 1;
                    }
                }
            }
            free(separatorList2);
        } else { /* wrong number of argument after -s*/
            return 1;
        }
    } else {
        if (param->nbcomp == 1) {
            assert(param->separator_base[0] == 0);
            assert(param->separator_test[0] == 0);
        } else {
            fprintf(stderr, "If number of component is > 1, we need separator\n");
            return 1;
        }
    }

    if (ignoreList != NULL) {
        if (strcmp(ignoreList, "prec") == 0) {
            param->ignore_prec = 1;
        } else {
            fprintf(stderr, "Unsupported value for -i\n");
            return 1;
        }
    }

    if ((param->nr_flag) && (flagP || flagM)) {
        fprintf(stderr,
                "Wrong input parameters list: it is non-regression test or tolerance comparison\n");
        return 1;
    }
    if ((!param->nr_flag) && (!flagP || !flagM)) {
        fprintf(stderr,
                "Wrong input parameters list: it is non-regression test or tolerance comparison\n");
        return 1;
    }

    return 0;
}

/*******************************************************************************
 * MAIN
 *******************************************************************************/
int main(int argc, char **argv)
{
    test_cmp_parameters inParam;
    OPJ_UINT32 it_comp, itpxl;
    int failed = 1;
    int nbFilenamePGXbase = 0, nbFilenamePGXtest = 0;
    char *filenamePNGtest = NULL, *filenamePNGbase = NULL, *filenamePNGdiff = NULL;
    size_t memsizebasefilename, memsizetestfilename;
    size_t memsizedifffilename;
    int nbPixelDiff = 0;
    double sumDiff = 0.0;
    /* Structures to store image parameters and data*/
    opj_image_t *imageBase = NULL, *imageTest = NULL, *imageDiff = NULL;
    opj_image_cmptparm_t* param_image_diff = NULL;
    int decod_format;

    /* Get parameters from command line*/
    if (parse_cmdline_cmp(argc, argv, &inParam)) {
        compare_images_help_display();
        goto cleanup;
    }

    /* Display Parameters*/
    printf("******Parameters********* \n");
    printf(" base_filename = %s\n"
           " test_filename = %s\n"
           " nb of Components = %d\n"
           " Non regression test = %d\n"
           " separator Base = %s\n"
           " separator Test = %s\n",
           inParam.base_filename, inParam.test_filename, inParam.nbcomp,
           inParam.nr_flag, inParam.separator_base, inParam.separator_test);

    if ((inParam.tabMSEvalues != NULL) && (inParam.tabPEAKvalues != NULL)) {
        int it_comp2;
        printf(" MSE values = [");
        for (it_comp2 = 0; it_comp2 < inParam.nbcomp; it_comp2++) {
            printf(" %f ", inParam.tabMSEvalues[it_comp2]);
        }
        printf("]\n");
        printf(" PEAK values = [");
        for (it_comp2 = 0; it_comp2 < inParam.nbcomp; it_comp2++) {
            printf(" %f ", inParam.tabPEAKvalues[it_comp2]);
        }
        printf("]\n");
        printf(" Non-regression test = %d\n", inParam.nr_flag);
    }

    if (strlen(inParam.separator_base) != 0) {
        nbFilenamePGXbase = inParam.nbcomp;
    }

    if (strlen(inParam.separator_test) != 0) {
        nbFilenamePGXtest = inParam.nbcomp;
    }

    printf(" NbFilename to generate from base filename = %d\n", nbFilenamePGXbase);
    printf(" NbFilename to generate from test filename = %d\n", nbFilenamePGXtest);
    printf("************************* \n");

    /*----------BASELINE IMAGE--------*/
    memsizebasefilename = strlen(inParam.test_filename) + 1 + 5 + 2 + 4;
    memsizetestfilename = strlen(inParam.test_filename) + 1 + 5 + 2 + 4;

    decod_format = get_decod_format(&inParam);
    if (decod_format == -1) {
        fprintf(stderr, "Unhandled file format\n");
        goto cleanup;
    }
    assert(decod_format == PGX_DFMT || decod_format == TIF_DFMT ||
           decod_format == PXM_DFMT);

    if (decod_format == PGX_DFMT) {
        imageBase = readImageFromFilePGX(inParam.base_filename, nbFilenamePGXbase,
                                         inParam.separator_base);
        if (imageBase == NULL) {
            goto cleanup;
        }
    } else if (decod_format == TIF_DFMT) {
        imageBase = readImageFromFileTIF(inParam.base_filename, nbFilenamePGXbase, "");
        if (imageBase == NULL) {
            goto cleanup;
        }
    } else if (decod_format == PXM_DFMT) {
        imageBase = readImageFromFilePPM(inParam.base_filename, nbFilenamePGXbase,
                                         inParam.separator_base);
        if (imageBase == NULL) {
            goto cleanup;
        }
    }

    filenamePNGbase = (char*) malloc(memsizebasefilename);
    strcpy(filenamePNGbase, inParam.test_filename);
    strcat(filenamePNGbase, ".base");
    /*printf("filenamePNGbase = %s [%d / %d octets]\n",filenamePNGbase, strlen(filenamePNGbase),memsizebasefilename );*/

    /*----------TEST IMAGE--------*/

    if (decod_format == PGX_DFMT) {
        imageTest = readImageFromFilePGX(inParam.test_filename, nbFilenamePGXtest,
                                         inParam.separator_test);
        if (imageTest == NULL) {
            goto cleanup;
        }
    } else if (decod_format == TIF_DFMT) {
        imageTest = readImageFromFileTIF(inParam.test_filename, nbFilenamePGXtest, "");
        if (imageTest == NULL) {
            goto cleanup;
        }
    } else if (decod_format == PXM_DFMT) {
        imageTest = readImageFromFilePPM(inParam.test_filename, nbFilenamePGXtest,
                                         inParam.separator_test);
        if (imageTest == NULL) {
            goto cleanup;
        }
    }

    filenamePNGtest = (char*) malloc(memsizetestfilename);
    strcpy(filenamePNGtest, inParam.test_filename);
    strcat(filenamePNGtest, ".test");
    /*printf("filenamePNGtest = %s [%d / %d octets]\n",filenamePNGtest, strlen(filenamePNGtest),memsizetestfilename );*/

    /*----------DIFF IMAGE--------*/

    /* Allocate memory*/
    param_image_diff = malloc(imageBase->numcomps * sizeof(opj_image_cmptparm_t));

    /* Comparison of header parameters*/
    printf("Step 1 -> Header comparison\n");

    /* check dimensions (issue 286)*/
    if (imageBase->numcomps != imageTest->numcomps) {
        printf("ERROR: dim mismatch (%d><%d)\n", imageBase->numcomps,
               imageTest->numcomps);
        goto cleanup;
    }

    for (it_comp = 0; it_comp < imageBase->numcomps; it_comp++) {
        param_image_diff[it_comp].x0 = 0;
        param_image_diff[it_comp].y0 = 0;
        param_image_diff[it_comp].dx = 0;
        param_image_diff[it_comp].dy = 0;
        param_image_diff[it_comp].sgnd = 0;
        param_image_diff[it_comp].prec = 8;
        param_image_diff[it_comp].bpp = 1;
        param_image_diff[it_comp].h = imageBase->comps[it_comp].h;
        param_image_diff[it_comp].w = imageBase->comps[it_comp].w;

        if (imageBase->comps[it_comp].sgnd != imageTest->comps[it_comp].sgnd) {
            printf("ERROR: sign mismatch [comp %d] (%d><%d)\n", it_comp,
                   ((imageBase->comps)[it_comp]).sgnd, ((imageTest->comps)[it_comp]).sgnd);
            goto cleanup;
        }

        if (((imageBase->comps)[it_comp]).prec != ((imageTest->comps)[it_comp]).prec &&
                !inParam.ignore_prec) {
            printf("ERROR: prec mismatch [comp %d] (%d><%d)\n", it_comp,
                   ((imageBase->comps)[it_comp]).prec, ((imageTest->comps)[it_comp]).prec);
            goto cleanup;
        }

        if (((imageBase->comps)[it_comp]).bpp != ((imageTest->comps)[it_comp]).bpp &&
                !inParam.ignore_prec) {
            printf("ERROR: bit per pixel mismatch [comp %d] (%d><%d)\n", it_comp,
                   ((imageBase->comps)[it_comp]).bpp, ((imageTest->comps)[it_comp]).bpp);
            goto cleanup;
        }

        if (((imageBase->comps)[it_comp]).h != ((imageTest->comps)[it_comp]).h) {
            printf("ERROR: height mismatch [comp %d] (%d><%d)\n", it_comp,
                   ((imageBase->comps)[it_comp]).h, ((imageTest->comps)[it_comp]).h);
            goto cleanup;
        }

        if (((imageBase->comps)[it_comp]).w != ((imageTest->comps)[it_comp]).w) {
            printf("ERROR: width mismatch [comp %d] (%d><%d)\n", it_comp,
                   ((imageBase->comps)[it_comp]).w, ((imageTest->comps)[it_comp]).w);
            goto cleanup;
        }
    }

    imageDiff = opj_image_create(imageBase->numcomps, param_image_diff,
                                 OPJ_CLRSPC_UNSPECIFIED);
    /* Free memory*/
    free(param_image_diff);
    param_image_diff = NULL;

    /* Measurement computation*/
    printf("Step 2 -> measurement comparison\n");

    memsizedifffilename = strlen(inParam.test_filename) + 1 + 5 + 2 + 4;
    filenamePNGdiff = (char*) malloc(memsizedifffilename);
    strcpy(filenamePNGdiff, inParam.test_filename);
    strcat(filenamePNGdiff, ".diff");
    /*printf("filenamePNGdiff = %s [%d / %d octets]\n",filenamePNGdiff, strlen(filenamePNGdiff),memsizedifffilename );*/

    /* Compute pixel diff*/
    failed = 0;
    for (it_comp = 0; it_comp < imageDiff->numcomps; it_comp++) {
        double SE = 0, PEAK = 0;
        double MSE = 0;
        unsigned right_shift_input = 0;
        unsigned right_shift_output = 0;
        if (((imageBase->comps)[it_comp]).bpp > ((imageTest->comps)[it_comp]).bpp) {
            right_shift_input = ((imageBase->comps)[it_comp]).bpp - ((
                                    imageTest->comps)[it_comp]).bpp;
        } else {
            right_shift_output = ((imageTest->comps)[it_comp]).bpp - ((
                                     imageBase->comps)[it_comp]).bpp;
        }
        for (itpxl = 0;
                itpxl < ((imageDiff->comps)[it_comp]).w * ((imageDiff->comps)[it_comp]).h;
                itpxl++) {
            int valueDiff = (((imageBase->comps)[it_comp]).data[itpxl] >> right_shift_input)
                            - (((imageTest->comps)[it_comp]).data[itpxl] >> right_shift_output);
            if (valueDiff != 0) {
                ((imageDiff->comps)[it_comp]).data[itpxl] = abs(valueDiff);
                sumDiff += valueDiff;
                nbPixelDiff++;

                SE += (double)valueDiff * valueDiff;
                PEAK = (PEAK > abs(valueDiff)) ? PEAK : abs(valueDiff);
            } else {
                ((imageDiff->comps)[it_comp]).data[itpxl] = 0;
            }
        }/* h*w loop */

        MSE = SE / (((imageDiff->comps)[it_comp]).w * ((imageDiff->comps)[it_comp]).h);

        if (!inParam.nr_flag && (inParam.tabMSEvalues != NULL) &&
                (inParam.tabPEAKvalues != NULL)) {
            /* Conformance test*/
            printf("<DartMeasurement name=\"PEAK_%d\" type=\"numeric/double\"> %f </DartMeasurement> \n",
                   it_comp, PEAK);
            printf("<DartMeasurement name=\"MSE_%d\" type=\"numeric/double\"> %f </DartMeasurement> \n",
                   it_comp, MSE);

            if ((MSE > inParam.tabMSEvalues[it_comp]) ||
                    (PEAK > inParam.tabPEAKvalues[it_comp])) {
                printf("ERROR: MSE (%f) or PEAK (%f) values produced by the decoded file are greater "
                       "than the allowable error (respectively %f and %f) \n",
                       MSE, PEAK, inParam.tabMSEvalues[it_comp], inParam.tabPEAKvalues[it_comp]);
                failed = 1;
            }
        } else { /* Non regression-test */
            if (nbPixelDiff > 0) {
                char it_compc[255];
                it_compc[0] = 0;

                printf("<DartMeasurement name=\"NumberOfPixelsWithDifferences_%d\" type=\"numeric/int\"> %d </DartMeasurement> \n",
                       it_comp, nbPixelDiff);
                printf("<DartMeasurement name=\"ComponentError_%d\" type=\"numeric/double\"> %f </DartMeasurement> \n",
                       it_comp, sumDiff);
                printf("<DartMeasurement name=\"PEAK_%d\" type=\"numeric/double\"> %f </DartMeasurement> \n",
                       it_comp, PEAK);
                printf("<DartMeasurement name=\"MSE_%d\" type=\"numeric/double\"> %f </DartMeasurement> \n",
                       it_comp, MSE);

#ifdef OPJ_HAVE_LIBPNG
                {
                    char *filenamePNGbase_it_comp, *filenamePNGtest_it_comp,
                         *filenamePNGdiff_it_comp;

                    filenamePNGbase_it_comp = (char*) malloc(memsizebasefilename);
                    strcpy(filenamePNGbase_it_comp, filenamePNGbase);

                    filenamePNGtest_it_comp = (char*) malloc(memsizetestfilename);
                    strcpy(filenamePNGtest_it_comp, filenamePNGtest);

                    filenamePNGdiff_it_comp = (char*) malloc(memsizedifffilename);
                    strcpy(filenamePNGdiff_it_comp, filenamePNGdiff);

                    sprintf(it_compc, "_%i", it_comp);
                    strcat(it_compc, ".png");
                    strcat(filenamePNGbase_it_comp, it_compc);
                    /*printf("filenamePNGbase_it = %s [%d / %d octets]\n",filenamePNGbase_it_comp, strlen(filenamePNGbase_it_comp),memsizebasefilename );*/
                    strcat(filenamePNGtest_it_comp, it_compc);
                    /*printf("filenamePNGtest_it = %s [%d / %d octets]\n",filenamePNGtest_it_comp, strlen(filenamePNGtest_it_comp),memsizetestfilename );*/
                    strcat(filenamePNGdiff_it_comp, it_compc);
                    /*printf("filenamePNGdiff_it = %s [%d / %d octets]\n",filenamePNGdiff_it_comp, strlen(filenamePNGdiff_it_comp),memsizedifffilename );*/

                    /*
                    if ( imageToPNG(imageBase, filenamePNGbase_it_comp, it_comp) == EXIT_SUCCESS )
                    {
                    printf("<DartMeasurementFile name=\"BaselineImage_%d\" type=\"image/png\"> %s </DartMeasurementFile> \n", it_comp, filenamePNGbase_it_comp);
                    }

                    if ( imageToPNG(imageTest, filenamePNGtest_it_comp, it_comp) == EXIT_SUCCESS )
                    {
                    printf("<DartMeasurementFile name=\"TestImage_%d\" type=\"image/png\"> %s </DartMeasurementFile> \n", it_comp, filenamePNGtest_it_comp);
                    }

                    if ( imageToPNG(imageDiff, filenamePNGdiff_it_comp, it_comp) == EXIT_SUCCESS )
                    {
                    printf("<DartMeasurementFile name=\"DiffferenceImage_%d\" type=\"image/png\"> %s </DartMeasurementFile> \n", it_comp, filenamePNGdiff_it_comp);
                    }
                     */

                    free(filenamePNGbase_it_comp);
                    free(filenamePNGtest_it_comp);
                    free(filenamePNGdiff_it_comp);
                }
#endif
                failed = 1;
                goto cleanup;
            }
        }
    } /* it_comp loop */

    if (!failed) {
        printf("---- TEST SUCCEED ----\n");
    }
cleanup:
    /*-----------------------------*/
    free(param_image_diff);
    /* Free memory */
    opj_image_destroy(imageBase);
    opj_image_destroy(imageTest);
    opj_image_destroy(imageDiff);

    free(filenamePNGbase);
    free(filenamePNGtest);
    free(filenamePNGdiff);

    free(inParam.tabMSEvalues);
    free(inParam.tabPEAKvalues);
    free(inParam.base_filename);
    free(inParam.test_filename);

    return failed ? EXIT_FAILURE : EXIT_SUCCESS;
}
