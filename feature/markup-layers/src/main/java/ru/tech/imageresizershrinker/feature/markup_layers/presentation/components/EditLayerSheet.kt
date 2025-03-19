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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.BorderColor
import androidx.compose.material.icons.rounded.FormatColorFill
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.colordetector.util.ColorUtil.roundToTwoDigits
import ru.tech.imageresizershrinker.core.domain.model.Outline
import ru.tech.imageresizershrinker.core.domain.utils.ListUtils.toggle
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.emoji.Emoji
import ru.tech.imageresizershrinker.core.resources.icons.MiniEdit
import ru.tech.imageresizershrinker.core.resources.icons.MiniEditLarge
import ru.tech.imageresizershrinker.core.resources.shapes.MaterialStarShape
import ru.tech.imageresizershrinker.core.settings.presentation.model.toUiFont
import ru.tech.imageresizershrinker.core.ui.theme.inverseByLuma
import ru.tech.imageresizershrinker.core.ui.theme.takeColorFromScheme
import ru.tech.imageresizershrinker.core.ui.theme.toColor
import ru.tech.imageresizershrinker.core.ui.utils.provider.SafeLocalContainerColor
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.ColorRowSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.FontSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.ImageSelector
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedModalBottomSheet
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedSliderItem
import ru.tech.imageresizershrinker.core.ui.widget.image.Picture
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItemOverload
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceRowSwitch
import ru.tech.imageresizershrinker.core.ui.widget.sheets.EmojiSelectionSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.RoundedTextField
import ru.tech.imageresizershrinker.core.ui.widget.text.RoundedTextFieldColors
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem
import ru.tech.imageresizershrinker.feature.markup_layers.domain.DomainTextDecoration
import ru.tech.imageresizershrinker.feature.markup_layers.domain.LayerType
import ru.tech.imageresizershrinker.feature.markup_layers.presentation.components.model.UiMarkupLayer
import ru.tech.imageresizershrinker.feature.markup_layers.presentation.components.model.icon

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
                                            type.copy(
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            when (val type = layer.type) {
                is LayerType.Picture.Image -> {
                    ImageSelector(
                        value = type.imageData,
                        onValueChange = {
                            onUpdateLayer(layer.copy(type.copy(it)))
                        },
                        subtitle = null,
                        color = Color.Unspecified
                    )
                }

                is LayerType.Text -> {
                    RoundedTextField(
                        value = type.text,
                        onValueChange = {
                            onUpdateLayer(layer.copy(type.copy(text = it)))
                        },
                        hint = stringResource(R.string.text),
                        colors = RoundedTextFieldColors(
                            isError = false,
                            containerColor = SafeLocalContainerColor
                        ),
                        modifier = Modifier.container(
                            shape = ContainerShapeDefaults.defaultShape,
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
                                    type.copy(
                                        font = it.type
                                    )
                                )
                            )
                        },
                        shape = ContainerShapeDefaults.topShape,
                        color = MaterialTheme.colorScheme.surface
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
                                    type.copy(size = it)
                                )
                            )
                        },
                        valueRange = 0.01f..1f,
                        shape = ContainerShapeDefaults.centerShape,
                        color = MaterialTheme.colorScheme.surface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    ColorRowSelector(
                        value = type.backgroundColor.toColor(),
                        onValueChange = {
                            onUpdateLayer(
                                layer.copy(
                                    type.copy(
                                        backgroundColor = it.toArgb()
                                    )
                                )
                            )
                        },
                        title = stringResource(R.string.background_color),
                        icon = Icons.Rounded.FormatColorFill,
                        titleFontWeight = FontWeight.Medium,
                        modifier = Modifier.container(
                            shape = ContainerShapeDefaults.centerShape,
                            color = MaterialTheme.colorScheme.surface
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    ColorRowSelector(
                        value = type.color.toColor(),
                        onValueChange = {
                            onUpdateLayer(
                                layer.copy(
                                    type.copy(
                                        color = it.toArgb()
                                    )
                                )
                            )
                        },
                        title = stringResource(R.string.text_color),
                        titleFontWeight = FontWeight.Medium,
                        modifier = Modifier.container(
                            shape = ContainerShapeDefaults.centerShape,
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
                                type.copy(
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
                        shape = ContainerShapeDefaults.bottomShape,
                        color = MaterialTheme.colorScheme.surface,
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
                                                    type.copy(
                                                        outline = type.outline?.copy(
                                                            color = it.toArgb()
                                                        )
                                                    )
                                                )
                                            )
                                        },
                                        title = stringResource(R.string.outline_color),
                                        titleFontWeight = FontWeight.Medium,
                                        modifier = Modifier.container(
                                            shape = ContainerShapeDefaults.topShape,
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
                                                    type.copy(
                                                        outline = type.outline?.copy(
                                                            width = it
                                                        )
                                                    )
                                                )
                                            )
                                        },
                                        valueRange = 0.01f..10f,
                                        shape = ContainerShapeDefaults.bottomShape,
                                        color = MaterialTheme.colorScheme.surfaceContainerLow
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
                                        imageVector = Icons.Outlined.AddPhotoAlternate,
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
                        shape = RoundedCornerShape(20.dp),
                        color = Color.Unspecified,
                        drawStartIconContainer = false
                    )

                    val allEmojis = Emoji.allIcons()

                    EmojiSelectionSheet(
                        selectedEmojiIndex = null,
                        allEmojis = allEmojis,
                        onEmojiPicked = {
                            onUpdateLayer(layer.copy(type.copy(allEmojis[it])))
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