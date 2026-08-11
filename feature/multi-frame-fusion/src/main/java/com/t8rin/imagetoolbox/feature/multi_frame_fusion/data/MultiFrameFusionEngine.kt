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

package com.t8rin.imagetoolbox.feature.multi_frame_fusion.data

import android.graphics.Bitmap
import androidx.core.graphics.createBitmap
import com.t8rin.imagetoolbox.feature.multi_frame_fusion.domain.FusionMode
import com.t8rin.imagetoolbox.feature.multi_frame_fusion.domain.FusionParams
import com.t8rin.opencv_tools.utils.OpenCV
import org.opencv.android.Utils
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
import org.opencv.features.BFMatcher
import org.opencv.features.ORB
import org.opencv.geometry.Geometry
import org.opencv.imgproc.Imgproc
import org.opencv.photo.Photo
import javax.inject.Inject
import kotlin.math.pow

internal class MultiFrameFusionEngine @Inject constructor() : OpenCV() {

    fun fuse(
        bitmaps: List<Bitmap>,
        params: FusionParams,
        onFrameAligned: (Int) -> Unit = {}
    ): Bitmap? {
        if (bitmaps.size < FusionParams.MIN_IMAGES) return null

        val normalizedParams = params.normalized()
        val images = bitmaps.mapTo(mutableListOf(), ::bitmapToRgbMat)

        return try {
            normalizeSizes(images)
            if (normalizedParams.alignImages) {
                alignToFirstImage(
                    images = images,
                    cropToOverlap = normalizedParams.cropToOverlap,
                    onFrameAligned = onFrameAligned
                )
            } else {
                repeat(images.size) { onFrameAligned(it + 1) }
            }

            val output = when (normalizedParams.mode) {
                FusionMode.Exposure -> exposureFusion(images, normalizedParams)
                FusionMode.Focus -> focusStack(images, normalizedParams)
                FusionMode.Median -> medianStack(images)
                FusionMode.LongExposure -> longExposure(images)
                FusionMode.LightTrails -> lightTrails(images, normalizedParams)
                FusionMode.MotionTrails -> motionTrails(images, normalizedParams)
            }
            output.toBitmapAndRelease()
        } finally {
            images.forEach(Mat::release)
        }
    }

    private fun bitmapToRgbMat(bitmap: Bitmap): Mat {
        val rgba = Mat()
        val rgb = Mat()
        Utils.bitmapToMat(bitmap, rgba)
        Imgproc.cvtColor(rgba, rgb, Imgproc.COLOR_RGBA2RGB)
        rgba.release()
        return rgb
    }

    private fun normalizeSizes(images: MutableList<Mat>) {
        val targetSize = images.first().size()
        images.indices.drop(1).forEach { index ->
            val image = images[index]
            if (image.size() != targetSize) {
                val resized = Mat()
                Imgproc.resize(image, resized, targetSize, 0.0, 0.0, Imgproc.INTER_AREA)
                image.release()
                images[index] = resized
            }
        }
    }

    private fun alignToFirstImage(
        images: MutableList<Mat>,
        cropToOverlap: Boolean,
        onFrameAligned: (Int) -> Unit
    ) {
        val reference = images.first()
        val commonMask = Mat(
            reference.rows(),
            reference.cols(),
            CvType.CV_8UC1,
            Scalar.all(255.0)
        )
        val referenceFeatures = extractFeatures(reference)
        onFrameAligned(1)

        try {
            images.indices.drop(1).forEach { index ->
                val source = images[index]
                val homography = referenceFeatures?.let { findHomography(source, it) }
                if (homography != null) {
                    val warped = Mat()
                    val warpedMask = Mat()
                    val sourceMask = Mat(
                        source.rows(),
                        source.cols(),
                        CvType.CV_8UC1,
                        Scalar.all(255.0)
                    )
                    Imgproc.warpPerspective(
                        source,
                        warped,
                        homography,
                        reference.size(),
                        Imgproc.INTER_LINEAR,
                        Core.BORDER_CONSTANT,
                        Scalar.all(0.0)
                    )
                    Imgproc.warpPerspective(
                        sourceMask,
                        warpedMask,
                        homography,
                        reference.size(),
                        Imgproc.INTER_NEAREST,
                        Core.BORDER_CONSTANT,
                        Scalar.all(0.0)
                    )

                    val validFraction = Core.countNonZero(warpedMask).toDouble() /
                            (warpedMask.rows() * warpedMask.cols()).coerceAtLeast(1)
                    if (validFraction >= MIN_VALID_ALIGNMENT_FRACTION) {
                        source.release()
                        images[index] = warped
                        Core.bitwise_and(commonMask, warpedMask, commonMask)
                    } else {
                        warped.release()
                    }

                    sourceMask.release()
                    warpedMask.release()
                    homography.release()
                }
                onFrameAligned(index + 1)
            }

            if (cropToOverlap) {
                cropToCommonArea(images, commonMask)
            }
        } finally {
            referenceFeatures?.release()
            commonMask.release()
        }
    }

