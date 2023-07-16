#----------------------------------------------------------------------
#
# For further details regarding this file,
# see http://www.cmake.org/Wiki/CMake_Testing_With_CTest#Customizing_CTest
#
# and
# http://www.kitware.com/blog/home/post/27
#
#----------------------------------------------------------------------

set(CTEST_CUSTOM_MAXIMUM_PASSED_TEST_OUTPUT_SIZE 1000000)
set(CTEST_CUSTOM_MAXIMUM_NUMBER_OF_ERRORS   50)
set(CTEST_CUSTOM_MAXIMUM_NUMBER_OF_WARNINGS 2000)

set(CTEST_CUSTOM_COVERAGE_EXCLUDE
  ${CTEST_CUSTOM_COVERAGE_EXCLUDE}

  # Exclude files from the Testing directories
  ".*/tests/.*"

  # Exclude files from the ThirdParty Utilities directories
  ".*/thirdparty/.*"
  )

set(CTEST_CUSTOM_WARNING_EXCEPTION
  ${CTEST_CUSTOM_WARNING_EXCEPTION}

  # Ignore clang's summary warning, assuming prior text has matched some
  # other warning expression:
  "[0-9,]+ warnings? generated."
  # Suppress warning caused by intentional messages about deprecation
  ".*warning,.* is deprecated"
  # java also warns about deprecated API
  ".*java.*deprecation"
  ".*deprecation.*"
  # suppress warnings caused by 3rd party libs:
  ".*thirdparty.*"
  "libtiff.*has no symbols"
  "libpng.*has no symbols"
  )
