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

package com.t8rin.imagetoolbox.core.ui.widget.text

import androidx.compose.ui.graphics.vector.ImageVector
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.Date
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.FileSize
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.Geometry
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.OriginalName
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.Rand
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.Sequence
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.GeometryFormat
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.NameCase
import com.t8rin.imagetoolbox.core.domain.saving.model.RandomFilenamePattern
import com.t8rin.imagetoolbox.core.domain.utils.humanFileSize
import com.t8rin.imagetoolbox.core.domain.utils.slice
import com.t8rin.imagetoolbox.core.domain.utils.transliterate
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.AccessTime
import com.t8rin.imagetoolbox.core.resources.icons.AspectRatio
import com.t8rin.imagetoolbox.core.resources.icons.CalendarMonth
import com.t8rin.imagetoolbox.core.resources.icons.Code
import com.t8rin.imagetoolbox.core.resources.icons.Counter
import com.t8rin.imagetoolbox.core.resources.icons.DataObject
import com.t8rin.imagetoolbox.core.resources.icons.DateRange
import com.t8rin.imagetoolbox.core.resources.icons.EditCalendar
import com.t8rin.imagetoolbox.core.resources.icons.Event
import com.t8rin.imagetoolbox.core.resources.icons.Functions
import com.t8rin.imagetoolbox.core.resources.icons.History
import com.t8rin.imagetoolbox.core.resources.icons.ImageWeight
import com.t8rin.imagetoolbox.core.resources.icons.Keyboard
import com.t8rin.imagetoolbox.core.resources.icons.Language
import com.t8rin.imagetoolbox.core.resources.icons.Numeric
import com.t8rin.imagetoolbox.core.resources.icons.PhotoSizeSelectLarge
import com.t8rin.imagetoolbox.core.resources.icons.Public
import com.t8rin.imagetoolbox.core.resources.icons.Rotate90Cw
import com.t8rin.imagetoolbox.core.resources.icons.ShortText
import com.t8rin.imagetoolbox.core.resources.icons.Shuffle
import com.t8rin.imagetoolbox.core.resources.icons.Storage
import com.t8rin.imagetoolbox.core.resources.icons.TextFields
import com.t8rin.imagetoolbox.core.resources.icons.TextFormat
import com.t8rin.imagetoolbox.core.resources.icons.Timer
import com.t8rin.imagetoolbox.core.resources.icons.Translate
import com.t8rin.imagetoolbox.core.resources.icons.ViewWeek
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale
import java.util.TimeZone
import kotlin.random.Random

internal class ExampleGroup(
    val title: Int,
    val icon: ImageVector,
    val examples: List<Pair<String, String>>,
    val description: Int
)

internal fun FilenamePattern.examples(dateFormatOnly: Boolean): List<ExampleGroup> = when (this) {
    OriginalName -> originalNameExamples()
    Geometry -> geometryExamples()
    Sequence -> sequenceExamples()
    FileSize -> fileSizeExamples()
    Date -> dateExamples(dateFormatOnly)
    Rand -> randomExamples()
    else -> emptyList()
}

private fun originalNameExamples(): List<ExampleGroup> {
    val name = "Summer_TRIP_Miami"
    fun example(format: NameCase) = format.token to format.format(name)

    return listOf(
        ExampleGroup(
            title = R.string.filename_pattern_letter_case,
            icon = Icons.Rounded.TextFields,
            examples = listOf(
                OriginalName.value to name,
                example(NameCase.Lower),
                example(NameCase.Upper),
                OriginalName.upper().value to name.uppercase(),
                example(NameCase.Title)
            ),
            description = R.string.filename_pattern_help_name
        ),
        ExampleGroup(
            title = R.string.filename_pattern_word_styles,
            icon = Icons.Rounded.TextFormat,
            examples = listOf(
                NameCase.Spaces,
                NameCase.Snake,
                NameCase.Kebab,
                NameCase.Camel,
                NameCase.Pascal
            )
                .map(::example),
            description = R.string.filename_pattern_help_word_styles
        ),
        ExampleGroup(
            title = R.string.filename_pattern_name_parts,
            icon = Icons.Rounded.ShortText,
            examples = listOf(
                "$OriginalName{:6}" to name.slice(null, 6),
                "$OriginalName{7:11}" to name.slice(7, 11),
                "$OriginalName{-4:}" to name.slice(-4, null)
            ),
            description = R.string.filename_pattern_help_name_parts
        ),
        ExampleGroup(
            title = R.string.filename_pattern_text_changes,
            icon = Icons.Rounded.Translate,
            examples = listOf(
                "$OriginalName{t}" to name.transliterate(),
                "$OriginalName{s/_/-/}" to name.replace('_', '-'),
                "$OriginalName{s/_//}" to name.replace("_", "")
            ),
            description = R.string.filename_pattern_help_text_changes
        )
    )
}

