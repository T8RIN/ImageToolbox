package ru.tech.imageresizershrinker.core.ui.widget.modifier

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.onSwipeLeft(onSwipe: () -> Unit): Modifier {
    var dx = 0F

    return this.pointerInput(Unit) {
        detectHorizontalDragGestures(
            onDragEnd = {
                if (dx < 0) {
                    dx = 0F
                    onSwipe()
                }
            },
            onHorizontalDrag = { _, dragAmount ->
                dx = dragAmount
            }
        )
    }
}

fun Modifier.onSwipeRight(onSwipe: () -> Unit): Modifier {
    var dx = 0F

    return this.pointerInput(Unit) {
        detectHorizontalDragGestures(
            onDragEnd = {
                if (dx > 0) {
                    dx = 0F
                    onSwipe()
                }
            },
            onHorizontalDrag = { _, dragAmount ->
                dx = dragAmount
            }
        )
    }
}

fun Modifier.detectSwipes(
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit
): Modifier {
    var dx = 0F

    return this.pointerInput(Unit) {
        detectHorizontalDragGestures(
            onDragEnd = {
                if (dx > 0) {
                    onSwipeRight()
                } else {
                    onSwipeLeft()
                }
                dx = 0F
            },
            onHorizontalDrag = { _, dragAmount ->
                dx = dragAmount
            }
        )
    }
}
