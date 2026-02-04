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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BorderColor
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.colordetector.util.ColorUtil.roundToTwoDigits
import com.t8rin.imagetoolbox.core.domain.model.Outline
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.toggle
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.emoji.Emoji
import com.t8rin.imagetoolbox.core.resources.icons.AddPhotoAlt
import com.t8rin.imagetoolbox.core.resources.icons.BackgroundColor
import com.t8rin.imagetoolbox.core.resources.icons.MiniEdit
import com.t8rin.imagetoolbox.core.resources.icons.MiniEditLarge
import com.t8rin.imagetoolbox.core.resources.shapes.MaterialStarShape
import com.t8rin.imagetoolbox.core.settings.presentation.model.toUiFont
import com.t8rin.imagetoolbox.core.ui.theme.inverseByLuma
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.theme.toColor
import com.t8rin.imagetoolbox.core.ui.utils.provider.SafeLocalContainerColor
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ColorRowSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.FontSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ImageSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedVerticalScroll
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemOverload
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.core.ui.widget.sheets.EmojiSelectionSheet
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextFieldColors
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.feature.markup_layers.domain.DomainTextDecoration
import com.t8rin.imagetoolbox.feature.markup_layers.domain.LayerType
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.UiMarkupLayer
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.icon

@Composable
internal fun EditLayerSheet(
    visible: Boolean,
    onDismiss: (Boolean) -> Unit,
    onUpdateLayer: (UiMarkupLayer) -> Unit,
    layer: UiMarkupLayer
) {
    EnhancedModalBottomSheet(
        visible = visible,
        onDismiss = onDismiss,
        title = {
            when (val type = layer.type) {
                is LayerType.Picture -> {
                    TitleItem(
                        icon = Icons.Rounded.MiniEditLarge,
                        text = stringResource(R.string.edit_layer)
                    )
                }

                is LayerType.Text -> {
                    Row {
                        DomainTextDecoration.entries.forEach { decoration ->
                            EnhancedIconButton(
                                onClick = {
                                    onUpdateLayer(
                                        layer.copy(
                                            type = type.copy(
                                                decorations = type.decorations.toggle(decoration)
                                            )
                                        )
                                    )
                                },
                                containerColor = takeColorFromScheme {
                                    if (decoration in type.decorations) secondaryContainer else surface
                                },
                                contentColor = takeColorFromScheme {
                                    if (decoration in type.decorations) onSecondaryContainer else onSurface
                                }
                            ) {
                                Icon(
                                    imageVector = decoration.icon,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            EnhancedButton(
                onClick = {
                    onDismiss(false)
                }
            ) {
                Text(stringResource(R.string.close))
            }
        }
    ) {
        Column(
            modifier = Modifier
                .enhancedVerticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            when (val type = layer.type) {
                is LayerType.Picture.Image -> {
                    ImageSelector(
                        value = type.imageData,
                        onValueChange = {
                            onUpdateLayer(
                                layer.copy(
                                    type = type.copy(
                                        imageData = it
                                    )
                                )
                            )
                        },
                        subtitle = null,
                        color = Color.Unspecified
                    )
                }

                is LayerType.Text -> {
                    RoundedTextField(
                        value = type.text,
                        onValueChange = {
                            onUpdateLayer(
                                layer.copy(
                                    type = type.copy(
                                        text = it
                                    )
                                )
                            )
                        },
                        hint = stringResource(R.string.text),
                        colors = RoundedTextFieldColors(
                            isError = false,
                            containerColor = SafeLocalContainerColor
                        ),
                        modifier = Modifier.container(
                            shape = ShapeDefaults.default,
                            color = MaterialTheme.colorScheme.surface,
                            resultPadding = 8.dp
                        ),
                        keyboardOptions = KeyboardOptions(),
                        singleLine = false
                    )
                    Spacer(Modifier.height(8.dp))
                    FontSelector(
                        value = type.font.toUiFont(),
                        onValueChange = {
                            onUpdateLayer(
                                layer.copy(
                                    type = type.copy(
                                        font = it.type
                                    )
                                )
                            )
                        },
                        shape = ShapeDefaults.top,
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    EnhancedSliderItem(
                        value = type.size,
                        title = stringResource(R.string.font_scale),
                        internalStateTransformation = {
                            it.roundToTwoDigits()
                        },
                        onValueChange = {
                            onUpdateLayer(
                                layer.copy(
                                    type = type.copy(size = it)
                                )
                            )
                        },
                        valueRange = 0.01f..1f,
                        shape = ShapeDefaults.center,
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    ColorRowSelector(
                        value = type.backgroundColor.toColor(),
                        onValueChange = {
                            onUpdateLayer(
                                layer.copy(
                                    type = type.copy(
                                        backgroundColor = it.toArgb()
                                    )
                                )
                            )
                        },
                        title = stringResource(R.string.background_color),
                        icon = Icons.Outlined.BackgroundColor,
                        modifier = Modifier.container(
                            shape = ShapeDefaults.center,
                            color = MaterialTheme.colorScheme.surface
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    ColorRowSelector(
                        value = type.color.toColor(),
                        onValueChange = {
                            onUpdateLayer(
                                layer.copy(
                                    type = type.copy(
                                        color = it.toArgb()
                                    )
                                )
                            )
                        },
                        title = stringResource(R.string.text_color),
                        modifier = Modifier.container(
                            shape = ShapeDefaults.center,
                            color = MaterialTheme.colorScheme.surface
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    var haveOutline by remember {
                        mutableStateOf(type.outline != null)
                    }
                    LaunchedEffect(haveOutline) {
                        onUpdateLayer(
                            layer.copy(
                                type = type.copy(
                                    outline = if (haveOutline) {
                                        type.outline ?: Outline(
                                            color = type.color.toColor()
                                                .inverseByLuma()
                                                .toArgb(),
                                            width = 4f
                                        )
                                    } else null
                                )
                            )
                        )
                    }

                    PreferenceRowSwitch(
                        title = stringResource(R.string.add_outline),
                        subtitle = stringResource(R.string.add_outline_sub),
                        shape = ShapeDefaults.bottom,
                        containerColor = MaterialTheme.colorScheme.surface,
                        applyHorizontalPadding = false,
                        resultModifier = Modifier.padding(16.dp),
                        checked = haveOutline,
                        onClick = {
                            haveOutline = it
                        },
                        additionalContent = {
                            AnimatedVisibility(type.outline != null) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(top = 16.dp)
                                ) {
                                    ColorRowSelector(
                                        value = type.outline?.color?.toColor() ?: Color.Transparent,
                                        onValueChange = {
                                            onUpdateLayer(
                                                layer.copy(
                                                    type = type.copy(
                                                        outline = type.outline?.copy(
                                                            color = it.toArgb()
                                                        )
                                                    )
                                                )
                                            )
                                        },
                                        title = stringResource(R.string.outline_color),
                                        modifier = Modifier.container(
                                            shape = ShapeDefaults.top,
                                            color = MaterialTheme.colorScheme.surfaceContainerLow
                                        ),
                                        icon = Icons.Outlined.BorderColor
                                    )
                                    EnhancedSliderItem(
                                        value = type.outline?.width ?: 0.2f,
                                        title = stringResource(R.string.outline_size),
                                        internalStateTransformation = {
                                            it.roundToTwoDigits()
                                        },
                                        onValueChange = {
                                            onUpdateLayer(
                                                layer.copy(
                                                    type = type.copy(
                                                        outline = type.outline?.copy(
                                                            width = it
                                                        )
                                                    )
                                                )
                                            )
                                        },
                                        valueRange = 0.01f..10f,
                                        shape = ShapeDefaults.bottom,
                                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                                    )
                                }
                            }
                        }
                    )
                }

                is LayerType.Picture.Sticker -> {
                    var showEmojiPicker by rememberSaveable {
                        mutableStateOf(false)
                    }

                    PreferenceItemOverload(
                        title = stringResource(R.string.change_sticker),
                        subtitle = null,
                        onClick = {
                            showEmojiPicker = true
                        },
                        startIcon = {
                            Picture(
                                model = type.imageData,
                                contentPadding = PaddingValues(8.dp),
                                shape = MaterialStarShape,
                                modifier = Modifier.size(48.dp),
                                error = {
                                    Icon(
                                        imageVector = Icons.Outlined.AddPhotoAlt,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(MaterialStarShape)
                                            .background(
                                                color = MaterialTheme.colorScheme.secondaryContainer.copy(
                                                    0.5f
                                                )
                                            )
                                            .padding(8.dp)
                                    )
                                }
                            )
                        },
                        endIcon = {
                            Icon(
                                imageVector = Icons.Rounded.MiniEdit,
                                contentDescription = stringResource(R.string.edit)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = ShapeDefaults.large,
                        containerColor = Color.Unspecified,
                        drawStartIconContainer = false
                    )

                    val allEmojis = Emoji.allIcons()

                    EmojiSelectionSheet(
                        selectedEmojiIndex = null,
                        allEmojis = allEmojis,
                        onEmojiPicked = {
                            onUpdateLayer(
                                layer.copy(
                                    type = type.copy(
                                        imageData = allEmojis[it]
                                    )
                                )
                            )
                            showEmojiPicker = false
                        },
                        visible = showEmojiPicker,
                        onDismiss = {
                            showEmojiPicker = false
                        }
                    )
                }
            }
        }
    }
}