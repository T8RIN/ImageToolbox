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

package com.t8rin.opencv_tools.forensics

import android.graphics.Bitmap
import com.t8rin.opencv_tools.utils.OpenCV
import com.t8rin.opencv_tools.utils.toBitmap
import com.t8rin.opencv_tools.utils.toMat
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.ensureActive
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.CvType.CV_32F
import org.opencv.core.CvType.CV_8U
import org.opencv.core.Mat
import org.opencv.core.MatOfByte
import org.opencv.core.MatOfInt
import org.opencv.core.Rect
import org.opencv.core.Scalar
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.util.Random
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

object ImageForensics : OpenCV() {

    suspend fun errorLevelAnalysis(
        input: Bitmap,
        quality: Int = 90
    ): Bitmap = coroutineScope {
        ensureActive()
        val src = input.toMat()
        val buf = MatOfByte()
        val params = MatOfInt(Imgcodecs.IMWRITE_JPEG_QUALITY, quality)
        val diff = Mat()
        val dst = Mat()
        var resaved: Mat? = null
        try {
            Imgproc.cvtColor(src, src, Imgproc.COLOR_RGBA2BGR)
            Imgcodecs.imencode(".jpg", src, buf, params)
            ensureActive()
            resaved = Imgcodecs.imdecode(buf, Imgcodecs.IMREAD_COLOR)
            Core.absdiff(src, resaved, diff)
            Core.normalize(diff, dst, 0.0, 255.0, Core.NORM_MINMAX)
            dst.convertTo(dst, CvType.CV_8UC3)
            Imgproc.cvtColor(dst, dst, Imgproc.COLOR_BGR2RGBA)
            ensureActive()
            dst.toBitmap()
        } finally {
            src.release()
            buf.release()
            params.release()
            resaved?.release()
            diff.release()
            dst.release()
        }
    }

