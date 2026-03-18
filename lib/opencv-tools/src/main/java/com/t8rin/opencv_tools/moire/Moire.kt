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

@file:Suppress("SameParameterValue", "unused")

package com.t8rin.opencv_tools.moire

import android.graphics.Bitmap
import android.util.Log
import com.t8rin.opencv_tools.moire.model.MoireType
import com.t8rin.opencv_tools.utils.OpenCV
import com.t8rin.opencv_tools.utils.toBitmap
import com.t8rin.opencv_tools.utils.toMat
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Rect
import org.opencv.core.Scalar
import org.opencv.core.Size
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

object Moire : OpenCV() {

    fun remove(
        bitmap: Bitmap,
        type: MoireType = MoireType.AUTO
    ): Bitmap {
        val rgba = bitmap.toMat()

        val channels = mutableListOf<Mat>()
        Core.split(rgba, channels)

        val filteredChannels = channels.take(3).map { channel ->
            val floatMat = Mat()
            channel.convertTo(floatMat, CvType.CV_32F)

            val planes = mutableListOf(floatMat, Mat.zeros(channel.size(), CvType.CV_32F))
            val complex = Mat()
            Core.merge(planes, complex)
            Core.dft(complex, complex)
            shiftDFT(complex)

            val detected = detectNoiseType(complex)
            val appliedType =
                type.takeIf { it != MoireType.AUTO } ?: (detected ?: MoireType.VERTICAL)

            Log.d("Moire", "Detected: $detected, Applied: $appliedType")

            applyMask(complex, appliedType)

            val butterworth = butterworthLP(size = channel.size(), d0 = 80.0, n = 10)
            val filterPlanes = mutableListOf(butterworth, butterworth.clone())
            val filter = Mat()
            Core.merge(filterPlanes, filter)
            Core.mulSpectrums(complex, filter, complex, 0)

            shiftDFT(complex)

            val inverse = Mat()
            Core.idft(complex, inverse, Core.DFT_SCALE or Core.DFT_REAL_OUTPUT, 0)
            inverse.convertTo(inverse, CvType.CV_8U)
            inverse
        }

        // Собираем каналы обратно + alpha если был
        val outputChannels = ArrayList(filteredChannels)
        if (channels.size == 4) {
            outputChannels.add(channels[3]) // alpha
        }

        val merged = Mat()
        Core.merge(outputChannels, merged)

        return merged.toBitmap()
    }

    private fun detectNoiseType(dftMat: Mat): MoireType? {
        val planes = mutableListOf<Mat>()
        Core.split(dftMat, planes)
        val mag = Mat()
        Core.magnitude(planes[0], planes[1], mag)

        Core.add(mag, Scalar.all(1.0), mag)
        Core.log(mag, mag)

        Core.normalize(mag, mag, 0.0, 255.0, Core.NORM_MINMAX)
        mag.convertTo(mag, CvType.CV_8U)

        val center = Point(mag.cols() / 2.0, mag.rows() / 2.0)

        val threshold = 180.0
        val peaks = mutableListOf<Point>()

        for (y in 0 until mag.rows()) {
            for (x in 0 until mag.cols()) {
                val value = mag.get(y, x)[0]
                if (value > threshold) {
                    val dx = abs(x - center.x)
                    val dy = abs(y - center.y)
                    if (dx > 10 || dy > 10) {
                        peaks.add(Point(x.toDouble(), y.toDouble()))
                    }
                }
            }
        }

        Log.d("Moire", "Detected peaks: ${peaks.size}")
        if (peaks.size < 2) return null

        val dataPts = Mat(peaks.size, 2, CvType.CV_64F)
        for (i in peaks.indices) {
            dataPts.put(i, 0, peaks[i].x)
            dataPts.put(i, 1, peaks[i].y)
        }

        val mean = Mat()
        val eigenvectors = Mat()
        Core.PCACompute(dataPts, mean, eigenvectors)

        val vx = eigenvectors.get(0, 0)[0]
        val vy = eigenvectors.get(0, 1)[0]

        val angle = atan2(vy, vx) * 180.0 / Math.PI
        Log.d("Moire", "Angle: $angle")

        return when {
            abs(angle) < 15 -> MoireType.VERTICAL
            abs(angle) > 70 && abs(angle) < 110 -> MoireType.HORIZONTAL
            angle in 15.0..60.0 -> MoireType.RIGHT_DIAGONAL
            angle in -60.0..-30.0 -> MoireType.LEFT_DIAGONAL
            else -> null
        }
    }

