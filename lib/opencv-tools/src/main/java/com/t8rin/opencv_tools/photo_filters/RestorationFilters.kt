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

package com.t8rin.opencv_tools.photo_filters

import android.graphics.Bitmap
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import org.opencv.photo.Photo
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

object RestorationFilters {

    suspend fun contrastPreservingGrayscale(
        bitmap: Bitmap,
        colorContrast: Float,
        contrast: Float
    ): Bitmap = PhotoFilterRenderer.render(bitmap, 1f) { rgb ->
        val luminance = gray(rgb)
        val mixed = mat()
        val balance = colorContrast.coerceIn(0f, 1f).toDouble()
        if (balance == 0.0 || min(rgb.cols(), rgb.rows()) < 3) {
            luminance.copyTo(mixed)
        } else {
            val size = max(rgb.cols(), rgb.rows())
            val sample = if (size <= 768) rgb else resize(
                rgb,
                (rgb.cols() * 768.0 / size).roundToInt().coerceAtLeast(1),
                (rgb.rows() * 768.0 / size).roundToInt().coerceAtLeast(1), Imgproc.INTER_AREA
            )
            // Fit on a bounded sample and transfer the local grayscale difference at full size.
            val bgr = convert(sample, Imgproc.COLOR_RGB2BGR)
            val dec = mat()
            Photo.decolor(bgr, dec, mat())
            checkpoint()
            val delta = subtract(floats(dec), floats(gray(sample)))
            val correction = resize(delta, rgb.cols(), rgb.rows(), Imgproc.INTER_LINEAR)
            Core.addWeighted(floats(luminance), 1.0, correction, balance, 0.0, mixed)
            mixed.convertTo(mixed, CvType.CV_8U, 255.0)
        }
        val gain = contrast.coerceIn(0.4f, 2.5f).toDouble()
        mixed.convertTo(mixed, CvType.CV_8U, gain, 128.0 * (1.0 - gain))
        convert(mixed, Imgproc.COLOR_GRAY2RGB)
    }

    suspend fun guidedFilter(
        bitmap: Bitmap,
        radius: Float,
        epsilon: Float,
        guide: Int,
        strength: Float
    ): Bitmap = PhotoFilterRenderer.render(bitmap, strength, maxSide = 2048) { rgb ->
        val window = rgb.radius(radius.coerceIn(1f, 80f))
        val regularization = epsilon.coerceIn(0.001f, 0.2f).toDouble().pow(2)
        val grayscale = if (guide == 0) floats(gray(rgb)) else null
        val filtered = List(3) { i ->
            val output = mat()
            work {
                checkpoint()
                val source = floats(channel(rgb, i))
                val reference = grayscale ?: source
                val mean = box(reference, window)
                val sourceMean = box(source, window)
                val variance =
                    subtract(box(multiply(reference, reference), window), multiply(mean, mean))
                Core.max(variance, Scalar.all(0.0), variance)
                Core.add(variance, Scalar.all(regularization), variance)
                val covariance =
                    subtract(box(multiply(reference, source), window), multiply(mean, sourceMean))
                val a = divide(covariance, variance)
                val b = subtract(sourceMean, multiply(a, mean))
                add(multiply(box(a, window), reference), box(b, window)).convertTo(
                    output,
                    CvType.CV_8U,
                    255.0
                )
            }
            output
        }
        mat().also { Core.merge(filtered, it) }
    }

