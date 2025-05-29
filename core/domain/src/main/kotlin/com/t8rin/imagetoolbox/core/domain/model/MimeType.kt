/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.t8rin.imagetoolbox.core.domain.model

import com.t8rin.imagetoolbox.core.domain.model.MimeType.Multiple

sealed class MimeType(
    val entries: Set<String>
) {
    constructor(type: String) : this(setOf(type))

    data class Single(
        private val type: String
    ) : MimeType(type) {
        val entry = type
    }

    data class Multiple(
        private val types: Set<String>
    ) : MimeType(types)

    companion object {
        val All = Single("*/*")

        val Txt = Single("text/plain")
        val Pdf = Single("application/pdf")
        val Zip = Single("application/zip")
        val Webp = Single("image/webp")
        val Gif = Single("image/gif")
        val Apng = Single("image/apng")
        val StaticPng = Single("image/png")
        val Png = Apng + StaticPng
        val Audio = Single("audio/*")
        val Jpg = Single("image/jpg")
        val Jpeg = Single("image/jpeg")
        val JpgAll = Jpg + Jpeg
        val Font = Multiple(
            setOf(
                "font/ttf",
                "application/x-font-ttf",
                "font/otf"
            )
        )
        val Bmp = Single("image/bmp")
        val Avif = Single("image/avif")
        val Heif = Single("image/heif")
        val Heic = Single("image/heic")
        val Jxl = Single("image/jxl")
        val Jp2 = Single("image/jp2")
        val Tiff = Single("image/tiff")
        val Qoi = Single("image/qoi")
        val Ico = Single("image/x-icon")
        val Svg = Single("image/svg+xml")
    }

}

fun mimeType(
    type: String
): MimeType.Single = MimeType.Single(type)

fun String.toMimeType() = mimeType(this)

fun mimeTypeOf(
    vararg types: String
): Multiple = Multiple(types.toSet())

fun mimeTypeOf(
    vararg types: MimeType
): Multiple = Multiple(types.flatMapTo(mutableSetOf()) { it.entries })

operator fun MimeType.plus(
    type: MimeType
): Multiple = Multiple(types = entries + type.entries)

operator fun Multiple.minus(
    type: MimeType
): Multiple = Multiple(types = entries - type.entries)