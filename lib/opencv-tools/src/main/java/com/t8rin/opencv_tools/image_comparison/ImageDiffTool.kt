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

package com.t8rin.opencv_tools.image_comparison

import android.graphics.Bitmap
import androidx.core.graphics.createBitmap
import com.t8rin.opencv_tools.image_comparison.model.ComparisonType
import com.t8rin.opencv_tools.utils.OpenCV
import com.t8rin.opencv_tools.utils.resizeAndPad
import com.t8rin.opencv_tools.utils.toScalar
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import kotlin.math.log10
import kotlin.math.sqrt

data object ImageDiffTool : OpenCV() {

    private const val DEFAULT_WINDOW_SIZE = 11
    private const val GAUSSIAN_SIGMA = 1.5

    /*
     * SSIM constants for image values in the 0..255 range:
     *
     * C1 = (0.01 * 255)^2
     * C2 = (0.03 * 255)^2
     */
    private const val SSIM_C1 = 6.5025
    private const val SSIM_C2 = 58.5225

    private const val EPSILON = 1e-6

    /**
     * Compares [input] against [other] and highlights locally different pixels.
     *
     * [threshold] is a tolerance in the 0..100 range:
     *
     * - AE, MAE, RMSE and PSNR map it to a pixel error in the 0..255 range.
     * - SSIM maps it to the tolerated structural dissimilarity.
     * - NCC maps it to the tolerated correlation loss.
     *
     * The second image is resized and padded to match the first image.
     * All RGBA channels, including alpha, participate in the comparison.
     */
    fun highlightDifferences(
        input: Bitmap,
        other: Bitmap,
        comparisonType: ComparisonType,
        highlightColor: Int,
        threshold: Float = 4f
    ): ComparisonResult {
        require(input.width > 0 && input.height > 0) {
            "Input bitmap must not be empty"
        }
        require(other.width > 0 && other.height > 0) {
            "Other bitmap must not be empty"
        }

        val inputHolder = input.asSoftwareArgb8888()
        val otherHolder = other.asSoftwareArgb8888()

        try {
            return withMatScope {
                val firstRgba = mat()
                val secondRawRgba = mat()

                Utils.bitmapToMat(inputHolder.bitmap, firstRgba)
                Utils.bitmapToMat(otherHolder.bitmap, secondRawRgba)

                val resizedSecond = secondRawRgba.resizeAndPad(firstRgba.size())
                val secondRgba = own(resizedSecond)

                require(
                    firstRgba.rows() == secondRgba.rows() &&
                            firstRgba.cols() == secondRgba.cols()
                ) {
                    "Bitmaps must have the same size after resizing"
                }

                require(firstRgba.type() == secondRgba.type()) {
                    "Bitmaps must have the same OpenCV type"
                }

                require(firstRgba.channels() == secondRgba.channels()) {
                    "Bitmaps must have the same number of channels"
                }

                val first32 = mat()
                val second32 = mat()

                /*
                 * convertTo preserves the number of channels, so RGBA remains
                 * four-channel while calculations no longer overflow at 255.
                 */
                firstRgba.convertTo(first32, CvType.CV_32F)
                secondRgba.convertTo(second32, CvType.CV_32F)

                val tolerance = threshold.coerceIn(0f, 100f).toDouble()
                val pixelThreshold = tolerance * 255.0 / 100.0

                /*
                 * SSIM is treated as a 0..1 similarity.
                 *
                 * threshold = 0   -> only values below 1 are different
                 * threshold = 100 -> nothing is considered different
                 */
                val minimumSsim = 1.0 - tolerance / 100.0

                /*
                 * NCC is in the -1..1 range.
                 *
                 * threshold = 0   -> minimum accepted NCC is 1
                 * threshold = 100 -> minimum accepted NCC is -1
                 */
                val minimumNcc = 1.0 - 2.0 * tolerance / 100.0

                val computation = when (comparisonType) {
                    ComparisonType.AE -> computeAbsoluteError(
                        scope = this,
                        first = first32,
                        second = second32,
                        pixelThreshold = pixelThreshold
                    )

                    ComparisonType.MAE -> computeMeanAbsoluteError(
                        scope = this,
                        first = first32,
                        second = second32,
                        pixelThreshold = pixelThreshold
                    )

                    ComparisonType.RMSE -> computeRootMeanSquaredError(
                        scope = this,
                        first = first32,
                        second = second32,
                        pixelThreshold = pixelThreshold
                    )

                    ComparisonType.PSNR -> computePeakSignalToNoiseRatio(
                        scope = this,
                        first = first32,
                        second = second32,
                        pixelThreshold = pixelThreshold
                    )

                    ComparisonType.SSIM -> computeStructuralSimilarity(
                        scope = this,
                        first = first32,
                        second = second32,
                        minimumSimilarity = minimumSsim
                    )

                    ComparisonType.NCC -> computeNormalizedCrossCorrelation(
                        scope = this,
                        first = first32,
                        second = second32,
                        minimumSimilarity = minimumNcc
                    )
                }

                val resultRgba = own(firstRgba.clone())
                resultRgba.setTo(
                    highlightColor.toScalar(),
                    computation.mask
                )

                val outputBitmap = createBitmap(
                    width = inputHolder.bitmap.width,
                    height = inputHolder.bitmap.height
                )

                Utils.matToBitmap(resultRgba, outputBitmap)

                ComparisonResult(
                    score = computation.score,
                    highlightedBitmap = outputBitmap
                )
            }
        } finally {
            inputHolder.recycleIfOwned()
            otherHolder.recycleIfOwned()
        }
    }

