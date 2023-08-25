package com.smarttoolfactory.colorpicker.ui

import androidx.compose.ui.graphics.Color

fun gradientColorScaleRGB(alpha: Float = 1f) = listOf(
    Color.Red.copy(alpha = alpha),
    Color.Magenta.copy(alpha = alpha),
    Color.Blue.copy(alpha = alpha),
    Color.Cyan.copy(alpha = alpha),
    Color.Green.copy(alpha = alpha),
    Color.Yellow.copy(alpha = alpha),
    Color.Red.copy(alpha = alpha)
)

fun gradientColorScaleRGBReversed(alpha: Float = 1f) = listOf(
    Color.Red.copy(alpha = alpha),
    Color.Yellow.copy(alpha = alpha),
    Color.Green.copy(alpha = alpha),
    Color.Cyan.copy(alpha = alpha),
    Color.Blue.copy(alpha = alpha),
    Color.Magenta.copy(alpha = alpha),
    Color.Red.copy(alpha = alpha)
)

fun gradientColorScaleHSV(
    saturation: Float = 1f,
    value: Float = 1f,
    alpha: Float = 1f
) = listOf(
    Color.hsv(hue = 0f, saturation = saturation, value = value, alpha = alpha),
    Color.hsv(hue = 60f, saturation = saturation, value = value, alpha = alpha),
    Color.hsv(hue = 120f, saturation = saturation, value = value, alpha = alpha),
    Color.hsv(hue = 180f, saturation = saturation, value = value, alpha = alpha),
    Color.hsv(hue = 240f, saturation = saturation, value = value, alpha = alpha),
    Color.hsv(hue = 300f, saturation = saturation, value = value, alpha = alpha),
    Color.hsv(hue = 360f, saturation = saturation, value = value, alpha = alpha)
)

fun gradientColorScaleHSVReversed(
    saturation: Float = 1f,
    value: Float = 1f,
    alpha: Float = 1f
) = listOf(
    Color.hsv(hue = 360f, saturation = saturation, value = value, alpha = alpha),
    Color.hsv(hue = 300f, saturation = saturation, value = value, alpha = alpha),
    Color.hsv(hue = 240f, saturation = saturation, value = value, alpha = alpha),
    Color.hsv(hue = 180f, saturation = saturation, value = value, alpha = alpha),
    Color.hsv(hue = 120f, saturation = saturation, value = value, alpha = alpha),
    Color.hsv(hue = 60f, saturation = saturation, value = value, alpha = alpha),
    Color.hsv(hue = 0f, saturation = saturation, value = value, alpha = alpha)
)

fun gradientColorScaleHSL(
    saturation: Float = 1f,
    lightness: Float = .5f,
    alpha: Float = 1f
) = listOf(
    Color.hsl(hue = 0f, saturation = saturation, lightness = lightness, alpha = alpha),
    Color.hsl(hue = 60f, saturation = saturation, lightness = lightness, alpha = alpha),
    Color.hsl(hue = 120f, saturation = saturation, lightness = lightness, alpha = alpha),
    Color.hsl(hue = 180f, saturation = saturation, lightness = lightness, alpha = alpha),
    Color.hsl(hue = 240f, saturation = saturation, lightness = lightness, alpha = alpha),
    Color.hsl(hue = 300f, saturation = saturation, lightness = lightness, alpha = alpha),
    Color.hsl(hue = 360f, saturation = saturation, lightness = lightness, alpha = alpha)
)

fun gradientColorScaleHSLReversed(
    saturation: Float = 1f,
    lightness: Float = .5f,
    alpha: Float = 1f
) = listOf(
    Color.hsl(hue = 360f, saturation = saturation, lightness = lightness, alpha = alpha),
    Color.hsl(hue = 300f, saturation = saturation, lightness = lightness, alpha = alpha),
    Color.hsl(hue = 240f, saturation = saturation, lightness = lightness, alpha = alpha),
    Color.hsl(hue = 180f, saturation = saturation, lightness = lightness, alpha = alpha),
    Color.hsl(hue = 120f, saturation = saturation, lightness = lightness, alpha = alpha),
    Color.hsl(hue = 60f, saturation = saturation, lightness = lightness, alpha = alpha),
    Color.hsl(hue = 0f, saturation = saturation, lightness = lightness, alpha = alpha)
)

val gradientColorScaleHSV = listOf(
    Color.hsv(hue = 0f, saturation = 1f, value = 1f),
    Color.hsv(hue = 60f, saturation = 1f, value = 1f),
    Color.hsv(hue = 120f, saturation = 1f, value = 1f),
    Color.hsv(hue = 180f, saturation = 1f, value = 1f),
    Color.hsv(hue = 240f, saturation = 1f, value = 1f),
    Color.hsv(hue = 300f, saturation = 1f, value = 1f),
    Color.hsv(hue = 360f, saturation = 1f, value = 1f)
)

val gradientColorScaleRGB = listOf(
    Color.Red,
    Color.Magenta,
    Color.Blue,
    Color.Cyan,
    Color.Green,
    Color.Yellow,
    Color.Red
)

val gradientColorScaleRGBReversed = listOf(
    Color.Red,
    Color.Yellow,
    Color.Green,
    Color.Cyan,
    Color.Blue,
    Color.Magenta,
    Color.Red
)

val gradientColorScaleHSVReversed = listOf(
    Color.hsv(hue = 360f, saturation = 1f, value = 1f),
    Color.hsv(hue = 300f, saturation = 1f, value = 1f),
    Color.hsv(hue = 240f, saturation = 1f, value = 1f),
    Color.hsv(hue = 180f, saturation = 1f, value = 1f),
    Color.hsv(hue = 120f, saturation = 1f, value = 1f),
    Color.hsv(hue = 60f, saturation = 1f, value = 1f),
    Color.hsv(hue = 0f, saturation = 1f, value = 1f)
)

val gradientColorScaleHSL = listOf(
    Color.hsl(hue = 0f, saturation = 1f, lightness = .5f),
    Color.hsl(hue = 60f, saturation = 1f, lightness = .5f),
    Color.hsl(hue = 120f, saturation = 1f, lightness = .5f),
    Color.hsl(hue = 180f, saturation = 1f, lightness = .5f),
    Color.hsl(hue = 240f, saturation = 1f, lightness = .5f),
    Color.hsl(hue = 300f, saturation = 1f, lightness = .5f),
    Color.hsl(hue = 360f, saturation = 1f, lightness = .5f)

)

val gradientColorScaleHSLReversed = listOf(
    Color.hsl(hue = 360f, saturation = 1f, lightness = .5f),
    Color.hsl(hue = 300f, saturation = 1f, lightness = .5f),
    Color.hsl(hue = 240f, saturation = 1f, lightness = .5f),
    Color.hsl(hue = 180f, saturation = 1f, lightness = .5f),
    Color.hsl(hue = 120f, saturation = 1f, lightness = .5f),
    Color.hsl(hue = 60f, saturation = 1f, lightness = .5f),
    Color.hsl(hue = 0f, saturation = 1f, lightness = .5f)
)