#  Copyright (c) 2014 Mathieu Malaterre <mathieu.malaterre@voxxl.com>
#
#  Redistribution and use is allowed according to the terms of the New
#  BSD license.
#  For details see the accompanying COPYING-CMAKE-SCRIPTS file.

# check md5 refs
#
# This script will be used to make sure we never introduce a regression on any
# of the nonregression file.
#
# The approach is relatively simple, we compute a md5sum for each of the decode
# file. Anytime the md5sum is different from the reference one, we assume
# something went wrong and simply fails.  of course if could happen during the
# course of openjpeg development that the internals are changed that impact the
# decoding process that the output would be bitwise different (while PSNR would
# be kept identical).

# Another more conventional approach is to store the generated output from
# openjpeg however storing the full generated output is generally useless since
# we do not really care about the exact pixel value, we simply need to known
# when a code change impact output generation.  furthermore storing the
# complete generated output file, tends to make the svn:/openjpeg-data really
# big.

# This script expect two inputs
# REFFILE: Path to the md5sum.txt file
# OUTFILENAME: The name of the generated file we want to check The script will
# check whether a PGX or a PNG file was generated in the test suite (computed
# from OUTFILENAME)

get_filename_component(OUTFILENAME_NAME ${OUTFILENAME} NAME)
string(FIND ${OUTFILENAME_NAME} "." SHORTEST_EXT_POS REVERSE)
string(SUBSTRING ${OUTFILENAME_NAME} 0 ${SHORTEST_EXT_POS} OUTFILENAME_NAME_WE)
file(GLOB globfiles "Temporary/${OUTFILENAME_NAME_WE}*.pgx" "Temporary/${OUTFILENAME_NAME_WE}*.png" "Temporary/${OUTFILENAME_NAME_WE}*.tif")
if (NOT globfiles)
    message(SEND_ERROR "Could not find output PGX files: ${OUTFILENAME_NAME_WE}")
endif ()

# REFFILE follow what `md5sum -c` would expect as input:
file(READ ${REFFILE} variable)

foreach (pgxfullpath ${globfiles})
    file(MD5 ${pgxfullpath} output)
    get_filename_component(pgxfile ${pgxfullpath} NAME)

    string(REGEX MATCH "[0-9a-f]+  ${pgxfile}" output_var "${variable}")

    # Search for variant md5sum for libtiff >= 4.1
    string(REGEX MATCH "libtiff_4_1:[0-9a-f]+  ${pgxfile}" alternate_output_var "${variable}")

    set(output "${output}  ${pgxfile}")

    if ("${output_var}" STREQUAL "${output}")
        message(STATUS "equal: [${output_var}] vs [${output}]")
    elseif ("${alternate_output_var}" STREQUAL "libtiff_4_1:${output}")
        message(STATUS "equal: [${alternate_output_var}] vs [libtiff_4_1:${output}]")
    else ()
        message(SEND_ERROR "not equal: [${output_var}] vs [${output}]")
    endif ()
endforeach ()
