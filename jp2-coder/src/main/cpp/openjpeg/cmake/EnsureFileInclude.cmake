# Ensure that an include file is provided by the system
# Add the check about the mandatory status to the check_include_file macro
# provided by cmake

include(${CMAKE_ROOT}/Modules/CheckIncludeFile.cmake)

macro(ensure_file_include INCLUDE_FILENAME VARIABLE_NAME MANDATORY_STATUS)

    #message(WARNING "INCLUDE_FILENAME=${INCLUDE_FILENAME} \n"
    #                "VARIABLE_NAME=${VARIABLE_NAME} \n"
    #                "MANDATORY_STATUS=${MANDATORY_STATUS}")

    CHECK_INCLUDE_FILE(${INCLUDE_FILENAME} ${VARIABLE_NAME})

    #message(WARNING "INCLUDE_FILENAME=${INCLUDE_FILENAME} \n"
    #                "VARIABLE_NAME=${VARIABLE_NAME} \n"
    #                "VARIABLE_NAME_VALUE=${${VARIABLE_NAME}} \n"
    #                "MANDATORY_STATUS=${MANDATORY_STATUS}")

    if (NOT ${${VARIABLE_NAME}})
        if (${MANDATORY_STATUS})
            message(FATAL_ERROR "The file ${INCLUDE_FILENAME} is mandatory but not found on your system")
        endif ()
    endif ()

endmacro()
