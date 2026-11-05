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

package com.t8rin.opencv_tools.moire

import android.graphics.Bitmap
import com.t8rin.opencv_tools.moire.model.MoireType
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
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

object Moire : OpenCV() {

    suspend fun remove(
        bitmap: Bitmap,
        type: MoireType = MoireType.AUTO
    ): Bitmap = coroutineScope {
        ensureActive()
        val rgba = bitmap.toMat()
        val channel = Mat()
        val imaginary = Mat()
        val complex = Mat()
        val filter = Mat()
        try {
            imaginary.create(rgba.size(), CvType.CV_32F)
            imaginary.setTo(Scalar.all(0.0))
            val butterworth = butterworthLP(rgba.size(), d0 = 80.0, n = 10)
            try {
                Core.merge(listOf(butterworth, butterworth), filter)
            } finally {
                butterworth.release()
            }

            for (index in 0..2) {
                ensureActive()
                Core.extractChannel(rgba, channel, index)
                channel.convertTo(channel, CvType.CV_32F)
                Core.merge(listOf(channel, imaginary), complex)
                Core.dft(complex, complex)
                shiftDFT(complex)

                val magnitude = magnitude(complex)
                try {
                    val appliedType = if (type == MoireType.AUTO) {
                        detectNoiseType(magnitude) ?: MoireType.VERTICAL
                    } else type
                    applyMask(complex, magnitude, appliedType)
                } finally {
                    magnitude.release()
                }

                Core.mulSpectrums(complex, filter, complex, 0)
                shiftDFT(complex)
                Core.idft(complex, channel, Core.DFT_SCALE or Core.DFT_REAL_OUTPUT, 0)
                channel.convertTo(channel, CvType.CV_8U)
                Core.insertChannel(channel, rgba, index)
            }

            rgba.toBitmap()
        } finally {
            rgba.release()
            channel.release()
            imaginary.release()
            complex.release()
            filter.release()
        }
    }

    private fun magnitude(complex: Mat): Mat {
        val planes = mutableListOf<Mat>()
        val magnitude = Mat()
        try {
            Core.split(complex, planes)
            Core.magnitude(planes[0], planes[1], magnitude)
            Core.add(magnitude, Scalar.all(1.0), magnitude)
            Core.log(magnitude, magnitude)
            Core.normalize(magnitude, magnitude, 0.0, 255.0, Core.NORM_MINMAX)
            magnitude.convertTo(magnitude, CvType.CV_8U)
            return magnitude
        } catch (t: Throwable) {
            magnitude.release()
            throw t
        } finally {
            planes.forEach(Mat::release)
        }
    }

    private suspend fun detectNoiseType(magnitude: Mat): MoireType? = coroutineScope {
        val rows = magnitude.rows()
        val cols = magnitude.cols()
        val centerX = cols / 2.0
        val centerY = rows / 2.0
        val peaks = mutableListOf<Point>()
        val row = ByteArray(cols)
        for (y in 0 until rows) {
            ensureActive()
            magnitude.get(y, 0, row)
            for (x in 0 until cols) {
                if ((row[x].toInt() and 0xff) > 180 &&
                    (abs(x - centerX) > 10 || abs(y - centerY) > 10)
                ) {
                    peaks.add(Point(x.toDouble(), y.toDouble()))
                }
            }
        }
        if (peaks.size < 2) return@coroutineScope null

        val data = DoubleArray(peaks.size * 2)
        for (index in peaks.indices) {
            ensureActive()
            data[index * 2] = peaks[index].x
            data[index * 2 + 1] = peaks[index].y
        }
        val dataPts = Mat()
        val mean = Mat()
        val eigenvectors = Mat()
        try {
            dataPts.create(peaks.size, 2, CvType.CV_64F)
            dataPts.put(0, 0, *data)
            Core.PCACompute(dataPts, mean, eigenvectors)
            val direction = DoubleArray(2)
            eigenvectors.get(0, 0, direction)
            val angle = atan2(direction[1], direction[0]) * 180.0 / Math.PI
            when {
                abs(angle) < 15 -> MoireType.VERTICAL
                abs(angle) > 70 && abs(angle) < 110 -> MoireType.HORIZONTAL
                angle in 15.0..60.0 -> MoireType.RIGHT_DIAGONAL
                angle in -60.0..-30.0 -> MoireType.LEFT_DIAGONAL
                else -> null
            }
        } finally {
            dataPts.release()
            mean.release()
            eigenvectors.release()
        }
    }