    suspend fun rollingGuidance(
        bitmap: Bitmap,
        radius: Float,
        sensitivity: Float,
        iterations: Int,
        strength: Float
    ): Bitmap = PhotoFilterRenderer.render(bitmap, strength, maxSide = 1200) { rgb ->
        val width = rgb.cols()
        val height = rgb.rows()
        val sigma = (radius.coerceIn(1f, 30f) * rgb.referenceScale()).coerceAtLeast(0.75)
        val stride = (sigma * 0.65).roundToInt().coerceAtLeast(1)
        val offsets = (-2..2).flatMap { y -> (-2..2).map { x -> x * stride to y * stride } }
        val spatialWeights = offsets.map { (x, y) ->
            exp(-(x * x + y * y) / (2.0 * sigma * sigma)).toFloat()
        }
        val rangeSigma = sensitivity.coerceIn(0.01f, 0.5f) * 255f
        val rangeWeights = FloatArray(766) { i -> exp(-(i * i) / (6f * rangeSigma * rangeSigma)) }
        val source = ByteArray(width * height * 3)
        rgb.get(0, 0, source)
        var guide = ByteArray(source.size)
        gaussian(rgb, sigma).get(0, 0, guide)
        var output = ByteArray(source.size)
        repeat(iterations.coerceIn(1, 6)) {
            for (y in 0 until height) {
                checkpoint()
                val row = y * width * 3
                for (x in 0 until width) {
                    val center = row + x * 3
                    val r = guide[center].toInt() and 255
                    val g = guide[center + 1].toInt() and 255
                    val b = guide[center + 2].toInt() and 255
                    var total = 0f
                    var red = 0f
                    var green = 0f
                    var blue = 0f
                    for (i in offsets.indices) {
                        val (dx, dy) = offsets[i]
                        val index = ((y + dy).coerceIn(0, height - 1) * width + (x + dx).coerceIn(
                            0,
                            width - 1
                        )) * 3
                        val distance = abs(r - (guide[index].toInt() and 255)) +
                                abs(g - (guide[index + 1].toInt() and 255)) +
                                abs(b - (guide[index + 2].toInt() and 255))
                        val weight = spatialWeights[i] * rangeWeights[distance]
                        total += weight
                        red += weight * (source[index].toInt() and 255)
                        green += weight * (source[index + 1].toInt() and 255)
                        blue += weight * (source[index + 2].toInt() and 255)
                    }
                    output[center] = (red / total).roundToInt().toByte()
                    output[center + 1] = (green / total).roundToInt().toByte()
                    output[center + 2] = (blue / total).roundToInt().toByte()
                }
            }
            val previous = guide
            guide = output
            output = previous
        }
        mat(height, width, CvType.CV_8UC3).also { it.put(0, 0, guide) }
    }

    suspend fun nonLocalMeans(
        bitmap: Bitmap,
        luminanceNoise: Float,
        chromaNoise: Float,
        searchWindow: Int,
        strength: Float
    ): Bitmap = PhotoFilterRenderer.render(bitmap, strength, maxSide = 1400) { rgb ->
        val search = searchWindow.coerceIn(7, 23) or 1
        val luminance = luminanceNoise.coerceIn(0f, 25f)
        val chroma = chromaNoise.coerceIn(0f, 25f)
        if (luminance == 0f && chroma == 0f) return@render rgb
        val tile = 384
        val halo = search / 2 + 2
        val result = mat(rgb.rows(), rgb.cols(), CvType.CV_8UC3)
        for (top in 0 until rgb.rows() step tile) {
            for (left in 0 until rgb.cols() step tile) {
                checkpoint()
                work {
                    val bottom = min(top + tile, rgb.rows())
                    val right = min(left + tile, rgb.cols())
                    val y0 = max(0, top - halo)
                    val x0 = max(0, left - halo)
                    val patch = roi(
                        rgb,
                        y0,
                        min(bottom + halo, rgb.rows()),
                        x0,
                        min(right + halo, rgb.cols())
                    )
                    val denoised = mat()
                    Photo.fastNlMeansDenoisingColored(
                        convert(patch, Imgproc.COLOR_RGB2BGR),
                        denoised, luminance, chroma, 5, search
                    )
                    checkpoint()
                    val core = roi(denoised, top - y0, bottom - y0, left - x0, right - x0)
                    Imgproc.cvtColor(
                        core,
                        roi(result, top, bottom, left, right),
                        Imgproc.COLOR_BGR2RGB
                    )
                }
            }
        }
        result
    }

