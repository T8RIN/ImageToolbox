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

@file:Suppress("COMPOSE_APPLIER_CALL_MISMATCH")

package com.t8rin.imagetoolbox.feature.markup_layers.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import com.t8rin.imagetoolbox.core.resources.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.icons.DragHandle
import com.t8rin.imagetoolbox.core.resources.icons.EmojiSticky
import com.t8rin.imagetoolbox.core.resources.icons.ImageSticky
import com.t8rin.imagetoolbox.core.resources.icons.Lock
import com.t8rin.imagetoolbox.core.resources.icons.StackSticky
import com.t8rin.imagetoolbox.core.resources.icons.StarSticky
import com.t8rin.imagetoolbox.core.resources.icons.TextSticky
import com.t8rin.imagetoolbox.core.resources.icons.Visibility
import com.t8rin.imagetoolbox.core.resources.icons.VisibilityOff
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsCombinedClickable
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.longPress
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.press
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.transparencyChecker
import com.t8rin.imagetoolbox.core.ui.widget.other.AnimatedBorder
import com.t8rin.imagetoolbox.feature.markup_layers.domain.LayerType
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.UiMarkupLayer
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.toPreviewGroupData
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.uiCornerRadiusPercent
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
internal fun MarkupLayersSideMenuColumn(
    modifier: Modifier,
    contentPadding: PaddingValues,
    layers: List<UiMarkupLayer>,
    onReorderLayers: (List<UiMarkupLayer>) -> Unit,
    onActivateLayer: (UiMarkupLayer) -> Unit,
    isGroupingSelectionMode: Boolean,
    groupingSelectionIds: Set<Long>,
    onStartGroupingSelection: (UiMarkupLayer) -> Unit,
    onToggleGroupingSelection: (UiMarkupLayer) -> Unit,
    onToggleLayerVisibility: (UiMarkupLayer) -> Unit,
    onUnlockLayer: (UiMarkupLayer) -> Unit
) {
    val haptics = LocalHapticFeedback.current
    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(
        lazyListState = lazyListState
    ) { from, to ->
        if (isGroupingSelectionMode) return@rememberReorderableLazyListState
        haptics.press()
        val data = layers.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
        onReorderLayers(data)
    }
    LaunchedEffect(Unit) {
        val index = layers.indexOfFirst { it.state.isActive }
            .takeIf { it >= 0 } ?: return@LaunchedEffect

        lazyListState.scrollToItem(index)
    }
    LazyColumn(
        state = lazyListState,
        modifier = modifier,
        contentPadding = contentPadding + PaddingValues(
            top = 12.dp,
            bottom = 12.dp,
            start = 8.dp,
            end = 4.dp
        ),
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Bottom),
        reverseLayout = true,
        flingBehavior = enhancedFlingBehavior()
    ) {
        items(
            items = layers,
            key = { it.id }
        ) { layer ->
            ReorderableItem(
                state = reorderableLazyListState,
                key = layer.id
            ) {
                val type = layer.type
                val state = layer.state
                val isSelectedForGrouping = layer.id in groupingSelectionIds
                val density = LocalDensity.current
                val previewData by remember(layer) {
                    derivedStateOf {
                        layer.takeIf(UiMarkupLayer::isGroup)?.toPreviewGroupData()
                    }
                }
                val previewContentSize = remember(state.contentSize, previewData) {
                    previewData?.contentSize ?: state.contentSize.takeIf(IntSize::isSpecified)
                }
                val previewTextFullSize by remember(state.canvasSize) {
                    derivedStateOf {
                        min(state.canvasSize.width, state.canvasSize.height)
                            .coerceAtLeast(1)
                    }
                }
                val previewReferenceSize = remember(previewData, previewTextFullSize) {
                    previewData?.referenceSize ?: previewTextFullSize
                }

                val boxSize = 92.dp
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        imageVector = if (layer.state.isVisible) {
                            Icons.Rounded.Visibility
                        } else {
                            Icons.Rounded.VisibilityOff
                        },
                        contentDescription = null,
                        modifier = Modifier
                            .hapticsClickable(
                                indication = null,
                                interactionSource = null
                            ) {
                                onToggleLayerVisibility(layer)
                            }
                    )
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(boxSize)
                            .clip(ShapeDefaults.extraSmall)
                            .transparencyChecker()
                            .hapticsCombinedClickable(
                                onLongClick = {
                                    onStartGroupingSelection(layer)
                                }
                            ) {
                                if (isGroupingSelectionMode) {
                                    onToggleGroupingSelection(layer)
                                } else if (!layer.isLocked) {
                                    onActivateLayer(layer)
                                }
                            }
                    ) {
                        val borderAlpha by animateFloatAsState(
                            if (state.isActive || isSelectedForGrouping) 1f else 0f
                        )

                        BoxWithConstraints(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    MaterialTheme.colorScheme.primary.copy(
                                        0.16f * borderAlpha
                                    )
                                )
                                .padding(6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val scope = this
                            val previewContainerSize = remember(scope.constraints) {
                                IntSize(
                                    width = scope.constraints.maxWidth,
                                    height = scope.constraints.maxHeight
                                )
                            }
                            val previewFitScale by remember(
                                previewContentSize,
                                previewContainerSize,
                                state.rotation
                            ) {
                                derivedStateOf {
                                    previewContentSize?.let { contentSize ->
                                        calculatePreviewFitScale(
                                            contentSize = contentSize,
                                            containerSize = previewContainerSize,
                                            rotation = if (layer.isGroup) 0f else state.rotation
                                        )
                                    } ?: 1f
                                }
                            }
                            val previewModifier = remember(
                                previewContentSize,
                                density,
                                scope.maxWidth,
                                scope.maxHeight
                            ) {
                                previewContentSize?.let { contentSize ->
                                    Modifier.requiredSize(
                                        width = with(density) { contentSize.width.toDp() },
                                        height = with(density) { contentSize.height.toDp() }
                                    )
                                } ?: Modifier.sizeIn(
                                    maxWidth = scope.maxWidth,
                                    maxHeight = scope.maxHeight
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxSize(0.96f)
                                    .graphicsLayer {
                                        scaleX = if (layer.isGroup) {
                                            previewFitScale
                                        } else {
                                            previewFitScale *
                                                    if (state.isFlippedHorizontally) -1f else 1f
                                        }
                                        scaleY = if (layer.isGroup) {
                                            previewFitScale
                                        } else {
                                            previewFitScale *
                                                    if (state.isFlippedVertically) -1f else 1f
                                        }
                                        rotationZ = if (layer.isGroup) 0f else state.rotation
                                        alpha = if (layer.isGroup) 1f else state.alpha
                                        compositingStrategy =
                                            if ((if (layer.isGroup) 1f else state.alpha) >= 1f) {
                                                CompositingStrategy.Auto
                                            } else {
                                                CompositingStrategy.ModulateAlpha
                                            }
                                    }
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                LayerContent(
                                    modifier = previewModifier,
                                    type = type,
                                    groupedLayers = previewData?.layers ?: layer.groupedLayers,
                                    textFullSize = previewReferenceSize,
                                    maxLines = layer.visibleLineCount ?: Int.MAX_VALUE,
                                    cornerRadiusPercent = layer.uiCornerRadiusPercent()
                                )
                            }
                        }

                        AnimatedBorder(
                            modifier = Modifier.matchParentSize(),
                            alpha = borderAlpha,
                            scale = 1f,
                            shape = ShapeDefaults.extraSmall
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(3.dp)
                                .clip(ShapeDefaults.extraSmall)
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(0.8f))
                                .padding(2.dp)
                        ) {
                            Icon(
                                imageVector = if (layer.isGroup) {
                                    Icons.Outlined.StackSticky
                                } else {
                                    when (layer.type) {
                                        is LayerType.Picture.Image -> Icons.Outlined.ImageSticky
                                        is LayerType.Picture.Sticker -> Icons.Outlined.EmojiSticky
                                        is LayerType.Text -> Icons.Outlined.TextSticky
                                        is LayerType.Shape -> Icons.Outlined.StarSticky
                                    }
                                },
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(13.dp)
                            )
                        }

                        if (layer.isLocked) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(6.dp)
                                    .clip(ShapeDefaults.extraSmall)
                                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                    .hapticsClickable {
                                        onUnlockLayer(layer)
                                    }
                                    .padding(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Lock,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Rounded.DragHandle,
                        contentDescription = null,
                        modifier = if (isGroupingSelectionMode) {
                            Modifier.graphicsLayer {
                                alpha = 0.35f
                            }
                        } else {
                            Modifier.longPressDraggableHandle(
                                onDragStarted = {
                                    haptics.longPress()
                                }
                            )
                        }
                    )
                }
            }
        }
    }
}

private fun calculatePreviewFitScale(
    contentSize: IntSize,
    containerSize: IntSize,
    rotation: Float
): Float {
    if (!contentSize.isSpecified() || !containerSize.isSpecified()) return 1f

    val radians = Math.toRadians(rotation.toDouble())
    val cos = abs(cos(radians)).toFloat()
    val sin = abs(sin(radians)).toFloat()
    val rotatedWidth = contentSize.width * cos + contentSize.height * sin
    val rotatedHeight = contentSize.width * sin + contentSize.height * cos

    return min(
        containerSize.width / rotatedWidth.coerceAtLeast(1f),
        containerSize.height / rotatedHeight.coerceAtLeast(1f)
    ).coerceIn(0f, 1f)
}

private fun IntSize.isSpecified(): Boolean = width > 0 && height > 0
