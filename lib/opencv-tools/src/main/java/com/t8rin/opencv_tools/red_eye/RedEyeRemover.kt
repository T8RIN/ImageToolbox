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

package com.t8rin.opencv_tools.red_eye

import android.graphics.Bitmap
import com.t8rin.opencv_tools.utils.OpenCV
import com.t8rin.opencv_tools.utils.toBitmap
import com.t8rin.opencv_tools.utils.toMat
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.ensureActive
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Rect
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.FaceDetectorYN
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

object RedEyeRemover : OpenCV() {

    suspend fun removeRedEyes(
        bitmap: Bitmap,
        minEyeSize: Double = 50.0,
        redThreshold: Double = 150.0
    ): Bitmap = coroutineScope {
        ensureActive()
        val srcMat = bitmap.toMat()

        Imgproc.cvtColor(srcMat, srcMat, Imgproc.COLOR_RGBA2BGR)
        val resultMat = srcMat.clone()

        val eyes = detectEyes(srcMat, minEyeSize)

        for (rect in eyes) {
            ensureActive()
            val roi = Rect(rect.x, rect.y, rect.width, rect.height)
            val eyeMat = srcMat.submat(roi).clone()

            val channels = ArrayList<Mat>()
            Core.split(eyeMat, channels)
            val blue = channels[0]
            val green = channels[1]
            val red = channels[2]

            val bg = Mat()
            Core.add(blue, green, bg)

            val maskRed = Mat()
            Imgproc.threshold(red, maskRed, redThreshold, 255.0, Imgproc.THRESH_BINARY)

            val maskCmp = Mat()
            Core.compare(red, bg, maskCmp, Core.CMP_GT)

            val redEyeMask = Mat()
            Core.bitwise_and(maskRed, maskCmp, redEyeMask)

            val filledMask = fillHoles(redEyeMask)
            val dilatedMask = Mat()
            Imgproc.dilate(filledMask, dilatedMask, Mat(), Point(-1.0, -1.0), 3)

            val meanMat = Mat()
            Core.divide(bg, Scalar(2.0), meanMat)

            val mean3 = Mat()
            Core.merge(listOf(meanMat, meanMat, meanMat), mean3)

            val eyeOut = eyeMat.clone()
            mean3.copyTo(eyeOut, dilatedMask)

            eyeOut.copyTo(resultMat.submat(roi))

            eyeMat.release()
            bg.release()
            maskRed.release()
            maskCmp.release()
            redEyeMask.release()
            filledMask.release()
            dilatedMask.release()
            meanMat.release()
            mean3.release()
            eyeOut.release()
            channels.forEach(Mat::release)
        }

        Imgproc.cvtColor(resultMat, resultMat, Imgproc.COLOR_BGR2RGBA)

        val result = resultMat.toBitmap()

        srcMat.release()
        resultMat.release()

        result
    }

    private fun detectEyes(
        srcMat: Mat,
        minEyeSize: Double
    ): List<Rect> {
        val detector = loadFaceDetector(srcMat.size())
        val faces = Mat()

        detector.detect(srcMat, faces)

        val result = buildList {
            for (i in 0 until faces.rows()) {
                val faceWidth = faces.value(i, 2)
                val faceHeight = faces.value(i, 3)

                val eyeSize = max(
                    minEyeSize,
                    min(faceWidth, faceHeight) * 0.16
                )

                createEyeRect(
                    imageWidth = srcMat.cols(),
                    imageHeight = srcMat.rows(),
                    center = Point(
                        faces.value(i, 4),
                        faces.value(i, 5)
                    ),
                    size = eyeSize
                )?.let(::add)

                createEyeRect(
                    imageWidth = srcMat.cols(),
                    imageHeight = srcMat.rows(),
                    center = Point(
                        faces.value(i, 6),
                        faces.value(i, 7)
                    ),
                    size = eyeSize
                )?.let(::add)
            }
        }

        faces.release()

        return result
    }

    private fun createEyeRect(
        imageWidth: Int,
        imageHeight: Int,
        center: Point,
        size: Double
    ): Rect? {
        val left = (center.x - size / 2.0)
            .roundToInt()
            .coerceIn(0, imageWidth - 1)

        val top = (center.y - size / 2.0)
            .roundToInt()
            .coerceIn(0, imageHeight - 1)

        val right = (center.x + size / 2.0)
            .roundToInt()
            .coerceIn(0, imageWidth)

        val bottom = (center.y + size / 2.0)
            .roundToInt()
            .coerceIn(0, imageHeight)

        val rectWidth = right - left
        val rectHeight = bottom - top

        if (rectWidth <= 0 || rectHeight <= 0) return null

        return Rect(left, top, rectWidth, rectHeight)
    }

    private fun fillHoles(mask: Mat): Mat {
        val maskFloodfill = mask.clone()
        val rows = maskFloodfill.rows()
        val cols = maskFloodfill.cols()
        val floodfillMask = Mat.zeros(rows + 2, cols + 2, CvType.CV_8UC1)
        Imgproc.floodFill(maskFloodfill, floodfillMask, Point(0.0, 0.0), Scalar(255.0))
        val maskInv = Mat()
        Core.bitwise_not(maskFloodfill, maskInv)
        val filledMask = Mat()
        Core.bitwise_or(maskInv, mask, filledMask)

        maskFloodfill.release()
        floodfillMask.release()
        maskInv.release()

        return filledMask
    }

    private fun Mat.value(row: Int, col: Int): Double {
        return get(row, col)?.firstOrNull() ?: 0.0
    }

    private fun loadFaceDetector(inputSize: Size): FaceDetectorYN {
        return FaceDetectorYN.create(
            copyAssetToCache(),
            "",
            inputSize,
            0.7f,
            0.3f,
            5000
        )
    }

    private fun copyAssetToCache(assetName: String = "face_detection_yunet_2026may.onnx"): String {
        val file = File(context.cacheDir, assetName)

        if (!file.exists() || file.length() == 0L) {
            context.assets.open(assetName).use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
        }

        return file.absolutePath
    }
}