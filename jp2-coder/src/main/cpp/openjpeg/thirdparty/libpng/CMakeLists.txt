project(libpng C)

include_directories(
        "${CMAKE_CURRENT_SOURCE_DIR}"
        ${OPENJPEG_SOURCE_DIR}/thirdparty/include
)

file(GLOB SRCS *.c)
file(GLOB HDRS *.h)
set(EXT_HDRS
        ${OPENJPEG_SOURCE_DIR}/thirdparty/include/zlib.h
        ${OPENJPEG_SOURCE_DIR}/thirdparty/include/zconf.h
        )
#
set(LIBTARGET "png")
#
add_library(${LIBTARGET} STATIC ${SRCS} ${HDRS} ${EXT_HDRS})
#
if (MSVC)
    set_target_properties(${LIBTARGET} PROPERTIES PREFIX "lib")
endif (MSVC)
#
target_link_libraries(${LIBTARGET} ${Z_LIBNAME} ${M_LIBRARY})
#
set_target_properties(${LIBTARGET}
        PROPERTIES
        OUTPUT_NAME "${LIBTARGET}"
        ARCHIVE_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/thirdparty/lib)
#
