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

package com.t8rin.imagetoolbox.feature.markup_layers.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.ui.widget.other.rememberAnimatedBorder
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.LayerBounds
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.UiMarkupLayer
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.applyGroupGlobalChanges
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.canvasLeafLayers
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.combinedBounds
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.uiCornerRadiusPercent
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.visualBounds
import kotlin.math.abs

internal data class LayerSnapResult(
    val offset: Offset = Offset.Zero,
    val verticalGuide: Float? = null,
    val horizontalGuide: Float? = null
)

internal fun UiMarkupLayer.snappingBounds(
    canvasSize: IntegerSize
): LayerBounds? {
    if (isBackground || !state.isVisible || state.alpha <= 0f) return null

    return if (isGroup) {
        canvasLeafLayers(canvasSize = canvasSize)
            .filter { it.state.isVisible && it.state.alpha > 0f }
            .combinedBounds()
    } else {
        if (state.contentSize.width <= 0 || state.contentSize.height <= 0) return null
        visualBounds()
    }
}

internal fun UiMarkupLayer.snapToLayers(
    layers: List<UiMarkupLayer>,
    canvasSize: IntegerSize,
    threshold: Float
): LayerSnapResult? {
    val bounds = snappingBounds(canvasSize) ?: return null
    val targets = listOf(
        LayerBounds(
            left = -canvasSize.width / 2f,
            top = -canvasSize.height / 2f,
            right = canvasSize.width / 2f,
            bottom = canvasSize.height / 2f
        )
    ) + layers.filter { it.id != id }.mapNotNull { it.snappingBounds(canvasSize) }
    val result = calculateLayerSnapping(bounds, targets, threshold)
    val previousOffset = state.offset

    if (isGroup) {
        applyGroupGlobalChanges(offsetChange = result.offset)
    } else {
        state.applyGlobalChanges(
            parentMaxWidth = canvasSize.width,
            parentMaxHeight = canvasSize.height,
            contentSize = state.contentSize,
            cornerRadiusPercent = uiCornerRadiusPercent(),
            zoomChange = 1f,
            offsetChange = result.offset,
            rotationChange = 0f
        )
    }

    val correction = state.offset - previousOffset
    return result.copy(
        offset = correction,
        verticalGuide = result.verticalGuide.takeIf {
            abs(correction.x - result.offset.x) < 0.5f
        },
        horizontalGuide = result.horizontalGuide.takeIf {
            abs(correction.y - result.offset.y) < 0.5f
        }
    )
}

internal fun calculateLayerSnapping(
    bounds: LayerBounds,
    targets: List<LayerBounds>,
    threshold: Float
): LayerSnapResult {
    fun findSnap(
        anchors: List<Float>,
        targetAnchors: List<Float>
    ): Pair<Float, Float>? = targetAnchors.flatMap { target ->
        anchors.map { anchor -> target - anchor to target }
    }.filter { abs(it.first) <= threshold }
        .minByOrNull { abs(it.first) }

    val horizontal = findSnap(
        anchors = listOf(bounds.center.x, bounds.left, bounds.right),
        targetAnchors = targets.flatMap { listOf(it.center.x, it.left, it.right) }
    )
    val vertical = findSnap(
        anchors = listOf(bounds.center.y, bounds.top, bounds.bottom),
        targetAnchors = targets.flatMap { listOf(it.center.y, it.top, it.bottom) }
    )

    return LayerSnapResult(
        offset = Offset(horizontal?.first ?: 0f, vertical?.first ?: 0f),
        verticalGuide = horizontal?.second,
        horizontalGuide = vertical?.second
    )
}

@Composable
internal fun BoxScope.LayerSnapGuides(
    result: LayerSnapResult?
) {
    result ?: return
    if (result.verticalGuide == null && result.horizontalGuide == null) return

    val colorScheme = MaterialTheme.colorScheme
    val pathEffect = rememberAnimatedBorder()

    Canvas(Modifier.matchParentSize()) {
        val guides = listOfNotNull(
            result.verticalGuide?.let { x ->
                val position = x + size.width / 2f
                Offset(position, 0f) to Offset(position, size.height)
            },
            result.horizontalGuide?.let { y ->
                val position = y + size.height / 2f
                Offset(0f, position) to Offset(size.width, position)
            }
        )
        guides.forEach { (start, end) ->
            drawLine(colorScheme.primary, start, end, 3.dp.toPx())
            drawLine(
                color = colorScheme.primaryContainer,
                start = start,
                end = end,
                strokeWidth = 3.dp.toPx(),
                pathEffect = pathEffect
            )
        }
    }
}
