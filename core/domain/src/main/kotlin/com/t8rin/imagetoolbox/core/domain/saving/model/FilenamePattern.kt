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

@file:Suppress("ConstPropertyName")

package com.t8rin.imagetoolbox.core.domain.saving.model

import com.t8rin.imagetoolbox.core.domain.utils.trimTrailingZero

@JvmInline
value class FilenamePattern(
    val value: String
) {
    override fun toString(): String = value

    fun upper() = FilenamePattern(value.uppercase())

    fun hasUpper() = upperEntries.contains(this.upper())

    enum class NameCase {
        Lower, Upper, Spaces, Title, Snake, Kebab, Camel, Pascal;

        val token: String get() = "${OriginalName}{${name.lowercase()}}"

        fun format(originalName: String): String {
            if (this == Lower) return originalName.lowercase()
            if (this == Upper) return originalName.uppercase()

            val words = originalName.split(WORD_BOUNDARY).filter(String::isNotEmpty)
            if (this == Spaces) return words.joinToString(" ")

            val separator = when (this) {
                Title -> " "
                Snake -> "_"
                Kebab -> "-"
                else -> ""
            }
            return words.mapIndexed { index, word ->
                val lower = word.lowercase()
                if (this == Title || this == Pascal || (this == Camel && index > 0)) {
                    lower.replaceFirstChar(Char::titlecase)
                } else lower
            }.joinToString(separator)
        }
    }

    enum class GeometryFormat(private val suffix: String) {
        Ratio(""),
        Orientation("{orientation}"),
        Megapixels("{mp}");

        val token: String get() = "$Geometry$suffix"

        fun format(width: Int?, height: Int?): String {
            if (width == null || height == null || width <= 0 || height <= 0) return ""

            return when (this) {
                Ratio -> {
                    val divisor = gcd(width, height)
                    "${width / divisor}x${height / divisor}"
                }

                Orientation -> when {
                    width > height -> "landscape"
                    width < height -> "portrait"
                    else -> "square"
                }

                Megapixels -> "%.2f".format(width.toLong() * height / 1_000_000.0)
                    .trimTrailingZero()
            }
        }
    }

    companion object {
        val Prefix = FilenamePattern("\\p")
        val OriginalName = FilenamePattern("\\o")
        val Width = FilenamePattern("\\w")
        val Height = FilenamePattern("\\h")
        val Geometry = FilenamePattern("\\g")
        val Date = FilenamePattern("\\d")
        val Rand = FilenamePattern("\\r")
        val Sequence = FilenamePattern("\\c")
        val PresetInfo = FilenamePattern("\\i")
        val ScaleMode = FilenamePattern("\\m")
        val Suffix = FilenamePattern("\\s")
        val Extension = FilenamePattern("\\e")
        val ParentFolder = FilenamePattern("\\f")
        val FileSize = FilenamePattern("\\z")
        val Uuid = FilenamePattern("\\u")

        val PrefixUpper = FilenamePattern("\\p").upper()
        val OriginalNameUpper = FilenamePattern("\\o").upper()
        val DateUpper = FilenamePattern("\\d").upper()
        val PresetInfoUpper = FilenamePattern("\\i").upper()
        val ScaleModeUpper = FilenamePattern("\\m").upper()
        val SuffixUpper = FilenamePattern("\\s").upper()
        val ExtensionUpper = FilenamePattern("\\e").upper()
        val ParentFolderUpper = FilenamePattern("\\f").upper()
        val FileSizeUpper = FilenamePattern("\\z").upper()
        val UuidUpper = FilenamePattern("\\u").upper()

        val Default =
            "${Prefix}_$OriginalName($Width)x($Height)${Date}{yyyy-MM-dd_HH-mm-ss}_${Rand}{4}[${Sequence}]_${PresetInfo}_${ScaleMode}_${Suffix}.$Extension"

        val ForOriginal =
            "${Prefix}_$OriginalName($Width)x($Height)[${Sequence}]_${PresetInfo}_${ScaleMode}_${Suffix}.$Extension"


        fun String.replace(
            pattern: FilenamePattern,
            newValue: String,
            ignoreCase: Boolean = false
        ): String = replace(
            oldValue = pattern.value,
            newValue = newValue,
            ignoreCase = ignoreCase
        )

        fun String.replaceNameCases(originalName: String): String = replace(NAME_CASE_PATTERN) {
            nameCases.getValue(it.value).format(originalName)
        }

        fun String.replaceGeometry(width: Int?, height: Int?): String = replace(geometryPattern) {
            geometryFormats.getValue(it.value).format(width, height)
        }

        private val WORD_BOUNDARY = Regex(
            """[^\p{L}\p{M}\p{N}]+|(?<=[\p{Ll}\p{N}])(?=\p{Lu})|(?<=\p{Lu})(?=\p{Lu}\p{Ll})"""
        )
        private val nameCases = NameCase.entries.associateBy(NameCase::token)
        private val geometryFormats = GeometryFormat.entries.associateBy(GeometryFormat::token)
        private val NAME_CASE_PATTERN =
            Regex(nameCases.keys.joinToString("|", transform = Regex::escape))
        val geometryPattern = Regex(geometryFormats.keys.joinToString("|") { token ->
            if (token == Geometry.value) """\\g(?!\{)""" else Regex.escape(token)
        })

        private tailrec fun gcd(first: Int, second: Int): Int =
            if (second == 0) first else gcd(second, first % second)

        val entries by lazy {
            listOf(
                Prefix,
                OriginalName,
                Width,
                Height,
                Geometry,
                Date,
                Rand,
                Sequence,
                ParentFolder,
                Uuid,
                PresetInfo,
                ScaleMode,
                Suffix,
                Extension
            )
        }

        val upperEntries by lazy {
            listOf(
                PrefixUpper,
                OriginalNameUpper,
                DateUpper,
                ParentFolderUpper,
                FileSizeUpper,
                UuidUpper,
                PresetInfoUpper,
                ScaleModeUpper,
                SuffixUpper,
                ExtensionUpper
            )
        }
    }
}