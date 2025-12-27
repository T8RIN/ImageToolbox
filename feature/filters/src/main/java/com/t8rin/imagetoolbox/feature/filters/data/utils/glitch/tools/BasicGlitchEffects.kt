/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.feature.filters.data.utils.glitch.tools

import android.graphics.Bitmap
import kotlinx.coroutines.coroutineScope
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

internal interface BasicGlitchEffects {
    suspend fun glitchVariant(
        src: Bitmap,
        iterations: Int = 30,
        maxOffsetFraction: Float = 0.1f,   // 0f..1f
        channelShiftFraction: Float = 0.02f // 0f..1f
    ): Bitmap = coroutineScope {
        val w = src.width
        val h = src.height
        val result = src.copy(Bitmap.Config.ARGB_8888, true)

        val pixels = IntArray(w * h)
        val out = IntArray(w * h)
        result.getPixels(pixels, 0, w, 0, 0, w, h)
        pixels.copyInto(out)

        val maxOffset = ((w / 2) * maxOffsetFraction).roundToInt()
        val channelShift = ((w / 10) * channelShiftFraction).roundToInt()

        repeat(iterations) {
            val y = Random.nextInt(h)
            val height = Random.nextInt(1, 6)
            val offset = Random.nextInt(-maxOffset, maxOffset)

            for (dy in 0 until height) {
                val row = y + dy
                if (row !in 0 until h) continue

                for (x in 0 until w) {
                    val srcX = (x + offset).coerceIn(0, w - 1)
                    out[row * w + x] = pixels[row * w + srcX]
                }
            }
        }

        for (y in 0 until h) {
            for (x in 0 until w) {
                val i = y * w + x

                val rSrc = ((x + channelShift).coerceIn(0, w - 1)) + y * w
                val bSrc = ((x - channelShift).coerceIn(0, w - 1)) + y * w

                val r = (pixels[rSrc] shr 16) and 0xFF
                val g = (out[i] shr 8) and 0xFF
                val b = pixels[bSrc] and 0xFF
                val a = (out[i] ushr 24) and 0xFF

                out[i] = (a shl 24) or (r shl 16) or (g shl 8) or b
            }
        }

        result.setPixels(out, 0, w, 0, 0, w, h)

        result
    }

    suspend fun vhsGlitch(
        src: Bitmap,
        time: Float = 0f,
        strength: Float = 1f
    ): Bitmap = coroutineScope {
        val w = src.width
        val h = src.height

        val lineJitterPx = (w * 0.015f * strength).toInt()
        val rgbShiftPx = (w * 0.008f * strength).toInt()
        val waveAmp = 2f * strength
        val noiseAmp = (20 * strength).toInt()

        val result = src.copy(Bitmap.Config.ARGB_8888, true)

        val pixels = IntArray(w * h)
        val out = IntArray(w * h)
        result.getPixels(pixels, 0, w, 0, 0, w, h)

        for (y in 0 until h) {
            val wave =
                sin((y * 0.06f) + time * 4f) * waveAmp

            val jitter =
                if (Random.nextFloat() < 0.08f * strength)
                    Random.nextInt(-lineJitterPx, lineJitterPx + 1)
                else 0

            val row = y * w

            for (x in 0 until w) {
                val baseX = (x + wave + jitter).toInt()
                    .coerceIn(0, w - 1)

                val rX = (baseX + rgbShiftPx).coerceIn(0, w - 1)
                val bX = (baseX - rgbShiftPx).coerceIn(0, w - 1)

                val pR = pixels[row + rX]
                val pG = pixels[row + baseX]
                val pB = pixels[row + bX]

                var r = (pR shr 16) and 0xFF
                var g = (pG shr 8) and 0xFF
                var b = pB and 0xFF
                val a = (pG ushr 24)

                val noise = Random.nextInt(-noiseAmp, noiseAmp + 1)
                r = (r + noise).coerceIn(0, 255)
                g = (g + noise).coerceIn(0, 255)
                b = (b + noise).coerceIn(0, 255)

                if ((y % 3) == 0) {
                    r = (r * 0.92f).toInt()
                    g = (g * 0.92f).toInt()
                    b = (b * 0.92f).toInt()
                }

                out[row + x] =
                    (a shl 24) or
                            (r shl 16) or
                            (g shl 8) or
                            b
            }
        }

        result.setPixels(out, 0, w, 0, 0, w, h)

        result
    }

