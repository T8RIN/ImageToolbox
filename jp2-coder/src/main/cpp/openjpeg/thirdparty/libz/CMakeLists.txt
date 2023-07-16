#based on zlib-1.2.5/CMakeLists.txt
#
cmake_minimum_required(VERSION 2.6)
set(CMAKE_ALLOW_LOOSE_LOOP_CONSTRUCTS ON)

project(zlib C)

include(CheckTypeSize)
include(CheckFunctionExists)
include(CheckIncludeFile)
include(CheckCSourceCompiles)

CHECK_INCLUDE_FILE(sys/types.h HAVE_SYS_TYPES_H)
CHECK_INCLUDE_FILE(stdint.h HAVE_STDINT_H)
CHECK_INCLUDE_FILE(stddef.h HAVE_STDDEF_H)

#
# Check to see if we have large file support
#
set(CMAKE_REQUIRED_DEFINITIONS -D_LARGEFILE64_SOURCE=1)
# We add these other definitions here because CheckTypeSize.cmake
# in CMake 2.4.x does not automatically do so and we want
# compatibility with CMake 2.4.x.
if (HAVE_SYS_TYPES_H)
    list(APPEND CMAKE_REQUIRED_DEFINITIONS -DHAVE_SYS_TYPES_H)
endif ()
if (HAVE_STDINT_H)
    list(APPEND CMAKE_REQUIRED_DEFINITIONS -DHAVE_STDINT_H)
endif ()
if (HAVE_STDDEF_H)
    list(APPEND CMAKE_REQUIRED_DEFINITIONS -DHAVE_STDDEF_H)
endif ()
CHECK_TYPE_SIZE(off64_t OFF64_T)
if (HAVE_OFF64_T)
    add_definitions(-D_LARGEFILE64_SOURCE=1)
endif ()
set(CMAKE_REQUIRED_DEFINITIONS) # clear variable
#
# Check for fseeko
#
CHECK_FUNCTION_EXISTS(fseeko HAVE_FSEEKO)
if (NOT HAVE_FSEEKO)
    add_definitions(-DNO_FSEEKO)
endif ()
#
# Check for unistd.h
#
CHECK_INCLUDE_FILE(unistd.h Z_HAVE_UNISTD_H)

include_directories(
        ${CMAKE_CURRENT_SOURCE_DIR}
        ${OPENJPEG_SOURCE_DIR}/thirdparty/include
)
#
set(EXT_HDRS
        ${OPENJPEG_SOURCE_DIR}/thirdparty/include/zlib.h
        ${OPENJPEG_SOURCE_DIR}/thirdparty/include/zconf.h
        )

set(HDRS
        crc32.h
        deflate.h
        gzguts.h
        inffast.h
        inffixed.h
        inflate.h
        inftrees.h
        trees.h
        zutil.h
        )
set(SRCS
        adler32.c
        compress.c
        crc32.c
        deflate.c
        gzclose.c
        gzlib.c
        gzread.c
        gzwrite.c
        inflate.c
        infback.c
        inftrees.c
        inffast.c
        trees.c
        uncompr.c
        zutil.c
        )

set(LIBTARGET "z")
#
add_library(${LIBTARGET} STATIC ${SRCS} ${EXT_HDRS} ${HDRS})
#
if (MSVC)
    set_target_properties(${LIBTARGET} PROPERTIES PREFIX "lib")
endif (MSVC)

set_target_properties(${LIBTARGET}
        PROPERTIES
        OUTPUT_NAME "${LIBTARGET}"
        ARCHIVE_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/thirdparty/lib
        )
#
