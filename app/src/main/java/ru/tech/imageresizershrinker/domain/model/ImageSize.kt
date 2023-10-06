package ru.tech.imageresizershrinker.domain.model

data class ImageSize(
    val width: Int,
    val height: Int
) {
    operator fun times(i: Float): ImageSize = ImageSize(
        width = (width * i).toInt(),
        height = (height * i).toInt()
    ).coerceAtLeast(0, 0)

    private fun coerceAtLeast(
        minWidth: Int,
        minHeight: Int
    ): ImageSize = ImageSize(
        width = width.coerceAtLeast(minWidth),
        height = height.coerceAtLeast(minHeight)
    )

    fun isZero(): Boolean = width == 0 || height == 0
}