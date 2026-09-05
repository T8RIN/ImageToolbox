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
import android.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.t8rin.imagetoolbox.core.domain.model.GradientPalette
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.model.pt
import com.t8rin.imagetoolbox.feature.draw.domain.DrawBehavior
import com.t8rin.imagetoolbox.feature.draw.domain.DrawMode
import com.t8rin.imagetoolbox.feature.draw.presentation.components.UiPathPaint
import com.t8rin.imagetoolbox.feature.draw.presentation.components.drawCommittedPath
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.reflect.Proxy

@RunWith(AndroidJUnit4::class)
class DrawExportTest {
    @Test
    fun exportKeepsEachStrokesGradientLengthAndPreviewColours() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val applier = AndroidImageDrawApplier(
            context,
            unusedDependency(),
            unusedDependency(),
            unusedDependency()
        )
        val size = IntegerSize(256, 256)
        val path = Path().apply {
            moveTo(16f, 80f)
            cubicTo(70f, 25f, 210f, 145f, 240f, 75f)
        }
        for (length in listOf(.1f, .25f, 1f, 4f)) {
            for (softness in listOf(0.pt, 12.pt)) {
                for (mode in listOf(
                    DrawMode.Pen,
                    DrawMode.Highlighter,
                    DrawMode.Text(text = "Gradient")
                )) {
                    val entry = UiPathPaint(
                        path = path, strokeWidth = 65.pt, brushSoftness = softness,
                        drawColor = Color.White.copy(alpha = .65f), isErasing = false,
                        drawMode = mode, canvasSize = size,
                        gradientPalette = GradientPalette.SoftRainbow, gradientLength = length
                    )
                    val second = entry.copy(
                        path = Path().apply { moveTo(16f, 180f); lineTo(240f, 180f) },
                        gradientLength = 2f
                    )
                    val paths = listOf(entry, second)
                    val actual = checkNotNull(
                        applier.applyDrawToImage(
                            DrawBehavior.Background(
                                0,
                                size.width,
                                size.height,
                                android.graphics.Color.WHITE
                            ),
                            paths, ""
                        )
                    )
                    val expected =
                        Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888)
                            .apply { eraseColor(android.graphics.Color.WHITE) }
                    paths.forEach {
                        Canvas(expected).drawCommittedPath(
                            it, size, context,
                            source = { error("A gradient requested filtering") },
                            onRequestFiltering = { _, _ -> error("A gradient requested filtering") }
                        )
                    }
                    assertTrue(
                        "Export changed $length / $softness / $mode",
                        expected.sameAs(actual)
                    )
                    expected.recycle()
                    actual.recycle()
                }
            }
        }
    }

    @Test
    fun floodFillPreservesHolesAndSinglePixelRegions() {
        val source = Bitmap.createBitmap(32, 32, Bitmap.Config.ARGB_8888)
            .apply { eraseColor(android.graphics.Color.WHITE) }
        for (x in 8..23) {
            source.setPixel(x, 8, android.graphics.Color.BLACK)
            source.setPixel(x, 23, android.graphics.Color.BLACK)
        }
        for (y in 8..23) {
            source.setPixel(8, y, android.graphics.Color.BLACK)
            source.setPixel(23, y, android.graphics.Color.BLACK)
        }
        source.setPixel(15, 15, android.graphics.Color.RED)
        for ((sx, sy) in listOf(0 to 0, 10 to 10, 15 to 15, 8 to 8)) {
            val visited = mutableSetOf(sx to sy)
            val pending = ArrayDeque<Pair<Int, Int>>().apply { add(sx to sy) }
            while (pending.isNotEmpty()) {
                val (x, y) = pending.removeFirst()
                for (next in listOf(x - 1 to y, x + 1 to y, x to y - 1, x to y + 1)) {
                    if (next.first in 0..31 && next.second in 0..31 && next !in visited &&
                        source.getPixel(next.first, next.second) == source.getPixel(sx, sy)
                    ) {
                        visited.add(next)
                        pending.add(next)
                    }
                }
            }
            val fill = checkNotNull(
                com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.FloodFill(source)
                    .performFloodFill(sx, sy, 0f)
            )
            val mask = Bitmap.createBitmap(32, 32, Bitmap.Config.ARGB_8888)
            Canvas(mask).drawPath(
                fill,
                android.graphics.Paint().apply { color = android.graphics.Color.WHITE })
            for (y in 0..31) for (x in 0..31) {
                assertTrue(
                    "Fill crossed a boundary from $sx/$sy to $x/$y",
                    (mask.getPixel(x, y) != 0) == (x to y in visited)
                )
            }
            mask.recycle()
        }
        source.recycle()
    }

    private inline fun <reified T> unusedDependency(): T = Proxy.newProxyInstance(
        T::class.java.classLoader, arrayOf(T::class.java)
    ) { _, method, _ -> error("Unexpected dependency call: ${method.name}") } as T
}