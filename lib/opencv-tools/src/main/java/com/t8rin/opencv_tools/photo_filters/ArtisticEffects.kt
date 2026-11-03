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
import org.opencv.core.Size
import org.opencv.geometry.Geometry
import org.opencv.imgproc.Imgproc
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.tanh
import kotlin.random.Random

object ArtisticEffects {

    suspend fun xdog(
        bitmap: Bitmap,
        sigma: Float,
        ratio: Float,
        weight: Float,
        threshold: Float,
        sharpness: Float,
        ink: Int,
        paper: Int,
        strength: Float
    ): Bitmap = PhotoFilterRenderer.render(bitmap, strength) { rgb ->
        val grayscale = floats(gray(rgb))
        val fineScale = max(0.35, rgb.referenceScale() * sigma.coerceIn(0.5f, 12f))
        val fine = gaussian(grayscale, fineScale)
        val coarse = gaussian(grayscale, fineScale * ratio.coerceIn(1.1f, 4f))
        val response = mat()
        val gain = weight.coerceIn(1f, 30f).toDouble()
        Core.addWeighted(fine, 1.0 + gain, coarse, -gain, 0.0, response)
        colorField(response, ink, paper) { value ->
            (1.0 + tanh(
                (value - threshold.coerceIn(-0.5f, 1f)) *
                        sharpness.coerceIn(1f, 60f).toDouble()
            )).coerceIn(0.0, 1.0).toFloat()
        }
    }

    suspend fun signedDistanceField(
        bitmap: Bitmap,
        threshold: Float,
        distance: Float,
        mode: Int,
        spacing: Float,
        insideColor: Int,
        outsideColor: Int,
        invert: Boolean,
        alphaMask: Boolean
    ): Bitmap = PhotoFilterRenderer.render(bitmap, 1f, alphaAsInput = alphaMask) { source ->
        val mask = mat()
        val luminance = if (alphaMask) source else gray(source)
        Imgproc.threshold(
            luminance, mask, threshold.coerceIn(0f, 1f) * 255.0, 255.0,
            if (invert) Imgproc.THRESH_BINARY_INV else Imgproc.THRESH_BINARY
        )
        val filled = Core.countNonZero(mask).toLong()
        if (filled == 0L || filled == mask.total()) {
            val color = if (mode == 0 && filled != 0L) insideColor else outsideColor
            val rgb = Scalar(
                ((color ushr 16) and 255).toDouble(),
                ((color ushr 8) and 255).toDouble(), (color and 255).toDouble()
            )
            return@render mat(source.rows(), source.cols(), CvType.CV_8UC3).also { it.setTo(rgb) }
        }
        val inverse = mat()
        Core.bitwise_not(mask, inverse)
        val inside = mat()
        val outside = mat()
        Imgproc.distanceTransform(mask, inside, Geometry.DIST_L2, Imgproc.DIST_MASK_5)
        checkpoint()
        Imgproc.distanceTransform(inverse, outside, Geometry.DIST_L2, Imgproc.DIST_MASK_5)
        val signed = subtract(inside, outside)
        val span = source.radius(distance.coerceIn(1f, 180f)).toFloat()
        val interval = source.radius(spacing.coerceIn(1f, 60f)).toFloat()
        colorField(signed, outsideColor, insideColor) { value ->
            when (mode) {
                1 -> exp(-abs(value) / span)
                2 -> (0.5f + 0.5f * cos(2.0 * PI * value / interval).toFloat()) * exp(-abs(value) / span)
                else -> (0.5f + value / (2f * span)).coerceIn(0f, 1f)
            }
        }
    }

