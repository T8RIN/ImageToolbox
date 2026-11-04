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

import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

internal fun PhotoFilterScope.fourier(input: Mat): Mat {
    checkpoint()
    return mat().also {
        Core.dft(input, it, Core.DFT_COMPLEX_OUTPUT)
        checkpoint()
    }
}

internal fun PhotoFilterScope.inverseFourier(frequency: Mat): Mat {
    checkpoint()
    return mat().also {
        Core.idft(frequency, it, Core.DFT_SCALE or Core.DFT_REAL_OUTPUT)
        checkpoint()
    }
}

internal fun PhotoFilterScope.centerFrequency(input: Mat): Mat {
    val width = input.cols()
    val height = input.rows()
    val result = mat(height, width, CvType.CV_32F)
    val source = FloatArray(width)
    val shifted = FloatArray(width)
    val offset = (width + 1) / 2 % width
    for (y in 0 until height) {
        checkpoint()
        input.get((y + (height + 1) / 2) % height, 0, source)
        source.copyInto(shifted, 0, offset, width)
        source.copyInto(shifted, width - offset, 0, offset)
        result.put(y, 0, shifted)
    }
    return result
}

internal fun PhotoFilterScope.frequencyDisplay(input: Mat, contrast: Double): Mat {
    val normalized = mat()
    Core.normalize(input, normalized, 0.0, 1.0, Core.NORM_MINMAX)
    Core.pow(normalized, 1.0 / contrast, normalized)
    return convert(bytes(normalized), Imgproc.COLOR_GRAY2RGB)
}