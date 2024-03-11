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

package ru.tech.imageresizershrinker.core.domain.model

sealed class ImageFormatGroup(
    val title: String,
    val formats: List<ImageFormat>
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
            ImageFormat.PngLossless,
            ImageFormat.PngLossy
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
            ImageFormat.Avif
        )
    )

    data object Heic : ImageFormatGroup(
        title = "HEIC",
        formats = listOf(
            ImageFormat.Heic,
            ImageFormat.Heif
        )
    )

    data object Jxl : ImageFormatGroup(
        title = "JXL",
        formats = listOf(
            ImageFormat.Jxl.Lossless,
            ImageFormat.Jxl.Lossy
        )
    )

    companion object {
        val entries by lazy {
            listOf(Jpg, Png, Webp, Avif, Heic, Jxl, Bmp)
        }

        val alphaContainedEntries
            get() = listOf(
                Png,
                Webp,
                Avif,
                Heic,
                Jxl
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