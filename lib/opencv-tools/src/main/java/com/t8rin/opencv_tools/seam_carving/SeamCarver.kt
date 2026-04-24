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
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

object SeamCarver : OpenCV() {

    fun distort(
        bitmap: Bitmap,
        distortionPercent: Float,
        maxInputSide: Int = 512
    ): Bitmap {
        val amount = distortionPercent.coerceIn(0f, 95f)
        if (amount <= 0f) return bitmap

        val originalWidth = bitmap.width
        val originalHeight = bitmap.height
        val targetScale = 1f - amount / 100f

        val originalMat = bitmap.toMat()
        var mat = originalMat.resizeToFit(maxInputSide)
        if (mat !== originalMat) {
            originalMat.release()
        }

        val targetWidth = (mat.cols() * targetScale).roundToInt().coerceAtLeast(1)
        val targetHeight = (mat.rows() * targetScale).roundToInt().coerceAtLeast(1)

        mat = carveMat(
            mat = mat,
            desiredWidth = targetWidth,
            desiredHeight = targetHeight
        )

        val restored = Mat()
        Imgproc.resize(
            mat,
            restored,
            Size(originalWidth.toDouble(), originalHeight.toDouble()),
            0.0,
            0.0,
            Imgproc.INTER_CUBIC
        )
        mat.release()

        return restored.toBitmap()
    }

    /**
     * Main entry:
     * input: bitmap, desiredWidth, desiredHeight
     * returns: processed Bitmap (content-aware reduced). If desired dimensions are larger than source,
     * result will be upscaled with normal resizing after seam carving.
     */
    fun carve(bitmap: Bitmap, desiredWidth: Int, desiredHeight: Int): Bitmap = carveMat(
        mat = bitmap.toMat(),
        desiredWidth = desiredWidth,
        desiredHeight = desiredHeight
    ).toBitmap()

    private fun carveMat(mat: Mat, desiredWidth: Int, desiredHeight: Int): Mat {
        var current = mat
        val targetW = desiredWidth.coerceAtLeast(1)
        val targetH = desiredHeight.coerceAtLeast(1)

        // If target dims are bigger than current, we will only seam-carve to min(current, target) and then resize up.
        val targetWClamp = min(targetW, current.cols())
        val targetHClamp = min(targetH, current.rows())

        // reduce width
        while (current.cols() > targetWClamp) {
            val energy = computeEnergy(current)
            val seam = findVerticalSeam(energy)
            energy.release()

            val previous = current
            current = removeVerticalSeam(current, seam)
            previous.release()
        }

        // reduce height via transpose trick
        var transposed = transposeMat(current)
        current.release()
        current = transposed

        while (current.cols() > targetHClamp) {
            val energy = computeEnergy(current)
            val seam = findVerticalSeam(energy)
            energy.release()

            val previous = current
            current = removeVerticalSeam(current, seam)
            previous.release()
        }
        transposed = transposeMat(current)
        current.release()
        current = transposed

        // If the user requested larger dimensions than we could seam-carve to, upscale with interpolation.
        return if (current.cols() != targetW || current.rows() != targetH) {
            val dst = Mat()
            Imgproc.resize(
                current,
                dst,
                Size(targetW.toDouble(), targetH.toDouble()),
                0.0,
                0.0,
                Imgproc.INTER_CUBIC
            )
            current.release()
            dst
        } else {
            current
        }
    }

    private fun Mat.resizeToFit(maxSide: Int): Mat {
        val currentMaxSide = max(cols(), rows())
        if (maxSide !in 1..<currentMaxSide) return this

        val scale = maxSide.toDouble() / currentMaxSide
        val resized = Mat()
        Imgproc.resize(
            this,
            resized,
            Size(
                (cols() * scale).roundToInt().coerceAtLeast(1).toDouble(),
                (rows() * scale).roundToInt().coerceAtLeast(1).toDouble()
            ),
            0.0,
            0.0,
            Imgproc.INTER_AREA
        )
        return resized
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

        gray.release()
        gradX.release()
        gradY.release()
        gradXSq.release()
        gradYSq.release()

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

        var previous = DoubleArray(cols)
        var current = DoubleArray(cols)
        val rowEnergy = DoubleArray(cols)
        val backtrack = Array(rows) { IntArray(cols) }

        energy.get(0, 0, previous)

        for (r in 1 until rows) {
            energy.get(r, 0, rowEnergy)
            for (c in 0 until cols) {
                // consider three pixels from previous row
                var minPrev = previous[c]
                var idx = c
                if (c > 0 && previous[c - 1] < minPrev) {
                    minPrev = previous[c - 1]; idx = c - 1
                }
                if (c < cols - 1 && previous[c + 1] < minPrev) {
                    minPrev = previous[c + 1]; idx = c + 1
                }
                current[c] = rowEnergy[c] + minPrev
                backtrack[r][c] = idx
            }
            val swap = previous
            previous = current
            current = swap
        }

        // find min in last row
        var minCol = 0
        var minVal = previous[0]
        for (c in 1 until cols) {
            if (previous[c] < minVal) {
                minVal = previous[c]
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