    suspend fun adaptiveBinarization(
        bitmap: Bitmap,
        algorithm: Int,
        radius: Float,
        coefficient: Float,
        dynamicRange: Float,
        ink: Int,
        paper: Int,
        invert: Boolean
    ): Bitmap = PhotoFilterRenderer.render(bitmap, 1f) { rgb ->
        val gray = floats(gray(rgb))
        val window = rgb.radius(radius.coerceIn(1f, 80f))
        val mean = box(gray, window)
        val variance = subtract(box(multiply(gray, gray), window), multiply(mean, mean))
        Core.max(variance, Scalar.all(0.0), variance)
        Core.sqrt(variance, variance)
        val wolf = algorithm == 2
        val maxDeviation = if (wolf) Core.minMaxLoc(variance).maxVal.coerceAtLeast(1e-6) else 1.0
        val minValue = if (wolf) Core.minMaxLoc(gray).minVal else 0.0
        val range = dynamicRange.coerceIn(0.02f, 1f)
        val k = coefficient.coerceIn(-1f, 1f)
        val pixels = FloatArray(gray.cols())
        val means = FloatArray(gray.cols())
        val stds = FloatArray(gray.cols())
        val output = ByteArray(gray.cols() * 3)
        val result = mat(gray.rows(), gray.cols(), CvType.CV_8UC3)
        for (y in 0 until gray.rows()) {
            checkpoint()
            gray.get(y, 0, pixels)
            mean.get(y, 0, means)
            variance.get(y, 0, stds)
            for (x in pixels.indices) {
                val avg = means[x]
                val sd = stds[x]
                // Niblack and NICK use -k so positive values work for dark ink in every mode.
                val threshold = when (algorithm) {
                    1 -> (avg - k * sd).toDouble()
                    2 -> avg + k * (sd / maxDeviation - 1.0) * (avg - minValue)
                    3 -> (avg - k * sqrt(2f * sd * sd + avg * avg)).toDouble()
                    else -> (avg * (1f + k * (sd / range - 1f))).toDouble()
                }
                val color = if ((pixels[x] < threshold) xor invert) ink else paper
                output[x * 3] = (color shr 16).toByte()
                output[x * 3 + 1] = (color shr 8).toByte()
                output[x * 3 + 2] = color.toByte()
            }
            result.put(y, 0, output)
        }
        result
    }

    suspend fun rgbChannelRegistration(
        bitmap: Bitmap,
        maxShift: Float,
        confidence: Float,
        strength: Float
    ): Bitmap = PhotoFilterRenderer.render(bitmap, 1f) { rgb ->
        if (min(rgb.cols(), rgb.rows()) < 8 || strength <= 0f) return@render rgb
        val scale = (768.0 / max(rgb.cols(), rgb.rows())).coerceAtMost(1.0)
        val sample = if (scale == 1.0) rgb else resize(
            rgb,
            (rgb.cols() * scale).roundToInt().coerceAtLeast(3),
            (rgb.rows() * scale).roundToInt().coerceAtLeast(3), Imgproc.INTER_AREA
        )
        val window = mat()
        Imgproc.createHanningWindow(window, sample.size(), CvType.CV_32F)
        val guide = channel(sample, 1)
        val limit = maxShift.coerceIn(1f, 80f) * rgb.referenceScale()
        val correction = strength.coerceIn(0f, 1f)
        val channels = List(3) { i ->
            val original = channel(rgb, i)
            if (i == 1) original else {
                val output = mat()
                work {
                    checkpoint()
                    val reference = gradientMagnitude(guide)
                    val target = gradientMagnitude(channel(sample, i))
                    val response = DoubleArray(1)
                    val offset = Imgproc.phaseCorrelate(reference, target, window, response)
                    val dx = offset.x * rgb.cols() / sample.cols()
                    val dy = offset.y * rgb.rows() / sample.rows()
                    if (response[0] < confidence.coerceIn(0.05f, 0.95f) ||
                        !dx.isFinite() || !dy.isFinite() || abs(dx) > limit || abs(dy) > limit
                    ) original.copyTo(output) else {
                        val matrix = mat(2, 3, CvType.CV_64F)
                        matrix.put(0, 0, 1.0, 0.0, -dx * correction, 0.0, 1.0, -dy * correction)
                        Imgproc.warpAffine(
                            original, output, matrix, rgb.size(), Imgproc.INTER_LINEAR,
                            Core.BORDER_REPLICATE
                        )
                    }
                }
                output
            }
        }
        mat().also { Core.merge(channels, it) }
    }

