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

package com.t8rin.imagetoolbox.feature.assistant.domain

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale

internal data class AssistantGalleryDates(
    val from: LocalDate?,
    val to: LocalDate?,
    val spans: List<IntRange>
)

internal fun parseGalleryDates(
    text: String,
    today: LocalDate,
    excluded: List<IntRange> = emptyList()
): AssistantGalleryDates? {
    fun available(range: IntRange) =
        excluded.none { it.first <= range.last && range.first <= it.last }

    val dates = mutableListOf<AssistantGalleryDates>()
    calendarDate.findAll(text).filter { available(it.range) }.forEach { match ->
        val date = if ('-' in match.value) LocalDate.parse(match.value) else {
            LocalDate.parse(
                match.value,
                DateTimeFormatter.ofPattern("d.M.uuuu").withResolverStyle(ResolverStyle.STRICT)
            )
        }
        dates.add(AssistantGalleryDates(date, date, listOf(match.range)))
    }
    val year = calendarYear.findAll(text).firstOrNull { available(it.range) }?.value?.toInt()
    calendarMonth.findAll(text).filter { match ->
        available(match.range) && dates.none { date -> date.spans.any { it.first <= match.range.last && match.range.first <= it.last } }
    }.forEach { match ->
        val month = checkNotNull(monthNames[match.value.lowercase(Locale.ROOT).replace('ё', 'е')])
        val followingYear =
            Regex("""^\s+(\d{4})(?!\d)""").find(text.substring(match.range.last + 1))
        val precedingDay =
            Regex("""(?<!\d)(\d{1,2})\s*$""").find(text.substring(0, match.range.first))
        val prefix = text.substring(0, match.range.first)
        val adjective = match.value.contains("ск", ignoreCase = true)
        val connected = dates.lastOrNull()?.let { previous ->
            calendarConnection.matches(
                text.substring(
                    previous.spans.last().last + 1,
                    match.range.first
                ).trim()
            )
        } == true
        val beforePhotos = Regex(
            """^\s+(?:photos?|pictures?|images?)(?!\p{L})""",
            RegexOption.IGNORE_CASE
        ).containsMatchIn(text.substring(match.range.last + 1))
        if (precedingDay == null && followingYear == null && !adjective && !connected && !beforePhotos && !monthContext.containsMatchIn(
                prefix
            ) && !text.trim().equals(match.value, true)
        ) return@forEach
        val value =
            YearMonth.of(followingYear?.groupValues?.get(1)?.toInt() ?: year ?: today.year, month)
        val from = precedingDay?.groupValues?.get(1)?.toInt()?.let(value::atDay) ?: value.atDay(1)
        val to = if (precedingDay != null) from else value.atEndOfMonth()
        val end = followingYear?.let { match.range.last + it.value.length } ?: match.range.last
        dates.add(
            AssistantGalleryDates(
                from,
                to,
                listOf((precedingDay?.range?.first ?: match.range.first)..end)
            )
        )
    }
    if (dates.isNotEmpty()) {
        val ordered = dates.sortedBy { it.spans.first().first }
        require(ordered.size <= 2) { "Too many date ranges" }
        if (ordered.size == 2) {
            val between =
                text.substring(ordered[0].spans.last().last + 1, ordered[1].spans.first().first)
                    .trim()
            require(dateRangeConnector.matches(between)) { "Unsupported date expression" }
        }
        val from = ordered.first().from
        val to = ordered.last().to
        require(from == null || to == null || from <= to) { "Reversed date range" }
        val spans =
            ordered.flatMap { it.spans } + calendarYear.findAll(text).filter { available(it.range) }
                .map { it.range }
        val result = if (ordered.size == 2) {
            val first = ordered.first().spans.first().first
            val prefix = dateRangeStart.find(text.substring(0, first))?.range?.first ?: first
            AssistantGalleryDates(
                from,
                to,
                spans + listOf(prefix..ordered.last().spans.last().last)
            )
        } else AssistantGalleryDates(from, to, spans)
        return if (ordered.size == 1) applyGalleryDateBoundary(result, text) else result
    }
    relativeDate.find(text)?.takeIf { available(it.range) }?.let { match ->
        val value = match.value.lowercase(Locale.ROOT).replace('ё', 'е')
        val previous = value.startsWith("прошл") || value.startsWith("last")
        val range = when {
            value == "today" || value == "сегодня" -> today to today
            value == "yesterday" || value == "вчера" -> today.minusDays(1).let { it to it }
            value == "позавчера" -> today.minusDays(2).let { it to it }
            "week" in value || "недел" in value -> {
                val start = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                    .minusWeeks(if (previous) 1 else 0)
                start to if (previous) start.plusDays(6) else today
            }

            "month" in value || "месяц" in value -> {
                val month = YearMonth.from(today).minusMonths(if (previous) 1 else 0)
                month.atDay(1) to if (previous) month.atEndOfMonth() else today
            }

            else -> {
                val yearStart = today.withDayOfYear(1).minusYears(if (previous) 1 else 0)
                yearStart to if (previous) yearStart.withMonth(12).withDayOfMonth(31) else today
            }
        }
        return applyGalleryDateBoundary(
            AssistantGalleryDates(
                range.first,
                range.second,
                listOf(match.range)
            ), text
        )
    }
    return calendarYear.findAll(text).firstOrNull { available(it.range) }?.let { match ->
        val first = LocalDate.of(match.value.toInt(), 1, 1)
        applyGalleryDateBoundary(
            AssistantGalleryDates(
                first,
                first.withMonth(12).withDayOfMonth(31),
                listOf(match.range)
            ), text
        )
    }
}

