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
import androidx.compose.material.icons.automirrored.rounded.FormatAlignLeft
import androidx.compose.material.icons.automirrored.rounded.FormatAlignRight
import androidx.compose.material.icons.outlined.BorderColor
import androidx.compose.material.icons.outlined.Percent
import androidx.compose.material.icons.outlined.Rectangle
import androidx.compose.material.icons.rounded.FormatAlignCenter
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
import com.t8rin.colors.util.roundToTwoDigits
import com.t8rin.imagetoolbox.core.domain.model.Outline
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.toggle
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.emoji.Emoji
import com.t8rin.imagetoolbox.core.resources.icons.AddPhotoAlt
import com.t8rin.imagetoolbox.core.resources.icons.BackgroundColor
import com.t8rin.imagetoolbox.core.resources.icons.MiniEdit
import com.t8rin.imagetoolbox.core.resources.icons.MiniEditLarge
import com.t8rin.imagetoolbox.core.resources.icons.Shadow
import com.t8rin.imagetoolbox.core.resources.icons.StackSticky
import com.t8rin.imagetoolbox.core.resources.shapes.MaterialStarShape
import com.t8rin.imagetoolbox.core.settings.presentation.model.toUiFont
import com.t8rin.imagetoolbox.core.ui.theme.inverseByLuma
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.theme.toColor
import com.t8rin.imagetoolbox.core.ui.utils.provider.SafeLocalContainerColor
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.AlphaSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.BlendingModeSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ColorRowSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.FontSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ImageSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
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
import com.t8rin.imagetoolbox.feature.markup_layers.domain.DropShadow
import com.t8rin.imagetoolbox.feature.markup_layers.domain.LayerType
import com.t8rin.imagetoolbox.feature.markup_layers.domain.LayerType.Text.Alignment
import com.t8rin.imagetoolbox.feature.markup_layers.domain.TextGeometricTransform
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.UiMarkupLayer
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.icon
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.titleRes
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.withCoerceToBoundsRecursively
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.screenLogic.MarkupLayersComponent
import kotlin.math.roundToInt

