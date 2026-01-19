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

package com.t8rin.imagetoolbox.feature.draw.data

import android.graphics.Bitmap
import com.t8rin.imagetoolbox.feature.draw.domain.DisplacementMap
import com.t8rin.imagetoolbox.feature.draw.domain.WarpBrush
import com.t8rin.imagetoolbox.feature.draw.domain.WarpMode
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

internal class WarpEngine(
    src: Bitmap
) {
    private val w = src.width
    private val h = src.height

    private val pixels = IntArray(w * h)
    private var map = DisplacementMap(w, h)

    // Добавляем переменные для отслеживания предыдущей точки
    private var lastX: Float? = null
    private var lastY: Float? = null

    init {
        src.getPixels(pixels, 0, w, 0, 0, w, h)
    }

    fun applyStroke(
        fromX: Float,
        fromY: Float,
        toX: Float,
        toY: Float,
        brush: WarpBrush,
        mode: WarpMode
    ) {
        if (lastX == null || lastY == null) {
            lastX = fromX
            lastY = fromY
        }

        interpolateAndApplyStroke(
            fromX = lastX!!,
            fromY = lastY!!,
            toX = toX,
            toY = toY,
            brush = brush,
            mode = mode
        )

        lastX = toX
        lastY = toY
    }

    private fun interpolateAndApplyStroke(
        fromX: Float,
        fromY: Float,
        toX: Float,
        toY: Float,
        brush: WarpBrush,
        mode: WarpMode
    ) {
        val dx = toX - fromX
        val dy = toY - fromY
        val distance = sqrt(dx * dx + dy * dy)

        val step = brush.radius / 2f

        if (distance <= step) {
            applyStrokeToPoint(fromX, fromY, toX, toY, brush, mode)
        } else {
            val steps = ceil(distance / step).toInt()
            val stepX = dx / steps
            val stepY = dy / steps

            for (i in 0 until steps) {
                val currentFromX = fromX + stepX * (i - 1).coerceAtLeast(0)
                val currentFromY = fromY + stepY * (i - 1).coerceAtLeast(0)
                val currentToX = fromX + stepX * i
                val currentToY = fromY + stepY * i

                applyStrokeToPoint(currentFromX, currentFromY, currentToX, currentToY, brush, mode)
            }
        }
    }

    private fun applyStrokeToPoint(
        fromX: Float,
        fromY: Float,
        toX: Float,
        toY: Float,
        brush: WarpBrush,
        mode: WarpMode
    ) {
        val r = brush.radius
        val r2 = r * r

        val minX = (toX - r).toInt().coerceIn(0, w - 1)
        val maxX = (toX + r).toInt().coerceIn(0, w - 1)
        val minY = (toY - r).toInt().coerceIn(0, h - 1)
        val maxY = (toY + r).toInt().coerceIn(0, h - 1)

        for (y in minY..maxY) {
            for (x in minX..maxX) {
                val dx = x - toX
                val dy = y - toY
                val dist2 = dx * dx + dy * dy
                if (dist2 > r2) continue

                val dist = sqrt(dist2)
                val t = 1f - dist / r

                val amp = when (mode) {
                    WarpMode.MOVE -> 1f
                    WarpMode.GROW -> 0.015f
                    WarpMode.SHRINK -> 0.015f
                    WarpMode.SWIRL_CW -> 0.2f
                    WarpMode.SWIRL_CCW -> 0.2f
                }

                val falloff = smoothstep(edge0 = brush.hardness, x = t) * (brush.strength * amp)

                val idx = map.index(x, y)

                when (mode) {
                    WarpMode.MOVE -> {
                        map.dx[idx] += (fromX - toX) * falloff
                        map.dy[idx] += (fromY - toY) * falloff
                    }

                    WarpMode.GROW -> {
                        val len = sqrt(dx * dx + dy * dy)
                        if (len > 0f) {
                            map.dx[idx] -= (dx / len) * falloff * r
                            map.dy[idx] -= (dy / len) * falloff * r
                        }
                    }

                    WarpMode.SHRINK -> {
                        val len = sqrt(dx * dx + dy * dy)
                        if (len > 0f) {
                            map.dx[idx] += (dx / len) * falloff * r
                            map.dy[idx] += (dy / len) * falloff * r
                        }
                    }

                    WarpMode.SWIRL_CW,
                    WarpMode.SWIRL_CCW -> {
                        val angleMax = 0.8f
                        val angle = t * angleMax * falloff *
                                if (mode == WarpMode.SWIRL_CW) 1f else -1f
                        val sin = sin(angle)
                        val cos = cos(angle)

                        val rx = dx * cos - dy * sin
                        val ry = dx * sin + dy * cos

                        map.dx[idx] += (rx - dx)
                        map.dy[idx] += (ry - dy)
                    }
                }
            }
        }
    }

    fun render(): Bitmap {
        val out = IntArray(w * h)

        for (y in 0 until h) {
            for (x in 0 until w) {
                val i = y * w + x
                val sx = x + map.dx[i]
                val sy = y + map.dy[i]
                out[i] = sampleBilinear(sx, sy)
            }
        }

        return Bitmap.createBitmap(out, w, h, Bitmap.Config.ARGB_8888)
    }

    private fun sampleBilinear(fx: Float, fy: Float): Int {
        val x0 = fx.toInt().coerceIn(0, w - 1)
        val y0 = fy.toInt().coerceIn(0, h - 1)
        val x1 = (x0 + 1).coerceIn(0, w - 1)
        val y1 = (y0 + 1).coerceIn(0, h - 1)

        val dx = fx - x0
        val dy = fy - y0

        val c00 = pixels[y0 * w + x0]
        val c10 = pixels[y0 * w + x1]
        val c01 = pixels[y1 * w + x0]
        val c11 = pixels[y1 * w + x1]

        return lerpColor(
            lerpColor(c00, c10, dx),
            lerpColor(c01, c11, dx),
            dy
        )
    }

    private fun smoothstep(
        edge0: Float,
        edge1: Float = 1f,
        x: Float
    ): Float {
        val t = ((x - edge0) / (edge1 - edge0)).coerceIn(0f, 1f)
        return t * t * (3 - 2 * t)
    }

    private fun lerpColor(a: Int, b: Int, t: Float): Int {
        val ar = a shr 16 and 0xff
        val ag = a shr 8 and 0xff
        val ab = a and 0xff

        val br = b shr 16 and 0xff
        val bg = b shr 8 and 0xff
        val bb = b and 0xff

        val r = (ar + (br - ar) * t).toInt()
        val g = (ag + (bg - ag) * t).toInt()
        val bl = (ab + (bb - ab) * t).toInt()

        return (0xff shl 24) or (r shl 16) or (g shl 8) or bl
    }
}