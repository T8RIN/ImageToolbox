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
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfDMatch
import org.opencv.core.MatOfDouble
import org.opencv.core.MatOfKeyPoint
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.core.Rect
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.core.times
import org.opencv.features.FlannBasedMatcher
import org.opencv.features.SIFT
import org.opencv.geometry.Geometry
import org.opencv.imgproc.Imgproc
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.floor
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
        diff: Boolean = true,
        blendRadius: Int = 0
    ): Mat? {
        return if (homo) {
            stitchHomography(mat0, mat1, diff, blendRadius)
        } else {
            stitchPhaseCorrelate(mat0, mat1, diff)
        }
    }

    private fun stitchHomography(
        mat0: Mat,
        mat1: Mat,
        diff: Boolean,
        blendRadius: Int
    ): Mat? {
        val homoMat = findHomography(
            source = mat1,
            target = mat0,
            diff = diff
        ) ?: return null

        val corners = arrayOf(
            Point(0.0, 0.0),
            Point(mat1.cols().toDouble(), 0.0),
            Point(mat1.cols().toDouble(), mat1.rows().toDouble()),
            Point(0.0, mat1.rows().toDouble())
        )
        val transformedCorners = MatOfPoint2f(*corners).let { src ->
            val dst = MatOfPoint2f()
            Core.perspectiveTransform(src, dst, homoMat)
            dst.toArray().also {
                src.release()
                dst.release()
            }
        }

        val allX = transformedCorners.map { it.x } + listOf(0.0, mat0.cols().toDouble())
        val allY = transformedCorners.map { it.y } + listOf(0.0, mat0.rows().toDouble())
        val minX = floor(allX.minOrNull() ?: 0.0)
        val minY = floor(allY.minOrNull() ?: 0.0)
        val maxX = ceil(allX.maxOrNull() ?: mat0.cols().toDouble())
        val maxY = ceil(allY.maxOrNull() ?: mat0.rows().toDouble())
        val width = (maxX - minX).toInt()
        val height = (maxY - minY).toInt()

        val offset = Mat.eye(3, 3, CvType.CV_64F)
        offset.put(0, 2, -minX)
        offset.put(1, 2, -minY)
        val adjustedHomo = offset * homoMat

        val canvas = Mat(
            Size(width.toDouble(), height.toDouble()),
            mat0.type(),
            Scalar.all(0.0)
        )
        val warpedMat1 = Mat(canvas.size(), mat1.type(), Scalar.all(0.0))
        Imgproc.warpPerspective(mat1, warpedMat1, adjustedHomo, canvas.size())

        val occupiedMask = Mat(canvas.size(), CvType.CV_8UC1, Scalar.all(0.0))
        val mat1Mask = createContentMask(mat1)
        val warpedMat1Mask = Mat(canvas.size(), CvType.CV_8UC1, Scalar.all(0.0))
        Imgproc.warpPerspective(
            mat1Mask,
            warpedMat1Mask,
            adjustedHomo,
            canvas.size(),
            Imgproc.INTER_NEAREST
        )
        copyWithoutBorderSeam(
            source = warpedMat1,
            sourceMask = warpedMat1Mask,
            destination = canvas,
            occupiedMask = occupiedMask,
            blendRadius = blendRadius
        )
        val roi0 = Rect((-minX).toInt(), (-minY).toInt(), mat0.cols(), mat0.rows())
        val canvasRoi = canvas.submat(roi0)
        val occupiedRoi = occupiedMask.submat(roi0)
        val mat0Mask = createContentMask(mat0)
        copyWithoutBorderSeam(
            source = mat0,
            sourceMask = mat0Mask,
            destination = canvasRoi,
            occupiedMask = occupiedRoi,
            blendRadius = blendRadius
        )
        canvasRoi.release()
        occupiedRoi.release()
        mat0Mask.release()
        mat1Mask.release()
        warpedMat1.release()
        warpedMat1Mask.release()
        occupiedMask.release()
        adjustedHomo.release()
        offset.release()
        homoMat.release()
        return canvas
    }

    private fun findHomography(
        source: Mat,
        target: Mat,
        diff: Boolean
    ): Mat? {
        val sourceProc = if (diff) {
            val tmp = Mat()
            Imgproc.filter2D(source, tmp, CvType.CV_8U, kernel)
            tmp
        } else source
        val targetProc = if (diff) {
            val tmp = Mat()
            Imgproc.filter2D(target, tmp, CvType.CV_8U, kernel)
            tmp
        } else target

        val sift = SIFT.create()
        val sourceKeyPoints = MatOfKeyPoint()
        val targetKeyPoints = MatOfKeyPoint()
        val sourceDescriptors = Mat()
        val targetDescriptors = Mat()
        val matcher = FlannBasedMatcher.create()
        val knnMatches = mutableListOf<MatOfDMatch>()
        val detectionMask = Mat()
        try {
            sift.detectAndCompute(sourceProc, detectionMask, sourceKeyPoints, sourceDescriptors)
            sift.detectAndCompute(targetProc, detectionMask, targetKeyPoints, targetDescriptors)
            if (sourceKeyPoints.empty() || targetKeyPoints.empty()) return null

            matcher.knnMatch(targetDescriptors, sourceDescriptors, knnMatches, 2)

            val sourcePoints = mutableListOf<Point>()
            val targetPoints = mutableListOf<Point>()
            val sourceKeyPointsArray = sourceKeyPoints.toArray()
            val targetKeyPointsArray = targetKeyPoints.toArray()
            for (m in knnMatches) {
                val matches = m.toArray()
                if (matches.size < 2) continue
                if (matches[0].distance > 0.7 * matches[1].distance) continue
                sourcePoints.add(sourceKeyPointsArray[matches[0].trainIdx].pt)
                targetPoints.add(targetKeyPointsArray[matches[0].queryIdx].pt)
            }
            if (sourcePoints.size < 10) return null

            val sourcePointsMat = MatOfPoint2f(*sourcePoints.toTypedArray())
            val targetPointsMat = MatOfPoint2f(*targetPoints.toTypedArray())
            val homography = Geometry.findHomography(
                sourcePointsMat,
                targetPointsMat,
                Geometry.RANSAC
            )
            sourcePointsMat.release()
            targetPointsMat.release()
            return if (homography.empty()) {
                homography.release()
                null
            } else homography
        } finally {
            if (diff) {
                sourceProc.release()
                targetProc.release()
            }
            sourceKeyPoints.release()
            targetKeyPoints.release()
            sourceDescriptors.release()
            targetDescriptors.release()
            detectionMask.release()
            knnMatches.forEach(MatOfDMatch::release)
        }
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
        val result = when (combiningParams.stitchMode) {
            is StitchMode.Screenshot -> cvScreenshotStitch(
                uris = imageUris,
                imageScale = combiningParams.outputScale,
                stitchMode = combiningParams.stitchMode
            ) ?: cvStitch(
                uris = imageUris,
                imageScale = combiningParams.outputScale,
                stitchMode = combiningParams.stitchMode
            )

            is StitchMode.Panorama -> cvPanorama(
                uris = imageUris,
                imageScale = combiningParams.outputScale,
                stitchMode = combiningParams.stitchMode
            )

            else -> cvStitch(
                uris = imageUris,
                imageScale = combiningParams.outputScale,
                stitchMode = combiningParams.stitchMode
            )
        } ?: imageUris.first().toBitmap(
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

    private suspend fun cvScreenshotStitch(
        uris: List<String>,
        imageScale: Float,
        stitchMode: StitchMode.Screenshot
    ): Bitmap? {
        if (uris.size < 2) return null

        val images = uris.mapNotNull {
            it.toBitmap(
                imageScale = imageScale,
                stitchMode = stitchMode
            )?.toMat()
        }
        if (
            images.size != uris.size ||
            images.any { image ->
                image.cols() != images.first().cols() ||
                        image.rows() != images.first().rows()
            }
        ) {
            images.forEach(Mat::release)
            return null
        }

        val overlaps = images.zipWithNext { previous, next ->
            findScreenshotOverlap(previous, next)
        }
        if (overlaps.any { it == null }) {
            images.forEach(Mat::release)
            return null
        }

        val segments = mutableListOf<Mat>()
        images.forEachIndexed { index, image ->
            val start = if (index == 0) {
                0
            } else {
                overlaps[index - 1]!!.nextOverlapEnd
            }
            val end = if (index == images.lastIndex) {
                image.rows()
            } else {
                overlaps[index]!!.previousContentEnd
            }

            if (end > start) {
                segments += image.submat(Rect(0, start, image.cols(), end - start))
            }
        }
        if (segments.size < 2) {
            segments.forEach(Mat::release)
            images.forEach(Mat::release)
            return null
        }

        val result = Mat()
        Core.vconcat(segments, result)
        segments.forEach(Mat::release)
        images.forEach(Mat::release)

        val output = if (stitchMode.cropToContent) {
            cropToOpaqueArea(result)
        } else result
        return output.toBitmap().also {
            if (output !== result) output.release()
            result.release()
        }
    }

    private fun findScreenshotOverlap(
        previous: Mat,
        next: Mat
    ): ScreenshotOverlap? {
        val previousPrepared = prepareScreenshotForMatching(previous)
        val nextPrepared = prepareScreenshotForMatching(next)
        try {
            if (previousPrepared.size() != nextPrepared.size()) return null

            val fixedTop = detectFixedEdge(
                previous = previousPrepared,
                next = nextPrepared,
                fromTop = true,
                limit = (previousPrepared.rows() * MAX_FIXED_TOP_RATIO).roundToInt()
            )
            val fixedBottom = detectFixedEdge(
                previous = previousPrepared,
                next = nextPrepared,
                fromTop = false,
                limit = (previousPrepared.rows() * MAX_FIXED_BOTTOM_RATIO).roundToInt()
            )
            val contentEnd = previousPrepared.rows() - fixedBottom
            val contentHeight = contentEnd - fixedTop
            if (contentHeight < MIN_MATCH_CONTENT_HEIGHT) return null

            val minOffset = max(
                MIN_SCROLL_OFFSET,
                (contentHeight * MIN_SCROLL_OFFSET_RATIO).roundToInt()
            )
            val minOverlap = max(
                MIN_SCREENSHOT_OVERLAP,
                (contentHeight * MIN_SCREENSHOT_OVERLAP_RATIO).roundToInt()
            )
            val candidates = PROBE_POSITIONS.mapNotNull { position ->
                findScreenshotOffsetCandidate(
                    previous = previousPrepared,
                    next = nextPrepared,
                    contentStart = fixedTop,
                    contentEnd = contentEnd,
                    minOffset = minOffset,
                    minOverlap = minOverlap,
                    probePosition = position
                )
            }
            if (candidates.isEmpty()) return null

            val best = candidates.maxWithOrNull(
                compareBy<OffsetCandidate> { candidate ->
                    candidates.count { other ->
                        kotlin.math.abs(candidate.offset - other.offset) <= OFFSET_CLUSTER_RADIUS
                    }
                }.thenBy { it.score }
            )
                ?: return null
            if (best.score < MIN_TEMPLATE_SCORE) return null

            val overlapScore = scoreScreenshotOffset(
                previous = previousPrepared,
                next = nextPrepared,
                contentStart = fixedTop,
                contentEnd = contentEnd,
                offset = best.offset
            )
            if (overlapScore < MIN_OVERLAP_SCORE) return null

            val scaleY = previous.rows().toDouble() / previousPrepared.rows()
            val originalOffset = (best.offset * scaleY).roundToInt()
            val assemblyFixedBottom = max(
                fixedBottom,
                (previousPrepared.rows() * SCREENSHOT_BOTTOM_SAFE_INSET_RATIO).roundToInt()
            )
            val originalFixedBottom = (assemblyFixedBottom * scaleY).roundToInt()
            val previousContentEnd = previous.rows() - originalFixedBottom
            val nextOverlapEnd = (previousContentEnd - originalOffset)
                .coerceIn(1, next.rows() - 1)

            return ScreenshotOverlap(
                previousContentEnd = previousContentEnd,
                nextOverlapEnd = nextOverlapEnd
            )
        } finally {
            previousPrepared.release()
            nextPrepared.release()
        }
    }

    private fun prepareScreenshotForMatching(source: Mat): Mat {
        val horizontalInset = (source.cols() * MATCH_HORIZONTAL_INSET_RATIO).roundToInt()
        val sourceRoi = source.submat(
            Rect(
                horizontalInset,
                0,
                source.cols() - horizontalInset * 2,
                source.rows()
            )
        )
        val gray = Mat()
        Imgproc.cvtColor(sourceRoi, gray, Imgproc.COLOR_RGBA2GRAY)
        sourceRoi.release()

        val resized = Mat()
        val targetHeight = (gray.rows() * MATCH_WIDTH.toDouble() / gray.cols())
            .roundToInt()
            .coerceAtLeast(1)
        Imgproc.resize(
            gray,
            resized,
            Size(MATCH_WIDTH.toDouble(), targetHeight.toDouble()),
            0.0,
            0.0,
            Imgproc.INTER_AREA
        )
        gray.release()

        return Mat().also { edges ->
            Imgproc.Sobel(resized, edges, CvType.CV_8U, 0, 1, 3)
            resized.release()
        }
    }

    private fun detectFixedEdge(
        previous: Mat,
        next: Mat,
        fromTop: Boolean,
        limit: Int
    ): Int {
        var lastMatchingRow = -1
        var misses = 0
        for (distance in 0 until limit) {
            val row = if (fromTop) distance else previous.rows() - distance - 1
            val previousRow = previous.row(row)
            val nextRow = next.row(row)
            val difference = Core.norm(previousRow, nextRow, Core.NORM_L1) / previous.cols()
            previousRow.release()
            nextRow.release()

            if (difference <= FIXED_ROW_MAX_ERROR) {
                lastMatchingRow = distance
                misses = 0
            } else if (++misses > FIXED_EDGE_TOLERANCE) {
                break
            }
        }

        val size = lastMatchingRow + 1
        return if (size >= MIN_FIXED_EDGE_HEIGHT) size else 0
    }

    private fun findScreenshotOffsetCandidate(
        previous: Mat,
        next: Mat,
        contentStart: Int,
        contentEnd: Int,
        minOffset: Int,
        minOverlap: Int,
        probePosition: Double
    ): OffsetCandidate? {
        val contentHeight = contentEnd - contentStart
        val probeHeight = (contentHeight * PROBE_HEIGHT_RATIO)
            .roundToInt()
            .coerceIn(MIN_PROBE_HEIGHT, MAX_PROBE_HEIGHT)
        val probeY = contentStart +
                ((contentHeight - probeHeight) * probePosition).roundToInt()
        val searchStart = probeY + minOffset
        val searchEnd = contentEnd - probeHeight
        val maxOffset = contentHeight - minOverlap
        if (searchEnd < searchStart || searchStart - probeY > maxOffset) return null

        val actualSearchEnd = minOf(searchEnd, probeY + maxOffset)
        val template = next.submat(Rect(0, probeY, next.cols(), probeHeight))
        val mean = MatOfDouble()
        val deviation = MatOfDouble()
        Core.meanStdDev(template, mean, deviation)
        val templateDeviation = deviation.toArray().firstOrNull() ?: 0.0
        mean.release()
        deviation.release()
        if (templateDeviation < MIN_PROBE_DEVIATION) {
            template.release()
            return null
        }

        val search = previous.submat(
            Rect(
                0,
                searchStart,
                previous.cols(),
                actualSearchEnd - searchStart + probeHeight
            )
        )
        val matches = Mat()
        Imgproc.matchTemplate(search, template, matches, Imgproc.TM_CCOEFF_NORMED)
        val match = Core.minMaxLoc(matches)
        val result = OffsetCandidate(
            offset = searchStart + match.maxLoc.y.roundToInt() - probeY,
            score = match.maxVal
        )
        template.release()
        search.release()
        matches.release()
        return result
    }

    private fun scoreScreenshotOffset(
        previous: Mat,
        next: Mat,
        contentStart: Int,
        contentEnd: Int,
        offset: Int
    ): Double {
        val overlapHeight = contentEnd - contentStart - offset
        if (overlapHeight < MIN_SCREENSHOT_OVERLAP) return 0.0

        val previousOverlap = previous.submat(
            Rect(0, contentStart + offset, previous.cols(), overlapHeight)
        )
        val nextOverlap = next.submat(
            Rect(0, contentStart, next.cols(), overlapHeight)
        )
        val matches = Mat()
        Imgproc.matchTemplate(
            previousOverlap,
            nextOverlap,
            matches,
            Imgproc.TM_CCOEFF_NORMED
        )
        val score = Core.minMaxLoc(matches).maxVal
        previousOverlap.release()
        nextOverlap.release()
        matches.release()
        return score
    }

    private suspend fun cvStitch(
        uris: List<String>,
        imageScale: Float,
        stitchMode: StitchMode
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
                mat1 = next,
                blendRadius = stitchMode.blendRadius
            )
            current.release()
            next.release()

            stitched ?: return null
            current = stitched
        }

        val output = if (stitchMode.cropToContent) {
            cropToOpaqueArea(current)
        } else current
        return output.toBitmap().also {
            if (output !== current) output.release()
            current.release()
        }
    }

    private suspend fun cvPanorama(
        uris: List<String>,
        imageScale: Float,
        stitchMode: StitchMode.Panorama
    ): Bitmap? {
        if (uris.size < 2) return null

        val images = uris.mapNotNull {
            it.toBitmap(
                imageScale = imageScale,
                stitchMode = stitchMode
            )?.toMat()
        }
        if (images.size != uris.size) {
            images.forEach(Mat::release)
            return null
        }

        val centerIndex = images.size / 2
        val transforms = arrayOfNulls<Mat>(images.size)
        transforms[centerIndex] = Mat.eye(3, 3, CvType.CV_64F)

        for (index in centerIndex - 1 downTo 0) {
            val homography = findHomography(
                source = images[index],
                target = images[index + 1],
                diff = true
            ) ?: return releaseAndReturnNull(images, transforms)
            transforms[index] = transforms[index + 1]!! * homography
            homography.release()
        }
        for (index in centerIndex + 1 until images.size) {
            val homography = findHomography(
                source = images[index],
                target = images[index - 1],
                diff = true
            ) ?: return releaseAndReturnNull(images, transforms)
            transforms[index] = transforms[index - 1]!! * homography
            homography.release()
        }

        val transformedCorners = images.flatMapIndexed { index, image ->
            val corners = MatOfPoint2f(
                Point(0.0, 0.0),
                Point(image.cols().toDouble(), 0.0),
                Point(image.cols().toDouble(), image.rows().toDouble()),
                Point(0.0, image.rows().toDouble())
            )
            val result = MatOfPoint2f()
            Core.perspectiveTransform(corners, result, transforms[index]!!)
            result.toArray().asList().also {
                corners.release()
                result.release()
            }
        }
        if (transformedCorners.any { !it.x.isFinite() || !it.y.isFinite() }) {
            return releaseAndReturnNull(images, transforms)
        }

        val minX = floor(transformedCorners.minOf { it.x })
        val minY = floor(transformedCorners.minOf { it.y })
        val maxX = ceil(transformedCorners.maxOf { it.x })
        val maxY = ceil(transformedCorners.maxOf { it.y })
        val width = maxX - minX
        val height = maxY - minY
        val inputPixels = images.sumOf { it.cols().toLong() * it.rows() }
        if (
            width < 1.0 || height < 1.0 ||
            width > Int.MAX_VALUE || height > Int.MAX_VALUE ||
            width.toLong() * height.toLong() > inputPixels * 8
        ) {
            return releaseAndReturnNull(images, transforms)
        }

        val canvasSize = Size(width, height)
        val canvas = Mat(canvasSize, images.first().type(), Scalar.all(0.0))
        val occupiedMask = Mat(canvasSize, CvType.CV_8UC1, Scalar.all(0.0))
        val offset = Mat.eye(3, 3, CvType.CV_64F).apply {
            put(0, 2, -minX)
            put(1, 2, -minY)
        }
        images.indices
            .sortedByDescending { kotlin.math.abs(it - centerIndex) }
            .forEach { index ->
                val adjustedTransform = offset * transforms[index]!!
                val warped = Mat(canvasSize, images[index].type(), Scalar.all(0.0))
                val sourceMask = createContentMask(images[index])
                val warpedMask = Mat(canvasSize, CvType.CV_8UC1, Scalar.all(0.0))
                Imgproc.warpPerspective(images[index], warped, adjustedTransform, canvasSize)
                Imgproc.warpPerspective(
                    sourceMask,
                    warpedMask,
                    adjustedTransform,
                    canvasSize,
                    Imgproc.INTER_NEAREST
                )
                copyWithoutBorderSeam(
                    source = warped,
                    sourceMask = warpedMask,
                    destination = canvas,
                    occupiedMask = occupiedMask,
                    blendRadius = stitchMode.blendRadius
                )
                adjustedTransform.release()
                warped.release()
                sourceMask.release()
                warpedMask.release()
            }

        val output = if (stitchMode.cropToContent) {
            cropToFilledArea(canvas, occupiedMask)
        } else canvas
        val result = output.toBitmap()
        if (output !== canvas) output.release()
        canvas.release()
        occupiedMask.release()
        offset.release()
        images.forEach(Mat::release)
        transforms.filterNotNull().forEach(Mat::release)
        return result
    }

    private fun copyWithoutBorderSeam(
        source: Mat,
        sourceMask: Mat,
        destination: Mat,
        occupiedMask: Mat,
        blendRadius: Int
    ) {
        val erodedMask = Mat()
        val invertedOccupiedMask = Mat()
        val newAreaMask = Mat()
        val overlapMask = Mat()
        val erosionKernel = Mat.ones(5, 5, CvType.CV_8UC1)

        Imgproc.erode(
            sourceMask,
            erodedMask,
            erosionKernel,
            Point(-1.0, -1.0),
            1,
            Core.BORDER_CONSTANT,
            Scalar.all(0.0)
        )
        Core.bitwise_not(occupiedMask, invertedOccupiedMask)
        Core.bitwise_and(erodedMask, invertedOccupiedMask, newAreaMask)
        Core.bitwise_and(erodedMask, occupiedMask, overlapMask)
        val compensatedSource = if (blendRadius > 0) {
            compensateExposure(
                source = source,
                destination = destination,
                overlapMask = overlapMask,
                blendRadius = blendRadius
            )
        } else null
        val sourceToBlend = compensatedSource ?: source
        sourceToBlend.copyTo(destination, newAreaMask)
        if (blendRadius > 0) {
            featherBlend(
                source = sourceToBlend,
                destination = destination,
                sourceMask = erodedMask,
                destinationMask = occupiedMask,
                overlapMask = overlapMask,
                blendRadius = blendRadius
            )
        } else {
            sourceToBlend.copyTo(destination, overlapMask)
        }
        Core.bitwise_or(occupiedMask, erodedMask, occupiedMask)

        compensatedSource?.release()
        erosionKernel.release()
        erodedMask.release()
        invertedOccupiedMask.release()
        newAreaMask.release()
        overlapMask.release()
    }

    private fun compensateExposure(
        source: Mat,
        destination: Mat,
        overlapMask: Mat,
        blendRadius: Int
    ): Mat? {
        if (Core.countNonZero(overlapMask) == 0) return null

        val sourceMean = Core.mean(source, overlapMask)
        val destinationMean = Core.mean(destination, overlapMask)
        val strength = (blendRadius / EXPOSURE_BLEND_RADIUS).coerceIn(0.0, 1.0)
        val gains = DoubleArray(4) { 1.0 }

        repeat(minOf(source.channels(), 3)) { channel ->
            val sourceValue = sourceMean.`val`[channel]
            if (sourceValue > 1.0) {
                val targetGain = (destinationMean.`val`[channel] / sourceValue)
                    .coerceIn(MIN_EXPOSURE_GAIN, MAX_EXPOSURE_GAIN)
                gains[channel] = 1.0 + (targetGain - 1.0) * strength
            }
        }

        return Mat().also {
            Core.multiply(
                source,
                Scalar(gains[0], gains[1], gains[2], gains[3]),
                it
            )
        }
    }

    private fun featherBlend(
        source: Mat,
        destination: Mat,
        sourceMask: Mat,
        destinationMask: Mat,
        overlapMask: Mat,
        blendRadius: Int
    ) {
        val overlapBounds = nonZeroBounds(overlapMask) ?: return
        val sourceDistance = Mat()
        val destinationDistance = Mat()
        Imgproc.distanceTransform(
            sourceMask,
            sourceDistance,
            Geometry.DIST_L2,
            Imgproc.DIST_MASK_3
        )
        Imgproc.distanceTransform(
            destinationMask,
            destinationDistance,
            Geometry.DIST_L2,
            Imgproc.DIST_MASK_3
        )

        val sourceRoi = source.submat(overlapBounds)
        val destinationRoi = destination.submat(overlapBounds)
        val overlapRoi = overlapMask.submat(overlapBounds)
        val sourceDistanceRoi = sourceDistance.submat(overlapBounds)
        val destinationDistanceRoi = destinationDistance.submat(overlapBounds)
        val sourceWeight = Mat()
        val destinationWeight = Mat()
        val totalWeight = Mat()
        val featherPower = (FEATHER_REFERENCE_RADIUS / blendRadius)
            .coerceIn(MIN_FEATHER_POWER, MAX_FEATHER_POWER)
        Core.pow(sourceDistanceRoi, featherPower, sourceWeight)
        Core.pow(destinationDistanceRoi, featherPower, destinationWeight)
        Core.add(sourceWeight, destinationWeight, totalWeight)
        Core.add(totalWeight, Scalar.all(0.001), totalWeight)
        val normalizedAlpha = Mat()
        Core.divide(sourceWeight, totalWeight, normalizedAlpha)
        val alpha = Mat.zeros(overlapBounds.size(), CvType.CV_32FC1)
        normalizedAlpha.copyTo(alpha, overlapRoi)
        val alphaSquared = Mat()
        val smoothFactor = Mat()
        Core.multiply(alpha, alpha, alphaSquared)
        Core.multiply(alpha, Scalar.all(-2.0), smoothFactor)
        Core.add(smoothFactor, Scalar.all(3.0), smoothFactor)
        Core.multiply(alphaSquared, smoothFactor, alpha)
        val inverseAlpha = Mat()
        Core.multiply(alpha, Scalar.all(-1.0), inverseAlpha)
        Core.add(inverseAlpha, Scalar.all(1.0), inverseAlpha)

        repeat(source.channels()) { channel ->
            val sourceChannel = Mat()
            val destinationChannel = Mat()
            val sourceFloat = Mat()
            val destinationFloat = Mat()
            val blendedFloat = Mat()
            val blendedChannel = Mat()

            Core.extractChannel(sourceRoi, sourceChannel, channel)
            Core.extractChannel(destinationRoi, destinationChannel, channel)
            sourceChannel.convertTo(sourceFloat, CvType.CV_32FC1)
            destinationChannel.convertTo(destinationFloat, CvType.CV_32FC1)
            Core.multiply(sourceFloat, alpha, sourceFloat)
            Core.multiply(destinationFloat, inverseAlpha, destinationFloat)
            Core.add(sourceFloat, destinationFloat, blendedFloat)
            blendedFloat.convertTo(blendedChannel, CvType.CV_8UC1)
            Core.insertChannel(blendedChannel, destinationRoi, channel)

            sourceChannel.release()
            destinationChannel.release()
            sourceFloat.release()
            destinationFloat.release()
            blendedFloat.release()
            blendedChannel.release()
        }

        sourceRoi.release()
        destinationRoi.release()
        overlapRoi.release()
        sourceDistanceRoi.release()
        destinationDistanceRoi.release()
        sourceWeight.release()
        destinationWeight.release()
        totalWeight.release()
        normalizedAlpha.release()
        alpha.release()
        alphaSquared.release()
        smoothFactor.release()
        inverseAlpha.release()
        sourceDistance.release()
        destinationDistance.release()
    }

    private fun nonZeroBounds(mask: Mat): Rect? {
        val rowData = ByteArray(mask.cols())
        var minX = mask.cols()
        var minY = mask.rows()
        var maxX = -1
        var maxY = -1

        repeat(mask.rows()) { row ->
            mask.get(row, 0, rowData)
            repeat(mask.cols()) { column ->
                if (rowData[column].toInt() != 0) {
                    minX = minOf(minX, column)
                    minY = minOf(minY, row)
                    maxX = maxOf(maxX, column)
                    maxY = maxOf(maxY, row)
                }
            }
        }
        return if (maxX < minX || maxY < minY) null
        else Rect(minX, minY, maxX - minX + 1, maxY - minY + 1)
    }

    private fun cropToFilledArea(image: Mat, mask: Mat): Mat {
        val cropRect = largestFilledRectangle(mask) ?: return image
        if (
            cropRect.x == 0 && cropRect.y == 0 &&
            cropRect.width == image.cols() && cropRect.height == image.rows()
        ) {
            return image
        }
        val croppedView = image.submat(cropRect)
        return croppedView.clone().also { croppedView.release() }
    }

    private fun cropToOpaqueArea(image: Mat): Mat {
        if (image.channels() < 4) return image

        val alpha = Mat()
        val mask = Mat()
        Core.extractChannel(image, alpha, 3)
        Imgproc.threshold(alpha, mask, 254.0, 255.0, Imgproc.THRESH_BINARY)
        val result = cropToFilledArea(image, mask)
        alpha.release()
        mask.release()
        return result
    }

    private fun createContentMask(image: Mat): Mat {
        if (image.channels() < 4) {
            return Mat(image.size(), CvType.CV_8UC1, Scalar.all(255.0))
        }

        val alpha = Mat()
        val mask = Mat()
        Core.extractChannel(image, alpha, 3)
        Imgproc.threshold(alpha, mask, 0.0, 255.0, Imgproc.THRESH_BINARY)
        alpha.release()
        return mask
    }

    private fun largestFilledRectangle(mask: Mat): Rect? {
        val columns = mask.cols()
        val heights = IntArray(columns)
        val stack = IntArray(columns + 1)
        val rowData = ByteArray(columns)
        var bestArea = 0L
        var bestRect: Rect? = null

        repeat(mask.rows()) { row ->
            mask.get(row, 0, rowData)
            repeat(columns) { column ->
                heights[column] = if (rowData[column].toInt() != 0) {
                    heights[column] + 1
                } else 0
            }

            var stackSize = 0
            for (column in 0..columns) {
                val currentHeight = if (column < columns) heights[column] else 0
                while (
                    stackSize > 0 &&
                    heights[stack[stackSize - 1]] > currentHeight
                ) {
                    val height = heights[stack[--stackSize]]
                    val start = if (stackSize == 0) 0 else stack[stackSize - 1] + 1
                    val area = height.toLong() * (column - start)
                    if (area > bestArea) {
                        bestArea = area
                        bestRect = Rect(
                            start,
                            row - height + 1,
                            column - start,
                            height
                        )
                    }
                }
                stack[stackSize++] = column
            }
        }
        return bestRect
    }

    private fun releaseAndReturnNull(
        images: List<Mat>,
        transforms: Array<Mat?>
    ): Nothing? {
        images.forEach(Mat::release)
        transforms.filterNotNull().forEach(Mat::release)
        return null
    }

    private suspend fun String.toBitmap(
        imageScale: Float,
        stitchMode: StitchMode
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

    private data class ScreenshotOverlap(
        val previousContentEnd: Int,
        val nextOverlapEnd: Int
    )

    private data class OffsetCandidate(
        val offset: Int,
        val score: Double
    )

    private companion object {
        const val MATCH_WIDTH = 160
        const val MATCH_HORIZONTAL_INSET_RATIO = 0.08
        const val MAX_FIXED_TOP_RATIO = 0.25
        const val MAX_FIXED_BOTTOM_RATIO = 0.18
        const val SCREENSHOT_BOTTOM_SAFE_INSET_RATIO = 0.045
        const val FIXED_ROW_MAX_ERROR = 4.0
        const val FIXED_EDGE_TOLERANCE = 3
        const val MIN_FIXED_EDGE_HEIGHT = 6
        const val MIN_MATCH_CONTENT_HEIGHT = 48
        const val MIN_SCROLL_OFFSET = 4
        const val MIN_SCROLL_OFFSET_RATIO = 0.015
        const val MIN_SCREENSHOT_OVERLAP = 24
        const val MIN_SCREENSHOT_OVERLAP_RATIO = 0.1
        const val PROBE_HEIGHT_RATIO = 0.12
        const val MIN_PROBE_HEIGHT = 16
        const val MAX_PROBE_HEIGHT = 56
        const val MIN_PROBE_DEVIATION = 6.0
        const val OFFSET_CLUSTER_RADIUS = 2
        const val MIN_TEMPLATE_SCORE = 0.6
        const val MIN_OVERLAP_SCORE = 0.45
        const val EXPOSURE_BLEND_RADIUS = 32.0
        const val FEATHER_REFERENCE_RADIUS = 32.0
        const val MIN_FEATHER_POWER = 0.25
        const val MAX_FEATHER_POWER = 32.0
        const val MIN_EXPOSURE_GAIN = 0.75
        const val MAX_EXPOSURE_GAIN = 1.33

        val PROBE_POSITIONS = listOf(0.02, 0.14, 0.28, 0.44)
    }

}
