// based on openjpeg-2.1.1/jni/openjpeg/src/bin/jp2/opj_decompress.c and opj_compress.c

#include "opj_config.h"
#include <jni.h> 
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "openjpeg.h"
typedef unsigned int OPJ_BITFIELD;
#include "event.h"
#include "function_list.h"
#include "thread.h"
#include "cio.h"
#include "j2k.h"
#include "jp2.h"
#include "opj_codec.h"

#include <android/log.h>

#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, "OpenJPEG",__VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG  , "OpenJPEG",__VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO   , "OpenJPEG",__VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN   , "OpenJPEG",__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR  , "OpenJPEG",__VA_ARGS__)

#ifdef __cplusplus
extern "C" {
#endif

#include "color.h"

#define JP2_RFC3745_MAGIC "\x00\x00\x00\x0c\x6a\x50\x20\x20\x0d\x0a\x87\x0a"
#define JP2_MAGIC "\x0d\x0a\x87\x0a"
/* position 45: "\xff\x52" */
#define J2K_CODESTREAM_MAGIC "\xff\x4f\xff\x51"

#define MIN(a,b) ((a) > (b) ? (b) : (a))
#define MAX(a,b) ((a) < (b) ? (b) : (a))

//stores decoded image data
typedef struct image_data {
    jint width;
    jint height;
    jint hasAlpha; //0 = false; 1 = true
    int* pixels;
} image_data_t;

//stores decoded image header
typedef struct image_header {
    jint width;
    jint height;
    jint hasAlpha; //0 = false; 1 = true
    jint numResolutions;
    jint numQualityLayers;
} image_header_t;

#define J2K_CFMT 0
#define JP2_CFMT 1

#define EXIT_SUCCESS 0
#define EXIT_FAILURE 1

int get_file_format(const char *filename) {
    unsigned int i;
    static const char *extension[] = {"j2k", "jp2", "j2c", "jpc" };
    static const int format[] = { J2K_CFMT, JP2_CFMT, J2K_CFMT, J2K_CFMT };
    const char * ext = strrchr(filename, '.');
    if (ext == NULL)
        return -1;
    ext++;
    if(ext) {
        for(i = 0; i < sizeof(format)/sizeof(*format); i++) {
            if(strcasecmp(ext, extension[i]) == 0) {
                return format[i];
            }
        }
    }

    return -1;
}

static int get_magic_format(char *buf) {
    int magic_format;
    if (memcmp(buf, JP2_RFC3745_MAGIC, 12) == 0 || memcmp(buf, JP2_MAGIC, 4) == 0) {
        magic_format = JP2_CFMT;
    }
    else if (memcmp(buf, J2K_CODESTREAM_MAGIC, 4) == 0) {
        magic_format = J2K_CFMT;
    } else {
        return -1;
    }
    return magic_format;
}

static int infile_format(const char *fname)
{
    FILE *reader;
    const char *s, *magic_s;
    int ext_format, magic_format;
    unsigned char buf[12];
    unsigned int l_nb_read;

    reader = fopen(fname, "rb");

    if (reader == NULL) {
        LOGE("Error opening file %s for reading", fname);
        return -2;
    }

    memset(buf, 0, 12);
    l_nb_read = fread(buf, 1, 12, reader);
    fclose(reader);
    if (l_nb_read != 12) {
        LOGE("Error reading header from file %s", fname);
        return -1;
    }



    ext_format = get_file_format(fname);

    magic_format = get_magic_format((char *)buf);
    if (magic_format == JP2_CFMT) {
        magic_s = ".jp2";
    } else if (magic_format == J2K_CFMT) {
        magic_s = ".j2k or .jpc or .j2c";
    } else if (magic_format == -1) {
        LOGE("Unrecognized file format");
        return -1;
    }

    if (magic_format == ext_format)
        return ext_format;

    s = fname + strlen(fname) - 4;

    LOGE("The extension of this file is incorrect.\nFOUND %s. SHOULD BE %s", s, magic_s);

    return magic_format;
}

static int imagetoargb(opj_image_t *image, image_data_t *outImage) {
    int w, h;
    int i;
    int adjustR, adjustG, adjustB, adjustA;

    outImage->hasAlpha = false;

    if (image->comps[0].prec < 8) {
        LOGE("Unsupported number of components: %d\n", image->comps[0].prec);
        return 1;
    }
    
    if (image->numcomps >= 3 && image->comps[0].dx == image->comps[1].dx
        && image->comps[1].dx == image->comps[2].dx
        && image->comps[0].dy == image->comps[1].dy
        && image->comps[1].dy == image->comps[2].dy
        && image->comps[0].prec == image->comps[1].prec
        && image->comps[1].prec == image->comps[2].prec) {
        
        /* -->> -->> -->> -->>    
        24/32 bits color
        <<-- <<-- <<-- <<-- */
        
        w = image->comps[0].w;        
        h = image->comps[0].h;
        
        outImage->pixels = (int *) malloc(sizeof(int) * w * h);
        outImage->height = h;
        outImage->width = w;
        
        if (!outImage->pixels) {
            LOGE("Could not allocate %d bytes of memory.\n", w * h * sizeof(int));
            return 1;
        }
        
        if (image->comps[0].prec > 8) {
            adjustR = image->comps[0].prec - 8;
            printf("RGB CONVERSION: Truncating component 0 from %d bits to 8 bits\n", image->comps[0].prec);
        }
        else 
            adjustR = 0;
        if (image->comps[1].prec > 8) {
            adjustG = image->comps[1].prec - 8;
            printf("RGB CONVERSION: Truncating component 1 from %d bits to 8 bits\n", image->comps[1].prec);
        }
        else 
            adjustG = 0;
        if (image->comps[2].prec > 8) {
            adjustB = image->comps[2].prec - 8;
            printf("RGB CONVERSION: Truncating component 2 from %d bits to 8 bits\n", image->comps[2].prec);
        }
        else 
            adjustB = 0;

        if (image->numcomps >= 4) { //alpha
            outImage->hasAlpha = true;
            if (image->comps[3].prec > 8) {
                adjustA = image->comps[3].prec - 8;
                printf("RGB CONVERSION: Truncating component 3 from %d bits to 8 bits\n", image->comps[3].prec);
            }
            else
                adjustA = 0;
        }

        for (i = 0; i < w * h; i++) {
            OPJ_UINT8 rc, gc, bc, ac;
            int r, g, b, a;
                            
            r = image->comps[0].data[i];
            r += (image->comps[0].sgnd ? 1 << (image->comps[0].prec - 1) : 0);
            r = ((r >> adjustR)/*+((r >> (adjustR-1))%2)*/);
            if(r > 255) r = 255; else if(r < 0) r = 0;
            rc = (OPJ_UINT8)r;

            g = image->comps[1].data[i];
            g += (image->comps[1].sgnd ? 1 << (image->comps[1].prec - 1) : 0);
            g = ((g >> adjustG)/*+((g >> (adjustG-1))%2)*/);
            if(g > 255) g = 255; else if(g < 0) g = 0;
            gc = (OPJ_UINT8)g;

            b = image->comps[2].data[i];
            b += (image->comps[2].sgnd ? 1 << (image->comps[2].prec - 1) : 0);
            b = ((b >> adjustB)/*+((b >> (adjustB-1))%2)*/);
            if(b > 255) b = 255; else if(b < 0) b = 0;
            bc = (OPJ_UINT8)b;

            if (outImage->hasAlpha) {
                a = image->comps[3].data[i];
                a += (image->comps[3].sgnd ? 1 << (image->comps[3].prec - 1) : 0);
                a = ((a >> adjustA)/*+((a >> (adjustA-1))%2)*/);
                if (a > 255) a = 255; else if (a < 0) a = 0;
                ac = (OPJ_UINT8) a;
            } else {
                ac = 0xFF;
            }

            outImage->pixels[i] = (ac << 24) | (rc << 16) | (gc << 8) | bc;
        }
    } else {            /* Gray-scale */

        /* -->> -->> -->> -->>
        8 bits non code (Gray scale)
        <<-- <<-- <<-- <<-- */

        w = image->comps[0].w;        
        h = image->comps[0].h;
        
        outImage->pixels = (int *) malloc(sizeof(int) * w * h);
        outImage->height = h;
        outImage->width = w;
        
        if (!outImage->pixels) {
            LOGE("Could not allocate %d bytes of memory.\n", w * h * sizeof(int));
            return 1;
        }

        if (image->comps[0].prec > 8) {
            adjustR = image->comps[0].prec - 8;
            printf("BMP CONVERSION: Truncating component 0 from %d bits to 8 bits\n", image->comps[0].prec);
        }else 
            adjustR = 0;

        if (image->numcomps >= 2) { //alpha
            outImage->hasAlpha = true;
            if (image->comps[1].prec > 8) {
                adjustA = image->comps[1].prec - 8;
                printf("RGB CONVERSION: Truncating component 1 from %d bits to 8 bits\n", image->comps[1].prec);
            }
            else
                adjustA = 0;
        }

        for (i = 0; i < w * h; i++) {
            int r, a;
            
            r = image->comps[0].data[i];
            r += (image->comps[0].sgnd ? 1 << (image->comps[0].prec - 1) : 0);
            r = ((r >> adjustR)/*+((r >> (adjustR-1))%2)*/);
            if(r > 255) r = 255; else if(r < 0) r = 0;

            if (outImage->hasAlpha) {
                a = image->comps[1].data[i];
                a += (image->comps[1].sgnd ? 1 << (image->comps[1].prec - 1) : 0);
                a = ((a >> adjustA)/*+((a >> (adjustA-1))%2)*/);
                if (a > 255) a = 255; else if (a < 0) a = 0;
            } else {
                a = 0xFF;
            }

            outImage->pixels[i] = (a << 24) | (r << 16) | (r << 8) | r;
        }
    }

    return 0;
}

/**
sample error callback expecting a FILE* client object
*/
static void error_callback(const char *msg, void *client_data) {
    (void)client_data;
    LOGE("[ERROR] %s", msg);
}
/**
sample warning callback expecting a FILE* client object
*/
static void warning_callback(const char *msg, void *client_data) {
    (void)client_data;
    LOGW("[WARNING] %s", msg);
}
/**
sample debug callback expecting no client object
*/
static void info_callback(const char *msg, void *client_data) {
    (void)client_data;
    LOGI("[INFO] %s", msg);
}

jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
    JNIEnv* env;
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }

    // Get jclass with env->FindClass.
    // Register methods with env->RegisterNatives.

    return JNI_VERSION_1_6;
}

