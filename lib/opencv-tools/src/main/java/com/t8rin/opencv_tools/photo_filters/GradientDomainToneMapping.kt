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
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.roundToInt

object GradientDomainToneMapping {

    suspend fun apply(
        input: Bitmap,
        compression: Float = 0.6f,
        detail: Float = 1f,
        strength: Float = 1f
    ): Bitmap = PhotoFilterRenderer.render(input, strength) { rgb ->
        val amount = compression.takeIf(Float::isFinite)?.coerceIn(0f, 1f) ?: 0.6f
        val detailGain = detail.takeIf(Float::isFinite)?.coerceIn(0.5f, 2f) ?: 1f
        if (amount == 0f && detailGain == 1f) return@render rgb
        val factor = (512.0 / max(rgb.cols(), rgb.rows())).coerceAtMost(1.0)
        // OpenCV's DCT requires even lengths. Neumann boundaries avoid a periodic image seam.
        val width = ((rgb.cols() * factor).roundToInt().coerceAtLeast(2) + 1) / 2 * 2
        val height = ((rgb.rows() * factor).roundToInt().coerceAtLeast(2) + 1) / 2 * 2
        val sample = resize(gray(rgb), width, height, Imgproc.INTER_AREA)
        val logarithm = floats(sample)
        Core.max(logarithm, Scalar.all(1.0 / 255.0), logarithm)
        Core.log(logarithm, logarithm)
        val weights = mat(height, width, CvType.CV_32F)
        weights.setTo(Scalar.all(1.0))
        // Attenuate strong gradients at several scales; fine low-contrast texture survives.
        for (radius in doubleArrayOf(2.0, 4.0, 8.0, 16.0)) {
            work {
                checkpoint()
                val smooth = gaussian(logarithm, radius * sample.referenceScale())
                val dx = mat()
                val dy = mat()
                Imgproc.Sobel(smooth, dx, CvType.CV_32F, 1, 0, 3, 0.125)
                Imgproc.Sobel(smooth, dy, CvType.CV_32F, 0, 1, 3, 0.125)
                val magnitude = mat()
                Core.magnitude(dx, dy, magnitude)
                val threshold = (Core.mean(magnitude).`val`[0] * 0.3).coerceAtLeast(1e-5)
                Core.max(magnitude, Scalar.all(threshold), magnitude)
                Core.multiply(magnitude, Scalar.all(1.0 / threshold), magnitude)
                Core.pow(magnitude, -amount * 0.25, magnitude)
                Core.multiply(weights, magnitude, weights)
            }
        }
        val values = FloatArray(width * height)
        val attenuation = FloatArray(values.size)
        logarithm.get(0, 0, values)
        weights.get(0, 0, attenuation)
        val horizontal = FloatArray(values.size)
        val vertical = FloatArray(values.size)
        val divergence = FloatArray(values.size)
        for (y in 0 until height) {
            checkpoint()
            for (x in 0 until width) {
                val i = y * width + x
                if (x + 1 < width) horizontal[i] =
                    (values[i + 1] - values[i]) * (attenuation[i] + attenuation[i + 1]) * 0.5f
                if (y + 1 < height) vertical[i] =
                    (values[i + width] - values[i]) * (attenuation[i] + attenuation[i + width]) * 0.5f
            }
        }
        for (y in 0 until height) {
            checkpoint()
            for (x in 0 until width) {
                val i = y * width + x
                divergence[i] = horizontal[i] + vertical[i] -
                        (if (x > 0) horizontal[i - 1] else 0f) - (if (y > 0) vertical[i - width] else 0f)
            }
        }
        val rhs = mat(height, width, CvType.CV_32F)
        rhs.put(0, 0, divergence)
        val reconstructed = solvePoisson(rhs)
        Core.add(reconstructed, Core.mean(logarithm), reconstructed)
        if (detailGain != 1f) {
            val fine = subtract(logarithm, gaussian(logarithm, 2.0 * sample.referenceScale()))
            Core.addWeighted(
                reconstructed,
                1.0,
                fine,
                (detailGain - 1f).toDouble(),
                0.0,
                reconstructed
            )
        }
        // Preserve the brightest source luminance while opening compressed shadows.
        val whitePoint = Core.minMaxLoc(reconstructed).maxVal
        val originalWhite = Core.minMaxLoc(logarithm).maxVal
        Core.add(reconstructed, Scalar.all(originalWhite - whitePoint), reconstructed)
        val difference = subtract(reconstructed, logarithm)
        val correction = ScalarMapSampler(difference)
        val result = mat(rgb.rows(), rgb.cols(), rgb.type())
        val row = ByteArray(rgb.cols() * 3)
        val correctionRow = FloatArray(rgb.cols())
        for (y in 0 until rgb.rows()) {
            checkpoint()
            rgb.get(y, 0, row)
            correction.readRow(y, rgb.rows(), correctionRow)
            for (x in 0 until rgb.cols()) {
                val gain = exp(correctionRow[x].coerceIn(-6f, 6f))
                for (channel in 0..2) {
                    val i = x * 3 + channel
                    row[i] =
                        ((row[i].toInt() and 255) * gain).roundToInt().coerceIn(0, 255).toByte()
                }
            }
            result.put(y, 0, row)
        }
        result
    }

    private fun PhotoFilterScope.solvePoisson(divergence: Mat): Mat {
        val frequency = mat()
        checkpoint()
        Core.dct(divergence, frequency)
        checkpoint()
        val width = divergence.cols()
        val height = divergence.rows()
        val row = FloatArray(width)
        val horizontal = DoubleArray(width) { 2.0 * cos(PI * it / width) - 2.0 }
        for (y in 0 until height) {
            checkpoint()
            frequency.get(y, 0, row)
            val vertical = 2.0 * cos(PI * y / height) - 2.0
            for (x in row.indices) {
                row[x] =
                    if (x == 0 && y == 0) 0f else (row[x] / (horizontal[x] + vertical)).toFloat()
            }
            frequency.put(y, 0, row)
        }
        return mat().also {
            Core.idct(frequency, it)
            checkpoint()
        }
    }
}