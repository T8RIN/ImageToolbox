package ru.tech.imageresizershrinker.domain.model

data class IntegerSize(
    val width: Int,
    val height: Int
) {
    val aspectRatio: Float get() = width.toFloat() / height

    operator fun times(i: Float): IntegerSize = IntegerSize(
        width = (width * i).toInt(),
        height = (height * i).toInt()
    ).coerceAtLeast(0, 0)

    private fun coerceAtLeast(
        minWidth: Int,
        minHeight: Int
    ): IntegerSize = IntegerSize(
        width = width.coerceAtLeast(minWidth),
        height = height.coerceAtLeast(minHeight)
    )

    fun isZero(): Boolean = width == 0 || height == 0
}