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

package com.t8rin.imagetoolbox.feature.compare.data

import android.graphics.Canvas
import androidx.core.graphics.createBitmap
import com.t8rin.gif_converter.GifEncoder
import com.t8rin.imagetoolbox.feature.compare.domain.CompareAnimationParams
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import java.io.OutputStream

internal suspend fun CompareAnimationRenderer.writeGif(
    output: OutputStream,
    params: CompareAnimationParams,
    onProgress: (Int, Int) -> Unit
) {
    val frame = createBitmap(width, height)
    val canvas = Canvas(frame)
    val encoder = GifEncoder()
        .setRepeat(0)
        .setQuality(90)
        .setDispose(1)
        .setSize(width, height)
    var finished = false
    try {
        check(encoder.start(output)) { "Cannot start GIF encoding" }
        val frames = params.frames()
        frames.forEachIndexed { index, animationFrame ->
            currentCoroutineContext().ensureActive()
            draw(canvas, animationFrame.position, params)
            check(encoder.setDelay(animationFrame.durationMillis).addFrame(frame)) {
                "Cannot encode GIF frame"
            }
            onProgress(index + 1, frames.size)
        }
        currentCoroutineContext().ensureActive()
        val success = encoder.finish()
        finished = true
        check(success) { "Cannot finish GIF encoding" }
    } finally {
        if (!finished) runCatching { encoder.finish() }
        frame.recycle()
    }
}