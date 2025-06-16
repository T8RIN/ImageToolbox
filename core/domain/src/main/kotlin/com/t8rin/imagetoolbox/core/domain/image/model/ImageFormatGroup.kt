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

sealed class ImageFormatGroup(
    open val title: String,
    open val formats: List<ImageFormat>
) {
    data object Jpg : ImageFormatGroup(
        title = "JPG",
        formats = listOf(
            ImageFormat.Jpg,
            ImageFormat.Jpeg,
            ImageFormat.MozJpeg,
            ImageFormat.Jpegli
        )
    )

    data object Png : ImageFormatGroup(
        title = "PNG",
        formats = listOf(
            ImageFormat.Png.Lossless,
            ImageFormat.Png.Lossy
        )
    )

    data object Bmp : ImageFormatGroup(
        title = "BMP",
        formats = listOf(
            ImageFormat.Bmp
        )
    )

    data object Webp : ImageFormatGroup(
        title = "WEBP",
        formats = listOf(
            ImageFormat.Webp.Lossless,
            ImageFormat.Webp.Lossy
        )
    )

    data object Avif : ImageFormatGroup(
        title = "AVIF",
        formats = listOf(
            ImageFormat.Avif.Lossless,
            ImageFormat.Avif.Lossy
        )
    )

    data object Heic : ImageFormatGroup(
        title = "HEIC",
        formats = listOf(
            ImageFormat.Heic.Lossless,
            ImageFormat.Heic.Lossy,
            ImageFormat.Heif.Lossless,
            ImageFormat.Heif.Lossy
        )
    )

    data object Jxl : ImageFormatGroup(
        title = "JXL",
        formats = listOf(
            ImageFormat.Jxl.Lossless,
            ImageFormat.Jxl.Lossy
        )
    )

    data object Jpeg2000 : ImageFormatGroup(
        title = "JPEG 2000",
        formats = listOf(
            ImageFormat.Jpeg2000.Jp2,
            ImageFormat.Jpeg2000.J2k
        )
    )

    data object Tiff : ImageFormatGroup(
        title = "TIFF",
        formats = listOf(
            ImageFormat.Tif,
            ImageFormat.Tiff
        )
    )

    data object Qoi : ImageFormatGroup(
        title = "QOI",
        formats = listOf(
            ImageFormat.Qoi
        )
    )

    data object Ico : ImageFormatGroup(
        title = "ICO",
        formats = listOf(
            ImageFormat.Ico
        )
    )

    data object Gif : ImageFormatGroup(
        title = "GIF",
        formats = listOf(
            ImageFormat.Gif
        )
    )

    data class Custom(
        override val title: String,
        override val formats: List<ImageFormat>
    ) : ImageFormatGroup(title, formats)

    companion object {
        val entries by lazy {
            listOf(Jpg, Png, Webp, Avif, Heic, Jxl, Bmp, Jpeg2000, Tiff, Qoi, Ico, Gif)
        }

        val alphaContainedEntries
            get() = listOf(
                Png,
                Webp,
                Avif,
                Heic,
                Jxl,
                Jpeg2000,
                Qoi,
                Ico,
                Gif
            )

        val highLevelFormats by lazy {
            listOf(
                Avif,
                Heic
            )
        }

        fun fromFormat(
            imageFormat: ImageFormat
        ): ImageFormatGroup = entries.first { imageFormat in it.formats }
    }
}

val ImageFormat.Companion.alphaContainedEntries: List<ImageFormat> by lazy {
    ImageFormatGroup.alphaContainedEntries.flatMap { it.formats }
}