    private fun butterworthLP(size: Size, d0: Double, n: Int): Mat {
        val rows = size.height.toInt()
        val cols = size.width.toInt()
        val centerX = rows / 2.0
        val centerY = cols / 2.0

        val filter = Mat(rows, cols, CvType.CV_32F)
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                val d = sqrt((i - centerX).pow(2) + (j - centerY).pow(2))
                val value = 1.0 / (1.0 + (d / d0).pow(2.0 * n))
                filter.put(i, j, value)
            }
        }
        return filter
    }

    private fun applyMask(dftMat: Mat, type: MoireType?) {
        if (type == null || type == MoireType.AUTO) return

        val rows = dftMat.rows()
        val cols = dftMat.cols()
        val crow = rows / 2
        val ccol = cols / 2

        val radius = 8

        fun suppressCircle(cx: Int, cy: Int) {
            for (y in -radius..radius) {
                for (x in -radius..radius) {
                    val dx = cx + x
                    val dy = cy + y
                    if (dx in 0 until cols && dy in 0 until rows) {
                        if (x * x + y * y <= radius * radius) {
                            dftMat.put(dy, dx, 0.0, 0.0)
                        }
                    }
                }
            }
        }

        val planes = mutableListOf<Mat>()
        Core.split(dftMat, planes)
        val mag = Mat()
        Core.magnitude(planes[0], planes[1], mag)

        Core.add(mag, Scalar.all(1.0), mag)
        Core.log(mag, mag)
        Core.normalize(mag, mag, 0.0, 255.0, Core.NORM_MINMAX)
        mag.convertTo(mag, CvType.CV_8U)

        val center = Point(ccol.toDouble(), crow.toDouble())
        val threshold = 180.0
        val peaks = mutableListOf<Point>()

        for (y in 0 until rows) {
            for (x in 0 until cols) {
                val value = mag.get(y, x)[0]
                if (value > threshold) {
                    val dx = x - center.x
                    val dy = y - center.y
                    if (abs(dx) > 10 || abs(dy) > 10) {
                        peaks.add(Point(x.toDouble(), y.toDouble()))
                    }
                }
            }
        }

        Log.d("Moire", "Masking ${peaks.size} peaks for $type")

        for (peak in peaks) {
            val dx = peak.x - center.x
            val dy = peak.y - center.y
            val angle = atan2(dy, dx) * 180.0 / Math.PI

            val matched = when (type) {
                MoireType.VERTICAL -> abs(angle) !in 15.0..165.0
                MoireType.HORIZONTAL -> abs(angle - 90) < 15 || abs(angle + 90) < 15
                MoireType.RIGHT_DIAGONAL -> abs(angle - 45) < 15 || abs(angle + 135) < 15
                MoireType.LEFT_DIAGONAL -> abs(angle + 45) < 15 || abs(angle - 135) < 15
            }

            if (matched) {
                suppressCircle(peak.x.toInt(), peak.y.toInt())
            }
        }
    }

    private fun shiftDFT(mat: Mat) {
        val cx = mat.cols() / 2
        val cy = mat.rows() / 2

        val q0 = Mat(mat, Rect(0, 0, cx, cy))        // Top-Left
        val q1 = Mat(mat, Rect(cx, 0, cx, cy))       // Top-Right
        val q2 = Mat(mat, Rect(0, cy, cx, cy))       // Bottom-Left
        val q3 = Mat(mat, Rect(cx, cy, cx, cy))      // Bottom-Right

        val tmp = Mat()
        q0.copyTo(tmp)
        q3.copyTo(q0)
        tmp.copyTo(q3)

        q1.copyTo(tmp)
        q2.copyTo(q1)
        tmp.copyTo(q2)
    }

}