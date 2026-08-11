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
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.core.graphics.createBitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.t8rin.imagetoolbox.feature.multi_frame_fusion.domain.FusionMode
import com.t8rin.imagetoolbox.feature.multi_frame_fusion.domain.FusionParams
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.abs

@RunWith(AndroidJUnit4::class)
class MultiFrameFusionEngineTest {

    private val engine = MultiFrameFusionEngine()

    @Test
    fun medianStackRemovesOutlierFrame() {
        val normal = solidBitmap(Color.rgb(24, 96, 168))
        val outlier = solidBitmap(Color.rgb(240, 16, 32))

        val result = engine.fuse(
            bitmaps = listOf(normal, outlier, normal),
            params = FusionParams(
                mode = FusionMode.Median,
                alignImages = false
            )
        )

        assertNotNull(result)
        assertEquals(normal.width, result!!.width)
        assertEquals(normal.height, result.height)
        assertColorClose(normal.getPixel(0, 0), result.getPixel(0, 0), tolerance = 1)
    }

    @Test
    fun exposureFusionProducesValidImageFromBracketedFrames() {
        val base = gradientBitmap(width = 96, height = 64)
        val dark = adjustBrightness(base, 0.45f)
        val bright = adjustBrightness(base, 1.7f)

        val result = engine.fuse(
            bitmaps = listOf(dark, base, bright),
            params = FusionParams(
                mode = FusionMode.Exposure,
                alignImages = false
            )
        )

        assertNotNull(result)
        assertEquals(base.width, result!!.width)
        assertEquals(base.height, result.height)
        val luminance = result.averageLuminance()
        assertTrue("Unexpectedly dark fusion: $luminance", luminance > 20.0)
        assertTrue("Unexpectedly bright fusion: $luminance", luminance < 235.0)
    }

    @Test
    fun focusStackKeepsSharpDetailsFromBothFrames() {
        val first = halfSharpBitmap(sharpLeft = true)
        val second = halfSharpBitmap(sharpLeft = false)

        val result = engine.fuse(
            bitmaps = listOf(first, second),
            params = FusionParams(
                mode = FusionMode.Focus,
                alignImages = false,
                focusRadius = 3,
                focusStrength = 4f
            )
        )

        assertNotNull(result)
        result!!
        val leftContrast = channelDifference(result.getPixel(10, 20), result.getPixel(11, 20))
        val rightContrast = channelDifference(result.getPixel(82, 20), result.getPixel(83, 20))
        assertTrue("Left side lost focus: $leftContrast", leftContrast > 80)
        assertTrue("Right side lost focus: $rightContrast", rightContrast > 80)
    }

    @Test
    fun focusStackNormalizesExposureBetweenFrames() {
        val base = gradientBitmap(width = 96, height = 64)
        val dark = adjustBrightness(base, 0.4f)
        val bright = adjustBrightness(base, 1.8f)

        val result = engine.fuse(
            bitmaps = listOf(dark, base, bright),
            params = FusionParams(
                mode = FusionMode.Focus,
                alignImages = false,
                focusRadius = 3,
                focusStrength = 4f
            )
        )

        assertNotNull(result)
        val luminanceDifference = abs(result!!.averageLuminance() - base.averageLuminance())
        assertTrue("Focus fusion changed exposure: $luminanceDifference", luminanceDifference < 15)
        assertTrue(
            "Focus fusion produced exposure bands",
            result.maximumRowLuminanceJump() < 10
        )
    }

    @Test
    fun alignmentAndOverlapCropHandleShiftedFrame() {
        val base = featureBitmap(width = 320, height = 240)
        val shifted = createBitmap(base.width, base.height).also { bitmap ->
            Canvas(bitmap).drawBitmap(base, 9f, 6f, null)
        }
        var alignedFrames = 0

        val result = engine.fuse(
            bitmaps = listOf(base, shifted),
            params = FusionParams(
                mode = FusionMode.Median,
                alignImages = true,
                cropToOverlap = true
            ),
            onFrameAligned = { alignedFrames = it }
        )

        assertNotNull(result)
        assertEquals(2, alignedFrames)
        assertTrue(result!!.width in 240..base.width)
        assertTrue(result.height in 180..base.height)
    }