    suspend fun capillaryInkBleed(
        bitmap: Bitmap,
        wetness: Float,
        direction: Float,
        spread: Float,
        roughness: Float,
        steps: Int,
        seed: Int,
        strength: Float
    ): Bitmap = PhotoFilterRenderer.render(bitmap, strength, maxSide = 1200) { rgb ->
        val width = rgb.cols()
        val height = rgb.rows()
        val original = ByteArray(width * height * 3)
        rgb.get(0, 0, original)
        var pigment = FloatArray(original.size) { 1f - (original[it].toInt() and 255) / 255f }
        var next = FloatArray(original.size)
        val random = Random(seed)
        val paper = FloatArray(width * height) { random.nextFloat() }
        val radians = Math.toRadians(direction.toDouble())
        val vx = cos(radians)
        val vy = sin(radians)
        val length = rgb.radius(spread.coerceIn(1f, 16f))
        val humidity = wetness.coerceIn(0f, 1f)
        val texture = roughness.coerceIn(0f, 1f)
        val count = steps.coerceIn(1, 12)
        for (step in 0 until count) {
            val diffusion = humidity * (1f - 0.8f * step / count)
            val jump = max(1.0, length / kotlin.math.sqrt(count.toDouble()))
            for (y in 0 until height) {
                checkpoint()
                for (x in 0 until width) {
                    val i = y * width + x
                    val jitter = (paper[i] - 0.5) * texture
                    val dx = ((vx - vy * jitter) * jump).roundToInt()
                    val dy = ((vy + vx * jitter) * jump).roundToInt()
                    val forward = ((y + dy).coerceIn(0, height - 1) * width + (x + dx).coerceIn(
                        0,
                        width - 1
                    )) * 3
                    val back = ((y - dy).coerceIn(0, height - 1) * width + (x - dx).coerceIn(
                        0,
                        width - 1
                    )) * 3
                    val amount = diffusion * (0.32f + 0.26f * paper[i])
                    for (c in 0..2) {
                        val value = pigment[i * 3 + c]
                        val a = pigment[forward + c]
                        val b = pigment[back + c]
                        val deposit = 0.22f * max(0f, max(a, b) - value)
                        next[i * 3 + c] =
                            (value + amount * ((a + b) * 0.5f - value + deposit)).coerceIn(0f, 1f)
                    }
                }
            }
            val previous = pigment
            pigment = next
            next = previous
        }
        val pixels = ByteArray(width * 3)
        val output = mat(height, width, CvType.CV_8UC3)
        for (y in 0 until height) {
            checkpoint()
            for (x in 0 until width) {
                val i = y * width + x
                val paperShade = 1f - texture * (0.015f + 0.075f * paper[i])
                for (c in 0..2) {
                    pixels[x * 3 + c] =
                        ((1f - pigment[i * 3 + c]) * paperShade * 255f).roundToInt().toByte()
                }
            }
            output.put(y, 0, pixels)
        }
        output
    }

    suspend fun layeredPaperCut(
        bitmap: Bitmap,
        layers: Int,
        smoothness: Float,
        depth: Float,
        lightAngle: Float,
        grain: Float,
        strength: Float
    ): Bitmap = PhotoFilterRenderer.render(bitmap, strength, maxSide = 1800) { rgb ->
        val count = layers.coerceIn(2, 12)
        val values = gaussian(gray(rgb), rgb.referenceScale() * smoothness.coerceIn(0.2f, 12f))
        val offset = rgb.referenceScale() * depth.coerceIn(1f, 22f)
        val angle = Math.toRadians(lightAngle.toDouble())
        val transform = mat(2, 3, CvType.CV_64F)
        transform.put(0, 0, 1.0, 0.0, offset * cos(angle), 0.0, 1.0, offset * sin(angle))
        val result = mat(rgb.rows(), rgb.cols(), CvType.CV_8UC3)
        result.setTo(Core.mean(rgb))
        val upper = mat()
        val exact = mat()
        val lower = mat()
        val cast = mat()
        val random = Random(0)
        val output = ByteArray(rgb.cols() * 3)
        val mask = ByteArray(rgb.cols())
        val shadows = ByteArray(rgb.cols())
        val edge = FloatArray(rgb.cols())
        val edges = mat()
        for (i in 0 until count) {
            checkpoint()
            val threshold = 256.0 * i / count - 1
            Imgproc.threshold(values, upper, threshold, 255.0, Imgproc.THRESH_BINARY)
            Imgproc.threshold(
                values,
                lower,
                256.0 * (i + 1) / count - 1,
                255.0,
                Imgproc.THRESH_BINARY_INV
            )
            Core.bitwise_and(upper, lower, exact)
            if (Core.countNonZero(exact) == 0) continue
            val tone = Core.mean(rgb, exact).`val`
            Imgproc.warpAffine(
                upper, cast, transform, values.size(), Imgproc.INTER_LINEAR,
                Core.BORDER_CONSTANT, Scalar.all(0.0)
            )
            Imgproc.GaussianBlur(cast, cast, Size(0.0, 0.0), max(0.35, offset * 0.42))
            Imgproc.distanceTransform(upper, edges, Geometry.DIST_L2, Imgproc.DIST_MASK_5)
            for (y in 0 until rgb.rows()) {
                checkpoint()
                upper.get(y, 0, mask)
                cast.get(y, 0, shadows)
                edges.get(y, 0, edge)
                result.get(y, 0, output)
                for (x in mask.indices) {
                    val lit = mask[x].toInt() != 0
                    val shade = if (lit) {
                        val bevel = (edge[x] / max(1.0, offset)).coerceIn(0.0, 1.0)
                        0.82 + bevel * 0.18
                    } else 1.0 - (shadows[x].toInt() and 255) / 255.0 * 0.3
                    val texture =
                        if (lit) (random.nextFloat() - 0.5) * grain.coerceIn(0f, 1f) * 14 else 0.0
                    for (c in 0..2) {
                        val current =
                            if (lit) tone[c] else (output[x * 3 + c].toInt() and 255).toDouble()
                        output[x * 3 + c] =
                            (current * shade + texture).roundToInt().coerceIn(0, 255).toByte()
                    }
                }
                result.put(y, 0, output)
            }
        }
        result
    }

