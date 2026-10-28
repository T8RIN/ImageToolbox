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

package com.t8rin.imagetoolbox.feature.ai_tools.data

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.scale
import com.t8rin.neural_tools.runCancellable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.ensureActive
import java.nio.FloatBuffer
import javax.inject.Inject
import kotlin.math.roundToInt

internal class DocShadowProcessor @Inject constructor() {

    suspend fun process(source: Bitmap, session: OrtSession): Bitmap = coroutineScope {
        val ratio = minOf(1f, 1024f / maxOf(source.width, source.height))
        val width = (source.width * ratio).roundToInt().coerceAtLeast(1)
        val height = (source.height * ratio).roundToInt().coerceAtLeast(1)
        val scaled = source.scale(width, height)
        val inputWidth = (width + 127) / 128 * 128
        val inputHeight = (height + 127) / 128 * 128
        val size = inputWidth * inputHeight
        val pixels = IntArray(width * height)
        scaled.getPixels(pixels, 0, width, 0, 0, width, height)
        if (scaled !== source) scaled.recycle()
        val input = FloatArray(size * 3)
        for (y in 0 until inputHeight) {
            for (x in 0 until inputWidth) {
                val color = pixels[y.coerceAtMost(height - 1) * width + x.coerceAtMost(width - 1)]
                val index = y * inputWidth + x
                input[index] = Color.red(color) / 255f
                input[size + index] = Color.green(color) / 255f
                input[2 * size + index] = Color.blue(color) / 255f
            }
        }
        val gains = OnnxTensor.createTensor(
            OrtEnvironment.getEnvironment(), FloatBuffer.wrap(input),
            longArrayOf(1, 3, inputHeight.toLong(), inputWidth.toLong())
        ).use { tensor ->
            session.runCancellable(mapOf("image" to tensor)).use { output ->
                val result = output[0] as OnnxTensor
                check(
                    result.info.shape.contentEquals(
                        longArrayOf(1, 3, inputHeight.toLong(), inputWidth.toLong())
                    )
                )
                val values = FloatArray(size * 3)
                result.floatBuffer.get(values)
                Array(3) { channel ->
                    FloatArray(width * height) { index ->
                        val position = channel * size + (index / width) * inputWidth + index % width
                        (values[position] / input[position].coerceAtLeast(0.03f)).coerceIn(0f, 8f)
                    }
                }
            }
        }
        val result = source.copy(Bitmap.Config.ARGB_8888, true)
        val row = IntArray(source.width)
        try {
            for (y in 0 until source.height) {
                ensureActive()
                source.getPixels(row, 0, source.width, 0, y, source.width, 1)
                for (x in row.indices) {
                    val sx = ((x + 0.5f) * width / source.width - 0.5f).coerceIn(
                        0f,
                        (width - 1).toFloat()
                    )
                    val sy = ((y + 0.5f) * height / source.height - 0.5f).coerceIn(
                        0f,
                        (height - 1).toFloat()
                    )
                    val color = row[x]
                    row[x] = Color.argb(
                        Color.alpha(color),
                        (Color.red(color) * sample(gains[0], width, height, sx, sy)).roundToInt()
                            .coerceIn(0, 255),
                        (Color.green(color) * sample(gains[1], width, height, sx, sy)).roundToInt()
                            .coerceIn(0, 255),
                        (Color.blue(color) * sample(gains[2], width, height, sx, sy)).roundToInt()
                            .coerceIn(0, 255)
                    )
                }
                result.setPixels(row, 0, source.width, 0, y, source.width, 1)
            }
            result
        } catch (error: Throwable) {
            result.recycle()
            throw error
        }
    }

    private fun sample(values: FloatArray, width: Int, height: Int, x: Float, y: Float): Float {
        val x0 = x.toInt()
        val y0 = y.toInt()
        val x1 = (x0 + 1).coerceAtMost(width - 1)
        val y1 = (y0 + 1).coerceAtMost(height - 1)
        val tx = x - x0
        val ty = y - y0
        return (values[y0 * width + x0] * (1 - tx) + values[y0 * width + x1] * tx) * (1 - ty) +
                (values[y1 * width + x0] * (1 - tx) + values[y1 * width + x1] * tx) * ty
    }
}