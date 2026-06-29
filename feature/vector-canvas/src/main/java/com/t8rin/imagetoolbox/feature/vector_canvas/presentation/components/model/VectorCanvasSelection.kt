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

@file:Suppress("PackageName", "PackageNaming")

package com.t8rin.imagetoolbox.feature.vector_canvas.presentation.components.model

import androidx.compose.ui.graphics.Color
import com.t8rin.imagetoolbox.feature.draw.domain.DrawLineStyle
import io.ak1.drawbox.domain.model.Element
import io.ak1.drawbox.domain.model.Mode
import io.ak1.drawbox.domain.model.ShapeType
import io.ak1.drawbox.domain.model.State
import io.ak1.drawbox.domain.model.StrokeStyle
import io.ak1.drawbox.domain.model.TextAlignment

data class VectorCanvasSelection(
    val elements: List<Element>,
    val shape: Element.Shape?,
    val text: Element.Text?,
    val strokeColor: Color,
    val strokeWidth: Float,
    val strokeStyle: StrokeStyle,
    val cornerRadius: Float,
    val fontSize: Float,
    val fontFamilyKey: String,
    val textAlignment: TextAlignment
) {
    val hasSelection: Boolean get() = elements.isNotEmpty()
    val hasStrokeSelection: Boolean
        get() = elements.any {
            it is Element.Path || it is Element.Shape
        }
}

fun State.vectorCanvasSelection(): VectorCanvasSelection {
    val selectedElements = elements.filter { it.id in selectedIds }
    val strokeElement = selectedElements.firstOrNull {
        it is Element.Path || it is Element.Shape
    }
    val selectedShape = selectedElements.filterIsInstance<Element.Shape>().firstOrNull()
    val selectedText = selectedElements.filterIsInstance<Element.Text>()
        .singleOrNull()
        ?.takeIf { selectedElements.size == 1 }

    return VectorCanvasSelection(
        elements = selectedElements,
        shape = selectedShape,
        text = selectedText,
        strokeColor = strokeElement.strokeColorOr(selectedText?.color ?: strokeColor),
        strokeWidth = strokeElement.strokeWidthOr(strokeWidth),
        strokeStyle = strokeElement.strokeStyleOr(currentItemStrokeStyle),
        cornerRadius = selectedShape?.cornerRadius ?: currentItemCornerRadius,
        fontSize = selectedText?.fontSize ?: currentItemFontSize,
        fontFamilyKey = selectedText?.fontFamilyKey ?: currentItemFontFamilyKey,
        textAlignment = selectedText?.alignment ?: currentItemTextAlignment
    )
}

private fun Element?.strokeColorOr(default: Color): Color = when (this) {
    is Element.Path -> strokeColor
    is Element.Shape -> strokeColor
    else -> default
}

private fun Element?.strokeWidthOr(default: Float): Float = when (this) {
    is Element.Path -> strokeWidth
    is Element.Shape -> strokeWidth
    else -> default
}

private fun Element?.strokeStyleOr(default: StrokeStyle): StrokeStyle = when (this) {
    is Element.Path -> strokeStyle
    is Element.Shape -> strokeStyle
    else -> default
}

fun Element.withZIndex(zIndex: Int): Element = when (this) {
    is Element.Path -> copy(zIndex = zIndex)
    is Element.Shape -> copy(zIndex = zIndex)
    is Element.Image -> copy(zIndex = zIndex)
    is Element.Text -> copy(zIndex = zIndex)
}

internal fun Mode.supportsOpacity(): Boolean = this == Mode.PEN ||
        this == Mode.LINE ||
        this == Mode.RECTANGLE ||
        this == Mode.CIRCLE ||
        this == Mode.TRIANGLE ||
        this == Mode.ARROW

internal fun State.canRound(selection: VectorCanvasSelection): Boolean =
    mode == Mode.RECTANGLE ||
            mode == Mode.TRIANGLE ||
            selection.shape?.shapeType == ShapeType.RECTANGLE ||
            selection.shape?.shapeType == ShapeType.TRIANGLE

internal fun StrokeStyle.toDrawLineStyle(): DrawLineStyle = when (this) {
    StrokeStyle.SOLID -> DrawLineStyle.None
    StrokeStyle.DASHED -> DrawLineStyle.Dashed()
    StrokeStyle.DOTTED -> DrawLineStyle.DotDashed
}

internal fun DrawLineStyle.toStrokeStyle(): StrokeStyle = when (this) {
    DrawLineStyle.None -> StrokeStyle.SOLID
    is DrawLineStyle.Dashed -> StrokeStyle.DASHED
    else -> StrokeStyle.DOTTED
}
