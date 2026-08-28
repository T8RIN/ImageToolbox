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

package com.t8rin.imagetoolbox.feature.archive_tools.domain

import com.t8rin.archive.ArchiveCompressionLevel
import com.t8rin.archive.ArchiveEncryptionStatus
import com.t8rin.archive.ArchiveEntryInfo
import com.t8rin.archive.ArchiveFormat
import com.t8rin.archive.SevenZipCompressionMethod
import com.t8rin.archive.ZipCompressionMethod
import com.t8rin.imagetoolbox.core.domain.saving.io.Writeable
import com.t8rin.imagetoolbox.feature.archive_tools.domain.model.ArchiveExtractionOptions

interface ArchiveManager {

    suspend fun archive(
        files: List<String>,
        destination: Writeable,
        format: ArchiveFormat,
        zipCompressionMethod: ZipCompressionMethod,
        sevenZipCompressionMethod: SevenZipCompressionMethod,
        compressionLevel: ArchiveCompressionLevel,
        passphrase: String?,
        onProgress: () -> Unit
    )

    suspend fun getArchiveEncryptionStatus(archive: String): ArchiveEncryptionStatus

    suspend fun verifyArchivePassphrase(
        archive: String,
        passphrase: String
    ): Boolean

    suspend fun listEntries(
        archive: String,
        passphrase: String?
    ): List<ArchiveEntryInfo>

    suspend fun extract(
        archive: String,
        destinationFolder: String,
        passphrase: String?,
        options: ArchiveExtractionOptions,
        onProgress: () -> Unit
    ): Int

    suspend fun extractToCache(
        archive: String,
        passphrase: String?,
        options: ArchiveExtractionOptions,
        onProgress: () -> Unit
    ): List<String>
}
