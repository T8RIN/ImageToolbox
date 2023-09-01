package ru.tech.imageresizershrinker.presentation.draw_screen.components

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