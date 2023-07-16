#!/bin/bash

set -e

if [ "$OUT" == "" ]; then
    echo "OUT env var not defined"
    exit 1
fi

SRC_DIR=$(dirname $0)/../..

cd $SRC_DIR/data/input/conformance
rm -f $OUT/opj_decompress_fuzzer_seed_corpus.zip
zip $OUT/opj_decompress_fuzzer_seed_corpus.zip *.jp2 *.j2k
cd $OLDPWD
