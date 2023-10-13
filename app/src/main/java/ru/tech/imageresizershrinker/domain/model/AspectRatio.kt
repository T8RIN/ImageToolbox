package ru.tech.imageresizershrinker.domain.model

sealed class AspectRatio(
    open val widthProportion: Float,
    open val heightProportion: Float
) {
    val value: Float get() = widthProportion / heightProportion

    data class Numeric(
        override val widthProportion: Float,
        override val heightProportion: Float
    ) : AspectRatio(widthProportion = widthProportion, heightProportion = heightProportion)

    data object Original : AspectRatio(widthProportion = -1f, heightProportion = 1f)

    companion object {
        val defaultList: List<AspectRatio> by lazy {
            listOf(
                Original,
                Numeric(widthProportion = 1f, heightProportion = 1f),
                Numeric(widthProportion = 10f, heightProportion = 16f),
                Numeric(widthProportion = 9f, heightProportion = 16f),
                Numeric(widthProportion = 9f, heightProportion = 18.5f),
                Numeric(widthProportion = 9f, heightProportion = 21f),
                Numeric(widthProportion = 1f, heightProportion = 1.91f),
                Numeric(widthProportion = 2f, heightProportion = 3f),
                Numeric(widthProportion = 1f, heightProportion = 2f),
                Numeric(widthProportion = 5f, heightProportion = 3f),
                Numeric(widthProportion = 4f, heightProportion = 3f),
                Numeric(widthProportion = 21f, heightProportion = 9f),
                Numeric(widthProportion = 18.5f, heightProportion = 9f),
                Numeric(widthProportion = 16f, heightProportion = 9f),
                Numeric(widthProportion = 16f, heightProportion = 10f),
                Numeric(widthProportion = 1.91f, heightProportion = 1f),
                Numeric(widthProportion = 3f, heightProportion = 2f),
                Numeric(widthProportion = 3f, heightProportion = 4f),
                Numeric(widthProportion = 3f, heightProportion = 5f),
                Numeric(widthProportion = 2f, heightProportion = 1f)
            )
        }
    }
}