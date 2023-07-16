# Tests
include_directories(
        ${OPENJPEG_BINARY_DIR}/src/lib/openjp2 # opj_config.h
        ${OPENJPEG_BINARY_DIR}/src/bin/common # opj_apps_config.h
        ${OPENJPEG_SOURCE_DIR}/src/lib/openjp2
        ${OPENJPEG_SOURCE_DIR}/src/bin/jp2
        ${OPENJPEG_SOURCE_DIR}/src/bin/common
        ${Z_INCLUDE_DIRNAME}
        ${PNG_INCLUDE_DIRNAME}
        ${TIFF_INCLUDE_DIRNAME}
)

# First thing define the common source:
set(compare_images_SRCS compare_images.c
        ${OPENJPEG_SOURCE_DIR}/src/bin/jp2/convert.c
        ${OPENJPEG_SOURCE_DIR}/src/bin/jp2/converttif.c
        ${OPENJPEG_SOURCE_DIR}/src/bin/common/opj_getopt.c
        )

set(compare_dump_files_SRCS compare_dump_files.c
        ${OPENJPEG_SOURCE_DIR}/src/bin/common/opj_getopt.c)

set(compare_raw_files_SRCS compare_raw_files.c
        ${OPENJPEG_SOURCE_DIR}/src/bin/common/opj_getopt.c)

add_executable(compare_images ${compare_images_SRCS})
target_link_libraries(compare_images
        ${OPENJPEG_LIBRARY_NAME}
        ${PNG_LIBNAME} ${TIFF_LIBNAME}
        )
# To support universal exe:
if (ZLIB_FOUND AND APPLE)
    target_link_libraries(compare_images z)
else (ZLIB_FOUND AND APPLE)
    target_link_libraries(compare_images ${Z_LIBNAME})
endif ()

add_executable(compare_dump_files ${compare_dump_files_SRCS})

add_executable(j2k_random_tile_access j2k_random_tile_access.c)
target_link_libraries(j2k_random_tile_access ${OPENJPEG_LIBRARY_NAME})

add_executable(compare_raw_files ${compare_raw_files_SRCS})

add_executable(test_tile_encoder test_tile_encoder.c)
target_link_libraries(test_tile_encoder ${OPENJPEG_LIBRARY_NAME})

add_executable(test_decode_area test_decode_area.c)
target_link_libraries(test_decode_area ${OPENJPEG_LIBRARY_NAME})

# Let's try a couple of possibilities:
add_test(NAME tte0 COMMAND test_tile_encoder)
add_test(NAME tte1 COMMAND test_tile_encoder 3 2048 2048 1024 1024 8 1 tte1.j2k)
add_test(NAME tte2 COMMAND test_tile_encoder 3 2048 2048 1024 1024 8 1 tte2.jp2)
add_test(NAME tte3 COMMAND test_tile_encoder 1 2048 2048 1024 1024 8 1 tte3.j2k)
add_test(NAME tte4 COMMAND test_tile_encoder 1 256 256 128 128 8 0 tte4.j2k)
add_test(NAME tte5 COMMAND test_tile_encoder 1 512 512 256 256 8 0 tte5.j2k)
#add_test(NAME tte6 COMMAND test_tile_encoder 1 8192 8192  512  512 8 0 tte6.j2k)
#add_test(NAME tte7 COMMAND test_tile_encoder 1 32768 32768 512  512 8 0 tte7.jp2)

add_executable(test_tile_decoder test_tile_decoder.c)
target_link_libraries(test_tile_decoder ${OPENJPEG_LIBRARY_NAME})

add_test(NAME ttd0 COMMAND test_tile_decoder)
set_property(TEST ttd0 APPEND PROPERTY DEPENDS tte0)
add_test(NAME ttd1 COMMAND test_tile_decoder 0 0 1024 1024 tte1.j2k)
set_property(TEST ttd1 APPEND PROPERTY DEPENDS tte1)
add_test(NAME ttd2 COMMAND test_tile_decoder 0 0 1024 1024 tte2.jp2)
set_property(TEST ttd2 APPEND PROPERTY DEPENDS tte2)
#add_test(NAME ttd6 COMMAND test_tile_decoder 0 0  512  512 tte6.j2k)
#set_property(TEST ttd6 APPEND PROPERTY DEPENDS tte6)
#add_test(NAME ttd7 COMMAND test_tile_decoder 0 0  512  512 tte7.jp2)
#set_property(TEST ttd7 APPEND PROPERTY DEPENDS tte7)

