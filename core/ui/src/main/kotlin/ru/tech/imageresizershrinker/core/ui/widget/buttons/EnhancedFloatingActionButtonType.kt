package ru.tech.imageresizershrinker.core.ui.widget.buttons

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

sealed class EnhancedFloatingActionButtonType(val size: Dp, val shape: Shape) {
    data object Small : EnhancedFloatingActionButtonType(
        size = 40.dp,
        shape = RoundedCornerShape(12.dp)
    )

    data object Primary : EnhancedFloatingActionButtonType(
        size = 56.dp,
        shape = RoundedCornerShape(16.dp)
    )

    data object Large : EnhancedFloatingActionButtonType(
        size = 96.dp,
        shape = RoundedCornerShape(28.dp)
    )

    class Custom(
        size: Dp,
        shape: Shape
    ) : EnhancedFloatingActionButtonType(
        size = size,
        shape = shape
    )
}