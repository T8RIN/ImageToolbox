# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.18.1)

# Declares and names the project.

project("coder")

add_library( # Sets the name of the library.
        coder
        # Sets the library as a shared library.
        SHARED
        # Provides a relative path to your source file(s).
        coder.cpp jni_exception.cpp scaler.cpp)

add_library(libaom STATIC IMPORTED)
add_library(libx265 STATIC IMPORTED)
add_library(libheif STATIC IMPORTED)
add_library(libyuv STATIC IMPORTED)
add_library(libde265 STATIC IMPORTED)
add_library(libdav1d STATIC IMPORTED)

set_target_properties(libaom PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/lib/${ANDROID_ABI}/libaom.a)
set_target_properties(libx265 PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/lib/${ANDROID_ABI}/libx265.a)
set_target_properties(libheif PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/lib/${ANDROID_ABI}/libheif.a)
set_target_properties(libyuv PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/lib/${ANDROID_ABI}/libyuv.a)
set_target_properties(libde265 PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/lib/${ANDROID_ABI}/libde265.a)
set_target_properties(libdav1d PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/lib/${ANDROID_ABI}/libdav1d.a)

add_library(cpufeatures STATIC ${ANDROID_NDK}/sources/android/cpufeatures/cpu-features.c)
target_include_directories(cpufeatures PUBLIC ${ANDROID_NDK}/sources/android/cpufeatures)
target_link_libraries(cpufeatures dl)
list(APPEND EXTRA_LIBS cpufeatures)

target_include_directories(coder PRIVATE ${CMAKE_SOURCE_DIR}/libheif ${CMAKE_SOURCE_DIR} ${CMAKE_SOURCE_DIR}/libyuv)
# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
        log-lib
        log)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        coder

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib} libaom libx265 libheif cpufeatures libyuv -ljnigraphics libde265 libdav1d)