add_test(NAME rta1 COMMAND j2k_random_tile_access tte1.j2k)
set_property(TEST rta1 APPEND PROPERTY DEPENDS tte1)
add_test(NAME rta2 COMMAND j2k_random_tile_access tte2.jp2)
set_property(TEST rta2 APPEND PROPERTY DEPENDS tte2)
add_test(NAME rta3 COMMAND j2k_random_tile_access tte3.j2k)
set_property(TEST rta3 APPEND PROPERTY DEPENDS tte3)
add_test(NAME rta4 COMMAND j2k_random_tile_access tte4.j2k)
set_property(TEST rta4 APPEND PROPERTY DEPENDS tte4)
add_test(NAME rta5 COMMAND j2k_random_tile_access tte5.j2k)
set_property(TEST rta5 APPEND PROPERTY DEPENDS tte5)

add_test(NAME tda_prep_reversible_no_precinct COMMAND test_tile_encoder 1 256 256 32 32 8 0 reversible_no_precinct.j2k 4 4 3 0 0 1)
add_test(NAME tda_reversible_no_precinct COMMAND test_decode_area -q reversible_no_precinct.j2k)
set_property(TEST tda_reversible_no_precinct APPEND PROPERTY DEPENDS tda_prep_reversible_no_precinct)

add_test(NAME tda_prep_reversible_203_201_17_19_no_precinct COMMAND test_tile_encoder 1 203 201 17 19 8 0 reversible_203_201_17_19_no_precinct.j2k 4 4 3 0 0 1)
add_test(NAME tda_reversible_203_201_17_19_no_precinct COMMAND test_decode_area -q reversible_203_201_17_19_no_precinct.j2k)
set_property(TEST tda_reversible_203_201_17_19_no_precinct APPEND PROPERTY DEPENDS tda_prep_reversible_203_201_17_19_no_precinct)

add_test(NAME tda_prep_reversible_with_precinct COMMAND test_tile_encoder 1 256 256 32 32 8 0 reversible_with_precinct.j2k 4 4 3 0 0 1 16 16)
add_test(NAME tda_reversible_with_precinct COMMAND test_decode_area -q reversible_with_precinct.j2k)
set_property(TEST tda_reversible_with_precinct APPEND PROPERTY DEPENDS tda_prep_reversible_with_precinct)

add_test(NAME tda_prep_irreversible_no_precinct COMMAND test_tile_encoder 1 256 256 32 32 8 1 irreversible_no_precinct.j2k 4 4 3 0 0 1)
add_test(NAME tda_irreversible_no_precinct COMMAND test_decode_area -q irreversible_no_precinct.j2k)
set_property(TEST tda_irreversible_no_precinct APPEND PROPERTY DEPENDS tda_prep_irreversible_no_precinct)

add_test(NAME tda_prep_irreversible_203_201_17_19_no_precinct COMMAND test_tile_encoder 1 203 201 17 19 8 1 irreversible_203_201_17_19_no_precinct.j2k 4 4 3 0 0 1)
add_test(NAME tda_irreversible_203_201_17_19_no_precinct COMMAND test_decode_area -q irreversible_203_201_17_19_no_precinct.j2k)
set_property(TEST tda_irreversible_203_201_17_19_no_precinct APPEND PROPERTY DEPENDS tda_prep_irreversible_203_201_17_19_no_precinct)

add_test(NAME tda_prep_strip COMMAND test_tile_encoder 1 256 256 256 256 8 0 tda_single_tile.j2k)
add_test(NAME tda_strip COMMAND test_decode_area -q -strip_height 3 -strip_check tda_single_tile.j2k)
set_property(TEST tda_strip APPEND PROPERTY DEPENDS tda_prep_strip)

add_executable(include_openjpeg include_openjpeg.c)

# No image send to the dashboard if lib PNG is not available.
if (NOT OPJ_HAVE_LIBPNG)
    message(WARNING "Lib PNG seems to be not available: if you want run the non-regression tests with images reported to the dashboard, you need it (try BUILD_THIRDPARTY)")
endif ()

add_subdirectory(conformance)
add_subdirectory(nonregression)
add_subdirectory(unit)

if (BUILD_JPIP)
    if (JPIP_SERVER)
        #set(s "http://jpip.example.com/myFCGI?target=16.jp2&fsiz=170,170&cnew=http&type=jpp-stream")
        set(s "${JPIP_SERVER}?target=16.jp2&fsiz=170,170&cnew=http&type=jpp-stream")
        set(p "${CMAKE_CURRENT_BINARY_DIR}/jpip.dat")
        set(md5 "d41d8cd98f00b204e9800998ecf8427e")
        add_test(NAME TestJPIP1 COMMAND ${CMAKE_COMMAND} -DD_URL:STRING=${s} -DD_FILE:PATH=${p}
                -DEXPECTED_MD5=${md5} -P ${PROJECT_SOURCE_DIR}/cmake/JPIPTestDriver.cmake)
    endif ()
endif ()

add_executable(ppm2rgb3 ppm2rgb3.c)
#add_executable(pdf2jp2 pdf2jp2.c)
