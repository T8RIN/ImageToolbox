/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package com.t8rin.archive

internal const val EncryptedRar =
    "UmFyIRoHAQAYOJrPIQQAAAEPprqRs1Vs70VeAnJr65GiUWzJnBs88EB6pEDZCDMNebpM1FRWRid" +
            "OoP8NEKunwvQXSE6qyWZSmFdTmJz5B4PRrGmc/9wgf07nAr0VnT/SUD7KGRm04mC2+uJap3b" +
            "ok3fPNwjtWnVbqxga+30ke8uVJYZkiuuGhz7dmPmsjcbbifv8JRtif4lMcFsoiFclxaKGgHW" +
            "EAQ5iUr3A418NqLr87fq2lB4LpFyCVjVgrfNS3Ou5IdI1MBz0SPemsbqYy9mjOR0uTISWgDq" +
            "gl9qBApSZxzov0pm4HbMDGpR2jEAzLnvZFtWMyUYPoVpguDM="

// Store-only archive containing pages/ and two text files.
internal const val StoredRar4 =
    "UmFyIRoHAM+QcwAADQAAAAAAAADczHTggCUAAAAAAAAAAAADAAAAAAAAAAAUMAUA7UEAAHBhZ2Vz" +
            "JeV0AIAvAAoAAAAKAAAAA9p9J9cAAAAAFDAPAKSBAABwYWdlcy9wYWdlMS50eHRGaXJzdCBwYWdl" +
            "T9x0AIAvAAsAAAALAAAAA0f2U20AAAAAFDAPAKSBAABwYWdlcy9wYWdlMi50eHRTZWNvbmQgcGFn" +
            "ZQSwewAABwA="

// Store-only archive containing pages/ and two text files.
internal const val StoredRar5 =
    "UmFyIRoHAQDFGjMyAwEAAKoTEpAUAgIABQDtgwEAAAAAAAEFcGFnZXM2dgvWHgICCgQKpIMC2n0n" +
            "1wABD3BhZ2VzL3BhZ2UxLnR4dEZpcnN0IHBhZ2VPpWFcHgICCwQLpIMCR/ZTbQABD3BhZ2VzL3Bh" +
            "Z2UyLnR4dFNlY29uZCBwYWdlGbI6NQMFAAA="

// RAR4 encryption fixtures from libarchive/libarchive, libarchive/test/
// test_read_format_rar_encryption_{data,header}.rar.uu (password: 12345678).
/*-
 * Copyright (c) 2013 Konrad Kleine
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
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR(S) ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR(S) BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

internal const val EncryptedRar4Data =
    "UmFyIRoHAM+QcwAADQAAAAAAAABd/HQkhC8AIAAAABAAAAADXIatpqVMMUMdMwcAtIEAAGZvby50" +
            "eHRPA7zZ1hZM30VAGpKSiN8VK46ez3C5QOjJR+bO1r9E4rg1qJzoZTcBB0V0JIQvACAAAAAQAAAA" +
            "A77ikM2mTDFDHTMHALSBAABiYXIudHh0TwO82dYWTN9wdHpIiYgPZlt95Qa5ch6oJVX4yjcBo+30" +
            "8RTzI2TRocQ9ewBABwA="

internal const val EncryptedRar4Headers =
    "UmFyIRoHAM6Zc4AADQAAAAAAAADjLqtkp1x2jIRHQgDLysyfF2eRlxtxdgRzM7EaQ58bHnY/jQkF" +
            "SsCaKbOiKstQBMBOSByi1fLAk0uZr4k8cUynzjcrVOE1V6w+SLWCMjUAx+uKt3QJcE1M4y6rZKdc" +
            "doyqlt/wYnpuaKU6kwceLDrjja8yEJwG/PkSpHIUMXE+DS78M5CkOS0EgiRDqN6E9yW/KIE3XLBq" +
            "o8MI4oMHjXmjgiM8EReLj4NdANFmTjWFp+Muq2SnXHaMqmfUrzaWd0hrFE2d9SLu+g=="