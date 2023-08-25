package com.smarttoolfactory.colorpicker.ui.brush

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.smarttoolfactory.colorpicker.ui.Gray
import com.smarttoolfactory.colorpicker.ui.WhiteTransparent


/*
    Vertical Brushes for adding lightness or value to Rectangle Color pickers
 */
fun transparentToBlackVerticalGradient(
    startY: Float = 0.0f,
    endY: Float = Float.POSITIVE_INFINITY
): Brush {
    return Brush.verticalGradient(
        colors = listOf(Color.Transparent, Color.Black),
        startY = startY,
        endY = endY
    )
}

fun transparentToWhiteVerticalGradient(
    startY: Float = 0.0f,
    endY: Float = Float.POSITIVE_INFINITY
): Brush {
    return Brush.verticalGradient(
        colors = listOf(Color.Transparent, Color.White),
        startY = startY,
        endY = endY
    )
}

fun transparentToGrayVerticalGradient(
    startY: Float = 0.0f,
    endY: Float = Float.POSITIVE_INFINITY
): Brush {
    return Brush.verticalGradient(
        colors = listOf(Color.Transparent, Gray),
        startY = startY,
        endY = endY
    )
}

fun whiteToTransparentToBlackVerticalGradient(
    startY: Float = 0.0f,
    endY: Float = Float.POSITIVE_INFINITY
): Brush {
    return Brush.verticalGradient(
        0.0f to Color.White,
        0.5f to WhiteTransparent,
        0.5f to Color.Transparent,
        1f to Color.Black,
        startY = startY,
        endY = endY
    )
}

fun whiteToTransparentToBlackHorizontalGradient(
    startX: Float = 0.0f,
    endX: Float = Float.POSITIVE_INFINITY
): Brush {
    return Brush.horizontalGradient(
        0.0f to Color.White,
        0.5f to WhiteTransparent,
        0.5f to Color.Transparent,
        1f to Color.Black,
        startX = startX,
        endX = endX
    )
}

fun whiteToBlackGradient(
    startY: Float = 0.0f,
    endY: Float = Float.POSITIVE_INFINITY
): Brush {
    return Brush.verticalGradient(
        colors = listOf(Color.White, Color.Black),
        startY = startY,
        endY = endY
    )
}
