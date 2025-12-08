/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.core.domain.saving

import com.t8rin.imagetoolbox.core.domain.image.Metadata
import com.t8rin.imagetoolbox.core.domain.image.MetadataProvider
import com.t8rin.imagetoolbox.core.domain.saving.io.Writeable
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveTarget
import kotlinx.coroutines.flow.Flow

interface FileController : ObjectSaver, MetadataProvider {
    val defaultSavingPath: String

    suspend fun save(
        saveTarget: SaveTarget,
        keepOriginalMetadata: Boolean,
        oneTimeSaveLocationUri: String? = null,
    ): SaveResult

    fun getSize(uri: String): Long?

    fun clearCache(onComplete: (String) -> Unit = {})

    fun getReadableCacheSize(): String

    suspend fun readBytes(uri: String): ByteArray

    suspend fun writeBytes(
        uri: String,
        block: suspend (Writeable) -> Unit,
    ): SaveResult

    suspend fun transferBytes(
        fromUri: String,
        toUri: String
    ): SaveResult

    suspend fun transferBytes(
        fromUri: String,
        to: Writeable
    ): SaveResult

    suspend fun writeMetadata(
        imageUri: String,
        metadata: Metadata?
    )

    suspend fun listFilesInDirectory(treeUri: String): List<String>

    fun listFilesInDirectoryAsFlow(treeUri: String): Flow<String>

    companion object {
        fun FileController.toMetadataProvider(): MetadataProvider =
            object : MetadataProvider by this {}
    }
}