    private fun extractFeatures(image: Mat): Features? {
        val gray = Mat()
        val keyPoints = MatOfKeyPoint()
        val descriptors = Mat()
        val emptyMask = Mat()
        val detector = ORB.create(MAX_FEATURES)
        return try {
            Imgproc.cvtColor(image, gray, Imgproc.COLOR_RGB2GRAY)
            detector.detectAndCompute(gray, emptyMask, keyPoints, descriptors)
            if (keyPoints.empty() || descriptors.empty()) {
                keyPoints.release()
                descriptors.release()
                null
            } else {
                Features(keyPoints, descriptors)
            }
        } finally {
            detector.clear()
            gray.release()
            emptyMask.release()
        }
    }

    private fun findHomography(source: Mat, reference: Features): Mat? {
        val sourceFeatures = extractFeatures(source) ?: return null
        val matcher = BFMatcher.create(Core.NORM_HAMMING, false)
        val matches = mutableListOf<MatOfDMatch>()
        return try {
            matcher.knnMatch(sourceFeatures.descriptors, reference.descriptors, matches, 2)
            val sourceKeyPoints = sourceFeatures.keyPoints.toArray()
            val referenceKeyPoints = reference.keyPoints.toArray()
            val sourcePoints = mutableListOf<Point>()
            val referencePoints = mutableListOf<Point>()

            matches.forEach { matchSet ->
                val pair = matchSet.toArray()
                if (pair.size >= 2 && pair[0].distance < LOWE_RATIO * pair[1].distance) {
                    sourcePoints += sourceKeyPoints[pair[0].queryIdx].pt
                    referencePoints += referenceKeyPoints[pair[0].trainIdx].pt
                }
            }
            if (sourcePoints.size < MIN_FEATURE_MATCHES) return null

            val sourcePointsMat = MatOfPoint2f(*sourcePoints.toTypedArray())
            val referencePointsMat = MatOfPoint2f(*referencePoints.toTypedArray())
            try {
                Geometry.findHomography(
                    sourcePointsMat,
                    referencePointsMat,
                    Geometry.RANSAC,
                    RANSAC_REPROJECTION_THRESHOLD
                ).takeUnless(Mat::empty)?.also {
                    if (!isSaneHomography(it, source.size())) {
                        it.release()
                        return null
                    }
                }
            } finally {
                sourcePointsMat.release()
                referencePointsMat.release()
            }
        } finally {
            matcher.clear()
            matches.forEach(MatOfDMatch::release)
            sourceFeatures.release()
        }
    }

    private fun isSaneHomography(homography: Mat, imageSize: Size): Boolean {
        val sourceCorners = MatOfPoint2f(
            Point(0.0, 0.0),
            Point(imageSize.width, 0.0),
            Point(imageSize.width, imageSize.height),
            Point(0.0, imageSize.height)
        )
        val transformedCorners = MatOfPoint2f()
        return try {
            Core.perspectiveTransform(sourceCorners, transformedCorners, homography)
            val corners = transformedCorners.toArray()
            if (corners.size != 4 || corners.any { !it.x.isFinite() || !it.y.isFinite() }) {
                false
            } else {
                val width = maxOf(
                    corners[0].distanceTo(corners[1]),
                    corners[2].distanceTo(corners[3])
                )
                val height = maxOf(
                    corners[0].distanceTo(corners[3]),
                    corners[1].distanceTo(corners[2])
                )
                width in imageSize.width * MIN_TRANSFORM_SCALE..imageSize.width * MAX_TRANSFORM_SCALE &&
                        height in imageSize.height * MIN_TRANSFORM_SCALE..imageSize.height * MAX_TRANSFORM_SCALE
            }
        } finally {
            sourceCorners.release()
            transformedCorners.release()
        }
    }

    private fun Point.distanceTo(other: Point): Double = kotlin.math.hypot(
        x - other.x,
        y - other.y
    )

