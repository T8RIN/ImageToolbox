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

package com.t8rin.opencv_tools.document_detector

import android.graphics.Bitmap
import com.t8rin.opencv_tools.utils.OpenCV
import com.t8rin.opencv_tools.utils.toMat
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.core.Size
import org.opencv.geometry.Geometry
import org.opencv.imgproc.Imgproc
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

/**
 * This class uses OpenCV to find document corners.
 *
 * @constructor creates document detector
 */
object DocumentDetector : OpenCV() {

    private const val RESIZE_THRESHOLD = 500.0
    private const val MIN_AREA_FACTOR = 0.04
    private const val MAX_AREA_FACTOR = 0.92
    private const val EXPECTED_MAX_COSINE = 0.4
    private const val EXPECTED_OPTIMAL_MAX_COSINE = 0.3
    private const val EXPECTED_AREA_FACTOR = 0.2
    private const val APPROX_EPSILON_FACTOR = 0.02
    private const val THRESHOLD_VALUE = 160.0
    private const val THRESHOLD_MAX_VALUE = 255.0

    private data class DocumentCandidate(
        val points: List<Point>,
        val area: Double,
        val maxCosine: Double,
        val weight: Double
    ) {
        val score: Double = area + weight * (1 - maxCosine)
    }

    /**
     * take a photo with a document, and find the document's corners
     *
     * @param image a photo with a document
     * @return a list with document corners (top left, top right, bottom right, bottom left)
     */
    fun findDocumentCorners(image: Bitmap): List<Point>? {

        // convert bitmap to OpenCV matrix
        val source = image.toMat()

        // shrink photo to make it easier to find document corners
        val maxSide = max(image.width, image.height).toDouble()
        val resizeScale = if (maxSide > RESIZE_THRESHOLD) {
            maxSide / RESIZE_THRESHOLD
        } else {
            1.0
        }
        val scaledWidth = image.width / resizeScale
        val scaledHeight = image.height / resizeScale
        val resized = Mat()
        Imgproc.resize(source, resized, Size(scaledWidth, scaledHeight))

        val rgbImage = Mat()
        when (resized.channels()) {
            4 -> Imgproc.cvtColor(resized, rgbImage, Imgproc.COLOR_RGBA2RGB)
            1 -> Imgproc.cvtColor(resized, rgbImage, Imgproc.COLOR_GRAY2RGB)
            else -> resized.copyTo(rgbImage)
        }

        val blurredImage = Mat()
        Imgproc.medianBlur(rgbImage, blurredImage, 9)

        val candidates = mutableListOf<DocumentCandidate>()
        val imageSplitByColorChannel = mutableListOf<Mat>()
        Core.split(blurredImage, imageSplitByColorChannel)

        val luvImage = Mat()
        val imageSplitByLuvChannel = mutableListOf<Mat>()
        Imgproc.cvtColor(blurredImage, luvImage, Imgproc.COLOR_RGB2Luv)
        Core.split(luvImage, imageSplitByLuvChannel)

        var weight = 3_000_000.0
        (imageSplitByColorChannel + imageSplitByLuvChannel).forEach { channel ->
            findCandidates(
                image = channel,
                imageWidth = scaledWidth,
                imageHeight = scaledHeight,
                candidates = candidates,
                weight = weight
            )
            weight -= 1.0
        }

        val documentCorners: List<Point>? = candidates
            .maxByOrNull(DocumentCandidate::score)
            ?.points
            ?.map { point ->
                Point(
                    point.x * resizeScale,
                    point.y * resizeScale
                )
            }

        // sort points to force this order (top left, top right, bottom left, bottom right)
        return documentCorners?.sortForCropper()
    }

