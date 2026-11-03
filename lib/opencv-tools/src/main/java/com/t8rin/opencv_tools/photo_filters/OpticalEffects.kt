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
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

object OpticalEffects {

    suspend fun filmHalation(
        bitmap: Bitmap,
        threshold: Float,
        knee: Float,
        radius: Float,
        warmth: Float,
        intensity: Float
    ): Bitmap = PhotoFilterRenderer.render(bitmap, 1f) { rgb ->
        val highlights = highlightMap(rgb, threshold, knee)
        val redHalo = gaussian(highlights, highlights.referenceScale() * radius.coerceIn(1f, 40f))
        val greenHalo =
            gaussian(highlights, highlights.referenceScale() * radius.coerceIn(1f, 40f) * 0.43)
        val warm = warmth.coerceIn(0f, 1f)
        compositeLight(
            rgb, listOf(redHalo, greenHalo, greenHalo),
            floatArrayOf(1f, 0.55f - warm * 0.35f, 0.3f - warm * 0.28f), intensity,
            suppressHighlights = true
        )
    }

    suspend fun anamorphicStreaks(
        bitmap: Bitmap,
        threshold: Float,
        angle: Float,
        length: Float,
        width: Float,
        falloff: Float,
        intensity: Float
    ): Bitmap = PhotoFilterRenderer.render(bitmap, 1f) { rgb ->
        val highlights = highlightMap(rgb, threshold, 0.15f)
        val direction = Math.toRadians(angle.toDouble())
        val radius = highlights.radius(length.coerceIn(2f, 180f))
        val sigma = highlights.referenceScale() * width.coerceIn(0.5f, 16f)
        val softened = gaussian(highlights, sigma)
        val dx = cos(direction)
        val dy = sin(direction)
        val halfWidth = kotlin.math.ceil(abs(dx) * radius).toInt()
        val halfHeight = kotlin.math.ceil(abs(dy) * radius).toInt()
        val cols = 2 * halfWidth + 1
        val rows = 2 * halfHeight + 1
        val taps = FloatArray(rows * cols)
        val decay = falloff.coerceIn(0.5f, 8f)
        var total = 0.0
        for (i in -2 * radius..2 * radius) {
            val distance = i * 0.5
            val x = (halfWidth + distance * dx).coerceIn(0.0, cols - 1.0)
            val y = (halfHeight + distance * dy).coerceIn(0.0, rows - 1.0)
            val weight = exp(-abs(distance / radius) * decay)
            val left = x.toInt()
            val top = y.toInt()
            for (row in top..min(top + 1, rows - 1)) {
                for (col in left..min(left + 1, cols - 1)) {
                    taps[row * cols + col] += (weight * (1.0 - abs(x - col)) * (1.0 - abs(y - row))).toFloat()
                }
            }
            total += weight
        }
        for (i in taps.indices) taps[i] = (taps[i] / total).toFloat()
        val kernel = mat(rows, cols, CvType.CV_32F)
        kernel.put(0, 0, taps)
        val streak = mat()
        Imgproc.filter2D(softened, streak, -1, kernel, Point(-1.0, -1.0), 0.0, Core.BORDER_CONSTANT)
        checkpoint()
        compositeLight(rgb, List(3) { streak }, floatArrayOf(0.33f, 0.67f, 1f), intensity)
    }