int readJ2KHeader(opj_codec_t* l_codec, int decod_format, image_header_t *outHeader) {
    int compno;
    opj_j2k* header;

    if (l_codec == NULL) {
        LOGE("Codec is null");
        return EXIT_FAILURE;
    }

    switch(decod_format) {
        case J2K_CFMT:    /* JPEG-2000 codestream */
        {
            header = (opj_j2k *)((opj_codec_private_t *)l_codec)->m_codec;
            break;
        }
        case JP2_CFMT:    /* JPEG 2000 compressed image data */
        {
            header = ((opj_jp2_t*)((opj_codec_private_t *)l_codec)->m_codec)->j2k;
            break;
        }
        default:
            LOGE("Unknown file format");
            return EXIT_FAILURE;
    }

    if (header == NULL || header->m_private_image == NULL) {
        LOGE("Error getting header");
        opj_destroy_codec(l_codec);
        return EXIT_FAILURE;
    }

    memset(outHeader, 0, sizeof(image_header_t));

    //select the size of the largest component (they can be different in case of subsampling)
    outHeader->width = outHeader->height = 0;
    for (unsigned int i = 0; i < header->m_private_image->numcomps; i++) {
        outHeader->width = MAX(outHeader->width, header->m_private_image->comps[i].w);
        outHeader->height = MAX(outHeader->height, header->m_private_image->comps[i].h);
    }
    outHeader->hasAlpha = (header->m_private_image->numcomps == 2 || header->m_private_image->numcomps == 4);
    opj_tcp_t * l_default_tile = header->m_specific_param.m_decoder.m_default_tcp;
    if (l_default_tile) {
        outHeader->numQualityLayers = l_default_tile->numlayers;

        //return the lowest number of resolutions in all components
        for (compno = 0; compno < header->m_private_image->numcomps; compno++) {
            opj_tccp_t *l_tccp = &(l_default_tile->tccps[compno]);
            if (outHeader->numResolutions == 0) outHeader->numResolutions = l_tccp->numresolutions;
            else outHeader->numResolutions = MIN(outHeader->numResolutions, l_tccp->numresolutions);
        }
    } else {
        LOGW("Error reading default tile. Number of resolutions and quality layers could not be obtained.");
        outHeader->numResolutions = 1;
        outHeader->numQualityLayers = 1;
    }
    return EXIT_SUCCESS;
}

