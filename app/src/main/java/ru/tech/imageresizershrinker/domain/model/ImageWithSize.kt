package ru.tech.imageresizershrinker.domain.model

data class ImageWithSize<I>(
    val image: I,
    val size: ImageSize
)

infix fun <I> I.withSize(
    size: ImageSize
): ImageWithSize<I> = ImageWithSize(
    image = this,
    size = size
)