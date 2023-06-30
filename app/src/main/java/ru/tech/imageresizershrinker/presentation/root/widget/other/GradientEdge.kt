package ru.tech.imageresizershrinker.presentation.root.widget.other

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun GradientEdge(
    modifier: Modifier,
    orientation: Orientation = Orientation.Vertical,
    startColor: Color,
    endColor: Color
) {
    when (orientation) {
        Orientation.Vertical -> {
            Box(
                modifier = modifier
                    .background(
                        brush = Brush.verticalGradient(
                            0f to startColor,
                            0.7f to startColor,
                            1f to endColor
                        )
                    )
            )
        }

        Orientation.Horizontal -> {
            Box(
                modifier = modifier
                    .background(
                        brush = Brush.horizontalGradient(
                            0f to startColor, 0.7f to startColor, 1f to endColor
                        )
                    )
            )
        }
    }
}