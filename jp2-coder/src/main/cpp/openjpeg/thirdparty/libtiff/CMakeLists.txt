cmake_minimum_required(VERSION 2.6)
project(libtiff C)
# This convenient copy of libtiff does not support encapsulated zlib or jpeg
# stream. see ZIP_SUPPORT and JPEG_SUPPORT values

include_directories(BEFORE "${CMAKE_CURRENT_SOURCE_DIR}")
include_directories(BEFORE "${CMAKE_CURRENT_BINARY_DIR}")

set(TARGET_FILES
        t4.h
        tiffiop.h
        tif_aux.c
        tif_close.c
        tif_codec.c
        tif_color.c
        tif_compress.c
        tif_dir.c
        tif_dir.h
        tif_dirinfo.c
        tif_dirread.c
        tif_dirwrite.c
        tif_dumpmode.c
        tif_error.c
        tif_extension.c
        tif_fax3.c
        tif_fax3.h
        tif_fax3sm.c
        tif_flush.c
        tif_getimage.c
        tif_jbig.c
        tif_jpeg.c
        tif_luv.c
        tif_lzw.c
        tif_next.c
        tif_ojpeg.c
        tif_open.c
        tif_packbits.c
        tif_pixarlog.c
        tif_predict.c
        tif_predict.h
        tif_print.c
        tif_read.c
        tif_strip.c
        tif_swab.c
        tif_thunder.c
        tif_tile.c
        tif_version.c
        tif_warning.c
        tif_write.c
        tif_zip.c
        tif_jpeg_12.c
        tif_lzma.c
        uvcode.h
        )

if (UNIX)
    set(TARGET_FILES ${TARGET_FILES} tif_unix.c)
    # Large file support
    # This might not catch every possibility catered for by
    # AC_SYS_LARGEFILE.
    add_definitions(-D_FILE_OFFSET_BITS=64)
    set(_FILE_OFFSET_BITS 64)
else ()
    set(TARGET_FILES ${TARGET_FILES} tif_win32.c)
endif ()

include(${CMAKE_ROOT}/Modules/TestBigEndian.cmake)
TEST_BIG_ENDIAN(WORDS_BIGENDIAN)
set(HOST_BIGENDIAN ${WORDS_BIGENDIAN})
if (HOST_BIGENDIAN)
    set(HOST_FILLORDER "FILLORDER_MSB2LSB")
else ()
    set(HOST_FILLORDER "FILLORDER_LSB2MSB")
endif ()
include(CheckIncludeFiles)
include(CheckSymbolExists)
include(CheckFunctionExists)
include(CheckCSourceCompiles)

CHECK_INCLUDE_FILES("zlib.h" HAVE_ZLIB_H)
CHECK_INCLUDE_FILES("jpeglib.h" HAVE_JPEGLIB_H)
if (HAVE_JPEGLIB_H)
    set(JPEG_SUPPORT 1)
endif ()
if (HAVE_ZLIB_H)
    include_directories(
            ${ZLIB_INCLUDE_DIR}
    )
    set(ZIP_SUPPORT 1)
    set(PIXARLOG_SUPPORT 1) # require zlib
endif ()
CHECK_INCLUDE_FILES("assert.h" HAVE_ASSERT_H)
CHECK_INCLUDE_FILES("dlfcn.h" HAVE_DLFCN_H)
CHECK_INCLUDE_FILES("fcntl.h" HAVE_FCNTL_H)
CHECK_INCLUDE_FILES("inttypes.h" HAVE_INTTYPES_H)
CHECK_INCLUDE_FILES("io.h" HAVE_IO_H)
CHECK_INCLUDE_FILES("limits.h" HAVE_LIMITS_H)
CHECK_INCLUDE_FILES("malloc.h" HAVE_MALLOC_H)
CHECK_INCLUDE_FILES("memory.h" HAVE_MEMORY_H)
CHECK_INCLUDE_FILES("search.h" HAVE_SEARCH_H)
CHECK_INCLUDE_FILES("stdint.h" HAVE_STDINT_H)
CHECK_INCLUDE_FILES("stdlib.h" HAVE_STDLIB_H)
CHECK_INCLUDE_FILES("string.h" HAVE_STRING_H)
CHECK_INCLUDE_FILES("strings.h" HAVE_STRINGS_H)
CHECK_INCLUDE_FILES("sys/stat.h" HAVE_SYS_STAT_H)
CHECK_INCLUDE_FILES("sys/time.h" HAVE_SYS_TIME_H)
CHECK_INCLUDE_FILES("time.h" HAVE_TIME_H)
CHECK_INCLUDE_FILES("sys/types.h" HAVE_SYS_TYPES_H)
CHECK_INCLUDE_FILES("unistd.h" HAVE_UNISTD_H)
CHECK_INCLUDE_FILES("windows.h" HAVE_WINDOWS_H)
CHECK_INCLUDE_FILES("strings.h" HAVE_STRINGS_H)
CHECK_INCLUDE_FILES("ieeefp.h" HAVE_IEEEFP_H)

