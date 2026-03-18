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
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.CvType.CV_32F
import org.opencv.core.CvType.CV_8U
import org.opencv.core.Mat
import org.opencv.core.MatOfByte
import org.opencv.core.MatOfInt
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

    fun errorLevelAnalysis(
        input: Bitmap,
        quality: Int = 90
    ): Bitmap {
        val src = input.toMat()
        Utils.bitmapToMat(input, src)
        Imgproc.cvtColor(src, src, Imgproc.COLOR_RGBA2BGR)

        val buf = MatOfByte()
        val params = MatOfInt(Imgcodecs.IMWRITE_JPEG_QUALITY, quality)
        Imgcodecs.imencode(".jpg", src, buf, params)

        val resaved = Imgcodecs.imdecode(buf, Imgcodecs.IMREAD_COLOR)

        val diff = Mat()
        Core.absdiff(src, resaved, diff)

        val dst = Mat()
        Core.normalize(diff, dst, 0.0, 255.0, Core.NORM_MINMAX)
        dst.convertTo(dst, CvType.CV_8UC3)

        Imgproc.cvtColor(dst, dst, Imgproc.COLOR_BGR2RGBA)
        val outBmp = dst.toBitmap()

        src.release()
        resaved.release()
        diff.release()
        dst.release()
        buf.release()

        return outBmp
    }

    fun luminanceGradient(input: Bitmap): Bitmap {
        val src = input.toMat()
        Imgproc.cvtColor(src, src, Imgproc.COLOR_RGBA2BGR)

        val gray = Mat()
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY)
        gray.convertTo(gray, CV_32F)

        val sobelX = Mat()
        val sobelY = Mat()
        Imgproc.Sobel(gray, sobelX, CV_32F, 1, 0, 3, 1.0, 0.0)
        Imgproc.Sobel(gray, sobelY, CV_32F, 0, 1, 3, 1.0, 0.0)

        val magnitude = Mat()
        Core.magnitude(sobelX, sobelY, magnitude)

        val rows = magnitude.rows()
        val cols = magnitude.cols()

        val dst = Mat(rows, cols, CvType.CV_32FC3)

        val sxRow = FloatArray(cols)
        val syRow = FloatArray(cols)
        val magRow = FloatArray(cols)

        for (r in 0 until rows) {
            sobelX.get(r, 0, sxRow)
            sobelY.get(r, 0, syRow)
            magnitude.get(r, 0, magRow)
            val outRow = FloatArray(cols * 3)
            for (c in 0 until cols) {
                val angle = atan2(sxRow[c].toDouble(), syRow[c].toDouble()).toFloat()
                val mag = magRow[c]
                val g = (-sin(angle.toDouble()) / 2.0 + 0.5).toFloat()
                val rchan = (-cos(angle.toDouble()) / 2.0 + 0.5).toFloat()
                outRow[c * 3 + 0] = mag
                outRow[c * 3 + 1] = g
                outRow[c * 3 + 2] = rchan
            }
            dst.put(r, 0, outRow)
        }

        val channels = ArrayList<Mat>(3)
        Core.split(dst, channels)
        Core.normalize(channels[0], channels[0], 0.0, 1.0, Core.NORM_MINMAX)
        Core.merge(channels, dst)

        dst.convertTo(dst, CvType.CV_8UC3, 255.0)
        Imgproc.cvtColor(dst, dst, Imgproc.COLOR_BGR2RGBA)
        val outBmp = dst.toBitmap()

        gray.release()
        sobelX.release()
        sobelY.release()
        magnitude.release()

        for (m in channels) m.release()

        dst.release()
        src.release()

        return outBmp
    }

    fun averageDistance(input: Bitmap): Bitmap {
        val src = input.toMat()
        Imgproc.cvtColor(src, src, Imgproc.COLOR_RGBA2BGR)

        val src32 = Mat()
        src.convertTo(src32, CV_32F, 1.0 / 255.0)

        val kernel = Mat(3, 3, CV_32F)
        kernel.put(0, 0, 0.0, 0.25, 0.0)
        kernel.put(1, 0, 0.25, 0.0, 0.25)
        kernel.put(2, 0, 0.0, 0.25, 0.0)

        val filtered = Mat()
        Imgproc.filter2D(src32, filtered, -1, kernel)

        val diff = Mat()
        Core.absdiff(src32, filtered, diff)
        Core.normalize(diff, diff, 0.0, 1.0, Core.NORM_MINMAX)

        diff.convertTo(diff, CvType.CV_8UC3, 255.0)
        Imgproc.cvtColor(diff, diff, Imgproc.COLOR_BGR2RGBA)
        val outBmp = diff.toBitmap()

        src.release()
        src32.release()
        kernel.release()
        filtered.release()

        diff.release()
        return outBmp
    }

    fun detectCopyMove(
        input: Bitmap,
        retain: Int = 4,
        qCoefficent: Double = 1.0
    ): Bitmap {
        val srcColor = input.toMat()
        val srcGray = Mat()
        Imgproc.cvtColor(srcColor, srcGray, Imgproc.COLOR_BGR2GRAY)
        srcGray.convertTo(srcGray, CV_32F)

        val blockSize = 16
        val blocksHeight = srcGray.rows() - blockSize + 1
        val blocksWidth = srcGray.cols() - blockSize + 1
        val totalBlocks = blocksHeight * blocksWidth
        val blocks = ArrayList<Mat>(totalBlocks)
        val tmp = Mat()

        for (y in 0 until blocksHeight) {
            for (x in 0 until blocksWidth) {
                val roi = srcGray.submat(y, y + blockSize, x, x + blockSize)
                Core.dct(roi, tmp)
                Core.divide(tmp, Scalar(qCoefficent), tmp)
                tmp.convertTo(tmp, CV_8U)
                blocks.add(tmp.submat(0, retain, 0, retain).clone())
            }
        }

        val index = Array(totalBlocks) { it }
        index.sortWith { a, b ->
            val aBytes = ByteArray(retain * retain)
            val bBytes = ByteArray(retain * retain)
            blocks[a].get(0, 0, aBytes)
            blocks[b].get(0, 0, bBytes)
            for (i in aBytes.indices) {
                val diff = aBytes[i].toInt() - bBytes[i].toInt()
                if (diff != 0) return@sortWith diff
            }
            0
        }

        val sCount = IntArray(srcGray.rows() * srcGray.cols() * 2)
        val rectBuffer = srcColor.clone()

        for (i in 0 until totalBlocks - 1) {
            val aBytes = ByteArray(retain * retain)
            val bBytes = ByteArray(retain * retain)
            blocks[index[i]].get(0, 0, aBytes)
            blocks[index[i + 1]].get(0, 0, bBytes)
            if (aBytes.contentEquals(bBytes)) {
                val curX = index[i] % blocksWidth
                val curY = index[i] / blocksWidth
                val nextX = index[i + 1] % blocksWidth
                val nextY = index[i + 1] / blocksWidth
                val shiftX = abs(curX - nextX)
                var shiftY = abs(curY - nextY)
                val magnitude = hypot(shiftX.toDouble(), shiftY.toDouble())
                shiftY += srcGray.rows()
                if (magnitude > blockSize) sCount[shiftY * srcGray.cols() + shiftX]++
            }
        }

        for (i in 0 until totalBlocks - 1) {
            val aBytes = ByteArray(retain * retain)
            val bBytes = ByteArray(retain * retain)
            blocks[index[i]].get(0, 0, aBytes)
            blocks[index[i + 1]].get(0, 0, bBytes)
            if (aBytes.contentEquals(bBytes)) {
                val curX = index[i] % blocksWidth
                val curY = index[i] / blocksWidth
                val nextX = index[i + 1] % blocksWidth
                val nextY = index[i + 1] / blocksWidth
                val shiftX = abs(curX - nextX)
                var shiftY = abs(curY - nextY)
                val magnitude = hypot(shiftX.toDouble(), shiftY.toDouble())
                shiftY += srcGray.rows()
                if (sCount[shiftY * srcGray.cols() + shiftX] > 10) {
                    val rng = Random(magnitude.toLong())
                    val color = Scalar(
                        rng.nextInt(256).toDouble(),
                        rng.nextInt(256).toDouble(),
                        rng.nextInt(256).toDouble()
                    )
                    for (ii in 0 until blockSize) {
                        for (jj in 0 until blockSize) {
                            rectBuffer.put(curY + ii, curX + jj, *color.`val`)
                            rectBuffer.put(nextY + ii, nextX + jj, *color.`val`)
                        }
                    }
                }
            }
        }

        val dst = Mat()
        Core.addWeighted(srcColor, 0.2, rectBuffer, 0.8, 0.0, dst)
        return dst.toBitmap()
    }

}