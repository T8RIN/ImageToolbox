/* Version ID for the JPEG library.
 * Might be useful for tests like "#if JPEG_LIB_VERSION >= 60".
 */
#define JPEG_LIB_VERSION  62

/* libjpeg-turbo version */
#define LIBJPEG_TURBO_VERSION  

/* libjpeg-turbo version in integer form */
#define LIBJPEG_TURBO_VERSION_NUMBER  

/* Support arithmetic encoding */
/* #undef C_ARITH_CODING_SUPPORTED */

/* Support arithmetic decoding */
/* #undef D_ARITH_CODING_SUPPORTED */

/* Support in-memory source/destination managers */
#define MEM_SRCDST_SUPPORTED 1

/* Use accelerated SIMD routines. */
/* #undef WITH_SIMD */

/*
 * Define BITS_IN_JSAMPLE as either
 *   8   for 8-bit sample values (the usual setting)
 *   12  for 12-bit sample values
 * Only 8 and 12 are legal data precisions for lossy JPEG according to the
 * JPEG standard, and the IJG code does not support anything else!
 * We do not support run-time selection of data precision, sorry.
 */

#define BITS_IN_JSAMPLE  8      /* use 8 or 12 */

/* Define if your (broken) compiler shifts signed values as if they were
   unsigned. */
/* #undef RIGHT_SHIFT_IS_UNSIGNED */
