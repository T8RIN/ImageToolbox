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

package com.t8rin.imagetoolbox.feature.palette_tools.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.InsertDriveFile
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.replaceAt
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.resources.icons.Swatch
import com.t8rin.imagetoolbox.core.ui.theme.inverse
import com.t8rin.imagetoolbox.core.ui.utils.helper.toHex
import com.t8rin.imagetoolbox.core.ui.widget.color_picker.ColorPickerSheet
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.DataSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.shapeByInteraction
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.feature.palette_tools.presentation.components.model.NamedColor
import com.t8rin.imagetoolbox.feature.palette_tools.presentation.components.model.NamedPalette
import com.t8rin.imagetoolbox.feature.palette_tools.presentation.components.model.PaletteFormatHelper
import com.t8rin.palette.PaletteFormat


@Composable
internal fun EditPaletteControls(
    paletteFormat: PaletteFormat?,
    onPaletteFormatChange: (PaletteFormat) -> Unit,
    palette: NamedPalette,
    onPaletteChange: (NamedPalette) -> Unit
) {
    Spacer(modifier = Modifier.height(16.dp))

    val entries = PaletteFormatHelper.entries
    val format = paletteFormat ?: entries.first()

    AnimatedContent(
        targetState = format,
        contentKey = { it.withPaletteName },
        modifier = Modifier.fillMaxWidth()
    ) { formatAnimated ->
        RoundedTextField(
            value = palette.name,
            onValueChange = {
                onPaletteChange(
                    palette.copy(
                        name = it
                    )
                )
            },
            enabled = formatAnimated.withPaletteName,
            modifier = Modifier
                .container(
                    shape = ShapeDefaults.top,
                    resultPadding = 8.dp
                ),
            isError = !formatAnimated.withPaletteName,
            supportingText = if (!formatAnimated.withPaletteName) {
                {
                    Text(
                        stringResource(
                            R.string.palette_name_not_supported,
                            formatAnimated.name.uppercase().replace("_", " ")
                        )
                    )
                }
            } else null,
            label = { Text(stringResource(R.string.palette_name)) },
            startIcon = {
                Icon(
                    imageVector = Icons.Rounded.Swatch,
                    contentDescription = null
                )
            }
        )
    }
    Spacer(modifier = Modifier.height(4.dp))
    DataSelector(
        shape = ShapeDefaults.bottom,
        value = format,
        onValueChange = onPaletteFormatChange,
        entries = entries,
        title = stringResource(R.string.palette_format),
        titleIcon = Icons.AutoMirrored.Rounded.InsertDriveFile,
        itemContentText = {
            it.name.uppercase().replace("_", " ")
        },
        badgeContent = {
            Text(entries.size.toString())
        }
    )
    Spacer(modifier = Modifier.height(12.dp))
    Column(
        modifier = Modifier.container(resultPadding = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        (palette.colors + null).forEachIndexed { index, data ->
            var showColorPicker by remember {
                mutableStateOf(false)
            }

            ColorPickerSheet(
                visible = showColorPicker,
                onDismiss = { showColorPicker = false },
                color = data?.color,
                onColorSelected = {
                    onPaletteChange(
                        palette.copy(
                            colors = if (data == null) {
                                palette.colors + NamedColor(
                                    color = it,
                                    name = ""
                                )
                            } else {
                                palette.colors.replaceAt(index) { item ->
                                    item.copy(
                                        color = it.copy(1f)
                                    )
                                }
                            }
                        )
                    )
                },
                allowAlpha = false
            )

            if (data == null) {
                PreferenceItem(
                    onClick = { showColorPicker = true },
                    title = stringResource(R.string.add_color),
                    subtitle = stringResource(R.string.add_color_palette_sub),
                    startIcon = Icons.Rounded.Palette,
                    endIcon = Icons.Outlined.AddCircleOutline,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    shape = ShapeDefaults.default,
                    containerColor = MaterialTheme.colorScheme.surface
                )
            } else {
                val baseShape = ShapeDefaults.byIndex(
                    index = index,
                    size = palette.colors.size
                )
                val interactionSource = remember { MutableInteractionSource() }
                val shape = shapeByInteraction(
                    shape = baseShape,
                    pressedShape = ShapeDefaults.pressed,
                    interactionSource = interactionSource
                )

                Row(
                    modifier = Modifier
                        .container(
                            shape = shape,
                            color = MaterialTheme.colorScheme.surface,
                            resultPadding = 0.dp
                        )
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val colorInteractionSource =
                        remember { MutableInteractionSource() }

                    Row(
                        modifier = Modifier
                            .container(
                                shape = ShapeDefaults.circle,
                                color = data.color.inverse(
                                    fraction = { 0.8f },
                                    darkMode = data.color.luminance() < 0.3f
                                )
                            )
                            .hapticsClickable(
                                interactionSource = colorInteractionSource,
                                indication = LocalIndication.current
                            ) {
                                showColorPicker = true
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .container(
                                    shape = ShapeDefaults.circle,
                                    color = data.color
                                )
                        )

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(
                                horizontal = 8.dp
                            )
                        ) {
                            Text(
                                text = "#FFFFFF",
                                fontSize = 15.sp,
                                modifier = Modifier.alpha(0f)
                            )

                            Text(
                                text = remember(data.color) {
                                    data.color.toHex().uppercase()
                                },
                                color = data.color,
                                fontSize = 15.sp
                            )
                        }
                    }

                    PaletteColorNameField(
                        value = data.name,
                        onValueChange = {
                            onPaletteChange(
                                palette.copy(
                                    colors = palette.colors.replaceAt(index) { item ->
                                        item.copy(
                                            name = it
                                        )
                                    }
                                )
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 40.dp)
                    )

                    EnhancedIconButton(
                        onClick = {
                            onPaletteChange(
                                palette.copy(
                                    colors = palette.colors - data
                                )
                            )
                        },
                        modifier = Modifier.size(28.dp, 40.dp),
                        forceMinimumInteractiveComponentSize = false,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(0.5f)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}
