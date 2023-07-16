#!/bin/bash

set -e

if [ "$SRC" == "" ]; then
    echo "SRC env var not defined"
    exit 1
fi

if [ "$OUT" == "" ]; then
    echo "OUT env var not defined"
    exit 1
fi

if [ "$CXX" == "" ]; then
    echo "CXX env var not defined"
    exit 1
fi

SRC_DIR=$(dirname $0)/../..

build_fuzzer()
{
    fuzzerName=$1
    sourceFilename=$2
    shift
    shift
    echo "Building fuzzer $fuzzerName"
    $CXX $CXXFLAGS -std=c++11 -I$SRC_DIR/src/lib/openjp2 -I$SRC_DIR/build/src/lib/openjp2 \
        $sourceFilename $* -o $OUT/$fuzzerName \
        $LIB_FUZZING_ENGINE $SRC_DIR/build/bin/libopenjp2.a -lm -lpthread
}

fuzzerFiles=$(dirname $0)/*.cpp
for F in $fuzzerFiles; do
    fuzzerName=$(basename $F .cpp)
    build_fuzzer $fuzzerName $F
done

