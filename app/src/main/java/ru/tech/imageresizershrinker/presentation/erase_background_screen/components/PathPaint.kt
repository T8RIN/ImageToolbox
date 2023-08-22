package ru.tech.imageresizershrinker.presentation.erase_background_screen.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

data class PathPaint(
    val path: Path,
    val strokeWidth: Float,
    val blurRadius: Float,
    val drawColor: Color = Color.Transparent,
    val isErasing: Boolean,
    val drawMode: DrawMode = DrawMode.Pen
)

sealed class DrawMode(val ordinal: Int) {
    data object Neon : DrawMode(1)
    data object Highlighter : DrawMode(2)
    data object Pen : DrawMode(0)

    companion object {
        val entries by lazy {
            listOf(
                Pen, Neon, Highlighter
            )
        }

        operator fun invoke(ordinal: Int) = when (ordinal) {
            1 -> Neon
            2 -> Highlighter
            else -> Pen
        }
    }
}