    suspend fun blockGlitch(
        src: Bitmap,
        strength: Float = 0.5f,   // 0f..1f
        blockSizeFraction: Float = 0.02f // 0f..1f
    ): Bitmap = coroutineScope {
        val w = src.width
        val h = src.height

        val blockSize = (w * blockSizeFraction)
            .toInt()
            .coerceAtLeast(4)

        val maxOffset = (w * 0.15f * strength).toInt()

        val result = src.copy(Bitmap.Config.ARGB_8888, true)

        val pixels = IntArray(w * h)
        val out = IntArray(w * h)
        result.getPixels(pixels, 0, w, 0, 0, w, h)
        pixels.copyInto(out)

        var y = 0
        while (y < h) {
            var x = 0
            while (x < w) {
                if (Random.nextFloat() < strength * 0.4f) {

                    val offsetX = Random.nextInt(-maxOffset, maxOffset + 1)
                    val offsetY = Random.nextInt(-blockSize, blockSize + 1)

                    for (dy in 0 until blockSize) {
                        val sy = (y + dy + offsetY).coerceIn(0, h - 1)
                        val dyOut = y + dy
                        if (dyOut !in 0 until h) continue

                        for (dx in 0 until blockSize) {
                            val sx = (x + dx + offsetX).coerceIn(0, w - 1)
                            val dxOut = x + dx
                            if (dxOut !in 0 until w) continue

                            out[dyOut * w + dxOut] =
                                pixels[sy * w + sx]
                        }
                    }
                }
                x += blockSize
            }
            y += blockSize
        }

        result.setPixels(out, 0, w, 0, 0, w, h)

        result
    }

    suspend fun crtCurvature(
        src: Bitmap,
        curvature: Float = 0.25f,   // -1f..1f,
        vignette: Float = 0.35f,    // 0f..1f,
        chroma: Float = 0.015f      // 0f..1f
    ): Bitmap = coroutineScope {
        val w = src.width
        val h = src.height

        val cx = w * 0.5f
        val cy = h * 0.5f
        val maxR = sqrt(cx * cx + cy * cy)

        val curve = curvature * 0.45f
        val chromaPx = (w * chroma).toInt()

        // Масштаб, чтобы края не уходили за пределы
        val scaleFactor = 1f - curve * 0.2f

        val srcPixels = IntArray(w * h)
        val out = IntArray(w * h)
        src.getPixels(srcPixels, 0, w, 0, 0, w, h)

        for (y in 0 until h) {
            for (x in 0 until w) {

                // нормализуем координаты и применяем scale
                val nx = ((x - cx) / cx) * scaleFactor
                val ny = ((y - cy) / cy) * scaleFactor

                val r2 = nx * nx + ny * ny
                val k = 1f - r2 * curve // выпуклость наружу

                val sx = (cx + nx * cx / k).toInt().coerceIn(0, w - 1)
                val sy = (cy + ny * cy / k).toInt().coerceIn(0, h - 1)

                val base = sy * w + sx

                val rSrc = (sx + chromaPx).coerceIn(0, w - 1) + sy * w
                val bSrc = (sx - chromaPx).coerceIn(0, w - 1) + sy * w

                val pR = srcPixels[rSrc]
                val pG = srcPixels[base]
                val pB = srcPixels[bSrc]

                var r = (pR shr 16) and 0xFF
                var g = (pG shr 8) and 0xFF
                var b = pB and 0xFF
                val a = (pG ushr 24)

                // Vignette
                val dist = sqrt((x - cx).pow(2) + (y - cy).pow(2)) / maxR
                val vig = 1f - vignette * dist * dist

                r = (r * vig).toInt().coerceIn(0, 255)
                g = (g * vig).toInt().coerceIn(0, 255)
                b = (b * vig).toInt().coerceIn(0, 255)

                out[y * w + x] =
                    (a shl 24) or
                            (r shl 16) or
                            (g shl 8) or
                            b
            }
        }

        Bitmap.createBitmap(out, w, h, Bitmap.Config.ARGB_8888)
    }

    suspend fun pixelMelt(
        src: Bitmap,
        strength: Float = 0.5f,   // 0f..1f,
        maxDrop: Int = 20
    ): Bitmap = coroutineScope {
        val w = src.width
        val h = src.height

        val pixels = IntArray(w * h)
        val out = IntArray(w * h)
        src.getPixels(pixels, 0, w, 0, 0, w, h)
        pixels.copyInto(out)

        for (x in 0 until w) {
            var drop = 0
            for (y in 0 until h) {
                val i = y * w + x

                if (Random.nextFloat() < strength) {
                    drop = Random.nextInt(1, maxDrop + 1)
                }

                val newY = (y + drop).coerceAtMost(h - 1)
                out[newY * w + x] = pixels[i]
            }
        }

        Bitmap.createBitmap(out, w, h, Bitmap.Config.ARGB_8888)
    }
}