private fun geometryExamples(): List<ExampleGroup> {
    fun example(format: GeometryFormat) = format.token to format.format(1920, 1080)

    return listOf(
        ExampleGroup(
            title = R.string.aspect_ratio,
            icon = Icons.Outlined.AspectRatio,
            examples = listOf(example(GeometryFormat.Ratio)),
            description = R.string.filename_pattern_help_geometry
        ),
        ExampleGroup(
            title = R.string.orientation,
            icon = Icons.Outlined.Rotate90Cw,
            examples = listOf(example(GeometryFormat.Orientation)),
            description = R.string.filename_pattern_help_orientation
        ),
        ExampleGroup(
            title = R.string.filename_pattern_geometry_mp,
            icon = Icons.Outlined.PhotoSizeSelectLarge,
            examples = listOf(example(GeometryFormat.Megapixels)),
            description = R.string.filename_pattern_help_megapixels
        )
    )
}

private fun sequenceExamples(): List<ExampleGroup> = listOf(
    ExampleGroup(
        title = R.string.caption_sequence_number,
        icon = Icons.Outlined.Counter,
        examples = listOf(Sequence.value to "14", "$Sequence{3}" to "014"),
        description = R.string.filename_pattern_help_sequence_number
    ),
    ExampleGroup(
        title = R.string.filename_pattern_sequence_step,
        icon = Icons.Rounded.Functions,
        examples = listOf(
            "$Sequence{10:2}" to "36",
            "$Sequence{1:2:3}" to "027",
            "$Sequence{10::3}" to "023"
        ),
        description = R.string.filename_pattern_help_sequence
    )
)

private fun fileSizeExamples(): List<ExampleGroup> {
    val bytes = 524_288L
    val size = humanFileSize(bytes)
    return listOf(
        ExampleGroup(
            title = R.string.filename_pattern_size_auto,
            icon = Icons.Outlined.ImageWeight,
            examples = listOf(FileSize.value to size, FileSize.upper().value to size.uppercase()),
            description = R.string.filename_pattern_help_size_auto
        ),
        ExampleGroup(
            title = R.string.filename_pattern_size_units,
            icon = Icons.Rounded.Storage,
            examples = listOf(
                "$FileSize{b}" to bytes.toString(),
                "$FileSize{kb}" to "%.1f".format(bytes / 1024.0),
                "$FileSize{mb}" to "%.1f".format(bytes / (1024.0 * 1024.0))
            ),
            description = R.string.filename_pattern_help_size
        )
    )
}

private fun randomExamples(): List<ExampleGroup> {
    val random = Random(3377)
    fun example(token: String) = token to RandomFilenamePattern.replaceIn(token, random)
    fun presets(vararg formats: RandomFilenamePattern) = formats.map { example(it.token(8)) }

    return listOf(
        ExampleGroup(
            title = R.string.filename_pattern_digits,
            icon = Icons.Filled.Numeric,
            examples = listOf(example(Rand.value), example(RandomFilenamePattern.Digits.token(8))),
            description = R.string.filename_pattern_help_random
        ),
        ExampleGroup(
            title = R.string.filename_pattern_letters,
            icon = Icons.Rounded.TextFields,
            examples = presets(
                RandomFilenamePattern.Lower,
                RandomFilenamePattern.Upper,
                RandomFilenamePattern.Mixed
            ),
            description = R.string.filename_pattern_help_random_letters
        ),
        ExampleGroup(
            title = R.string.filename_pattern_letters_digits,
            icon = Icons.Rounded.Shuffle,
            examples = presets(
                RandomFilenamePattern.LowerNum,
                RandomFilenamePattern.UpperNum,
                RandomFilenamePattern.Alnum
            ),
            description = R.string.filename_pattern_mixed_groups
        ),
        ExampleGroup(
            title = R.string.filename_pattern_number_bases,
            icon = Icons.Rounded.Functions,
            examples = presets(
                RandomFilenamePattern.Binary,
                RandomFilenamePattern.Hex,
                RandomFilenamePattern.HexUpper
            ),
            description = R.string.filename_pattern_help_number_bases
        ),
        ExampleGroup(
            title = R.string.filename_pattern_encoding_base58,
            icon = Icons.Outlined.DataObject,
            examples = presets(RandomFilenamePattern.Base58),
            description = R.string.filename_pattern_base58_characters
        ),
        ExampleGroup(
            title = R.string.filename_pattern_custom_alphabet,
            icon = Icons.Outlined.Keyboard,
            examples = listOf(example("$Rand{8:[АБВ234]}")),
            description = R.string.filename_pattern_custom_characters
        )
    )
}

