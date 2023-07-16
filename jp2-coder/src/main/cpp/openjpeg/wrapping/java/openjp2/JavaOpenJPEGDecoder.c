/*
 * Copyright (c) 2002-2014, Universite catholique de Louvain (UCL), Belgium
 * Copyright (c) 2002-2014, Professor Benoit Macq
 * Copyright (c) 2001-2003, David Janssens
 * Copyright (c) 2002-2003, Yannick Verschueren
 * Copyright (c) 2003-2007, Francois-Olivier Devaux 
 * Copyright (c) 2003-2014, Antonin Descampe
 * Copyright (c) 2005, Herve Drolon, FreeImage Team
 * Copyright (c) 2006-2007, Parvatha Elangovan
 * Copyright (c) 2007, Patrick Piscaglia (Telemis)
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
#include <jni.h>
#include <math.h>

#include "openjpeg.h"
#include "opj_includes.h"
#include "opj_getopt.h"
#include "convert.h"
#include "dirent.h"
#include "org_openJpeg_OpenJPEGJavaDecoder.h"

#ifndef _WIN32
#define stricmp strcasecmp
#define strnicmp strncasecmp
#endif

#include "format_defs.h"

typedef struct callback_variables {
	JNIEnv *env;
	/** 'jclass' object used to call a Java method from the C */
	jobject *jobj;
	/** 'jclass' object used to call a Java method from the C */
	jmethodID message_mid;
	jmethodID error_mid;
} callback_variables_t;

typedef struct dircnt{
	/** Buffer for holding images read from Directory*/
	char *filename_buf;
	/** Pointer to the buffer*/
	char **filename;
}dircnt_t;


typedef struct img_folder{
	/** The directory path of the folder containing input images*/
	char *imgdirpath;
	/** Output format*/
	char *out_format;
	/** Enable option*/
	char set_imgdir;
	/** Enable Cod Format for output*/
	char set_out_format;

}img_fol_t;


void decode_help_display() {
	fprintf(stdout,"HELP\n----\n\n");
	fprintf(stdout,"- the -h option displays this help information on screen\n\n");

/* UniPG>> */
	fprintf(stdout,"List of parameters for the JPEG 2000 "
#ifdef USE_JPWL
		"+ JPWL "
#endif /* USE_JPWL */
		"decoder:\n");
/* <<UniPG */
	fprintf(stdout,"\n");
	fprintf(stdout,"\n");
	fprintf(stdout,"  -ImgDir \n");
	fprintf(stdout,"	Image file Directory path \n");
	fprintf(stdout,"  -OutFor \n");
	fprintf(stdout,"    REQUIRED only if -ImgDir is used\n");
	fprintf(stdout,"	  Need to specify only format without filename <BMP>  \n");
	fprintf(stdout,"    Currently accepts PGM, PPM, PNM, PGX, BMP format\n");
	fprintf(stdout,"  -i <compressed file>\n");
	fprintf(stdout,"    REQUIRED only if an Input image directory not specified\n");
	fprintf(stdout,"    Currently accepts J2K-files, JP2-files and JPT-files. The file type\n");
	fprintf(stdout,"    is identified based on its suffix.\n");
	fprintf(stdout,"  -o <decompressed file>\n");
	fprintf(stdout,"    REQUIRED\n");
	fprintf(stdout,"    Currently accepts PGM-files, PPM-files, PNM-files, PGX-files and\n");
	fprintf(stdout,"    BMP-files. Binary data is written to the file (not ascii). If a PGX\n");
	fprintf(stdout,"    filename is given, there will be as many output files as there are\n");
	fprintf(stdout,"    components: an indice starting from 0 will then be appended to the\n");
	fprintf(stdout,"    output filename, just before the \"pgx\" extension. If a PGM filename\n");
	fprintf(stdout,"    is given and there are more than one component, only the first component\n");
	fprintf(stdout,"    will be written to the file.\n");
	fprintf(stdout,"  -r <reduce factor>\n");
	fprintf(stdout,"    Set the number of highest resolution levels to be discarded. The\n");
	fprintf(stdout,"    image resolution is effectively divided by 2 to the power of the\n");
	fprintf(stdout,"    number of discarded levels. The reduce factor is limited by the\n");
	fprintf(stdout,"    smallest total number of decomposition levels among tiles.\n");
	fprintf(stdout,"  -l <number of quality layers to decode>\n");
	fprintf(stdout,"    Set the maximum number of quality layers to decode. If there are\n");
	fprintf(stdout,"    less quality layers than the specified number, all the quality layers\n");
	fprintf(stdout,"    are decoded.\n");
/* UniPG>> */
#ifdef USE_JPWL
	fprintf(stdout,"  -W <options>\n");
	fprintf(stdout,"    Activates the JPWL correction capability, if the codestream complies.\n");
	fprintf(stdout,"    Options can be a comma separated list of <param=val> tokens:\n");
	fprintf(stdout,"    c, c=numcomps\n");
	fprintf(stdout,"       numcomps is the number of expected components in the codestream\n");
	fprintf(stdout,"       (search of first EPB rely upon this, default is %d)\n", JPWL_EXPECTED_COMPONENTS);
#endif /* USE_JPWL */
/* <<UniPG */
	fprintf(stdout,"\n");
}

