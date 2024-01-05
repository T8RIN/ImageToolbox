package ru.tech.imageresizershrinker.core.ui.widget.modifier

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.rotate

fun Modifier.rotateAnimation(
    range: IntRange = 0..180,
    duration: Int = 1000,
    enabled: Boolean = true
) = composed {
    val infiniteTransition = rememberInfiniteTransition()

    val rotation by infiniteTransition.animateFloat(
        initialValue = range.first.toFloat(),
        targetValue = range.last.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = duration),
            repeatMode = RepeatMode.Reverse
        )
    )

    if (enabled) rotate(rotation)
    else this
}