    private fun findCandidates(
        image: Mat,
        imageWidth: Double,
        imageHeight: Double,
        candidates: MutableList<DocumentCandidate>,
        weight: Double
    ) {
        val morphologyStruct = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(4.0, 4.0))
        val dilateStruct = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(3.0, 3.0))
        val thresholdImage = Mat()

        Imgproc.threshold(
            image,
            thresholdImage,
            THRESHOLD_VALUE,
            THRESHOLD_MAX_VALUE,
            Imgproc.THRESH_BINARY
        )
        Imgproc.morphologyEx(
            thresholdImage,
            thresholdImage,
            Imgproc.MORPH_CLOSE,
            morphologyStruct
        )
        Imgproc.dilate(thresholdImage, thresholdImage, dilateStruct)
        collectCandidates(thresholdImage, imageWidth, imageHeight, candidates, weight)

        val otsuImage = Mat()
        Imgproc.threshold(
            image,
            otsuImage,
            0.0,
            THRESHOLD_MAX_VALUE,
            Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU
        )
        Imgproc.morphologyEx(
            otsuImage,
            otsuImage,
            Imgproc.MORPH_CLOSE,
            morphologyStruct
        )
        Imgproc.dilate(otsuImage, otsuImage, dilateStruct)
        collectCandidates(otsuImage, imageWidth, imageHeight, candidates, weight - 0.25)

        var threshold = 60
        while (threshold >= 10) {
            val edgeImage = Mat()
            Imgproc.Canny(
                image,
                edgeImage,
                threshold * 2.0,
                threshold * 4.0
            )
            Imgproc.dilate(edgeImage, edgeImage, dilateStruct)
            collectCandidates(
                image = edgeImage,
                imageWidth = imageWidth,
                imageHeight = imageHeight,
                candidates = candidates,
                weight = weight - (60 - threshold + 1)
            )

            val bestCandidate = candidates.maxByOrNull(DocumentCandidate::score)
            if (
                bestCandidate != null &&
                bestCandidate.maxCosine < EXPECTED_OPTIMAL_MAX_COSINE &&
                bestCandidate.area > imageWidth * imageHeight * EXPECTED_AREA_FACTOR
            ) {
                break
            }

            threshold -= 10
        }
    }

    private fun collectCandidates(
        image: Mat,
        imageWidth: Double,
        imageHeight: Double,
        candidates: MutableList<DocumentCandidate>,
        weight: Double
    ) {
        val contours: MutableList<MatOfPoint> = mutableListOf()
        Imgproc.findContours(
            image,
            contours,
            Mat(),
            Imgproc.RETR_TREE,
            Imgproc.CHAIN_APPROX_SIMPLE
        )

        val minArea = imageWidth * imageHeight * MIN_AREA_FACTOR
        val maxArea = imageWidth * imageHeight * MAX_AREA_FACTOR

        contours.forEach { contour ->
            val contourArea = Geometry.contourArea(contour)
            if (contourArea < minArea || contourArea >= maxArea) return@forEach

            val approxContour = MatOfPoint2f()
            val contour2f = MatOfPoint2f(*contour.toArray())
            val arcLength = Geometry.arcLength(contour2f, true)
            if (arcLength < 100) return@forEach

            Geometry.approxPolyDP(
                contour2f,
                approxContour,
                APPROX_EPSILON_FACTOR * arcLength,
                true
            )

            if (approxContour.total() != 4L) return@forEach

            val approx = MatOfPoint(*approxContour.toArray())
            if (!Geometry.isContourConvex(approx)) return@forEach

            val points = approx.toList()
            val maxCosine = points.maxCornerCosine()
            if (maxCosine >= EXPECTED_MAX_COSINE) return@forEach

            candidates += DocumentCandidate(
                points = points,
                area = contourArea,
                maxCosine = maxCosine,
                weight = weight
            )
        }
    }

    private fun List<Point>.maxCornerCosine(): Double {
        var maxCosine = 0.0
        for (i in 2 until 6) {
            val cosine = abs(
                angleCosine(
                    point1 = this[i % 4],
                    point2 = this[i - 2],
                    origin = this[(i - 1) % 4]
                )
            )
            maxCosine = max(maxCosine, cosine)
        }
        return maxCosine
    }

    private fun angleCosine(
        point1: Point,
        point2: Point,
        origin: Point
    ): Double {
        val dx1 = point1.x - origin.x
        val dy1 = point1.y - origin.y
        val dx2 = point2.x - origin.x
        val dy2 = point2.y - origin.y
        return (dx1 * dx2 + dy1 * dy2) /
                sqrt((dx1 * dx1 + dy1 * dy1) * (dx2 * dx2 + dy2 * dy2) + 1e-10)
    }

    private fun List<Point>.sortForCropper(): List<Point> {
        val sortedByY = sortedBy(Point::y)
        val top = sortedByY.take(2).sortedBy(Point::x)
        val bottom = sortedByY.takeLast(2).sortedBy(Point::x)
        return top + bottom
    }
}