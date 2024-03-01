/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

@file:Suppress("MemberVisibilityCanBePrivate")

package ru.tech.imageresizershrinker.feature.filters.data.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import kotlin.math.sqrt


object Pixelate {
    private val SQRT2 = sqrt(2.0).toFloat()

    fun fromBitmap(
        input: Bitmap,
        vararg layers: PixelationLayer
    ): Bitmap {
        val out = Bitmap.createBitmap(input.width, input.height, Bitmap.Config.ARGB_8888)
        render(input, out, *layers)
        return out
    }

    fun render(
        input: Bitmap,
        out: Bitmap,
        vararg layers: PixelationLayer
    ) {
        render(input, null, out, *layers)
    }

    fun render(
        input: Bitmap,
        inBounds: Rect?,
        out: Bitmap,
        vararg layers: PixelationLayer
    ) {
        render(input, inBounds, out, null, *layers)
    }

    fun render(
        input: Bitmap,
        inBounds: Rect?,
        out: Bitmap,
        outBounds: Rect?,
        vararg layers: PixelationLayer,
    ) {
        var bounds = outBounds
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG or Paint.FILTER_BITMAP_FLAG)
        if (bounds == null) {
            bounds = Rect(0, 0, out.width, out.height)
        }
        render(input, inBounds, Canvas(out), bounds, paint, *layers)
    }

    fun render(
        input: Bitmap,
        inBounds: Rect?,
        canvas: Canvas,
        outBounds: Rect,
        paint: Paint,
        vararg layers: PixelationLayer,
    ) {
        val inWidth = inBounds?.width() ?: input.width
        val inHeight = inBounds?.height() ?: input.height
        val inX = inBounds?.left ?: 0
        val inY = inBounds?.top ?: 0
        val scaleX = outBounds.width().toFloat() / inWidth
        val scaleY = outBounds.height().toFloat() / inHeight
        canvas.save()
        canvas.clipRect(outBounds)
        canvas.translate(outBounds.left.toFloat(), outBounds.top.toFloat())
        canvas.scale(scaleX, scaleY)
        for (layer in layers) {
            // option defaults
            val size: Float = if (layer.size == null) layer.resolution else layer.size!!
            val cols = (inWidth / layer.resolution + 1).toInt()
            val rows = (inHeight / layer.resolution + 1).toInt()
            val halfSize = size / 2f
            val diamondSize = size / SQRT2
            val halfDiamondSize = diamondSize / 2f
            for (row in 0..rows) {
                val y: Float = (row - 0.5f) * layer.resolution + layer.offsetY
                // normalize y so shapes around edges get color
                val pixelY = inY + y.coerceAtMost((inHeight - 1).toFloat()).coerceAtLeast(0f)
                for (col in 0..cols) {
                    val x: Float = (col - 0.5f) * layer.resolution + layer.offsetX
                    // normalize y so shapes around edges get color
                    val pixelX = inX + x.coerceAtMost((inWidth - 1).toFloat()).coerceAtLeast(0f)
                    paint.color = getPixelColor(input, pixelX.toInt(), pixelY.toInt(), layer)
                    when (layer.shape) {
                        PixelationLayer.Shape.Circle -> canvas.drawCircle(x, y, halfSize, paint)
                        PixelationLayer.Shape.Diamond -> {
                            canvas.save()
                            canvas.translate(x, y)
                            canvas.rotate(45f)
                            canvas.drawRect(
                                -halfDiamondSize,
                                -halfDiamondSize,
                                halfDiamondSize,
                                halfDiamondSize,
                                paint
                            )
                            canvas.restore()
                        }

                        PixelationLayer.Shape.Square -> canvas.drawRect(
                            x - halfSize,
                            y - halfSize,
                            x + halfSize,
                            y + halfSize,
                            paint
                        )
                    }
                } // col
            } // row
        }
        canvas.restore()
    }

    /**
     * Returns the color of the cluster. If options.enableDominantColor is true, return the
     * dominant color around the provided point. Return the color of the point itself otherwise.
     * The dominant color algorithm is based on simple counting search, so use with caution.
     *
     * @param pixels the bitmap
     * @param pixelX the x coordinate of the reference point
     * @param pixelY the y coordinate of the reference point
     * @param opts additional options
     * @return the color of the cluster
     */
    private fun getPixelColor(
        pixels: Bitmap,
        pixelX: Int,
        pixelY: Int,
        opts: PixelationLayer
    ): Int {
        var pixel = pixels.getPixel(pixelX, pixelY)
        if (opts.enableDominantColor) {
            val colorCounter: MutableMap<Int, Int> = HashMap(100)
            for (x in 0.coerceAtLeast((pixelX - opts.resolution).toInt()) until pixels.width.coerceAtMost(
                (pixelX + opts.resolution).toInt()
            )) {
                for (y in 0.coerceAtLeast((pixelY - opts.resolution).toInt()) until pixels.height.coerceAtMost(
                    (pixelY + opts.resolution).toInt()
                )) {
                    val currentRGB = pixels.getPixel(x, y)
                    val count =
                        if (colorCounter.containsKey(currentRGB)) colorCounter[currentRGB]!! else 0
                    colorCounter[currentRGB] = count + 1
                }
            }
            var max: Int? = null
            var dominantRGB: Int? = null
            for ((key, value) in colorCounter) {
                if (max == null || value > max) {
                    max = value
                    dominantRGB = key
                }
            }
            pixel = dominantRGB!!
        }
        val red = Color.red(pixel)
        val green = Color.green(pixel)
        val blue = Color.blue(pixel)
        val alpha = (opts.alpha * Color.alpha(pixel)).toInt()
        return Color.argb(alpha, red, green, blue)
    }
}