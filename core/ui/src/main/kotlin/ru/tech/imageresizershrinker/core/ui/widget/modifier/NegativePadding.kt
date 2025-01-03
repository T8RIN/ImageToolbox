package ru.tech.imageresizershrinker.core.ui.widget.modifier

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset

fun Modifier.negativePadding(
    paddingValues: PaddingValues
): Modifier = this.layout { measurable, constraints ->
    val horizontal = paddingValues.calculateLeftPadding(layoutDirection).roundToPx() +
            paddingValues.calculateRightPadding(layoutDirection).roundToPx()
    val vertical = paddingValues.calculateTopPadding().roundToPx() +
            paddingValues.calculateBottomPadding().roundToPx()

    val placeable = measurable.measure(constraints.offset(horizontal, vertical))

    layout(
        width = placeable.measuredWidth,
        height = placeable.measuredHeight
    ) {
        placeable.place(0, 0)
    }
}

fun Modifier.negativePadding(
    all: Dp
): Modifier = negativePadding(
    PaddingValues(all)
)

fun Modifier.negativePadding(
    horizontal: Dp = 0.dp,
    vertical: Dp = 0.dp
) = negativePadding(
    PaddingValues(
        horizontal = horizontal,
        vertical = vertical
    )
)

fun Modifier.negativePadding(
    start: Dp = 0.dp,
    top: Dp = 0.dp,
    end: Dp = 0.dp,
    bottom: Dp = 0.dp
) = negativePadding(
    PaddingValues(
        start = start,
        top = top,
        end = end,
        bottom = bottom
    )
)