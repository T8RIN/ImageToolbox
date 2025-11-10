/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.replaceAt
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Swatch
import com.t8rin.imagetoolbox.core.ui.utils.helper.toHex
import com.t8rin.imagetoolbox.core.ui.widget.color_picker.ColorPickerSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedChip
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateContentSizeNoClip
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.shapeByInteraction
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.feature.palette_tools.presentation.components.model.NamedPalette
import com.t8rin.palette.PaletteFormat


@Composable
internal fun EditPaletteControls(
    paletteFormat: PaletteFormat?,
    onPaletteFormatChange: (PaletteFormat) -> Unit,
    palette: NamedPalette,
    onPaletteChange: (NamedPalette) -> Unit
) {
    Spacer(modifier = Modifier.height(16.dp))

    RoundedTextField(
        value = palette.name,
        onValueChange = {
            onPaletteChange(
                palette.copy(
                    name = it
                )
            )
        },
        modifier = Modifier
            .container(
                shape = ShapeDefaults.top,
                resultPadding = 8.dp
            ),
        label = { Text(stringResource(R.string.palette_name)) },
        startIcon = {
            Icon(
                imageVector = Icons.Rounded.Swatch,
                contentDescription = null
            )
        }
    )
    Spacer(modifier = Modifier.height(4.dp))
    PaletteFormatSelector(
        shape = ShapeDefaults.bottom,
        value = paletteFormat ?: PaletteFormat.JSON,
        onValueChange = onPaletteFormatChange
    )
    Spacer(modifier = Modifier.height(12.dp))
    AnimatedVisibility(
        visible = palette.colors.isNotEmpty(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.container(resultPadding = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            palette.colors.forEachIndexed { index, data ->
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
                var showColorPicker by remember {
                    mutableStateOf(false)
                }

                ColorPickerSheet(
                    visible = showColorPicker,
                    onDismiss = { showColorPicker = false },
                    color = data.color,
                    onColorSelected = {
                        onPaletteChange(
                            palette.copy(
                                colors = palette.colors.replaceAt(index) { item ->
                                    item.copy(
                                        color = it.copy(1f)
                                    )
                                }
                            )
                        )
                    },
                    allowAlpha = false
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
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .container(
                                shape = CircleShape,
                                color = data.color
                            )
                    )

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

                    val containerColor =
                        MaterialTheme.colorScheme.secondaryContainer
                    val interactionSource =
                        remember { MutableInteractionSource() }

                    Box(
                        modifier = Modifier
                            .container(
                                shape = shapeByInteraction(
                                    shape = CircleShape,
                                    pressedShape = ShapeDefaults.pressed,
                                    interactionSource = interactionSource
                                ),
                                color = containerColor,
                                resultPadding = 0.dp,
                            )
                            .hapticsClickable(
                                interactionSource = interactionSource,
                                indication = LocalIndication.current
                            ) {
                                showColorPicker = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "#FFFFFF",
                            fontSize = 15.sp,
                            modifier = Modifier
                                .padding(
                                    vertical = 8.dp,
                                    horizontal = 16.dp
                                )
                                .alpha(0f)
                        )

                        Text(
                            text = remember(data.color) {
                                data.color.toHex().uppercase()
                            },
                            color = MaterialTheme.colorScheme.contentColorFor(
                                containerColor
                            ),
                            fontSize = 15.sp,
                            modifier = Modifier
                                .padding(
                                    vertical = 8.dp,
                                    horizontal = 16.dp
                                )
                        )
                    }
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
internal fun PaletteFormatSelector(
    modifier: Modifier = Modifier,
    shape: Shape = ShapeDefaults.extraLarge,
    backgroundColor: Color = Color.Unspecified,
    entries: List<PaletteFormat> = PaletteFormat.formatsWithDecodeAndEncode,
    value: PaletteFormat,
    onValueChange: (PaletteFormat) -> Unit
) {
    Column(
        modifier = modifier
            .container(
                shape = shape,
                color = backgroundColor
            )
            .animateContentSizeNoClip(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.palette_format),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )

        AnimatedContent(
            targetState = entries,
            modifier = Modifier.fillMaxWidth()
        ) { items ->
            FlowRow(
                verticalArrangement = Arrangement.spacedBy(
                    space = 8.dp,
                    alignment = Alignment.CenterVertically
                ),
                horizontalArrangement = Arrangement.spacedBy(
                    space = 8.dp,
                    alignment = Alignment.CenterHorizontally
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .container(
                        shape = ShapeDefaults.default,
                        color = MaterialTheme.colorScheme.surface
                    )
                    .padding(horizontal = 8.dp, vertical = 12.dp)
            ) {
                items.forEach {
                    EnhancedChip(
                        onClick = {
                            onValueChange(it)
                        },
                        selected = value == it,
                        label = {
                            Text(text = it.name.uppercase().replace("_", " "))
                        },
                        selectedColor = MaterialTheme.colorScheme.tertiary,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }
        }
        Spacer(Modifier.height(4.dp))
    }
}
