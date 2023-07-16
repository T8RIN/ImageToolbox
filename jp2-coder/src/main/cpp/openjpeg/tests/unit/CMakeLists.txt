# UNIT TESTS

include_directories(
        ${OPENJPEG_BINARY_DIR}/src/lib/openjp2 # opj_config.h
        ${OPENJPEG_SOURCE_DIR}/src/lib/openjp2
)

set(unit_test
        testempty0
        testempty1
        testempty2
        )
foreach (ut ${unit_test})
    add_executable(${ut} ${ut}.c)
    target_link_libraries(${ut} openjp2)
    add_test(NAME ${ut} COMMAND ${ut})
endforeach ()