    suspend fun diffractionPsf(
        bitmap: Bitmap,
        blades: Int,
        rotation: Float,
        radius: Float,
        spectralSpread: Float,
        threshold: Float,
        intensity: Float
    ): Bitmap = PhotoFilterRenderer.render(bitmap, 1f) { rgb ->
        val highlights = highlightMap(rgb, threshold, 0.16f)
        val size = 128
        val aperture = mat(size, size, CvType.CV_32F)
        val row = FloatArray(size)
        val sides = blades.coerceIn(3, 12)
        val segment = 2 * PI / sides
        val angle = Math.toRadians(rotation.toDouble())
        val apothem = 13 * cos(PI / sides)
        for (y in 0 until size) {
            checkpoint()
            for (x in 0 until size) {
                val dx = x - size / 2.0
                val dy = y - size / 2.0
                val theta = kotlin.math.atan2(dy, dx) - angle
                val local = theta - segment * kotlin.math.floor(theta / segment + 0.5)
                row[x] = if (hypot(dx, dy) * cos(local) <= apothem) 1f else 0f
            }
            aperture.put(y, 0, row)
        }
        val spectrum = mat()
        Core.dft(aperture, spectrum, Core.DFT_COMPLEX_OUTPUT)
        val real = channel(spectrum, 0)
        val imaginary = channel(spectrum, 1)
        val psf = add(multiply(real, real), multiply(imaginary, imaginary))
        val shifted = mat(size, size, CvType.CV_32F)
        for (y in 0 until size) {
            checkpoint()
            psf.get((y + size / 2) % size, 0, row)
            val swapped = FloatArray(size) { x -> row[(x + size / 2) % size] }
            shifted.put(y, 0, swapped)
        }
        // Remove the central peak to isolate light scattered away from the source.
        for (y in size / 2 - 2..size / 2 + 2) {
            shifted.get(y, 0, row)
            for (x in size / 2 - 2..size / 2 + 2) row[x] *= 0.12f
            shifted.put(y, 0, row)
        }
        val spread = spectralSpread.coerceIn(0f, 1f)
        val responses = List(3) { channel ->
            checkpoint()
            val wavelength = 1.0 + (1 - channel) * 0.28 * spread
            val kernelRadius = (highlights.radius(radius.coerceIn(3f, 65f)) * wavelength)
                .roundToInt().coerceIn(3, 80)
            val kernelSize = kernelRadius * 2 + 1
            val kernel = resize(shifted, kernelSize, kernelSize, Imgproc.INTER_LINEAR)
            val energy = Core.sumElems(kernel).`val`[0].coerceAtLeast(1.0)
            Core.multiply(kernel, Scalar.all(1.0 / energy), kernel)
            mat().also {
                Imgproc.filter2D(
                    highlights,
                    it,
                    -1,
                    kernel,
                    Point(-1.0, -1.0),
                    0.0,
                    Core.BORDER_CONSTANT
                )
            }
        }
        compositeLight(rgb, responses, floatArrayOf(1f, 0.9f, 0.82f), intensity)
    }

    suspend fun bayerSensor(
        bitmap: Bitmap,
        pattern: Int,
        scale: Float,
        shotNoise: Float,
        readNoise: Float,
        seed: Int,
        strength: Float,
        edgeAware: Boolean
    ): Bitmap = PhotoFilterRenderer.render(bitmap, strength) { rgb ->
        if (min(rgb.cols(), rgb.rows()) < 3) return@render rgb
        val sensorScale = scale.coerceIn(0.1f, 1f)
        val width = (rgb.cols() * sensorScale).roundToInt().coerceAtLeast(3)
        val height = (rgb.rows() * sensorScale).roundToInt().coerceAtLeast(3)
        // Point sampling preserves the aliasing that a CFA introduces on fine patterns.
        val sensor = resize(rgb, width, height, Imgproc.INTER_NEAREST_EXACT)
        val cfa = mat(height, width, CvType.CV_16UC1)
        val data = ByteArray(width * 3)
        val row = ShortArray(width)
        val mode = pattern.coerceIn(0, 3)
        val filter = when (mode) {
            0 -> intArrayOf(0, 1, 1, 2)
            1 -> intArrayOf(2, 1, 1, 0)
            2 -> intArrayOf(1, 0, 2, 1)
            else -> intArrayOf(1, 2, 0, 1)
        }
        val random = Random(seed)
        val shot = shotNoise.coerceIn(0f, 0.25f)
        val read = readNoise.coerceIn(0f, 0.15f)
        for (y in 0 until height) {
            checkpoint()
            sensor.get(y, 0, data)
            for (x in 0 until width) {
                val value =
                    linearLookup[data[x * 3 + filter[(y and 1) * 2 + (x and 1)]].toInt() and 255]
                val noise = if (shot + read == 0f) 0f else {
                    val white = random.nextFloat() + random.nextFloat() + random.nextFloat() - 1.5f
                    white * (shot * kotlin.math.sqrt(value) + read)
                }
                row[x] = ((value + noise).coerceIn(0f, 1f) * 65535f).roundToInt().toShort()
            }
            cfa.put(y, 0, row)
        }
        val demosaiced = mat()
        val conversion = when (mode) {
            0 -> if (edgeAware) Imgproc.COLOR_BayerRGGB2RGB_EA else Imgproc.COLOR_BayerRGGB2RGB
            1 -> if (edgeAware) Imgproc.COLOR_BayerBGGR2RGB_EA else Imgproc.COLOR_BayerBGGR2RGB
            2 -> if (edgeAware) Imgproc.COLOR_BayerGRBG2RGB_EA else Imgproc.COLOR_BayerGRBG2RGB
            else -> if (edgeAware) Imgproc.COLOR_BayerGBRG2RGB_EA else Imgproc.COLOR_BayerGBRG2RGB
        }
        Imgproc.demosaicing(cfa, demosaiced, conversion)
        val linear = ShortArray(width * 3)
        val converted = mat(height, width, CvType.CV_8UC3)
        for (y in 0 until height) {
            checkpoint()
            demosaiced.get(y, 0, linear)
            for (x in linear.indices) data[x] = srgbByte((linear[x].toInt() and 65535) / 65535f)
            converted.put(y, 0, data)
        }
        converted
    }

