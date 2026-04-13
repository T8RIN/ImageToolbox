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

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import android.util.TypedValue
import com.t8rin.imagetoolbox.core.utils.toTypeface
import com.t8rin.imagetoolbox.feature.markup_layers.domain.LayerType
import kotlin.math.abs
import kotlin.math.ceil

internal data class TextLayerPadding(
    val leftPx: Float,
    val topPx: Float,
    val rightPx: Float,
    val bottomPx: Float
) {
    companion object {
        val Zero = TextLayerPadding(
            leftPx = 0f,
            topPx = 0f,
            rightPx = 0f,
            bottomPx = 0f
        )
    }
}

internal data class TextLayerMetrics(
    val fontSizePx: Float,
    val lineHeightPx: Float,
    val padding: TextLayerPadding,
    val typeface: Typeface
)

internal fun Context.calculateTextLayerMetrics(
    type: LayerType.Text,
    textFullSize: Int
): TextLayerMetrics {
    val displayMetrics = resources.displayMetrics
    val baseTextSize = textFullSize.coerceAtLeast(1) * type.size
    val fontSizePx = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        baseTextSize / 12.5f,
        displayMetrics
    ).coerceAtLeast(1f)
    val typeface = createTextLayerTypeface(type)
    val lineHeightPx = TextPaint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG).apply {
        textSize = fontSizePx
        hinting = Paint.HINTING_ON
        isLinearText = true
        isUnderlineText = type.decorations.any { it == LayerType.Text.Decoration.Underline }
        isStrikeThruText = type.decorations.any { it == LayerType.Text.Decoration.LineThrough }
        this.typeface = typeface
    }.fontMetrics.run {
        ceil((descent - ascent + leading.coerceAtLeast(0f)).toDouble())
            .toFloat()
            .coerceAtLeast(fontSizePx)
    }
    val baseHorizontalPaddingPx = baseTextSize / 10f
    val baseVerticalPaddingPx = baseTextSize / 12f
    val shadowPadding = type.calculateShadowPadding()
    val geometricTransformPaddingPx = lineHeightPx * abs(type.geometricTransform?.skewX ?: 0f)

    return TextLayerMetrics(
        fontSizePx = fontSizePx,
        lineHeightPx = lineHeightPx,
        padding = TextLayerPadding(
            leftPx = baseHorizontalPaddingPx + geometricTransformPaddingPx + shadowPadding.leftPx,
            topPx = baseVerticalPaddingPx + shadowPadding.topPx,
            rightPx = baseHorizontalPaddingPx + geometricTransformPaddingPx + shadowPadding.rightPx,
            bottomPx = baseVerticalPaddingPx + shadowPadding.bottomPx
        ),
        typeface = typeface
    )
}

internal fun createTextLayerTypeface(type: LayerType.Text): Typeface {
    val isBold = type.decorations.any { it == LayerType.Text.Decoration.Bold }
    val isItalic = type.decorations.any { it == LayerType.Text.Decoration.Italic }
    val style = when {
        isBold && isItalic -> Typeface.BOLD_ITALIC
        isBold -> Typeface.BOLD
        isItalic -> Typeface.ITALIC
        else -> Typeface.NORMAL
    }

    return Typeface.create(type.font.toTypeface() ?: Typeface.DEFAULT, style)
}

private fun LayerType.Text.calculateShadowPadding(): TextLayerPadding {
    val shadow = shadow ?: return TextLayerPadding.Zero
    val blurRadius = shadow.blurRadius.coerceAtLeast(0f)

    return TextLayerPadding(
        leftPx = (blurRadius - shadow.offsetX).coerceAtLeast(0f),
        topPx = (blurRadius - shadow.offsetY).coerceAtLeast(0f),
        rightPx = (blurRadius + shadow.offsetX).coerceAtLeast(0f),
        bottomPx = (blurRadius + shadow.offsetY).coerceAtLeast(0f)
    )
}
