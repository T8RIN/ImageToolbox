# package bundler
if (EXISTS "${CMAKE_ROOT}/Modules/CPack.cmake")
    if (EXISTS "${CMAKE_ROOT}/Modules/InstallRequiredSystemLibraries.cmake")
        set(CMAKE_INSTALL_MFC_LIBRARIES 0)
        set(CMAKE_INSTALL_DEBUG_LIBRARIES 0)
        if (NOT DEFINED CMAKE_INSTALL_SYSTEM_RUNTIME_LIBS_NO_WARNINGS)
            set(CMAKE_INSTALL_SYSTEM_RUNTIME_LIBS_NO_WARNINGS ON)
        endif ()
        include(${CMAKE_ROOT}/Modules/InstallRequiredSystemLibraries.cmake)
    endif ()

    set(OPJ_PACKAGE_DESCRIPTION_SUMMARY "OpenJPEG - OpenJPEG a JPEG 2000 implementation.")
    set(OPJ_PACKAGE_CONTACT "openjpeg users <openjpeg@googlegroups.com>")

    set(CPACK_PACKAGE_DESCRIPTION_SUMMARY ${OPJ_PACKAGE_DESCRIPTION_SUMMARY})
    set(CPACK_PACKAGE_VENDOR "OpenJPEG Team")
    configure_file("${CMAKE_CURRENT_SOURCE_DIR}/LICENSE"
            "${CMAKE_CURRENT_BINARY_DIR}/LICENSE.txt" COPYONLY
            )
    # For PackageMaker on MacOSX it is important to have a file extension:
    set(CPACK_PACKAGE_DESCRIPTION_FILE "${CMAKE_CURRENT_BINARY_DIR}/LICENSE.txt")
    set(CPACK_RESOURCE_FILE_LICENSE "${CMAKE_CURRENT_BINARY_DIR}/LICENSE.txt")
    set(CPACK_PACKAGE_VERSION_MAJOR "${OPENJPEG_VERSION_MAJOR}")
    set(CPACK_PACKAGE_VERSION_MINOR "${OPENJPEG_VERSION_MINOR}")
    set(CPACK_PACKAGE_VERSION_PATCH "${OPENJPEG_VERSION_BUILD}")
    set(CPACK_PACKAGE_INSTALL_DIRECTORY "OpenJPEG ${CPACK_PACKAGE_VERSION_MAJOR}.${CPACK_PACKAGE_VERSION_MINOR}")
    set(CPACK_SOURCE_PACKAGE_FILE_NAME "openjpeg-${CPACK_PACKAGE_VERSION_MAJOR}.${CPACK_PACKAGE_VERSION_MINOR}.${CPACK_PACKAGE_VERSION_PATCH}")

    # Make this explicit here, rather than accepting the CPack default value,
    # so we can refer to it:
    set(CPACK_PACKAGE_NAME "${OPENJPEG_LIBRARY_NAME}")

    if (NOT DEFINED CPACK_SYSTEM_NAME)
        # make sure package is not Cygwin-unknown, for Cygwin just
        # cygwin is good for the system name
        if ("${CMAKE_SYSTEM_NAME}" STREQUAL "CYGWIN")
            set(CPACK_SYSTEM_NAME Cygwin)
        else ()
            set(CPACK_SYSTEM_NAME ${CMAKE_SYSTEM_NAME}-${CMAKE_SYSTEM_PROCESSOR})
        endif ()
    endif ()
    if (${CPACK_SYSTEM_NAME} MATCHES Windows)
        if (CMAKE_CL_64)
            set(CPACK_SYSTEM_NAME win64-x64)
        else ()
            set(CPACK_SYSTEM_NAME win32-x86)
        endif ()
    endif ()

    if (NOT DEFINED CPACK_PACKAGE_FILE_NAME)
        # if the CPACK_PACKAGE_FILE_NAME is not defined by the cache
        # default to source package - system, on cygwin system is not
        # needed
        if (CYGWIN)
            set(CPACK_PACKAGE_FILE_NAME "${CPACK_SOURCE_PACKAGE_FILE_NAME}")
        else ()
            set(CPACK_PACKAGE_FILE_NAME
                    "${CPACK_SOURCE_PACKAGE_FILE_NAME}-${CPACK_SYSTEM_NAME}")
        endif ()
    endif ()

    set(CPACK_BUNDLE_NAME "OpenJPEG ${CPACK_PACKAGE_VERSION_MAJOR}.${CPACK_PACKAGE_VERSION_MINOR}")
    if (APPLE)
        configure_file(${CMAKE_ROOT}/Templates/AppleInfo.plist
                ${CMAKE_CURRENT_BINARY_DIR}/opj.plist)
        set(CPACK_BUNDLE_PLIST
                ${CMAKE_CURRENT_BINARY_DIR}/opj.plist)
        #include(BundleUtilities)
    endif ()

    include(CPack)
endiF (EXISTS "${CMAKE_ROOT}/Modules/CPack.cmake")