    private fun cropToCommonArea(images: MutableList<Mat>, commonMask: Mat) {
        val rect = largestValidRectangle(commonMask) ?: return
        val fullArea = commonMask.rows().toLong() * commonMask.cols()
        val cropArea = rect.width.toLong() * rect.height
        if (rect.width < MIN_CROP_SIDE || rect.height < MIN_CROP_SIDE ||
            cropArea < fullArea * MIN_CROP_AREA_FRACTION
        ) {
            return
        }

        images.indices.forEach { index ->
            val source = images[index]
            val cropped = Mat(source, Rect(rect.x, rect.y, rect.width, rect.height)).clone()
            source.release()
            images[index] = cropped
        }
    }

    private fun largestValidRectangle(mask: Mat): Rect? {
        val width = mask.cols()
        val height = mask.rows()
        if (width == 0 || height == 0) return null

        val pixels = ByteArray(width * height)
        mask.get(0, 0, pixels)
        val histogram = IntArray(width)
        val stack = IntArray(width + 1)
        var bestRect: Rect? = null
        var bestArea = 0

        repeat(height) { y ->
            repeat(width) { x ->
                histogram[x] = if (pixels[y * width + x].toInt() != 0) {
                    histogram[x] + 1
                } else {
                    0
                }
            }

            var stackSize = 0
            for (x in 0..width) {
                val currentHeight = if (x == width) 0 else histogram[x]
                while (stackSize > 0 && histogram[stack[stackSize - 1]] > currentHeight) {
                    val barIndex = stack[--stackSize]
                    val rectangleHeight = histogram[barIndex]
                    val left = if (stackSize == 0) 0 else stack[stackSize - 1] + 1
                    val rectangleWidth = x - left
                    val area = rectangleWidth * rectangleHeight
                    if (area > bestArea) {
                        bestArea = area
                        bestRect = Rect(
                            left,
                            y - rectangleHeight + 1,
                            rectangleWidth,
                            rectangleHeight
                        )
                    }
                }
                stack[stackSize++] = x
            }
        }

        return bestRect
    }

    private fun exposureFusion(images: List<Mat>, params: FusionParams): Mat {
        val output = Mat()
        val merger = Photo.createMergeMertens(
            params.contrastWeight,
            params.saturationWeight,
            params.exposureWeight
        )
        merger.process(images, output)
        val result = Mat()
        output.convertTo(result, CvType.CV_8UC3, 255.0)
        output.release()
        return result
    }

    private fun focusStack(images: List<Mat>, params: FusionParams): Mat {
        val rows = images.first().rows()
        val cols = images.first().cols()
        val accumulator = Mat.zeros(rows, cols, CvType.CV_32FC3)
        val weightSum = Mat.zeros(rows, cols, CvType.CV_32FC1)
        val brightnessValues = images.map { image -> image.averageBrightness() }
        val targetBrightness = brightnessValues.sorted()[brightnessValues.size / 2]

        images.forEachIndexed { index, image ->
            val gray = Mat()
            val sharpness = Mat()
            val weight = Mat()
            val floatImage = Mat()
            val weightChannels = Mat()
            val weightedImage = Mat()
            try {
                Imgproc.cvtColor(image, gray, Imgproc.COLOR_RGB2GRAY)
                Imgproc.Laplacian(gray, sharpness, CvType.CV_32F, 3)
                Core.absdiff(sharpness, Scalar.all(0.0), sharpness)
                Imgproc.GaussianBlur(
                    sharpness,
                    weight,
                    Size(params.focusRadius.toDouble(), params.focusRadius.toDouble()),
                    0.0
                )
                Core.add(weight, Scalar.all(FOCUS_WEIGHT_EPSILON), weight)
                Core.pow(weight, params.focusStrength.toDouble(), weight)
                val exposureScale = (targetBrightness / brightnessValues[index])
                    .coerceIn(MIN_EXPOSURE_SCALE, MAX_EXPOSURE_SCALE)
                image.convertTo(
                    floatImage,
                    CvType.CV_32FC3,
                    exposureScale / 255.0
                )
                Core.merge(listOf(weight, weight, weight), weightChannels)
                Core.multiply(floatImage, weightChannels, weightedImage)
                Core.add(accumulator, weightedImage, accumulator)
                Core.add(weightSum, weight, weightSum)
            } finally {
                gray.release()
                sharpness.release()
                weight.release()
                floatImage.release()
                weightChannels.release()
                weightedImage.release()
            }
        }

        val accumulatedChannels = mutableListOf<Mat>()
        val resultChannels = mutableListOf<Mat>()
        val resultFloat = Mat()
        val result = Mat()
        try {
            Core.split(accumulator, accumulatedChannels)
            accumulatedChannels.forEach { channel ->
                val divided = Mat()
                Core.divide(channel, weightSum, divided)
                resultChannels += divided
            }
            Core.merge(resultChannels, resultFloat)
            resultFloat.convertTo(result, CvType.CV_8UC3, 255.0)
            return result
        } finally {
            accumulator.release()
            weightSum.release()
            accumulatedChannels.forEach(Mat::release)
            resultChannels.forEach(Mat::release)
            resultFloat.release()
        }
    }