/* -------------------------------------------------------------------------- */

int get_num_images(char *imgdirpath){
	DIR *dir;
	struct dirent* content;	
	int num_images = 0;

	/*Reading the input images from given input directory*/

	dir= opendir(imgdirpath);
	if(!dir){
		fprintf(stderr,"Could not open Folder %s\n",imgdirpath);
		return 0;
	}
	
	while((content=readdir(dir))!=NULL){
		if(strcmp(".",content->d_name)==0 || strcmp("..",content->d_name)==0 )
			continue;
		num_images++;
	}
	return num_images;
}

int load_images(dircnt_t *dirptr, char *imgdirpath){
	DIR *dir;
	struct dirent* content;	
	int i = 0;

	/*Reading the input images from given input directory*/

	dir= opendir(imgdirpath);
	if(!dir){
		fprintf(stderr,"Could not open Folder %s\n",imgdirpath);
		return 1;
	}else	{
		fprintf(stderr,"Folder opened successfully\n");
	}
	
	while((content=readdir(dir))!=NULL){
		if(strcmp(".",content->d_name)==0 || strcmp("..",content->d_name)==0 )
			continue;

		strcpy(dirptr->filename[i],content->d_name);
		i++;
	}
	return 0;	
}

int get_file_format(char *filename) {
	unsigned int i;
	static const char *extension[] = {"pgx", "pnm", "pgm", "ppm", "bmp","tif", "raw", "tga", "j2k", "jp2", "jpt", "j2c" };
	static const int format[] = { PGX_DFMT, PXM_DFMT, PXM_DFMT, PXM_DFMT, BMP_DFMT, TIF_DFMT, RAW_DFMT, TGA_DFMT, J2K_CFMT, JP2_CFMT, JPT_CFMT, J2K_CFMT };
	char * ext = strrchr(filename, '.');
	if (ext == NULL)
		return -1;
	ext++;
	if(ext) {
		for(i = 0; i < sizeof(format)/sizeof(*format); i++) {
			if(strnicmp(ext, extension[i], 3) == 0) {
				return format[i];
			}
		}
	}

	return -1;
}


/* -------------------------------------------------------------------------- */

