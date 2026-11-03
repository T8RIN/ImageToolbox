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
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

object SpectralFilters {

    suspend fun wienerDeconvolution(
        bitmap: Bitmap,
        psf: Int,
        radius: Float,
        angle: Float,
        regularization: Float,
        strength: Float
    ): Bitmap = PhotoFilterRenderer.render(bitmap, strength, maxSide = 1800) { rgb ->
        val size = rgb.radius(radius.coerceIn(1f, 35f)).coerceIn(1, 62)
        val pad = size * 2
        val width = Core.getOptimalDFTSize(rgb.cols() + pad * 2)
        val height = Core.getOptimalDFTSize(rgb.rows() + pad * 2)
        val kernel = mat(height, width, CvType.CV_32F)
        kernel.setTo(Scalar.all(0.0))
        val diameter = size * 2 + 1
        val local = mat(diameter, diameter, CvType.CV_32F)
        local.setTo(Scalar.all(0.0))
        val center = Point(size.toDouble(), size.toDouble())
        when (psf) {
            1 -> Imgproc.circle(local, center, size, Scalar.all(1.0), -1)
            2 -> {
                val column =
                    Imgproc.getGaussianKernel(diameter, max(0.35, size / 2.5), CvType.CV_32F)
                try {
                    val row = column.t()
                    try {
                        Core.gemm(column, row, 1.0, mat(), 0.0, local)
                    } finally {
                        row.release()
                    }
                } finally {
                    column.release()
                }
            }

            else -> {
                val rotation = Math.toRadians(angle.toDouble())
                val dx = cos(rotation) * size
                val dy = sin(rotation) * size
                // Supersampled line footprint with bilinear deposit.
                val taps = FloatArray(diameter * diameter)
                for (step in -size * 4..size * 4) {
                    val t = step / (size * 4.0)
                    val x = (center.x + dx * t).coerceIn(0.0, diameter - 1.0)
                    val y = (center.y + dy * t).coerceIn(0.0, diameter - 1.0)
                    val x0 = x.toInt()
                    val y0 = y.toInt()
                    for (j in y0..min(y0 + 1, diameter - 1)) {
                        for (i in x0..min(x0 + 1, diameter - 1)) {
                            taps[j * diameter + i] += ((1 - abs(x - i)) * (1 - abs(y - j))).toFloat()
                        }
                    }
                }
                local.put(0, 0, taps)
            }
        }
        val energy = Core.sumElems(local).`val`[0].coerceAtLeast(1e-9)
        val row = FloatArray(diameter)
        for (y in -size..size) {
            local.get(y + size, 0, row)
            val destY = (y + height) % height
            for (x in row.indices) row[x] = (row[x] / energy).toFloat()
            kernel.put(destY, width - size, row.copyOfRange(0, size))
            kernel.put(destY, 0, row.copyOfRange(size, diameter))
        }
        val response = spectrum(kernel)
        val real = channel(response, 0)
        val imaginary = channel(response, 1)
        val denominator = add(multiply(real, real), multiply(imaginary, imaginary))
        val noise = regularization.coerceIn(0.0001f, 0.25f).toDouble()
        Core.add(denominator, Scalar.all(noise), denominator)
        val inverse = mat()
        Core.merge(
            listOf(
                scale(divide(real, denominator), 1.0 + noise),
                scale(divide(imaginary, denominator), -1.0 - noise)
            ), inverse
        )
        val channels = List(3) { i ->
            val result = mat()
            work {
                checkpoint()
                val input = floats(channel(rgb, i))
                val padded = mat()
                Core.copyMakeBorder(
                    input, padded, pad, height - rgb.rows() - pad, pad,
                    width - rgb.cols() - pad, Core.BORDER_REFLECT_101
                )
                val frequency = spectrum(padded)
                Core.mulSpectrums(frequency, inverse, frequency, 0)
                val restored = realImage(frequency)
                val cropped = restored.submat(pad, pad + rgb.rows(), pad, pad + rgb.cols())
                try {
                    cropped.convertTo(result, CvType.CV_8U, 255.0)
                } finally {
                    cropped.release()
                }
            }
            result
        }
        mat().also { Core.merge(channels, it) }
    }

