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

package ru.tech.imageresizershrinker.core.ui.widget.controls

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.shapes.MaterialStarShape
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.utils.helper.ProvidesValue
import ru.tech.imageresizershrinker.core.ui.utils.helper.rememberRipple
import ru.tech.imageresizershrinker.core.ui.widget.modifier.materialShadow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    onValueChangeFinished: (() -> Unit)? = null,
    valueRange: ClosedFloatingPointRange<Float>,
    thumbShape: Shape = MaterialStarShape,
    steps: Int = 0,
    enabled: Boolean = true,
    colors: SliderColors = SliderDefaults.colors(
        activeTickColor = MaterialTheme.colorScheme.inverseSurface,
        inactiveTickColor = MaterialTheme.colorScheme.surface,
        activeTrackColor = MaterialTheme.colorScheme.primaryContainer,
        inactiveTrackColor = SwitchDefaults.colors().disabledCheckedTrackColor,
        disabledThumbColor = SwitchDefaults.colors().disabledCheckedThumbColor,
        thumbColor = MaterialTheme.colorScheme.onPrimaryContainer
    ),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    if (steps != 0) {
        var compositions by remember {
            mutableIntStateOf(0)
        }
        val haptics = LocalHapticFeedback.current
        val updatedValue by rememberUpdatedState(newValue = value)

        LaunchedEffect(updatedValue) {
            if (compositions > 0) {
                haptics.performHapticFeedback(
                    HapticFeedbackType.TextHandleMove
                )
            }
            compositions++
        }
    }

    val thumbColor by animateColorAsState(
        if (enabled) colors.thumbColor else colors.disabledThumbColor
    )

    val thumb: @Composable (SliderState) -> Unit = {
        Spacer(
            Modifier
                .zIndex(100f)
                .size(20.dp)
                .indication(
                    interactionSource = interactionSource,
                    indication = rememberRipple(
                        bounded = false,
                        radius = 22.dp
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
                .materialShadow(
                    shape = CircleShape,
                    elevation = animateDpAsState(
                        if (settingsState.borderWidth > 0.dp) {
                            0.dp
                        } else 1.dp
                    ).value,
                    enabled = LocalSettingsState.current.drawSliderShadows,
                    isClipped = true
                )
                .border(
                    width = settingsState.borderWidth,
                    color = MaterialTheme.colorScheme.outlineVariant(
                        luminance = 0.1f,
                        onTopOf = colors.inactiveTrackColor
                    ),
                    shape = CircleShape
                )
                .padding(horizontal = 6.dp),
            colors = colors.toCustom(),
            value = animateFloatAsState(value).value,
            onValueChange = onValueChange,
            onValueChangeFinished = onValueChangeFinished,
            valueRange = valueRange,
            steps = steps
        )
    }
}

private fun SliderColors.toCustom(): CustomSliderColors {
    return CustomSliderColors(
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
}
