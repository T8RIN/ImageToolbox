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

        val resultMat = Mat()
        val kernel = Mat()
        try {
            Imgproc.cvtColor(srcMat, srcMat, Imgproc.COLOR_RGBA2BGR)
            srcMat.copyTo(resultMat)
            val eyes = detectEyes(srcMat, minEyeSize)
            for (rect in eyes) {
                ensureActive()
                val eyeMat = srcMat.submat(rect)
                val resultEye = resultMat.submat(rect)
                val channels = ArrayList<Mat>()
                val bg = Mat()
                val maskRed = Mat()
                val maskCmp = Mat()
                val redEyeMask = Mat()
                val dilatedMask = Mat()
                val meanMat = Mat()
                val mean3 = Mat()
                val eyeOut = Mat()
                var filledMask: Mat? = null
                try {
                    Core.split(eyeMat, channels)
                    val blue = channels[0]
                    val green = channels[1]
                    val red = channels[2]
                    Core.add(blue, green, bg)
                    Imgproc.threshold(red, maskRed, redThreshold, 255.0, Imgproc.THRESH_BINARY)
                    Core.compare(red, bg, maskCmp, Core.CMP_GT)
                    Core.bitwise_and(maskRed, maskCmp, redEyeMask)
                    filledMask = fillHoles(redEyeMask)
                    Imgproc.dilate(filledMask, dilatedMask, kernel, Point(-1.0, -1.0), 3)
                    Core.divide(bg, Scalar(2.0), meanMat)
                    Core.merge(listOf(meanMat, meanMat, meanMat), mean3)
                    eyeMat.copyTo(eyeOut)
                    mean3.copyTo(eyeOut, dilatedMask)
                    eyeOut.copyTo(resultEye)
                } finally {
                    eyeMat.release()
                    resultEye.release()
                    channels.forEach(Mat::release)
                    bg.release()
                    maskRed.release()
                    maskCmp.release()
                    redEyeMask.release()
                    filledMask?.release()
                    dilatedMask.release()
                    meanMat.release()
                    mean3.release()
                    eyeOut.release()
                }
            }
            Imgproc.cvtColor(resultMat, resultMat, Imgproc.COLOR_BGR2RGBA)
            resultMat.toBitmap()
        } finally {
            srcMat.release()
            resultMat.release()
            kernel.release()
        }
    }

    private fun detectEyes(
        srcMat: Mat,
        minEyeSize: Double
    ): List<Rect> {
        val detector = loadFaceDetector(srcMat.size())
        val faces = Mat()

        try {
            detector.detect(srcMat, faces)

            return buildList {
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

        } finally {
            faces.release()
        }
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
        try {
            Imgproc.floodFill(maskFloodfill, floodfillMask, Point(0.0, 0.0), Scalar(255.0))
            Core.bitwise_not(maskFloodfill, maskFloodfill)
            Core.bitwise_or(maskFloodfill, mask, maskFloodfill)
            return maskFloodfill
        } catch (t: Throwable) {
            maskFloodfill.release()
            throw t
        } finally {
            floodfillMask.release()
        }
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
