package ru.tech.imageresizershrinker.feature.draw.presentation.components.utils

enum class MotionEvent {
    Idle,
    Down,
    Move,
    Up;
}

inline fun MotionEvent.handle(
    onDown: () -> Unit,
    onMove: () -> Unit,
    onUp: () -> Unit
) {
    when (this) {
        MotionEvent.Down -> onDown()
        MotionEvent.Move -> onMove()
        MotionEvent.Up -> onUp()
        else -> Unit
    }
}