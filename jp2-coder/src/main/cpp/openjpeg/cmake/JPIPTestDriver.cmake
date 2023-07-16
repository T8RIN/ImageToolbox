# JPIP test driver
#message(STATUS "${D_URL}")
file(DOWNLOAD
        "${D_URL}"
        "${D_FILE}"
        LOG log
        EXPECTED_MD5 "${EXPECTED_MD5}"
        )
message(STATUS "LOG: ${log}")
