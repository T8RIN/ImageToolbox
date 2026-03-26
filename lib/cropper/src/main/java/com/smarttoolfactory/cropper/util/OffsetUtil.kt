package com.smarttoolfactory.cropper.util

import androidx.compose.ui.geometry.Offset


/**
 * Coerce an [Offset] x value in [horizontalRange] and y value in [verticalRange]
 */
fun Offset.coerceIn(
    horizontalRange: ClosedRange<Float>,
    verticalRange: ClosedRange<Float>
) = Offset(this.x.coerceIn(horizontalRange), this.y.coerceIn(verticalRange))