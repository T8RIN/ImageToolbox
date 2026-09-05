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

package com.t8rin.imagetoolbox.feature.draw.presentation.components.utils

import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.t8rin.imagetoolbox.core.domain.model.GradientPalette
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@RunWith(AndroidJUnit4::class)
class DrawGradientTest {

    @Test
    fun tightTurnsAndSelfIntersectionsKeepNativeStrokeCoverage() {
        val path = curl()
        for (softness in listOf(0f, 4f, 16f)) {
            val actual = render(path, softness = softness, alpha = 100)
            val expected = bitmap().also {
                Canvas(it).drawPath(path, paint(255, softness))
            }
            var maxDifference = 0
            var worstPixel = ""
            var expectedCoverage = 0L
            var actualCoverage = 0L
            for (y in 0 until SIZE) for (x in 0 until SIZE) {
                val expectedAlpha = (Color.alpha(expected.getPixel(x, y)) * 100 + 127) / 255
                val actualAlpha = Color.alpha(actual.getPixel(x, y))
                assertTrue("Stroke opacity accumulated at $x,$y", actualAlpha <= 100)
                expectedCoverage += expectedAlpha
                actualCoverage += actualAlpha
                val difference = abs(expectedAlpha - actualAlpha)
                if (difference > maxDifference) {
                    maxDifference = difference
                    worstPixel = "$x,$y: expected $expectedAlpha, actual $actualAlpha"
                }
            }
            // BlurMaskFilter approximates a Gaussian; the brush convolves RGBA exactly.
            // Allow their edge profiles to differ, while checking opacity and total coverage.
            val tolerance = if (softness == 0f) 2 else 10
            assertTrue(
                "Coverage differs by $maxDifference at softness $softness ($worstPixel)",
                maxDifference <= tolerance
            )
            assertTrue(
                "Stroke coverage changed",
                abs(actualCoverage - expectedCoverage) < expectedCoverage * 0.03f
            )
            actual.recycle()
            expected.recycle()
        }
    }

    @Test
    fun roundCapsAndSingleDabsStayRound() {
        val line = Path().apply { moveTo(80f, 128f); lineTo(176f, 128f) }
        val image = render(line)
        assertEquals(255, Color.alpha(image.getPixel(65, 128)))
        assertEquals(0, Color.alpha(image.getPixel(62, 110)))
        val dot = render(Path().apply { moveTo(128f, 128f); lineTo(128f, 128f) })
        assertEquals(255, Color.alpha(dot.getPixel(128, 128)))
        assertEquals(0, Color.alpha(dot.getPixel(110, 110)))
        image.recycle()
        dot.recycle()
    }

    @Test
    fun softDabPreservesItsColourThroughTheTransparentEdge() {
        val dot = render(Path().apply { moveTo(128f, 128f); lineTo(128f, 128f) }, softness = 12f)
        val colour = GradientPalette.RGB.colors.first().colorInt
        for (y in 80..176) for (x in 80..176) {
            val pixel = dot.getPixel(x, y)
            if (Color.alpha(pixel) >= 32) {
                val alpha = Color.alpha(pixel) / 255f
                for (shift in listOf(0, 8, 16)) {
                    val error =
                        abs(((pixel ushr shift) and 255) - ((colour ushr shift) and 255)) * alpha
                    assertTrue("Premultiplied edge error: $error", error <= 1.5f)
                }
            }
        }
        dot.recycle()
    }

    @Test
    fun squareCapsKeepTheirCorners() {
        val path = Path().apply { moveTo(80f, 128f); lineTo(176f, 128f) }
        val image = bitmap()
        Canvas(image).drawPathWithGradient(
            path, paint().apply { strokeCap = Paint.Cap.SQUARE },
            GradientPalette.RGB, false, IntegerSize(SIZE, SIZE)
        )
        assertEquals(255, Color.alpha(image.getPixel(61, 109)))
        image.recycle()
    }

