package com.smarttoolfactory.image.transform

/**
 * Enum for detecting which section of Composable user has initially touched
 */
enum class TouchRegion {
    // Corners
    TopLeft, TopRight, BottomLeft, BottomRight,

    // Center of each side
    CenterLeft, CenterRight, TopCenter, BottomCenter,

    // Touch is inside or outside of Composable
    Inside, None
}

/**
 * Enum class for placing handles for transform operations.
 * * [HandlePlacement.Corner] places handles
 * top left, top right, bottom left, and bottom right corners
 * * [HandlePlacement.Side] places handles center of left, right, top and bottom sides
 * * [HandlePlacement.Both] places handles both corners and centers of each sides
 */
enum class HandlePlacement {
    Corner, Side, Both
}