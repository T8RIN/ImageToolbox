package ru.tech.imageresizershrinker.domain.model

import ru.tech.imageresizershrinker.domain.Domain

sealed class ImageFormat(
    val title: String,
    val extension: String,
    val type: String,
    val canChangeCompressionValue: Boolean,
    val canWriteExif: Boolean = false,
    val compressionRange: IntRange = 0..100,
    val compressionType: CompressionType = CompressionType.Quality
) : Domain {
    data object Png : ImageFormat(
        title = "PNG",
        extension = "png",
        type = "image/png",
        canChangeCompressionValue = false,
        canWriteExif = true
    )

    data object Jpg : ImageFormat(
        title = "JPG",
        extension = "jpg",
        type = "image/jpg",
        canChangeCompressionValue = true,
        canWriteExif = true
    )

    data object Jpeg : ImageFormat(
        title = "JPEG",
        extension = "jpeg",
        type = "image/jpeg",
        canChangeCompressionValue = true,
        canWriteExif = true
    )

    sealed class Webp(
        title: String,
        compressionType: CompressionType
    ) : ImageFormat(
        extension = "webp",
        type = "image/webp",
        canChangeCompressionValue = true,
        title = title,
        canWriteExif = true,
        compressionType = compressionType
    ) {
        data object Lossless : Webp(
            title = "WEBP Lossless",
            compressionType = CompressionType.Effort
        )

        data object Lossy : Webp(
            title = "WEBP Lossy",
            compressionType = CompressionType.Quality
        )
    }

    data object Bmp : ImageFormat(
        title = "BMP",
        extension = "bmp",
        type = "image/bmp",
        canChangeCompressionValue = false
    )

    data object Avif : ImageFormat(
        title = "AVIF",
        extension = "avif",
        type = "image/avif",
        canChangeCompressionValue = true
    )

    data object Heif : ImageFormat(
        title = "HEIF",
        extension = "heif",
        type = "image/heif",
        canChangeCompressionValue = true
    )

    data object Heic : ImageFormat(
        title = "HEIC",
        extension = "heic",
        type = "image/heic",
        canChangeCompressionValue = true
    )

    sealed class Jxl(title: String) : ImageFormat(
        extension = "jxl",
        type = "image/jxl",
        canChangeCompressionValue = true,
        title = title,
        compressionRange = 1..9,
        compressionType = CompressionType.Effort
    ) {
        data object Lossless : Jxl(title = "JXL Lossless")

        data object Lossy : Jxl(title = "JXL Lossy")
    }

    companion object {
        sealed class CompressionType {
            data object Quality : CompressionType()
            data object Effort : CompressionType()
        }

        fun Default(): ImageFormat = Jpg

        operator fun get(typeString: String?): ImageFormat = when {
            typeString == null -> Default()
            typeString.contains("jxl") -> Jxl.Lossless
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
                Heif,
                Jxl.Lossless,
                Jxl.Lossy
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
                Heif,
                Jxl.Lossless,
                Jxl.Lossy
            )
    }
}