    /**
     * AE is represented as the number of pixels where at least one RGBA
     * channel exceeds the requested tolerance.
     */
    private fun computeAbsoluteError(
        scope: MatScope,
        first: Mat,
        second: Mat,
        pixelThreshold: Double
    ): MetricComputation {
        val absoluteDifference = scope.mat()
        Core.absdiff(first, second, absoluteDifference)

        val channelDifferences = scope.split(absoluteDifference)
        val maximumChannelDifference = scope.maximum(channelDifferences)

        val mask = scope.mat()
        Core.compare(
            maximumChannelDifference,
            Scalar.all(pixelThreshold),
            mask,
            Core.CMP_GT
        )

        return MetricComputation(
            score = Core.countNonZero(mask).toDouble(),
            mask = mask
        )
    }

    /**
     * Global MAE:
     *
     * mean(|first - second|)
     *
     * The local map contains the mean absolute RGBA error for each pixel.
     */
    private fun computeMeanAbsoluteError(
        scope: MatScope,
        first: Mat,
        second: Mat,
        pixelThreshold: Double
    ): MetricComputation {
        val absoluteDifference = scope.mat()
        Core.absdiff(first, second, absoluteDifference)

        val channelDifferences = scope.split(absoluteDifference)
        val perPixelMae = scope.average(channelDifferences)

        val mask = scope.mat()
        Core.compare(
            perPixelMae,
            Scalar.all(pixelThreshold),
            mask,
            Core.CMP_GT
        )

        return MetricComputation(
            score = Core.mean(perPixelMae).`val`[0],
            mask = mask
        )
    }

    /**
     * Global RMSE:
     *
     * sqrt(mean((first - second)^2))
     *
     * The local map contains the root mean squared RGBA error per pixel.
     */
    private fun computeRootMeanSquaredError(
        scope: MatScope,
        first: Mat,
        second: Mat,
        pixelThreshold: Double
    ): MetricComputation {
        val perPixelMse = createPerPixelMse(
            scope = scope,
            first = first,
            second = second
        )

        val perPixelRmse = scope.mat()
        Core.sqrt(perPixelMse, perPixelRmse)

        val mask = scope.mat()
        Core.compare(
            perPixelRmse,
            Scalar.all(pixelThreshold),
            mask,
            Core.CMP_GT
        )

        val globalMse = Core.mean(perPixelMse).`val`[0]
            .coerceAtLeast(0.0)

        return MetricComputation(
            score = sqrt(globalMse),
            mask = mask
        )
    }

