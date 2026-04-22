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

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.feature.markup_layers.domain.LayerType
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.UiMarkupLayer
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.canvasLeafLayers
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.combinedBounds
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.groupContentSize
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.renderCopy
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.uiCornerRadiusPercent
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.screenLogic.MarkupLayersComponent
import kotlinx.coroutines.launch

@Composable
internal fun BoxWithConstraintsScope.Layer(
    component: MarkupLayersComponent?,
    layer: UiMarkupLayer,
    onActivate: (() -> Unit)?,
    onShowContextOptions: (() -> Unit)?,
    onUpdateLayer: ((UiMarkupLayer, Boolean) -> Unit)?,
    referenceSizeOverride: Int? = null
) {
    val canEditLayer = onUpdateLayer != null && component != null

    if (layer.isGroup) {
        GroupLayer(
            component = component,
            layer = layer,
            onActivate = onActivate,
            onShowContextOptions = onShowContextOptions,
            onUpdateLayer = onUpdateLayer,
            canEditLayer = canEditLayer
        )
        return
    }

    val type = layer.type
    val cornerRadiusPercent = layer.uiCornerRadiusPercent()
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
                textFullSize = referenceSizeOverride
                    ?: this@Layer.constraints.run { minOf(maxWidth, maxHeight) },
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
    component: MarkupLayersComponent?,
    layer: UiMarkupLayer,
    onActivate: (() -> Unit)?,
    onShowContextOptions: (() -> Unit)?,
    onUpdateLayer: ((UiMarkupLayer, Boolean) -> Unit)?,
    canEditLayer: Boolean
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val tapScale = remember { Animatable(1f) }
    val parentCanvasSize = IntegerSize(
        width = constraints.maxWidth,
        height = constraints.maxHeight
    )
    val animateGroupTap = {
        scope.launch {
            tapScale.stop()
            tapScale.snapTo(1f)
            tapScale.animateTo(0.98f)
            tapScale.animateTo(1.02f)
            tapScale.animateTo(1f)
        }
    }
    val activateGroup = if (!layer.isLocked && onActivate != null) {
        {
            if (layer.state.isActive && canEditLayer) {
                layer.state.isInEditMode = true
            } else {
                onActivate()
            }
        }
    } else null

    val leafLayers = layer.canvasLeafLayers(canvasSize = parentCanvasSize)
    val groupCenter = leafLayers.combinedBounds()?.center ?: Offset.Zero

    leafLayers.forEach { child ->
        val renderedChild = child.renderCopy().let { renderCopy ->
            val displayCopy = renderCopy.withTransientGroupTapScale(
                scaleMultiplier = tapScale.value,
                groupCenter = groupCenter
            )

            if (layer.state.isActive) {
                displayCopy.copy(
                    state = displayCopy.state.copy(
                        isActive = true
                    )
                )
            } else displayCopy
        }
        Layer(
            component = null,
            layer = renderedChild,
            onActivate = null,
            onShowContextOptions = null,
            onUpdateLayer = null
        )
    }

    if (!layer.isLocked && (activateGroup != null || onShowContextOptions != null)) {
        leafLayers.forEach { child ->
            val hitContentSize = child.state.contentSize
                .takeIf(IntSize::isSpecified)
                ?: IntSize(1, 1)

            EditBox(
                state = child.state.copy(
                    isActive = false,
                    isInEditMode = false
                ),
                cornerRadiusPercent = child.uiCornerRadiusPercent(),
                isInteractive = true,
                animateOnTapWhenInactive = true,
                showSelectionBackground = false,
                onTap = {
                    animateGroupTap()
                    activateGroup?.invoke()
                },
                onLongTap = {
                    animateGroupTap()
                    if (!layer.state.isActive) {
                        activateGroup?.invoke()
                    }
                    onShowContextOptions?.invoke()
                }
            ) {
                Box(
                    modifier = Modifier.requiredSize(
                        width = with(density) { hitContentSize.width.toDp() },
                        height = with(density) { hitContentSize.height.toDp() }
                    )
                )
            }
        }
    }

    val measuredContentSize = layer.groupContentSize()
        ?.takeIf { it.isSpecified() }
        ?: layer.state.contentSize.takeIf { it.isSpecified() }
        ?: IntSize(1, 1)

    SideEffect {
        layer.state.syncCanvasSize(
            value = parentCanvasSize,
            forceScaleAdjustment = true
        )
        if (layer.state.contentSize != measuredContentSize) {
            layer.state.contentSize = measuredContentSize
        }
    }

    if (canEditLayer && component != null && onUpdateLayer != null) {
        EditLayerSheet(
            component = component,
            visible = layer.state.isInEditMode && !layer.isLocked,
            onDismiss = { layer.state.isInEditMode = it },
            onUpdateLayer = onUpdateLayer,
            layer = layer
        )
    }
}

private fun IntSize.isSpecified(): Boolean = width > 0 && height > 0

private fun UiMarkupLayer.withTransientGroupTapScale(
    scaleMultiplier: Float,
    groupCenter: Offset
): UiMarkupLayer {
    if (scaleMultiplier == 1f || isGroup) return this

    return copy(
        state = state.copy(
            scale = state.scale * scaleMultiplier,
            offset = groupCenter + (state.offset - groupCenter) * scaleMultiplier
        )
    )
}