    suspend fun spectralNotch(
        bitmap: Bitmap,
        frequencyX: Float,
        frequencyY: Float,
        bandwidth: Float,
        strength: Float
    ): Bitmap = PhotoFilterRenderer.render(bitmap, strength) { rgb ->
        val fx = frequencyX.coerceIn(-0.5f, 0.5f).toDouble()
        val fy = frequencyY.coerceIn(-0.5f, 0.5f).toDouble()
        if (fx == 0.0 && fy == 0.0) return@render rgb
        val sigma = bandwidth.coerceIn(0.0005f, 0.12f)
        val ycc = convert(rgb, Imgproc.COLOR_RGB2YCrCb)
        val luminance = channel(ycc, 0)
        val longest = max(luminance.cols(), luminance.rows())
        val working = if (longest <= 2048) luminance else {
            val scale = 2048.0 / longest
            resize(
                luminance,
                (luminance.cols() * scale).roundToInt().coerceAtLeast(1),
                (luminance.rows() * scale).roundToInt().coerceAtLeast(1),
                Imgproc.INTER_AREA
            )
        }
        val samples = floats(working)
        val frequency = spectrum(samples)
        val width = frequency.cols()
        val height = frequency.rows()
        fun periodicDistance(value: Double): Double {
            val d = abs(value)
            return min(d, 1.0 - d)
        }
        editSpectrum(frequency) { x, y, row ->
            if (x == 0 && y == 0) return@editSpectrum
            val u = if (x <= width / 2) x.toDouble() / width else x.toDouble() / width - 1.0
            val v = if (y <= height / 2) y.toDouble() / height else y.toDouble() / height - 1.0
            fun reject(a: Double, b: Double): Double {
                val dx = periodicDistance(u - a)
                val dy = periodicDistance(v - b)
                return 1.0 - exp(-(dx * dx + dy * dy) / (2.0 * sigma * sigma))
            }

            val weight = (reject(fx, fy) * reject(-fx, -fy)).toFloat()
            row[x * 2] *= weight
            row[x * 2 + 1] *= weight
        }
        val filtered = realImage(frequency)
        val corrected = if (working === luminance) bytes(filtered) else {
            // Apply only the removed frequency band at full size.
            val difference = subtract(filtered, samples)
            val correction =
                resize(difference, luminance.cols(), luminance.rows(), Imgproc.INTER_LINEAR)
            mat().also {
                Core.addWeighted(
                    luminance,
                    1.0,
                    correction,
                    255.0,
                    0.0,
                    it,
                    CvType.CV_8U
                )
            }
        }
        Core.insertChannel(corrected, ycc, 0)
        convert(ycc, Imgproc.COLOR_YCrCb2RGB)
    }

    suspend fun fourierPhaseScramble(
        bitmap: Bitmap,
        minFrequency: Float,
        maxFrequency: Float,
        phase: Float,
        seed: Int,
        strength: Float
    ): Bitmap = PhotoFilterRenderer.render(bitmap, strength, maxSide = 1600) { rgb ->
        val minimum = minFrequency.coerceIn(0f, 0.7f)
        val maximum = maxFrequency.coerceIn(minimum, 0.71f)
        val amplitude = phase.coerceIn(0f, 1f) * PI
        if (amplitude == 0.0) return@render rgb
        val channels = List(3) { i ->
            val result = mat()
            work {
                checkpoint()
                val frequency = spectrum(floats(channel(rgb, i)))
                val width = frequency.cols()
                val height = frequency.rows()
                editSpectrum(frequency) { x, y, row ->
                    val oppositeX = (width - x) % width
                    val oppositeY = (height - y) % height
                    if (oppositeX == x && oppositeY == y) return@editSpectrum
                    val distance = hypot(
                        min(x, oppositeX).toDouble() / width,
                        min(y, oppositeY).toDouble() / height
                    )
                    if (distance !in minimum..maximum) return@editSpectrum
                    val index = y * width + x
                    val opposite = oppositeY * width + oppositeX
                    val angle = amplitude * signedNoise(min(index, opposite), seed) *
                            (if (index < opposite) 1.0 else -1.0)
                    val cosine = cos(angle).toFloat()
                    val sine = sin(angle).toFloat()
                    val real = row[x * 2]
                    val imag = row[x * 2 + 1]
                    row[x * 2] = real * cosine - imag * sine
                    row[x * 2 + 1] = real * sine + imag * cosine
                }
                realImage(frequency).convertTo(result, CvType.CV_8U, 255.0)
            }
            result
        }
        mat().also { Core.merge(channels, it) }
    }

