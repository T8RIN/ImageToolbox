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

package com.t8rin.imagetoolbox.feature.zip.domain.model

internal val SupportedArchiveExtensions = listOf(
    ".tar.gz", ".tar.bz2", ".tar.xz", ".tar.zst",
    ".tar.lz4", ".tar.lz", ".tar.lzma",
    ".tgz", ".tbz2", ".txz", ".tzst",
    ".zip", ".zipx", ".7z", ".rar", ".cbr", ".cbz", ".cb7", ".cbt",
    ".tar", ".gz", ".bz2", ".xz", ".zst", ".lz4", ".lz", ".lzma", ".lzip",
    ".cab", ".iso", ".lha", ".lzh", ".xar", ".ar", ".cpio"
)

internal fun String.hasSupportedArchiveExtension(): Boolean {
    val filename = lowercase()
    return SupportedArchiveExtensions.any(filename::endsWith)
}