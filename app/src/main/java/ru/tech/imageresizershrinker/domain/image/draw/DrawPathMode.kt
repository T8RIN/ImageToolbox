package ru.tech.imageresizershrinker.domain.image.draw

sealed class DrawPathMode(open val ordinal: Int) {

    data object Free : DrawPathMode(-1)
    data object Line : DrawPathMode(0)
    data object PointingArrow : DrawPathMode(1)
    data object DoublePointingArrow : DrawPathMode(2)
    data object LinePointingArrow : DrawPathMode(3)
    data object DoubleLinePointingArrow : DrawPathMode(4)

    //TODO data object DoubleLinePointingArrowWithText: DrawPathMode(5)

    companion object {
        val entries by lazy {
            listOf(
                Free,
                Line,
                PointingArrow,
                DoublePointingArrow,
                LinePointingArrow,
                DoubleLinePointingArrow
            )
        }

        operator fun invoke(ordinal: Int) = when (ordinal) {
            -1 -> Free
            0 -> Line
            1 -> PointingArrow
            2 -> DoublePointingArrow
            3 -> LinePointingArrow
            4 -> DoubleLinePointingArrow
            else -> Line
        }
    }
}