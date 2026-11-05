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

package com.t8rin.opencv_tools.qr_prepare

import android.graphics.Bitmap
import com.t8rin.opencv_tools.utils.OpenCV
import com.t8rin.opencv_tools.utils.toBitmap
import com.t8rin.opencv_tools.utils.toMat
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

object QrPrepareHelper : OpenCV() {

    fun prepareQrForDecode(bitmap: Bitmap): Bitmap {
        val gray = bitmap.toMat()
        val binary = Mat()
        val kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(3.0, 3.0))
        val clahe = Imgproc.createCLAHE(2.0, Size(8.0, 8.0))
        try {
            Imgproc.cvtColor(gray, gray, Imgproc.COLOR_RGBA2GRAY)
            clahe.apply(gray, binary)
            Imgproc.threshold(
                binary,
                binary,
                0.0,
                255.0,
                Imgproc.THRESH_BINARY or Imgproc.THRESH_OTSU
            )
            Imgproc.morphologyEx(binary, binary, Imgproc.MORPH_CLOSE, kernel)
            Imgproc.cvtColor(binary, binary, Imgproc.COLOR_GRAY2RGBA)
            return binary.toBitmap()
        } finally {
            gray.release()
            binary.release()
            kernel.release()
            clahe.collectGarbage()
        }
    }

}