    private fun Mat.averageBrightness(): Double = Core.mean(this).`val`
        .take(3)
        .average()
        .coerceAtLeast(MIN_BRIGHTNESS)

    private fun medianStack(images: List<Mat>): Mat {
        val rows = images.first().rows()
        val columns = images.first().cols() * images.first().channels()
        val stack = Mat(images.size, rows * columns, CvType.CV_8UC1)
        val sorted = Mat()
        val medianFlat = Mat()
        try {
            images.forEachIndexed { index, image ->
                image.reshape(1, 1).copyTo(stack.row(index))
            }
            Core.sort(stack, sorted, Core.SORT_EVERY_COLUMN or Core.SORT_ASCENDING)
            if (images.size % 2 == 1) {
                sorted.row(images.size / 2).copyTo(medianFlat)
            } else {
                Core.addWeighted(
                    sorted.row(images.size / 2 - 1),
                    0.5,
                    sorted.row(images.size / 2),
                    0.5,
                    0.0,
                    medianFlat
                )
            }
            return medianFlat.reshape(3, rows).clone()
        } finally {
            stack.release()
            sorted.release()
            medianFlat.release()
        }
    }

    private fun longExposure(images: List<Mat>): Mat {
        val average = linearAverage(images)
        return try {
            average.toRgb8()
        } finally {
            average.release()
        }
    }

    private fun lightTrails(images: List<Mat>, params: FusionParams): Mat {
        val base = linearAverage(images)
        val brightest = images.first().toLinearFloat()
        val brightestIntensity = brightest.intensity()
        val candidate = Mat()
        val candidateIntensity = Mat()
        val brighterMask = Mat()
        val baseIntensity = Mat()
        val trailMask = Mat()
        val trailMaskChannels = Mat()
        val delta = Mat()
        val weightedDelta = Mat()
        val resultLinear = Mat()

        return try {
            images.drop(1).forEach { image ->
                image.convertTo(candidate, CvType.CV_32FC3, BYTE_TO_UNIT)
                Core.pow(candidate, LINEAR_GAMMA, candidate)
                candidate.intensity(candidateIntensity)
                Core.compare(candidateIntensity, brightestIntensity, brighterMask, Core.CMP_GT)
                candidate.copyTo(brightest, brighterMask)
                candidateIntensity.copyTo(brightestIntensity, brighterMask)
            }

            base.intensity(baseIntensity)
            Core.subtract(brightestIntensity, baseIntensity, trailMask)
            Core.subtract(
                trailMask,
                Scalar.all(params.lightTrailThreshold.toDouble()),
                trailMask
            )
            Core.max(trailMask, Scalar.all(0.0), trailMask)
            Core.multiply(
                trailMask,
                Scalar.all(1.0 / (1.0 - params.lightTrailThreshold)),
                trailMask
            )
            Core.min(trailMask, Scalar.all(1.0), trailMask)
            Core.sqrt(trailMask, trailMask)
            Core.multiply(
                trailMask,
                Scalar.all(params.trailStrength.toDouble()),
                trailMask
            )
            Core.merge(listOf(trailMask, trailMask, trailMask), trailMaskChannels)
            Core.subtract(brightest, base, delta)
            Core.multiply(delta, trailMaskChannels, weightedDelta)
            Core.add(base, weightedDelta, resultLinear)
            resultLinear.toRgb8()
        } finally {
            base.release()
            brightest.release()
            brightestIntensity.release()
            candidate.release()
            candidateIntensity.release()
            brighterMask.release()
            baseIntensity.release()
            trailMask.release()
            trailMaskChannels.release()
            delta.release()
            weightedDelta.release()
            resultLinear.release()
        }
    }

