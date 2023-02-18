package com.smarttoolfactory.cropper.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect


/**
 * Class that contains information about
 * current zoom, pan and rotation, and rectangle of zoomed and panned area for cropping [cropRect],
 * and area of overlay as[overlayRect]
 *
 */
@Immutable
data class CropData(
    val zoom: Float = 1f,
    val pan: Offset = Offset.Zero,
    val rotation: Float = 0f,
    val overlayRect: Rect,
    val cropRect: Rect
)
