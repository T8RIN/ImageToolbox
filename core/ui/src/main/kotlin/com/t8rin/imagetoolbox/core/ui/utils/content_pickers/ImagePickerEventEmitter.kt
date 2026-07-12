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

package com.t8rin.imagetoolbox.core.ui.utils.content_pickers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.LoadingDialog
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import java.util.concurrent.atomic.AtomicLong

internal interface ImagePickerEventEmitter {
    val events: Flow<ImagePickerEvent>

    fun onFolderProcessingStarted(): Long

    fun onFolderProcessingFinished(requestId: Long)
}

@Composable
internal fun rememberImagePickerEventEmitter(): ImagePickerEventEmitter =
    remember { ImagePickerEventEmitterImpl() }

internal sealed interface ImagePickerEvent {
    data class FolderProcessingStarted(val requestId: Long) : ImagePickerEvent
    data class FolderProcessingFinished(val requestId: Long) : ImagePickerEvent
}

private class ImagePickerEventEmitterImpl : ImagePickerEventEmitter {

    private val eventChannel = Channel<ImagePickerEvent>(Channel.UNLIMITED)
    override val events: Flow<ImagePickerEvent> = eventChannel.receiveAsFlow()

    private val nextRequestId = AtomicLong()

    override fun onFolderProcessingStarted(): Long = nextRequestId.incrementAndGet().also {
        eventChannel.trySend(ImagePickerEvent.FolderProcessingStarted(it))
    }

    override fun onFolderProcessingFinished(requestId: Long) {
        eventChannel.trySend(ImagePickerEvent.FolderProcessingFinished(requestId))
    }
}

internal val LocalImagePickerEventEmitter = compositionLocalOf<ImagePickerEventEmitter> {
    error("ImagePickerEventEmitter not registered")
}

@Composable
internal fun ImagePickerEventsHandler() {
    val eventEmitter = LocalImagePickerEventEmitter.current
    var activeRequests by remember { mutableStateOf(emptySet<Long>()) }

    LaunchedEffect(eventEmitter) {
        eventEmitter.events.collect { event ->
            activeRequests = when (event) {
                is ImagePickerEvent.FolderProcessingStarted -> {
                    activeRequests + event.requestId
                }

                is ImagePickerEvent.FolderProcessingFinished -> {
                    activeRequests - event.requestId
                }
            }
        }
    }

    LoadingDialog(
        visible = activeRequests.isNotEmpty(),
        canCancel = false,
        isForSaving = false
    )
}