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
import kotlin.math.sqrt

object PhaseOnlyReconstruction {

    suspend fun apply(
        input: Bitmap,
        contrast: Float = 2f,
        strength: Float = 1f
    ): Bitmap = PhotoFilterRenderer.render(input, strength, maxSide = 1024) { rgb ->
        val frequency = fourier(floats(gray(rgb)))
        val real = channel(frequency, 0)
        val imaginary = channel(frequency, 1)
        val magnitude = mat()
        Core.magnitude(real, imaginary, magnitude)
        val epsilon = (Core.minMaxLoc(magnitude).maxVal * 1e-6).coerceAtLeast(1e-8)
        Core.max(magnitude, Scalar.all(epsilon), magnitude)
        Core.divide(real, magnitude, real)
        Core.divide(imaginary, magnitude, imaginary)
        Core.merge(listOf(real, imaginary), frequency)
        val restored = inverseFourier(frequency)
        val mean = Core.mean(restored).`val`[0]
        val variance = Core.mean(multiply(restored, restored)).`val`[0] - mean * mean
        val sigma = sqrt(variance.coerceAtLeast(0.0))
        val amount = contrast.takeIf(Float::isFinite)?.coerceIn(0.25f, 8f) ?: 2f
        val gain = if (sigma > 1e-8) 255.0 * amount / (6.0 * sigma) else 0.0
        restored.convertTo(restored, CvType.CV_8U, gain, 127.5 - mean * gain)
        convert(restored, Imgproc.COLOR_GRAY2RGB)
    }
}