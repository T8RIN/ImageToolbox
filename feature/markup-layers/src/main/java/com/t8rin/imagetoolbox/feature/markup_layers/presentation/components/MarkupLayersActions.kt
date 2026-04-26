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

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import com.t8rin.imagetoolbox.core.resources.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.emoji.Emoji
import com.t8rin.imagetoolbox.core.resources.icons.AddSticky
import com.t8rin.imagetoolbox.core.resources.icons.EmojiSticky
import com.t8rin.imagetoolbox.core.resources.icons.ImageSticky
import com.t8rin.imagetoolbox.core.resources.icons.Layers
import com.t8rin.imagetoolbox.core.resources.icons.Redo
import com.t8rin.imagetoolbox.core.resources.icons.StarSticky
import com.t8rin.imagetoolbox.core.resources.icons.TextSticky
import com.t8rin.imagetoolbox.core.resources.icons.Undo
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberImagePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedHorizontalScroll
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.fadingEdges
import com.t8rin.imagetoolbox.core.ui.widget.sheets.EmojiSelectionSheet
import com.t8rin.imagetoolbox.feature.markup_layers.domain.LayerType
import com.t8rin.imagetoolbox.feature.markup_layers.domain.withPreferredInitialGeometryFor
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.UiMarkupLayer
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.screenLogic.MarkupLayersComponent

@Composable
internal fun MarkupLayersActions(
    component: MarkupLayersComponent,
    showLayersSelection: Boolean,
    onToggleLayersSection: () -> Unit,
    onToggleLayersSectionQuick: () -> Unit
) {
    val layerImagePicker = rememberImagePicker { uri: Uri ->
        component.addLayer(
            UiMarkupLayer(
                type = LayerType.Picture.Image(uri)
            )
        )
    }
    var showTextEnteringDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var showEmojiPicker by rememberSaveable {
        mutableStateOf(false)
    }
    var showShapePicker by rememberSaveable {
        mutableStateOf(false)
    }

    val state = rememberScrollState()
    Row(
        modifier = Modifier
            .fadingEdges(state)
            .enhancedHorizontalScroll(state)
            .padding(bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        EnhancedIconButton(
            containerColor = takeColorFromScheme {
                if (showLayersSelection) tertiary
                else Color.Transparent
            },
            onLongClick = onToggleLayersSectionQuick,
            onClick = onToggleLayersSection,
            enabled = component.layers.isNotEmpty()
        ) {
            Icon(
                imageVector = Icons.Rounded.Layers,
                contentDescription = null
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.container(
                shape = ShapeDefaults.circle,
                color = takeColorFromScheme {
                    if (component.isOptionsExpanded) surface else Color.Transparent
                },
                composeColorOnTopOfBackground = false,
                clip = false,
                resultPadding = 0.dp
            )
        ) {
            EnhancedIconButton(
                onClick = component::toggleExpandOptions,
                containerColor = takeColorFromScheme {
                    if (component.isOptionsExpanded) secondaryContainer else Color.Transparent
                },
                contentColor = takeColorFromScheme {
                    if (component.isOptionsExpanded) onSecondaryContainer else LocalContentColor.current
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.AddSticky,
                    contentDescription = null
                )
            }

            AnimatedVisibility(
                visible = component.isOptionsExpanded,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    EnhancedIconButton(
                        onClick = {
                            showTextEnteringDialog = true
                        },
                        forceMinimumInteractiveComponentSize = false
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.TextSticky,
                            contentDescription = null
                        )
                    }
                    EnhancedIconButton(
                        onClick = layerImagePicker::pickImage,
                        forceMinimumInteractiveComponentSize = false
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ImageSticky,
                            contentDescription = null
                        )
                    }
                    EnhancedIconButton(
                        onClick = {
                            showEmojiPicker = true
                        },
                        forceMinimumInteractiveComponentSize = false
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.EmojiSticky,
                            contentDescription = null
                        )
                    }
                    EnhancedIconButton(
                        onClick = {
                            showShapePicker = true
                        },
                        forceMinimumInteractiveComponentSize = false
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.StarSticky,
                            contentDescription = null
                        )
                    }
                }
            }
        }

        val isPortrait by isPortraitOrientationAsState()
        if (isPortrait) {
            Spacer(Modifier.width(8.dp))
            MarkupLayersUndoRedo(
                component = component,
                color = MaterialTheme.colorScheme.surface,
                removePadding = true
            )
            Spacer(Modifier.width(8.dp))
        }
    }

    val allEmojis = Emoji.allIcons()

    EmojiSelectionSheet(
        selectedEmojiIndex = null,
        allEmojis = allEmojis,
        onEmojiPicked = {
            component.addLayer(
                UiMarkupLayer(
                    type = LayerType.Picture.Sticker(allEmojis[it])
                )
            )
            showEmojiPicker = false
        },
        visible = showEmojiPicker,
        onDismiss = {
            showEmojiPicker = false
        },
        icon = Icons.Outlined.EmojiSticky
    )

    AddShapeLayerDialog(
        visible = showShapePicker,
        onDismiss = {
            showShapePicker = false
        },
        onShapePicked = { mode ->
            component.addLayer(
                UiMarkupLayer(
                    type = LayerType.Shape.Default.withPreferredInitialGeometryFor(mode)
                )
            )
            showShapePicker = false
        }
    )

    AddTextLayerDialog(
        visible = showTextEnteringDialog,
        onDismiss = { showTextEnteringDialog = false },
        onAddLayer = component::addLayer
    )
}

@Composable
internal fun MarkupLayersUndoRedo(
    component: MarkupLayersComponent,
    color: Color,
    removePadding: Boolean
) {
    Row(
        modifier = Modifier.container(
            shape = ShapeDefaults.circle,
            color = color,
            resultPadding = if (removePadding) 0.dp else 4.dp
        )
    ) {
        EnhancedIconButton(
            onClick = component::undo,
            enabled = component.canUndo
        ) {
            Icon(
                imageVector = Icons.Rounded.Undo,
                contentDescription = "Undo"
            )
        }
        EnhancedIconButton(
            onClick = component::redo,
            enabled = component.canRedo
        ) {
            Icon(
                imageVector = Icons.Rounded.Redo,
                contentDescription = "Redo"
            )
        }
    }
}
