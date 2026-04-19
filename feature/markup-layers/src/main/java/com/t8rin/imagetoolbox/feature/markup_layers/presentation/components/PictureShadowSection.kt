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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.colors.util.roundToTwoDigits
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Shadow
import com.t8rin.imagetoolbox.core.ui.theme.toColor
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ColorRowSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.feature.markup_layers.domain.DropShadow
import com.t8rin.imagetoolbox.feature.markup_layers.domain.LayerType
import com.t8rin.imagetoolbox.feature.markup_layers.domain.withShadow
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.UiMarkupLayer

@Composable
internal fun PictureShadowSection(
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