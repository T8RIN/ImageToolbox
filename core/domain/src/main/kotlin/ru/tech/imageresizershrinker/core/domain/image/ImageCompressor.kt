package ru.tech.imageresizershrinker.core.domain.image

import ru.tech.imageresizershrinker.core.domain.model.ImageFormat

interface ImageCompressor<Image> {

    suspend fun compress(
        image: Image,
        imageFormat: ImageFormat,
        quality: Float
    ): ByteArray

}