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

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.blend
import com.t8rin.imagetoolbox.core.ui.theme.outlineVariant
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.utils.provider.SafeLocalContainerColor
import com.t8rin.imagetoolbox.core.ui.widget.modifier.AutoCircleShape
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.sliders.custom_slider.CustomRangeSlider
import com.t8rin.imagetoolbox.core.ui.widget.sliders.custom_slider.CustomSlider
import com.t8rin.imagetoolbox.core.ui.widget.sliders.custom_slider.CustomSliderDefaults
import androidx.compose.material3.SliderColors as M3SliderColors
import androidx.compose.material3.SliderDefaults as M3Defaults

@Composable
fun HyperOSSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    onValueChangeFinished: (() -> Unit)? = null,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    colors: M3SliderColors = M3Defaults.colors(),
    steps: Int = 0,
    drawContainer: Boolean = true
) {
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    val shape = AutoCircleShape()
    val settingsState = LocalSettingsState.current
    val sliderColors = colors.toCustom()

    CustomSlider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        onValueChangeFinished = onValueChangeFinished,
        colors = sliderColors,
        interactionSource = interactionSource,
        steps = steps,
        thumb = {},
        track = { sliderState ->
            CustomSliderDefaults.Track(
                colors = sliderColors,
                enabled = enabled,
                sliderState = sliderState,
                trackHeight = 30.dp,
                modifier = Modifier.then(
                    if (drawContainer) {
                        Modifier.container(
                            shape = shape,
                            autoShadowElevation = animateDpAsState(
                                if (settingsState.drawSliderShadows) 1.dp else 0.dp
                            ).value,
                            resultPadding = 0.dp,
                            borderColor = MaterialTheme.colorScheme.outlineVariant(
                                luminance = 0.1f,
                                onTopOf = SwitchDefaults.colors().disabledCheckedTrackColor
                            ).copy(0.3f),
                            color = SafeLocalContainerColor
                                .copy(0.3f)
                                .compositeOver(
                                    takeColorFromScheme {
                                        if (it) tertiaryContainer
                                            .blend(secondaryContainer, 0.5f)
                                            .copy(0.1f)
                                        else secondaryContainer
                                            .blend(tertiaryContainer, 0.3f)
                                            .copy(0.2f)
                                    }
                                )
                                .copy(sliderColors.activeTrackColor.alpha),
                            composeColorOnTopOfBackground = false
                        )
                    } else Modifier
                ),
                strokeCap = StrokeCap.Butt
            )
        },
        valueRange = valueRange
    )
}

@Composable
fun HyperOSRangeSlider(
    value: ClosedFloatingPointRange<Float>,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    startInteractionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    endInteractionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onValueChangeFinished: (() -> Unit)? = null,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    colors: M3SliderColors = M3Defaults.colors(),
    steps: Int = 0,
    drawContainer: Boolean = true,
) {
    val shape = AutoCircleShape()
    val settingsState = LocalSettingsState.current
    val sliderColors = colors.toCustom()

    CustomRangeSlider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        onValueChangeFinished = onValueChangeFinished,
        colors = sliderColors,
        startInteractionSource = startInteractionSource,
        endInteractionSource = endInteractionSource,
        steps = steps,
        startThumb = {},
        endThumb = {},
        track = { rangeSliderState ->
            CustomSliderDefaults.Track(
                colors = sliderColors,
                enabled = enabled,
                rangeSliderState = rangeSliderState,
                trackHeight = 30.dp,
                modifier = Modifier.then(
                    if (drawContainer) {
                        Modifier.container(
                            shape = shape,
                            autoShadowElevation = animateDpAsState(
                                if (settingsState.drawSliderShadows) 1.dp else 0.dp
                            ).value,
                            resultPadding = 0.dp,
                            borderColor = MaterialTheme.colorScheme.outlineVariant(
                                luminance = 0.1f,
                                onTopOf = SwitchDefaults.colors().disabledCheckedTrackColor
                            ).copy(0.3f),
                            color = SafeLocalContainerColor
                                .copy(0.3f)
                                .compositeOver(
                                    takeColorFromScheme {
                                        if (it) tertiaryContainer
                                            .blend(secondaryContainer, 0.5f)
                                            .copy(0.1f)
                                        else secondaryContainer
                                            .blend(tertiaryContainer, 0.3f)
                                            .copy(0.2f)
                                    }
                                )
                                .copy(sliderColors.activeTrackColor.alpha),
                            composeColorOnTopOfBackground = false
                        )
                    } else Modifier
                ),
                strokeCap = StrokeCap.Butt
            )
        },
        valueRange = valueRange
    )
}