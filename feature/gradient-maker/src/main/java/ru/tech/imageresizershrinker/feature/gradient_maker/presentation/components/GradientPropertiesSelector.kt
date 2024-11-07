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

package ru.tech.imageresizershrinker.feature.gradient_maker.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import com.smarttoolfactory.colordetector.util.ColorUtil.roundToTwoDigits
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedSliderItem
import ru.tech.imageresizershrinker.feature.gradient_maker.domain.GradientType
import kotlin.math.roundToInt

@Composable
fun GradientPropertiesSelector(
    gradientType: GradientType,
    linearAngle: Float,
    centerFriction: Offset,
    radiusFriction: Float,
    onLinearAngleChange: (Float) -> Unit,
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
                    value = linearAngle,
                    title = stringResource(id = R.string.angle),
                    valueRange = 0f..360f,
                    internalStateTransformation = { it.roundToInt() },
                    onValueChange = {
                        onLinearAngleChange(it.roundToInt().toFloat())
                    }
                )
            }

            GradientType.Radial,
            GradientType.Sweep -> {
                var centerX by remember { mutableFloatStateOf(centerFriction.x) }
                var centerY by remember { mutableFloatStateOf(centerFriction.y) }
                var radius by remember { mutableFloatStateOf(radiusFriction) }

                onRadialDimensionsChange(Offset(centerX, centerY), radius)

                Column {
                    EnhancedSliderItem(
                        value = centerX,
                        title = stringResource(id = R.string.center_x),
                        internalStateTransformation = {
                            it.roundToTwoDigits()
                        },
                        onValueChange = {
                            centerX = it
                        },
                        valueRange = 0f..1f,
                        behaveAsContainer = false
                    )
                    EnhancedSliderItem(
                        value = centerY,
                        title = stringResource(id = R.string.center_y),
                        internalStateTransformation = {
                            it.roundToTwoDigits()
                        },
                        onValueChange = {
                            centerY = it
                        },
                        valueRange = 0f..1f,
                        behaveAsContainer = false
                    )
                    AnimatedVisibility(
                        visible = type != GradientType.Sweep
                    ) {
                        EnhancedSliderItem(
                            value = radius,
                            title = stringResource(id = R.string.radius),
                            internalStateTransformation = {
                                it.roundToTwoDigits()
                            },
                            onValueChange = {
                                radius = it
                            },
                            valueRange = 0f..1f,
                            behaveAsContainer = false
                        )
                    }
                }
            }
        }
    }
}