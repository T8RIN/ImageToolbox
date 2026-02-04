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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.longPress
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.press
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.transparencyChecker
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.UiMarkupLayer
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
internal fun MarkupLayersSideMenuColumn(
    modifier: Modifier,
    layers: List<UiMarkupLayer>,
    onReorderLayers: (List<UiMarkupLayer>) -> Unit,
    onActivateLayer: (UiMarkupLayer) -> Unit
) {
    val haptics = LocalHapticFeedback.current
    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(
        lazyListState = lazyListState
    ) { from, to ->
        haptics.press()
        val data = layers.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
        onReorderLayers(data)
    }
    LazyColumn(
        state = lazyListState,
        modifier = modifier,
        contentPadding = PaddingValues(
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
            key = { it.hashCode() }
        ) { layer ->
            ReorderableItem(
                state = reorderableLazyListState,
                key = layer.hashCode()
            ) {
                val (type, state) = layer

                val boxSize = 84.dp
                val density = LocalDensity.current
                val size by remember(state.rotation, density) {
                    derivedStateOf {
                        DpSize(
                            width = boxSize,
                            height = boxSize
                        ).rotateBy(
                            degrees = state.rotation,
                            density = density
                        )
                    }
                }
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
                                layer.state.isVisible = !layer.state.isVisible
                            }
                    )
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(boxSize)
                            .clip(ShapeDefaults.extraSmall)
                            .transparencyChecker()
                            .hapticsClickable {
                                onActivateLayer(layer)
                            }
                    ) {
                        val borderAlpha by animateFloatAsState(if (state.isActive) 1f else 0f)

                        BoxWithConstraints(
                            modifier = Modifier
                                .size(size)
                                .background(
                                    MaterialTheme.colorScheme.primary.copy(
                                        0.2f * borderAlpha
                                    )
                                )
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val scope = this

                            Box(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .graphicsLayer {
                                        rotationZ = state.rotation
                                        alpha = state.alpha
                                    }
                            ) {
                                LayerContent(
                                    modifier = Modifier.sizeIn(
                                        maxWidth = scope.maxWidth,
                                        maxHeight = scope.maxHeight
                                    ),
                                    type = type,
                                    textFullSize = scope.constraints.run {
                                        minOf(maxWidth, maxHeight)
                                    }
                                )
                            }
                        }

                        AnimatedBorder(
                            modifier = Modifier.matchParentSize(),
                            alpha = borderAlpha,
                            scale = 1f,
                            shape = ShapeDefaults.extraSmall
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Rounded.DragHandle,
                        contentDescription = null,
                        modifier = Modifier.longPressDraggableHandle(
                            onDragStarted = {
                                haptics.longPress()
                            }
                        )
                    )
                }
            }
        }
    }
}