    suspend fun anisotropicKuwahara(
        bitmap: Bitmap,
        radius: Float,
        anisotropy: Float,
        selectivity: Float,
        strength: Float
    ): Bitmap = PhotoFilterRenderer.render(bitmap, strength, maxSide = 1100) { rgb ->
        val width = rgb.cols()
        val height = rgb.rows()
        val tensors = structureTensor(rgb, max(0.65, rgb.referenceScale() * radius * 0.4))
        val source = ByteArray(width * height * 3)
        rgb.get(0, 0, source)
        val pixels = ByteArray(width * 3)
        val result = mat(height, width, CvType.CV_8UC3)
        val range = (rgb.referenceScale() * radius.coerceIn(1f, 24f)).coerceAtLeast(1.0)
        val stretch = anisotropy.coerceIn(0f, 1f)
        val falloff = selectivity.coerceIn(1f, 24f)
        val a = FloatArray(width)
        val b = FloatArray(width)
        val c = FloatArray(width)
        val sums = FloatArray(24)
        val taps = (-2..2).flatMap { y ->
            (-2..2).filter { x -> x * x + y * y <= 5 }.map { x ->
                // Store overlapping quadrant membership explicitly.
                val quadrants = (0..3).filter {
                    (if (it and 1 == 0) x >= 0 else x <= 0) &&
                            (if (it and 2 == 0) y >= 0 else y <= 0)
                }.toIntArray()
                KuwaharaTap(x * 0.5, y * 0.5, exp(-(x * x + y * y) * 0.24f), quadrants)
            }
        }
        for (y in 0 until height) {
            checkpoint()
            tensors[0].get(y, 0, a)
            tensors[1].get(y, 0, b)
            tensors[2].get(y, 0, c)
            for (x in 0 until width) {
                sums.fill(0f)
                val direction = 0.5 * atan2(2.0 * b[x], (a[x] - c[x]).toDouble()) + PI / 2
                val coherence = (hypot((a[x] - c[x]).toDouble(), 2.0 * b[x]) /
                        (a[x] + c[x] + 1e-6)).coerceIn(0.0, 1.0)
                val aspect = 1 + coherence * stretch * 1.6
                val dx = cos(direction) * range
                val dy = sin(direction) * range
                for (tap in taps) {
                    val sx = (x + dx * tap.x * aspect - dy * tap.y / aspect).roundToInt()
                        .coerceIn(0, width - 1)
                    val sy = (y + dy * tap.x * aspect + dx * tap.y / aspect).roundToInt()
                        .coerceIn(0, height - 1)
                    val i = (sy * width + sx) * 3
                    val r = (source[i].toInt() and 255).toFloat()
                    val g = (source[i + 1].toInt() and 255).toFloat()
                    val bl = (source[i + 2].toInt() and 255).toFloat()
                    val luma = (0.2126f * r + 0.7152f * g + 0.0722f * bl) / 255f
                    for (sector in tap.quadrants) {
                        val index = sector * 6
                        sums[index] += tap.weight
                        sums[index + 1] += r * tap.weight
                        sums[index + 2] += g * tap.weight
                        sums[index + 3] += bl * tap.weight
                        sums[index + 4] += luma * tap.weight
                        sums[index + 5] += luma * luma * tap.weight
                    }
                }
                var total = 0f
                var red = 0f
                var green = 0f
                var blue = 0f
                for (i in 0..3) {
                    val k = i * 6
                    val count = sums[k]
                    val mean = sums[k + 4] / count
                    val variance = max(0f, sums[k + 5] / count - mean * mean)
                    val weight = 1f / (1f + variance * 200f * falloff)
                    total += weight
                    red += sums[k + 1] / count * weight
                    green += sums[k + 2] / count * weight
                    blue += sums[k + 3] / count * weight
                }
                pixels[x * 3] = (red / total).roundToInt().toByte()
                pixels[x * 3 + 1] = (green / total).roundToInt().toByte()
                pixels[x * 3 + 2] = (blue / total).roundToInt().toByte()
            }
            result.put(y, 0, pixels)
        }
        result
    }

