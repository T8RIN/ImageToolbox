default: dummyfuzzers

clean:
	$(RM) -f *.o *.a

fuzzingengine.o: fuzzingengine.c
	$(CC) $(CFLAGS) -c -o $@ $<

dummyfuzzers: fuzzingengine.o
	$(AR) r libFuzzingEngine.a fuzzingengine.o
	LIB_FUZZING_ENGINE="-lFuzzingEngine" CXX="${CXX}" CXXFLAGS="-L. ${CXXFLAGS}" \
	    SRC=/tmp OUT=/tmp ./build_google_oss_fuzzers.sh
	OUT=/tmp ./build_seed_corpus.sh
