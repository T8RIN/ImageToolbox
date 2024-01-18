package ru.tech.imageresizershrinker.core.domain.image

import ru.tech.imageresizershrinker.core.domain.model.ImageData

interface ImageGetter<I, M> {

    suspend fun getImage(
        uri: String,
        originalSize: Boolean = true
    ): ImageData<I, M>?

    fun getImageAsync(
        uri: String,
        originalSize: Boolean = true,
        onGetImage: (ImageData<I, M>) -> Unit,
        onError: (Throwable) -> Unit
    )

    suspend fun getImageWithTransformations(
        uri: String,
        transformations: List<Transformation<I>>,
        originalSize: Boolean = true
    ): ImageData<I, M>?

    suspend fun getImage(data: Any, originalSize: Boolean = true): I?

    fun getExtension(
        uri: String
    ): String?

}