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

package com.t8rin.imagetoolbox.feature.compare.data

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.core.graphics.createBitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.t8rin.gif_converter.GifDecoder
import com.t8rin.imagetoolbox.feature.compare.domain.CompareAnimationParams
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStream

@RunWith(AndroidJUnit4::class)
class CompareAnimationRendererTest {

    @Test
    fun horizontalAndVerticalWipesRevealTheCorrectImage() {
        withRenderer { renderer, output ->
            val canvas = Canvas(output)
            val params = CompareAnimationParams(showLabels = false)
            renderer.draw(canvas, 0f, params)
            assertEquals(Color.RED, output.getPixel(100, 50))
            renderer.draw(canvas, 1f, params)
            assertEquals(Color.BLUE, output.getPixel(100, 50))
            renderer.draw(canvas, 0.5f, params)
            assertEquals(Color.BLUE, output.getPixel(20, 50))
            assertEquals(Color.RED, output.getPixel(180, 50))
            renderer.draw(canvas, 0.5f, params.copy(isVertical = true))
            assertEquals(Color.BLUE, output.getPixel(100, 20))
            assertEquals(Color.RED, output.getPixel(100, 80))
        }
    }

    @Test
    fun previewCanvasSizeAndTranslationDoNotMoveTheLabels() {
        withRenderer { renderer, output ->
            val preview = createBitmap(400, 300)
            try {
                val params = CompareAnimationParams()
                renderer.draw(Canvas(output), 0f, params)
                val canvas = Canvas(preview)
                canvas.translate(40f, 60f)
                canvas.clipRect(0, 0, renderer.width, renderer.height)
                renderer.draw(canvas, 0f, params)
                for (y in 0 until output.height) {
                    for (x in 0 until output.width) {
                        assertEquals(output.getPixel(x, y), preview.getPixel(x + 40, y + 60))
                    }
                }
            } finally {
                preview.recycle()
            }
        }
    }

    @Test
    fun differentAspectRatiosAreFittedWithoutStretching() {
        val before = createBitmap(100, 100).apply { eraseColor(Color.RED) }
        val after = createBitmap(200, 100).apply { eraseColor(Color.BLUE) }
        val output = createBitmap(100, 100)
        try {
            CompareAnimationRenderer(before, after, 100, 100, "Before", "After").draw(
                Canvas(output), 1f, CompareAnimationParams(showLabels = false)
            )
            assertEquals(Color.WHITE, output.getPixel(50, 10))
            assertEquals(Color.BLUE, output.getPixel(50, 50))
            assertEquals(Color.WHITE, output.getPixel(50, 90))
        } finally {
            before.recycle()
            after.recycle()
            output.recycle()
        }
    }

    @Test
    fun encodedGifContainsBothImagesAndKeepsTheLoopDuration() = runBlocking {
        withRenderer { renderer, _ ->
            val params = CompareAnimationParams(durationSeconds = 2, showLabels = false)
            val output = ByteArrayOutputStream()
            var completedFrames = 0
            renderer.writeGif(output, params) { done, total ->
                assertEquals(completedFrames + 1, done)
                assertEquals(params.frames().size, total)
                completedFrames = done
            }
            val decoder = GifDecoder().apply { read(output.toByteArray()) }
            assertEquals(params.frames().size, decoder.frameCount)
            var duration = 0
            var sawBefore = false
            var sawAfter = false
            repeat(decoder.frameCount) {
                decoder.advance()
                duration += decoder.nextDelay
                val frame = requireNotNull(decoder.nextFrame)
                assertEquals(renderer.width, frame.width)
                assertEquals(renderer.height, frame.height)
                val color = frame.getPixel(100, 50)
                sawBefore = sawBefore || color == Color.RED
                sawAfter = sawAfter || color == Color.BLUE
            }
            assertEquals(params.durationMillis, duration)
            assertTrue(sawBefore && sawAfter)
        }
    }

    @Test
    fun partialFramesMatchFullFramesInBothDirectionsAndAtTheImageEdges() = runBlocking {
        val before = createBitmap(201, 103).apply {
            eraseColor(Color.RED)
            Canvas(this).apply {
                val paint = Paint().apply { color = Color.GREEN }
                drawRect(0f, 0f, 201f, 8f, paint)
                drawRect(0f, 95f, 201f, 103f, paint)
            }
        }
        val after = createBitmap(103, 201).apply {
            eraseColor(Color.BLUE)
            Canvas(this).apply {
                val paint = Paint().apply { color = Color.MAGENTA }
                drawRect(0f, 0f, 103f, 8f, paint)
                drawRect(0f, 193f, 103f, 201f, paint)
            }
        }
        val expected = createBitmap(201, 103)
        val renderer = CompareAnimationRenderer(before, after, 201, 103, "Before", "After")
        try {
            for ((vertical, labels) in listOf(
                false to false,
                true to false,
                false to true,
                true to true
            )) {
                val params = CompareAnimationParams(
                    durationSeconds = 2,
                    isVertical = vertical,
                    showLabels = labels
                )
                val output = ByteArrayOutputStream()
                renderer.writeGif(output, params) { _, _ -> }
                val decoder = GifDecoder().apply { read(output.toByteArray()) }
                assertEquals(params.frames().size, decoder.frameCount)
                params.frames().forEachIndexed { index, animationFrame ->
                    decoder.advance()
                    val frame = requireNotNull(decoder.nextFrame)
                    renderer.draw(Canvas(expected), animationFrame.position, params)
                    for (y in 0 until expected.height step 4) {
                        for (x in 0 until expected.width step 4) {
                            val reference = expected.getPixel(x, y)
                            val actual = frame.getPixel(x, y)
                            assertSimilarColor(
                                expected = reference,
                                actual = actual,
                                message = "Frame $index at ($x, $y), vertical=$vertical, labels=$labels",
                                // GIF quantization also changes the antialiased label colors.
                                tolerance = if (labels) 32 else 12
                            )
                        }
                    }
                }
            }
        } finally {
            before.recycle()
            after.recycle()
            expected.recycle()
        }
    }