int decodeJP2Header(opj_stream_t *l_stream, opj_dparameters_t *parameters, image_header_t *outHeader) {
    opj_codec_t* l_codec = NULL;                /* Handle to a decompressor */
    opj_image_t* image = NULL;

    parameters->flags |= OPJ_DPARAMETERS_DUMP_FLAG;

    /* decode the JPEG2000 stream */
    /* ---------------------- */

    switch(parameters->decod_format) {
        case J2K_CFMT:    /* JPEG-2000 codestream */
        {
            /* Get a decoder handle */
            l_codec = opj_create_decompress(OPJ_CODEC_J2K);
            break;
        }
        case JP2_CFMT:    /* JPEG 2000 compressed image data */
        {
            /* Get a decoder handle */
            l_codec = opj_create_decompress(OPJ_CODEC_JP2);
            break;
        }
        default:
            LOGE("Unknown file format");
            return EXIT_FAILURE;
    }

    /* catch events using our callbacks and give a local context */
    opj_set_info_handler(l_codec, info_callback,00);
    opj_set_warning_handler(l_codec, warning_callback,00);
    opj_set_error_handler(l_codec, error_callback,00);

    /* Setup the decoder decoding parameters using user parameters */
    if ( !opj_setup_decoder(l_codec, parameters) ){
        LOGE("ERROR -> j2k_dump: failed to setup the decoder\n");
        opj_destroy_codec(l_codec);
        return EXIT_FAILURE;
    }

    /* Read the main header of the codestream and if necessary the JP2 boxes*/
    if(! opj_read_header(l_stream, l_codec, &image)){
        LOGE("ERROR -> opj_decompress: failed to read the header\n");
        opj_destroy_codec(l_codec);
        opj_image_destroy(image);
        return EXIT_FAILURE;
    }

    if (readJ2KHeader(l_codec, parameters->decod_format, outHeader) != EXIT_SUCCESS) {
        LOGE("Could not read header");
        return EXIT_FAILURE;
    }

    /* free remaining structures */
    if (l_codec) {
        opj_destroy_codec(l_codec);
    }

    /* free image data structure */
    opj_image_destroy(image);

    return EXIT_SUCCESS;
}


int decodeJP2Stream(opj_stream_t *l_stream, opj_dparameters_t *parameters, image_data_t *outImage, jint reduce) {
    opj_codec_t* l_codec = NULL;                /* Handle to a decompressor */
    opj_image_t* image = NULL;
    image_header_t outHeader;

    /* decode the JPEG2000 stream */
    /* ---------------------- */

    switch(parameters->decod_format) {
        case J2K_CFMT:    /* JPEG-2000 codestream */
        {
            /* Get a decoder handle */
            l_codec = opj_create_decompress(OPJ_CODEC_J2K);
            break;
        }
        case JP2_CFMT:    /* JPEG 2000 compressed image data */
        {
            /* Get a decoder handle */
            l_codec = opj_create_decompress(OPJ_CODEC_JP2);
            break;
        }
        default:
            LOGE("Unknown file format");
            return EXIT_FAILURE;
    }

    /* catch events using our callbacks and give a local context */        
    opj_set_info_handler(l_codec, info_callback,00);
    opj_set_warning_handler(l_codec, warning_callback,00);
    opj_set_error_handler(l_codec, error_callback,00);

    /* Setup the decoder decoding parameters using user parameters */
    if ( !opj_setup_decoder(l_codec, parameters) ){
        LOGE("ERROR -> j2k_dump: failed to setup the decoder\n");
        opj_destroy_codec(l_codec);
        return EXIT_FAILURE;
    }

    /* Read the main header of the codestream and if necessary the JP2 boxes*/
    if(! opj_read_header(l_stream, l_codec, &image)){
        LOGE("ERROR -> opj_decompress: failed to read the header\n");
        opj_destroy_codec(l_codec);
        opj_image_destroy(image);
        return EXIT_FAILURE;
    }

    parameters->cp_reduce = reduce;

    //check the decode parameters against the header - fix them if necessary
    if (readJ2KHeader(l_codec, parameters->decod_format, &outHeader) == EXIT_SUCCESS) {
        if (parameters->cp_reduce >= outHeader.numResolutions) {
            LOGE("The number of resolutions to remove (%d) is greater or equal than the number of resolutions of this image (%d). Changing to %d.", parameters->cp_reduce, outHeader.numResolutions, outHeader.numResolutions - 1);
            parameters->cp_reduce = outHeader.numResolutions - 1;
        }
        if (parameters->cp_layer > 0 && parameters->cp_layer > outHeader.numQualityLayers) {
            /* It's too late to change the cp_layer value; it was already used in opj_read_header().
             * But in this case we don't actually need to change it, because OpenJPEG quietly ignores it
             * if the value is too high. So we just log this.
             */
            LOGE("The number of quality layers to decode (%d) is greater than the number of quality layers of this image (%d). Changing to %d.", parameters->cp_layer, outHeader.numQualityLayers, outHeader.numQualityLayers);
        }
    }

    /* Setup the decoder again with fixed parameters */
    if ( !opj_setup_decoder(l_codec, parameters) ){
        LOGE("ERROR -> j2k_dump: failed to setup the decoder\n");
        opj_destroy_codec(l_codec);
        return EXIT_FAILURE;
    }


    /* Optional if you want decode the entire image */
    if (!opj_set_decode_area(l_codec, image, parameters->DA_x0,
            parameters->DA_y0, parameters->DA_x1, parameters->DA_y1)){
        LOGE("ERROR -> opj_decompress: failed to set the decoded area\n");
        opj_destroy_codec(l_codec);
        opj_image_destroy(image);
        return EXIT_FAILURE;
    }

    /* Get the decoded image */
    if (!(opj_decode(l_codec, l_stream, image) && opj_end_decompress(l_codec,    l_stream))) {
        LOGE("ERROR -> opj_decompress: failed to decode image!\n");
        opj_destroy_codec(l_codec);
        opj_image_destroy(image);
        return EXIT_FAILURE;
    }

    /* Convert the decoded image data to RGB with no subsampling */
    if (image->color_space != OPJ_CLRSPC_SYCC
        && image->numcomps == 3 && image->comps[0].dx == image->comps[0].dy
        && image->comps[1].dx != 1) {
        image->color_space = OPJ_CLRSPC_SYCC;
    } else if (image->numcomps <= 2) {
        image->color_space = OPJ_CLRSPC_GRAY;
    }

    if (image->color_space == OPJ_CLRSPC_SYCC) {
        color_sycc_to_rgb(image);
    } else if ((image->color_space == OPJ_CLRSPC_CMYK)/* &&
               (parameters.cod_format != TIF_DFMT)*/) {
        color_cmyk_to_rgb(image);
    } else if (image->color_space == OPJ_CLRSPC_EYCC) {
        color_esycc_to_rgb(image);
    }

    if (image->icc_profile_buf) {
#if defined(OPJ_HAVE_LIBLCMS1) || defined(OPJ_HAVE_LIBLCMS2)
        if (image->icc_profile_len) {
                color_apply_icc_profile(image);
            } else {
                color_cielab_to_rgb(image);
            }
#endif
        free(image->icc_profile_buf);
        image->icc_profile_buf = NULL;
        image->icc_profile_len = 0;
    }
    
    //convert the image data to image_data_t which will be returned to Java
    imagetoargb(image, outImage);

    /* free remaining structures */
    if (l_codec) {
        opj_destroy_codec(l_codec);
    }

    /* free image data structure */
    opj_image_destroy(image);
    
    return EXIT_SUCCESS;
}

