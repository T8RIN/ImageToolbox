# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html.
# For more examples on how to use CMake, see https://github.com/android/ndk-samples.

# Sets the minimum CMake version required for this project.
cmake_minimum_required(VERSION 3.22.1)

# Declares the project name. The project name can be accessed via ${ PROJECT_NAME},
# Since this is the top level CMakeLists.txt, the project name is also accessible
# with ${CMAKE_PROJECT_NAME} (both CMake variables are in-sync within the top level
# build script scope).
project("jxlcoder")

add_library(libbrotlicommon SHARED IMPORTED)
add_library(libbrotlidec SHARED IMPORTED)
add_library(libbrotlienc SHARED IMPORTED)
add_library(libjxl SHARED IMPORTED)
add_library(libjxl_threads SHARED IMPORTED)
add_library(libyuv STATIC IMPORTED)

add_library(cpufeatures STATIC ${ANDROID_NDK}/sources/android/cpufeatures/cpu-features.c)
target_include_directories(cpufeatures PUBLIC ${ANDROID_NDK}/sources/android/cpufeatures)
target_link_libraries(cpufeatures dl)
list(APPEND EXTRA_LIBS cpufeatures)

add_library(${CMAKE_PROJECT_NAME} SHARED
        # List C/C++ source files with relative paths to this CMakeLists.txt.
        jxlcoder.cpp)

target_include_directories("jxlcoder" PRIVATE ${CMAKE_SOURCE_DIR}/jxl ${CMAKE_SOURCE_DIR} ${CMAKE_SOURCE_DIR}/libyuv)

set_target_properties(jxlcoder libbrotlicommon PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/lib/${ANDROID_ABI}/libbrotlicommon.so)
set_target_properties(jxlcoder libbrotlidec PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/lib/${ANDROID_ABI}/libbrotlidec.so)
set_target_properties(jxlcoder libbrotlienc PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/lib/${ANDROID_ABI}/libbrotlienc.so)
set_target_properties(jxlcoder libjxl PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/lib/${ANDROID_ABI}/libjxl.so)
set_target_properties(jxlcoder libjxl_threads PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/lib/${ANDROID_ABI}/libjxl_threads.so)
set_target_properties(libyuv PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/lib/${ANDROID_ABI}/libyuv.a)

# Specifies libraries CMake should link to your target library. You
# can link libraries from various origins, such as libraries defined in this
# build script, prebuilt third-party libraries, or Android system libraries.
target_link_libraries(${CMAKE_PROJECT_NAME}
        # List libraries link to the target library
        android
        cpufeatures
        libbrotlicommon
        libbrotlidec
        libbrotlienc
        libjxl
        libjxl_threads
        -ljnigraphics
        libyuv
        log)