# wotsit ?
if (HAVE_TIME_H AND HAVE_SYS_TIME_H)
    set(TIME_WITH_SYS_TIME 1)
endif ()
set(LZMA_SUPPORT 0) # ?
set(MDI_SUPPORT 1) # ?
set(STDC_HEADERS 1) # why not ?
set(DEFAULT_EXTRASAMPLE_AS_ALPHA 1)
set(CHECK_JPEG_YCBCR_SUBSAMPLING 1)
set(CCITT_SUPPORT 1)
set(DEFER_STRILE_LOAD 0)
set(HAVE_JBG_NEWLEN 0) # FIXME: jbigkit stuff
set(STRIPCHOP_DEFAULT "TIFF_STRIPCHOP")
set(STRIP_SIZE_DEFAULT 8192)
set(SUBIFD_SUPPORT 1)
set(THUNDER_SUPPORT 1)

CHECK_FUNCTION_EXISTS(getopt HAVE_GETOPT)
CHECK_FUNCTION_EXISTS(isascii HAVE_ISASCII)
CHECK_FUNCTION_EXISTS(memmove HAVE_MEMMOVE)
CHECK_FUNCTION_EXISTS(memset HAVE_MEMSET)
CHECK_FUNCTION_EXISTS(mmap HAVE_MMAP)
CHECK_FUNCTION_EXISTS(strcasecmp HAVE_STRCASECMP)
CHECK_FUNCTION_EXISTS(strchr HAVE_STRCHR)
CHECK_FUNCTION_EXISTS(strrchr HAVE_STRRCHR)
CHECK_FUNCTION_EXISTS(strstr HAVE_STRSTR)
CHECK_FUNCTION_EXISTS(strtol HAVE_STRTOL)
CHECK_FUNCTION_EXISTS(strtoul HAVE_STRTOUL)
CHECK_FUNCTION_EXISTS(strtoull HAVE_STRTOULL)