#define BYTE_ARRAY_SRC_CHUNK_LENGTH 4096
typedef struct opj_byte_array_source {
    char * data;
    unsigned int offset;
    unsigned int length;
    unsigned int availableLength;
} opj_byte_array_source;

static OPJ_SIZE_T opj_read_from_byte_array (void * p_buffer, OPJ_SIZE_T p_nb_bytes, opj_byte_array_source * p_user_data)
{
    //LOGD("opj_read_from_byte_array started");
    size_t toRead = MIN(p_nb_bytes, p_user_data->length - p_user_data->offset);
    memcpy(p_buffer, p_user_data->data + p_user_data->offset, toRead);
    p_user_data->offset += toRead;
    //LOGD("opj_read_from_byte_array finished");
    return toRead > 0 ? toRead : (OPJ_SIZE_T)-1;
}

static OPJ_SIZE_T opj_write_from_byte_array (void * p_buffer, OPJ_SIZE_T p_nb_bytes, opj_byte_array_source * p_user_data)
{
    //LOGD("opj_write_from_byte_array started");
    while (p_nb_bytes + p_user_data->offset > p_user_data->availableLength) {
        //allocate bigger buffer
        //LOGD("opj_write_from_byte_array - realloc");
        char * newBuffer = (char *)realloc(p_user_data->data, p_user_data->availableLength + BYTE_ARRAY_SRC_CHUNK_LENGTH);
        //LOGD("opj_write_from_byte_array - realloc succeeded");
        if (newBuffer) {
            p_user_data->data = newBuffer;
            p_user_data->availableLength += BYTE_ARRAY_SRC_CHUNK_LENGTH;
        } else {
            p_nb_bytes = p_user_data->availableLength - p_user_data->offset;
        }
    }
    memcpy(p_user_data->data + p_user_data->offset, p_buffer, p_nb_bytes);
    p_user_data->offset += p_nb_bytes;
    p_user_data->length = MAX(p_user_data->length, p_user_data->offset);
    //LOGD("opj_write_from_byte_array finished");
    return p_nb_bytes;
}

static OPJ_OFF_T opj_skip_from_byte_array (OPJ_OFF_T p_nb_bytes, opj_byte_array_source * p_user_data)
{
    //LOGD("opj_skip_from_byte_array started");
    while (p_nb_bytes + p_user_data->offset > p_user_data->availableLength) {
        //allocate bigger buffer
        //LOGD("opj_skip_from_byte_array - realloc");
        char * newBuffer = (char *)realloc(p_user_data->data, p_user_data->availableLength + BYTE_ARRAY_SRC_CHUNK_LENGTH);
        //LOGD("opj_skip_from_byte_array - realloc succeeded");
        if (newBuffer) {
            p_user_data->data = newBuffer;
            memset(newBuffer + p_user_data->availableLength, 0, BYTE_ARRAY_SRC_CHUNK_LENGTH);
            p_user_data->availableLength += BYTE_ARRAY_SRC_CHUNK_LENGTH;
        } else {
            //LOGD("opj_skip_from_byte_array end - returns -1");
            return -1;
        }
    }
    
    p_user_data->offset += p_nb_bytes;

    //LOGD("opj_skip_from_byte_array finished successfully");
    return p_nb_bytes;
}