    private fun PhotoFilterScope.gradientMagnitude(input: Mat): Mat {
        val dx = mat()
        val dy = mat()
        Imgproc.Sobel(input, dx, CvType.CV_32F, 1, 0)
        Imgproc.Sobel(input, dy, CvType.CV_32F, 0, 1)
        return mat().also { Core.magnitude(dx, dy, it) }
    }

    suspend fun domainTransform(
        bitmap: Bitmap,
        radius: Float,
        sensitivity: Float,
        iterations: Int,
        strength: Float
    ): Bitmap = PhotoFilterRenderer.render(bitmap, strength, maxSide = 1800) { rgb ->
        val width = rgb.cols()
        val height = rgb.rows()
        val source = ByteArray(width * height * 3)
        rgb.get(0, 0, source)
        val output = FloatArray(source.size) { (source[it].toInt() and 255).toFloat() }
        val horizontal = ShortArray(width * height)
        val vertical = ShortArray(width * height)
        fun distance(a: Int, b: Int): Short {
            val ia = a * 3
            val ib = b * 3
            return (abs((source[ia].toInt() and 255) - (source[ib].toInt() and 255)) +
                    abs((source[ia + 1].toInt() and 255) - (source[ib + 1].toInt() and 255)) +
                    abs((source[ia + 2].toInt() and 255) - (source[ib + 2].toInt() and 255))).toShort()
        }
        for (y in 0 until height) {
            checkpoint()
            for (x in 0 until width) {
                val p = y * width + x
                if (x > 0) horizontal[p] = distance(p, p - 1)
                if (y > 0) vertical[p] = distance(p, p - width)
            }
        }
        val spatial = (radius.coerceIn(1f, 100f) * rgb.referenceScale()).coerceAtLeast(0.25)
        val range = sensitivity.coerceIn(0.01f, 0.6f) * 255.0
        val passes = iterations.coerceIn(1, 5)
        repeat(passes) { pass ->
            val sigma =
                spatial * sqrt(3.0) * (1 shl (passes - pass - 1)) / sqrt((1 shl (2 * passes)) - 1.0)
            val weights =
                FloatArray(766) { exp(-sqrt(2.0) / sigma * (1.0 + it * spatial / range)).toFloat() }

            fun blend(to: Int, from: Int, weight: Float) {
                val dest = to * 3
                val src = from * 3
                for (channel in 0..2) {
                    output[dest + channel] += weight * (output[src + channel] - output[dest + channel])
                }
            }
            for (y in 0 until height) {
                checkpoint()
                val row = y * width
                for (x in 1 until width) {
                    val p = row + x
                    blend(p, p - 1, weights[horizontal[p].toInt()])
                }
                for (x in width - 2 downTo 0) {
                    val p = row + x
                    blend(p, p + 1, weights[horizontal[p + 1].toInt()])
                }
            }
            for (y in 1 until height) {
                checkpoint()
                for (x in 0 until width) {
                    val p = y * width + x
                    blend(p, p - width, weights[vertical[p].toInt()])
                }
            }
            for (y in height - 2 downTo 0) {
                checkpoint()
                for (x in 0 until width) {
                    val p = y * width + x
                    blend(p, p + width, weights[vertical[p + width].toInt()])
                }
            }
        }
        for (y in 0 until height) {
            checkpoint()
            val start = y * width * 3
            for (i in start until start + width * 3) source[i] = output[i].roundToInt().toByte()
        }
        mat(height, width, CvType.CV_8UC3).also { it.put(0, 0, source) }
    }
}
