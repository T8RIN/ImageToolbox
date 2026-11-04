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
import kotlin.math.roundToInt

object SpecularHighlightSuppression {

    suspend fun apply(
        input: Bitmap,
        radius: Float = 24f,
        threshold: Float = 0.65f,
        strength: Float = 1f
    ): Bitmap = PhotoFilterRenderer.render(input, strength) { rgb ->
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
        val sum = add(add(red, green), blue)
        Core.max(sum, Scalar.all(1e-6), sum)
        val maximum = mat()
        Core.max(red, green, maximum)
        Core.max(maximum, blue, maximum)
        val chromaticity = divide(maximum, sum)
        val size = sample.radius(radius.takeIf(Float::isFinite)?.coerceIn(2f, 80f) ?: 24f)
        val kernel = mat(size * 2 + 1, size * 2 + 1, CvType.CV_8U)
        kernel.setTo(Scalar.all(1.0))
        val diffuse = mat()
        Imgproc.dilate(chromaticity, diffuse, kernel)
        val local = gaussian(maximum, size * 0.5)
        val diffuseMap = ScalarMapSampler(diffuse)
        val localMap = ScalarMapSampler(local)
        val cutoff = threshold.takeIf(Float::isFinite)?.coerceIn(0.3f, 0.95f) ?: 0.65f
        val result = mat(rgb.rows(), rgb.cols(), rgb.type())
        val row = ByteArray(rgb.cols() * 3)
        val diffuseRow = FloatArray(rgb.cols())
        val localRow = FloatArray(rgb.cols())
        for (y in 0 until rgb.rows()) {
            checkpoint()
            rgb.get(y, 0, row)
            diffuseMap.readRow(y, rgb.rows(), diffuseRow)
            localMap.readRow(y, rgb.rows(), localRow)
            for (x in 0 until rgb.cols()) {
                val offset = x * 3
                val r = (row[offset].toInt() and 255) / 255f
                val g = (row[offset + 1].toInt() and 255) / 255f
                val b = (row[offset + 2].toInt() and 255) / 255f
                val total = r + g + b
                val peak = max(r, max(g, b))
                // White specular reflection adds the same amount to every color channel.
                // The diffuse chromaticity gives its magnitude through the dichromatic model.
                val denominator = 3f * diffuseRow[x] - 1f
                if (denominator <= 0.03f || peak <= cutoff) continue
                val diffuseIntensity = (3f * peak - total) / denominator
                val highlight = ((peak - cutoff) / (1f - cutoff)).coerceIn(0f, 1f)
                val localContrast = ((peak - localRow[x]) / 0.12f).coerceIn(0f, 1f)
                val specular = ((total - diffuseIntensity) / 3f).coerceIn(0f, min(r, min(g, b))) *
                        highlight * localContrast
                row[offset] = ((r - specular) * 255).roundToInt().toByte()
                row[offset + 1] = ((g - specular) * 255).roundToInt().toByte()
                row[offset + 2] = ((b - specular) * 255).roundToInt().toByte()
            }
            result.put(y, 0, row)
        }
        result
    }
}