int parse_cmdline_decoder(int argc, char **argv, opj_dparameters_t *parameters,img_fol_t *img_fol) {
	/* parse the command line */
	int totlen;
	opj_option_t long_option[]={
		{"ImgDir",REQ_ARG, NULL ,'y'},
		{"OutFor",REQ_ARG, NULL ,'O'},
	};

/* UniPG>> */
	const char optlist[] = "i:o:r:l:hx:"

#ifdef USE_JPWL
					"W:"
#endif /* USE_JPWL */
					;
	/*for (i=0; i<argc; i++) {
		printf("[%s]",argv[i]);
	}
	printf("\n");*/

/* <<UniPG */
	totlen=sizeof(long_option);
	img_fol->set_out_format = 0;
	reset_options_reading();

	while (1) {
		int c = opj_getopt_long(argc, argv,optlist,long_option,totlen);
		if (c == -1)
			break;
		switch (c) {
			case 'i':			/* input file */
			{
				char *infile = opj_optarg;
				parameters->decod_format = get_file_format(infile);
				switch(parameters->decod_format) {
					case J2K_CFMT:
					case JP2_CFMT:
					case JPT_CFMT:
						break;
					default:
						fprintf(stderr, 
							"!! Unrecognized format for infile : %s [accept only *.j2k, *.jp2, *.jpc or *.jpt] !!\n\n", 
							infile);
						return 1;
				}
				strncpy(parameters->infile, infile, sizeof(parameters->infile)-1);
			}
			break;
				
				/* ----------------------------------------------------- */

			case 'o':			/* output file */
			{
				char *outfile = opj_optarg;
				parameters->cod_format = get_file_format(outfile);
				switch(parameters->cod_format) {
					case PGX_DFMT:
					case PXM_DFMT:
					case BMP_DFMT:
					case TIF_DFMT:
					case RAW_DFMT:
					case TGA_DFMT:
						break;
					default:
						fprintf(stderr, "Unknown output format image %s [only *.pnm, *.pgm, *.ppm, *.pgx, *.bmp, *.tif, *.raw or *.tga]!! \n", outfile);
						return 1;
				}
				strncpy(parameters->outfile, outfile, sizeof(parameters->outfile)-1);
			}
			break;
			
				/* ----------------------------------------------------- */

			case 'O':			/* output format */
			{
				char outformat[50];
				char *of = opj_optarg;
				sprintf(outformat,".%s",of);
				img_fol->set_out_format = 1;
				parameters->cod_format = get_file_format(outformat);
				switch(parameters->cod_format) {
					case PGX_DFMT:
						img_fol->out_format = "pgx";
						break;
					case PXM_DFMT:
						img_fol->out_format = "ppm";
						break;
					case BMP_DFMT:
						img_fol->out_format = "bmp";
						break;
					case TIF_DFMT:
						img_fol->out_format = "tif";
						break;
					case RAW_DFMT:
						img_fol->out_format = "raw";
						break;
					case TGA_DFMT:
						img_fol->out_format = "raw";
						break;
					default:
						fprintf(stderr, "Unknown output format image %s [only *.pnm, *.pgm, *.ppm, *.pgx, *.bmp, *.tif, *.raw or *.tga]!! \n", outformat);
						return 1;
						break;
				}
			}
			break;

				/* ----------------------------------------------------- */


			case 'r':		/* reduce option */
			{
				sscanf(opj_optarg, "%d", &parameters->cp_reduce);
			}
			break;
			
				/* ----------------------------------------------------- */
      

			case 'l':		/* layering option */
			{
				sscanf(opj_optarg, "%d", &parameters->cp_layer);
			}
			break;
			
				/* ----------------------------------------------------- */

			case 'h': 			/* display an help description */
				decode_help_display();
				return 1;				

				/* ------------------------------------------------------ */

			case 'y':			/* Image Directory path */
				{
					img_fol->imgdirpath = (char*)opj_malloc(strlen(opj_optarg) + 1);
					strcpy(img_fol->imgdirpath,opj_optarg);
					img_fol->set_imgdir=1;
				}
				break;
				/* ----------------------------------------------------- */
/* UniPG>> */
#ifdef USE_JPWL
			
			case 'W': 			/* activate JPWL correction */
			{
				char *token = NULL;

				token = strtok(opj_optarg, ",");
				while(token != NULL) {

					/* search expected number of components */
					if (*token == 'c') {

						static int compno;

						compno = JPWL_EXPECTED_COMPONENTS; /* predefined no. of components */

						if(sscanf(token, "c=%d", &compno) == 1) {
							/* Specified */
							if ((compno < 1) || (compno > 256)) {
								fprintf(stderr, "ERROR -> invalid number of components c = %d\n", compno);
								return 1;
							}
							parameters->jpwl_exp_comps = compno;

						} else if (!strcmp(token, "c")) {
							/* default */
							parameters->jpwl_exp_comps = compno; /* auto for default size */

						} else {
							fprintf(stderr, "ERROR -> invalid components specified = %s\n", token);
							return 1;
						};
					}

					/* search maximum number of tiles */
					if (*token == 't') {

						static int tileno;

						tileno = JPWL_MAXIMUM_TILES; /* maximum no. of tiles */

						if(sscanf(token, "t=%d", &tileno) == 1) {
							/* Specified */
							if ((tileno < 1) || (tileno > JPWL_MAXIMUM_TILES)) {
								fprintf(stderr, "ERROR -> invalid number of tiles t = %d\n", tileno);
								return 1;
							}
							parameters->jpwl_max_tiles = tileno;

						} else if (!strcmp(token, "t")) {
							/* default */
							parameters->jpwl_max_tiles = tileno; /* auto for default size */

						} else {
							fprintf(stderr, "ERROR -> invalid tiles specified = %s\n", token);
							return 1;
						};
					}

					/* next token or bust */
					token = strtok(NULL, ",");
				};
				parameters->jpwl_correct = true;
				fprintf(stdout, "JPWL correction capability activated\n");
				fprintf(stdout, "- expecting %d components\n", parameters->jpwl_exp_comps);
			}
			break;	
#endif /* USE_JPWL */
/* <<UniPG */            

				/* ----------------------------------------------------- */
			
			default:
				fprintf(stderr,"WARNING -> this option is not valid \"-%c %s\"\n",c, opj_optarg);
				break;
		}
	}

	/* No check for possible errors before the -i and -o options are of course not mandatory*/

	return 0;
}

