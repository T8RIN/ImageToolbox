/*
 * Copyright (c) 2011-2012, Centre National d'Etudes Spatiales (CNES), France 
 * Copyright (c) 2012, CS Systemes d'Information, France
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
#include "opj_config.h"

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <math.h>

#ifdef _WIN32
#include <windows.h>
#else
#include <strings.h>
#define _stricmp strcasecmp
#define _strnicmp strncasecmp
#endif /* _WIN32 */

#include "openjpeg.h"
#include "format_defs.h"

/* -------------------------------------------------------------------------- */
static int get_file_format(const char *filename) {
	unsigned int i;
	static const char *extension[] = {"pgx", "pnm", "pgm", "ppm", "bmp","tif", "raw", "tga", "png", "j2k", "jp2", "jpt", "j2c", "jpc" };
	static const int format[] = { PGX_DFMT, PXM_DFMT, PXM_DFMT, PXM_DFMT, BMP_DFMT, TIF_DFMT, RAW_DFMT, TGA_DFMT, PNG_DFMT, J2K_CFMT, JP2_CFMT, JPT_CFMT, J2K_CFMT, J2K_CFMT };
	char * ext = strrchr(filename, '.');
	if (ext == NULL)
		return -1;
	ext++;
	if(ext) {
		for(i = 0; i < sizeof(format)/sizeof(*format); i++) {
			if(_strnicmp(ext, extension[i], 3) == 0) {
				return format[i];
			}
		}
	}

	return -1;
}

/* -------------------------------------------------------------------------- */

/**
sample error callback expecting a FILE* client object
*/
static void error_callback(const char *msg, void *client_data) {
	(void)client_data;
	fprintf(stdout, "[ERROR] %s", msg);
}
/**
sample warning callback expecting a FILE* client object
*/
static void warning_callback(const char *msg, void *client_data) {
	(void)client_data;
	fprintf(stdout, "[WARNING] %s", msg);
}
/**
sample debug callback expecting no client object
*/
static void info_callback(const char *msg, void *client_data) {
	(void)client_data;
	fprintf(stdout, "[INFO] %s", msg);
}


/* -------------------------------------------------------------------------- */
#define JP2_RFC3745_MAGIC "\x00\x00\x00\x0c\x6a\x50\x20\x20\x0d\x0a\x87\x0a"
#define JP2_MAGIC "\x0d\x0a\x87\x0a"
/* position 45: "\xff\x52" */
#define J2K_CODESTREAM_MAGIC "\xff\x4f\xff\x51"

static int infile_format(const char *fname)
{
	FILE *reader;
	const char *s, *magic_s;
	int ext_format, magic_format;
	unsigned char buf[12];
	OPJ_SIZE_T l_nb_read;

	reader = fopen(fname, "rb");

	if (reader == NULL)
		return -1;

	memset(buf, 0, 12);
	l_nb_read = fread(buf, 1, 12, reader);
	fclose(reader);
	if (l_nb_read != 12)
		return -1;



	ext_format = get_file_format(fname);

	if (ext_format == JPT_CFMT)
		return JPT_CFMT;

	if (memcmp(buf, JP2_RFC3745_MAGIC, 12) == 0 || memcmp(buf, JP2_MAGIC, 4) == 0) {
		magic_format = JP2_CFMT;
		magic_s = ".jp2";
	}
	else if (memcmp(buf, J2K_CODESTREAM_MAGIC, 4) == 0) {
		magic_format = J2K_CFMT;
		magic_s = ".j2k or .jpc or .j2c";
	}
	else
		return -1;

	if (magic_format == ext_format)
		return ext_format;

	s = fname + strlen(fname) - 4;

	fputs("\n===========================================\n", stderr);
	fprintf(stderr, "The extension of this file is incorrect.\n"
					"FOUND %s. SHOULD BE %s\n", s, magic_s);
	fputs("===========================================\n", stderr);

	return magic_format;
}

/* -------------------------------------------------------------------------- */
/**
 * J2K_RANDOM_TILE_ACCESS MAIN
 */