    suspend fun dctGlitch(
        bitmap: Bitmap,
        blockSize: Int,
        minimumFrequency: Float,
        maximumFrequency: Float,
        operation: Int,
        probability: Float,
        seed: Int,
        strength: Float
    ): Bitmap = PhotoFilterRenderer.render(bitmap, strength, maxSide = 1800) { rgb ->
        val block = if (blockSize == 0) 8 else 16
        val minFrequency = minimumFrequency.coerceIn(0f, 1f)
        val maxFrequency = maximumFrequency.coerceIn(minFrequency, 1f)
        val selected = (1 until block * block).filter {
            (it % block + it / block).toFloat() / (2 * (block - 1)) in minFrequency..maxFrequency
        }
        if (selected.isEmpty()) return@render rgb
        val ycc = convert(rgb, Imgproc.COLOR_RGB2YCrCb)
        val random = Random(seed)
        val channels = if (operation == 2) 1..2 else 0..0
        for (channel in channels) {
            work {
                val input = floats(channel(ycc, channel))
                val padded = mat()
                val right = (block - input.cols() % block) % block
                val bottom = (block - input.rows() % block) % block
                Core.copyMakeBorder(input, padded, 0, bottom, 0, right, Core.BORDER_REFLECT_101)
                val coefficients = mat()
                val values = FloatArray(block * block)
                for (y in 0 until padded.rows() step block) {
                    checkpoint()
                    for (x in 0 until padded.cols() step block) {
                        if (random.nextFloat() >= probability.coerceIn(0f, 1f)) continue
                        val patch = padded.submat(y, y + block, x, x + block)
                        try {
                            Core.dct(patch, coefficients)
                            coefficients.get(0, 0, values)
                            for (index in selected) {
                                values[index] = when (operation) {
                                    0 -> 0f
                                    1 -> -values[index]
                                    else -> values[index] * (if (random.nextBoolean()) -2.8f else 3.6f)
                                }
                            }
                            coefficients.put(0, 0, values)
                            Core.idct(coefficients, patch)
                        } finally {
                            patch.release()
                        }
                    }
                }
                val crop = padded.submat(0, rgb.rows(), 0, rgb.cols())
                try {
                    Core.insertChannel(bytes(crop), ycc, channel)
                } finally {
                    crop.release()
                }
            }
        }
        convert(ycc, Imgproc.COLOR_YCrCb2RGB)
    }