/* -------------------------------------------------------------------------- */

/**
error callback returning the message to Java andexpecting a callback_variables_t client object
*/
void error_callback(const char *msg, void *client_data) {
	callback_variables_t* vars = (callback_variables_t*) client_data;
	JNIEnv *env = vars->env;
	jstring jbuffer;

	jbuffer = (*env)->NewStringUTF(env, msg);
	(*env)->ExceptionClear(env);
	(*env)->CallVoidMethod(env, *(vars->jobj), vars->error_mid, jbuffer);

	if ((*env)->ExceptionOccurred(env)) {
		fprintf(stderr,"C: Exception during call back method\n");
		(*env)->ExceptionDescribe(env);
		(*env)->ExceptionClear(env);
	}
	(*env)->DeleteLocalRef(env, jbuffer);
}
/**
warning callback returning the message to Java andexpecting a callback_variables_t client object
*/
void warning_callback(const char *msg, void *client_data) {
	callback_variables_t* vars = (callback_variables_t*) client_data;
	JNIEnv *env = vars->env;
	jstring jbuffer;

	jbuffer = (*env)->NewStringUTF(env, msg);
	(*env)->ExceptionClear(env);
	(*env)->CallVoidMethod(env, *(vars->jobj), vars->message_mid, jbuffer);
	
	if ((*env)->ExceptionOccurred(env)) {
		fprintf(stderr,"C: Exception during call back method\n");
		(*env)->ExceptionDescribe(env);
		(*env)->ExceptionClear(env);
	}
	(*env)->DeleteLocalRef(env, jbuffer);
}
/**
information callback returning the message to Java andexpecting a callback_variables_t client object
*/
void info_callback(const char *msg, void *client_data) {
	callback_variables_t* vars = (callback_variables_t*) client_data;
	JNIEnv *env = vars->env;
	jstring jbuffer;

	jbuffer = (*env)->NewStringUTF(env, msg);
	(*env)->ExceptionClear(env);
	(*env)->CallVoidMethod(env, *(vars->jobj), vars->message_mid, jbuffer);

	if ((*env)->ExceptionOccurred(env)) {
		fprintf(stderr,"C: Exception during call back method\n");
		(*env)->ExceptionDescribe(env);
		(*env)->ExceptionClear(env);
	}
	(*env)->DeleteLocalRef(env, jbuffer);
}


