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
    ): ImageData<I, M> = create(image, imageInfo, metadata)

    operator fun component1() = image
    operator fun component2() = imageInfo
    operator fun component3() = metadata

    companion object {
        fun <I, M> create(
            image: I,
            imageInfo: ImageInfo,
            metadata: M? = null,
        ): ImageData<I, M> = object : ImageData<I, M> {
            override val image: I
                get() = image
            override val imageInfo: ImageInfo
                get() = imageInfo
            override val metadata: M?
                get() = metadata
        }
    }
}