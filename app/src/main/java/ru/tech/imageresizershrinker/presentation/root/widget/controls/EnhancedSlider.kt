package ru.tech.imageresizershrinker.presentation.root.widget.controls

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.Interaction
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import ru.tech.imageresizershrinker.presentation.draw_screen.components.materialShadow
import ru.tech.imageresizershrinker.presentation.root.shapes.DavidStarShape
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.container

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
        val interactions = remember { mutableStateListOf<Interaction>() }
        LaunchedEffect(interactionSource) {
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> interactions.add(interaction)
                    is PressInteraction.Release -> interactions.remove(interaction.press)
                    is PressInteraction.Cancel -> interactions.remove(interaction.press)
                    is DragInteraction.Start -> interactions.add(interaction)
                    is DragInteraction.Stop -> interactions.remove(interaction.start)
                    is DragInteraction.Cancel -> interactions.remove(interaction.start)
                }
            }
        }

        val elevation = if (interactions.isNotEmpty()) {
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
                .materialShadow(shape = thumbShape, elevation = elevation)
                .background(thumbColor, thumbShape)
        )
    }

    CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
        Slider(
            interactionSource = interactionSource,
            thumb = thumb,
            enabled = enabled,
            modifier = modifier
                .container(
                    shape = CircleShape,
                    resultPadding = 0.dp,
                    color = Color.Transparent,
                    composeColorOnTopOfBackground = false,
                    clip = false
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