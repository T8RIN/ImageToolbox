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

package com.t8rin.imagetoolbox.feature.gradient_maker.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import com.t8rin.colors.util.roundToTwoDigits
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.feature.gradient_maker.domain.GradientType
import kotlin.math.roundToInt

@Composable
fun GradientPropertiesSelector(
    gradientType: GradientType,
    angle: Float,
    centerFriction: Offset,
    radiusFriction: Float,
    onAngleChange: (Float) -> Unit,
    onRadialDimensionsChange: (Offset, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = gradientType,
        modifier = modifier
    ) { type ->
        when (type) {
            GradientType.Linear -> {
                EnhancedSliderItem(
                    behaveAsContainer = false,
                    value = angle,
                    title = stringResource(id = R.string.angle),
                    valueRange = 0f..360f,
                    internalStateTransformation = { it.roundToInt() },
                    onValueChange = {
                        onAngleChange(it.roundToInt().toFloat())
                    }
                )
            }

            GradientType.Radial,
            GradientType.Sweep -> {
                Column {
                    EnhancedSliderItem(
                        value = centerFriction.x,
                        title = stringResource(id = R.string.center_x),
                        internalStateTransformation = {
                            it.roundToTwoDigits()
                        },
                        onValueChange = {
                            onRadialDimensionsChange(
                                Offset(it, centerFriction.y), radiusFriction
                            )
                        },
                        valueRange = 0f..1f,
                        behaveAsContainer = false
                    )
                    EnhancedSliderItem(
                        value = centerFriction.y,
                        title = stringResource(id = R.string.center_y),
                        internalStateTransformation = {
                            it.roundToTwoDigits()
                        },
                        onValueChange = {
                            onRadialDimensionsChange(
                                Offset(centerFriction.x, it), radiusFriction
                            )
                        },
                        valueRange = 0f..1f,
                        behaveAsContainer = false
                    )
                    AnimatedVisibility(
                        visible = type == GradientType.Radial,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        EnhancedSliderItem(
                            value = radiusFriction,
                            title = stringResource(id = R.string.radius),
                            internalStateTransformation = {
                                it.roundToTwoDigits()
                            },
                            onValueChange = {
                                onRadialDimensionsChange(centerFriction, it)
                            },
                            valueRange = 0f..1f,
                            behaveAsContainer = false
                        )
                    }
                    AnimatedVisibility(
                        visible = type != GradientType.Radial,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        EnhancedSliderItem(
                            behaveAsContainer = false,
                            value = angle,
                            title = stringResource(id = R.string.angle),
                            valueRange = 0f..360f,
                            internalStateTransformation = { it.roundToInt() },
                            onValueChange = {
                                onAngleChange(it.roundToInt().toFloat())
                            }
                        )
                    }
                }
            }
        }
    }
}