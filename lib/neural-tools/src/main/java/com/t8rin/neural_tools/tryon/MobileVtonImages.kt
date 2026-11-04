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

package com.t8rin.neural_tools.tryon

import android.graphics.Bitmap
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import java.nio.FloatBuffer
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.round

internal object MobileVtonImages {

    suspend fun write(
        image: Bitmap,
        buffer: FloatBuffer,
        width: Int,
        height: Int,
        imageEncoder: Boolean = false
    ) {
        val source = if (image.config == Bitmap.Config.HARDWARE) {
            checkNotNull(image.copy(Bitmap.Config.ARGB_8888, false))
        } else image
        try {
            val shortest = minOf(source.width, source.height)
            val resizedWidth = if (imageEncoder) source.width * 256L / shortest else width.toLong()
            val resizedHeight =
                if (imageEncoder) source.height * 256L / shortest else height.toLong()
            require(resizedWidth <= Int.MAX_VALUE && resizedHeight <= Int.MAX_VALUE)
            val horizontal = weights(source.width, resizedWidth.toInt(), width)
            val vertical = weights(source.height, resizedHeight.toInt(), height)
            val row = IntArray(source.width)
            val rows = mutableMapOf<Int, IntArray>()
            val plane = width * height
            for (y in 0 until height) {
                currentCoroutineContext().ensureActive()
                val filter = vertical[y]
                rows.keys.removeAll { it !in filter.first until filter.first + filter.second.size }
                for (sy in filter.first until filter.first + filter.second.size) {
                    if (sy in rows) continue
                    source.getPixels(row, 0, source.width, 0, sy, source.width, 1)
                    val resized = IntArray(width)
                    for (x in 0 until width) {
                        val (start, coefficients) = horizontal[x]
                        var red = 1L shl 21
                        var green = red
                        var blue = red
                        coefficients.forEachIndexed { index, weight ->
                            val pixel = row[start + index]
                            val alpha = pixel ushr 24
                            red += composite(pixel shr 16 and 255, alpha) * weight.toLong()
                            green += composite(pixel shr 8 and 255, alpha) * weight.toLong()
                            blue += composite(pixel and 255, alpha) * weight.toLong()
                        }
                        resized[x] =
                            (channel(red) shl 16) or (channel(green) shl 8) or channel(blue)
                    }
                    rows[sy] = resized
                }
                for (x in 0 until width) {
                    var red = 1L shl 21
                    var green = red
                    var blue = red
                    filter.second.forEachIndexed { index, weight ->
                        val pixel = rows.getValue(filter.first + index)[x]
                        red += (pixel shr 16 and 255) * weight.toLong()
                        green += (pixel shr 8 and 255) * weight.toLong()
                        blue += (pixel and 255) * weight.toLong()
                    }
                    val offset = y * width + x
                    buffer.put(offset, normalize(channel(red), 0, imageEncoder))
                    buffer.put(plane + offset, normalize(channel(green), 1, imageEncoder))
                    buffer.put(2 * plane + offset, normalize(channel(blue), 2, imageEncoder))
                }
            }
        } finally {
            if (source !== image) source.recycle()
        }
    }

    suspend fun bitmap(buffer: FloatBuffer, width: Int, height: Int): Bitmap {
        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            currentCoroutineContext().ensureActive()
            for (x in 0 until width) {
                val index = y * width + x
                fun channel(offset: Int): Int {
                    val value = buffer.get(index + offset)
                    check(value.isFinite()) { "Mobile-VTON produced a non-finite pixel" }
                    return round((value / 2f + 0.5f).coerceIn(0f, 1f) * 255f).toInt()
                }
                pixels[index] = (255 shl 24) or (channel(0) shl 16) or
                        (channel(pixels.size) shl 8) or channel(pixels.size * 2)
            }
        }
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888)
    }

    private fun normalize(value: Int, channel: Int, imageEncoder: Boolean): Float =
        if (imageEncoder) (value / 255f - means[channel]) / deviations[channel]
        else (value / 255f - 0.5f) / 0.5f

    private fun composite(value: Int, alpha: Int): Int =
        (value * alpha + 255 * (255 - alpha) + 127) / 255

    private fun channel(value: Long): Int = (value shr 22).coerceIn(0, 255).toInt()

    private fun weights(source: Int, resized: Int, cropped: Int): List<Pair<Int, IntArray>> {
        val scale = source.toDouble() / resized
        val filterScale = max(1.0, scale)
        val support = 2.0 * filterScale
        val crop = (resized - cropped) / 2
        return List(cropped) { index ->
            val center = (index + crop + 0.5) * scale
            val start = max(0, (center - support + 0.5).toInt())
            val end = minOf(source, (center + support + 0.5).toInt())
            val coefficients = DoubleArray(end - start) {
                cubic((it + start - center + 0.5) / filterScale)
            }
            val sum = coefficients.sum()
            start to IntArray(coefficients.size) {
                val value = coefficients[it] / sum * (1 shl 22)
                (value + if (value < 0) -0.5 else 0.5).toInt()
            }
        }
    }

    private fun cubic(value: Double): Double {
        val x = abs(value)
        return when {
            x < 1 -> ((1.5 * x - 2.5) * x) * x + 1
            x < 2 -> (((-0.5 * x + 2.5) * x - 4) * x) + 2
            else -> 0.0
        }
    }

    private val means = floatArrayOf(0.485f, 0.456f, 0.406f)
    private val deviations = floatArrayOf(0.229f, 0.224f, 0.225f)
}