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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.core.graphics.createBitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.model.pt
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.SpotHealMode
import com.t8rin.imagetoolbox.core.ui.utils.helper.scaleToFitCanvas
import com.t8rin.imagetoolbox.feature.draw.domain.DrawMode
import com.t8rin.imagetoolbox.feature.draw.domain.DrawPathMode
import com.t8rin.imagetoolbox.feature.draw.presentation.components.UiPathPaint
import com.t8rin.imagetoolbox.feature.draw.presentation.components.drawCommittedPath
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import android.graphics.Color as NativeColor

@RunWith(AndroidJUnit4::class)
class SpotHealMaskTest {
    private val size = IntegerSize(200, 200)
    private val mode = DrawMode.SpotHeal(SpotHealMode.LaMa)

    private fun stroke(y: Float, width: Int = 50, canvas: IntegerSize = size) = UiPathPaint(
        path = Path().apply {
            moveTo(canvas.width * .2f, canvas.height * y)
            lineTo(canvas.width * .8f, canvas.height * y)
        },
        strokeWidth = width.pt,
        brushSoftness = 0.pt,
        drawColor = Color.Red,
        isErasing = false,
        drawMode = mode,
        canvasSize = canvas,
        isPendingSpotHeal = true
    )

    private fun mask(paths: List<UiPathPaint>, target: IntegerSize = size): Bitmap =
        createBitmap(target.width, target.height).apply {
            eraseColor(NativeColor.BLACK)
            val canvas = Canvas(this)
            paths.forEach {
                canvas.drawPath(
                    it.path.scaleToFitCanvas(target, it.canvasSize).asAndroidPath(),
                    pathEffectPaint(it.strokeWidth, it.drawPathMode, target).apply {
                        color = NativeColor.WHITE
                        xfermode = null
                        isAntiAlias = false
                    }
                )
            }
        }

    @Test
    fun combinedMaskKeepsWidthsShapesAndViewportCoordinates() {
        val entries = listOf(
            stroke(.2f, 20),
            stroke(.5f, 80, IntegerSize(400, 400)),
            stroke(.8f).copy(
                path = Path().apply { addRect(Rect(40f, 150f, 160f, 180f)) },
                drawPathMode = DrawPathMode.Rect()
            )
        )
        val combined = checkNotNull(entries.combineSpotHealPaths(mode))
        assertEquals(mode, combined.drawMode)
        assertTrue(!combined.isPendingSpotHeal)
        for (target in listOf(size, IntegerSize(600, 600))) {
            val expected = mask(entries, target)
            val actual = mask(listOf(combined), target)
            assertTrue("Batch mask changed at $target", expected.sameAs(actual))
            expected.recycle()
            actual.recycle()
        }
    }

    @Test
    fun overlappingFilledPathsKeepTheirHolesAndUnion() {
        val ring = stroke(.5f).copy(
            path = Path().apply {
                fillType = androidx.compose.ui.graphics.PathFillType.EvenOdd
                addRect(Rect(20f, 20f, 180f, 180f))
                addRect(Rect(50f, 50f, 150f, 150f))
            },
            drawPathMode = DrawPathMode.Lasso
        )
        val entries = listOf(ring, stroke(.8f, 30), ring)
        val expected = mask(entries)
        val actual = mask(listOf(checkNotNull(entries.combineSpotHealPaths(mode))))
        assertTrue(expected.sameAs(actual))
        assertEquals(NativeColor.BLACK, actual.getPixel(100, 100))
        expected.recycle()
        actual.recycle()
    }

