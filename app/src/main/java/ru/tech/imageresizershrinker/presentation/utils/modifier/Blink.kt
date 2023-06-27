package ru.tech.imageresizershrinker.presentation.utils.modifier

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isUnspecified

fun Modifier.blink(
    key: Any? = Unit,
    blinkColor: Color = Color.Unspecified,
    iterations: Int = 2,
    enabled: Boolean = true
) = composed {
    val animatable = remember(key) { Animatable(Color.Transparent) }

    val color = if (blinkColor.isUnspecified) {
        MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.4f)
    } else blinkColor

    LaunchedEffect(key) {
        kotlinx.coroutines.delay(500L)
        repeat(iterations) {
            animatable.animateTo(color, animationSpec = tween(durationMillis = 1000))
            animatable.animateTo(Color.Transparent, animationSpec = tween(durationMillis = 1000))
        }
    }

    if (enabled) drawWithContent {
        drawContent()
        drawRect(animatable.value, size = this.size)
    }
    else this
}