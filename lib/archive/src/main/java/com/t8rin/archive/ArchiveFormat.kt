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

enum class ArchiveFormat(
    val title: String,
    val extension: String,
    val mimeType: String,
    val supportsEncryption: Boolean = false,
    val isRaw: Boolean = false
) {
    Zip("ZIP", "zip", "application/zip", supportsEncryption = true),
    SevenZip("7Z", "7z", "application/x-7z-compressed"),
    Tar("TAR", "tar", "application/x-tar"),
    PaxTar("TAR (PAX)", "tar", "application/x-tar"),
    GnuTar("TAR (GNU)", "tar", "application/x-tar"),
    Ustar("TAR (USTAR)", "tar", "application/x-tar"),
    V7Tar("TAR (V7)", "tar", "application/x-tar"),
    TarGzip("TAR.GZ", "tar.gz", "application/gzip"),
    TarCompress("TAR.Z", "tar.Z", "application/x-compress"),
    TarBzip2("TAR.BZ2", "tar.bz2", "application/x-bzip2"),
    TarXz("TAR.XZ", "tar.xz", "application/x-xz"),
    TarZstd("TAR.ZST", "tar.zst", "application/zstd"),
    TarLz4("TAR.LZ4", "tar.lz4", "application/x-lz4"),
    TarLzip("TAR.LZIP", "tar.lz", "application/lzip"),
    TarLzma("TAR.LZMA", "tar.lzma", "application/x-lzma"),
    Cpio("CPIO (NEWC)", "cpio", "application/x-cpio"),
    CpioOdc("CPIO (ODC)", "cpio", "application/x-cpio"),
    CpioBinary("CPIO (Binary)", "cpio", "application/x-cpio"),
    CpioPwb("CPIO (PWB)", "cpio", "application/x-cpio"),
    ArBsd("AR (BSD)", "ar", "application/x-archive"),
    ArGnu("AR (GNU)", "ar", "application/x-archive"),
    Iso("ISO", "iso", "application/x-iso9660-image"),
    Gzip("GZIP", "gz", "application/gzip", isRaw = true),
    Bzip2("BZIP2", "bz2", "application/x-bzip2", isRaw = true),
    Xz("XZ", "xz", "application/x-xz", isRaw = true),
    Zstd("ZSTD", "zst", "application/zstd", isRaw = true),
    Lz4("LZ4", "lz4", "application/x-lz4", isRaw = true),
    Lzip("LZIP", "lz", "application/lzip", isRaw = true),
    Lzma("LZMA", "lzma", "application/x-lzma", isRaw = true),
    Compress("Compress", "Z", "application/x-compress", isRaw = true);

    val supportsMultipleFiles: Boolean
        get() = !isRaw
}
