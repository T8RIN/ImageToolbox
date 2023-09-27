package ru.tech.imageresizershrinker.presentation.draw_screen.components

sealed class DrawMode(val ordinal: Int) {
    data object Neon : DrawMode(2)
    data object Highlighter : DrawMode(3)
    data object Pen : DrawMode(0)

    data object PrivacyBlur : DrawMode(1)

    companion object {
        val entries by lazy {
            listOf(
                Pen, PrivacyBlur, Neon, Highlighter
            )
        }

        operator fun invoke(ordinal: Int) = when (ordinal) {
            0 -> Pen
            1 -> PrivacyBlur
            2 -> Neon
            3 -> Highlighter
            else -> Pen
        }
    }
}