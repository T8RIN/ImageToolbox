package ru.tech.imageresizershrinker.core.domain

sealed class ImageScaleMode(val value: Int) {
    data object NotPresent: ImageScaleMode(-2)
    data object Default : ImageScaleMode(-1)
    data object Bilinear : ImageScaleMode(0)
    data object Nearest : ImageScaleMode(1)
    data object Bicubic : ImageScaleMode(2)
    data object Mitchell : ImageScaleMode(3)
    data object Lanczos : ImageScaleMode(4)
    data object Catmull : ImageScaleMode(5)
    data object Hermite : ImageScaleMode(6)
    data object Spline : ImageScaleMode(7)
    data object Hann : ImageScaleMode(8)

    companion object {
        val entries by lazy {
            listOf(
                Default,
                Bilinear,
                Nearest,
                Bicubic,
                Mitchell,
                Lanczos,
                Catmull,
                Hermite,
                Spline,
                Hann
            )
        }

        fun fromInt(value: Int): ImageScaleMode = when(value) {
            -1 -> Default
            0 -> Bilinear
            1 -> Nearest
            2 -> Bicubic
            3 -> Mitchell
            4 -> Lanczos
            5 -> Catmull
            6 -> Hermite
            7 -> Spline
            8 -> Hann

            else -> NotPresent
        }
    }
}