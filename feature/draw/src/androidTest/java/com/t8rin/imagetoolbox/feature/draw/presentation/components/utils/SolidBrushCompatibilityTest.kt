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
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.nativePaint
import androidx.compose.ui.graphics.toArgb
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.model.Pt
import com.t8rin.imagetoolbox.core.domain.model.pt
import com.t8rin.imagetoolbox.core.utils.toTypeface
import com.t8rin.imagetoolbox.feature.draw.domain.DrawLineStyle
import com.t8rin.imagetoolbox.feature.draw.domain.DrawMode
import com.t8rin.imagetoolbox.feature.draw.domain.DrawPathMode
import com.t8rin.imagetoolbox.feature.draw.presentation.components.UiPathPaint
import com.t8rin.imagetoolbox.feature.draw.presentation.components.drawCommittedPath
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import android.graphics.Paint as NativePaint

@RunWith(AndroidJUnit4::class)
class SolidBrushCompatibilityTest {
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun solidBrushesMatchMasterRendering() = runBlocking {
        val size = IntegerSize(192, 192)
        val path = Path().apply {
            moveTo(22f, 144f)
            cubicTo(30f, 10f, 148f, 160f, 170f, 28f)
            lineTo(122f, 165f)
        }
        val shapes = DrawPathMode.entries.filterNot { it is DrawPathMode.FloodFill }
        val styles = listOf(DrawLineStyle.None, DrawLineStyle.Dashed(), DrawLineStyle.DotDashed)
        for (mode in listOf(DrawMode.Pen, DrawMode.Highlighter, DrawMode.Neon)) {
            for (shape in shapes) for (style in styles) for (erase in listOf(false, true)) {
                for (softness in listOf(0.pt, 10.pt)) {
                    val before =
                        Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888)
                            .apply { eraseColor(android.graphics.Color.WHITE) }
                    val after = before.copy(Bitmap.Config.ARGB_8888, true)
                    Canvas(before).drawPath(
                        path.asAndroidPath(),
                        masterPaint(
                            65.pt,
                            erase,
                            Color(0xA61756AD),
                            softness,
                            mode,
                            size,
                            shape,
                            style
                        )
                    )
                    Canvas(after).drawCommittedPath(
                        UiPathPaint(
                            path,
                            65.pt,
                            softness,
                            Color(0xA61756AD),
                            erase,
                            mode,
                            size,
                            shape,
                            style
                        ),
                        size, context,
                        source = { error("A solid stroke requested a filter source") },
                        onRequestFiltering = { _, _ -> error("A solid stroke requested filtering") }
                    )
                    assertTrue(
                        "Solid brush changed: $mode / $shape / $style / $erase / $softness",
                        before.sameAs(after)
                    )
                    before.recycle()
                    after.recycle()
                }
            }
        }
    }

    private fun masterPaint(
        strokeWidth: Pt,
        isEraserOn: Boolean,
        drawColor: Color,
        brushSoftness: Pt,
        drawMode: DrawMode,
        canvasSize: IntegerSize,
        drawPathMode: DrawPathMode,
        drawLineStyle: DrawLineStyle
    ): NativePaint = run {
        val isSharpEdge = drawPathMode.isSharpEdge
        val isFilled = drawPathMode.isFilled

        Paint().apply {
            if (drawMode !is DrawMode.Text && drawMode !is DrawMode.Image) {
                pathEffect = drawLineStyle.asPathEffect(
                    canvasSize = canvasSize,
                    strokeWidth = strokeWidth.toPx(canvasSize),
                    context = context
                )
            }
            blendMode = if (!isEraserOn) blendMode else BlendMode.Clear
            if (isEraserOn) {
                style = PaintingStyle.Stroke
                this.strokeWidth = strokeWidth.toPx(canvasSize)
                strokeCap = StrokeCap.Round
                strokeJoin = StrokeJoin.Round
            } else {
                if (drawMode !is DrawMode.Text) {
                    if (isFilled) {
                        style = PaintingStyle.Fill
                    } else {
                        style = PaintingStyle.Stroke
                        this.strokeWidth = drawPathMode.convertStrokeWidth(
                            strokeWidth = strokeWidth,
                            canvasSize = canvasSize
                        )
                        if (drawMode is DrawMode.Highlighter || isSharpEdge) {
                            strokeCap = StrokeCap.Square
                        } else {
                            strokeCap = StrokeCap.Round
                            strokeJoin = StrokeJoin.Round
                        }
                    }
                }
            }
            color = if (drawMode is DrawMode.PathEffect) {
                Color.Transparent
            } else drawColor
            alpha = drawColor.alpha
        }.nativePaint.apply {
            if (drawMode is DrawMode.Neon && !isEraserOn) {
                this.color = Color.White.toArgb()
                setShadowLayer(
                    brushSoftness.toPx(canvasSize),
                    0f,
                    0f,
                    drawColor
                        .copy(alpha = .8f)
                        .toArgb()
                )
            } else if (brushSoftness.value > 0f) {
                maskFilter = BlurMaskFilter(
                    brushSoftness.toPx(canvasSize),
                    BlurMaskFilter.Blur.NORMAL
                )
            }
            if (drawMode is DrawMode.Text && !isEraserOn) {
                isAntiAlias = true
                textSize = strokeWidth.toPx(canvasSize)
                typeface = drawMode.font.toTypeface()
            }
        }
    }
}