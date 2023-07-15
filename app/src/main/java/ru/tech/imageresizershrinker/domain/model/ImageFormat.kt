package ru.tech.imageresizershrinker.domain.model

import ru.tech.imageresizershrinker.domain.Domain

sealed class ImageFormat(
    val title: String,
    val extension: String,
    val type: String,
    val canChangeQuality: Boolean
) : Domain {
    object Png : ImageFormat(
        title = "PNG",
        extension = "png",
        type = "image/png",
        canChangeQuality = false
    )

    object Jpg : ImageFormat(
        title = "JPG",
        extension = "jpg",
        type = "image/jpg",
        canChangeQuality = true
    )

    object Jpeg : ImageFormat(
        title = "JPEG",
        extension = "jpeg",
        type = "image/jpeg",
        canChangeQuality = true
    )

    sealed class Webp(title: String) : ImageFormat(
        extension = "webp",
        type = "image/webp",
        canChangeQuality = true,
        title = title
    ) {
        object Lossless : Webp(title = "WEBP Lossless")

        object Lossy : Webp(title = "WEBP Lossy")
    }

    object Bmp : ImageFormat(
        title = "BMP",
        extension = "bmp",
        type = "image/bmp",
        canChangeQuality = false
    )

    companion object {
        fun Default(): ImageFormat = Jpg

        operator fun get(typeString: String?): ImageFormat = when {
            typeString == null -> Default()
            typeString.contains("bmp") -> Bmp
            typeString.contains("jpeg") -> Jpeg
            typeString.contains("jpg") -> Jpg
            typeString.contains("webp") -> Webp.Lossless
            else -> Default()
        }

        val entries
            get() = listOf(
                Jpg,
                Jpeg,
                Png,
                Bmp,
                Webp.Lossy,
                Webp.Lossless,
            )
    }
}