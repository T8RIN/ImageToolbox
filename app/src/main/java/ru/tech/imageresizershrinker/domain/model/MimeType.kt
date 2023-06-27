package ru.tech.imageresizershrinker.domain.model

import ru.tech.imageresizershrinker.domain.Domain

sealed class MimeType(
    val extension: String,
    val type: String
) : Domain {
    object Png : MimeType("png", "image/png")
    object Jpg : MimeType("jpg", "image/jpg")
    object Jpeg : MimeType("jpeg", "image/jpeg")

    sealed class Webp : MimeType("webp", "image/webp") {
        object Lossless : Webp()
        object Lossy : Webp()
    }

    object Bmp : MimeType("bmp", "image/bmp")

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

        val entries
            get() = listOf(
                Jpg,
                Jpeg,
                Png,
                Webp.Lossy,
                Webp.Lossless,
                Bmp
            )
    }
}