    /**
     * Global PSNR:
     *
     * 10 * log10(255^2 / MSE)
     *
     * PSNR itself is a global value. For highlighting, a local Gaussian-window
     * MSE is used, so regions responsible for a low PSNR are highlighted.
     */
    private fun computePeakSignalToNoiseRatio(
        scope: MatScope,
        first: Mat,
        second: Mat,
        pixelThreshold: Double
    ): MetricComputation {
        val perPixelMse = createPerPixelMse(
            scope = scope,
            first = first,
            second = second
        )

        val globalMse = Core.mean(perPixelMse).`val`[0]
            .coerceAtLeast(0.0)

        val score = if (globalMse <= EPSILON) {
            Double.POSITIVE_INFINITY
        } else {
            10.0 * log10(255.0 * 255.0 / globalMse)
        }

        val localMse = scope.mat()
        Imgproc.GaussianBlur(
            perPixelMse,
            localMse,
            gaussianWindow(perPixelMse),
            GAUSSIAN_SIGMA
        )

        clampMinimumToZero(localMse)

        val localRmse = scope.mat()
        Core.sqrt(localMse, localRmse)

        val mask = scope.mat()
        Core.compare(
            localRmse,
            Scalar.all(pixelThreshold),
            mask,
            Core.CMP_GT
        )

        return MetricComputation(
            score = score,
            mask = mask
        )
    }

    /**
     * Computes SSIM independently for every RGBA channel.
     *
     * The result score is the average channel SSIM.
     * The highlight map uses the minimum channel SSIM so that a strong
     * difference in any channel is not hidden by the other channels.
     */
    private fun computeStructuralSimilarity(
        scope: MatScope,
        first: Mat,
        second: Mat,
        minimumSimilarity: Double
    ): MetricComputation {
        val firstChannels = scope.split(first)
        val secondChannels = scope.split(second)

        require(firstChannels.size == secondChannels.size)

        val window = gaussianWindow(first)
        val channelMaps = firstChannels.indices.map { index ->
            createSsimMap(
                scope = scope,
                first = firstChannels[index],
                second = secondChannels[index],
                window = window
            )
        }

        val score = channelMaps
            .map { Core.mean(it).`val`[0] }
            .average()
            .coerceIn(0.0, 1.0)

        val weakestChannelSimilarity = scope.minimum(channelMaps)

        val mask = scope.mat()
        Core.compare(
            weakestChannelSimilarity,
            Scalar.all(minimumSimilarity),
            mask,
            Core.CMP_LT
        )

        return MetricComputation(
            score = score,
            mask = mask
        )
    }

    /**
     * Computes a global NCC score and a local Gaussian-window NCC map.
     *
     * Global score is averaged across RGBA channels.
     * The local highlight map uses the minimum correlation across channels.
     */
    private fun computeNormalizedCrossCorrelation(
        scope: MatScope,
        first: Mat,
        second: Mat,
        minimumSimilarity: Double
    ): MetricComputation {
        val firstChannels = scope.split(first)
        val secondChannels = scope.split(second)

        require(firstChannels.size == secondChannels.size)

        val window = gaussianWindow(first)

        val channelScores = firstChannels.indices.map { index ->
            computeGlobalNcc(
                scope = scope,
                first = firstChannels[index],
                second = secondChannels[index]
            )
        }

        val channelMaps = firstChannels.indices.map { index ->
            createLocalNccMap(
                scope = scope,
                first = firstChannels[index],
                second = secondChannels[index],
                window = window
            )
        }

        val weakestChannelSimilarity = scope.minimum(channelMaps)

        val mask = scope.mat()
        Core.compare(
            weakestChannelSimilarity,
            Scalar.all(minimumSimilarity),
            mask,
            Core.CMP_LT
        )

        return MetricComputation(
            score = channelScores.average().coerceIn(-1.0, 1.0),
            mask = mask
        )
    }