    private fun motionTrails(images: List<Mat>, params: FusionParams): Mat {
        val accumulator = Mat.zeros(
            images.first().rows(),
            images.first().cols(),
            CvType.CV_32FC3
        )
        val frame = Mat()
        val weightedFrame = Mat()
        val average = Mat()
        val latest = images.last().toLinearFloat()
        val resultLinear = Mat()
        var weightSum = 0.0

        return try {
            images.forEachIndexed { index, image ->
                val weight = params.trailPersistence.toDouble()
                    .pow(images.lastIndex - index)
                image.convertTo(frame, CvType.CV_32FC3, BYTE_TO_UNIT)
                Core.pow(frame, LINEAR_GAMMA, frame)
                Core.multiply(frame, Scalar.all(weight), weightedFrame)
                Core.add(accumulator, weightedFrame, accumulator)
                weightSum += weight
            }
            Core.multiply(accumulator, Scalar.all(1.0 / weightSum), average)
            Core.addWeighted(
                latest,
                1.0 - params.trailStrength,
                average,
                params.trailStrength.toDouble(),
                0.0,
                resultLinear
            )
            resultLinear.toRgb8()
        } finally {
            accumulator.release()
            frame.release()
            weightedFrame.release()
            average.release()
            latest.release()
            resultLinear.release()
        }
    }

    private fun linearAverage(images: List<Mat>): Mat {
        val accumulator = Mat.zeros(
            images.first().rows(),
            images.first().cols(),
            CvType.CV_32FC3
        )
        val frame = Mat()
        try {
            images.forEach { image ->
                image.convertTo(frame, CvType.CV_32FC3, BYTE_TO_UNIT)
                Core.pow(frame, LINEAR_GAMMA, frame)
                Core.add(accumulator, frame, accumulator)
            }
            Core.multiply(accumulator, Scalar.all(1.0 / images.size), accumulator)
            return accumulator
        } catch (throwable: Throwable) {
            accumulator.release()
            throw throwable
        } finally {
            frame.release()
        }
    }

    private fun Mat.toLinearFloat(): Mat = Mat().also { output ->
        convertTo(output, CvType.CV_32FC3, BYTE_TO_UNIT)
        Core.pow(output, LINEAR_GAMMA, output)
    }

    private fun Mat.intensity(): Mat = Mat().also { output -> intensity(output) }

    private fun Mat.intensity(output: Mat) {
        val channels = mutableListOf<Mat>()
        try {
            Core.split(this, channels)
            Core.max(channels[0], channels[1], output)
            Core.max(output, channels[2], output)
        } finally {
            channels.forEach(Mat::release)
        }
    }

    private fun Mat.toRgb8(): Mat = Mat().also { output ->
        Core.pow(this, INVERSE_LINEAR_GAMMA, output)
        output.convertTo(output, CvType.CV_8UC3, 255.0)
    }

    private fun Mat.toBitmapAndRelease(): Bitmap {
        val rgba = Mat()
        return try {
            when (channels()) {
                1 -> Imgproc.cvtColor(this, rgba, Imgproc.COLOR_GRAY2RGBA)
                3 -> Imgproc.cvtColor(this, rgba, Imgproc.COLOR_RGB2RGBA)
                else -> copyTo(rgba)
            }
            createBitmap(cols(), rows()).also { Utils.matToBitmap(rgba, it) }
        } finally {
            rgba.release()
            release()
        }
    }

    private data class Features(
        val keyPoints: MatOfKeyPoint,
        val descriptors: Mat
    ) {
        fun release() {
            keyPoints.release()
            descriptors.release()
        }
    }

    private companion object {
        const val MAX_FEATURES = 1800
        const val MIN_FEATURE_MATCHES = 12
        const val LOWE_RATIO = 0.76f
        const val RANSAC_REPROJECTION_THRESHOLD = 3.0
        const val MIN_VALID_ALIGNMENT_FRACTION = 0.45
        const val MIN_TRANSFORM_SCALE = 0.55
        const val MAX_TRANSFORM_SCALE = 1.8
        const val MIN_CROP_SIDE = 64
        const val MIN_CROP_AREA_FRACTION = 0.25
        const val FOCUS_WEIGHT_EPSILON = 0.5
        const val MIN_BRIGHTNESS = 1.0
        const val MIN_EXPOSURE_SCALE = 0.25
        const val MAX_EXPOSURE_SCALE = 4.0
        const val BYTE_TO_UNIT = 1.0 / 255.0
        const val LINEAR_GAMMA = 2.2
        const val INVERSE_LINEAR_GAMMA = 1.0 / LINEAR_GAMMA
    }
}