/* --------------------------------------------------------------------------
   --------------------   MAIN METHOD, CALLED BY JAVA -----------------------*/
JNIEXPORT jint JNICALL Java_org_openJpeg_OpenJPEGJavaDecoder_internalDecodeJ2KtoImage(JNIEnv *env, jobject obj, jobjectArray javaParameters) {
	int argc;		/* To simulate the command line parameters (taken from the javaParameters variable) and be able to re-use the */
	char **argv;	/*  'parse_cmdline_decoder' method taken from the j2k_to_image project */
	opj_dparameters_t parameters;	/* decompression parameters */
	img_fol_t img_fol;
	opj_event_mgr_t event_mgr;		/* event manager */
	opj_image_t *image = NULL;
	FILE *fsrc = NULL;
	unsigned char *src = NULL;
	int file_length;
	int num_images;
	int i,j,imageno;
	opj_dinfo_t* dinfo = NULL;	/* handle to a decompressor */
	opj_cio_t *cio = NULL;
	int w,h;
	long min_value, max_value;
	short tempS; unsigned char tempUC, tempUC1, tempUC2;
	/* ==> Access variables to the Java member variables*/
	jsize		arraySize;
	jclass		cls;
	jobject		object;
	jboolean	isCopy;
	jfieldID	fid;
	jbyteArray	jba;
	jshortArray jsa;
	jintArray	jia;
	jbyte		*jbBody, *ptrBBody;
	jshort		*jsBody, *ptrSBody;
	jint		*jiBody, *ptrIBody;
	callback_variables_t msgErrorCallback_vars;
	/* <=== access variable to Java member variables */
	int *ptr, *ptr1, *ptr2;				/* <== To transfer the decoded image to Java*/

	/* configure the event callbacks */
	memset(&event_mgr, 0, sizeof(opj_event_mgr_t));	
	event_mgr.error_handler = error_callback;
	event_mgr.warning_handler = warning_callback;
	event_mgr.info_handler = info_callback;

	/* JNI reference to the calling class*/
	cls = (*env)->GetObjectClass(env, obj);

	/* Pointers to be able to call a Java method for all the info and error messages*/
	msgErrorCallback_vars.env = env;
	msgErrorCallback_vars.jobj = &obj;
	msgErrorCallback_vars.message_mid = (*env)->GetMethodID(env, cls, "logMessage", "(Ljava/lang/String;)V");
	msgErrorCallback_vars.error_mid = (*env)->GetMethodID(env, cls, "logError", "(Ljava/lang/String;)V");

	/* Get the String[] containing the parameters, and converts it into a char** to simulate command line arguments.*/
	arraySize = (*env)->GetArrayLength(env, javaParameters);
	argc = (int) arraySize +1;
	argv = opj_malloc(argc*sizeof(char*));
	argv[0] = "ProgramName.exe";	/* The program name: useless*/
	j=0;
	for (i=1; i<argc; i++) {
		object = (*env)->GetObjectArrayElement(env, javaParameters, i-1);
		argv[i] = (char*)(*env)->GetStringUTFChars(env, object, &isCopy);
	}

	/*printf("C: decoder params = ");
	for (i=0; i<argc; i++) {
		printf("[%s]",argv[i]);
	}
	printf("\n");*/

	/* set decoding parameters to default values */
	opj_set_default_decoder_parameters(&parameters);
	parameters.decod_format = J2K_CFMT;

	/* parse input and get user encoding parameters */
	if(parse_cmdline_decoder(argc, argv, &parameters,&img_fol) == 1) {
		/* Release the Java arguments array*/
		for (i=1; i<argc; i++)
			(*env)->ReleaseStringUTFChars(env, (*env)->GetObjectArrayElement(env, javaParameters, i-1), argv[i]);
		return -1;
	}
	/* Release the Java arguments array*/
	for (i=1; i<argc; i++)
		(*env)->ReleaseStringUTFChars(env, (*env)->GetObjectArrayElement(env, javaParameters, i-1), argv[i]);

	num_images=1;

	/* Get additional information from the Java object variables*/
	fid = (*env)->GetFieldID(env, cls,"skippedResolutions", "I");
	parameters.cp_reduce = (short) (*env)->GetIntField(env, obj, fid);

	/*Decoding image one by one*/
	for(imageno = 0; imageno < num_images ; imageno++)
	{
		image = NULL;
		fprintf(stderr,"\n");

		/* read the input file and put it in memory into the 'src' object, if the -i option is given in JavaParameters.
		   Implemented for debug purpose. */
		/* -------------------------------------------------------------- */
		if (parameters.infile && parameters.infile[0]!='\0') {
			/*printf("C: opening [%s]\n", parameters.infile);*/
			fsrc = fopen(parameters.infile, "rb");
			if (!fsrc) {
				fprintf(stderr, "ERROR -> failed to open %s for reading\n", parameters.infile);
				return 1;
			}
			fseek(fsrc, 0, SEEK_END);
			file_length = ftell(fsrc);
			fseek(fsrc, 0, SEEK_SET);
			src = (unsigned char *) opj_malloc(file_length);
			fread(src, 1, file_length, fsrc);
			fclose(fsrc);
			/*printf("C: %d bytes read from file\n",file_length);*/
		} else {
			/* Preparing the transfer of the codestream from Java to C*/
			/*printf("C: before transferring codestream\n");*/
			fid = (*env)->GetFieldID(env, cls,"compressedStream", "[B");
			jba = (*env)->GetObjectField(env, obj, fid);
			file_length = (*env)->GetArrayLength(env, jba);
			jbBody = (*env)->GetByteArrayElements(env, jba, &isCopy);
			src = (unsigned char*)jbBody;
		}

		/* decode the code-stream */
		/* ---------------------- */

		switch(parameters.decod_format) {
		case J2K_CFMT:
		{
			/* JPEG-2000 codestream */

			/* get a decoder handle */
			dinfo = opj_create_decompress(CODEC_J2K);

			/* catch events using our callbacks and give a local context */
			opj_set_event_mgr((opj_common_ptr)dinfo, &event_mgr, &msgErrorCallback_vars);

			/* setup the decoder decoding parameters using user parameters */
			opj_setup_decoder(dinfo, &parameters);

			/* open a byte stream */
			cio = opj_cio_open((opj_common_ptr)dinfo, src, file_length);

			/* decode the stream and fill the image structure */
			image = opj_decode(dinfo, cio);
			if(!image) {
				fprintf(stderr, "ERROR -> j2k_to_image: failed to decode image!\n");
				opj_destroy_decompress(dinfo);
				opj_cio_close(cio);
				return 1;
			}

			/* close the byte stream */
			opj_cio_close(cio);
		}
		break;

		case JP2_CFMT:
		{
			/* JPEG 2000 compressed image data */

			/* get a decoder handle */
			dinfo = opj_create_decompress(CODEC_JP2);

			/* catch events using our callbacks and give a local context */
			opj_set_event_mgr((opj_common_ptr)dinfo, &event_mgr, &msgErrorCallback_vars);

			/* setup the decoder decoding parameters using the current image and user parameters */
			opj_setup_decoder(dinfo, &parameters);

			/* open a byte stream */
			cio = opj_cio_open((opj_common_ptr)dinfo, src, file_length);

			/* decode the stream and fill the image structure */
			image = opj_decode(dinfo, cio);
			if(!image) {
				fprintf(stderr, "ERROR -> j2k_to_image: failed to decode image!\n");
				opj_destroy_decompress(dinfo);
				opj_cio_close(cio);
				return 1;
			}

			/* close the byte stream */
			opj_cio_close(cio);

		}
		break;

		case JPT_CFMT:
		{
			/* JPEG 2000, JPIP */

			/* get a decoder handle */
			dinfo = opj_create_decompress(CODEC_JPT);

			/* catch events using our callbacks and give a local context */
			opj_set_event_mgr((opj_common_ptr)dinfo, &event_mgr, &msgErrorCallback_vars);

			/* setup the decoder decoding parameters using user parameters */
			opj_setup_decoder(dinfo, &parameters);

			/* open a byte stream */
			cio = opj_cio_open((opj_common_ptr)dinfo, src, file_length);

			/* decode the stream and fill the image structure */
			image = opj_decode(dinfo, cio);
			if(!image) {
				fprintf(stderr, "ERROR -> j2k_to_image: failed to decode image!\n");
				opj_destroy_decompress(dinfo);
				opj_cio_close(cio);
				return 1;
			}

			/* close the byte stream */
			opj_cio_close(cio);
		}
		break;

		default:
			fprintf(stderr, "skipping file..\n");
			continue;
	}

		/* free the memory containing the code-stream */
		if (parameters.infile && parameters.infile[0]!='\0') {
			opj_free(src);
		} else {
			(*env)->ReleaseByteArrayElements(env, jba, jbBody, 0);
		}
		src = NULL;

		/* create output image.
			If the -o parameter is given in the JavaParameters, write the decoded version into a file.
			Implemented for debug purpose. */
		/* ---------------------------------- */
		switch (parameters.cod_format) {
		case PXM_DFMT:			/* PNM PGM PPM */
			if (imagetopnm(image, parameters.outfile)) {
				fprintf(stdout,"Outfile %s not generated\n",parameters.outfile);
			}
			else {
				fprintf(stdout,"Generated Outfile %s\n",parameters.outfile);
			}
			break;

		case PGX_DFMT:			/* PGX */
			if(imagetopgx(image, parameters.outfile)){
				fprintf(stdout,"Outfile %s not generated\n",parameters.outfile);
			}
			else {
				fprintf(stdout,"Generated Outfile %s\n",parameters.outfile);
			}
			break;

		case BMP_DFMT:			/* BMP */
			if(imagetobmp(image, parameters.outfile)){
				fprintf(stdout,"Outfile %s not generated\n",parameters.outfile);
			}
			else {
				fprintf(stdout,"Generated Outfile %s\n",parameters.outfile);
			}
			break;

		}

		/* ========= Return the image to the Java structure ===============*/
#ifdef CHECK_THRESHOLDS
		printf("C: checking thresholds\n");
#endif
		/* First compute the real with and height, in function of the resolutions decoded.*/
		/*wr = (image->comps[0].w + (1 << image->comps[0].factor) -1) >> image->comps[0].factor;*/
		/*hr = (image->comps[0].h + (1 << image->comps[0].factor) -1) >> image->comps[0].factor;*/
		w = image->comps[0].w;
		h = image->comps[0].h;

		if (image->numcomps==3) {	/* 3 components color image*/
			ptr = image->comps[0].data;
			ptr1 = image->comps[1].data;
			ptr2 = image->comps[2].data;
#ifdef CHECK_THRESHOLDS 
			if (image->comps[0].sgnd) {
				min_value = -128;
				max_value = 127;
			} else {
				min_value = 0;
				max_value = 255;
			}
#endif			
			/* Get the pointer to the Java structure where the data must be copied*/
			fid = (*env)->GetFieldID(env, cls,"image24", "[I");
			jia = (*env)->GetObjectField(env, obj, fid);
			jiBody = (*env)->GetIntArrayElements(env, jia, 0);
			ptrIBody = jiBody;
			printf("C: transferring image24: %d int to Java pointer=%d\n",image->numcomps*w*h, ptrIBody);

			for (i=0; i<w*h; i++) {
				tempUC = (unsigned char)(ptr[i]);
				tempUC1 = (unsigned char)(ptr1[i]);
				tempUC2 = (unsigned char)(ptr2[i]);
#ifdef CHECK_THRESHOLDS
				if (tempUC < min_value)
					tempUC=min_value;
				else if (tempUC > max_value)
					tempUC=max_value;
				if (tempUC1 < min_value)
					tempUC1=min_value;
				else if (tempUC1 > max_value)
					tempUC1=max_value;
				if (tempUC2 < min_value)
					tempUC2=min_value;
				else if (tempUC2 > max_value)
					tempUC2=max_value;
#endif
				*(ptrIBody++)  = (int) ( (tempUC2<<16) + (tempUC1<<8) + tempUC );
			}
			(*env)->ReleaseIntArrayElements(env, jia, jiBody, 0);

		} else {	/* 1 component 8 or 16 bpp image*/
			ptr = image->comps[0].data;
			printf("C: before transferring a %d bpp image to java (length = %d)\n",image->comps[0].prec ,w*h);
			if (image->comps[0].prec<=8) {
				fid = (*env)->GetFieldID(env, cls,"image8", "[B");
				jba = (*env)->GetObjectField(env, obj, fid);
				jbBody = (*env)->GetByteArrayElements(env, jba, 0);
				ptrBBody = jbBody;
#ifdef CHECK_THRESHOLDS 
				if (image->comps[0].sgnd) {
					min_value = -128;
					max_value = 127;
				} else {
					min_value = 0;
					max_value = 255;
				}
#endif								
				/*printf("C: transferring %d shorts to Java image8 pointer = %d\n", wr*hr,ptrSBody);*/
				for (i=0; i<w*h; i++) {
					tempUC = (unsigned char) (ptr[i]);
#ifdef CHECK_THRESHOLDS
					if (tempUC<min_value)
						tempUC = min_value;
					else if (tempUC > max_value)
						tempUC = max_value;
#endif
					*(ptrBBody++) = tempUC;
				}
				(*env)->ReleaseByteArrayElements(env, jba, jbBody, 0);
				printf("C: image8 transferred to Java\n");
			} else {
				fid = (*env)->GetFieldID(env, cls,"image16", "[S");
				jsa = (*env)->GetObjectField(env, obj, fid);
				jsBody = (*env)->GetShortArrayElements(env, jsa, 0);
				ptrSBody = jsBody;
#ifdef CHECK_THRESHOLDS 
				if (image->comps[0].sgnd) {
					min_value = -32768;
					max_value = 32767;
				} else {
					min_value = 0;
					max_value = 65535;
				}
				printf("C: minValue = %d, maxValue = %d\n", min_value, max_value);
#endif				
				printf("C: transferring %d shorts to Java image16 pointer = %d\n", w*h,ptrSBody);
				for (i=0; i<w*h; i++) {
					tempS = (short) (ptr[i]);
#ifdef CHECK_THRESHOLDS
					if (tempS<min_value) {
						printf("C: value %d truncated to %d\n", tempS, min_value);
						tempS = min_value;
					} else if (tempS > max_value) {
						printf("C: value %d truncated to %d\n", tempS, max_value);
						tempS = max_value;
					}
#endif
					*(ptrSBody++) = tempS;
				}
				(*env)->ReleaseShortArrayElements(env, jsa, jsBody, 0);
				printf("C: image16 completely filled\n");
			}
		}	


		/* free remaining structures */
		if(dinfo) {
			opj_destroy_decompress(dinfo);
		}
		/* free image data structure */
		opj_image_destroy(image);

	}
	return 1; /* OK */
}
/*end main*/

