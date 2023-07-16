# openjp3d
include_regular_expression("^.*$")

include_directories(
        ${OPENJPEG_BINARY_DIR}/src/lib/openjp2 # opj_config.h
)

set(OPENJP3D_LIBRARY_NAME openjp3d)
# Defines the source code for the library
set(OPENJP3D_SRCS
        bio.c cio.c dwt.c event.c jp3d.c jp3d_lib.c mct.c mqc.c openjp3d.c
        pi.c raw.c t1.c t1_3d.c t2.c tcd.c tgt.c volume.c
        )

# Build the library
if (WIN32)
    if (BUILD_SHARED_LIBS)
        add_definitions(-DOPJ_EXPORTS)
    else ()
        add_definitions(-DOPJ_STATIC)
    endif ()
endif ()

# build jp3d lib:
add_library(${OPENJP3D_LIBRARY_NAME} ${OPENJP3D_SRCS})
if (UNIX)
    target_link_libraries(${OPENJP3D_LIBRARY_NAME} m)
endif ()
set_target_properties(${OPENJP3D_LIBRARY_NAME} PROPERTIES ${OPENJPEG_LIBRARY_PROPERTIES})
if (NOT ${CMAKE_VERSION} VERSION_LESS "2.8.12")
    target_compile_options(${OPENJP3D_LIBRARY_NAME} PRIVATE ${OPENJPEG_LIBRARY_COMPILE_OPTIONS})
endif ()

# Install library
install(TARGETS ${OPENJP3D_LIBRARY_NAME}
        EXPORT OpenJP3DTargets
        DESTINATION ${OPENJPEG_INSTALL_LIB_DIR}
        COMPONENT Libraries
        )

# Install includes files
install(FILES openjp3d.h
        DESTINATION ${OPENJPEG_INSTALL_INCLUDE_DIR}
        COMPONENT Headers
        )