    suspend fun luminanceGradient(input: Bitmap): Bitmap = coroutineScope {
        ensureActive()
        val src = input.toMat()
        val gray = Mat()
        val sobelX = Mat()
        val sobelY = Mat()
        val magnitude = Mat()
        val dst = Mat()
        val channels = ArrayList<Mat>(3)
        try {
            Imgproc.cvtColor(src, src, Imgproc.COLOR_RGBA2BGR)
            Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY)
            gray.convertTo(gray, CV_32F)
            Imgproc.Sobel(gray, sobelX, CV_32F, 1, 0, 3, 1.0, 0.0)
            Imgproc.Sobel(gray, sobelY, CV_32F, 0, 1, 3, 1.0, 0.0)
            Core.magnitude(sobelX, sobelY, magnitude)

            val rows = magnitude.rows()
            val cols = magnitude.cols()
            dst.create(rows, cols, CvType.CV_32FC3)
            val sxRow = FloatArray(cols)
            val syRow = FloatArray(cols)
            val magRow = FloatArray(cols)
            val outRow = FloatArray(cols * 3)
            for (r in 0 until rows) {
                ensureActive()
                sobelX.get(r, 0, sxRow)
                sobelY.get(r, 0, syRow)
                magnitude.get(r, 0, magRow)
                for (c in 0 until cols) {
                    val angle = atan2(sxRow[c].toDouble(), syRow[c].toDouble()).toFloat()
                    val mag = magRow[c]
                    val g = (-sin(angle.toDouble()) / 2.0 + 0.5).toFloat()
                    val rchan = (-cos(angle.toDouble()) / 2.0 + 0.5).toFloat()
                    outRow[c * 3] = mag
                    outRow[c * 3 + 1] = g
                    outRow[c * 3 + 2] = rchan
                }
                dst.put(r, 0, outRow)
            }

            Core.split(dst, channels)
            ensureActive()
            Core.normalize(channels[0], channels[0], 0.0, 1.0, Core.NORM_MINMAX)
            Core.merge(channels, dst)
            dst.convertTo(dst, CvType.CV_8UC3, 255.0)
            Imgproc.cvtColor(dst, dst, Imgproc.COLOR_BGR2RGBA)
            dst.toBitmap()
        } finally {
            src.release()
            gray.release()
            sobelX.release()
            sobelY.release()
            magnitude.release()
            dst.release()
            channels.forEach(Mat::release)
        }
    }

    suspend fun averageDistance(input: Bitmap): Bitmap = coroutineScope {
        ensureActive()
        val src = input.toMat()
        val src32 = Mat()
        val kernel = Mat(3, 3, CV_32F)
        val filtered = Mat()
        val diff = Mat()
        try {
            Imgproc.cvtColor(src, src, Imgproc.COLOR_RGBA2BGR)
            src.convertTo(src32, CV_32F, 1.0 / 255.0)
            kernel.put(0, 0, 0.0, 0.25, 0.0)
            kernel.put(1, 0, 0.25, 0.0, 0.25)
            kernel.put(2, 0, 0.0, 0.25, 0.0)
            Imgproc.filter2D(src32, filtered, -1, kernel)
            ensureActive()
            Core.absdiff(src32, filtered, diff)
            Core.normalize(diff, diff, 0.0, 1.0, Core.NORM_MINMAX)
            diff.convertTo(diff, CvType.CV_8UC3, 255.0)
            Imgproc.cvtColor(diff, diff, Imgproc.COLOR_BGR2RGBA)
            ensureActive()
            diff.toBitmap()
        } finally {
            src.release()
            src32.release()
            kernel.release()
            filtered.release()
            diff.release()
        }
    }

    suspend fun detectCopyMove(
        input: Bitmap,
        retain: Int = 4,
        qCoefficent: Double = 1.0
    ): Bitmap = coroutineScope {
        ensureActive()
        val srcColor = input.toMat()
        val srcGray = Mat()
        val tmp = Mat()
        val quantized = Mat()
        val rectBuffer = Mat()
        val dst = Mat()
        try {
            Imgproc.cvtColor(srcColor, srcGray, Imgproc.COLOR_BGR2GRAY)
            srcGray.convertTo(srcGray, CV_32F)

            val blockSize = 16
            val blocksHeight = srcGray.rows() - blockSize + 1
            val blocksWidth = srcGray.cols() - blockSize + 1
            if (blocksHeight <= 0 || blocksWidth <= 0) return@coroutineScope srcColor.toBitmap()
            val totalBlocks = blocksHeight * blocksWidth
            val retained = retain.coerceIn(1, blockSize)
            val featureSize = retained * retained
            val features = ByteArray(Math.multiplyExact(totalBlocks, featureSize))
            val bytes = ByteArray(blockSize * blockSize)
            for (y in 0 until blocksHeight) {
                ensureActive()
                for (x in 0 until blocksWidth) {
                    ensureActive()
                    val roi = srcGray.submat(y, y + blockSize, x, x + blockSize)
                    try {
                        Core.dct(roi, tmp)
                    } finally {
                        roi.release()
                    }
                    Core.divide(tmp, Scalar(qCoefficent), tmp)
                    tmp.convertTo(quantized, CV_8U)
                    quantized.get(0, 0, bytes)
                    val offset = (y * blocksWidth + x) * featureSize
                    for (row in 0 until retained) {
                        bytes.copyInto(
                            features,
                            offset + row * retained,
                            row * blockSize,
                            row * blockSize + retained
                        )
                    }
                }
            }

            fun compareBlocks(a: Int, b: Int): Int {
                val first = a * featureSize
                val second = b * featureSize
                for (i in 0 until featureSize) {
                    val diff = features[first + i].toInt() - features[second + i].toInt()
                    if (diff != 0) return diff
                }
                return 0
            }

            ensureActive()
            val index = Array(totalBlocks) { it }
            index.sortWith { a, b ->
                ensureActive()
                compareBlocks(a, b)
            }

            val sCount = IntArray(srcGray.rows() * srcGray.cols() * 2)
            srcColor.copyTo(rectBuffer)
            for (i in 0 until totalBlocks - 1) {
                ensureActive()
                if (compareBlocks(index[i], index[i + 1]) != 0) continue
                val curX = index[i] % blocksWidth
                val curY = index[i] / blocksWidth
                val nextX = index[i + 1] % blocksWidth
                val nextY = index[i + 1] / blocksWidth
                val shiftX = abs(curX - nextX)
                val shiftY = abs(curY - nextY)
                if (hypot(shiftX.toDouble(), shiftY.toDouble()) > blockSize) {
                    sCount[(shiftY + srcGray.rows()) * srcGray.cols() + shiftX]++
                }
            }

            for (i in 0 until totalBlocks - 1) {
                ensureActive()
                if (compareBlocks(index[i], index[i + 1]) != 0) continue
                val curX = index[i] % blocksWidth
                val curY = index[i] / blocksWidth
                val nextX = index[i + 1] % blocksWidth
                val nextY = index[i + 1] / blocksWidth
                val shiftX = abs(curX - nextX)
                val shiftY = abs(curY - nextY)
                if (sCount[(shiftY + srcGray.rows()) * srcGray.cols() + shiftX] > 10) {
                    val magnitude = hypot(shiftX.toDouble(), shiftY.toDouble())
                    val rng = Random(magnitude.toLong())
                    val color = Scalar(
                        rng.nextInt(256).toDouble(),
                        rng.nextInt(256).toDouble(),
                        rng.nextInt(256).toDouble()
                    )
                    Imgproc.rectangle(
                        rectBuffer,
                        Rect(curX, curY, blockSize, blockSize),
                        color,
                        Imgproc.FILLED
                    )
                    Imgproc.rectangle(
                        rectBuffer,
                        Rect(nextX, nextY, blockSize, blockSize),
                        color,
                        Imgproc.FILLED
                    )
                }
            }

            Core.addWeighted(srcColor, 0.2, rectBuffer, 0.8, 0.0, dst)
            dst.toBitmap()
        } finally {
            srcColor.release()
            srcGray.release()
            tmp.release()
            quantized.release()
            rectBuffer.release()
            dst.release()
        }
    }

}