package ru.tech.imageresizershrinker.coredomain.image.draw

sealed class DrawBehavior(
    open val orientation: Int
) {
    data object None : DrawBehavior(2)

    data class Image(
        override val orientation: Int
    ) : DrawBehavior(orientation = orientation)

    data class Background(
        override val orientation: Int,
        val width: Int,
        val height: Int,
        val color: Int
    ) : DrawBehavior(orientation = orientation)
}