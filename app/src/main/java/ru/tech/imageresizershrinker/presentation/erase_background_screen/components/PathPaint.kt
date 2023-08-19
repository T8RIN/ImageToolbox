package ru.tech.imageresizershrinker.presentation.erase_background_screen.components

import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path

data class PathPaint(
    val path: Path,
    val paint: Paint,
    val isEraser: Boolean
)