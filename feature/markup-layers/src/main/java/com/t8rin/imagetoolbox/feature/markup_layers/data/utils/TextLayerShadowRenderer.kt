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

package com.t8rin.imagetoolbox.feature.markup_layers.data.utils

import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Paint
import android.graphics.PorterDuff
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import com.t8rin.imagetoolbox.feature.markup_layers.domain.LayerType
import kotlin.math.ceil
import androidx.compose.ui.graphics.Canvas as ComposeCanvas
import androidx.compose.ui.graphics.Color as ComposeColor

internal data class TextShadowRenderData(
    val bitmap: Bitmap,
    val left: Float,
    val top: Float,
    val rasterScale: Float
)

internal fun buildTextShadowRenderData(
    type: LayerType.Text,
    textMetrics: TextLayerMetrics,
    textLayoutResult: TextLayoutResult,
    rasterScale: Float = 1f
): TextShadowRenderData? {
    val shadow = type.shadow ?: return null
    val safeRasterScale = rasterScale.coerceAtLeast(1f)
    val sourcePadding = shadowSourcePadding(
        textMetrics = textMetrics,
        rasterScale = safeRasterScale
    )
    val sourceWidth = ceil(
        textLayoutResult.size.width * safeRasterScale +
                sourcePadding.leftPx +
                sourcePadding.rightPx
    ).toInt().coerceAtLeast(1)
    val sourceHeight = ceil(
        textLayoutResult.size.height * safeRasterScale +
                sourcePadding.topPx +
                sourcePadding.bottomPx
    ).toInt().coerceAtLeast(1)
    val sourceBitmap = createBitmap(
        width = sourceWidth,
        height = sourceHeight
    ).applyCanvas {
        CanvasDrawScope().draw(
            density = Density(1f),
            layoutDirection = LayoutDirection.Ltr,
            canvas = ComposeCanvas(this),
            size = Size(sourceWidth.toFloat(), sourceHeight.toFloat())
        ) {
            drawContext.canvas.save()
            drawContext.canvas.translate(sourcePadding.leftPx, sourcePadding.topPx)
            drawContext.canvas.scale(safeRasterScale, safeRasterScale)
            drawText(
                textLayoutResult = textLayoutResult,
                color = ComposeColor.Black
            )
            drawContext.canvas.restore()
        }
    }

    val blurRadius = shadow.blurRadius.coerceAtLeast(0f) * safeRasterScale
    val offset = IntArray(2)
    val alphaBitmap = sourceBitmap.extractAlpha(
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            if (blurRadius > 0f) {
                maskFilter = BlurMaskFilter(
                    blurRadius,
                    BlurMaskFilter.Blur.NORMAL
                )
            }
        },
        offset
    )

    val tintedBitmap = createBitmap(
        width = alphaBitmap.width.coerceAtLeast(1),
        height = alphaBitmap.height.coerceAtLeast(1)
    ).applyCanvas {
        drawBitmap(
            alphaBitmap,
            0f,
            0f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                isFilterBitmap = true
            }
        )
        drawColor(shadow.color, PorterDuff.Mode.SRC_IN)
    }

    sourceBitmap.recycle()
    alphaBitmap.recycle()

    return TextShadowRenderData(
        bitmap = tintedBitmap,
        left = offset[0].toFloat() - sourcePadding.leftPx + shadow.offsetX * safeRasterScale,
        top = offset[1].toFloat() - sourcePadding.topPx + shadow.offsetY * safeRasterScale,
        rasterScale = safeRasterScale
    )
}

private fun shadowSourcePadding(
    textMetrics: TextLayerMetrics,
    rasterScale: Float
): TextLayerPadding = TextLayerPadding(
    leftPx = ceil(textMetrics.padding.leftPx * rasterScale),
    topPx = ceil(textMetrics.padding.topPx * rasterScale),
    rightPx = ceil(textMetrics.padding.rightPx * rasterScale),
    bottomPx = ceil(textMetrics.padding.bottomPx * rasterScale)
)