# May be inlined, so check it compiles:
check_c_source_compiles("
#include <stdio.h>
int main(void) {
  char buf[10];
  snprintf(buf, 10, \"Test %d\", 1);
  return 0;
}" HAVE_SNPRINTF)

if (NOT HAVE_SNPRINTF)
    set(TARGET_FILES ${TARGET_FILES} snprintf.c)
endif ()

include(CheckTypeSize)

CHECK_TYPE_SIZE("signed int" SIZEOF_SIGNED_INT)
CHECK_TYPE_SIZE("signed long" SIZEOF_SIGNED_LONG)
CHECK_TYPE_SIZE("signed long long" SIZEOF_SIGNED_LONG_LONG)
CHECK_TYPE_SIZE("signed short" SIZEOF_SIGNED_SHORT)
CHECK_TYPE_SIZE("unsigned int" SIZEOF_UNSIGNED_INT)
CHECK_TYPE_SIZE("unsigned long" SIZEOF_UNSIGNED_LONG)
CHECK_TYPE_SIZE("unsigned long long" SIZEOF_UNSIGNED_LONG_LONG)
CHECK_TYPE_SIZE("unsigned short" SIZEOF_UNSIGNED_SHORT)
CHECK_TYPE_SIZE("unsigned char*" SIZEOF_UNSIGNED_CHAR_P)
# check platform has ssize_t:
CHECK_TYPE_SIZE(ssize_t SSIZE_T)

if (HAVE_STDINT_H)
    set(TIFF_INT8_T int8_t)
    set(TIFF_INT16_T int16_t)
    set(TIFF_INT32_T int32_t)
    set(TIFF_INT64_T int64_t)
    set(TIFF_UINT8_T uint8_t)
    set(TIFF_UINT16_T uint16_t)
    set(TIFF_UINT32_T uint32_t)
    set(TIFF_UINT64_T uint64_t)
    set(TIFF_PTRDIFF_T ptrdiff_t)
    if (NOT HAVE_SSIZE_T)
        if ("${CMAKE_SIZEOF_VOID_P}" EQUAL 8)
            set(TIFF_SSIZE_T int64_t)
            set(TIFF_SSIZE_FORMAT "%lld")
            set(TIFF_SIZE_FORMAT "%llu")
        elseif ("${CMAKE_SIZEOF_VOID_P}" EQUAL 4)
            set(TIFF_SSIZE_T int32_t)
            set(TIFF_SSIZE_FORMAT "%d")
            set(TIFF_SIZE_FORMAT "%u")
        else ()
            message(FATAL_ERROR "unknown ssize_t")
        endif ()
    else ()
        set(TIFF_SSIZE_T ssize_t)
    endif ()
    set(TIFF_INT32_FORMAT "%d")
    set(TIFF_UINT32_FORMAT "%u")
    set(TIFF_INT64_FORMAT "%lld")
    set(TIFF_UINT64_FORMAT "%llu")
    set(TIFF_PTRDIFF_FORMAT "%ld")
else ()
    set(TIFF_INT8_T "signed __int8")
    set(TIFF_INT16_T "signed __int16")
    set(TIFF_INT32_T "signed __int32")
    set(TIFF_INT64_T "signed __int64")
    set(TIFF_UINT8_T "unsigned __int8")
    set(TIFF_UINT16_T "unsigned __int16")
    set(TIFF_UINT32_T "unsigned __int32")
    set(TIFF_UINT64_T "unsigned __int64")
    set(TIFF_PTRDIFF_T ptrdiff_t)
    if (NOT HAVE_SSIZE_T)
        if ("${CMAKE_SIZEOF_VOID_P}" EQUAL 8)
            set(TIFF_SSIZE_T "signed __int64")
            set(TIFF_SSIZE_FORMAT "%lld")
            set(TIFF_SIZE_FORMAT "%llu")
        elseif ("${CMAKE_SIZEOF_VOID_P}" EQUAL 4)
            set(TIFF_SSIZE_T "signed __int32")
            set(TIFF_SSIZE_FORMAT "%d")
            set(TIFF_SIZE_FORMAT "%u")
        else ()
            message(FATAL_ERROR "unknown ssize_t")
        endif ()
    else ()
        set(TIFF_SSIZE_T ssize_t)
    endif ()
    set(TIFF_INT32_FORMAT "%d")
    set(TIFF_UINT32_FORMAT "%u")
    set(TIFF_INT64_FORMAT "%lld")
    set(TIFF_UINT64_FORMAT "%llu")
    set(TIFF_PTRDIFF_FORMAT "%ld")
endif ()

#
set(VERSION "\"4.0.6\"")
set(PACKAGE_VERSION ${VERSION})
set(PACKAGE "\"tiff\"")

# universal build ?
if (APPLE)
    set(AC_APPLE_UNIVERSAL_BUILD 1)
endif ()
if (UNIX)
    set(CMAKE_REQUIRED_LIBRARIES m)
    set(HAVE_LIBM 1)
endif ()
CHECK_SYMBOL_EXISTS(floor "math.h" HAVE_FLOOR)
CHECK_SYMBOL_EXISTS(sqrt "math.h" HAVE_SQRT)
CHECK_SYMBOL_EXISTS(pow "math.h" HAVE_POW)
CHECK_SYMBOL_EXISTS(lfind "search.h" HAVE_LFIND)
CHECK_SYMBOL_EXISTS(setmod "io.h" HAVE_SETMODE)

# http://www.cmake.org/pipermail/cmake/2007-September/016285.html
foreach (KEYWORD "inline" "__inline__" "__inline")
    if (NOT DEFINED C_INLINE)
        try_compile(C_HAS_${KEYWORD} "${CMAKE_CURRENT_BINARY_DIR}"
                "${CMAKE_CURRENT_SOURCE_DIR}/test_inline.c"
                COMPILE_DEFINITIONS "-Dinline=${KEYWORD}")
        if (C_HAS_${KEYWORD})
            set(C_INLINE TRUE)
            set(INLINE_KEYWORD "${KEYWORD}")
        endif (C_HAS_${KEYWORD})
    endif (NOT DEFINED C_INLINE)
endforeach (KEYWORD)

configure_file(${CMAKE_CURRENT_SOURCE_DIR}/tiffconf.h.cmake.in
        ${CMAKE_CURRENT_BINARY_DIR}/tiffconf.h @ONLY)
configure_file(${CMAKE_CURRENT_SOURCE_DIR}/tif_config.h.cmake.in
        ${CMAKE_CURRENT_BINARY_DIR}/tif_config.h @ONLY)

set(LIBTARGET "tiff")
#
add_library(${LIBTARGET} STATIC ${TARGET_FILES})
#
set_target_properties(${LIBTARGET}
        PROPERTIES
        OUTPUT_NAME "${LIBTARGET}"
        ARCHIVE_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/thirdparty/lib
        )
