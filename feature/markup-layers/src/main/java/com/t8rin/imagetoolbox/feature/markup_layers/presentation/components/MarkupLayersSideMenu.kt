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

package com.t8rin.imagetoolbox.feature.markup_layers.presentation.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.utils.roundTo
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSlider
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.tappable
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.UiMarkupLayer
import com.t8rin.modalsheet.FullscreenPopup

@Composable
internal fun MarkupLayersSideMenu(
    visible: Boolean,
    onDismiss: () -> Unit,
    isContextOptionsVisible: Boolean,
    onContextOptionsVisibleChange: (Boolean) -> Unit,
    onRemoveLayer: (UiMarkupLayer) -> Unit,
    onReorderLayers: (List<UiMarkupLayer>) -> Unit,
    onActivateLayer: (UiMarkupLayer) -> Unit,
    onCopyLayer: (UiMarkupLayer) -> Unit,
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
                        .tappable { onDismiss() }
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
                                Box {
                                    EnhancedIconButton(
                                        onClick = {
                                            onContextOptionsVisibleChange(true)
                                        },
                                        enabled = activeLayer != null
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Build,
                                            contentDescription = null
                                        )
                                    }
                                    MarkupLayersContextActions(
                                        visible = isContextOptionsVisible && activeLayer != null,
                                        onDismiss = { onContextOptionsVisibleChange(false) },
                                        onCopyLayer = {
                                            activeLayer?.let(onCopyLayer)
                                        },
                                        onToggleEditMode = {
                                            activeLayer?.state?.isInEditMode = true
                                        },
                                        onRemoveLayer = {
                                            activeLayer?.let(onRemoveLayer)
                                        },
                                        onActivateLayer = {
                                            activeLayer?.state?.isActive = false
                                        },
                                        rotationDegrees = activeLayer?.state?.rotation?.roundTo(1),
                                        onRotationDegreesChange = {
                                            activeLayer?.state?.rotation = it.roundTo(1)
                                        }
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
                        MarkupLayersSideMenuColumn(
                            modifier = Modifier.weight(1f),
                            layers = layers,
                            onReorderLayers = onReorderLayers,
                            onActivateLayer = onActivateLayer
                        )
                    }
                }
            }
        }
    }
}