    @Test
    fun repairingCoveragePreservesDashedLineGaps() {
        val path = Path().apply { moveTo(20f, 128f); lineTo(236f, 128f) }
        val brush = paint().apply {
            strokeWidth = 12f
            pathEffect = android.graphics.DashPathEffect(floatArrayOf(20f, 40f), 0f)
        }
        val actual = bitmap()
        val expected = bitmap()
        Canvas(actual).drawPathWithGradient(
            path, brush, GradientPalette.RGB, false, IntegerSize(SIZE, SIZE)
        )
        Canvas(expected).drawPath(path, brush)
        for (y in 0 until SIZE) for (x in 0 until SIZE) {
            assertTrue(
                "Dash coverage changed at $x,$y",
                abs(Color.alpha(actual.getPixel(x, y)) - Color.alpha(expected.getPixel(x, y))) <= 2
            )
        }
        actual.recycle()
        expected.recycle()
    }

    @Test
    fun softnessFeathersTheEdgeWithoutSegmentSeams() {
        val path = Path().apply { moveTo(50f, 128f); lineTo(206f, 128f) }
        val hard = render(path)
        val soft = render(path, softness = 12f)
        assertEquals(0, Color.alpha(hard.getPixel(128, 102)))
        assertTrue(Color.alpha(soft.getPixel(128, 102)) in 1..120)
        val edge = Color.alpha(soft.getPixel(100, 108))
        for (x in 80..175) {
            assertTrue(abs(Color.alpha(soft.getPixel(x, 108)) - edge) <= 2)
        }
        hard.recycle()
        soft.recycle()
    }

    @Test
    fun extendingAStrokeDoesNotRecolourItsExistingBody() {
        val short = render(Path().apply { moveTo(40f, 128f); lineTo(140f, 128f) })
        val long = render(Path().apply { moveTo(40f, 128f); lineTo(220f, 128f) })
        for (x in 45..115) for (y in 112..144) {
            assertEquals("Colour moved at $x,$y", short.getPixel(x, y), long.getPixel(x, y))
        }
        short.recycle()
        long.recycle()
    }

    @Test
    fun mirroredContoursUseTheSameColours() {
        val path = Path().apply {
            moveTo(30f, 60f); lineTo(210f, 60f)
            moveTo(30f, 190f); lineTo(210f, 190f)
        }
        val image = render(path)
        for (x in 35..205) assertEquals(image.getPixel(x, 60), image.getPixel(x, 190))
        image.recycle()
    }

    @Test
    fun closedContoursJoinWithoutAColourSeam() {
        val circle = render(Path().apply { addCircle(128f, 128f, 70f, Path.Direction.CW) })
        val before = circle.getPixel(198, 127)
        val after = circle.getPixel(198, 129)
        val difference = abs(Color.red(before) - Color.red(after)) +
                abs(Color.green(before) - Color.green(after)) +
                abs(Color.blue(before) - Color.blue(after))
        assertTrue("Closed contour colour seam: $difference", difference < 25)
        circle.recycle()
    }

    @Test
    fun exportScalePreservesTheGradient() {
        val path = curl()
        val small = render(path)
        val large = bitmap(SIZE * 3)
        val scaled = Path(path).apply { transform(Matrix().apply { setScale(3f, 3f) }) }
        Canvas(large).drawPathWithGradient(
            scaled, paint().apply { strokeWidth *= 3f }, GradientPalette.RGB,
            false, IntegerSize(SIZE * 3, SIZE * 3)
        )
        var totalError = 0L
        var samples = 0
        for (y in 1 until SIZE - 1) for (x in 1 until SIZE - 1) {
            val a = small.getPixel(x, y)
            val b = large.getPixel(x * 3 + 1, y * 3 + 1)
            val neighbours = listOf(
                small.getPixel(x - 1, y),
                small.getPixel(x + 1, y),
                small.getPixel(x, y - 1),
                small.getPixel(x, y + 1)
            )
            val smoothInterior = neighbours.all {
                Color.alpha(it) == 255 && abs(Color.red(it) - Color.red(a)) +
                        abs(Color.green(it) - Color.green(a)) + abs(Color.blue(it) - Color.blue(a)) < 30
            }
            if (Color.alpha(a) == 255 && Color.alpha(b) == 255 && smoothInterior) {
                totalError += abs(Color.red(a) - Color.red(b)) +
                        abs(Color.green(a) - Color.green(b)) + abs(Color.blue(a) - Color.blue(b))
                samples++
            }
        }
        assertTrue("Insufficient interior samples", samples > 5000)
        assertTrue(
            "Scale colour error: ${totalError.toFloat() / samples}",
            totalError.toFloat() / samples < 4f
        )
        small.recycle()
        large.recycle()
    }

