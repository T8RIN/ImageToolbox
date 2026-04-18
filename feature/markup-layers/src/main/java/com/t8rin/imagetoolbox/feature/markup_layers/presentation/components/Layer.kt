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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import com.t8rin.imagetoolbox.feature.markup_layers.domain.LayerType
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.UiMarkupLayer
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.composeToParentSpace
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.groupContentSize
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.groupOverlayState
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.renderCopy
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.uiCornerRadiusPercent
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.screenLogic.MarkupLayersComponent

@Composable
internal fun BoxWithConstraintsScope.Layer(
    component: MarkupLayersComponent?,
    layer: UiMarkupLayer,
    onActivate: (() -> Unit)?,
    onShowContextOptions: (() -> Unit)?,
    onUpdateLayer: ((UiMarkupLayer, Boolean) -> Unit)?
) {
    if (layer.isGroup) {
        GroupLayer(
            layer = layer,
            onActivate = onActivate,
            onShowContextOptions = onShowContextOptions
        )
        return
    }

    val type = layer.type
    val cornerRadiusPercent = layer.uiCornerRadiusPercent()
    val canEditLayer = onUpdateLayer != null && component != null
    val isInteractive = !layer.isLocked && onActivate != null

    EditBox(
        state = layer.state,
        cornerRadiusPercent = cornerRadiusPercent,
        blendingMode = layer.blendingMode,
        isInteractive = isInteractive,
        onTap = {
            if (layer.state.isActive && canEditLayer) {
                layer.state.isInEditMode = true
            } else {
                onActivate?.invoke()
            }
        },
        onLongTap = {
            if (!layer.state.isActive) {
                onActivate?.invoke()
            }
            onShowContextOptions?.invoke()
        },
        content = {
            val measuredContentSize = layer.state.contentSize
            val density = LocalDensity.current
            val contentModifier = when {
                measuredContentSize.width > 0 &&
                        measuredContentSize.height > 0 &&
                        (layer.isGroup || onUpdateLayer == null) -> Modifier.requiredSize(
                    width = with(density) { measuredContentSize.width.toDp() },
                    height = with(density) { measuredContentSize.height.toDp() }
                )

                layer.isGroup || type is LayerType.Text -> Modifier.sizeIn(
                    maxWidth = this@Layer.maxWidth,
                    maxHeight = this@Layer.maxHeight
                )

                else -> {
                    Modifier.sizeIn(
                        maxWidth = this@Layer.maxWidth / 2,
                        maxHeight = this@Layer.maxHeight / 2
                    )
                }
            }

            LayerContent(
                modifier = contentModifier,
                type = type,
                groupedLayers = layer.groupedLayers,
                textFullSize = this@Layer.constraints.run { minOf(maxWidth, maxHeight) },
                cornerRadiusPercent = cornerRadiusPercent,
                onTextLayout = if (layer.type is LayerType.Text && onUpdateLayer != null) {
                    { result ->
                        val visibleLineCount = if (result.didOverflowHeight) {
                            (0 until result.lineCount).count { lineIndex ->
                                result.getLineBottom(lineIndex) <= result.size.height
                            }
                        } else {
                            result.lineCount
                        }

                        if (visibleLineCount > 0 && layer.visibleLineCount != visibleLineCount) {
                            onUpdateLayer(
                                layer.copy(visibleLineCount = visibleLineCount),
                                false
                            )
                        }
                    }
                } else null
            )
        }
    )

    if (canEditLayer) {
        EditLayerSheet(
            component = component,
            visible = layer.state.isInEditMode && !layer.isLocked,
            onDismiss = { layer.state.isInEditMode = it },
            onUpdateLayer = onUpdateLayer,
            layer = layer
        )
    }
}

@Composable
private fun BoxWithConstraintsScope.GroupLayer(
    layer: UiMarkupLayer,
    onActivate: (() -> Unit)?,
    onShowContextOptions: (() -> Unit)?
) {
    val density = LocalDensity.current

    layer.groupedLayers.forEach { child ->
        val renderedChild = child.composeToParentSpace(layer).renderCopy().let { renderCopy ->
            if (layer.state.isActive) {
                renderCopy.copy(
                    state = renderCopy.state.copy(
                        isActive = true
                    )
                )
            } else renderCopy
        }
        Layer(
            component = null,
            layer = renderedChild,
            onActivate = null,
            onShowContextOptions = null,
            onUpdateLayer = null
        )
    }

    val measuredContentSize = layer.groupContentSize()
        ?.takeIf { it.isSpecified() }
        ?: layer.state.contentSize.takeIf { it.isSpecified() }
        ?: IntSize(1, 1)

    SideEffect {
        if (layer.state.contentSize != measuredContentSize) {
            layer.state.contentSize = measuredContentSize
        }
    }

    val overlayState = layer.groupOverlayState()
        ?.copy(isActive = false)
        ?: return
    val isInteractive = onActivate != null || onShowContextOptions != null
    if (!isInteractive) return

    EditBox(
        state = overlayState,
        cornerRadiusPercent = 0,
        isInteractive = true,
        showSelectionBackground = false,
        onTap = {
            onActivate?.invoke()
        },
        onLongTap = {
            if (!layer.state.isActive) {
                onActivate?.invoke()
            }
            onShowContextOptions?.invoke()
        }
    ) {
        Box(
            modifier = Modifier.requiredSize(
                width = with(density) { overlayState.contentSize.width.toDp() },
                height = with(density) { overlayState.contentSize.height.toDp() }
            )
        )
    }
}

private fun IntSize.isSpecified(): Boolean = width > 0 && height > 0
