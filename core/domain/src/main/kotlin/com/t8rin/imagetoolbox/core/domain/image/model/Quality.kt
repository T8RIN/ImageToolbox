/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
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

import androidx.annotation.IntRange

sealed interface Quality {
    val qualityValue: Int

    fun coerceIn(
        imageFormat: ImageFormat
    ): Quality {
        return when (imageFormat) {
            is ImageFormat.Jxl -> {
                val value = this as? Jxl ?: return Jxl()
                value.copy(
                    qualityValue = qualityValue.coerceIn(1..100),
                    effort = effort.coerceIn(1..10),
                    speed = speed.coerceIn(0..4)
                )
            }

            is ImageFormat.Png.Lossy -> {
                val value = this as? PngLossy
                    ?: return PngLossy()
                value.copy(
                    maxColors = value.maxColors.coerceIn(2..1024),
                    compressionLevel = compressionLevel.coerceIn(0..9)
                )
            }

            is ImageFormat.Avif -> {
                val value = this as? Avif
                    ?: return Avif()
                value.copy(
                    qualityValue = qualityValue.coerceIn(1..100),
                    effort = effort.coerceIn(0..9)
                )
            }

            is ImageFormat.Tif,
            is ImageFormat.Tiff -> {
                val value = this as? Tiff
                    ?: return Tiff()
                value.copy(
                    compressionScheme = value.compressionScheme.coerceIn(0..9)
                )
            }

            is ImageFormat.Jpeg2000 -> Base(qualityValue.coerceIn(20..100))

            else -> {
                Base(qualityValue.coerceIn(0..100))
            }
        }
    }

    fun isNonAlpha(): Boolean = if (this is Jxl) {
        channels == Channels.RGB || channels == Channels.Monochrome
    } else false

    fun isDefault(): Boolean = when (this) {
        is Base -> this == Base()
        is Avif -> this == Avif()
        is Jxl -> this == Jxl()
        is PngLossy -> this == PngLossy()
        is Tiff -> this == Tiff()
    }

    data class Jxl(
        @IntRange(from = 1, to = 100)
        override val qualityValue: Int = 50,
        @IntRange(from = 1, to = 10)
        val effort: Int = 2,
        @IntRange(from = 0, to = 4)
        val speed: Int = 0,
        val channels: Channels = Channels.RGBA
    ) : Quality

    data class Avif(
        @IntRange(from = 1, to = 100)
        override val qualityValue: Int = 50,
        @IntRange(from = 0, to = 9)
        val effort: Int = 0
    ) : Quality

    data class PngLossy(
        @IntRange(from = 2, to = 1024)
        val maxColors: Int = 512,
        @IntRange(from = 0, to = 9)
        val compressionLevel: Int = 7,
    ) : Quality {
        override val qualityValue: Int = compressionLevel
    }

    data class Tiff(
        val compressionScheme: Int = 5
    ) : Quality {
        override val qualityValue: Int = compressionScheme
    }

    data class Base(
        override val qualityValue: Int = 100
    ) : Quality

    enum class Channels {
        RGBA, RGB, Monochrome;

        companion object {
            fun fromInt(int: Int) = when (int) {
                1 -> RGB
                2 -> Monochrome
                else -> RGBA
            }
        }
    }
}