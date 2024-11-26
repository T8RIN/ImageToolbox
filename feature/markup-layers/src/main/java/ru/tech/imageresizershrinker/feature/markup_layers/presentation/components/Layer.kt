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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.request.ImageRequest
import com.smarttoolfactory.colordetector.util.ColorUtil.roundToTwoDigits
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.MiniEditLarge
import ru.tech.imageresizershrinker.core.settings.presentation.model.UiFontFamily
import ru.tech.imageresizershrinker.core.ui.theme.toColor
import ru.tech.imageresizershrinker.core.ui.utils.provider.SafeLocalContainerColor
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.ColorRowSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.FontResSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.ImageSelector
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedModalBottomSheet
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedSliderItem
import ru.tech.imageresizershrinker.core.ui.widget.image.Picture
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.core.ui.widget.text.RoundedTextField
import ru.tech.imageresizershrinker.core.ui.widget.text.RoundedTextFieldColors
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem
import ru.tech.imageresizershrinker.feature.markup_layers.domain.LayerType
import ru.tech.imageresizershrinker.feature.markup_layers.presentation.components.model.UiMarkupLayer

@Composable
internal fun BoxWithConstraintsScope.Layer(
    layer: UiMarkupLayer,
    onActivate: () -> Unit,
    onUpdateLayer: (UiMarkupLayer) -> Unit
) {
    val type = layer.type

    var showEditDialog by rememberSaveable {
        mutableStateOf(false)
    }
    EditBox(
        state = layer.state,
        onTap = {
            if (layer.state.isActive) {
                showEditDialog = true
            } else {
                onActivate()
            }
        },
        content = {
            when (type) {
                is LayerType.Image -> {
                    Picture(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(type.imageData)
                            .size(1600)
                            .build(),
                        contentScale = ContentScale.Companion.Fit,
                        modifier = Modifier.Companion.sizeIn(
                            maxWidth = this@Layer.maxWidth / 2,
                            maxHeight = this@Layer.maxHeight / 2
                        ),
                        showTransparencyChecker = false
                    )
                }

                is LayerType.Text -> {
                    val style = LocalTextStyle.current
                    val mergedStyle by remember(style, type) {
                        derivedStateOf {
                            val fullSize = this@Layer.constraints.run { minOf(maxWidth, maxHeight) }

                            style.copy(
                                color = type.color.toColor(),
                                fontSize = (fullSize * type.size / 5).sp,
                                lineHeight = (fullSize * type.size / 5).sp,
                                fontFamily = UiFontFamily.Companion.entries.firstOrNull {
                                    (it.fontRes ?: 0) == type.font
                                }?.fontFamily,
                                drawStyle = when (type.style) {
                                    0 -> Fill
                                    1 -> Stroke()
                                    else -> null
                                }
                            )
                        }
                    }
                    AutoSizeText(
                        text = type.text,
                        style = mergedStyle,
                        modifier = Modifier.Companion
                            .background(
                                color = type.backgroundColor.toColor(),
                                shape = RoundedCornerShape(3.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        }
    )

    EnhancedModalBottomSheet(
        visible = showEditDialog,
        onDismiss = {
            showEditDialog = it
        },
        title = {
            TitleItem(
                icon = Icons.Rounded.MiniEditLarge,
                text = stringResource(R.string.edit_layer)
            )
        },
        confirmButton = {
            EnhancedButton(
                onClick = {
                    showEditDialog = false
                }
            ) {
                Text(stringResource(R.string.close))
            }
        }
    ) {
        Column(
            modifier = Modifier.Companion
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            when (type) {
                is LayerType.Image -> {
                    ImageSelector(
                        value = type.imageData,
                        onValueChange = {
                            onUpdateLayer(layer.copy(type.copy(it)))
                        },
                        subtitle = null,
                        color = Color.Companion.Unspecified
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
                        modifier = Modifier.Companion.container(
                            shape = ContainerShapeDefaults.defaultShape,
                            color = MaterialTheme.colorScheme.surface,
                            resultPadding = 8.dp
                        )
                    )
                    Spacer(Modifier.Companion.height(8.dp))
                    FontResSelector(
                        fontRes = type.font,
                        onValueChange = {
                            onUpdateLayer(
                                layer.copy(
                                    type.copy(
                                        font = it.fontRes ?: 0
                                    )
                                )
                            )
                        },
                        shape = ContainerShapeDefaults.topShape,
                        color = MaterialTheme.colorScheme.surface
                    )
                    Spacer(modifier = Modifier.Companion.height(4.dp))
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
                    Spacer(modifier = Modifier.Companion.height(4.dp))
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
                        titleFontWeight = FontWeight.Companion.Medium,
                        modifier = Modifier.Companion.container(
                            shape = ContainerShapeDefaults.centerShape,
                            color = MaterialTheme.colorScheme.surface
                        )
                    )
                    Spacer(modifier = Modifier.Companion.height(4.dp))
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
                        titleFontWeight = FontWeight.Companion.Medium,
                        modifier = Modifier.Companion.container(
                            shape = ContainerShapeDefaults.bottomShape,
                            color = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }
        }
    }
}