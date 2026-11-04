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
import org.opencv.core.Scalar
import kotlin.math.PI
import kotlin.math.cos

object FourierSpectrum {

    suspend fun apply(
        input: Bitmap,
        windowing: Float = 0.5f,
        contrast: Float = 1f,
        strength: Float = 1f
    ): Bitmap = PhotoFilterRenderer.render(input, strength, maxSide = 1024) { rgb ->
        val luminance = floats(gray(rgb))
        val amount = windowing.takeIf(Float::isFinite)?.coerceIn(0f, 1f) ?: 0.5f
        val width = luminance.cols()
        val height = luminance.rows()
        val horizontal = FloatArray(width) { x ->
            if (width < 3) 1f else (0.5 - 0.5 * cos(2 * PI * x / (width - 1))).toFloat()
        }
        val row = FloatArray(width)
        for (y in 0 until height) {
            checkpoint()
            val vertical = if (height < 3) 1f else {
                (0.5 - 0.5 * cos(2 * PI * y / (height - 1))).toFloat()
            }
            luminance.get(y, 0, row)
            for (x in row.indices) row[x] *= 1f - amount + amount * horizontal[x] * vertical
            luminance.put(y, 0, row)
        }
        val frequency = fourier(luminance)
        val magnitude = mat()
        Core.magnitude(channel(frequency, 0), channel(frequency, 1), magnitude)
        Core.add(magnitude, Scalar.all(1.0), magnitude)
        Core.log(magnitude, magnitude)
        frequencyDisplay(
            centerFrequency(magnitude),
            contrast.takeIf(Float::isFinite)?.coerceIn(0.25f, 3f)?.toDouble() ?: 1.0
        )
    }
}