    suspend fun l0Smoothing(
        bitmap: Bitmap,
        lambda: Float,
        penaltyGrowth: Float,
        iterations: Int,
        strength: Float
    ): Bitmap = PhotoFilterRenderer.render(bitmap, strength, maxSide = 1400) { rgb ->
        if (min(rgb.cols(), rgb.rows()) < 2) return@render rgb
        val ycc = convert(rgb, Imgproc.COLOR_RGB2YCrCb)
        val luma = floats(channel(ycc, 0))
        // Reflect a margin before solving the periodic linear system.
        val pad = 12
        val width = Core.getOptimalDFTSize(luma.cols() + 2 * pad)
        val height = Core.getOptimalDFTSize(luma.rows() + 2 * pad)
        val source = mat()
        Core.copyMakeBorder(
            luma, source, pad, height - luma.rows() - pad,
            pad, width - luma.cols() - pad, Core.BORDER_REFLECT_101
        )
        val original = spectrum(source)
        val derivativeX = FloatArray(width) { (2.0 - 2.0 * cos(2.0 * PI * it / width)).toFloat() }
        val derivativeY = FloatArray(height) { (2.0 - 2.0 * cos(2.0 * PI * it / height)).toFloat() }
        val size = width * height
        val solution = FloatArray(size)
        val dx = FloatArray(size)
        val dy = FloatArray(size)
        val row = FloatArray(width)
        val divergence = mat(height, width, CvType.CV_32F)
        val gradientSpectrum = mat()
        var current = source
        val smoothing = lambda.coerceIn(0.0001f, 0.2f)
        var beta = smoothing * 2f
        val growth = penaltyGrowth.coerceIn(1.3f, 4f)
        repeat(iterations.coerceIn(1, 14)) {
            current.get(0, 0, solution)
            val limit = smoothing / beta
            for (y in 0 until height) {
                checkpoint()
                val offset = y * width
                val next = (y + 1) % height * width
                for (x in 0 until width) {
                    val i = offset + x
                    val horizontal = solution[offset + (x + 1) % width] - solution[i]
                    val vertical = solution[next + x] - solution[i]
                    val preserve = horizontal * horizontal + vertical * vertical >= limit
                    dx[i] = if (preserve) horizontal else 0f
                    dy[i] = if (preserve) vertical else 0f
                }
            }
            for (y in 0 until height) {
                checkpoint()
                val offset = y * width
                val previous = (y + height - 1) % height * width
                for (x in 0 until width) {
                    row[x] = dx[offset + (x + width - 1) % width] - dx[offset + x] +
                            dy[previous + x] - dy[offset + x]
                }
                divergence.put(y, 0, row)
            }
            Core.dft(divergence, gradientSpectrum, Core.DFT_COMPLEX_OUTPUT)
            val reference = FloatArray(width * 2)
            var lastRow = -1
            editSpectrum(gradientSpectrum) { x, y, rowSpectrum ->
                if (lastRow != y) {
                    original.get(y, 0, reference)
                    lastRow = y
                }
                val divisor = 1f + beta * (derivativeX[x] + derivativeY[y])
                rowSpectrum[x * 2] = (reference[x * 2] + beta * rowSpectrum[x * 2]) / divisor
                rowSpectrum[x * 2 + 1] =
                    (reference[x * 2 + 1] + beta * rowSpectrum[x * 2 + 1]) / divisor
            }
            if (current === source) current = mat()
            Core.idft(gradientSpectrum, current, Core.DFT_SCALE or Core.DFT_REAL_OUTPUT)
            beta *= growth
        }
        val crop = current.submat(pad, pad + luma.rows(), pad, pad + luma.cols())
        try {
            Core.insertChannel(bytes(crop), ycc, 0)
        } finally {
            crop.release()
        }
        convert(ycc, Imgproc.COLOR_YCrCb2RGB)
    }

    private fun PhotoFilterScope.spectrum(input: Mat): Mat =
        mat().also { Core.dft(input, it, Core.DFT_COMPLEX_OUTPUT) }

    private fun PhotoFilterScope.realImage(spectrum: Mat): Mat =
        mat().also { Core.idft(spectrum, it, Core.DFT_SCALE or Core.DFT_REAL_OUTPUT) }

    private inline fun PhotoFilterScope.editSpectrum(
        frequency: Mat,
        edit: (x: Int, y: Int, row: FloatArray) -> Unit
    ) {
        val row = FloatArray(frequency.cols() * 2)
        for (y in 0 until frequency.rows()) {
            checkpoint()
            frequency.get(y, 0, row)
            for (x in 0 until row.size / 2) edit(x, y, row)
            frequency.put(y, 0, row)
        }
    }

    private fun signedNoise(index: Int, seed: Int): Double {
        var value = index * 0x9e3779b9.toInt() xor seed
        value = (value xor (value ushr 16)) * 0x7feb352d
        value = (value xor (value ushr 15)) * 0x846ca68b.toInt()
        return ((value xor (value ushr 16)) ushr 8) / 8388608.0 - 1.0
    }
}
