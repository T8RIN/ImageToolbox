package ru.tech.imageresizershrinker.presentation.root.widget.controls

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import ru.tech.imageresizershrinker.presentation.draw_screen.components.materialShadow
import ru.tech.imageresizershrinker.presentation.root.shapes.DavidStarShape
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedSlider(
    modifier: Modifier,
    value: Float,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: (() -> Unit)? = null,
    valueRange: ClosedFloatingPointRange<Float>,
    thumbShape: Shape = DavidStarShape,
    thumbColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    steps: Int = 0,
    enabled: Boolean = true,
    colors: SliderColors = SliderDefaults.colors(
        activeTickColor = MaterialTheme.colorScheme.inverseSurface,
        inactiveTickColor = MaterialTheme.colorScheme.surface,
        activeTrackColor = MaterialTheme.colorScheme.primaryContainer,
        inactiveTrackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.15f),
        thumbColor = thumbColor
    )
) {
    val interactionSource = remember { MutableInteractionSource() }
    val thumb: @Composable (SliderState) -> Unit = {
        val interaction by interactionSource.interactions.collectAsState(initial = null)

        val elevation = if (interaction is PressInteraction.Press) {
            6.dp
        } else {
            1.dp
        }

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
                    elevation = elevation,
                    enabled = LocalSettingsState.current.drawSliderShadows
                )
                .background(thumbColor, thumbShape)
        )
    }

    val settingsState = LocalSettingsState.current
    CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
        Slider(
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
                .padding(horizontal = 6.dp),
            colors = colors,
            value = animateFloatAsState(value).value,
            onValueChange = onValueChange,
            onValueChangeFinished = onValueChangeFinished,
            valueRange = valueRange,
            steps = steps
        )
    }
}