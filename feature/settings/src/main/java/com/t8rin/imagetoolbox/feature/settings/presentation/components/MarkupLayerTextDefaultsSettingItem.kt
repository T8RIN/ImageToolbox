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

package com.t8rin.imagetoolbox.feature.settings.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.colors.util.roundToTwoDigits
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.BackgroundColor
import com.t8rin.imagetoolbox.core.resources.icons.BorderColor
import com.t8rin.imagetoolbox.core.resources.icons.BorderStyle
import com.t8rin.imagetoolbox.core.resources.icons.FormatAlignCenter
import com.t8rin.imagetoolbox.core.resources.icons.FormatAlignLeft
import com.t8rin.imagetoolbox.core.resources.icons.FormatAlignRight
import com.t8rin.imagetoolbox.core.resources.icons.FormatBold
import com.t8rin.imagetoolbox.core.resources.icons.FormatItalic
import com.t8rin.imagetoolbox.core.resources.icons.FormatStrikethrough
import com.t8rin.imagetoolbox.core.resources.icons.FormatUnderlined
import com.t8rin.imagetoolbox.core.resources.icons.MiniEdit
import com.t8rin.imagetoolbox.core.resources.icons.Shadow
import com.t8rin.imagetoolbox.core.resources.icons.SkewMore
import com.t8rin.imagetoolbox.core.resources.icons.TextSticky
import com.t8rin.imagetoolbox.core.settings.domain.model.DomainFontFamily
import com.t8rin.imagetoolbox.core.settings.domain.model.MarkupLayerDropShadow
import com.t8rin.imagetoolbox.core.settings.domain.model.MarkupLayerOutline
import com.t8rin.imagetoolbox.core.settings.domain.model.MarkupLayerTextAlignment
import com.t8rin.imagetoolbox.core.settings.domain.model.MarkupLayerTextDecoration
import com.t8rin.imagetoolbox.core.settings.domain.model.MarkupLayerTextDefaults
import com.t8rin.imagetoolbox.core.settings.domain.model.MarkupLayerTextGeometricTransform
import com.t8rin.imagetoolbox.core.settings.presentation.model.asDomain
import com.t8rin.imagetoolbox.core.settings.presentation.model.asFontType
import com.t8rin.imagetoolbox.core.settings.presentation.model.toUiFont
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.inverseByLuma
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.theme.toColor
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ColorRowSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.FontSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedVerticalScroll
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch

