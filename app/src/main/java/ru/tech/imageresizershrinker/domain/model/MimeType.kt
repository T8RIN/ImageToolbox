package ru.tech.imageresizershrinker.domain.model

import ru.tech.imageresizershrinker.domain.utils.Domain

sealed class MimeType(
    val title: String,
    val extension: String,
    val type: String,
    val canChangeQuality: Boolean
) : Domain {
    object Png :
        MimeType(title = "PNG", extension = "png", type = "image/png", canChangeQuality = false)

    object Jpg :
        MimeType(title = "JPG", extension = "jpg", type = "image/jpg", canChangeQuality = true)

    object Jpeg :
        MimeType(title = "JPEG", extension = "jpeg", type = "image/jpeg", canChangeQuality = true)

    sealed class Webp(
        title: String,
        canChangeQuality: Boolean
    ) : MimeType(
        extension = "webp",
        type = "image/webp",
        canChangeQuality = canChangeQuality,
        title = title
    ) {
        object Lossless : Webp(title = "WEBP Lossless", canChangeQuality = false)
        object Lossy : Webp(title = "WEBP Lossy", canChangeQuality = true)
    }

    object Bmp :
        MimeType(title = "BMP", extension = "bmp", type = "image/bmp", canChangeQuality = false)

    companion object {
        fun Default(): MimeType = Jpg

        fun create(typeString: String?): MimeType = when {
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