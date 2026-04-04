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
import kotlin.math.ceil

internal data class TextLayerMetrics(
    val fontSizePx: Float,
    val lineHeightPx: Float,
    val horizontalPaddingPx: Float,
    val verticalPaddingPx: Float,
    val cornerRadiusPx: Float,
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
        baseTextSize / 5f,
        displayMetrics
    )
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

    return TextLayerMetrics(
        fontSizePx = fontSizePx,
        lineHeightPx = lineHeightPx,
        horizontalPaddingPx = (baseTextSize / 10f) * displayMetrics.density,
        verticalPaddingPx = (baseTextSize / 12f) * displayMetrics.density,
        cornerRadiusPx = 4f * displayMetrics.density,
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