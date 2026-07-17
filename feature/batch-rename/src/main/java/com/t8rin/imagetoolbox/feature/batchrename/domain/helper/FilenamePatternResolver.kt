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

package com.t8rin.imagetoolbox.feature.batchrename.domain.helper

import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.replace
import com.t8rin.imagetoolbox.core.domain.utils.humanFileSize
import com.t8rin.imagetoolbox.core.domain.utils.slice
import com.t8rin.imagetoolbox.core.domain.utils.timestamp
import com.t8rin.imagetoolbox.core.domain.utils.transliterate
import com.t8rin.imagetoolbox.feature.batchrename.domain.model.RenameFile
import kotlin.random.Random
import kotlin.uuid.Uuid

class FilenamePatternResolver(
    private val prefix: String,
    private val suffix: String,
    randomSeed: Long = System.currentTimeMillis()
) {
    private val random = Random(randomSeed)

    fun resolve(
        pattern: String,
        file: RenameFile,
        sequence: Int,
        dateMillis: Long
    ): String {
        fun randomDigits(count: Int): String = buildString {
            repeat(count.coerceIn(0, 500)) {
                append(random.nextInt(10))
            }
        }

        var result = pattern

        result = runCatching {
            result.replace(DATE_PATTERN) { match ->
                timestamp(match.groupValues[1], dateMillis)
            }.replace(DATE_PATTERN_UPPER) { match ->
                timestamp(match.groupValues[1], dateMillis).uppercase()
            }
        }.getOrDefault(result)

        result = runCatching {
            result.replace(SEQUENCE_PATTERN) { match ->
                val params = match.groupValues[1].split(":")
                val start = if (params.size == 1) 1 else params[0].toIntOrNull() ?: 1
                val step = params.getOrNull(1)?.toIntOrNull() ?: 1
                val padding = params.getOrNull(2)?.toIntOrNull()
                    ?: if (params.size == 1) params[0].toIntOrNull() ?: 0 else 0

                val current = start + (sequence - 1) * step
                current.toString().padStart(padding, '0')
            }
        }.getOrDefault(result)

        result = runCatching {
            result.replace(RANDOM_PATTERN) { match ->
                randomDigits(match.groupValues[1].toIntOrNull() ?: DEFAULT_RANDOM_LENGTH)
            }
        }.getOrDefault(result)

        val extension = file.extension
        val originalName = file.nameWithoutExtension
        val defaultDate = timestamp(date = dateMillis)

        result = runCatching {
            result.replace(ORIGINAL_NAME_SLICE_PATTERN) { match ->
                val start = match.groupValues[1].toIntOrNull()
                val end = match.groupValues[2].toIntOrNull()
                originalName.slice(start, end)
            }.replace(ORIGINAL_NAME_SLICE_PATTERN_UPPER) { match ->
                val start = match.groupValues[1].toIntOrNull()
                val end = match.groupValues[2].toIntOrNull()
                originalName.uppercase().slice(start, end)
            }.replace(ORIGINAL_NAME_TRANS_PATTERN) {
                originalName.transliterate()
            }.replace(ORIGINAL_NAME_TRANS_PATTERN_UPPER) {
                originalName.uppercase().transliterate()
            }.replace(ORIGINAL_NAME_REPLACE_PATTERN) { match ->
                val old = match.groupValues[1]
                val new = match.groupValues[2]
                originalName.replace(old, new)
            }.replace(ORIGINAL_NAME_REPLACE_PATTERN_UPPER) { match ->
                val old = match.groupValues[1]
                val new = match.groupValues[2]
                originalName.uppercase().replace(old, new)
            }
        }.getOrDefault(result)
        val parentFolder = file.parentFolder.orEmpty()

        val fileSizeRaw = file.fileSize ?: 0L
        result = runCatching {
            result.replace(FILE_SIZE_PATTERN) { match ->
                val unit = match.groupValues[1].lowercase()
                when (unit) {
                    "b" -> fileSizeRaw.toString()
                    "kb" -> (fileSizeRaw / 1024.0).let { "%.1f".format(it) }
                    "mb" -> (fileSizeRaw / (1024.0 * 1024.0)).let { "%.1f".format(it) }
                    else -> humanFileSize(fileSizeRaw)
                }
            }
        }.getOrDefault(result)

        val fileSize = humanFileSize(fileSizeRaw)

        return result
            .replace(FilenamePattern.Width, file.width?.toString().orEmpty())
            .replace(FilenamePattern.Height, file.height?.toString().orEmpty())
            .replace(FilenamePattern.Prefix, prefix)
            .replace(FilenamePattern.Suffix, suffix)
            .replace(FilenamePattern.OriginalName, originalName)
            .replace(FilenamePattern.Sequence, sequence.toString())
            .replace(FilenamePattern.Extension, extension)
            .replace(FilenamePattern.Rand, randomDigits(DEFAULT_RANDOM_LENGTH))
            .replace(FilenamePattern.Date, defaultDate)
            .replace(FilenamePattern.ParentFolder, parentFolder)
            .replace(FilenamePattern.FileSize, fileSize)
            .replace(FilenamePattern.Uuid, Uuid.random().toString())
            .replace(FilenamePattern.PrefixUpper, prefix.uppercase())
            .replace(FilenamePattern.SuffixUpper, suffix.uppercase())
            .replace(FilenamePattern.OriginalNameUpper, originalName.uppercase())
            .replace(FilenamePattern.ExtensionUpper, extension.uppercase())
            .replace(FilenamePattern.DateUpper, defaultDate.uppercase())
            .replace(FilenamePattern.ParentFolderUpper, parentFolder.uppercase())
            .replace(FilenamePattern.FileSizeUpper, fileSize.uppercase())
            .replace(FilenamePattern.UuidUpper, Uuid.random().toString().uppercase())
    }

    private companion object {
        const val DEFAULT_RANDOM_LENGTH = 4

        val DATE_PATTERN = """\\d[{]([^}]*)[}]""".toRegex()
        val DATE_PATTERN_UPPER = """\\D[{]([^}]*)[}]""".toRegex()
        val RANDOM_PATTERN = """\\r[{](\d+)[}]""".toRegex()
        val ORIGINAL_NAME_SLICE_PATTERN = """\\o\{(-?\d+)?:(-?\d+)?\}""".toRegex()
        val ORIGINAL_NAME_SLICE_PATTERN_UPPER = """\\O\{(-?\d+)?:(-?\d+)?\}""".toRegex()
        val ORIGINAL_NAME_TRANS_PATTERN = """\\o\{t\}""".toRegex()
        val ORIGINAL_NAME_TRANS_PATTERN_UPPER = """\\O\{t\}""".toRegex()
        val ORIGINAL_NAME_REPLACE_PATTERN = """\\o\{s/(.*?)/(.*?)/\}""".toRegex()
        val ORIGINAL_NAME_REPLACE_PATTERN_UPPER = """\\O\{s/(.*?)/(.*?)/\}""".toRegex()
        val SEQUENCE_PATTERN = """\\c\{([^}]*)\}""".toRegex()
        val FILE_SIZE_PATTERN = """\\z\{([^}]*)\}""".toRegex()
    }
}