# - Find LCMS library
# Find the native LCMS includes and library
# Once done this will define
#
#  LCMS_INCLUDE_DIR    - Where to find lcms.h, etc.
#  LCMS_LIBRARIES      - Libraries to link against to use LCMS.
#  LCMS_FOUND          - If false, do not try to use LCMS.
#
# also defined, but not for general use are
#  LCMS_LIBRARY, where to find the LCMS library.

#=============================================================================
#=============================================================================

find_path(LCMS_INCLUDE_DIR lcms.h PATHS /usr/include /usr/local/include /opt/include /opt/local/include)

set(LCMS_NAMES ${LCMS_NAMES} lcms liblcms liblcms_static)

find_library(LCMS_LIBRARY NAMES ${LCMS_NAMES})

mark_as_advanced(LCMS_INCLUDE_DIR LCMS_LIBRARY)

# handle the QUIETLY and REQUIRED arguments and set LCMS_FOUND to TRUE if
# all listed variables are TRUE
include(FindPackageHandleStandardArgs)
FIND_PACKAGE_HANDLE_STANDARD_ARGS(LCMS DEFAULT_MSG LCMS_LIBRARY LCMS_INCLUDE_DIR)

if (LCMS_FOUND)
    set(LCMS_INCLUDE_DIRS ${LCMS_INCLUDE_DIR})
    set(LCMS_LIBRARIES ${LCMS_LIBRARY})
endif ()
