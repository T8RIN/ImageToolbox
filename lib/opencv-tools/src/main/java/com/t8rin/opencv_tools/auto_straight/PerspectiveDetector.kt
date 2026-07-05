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

@file:Suppress("SameParameterValue")

package com.t8rin.opencv_tools.auto_straight

import android.graphics.Bitmap
import com.t8rin.opencv_tools.document_detector.DocumentDetector
import com.t8rin.opencv_tools.utils.OpenCV
import com.t8rin.opencv_tools.utils.toMat
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.core.Size
import org.opencv.geometry.Geometry
import org.opencv.imgproc.Imgproc
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

internal object PerspectiveDetector : OpenCV() {

    private const val MAX_DETECTION_SIDE = 1200.0
    private const val MIN_AREA_FACTOR = 0.02
    private const val MAX_CORNER_COSINE = 0.82
    private val approximationFactors = doubleArrayOf(0.012, 0.02, 0.035, 0.05)

    private class Candidate(
        val points: Array<Point>,
        val score: Double
    )

    suspend fun findCorners(image: Bitmap): Array<Point>? = coroutineScope {
        ensureActive()
        val candidates = mutableListOf<Candidate>()

        DocumentDetector.findDocumentCorners(image)?.let { points ->
            val sortedPoints = sortCorners(points.toTypedArray())
            addCandidate(
                points = sortedPoints,
                contourArea = polygonArea(sortedPoints),
                imageWidth = image.width.toDouble(),
                imageHeight = image.height.toDouble(),
                scale = 1.0,
                sourceBonus = 0.3,
                candidates = candidates
            )
        }
        ensureActive()

        val source = image.toMat()
        val resized = Mat()
        val gray = Mat()
        val blurred = Mat()
        val kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(5.0, 5.0))
        val masks = mutableListOf<Pair<Mat, Double>>()

        try {
            val maxSide = max(image.width, image.height).toDouble()
            val scale = max(1.0, maxSide / MAX_DETECTION_SIDE)
            val width = image.width / scale
            val height = image.height / scale
            Imgproc.resize(source, resized, Size(width, height))

            when (resized.channels()) {
                4 -> Imgproc.cvtColor(resized, gray, Imgproc.COLOR_RGBA2GRAY)
                3 -> Imgproc.cvtColor(resized, gray, Imgproc.COLOR_RGB2GRAY)
                else -> resized.copyTo(gray)
            }
            Imgproc.GaussianBlur(gray, blurred, Size(5.0, 5.0), 0.0)

            val otsuThreshold = addOtsuMask(
                blurred,
                kernel,
                Imgproc.THRESH_BINARY,
                0.1,
                masks
            )
            addOtsuMask(blurred, kernel, Imgproc.THRESH_BINARY_INV, 0.1, masks)
            val dynamicLowerThreshold = (otsuThreshold * 0.45).coerceIn(20.0, 110.0)
            val dynamicUpperThreshold = (otsuThreshold * 1.35)
                .coerceIn(dynamicLowerThreshold + 30.0, 255.0)
            addEdgeMask(
                blurred,
                kernel,
                dynamicLowerThreshold,
                dynamicUpperThreshold,
                0.25,
                masks
            )
            addEdgeMask(blurred, kernel, 30.0, 100.0, 0.2, masks)

            val adaptiveBlockSize = (min(width, height) / 12)
                .toInt()
                .coerceIn(15, 101)
                .let { if (it % 2 == 0) it + 1 else it }
            addAdaptiveMask(
                image = blurred,
                kernel = kernel,
                thresholdType = Imgproc.THRESH_BINARY,
                blockSize = adaptiveBlockSize,
                sourceBonus = 0.15,
                masks = masks
            )
            addAdaptiveMask(
                image = blurred,
                kernel = kernel,
                thresholdType = Imgproc.THRESH_BINARY_INV,
                blockSize = adaptiveBlockSize,
                sourceBonus = 0.15,
                masks = masks
            )

            masks.forEach { (mask, sourceBonus) ->
                ensureActive()
                collectCandidates(
                    mask = mask,
                    imageWidth = width,
                    imageHeight = height,
                    scale = scale,
                    sourceBonus = sourceBonus,
                    candidates = candidates
                )
            }
        } finally {
            source.release()
            resized.release()
            gray.release()
            blurred.release()
            kernel.release()
            masks.forEach { (mask) -> mask.release() }
        }

