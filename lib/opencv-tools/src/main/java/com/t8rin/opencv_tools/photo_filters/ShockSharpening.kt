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
import org.opencv.imgproc.Imgproc
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round
import kotlin.math.roundToInt
import kotlin.math.sqrt

object ShockSharpening {

    suspend fun apply(
        input: Bitmap,
        iterations: Int = 12,
        smoothing: Float = 1f,
        strength: Float = 1f
    ): Bitmap = PhotoFilterRenderer.render(input, strength) { rgb ->
        val ycc = convert(rgb, Imgproc.COLOR_RGB2YCrCb)
        val luminance = channel(ycc, 0)
        val longestSide = max(rgb.cols(), rgb.rows())
        val sample = if (longestSide <= 768) luminance else {
            val scale = 768.0 / longestSide
            resize(
                luminance, (rgb.cols() * scale).roundToInt().coerceAtLeast(1),
                (rgb.rows() * scale).roundToInt().coerceAtLeast(1), Imgproc.INTER_AREA
            )
        }
        val width = sample.cols()
        val height = sample.rows()
        val current = floats(sample)
        val values = FloatArray(width * height)
        val curvature = FloatArray(values.size)
        val output = FloatArray(values.size)
        val sigma =
            (smoothing.takeIf(Float::isFinite)?.coerceIn(0.3f, 3f) ?: 1f) * sample.referenceScale()
        repeat(iterations.coerceIn(1, 40)) {
            work {
                val smooth = gaussian(current, sigma)
                val laplacian = mat()
                Imgproc.Laplacian(smooth, laplacian, CvType.CV_32F, 1)
                current.get(0, 0, values)
                laplacian.get(0, 0, curvature)
                evolve(
                    width, values, curvature, output,
                    timeStep = (0.2 * sample.referenceScale()).toFloat(),
                    checkpoint = ::checkpoint
                )
                current.put(0, 0, output)
            }
        }
        val result = if (sample === luminance) bytes(current) else {
            val difference = subtract(current, floats(sample))
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

    // Godunov upwinding for u_t = -sign(Laplacian(G_sigma * u)) |grad u|.
    // The timestep is capped below the two-dimensional CFL bound to preserve local extrema.
    internal fun evolve(
        width: Int,
        values: FloatArray,
        curvature: FloatArray,
        output: FloatArray,
        timeStep: Float = 0.2f,
        checkpoint: () -> Unit
    ) {
        val step = timeStep.takeIf(Float::isFinite)?.coerceIn(0f, 0.25f) ?: 0.2f
        val height = values.size / width
        for (y in 0 until height) {
            checkpoint()
            for (x in 0 until width) {
                val i = y * width + x
                val center = values[i]
                val left = values[y * width + max(0, x - 1)]
                val right = values[y * width + min(width - 1, x + 1)]
                val top = values[max(0, y - 1) * width + x]
                val bottom = values[min(height - 1, y + 1) * width + x]
                val sign = when {
                    curvature[i] > 1e-6f -> 1f
                    curvature[i] < -1e-6f -> -1f
                    else -> 0f
                }
                val dx = max(max(sign * (center - left), 0f), max(-sign * (right - center), 0f))
                val dy = max(max(sign * (center - top), 0f), max(-sign * (bottom - center), 0f))
                output[i] = center - step * sign * sqrt(dx * dx + dy * dy)
            }
        }
    }
}