private fun dateExamples(dateFormatOnly: Boolean): List<ExampleGroup> {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val deviceZone = TimeZone.getDefault()
    val sample = GregorianCalendar(2025, Calendar.MARCH, 8, 0, 5, 9).apply {
        set(Calendar.MILLISECOND, 123)
    }.time
    val yearEnd = GregorianCalendar(2018, Calendar.DECEMBER, 31).time
    val kathmandu = TimeZone.getTimeZone("Asia/Kathmandu")

    fun format(pattern: String, date: Date, zone: TimeZone = deviceZone): String {
        formatter.applyPattern(pattern)
        formatter.timeZone = zone
        return formatter.format(date)
    }

    fun example(
        pattern: String,
        date: Date = sample,
        zone: TimeZone = deviceZone,
        uppercase: Boolean = false
    ): Pair<String, String> {
        val result = format(pattern, date, zone)
        if (dateFormatOnly) return pattern to result
        val token = if (uppercase) FilenamePattern.DateUpper else Date
        return "$token{$pattern}" to if (uppercase) result.uppercase() else result
    }

    fun examples(vararg patterns: String) = patterns.map { example(it) }

    val groups = listOf(
        ExampleGroup(
            title = R.string.filename_pattern_calendar_fields,
            icon = Icons.Rounded.DateRange,
            examples = buildList {
                add(example("yyyy-MM-dd"))
                add(example(if (dateFormatOnly) "dd.MM.yyyy HH:mm:ss" else "yyyyMMdd_HHmmss"))
                if (!dateFormatOnly) add(example("d MMMM", uppercase = true))
            },
            description = if (dateFormatOnly) {
                R.string.filename_pattern_help_date_watermark
            } else R.string.filename_pattern_help_date
        ),
        ExampleGroup(
            title = R.string.filename_pattern_date_years,
            icon = Icons.Rounded.History,
            examples = examples("G y", "yy", "yyyy"),
            description = R.string.filename_pattern_help_date_years
        ),
        ExampleGroup(
            title = R.string.filename_pattern_date_months,
            icon = Icons.Rounded.CalendarMonth,
            examples = examples("M", "MM", "d MMM", "d MMMM", "MMMMM", "LLL", "LLLL", "LLLLL"),
            description = R.string.filename_pattern_help_date_months
        ),
        ExampleGroup(
            title = R.string.filename_pattern_date_days,
            icon = Icons.Outlined.Event,
            examples = examples("d", "dd", "D", "DDD", "F"),
            description = R.string.filename_pattern_help_date_days
        ),
        ExampleGroup(
            title = R.string.filename_pattern_date_weekdays,
            icon = Icons.Outlined.EditCalendar,
            examples = examples("EEE", "EEEE", "EEEEE", "ccc", "cccc", "ccccc", "u"),
            description = R.string.filename_pattern_help_date_weekdays
        ),
        ExampleGroup(
            title = R.string.filename_pattern_date_weeks,
            icon = Icons.Outlined.ViewWeek,
            examples = listOf("yyyy-MM-dd", "YYYY-'W'ww-u", "w", "W").map { example(it, yearEnd) },
            description = R.string.filename_pattern_help_date_weeks
        ),
        ExampleGroup(
            title = R.string.time,
            icon = Icons.Outlined.AccessTime,
            examples = if (dateFormatOnly) {
                examples("HH:mm", "kk:mm", "K:mm a", "h:mm a")
            } else {
                examples("HH-mm", "kk-mm", "K-mm a", "h-mm a")
            },
            description = R.string.filename_pattern_help_date_time
        ),
        ExampleGroup(
            title = R.string.filename_pattern_date_seconds,
            icon = Icons.Outlined.Timer,
            examples = examples("m", "mm", "s", "ss", "ss.S", "ss.SS", "ss.SSS", "ss.SSSS"),
            description = R.string.filename_pattern_help_date_seconds
        ),
        ExampleGroup(
            title = R.string.filename_pattern_date_zone,
            icon = Icons.Rounded.Language,
            examples = listOf("z", "zzzz", "Z", "ZZZZ", "ZZZZZ").map {
                example(
                    it,
                    zone = kathmandu
                )
            },
            description = R.string.filename_pattern_help_date_zone
        ),
        ExampleGroup(
            title = R.string.filename_pattern_date_iso_zone,
            icon = Icons.Rounded.Public,
            examples = listOf("X", "XX", "XXX").map { example(it, zone = kathmandu) },
            description = R.string.filename_pattern_help_date_iso_zone
        ),
        ExampleGroup(
            title = R.string.filename_pattern_literals,
            icon = Icons.Rounded.Code,
            examples = examples(
                if (dateFormatOnly) "yyyy-MM-dd'T'HH:mm:ss" else "yyyy-MM-dd'T'HH-mm-ss",
                "'IMG_'yyyyMMdd",
                "h 'o''clock' a"
            ),
            description = R.string.filename_pattern_help_date_literals
        )
    )

    if (dateFormatOnly) return groups
    return groups + ExampleGroup(
        title = R.string.filename_pattern_default_date,
        icon = Icons.Outlined.AccessTime,
        examples = listOf(Date.value to format("yyyy-MM-dd_HH-mm-ss", sample)),
        description = R.string.filename_pattern_help_default_date
    )
}