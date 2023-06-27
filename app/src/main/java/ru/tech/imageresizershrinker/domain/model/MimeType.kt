package ru.tech.imageresizershrinker.domain.model

import ru.tech.imageresizershrinker.domain.Domain

sealed class MimeType(
    val extension: String
) : Domain {
    object Png : MimeType("png")
    object Jpg : MimeType("jpg")
    object Jpeg : MimeType("jpeg")

    sealed class Webp : MimeType("webp") {
        object Lossless : Webp()
        object Lossy : Webp()
    }

    object Bmp : MimeType("bmp")

    companion object {
        fun Default(): MimeType = Png

        fun create(typeString: String?): MimeType = when {
            typeString == null -> Default()
            typeString.contains("bmp") -> Bmp
            typeString.contains("jpg") -> Jpg
            typeString.contains("jpeg") -> Jpeg
            typeString.contains("webp") -> Webp.Lossless
            else -> Default()
        }
    }
}