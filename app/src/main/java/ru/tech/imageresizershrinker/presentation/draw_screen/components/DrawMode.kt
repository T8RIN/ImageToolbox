package ru.tech.imageresizershrinker.presentation.draw_screen.components

sealed class DrawMode(open val ordinal: Int) {
    data object Neon : DrawMode(2)
    data object Highlighter : DrawMode(3)
    data object Pen : DrawMode(0)

    sealed class PathEffect(override val ordinal: Int) : DrawMode(ordinal) {
        data class PrivacyBlur(
            val blurRadius: Int = 50
        ) : PathEffect(1)

        data class Pixelation(
            val pixelSize: Float = 35f
        ) : PathEffect(4)
    }

    companion object {
        val entries by lazy {
            listOf(
                Pen, PathEffect.PrivacyBlur(), Neon, Highlighter, PathEffect.Pixelation()
            )
        }

        operator fun invoke(ordinal: Int) = when (ordinal) {
            0 -> Pen
            1 -> PathEffect.PrivacyBlur()
            2 -> Neon
            3 -> Highlighter
            4 -> PathEffect.Pixelation()
            else -> Pen
        }
    }
}