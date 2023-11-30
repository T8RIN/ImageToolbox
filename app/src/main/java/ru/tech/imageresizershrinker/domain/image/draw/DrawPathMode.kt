package ru.tech.imageresizershrinker.domain.image.draw

sealed class DrawPathMode(open val ordinal: Int) {

    data object Free : DrawPathMode(-1)
    data object Line : DrawPathMode(0)
    data object PointingArrow : DrawPathMode(1)
    data object DoublePointingArrow : DrawPathMode(2)
    data object LinePointingArrow : DrawPathMode(3)
    data object DoubleLinePointingArrow : DrawPathMode(4)
    data object OutlinedRect : DrawPathMode(5)
    data object OutlinedOval : DrawPathMode(6)
    data object Rect : DrawPathMode(7)
    data object Oval : DrawPathMode(8)

    //TODO data object DoubleLinePointingArrowWithText: DrawPathMode(5)

    companion object {
        val entries by lazy {
            listOf(
                Free,
                Line,
                PointingArrow,
                DoublePointingArrow,
                LinePointingArrow,
                DoubleLinePointingArrow,
                OutlinedRect,
                OutlinedOval,
                Rect,
                Oval
            )
        }

        operator fun invoke(ordinal: Int) = entries.find {
            it.ordinal == ordinal
        } ?: Free
    }
}