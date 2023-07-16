/*
 * The copyright in this software is being made available under the 2-clauses
 * BSD License, included below. This software may be subject to other third
 * party and contributor rights, including patent rights, and no such rights
 * are granted under this license.
 *
 * Copyright (c) 2017, IntoPIX SA <support@intopix.com>
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

#define CPUID_SSSE3_ECX_BIT     9
#define CPUID_OSXSAVE_ECX_BIT   27
#define CPUID_AVX_ECX_BIT       28

#define CPUID_AVX2_EBX_BIT      5

#define CPUID_SSE_EDX_BIT       25

#define BIT_XMM_STATE           (1 << 1)
#define BIT_YMM_STATE           (2 << 1)

#define REG_EAX                 0
#define REG_EBX                 1
#define REG_ECX                 2
#define REG_EDX                 3

#if defined(__GNUC__) && (defined(__i386__) ||defined(__x86_64))

#include <cpuid.h>

#define CPL_CPUID(level, subfunction, array) __cpuid_count(level, subfunction, array[0], array[1], array[2], array[3])

#elif defined(_MSC_VER) && (defined(_M_IX86) || defined(_M_X64))

#include <intrin.h>

#define CPL_CPUID(level, subfunction, array) __cpuidex(array, level, subfunction)

#else

#error "not supported"

#endif

#if defined(__GNUC__) && (defined(__i386__) ||defined(__x86_64))

int CPLHaveRuntimeAVX()
{
    int cpuinfo[4] = { 0, 0, 0, 0 };
    unsigned int nXCRLow;
    unsigned int nXCRHigh;

    CPL_CPUID(1, 0, cpuinfo);

    // Check OSXSAVE feature.
    if ((cpuinfo[REG_ECX] & (1 << CPUID_OSXSAVE_ECX_BIT)) == 0) {
        return 0;
    }

    // Check AVX feature.
    if ((cpuinfo[REG_ECX] & (1 << CPUID_AVX_ECX_BIT)) == 0) {
        return 0;
    }

    // Issue XGETBV and check the XMM and YMM state bit.

    __asm__("xgetbv" : "=a"(nXCRLow), "=d"(nXCRHigh) : "c"(0));
    if ((nXCRLow & (BIT_XMM_STATE | BIT_YMM_STATE)) !=
            (BIT_XMM_STATE | BIT_YMM_STATE)) {
        return 0;
    }

    return 1;
}

#elif defined(_MSC_FULL_VER) && (_MSC_FULL_VER >= 160040219) && (defined(_M_IX86) || defined(_M_X64))
// _xgetbv available only in Visual Studio 2010 SP1 or later

int CPLHaveRuntimeAVX()
{
    int cpuinfo[4] = { 0, 0, 0, 0 };
    unsigned __int64 xcrFeatureMask;

    CPL_CPUID(1, 0, cpuinfo);

    // Check OSXSAVE feature.
    if ((cpuinfo[REG_ECX] & (1 << CPUID_OSXSAVE_ECX_BIT)) == 0) {
        return 0;
    }

    // Check AVX feature.
    if ((cpuinfo[REG_ECX] & (1 << CPUID_AVX_ECX_BIT)) == 0) {
        return 0;
    }

    // Issue XGETBV and check the XMM and YMM state bit.
    xcrFeatureMask = _xgetbv(_XCR_XFEATURE_ENABLED_MASK);
    if ((xcrFeatureMask & (BIT_XMM_STATE | BIT_YMM_STATE)) !=
            (BIT_XMM_STATE | BIT_YMM_STATE)) {
        return 0;
    }

    return 1;
}

#endif

int CPLHaveRuntimeAVX2()
{
    int cpuinfo[4] = { 0, 0, 0, 0 };
    if (!CPLHaveRuntimeAVX()) {
        return 0;
    }

    CPL_CPUID(7, 0, cpuinfo);

    // Check AVX2 feature.
    if ((cpuinfo[REG_EBX] & (1 << CPUID_AVX2_EBX_BIT)) == 0) {
        return 0;
    }

    return 1;
}

int main()
{
    if (CPLHaveRuntimeAVX2()) {
        return 0;
    }
    return 1;
}
