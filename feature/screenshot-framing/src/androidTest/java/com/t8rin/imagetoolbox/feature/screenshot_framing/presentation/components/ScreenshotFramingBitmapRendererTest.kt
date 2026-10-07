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

package com.t8rin.imagetoolbox.feature.screenshot_framing.presentation.components

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.createBitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.t8rin.imagetoolbox.core.domain.model.DomainAspectRatio
import com.t8rin.imagetoolbox.feature.screenshot_framing.presentation.model.ScreenshotFramingParams
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.abs
import androidx.compose.ui.graphics.Color as ComposeColor

@RunWith(AndroidJUnit4::class)
class ScreenshotFramingBitmapRendererTest {

    private val plainParams = ScreenshotFramingParams(
        backgroundColors = listOf(ComposeColor.White, ComposeColor.White),
        useGradient = false,
        padding = 0,
        cornerRadius = 0,
        showShadow = false
    )

    @Test
    fun landscapeCanvasFitsTheWholePortraitImage() {
        val image = createBitmap(100, 200).apply {
            eraseColor(Color.RED)
            for (x in 0 until width) {
                setPixel(x, 0, Color.GREEN)
                setPixel(x, height - 1, Color.BLUE)
            }
        }
        val result = renderScreenshotFramingBitmap(
            image,
            plainParams.copy(aspectRatio = DomainAspectRatio.Numeric(16f, 9f))
        )

        assertTrue(abs(result.width.toFloat() / result.height - 16f / 9f) < 0.01f)
        assertEquals(Color.GREEN, result.getPixel(result.width / 2, 0))
        assertEquals(Color.BLUE, result.getPixel(result.width / 2, result.height - 1))
        assertEquals(Color.RED, result.getPixel(result.width / 2, result.height / 2))
        assertEquals(Color.WHITE, result.getPixel(0, result.height / 2))
        image.recycle()
        result.recycle()
    }

    @Test
    fun automaticCanvasKeepsSourceDimensionsWithoutPadding() {
        val image = solidImage(100, 240)
        val result = renderScreenshotFramingBitmap(image, plainParams)

        assertEquals(100, result.width)
        assertEquals(240, result.height)
        assertTrue(image.sameAs(result))
        image.recycle()
        result.recycle()
    }

    @Test
    fun predefinedAndCustomCanvasRatiosArePreserved() {
        val image = solidImage(300, 100)
        val aspectRatios = DomainAspectRatio.defaultList + DomainAspectRatio.Custom(7f, 11f)
        aspectRatios.forEach { aspectRatio ->
            val ratio = aspectRatio.value.takeIf { it > 0f } ?: return@forEach
            val result = renderScreenshotFramingBitmap(
                image,
                ScreenshotFramingParams(aspectRatio = aspectRatio)
            )
            assertTrue(
                "Expected $ratio, got ${result.width} x ${result.height}",
                abs(result.width - result.height * ratio) <= maxOf(1f, ratio)
            )
            result.recycle()
        }
        image.recycle()
    }

    @Test
    fun previewAndExportUseTheSameRelativeLayout() {
        val smallImage = solidImage(100, 200)
        val largeImage = solidImage(400, 800)
        val params = plainParams.copy(padding = 20, cornerRadius = 10)
        val preview = renderScreenshotFramingBitmap(smallImage, params)
        val export = renderScreenshotFramingBitmap(largeImage, params)

        assertEquals(preview.width * 4, export.width)
        assertEquals(preview.height * 4, export.height)
        listOf(0.1f, 0.3f, 0.5f, 0.7f, 0.9f).forEach { fraction ->
            assertEquals(
                preview.getPixel((preview.width * fraction).toInt(), preview.height / 2),
                export.getPixel((export.width * fraction).toInt(), export.height / 2)
            )
        }
        smallImage.recycle()
        largeImage.recycle()
        preview.recycle()
        export.recycle()
    }

    @Test
    fun roundedCornersRevealTheBackground() {
        val image = solidImage(100, 100)
        val result = renderScreenshotFramingBitmap(
            image,
            plainParams.copy(cornerRadius = 20)
        )

        assertEquals(Color.WHITE, result.getPixel(0, 0))
        assertEquals(Color.RED, result.getPixel(50, 0))
        assertEquals(Color.RED, result.getPixel(50, 50))
        image.recycle()
        result.recycle()
    }

    @Test
    fun shadowDoesNotFillTransparentSourcePixels() {
        val image = createBitmap(100, 100)
        val result = renderScreenshotFramingBitmap(
            image,
            plainParams.copy(showShadow = true, shadowBlur = 6, shadowOpacity = 80)
        )

        assertEquals(Color.WHITE, result.getPixel(result.width / 2, result.height / 2))
        assertEquals(Color.WHITE, result.getPixel(0, 0))
        val shadowPixel = result.getPixel(result.width / 2, result.height - 10)
        assertTrue("Expected a visible shadow outside the image", Color.red(shadowPixel) < 250)
        image.recycle()
        result.recycle()
    }

    @Test
    fun gradientUsesTheMiddlePaletteColors() {
        val image = createBitmap(100, 100)
        val result = renderScreenshotFramingBitmap(
            image,
            plainParams.copy(
                useGradient = true,
                backgroundColors = listOf(ComposeColor.Red, ComposeColor.Green, ComposeColor.Blue)
            )
        )

        assertTrue(Color.red(result.getPixel(0, 0)) > 250)
        val middle = result.getPixel(result.width / 2, result.height / 2)
        assertTrue(Color.green(middle) > 240)
        assertTrue(Color.red(middle) < 10 && Color.blue(middle) < 10)
        assertTrue(Color.blue(result.getPixel(result.width - 1, result.height - 1)) > 250)
        image.recycle()
        result.recycle()
    }

    @Test
    fun largeCanvasStaysWithinTheRequestedPixelBudget() {
        val image = solidImage(100, 4000)
        val result = renderScreenshotFramingBitmap(
            image = image,
            params = ScreenshotFramingParams(
                aspectRatio = DomainAspectRatio.Numeric(16f, 9f),
                padding = 40,
                shadowBlur = 12
            ),
            maxBitmapPixels = 500_000f
        )

        assertTrue(result.width.toLong() * result.height <= 500_000L)
        assertTrue(result.width <= 8192 && result.height <= 8192)
        assertTrue(abs(result.width.toFloat() / result.height - 16f / 9f) < 0.01f)
        image.recycle()
        result.recycle()
    }

    private fun solidImage(width: Int, height: Int): Bitmap = createBitmap(width, height).apply {
        eraseColor(Color.RED)
    }
}