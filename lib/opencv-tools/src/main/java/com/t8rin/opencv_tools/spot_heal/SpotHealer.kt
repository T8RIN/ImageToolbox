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

package com.t8rin.opencv_tools.spot_heal

import android.graphics.Bitmap
import com.t8rin.opencv_tools.spot_heal.model.HealType
import com.t8rin.opencv_tools.utils.OpenCV
import com.t8rin.opencv_tools.utils.toBitmap
import com.t8rin.opencv_tools.utils.toMat
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import org.opencv.photo.Photo

object SpotHealer : OpenCV() {

    fun heal(
        image: Bitmap,
        mask: Bitmap,
        radius: Float,
        type: HealType
    ): Bitmap {
        val src = image.toMat()
        val inpaintMask = Mat()

        Imgproc.resize(
            mask.toMat(),
            inpaintMask,
            Size(image.width.toDouble(), image.height.toDouble())
        )

        Imgproc.cvtColor(src, src, Imgproc.COLOR_RGB2XYZ)
        Imgproc.cvtColor(inpaintMask, inpaintMask, Imgproc.COLOR_BGR2GRAY)

        val dst = Mat()

        Photo.inpaint(
            src,
            inpaintMask,
            dst,
            radius.toDouble(),
            type.ordinal
        )

        Imgproc.cvtColor(dst, dst, Imgproc.COLOR_XYZ2RGB)

        return dst.toBitmap()
    }


}