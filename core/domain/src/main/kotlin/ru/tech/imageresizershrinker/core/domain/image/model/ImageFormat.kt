/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package ru.tech.imageresizershrinker.core.domain.image.model

sealed class ImageFormat(
    val title: String,
    val extension: String,
    val mimeType: String,
    val canChangeCompressionValue: Boolean,
    val canWriteExif: Boolean = false,
    val compressionTypes: List<CompressionType> = listOf(CompressionType.Quality(0..100))
) {

    sealed class Png(
        title: String,
        compressionTypes: List<CompressionType>,
        canChangeCompressionValue: Boolean
    ) : ImageFormat(
        extension = "png",
        mimeType = "image/png",
        canChangeCompressionValue = canChangeCompressionValue,
        title = title,
        canWriteExif = true,
        compressionTypes = compressionTypes
    ) {
        data object Lossless : Png(
            title = "PNG Lossless",
            compressionTypes = emptyList(),
            canChangeCompressionValue = false
        )

        data object Lossy : Png(
            title = "PNG Lossy",
            compressionTypes = listOf(
                CompressionType.Effort(0..9)
            ),
            canChangeCompressionValue = true
        )
    }

    data object Jpg : ImageFormat(
        title = "JPG",
        extension = "jpg",
        mimeType = "image/jpeg",
        canChangeCompressionValue = true,
        canWriteExif = true
    )

    data object Jpeg : ImageFormat(
        title = "JPEG",
        extension = "jpeg",
        mimeType = "image/jpeg",
        canChangeCompressionValue = true,
        canWriteExif = true
    )

    data object MozJpeg : ImageFormat(
        title = "MozJpeg",
        extension = "jpg",
        mimeType = "image/jpeg",
        canChangeCompressionValue = true,
        canWriteExif = true
    )

    data object Jpegli : ImageFormat(
        title = "Jpegli",
        extension = "jpg",
        mimeType = "image/jpeg",
        canChangeCompressionValue = true,
        canWriteExif = true
    )

    sealed class Webp(
        title: String,
        compressionTypes: List<CompressionType>
    ) : ImageFormat(
        extension = "webp",
        mimeType = "image/webp",
        canChangeCompressionValue = true,
        title = title,
        canWriteExif = true,
        compressionTypes = compressionTypes
    ) {
        data object Lossless : Webp(
            title = "WEBP Lossless",
            compressionTypes = listOf(CompressionType.Effort(0..100))
        )

        data object Lossy : Webp(
            title = "WEBP Lossy",
            compressionTypes = listOf(CompressionType.Quality(0..100))
        )
    }

    data object Bmp : ImageFormat(
        title = "BMP",
        extension = "bmp",
        mimeType = "image/bmp",
        canChangeCompressionValue = false
    )

    sealed class Avif(
        title: String,
        compressionTypes: List<CompressionType>
    ) : ImageFormat(
        title = title,
        extension = "avif",
        mimeType = "image/avif",
        canChangeCompressionValue = true,
        compressionTypes = compressionTypes
    ) {
        data object Lossless : Avif(
            title = "AVIF Lossless",
            compressionTypes = listOf(
                CompressionType.Effort(0..9)
            )
        )

        data object Lossy : Avif(
            title = "AVIF Lossy",
            compressionTypes = listOf(
                CompressionType.Quality(1..100),
                CompressionType.Effort(0..9)
            )
        )
    }

    sealed class Heif(
        title: String,
        canChangeCompressionValue: Boolean
    ) : ImageFormat(
        title = title,
        extension = "heif",
        mimeType = "image/heif",
        canChangeCompressionValue = canChangeCompressionValue
    ) {
        data object Lossless : Heif(
            title = "HEIF Lossless",
            canChangeCompressionValue = false
        )

        data object Lossy : Heif(
            title = "HEIF Lossy",
            canChangeCompressionValue = true
        )
    }

    sealed class Heic(
        title: String,
        canChangeCompressionValue: Boolean
    ) : ImageFormat(
        title = title,
        extension = "heic",
        mimeType = "image/heic",
        canChangeCompressionValue = canChangeCompressionValue
    ) {
        data object Lossless : Heif(
            title = "HEIC Lossless",
            canChangeCompressionValue = false
        )

        data object Lossy : Heif(
            title = "HEIC Lossy",
            canChangeCompressionValue = true
        )
    }

    sealed class Jxl(
        title: String,
        compressionTypes: List<CompressionType>
    ) : ImageFormat(
        extension = "jxl",
        mimeType = "image/jxl",
        canChangeCompressionValue = true,
        title = title,
        compressionTypes = compressionTypes
    ) {
        data object Lossless : Jxl(
            title = "JXL Lossless",
            compressionTypes = listOf(
                CompressionType.Effort(0..9)
            )
        )

        data object Lossy : Jxl(
            title = "JXL Lossy",
            compressionTypes = listOf(
                CompressionType.Quality(1..100),
                CompressionType.Effort(0..9)
            )
        )
    }

    sealed class Jpeg2000(
        title: String,
        extension: String
    ) : ImageFormat(
        title = title,
        extension = extension,
        mimeType = "image/jp2",
        canChangeCompressionValue = true,
        compressionTypes = listOf(
            CompressionType.Quality(20..100)
        )
    ) {

        data object Jp2 : Jpeg2000(
            title = "JP2",
            extension = "jp2"
        )

        data object J2k : Jpeg2000(
            title = "J2K",
            extension = "j2k"
        )

    }

    companion object {
        sealed class CompressionType(
            open val compressionRange: IntRange = 0..100
        ) {
            data class Quality(
                override val compressionRange: IntRange = 0..100
            ) : CompressionType(compressionRange)

            data class Effort(
                override val compressionRange: IntRange = 0..100
            ) : CompressionType(compressionRange)
        }

        val Default: ImageFormat by lazy { Jpg }

        operator fun get(typeString: String?): ImageFormat = when {
            typeString == null -> Default
            typeString.contains("jp2") -> Jpeg2000.Jp2
            typeString.contains("j2k") -> Jpeg2000.J2k
            typeString.contains("jxl") -> Jxl.Lossless
            typeString.contains("png") -> Png.Lossless
            typeString.contains("bmp") -> Bmp
            typeString.contains("jpeg") -> Jpeg
            typeString.contains("jpg") -> Jpg
            typeString.contains("webp") -> Webp.Lossless
            typeString.contains("avif") -> Avif.Lossless
            typeString.contains("heif") -> Heif.Lossless
            typeString.contains("heic") -> Heic.Lossless
            else -> Default
        }

        val highLevelFormats by lazy {
            listOf(
                Avif.Lossy,
                Avif.Lossless,
                Heic.Lossy,
                Heic.Lossless,
                Heif.Lossy,
                Heif.Lossless
            )
        }

        val entries by lazy {
            listOf(
                Jpg,
                Jpeg,
                MozJpeg,
                Jpegli,
                Png.Lossless,
                Png.Lossy,
                Bmp,
                Webp.Lossless,
                Webp.Lossy,
                Avif.Lossless,
                Avif.Lossy,
                Heic.Lossless,
                Heic.Lossy,
                Heif.Lossless,
                Heif.Lossy,
                Jxl.Lossless,
                Jxl.Lossy,
                Jpeg2000.Jp2,
                Jpeg2000.J2k
            )
        }
    }
}