        candidates.maxByOrNull(Candidate::score)?.points
    }

    private fun addEdgeMask(
        image: Mat,
        kernel: Mat,
        lowerThreshold: Double,
        upperThreshold: Double,
        sourceBonus: Double,
        masks: MutableList<Pair<Mat, Double>>
    ) {
        val mask = Mat()
        masks += mask to sourceBonus
        Imgproc.Canny(image, mask, lowerThreshold, upperThreshold)
        Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_CLOSE, kernel)
        Imgproc.dilate(mask, mask, kernel)
    }

    private fun addOtsuMask(
        image: Mat,
        kernel: Mat,
        thresholdType: Int,
        sourceBonus: Double,
        masks: MutableList<Pair<Mat, Double>>
    ): Double {
        val mask = Mat()
        masks += mask to sourceBonus
        val threshold = Imgproc.threshold(
            image,
            mask,
            0.0,
            255.0,
            thresholdType or Imgproc.THRESH_OTSU
        )
        Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_CLOSE, kernel)
        return threshold
    }

    private fun addAdaptiveMask(
        image: Mat,
        kernel: Mat,
        thresholdType: Int,
        blockSize: Int,
        sourceBonus: Double,
        masks: MutableList<Pair<Mat, Double>>
    ) {
        val mask = Mat()
        masks += mask to sourceBonus
        Imgproc.adaptiveThreshold(
            image,
            mask,
            255.0,
            Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
            thresholdType,
            blockSize,
            7.0
        )
        Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_CLOSE, kernel)
    }

    private suspend fun collectCandidates(
        mask: Mat,
        imageWidth: Double,
        imageHeight: Double,
        scale: Double,
        sourceBonus: Double,
        candidates: MutableList<Candidate>
    ) {
        val contours = mutableListOf<MatOfPoint>()
        val hierarchy = Mat()
        try {
            Imgproc.findContours(
                mask,
                contours,
                hierarchy,
                Imgproc.RETR_LIST,
                Imgproc.CHAIN_APPROX_SIMPLE
            )

            val imageArea = imageWidth * imageHeight
            for (contour in contours) {
                currentCoroutineContext().ensureActive()
                val contourArea = abs(Geometry.contourArea(contour))
                if (contourArea < imageArea * MIN_AREA_FACTOR) continue

                val contour2f = MatOfPoint2f(*contour.toArray())
                try {
                    val perimeter = Geometry.arcLength(contour2f, true)
                    if (perimeter <= 0.0) continue

                    for (approximationFactor in approximationFactors) {
                        val approximation = MatOfPoint2f()
                        try {
                            Geometry.approxPolyDP(
                                contour2f,
                                approximation,
                                perimeter * approximationFactor,
                                true
                            )
                            if (approximation.total() != 4L) continue

                            val quad = MatOfPoint(*approximation.toArray())
                            try {
                                if (!Geometry.isContourConvex(quad)) continue

                                addCandidate(
                                    points = approximation.toArray(),
                                    contourArea = contourArea,
                                    imageWidth = imageWidth,
                                    imageHeight = imageHeight,
                                    scale = scale,
                                    sourceBonus = sourceBonus,
                                    candidates = candidates
                                )
                            } finally {
                                quad.release()
                            }
                        } finally {
                            approximation.release()
                        }
                    }
                } finally {
                    contour2f.release()
                }
            }
        } finally {
            contours.forEach(Mat::release)
            hierarchy.release()
        }
    }

    private fun addCandidate(
        points: Array<Point>,
        contourArea: Double,
        imageWidth: Double,
        imageHeight: Double,
        scale: Double,
        sourceBonus: Double,
        candidates: MutableList<Candidate>
    ) {
        val sorted = sortCorners(points)
        val quadArea = polygonArea(sorted)
        val imageArea = imageWidth * imageHeight
        val areaFactor = quadArea / imageArea
        if (areaFactor !in MIN_AREA_FACTOR..1.02) return

        val maxCosine = sorted.maxCornerCosine()
        if (maxCosine > MAX_CORNER_COSINE) return

        val sides = sorted.indices.map { index ->
            distance(sorted[index], sorted[(index + 1) % sorted.size])
        }
        if (sides.min() < min(imageWidth, imageHeight) * 0.04) return

        val contourFill = (contourArea / quadArea).coerceIn(0.0, 1.0)
        if (contourFill < 0.45) return

        val borderMargin = min(imageWidth, imageHeight) * 0.012
        val borderPoints = sorted.count { point ->
            point.x <= borderMargin ||
                    point.y <= borderMargin ||
                    point.x >= imageWidth - borderMargin ||
                    point.y >= imageHeight - borderMargin
        }
        if (borderPoints == 4) return

        val borderPenalty = when (borderPoints) {
            3 -> 0.7
            else -> 0.0
        }
        val score = areaFactor * 5.0 +
                (1.0 - maxCosine) * 2.0 +
                contourFill +
                sourceBonus -
                borderPenalty

        candidates += Candidate(
            points = sorted.map { point ->
                Point(point.x * scale, point.y * scale)
            }.toTypedArray(),
            score = score
        )
    }

    private fun sortCorners(points: Array<Point>): Array<Point> {
        val centerX = points.map(Point::x).average()
        val centerY = points.map(Point::y).average()
        val ordered = points.sortedBy { point ->
            atan2(point.y - centerY, point.x - centerX)
        }
        val topLeftIndex = ordered.indices.minBy { index ->
            ordered[index].x + ordered[index].y
        }

        return Array(ordered.size) { offset ->
            ordered[(topLeftIndex + offset) % ordered.size]
        }
    }

    private fun Array<Point>.maxCornerCosine(): Double = indices.maxOf { index ->
        val previous = this[(index + size - 1) % size]
        val current = this[index]
        val next = this[(index + 1) % size]
        val firstX = previous.x - current.x
        val firstY = previous.y - current.y
        val secondX = next.x - current.x
        val secondY = next.y - current.y
        val denominator = sqrt(
            (firstX * firstX + firstY * firstY) *
                    (secondX * secondX + secondY * secondY)
        )

        if (denominator == 0.0) 1.0
        else abs((firstX * secondX + firstY * secondY) / denominator)
    }

    private fun polygonArea(points: Array<Point>): Double = abs(
        points.indices.sumOf { index ->
            val current = points[index]
            val next = points[(index + 1) % points.size]
            current.x * next.y - next.x * current.y
        } / 2.0
    )

    private fun distance(first: Point, second: Point): Double {
        val dx = first.x - second.x
        val dy = first.y - second.y
        return sqrt(dx * dx + dy * dy)
    }
}