package com.smarttoolfactory.image.zoom

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntRect

/**
 * class that contains current zoom, pan and rotation information
 */
@Immutable
data class ZoomData(
    val zoom: Float = 1f,
    val pan: Offset = Offset.Zero,
    val rotation: Float = 0f
)


/**
 * Class that contains current zoom, pan and rotation, and rectangle of zoomed and panned area
 */
@Immutable
data class EnhancedZoomData(
    val zoom: Float = 1f,
    val pan: Offset = Offset.Zero,
    val rotation: Float = 0f,
    val imageRegion: Rect,
    val visibleRegion: IntRect
)
