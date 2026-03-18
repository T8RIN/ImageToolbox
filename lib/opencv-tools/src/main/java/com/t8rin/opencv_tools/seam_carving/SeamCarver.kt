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

package com.t8rin.opencv_tools.seam_carving

import android.graphics.Bitmap
import com.t8rin.opencv_tools.utils.OpenCV
import com.t8rin.opencv_tools.utils.toBitmap
import com.t8rin.opencv_tools.utils.toMat
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import kotlin.math.min

object SeamCarver : OpenCV() {

    /**
     * Main entry:
     * input: bitmap, desiredWidth, desiredHeight
     * returns: processed Bitmap (content-aware reduced). If desired dimensions are larger than source,
     * result will be upscaled with normal resizing after seam carving.
     */
    fun carve(bitmap: Bitmap, desiredWidth: Int, desiredHeight: Int): Bitmap {
        var mat = bitmap.toMat()

        val targetW = desiredWidth.coerceAtLeast(1)
        val targetH = desiredHeight.coerceAtLeast(1)

        // If target dims are bigger than current, we will only seam-carve to min(current, target) and then resize up.
        val targetWClamp = min(targetW, mat.cols())
        val targetHClamp = min(targetH, mat.rows())

        // reduce width
        while (mat.cols() > targetWClamp) {
            val energy = computeEnergy(mat)
            val seam = findVerticalSeam(energy)
            mat = removeVerticalSeam(mat, seam)
        }

        // reduce height via transpose trick
        mat = transposeMat(mat)
        while (mat.cols() > targetHClamp) {
            val energy = computeEnergy(mat)
            val seam = findVerticalSeam(energy)
            mat = removeVerticalSeam(mat, seam)
        }
        mat = transposeMat(mat)

        // If the user requested larger dimensions than we could seam-carve to, upscale with interpolation.
        val finalMat = if (mat.cols() != targetW || mat.rows() != targetH) {
            val dst = Mat()
            Imgproc.resize(
                mat,
                dst,
                Size(targetW.toDouble(), targetH.toDouble()),
                0.0,
                0.0,
                Imgproc.INTER_CUBIC
            )
            dst
        } else {
            mat
        }

        return finalMat.toBitmap()
    }

    private fun transposeMat(src: Mat): Mat {
        val dst = Mat()
        Core.transpose(src, dst)
        return dst
    }

    /**
     * Compute energy map using gradient magnitude (Sobel)
     * returns Mat of type CV_64F with shape rows x cols
     */
    private fun computeEnergy(src: Mat): Mat {
        val gray = Mat()
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_RGB2GRAY)
        gray.convertTo(gray, CvType.CV_64F)

        val gradX = Mat()
        val gradY = Mat()
        Imgproc.Sobel(gray, gradX, CvType.CV_64F, 1, 0, 3, 1.0, 0.0, Core.BORDER_DEFAULT)
        Imgproc.Sobel(gray, gradY, CvType.CV_64F, 0, 1, 3, 1.0, 0.0, Core.BORDER_DEFAULT)

        val gradXSq = Mat()
        val gradYSq = Mat()
        Core.multiply(gradX, gradX, gradXSq)
        Core.multiply(gradY, gradY, gradYSq)

        val energy = Mat()
        Core.add(gradXSq, gradYSq, energy)
        Core.sqrt(energy, energy) // sqrt(gx^2 + gy^2)
        return energy
    }

    /**
     * Find vertical seam (one pixel per row) with minimum cumulative energy.
     * energy: CV_64F Mat.
     * returns IntArray of length rows where each element is column index of seam in that row.
     */
    private fun findVerticalSeam(energy: Mat): IntArray {
        val rows = energy.rows()
        val cols = energy.cols()

        // read energy into 2D double array
        val e = Array(rows) { DoubleArray(cols) }
        val buf = DoubleArray(cols)
        for (r in 0 until rows) {
            energy.get(r, 0, buf)
            for (c in 0 until cols) e[r][c] = buf[c]
        }

        // dp cumulative energy
        val dp = Array(rows) { DoubleArray(cols) { Double.POSITIVE_INFINITY } }
        val backtrack = Array(rows) { IntArray(cols) }

        // initialize first row
        for (c in 0 until cols) dp[0][c] = e[0][c]

        for (r in 1 until rows) {
            for (c in 0 until cols) {
                // consider three pixels from previous row
                var minPrev = dp[r - 1][c]
                var idx = c
                if (c > 0 && dp[r - 1][c - 1] < minPrev) {
                    minPrev = dp[r - 1][c - 1]; idx = c - 1
                }
                if (c < cols - 1 && dp[r - 1][c + 1] < minPrev) {
                    minPrev = dp[r - 1][c + 1]; idx = c + 1
                }
                dp[r][c] = e[r][c] + minPrev
                backtrack[r][c] = idx
            }
        }

        // find min in last row
        var minCol = 0
        var minVal = dp[rows - 1][0]
        for (c in 1 until cols) {
            if (dp[rows - 1][c] < minVal) {
                minVal = dp[rows - 1][c]
                minCol = c
            }
        }

        // reconstruct seam
        val seam = IntArray(rows)
        var cur = minCol
        for (r in rows - 1 downTo 0) {
            seam[r] = cur
            if (r > 0) cur = backtrack[r][cur]
        }
        return seam
    }

    /**
     * Remove vertical seam from src (3-channel RGB Mat) and return new Mat with cols-1.
     */
    private fun removeVerticalSeam(src: Mat, seam: IntArray): Mat {
        val rows = src.rows()
        val cols = src.cols()
        val dst = Mat(rows, cols - 1, src.type())

        // We'll copy row by row
        val rowBuf = ByteArray(cols * src.channels())
        val outBuf = ByteArray((cols - 1) * src.channels())

        val channels = src.channels()

        for (r in 0 until rows) {
            src.get(r, 0, rowBuf)
            val skipCol = seam[r]
            var dstIdx = 0
            var srcIdx = 0
            for (c in 0 until cols) {
                if (c == skipCol) {
                    srcIdx += channels
                    continue
                }
                // copy channels
                for (ch in 0 until channels) {
                    outBuf[dstIdx++] = rowBuf[srcIdx++]
                }
            }
            dst.put(r, 0, outBuf)
        }
        return dst
    }
}