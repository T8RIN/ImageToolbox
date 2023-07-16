#JavaOpenJPEG/CMakeLists.txt

# First thing define the common source:
set(openjpegjni_SRCS
        JavaOpenJPEGDecoder.c
        JavaOpenJPEG.c
        ${OPENJPEG_SOURCE_DIR}/src/bin/common/opj_getopt.c
        ${OPENJPEG_SOURCE_DIR}/src/bin/jp2/convert.c
        index.c
        )

# JNI binding:
find_package(JNI REQUIRED)
include_directories(${JNI_INCLUDE_DIRS})

# required header file:
include_directories(
        ${OPENJPEG_BINARY_DIR}/src/lib/openjp2 # opj_config.h
        ${OPENJPEG_SOURCE_DIR}/src/lib/openmj2
        ${OPENJPEG_SOURCE_DIR}/src/bin/common
        ${OPENJPEG_SOURCE_DIR}/src/bin/jp2
)

add_library(openjpegjni MODULE
        ${openjpegjni_SRCS}
        )
# Java module should not have a SONAME:
set_property(TARGET openjpegjni PROPERTY NO_SONAME 1)

# FIXME (need to use old API):
if (BUILD_MJ2)
    target_link_libraries(openjpegjni openmj2)
endif ()
target_link_libraries(openjpegjni
        ${PNG_LIBNAME} ${TIFF_LIBNAME} ${LCMS_LIBNAME} ${Z_LIBNAME}
        )
if (UNIX)
    target_link_libraries(openjpegjni m)
endif ()

install(TARGETS openjpegjni
        EXPORT OpenJPEGTargets
        LIBRARY DESTINATION ${OPENJPEG_INSTALL_JNI_DIR} COMPONENT Libraries
        )

# build jar:
find_package(Java 1.6 REQUIRED) # javac, jar

# build dep list:
file(GLOB java_srcs "java-sources/org/openJpeg/*.java")

# make sure target javac dir exists:
file(MAKE_DIRECTORY ${CMAKE_CURRENT_BINARY_DIR}/classes)
# Build java
add_custom_command(
        OUTPUT ${LIBRARY_OUTPUT_PATH}/openjpeg.jar
        COMMAND ${Java_JAVAC_EXECUTABLE} -sourcepath "${CMAKE_CURRENT_SOURCE_DIR}/java-sources"
        ${java_srcs} -d ${CMAKE_CURRENT_BINARY_DIR}/classes
        COMMAND ${Java_JAR_EXECUTABLE} cvf ${LIBRARY_OUTPUT_PATH}/openjpeg.jar org
        WORKING_DIRECTORY ${CMAKE_CURRENT_BINARY_DIR}/classes
        DEPENDS ${java_srcs}
        COMMENT "javac *.java; jar cvf -> openjpeg.jar"
)

# name the target
add_custom_target(OpenJPEGJavaJar ALL
        DEPENDS ${LIBRARY_OUTPUT_PATH}/openjpeg.jar
        COMMENT "building openjpeg.jar"
        )

install(FILES ${LIBRARY_OUTPUT_PATH}/openjpeg.jar
        DESTINATION ${OPENJPEG_INSTALL_SHARE_DIR} COMPONENT JavaModule
        )
