package ru.tech.imageresizershrinker.domain.image.draw

sealed class DrawMode(open val ordinal: Int) {
    data object Neon : DrawMode(2)
    data object Highlighter : DrawMode(3)
    data object Pen : DrawMode(0)

    sealed class PathEffect(override val ordinal: Int) : DrawMode(ordinal) {
        data class PrivacyBlur(
            val blurRadius: Int = 20
        ) : PathEffect(1)

        data class Pixelation(
            val pixelSize: Float = 35f
        ) : PathEffect(4)
    }

    companion object {
        val entries by lazy {
            listOf(
                Pen,
                PathEffect.PrivacyBlur(),
                Neon,
                Highlighter,
                PathEffect.Pixelation()
            )
        }

        operator fun invoke(ordinal: Int) = entries.find {
            it.ordinal == ordinal
        } ?: Pen
    }
}