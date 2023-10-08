package ru.tech.imageresizershrinker.domain.model

data class ImageWithSize<I>(
    val image: I,
    val size: IntegerSize
)

infix fun <I> I.withSize(
    size: IntegerSize
): ImageWithSize<I> = ImageWithSize(
    image = this,
    size = size
)