    /**
     * Produces a single-channel map:
     *
     * mean((firstRGBA - secondRGBA)^2)
     */
    private fun createPerPixelMse(
        scope: MatScope,
        first: Mat,
        second: Mat
    ): Mat {
        val difference = scope.mat()
        Core.subtract(first, second, difference)

        val squaredDifference = scope.mat()
        Core.multiply(
            difference,
            difference,
            squaredDifference
        )

        val squaredChannels = scope.split(squaredDifference)
        return scope.average(squaredChannels)
    }

    /**
     * Creates the local SSIM map for one channel.
     */
    private fun createSsimMap(
        scope: MatScope,
        first: Mat,
        second: Mat,
        window: Size
    ): Mat {
        val firstSquared = scope.mat()
        val secondSquared = scope.mat()
        val firstSecond = scope.mat()

        Core.multiply(first, first, firstSquared)
        Core.multiply(second, second, secondSquared)
        Core.multiply(first, second, firstSecond)

        val firstMean = scope.mat()
        val secondMean = scope.mat()

        Imgproc.GaussianBlur(
            first,
            firstMean,
            window,
            GAUSSIAN_SIGMA
        )
        Imgproc.GaussianBlur(
            second,
            secondMean,
            window,
            GAUSSIAN_SIGMA
        )

        val firstMeanSquared = scope.mat()
        val secondMeanSquared = scope.mat()
        val firstMeanSecondMean = scope.mat()

        Core.multiply(
            firstMean,
            firstMean,
            firstMeanSquared
        )
        Core.multiply(
            secondMean,
            secondMean,
            secondMeanSquared
        )
        Core.multiply(
            firstMean,
            secondMean,
            firstMeanSecondMean
        )

        val firstVariance = scope.mat()
        val secondVariance = scope.mat()
        val covariance = scope.mat()

        Imgproc.GaussianBlur(
            firstSquared,
            firstVariance,
            window,
            GAUSSIAN_SIGMA
        )
        Core.subtract(
            firstVariance,
            firstMeanSquared,
            firstVariance
        )

        Imgproc.GaussianBlur(
            secondSquared,
            secondVariance,
            window,
            GAUSSIAN_SIGMA
        )
        Core.subtract(
            secondVariance,
            secondMeanSquared,
            secondVariance
        )

        Imgproc.GaussianBlur(
            firstSecond,
            covariance,
            window,
            GAUSSIAN_SIGMA
        )
        Core.subtract(
            covariance,
            firstMeanSecondMean,
            covariance
        )

        /*
         * Floating-point rounding can create extremely small negative
         * variances. Variance itself cannot be negative.
         */
        clampMinimumToZero(firstVariance)
        clampMinimumToZero(secondVariance)

        val luminanceNumerator = scope.mat()
        Core.multiply(
            firstMeanSecondMean,
            Scalar.all(2.0),
            luminanceNumerator
        )
        Core.add(
            luminanceNumerator,
            Scalar.all(SSIM_C1),
            luminanceNumerator
        )

        val structureNumerator = scope.mat()
        Core.multiply(
            covariance,
            Scalar.all(2.0),
            structureNumerator
        )
        Core.add(
            structureNumerator,
            Scalar.all(SSIM_C2),
            structureNumerator
        )

        val numerator = scope.mat()
        Core.multiply(
            luminanceNumerator,
            structureNumerator,
            numerator
        )

        val luminanceDenominator = scope.mat()
        Core.add(
            firstMeanSquared,
            secondMeanSquared,
            luminanceDenominator
        )
        Core.add(
            luminanceDenominator,
            Scalar.all(SSIM_C1),
            luminanceDenominator
        )

        val structureDenominator = scope.mat()
        Core.add(
            firstVariance,
            secondVariance,
            structureDenominator
        )
        Core.add(
            structureDenominator,
            Scalar.all(SSIM_C2),
            structureDenominator
        )

        val denominator = scope.mat()
        Core.multiply(
            luminanceDenominator,
            structureDenominator,
            denominator
        )
        Core.add(
            denominator,
            Scalar.all(EPSILON),
            denominator
        )

        val ssimMap = scope.mat()
        Core.divide(
            numerator,
            denominator,
            ssimMap
        )

        /*
         * SSIM is exposed to the caller as a 0..1 similarity value.
         * Negative theoretical values are treated as complete dissimilarity.
         */
        clampZeroToOne(ssimMap)

        return ssimMap
    }

