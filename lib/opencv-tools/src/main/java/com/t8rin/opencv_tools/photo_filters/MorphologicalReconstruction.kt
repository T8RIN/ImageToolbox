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

package com.t8rin.opencv_tools.photo_filters

import android.graphics.Bitmap
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round
import kotlin.math.roundToInt

object MorphologicalReconstruction {

    suspend fun apply(
        input: Bitmap,
        radius: Float = 12f,
        mode: Int = 0,
        strength: Float = 1f
    ): Bitmap = PhotoFilterRenderer.render(input, strength) { rgb ->
        val ycc = convert(rgb, Imgproc.COLOR_RGB2YCrCb)
        val luminance = channel(ycc, 0)
        val longestSide = max(rgb.cols(), rgb.rows())
        val samples = if (longestSide <= 768) luminance else {
            val scale = 768.0 / longestSide
            resize(
                luminance,
                (rgb.cols() * scale).roundToInt().coerceAtLeast(1),
                (rgb.rows() * scale).roundToInt().coerceAtLeast(1),
                Imgproc.INTER_AREA
            )
        }
        val closing = mode == 1
        val source = if (closing) mat().also { Core.bitwise_not(samples, it) } else samples
        val size = samples.radius(radius.takeIf(Float::isFinite)?.coerceIn(1f, 80f) ?: 12f)
        val kernel = mat(size * 2 + 1, size * 2 + 1, CvType.CV_8U)
        kernel.setTo(Scalar.all(1.0))
        val marker = mat()
        Imgproc.erode(source, marker, kernel)
        checkpoint()
        val mask = ByteArray(samples.rows() * samples.cols())
        val values = ByteArray(mask.size)
        source.get(0, 0, mask)
        marker.get(0, 0, values)
        reconstruct(samples.cols(), values, mask, ::checkpoint)
        marker.put(0, 0, values)
        if (closing) Core.bitwise_not(marker, marker)
        val result = if (samples === luminance) marker else {
            // Transfer only the luminance correction; keep the original color and fine detail.
            val difference = subtract(floats(marker), floats(samples))
            val correction = ScalarMapSampler(difference)
            val result = mat(rgb.rows(), rgb.cols(), CvType.CV_8U)
            val row = ByteArray(rgb.cols())
            val correctionRow = FloatArray(rgb.cols())
            for (y in 0 until rgb.rows()) {
                checkpoint()
                luminance.get(y, 0, row)
                correction.readRow(y, rgb.rows(), correctionRow)
                for (x in row.indices) {
                    row[x] = round((row[x].toInt() and 255) + correctionRow[x] * 255.0)
                        .toInt().coerceIn(0, 255).toByte()
                }
                result.put(y, 0, row)
            }
            result
        }
        Core.insertChannel(result, ycc, 0)
        convert(ycc, Imgproc.COLOR_YCrCb2RGB)
    }

    // Descending level queues compute the exact geodesic fixed point without repeated dilations.
    // Each pixel has one queue entry, so both memory and propagation are bounded by image size.
    internal fun reconstruct(
        width: Int,
        marker: ByteArray,
        mask: ByteArray,
        checkpoint: () -> Unit
    ) {
        require(width > 0 && marker.size == mask.size && marker.size % width == 0)
        val height = marker.size / width
        val levels = IntArray(marker.size)
        val heads = IntArray(256) { -1 }
        val next = IntArray(marker.size) { -1 }
        val previous = IntArray(marker.size) { -1 }

        fun enqueue(pixel: Int, level: Int) {
            val head = heads[level]
            next[pixel] = head
            previous[pixel] = -1
            if (head >= 0) previous[head] = pixel
            heads[level] = pixel
        }

        fun remove(pixel: Int, level: Int) {
            val before = previous[pixel]
            val after = next[pixel]
            if (before >= 0) next[before] = after else heads[level] = after
            if (after >= 0) previous[after] = before
        }

        for (pixel in marker.indices) {
            if (pixel and 4095 == 0) checkpoint()
            val level = min(marker[pixel].toInt() and 255, mask[pixel].toInt() and 255)
            levels[pixel] = level
            if (level > 0) enqueue(pixel, level)
        }

        var visited = 0
        for (level in 255 downTo 1) {
            while (heads[level] >= 0) {
                if (visited++ and 1023 == 0) checkpoint()
                val pixel = heads[level]
                remove(pixel, level)
                val x = pixel % width
                val y = pixel / width
                for (dy in -1..1) {
                    val row = y + dy
                    if (row !in 0 until height) continue
                    for (dx in -1..1) {
                        val column = x + dx
                        if (column !in 0 until width || dx == 0 && dy == 0) continue
                        val neighbor = row * width + column
                        val candidate = min(level, mask[neighbor].toInt() and 255)
                        val old = levels[neighbor]
                        if (candidate <= old) continue
                        if (old > 0) remove(neighbor, old)
                        levels[neighbor] = candidate
                        enqueue(neighbor, candidate)
                    }
                }
            }
        }
        for (pixel in marker.indices) {
            if (pixel and 4095 == 0) checkpoint()
            marker[pixel] = levels[pixel].toByte()
        }
    }
}