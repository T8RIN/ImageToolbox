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
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.t8rin.imagetoolbox.core.domain.model.GradientPalette
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.model.pt
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.feature.draw.domain.DrawLineStyle
import com.t8rin.imagetoolbox.feature.draw.domain.DrawMode
import com.t8rin.imagetoolbox.feature.draw.domain.DrawPathMode
import com.t8rin.imagetoolbox.feature.draw.domain.WarpStroke
import com.t8rin.imagetoolbox.feature.draw.presentation.components.UiPathPaint
import com.t8rin.imagetoolbox.feature.draw.presentation.components.drawCommittedPath
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import android.graphics.Color as NativeColor

@RunWith(AndroidJUnit4::class)
class DrawHistoryTest {
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext
    private val size = IntegerSize(192, 192)
    private val base get() = bitmap().apply { eraseColor(NativeColor.WHITE) }
    private fun bitmap() = Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888)
    private fun history() =
        DrawHistoryCache<UiPathPaint>(size.width, size.height, NativeColor.TRANSPARENT)

    private fun path(second: Boolean = false) = Path().apply {
        if (second) {
            moveTo(35f, 25f); cubicTo(165f, 90f, 20f, 145f, 155f, 166f)
        } else {
            moveTo(25f, 135f); cubicTo(60f, 10f, 145f, 165f, 166f, 40f)
        }
    }

    private fun stroke(mode: DrawMode = DrawMode.Pen, second: Boolean = false) = UiPathPaint(
        path = path(second), strokeWidth = 90.pt, brushSoftness = 0.pt,
        drawColor = Color(0xCC1756AD), isErasing = false, drawMode = mode, canvasSize = size
    )

    private val filter: suspend (Bitmap, List<Filter<*>>) -> Bitmap? = { source, _ ->
        delay(1)
        bitmap().also {
            Canvas(it).drawBitmap(source, 0f, 0f, Paint().apply {
                colorFilter = ColorMatrixColorFilter(
                    ColorMatrix(
                        floatArrayOf(
                            -1f, 0f, 0f, 0f, 255f, 0f, -1f, 0f, 0f, 255f,
                            0f, 0f, -1f, 0f, 255f, 0f, 0f, 0f, 1f, 0f
                        )
                    )
                )
            })
        }
    }

    private suspend fun DrawHistoryCache<UiPathPaint>.render(paths: List<UiPathPaint>): Bitmap {
        val source = base.asImageBitmap()
        return render(paths) { canvas, layer, entry, _ ->
            canvas.drawCommittedPath(
                entry, size, context,
                { source.overlay(layer.asImageBitmap()).asAndroidBitmap() }, filter
            )
        }
    }

    @Test
    fun everyBrushPairSurvivesCommitUndoRedoAndBranching() = runBlocking {
        val stamp = bitmap().apply { eraseColor(NativeColor.GREEN) }
        val modes = DrawMode.entries.map {
            when (it) {
                is DrawMode.Image -> it.copy(imageData = stamp)
                is DrawMode.Warp -> it.copy(strokes = listOf(WarpStroke(60f, 80f, 95f, 95f)))
                else -> it
            }
        }
        val brushes = modes.map(::stroke) + listOf(
            stroke().copy(gradientPalette = GradientPalette.SoftRainbow),
            stroke().copy(isErasing = true, gradientPalette = GradientPalette.SoftRainbow)
        )
        for ((i, first) in brushes.withIndex()) for ((j, brush) in brushes.withIndex()) {
            val second = brush.copy(path = path(true))
            val cache = history()
            val before = cache.render(listOf(first))
            val beforeCopy = before.copy(Bitmap.Config.ARGB_8888, false)
            val after = cache.render(listOf(first, second))
            assertTrue("Published history mutated for $i/$j", before.sameAs(beforeCopy))
            assertTrue(
                "Commit differs for $i/$j",
                after.sameAs(history().render(listOf(first, second)))
            )
            assertTrue("Undo differs for $i/$j", before.sameAs(cache.render(listOf(first))))
            assertSame(
                "Redo recalculated a completed history for $i/$j",
                after,
                cache.render(listOf(first, second))
            )
            val branch = stroke(DrawMode.Highlighter, true)
            assertTrue(
                "Redo branch leaked for $i/$j",
                cache.render(listOf(first, branch)).sameAs(history().render(listOf(first, branch)))
            )
            beforeCopy.recycle()
        }
    }

    @Test
    fun shapesStylesSoftnessAndPalettesKeepHistoryStable() = runBlocking {
        val styles = listOf(
            DrawLineStyle.None,
            DrawLineStyle.Dashed(),
            DrawLineStyle.DotDashed,
            DrawLineStyle.ZigZag(),
            DrawLineStyle.Stamped<android.graphics.Path>(
                shape = android.graphics.Path()
                    .apply { addCircle(8f, 8f, 8f, android.graphics.Path.Direction.CW) })
        )
        val under = stroke().copy(gradientPalette = GradientPalette.SoftRainbow)
        for (mode in DrawPathMode.entries) for (style in styles) {
            val shape = stroke(second = true).copy(
                path = Path().apply { addOval(Rect(40f, 45f, 155f, 150f)) },
                drawPathMode = mode,
                drawLineStyle = style,
                gradientPalette = GradientPalette.Turbo,
                brushSoftness = 8.pt
            )
            val cache = history()
            cache.render(listOf(under))
            assertTrue(
                "Shape/style changed at commit: $mode / $style",
                cache.render(listOf(under, shape)).sameAs(history().render(listOf(under, shape)))
            )
        }
        for (palette in GradientPalette.entries) {
            val gradient = under.copy(gradientPalette = palette)
            val cache = history()
            val original = cache.render(listOf(gradient))
            cache.render(listOf(gradient, stroke(DrawMode.PathEffect.PrivacyBlur(), true)))
            assertSame("Undo lost palette $palette", original, cache.render(listOf(gradient)))
        }
    }

    @Test
    fun filterSeesOnlyItsOwnPrecedingLayersAndStaysInsideItsMask() = runBlocking {
        val first = stroke().copy(
            drawColor = Color.Red,
            path = Path().apply { addRect(Rect(0f, 0f, 192f, 192f)) },
            drawPathMode = DrawPathMode.Rect()
        )
        val effect = stroke(DrawMode.PathEffect.PrivacyBlur()).copy(path = Path().apply {
            moveTo(
                30f,
                96f
            ); lineTo(160f, 96f)
        })
        val later = first.copy(
            drawColor = Color.Blue,
            path = Path().apply { addRect(Rect(0f, 0f, 25f, 25f)) })
        var calls = 0
        val result = history().render(listOf(first, effect, later)) { canvas, layer, entry, _ ->
            canvas.drawCommittedPath(
                entry,
                size,
                context,
                { layer.copy(Bitmap.Config.ARGB_8888, false) },
                onRequestFiltering = { source, filters ->
                    calls++
                    assertEquals(
                        "A later stroke entered the filter source",
                        NativeColor.RED,
                        source.getPixel(5, 5)
                    )
                    filter(source, filters)
                })
        }
        assertEquals(1, calls)
        assertEquals("Filter erased outside its path", NativeColor.RED, result.getPixel(100, 30))
        assertEquals("Filtered stroke disappeared", NativeColor.CYAN, result.getPixel(100, 96))
        assertEquals(NativeColor.BLUE, result.getPixel(5, 5))
    }

    @Test
    fun aPreparedFilterCommitsWithoutRepeatingTheCalculation() = runBlocking {
        val target = base.apply { eraseColor(NativeColor.RED) }
        val prepared = base.apply { eraseColor(NativeColor.CYAN) }
        val entry = stroke(DrawMode.PathEffect.PrivacyBlur()).copy(
            path = Path().apply { moveTo(30f, 96f); lineTo(160f, 96f) }
        )
        Canvas(target).drawCommittedPath(
            entry, size, context,
            source = { error("Prepared filter requested its source again") },
            onRequestFiltering = { _, _ -> error("Prepared filter was recalculated") },
            preparedEffect = prepared
        )
        assertEquals(NativeColor.CYAN, target.getPixel(100, 96))
        assertEquals(NativeColor.RED, target.getPixel(100, 30))
        target.recycle()
        prepared.recycle()
    }

    @Test
    fun cancellationCannotPublishAnUndoneFilterOrMutateItsPrefix() = runBlocking {
        val cache = DrawHistoryCache<Int>(32, 32, NativeColor.WHITE)
        val before =
            cache.render(listOf(1)) { canvas, _, _, _ -> canvas.drawColor(NativeColor.RED) }
        val entered = CompletableDeferred<Unit>()
        val release = CompletableDeferred<Unit>()
        val job = async {
            cache.render(listOf(1, 2)) { canvas, _, _, _ ->
                canvas.drawColor(NativeColor.BLUE)
                entered.complete(Unit)
                release.await()
            }
        }
        entered.await()
        job.cancelAndJoin()
        val undone =
            cache.render(listOf(1)) { _, _, _, _ -> fail("Undo rerendered its cached prefix") }
        assertSame(before, undone)
        assertEquals(NativeColor.RED, undone.getPixel(10, 10))
    }

    @Test
    fun appendingAfterAFilterDoesNotRerenderOldGradientsOrRefilter() = runBlocking {
        val cache = history()
        val paths = listOf(
            stroke().copy(gradientPalette = GradientPalette.SoftRainbow),
            stroke(DrawMode.PathEffect.PrivacyBlur(), true)
        )
        var draws = 0
        suspend fun render(list: List<UiPathPaint>) =
            cache.render(list) { canvas, layer, entry, _ ->
            draws++
            canvas.drawCommittedPath(
                entry,
                size,
                context,
                { base.asImageBitmap().overlay(layer.asImageBitmap()).asAndroidBitmap() },
                filter
            )
        }
        render(paths)
        val more = paths + stroke(second = true)
        render(more)
        repeat(60) { render(more) }
        assertEquals("Completed brushes were rerendered", 3, draws)
    }

    @Test
    fun undoingFourQuickStrokesKeepsTheFilteredPrefix() = runBlocking {
        val cache = DrawHistoryCache<Int>(720, 1600, NativeColor.WHITE)
        var filterCalls = 0
        suspend fun render(paths: List<Int>) = cache.render(paths) { canvas, _, entry, _ ->
            if (entry == 0) filterCalls++
            canvas.drawColor(NativeColor.rgb(entry * 30, 0, 0))
        }

        val filtered = render(listOf(0))
        for (last in 1..4) render((0..last).toList())
        for (last in 3 downTo 0) render((0..last).toList())
        assertSame("Deep undo discarded the filtered prefix", filtered, render(listOf(0)))
        assertEquals("Undo recalculated the expensive filter", 1, filterCalls)
    }

    @Test
    fun healingSurvivesViewportChangesAndHistoryEviction() = runBlocking {
        val cache = DrawRenderCache()
        val original = base
        val session = cache.sessionFor(original, NativeColor.TRANSPARENT)
        val gradient = stroke().copy(gradientPalette = GradientPalette.SoftRainbow)
        val heal = stroke(DrawMode.SpotHeal(), true)
        val paths = listOf(gradient, heal)
        var calls = 0
        suspend fun render(entries: List<UiPathPaint>, extent: Int): Bitmap =
            session.render(
                entries, IntegerSize(extent, extent), original, context,
                onRequestFiltering = { input, _ ->
                    calls++
                    Bitmap.createBitmap(input.width, input.height, Bitmap.Config.ARGB_8888)
                        .apply { eraseColor(NativeColor.CYAN) }
                }
            )

        val before = render(paths, size.width)
        for (extent in listOf(128, 256, 192)) {
            assertSame(session, cache.sessionFor(original, NativeColor.TRANSPARENT))
            render(paths, extent)
            render(paths.take(1), extent)
            render(paths, extent)
        }
        var extended = paths
        repeat(12) { index ->
            extended += stroke().copy(drawColor = Color(index + NativeColor.RED))
            render(extended, size.width)
        }
        val restored = render(paths, size.width)
        assertEquals("SpotHeal was recalculated after resizing or cache eviction", 1, calls)
        assertTrue(before.sameAs(restored))
    }

    @Test
    fun healingIsInvalidatedByItsSourceAndPrecedingHistory() = runBlocking {
        val cache = DrawRenderCache()
        val original = base
        val heal = stroke(DrawMode.SpotHeal(), true)
        var calls = 0
        suspend fun render(source: Bitmap, background: Int, first: UiPathPaint) {
            cache.sessionFor(source, background).render(
                listOf(first, heal), size, source, context,
                onRequestFiltering = { input, _ ->
                    calls++
                    input.copy(Bitmap.Config.ARGB_8888, false)
                }
            )
        }

        val first = stroke()
        render(original, NativeColor.TRANSPARENT, first)
        render(original, NativeColor.TRANSPARENT, first.copy(drawColor = Color.Green))
        assertEquals("Changed preceding strokes reused an obsolete heal", 2, calls)
        render(original, NativeColor.TRANSPARENT, first)
        assertEquals("Returning to the original branch lost its heal", 2, calls)
        original.eraseColor(NativeColor.BLUE)
        render(original, NativeColor.TRANSPARENT, first)
        assertEquals("An edited source bitmap reused an obsolete heal", 3, calls)
        render(original, NativeColor.WHITE, first)
        assertEquals("Changing background reused an obsolete heal", 4, calls)
        render(original.copy(Bitmap.Config.ARGB_8888, false), NativeColor.WHITE, first)
        assertEquals("Another image reused an obsolete heal", 5, calls)
        cache.clear()
        render(original, NativeColor.TRANSPARENT, first)
        assertEquals("A cleared drawing retained its heal", 6, calls)
    }

    @Test
    fun clearingTheComponentCacheCannotPublishAnOldInFlightHeal() = runBlocking {
        val cache = DrawRenderCache()
        val source = base
        val oldSession = cache.sessionFor(source, NativeColor.TRANSPARENT)
        val paths = listOf(stroke(DrawMode.SpotHeal()))
        val entered = CompletableDeferred<Unit>()
        val release = CompletableDeferred<Unit>()
        val oldRender = async {
            oldSession.render(paths, size, source, context, onRequestFiltering = { input, _ ->
                entered.complete(Unit)
                release.await()
                input.copy(Bitmap.Config.ARGB_8888, true).apply { eraseColor(NativeColor.RED) }
            })
        }
        entered.await()
        cache.clear()
        val currentSession = cache.sessionFor(source, NativeColor.TRANSPARENT)
        var calls = 0
        suspend fun render() = currentSession.render(
            paths, size, source, context, onRequestFiltering = { input, _ ->
                calls++
                input.copy(Bitmap.Config.ARGB_8888, true).apply { eraseColor(NativeColor.BLUE) }
            }
        )

        val current = render()
        release.complete(Unit)
        oldRender.await()
        assertSame(current, render())
        assertEquals("Old work contaminated the new session", 1, calls)
    }


    @Test
    fun repeatedHealEntriesUseTheirActualPositionInHistory() = runBlocking {
        val source = base
        val paths = listOf(stroke(DrawMode.SpotHeal())).let { it + it }
        var calls = 0
        DrawRenderCache().sessionFor(source, NativeColor.TRANSPARENT).render(
            paths, size, source, context,
            onRequestFiltering = { input, _ ->
                calls++
                input.copy(Bitmap.Config.ARGB_8888, false)
            }
        )
        assertEquals("The second heal reused a result from a different prefix", 2, calls)
    }

}