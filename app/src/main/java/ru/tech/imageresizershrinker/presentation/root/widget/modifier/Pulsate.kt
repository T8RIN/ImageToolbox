package ru.tech.imageresizershrinker.presentation.root.widget.modifier

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale

fun Modifier.pulsate(
    range: ClosedFloatingPointRange<Float> = 0.95f..1.05f,
    duration: Int = 1000,
    enabled: Boolean = true
) = composed {
    val infiniteTransition = rememberInfiniteTransition()

    val scale by infiniteTransition.animateFloat(
        initialValue = range.start,
        targetValue = range.endInclusive,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = duration),
            repeatMode = RepeatMode.Reverse
        )
    )

    if (enabled) scale(scale)
    else this
}