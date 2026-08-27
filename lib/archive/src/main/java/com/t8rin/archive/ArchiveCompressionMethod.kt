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

enum class ZipCompressionMethod(
    val title: String,
    val supportsEncryption: Boolean = false
) {
    Store("Store", supportsEncryption = true),
    Deflate("Deflate", supportsEncryption = true),
    Bzip2("BZIP2"),
    Lzma("LZMA"),
    Xz("XZ"),
    Zstd("ZSTD")
}

enum class SevenZipCompressionMethod(
    val title: String,
    internal val option: String
) {
    Copy("Copy", "copy"),
    Deflate("Deflate", "deflate"),
    Bzip2("BZIP2", "bzip2"),
    Lzma1("LZMA1", "lzma1"),
    Lzma2("LZMA2", "lzma2"),
    Ppmd("PPMd", "ppmd")
}
