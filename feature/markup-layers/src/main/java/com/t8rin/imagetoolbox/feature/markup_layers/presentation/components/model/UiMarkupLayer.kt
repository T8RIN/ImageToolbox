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

package com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model

import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.icons.FormatBold
import com.t8rin.imagetoolbox.core.resources.icons.FormatItalic
import com.t8rin.imagetoolbox.core.resources.icons.FormatStrikethrough
import com.t8rin.imagetoolbox.core.resources.icons.FormatUnderlined
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.IntSize
import com.t8rin.imagetoolbox.core.domain.image.model.BlendingMode
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.feature.markup_layers.domain.DomainTextDecoration
import com.t8rin.imagetoolbox.feature.markup_layers.domain.LayerPosition
import com.t8rin.imagetoolbox.feature.markup_layers.domain.LayerType
import com.t8rin.imagetoolbox.feature.markup_layers.domain.MarkupLayer
import com.t8rin.imagetoolbox.feature.markup_layers.domain.layerCornerRadiusPercent
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.EditBoxState
import java.util.concurrent.atomic.AtomicLong

data class UiMarkupLayer(
    val id: Long = nextUiLayerId(),
    val type: LayerType,
    val visibleLineCount: Int? = null,
    val cornerRadiusPercent: Int = 0,
    val blendingMode: BlendingMode = BlendingMode.SrcOver,
    val isLocked: Boolean = false,
    val groupedLayers: List<UiMarkupLayer> = emptyList(),
    val state: EditBoxState = EditBoxState(isActive = true)
) {
    val isGroup: Boolean
        get() = groupedLayers.isNotEmpty()

    fun copy(
        isActive: Boolean = state.isActive,
        coerceToBounds: Boolean = state.coerceToBounds
    ) = UiMarkupLayer(
        id = id,
        type = type,
        visibleLineCount = visibleLineCount,
        cornerRadiusPercent = cornerRadiusPercent,
        blendingMode = blendingMode,
        isLocked = isLocked,
        groupedLayers = groupedLayers,
        state = state.copy(
            isActive = isActive,
            coerceToBounds = coerceToBounds
        )
    )
}

fun UiMarkupLayer.asDomain(): MarkupLayer = MarkupLayer(
    type = if (isGroup) defaultGroupPlaceholderType() else type,
    position = LayerPosition(
        scale = state.scale,
        rotation = state.rotation,
        isFlippedHorizontally = state.isFlippedHorizontally,
        isFlippedVertically = state.isFlippedVertically,
        offsetX = state.offset.x,
        offsetY = state.offset.y,
        alpha = state.alpha,
        currentCanvasSize = state.canvasSize,
        coerceToBounds = state.coerceToBounds,
        isVisible = state.isVisible
    ),
    contentSize = state.contentSize.toIntegerSize(),
    visibleLineCount = visibleLineCount,
    cornerRadiusPercent = type.layerCornerRadiusPercent(cornerRadiusPercent),
    isLocked = isLocked,
    blendingMode = blendingMode,
    groupedLayers = groupedLayers.map(UiMarkupLayer::asDomain)
)

fun MarkupLayer.asUi(): UiMarkupLayer = UiMarkupLayer(
    type = type,
    visibleLineCount = visibleLineCount,
    cornerRadiusPercent = type.layerCornerRadiusPercent(cornerRadiusPercent),
    blendingMode = blendingMode,
    isLocked = isLocked,
    groupedLayers = groupedLayers.map(MarkupLayer::asUi),
    state = EditBoxState(
        scale = position.scale,
        rotation = position.rotation,
        isFlippedHorizontally = position.isFlippedHorizontally,
        isFlippedVertically = position.isFlippedVertically,
        offset = Offset(
            x = position.offsetX,
            y = position.offsetY
        ),
        alpha = position.alpha,
        isActive = false,
        canvasSize = position.currentCanvasSize,
        contentSize = contentSize.toIntSize(),
        isVisible = position.isVisible,
        coerceToBounds = position.coerceToBounds
    )
)

private fun IntSize.toIntegerSize(): IntegerSize = IntegerSize(
    width = width.coerceAtLeast(0),
    height = height.coerceAtLeast(0)
)

private fun IntegerSize.toIntSize(): IntSize = IntSize(
    width = width.coerceAtLeast(0),
    height = height.coerceAtLeast(0)
)

internal fun defaultGroupPlaceholderType(): LayerType = LayerType.Shape.Default.copy(
    color = 0,
    shadow = null
)

internal fun noteUiLayerId(id: Long) {
    uiLayerIdCounter.updateAndGet { current ->
        maxOf(current, id + 1)
    }
}

private fun nextUiLayerId(): Long = uiLayerIdCounter.getAndIncrement()

private val uiLayerIdCounter = AtomicLong(1L)

val DomainTextDecoration.icon: ImageVector
    get() = when (this) {
        LayerType.Text.Decoration.Bold -> Icons.Rounded.FormatBold
        LayerType.Text.Decoration.Italic -> Icons.Rounded.FormatItalic
        LayerType.Text.Decoration.Underline -> Icons.Rounded.FormatUnderlined
        LayerType.Text.Decoration.LineThrough -> Icons.Rounded.FormatStrikethrough
    }