static OPJ_BOOL opj_seek_from_byte_array (OPJ_OFF_T p_nb_bytes, opj_byte_array_source * p_user_data)
{
    //LOGD("opj_seek_from_byte_array started");
    while (p_nb_bytes > p_user_data->availableLength) {
        //allocate bigger buffer
        //LOGD("opj_seek_from_byte_array - realloc");
        char * newBuffer = (char *)realloc(p_user_data->data, p_user_data->availableLength + BYTE_ARRAY_SRC_CHUNK_LENGTH);
        //LOGD("opj_seek_from_byte_array - realloc succeeded");
        if (newBuffer) {
            p_user_data->data = newBuffer;
            memset(newBuffer + p_user_data->availableLength, 0, BYTE_ARRAY_SRC_CHUNK_LENGTH);
            p_user_data->availableLength += BYTE_ARRAY_SRC_CHUNK_LENGTH;
        } else {
            //LOGD("opj_seek_from_byte_array finished unsuccessfully");
            return OPJ_FALSE;
        }
    }
    
    if (p_nb_bytes > p_user_data->length) {
        //LOGD("opj_seek_from_byte_array finished unsuccessfully");
        return OPJ_FALSE;
    }
    
    p_user_data->offset = p_nb_bytes;

    //LOGD("opj_seek_from_byte_array finished successfully");
    return OPJ_TRUE;
}

opj_stream_t* OPJ_CALLCONV opj_stream_create_byte_array_stream (    char * data, unsigned int length,
                                                                    OPJ_SIZE_T p_size, 
                                                                    OPJ_BOOL p_is_read_stream)
{
    opj_stream_t* l_stream = 00;

    l_stream = opj_stream_create(p_size,p_is_read_stream);
    if (! l_stream) {
        return NULL;
    }
    
    opj_byte_array_source * data_src = (opj_byte_array_source *)malloc(sizeof(opj_byte_array_source));
    data_src->data = data;
    data_src->offset = 0;
    data_src->length = length;
    data_src->availableLength = length;

    opj_stream_set_user_data(l_stream, data_src, NULL);
    opj_stream_set_user_data_length(l_stream, length);
    opj_stream_set_read_function(l_stream, (opj_stream_read_fn) opj_read_from_byte_array);
    opj_stream_set_write_function(l_stream, (opj_stream_write_fn) opj_write_from_byte_array);
    opj_stream_set_skip_function(l_stream, (opj_stream_skip_fn) opj_skip_from_byte_array);
    opj_stream_set_seek_function(l_stream, (opj_stream_seek_fn) opj_seek_from_byte_array);

    return l_stream;
}

int setEncoderParameters(opj_cparameters_t *parameters, JNIEnv *env, jint fileFormat, jint numResolutions, jfloatArray compressionRates, jfloatArray qualityValues) {
    int i;
    jfloat *bufferPtr;
    jsize dataLength;
    
    /* set encoding parameters to default values */
    opj_set_default_encoder_parameters(parameters);
    //LOGD("1");
    
    parameters->numresolution = numResolutions;
    parameters->cod_format = fileFormat;
    
    if (compressionRates) {
        dataLength = env->GetArrayLength(compressionRates);
        if (dataLength > 100) dataLength = 100; //opj_cparameters supports maximum of 100 quality layers
        if (dataLength > 0) {
            bufferPtr = env->GetFloatArrayElements(compressionRates, NULL);
			parameters->tcp_numlayers = dataLength;
            parameters->cp_disto_alloc = 1;
            for (i = 0; i < dataLength; i++) {
                parameters->tcp_rates[i] = bufferPtr[i];
            }
            env->ReleaseFloatArrayElements(compressionRates, bufferPtr, JNI_ABORT);
        }
    }
    
    if (qualityValues) {
        dataLength = env->GetArrayLength(qualityValues);
        if (dataLength > 100) dataLength = 100; //opj_cparameters supports maximum of 100 quality layers
        if (dataLength > 0) {
            bufferPtr = env->GetFloatArrayElements(qualityValues, NULL);
            parameters->cp_fixed_quality = 1;
			parameters->tcp_numlayers = dataLength;
            for (i = 0; i < dataLength; i++) {
                parameters->tcp_distoratio[i] = bufferPtr[i];
            }
            env->ReleaseFloatArrayElements(qualityValues, bufferPtr, JNI_ABORT);
        }
    }
    
	/* check for possible errors */
	if (parameters->cp_cinema){
		if(parameters->tcp_numlayers > 1){
			parameters->cp_rsiz = OPJ_STD_RSIZ;
            LOGW("Warning: DC profiles do not allow more than one quality layer. The codestream created will not be compliant with the DC profile");
		}
	}

	if ((parameters->cp_disto_alloc || parameters->cp_fixed_alloc || parameters->cp_fixed_quality)
		&& (!(parameters->cp_disto_alloc ^ parameters->cp_fixed_alloc ^ parameters->cp_fixed_quality))) {
		LOGE("Error: options -r -q and -f cannot be used together !!");
		return EXIT_FAILURE;
	}				/* mod fixed_quality */

	/* if no rate entered, lossless by default */
	if (parameters->tcp_numlayers == 0) {
		parameters->tcp_rates[0] = 0;	/* MOD antonin : losslessbug */
		parameters->tcp_numlayers++;
		parameters->cp_disto_alloc = 1;
	}

	if((parameters->cp_tx0 > parameters->image_offset_x0) || (parameters->cp_ty0 > parameters->image_offset_y0)) {
		LOGE("Error: Tile offset dimension is unnappropriate --> TX0(%d)<=IMG_X0(%d) TYO(%d)<=IMG_Y0(%d) \n",
			parameters->cp_tx0, parameters->image_offset_x0, parameters->cp_ty0, parameters->image_offset_y0);
		return EXIT_FAILURE;
	}

	for (i = 0; i < parameters->numpocs; i++) {
		if (parameters->POC[i].prg == -1) {
			LOGW("Unrecognized progression order in option -P (POC n %d) [LRCP, RLCP, RPCL, PCRL, CPRL] !!\n", i + 1);
		}
	}

    /* Create comment for codestream */
    if(parameters->cp_comment == NULL) {
        const char comment[] = "Created by OpenJPEG version ";
        const size_t clen = strlen(comment);
        const char *version = opj_version();
/* UniPG>> */
#ifdef USE_JPWL
        parameters->cp_comment = (char*)malloc(clen+strlen(version)+11);
        sprintf(parameters->cp_comment,"%s%s with JPWL", comment, version);
#else
        parameters->cp_comment = (char*)malloc(clen+strlen(version)+1);
        sprintf(parameters->cp_comment,"%s%s", comment, version);
#endif
/* <<UniPG */
    }
    //LOGD("2");
    
	return EXIT_SUCCESS;
}

