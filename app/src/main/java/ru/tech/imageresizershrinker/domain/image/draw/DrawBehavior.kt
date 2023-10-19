package ru.tech.imageresizershrinker.domain.image.draw

sealed class DrawBehavior(
    open val orientation: Int
) {
    data object None : DrawBehavior(2)

    class Image(
        override val orientation: Int
    ) : DrawBehavior(orientation = orientation)

    class Background(
        override val orientation: Int,
        val width: Int,
        val height: Int,
        val color: Int
    ) : DrawBehavior(orientation = orientation)
}