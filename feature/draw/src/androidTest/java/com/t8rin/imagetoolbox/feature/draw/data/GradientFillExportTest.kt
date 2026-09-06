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

import android.graphics.Canvas
import android.graphics.Color
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.graphics.createBitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.domain.model.GradientFill
import com.t8rin.imagetoolbox.core.domain.model.GradientPalette
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.model.pt
import com.t8rin.imagetoolbox.core.ui.utils.helper.toBrush
import com.t8rin.imagetoolbox.feature.draw.domain.DrawBehavior
import com.t8rin.imagetoolbox.feature.draw.domain.DrawMode
import com.t8rin.imagetoolbox.feature.draw.domain.DrawPathMode
import com.t8rin.imagetoolbox.feature.draw.presentation.components.UiPathPaint
import com.t8rin.imagetoolbox.feature.draw.presentation.components.drawCommittedPath
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.reflect.Proxy
import androidx.compose.ui.graphics.Canvas as ComposeCanvas
import androidx.compose.ui.graphics.Color as ComposeColor

@RunWith(AndroidJUnit4::class)
class GradientFillExportTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val applier = AndroidImageDrawApplier(context, unused(), unused(), unused())
    private val size = IntegerSize(256, 128)
    private val palette =
        GradientPalette.Custom(listOf(ColorModel(Color.RED), ColorModel(Color.BLUE)))

    @Test
    fun backgroundMatchesComposeWithDirectionAndTransparency() = runBlocking {
        val transparent = GradientPalette.Custom(
            listOf(
                ColorModel(0x80FF0000.toInt()),
                ColorModel(0x800000FF.toInt())
            )
        )
        for (colors in listOf(palette, transparent)) for (angle in listOf(
            0f,
            45f,
            90f,
            180f,
            270f
        )) {
            val gradient = GradientFill(colors, angle)
            val actual = checkNotNull(
                applier.applyDrawToImage(
                    DrawBehavior.Background(0, size.width, size.height, Color.GREEN, gradient),
                    emptyList(),
                    ""
                )
            )
            val expected = createBitmap(size.width, size.height)
            CanvasDrawScope().draw(
                Density(1f),
                LayoutDirection.Ltr,
                ComposeCanvas(Canvas(expected)),
                Size(256f, 128f)
            ) {
                drawRect(gradient.toBrush())
            }
            val expectedPixels = IntArray(size.width * size.height).also {
                expected.getPixels(it, 0, size.width, 0, 0, size.width, size.height)
            }
            val actualPixels = IntArray(expectedPixels.size).also {
                actual.getPixels(it, 0, size.width, 0, 0, size.width, size.height)
            }
            val difference =
                expectedPixels.indices.firstOrNull { expectedPixels[it] != actualPixels[it] }
            assertTrue(
                "Background changed at $angle, pixel $difference: " +
                        "${difference?.let { expectedPixels[it].toUInt().toString(16) }} / " +
                        "${difference?.let { actualPixels[it].toUInt().toString(16) }}",
                difference == null
            )
            if (colors == transparent) assertEquals(128, Color.alpha(actual.getPixel(128, 64)))
            if (angle == 0f) {
                assertTrue(Color.red(actual.getPixel(0, 64)) > 240)
                assertTrue(Color.blue(actual.getPixel(255, 64)) > 240)
            }
            expected.recycle()
            actual.recycle()
        }
    }

    @Test
    fun independentShapeFillMatchesCommittedPreviewAndKeepsOutline() = runBlocking {
        val entry = UiPathPaint(
            path = Path().apply { addRect(Rect(20f, 20f, 236f, 108f)) },
            strokeWidth = 10.pt,
            brushSoftness = 0.pt,
            drawColor = ComposeColor.Green,
            isErasing = false,
            drawMode = DrawMode.Pen,
            canvasSize = size,
            drawPathMode = DrawPathMode.OutlinedRect(fillGradientPalette = palette)
        )
        val actual = checkNotNull(
            applier.applyDrawToImage(
                DrawBehavior.Background(0, size.width, size.height, Color.TRANSPARENT),
                listOf(entry),
                ""
            )
        )
        val expected = createBitmap(size.width, size.height)
        Canvas(expected).drawCommittedPath(
            entry, size, context,
            source = { error("Unexpected effect") },
            onRequestFiltering = { _, _ -> error("Unexpected filter") })
        assertTrue(expected.sameAs(actual))
        assertEquals(Color.GREEN, actual.getPixel(20, 64))
        assertTrue(Color.red(actual.getPixel(40, 64)) > 200)
        assertTrue(Color.blue(actual.getPixel(215, 64)) > 200)
        expected.recycle()
        actual.recycle()
    }

    @Suppress("UNCHECKED_CAST")
    private inline fun <reified T> unused(): T = Proxy.newProxyInstance(
        T::class.java.classLoader, arrayOf(T::class.java)
    ) { _, method, _ -> error("Unexpected dependency call: ${method.name}") } as T
}