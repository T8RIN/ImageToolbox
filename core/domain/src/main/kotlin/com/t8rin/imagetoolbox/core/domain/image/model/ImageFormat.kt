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

package com.t8rin.imagetoolbox.core.domain.image.model

import com.t8rin.imagetoolbox.core.domain.model.MimeType

sealed class ImageFormat(
    val title: String,
    val extension: String,
    val mimeType: MimeType.Single,
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
        mimeType = MimeType.StaticPng,
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
        mimeType = MimeType.Jpeg,
        canChangeCompressionValue = true,
        canWriteExif = true
    )

    data object Jpeg : ImageFormat(
        title = "JPEG",
        extension = "jpeg",
        mimeType = MimeType.Jpeg,
        canChangeCompressionValue = true,
        canWriteExif = true
    )

    data object MozJpeg : ImageFormat(
        title = "MozJPEG",
        extension = "jpg",
        mimeType = MimeType.Jpeg,
        canChangeCompressionValue = true,
        canWriteExif = true
    )

    data object Jpegli : ImageFormat(
        title = "Jpegli",
        extension = "jpg",
        mimeType = MimeType.Jpeg,
        canChangeCompressionValue = true,
        canWriteExif = true
    )

    sealed class Webp(
        title: String,
        compressionTypes: List<CompressionType>
    ) : ImageFormat(
        extension = "webp",
        mimeType = MimeType.Webp,
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
        mimeType = MimeType.Bmp,
        canChangeCompressionValue = false
    )

    sealed class Avif(
        title: String,
        compressionTypes: List<CompressionType>
    ) : ImageFormat(
        title = title,
        extension = "avif",
        mimeType = MimeType.Avif,
        canChangeCompressionValue = true,
        compressionTypes = compressionTypes
    ) {
        data object Lossless : Avif(
            title = "AVIF Lossless",
            compressionTypes = listOf(
                CompressionType.Effort(0..10)
            )
        )

        data object Lossy : Avif(
            title = "AVIF Lossy",
            compressionTypes = listOf(
                CompressionType.Quality(1..100),
                CompressionType.Effort(0..10)
            )
        )
    }

    sealed class Heif(
        title: String,
        compressionTypes: List<CompressionType>
    ) : ImageFormat(
        title = title,
        extension = "heif",
        mimeType = MimeType.Heif,
        compressionTypes = compressionTypes,
        canChangeCompressionValue = compressionTypes.isNotEmpty()
    ) {
        data object Lossless : Heif(
            title = "HEIF Lossless",
            compressionTypes = listOf()
        )

        data object Lossy : Heif(
            title = "HEIF Lossy",
            compressionTypes = listOf(
                CompressionType.Quality(0..100)
            )
        )
    }

    sealed class Heic(
        title: String,
        compressionTypes: List<CompressionType>
    ) : ImageFormat(
        title = title,
        extension = "heic",
        mimeType = MimeType.Heic,
        compressionTypes = compressionTypes,
        canChangeCompressionValue = compressionTypes.isNotEmpty()
    ) {
        data object Lossless : Heic(
            title = "HEIC Lossless",
            compressionTypes = listOf()
        )

        data object Lossy : Heic(
            title = "HEIC Lossy",
            compressionTypes = listOf(
                CompressionType.Quality(0..100)
            )
        )
    }

    sealed class Jxl(
        title: String,
        compressionTypes: List<CompressionType>
    ) : ImageFormat(
        extension = "jxl",
        mimeType = MimeType.Jxl,
        canChangeCompressionValue = true,
        title = title,
        compressionTypes = compressionTypes
    ) {
        data object Lossless : Jxl(
            title = "JXL Lossless",
            compressionTypes = listOf(
                CompressionType.Effort(1..10)
            )
        )

        data object Lossy : Jxl(
            title = "JXL Lossy",
            compressionTypes = listOf(
                CompressionType.Quality(1..100),
                CompressionType.Effort(1..10)
            )
        )
    }

    sealed class Jpeg2000(
        title: String,
        extension: String
    ) : ImageFormat(
        title = title,
        extension = extension,
        mimeType = MimeType.Jp2,
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

    data object Tiff : ImageFormat(
        title = "TIFF",
        extension = "tiff",
        mimeType = MimeType.Tiff,
        canChangeCompressionValue = true,
        compressionTypes = emptyList()
    )

    data object Tif : ImageFormat(
        title = "TIF",
        extension = "tif",
        mimeType = MimeType.Tiff,
        canChangeCompressionValue = true,
        compressionTypes = emptyList()
    )

    data object Qoi : ImageFormat(
        title = "QOI",
        extension = "qoi",
        mimeType = MimeType.Qoi,
        canChangeCompressionValue = false
    )

    data object Ico : ImageFormat(
        title = "ICO",
        extension = "ico",
        mimeType = MimeType.Ico,
        canChangeCompressionValue = false
    )

    data object Gif : ImageFormat(
        title = "GIF",
        extension = "gif",
        mimeType = MimeType.Gif,
        canChangeCompressionValue = true
    )

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

    companion object {
        val Default: ImageFormat by lazy { Jpg }

        operator fun get(typeString: String?): ImageFormat = when {
            typeString == null -> Default
            typeString.contains("tiff") -> Tiff
            typeString.contains("tif") -> Tif
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
            typeString.contains("qoi") -> Qoi
            typeString.contains("ico") -> Ico
            typeString.contains("svg") -> Png.Lossless
            typeString.contains("gif") -> Gif
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
                Tif,
                Tiff,
                Jpeg2000.Jp2,
                Jpeg2000.J2k,
                Qoi,
                Ico,
                Gif
            )
        }
    }
}