@Composable
internal fun EditLayerSheet(
    component: MarkupLayersComponent,
    visible: Boolean,
    onDismiss: (Boolean) -> Unit,
    onUpdateLayer: (UiMarkupLayer, Boolean) -> Unit,
    layer: UiMarkupLayer
) {
    val updateLayerWithHistory: (UiMarkupLayer) -> Unit = {
        onUpdateLayer(it, true)
    }
    val updateLayerContinuously: (UiMarkupLayer) -> Unit = {
        component.beginHistoryTransaction()
        onUpdateLayer(it, false)
    }
    val finishContinuousEdit = component::commitHistoryTransaction

    EnhancedModalBottomSheet(
        visible = visible,
        onDismiss = onDismiss,
        title = {
            if (layer.isGroup) {
                TitleItem(
                    icon = Icons.Outlined.StackSticky,
                    text = stringResource(R.string.edit_layer)
                )
            } else when (val type = layer.type) {
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
                                    updateLayerWithHistory(
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

                is LayerType.Shape -> {
                    TitleItem(
                        icon = type.shapeMode.kind.icon,
                        text = stringResource(type.shapeMode.kind.titleRes)
                    )
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
            if (!layer.isGroup) {
                when (val type = layer.type) {
                    is LayerType.Picture.Image -> {
                        ImageSelector(
                            value = type.imageData,
                            onValueChange = {
                                updateLayerWithHistory(
                                    layer.copy(
                                        type = type.copy(
                                            imageData = it
                                        )
                                    )
                                )
                            },
                            subtitle = null,
                            color = MaterialTheme.colorScheme.surface
                        )
                    }

                is LayerType.Text -> {
                    RoundedTextField(
                        value = type.text,
                        onValueChange = {
                            updateLayerWithHistory(
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
                            shape = ShapeDefaults.top,
                            color = MaterialTheme.colorScheme.surface,
                            resultPadding = 8.dp
                        ),
                        keyboardOptions = KeyboardOptions(),
                        singleLine = false
                    )
                    Spacer(Modifier.height(4.dp))
                    EnhancedButtonGroup(
                        modifier = Modifier
                            .container(
                                shape = ShapeDefaults.bottom,
                                color = MaterialTheme.colorScheme.surface
                            ),
                        title = stringResource(id = R.string.alignment),
                        entries = Alignment.entries,
                        value = type.alignment,
                        onValueChange = {
                            updateLayerWithHistory(
                                layer.copy(
                                    type = type.copy(alignment = it)
                                )
                            )
                        },
                        itemContent = {
                            Icon(
                                imageVector = when (it) {
                                    Alignment.Start -> Icons.AutoMirrored.Rounded.FormatAlignLeft
                                    Alignment.Center -> Icons.Rounded.FormatAlignCenter
                                    Alignment.End -> Icons.AutoMirrored.Rounded.FormatAlignRight
                                },
                                contentDescription = null
                            )
                        },
                        activeButtonColor = MaterialTheme.colorScheme.secondaryContainer,
                        inactiveButtonColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        isScrollable = false
                    )
                    Spacer(Modifier.height(8.dp))
                    FontSelector(
                        value = type.font.toUiFont(),
                        onValueChange = {
                            updateLayerWithHistory(
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
                            updateLayerContinuously(
                                layer.copy(
                                    type = type.copy(size = it)
                                )
                            )
                        },
                        onValueChangeFinished = { _ -> finishContinuousEdit() },
                        valueRange = 0.01f..1f,
                        shape = ShapeDefaults.center,
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    ColorRowSelector(
                        value = type.backgroundColor.toColor(),
                        onValueChange = {
                            updateLayerWithHistory(
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
                            updateLayerWithHistory(
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
                    var haveTextGeometry by remember {
                        mutableStateOf(type.geometricTransform != null)
                    }
                    LaunchedEffect(haveTextGeometry, type.geometricTransform) {
                        val desiredGeometricTransform = if (haveTextGeometry) {
                            type.geometricTransform ?: TextGeometricTransform()
                        } else null

                        if (type.geometricTransform != desiredGeometricTransform) {
                            updateLayerWithHistory(
                                layer.copy(
                                    type = type.copy(
                                        geometricTransform = desiredGeometricTransform
                                    )
                                )
                            )
                        }
                    }

                    PreferenceRowSwitch(
                        title = stringResource(R.string.text_geometry),
                        subtitle = stringResource(R.string.text_geometry_sub),
                        shape = ShapeDefaults.center,
                        containerColor = MaterialTheme.colorScheme.surface,
                        applyHorizontalPadding = false,
                        resultModifier = Modifier.padding(16.dp),
                        checked = haveTextGeometry,
                        onClick = {
                            haveTextGeometry = it
                        },
                        additionalContent = {
                            AnimatedVisibility(
                                visible = type.geometricTransform != null,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(top = 16.dp)
                                ) {
                                    EnhancedSliderItem(
                                        value = type.geometricTransform?.scaleX ?: 1f,
                                        title = stringResource(R.string.scale_x),
                                        internalStateTransformation = {
                                            it.roundToTwoDigits()
                                        },
                                        onValueChange = {
                                            updateLayerContinuously(
                                                layer.copy(
                                                    type = type.copy(
                                                        geometricTransform = type.geometricTransform?.copy(
                                                            scaleX = it
                                                        )
                                                    )
                                                )
                                            )
                                        },
                                        onValueChangeFinished = { _ -> finishContinuousEdit() },
                                        valueRange = 0.25f..3f,
                                        shape = ShapeDefaults.top,
                                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                                    )
                                    EnhancedSliderItem(
                                        value = type.geometricTransform?.skewX ?: 0f,
                                        title = stringResource(R.string.skew_x),
                                        internalStateTransformation = {
                                            it.roundToTwoDigits()
                                        },
                                        onValueChange = {
                                            updateLayerContinuously(
                                                layer.copy(
                                                    type = type.copy(
                                                        geometricTransform = type.geometricTransform?.copy(
                                                            skewX = it
                                                        )
                                                    )
                                                )
                                            )
                                        },
                                        onValueChangeFinished = { _ -> finishContinuousEdit() },
                                        valueRange = -1.5f..1.5f,
                                        shape = ShapeDefaults.bottom,
                                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                                    )
                                }
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    var haveShadow by remember {
                        mutableStateOf(type.shadow != null)
                    }
                    LaunchedEffect(haveShadow, type.shadow, type.color) {
                        val desiredShadow = if (haveShadow) {
                            type.shadow ?: DropShadow.Default
                        } else null

                        if (type.shadow != desiredShadow) {
                            updateLayerWithHistory(
                                layer.copy(
                                    type = type.copy(
                                        shadow = desiredShadow
                                    )
                                )
                            )
                        }
                    }

                    PreferenceRowSwitch(
                        title = stringResource(R.string.add_shadow),
                        subtitle = stringResource(R.string.add_shadow_sub),
                        shape = ShapeDefaults.center,
                        containerColor = MaterialTheme.colorScheme.surface,
                        applyHorizontalPadding = false,
                        resultModifier = Modifier.padding(16.dp),
                        checked = haveShadow,
                        onClick = {
                            haveShadow = it
                        },
                        additionalContent = {
                            AnimatedVisibility(
                                visible = type.shadow != null,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(top = 16.dp)
                                ) {
                                    ColorRowSelector(
                                        value = type.shadow?.color?.toColor() ?: Color.Transparent,
                                        onValueChange = {
                                            updateLayerWithHistory(
                                                layer.copy(
                                                    type = type.copy(
                                                        shadow = type.shadow?.copy(
                                                            color = it.toArgb()
                                                        )
                                                    )
                                                )
                                            )
                                        },
                                        title = stringResource(R.string.shadow_color),
                                        modifier = Modifier.container(
                                            shape = ShapeDefaults.top,
                                            color = MaterialTheme.colorScheme.surfaceContainerLow
                                        ),
                                        icon = Icons.Filled.Shadow
                                    )
                                    EnhancedSliderItem(
                                        value = type.shadow?.blurRadius ?: 0f,
                                        title = stringResource(R.string.blur_radius),
                                        internalStateTransformation = {
                                            it.roundToTwoDigits()
                                        },
                                        onValueChange = {
                                            updateLayerContinuously(
                                                layer.copy(
                                                    type = type.copy(
                                                        shadow = type.shadow?.copy(
                                                            blurRadius = it
                                                        )
                                                    )
                                                )
                                            )
                                        },
                                        onValueChangeFinished = { _ -> finishContinuousEdit() },
                                        valueRange = DropShadow.BlurRadiusRange,
                                        shape = ShapeDefaults.center,
                                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                                    )
                                    EnhancedSliderItem(
                                        value = type.shadow?.offsetX ?: 0f,
                                        title = stringResource(R.string.offset_x),
                                        internalStateTransformation = {
                                            it.roundToTwoDigits()
                                        },
                                        onValueChange = {
                                            updateLayerContinuously(
                                                layer.copy(
                                                    type = type.copy(
                                                        shadow = type.shadow?.copy(
                                                            offsetX = it
                                                        )
                                                    )
                                                )
                                            )
                                        },
                                        onValueChangeFinished = { _ -> finishContinuousEdit() },
                                        valueRange = DropShadow.OffsetXRange,
                                        shape = ShapeDefaults.center,
                                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                                    )
                                    EnhancedSliderItem(
                                        value = type.shadow?.offsetY ?: 0f,
                                        title = stringResource(R.string.offset_y),
                                        internalStateTransformation = {
                                            it.roundToTwoDigits()
                                        },
                                        onValueChange = {
                                            updateLayerContinuously(
                                                layer.copy(
                                                    type = type.copy(
                                                        shadow = type.shadow?.copy(
                                                            offsetY = it
                                                        )
                                                    )
                                                )
                                            )
                                        },
                                        onValueChangeFinished = { _ -> finishContinuousEdit() },
                                        valueRange = DropShadow.OffsetYRange,
                                        shape = ShapeDefaults.bottom,
                                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                                    )
                                }
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    var haveOutline by remember {
                        mutableStateOf(type.outline != null)
                    }
                    LaunchedEffect(haveOutline, type.outline, type.color) {
                        val desiredOutline = if (haveOutline) {
                            type.outline ?: Outline(
                                color = type.color.toColor()
                                    .inverseByLuma()
                                    .toArgb(),
                                width = 4f
                            )
                        } else null

                        if (type.outline != desiredOutline) {
                            updateLayerWithHistory(
                                layer.copy(
                                    type = type.copy(
                                        outline = desiredOutline
                                    )
                                )
                            )
                        }
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
                            AnimatedVisibility(
                                visible = type.outline != null,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(top = 16.dp)
                                ) {
                                    ColorRowSelector(
                                        value = type.outline?.color?.toColor() ?: Color.Transparent,
                                        onValueChange = {
                                            updateLayerWithHistory(
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
                                            updateLayerContinuously(
                                                layer.copy(
                                                    type = type.copy(
                                                        outline = type.outline?.copy(
                                                            width = it
                                                        )
                                                    )
                                                )
                                            )
                                        },
                                        onValueChangeFinished = { _ -> finishContinuousEdit() },
                                        valueRange = 0.01f..10f,
                                        shape = ShapeDefaults.bottom,
                                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                                    )
                                }
                            }
                        }
                    )
                }

                is LayerType.Shape -> {
                    ShapeLayerEditorSection(
                        layer = layer,
                        type = type,
                        onUpdateLayer = updateLayerWithHistory,
                        onUpdateLayerContinuously = updateLayerContinuously,
                        onContinuousEditFinished = finishContinuousEdit
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
                        containerColor = MaterialTheme.colorScheme.surface,
                        drawStartIconContainer = false
                    )

                    val allEmojis = Emoji.allIcons()

                    EmojiSelectionSheet(
                        selectedEmojiIndex = null,
                        allEmojis = allEmojis,
                        onEmojiPicked = {
                            updateLayerWithHistory(
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

                (layer.type as? LayerType.Picture)?.let { type ->
                    Spacer(modifier = Modifier.height(4.dp))
                    PictureShadowSection(
                        layer = layer,
                        type = type,
                        onUpdateLayer = updateLayerWithHistory,
                        onUpdateLayerContinuously = updateLayerContinuously,
                        onContinuousEditFinished = finishContinuousEdit
                    )
                }

                (layer.type as? LayerType.Shape)?.let { type ->
                    Spacer(modifier = Modifier.height(4.dp))
                    ShapeShadowSection(
                        layer = layer,
                        type = type,
                        onUpdateLayer = updateLayerWithHistory,
                        onUpdateLayerContinuously = updateLayerContinuously,
                        onContinuousEditFinished = finishContinuousEdit
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
            PreferenceRowSwitch(
                title = stringResource(R.string.coerce_points_to_image_bounds),
                subtitle = stringResource(R.string.coerce_points_to_image_bounds_sub),
                startIcon = Icons.Outlined.Rectangle,
                checked = layer.state.coerceToBounds,
                onClick = {
                    updateLayerWithHistory(
                        layer.withCoerceToBoundsRecursively(it)
                    )
                },
                shape = if (layer.isGroup) ShapeDefaults.large else ShapeDefaults.top,
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.surface
            )
            if (!layer.isGroup) {
                Spacer(modifier = Modifier.height(4.dp))
                AlphaSelector(
                    value = layer.state.alpha,
                    onValueChange = {
                        component.beginHistoryTransaction()
                        component.updateLayerState(
                            layer = layer,
                            commitToHistory = false
                        ) {
                            alpha = it
                        }
                    },
                    onValueChangeFinished = { _ ->
                        finishContinuousEdit()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    title = stringResource(R.string.layer_alpha),
                    color = MaterialTheme.colorScheme.surface,
                    shape = ShapeDefaults.center
                )
                Spacer(modifier = Modifier.height(4.dp))
                BlendingModeSelector(
                    value = layer.blendingMode,
                    onValueChange = {
                        updateLayerWithHistory(
                            layer.copy(blendingMode = it)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    shape = if (layer.type is LayerType.Shape) {
                        ShapeDefaults.bottom
                    } else {
                        ShapeDefaults.center
                    }
                )
                if (layer.type !is LayerType.Shape) {
                    Spacer(modifier = Modifier.height(4.dp))
                    EnhancedSliderItem(
                        value = layer.cornerRadiusPercent,
                        title = stringResource(R.string.corners_size),
                        icon = Icons.Outlined.Percent,
                        internalStateTransformation = {
                            it.roundToInt()
                        },
                        onValueChange = {
                            updateLayerContinuously(
                                layer.copy(
                                    cornerRadiusPercent = it.roundToInt().coerceIn(0, 50)
                                )
                            )
                        },
                        onValueChangeFinished = { _ -> finishContinuousEdit() },
                        valueRange = 0f..50f,
                        steps = 49,
                        shape = ShapeDefaults.bottom,
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                }
            }
        }
    }
}

@Composable
private fun PictureShadowSection(
    layer: UiMarkupLayer,
    type: LayerType.Picture,
    onUpdateLayer: (UiMarkupLayer) -> Unit,
    onUpdateLayerContinuously: (UiMarkupLayer) -> Unit,
    onContinuousEditFinished: () -> Unit
) {
    var haveShadow by remember {
        mutableStateOf(type.shadow != null)
    }
    LaunchedEffect(haveShadow, type.shadow) {
        val desiredShadow = if (haveShadow) {
            type.shadow ?: DropShadow.Default
        } else null

        if (type.shadow != desiredShadow) {
            onUpdateLayer(
                layer.copy(
                    type = type.withShadow(desiredShadow)
                )
            )
        }
    }

    PreferenceRowSwitch(
        title = stringResource(R.string.add_shadow),
        subtitle = stringResource(R.string.add_shadow_sub),
        shape = ShapeDefaults.large,
        containerColor = MaterialTheme.colorScheme.surface,
        applyHorizontalPadding = false,
        resultModifier = Modifier.padding(16.dp),
        checked = haveShadow,
        onClick = {
            haveShadow = it
        },
        additionalContent = {
            AnimatedVisibility(
                visible = type.shadow != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    ColorRowSelector(
                        value = type.shadow?.color?.toColor() ?: Color.Transparent,
                        onValueChange = {
                            onUpdateLayer(
                                layer.copy(
                                    type = type.withShadow(
                                        type.shadow?.copy(
                                            color = it.toArgb()
                                        )
                                    )
                                )
                            )
                        },
                        title = stringResource(R.string.shadow_color),
                        modifier = Modifier.container(
                            shape = ShapeDefaults.top,
                            color = MaterialTheme.colorScheme.surfaceContainerLow
                        ),
                        icon = Icons.Filled.Shadow
                    )
                    EnhancedSliderItem(
                        value = type.shadow?.blurRadius ?: 0f,
                        title = stringResource(R.string.blur_radius),
                        internalStateTransformation = {
                            it.roundToTwoDigits()
                        },
                        onValueChange = {
                            onUpdateLayerContinuously(
                                layer.copy(
                                    type = type.withShadow(
                                        type.shadow?.copy(
                                            blurRadius = it
                                        )
                                    )
                                )
                            )
                        },
                        onValueChangeFinished = { _ -> onContinuousEditFinished() },
                        valueRange = DropShadow.BlurRadiusRange,
                        shape = ShapeDefaults.center,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    )
                    EnhancedSliderItem(
                        value = type.shadow?.offsetX ?: 0f,
                        title = stringResource(R.string.offset_x),
                        internalStateTransformation = {
                            it.roundToTwoDigits()
                        },
                        onValueChange = {
                            onUpdateLayerContinuously(
                                layer.copy(
                                    type = type.withShadow(
                                        type.shadow?.copy(
                                            offsetX = it
                                        )
                                    )
                                )
                            )
                        },
                        onValueChangeFinished = { _ -> onContinuousEditFinished() },
                        valueRange = DropShadow.OffsetXRange,
                        shape = ShapeDefaults.center,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    )
                    EnhancedSliderItem(
                        value = type.shadow?.offsetY ?: 0f,
                        title = stringResource(R.string.offset_y),
                        internalStateTransformation = {
                            it.roundToTwoDigits()
                        },
                        onValueChange = {
                            onUpdateLayerContinuously(
                                layer.copy(
                                    type = type.withShadow(
                                        type.shadow?.copy(
                                            offsetY = it
                                        )
                                    )
                                )
                            )
                        },
                        onValueChangeFinished = { _ -> onContinuousEditFinished() },
                        valueRange = DropShadow.OffsetYRange,
                        shape = ShapeDefaults.bottom,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    )
                }
            }
        }
    )
}

private fun LayerType.Picture.withShadow(
    shadow: DropShadow?
): LayerType.Picture = when (this) {
    is LayerType.Picture.Image -> copy(shadow = shadow)
    is LayerType.Picture.Sticker -> copy(shadow = shadow)
}