//convert raw bitmap pixels to opj_image_t structure
opj_image_t * getImage(JNIEnv *env, jintArray pixels, jboolean hasAlpha, jint width, jint height, opj_cparameters_t * parameters) {
    opj_image_t *image = NULL;
    int i;
    jint *bufferPtr;
    jsize dataLength;
    int numcomps = hasAlpha ? 4 : 3;
    OPJ_COLOR_SPACE color_space;
    opj_image_cmptparm_t cmptparm[numcomps];    /* maximum of 3 components */
    
    color_space = OPJ_CLRSPC_SRGB;
    /* initialize image components */
    memset(&cmptparm[0], 0, numcomps * sizeof(opj_image_cmptparm_t));
    for(i = 0; i < numcomps; i++) {
        cmptparm[i].prec = 8;
        cmptparm[i].bpp = 8;
        cmptparm[i].sgnd = 0;
        cmptparm[i].dx = parameters->subsampling_dx;
        cmptparm[i].dy = parameters->subsampling_dy;
        cmptparm[i].w = (int)width;
        cmptparm[i].h = (int)height;
    }
    
    /* create the image */
    image = opj_image_create(numcomps, &cmptparm[0], color_space);
    if (!image) {
        LOGE("could not create image data structure");
		return NULL;
    }
    //LOGD("3");
    
    /* set image offset and reference grid */
    image->x0 = parameters->image_offset_x0;
    image->y0 = parameters->image_offset_y0;
    image->x1 =    !image->x0 ? (width - 1) * parameters->subsampling_dx + 1 : image->x0 + (width - 1) * parameters->subsampling_dx + 1;
    image->y1 =    !image->y0 ? (height - 1) * parameters->subsampling_dy + 1 : image->y0 + (height - 1) * parameters->subsampling_dy + 1;
    
    //LOGD("4");

    //copy bytes from java to the image structure
    dataLength = env->GetArrayLength(pixels);
    bufferPtr = env->GetIntArrayElements(pixels, NULL);
    for (i = 0; i < width * height; i++) {
        image->comps[0].data[i] = (bufferPtr[i] >> 16) & 0xFF;    /* R */
        image->comps[1].data[i] = (bufferPtr[i] >>  8) & 0xFF;    /* G */
        image->comps[2].data[i] = (bufferPtr[i]      ) & 0xFF;    /* B */
        if (hasAlpha) image->comps[3].data[i] = (bufferPtr[i] >> 24 ) & 0xFF; /* A */
    }
    env->ReleaseIntArrayElements(pixels, bufferPtr, JNI_ABORT);
    //LOGD("5");
    
    return image;
}

//encode a opj_image_t (prepared from the raw bitmap data) into a JPEG-2000 byte array
int encodeJP2(opj_cparameters_t *parameters, opj_image_t *image, opj_byte_array_source ** outByteArray) {
    int i, j;
    opj_stream_private_t * l_stream = NULL;
	opj_codec_t* l_codec = NULL;
    opj_byte_array_source * jp2data = NULL;
	OPJ_BOOL bSuccess;
    
    /* Decide if MCT should be used */
    parameters->tcp_mct = image->numcomps == 3 ? 1 : 0;

    /* encode the destination image */
    /* ---------------------------- */

    switch(parameters->cod_format) {
        case J2K_CFMT:    /* JPEG-2000 codestream */
        {
            /* Get a decoder handle */
            l_codec = opj_create_compress(OPJ_CODEC_J2K);
            break;
        }
        case JP2_CFMT:    /* JPEG 2000 compressed image data */
        {
            /* Get a decoder handle */
            l_codec = opj_create_compress(OPJ_CODEC_JP2);
            break;
        }
        default:
            LOGE("Unknown output format");
            opj_image_destroy(image);
            return EXIT_FAILURE;
    }
    //LOGD("6");
    
    /* catch events using our callbacks and give a local context */        
    opj_set_info_handler(l_codec, info_callback,00);
    opj_set_warning_handler(l_codec, warning_callback,00);
    opj_set_error_handler(l_codec, error_callback,00);

    opj_setup_encoder(l_codec, parameters, image);
    //LOGD("7");
    
    /* open a byte stream for writing and allocate memory for all tiles */
    if (parameters->outfile[0] != 0) {
		/* open a byte stream for writing and allocate memory for all tiles */
		l_stream = (opj_stream_private_t *)opj_stream_create_default_file_stream(parameters->outfile, OPJ_FALSE);
    } else {
        l_stream = (opj_stream_private_t *)opj_stream_create_byte_array_stream(NULL, 0, OPJ_J2K_STREAM_CHUNK_SIZE, OPJ_FALSE);
        jp2data = (opj_byte_array_source *)l_stream->m_user_data;
    }
    if (! l_stream){
        opj_destroy_codec(l_codec);
        opj_image_destroy(image);
        return EXIT_FAILURE;
    }
    //LOGD("8");
    
    /* encode the image */
    bSuccess = opj_start_compress(l_codec,image,(opj_stream_t*)l_stream);
    if (!bSuccess)  {
        LOGE("failed to encode image: opj_start_compress");
    } else {
        //LOGD("9");
        
        bSuccess = bSuccess && opj_encode(l_codec, (opj_stream_t*)l_stream);
        if (!bSuccess)  {
            LOGE("failed to encode image: opj_encode");
        } else {
            //LOGD("10");
            bSuccess = bSuccess && opj_end_compress(l_codec, (opj_stream_t*)l_stream);
            if (!bSuccess)  {
                LOGE("failed to encode image: opj_end_compress");
            } else {
                LOGI("Generated JPEG2000 data");
            }
        }
    }
    //LOGD("11");

    /* close and free the byte stream */
    opj_stream_destroy((opj_stream_t*)l_stream);
    //LOGD("12");
    
    /* free remaining compression structures */
    opj_destroy_codec(l_codec);
    //LOGD("13");

    /* free image data */
    opj_image_destroy(image);
    //LOGD("14");
    
    if (!bSuccess)  {
        if (jp2data) {
            free(jp2data->data);
            free(jp2data);
        }
        return EXIT_FAILURE;
    }
    if (outByteArray) *outByteArray = jp2data;
    //LOGD("15");
    return EXIT_SUCCESS;
}