    @Test
    fun original8kDimensionsArePreserved() {
        val first = createBitmap(7680, 512).apply {
            eraseColor(Color.rgb(32, 96, 160))
        }
        val second = createBitmap(first.width, first.height).apply {
            eraseColor(Color.rgb(48, 112, 176))
        }

        val result = engine.fuse(
            bitmaps = listOf(first, second),
            params = FusionParams(
                mode = FusionMode.Median,
                alignImages = false
            )
        )

        assertNotNull(result)
        assertEquals(7680, result!!.width)
        assertEquals(512, result.height)
    }

    private fun solidBitmap(color: Int): Bitmap = createBitmap(48, 32).apply {
        eraseColor(color)
    }

    private fun gradientBitmap(width: Int, height: Int): Bitmap =
        createBitmap(width, height).apply {
            repeat(height) { y ->
                repeat(width) { x ->
                    val red = 20 + x * 180 / width
                    val green = 25 + y * 170 / height
                    val blue = 30 + (x + y) * 160 / (width + height)
                    setPixel(x, y, Color.rgb(red, green, blue))
                }
            }
        }

    private fun adjustBrightness(source: Bitmap, multiplier: Float): Bitmap =
        createBitmap(source.width, source.height).apply {
            repeat(height) { y ->
                repeat(width) { x ->
                    val color = source.getPixel(x, y)
                    setPixel(
                        x,
                        y,
                        Color.rgb(
                            (Color.red(color) * multiplier).toInt().coerceIn(0, 255),
                            (Color.green(color) * multiplier).toInt().coerceIn(0, 255),
                            (Color.blue(color) * multiplier).toInt().coerceIn(0, 255)
                        )
                    )
                }
            }
        }

    private fun halfSharpBitmap(sharpLeft: Boolean): Bitmap = createBitmap(96, 64).apply {
        repeat(height) { y ->
            repeat(width) { x ->
                val isSharp = if (sharpLeft) x < width / 2 else x >= width / 2
                val value = if (isSharp) {
                    if ((x + y) % 2 == 0) 24 else 232
                } else {
                    128
                }
                setPixel(x, y, Color.rgb(value, value, value))
            }
        }
    }

    private fun featureBitmap(width: Int, height: Int): Bitmap = createBitmap(width, height).apply {
        eraseColor(Color.rgb(24, 28, 36))
        val canvas = Canvas(this)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        repeat(70) { index ->
            paint.color = Color.rgb(
                40 + index * 37 % 200,
                40 + index * 71 % 200,
                40 + index * 97 % 200
            )
            val x = (index * 83 % (width - 24)).toFloat()
            val y = (index * 47 % (height - 24)).toFloat()
            if (index % 2 == 0) {
                canvas.drawCircle(x + 8f, y + 8f, 4f + index % 7, paint)
            } else {
                canvas.drawRect(x, y, x + 7f + index % 13, y + 7f + index % 11, paint)
            }
        }
    }

    private fun Bitmap.averageLuminance(): Double {
        var total = 0L
        repeat(height) { y ->
            repeat(width) { x ->
                val color = getPixel(x, y)
                total += (Color.red(color) + Color.green(color) + Color.blue(color)) / 3
            }
        }
        return total.toDouble() / (width * height)
    }

    private fun Bitmap.maximumRowLuminanceJump(): Double {
        val rowAverages = DoubleArray(height) { y ->
            var total = 0L
            repeat(width) { x ->
                val color = getPixel(x, y)
                total += (Color.red(color) + Color.green(color) + Color.blue(color)) / 3
            }
            total.toDouble() / width
        }
        return rowAverages.asList()
            .zipWithNext { first, second -> abs(first - second) }
            .maxOrNull()
            ?: 0.0
    }

    private fun assertColorClose(expected: Int, actual: Int, tolerance: Int) {
        assertTrue(abs(Color.red(expected) - Color.red(actual)) <= tolerance)
        assertTrue(abs(Color.green(expected) - Color.green(actual)) <= tolerance)
        assertTrue(abs(Color.blue(expected) - Color.blue(actual)) <= tolerance)
    }

    private fun channelDifference(first: Int, second: Int): Int =
        abs(Color.red(first) - Color.red(second))
}
