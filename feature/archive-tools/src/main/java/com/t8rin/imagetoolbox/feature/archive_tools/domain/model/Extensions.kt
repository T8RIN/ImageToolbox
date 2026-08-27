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

package com.t8rin.imagetoolbox.feature.archive_tools.domain.model

internal val SupportedArchiveExtensions = listOf(
    ".tar.gz", ".tar.bz2", ".tar.xz", ".tar.zst", ".tar.zstd", ".tar.z",
    ".tar.lz4", ".tar.lz", ".tar.lzip", ".tar.lzma", ".warc.gz",
    ".tgz", ".tbz", ".tbz2", ".txz", ".tzst", ".taz", ".tlz",
    ".zip", ".zipx", ".7z", ".rar", ".cbr", ".cbz", ".cb7", ".cbt",
    ".apk", ".apks", ".xapk", ".aab", ".jar", ".aar", ".ipa", ".epub",
    ".whl", ".egg", ".nupkg", ".vsix",
    ".tar", ".gz", ".bz2", ".xz", ".zst", ".zstd", ".z", ".lz4", ".lz",
    ".lzip", ".lzma", ".uu", ".uue",
    ".cab", ".iso", ".lha", ".lzh", ".xar", ".ar", ".a", ".deb", ".udeb",
    ".cpio", ".rpm", ".warc", ".mtree"
)

internal fun String.hasSupportedArchiveExtension(): Boolean {
    val filename = lowercase()
    return SupportedArchiveExtensions.any(filename::endsWith)
}