    @Test
    fun renderVisualSamples() {
        val gallery = Bitmap.createBitmap(768, 768, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(gallery)
        canvas.drawColor(Color.rgb(245, 245, 245))
        for ((row, softness) in listOf(0f, 6f, 16f).withIndex()) {
            for ((column, path) in listOf(curl(), Path().apply {
                moveTo(40f, 60f); lineTo(190f, 60f); lineTo(70f, 190f); lineTo(210f, 190f)
            }, Path().apply {
                moveTo(30f, 130f)
                cubicTo(30f, 10f, 230f, 10f, 200f, 150f)
                cubicTo(170f, 260f, 50f, 240f, 60f, 120f)
                cubicTo(65f, 50f, 185f, 80f, 150f, 165f)
            }).withIndex()) {
                val image = render(path, softness, if (column == 1) 100 else 255)
                canvas.drawBitmap(image, column * 256f, row * 256f, null)
                image.recycle()
            }
        }
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        File(context.getExternalFilesDir(null), "gradient-brush-samples.png").outputStream().use {
            gallery.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        gallery.recycle()

        val largePath = curl().apply { transform(Matrix().apply { setScale(4f, 4f) }) }
        val target = bitmap(1024)
        val timings = mutableListOf<Long>()
        for (softness in listOf(0f, 24f)) repeat(3) {
            target.eraseColor(Color.TRANSPARENT)
            val started = System.nanoTime()
            Canvas(target).drawPathWithGradient(
                largePath, paint().apply { strokeWidth = 80f }, GradientPalette.RGB,
                false, IntegerSize(1024, 1024), softness
            )
            timings += (System.nanoTime() - started) / 1_000_000
        }
        File(
            context.getExternalFilesDir(null),
            "gradient-brush-timings.txt"
        ).writeText(timings.toString())
        target.recycle()
    }

    @Test
    fun wideCrossingsAtFullResolution() {
        val path = Path().apply {
            moveTo(230f, 200f)
            cubicTo(720f, 230f, 260f, 760f, 780f, 800f)
            cubicTo(1130f, 820f, 900f, 190f, 380f, 590f)
            cubicTo(-160f, 1030f, 650f, 1070f, 700f, 150f)
        }
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        for (width in listOf(40f, 180f, 350f)) {
            val target = bitmap(1200)
            val durations = mutableListOf<Long>()
            repeat(5) {
                target.eraseColor(Color.WHITE)
                val started = System.nanoTime()
                Canvas(target).drawPathWithGradient(
                    path, paint().apply { strokeWidth = width }, GradientPalette.Turbo,
                    false, IntegerSize(1200, 1200)
                )
                durations += (System.nanoTime() - started) / 1_000_000
            }
            File(context.getExternalFilesDir(null), "crossings-${width.toInt()}.png").outputStream()
                .use {
                    target.compress(Bitmap.CompressFormat.PNG, 100, it)
                }
            File(context.getExternalFilesDir(null), "crossings-${width.toInt()}.txt").writeText(
                durations.toString()
            )
            target.recycle()
        }
    }

    @Test
    fun incrementalStrokeMatchesFreshRender() {
        val cache = GradientStrokeCache()
        val target = bitmap(1200)
        val path = Path()
        val durations = mutableListOf<Long>()
        val brush = paint(150).apply { strokeWidth = 180f }
        try {
            for (index in 0..160) {
                val t = index * 0.055f
                val x = 600f + 420f * sin(t)
                val y = 600f + 420f * sin(t * 1.8f)
                if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                target.eraseColor(Color.TRANSPARENT)
                val started = System.nanoTime()
                Canvas(target).drawPathWithGradient(
                    path, brush, GradientPalette.RGB, false, IntegerSize(1200, 1200), cache = cache
                )
                if (index > 20) durations += (System.nanoTime() - started) / 1_000_000
                if (index in listOf(20, 79, 160)) {
                    val expected = bitmap(1200)
                    GradientStrokeCache().let { fresh ->
                        Canvas(expected).drawPathWithGradient(
                            path,
                            brush,
                            GradientPalette.RGB,
                            false,
                            IntegerSize(1200, 1200),
                            cache = fresh
                        )
                        fresh.clear()
                    }
                    assertTrue(
                        "Incremental rendering changed event $index",
                        expected.sameAs(target)
                    )
                    expected.recycle()
                }
            }
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            File(context.getExternalFilesDir(null), "incremental-timings.txt").writeText(
                "median=${durations.sorted()[durations.size / 2]}, p95=${durations.sorted()[(durations.size * 0.95).toInt()]}, max=${durations.max()} ms"
            )
            File(context.getExternalFilesDir(null), "incremental-crossings.png").outputStream()
                .use {
                    target.compress(Bitmap.CompressFormat.PNG, 100, it)
                }
            path.reset()
            path.moveTo(200f, 150f)
            path.quadTo(800f, 1100f, 1000f, 250f)
            for (softness in listOf(0f, 24f)) {
                target.eraseColor(Color.TRANSPARENT)
                Canvas(target).drawPathWithGradient(
                    path,
                    brush,
                    GradientPalette.RGB,
                    false,
                    IntegerSize(1200, 1200),
                    softness,
                    cache
                )
                val expected = bitmap(1200)
                GradientStrokeCache().let { fresh ->
                    Canvas(expected).drawPathWithGradient(
                        path,
                        brush,
                        GradientPalette.RGB,
                        false,
                        IntegerSize(1200, 1200),
                        softness,
                        fresh
                    )
                    fresh.clear()
                }
                assertTrue("Old colour remained at softness $softness", expected.sameAs(target))
                expected.recycle()
            }
        } finally {
            cache.clear()
            target.recycle()
        }
    }

    @Test
    fun internalCrossingsHavePixelSizedAntialiasing() {
        val size = 1200
        val width = 180f
        val colours =
            GradientPalette.Turbo.colors.map { it.colorInt }.let { it + it.first() }.toIntArray()
        val base = bitmap(size)
        val over = bitmap(size)
        val actual = bitmap(size)
        val path = Path().apply {
            moveTo(100f, 500f)
            lineTo(1100f, 500f)
        }
        Canvas(base).drawPathWithGradient(
            path,
            paint().apply { strokeWidth = width },
            GradientPalette.Turbo,
            false,
            IntegerSize(size, size)
        )
        path.lineTo(1100f, 1000f)
        path.lineTo(100f, 200f)
        Canvas(actual).drawPathWithGradient(
            path,
            paint().apply { strokeWidth = width },
            GradientPalette.Turbo,
            false,
            IntegerSize(size, size)
        )
        val length = sqrt(1000f * 1000f + 800f * 800f)
        val tx = -1000f / length
        val ty = -800f / length
        val gradient =
            LinearGradient(
                0f,
                0f,
                size.toFloat(),
                0f,
                colours,
                null,
                Shader.TileMode.REPEAT
            ).apply {
                setLocalMatrix(Matrix().apply {
                    setValues(
                        floatArrayOf(
                            tx,
                            -ty,
                            1100f - tx * 1500f,
                            ty,
                            tx,
                            1000f - ty * 1500f,
                            0f,
                            0f,
                            1f
                        )
                    )
                })
            }
        Canvas(over).drawPaint(Paint().apply { shader = gradient })
        var mixedPixels = 0
        var rows = 0
        for (y in 430 until 570) {
            val edge = 1100f + (91f - tx * (y + 0.5f - 1000f)) / -ty
            var mixedInRow = 0
            var contrasted = false
            for (x in edge.toInt() - 30..edge.toInt() + 30) {
                val a = base.getPixel(x, y)
                val b = over.getPixel(x, y)
                val c = actual.getPixel(x, y)
                var norm = 0f
                var projection = 0f
                for (shift in listOf(0, 8, 16)) {
                    val delta = ((b ushr shift) and 255) - ((a ushr shift) and 255)
                    norm += delta * delta
                    projection += (((c ushr shift) and 255) - ((a ushr shift) and 255)) * delta
                }
                if (norm > 4000f) {
                    contrasted = true
                    if (projection / norm in 0.03f..0.97f) mixedInRow++
                }
            }
            assertTrue("Enlarged colour pixels at row $y: $mixedInRow", mixedInRow <= 2)
            if (contrasted) {
                rows++
                mixedPixels += mixedInRow
            }
        }
        assertTrue(
            "Crossing has no subpixel antialiasing ($mixedPixels/$rows)",
            mixedPixels > rows / 10
        )
        base.recycle()
        over.recycle()
        actual.recycle()
    }

    @Test
    fun slowInputDoesNotLeaveCircularColourStamps() {
        val size = 1000
        val path = Path()
        var previousX = 0f
        var previousY = 0f
        for (index in 0..2800) {
            val t = index * 0.002f
            // Subpixel advance with sideways input jitter, as with a slow finger or mouse.
            val x = 500f + 260f * cos(t) + 1.2f * sin(index * 1.73f)
            val y = 500f + 330f * sin(t) + 0.9f * cos(index * 2.13f)
            if (index == 0) path.moveTo(x, y) else {
                path.quadTo(previousX, previousY, (previousX + x) / 2f, (previousY + y) / 2f)
            }
            previousX = x
            previousY = y
        }
        val target = bitmap(size)
        Canvas(target).drawPathWithGradient(
            path, paint().apply { strokeWidth = 150f }, GradientPalette.RGB,
            false, IntegerSize(size, size)
        )
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        File(context.getExternalFilesDir(null), "slow-stroke.png").outputStream().use {
            target.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        val interior = bitmap(size)
        val ideal = Path().apply {
            moveTo(760f, 500f)
            for (index in 1..2800) {
                val t = index * 0.002f
                lineTo(500f + 260f * cos(t), 500f + 330f * sin(t))
            }
        }
        Canvas(interior).drawPath(ideal, paint().apply { strokeWidth = 138f })
        var gradientHoles = 0
        for (y in 0 until size) for (x in 0 until size) {
            if (Color.alpha(interior.getPixel(x, y)) == 255) {
                if (Color.alpha(target.getPixel(x, y)) != 255) gradientHoles++
            }
        }
        assertEquals("Dense input left holes inside the brush", 0, gradientHoles)
        interior.recycle()
        // Check the centre and both shoulders, where rotating full-width dabs used
        // to expose their circular rims even though the stroke's alpha was correct.
        var maxJump = 0
        var jumps = 0
        for (index in 80..2660) {
            val t = index * 0.002f
            val dx = -260f * sin(t)
            val dy = 330f * cos(t)
            val length = sqrt(dx * dx + dy * dy)
            for (offset in listOf(-45f, 0f, 45f)) {
                val x = (500f + 260f * cos(t) - dy / length * offset).toInt()
                val y = (500f + 330f * sin(t) + dx / length * offset).toInt()
                val a = target.getPixel(x, y)
                val neighbours = listOf(target.getPixel(x + 1, y), target.getPixel(x, y + 1))
                for (pixel in neighbours) {
                    if (Color.alpha(a) != 255 || Color.alpha(pixel) != 255) continue
                    val jump = abs(Color.red(a) - Color.red(pixel)) +
                            abs(Color.green(a) - Color.green(pixel)) +
                            abs(Color.blue(a) - Color.blue(pixel))
                    maxJump = maxOf(maxJump, jump)
                    if (jump > 20) jumps++
                }
            }
        }
        File(
            context.getExternalFilesDir(null),
            "slow-stroke-jumps.txt"
        ).writeText("max=$maxJump, jumps=$jumps")
        assertTrue(
            "Circular colour seams: maximum pixel jump $maxJump ($jumps jumps)",
            maxJump <= 20
        )
        target.recycle()
    }

    private fun render(path: Path, softness: Float = 0f, alpha: Int = 255): Bitmap = bitmap().also {
        Canvas(it).drawPathWithGradient(
            path, paint(alpha), GradientPalette.RGB, false, IntegerSize(SIZE, SIZE), softness
        )
    }

    private fun bitmap(size: Int = SIZE) = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

    private fun paint(opacity: Int = 255, softness: Float = 0f) =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            alpha = opacity
            style = Paint.Style.STROKE
            strokeWidth = 40f
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            if (softness > 0f) maskFilter = BlurMaskFilter(softness, BlurMaskFilter.Blur.NORMAL)
        }

    private fun curl() = Path().apply {
        moveTo(30f, 180f)
        cubicTo(220f, 0f, 250f, 180f, 130f, 170f)
        cubicTo(20f, 160f, 70f, 25f, 125f, 100f)
        lineTo(125f, 190f)
        lineTo(145f, 110f)
        lineTo(205f, 220f)
    }

    private companion object {
        const val SIZE = 256
    }
}
