project(liblcms2 C)
#
include_directories("${CMAKE_CURRENT_SOURCE_DIR}/include")
#
file(GLOB SRCS src/*.c)
file(GLOB HDRS include/*.h)
#
set(LIBTARGET "lcms2")
#
add_library(${LIBTARGET} STATIC ${SRCS} ${HDRS})
#
if (MSVC)
    set_target_properties(${LIBTARGET} PROPERTIES PREFIX "lib")
endif (MSVC)
#
set_target_properties(${LIBTARGET}
        PROPERTIES
        OUTPUT_NAME "${LIBTARGET}"
        ARCHIVE_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/thirdparty/lib)
#
