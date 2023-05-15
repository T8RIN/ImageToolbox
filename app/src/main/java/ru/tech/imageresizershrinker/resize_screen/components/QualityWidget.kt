package ru.tech.imageresizershrinker.resize_screen.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.main_screen.components.LocalBorderWidth
import ru.tech.imageresizershrinker.theme.outlineVariant
import ru.tech.imageresizershrinker.utils.modifier.block

@Composable
fun QualityWidget(
    visible: Boolean,
    enabled: Boolean,
    quality: Float,
    onQualityChange: (Float) -> Unit
) {
    val sliderHeight = animateDpAsState(
        targetValue = if (visible) 44.dp else 0.dp
    ).value

    val alpha = animateFloatAsState(
        targetValue = if (visible) 1f else 0f
    ).value

    val sliderAlpha = animateFloatAsState(
        targetValue = if (visible && enabled) 1f else if (!enabled) 0.5f else 0f
    ).value

    ProvideTextStyle(
        value = TextStyle(
            color = if (!enabled) {
                MaterialTheme.colorScheme.onSurface
                    .copy(alpha = 0.38f)
                    .compositeOver(MaterialTheme.colorScheme.surface)
            } else Color.Unspecified
        )
    ) {
        Column(
            Modifier
                .height(sliderHeight * 2.2f)
                .alpha(alpha)
                .block(shape = RoundedCornerShape(24.dp)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.quality),
                modifier = Modifier.alpha(alpha)
            )
            Spacer(Modifier.weight(1f))
            Slider(
                modifier = Modifier
                    .padding(horizontal = 3.dp, vertical = 3.dp)
                    .height(sliderHeight)
                    .alpha(sliderAlpha)
                    .background(
                        MaterialTheme.colorScheme.secondaryContainer,
                        CircleShape
                    )
                    .border(
                        LocalBorderWidth.current,
                        MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer),
                        CircleShape
                    )
                    .padding(horizontal = 16.dp),
                colors = SliderDefaults.colors(
                    inactiveTrackColor =
                    MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer)
                ),
                enabled = enabled,
                value = animateFloatAsState(quality).value,
                onValueChange = onQualityChange,
                valueRange = 0f..100f,
                steps = 100
            )
        }
    }
}