    @Test
    fun batchRequestsOneFilterWithAllSpotsAndTheSelectedAlgorithm() = runBlocking {
        val combined = checkNotNull(listOf(stroke(.25f), stroke(.75f)).combineSpotHealPaths(mode))
        val source = createBitmap(size.width, size.height).apply { eraseColor(NativeColor.WHITE) }
        val result = createBitmap(size.width, size.height)
        var calls = 0
        Canvas(result).drawCommittedPath(
            combined, size, InstrumentationRegistry.getInstrumentation().targetContext,
            source = { source },
            onRequestFiltering = { input, filters ->
                calls++
                assertTrue(input.sameAs(source))
                val filter = filters.single() as Filter.SpotHeal
                assertEquals(SpotHealMode.LaMa, filter.value.second)
                val mask = filter.value.first.data as Bitmap
                assertEquals(NativeColor.WHITE, mask.getPixel(100, 50))
                assertEquals(NativeColor.WHITE, mask.getPixel(100, 150))
                assertEquals(NativeColor.BLACK, mask.getPixel(100, 100))
                createBitmap(size.width, size.height).apply { eraseColor(NativeColor.CYAN) }
            }
        )
        assertEquals(1, calls)
        assertEquals(NativeColor.CYAN, result.getPixel(100, 50))
        assertEquals(NativeColor.CYAN, result.getPixel(100, 150))
        assertEquals(NativeColor.TRANSPARENT, result.getPixel(100, 100))
        source.recycle()
        result.recycle()
    }

    @Test
    fun applyingPendingMarksKeepsOrdinaryStrokesAndUsesTheSelectedAlgorithm() {
        val pen = stroke(.5f).copy(drawMode = DrawMode.Pen, isPendingSpotHeal = false)
        val pending = stroke(.25f).copy(drawMode = DrawMode.SpotHeal())
        val history = listOf(pending, pen, stroke(.75f))
        val applied = history.applySpotHeal(mode)
        assertEquals(2, applied.size)
        assertTrue(applied.first() === pen)
        assertTrue(applied.none { it.isPendingSpotHeal })
        assertEquals(mode, applied.last().drawMode)
        val actual = mask(listOf(applied.last()))
        assertEquals(NativeColor.WHITE, actual.getPixel(100, 50))
        assertEquals(NativeColor.WHITE, actual.getPixel(100, 150))
        assertEquals(NativeColor.BLACK, actual.getPixel(100, 100))
        actual.recycle()
    }

    @Test
    fun pendingMarksCanBeAppliedAfterChangingTools() {
        val pending = listOf(stroke(.25f), stroke(.75f))
        val applied = pending.applySpotHeal(DrawMode.Pen)
        assertEquals(1, applied.size)
        assertEquals(mode, applied.single().drawMode)
        assertTrue(applied === applied.applySpotHeal(mode))
    }

    @Test
    fun erasingSubtractsEarlierMasksAndAllowsPaintingAgain() {
        val painted = stroke(.5f, 200)
        val eraser = stroke(.5f, 40, IntegerSize(400, 400)).copy(
            isErasing = true,
            drawPathMode = DrawPathMode.Lasso
        )
        val cut = checkNotNull(listOf(painted, eraser).combineSpotHealPaths(mode))
        val result = mask(listOf(cut))
        assertEquals(NativeColor.BLACK, result.getPixel(100, 100))
        assertEquals(NativeColor.WHITE, result.getPixel(100, 75))
        assertTrue(!cut.isErasing)
        val repainted = mask(
            listOf(
                checkNotNull(
                    listOf(painted, eraser, stroke(.5f, 20)).combineSpotHealPaths(mode)
                )
            )
        )
        assertEquals(NativeColor.WHITE, repainted.getPixel(100, 100))
        assertEquals(NativeColor.BLACK, repainted.getPixel(100, 105))
        result.recycle()
        repainted.recycle()
    }

    @Test
    fun fullyErasedMaskDoesNotApplyOrEraseOrdinaryStrokes() {
        val pen = stroke(.5f).copy(drawMode = DrawMode.Pen, isPendingSpotHeal = false)
        val painted = stroke(.5f)
        val eraser = painted.copy(isErasing = true, strokeWidth = 100.pt)
        assertNull(listOf(painted, eraser).combineSpotHealPaths(mode))
        assertNull(listOf(eraser).combineSpotHealPaths(mode))
        assertEquals(listOf(pen), listOf(pen, painted, eraser).applySpotHeal(mode))
    }

    @Test
    fun emptyMaskDoesNotCreateAHealingOperation() {
        assertNull(emptyList<UiPathPaint>().combineSpotHealPaths(mode))
    }
}