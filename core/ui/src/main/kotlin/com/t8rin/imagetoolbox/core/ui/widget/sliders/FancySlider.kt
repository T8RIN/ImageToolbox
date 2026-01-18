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

package com.t8rin.imagetoolbox.core.ui.widget.sliders

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.outlineVariant
import com.t8rin.imagetoolbox.core.ui.utils.animation.animateFloatingRangeAsState
import com.t8rin.imagetoolbox.core.ui.utils.helper.ProvidesValue
import com.t8rin.imagetoolbox.core.ui.utils.helper.rememberRipple
import com.t8rin.imagetoolbox.core.ui.utils.provider.SafeLocalContainerColor
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.materialShadow
import com.t8rin.imagetoolbox.core.ui.widget.sliders.custom_slider.CustomRangeSlider
import com.t8rin.imagetoolbox.core.ui.widget.sliders.custom_slider.CustomSlider
import com.t8rin.imagetoolbox.core.ui.widget.sliders.custom_slider.CustomSliderColors
import com.t8rin.imagetoolbox.core.ui.widget.sliders.custom_slider.CustomSliderDefaults
import com.t8rin.imagetoolbox.core.ui.widget.sliders.custom_slider.CustomSliderState

@Composable
fun FancySlider(
    value: Float,
    enabled: Boolean,
    colors: SliderColors,
    interactionSource: MutableInteractionSource,
    thumbShape: Shape,
    modifier: Modifier,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: (() -> Unit)?,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    drawContainer: Boolean = true
) {
    val thumbColor by animateColorAsState(
        if (enabled) colors.thumbColor else colors.disabledThumbColor
    )

    val thumb: @Composable (CustomSliderState) -> Unit = { sliderState ->
        val sliderFraction by remember {
            derivedStateOf {
                (value - sliderState.valueRange.start) / (sliderState.valueRange.endInclusive - sliderState.valueRange.start)
            }
        }
        Spacer(
            Modifier
                .zIndex(100f)
                .rotate(1080f * sliderFraction)
                .size(26.dp)
                .indication(
                    interactionSource = interactionSource,
                    indication = rememberRipple(
                        bounded = false,
                        radius = 24.dp
                    )
                )
                .hoverable(interactionSource = interactionSource)
                .materialShadow(
                    shape = thumbShape,
                    elevation = 1.dp,
                    enabled = LocalSettingsState.current.drawSliderShadows
                )
                .background(thumbColor, thumbShape)
        )
    }

    val settingsState = LocalSettingsState.current
    LocalMinimumInteractiveComponentSize.ProvidesValue(Dp.Unspecified) {
        CustomSlider(
            interactionSource = interactionSource,
            thumb = thumb,
            enabled = enabled,
            modifier = modifier
                .then(
                    if (drawContainer) {
                        Modifier
                            .container(
                                shape = CircleShape,
                                autoShadowElevation = animateDpAsState(
                                    if (settingsState.drawSliderShadows) {
                                        1.dp
                                    } else 0.dp
                                ).value,
                                resultPadding = 0.dp,
                                borderColor = MaterialTheme.colorScheme
                                    .outlineVariant(
                                        luminance = 0.1f,
                                        onTopOf = SwitchDefaults.colors().disabledCheckedTrackColor
                                    )
                                    .copy(0.3f),
                                color = SafeLocalContainerColor
                                    .copy(0.5f)
                                    .compositeOver(MaterialTheme.colorScheme.surface)
                                    .copy(colors.activeTrackColor.alpha),
                                composeColorOnTopOfBackground = false
                            )
                            .padding(horizontal = 6.dp)
                    } else Modifier
                ),
            colors = colors.toCustom(),
            value = value,
            onValueChange = onValueChange,
            onValueChangeFinished = onValueChangeFinished,
            valueRange = valueRange,
            steps = steps,
            track = { sliderState ->
                CustomSliderDefaults.Track(
                    sliderState = sliderState,
                    colors = colors.toCustom(),
                    trackHeight = 38.dp,
                    enabled = enabled
                )
            }
        )
    }
}