//encode a raw bitmap into JPEG-2000, return the result in a byte array
JNIEXPORT jbyteArray JNICALL Java_com_gemalto_jp2_JP2Encoder_encodeJP2ByteArray(JNIEnv *env, jclass thiz, jintArray pixels, jboolean hasAlpha, jint width, jint height,
                                                                             jint fileFormat, jint numResolutions, jfloatArray compressionRates, jfloatArray qualityValues) {
    opj_byte_array_source * jp2data = NULL;
    opj_cparameters_t parameters;    /* compression parameters */
    opj_image_t *image = NULL;
    
    if (setEncoderParameters(&parameters, env, fileFormat, numResolutions, compressionRates, qualityValues) != EXIT_SUCCESS) {
        return NULL;
    }
    parameters.outfile[0] = 0;
    
    image = getImage(env, pixels, hasAlpha, width, height, &parameters);
    if (!image) {
        return NULL;
    }
    
    if (encodeJP2(&parameters, image, &jp2data) != EXIT_SUCCESS) {
        LOGE("Error encoding JP2 data");
        return NULL;
    }
    
    jbyteArray ret = env->NewByteArray(jp2data->length);
    //LOGD("16, jp2data->data = %d, jp2data->offset = %d, jp2data->length = %d, jp2data->availableLength = %d", jp2data->data, jp2data->offset, jp2data->length, jp2data->availableLength);
    env->SetByteArrayRegion(ret, 0, jp2data->length, (jbyte *)jp2data->data);
    //LOGD("17");
    if (parameters.cp_comment != NULL) {
        free(parameters.cp_comment);
    }
    free(jp2data->data);
    free(jp2data);
    return ret;
}

//encode a raw bitmap into JPEG-2000, store the result into a file, return success/failure
JNIEXPORT jint JNICALL Java_com_gemalto_jp2_JP2Encoder_encodeJP2File(JNIEnv *env, jclass thiz, jstring fileName, jintArray pixels, jboolean hasAlpha, jint width, jint height,
                                                                        jint fileFormat, jint numResolutions, jfloatArray compressionRates, jfloatArray qualityValues) {
    opj_cparameters_t parameters;    /* compression parameters */
    opj_image_t *image = NULL;
    const char *c_file;
    
    if (setEncoderParameters(&parameters, env, fileFormat, numResolutions, compressionRates, qualityValues) != EXIT_SUCCESS) {
        return EXIT_FAILURE;
    }
    
    c_file = env->GetStringUTFChars(fileName, NULL);
    strcpy(parameters.outfile, c_file);
    env->ReleaseStringUTFChars(fileName, c_file);
    
    image = getImage(env, pixels, hasAlpha, width, height, &parameters);
    if (!image) {
        return EXIT_FAILURE;
    }
    
    return encodeJP2(&parameters, image, NULL);
}

//convert the image_data_t to integer array (use first 3 integers for width, height, and alpha information, then append the raw pixel data)
jintArray prepareReturnData(JNIEnv *env, image_data_t *outImage) {
    //prepare return data: first three integers in the array are width, height, hasAlpha, then image pixels
    jintArray ret = env->NewIntArray(outImage->width * outImage->height + 3);
    env->SetIntArrayRegion(ret, 0, 3, (jint*)outImage);
    env->SetIntArrayRegion(ret, 3, outImage->width * outImage->height, outImage->pixels);
    free(outImage->pixels);
    outImage->pixels = NULL;
    return ret;
}

//convert a image_header_t to an integer array
jintArray prepareReturnHeaderData(JNIEnv *env, image_header_t *outHeader) {
    //prepare return data: first three integers in the array are width, height, hasAlpha, then image pixels
    int length = sizeof(image_header_t) / sizeof(jint);
    jintArray ret = env->NewIntArray(length);
    env->SetIntArrayRegion(ret, 0, length, (jint*)outHeader);
    return ret;
}

//decode a JPEG-2000 encoded file, return in 32-bit raw RGBA pixels
JNIEXPORT jintArray JNICALL Java_com_gemalto_jp2_JP2Decoder_decodeJP2File(JNIEnv *env, jclass thiz, jstring fileName, jint reduce, jint layers) {
    opj_stream_t *l_stream = NULL;                /* Stream */
    opj_dparameters_t parameters;            /* decompression parameters */
    image_data_t outImage; //output data
    jintArray ret = NULL;

    //sanity check
    if (fileName == NULL) {
        LOGE("fileName is NULL!");
        return NULL;
    }

    /* set decoding parameters to default values */
    opj_set_default_decoder_parameters(&parameters);
    
    const char *c_file = env->GetStringUTFChars(fileName, NULL);
    strcpy(parameters.infile, c_file);
    env->ReleaseStringUTFChars(fileName, c_file);
    
    parameters.decod_format = infile_format(parameters.infile);
    parameters.cp_layer = layers;
    //We don't set the reduce parameter yet, because if it's too high, it would throw an error.
    //We will set it after we read the image header and find out actual number of resolutions.

    /* read the input file and put it in memory */
    /* ---------------------------------------- */
    l_stream = opj_stream_create_default_file_stream(parameters.infile,1);
    if (!l_stream){
        LOGE("ERROR -> failed to create the stream from the file\n");
        return NULL;
    }
    
    if (decodeJP2Stream(l_stream, &parameters, &outImage, reduce) == EXIT_SUCCESS) {
        ret = prepareReturnData(env, &outImage);
    }
    
    /* Close the byte stream */
    opj_stream_destroy(l_stream);

    return ret;
}

