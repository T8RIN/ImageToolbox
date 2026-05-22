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

package com.t8rin.opencv_tools.autocrop

import android.graphics.Bitmap
import com.t8rin.opencv_tools.autocrop.model.CropEdges
import com.t8rin.opencv_tools.autocrop.model.CropParameters
import com.t8rin.opencv_tools.autocrop.model.CropSensitivity
import com.t8rin.opencv_tools.autocrop.model.edgeCandidateThreshold
import com.t8rin.opencv_tools.utils.OpenCV
import com.t8rin.opencv_tools.utils.multiChannelMean
import com.t8rin.opencv_tools.utils.singleChannelMean
import com.t8rin.opencv_tools.utils.toMat
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.ensureActive
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc


object AutoCropper : OpenCV() {

    suspend fun crop(
        bitmap: Bitmap,
        @CropSensitivity sensitivity: Int
    ): Bitmap? = bitmap.findEdges(sensitivity)?.run {
        bitmap.cropByEdges(edges)
    }

    private fun Bitmap.cropByEdges(
        edges: CropEdges
    ): Bitmap = Bitmap.createBitmap(
        this,
        0,
        edges.top + 1,
        width,
        edges.height - 1
    )

    private suspend fun Bitmap.findEdges(
        @CropSensitivity sensitivity: Int
    ): CropParameters? = coroutineScope {
        ensureActive()
        val matRGBA = toMat()
        getEdgeCandidates(matRGBA, sensitivity)?.let {
            CropParameters(
                edges = getMaxScoreCropEdges(candidates = it, matRGBA = matRGBA),
                candidates = it
            )
        }
    }

    private suspend fun getEdgeCandidates(
        matRGBA: Mat,
        @CropSensitivity sensitivity: Int
    ): List<Int>? = coroutineScope {
        // Convert to gray scale
        val matGrayScale = Mat()
        Imgproc.cvtColor(matRGBA, matGrayScale, Imgproc.COLOR_RGBA2GRAY)
        Imgproc.medianBlur(matGrayScale, matGrayScale, 3)

        // Get canny edge detected matrix
        val matCanny = Mat()
        Imgproc.Canny(matGrayScale, matCanny, 100.0, 200.0)

        // Convert sensitivity to threshold
        val threshold = edgeCandidateThreshold(sensitivity)

        return@coroutineScope (0 until matCanny.rows()).filter { i ->
            ensureActive()
            matCanny.row(i).singleChannelMean() > threshold
        }.run {
            if (isEmpty()) null
            else listOf(0) + this + listOf(matCanny.rows())
        }
    }

    private suspend fun getMaxScoreCropEdges(
        candidates: List<Int>,
        matRGBA: Mat
    ): CropEdges = coroutineScope {
        val matSobel = Mat()
        Imgproc.Sobel(matRGBA, matSobel, CvType.CV_16U, 2, 2, 5)

        var maxScore = 0f
        var maxScoreEdges: CropEdges? = null

        candidates.windowed(2)
            .map { CropEdges(it) }
            .forEach { edges ->
                ensureActive()
                val cropAreaMean: Float =
                    matSobel.rowRange(edges.top, edges.bottom).multiChannelMean().toFloat()
                val heightPortion: Float = edges.height.toFloat() / matSobel.rows().toFloat()
                val score: Float = cropAreaMean * heightPortion

                if (score > maxScore) {
                    maxScore = score
                    maxScoreEdges = edges
                }
            }

        maxScoreEdges!!
    }

}