private val monthNames = buildMap {
    listOf(Locale.ENGLISH, Locale.forLanguageTag("ru")).forEach { locale ->
        Month.values().forEach { month ->
            listOf(TextStyle.FULL, TextStyle.FULL_STANDALONE).forEach { style ->
                val name = month.getDisplayName(style, locale).lowercase(locale).replace('ё', 'е')
                put(name, month)
                if (locale.language == "ru") {
                    val standalone = month.getDisplayName(TextStyle.FULL_STANDALONE, locale)
                    val stem = standalone.removeSuffix("ь").removeSuffix("й")
                    val endings = if (standalone.endsWith('ь') || standalone.endsWith('й')) listOf(
                        "ь",
                        "й",
                        "я",
                        "ю",
                        "е",
                        "ем"
                    ) else listOf("", "а", "у", "е", "ом")
                    endings.forEach { put(stem + it, month) }
                    val adjectiveRoots = listOf(standalone, standalone.removeSuffix("ь")).flatMap {
                        listOf(
                            it + "ск",
                            it + "овск"
                        )
                    }
                    adjectiveRoots.forEach { root ->
                        listOf(
                            "ий",
                            "ая",
                            "ое",
                            "ые",
                            "ие",
                            "ую",
                            "ой",
                            "ом",
                            "ого",
                            "ому",
                            "ых",
                            "ыми",
                            "им",
                            "ими"
                        ).forEach { put(root + it, month) }
                    }
                }
            }
        }
    }
}
private val calendarMonth = Regex(
    """(?<!\p{L})(?:${
        monthNames.keys.sortedByDescending(String::length)
            .joinToString("|", transform = Regex::escape)
    })(?!\p{L})""", RegexOption.IGNORE_CASE
)
private val calendarDate =
    Regex("""(?<![\p{L}\d])(?:\d{4}-\d{2}-\d{2}|\d{1,2}\.\d{1,2}\.\d{4})(?!\d)""")
private val calendarYear = Regex("""(?<![\p{L}\d])[12]\d{3}(?!\d)""")
private val dateRangeConnector =
    Regex("""(?:[-–—]|по|до|to|through|until|and|и)""", RegexOption.IGNORE_CASE)
private val relativeDate = Regex(
    """(?<!\p{L})(?:сегодня|позавчера|вчера|today|yesterday|(?:эт[а-я]*|прошл[а-я]*)\s+(?:недел[а-я]*|месяц[а-я]*|год[а-я]*)|(?:this|last)\s+(?:week|month|year))(?!\p{L})""",
    RegexOption.IGNORE_CASE
)

private val monthContext = Regex(
    """(?:^|\s)(?:за|в|с|до|по|от|in|from|for|during|before|after|since|between|through|until|only|только|теперь|после|перед)\s*$""",
    RegexOption.IGNORE_CASE
)

private fun applyGalleryDateBoundary(
    dates: AssistantGalleryDates,
    text: String
): AssistantGalleryDates {
    val start = dates.spans.minOf { it.first }
    val boundary = dateBoundary.find(text.substring(0, start)) ?: return dates
    val spans = dates.spans + listOf(boundary.range.first until start)
    return when (boundary.value.trim().lowercase(Locale.ROOT)) {
        "после", "after" -> dates.copy(
            from = checkNotNull(dates.to).plusDays(1),
            to = null,
            spans = spans
        )

        "до", "перед", "before", "until" -> dates.copy(
            from = null,
            to = checkNotNull(dates.from).minusDays(1),
            spans = spans
        )

        "по", "through" -> dates.copy(from = null, spans = spans)
        else -> dates.copy(to = null, spans = spans)
    }
}

private val dateBoundary = Regex(
    """(?<!\p{L})(?:после|перед|с|от|до|по|after|before|since|from|until|through)\s*$""",
    RegexOption.IGNORE_CASE
)

private val dateRangeStart =
    Regex("""(?<!\p{L})(?:с|от|from|between)\s*$""", RegexOption.IGNORE_CASE)

private val calendarConnection =
    Regex("""(?:[-–—,]|по|до|to|through|until|and|и|или|or)""", RegexOption.IGNORE_CASE)