    @Test
    fun changedFrameBoundsIncludeBothDividerPositionsWithoutExceedingTheCanvas() {
        withRenderer { renderer, _ ->
            for (vertical in listOf(false, true)) {
                val params = CompareAnimationParams(isVertical = vertical)
                var previous: Float? = null
                var encodedPixels = 0L
                for (frame in params.frames()) {
                    val bounds = renderer.frameBounds(previous, frame.position, params)
                    assertTrue(bounds.left >= 0 && bounds.top >= 0)
                    assertTrue(bounds.right <= renderer.width && bounds.bottom <= renderer.height)
                    assertTrue(bounds.width() > 0 && bounds.height() > 0)
                    encodedPixels += bounds.width().toLong() * bounds.height()
                    previous = frame.position
                }
                val fullFrames = renderer.width.toLong() * renderer.height * params.frames().size
                assertTrue(encodedPixels < fullFrames / 5)
            }
        }
    }

    @Test
    fun highResolutionExportKeepsAllFourEdgesOfLargePhotos() = runBlocking {
        val colors = listOf(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW)
        val images = List(2) { index ->
            createBitmap(4000, 2600).apply {
                eraseColor(Color.DKGRAY)
                Canvas(this).apply {
                    val paint = Paint()
                    paint.color = colors[index]
                    drawRect(0f, 0f, 4000f, 80f, paint)
                    paint.color = colors[(index + 1) % 4]
                    drawRect(0f, 2520f, 4000f, 2600f, paint)
                    paint.color = colors[(index + 2) % 4]
                    drawRect(0f, 80f, 80f, 2520f, paint)
                    paint.color = colors[(index + 3) % 4]
                    drawRect(3920f, 80f, 4000f, 2520f, paint)
                }
            }
        }
        try {
            val params = CompareAnimationParams(durationSeconds = 2, showLabels = false)
            val (width, height) = CompareAnimationParams.outputSize(4000, 2600, params.maxSize)
            val output = ByteArrayOutputStream()
            CompareAnimationRenderer(images[0], images[1], width, height, "Before", "After")
                .writeGif(output, params) { _, _ -> }
            val decoder = GifDecoder().apply { read(output.toByteArray()) }
            assertEquals(2048, width)
            params.frames().forEach { animationFrame ->
                decoder.advance()
                val frame = requireNotNull(decoder.nextFrame)
                assertEquals(width, frame.width)
                assertEquals(height, frame.height)
                if (animationFrame.durationMillis == 500) {
                    val index = animationFrame.position.toInt()
                    assertSimilarColor(colors[index], frame.getPixel(width / 2, 10))
                    assertSimilarColor(
                        colors[(index + 1) % 4],
                        frame.getPixel(width / 2, height - 10)
                    )
                    assertSimilarColor(colors[(index + 2) % 4], frame.getPixel(10, height / 2))
                    assertSimilarColor(
                        colors[(index + 3) % 4],
                        frame.getPixel(width - 10, height / 2)
                    )
                }
            }
        } finally {
            images.forEach(Bitmap::recycle)
        }
    }

    @Test
    fun outputFailureDoesNotReportSuccessfulEncoding() = runBlocking {
        withRenderer { renderer, _ ->
            val output = object : OutputStream() {
                override fun write(value: Int): Unit = throw IOException("Full storage")
            }
            try {
                renderer.writeGif(output, CompareAnimationParams()) { _, _ -> }
                fail("Encoding must fail when the output cannot be written")
            } catch (_: IllegalStateException) {
                // GifEncoder reports I/O failures as false; the wrapper must surface them.
            }
        }
    }

    @Test
    fun cancelledEncodingStopsBeforeProcessingTheNextFrame() {
        val job = Job()
        var completedFrames = 0
        try {
            runBlocking(job) {
                withRenderer { renderer, _ ->
                    renderer.writeGif(
                        ByteArrayOutputStream(),
                        CompareAnimationParams()
                    ) { done, _ ->
                        completedFrames = done
                        if (done == 2) job.cancel()
                    }
                    fail("Cancelled encoding must not complete")
                }
            }
            fail("Cancellation must be propagated")
        } catch (_: CancellationException) {
            assertEquals(2, completedFrames)
        }
    }

    private fun assertSimilarColor(
        expected: Int,
        actual: Int,
        message: String = "",
        tolerance: Int = 12
    ) {
        val difference = maxOf(
            kotlin.math.abs(Color.red(expected) - Color.red(actual)),
            kotlin.math.abs(Color.green(expected) - Color.green(actual)),
            kotlin.math.abs(Color.blue(expected) - Color.blue(actual))
        )
        assertTrue("$message: $expected != $actual", difference <= tolerance)
    }

    private inline fun withRenderer(block: (CompareAnimationRenderer, Bitmap) -> Unit) {
        val before = createBitmap(200, 100).apply { eraseColor(Color.RED) }
        val after = createBitmap(200, 100).apply { eraseColor(Color.BLUE) }
        val output = createBitmap(200, 100)
        try {
            block(CompareAnimationRenderer(before, after, 200, 100, "Before", "After"), output)
        } finally {
            before.recycle()
            after.recycle()
            output.recycle()
        }
    }
}