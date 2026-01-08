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

package com.t8rin.imagetoolbox.core.ui.widget.enhanced

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import com.t8rin.imagetoolbox.core.resources.shapes.MaterialStarShape
import com.t8rin.imagetoolbox.core.settings.domain.model.SliderType
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.widget.sliders.FancyRangeSlider
import com.t8rin.imagetoolbox.core.ui.widget.sliders.FancySlider
import com.t8rin.imagetoolbox.core.ui.widget.sliders.HyperOSRangeSlider
import com.t8rin.imagetoolbox.core.ui.widget.sliders.HyperOSSlider
import com.t8rin.imagetoolbox.core.ui.widget.sliders.M2RangeSlider
import com.t8rin.imagetoolbox.core.ui.widget.sliders.M2Slider
import com.t8rin.imagetoolbox.core.ui.widget.sliders.M3RangeSlider
import com.t8rin.imagetoolbox.core.ui.widget.sliders.M3Slider

@Composable
fun EnhancedSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    onValueChangeFinished: (() -> Unit)? = null,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int = 0,
    enabled: Boolean = true,
    colors: SliderColors? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    drawContainer: Boolean = true,
    isAnimated: Boolean = true
) {
    val settingsState = LocalSettingsState.current
    val sliderType = settingsState.sliderType

    val realColors = colors ?: when (sliderType) {
        SliderType.Fancy -> {
            SliderDefaults.colors(
                activeTickColor = MaterialTheme.colorScheme.inverseSurface,
                inactiveTickColor = MaterialTheme.colorScheme.surface,
                activeTrackColor = MaterialTheme.colorScheme.primaryContainer,
                inactiveTrackColor = SwitchDefaults.colors().disabledCheckedTrackColor,
                disabledThumbColor = SliderDefaults.colors().disabledThumbColor,
                disabledActiveTrackColor = SliderDefaults.colors().disabledActiveTrackColor,
                thumbColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        SliderType.MaterialYou -> SliderDefaults.colors()
        SliderType.Material -> SliderDefaults.colors()
        SliderType.HyperOS -> SliderDefaults.colors()
    }

    if (steps != 0) {
        var compositions by remember {
            mutableIntStateOf(0)
        }
        val haptics = LocalHapticFeedback.current
        val updatedValue by rememberUpdatedState(newValue = value)

        LaunchedEffect(updatedValue) {
            if (compositions > 0) haptics.press()

            compositions++
        }
    }

    val value = if (isAnimated) {
        animateFloatAsState(
            targetValue = value,
            animationSpec = tween(100)
        ).value
    } else {
        value
    }

    when (sliderType) {
        SliderType.Fancy -> {
            FancySlider(
                value = value,
                enabled = enabled,
                colors = realColors,
                interactionSource = interactionSource,
                thumbShape = MaterialStarShape,
                modifier = modifier,
                onValueChange = onValueChange,
                onValueChangeFinished = onValueChangeFinished,
                valueRange = valueRange,
                steps = steps,
                drawContainer = drawContainer
            )
        }

        SliderType.MaterialYou -> {
            M3Slider(
                value = value,
                enabled = enabled,
                colors = realColors,
                interactionSource = interactionSource,
                modifier = modifier,
                onValueChange = onValueChange,
                onValueChangeFinished = onValueChangeFinished,
                valueRange = valueRange,
                steps = steps,
                drawContainer = drawContainer
            )
        }

        SliderType.Material -> {
            M2Slider(
                value = value,
                enabled = enabled,
                colors = realColors,
                interactionSource = interactionSource,
                modifier = modifier,
                onValueChange = onValueChange,
                onValueChangeFinished = onValueChangeFinished,
                valueRange = valueRange,
                steps = steps,
                drawContainer = drawContainer
            )
        }

        SliderType.HyperOS -> {
            HyperOSSlider(
                value = value,
                enabled = enabled,
                colors = realColors,
                interactionSource = interactionSource,
                modifier = modifier,
                onValueChange = onValueChange,
                onValueChangeFinished = onValueChangeFinished,
                valueRange = valueRange,
                steps = steps,
                drawContainer = drawContainer
            )
        }
    }
}

@Composable
fun EnhancedRangeSlider(
    value: ClosedFloatingPointRange<Float>,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    modifier: Modifier = Modifier,
    onValueChangeFinished: (() -> Unit)? = null,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int = 0,
    enabled: Boolean = true,
    colors: SliderColors? = null,
    startInteractionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    endInteractionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    drawContainer: Boolean = true
) {
    val settingsState = LocalSettingsState.current
    val sliderType = settingsState.sliderType

    val realColors = colors ?: when (sliderType) {
        SliderType.Fancy -> {
            SliderDefaults.colors(
                activeTickColor = MaterialTheme.colorScheme.inverseSurface,
                inactiveTickColor = MaterialTheme.colorScheme.surface,
                activeTrackColor = MaterialTheme.colorScheme.primaryContainer,
                inactiveTrackColor = SwitchDefaults.colors().disabledCheckedTrackColor,
                disabledThumbColor = SliderDefaults.colors().disabledThumbColor,
                disabledActiveTrackColor = SliderDefaults.colors().disabledActiveTrackColor,
                thumbColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        SliderType.MaterialYou -> SliderDefaults.colors()
        SliderType.Material -> SliderDefaults.colors()
        SliderType.HyperOS -> SliderDefaults.colors()
    }

    if (steps != 0) {
        var compositions by remember {
            mutableIntStateOf(0)
        }
        val haptics = LocalHapticFeedback.current
        val updatedValue by rememberUpdatedState(newValue = value)

        LaunchedEffect(updatedValue) {
            if (compositions > 0) haptics.press()

            compositions++
        }
    }

    when (sliderType) {
        SliderType.Fancy -> {
            FancyRangeSlider(
                value = value,
                enabled = enabled,
                colors = realColors,
                startInteractionSource = startInteractionSource,
                endInteractionSource = endInteractionSource,
                thumbShape = MaterialStarShape,
                modifier = modifier,
                onValueChange = onValueChange,
                onValueChangeFinished = onValueChangeFinished,
                valueRange = valueRange,
                steps = steps,
                drawContainer = drawContainer
            )
        }

        SliderType.MaterialYou -> {
            M3RangeSlider(
                value = value,
                enabled = enabled,
                colors = realColors,
                startInteractionSource = startInteractionSource,
                endInteractionSource = endInteractionSource,
                modifier = modifier,
                onValueChange = onValueChange,
                onValueChangeFinished = onValueChangeFinished,
                valueRange = valueRange,
                steps = steps,
                drawContainer = drawContainer
            )
        }

        SliderType.Material -> {
            M2RangeSlider(
                value = value,
                enabled = enabled,
                colors = realColors,
                startInteractionSource = startInteractionSource,
                endInteractionSource = endInteractionSource,
                modifier = modifier,
                onValueChange = onValueChange,
                onValueChangeFinished = onValueChangeFinished,
                valueRange = valueRange,
                steps = steps,
                drawContainer = drawContainer
            )
        }

        SliderType.HyperOS -> {
            HyperOSRangeSlider(
                value = value,
                enabled = enabled,
                colors = realColors,
                startInteractionSource = startInteractionSource,
                endInteractionSource = endInteractionSource,
                modifier = modifier,
                onValueChange = onValueChange,
                onValueChangeFinished = onValueChangeFinished,
                valueRange = valueRange,
                steps = steps,
                drawContainer = drawContainer
            )
        }
    }
}