    suspend fun contourHatching(
        bitmap: Bitmap,
        spacing: Float,
        width: Float,
        angle: Float,
        contrast: Float,
        ink: Int,
        paper: Int,
        strength: Float
    ): Bitmap = PhotoFilterRenderer.render(bitmap, strength) { rgb ->
        val pitch = (rgb.referenceScale() * spacing.coerceIn(2f, 32f)).coerceAtLeast(2.0)
        val soft = gaussian(floats(gray(rgb)), max(0.25, pitch * 0.18))
        val direction = Math.toRadians(angle.toDouble())
        val nx = sin(direction)
        val ny = cos(direction)
        val gain = contrast.coerceIn(0.5f, 3f)
        val pen = width.coerceIn(0.1f, 0.9f)
        colorField(soft, ink, paper) { value, x, y ->
            val shadow = ((1.0 - value) * gain).coerceIn(0.0, 1.0)
            fun line(coordinate: Double, depth: Double): Double {
                val phase = coordinate / pitch
                val distance = abs(phase - floor(phase + 0.5))
                val half = depth * pen * pitch * 0.5
                val d = distance * pitch
                return 1.0 - (half + 0.5 - d).coerceIn(0.0, 1.0) +
                        (0.5 - half - d).coerceIn(0.0, 1.0)
            }

            val first = line(x * nx + y * ny + 0.27 * value * pitch, shadow)
            val second = if (shadow > 0.52) {
                line(x * ny - y * nx - 0.21 * value * pitch, (shadow - 0.52) / 0.48)
            } else 1.0
            (first * second * (1 - 0.15 * shadow)).toFloat()
        }
    }

    private data class KuwaharaTap(
        val x: Double,
        val y: Double,
        val weight: Float,
        val quadrants: IntArray
    )

    internal fun PhotoFilterScope.structureTensor(rgb: Mat, sigma: Double): List<Mat> {
        val gray = floats(gray(rgb))
        val dx = mat()
        val dy = mat()
        Imgproc.Sobel(gray, dx, CvType.CV_32F, 1, 0)
        Imgproc.Sobel(gray, dy, CvType.CV_32F, 0, 1)
        return listOf(
            gaussian(multiply(dx, dx), sigma), gaussian(multiply(dx, dy), sigma),
            gaussian(multiply(dy, dy), sigma)
        )
    }

    private fun PhotoFilterScope.colorField(
        field: Mat,
        low: Int,
        high: Int,
        map: (Float) -> Float
    ): Mat = colorField(field, low, high) { value, _, _ -> map(value) }

    private inline fun PhotoFilterScope.colorField(
        field: Mat,
        low: Int,
        high: Int,
        map: (Float, Int, Int) -> Float
    ): Mat {
        val data = FloatArray(field.cols())
        val pixels = ByteArray(field.cols() * 3)
        val result = mat(field.rows(), field.cols(), CvType.CV_8UC3)
        val from = IntArray(3) { (low ushr (16 - 8 * it)) and 255 }
        val to = IntArray(3) { (high ushr (16 - 8 * it)) and 255 }
        for (y in 0 until field.rows()) {
            checkpoint()
            field.get(y, 0, data)
            for (x in data.indices) {
                val v = map(data[x], x, y).coerceIn(0f, 1f)
                for (channel in 0..2) {
                    pixels[x * 3 + channel] =
                        (from[channel] + (to[channel] - from[channel]) * v).roundToInt().toByte()
                }
            }
            result.put(y, 0, pixels)
        }
        return result
    }
}
