This directory contain fuzzer main functions and scripts for the
Google OSS Fuzz project: https://github.com/google/oss-fuzz/

The main build scripts are in:
https://github.com/google/oss-fuzz/tree/master/projects/openjpeg
and call scripts in this directory.

The list of issues is in:
https://bugs.chromium.org/p/oss-fuzz/issues/list?q=openjpeg


- Simulate the build of (dummy) fuzzers like OSS Fuzz does:

   Preliminary steps:
    $ cd ${ROOT_OF_OPENJPEG}
    $ git clone --depth 1 https://github.com/uclouvain/openjpeg-data data
    $ mkdir build
    $ cd build
    $ cmake ..
    $ make
    $ cd ..

   Actual building of fuzzer and seed corpus:
    $ cd tests/fuzzers
    $ make

  They are created in /tmp/*_fuzzer as well as with the
  /tmp/*_fuzzer_seed_corpus.zip files

  Run one:
    $ /tmp/opj_decompress_fuzzer a_file_name

- Run locally OSS Fuzz:
    $ git clone https://github.com/google/oss-fuzz.git
    $ cd oss-fuzz
    $ python infra/helper.py build_image openjpeg

  Build fuzzers with the address sanitizer (could use undefined, etc...)
    $ python infra/helper.py build_fuzzers --sanitizer address openjpeg

  Test a particular fuzzer (replace opj_decompress_fuzzer by other fuzzers
  like the ones generated in /tmp by "make dummyfuzzers")
    $ python infra/helper.py run_fuzzer openjpeg opj_decompress_fuzzer


How to deal with issues reported in https://bugs.chromium.org/p/oss-fuzz/issues/list?q=openjpeg ?

    1. Leave a comment in (chromium database) bug entry to indicate that you work on it
    2. Work
    3. Commit a bug fix with log including "Credit to OSS-Fuzz" and a link to the bugs.chromium.org ticket
    4. Add in the bugs.chromium.org ticket a link to the github commit implementing the fix.
    5. Check chromium closed the bug (after one or two days typically)
