package ru.tech.imageresizershrinker.core.ui.utils.provider

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCoerceIn
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint

val LocalHazeState = compositionLocalOf<HazeState> { error("HazeState not provided") }

val HazeState.maxZIndex: Float
    @Composable
    get() = remember(areas) {
        areas.maxOfOrNull { it.zIndex } ?: 0f
    }

@Composable
fun hazeMaterial(
    containerColor: Color = MaterialTheme.colorScheme.surface,
    lightAlpha: Float = 0.35f,
    darkAlpha: Float = 0.55f,
    fraction: Float = 1f
): HazeStyle {
    val fractionAnimated by animateFloatAsState(fraction.fastCoerceIn(0f, 1f))

    return HazeStyle(
        blurRadius = 8.dp * fractionAnimated,
        backgroundColor = containerColor,
        tint = HazeTint(
            containerColor.copy(
                alpha = if (containerColor.luminance() >= 0.5) {
                    lightAlpha
                } else {
                    darkAlpha
                } * fractionAnimated
            ),
        ),
    )
}