    /**
     * Computes global normalized cross-correlation for one channel:
     *
     * sum((A - meanA) * (B - meanB))
     * ------------------------------------------------
     * sqrt(sum((A - meanA)^2) * sum((B - meanB)^2))
     */
    private fun computeGlobalNcc(
        scope: MatScope,
        first: Mat,
        second: Mat
    ): Double {
        val firstMean = Core.mean(first).`val`[0]
        val secondMean = Core.mean(second).`val`[0]

        val firstCentered = scope.mat()
        val secondCentered = scope.mat()

        Core.subtract(
            first,
            Scalar.all(firstMean),
            firstCentered
        )
        Core.subtract(
            second,
            Scalar.all(secondMean),
            secondCentered
        )

        val product = scope.mat()
        Core.multiply(
            firstCentered,
            secondCentered,
            product
        )

        val firstSquared = scope.mat()
        val secondSquared = scope.mat()

        Core.multiply(
            firstCentered,
            firstCentered,
            firstSquared
        )
        Core.multiply(
            secondCentered,
            secondCentered,
            secondSquared
        )

        val numerator = Core.sumElems(product).`val`[0]

        val firstEnergy = Core.sumElems(firstSquared).`val`[0]
            .coerceAtLeast(0.0)
        val secondEnergy = Core.sumElems(secondSquared).`val`[0]
            .coerceAtLeast(0.0)

        val denominator = sqrt(firstEnergy * secondEnergy)

        if (denominator <= EPSILON) {
            /*
             * NCC is undefined for constant images.
             *
             * Identical constant channels are treated as perfectly correlated.
             * Different constant channels are treated as uncorrelated.
             */
            val difference = scope.mat()
            Core.subtract(first, second, difference)

            val squaredDifference = scope.mat()
            Core.multiply(
                difference,
                difference,
                squaredDifference
            )

            val mse = Core.mean(squaredDifference).`val`[0]

            return if (mse <= EPSILON) {
                1.0
            } else {
                0.0
            }
        }

        return (numerator / denominator).coerceIn(-1.0, 1.0)
    }