//decode a JPEG-2000 encoded byte array, return in 32-bit raw RGBA pixels
JNIEXPORT jintArray JNICALL Java_com_gemalto_jp2_JP2Decoder_decodeJP2ByteArray(JNIEnv *env, jclass thiz, jbyteArray data, jint reduce, jint layers) {
    opj_stream_t *l_stream = NULL;                /* Stream */
    opj_dparameters_t parameters;            /* decompression parameters */
    char *imgData;
    jbyte *bufferPtr;
    jsize dataLength;
    opj_byte_array_source * streamData = NULL;
    image_data_t outImage; //output data
    jintArray ret = NULL;

    //sanity check
    if (data == NULL) {
        LOGE("data is NULL!");
        return NULL;
    }

    /* set decoding parameters to default values */
    opj_set_default_decoder_parameters(&parameters);

    //copy bytes from java
    dataLength = env->GetArrayLength(data);
    bufferPtr = env->GetByteArrayElements(data, NULL);
    imgData = (char *)malloc(dataLength * sizeof(char));
    memcpy(imgData, bufferPtr, dataLength);
    env->ReleaseByteArrayElements(data, bufferPtr, JNI_ABORT);
    
    
    parameters.decod_format = get_magic_format(imgData);
    parameters.cp_layer = layers;
    //We don't set the reduce parameter yet, because if it's too high, it would throw an error.
    //We will set it after we read the image header and find out actual number of resolutions.

    l_stream = opj_stream_create_byte_array_stream(imgData,dataLength,OPJ_J2K_STREAM_CHUNK_SIZE,1);
    if (!l_stream){
        LOGE("ERROR -> failed to create the stream from the byte array");
        free(imgData);
        return NULL;
    }
    
    streamData = (opj_byte_array_source *)((opj_stream_private_t *)l_stream)->m_user_data;
    
    if (decodeJP2Stream(l_stream, &parameters, &outImage, reduce) == EXIT_SUCCESS) {
        ret = prepareReturnData(env, &outImage);
    }
    
    /* Close the byte stream */
    opj_stream_destroy(l_stream);
    free(streamData->data); //this is where the imgData is stored now
    free(streamData);

    return ret;
}

//read meta-data information from a JPEG-2000 encoded file, return in an integer array (image_header_t representation)
JNIEXPORT jintArray JNICALL Java_com_gemalto_jp2_JP2Decoder_readJP2HeaderFile(JNIEnv *env, jclass thiz, jstring fileName) {
    opj_stream_t *l_stream = NULL;                /* Stream */
    opj_dparameters_t parameters;            /* decompression parameters */
    image_header_t outHeader; //output data
    jintArray ret = NULL;

    //sanity check
    if (fileName == NULL) {
        LOGE("fileName is NULL!");
        return NULL;
    }

    /* set decoding parameters to default values */
    opj_set_default_decoder_parameters(&parameters);
    parameters.flags |= OPJ_DPARAMETERS_DUMP_FLAG;

    const char *c_file = env->GetStringUTFChars(fileName, NULL);
    strcpy(parameters.infile, c_file);
    env->ReleaseStringUTFChars(fileName, c_file);

    parameters.decod_format = infile_format(parameters.infile);

    /* read the input file and put it in memory */
    /* ---------------------------------------- */
    l_stream = opj_stream_create_default_file_stream(parameters.infile,1);
    if (!l_stream){
        LOGE("ERROR -> failed to create the stream from the file\n");
        return NULL;
    }

    if (decodeJP2Header(l_stream, &parameters, &outHeader) == EXIT_SUCCESS) {
        ret = prepareReturnHeaderData(env, &outHeader);
    }

    /* Close the byte stream */
    opj_stream_destroy(l_stream);

    return ret;
}

//read meta-data information from a JPEG-2000 encoded byte array, return in an integer array (image_header_t representation)
JNIEXPORT jintArray JNICALL Java_com_gemalto_jp2_JP2Decoder_readJP2HeaderByteArray(JNIEnv *env, jclass thiz, jbyteArray data) {
    opj_stream_t *l_stream = NULL;                /* Stream */
    opj_dparameters_t parameters;            /* decompression parameters */
    char *imgData;
    jbyte *bufferPtr;
    jsize dataLength;
    opj_byte_array_source * streamData = NULL;
    image_header_t outHeader; //output data
    jintArray ret = NULL;

    //sanity check
    if (data == NULL) {
        LOGE("data is NULL!");
        return NULL;
    }

    /* set decoding parameters to default values */
    opj_set_default_decoder_parameters(&parameters);
    parameters.flags |= OPJ_DPARAMETERS_DUMP_FLAG;

    //copy bytes from java
    dataLength = env->GetArrayLength(data);
    bufferPtr = env->GetByteArrayElements(data, NULL);
    imgData = (char *)malloc(dataLength * sizeof(char));
    memcpy(imgData, bufferPtr, dataLength);
    env->ReleaseByteArrayElements(data, bufferPtr, JNI_ABORT);


    parameters.decod_format = get_magic_format(imgData);

    l_stream = opj_stream_create_byte_array_stream(imgData,dataLength,OPJ_J2K_STREAM_CHUNK_SIZE,1);
    if (!l_stream){
        LOGE("ERROR -> failed to create the stream from the byte array");
        free(imgData);
        return NULL;
    }

    streamData = (opj_byte_array_source *)((opj_stream_private_t *)l_stream)->m_user_data;

    if (decodeJP2Header(l_stream, &parameters, &outHeader) == EXIT_SUCCESS) {
        ret = prepareReturnHeaderData(env, &outHeader);
    }

    /* Close the byte stream */
    opj_stream_destroy(l_stream);
    free(streamData->data); //this is where the imgData is stored now
    free(streamData);

    return ret;
}


#ifdef __cplusplus
}
#endif