/* -------------------------------------------------------------------------- */
int main(int argc, char **argv)
{
    OPJ_UINT32 index;
	opj_dparameters_t parameters;			/* decompression parameters */
	opj_image_t* image = NULL;
	opj_stream_t *l_stream = NULL;				/* Stream */
	opj_codec_t* l_codec = NULL;				/* Handle to a decompressor */
	opj_codestream_info_v2_t* cstr_info = NULL;

	/* Index of corner tiles */
	OPJ_UINT32 tile_ul = 0;
	OPJ_UINT32 tile_ur = 0;
	OPJ_UINT32 tile_lr = 0;
	OPJ_UINT32 tile_ll = 0;

	if (argc != 2) {
		fprintf(stderr, "Usage: %s <input_file>\n", argv[0]);
		return EXIT_FAILURE;
	}

	/* Set decoding parameters to default values */
	opj_set_default_decoder_parameters(&parameters);

	strncpy(parameters.infile, argv[1], OPJ_PATH_LEN - 1);


	/* decode the JPEG2000 stream */
	/* -------------------------- */
	parameters.decod_format = infile_format(parameters.infile);

	switch(parameters.decod_format) {
		case J2K_CFMT:	/* JPEG-2000 codestream */
		{
			/* Get a decoder handle */
			l_codec = opj_create_decompress(OPJ_CODEC_J2K);
			break;
		}
		case JP2_CFMT:	/* JPEG 2000 compressed image data */
		{
			/* Get a decoder handle */
			l_codec = opj_create_decompress(OPJ_CODEC_JP2);
			break;
		}
		case JPT_CFMT:	/* JPEG 2000, JPIP */
		{
			/* Get a decoder handle */
			l_codec = opj_create_decompress(OPJ_CODEC_JPT);
			break;
		}
		default:
			fprintf(stderr,
				"Unrecognized format for input %s [accept only *.j2k, *.jp2, *.jpc or *.jpt]\n\n",
				parameters.infile);
			return EXIT_FAILURE;
	}

	/* catch events using our callbacks and give a local context */		
	opj_set_info_handler(l_codec, info_callback,00);
	opj_set_warning_handler(l_codec, warning_callback,00);
	opj_set_error_handler(l_codec, error_callback,00);

    l_stream = opj_stream_create_default_file_stream(parameters.infile,1);
	if (!l_stream){
        fprintf(stderr, "ERROR -> failed to create the stream from the file %s\n", parameters.infile);
		return EXIT_FAILURE;
	}

	/* Setup the decoder decoding parameters using user parameters */
	if ( !opj_setup_decoder(l_codec, &parameters) ){
		fprintf(stderr, "ERROR -> j2k_dump: failed to setup the decoder\n");
		opj_stream_destroy(l_stream);
		opj_destroy_codec(l_codec);
		return EXIT_FAILURE;
	}

	/* Read the main header of the codestream and if necessary the JP2 boxes*/
	if(! opj_read_header(l_stream, l_codec, &image)){
		fprintf(stderr, "ERROR -> j2k_to_image: failed to read the header\n");
		opj_stream_destroy(l_stream);
		opj_destroy_codec(l_codec);
		opj_image_destroy(image);
		return EXIT_FAILURE;
	}

	/* Extract some info from the code stream */
	cstr_info = opj_get_cstr_info(l_codec);

	fprintf(stdout, "The file contains %dx%d tiles\n", cstr_info->tw, cstr_info->th);

	tile_ul = 0;
	tile_ur = cstr_info->tw - 1;
	tile_lr = cstr_info->tw * cstr_info->th - 1;
	tile_ll = tile_lr - cstr_info->tw;

#define TEST_TILE( tile_index ) \
	fprintf(stdout, "Decoding tile %d ...\n", tile_index); \
	if(!opj_get_decoded_tile(l_codec, l_stream, image, tile_index )){ \
		fprintf(stderr, "ERROR -> j2k_to_image: failed to decode tile %d\n", tile_index); \
		opj_stream_destroy(l_stream); \
		opj_destroy_cstr_info(&cstr_info); \
		opj_destroy_codec(l_codec); \
		opj_image_destroy(image); \
		return EXIT_FAILURE; \
	} \
  for(index = 0; index < image->numcomps; ++index) { \
    if( image->comps[index].data == NULL ){ \
    	fprintf(stderr, "ERROR -> j2k_to_image: failed to decode tile %d\n", tile_index); \
		opj_stream_destroy(l_stream); \
		opj_destroy_cstr_info(&cstr_info); \
		opj_destroy_codec(l_codec); \
		opj_image_destroy(image); \
        return EXIT_FAILURE; \
        } \
  } \
	fprintf(stdout, "Tile %d is decoded successfully\n", tile_index);

	TEST_TILE(tile_ul)
	TEST_TILE(tile_lr)
	TEST_TILE(tile_ul)
	TEST_TILE(tile_ll)
	TEST_TILE(tile_ur)
	TEST_TILE(tile_lr)

	/* Close the byte stream */
	opj_stream_destroy(l_stream);

	/* Destroy code stream info */
	opj_destroy_cstr_info(&cstr_info);

	/* Free remaining structures */
	opj_destroy_codec(l_codec);

	/* Free image data structure */
	opj_image_destroy(image);

	return EXIT_SUCCESS;
}
/*end main*/




