package ru.tech.imageresizershrinker.domain.model

import ru.tech.imageresizershrinker.domain.Domain

sealed class ImageFormat(
    val title: String,
    val extension: String,
    val type: String,
    val canChangeQuality: Boolean,
    val canWriteExif: Boolean = false
) : Domain {
    data object Png : ImageFormat(
        title = "PNG",
        extension = "png",
        type = "image/png",
        canChangeQuality = false,
        canWriteExif = true
    )

    data object Jpg : ImageFormat(
        title = "JPG",
        extension = "jpg",
        type = "image/jpg",
        canChangeQuality = true,
        canWriteExif = true
    )

    data object Jpeg : ImageFormat(
        title = "JPEG",
        extension = "jpeg",
        type = "image/jpeg",
        canChangeQuality = true,
        canWriteExif = true
    )

    sealed class Webp(title: String) : ImageFormat(
        extension = "webp",
        type = "image/webp",
        canChangeQuality = true,
        canWriteExif = true,
        title = title
    ) {
        data object Lossless : Webp(title = "WEBP Lossless")

        data object Lossy : Webp(title = "WEBP Lossy")
    }

    data object Bmp : ImageFormat(
        title = "BMP",
        extension = "bmp",
        type = "image/bmp",
        canChangeQuality = false
    )

    data object Avif : ImageFormat(
        title = "AVIF",
        extension = "avif",
        type = "image/avif",
        canChangeQuality = true
    )

    data object Heif : ImageFormat(
        title = "HEIF",
        extension = "heif",
        type = "image/heif",
        canChangeQuality = true
    )

    data object Heic : ImageFormat(
        title = "HEIC",
        extension = "heic",
        type = "image/heic",
        canChangeQuality = true
    )

    companion object {
        fun Default(): ImageFormat = Jpg

        operator fun get(typeString: String?): ImageFormat = when {
            typeString == null -> Default()
            typeString.contains("png") -> Png
            typeString.contains("bmp") -> Bmp
            typeString.contains("jpeg") -> Jpeg
            typeString.contains("jpg") -> Jpg
            typeString.contains("webp") -> Webp.Lossless
            typeString.contains("avif") -> Avif
            typeString.contains("heif") -> Heif
            typeString.contains("heic") -> Heic
            else -> Default()
        }

        val alphaContainedEntries: List<ImageFormat>
            get() = listOf(
                Png,
                Webp.Lossy,
                Webp.Lossless,
                Avif,
                Heic,
                Heif
            )

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