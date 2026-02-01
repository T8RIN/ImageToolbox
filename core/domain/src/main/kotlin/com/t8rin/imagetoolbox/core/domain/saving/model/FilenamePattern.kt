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

@JvmInline
value class FilenamePattern(
    val value: String
) {
    override fun toString(): String = value

    fun upper() = FilenamePattern(value.uppercase())

    fun hasUpper() = upperEntries.contains(this.upper())

    companion object {
        val Prefix = FilenamePattern("\\p")
        val OriginalName = FilenamePattern("\\o")
        val Width = FilenamePattern("\\w")
        val Height = FilenamePattern("\\h")
        val Date = FilenamePattern("\\d")
        val Rand = FilenamePattern("\\r")
        val Sequence = FilenamePattern("\\c")
        val PresetInfo = FilenamePattern("\\i")
        val ScaleMode = FilenamePattern("\\m")
        val Suffix = FilenamePattern("\\s")
        val Extension = FilenamePattern("\\e")

        val PrefixUpper = FilenamePattern("\\p").upper()
        val OriginalNameUpper = FilenamePattern("\\o").upper()
        val DateUpper = FilenamePattern("\\d").upper()
        val PresetInfoUpper = FilenamePattern("\\i").upper()
        val ScaleModeUpper = FilenamePattern("\\m").upper()
        val SuffixUpper = FilenamePattern("\\s").upper()
        val ExtensionUpper = FilenamePattern("\\e").upper()

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

        val entries by lazy {
            listOf(
                Prefix,
                OriginalName,
                Width,
                Height,
                Date,
                Rand,
                Sequence,
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
                PresetInfoUpper,
                ScaleModeUpper,
                SuffixUpper,
                ExtensionUpper
            )
        }
    }
}