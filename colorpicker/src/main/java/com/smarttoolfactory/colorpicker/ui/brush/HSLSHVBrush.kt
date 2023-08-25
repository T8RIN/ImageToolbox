package com.smarttoolfactory.colorpicker.ui.brush

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/*
    HSV Gradients
 */
/**
 * Gradient for creating HSV saturation change with 0 degree rotation by default.
 */
fun saturationHSVGradient(
    hue: Float,
    value: Float = 1f,
    alpha: Float = 1f,
    start: Offset = Offset.Zero,
    end: Offset = Offset(Float.POSITIVE_INFINITY, 0f)
): Brush {
    return Brush.linearGradient(
        colors = listOf(
            Color.hsv(hue = hue, saturation = 0f, value = value, alpha = alpha),
            Color.hsv(hue = hue, saturation = 1f, value = value, alpha = alpha)
        ),
        start = start,
        end = end
    )
}

/**
 * Vertical gradient that goes from 1 value to 0 with 90 degree rotation by default.
 */
fun valueGradient(
    hue: Float,
    alpha: Float = 1f,
    start: Offset = Offset.Zero,
    end: Offset = Offset(0f, Float.POSITIVE_INFINITY)
): Brush {
    return Brush.linearGradient(
        colors = listOf(
            Color.hsv(hue = hue, saturation = 0f, value = 1f, alpha = alpha),
            Color.hsv(hue = hue, saturation = 0f, value = 0f, alpha = alpha)
        ),
        start = start,
        end = end
    )
}

/*
    HSL Gradients
 */
fun saturationHSLGradient(
    hue: Float,
    lightness: Float = .5f,
    alpha: Float = 1f,
    start: Offset = Offset.Zero,
    end: Offset = Offset(Float.POSITIVE_INFINITY, 0f)
): Brush {
    return Brush.linearGradient(
        colors = listOf(
            Color.hsl(hue = hue, saturation = 0f, lightness = lightness, alpha = alpha),
            Color.hsl(hue = hue, saturation = 1f, lightness = lightness, alpha = alpha),
        ),
        start = start,
        end = end
    )
}

fun lightnessGradient(
    hue: Float,
    alpha: Float = 1f,
    start: Offset = Offset.Zero,
    end: Offset = Offset(0f, Float.POSITIVE_INFINITY)
): Brush {
    return Brush.linearGradient(
        colors = listOf(
            Color.hsl(hue = hue, saturation = .5f, lightness = 1f, alpha = alpha),
            Color.hsl(hue = hue, saturation = .5f, lightness = 0f, alpha = alpha)
        ),
        start = start,
        end = end
    )
}
