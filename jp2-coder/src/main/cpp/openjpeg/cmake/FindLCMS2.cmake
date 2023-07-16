# - Find LCMS2 library
# Find the native LCMS2 includes and library
# Once done this will define
#
#  LCMS2_INCLUDE_DIR    - Where to find lcms2.h, etc.
#  LCMS2_LIBRARIES      - Libraries to link against to use LCMS2.
#  LCMS2_FOUND          - If false, do not try to use LCMS2.
#
# also defined, but not for general use are
#  LCMS2_LIBRARY        - Where to find the LCMS2 library.

#=============================================================================
#=============================================================================

find_path(LCMS2_INCLUDE_DIR lcms2.h PATHS /usr/include /usr/local/include /opt/include /opt/local/include)

set(LCMS2_NAMES ${LCMS2_NAMES} lcms2 liblcms2 liblcms2_static)

find_library(LCMS2_LIBRARY NAMES ${LCMS2_NAMES})

mark_as_advanced(LCMS2_INCLUDE_DIR LCMS2_LIBRARY)

# handle the QUIETLY and REQUIRED arguments and set LCMS2_FOUND to TRUE if
# all listed variables are TRUE
include(FindPackageHandleStandardArgs)
FIND_PACKAGE_HANDLE_STANDARD_ARGS(LCMS2 DEFAULT_MSG LCMS2_LIBRARY LCMS2_INCLUDE_DIR)

if (LCMS2_FOUND)
    set(LCMS2_INCLUDE_DIRS ${LCMS2_INCLUDE_DIR})
    set(LCMS2_LIBRARIES ${LCMS2_LIBRARY})
endif ()