@Composable
fun FancyRangeSlider(
    value: ClosedFloatingPointRange<Float>,
    enabled: Boolean,
    colors: SliderColors,
    startInteractionSource: MutableInteractionSource,
    endInteractionSource: MutableInteractionSource,
    thumbShape: Shape,
    modifier: Modifier,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    onValueChangeFinished: (() -> Unit)?,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    drawContainer: Boolean = true
) {
    val thumbColor by animateColorAsState(
        if (enabled) colors.thumbColor else colors.disabledThumbColor
    )


    val settingsState = LocalSettingsState.current
    LocalMinimumInteractiveComponentSize.ProvidesValue(Dp.Unspecified) {
        CustomRangeSlider(
            startInteractionSource = startInteractionSource,
            endInteractionSource = endInteractionSource,
            enabled = enabled,
            modifier = modifier
                .then(
                    if (drawContainer) {
                        Modifier
                            .container(
                                shape = CircleShape,
                                autoShadowElevation = animateDpAsState(
                                    if (settingsState.drawSliderShadows) {
                                        1.dp
                                    } else 0.dp
                                ).value,
                                resultPadding = 0.dp,
                                borderColor = MaterialTheme.colorScheme
                                    .outlineVariant(
                                        luminance = 0.1f,
                                        onTopOf = SwitchDefaults.colors().disabledCheckedTrackColor
                                    )
                                    .copy(0.3f),
                                color = SafeLocalContainerColor
                                    .copy(0.5f)
                                    .compositeOver(MaterialTheme.colorScheme.surface)
                                    .copy(colors.activeTrackColor.alpha),
                                composeColorOnTopOfBackground = false
                            )
                            .padding(horizontal = 6.dp)
                    } else Modifier
                ),
            colors = colors.toCustom(),
            value = animateFloatingRangeAsState(value).value,
            onValueChange = onValueChange,
            onValueChangeFinished = onValueChangeFinished,
            valueRange = valueRange,
            startThumb = {
                Spacer(
                    Modifier
                        .zIndex(100f)
                        .size(26.dp)
                        .indication(
                            interactionSource = startInteractionSource,
                            indication = rememberRipple(
                                bounded = false,
                                radius = 24.dp
                            )
                        )
                        .hoverable(interactionSource = startInteractionSource)
                        .materialShadow(
                            shape = thumbShape,
                            elevation = 1.dp,
                            enabled = LocalSettingsState.current.drawSliderShadows
                        )
                        .background(thumbColor, thumbShape)
                )
            },
            endThumb = {
                Spacer(
                    Modifier
                        .zIndex(100f)
                        .size(26.dp)
                        .indication(
                            interactionSource = endInteractionSource,
                            indication = rememberRipple(
                                bounded = false,
                                radius = 24.dp
                            )
                        )
                        .hoverable(interactionSource = endInteractionSource)
                        .materialShadow(
                            shape = thumbShape,
                            elevation = 1.dp,
                            enabled = LocalSettingsState.current.drawSliderShadows
                        )
                        .background(thumbColor, thumbShape)
                )
            },
            steps = steps,
            track = { sliderState ->
                CustomSliderDefaults.Track(
                    rangeSliderState = sliderState,
                    colors = colors.toCustom(),
                    trackHeight = 38.dp,
                    enabled = enabled
                )
            }
        )
    }
}

@Stable
internal fun SliderColors.toCustom(): CustomSliderColors = CustomSliderColors(
    thumbColor = thumbColor,
    activeTrackColor = activeTrackColor,
    activeTickColor = activeTickColor,
    inactiveTrackColor = inactiveTrackColor,
    inactiveTickColor = inactiveTickColor,
    disabledThumbColor = disabledThumbColor,
    disabledActiveTrackColor = disabledActiveTrackColor,
    disabledActiveTickColor = disabledActiveTickColor,
    disabledInactiveTrackColor = disabledInactiveTrackColor,
    disabledInactiveTickColor = disabledInactiveTickColor
)