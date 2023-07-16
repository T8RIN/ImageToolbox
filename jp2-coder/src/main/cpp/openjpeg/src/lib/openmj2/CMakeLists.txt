# openmj2:
set(OPENMJ2_LIBRARY_NAME openmj2)

include_directories(
        ${OPENJPEG_BINARY_DIR}/src/lib/openjp2 # opj_config.h
)

set(OPENMJ2_SRCS
        mj2.c
        mj2_convert.c
        # FIXME: legacy
        j2k_lib.c
        cio.c
        jp2.c
        j2k.c
        tcd.c
        mct.c
        t1.c
        t2.c
        pi.c
        bio.c
        mqc.c
        tgt.c
        dwt.c
        event.c
        image.c
        jpt.c
        raw.c
        openjpeg.c
        )

# Build the library
if (WIN32)
    if (BUILD_SHARED_LIBS)
        add_definitions(-DOPJ_EXPORTS)
    else ()
        add_definitions(-DOPJ_STATIC)
    endif ()
endif ()

# build mj2 lib:
add_library(${OPENMJ2_LIBRARY_NAME} ${OPENMJ2_SRCS})
# FIXME: We are using an openjpeg 1.5 copy:
#target_link_libraries(${OPENMJ2_LIBRARY_NAME} openjp2)
if (UNIX)
    target_link_libraries(${OPENMJ2_LIBRARY_NAME} m)
endif ()
set_target_properties(${OPENMJ2_LIBRARY_NAME} PROPERTIES ${OPENJPEG_LIBRARY_PROPERTIES})
if (NOT ${CMAKE_VERSION} VERSION_LESS "2.8.12")
    target_compile_options(${OPENMJ2_LIBRARY_NAME} PRIVATE ${OPENJPEG_LIBRARY_COMPILE_OPTIONS})
endif ()

# Install library
install(TARGETS ${OPENMJ2_LIBRARY_NAME}
        EXPORT OpenMJ2Targets
        DESTINATION ${OPENJPEG_INSTALL_LIB_DIR}
        COMPONENT Libraries
        )

# Install includes files
#install(FILES mj2.h
#  DESTINATION ${OPENJPEG_INSTALL_INCLUDE_DIR}/${subdir}
#  COMPONENT Headers
#)
