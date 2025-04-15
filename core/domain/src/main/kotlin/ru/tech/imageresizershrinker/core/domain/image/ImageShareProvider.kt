package ru.tech.imageresizershrinker.core.domain.image

import ru.tech.imageresizershrinker.core.domain.image.model.ImageInfo

interface ImageShareProvider<I> : ShareProvider {

    suspend fun shareImage(
        imageInfo: ImageInfo,
        image: I,
        onComplete: () -> Unit
    )

    suspend fun cacheImage(
        image: I,
        imageInfo: ImageInfo,
        filename: String? = null
    ): String?

    suspend fun shareImages(
        uris: List<String>,
        imageLoader: suspend (String) -> Pair<I, ImageInfo>?,
        onProgressChange: (Int) -> Unit
    )

}