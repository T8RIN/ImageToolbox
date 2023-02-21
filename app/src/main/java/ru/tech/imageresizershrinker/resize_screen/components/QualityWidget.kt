package ru.tech.imageresizershrinker.resize_screen.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Slider
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
import ru.tech.imageresizershrinker.resize_screen.block

@Composable
fun QualityWidget(
    visible: Boolean,
    enabled: Boolean,
    quality: Float,
    onQualityChange: (Float) -> Unit
) {
    val sliderHeight = animateDpAsState(
        targetValue = if (visible) 40.dp else 0.dp
    ).value

    val alpha = animateFloatAsState(
        targetValue = if (visible) 1f else 0f
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
                .height(sliderHeight * 2)
                .alpha(alpha)
                .block(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.quality),
                modifier = Modifier.alpha(alpha)
            )
            Slider(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .height(sliderHeight)
                    .alpha(alpha),
                enabled = enabled,
                value = quality,
                onValueChange = onQualityChange,
                valueRange = 0f..100f,
                steps = 100
            )
        }
    }
}