@Composable
fun MarkupLayerTextDefaultsSettingItem(
    onValueChange: (MarkupLayerTextDefaults) -> Unit,
    shape: Shape = ShapeDefaults.bottom,
    modifier: Modifier = Modifier.padding(horizontal = 8.dp)
) {
    val storedDefaults = LocalSettingsState.current.markupLayerTextDefaults
    var defaults by remember(storedDefaults) { mutableStateOf(storedDefaults) }
    var showSheet by rememberSaveable { mutableStateOf(false) }
    val updateDefaults: (MarkupLayerTextDefaults) -> Unit = {
        defaults = it
        onValueChange(it)
    }

    PreferenceItem(
        modifier = modifier,
        title = stringResource(R.string.markup_layers),
        subtitle = stringResource(R.string.markup_layer_text_defaults_sub),
        startIcon = Icons.Outlined.TextSticky,
        endIcon = Icons.Rounded.MiniEdit,
        shape = shape,
        onClick = { showSheet = true }
    )

    EnhancedModalBottomSheet(
        visible = showSheet,
        onDismiss = { showSheet = it },
        title = {
            Row {
                MarkupLayerTextDecoration.entries.forEach { decoration ->
                    val selected = decoration in defaults.decorations
                    EnhancedIconButton(
                        onClick = {
                            updateDefaults(
                                defaults.copy(
                                    decorations = if (selected) {
                                        defaults.decorations - decoration
                                    } else {
                                        defaults.decorations + decoration
                                    }
                                )
                            )
                        },
                        containerColor = takeColorFromScheme {
                            if (selected) secondaryContainer else surface
                        },
                        contentColor = takeColorFromScheme {
                            if (selected) onSecondaryContainer else onSurface
                        }
                    ) {
                        Icon(
                            imageVector = decoration.icon,
                            contentDescription = null
                        )
                    }
                }
            }
        },
        confirmButton = {
            EnhancedButton(onClick = { showSheet = false }) {
                Text(stringResource(R.string.close))
            }
        }
    ) {
        Column(
            modifier = Modifier
                .enhancedVerticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            EnhancedButtonGroup(
                modifier = Modifier.container(
                    shape = ShapeDefaults.large,
                    color = MaterialTheme.colorScheme.surface
                ),
                title = stringResource(R.string.alignment),
                entries = MarkupLayerTextAlignment.entries,
                value = defaults.alignment,
                onValueChange = { updateDefaults(defaults.copy(alignment = it)) },
                itemContent = {
                    Icon(
                        imageVector = when (it) {
                            MarkupLayerTextAlignment.Start -> Icons.Rounded.FormatAlignLeft
                            MarkupLayerTextAlignment.Center -> Icons.Rounded.FormatAlignCenter
                            MarkupLayerTextAlignment.End -> Icons.Rounded.FormatAlignRight
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
                value = DomainFontFamily.fromString(defaults.font).asFontType().toUiFont(),
                onValueChange = {
                    updateDefaults(
                        defaults.copy(
                            font = it.type?.asDomain()?.asString()
                        )
                    )
                },
                shape = ShapeDefaults.top,
                containerColor = MaterialTheme.colorScheme.surface
            )
            Spacer(Modifier.height(4.dp))
            EnhancedSliderItem(
                value = defaults.size,
                title = stringResource(R.string.font_scale),
                internalStateTransformation = { it.roundToTwoDigits() },
                onValueChange = { updateDefaults(defaults.copy(size = it)) },
                valueRange = 0.01f..1f,
                shape = ShapeDefaults.center,
                containerColor = MaterialTheme.colorScheme.surface
            )
            Spacer(Modifier.height(4.dp))
            ColorRowSelector(
                value = defaults.backgroundColor.toColor(),
                onValueChange = {
                    updateDefaults(defaults.copy(backgroundColor = it.toArgb()))
                },
                title = stringResource(R.string.background_color),
                icon = Icons.Outlined.BackgroundColor,
                modifier = Modifier.container(
                    shape = ShapeDefaults.center,
                    color = MaterialTheme.colorScheme.surface
                )
            )
            Spacer(Modifier.height(4.dp))
            ColorRowSelector(
                value = defaults.color.toColor(),
                onValueChange = { updateDefaults(defaults.copy(color = it.toArgb())) },
                title = stringResource(R.string.text_color),
                modifier = Modifier.container(
                    shape = ShapeDefaults.center,
                    color = MaterialTheme.colorScheme.surface
                )
            )
            Spacer(Modifier.height(4.dp))
            TextGeometrySection(
                defaults = defaults,
                onValueChange = updateDefaults
            )
            Spacer(Modifier.height(4.dp))
            DropShadowDefaultsSection(
                shadow = defaults.shadow,
                onValueChange = { updateDefaults(defaults.copy(shadow = it)) }
            )
            Spacer(Modifier.height(4.dp))
            OutlineDefaultsSection(
                defaults = defaults,
                onValueChange = updateDefaults
            )
        }
    }
}

@Composable
private fun TextGeometrySection(
    defaults: MarkupLayerTextDefaults,
    onValueChange: (MarkupLayerTextDefaults) -> Unit
) {
    PreferenceRowSwitch(
        title = stringResource(R.string.text_geometry),
        subtitle = stringResource(R.string.text_geometry_sub),
        shape = ShapeDefaults.center,
        containerColor = MaterialTheme.colorScheme.surface,
        startIcon = Icons.Outlined.SkewMore,
        checked = defaults.geometricTransform != null,
        onClick = {
            onValueChange(
                defaults.copy(
                    geometricTransform = if (it) {
                        MarkupLayerTextGeometricTransform()
                    } else null
                )
            )
        },
        additionalContent = {
            AnimatedVisibility(
                visible = defaults.geometricTransform != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                val transform = defaults.geometricTransform ?: MarkupLayerTextGeometricTransform()
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    EnhancedSliderItem(
                        value = transform.scaleX,
                        title = stringResource(R.string.scale_x),
                        internalStateTransformation = { it.roundToTwoDigits() },
                        onValueChange = {
                            onValueChange(
                                defaults.copy(
                                    geometricTransform = transform.copy(scaleX = it)
                                )
                            )
                        },
                        valueRange = 0.25f..3f,
                        shape = ShapeDefaults.top,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    )
                    EnhancedSliderItem(
                        value = transform.skewX,
                        title = stringResource(R.string.skew_x),
                        internalStateTransformation = { it.roundToTwoDigits() },
                        onValueChange = {
                            onValueChange(
                                defaults.copy(
                                    geometricTransform = transform.copy(skewX = it)
                                )
                            )
                        },
                        valueRange = -1.5f..1.5f,
                        shape = ShapeDefaults.bottom,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    )
                }
            }
        }
    )
}

@Composable
private fun DropShadowDefaultsSection(
    shadow: MarkupLayerDropShadow?,
    onValueChange: (MarkupLayerDropShadow?) -> Unit
) {
    PreferenceRowSwitch(
        title = stringResource(R.string.add_shadow),
        subtitle = stringResource(R.string.add_shadow_sub),
        shape = ShapeDefaults.center,
        containerColor = MaterialTheme.colorScheme.surface,
        startIcon = Icons.Outlined.Shadow,
        checked = shadow != null,
        onClick = {
            onValueChange(if (it) MarkupLayerDropShadow.Default else null)
        },
        additionalContent = {
            AnimatedVisibility(
                visible = shadow != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                val resolvedShadow = shadow ?: MarkupLayerDropShadow.Default
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    ColorRowSelector(
                        value = resolvedShadow.color.toColor(),
                        onValueChange = {
                            onValueChange(resolvedShadow.copy(color = it.toArgb()))
                        },
                        title = stringResource(R.string.shadow_color),
                        modifier = Modifier.container(
                            shape = ShapeDefaults.top,
                            color = MaterialTheme.colorScheme.surfaceContainerLow
                        )
                    )
                    EnhancedSliderItem(
                        value = resolvedShadow.blurRadius,
                        title = stringResource(R.string.blur_radius),
                        internalStateTransformation = { it.roundToTwoDigits() },
                        onValueChange = {
                            onValueChange(resolvedShadow.copy(blurRadius = it))
                        },
                        valueRange = MarkupLayerDropShadow.BlurRadiusRange,
                        shape = ShapeDefaults.center,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    )
                    EnhancedSliderItem(
                        value = resolvedShadow.offsetX,
                        title = stringResource(R.string.offset_x),
                        internalStateTransformation = { it.roundToTwoDigits() },
                        onValueChange = {
                            onValueChange(resolvedShadow.copy(offsetX = it))
                        },
                        valueRange = MarkupLayerDropShadow.OffsetXRange,
                        shape = ShapeDefaults.center,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    )
                    EnhancedSliderItem(
                        value = resolvedShadow.offsetY,
                        title = stringResource(R.string.offset_y),
                        internalStateTransformation = { it.roundToTwoDigits() },
                        onValueChange = {
                            onValueChange(resolvedShadow.copy(offsetY = it))
                        },
                        valueRange = MarkupLayerDropShadow.OffsetYRange,
                        shape = ShapeDefaults.bottom,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    )
                }
            }
        }
    )
}

@Composable
private fun OutlineDefaultsSection(
    defaults: MarkupLayerTextDefaults,
    onValueChange: (MarkupLayerTextDefaults) -> Unit
) {
    PreferenceRowSwitch(
        title = stringResource(R.string.add_outline),
        subtitle = stringResource(R.string.add_outline_sub),
        shape = ShapeDefaults.bottom,
        containerColor = MaterialTheme.colorScheme.surface,
        startIcon = Icons.Rounded.BorderStyle,
        checked = defaults.outline != null,
        onClick = {
            onValueChange(
                defaults.copy(
                    outline = if (it) {
                        MarkupLayerOutline(
                            color = defaults.color.toColor().inverseByLuma().toArgb(),
                            width = 4f
                        )
                    } else null
                )
            )
        },
        additionalContent = {
            AnimatedVisibility(
                visible = defaults.outline != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                val outline = defaults.outline
                    ?: MarkupLayerOutline(
                        color = Color.Transparent.toArgb(),
                        width = 4f
                    )
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    ColorRowSelector(
                        value = outline.color.toColor(),
                        onValueChange = {
                            onValueChange(
                                defaults.copy(outline = outline.copy(color = it.toArgb()))
                            )
                        },
                        title = stringResource(R.string.outline_color),
                        icon = Icons.Outlined.BorderColor,
                        modifier = Modifier.container(
                            shape = ShapeDefaults.top,
                            color = MaterialTheme.colorScheme.surfaceContainerLow
                        )
                    )
                    EnhancedSliderItem(
                        value = outline.width,
                        title = stringResource(R.string.outline_size),
                        internalStateTransformation = { it.roundToTwoDigits() },
                        onValueChange = {
                            onValueChange(
                                defaults.copy(outline = outline.copy(width = it))
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

private val MarkupLayerTextDecoration.icon: ImageVector
    get() = when (this) {
        MarkupLayerTextDecoration.Bold -> Icons.Rounded.FormatBold
        MarkupLayerTextDecoration.Italic -> Icons.Rounded.FormatItalic
        MarkupLayerTextDecoration.Underline -> Icons.Rounded.FormatUnderlined
        MarkupLayerTextDecoration.LineThrough -> Icons.Rounded.FormatStrikethrough
    }
