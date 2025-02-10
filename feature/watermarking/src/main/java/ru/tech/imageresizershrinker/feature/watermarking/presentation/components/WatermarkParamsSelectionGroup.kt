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

package ru.tech.imageresizershrinker.feature.watermarking.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TextRotationAngleup
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.colordetector.util.ColorUtil.roundToTwoDigits
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.model.toUiFont
import ru.tech.imageresizershrinker.core.ui.theme.toColor
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.AlphaSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.BlendingModeSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.ColorRowSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.FontSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.PositionSelector
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedSliderItem
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.other.ExpandableItem
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceRowSwitch
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem
import ru.tech.imageresizershrinker.feature.watermarking.domain.WatermarkParams
import ru.tech.imageresizershrinker.feature.watermarking.domain.WatermarkingType
import ru.tech.imageresizershrinker.feature.watermarking.domain.copy
import kotlin.math.roundToInt

@Composable
fun WatermarkParamsSelectionGroup(
    value: WatermarkParams,
    onValueChange: (WatermarkParams) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }
    val pressed by interactionSource.collectIsPressedAsState()
    val cornerSize by animateDpAsState(
        if (pressed) 8.dp
        else 24.dp
    )
    ExpandableItem(
        modifier = modifier,
        interactionSource = interactionSource,
        color = MaterialTheme.colorScheme.surfaceContainer,
        visibleContent = {
            TitleItem(
                text = stringResource(id = R.string.properties),
                icon = Icons.Outlined.Tune
            )
        },
        expandableContent = {
            Column(
                modifier = Modifier.padding(end = 8.dp, start = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                AnimatedVisibility(visible = !value.isRepeated && value.watermarkingType !is WatermarkingType.Stamp) {
                    Column {
                        EnhancedSliderItem(
                            value = value.positionX,
                            title = stringResource(id = R.string.offset_x),
                            internalStateTransformation = {
                                it.roundToTwoDigits()
                            },
                            onValueChange = {
                                onValueChange(value.copy(positionX = it))
                            },
                            valueRange = 0f..1f,
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        EnhancedSliderItem(
                            value = value.positionY,
                            title = stringResource(id = R.string.offset_y),
                            internalStateTransformation = {
                                it.roundToTwoDigits()
                            },
                            onValueChange = {
                                onValueChange(value.copy(positionY = it))
                            },
                            valueRange = 0f..1f,
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }

                AnimatedVisibility(visible = value.watermarkingType !is WatermarkingType.Stamp) {
                    EnhancedSliderItem(
                        value = value.rotation,
                        icon = Icons.Outlined.TextRotationAngleup,
                        title = stringResource(id = R.string.angle),
                        valueRange = 0f..360f,
                        internalStateTransformation = { it.roundToInt() },
                        onValueChange = {
                            onValueChange(value.copy(rotation = it.roundToInt()))
                        },
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface
                    )
                }

                AlphaSelector(
                    value = value.alpha,
                    onValueChange = {
                        onValueChange(value.copy(alpha = it))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface
                )

                AnimatedVisibility(visible = value.watermarkingType !is WatermarkingType.Stamp) {
                    PreferenceRowSwitch(
                        title = stringResource(id = R.string.repeat_watermark),
                        subtitle = stringResource(id = R.string.repeat_watermark_sub),
                        checked = value.isRepeated,
                        startIcon = Icons.Rounded.Repeat,
                        onClick = {
                            onValueChange(value.copy(isRepeated = it))
                        },
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface
                    )
                }

                BlendingModeSelector(
                    value = value.overlayMode,
                    onValueChange = {
                        onValueChange(
                            value.copy(overlayMode = it)
                        )
                    }
                )

                AnimatedVisibility(visible = value.watermarkingType is WatermarkingType.Text) {
                    val type = value.watermarkingType as? WatermarkingType.Text
                        ?: return@AnimatedVisibility

                    Column {
                        FontSelector(
                            value = type.params.font.toUiFont(),
                            onValueChange = {
                                onValueChange(
                                    value.copy(
                                        watermarkingType = type.copy(
                                            type.params.copy(
                                                font = it.type
                                            )
                                        )
                                    )
                                )
                            }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        EnhancedSliderItem(
                            value = type.params.size,
                            title = stringResource(R.string.watermark_size),
                            internalStateTransformation = {
                                it.roundToTwoDigits()
                            },
                            onValueChange = {
                                onValueChange(
                                    value.copy(
                                        watermarkingType = type.copy(
                                            type.params.copy(
                                                size = it
                                            )
                                        )
                                    )
                                )
                            },
                            valueRange = 0.01f..1f,
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        ColorRowSelector(
                            value = type.params.color.toColor(),
                            onValueChange = {
                                onValueChange(
                                    value.copy(
                                        watermarkingType = type.copy(
                                            type.params.copy(
                                                color = it.toArgb()
                                            )
                                        )
                                    )
                                )
                            },
                            title = stringResource(R.string.text_color),
                            titleFontWeight = FontWeight.Medium,
                            modifier = Modifier.container(
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.surface
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        ColorRowSelector(
                            value = type.params.backgroundColor.toColor(),
                            onValueChange = {
                                onValueChange(
                                    value.copy(
                                        watermarkingType = type.copy(
                                            type.params.copy(
                                                backgroundColor = it.toArgb()
                                            )
                                        )
                                    )
                                )
                            },
                            title = stringResource(R.string.background_color),
                            titleFontWeight = FontWeight.Medium,
                            modifier = Modifier.container(
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.surface
                            )
                        )
                    }
                }

                AnimatedVisibility(visible = value.watermarkingType is WatermarkingType.Image) {
                    val type = value.watermarkingType as? WatermarkingType.Image
                        ?: return@AnimatedVisibility

                    EnhancedSliderItem(
                        value = type.size,
                        title = stringResource(R.string.watermark_size),
                        internalStateTransformation = {
                            it.roundToTwoDigits()
                        },
                        onValueChange = {
                            onValueChange(
                                value.copy(
                                    watermarkingType = type.copy(size = it)
                                )
                            )
                        },
                        valueRange = 0.01f..1f,
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface
                    )
                }

                AnimatedVisibility(visible = value.watermarkingType is WatermarkingType.Stamp) {
                    val type = value.watermarkingType as? WatermarkingType.Stamp
                        ?: return@AnimatedVisibility

                    Column {
                        FontSelector(
                            value = type.params.font.toUiFont(),
                            onValueChange = {
                                onValueChange(
                                    value.copy(
                                        watermarkingType = type.copy(
                                            params = type.params.copy(
                                                font = it.type
                                            )
                                        )
                                    )
                                )
                            }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        PositionSelector(
                            value = type.position,
                            onValueChange = {
                                onValueChange(
                                    value.copy(
                                        watermarkingType = type.copy(
                                            position = it
                                        )
                                    )
                                )
                            },
                            selectedItemColor = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        EnhancedSliderItem(
                            value = type.padding,
                            title = stringResource(R.string.padding),
                            internalStateTransformation = {
                                it.roundToTwoDigits()
                            },
                            onValueChange = {
                                onValueChange(
                                    value.copy(
                                        watermarkingType = type.copy(
                                            padding = it
                                        )
                                    )
                                )
                            },
                            valueRange = 0f..50f,
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        EnhancedSliderItem(
                            value = type.params.size,
                            title = stringResource(R.string.watermark_size),
                            internalStateTransformation = {
                                it.roundToTwoDigits()
                            },
                            onValueChange = {
                                onValueChange(
                                    value.copy(
                                        watermarkingType = type.copy(
                                            params = type.params.copy(
                                                size = it
                                            )
                                        )
                                    )
                                )
                            },
                            valueRange = 0.01f..1f,
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        ColorRowSelector(
                            value = type.params.color.toColor(),
                            onValueChange = {
                                onValueChange(
                                    value.copy(
                                        watermarkingType = type.copy(
                                            params = type.params.copy(
                                                color = it.toArgb()
                                            )
                                        )
                                    )
                                )
                            },
                            title = stringResource(R.string.text_color),
                            titleFontWeight = FontWeight.Medium,
                            modifier = Modifier.container(
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.surface
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        ColorRowSelector(
                            value = type.params.backgroundColor.toColor(),
                            onValueChange = {
                                onValueChange(
                                    value.copy(
                                        watermarkingType = type.copy(
                                            params = type.params.copy(
                                                backgroundColor = it.toArgb()
                                            )
                                        )
                                    )
                                )
                            },
                            title = stringResource(R.string.background_color),
                            titleFontWeight = FontWeight.Medium,
                            modifier = Modifier.container(
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.surface
                            )
                        )
                    }
                }
            }
        },
        shape = RoundedCornerShape(cornerSize)
    )
}