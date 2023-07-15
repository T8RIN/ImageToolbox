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

    object Avif : ImageFormat(
        title = "AVIF",
        extension = "avif",
        type = "image/avif",
        canChangeQuality = true
    )

    object Heif : ImageFormat(
        title = "HEIF",
        extension = "heif",
        type = "image/heif",
        canChangeQuality = true
    )

    object Heic : ImageFormat(
        title = "HEIC",
        extension = "heic",
        type = "image/heic",
        canChangeQuality = true
    )

    companion object {
        fun Default(): ImageFormat = Jpg

        operator fun get(typeString: String?): ImageFormat = when {
            typeString == null -> Default()
            typeString.contains("bmp") -> Bmp
            typeString.contains("jpeg") -> Jpeg
            typeString.contains("jpg") -> Jpg
            typeString.contains("webp") -> Webp.Lossless
            typeString.contains("avif") -> Avif
            typeString.contains("heif") -> Heif
            typeString.contains("heic") -> Heic
            else -> Default()
        }

        val highLevelFormats
            get() = listOf(
                Avif,
                Heic,
                Heif
            )

        val entries
            get() = listOf(
                Jpg,
                Jpeg,
                Png,
                Bmp,
                Webp.Lossy,
                Webp.Lossless,
                Avif,
                Heic,
                Heif
            )
    }
}