/*
 * The copyright in this software is being made available under the 2-clauses
 * BSD License, included below. This software may be subject to other third
 * party and contributor rights, including patent rights, and no such rights
 * are granted under this license.
 *
 * Copyright (c) 2017, IntoPix SA <contact@intopix.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS `AS IS'
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

#include <stdlib.h>
#include <stdio.h>

int LLVMFuzzerTestOneInput(void *buf, size_t len);
int LLVMFuzzerInitialize(int* argc, char*** argv);

int main(int argc, char* argv[])
{
    LLVMFuzzerInitialize(&argc, &argv);
    if (argc < 2) {
        return LLVMFuzzerTestOneInput(" ", 1);
    } else {
        int nRet = 0;
        void* buf = NULL;
        int nLen = 0;
        FILE* f = fopen(argv[1], "rb");
        if (!f) {
            fprintf(stderr, "%s does not exist.\n", argv[1]);
            exit(1);
        }
        fseek(f, 0, SEEK_END);
        nLen = (int)ftell(f);
        fseek(f, 0, SEEK_SET);
        buf = malloc(nLen);
        if (!buf) {
            fprintf(stderr, "malloc failed.\n");
            fclose(f);
            exit(1);
        }
        if (fread(buf, nLen, 1, f) != 1) {
            fprintf(stderr, "fread failed.\n");
            fclose(f);
            free(buf);
            exit(1);
        }
        fclose(f);
        nRet = LLVMFuzzerTestOneInput(buf, nLen);
        free(buf);
        return nRet;
    }
}
