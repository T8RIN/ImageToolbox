#
# this module looks for JPYLYZER
# http://jpylyzer.openpreservation.org
#

find_program(JPYLYZER_EXECUTABLE
        jpylyzer
        )

mark_as_advanced(
        JPYLYZER_EXECUTABLE
)
