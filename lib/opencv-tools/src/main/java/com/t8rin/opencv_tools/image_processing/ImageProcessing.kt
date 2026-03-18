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

@file:Suppress("unused")

package com.t8rin.opencv_tools.image_processing

import android.graphics.Bitmap
import com.t8rin.opencv_tools.utils.OpenCV
import com.t8rin.opencv_tools.utils.toBitmap
import com.t8rin.opencv_tools.utils.toMat
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

object ImageProcessing : OpenCV() {

    fun canny(
        bitmap: Bitmap,
        thresholdOne: Float,
        thresholdTwo: Float
    ): Bitmap {
        val matGrayScale = Mat()
        Imgproc.cvtColor(bitmap.toMat(), matGrayScale, Imgproc.COLOR_RGBA2GRAY)
        Imgproc.medianBlur(matGrayScale, matGrayScale, 3)

        val matCanny = Mat()
        Imgproc.Canny(matGrayScale, matCanny, thresholdOne.toDouble(), thresholdTwo.toDouble())

        return matCanny.toBitmap()
    }

}