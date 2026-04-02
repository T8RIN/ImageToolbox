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
import androidx.core.graphics.createBitmap
import com.t8rin.opencv_tools.utils.OpenCV
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

object QrPrepareHelper : OpenCV() {

    fun prepareQrForDecode(bitmap: Bitmap): Bitmap {
        // 1. Bitmap -> Mat
        val src = Mat()
        Utils.bitmapToMat(bitmap, src)

        // 2. В grayscale
        val gray = Mat()
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_RGBA2GRAY)

        // 3. CLAHE для локального контраста
        val claheMat = Mat()
        val clahe = Imgproc.createCLAHE()
        clahe.clipLimit = 2.0
        clahe.tilesGridSize = Size(8.0, 8.0)
        clahe.apply(gray, claheMat)

        // 4. Глобальная бинаризация через Otsu (полностью заливает модули)
        val binary = Mat()
        Imgproc.threshold(
            claheMat,
            binary,
            0.0,
            255.0,
            Imgproc.THRESH_BINARY or Imgproc.THRESH_OTSU
        )

        // 5. Морфология для устранения дырок
        val kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(3.0, 3.0))
        Imgproc.morphologyEx(binary, binary, Imgproc.MORPH_CLOSE, kernel)

        // 6. Конвертируем в Bitmap
        val result = createBitmap(binary.cols(), binary.rows())
        Imgproc.cvtColor(binary, binary, Imgproc.COLOR_GRAY2RGBA)
        Utils.matToBitmap(binary, result)

        // 7. Освобождаем память
        src.release()
        gray.release()
        claheMat.release()
        binary.release()

        return result
    }

}