    private fun PhotoFilterScope.highlightMap(rgb: Mat, threshold: Float, knee: Float): Mat {
        val scale = (896.0 / max(rgb.rows(), rgb.cols())).coerceAtMost(1.0)
        val sample = if (scale == 1.0) rgb else resize(
            rgb,
            (rgb.cols() * scale).roundToInt().coerceAtLeast(1),
            (rgb.rows() * scale).roundToInt().coerceAtLeast(1), Imgproc.INTER_AREA
        )
        val data = ByteArray(sample.cols() * 3)
        val row = FloatArray(sample.cols())
        val map = mat(sample.rows(), sample.cols(), CvType.CV_32F)
        val limit = threshold.coerceIn(0f, 1f)
        val soft = knee.coerceIn(0.01f, 1f)
        for (y in 0 until sample.rows()) {
            checkpoint()
            sample.get(y, 0, data)
            for (x in row.indices) {
                val value = maxOf(
                    linearLookup[data[x * 3].toInt() and 255],
                    linearLookup[data[x * 3 + 1].toInt() and 255],
                    linearLookup[data[x * 3 + 2].toInt() and 255]
                )
                val t = ((value - limit + soft) / (2f * soft)).coerceIn(0f, 1f)
                row[x] = value * t * t * (3f - 2f * t)
            }
            map.put(y, 0, row)
        }
        return map
    }

    private fun PhotoFilterScope.compositeLight(
        rgb: Mat,
        responses: List<Mat>,
        tint: FloatArray,
        intensity: Float,
        suppressHighlights: Boolean = false
    ): Mat {
        val result = mat()
        rgb.copyTo(result)
        val gain = intensity.coerceIn(0f, 5f)
        val pixels = ByteArray(rgb.cols() * 3)
        val halo = FloatArray(rgb.cols())
        for ((channel, response) in responses.withIndex()) {
            checkpoint()
            work {
                val resized = resize(response, rgb.cols(), rgb.rows(), Imgproc.INTER_LINEAR)
                for (y in 0 until rgb.rows()) {
                    checkpoint()
                    result.get(y, 0, pixels)
                    resized.get(y, 0, halo)
                    for (x in halo.indices) {
                        val i = x * 3 + channel
                        val linear = linearLookup[pixels[i].toInt() and 255]
                        val headroom = if (suppressHighlights) 1f - linear * 0.83f else 1f
                        pixels[i] = srgbByte(linear + halo[x] * gain * tint[channel] * headroom)
                    }
                    result.put(y, 0, pixels)
                }
            }
        }
        return result
    }

    private val linearLookup = FloatArray(256) { index ->
        val v = index / 255f
        if (v <= 0.04045f) v / 12.92f else ((v + 0.055f) / 1.055f).pow(2.4f)
    }

    private fun srgbByte(value: Float): Byte {
        val v = value.coerceIn(0f, 1f)
        val encoded = if (v <= 0.0031308f) 12.92f * v else 1.055f * v.pow(1f / 2.4f) - 0.055f
        return (encoded * 255f).roundToInt().toByte()
    }
}