    private suspend fun butterworthLP(size: Size, d0: Double, n: Int): Mat = coroutineScope {
        val rows = size.height.toInt()
        val cols = size.width.toInt()
        val centerX = rows / 2.0
        val centerY = cols / 2.0
        val filter = Mat(rows, cols, CvType.CV_32F)
        val row = FloatArray(cols)
        try {
            for (i in 0 until rows) {
                ensureActive()
                for (j in 0 until cols) {
                    val d = sqrt((i - centerX).pow(2) + (j - centerY).pow(2))
                    row[j] = (1.0 / (1.0 + (d / d0).pow(2.0 * n))).toFloat()
                }
                filter.put(i, 0, row)
            }
            filter
        } catch (t: Throwable) {
            filter.release()
            throw t
        }
    }

    private suspend fun applyMask(
        dftMat: Mat,
        magnitude: Mat,
        type: MoireType
    ) = coroutineScope {
        val rows = dftMat.rows()
        val cols = dftMat.cols()
        val crow = rows / 2
        val ccol = cols / 2
        val radius = 8
        val circleWidths = IntArray(radius * 2 + 1) {
            val y = it - radius
            sqrt((radius * radius - y * y).toDouble()).toInt()
        }
        val zeros = Array(radius * 2 + 2) { FloatArray(it * 2) }
        val row = ByteArray(cols)
        for (y in 0 until rows) {
            ensureActive()
            magnitude.get(y, 0, row)
            for (x in 0 until cols) {
                if ((row[x].toInt() and 0xff) <= 180) continue
                val dx = x - ccol
                val dy = y - crow
                if (abs(dx) <= 10 && abs(dy) <= 10) continue
                val angle = atan2(dy.toDouble(), dx.toDouble()) * 180.0 / Math.PI
                val matched = when (type) {
                    MoireType.VERTICAL -> abs(angle) !in 15.0..165.0
                    MoireType.HORIZONTAL -> abs(angle - 90) < 15 || abs(angle + 90) < 15
                    MoireType.RIGHT_DIAGONAL -> abs(angle - 45) < 15 || abs(angle + 135) < 15
                    MoireType.LEFT_DIAGONAL -> abs(angle + 45) < 15 || abs(angle - 135) < 15
                    MoireType.AUTO -> false
                }
                if (!matched) continue
                for (offset in -radius..radius) {
                    ensureActive()
                    if (y + offset !in 0 until rows) continue
                    val width = circleWidths[offset + radius]
                    val start = (x - width).coerceAtLeast(0)
                    val end = (x + width).coerceAtMost(cols - 1)
                    dftMat.put(y + offset, start, zeros[end - start + 1])
                }
            }
        }
    }

    private fun shiftDFT(mat: Mat) {
        val cx = mat.cols() / 2
        val cy = mat.rows() / 2
        val q0 = Mat(mat, Rect(0, 0, cx, cy))
        val q1 = Mat(mat, Rect(cx, 0, cx, cy))
        val q2 = Mat(mat, Rect(0, cy, cx, cy))
        val q3 = Mat(mat, Rect(cx, cy, cx, cy))
        val tmp = Mat()
        try {
            q0.copyTo(tmp)
            q3.copyTo(q0)
            tmp.copyTo(q3)
            q1.copyTo(tmp)
            q2.copyTo(q1)
            tmp.copyTo(q2)
        } finally {
            q0.release()
            q1.release()
            q2.release()
            q3.release()
            tmp.release()
        }
    }

}