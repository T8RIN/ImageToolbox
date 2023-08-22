package ru.tech.imageresizershrinker.presentation.erase_background_screen.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

data class PathPaint(
    val path: Path,
    val strokeWidth: Float,
    val blurRadius: Float,
    val isErasing: Boolean,
    val drawColor: Color? = null
)