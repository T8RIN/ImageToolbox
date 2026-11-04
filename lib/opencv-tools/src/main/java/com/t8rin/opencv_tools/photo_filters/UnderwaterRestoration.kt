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
import kotlin.math.pow
import kotlin.math.roundToInt

object UnderwaterRestoration {

    suspend fun apply(
        input: Bitmap,
        radius: Float = 15f,
        restoration: Float = 0.8f,
        redRecovery: Float = 0.7f,
        strength: Float = 1f
    ): Bitmap = PhotoFilterRenderer.render(input, strength) { rgb ->
        val amount = restoration.takeIf(Float::isFinite)?.coerceIn(0f, 1f) ?: 0.8f
        if (amount == 0f) return@render rgb
        val longestSide = max(rgb.cols(), rgb.rows())
        val sample = if (longestSide <= 768) rgb else {
            val scale = 768.0 / longestSide
            resize(
                rgb, (rgb.cols() * scale).roundToInt().coerceAtLeast(1),
                (rgb.rows() * scale).roundToInt().coerceAtLeast(1), Imgproc.INTER_AREA
            )
        }
        val red = floats(channel(sample, 0))
        val green = floats(channel(sample, 1))
        val blue = floats(channel(sample, 2))
        val inverseRed = scale(red, -1.0)
        Core.add(inverseRed, Scalar.all(1.0), inverseRed)
        val prior = mat()
        Core.min(inverseRed, green, prior)
        Core.min(prior, blue, prior)
        val size = sample.radius(radius.takeIf(Float::isFinite)?.coerceIn(3f, 60f) ?: 15f)
        val kernel = mat(size * 2 + 1, size * 2 + 1, CvType.CV_8U)
        kernel.setTo(Scalar.all(1.0))
        val minimum = mat()
        Imgproc.erode(prior, minimum, kernel)
        checkpoint()
        // Galdran et al.: waterlight is the pixel with the largest local min(1-R, G, B).
        val location = Core.minMaxLoc(minimum).maxLoc
        val waterlight = sample.get(location.y.toInt(), location.x.toInt()).map {
            (it / 255.0).coerceIn(0.02, 0.98)
        }
        Core.multiply(inverseRed, Scalar.all(1.0 / (1.0 - waterlight[0])), prior)
        Core.min(prior, scale(green, 1.0 / waterlight[1]), prior)
        Core.min(prior, scale(blue, 1.0 / waterlight[2]), prior)
        val brightest = mat()
        val darkest = mat()
        Core.max(red, green, brightest)
        Core.max(brightest, blue, brightest)
        Core.min(red, green, darkest)
        Core.min(darkest, blue, darkest)
        Core.max(brightest, Scalar.all(1e-5), brightest)
        val saturation = divide(subtract(brightest, darkest), brightest)
        // The Red-Saturation prior leaves white artificial lights close to transmission one.
        Core.min(prior, saturation, prior)
        Imgproc.erode(prior, minimum, kernel)
        val rough = scale(minimum, -amount.toDouble())
        Core.add(rough, Scalar.all(1.0), rough)
        val guide = floats(gray(sample))
        val meanGuide = box(guide, size * 2)
        val meanTransmission = box(rough, size * 2)
        val variance =
            subtract(box(multiply(guide, guide), size * 2), multiply(meanGuide, meanGuide))
        Core.max(variance, Scalar.all(0.0), variance)
        Core.add(variance, Scalar.all(0.001), variance)
        val covariance =
            subtract(box(multiply(guide, rough), size * 2), multiply(meanGuide, meanTransmission))
        val a = divide(covariance, variance)
        val b = subtract(meanTransmission, multiply(a, meanGuide))
        val refined = add(multiply(box(a, size * 2), guide), box(b, size * 2))
        val transmission = ScalarMapSampler(refined)
        val recovery = redRecovery.takeIf(Float::isFinite)?.coerceIn(0f, 1f) ?: 0.7f
        val waterMaximum = max(waterlight[1], waterlight[2])
        // Different attenuation rates: red decays fastest; the dominant water color decays slowest.
        val ratios = doubleArrayOf(
            1.0, 1.0 - 0.65 * recovery * waterlight[1] / waterMaximum,
            1.0 - 0.65 * recovery * waterlight[2] / waterMaximum
        )
        val result = mat(rgb.rows(), rgb.cols(), rgb.type())
        val row = ByteArray(rgb.cols() * 3)
        val transmissionRow = FloatArray(rgb.cols())
        for (y in 0 until rgb.rows()) {
            checkpoint()
            rgb.get(y, 0, row)
            transmission.readRow(y, rgb.rows(), transmissionRow)
            for (x in 0 until rgb.cols()) {
                val t = transmissionRow[x].toDouble().coerceIn(0.1, 1.0)
                for (channel in 0..2) {
                    val index = x * 3 + channel
                    val original = (row[index].toInt() and 255) / 255.0
                    val restored =
                        (original - waterlight[channel]) / t.pow(ratios[channel]) + waterlight[channel]
                    row[index] = (restored.coerceIn(0.0, 1.0) * 255).roundToInt().toByte()
                }
            }
            result.put(y, 0, row)
        }
        result
    }
}