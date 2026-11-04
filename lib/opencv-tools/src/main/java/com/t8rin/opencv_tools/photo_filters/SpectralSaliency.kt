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
import kotlin.math.round
import kotlin.math.roundToInt

object SpectralSaliency {

    suspend fun apply(
        input: Bitmap,
        spectralRadius: Int = 1,
        smoothing: Float = 8f,
        overlay: Float = 0.65f,
        strength: Float = 1f
    ): Bitmap = PhotoFilterRenderer.render(input, strength) { rgb ->
        val luminance = gray(rgb)
        val longestSide = max(rgb.cols(), rgb.rows())
        val samples = if (longestSide <= 256) luminance else {
            val scale = 256.0 / longestSide
            resize(
                luminance,
                (rgb.cols() * scale).roundToInt().coerceAtLeast(1),
                (rgb.rows() * scale).roundToInt().coerceAtLeast(1),
                Imgproc.INTER_AREA
            )
        }
        val frequency = fourier(floats(samples))
        val real = channel(frequency, 0)
        val imaginary = channel(frequency, 1)
        val amplitude = mat()
        Core.magnitude(real, imaginary, amplitude)
        Core.max(amplitude, Scalar.all(1e-8), amplitude)
        val logAmplitude = mat()
        Core.log(amplitude, logAmplitude)
        val radius = spectralRadius.coerceIn(1, 8)
        val padded = mat()
        Core.copyMakeBorder(
            logAmplitude, padded, radius, radius, radius, radius, Core.BORDER_WRAP
        )
        val smoothed = box(padded, radius)
        val residual = subtract(
            logAmplitude,
            roi(smoothed, radius, radius + samples.rows(), radius, radius + samples.cols())
        )
        Core.exp(residual, residual)
        val scale = divide(residual, amplitude)
        Core.multiply(real, scale, real)
        Core.multiply(imaginary, scale, imaginary)
        Core.merge(listOf(real, imaginary), frequency)
        // Hou–Zhang spectral residual: squared inverse transform, then spatial smoothing.
        val restored = inverseFourier(frequency)
        val sigma = smoothing.takeIf(Float::isFinite)?.coerceIn(1f, 40f) ?: 8f
        val saliency = gaussian(multiply(restored, restored), sigma * samples.referenceScale())
        Core.normalize(saliency, saliency, 0.0, 1.0, Core.NORM_MINMAX)
        val fullSize = if (samples === luminance) bytes(saliency) else {
            val sampler = ScalarMapSampler(saliency)
            val result = mat(rgb.rows(), rgb.cols(), CvType.CV_8U)
            val samplesRow = FloatArray(rgb.cols())
            val row = ByteArray(rgb.cols())
            for (y in 0 until rgb.rows()) {
                checkpoint()
                sampler.readRow(y, rgb.rows(), samplesRow)
                for (x in row.indices) {
                    row[x] = round(samplesRow[x] * 255.0).toInt().coerceIn(0, 255).toByte()
                }
                result.put(y, 0, row)
            }
            result
        }
        val heatmap = mat()
        Imgproc.applyColorMap(fullSize, heatmap, Imgproc.COLORMAP_INFERNO)
        Imgproc.cvtColor(heatmap, heatmap, Imgproc.COLOR_BGR2RGB)
        val amount = overlay.takeIf(Float::isFinite)?.coerceIn(0f, 1f)?.toDouble() ?: 0.65
        // At zero the map is unobstructed; at one it is laid over the original image.
        Core.addWeighted(heatmap, 1.0 - 0.55 * amount, rgb, 0.55 * amount, 0.0, heatmap)
        heatmap
    }
}