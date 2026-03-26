package com.smarttoolfactory.cropper

/**
 * Enum for detecting which section of Composable user has initially touched
 */
enum class TouchRegion {
    TopLeft, TopRight, BottomLeft, BottomRight,
    TopCenter, CenterRight, BottomCenter, CenterLeft,
    Inside, None
}

fun handlesTouched(touchRegion: TouchRegion) =
    touchRegion != TouchRegion.None && touchRegion != TouchRegion.Inside
