# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -DUSE_JPIP")

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
        openjpeg

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        src/main/cpp/openjpeg/src/lib/openjp2/bio.c
        src/main/cpp/openjpeg/src/lib/openjp2/cidx_manager.c
        src/main/cpp/openjpeg/src/lib/openjp2/cio.c
        src/main/cpp/openjpeg/src/lib/openjp2/dwt.c
        src/main/cpp/openjpeg/src/lib/openjp2/event.c
        src/main/cpp/openjpeg/src/lib/openjp2/function_list.c
        src/main/cpp/openjpeg/src/lib/openjp2/image.c
        src/main/cpp/openjpeg/src/lib/openjp2/invert.c
        src/main/cpp/openjpeg/src/lib/openjp2/j2k.c
        src/main/cpp/openjpeg/src/lib/openjp2/jp2.c
        src/main/cpp/openjpeg/src/lib/openjp2/mct.c
        src/main/cpp/openjpeg/src/lib/openjp2/mqc.c
        src/main/cpp/openjpeg/src/lib/openjp2/openjpeg.c
        src/main/cpp/openjpeg/src/lib/openjp2/opj_clock.c
        src/main/cpp/openjpeg/src/lib/openjp2/opj_malloc.c
        src/main/cpp/openjpeg/src/lib/openjp2/phix_manager.c
        src/main/cpp/openjpeg/src/lib/openjp2/pi.c
        src/main/cpp/openjpeg/src/lib/openjp2/ppix_manager.c
        src/main/cpp/openjpeg/src/lib/openjp2/sparse_array.c
        src/main/cpp/openjpeg/src/lib/openjp2/t1.c
        src/main/cpp/openjpeg/src/lib/openjp2/t2.c
        src/main/cpp/openjpeg/src/lib/openjp2/tcd.c
        src/main/cpp/openjpeg/src/lib/openjp2/tgt.c
        src/main/cpp/openjpeg/src/lib/openjp2/thix_manager.c
        src/main/cpp/openjpeg/src/lib/openjp2/thread.c
        src/main/cpp/openjpeg/src/lib/openjp2/tpix_manager.c
        src/main/cpp/openjpeg/src/bin/common/color.c
        src/main/cpp/openjpg.cpp
        )

target_include_directories(openjpeg PRIVATE src/main/cpp src/main/cpp/openjpeg/src/lib/openjp2 src/main/cpp/openjpeg/src/bin/common)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        openjpeg

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})