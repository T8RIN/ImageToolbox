/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.feature.markup_layers.presentation.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.t8rin.modalsheet.FullscreenPopup
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedSlider
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.transparencyChecker
import ru.tech.imageresizershrinker.feature.markup_layers.presentation.components.model.UiMarkupLayer
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
internal fun MarkupLayersSideMenu(
    visible: Boolean,
    onDismiss: () -> Unit,
    onRemoveLayer: (UiMarkupLayer) -> Unit,
    onReorderLayers: (List<UiMarkupLayer>) -> Unit,
    onActivateLayer: (UiMarkupLayer) -> Unit,
    layers: List<UiMarkupLayer>
) {
    FullscreenPopup {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.CenterEnd
        ) {
            if (visible) {
                BackHandler(onBack = onDismiss)
                Box(
                    Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures { onDismiss() }
                        }
                )
            }

            val maxHeightFull = this.maxHeight
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally()
            ) {
                Surface(
                    color = Color.Transparent
                ) {
                    Column(
                        modifier = Modifier
                            .padding(8.dp)
                            .padding(
                                WindowInsets.systemBars
                                    .union(WindowInsets.displayCutout)
                                    .asPaddingValues()
                            )
                            .height(
                                minOf(maxHeightFull, 480.dp)
                            )
                            .width(168.dp)
                            .container(
                                color = MaterialTheme.colorScheme.surfaceContainer.copy(0.9f),
                                composeColorOnTopOfBackground = false,
                                resultPadding = 0.dp
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val activeLayer by remember(layers) {
                            derivedStateOf {
                                layers.find { it.state.isActive }
                            }
                        }
                        Column(
                            modifier = Modifier.container(
                                shape = RectangleShape,
                                color = MaterialTheme.colorScheme.surfaceContainerHigh
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                EnhancedIconButton(
                                    onClick = {
                                        activeLayer?.let(onRemoveLayer)
                                    },
                                    enabled = activeLayer != null
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Delete,
                                        contentDescription = null
                                    )
                                }
                                Spacer(Modifier.weight(1f))
                                EnhancedIconButton(
                                    onClick = {
                                        activeLayer?.state?.isInEditMode = true
                                        onDismiss()
                                    },
                                    enabled = activeLayer != null
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Build,
                                        contentDescription = null
                                    )
                                }
                            }
                            EnhancedSlider(
                                value = activeLayer?.state?.alpha ?: 1f,
                                enabled = activeLayer != null,
                                onValueChange = {
                                    activeLayer?.state?.alpha = it
                                },
                                valueRange = 0f..1f,
                                drawContainer = false,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                        val lazyListState = rememberLazyListState()
                        val reorderableLazyListState = rememberReorderableLazyListState(
                            lazyListState = lazyListState
                        ) { from, to ->
                            val data = layers.toMutableList().apply {
                                add(to.index, removeAt(from.index))
                            }
                            onReorderLayers(data)
                        }
                        LazyColumn(
                            state = lazyListState,
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(
                                top = 12.dp,
                                bottom = 12.dp,
                                start = 8.dp,
                                end = 4.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Bottom),
                            reverseLayout = true
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
                                                .clickable(
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
                                                .clip(RoundedCornerShape(4.dp))
                                                .transparencyChecker()
                                                .clickable {
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

                                                val rounded = abs(state.rotation.roundToInt())

                                                Box(
                                                    modifier = Modifier
                                                        .padding(
                                                            if (rounded % 90 == 0) {
                                                                0.dp
                                                            } else {
                                                                12.dp * (rounded % 360) / 180f
                                                            }
                                                        )
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
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                        }
                                        Spacer(Modifier.width(8.dp))
                                        Icon(
                                            imageVector = Icons.Rounded.DragHandle,
                                            contentDescription = null,
                                            modifier = Modifier.draggableHandle()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}