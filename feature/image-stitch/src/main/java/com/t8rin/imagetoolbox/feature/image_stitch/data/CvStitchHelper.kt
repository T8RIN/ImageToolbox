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

package com.t8rin.imagetoolbox.feature.image_stitch.data

import android.graphics.Bitmap
import androidx.core.graphics.createBitmap
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ImageScaler
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.feature.image_stitch.domain.CombiningParams
import com.t8rin.imagetoolbox.feature.image_stitch.domain.StitchMode
import com.t8rin.opencv_tools.utils.OpenCV
import com.t8rin.opencv_tools.utils.toBitmap
import com.t8rin.opencv_tools.utils.toMat
import com.t8rin.trickle.Trickle
import org.opencv.calib3d.Calib3d
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfDMatch
import org.opencv.core.MatOfKeyPoint
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.core.Rect
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.core.times
import org.opencv.features2d.FlannBasedMatcher
import org.opencv.features2d.SIFT
import org.opencv.imgproc.Imgproc
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.roundToInt

internal class CvStitchHelper @Inject constructor(
    private val imageGetter: ImageGetter<Bitmap>,
    private val imageScaler: ImageScaler<Bitmap>
) : OpenCV() {

    private val kernel = Mat(3, 3, CvType.CV_8SC1).apply {
        put(0, 0, 1.0, 1.0, 1.0)
        put(1, 0, 1.0, -8.0, 1.0)
        put(2, 0, 1.0, 1.0, 1.0)
    }

    fun stitchBitmaps(
        mat0: Mat,
        mat1: Mat,
        homo: Boolean = true,
        diff: Boolean = true
    ): Mat? {
        return if (homo) {
            stitchHomography(mat0, mat1, diff)
        } else {
            stitchPhaseCorrelate(mat0, mat1, diff)
        }
    }

    private fun stitchHomography(mat0: Mat, mat1: Mat, diff: Boolean): Mat? {
        val mat0Proc = if (diff) {
            val tmp = Mat()
            Imgproc.filter2D(mat0, tmp, CvType.CV_8U, kernel)
            tmp
        } else mat0
        val mat1Proc = if (diff) {
            val tmp = Mat()
            Imgproc.filter2D(mat1, tmp, CvType.CV_8U, kernel)
            tmp
        } else mat1

        val sift = SIFT.create()
        val kp0 = MatOfKeyPoint()
        val kp1 = MatOfKeyPoint()
        val desc0 = Mat()
        val desc1 = Mat()
        sift.detectAndCompute(mat0Proc, Mat(), kp0, desc0)
        sift.detectAndCompute(mat1Proc, Mat(), kp1, desc1)
        if (kp0.empty() || kp1.empty()) return null

        val matcher = FlannBasedMatcher.create()
        val knnMatches = mutableListOf<MatOfDMatch>()
        matcher.knnMatch(desc0, desc1, knnMatches, 2)

        val queryPoints = mutableListOf<Point>()
        val trainPoints = mutableListOf<Point>()
        val kp0a = kp0.toArray()
        val kp1a = kp1.toArray()
        for (m in knnMatches) {
            val matches = m.toArray()
            if (matches.size < 2) continue
            if (matches[0].distance > 0.7 * matches[1].distance) continue
            queryPoints.add(kp0a[matches[0].queryIdx].pt)
            trainPoints.add(kp1a[matches[0].trainIdx].pt)
        }
        if (queryPoints.size < 10) return null

        val homoMat = Calib3d.findHomography(
            MatOfPoint2f(*trainPoints.toTypedArray()),
            MatOfPoint2f(*queryPoints.toTypedArray()),
            Calib3d.RANSAC
        )

        val corners = arrayOf(
            Point(0.0, 0.0),
            Point(mat1.cols().toDouble(), 0.0),
            Point(mat1.cols().toDouble(), mat1.rows().toDouble()),
            Point(0.0, mat1.rows().toDouble())
        )
        val transformedCorners = MatOfPoint2f(*corners).let { src ->
            val dst = MatOfPoint2f()
            Core.perspectiveTransform(src, dst, homoMat)
            dst.toArray()
        }

        val allX = transformedCorners.map { it.x } + listOf(0.0, mat0.cols().toDouble())
        val allY = transformedCorners.map { it.y } + listOf(0.0, mat0.rows().toDouble())
        val minX = allX.minOrNull() ?: 0.0
        val minY = allY.minOrNull() ?: 0.0
        val maxX = allX.maxOrNull() ?: mat0.cols().toDouble()
        val maxY = allY.maxOrNull() ?: mat0.rows().toDouble()
        val width = (maxX - minX).toInt()
        val height = (maxY - minY).toInt()

        val offset = Mat.eye(3, 3, CvType.CV_64F)
        offset.put(0, 2, -minX)
        offset.put(1, 2, -minY)
        val adjustedHomo = offset * homoMat

        val canvas = Mat(Size(width.toDouble(), height.toDouble()), mat0.type())
        Imgproc.warpPerspective(mat1, canvas, adjustedHomo, canvas.size())
        val roi0 = Rect((-minX).toInt(), (-minY).toInt(), mat0.cols(), mat0.rows())
        mat0.copyTo(canvas.submat(roi0))
        return canvas
    }

    private fun stitchPhaseCorrelate(mat0: Mat, mat1: Mat, diff: Boolean): Mat? {
        if (mat0.size() != mat1.size()) {
            val targetSize = Size(
                minOf(mat0.cols(), mat1.cols()).toDouble(),
                minOf(mat0.rows(), mat1.rows()).toDouble()
            )
            val resized0 = Mat()
            val resized1 = Mat()
            Imgproc.resize(mat0, resized0, targetSize)
            Imgproc.resize(mat1, resized1, targetSize)
            return stitchPhaseCorrelate(resized0, resized1, diff)
        }

        val mat0Gray = Mat()
        val mat1Gray = Mat()
        if (diff) {
            val grad0 = Mat()
            val grad1 = Mat()
            Imgproc.filter2D(mat0, grad0, CvType.CV_8U, kernel)
            Imgproc.filter2D(mat1, grad1, CvType.CV_8U, kernel)
            val diffMat = Mat()
            Core.absdiff(grad0, grad1, diffMat)
            Core.bitwise_and(grad0, diffMat, grad0)
            Core.bitwise_and(grad1, diffMat, grad1)
            Imgproc.cvtColor(grad0, mat0Gray, Imgproc.COLOR_RGBA2GRAY)
            Imgproc.cvtColor(grad1, mat1Gray, Imgproc.COLOR_RGBA2GRAY)
        } else {
            Imgproc.cvtColor(mat0, mat0Gray, Imgproc.COLOR_RGBA2GRAY)
            Imgproc.cvtColor(mat1, mat1Gray, Imgproc.COLOR_RGBA2GRAY)
        }

        val matchResult = Mat()
        Imgproc.matchTemplate(mat0Gray, mat1Gray, matchResult, Imgproc.TM_CCORR_NORMED)
        val mmr = Core.minMaxLoc(matchResult)
        val dx = mmr.maxLoc.x.toInt()
        val dy = mmr.maxLoc.y.toInt()

        val width = max(mat0.cols(), mat1.cols() + dx)
        val height = max(mat0.rows(), mat1.rows() + dy)
        val canvas = Mat(Size(width.toDouble(), height.toDouble()), mat0.type())
        canvas.setTo(Scalar.all(0.0))
        mat0.copyTo(canvas.submat(Rect(0, 0, mat0.cols(), mat0.rows())))
        mat1.copyTo(canvas.submat(Rect(dx, dy, mat1.cols(), mat1.rows())))
        return canvas
    }

    suspend fun cvCombine(
        imageUris: List<String>,
        combiningParams: CombiningParams,
    ): Pair<Bitmap, ImageInfo> {
        val result = cvStitch(
            uris = imageUris,
            imageScale = combiningParams.outputScale,
            stitchMode = combiningParams.stitchMode as StitchMode.Auto
        ) ?: imageUris.first().toBitmap(
            imageScale = combiningParams.outputScale,
            stitchMode = combiningParams.stitchMode
        ) ?: createBitmap(1, 1)

        return Trickle.drawColorBehind(
            input = result,
            color = combiningParams.backgroundColor
        ) to ImageInfo(
            width = result.width,
            height = result.height,
            imageFormat = ImageFormat.Png.Lossless
        )
    }

    private suspend fun cvStitch(
        uris: List<String>,
        imageScale: Float,
        stitchMode: StitchMode.Auto
    ): Bitmap? {
        if (uris.size < 2) return null

        var current = uris.first().toBitmap(
            imageScale = imageScale,
            stitchMode = stitchMode
        )?.toMat() ?: return null

        for (i in 1 until uris.size) {
            val next = uris[i].toBitmap(
                imageScale = imageScale,
                stitchMode = stitchMode
            )?.toMat() ?: continue

            val stitched = stitchBitmaps(
                mat0 = current,
                mat1 = next
            )
            current.release()
            next.release()

            stitched ?: return null
            current = stitched
        }

        return current.toBitmap().also { current.release() }
    }

    private suspend fun String.toBitmap(
        imageScale: Float,
        stitchMode: StitchMode.Auto
    ): Bitmap? = imageGetter.getImage(
        data = this,
        originalSize = true
    )?.let {
        val scaled = it.createScaledBitmap(
            width = (it.width * imageScale).roundToInt(),
            height = (it.height * imageScale).roundToInt()
        )
        val newWidth = scaled.width - stitchMode.startDrop - stitchMode.endDrop
        val newHeight = scaled.height - stitchMode.topDrop - stitchMode.bottomDrop

        if (newWidth < 1 || newHeight < 1) {
            scaled
        } else {
            Bitmap.createBitmap(
                scaled,
                stitchMode.startDrop,
                stitchMode.topDrop,
                newWidth.coerceAtLeast(1),
                newHeight.coerceAtLeast(1)
            )
        }
    }

    private suspend fun Bitmap.createScaledBitmap(
        width: Int,
        height: Int
    ): Bitmap = imageScaler.scaleImage(
        image = this,
        width = width,
        height = height
    )
}