    /**
     * Computes a local normalized cross-correlation map for one channel.
     */
    private fun createLocalNccMap(
        scope: MatScope,
        first: Mat,
        second: Mat,
        window: Size
    ): Mat {
        val firstMean = scope.mat()
        val secondMean = scope.mat()

        Imgproc.GaussianBlur(
            first,
            firstMean,
            window,
            GAUSSIAN_SIGMA
        )
        Imgproc.GaussianBlur(
            second,
            secondMean,
            window,
            GAUSSIAN_SIGMA
        )

        val firstSquared = scope.mat()
        val secondSquared = scope.mat()
        val firstSecond = scope.mat()

        Core.multiply(first, first, firstSquared)
        Core.multiply(second, second, secondSquared)
        Core.multiply(first, second, firstSecond)

        val firstMeanSquared = scope.mat()
        val secondMeanSquared = scope.mat()
        val firstMeanSecondMean = scope.mat()

        Core.multiply(
            firstMean,
            firstMean,
            firstMeanSquared
        )
        Core.multiply(
            secondMean,
            secondMean,
            secondMeanSquared
        )
        Core.multiply(
            firstMean,
            secondMean,
            firstMeanSecondMean
        )

        val firstVariance = scope.mat()
        val secondVariance = scope.mat()
        val covariance = scope.mat()

        Imgproc.GaussianBlur(
            firstSquared,
            firstVariance,
            window,
            GAUSSIAN_SIGMA
        )
        Core.subtract(
            firstVariance,
            firstMeanSquared,
            firstVariance
        )

        Imgproc.GaussianBlur(
            secondSquared,
            secondVariance,
            window,
            GAUSSIAN_SIGMA
        )
        Core.subtract(
            secondVariance,
            secondMeanSquared,
            secondVariance
        )

        Imgproc.GaussianBlur(
            firstSecond,
            covariance,
            window,
            GAUSSIAN_SIGMA
        )
        Core.subtract(
            covariance,
            firstMeanSecondMean,
            covariance
        )

        clampMinimumToZero(firstVariance)
        clampMinimumToZero(secondVariance)

        val denominatorSquared = scope.mat()
        Core.multiply(
            firstVariance,
            secondVariance,
            denominatorSquared
        )
        clampMinimumToZero(denominatorSquared)

        val denominator = scope.mat()
        Core.sqrt(
            denominatorSquared,
            denominator
        )

        val flatWindowMask = scope.mat()
        Core.compare(
            denominator,
            Scalar.all(EPSILON),
            flatWindowMask,
            Core.CMP_LE
        )

        val safeDenominator = scope.mat()
        Core.add(
            denominator,
            Scalar.all(EPSILON),
            safeDenominator
        )

        val nccMap = scope.mat()
        Core.divide(
            covariance,
            safeDenominator,
            nccMap
        )

        /*
         * Correlation is undefined when both local windows are flat.
         * For those areas use similarity of their local means:
         *
         * 1 - |mean1 - mean2| / 255
         */
        val meanDifference = scope.mat()
        Core.absdiff(
            firstMean,
            secondMean,
            meanDifference
        )

        val flatWindowSimilarity = scope.mat()
        Core.multiply(
            meanDifference,
            Scalar.all(-1.0 / 255.0),
            flatWindowSimilarity
        )
        Core.add(
            flatWindowSimilarity,
            Scalar.all(1.0),
            flatWindowSimilarity
        )
        clampZeroToOne(flatWindowSimilarity)

        flatWindowSimilarity.copyTo(
            nccMap,
            flatWindowMask
        )

        clampMinusOneToOne(nccMap)

        return nccMap
    }

    private fun gaussianWindow(source: Mat): Size {
        fun oddSizeAtMost(value: Int): Int {
            val capped = minOf(
                DEFAULT_WINDOW_SIZE,
                value.coerceAtLeast(1)
            )

            return if (capped % 2 == 0) {
                (capped - 1).coerceAtLeast(1)
            } else {
                capped
            }
        }

        return Size(
            oddSizeAtMost(source.cols()).toDouble(),
            oddSizeAtMost(source.rows()).toDouble()
        )
    }

    private fun clampMinimumToZero(mat: Mat) {
        Imgproc.threshold(
            mat,
            mat,
            0.0,
            0.0,
            Imgproc.THRESH_TOZERO
        )
    }

    private fun clampZeroToOne(mat: Mat) {
        Imgproc.threshold(
            mat,
            mat,
            1.0,
            1.0,
            Imgproc.THRESH_TRUNC
        )

        Imgproc.threshold(
            mat,
            mat,
            0.0,
            0.0,
            Imgproc.THRESH_TOZERO
        )
    }

