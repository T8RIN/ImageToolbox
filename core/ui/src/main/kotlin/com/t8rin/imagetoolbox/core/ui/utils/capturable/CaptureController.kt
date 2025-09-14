/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.core.ui.utils.capturable

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * Controller for capturing [Composable] content.
 * @see capturable for implementation details.
 */
class CaptureController(internal val graphicsLayer: GraphicsLayer) {

    /**
     * Medium for providing capture requests
     *
     * Earlier, we were using `MutableSharedFlow` here but it was incapable of serving requests
     * which are created as soon as composition starts because this flow was collected later
     * underneath. So Channel with UNLIMITED capacity just works here and solves the issue as well.
     * See issue: https://github.com/PatilShreyas/Capturable/issues/202
     */
    private val _captureRequests = Channel<CaptureRequest>(capacity = Channel.UNLIMITED)
    internal val captureRequests = _captureRequests.receiveAsFlow()

    /**
     * Creates and requests for a Bitmap capture and returns
     * an [ImageBitmap] asynchronously.
     *
     * This method is safe to be called from the "main" thread directly.
     *
     * Make sure to call this method as a part of callback function and not as a part of the
     * [Composable] function itself.
     *
     */
    fun captureAsync(): Deferred<ImageBitmap> {
        val deferredImageBitmap = CompletableDeferred<ImageBitmap>()
        return deferredImageBitmap.also {
            _captureRequests.trySend(CaptureRequest(imageBitmapDeferred = it))
        }
    }

    suspend fun bitmap(): Bitmap = captureAsync().await().asAndroidBitmap()

    /**
     * Holds information of capture request
     */
    internal class CaptureRequest(val imageBitmapDeferred: CompletableDeferred<ImageBitmap>)
}

/**
 * Creates [CaptureController] and remembers it.
 */
@Composable
fun rememberCaptureController(): CaptureController {
    val graphicsLayer = rememberGraphicsLayer()
    return remember(graphicsLayer) { CaptureController(graphicsLayer) }
}