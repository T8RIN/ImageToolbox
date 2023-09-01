package ru.tech.imageresizershrinker.presentation.erase_background_screen.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import ru.tech.imageresizershrinker.presentation.draw_screen.components.DrawMode

data class PathPaint(
    val path: Path,
    val strokeWidth: Float,
    val blurRadius: Float,
    val drawColor: Color = Color.Transparent,
    val isErasing: Boolean,
    val drawMode: DrawMode = DrawMode.Pen
)