    private fun clampMinusOneToOne(mat: Mat) {
        Imgproc.threshold(
            mat,
            mat,
            1.0,
            1.0,
            Imgproc.THRESH_TRUNC
        )

        /*
         * Clamp values below -1:
         *
         * value += 1
         * value = max(value, 0)
         * value -= 1
         */
        Core.add(
            mat,
            Scalar.all(1.0),
            mat
        )

        Imgproc.threshold(
            mat,
            mat,
            0.0,
            0.0,
            Imgproc.THRESH_TOZERO
        )

        Core.subtract(
            mat,
            Scalar.all(1.0),
            mat
        )
    }

    private data class MetricComputation(
        val score: Double,
        val mask: Mat
    )

    private data class BitmapHolder(
        val bitmap: Bitmap,
        val owned: Boolean
    ) {
        fun recycleIfOwned() {
            if (owned && !bitmap.isRecycled) {
                bitmap.recycle()
            }
        }
    }

    private fun Bitmap.asSoftwareArgb8888(): BitmapHolder {
        if (config == Bitmap.Config.ARGB_8888) {
            return BitmapHolder(
                bitmap = this,
                owned = false
            )
        }

        val converted = copy(
            Bitmap.Config.ARGB_8888,
            false
        ) ?: throw IllegalArgumentException(
            "Could not convert bitmap to ARGB_8888"
        )

        return BitmapHolder(
            bitmap = converted,
            owned = true
        )
    }

    /**
     * Tracks every allocated Mat and releases them in reverse order.
     */
    private class MatScope {

        private val ownedMats = ArrayList<Mat>()

        fun mat(): Mat = own(Mat())

        fun own(mat: Mat): Mat {
            if (ownedMats.none { it === mat }) {
                ownedMats += mat
            }

            return mat
        }

        fun split(source: Mat): List<Mat> {
            val channels = ArrayList<Mat>(source.channels())
            Core.split(source, channels)
            channels.forEach(::own)
            return channels
        }

        fun average(matrices: List<Mat>): Mat {
            require(matrices.isNotEmpty()) {
                "At least one matrix is required"
            }

            val result = own(matrices.first().clone())

            for (index in 1 until matrices.size) {
                Core.add(
                    result,
                    matrices[index],
                    result
                )
            }

            Core.multiply(
                result,
                Scalar.all(1.0 / matrices.size),
                result
            )

            return result
        }

        fun maximum(matrices: List<Mat>): Mat {
            require(matrices.isNotEmpty()) {
                "At least one matrix is required"
            }

            val result = own(matrices.first().clone())

            for (index in 1 until matrices.size) {
                Core.max(
                    result,
                    matrices[index],
                    result
                )
            }

            return result
        }

        fun minimum(matrices: List<Mat>): Mat {
            require(matrices.isNotEmpty()) {
                "At least one matrix is required"
            }

            val result = own(matrices.first().clone())

            for (index in 1 until matrices.size) {
                Core.min(
                    result,
                    matrices[index],
                    result
                )
            }

            return result
        }

        fun releaseAll() {
            for (index in ownedMats.lastIndex downTo 0) {
                runCatching {
                    ownedMats[index].release()
                }
            }

            ownedMats.clear()
        }
    }

    private inline fun <T> withMatScope(
        block: MatScope.() -> T
    ): T {
        val scope = MatScope()

        return try {
            scope.block()
        } finally {
            scope.releaseAll()
        }
    }

    /**
     * Meaning of [score] depends on the selected metric:
     *
     * - AE: number of pixels whose error exceeds the requested threshold.
     * - MAE: mean absolute error in the 0..255 range.
     * - RMSE: root mean squared error in the 0..255 range.
     * - PSNR: peak signal-to-noise ratio in dB; positive infinity means identical images.
     * - NCC: normalized cross-correlation in the -1..1 range.
     * - SSIM: structural similarity in the 0..1 range.
     */
    data class ComparisonResult(
        val score: Double,
        val highlightedBitmap: Bitmap
    )
}