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

package com.t8rin.imagetoolbox.core.data.saving

import android.content.IntentSender
import kotlinx.coroutines.flow.Flow

interface FileControllerEventEmitter {
    val events: Flow<FileControllerEvent>

    fun deleteFiles(
        uris: List<String>,
        onResult: (FileDeletionResult) -> Unit
    )

    fun onDeleteOriginalsPermissionResult(
        requestId: Long,
        granted: Boolean
    )
}

data class FileDeletionResult(
    val deletedUris: List<String>,
    val failedUris: List<String>
)

sealed interface FileControllerEvent {
    data class RequestDeleteOriginalsPermission(
        val requestId: Long,
        val intentSender: IntentSender,
        val count: Int
    ) : FileControllerEvent

    data class OriginalFilesDeleteResult(
        val deleted: Int,
        val failed: Int
    ) : FileControllerEvent
}