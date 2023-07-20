package ru.tech.imageresizershrinker.domain.model

import ru.tech.imageresizershrinker.domain.Domain

interface ImageData<I, M> : Domain {
    val image: I
    val imageInfo: ImageInfo
    val metadata: M?

    fun copy(
        image: I = this.image,
        imageInfo: ImageInfo = this.imageInfo,
        metadata: M? = this.metadata,
    ): ImageData<I, M> = ImageData(image, imageInfo, metadata)

    operator fun component1() = image
    operator fun component2() = imageInfo
    operator fun component3() = metadata

    companion object {
        operator fun <I, M> invoke(
            image: I,
            imageInfo: ImageInfo,
            metadata: M? = null,
        ): ImageData<I, M> = ImageDataWrapper(image, imageInfo, metadata)
    }
}

private class ImageDataWrapper<I, M>(
    override val image: I,
    override val imageInfo: ImageInfo,
    override val metadata: M? = null,
) : ImageData<I, M>