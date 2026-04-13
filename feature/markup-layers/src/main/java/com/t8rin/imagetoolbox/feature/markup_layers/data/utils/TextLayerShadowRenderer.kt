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
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import com.t8rin.imagetoolbox.feature.markup_layers.domain.LayerType
import kotlin.math.ceil

internal data class TextShadowRenderData(
    val bitmap: Bitmap,
    val left: Float,
    val top: Float
)

internal fun buildTextShadowRenderData(
    type: LayerType.Text,
    textMetrics: TextLayerMetrics,
    layoutWidth: Int,
    maxLines: Int?
): TextShadowRenderData? {
    val shadow = type.shadow ?: return null
    val safeLayoutWidth = layoutWidth.coerceAtLeast(1)
    val text = type.text.ifEmpty { " " }
    val alignment = when (type.alignment) {
        LayerType.Text.Alignment.Start -> Layout.Alignment.ALIGN_NORMAL
        LayerType.Text.Alignment.Center -> Layout.Alignment.ALIGN_CENTER
        LayerType.Text.Alignment.End -> Layout.Alignment.ALIGN_OPPOSITE
    }
    val paint = TextPaint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG).apply {
        color = android.graphics.Color.BLACK
        textSize = textMetrics.fontSizePx
        hinting = Paint.HINTING_ON
        isLinearText = true
        isUnderlineText = type.decorations.any { it == LayerType.Text.Decoration.Underline }
        isStrikeThruText = type.decorations.any { it == LayerType.Text.Decoration.LineThrough }
        typeface = textMetrics.typeface
        textScaleX = type.geometricTransform?.scaleX?.coerceAtLeast(0.01f) ?: 1f
        textSkewX = type.geometricTransform?.skewX ?: 0f
    }
    val layout = createTextStaticLayout(
        text = text,
        paint = paint,
        width = safeLayoutWidth,
        alignment = alignment,
        lineHeightPx = textMetrics.lineHeightPx,
        maxLines = maxLines
    )
    val sourceBitmap = createBitmap(
        width = layout.width.coerceAtLeast(1),
        height = layout.height.coerceAtLeast(1)
    ).applyCanvas {
        layout.draw(this)
    }

    val blurRadius = shadow.blurRadius.coerceAtLeast(0f)
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
        left = offset[0].toFloat() + shadow.offsetX,
        top = offset[1].toFloat() + shadow.offsetY
    )
}

internal fun createTextStaticLayout(
    text: String,
    paint: TextPaint,
    width: Int,
    alignment: Layout.Alignment,
    lineHeightPx: Float,
    maxLines: Int?
): StaticLayout {
    val boundedText = maxLines
        ?.takeIf { it > 0 }
        ?.let { linesLimit ->
            val fullLayout = StaticLayout.Builder
                .obtain(text, 0, text.length, paint, width)
                .setAlignment(alignment)
                .setIncludePad(false)
                .build()

            if (fullLayout.lineCount <= linesLimit) {
                text
            } else {
                text.substring(0, fullLayout.getLineEnd(linesLimit - 1))
            }
        }
        ?: text

    val naturalLineHeightPx = ceil(
        (paint.fontMetrics.descent - paint.fontMetrics.ascent).toDouble()
    ).toFloat()
    return StaticLayout.Builder
        .obtain(boundedText, 0, boundedText.length, paint, width)
        .setAlignment(alignment)
        .setIncludePad(false)
        .setLineSpacing((lineHeightPx - naturalLineHeightPx).coerceAtLeast(0f), 1f)
        .build()
}
