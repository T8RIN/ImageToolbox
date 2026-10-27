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

import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import java.time.DateTimeException
import java.time.LocalDate
import java.util.Locale

internal class AssistantGalleryInterpreter(private val today: LocalDate = LocalDate.now()) {
    fun interpret(
        question: String,
        catalog: AssistantGalleryCatalog,
        current: AssistantGalleryQuery? = null,
        hasAttachments: Boolean = false
    ): AssistantGalleryCommand = try {
        parse(question, catalog, current, hasAttachments)
    } catch (failure: GalleryInterpretationException) {
        AssistantGalleryCommand(
            AssistantGalleryCommand.Action.Clarify,
            clarification = failure.reason
        )
    } catch (_: DateTimeException) {
        AssistantGalleryCommand(
            AssistantGalleryCommand.Action.Clarify,
            clarification = AssistantGalleryCommand.Clarification.Date
        )
    } catch (_: IllegalArgumentException) {
        AssistantGalleryCommand(AssistantGalleryCommand.Action.Clarify)
    }

    private fun parse(
        question: String,
        catalog: AssistantGalleryCatalog,
        current: AssistantGalleryQuery?,
        hasAttachments: Boolean
    ): AssistantGalleryCommand {
        if (hasAttachments || galleryHelpQuestion.containsMatchIn(question)) return AssistantGalleryCommand(
            AssistantGalleryCommand.Action.Help
        )
        val commandPrefix = galleryCommandPrefix.find(question)
        val commandText =
            commandPrefix?.let { question.substring(it.range.last + 1).trim() } ?: question.trim()
        if (nextPage.matches(question.trim()) || nextPage.matches(commandText)) return AssistantGalleryCommand(
            if (current == null) AssistantGalleryCommand.Action.Clarify else AssistantGalleryCommand.Action.More,
            clarification = AssistantGalleryCommand.Clarification.NoSearch
        )
        val spans = mutableListOf<IntRange>()
        commandPrefix?.range?.let(spans::add)
        val clear = clearFilters.findAll(question).toList()
        spans.addAll(clear.map { it.range })
        fun consumed(range: IntRange) =
            spans.any { it.first <= range.last && range.first <= it.last }

        fun literal(marker: Regex, known: List<String>): String? {
            if (marker.findAll(question)
                    .count() > 1
            ) rejectGalleryCondition(AssistantGalleryCommand.Clarification.Unsupported)
            val match = marker.find(question) ?: return null
            val start = match.range.last + 1
            val tail = question.substring(start).trimStart()
            val offset = question.length - tail.length
            val quoted = quotedValue.find(tail)
            val name =
                if (quoted != null) quoted.groupValues[1].ifBlank { quoted.groupValues[2] } else {
                    known.sortedByDescending(String::length).firstOrNull {
                        tail.startsWith(
                            it,
                            true
                        ) && (tail.length == it.length || !tail[it.length].isLetterOrDigit())
                    }
                        ?: tail.take(nextCondition.find(tail)?.range?.first ?: tail.length).trim()
                            .trimEnd('?', '!', '.', ',')
                }
            if (name.isBlank()) rejectGalleryCondition(AssistantGalleryCommand.Clarification.Literal)
            val length = quoted?.value?.length ?: name.length
            spans.add(match.range.first until offset + length)
            return name
        }

        var filename =
            literal(filenameMarker, emptyList()) ?: filenameLiteral.findAll(question).firstOrNull()
                ?.let {
                    spans.add(it.range)
                    it.value
                }
        val album = literal(albumMarker, catalog.albums)
        if (filename == null) {
            val formats = word.findAll(question)
                .filter { !consumed(it.range) && it.value.lowercase(Locale.ROOT) in extensions }
                .toList()
            if (formats.size > 1) rejectGalleryCondition(AssistantGalleryCommand.Clarification.Unsupported)
            formats.singleOrNull()?.let {
                filename = "*.${it.value.lowercase(Locale.ROOT)}"
                spans.add(it.range)
            }
        }
        val dates = try {
            parseGalleryDates(question, today, spans)
        } catch (_: IllegalArgumentException) {
            rejectGalleryCondition(AssistantGalleryCommand.Clarification.Date)
        }
        spans.addAll(dates?.spans.orEmpty())
        val names = mutableListOf<String>()
        val words = word.findAll(question).toList()
        catalog.people.sortedByDescending(String::length).forEach { name ->
            val parts = word.findAll(name).map { nameRoot(it.value) }.toList()
            if (parts.isEmpty()) return@forEach
            words.windowed(parts.size).forEach { candidate ->
                if (candidate.any { consumed(it.range) }) return@forEach
                if (candidate.map { nameRoot(it.value) } == parts) {
                    val matchingNames = catalog.people.distinct().filter { person ->
                        word.findAll(person).map { nameRoot(it.value) }.toList() == parts
                    }
                    val literalName = question.substring(
                        candidate.first().range.first,
                        candidate.last().range.last + 1
                    )
                    val exact = matchingNames.firstOrNull { it.equals(literalName, true) }
                    if (exact != null && exact != name) return@forEach
                    if (exact == null && matchingNames.size > 1) rejectGalleryCondition(
                        AssistantGalleryCommand.Clarification.Person
                    )
                    names.add(name)
                    spans.add(candidate.first().range.first..candidate.last().range.last)
                }
            }
        }
        val remaining = question.toCharArray()
        spans.forEach { range ->
            range.forEach {
                if (it in remaining.indices) remaining[it] = ' '
            }
        }
        val residual = String(remaining)
        if (unsupportedNegation.containsMatchIn(residual) || unsupportedFilter.containsMatchIn(
                residual
            )
        ) rejectGalleryCondition(AssistantGalleryCommand.Clarification.Unsupported)
        val remainingWords = word.findAll(residual).toList()
        var first = 0
        var last = remainingWords.lastIndex
        while (first <= last && galleryWords.matches(remainingWords[first].value)) first++
        while (last >= first && trailingWords.matches(remainingWords[last].value)) last--
        val content = if (first <= last) residual.substring(
            remainingWords[first].range.first,
            remainingWords[last].range.last + 1
        )
            .replace(Regex("\\s+"), " ").trim() else null
        val hasFilters =
            names.isNotEmpty() || dates != null || album != null || filename != null || clear.isNotEmpty()
        val explicitNarrow =
            galleryReference.containsMatchIn(question) || names.isNotEmpty() && galleryAddition.containsMatchIn(
                question
            )
        val update =
            current != null && (explicitNarrow || updateRequest.containsMatchIn(question) || !galleryVerb.containsMatchIn(
                question
            ) && hasFilters)
        if (!update && explicitNarrow) return AssistantGalleryCommand(
            AssistantGalleryCommand.Action.Clarify,
            clarification = AssistantGalleryCommand.Clarification.NoSearch
        )
        if (!update && !galleryVerb.containsMatchIn(question) && !(hasFilters && locationQuestion.containsMatchIn(
                question
            ))
        ) {
            return AssistantGalleryCommand(AssistantGalleryCommand.Action.Help)
        }
        if (!hasFilters && !galleryVerb.containsMatchIn(question) && !explicitNarrow) return AssistantGalleryCommand(
            AssistantGalleryCommand.Action.Help
        )
        var query = AssistantGalleryQuery(
            people = names.distinct(),
            from = dates?.from?.toString(),
            to = dates?.to?.toString(),
            album = album,
            filename = filename
        ).normalized()
        val mode = when {
            !update -> AssistantGallerySearch.Mode.New
            explicitNarrow -> AssistantGallerySearch.Mode.Narrow
            else -> AssistantGallerySearch.Mode.Update
        }
        if (update) {
            val previous = checkNotNull(current)
            query = previous.copy(
                people = if (mode == AssistantGallerySearch.Mode.Update && names.isNotEmpty()) names.distinct() else (previous.people + names).distinct(),
                from = if (dates != null) dates.from?.toString() else previous.from,
                to = if (dates != null) dates.to?.toString() else previous.to,
                album = album ?: previous.album,
                filename = filename ?: previous.filename
            )
            clear.forEach { match ->
                val field = match.groupValues[1].lowercase(Locale.ROOT)
                query = when {
                    field.startsWith("дат") || field == "date" -> query.copy(from = null, to = null)
                    field.startsWith("альбом") || field == "album" -> query.copy(album = null)
                    field.startsWith("люд") || field.startsWith("имен") || field == "people" -> query.copy(
                        people = emptyList()
                    )

                    else -> query.copy(filename = null)
                }
            }
            query = query.normalized()
        }
        if (content == null && query == AssistantGalleryQuery() && clear.isEmpty() && !allMedia.containsMatchIn(
                question
            ) && !mediaNoun.containsMatchIn(question)
        ) return AssistantGalleryCommand(AssistantGalleryCommand.Action.Clarify)
        if (content != null) {
            query = query.copy(
                semantic = if (mode == AssistantGallerySearch.Mode.Narrow && query.semantic != null) "${query.semantic}, $content" else content
            ).normalized()
        }
        return AssistantGalleryCommand(
            if (update) AssistantGalleryCommand.Action.Refine else AssistantGalleryCommand.Action.Search,
            query,
            mode
        )
    }
}

private fun nameRoot(value: String): String =
    value.lowercase(Locale.ROOT).replace('ё', 'е').replace(nameEnding, "").removeSuffix("ь")
        .removeSuffix("й").ifBlank { value.lowercase(Locale.ROOT) }

private val extensions = ImageFormat.entries.map { it.extension }.toSet()
private val nameEnding = Regex("(?:ами|ями|ого|ему|ому|ой|ий|а|я|у|ю|ы|и|е|ом|ем)$")
private val word = Regex("""[\p{L}\p{N}]+""")
private val quotedValue = Regex("""^(?:"([^"]+)"|«([^»]+)»)""")
private val filenameLiteral =
    Regex("""(?<![\p{L}\p{N}])(?:[\p{L}\p{N}_*-]+\.)+[a-zA-Z]{2,8}(?![\p{L}\p{N}])""")
private val allMedia = Regex("""(?<!\p{L})(?:все|all)(?!\p{L})""", RegexOption.IGNORE_CASE)
private val filenameMarker = Regex(
    """(?<!\p{L})(?:файл[а-я]*|filename|file)(?:\s+|$)(?:с\s+именем\s+|named\s+)?""",
    RegexOption.IGNORE_CASE
)
private val albumMarker =
    Regex("""(?<!\p{L})(?:альбом[а-я]*|album)(?:\s+|$)""", RegexOption.IGNORE_CASE)
private val nextCondition = Regex(
    """(?:^|\s+)(?:за|с|по|до|from|in|during|between|из\s+альбом[а-я]*|альбом[а-я]*|album|файл[а-я]*|filename|file)(?=\s)""",
    RegexOption.IGNORE_CASE
)
private val clearFilters = Regex(
    """(?<!\p{L})(?:убери|сбрось|очисти|remove|clear|reset)\s+(?:фильтр\s+по\s+|filter\s+by\s+)?(дат[а-я]*|альбом[а-я]*|люд[а-я]*|имен[а-я]*|файл[а-я]*|date|album|people|filename)(?!\p{L})""",
    RegexOption.IGNORE_CASE
)
private val unsupportedNegation = Regex(
    """(?<!\p{L})(?:без|кроме|не|или|without|except|not|or)(?!\p{L})""",
    RegexOption.IGNORE_CASE
)
internal val galleryHelpQuestion = Regex(
    """^(?:а\s+)?(?:как|how|зачем|почему|why)(?!\p{L})|^(?:(?:а|and)\s+)?(?:(?:пожалуйста|please)[,\s]+)?(?:покажи|show)\s+(?:(?:мне|нам|me|us|пожалуйста|please)[,\s]+)*(?:как|how|справк[а-я]*|инструмент[а-я]*|настройк[а-я]*|формат[а-я]*|поддерживаем[а-я]*|возможност[а-я]*|help|tools?|settings?|formats?|supported|features?)(?!\p{L})""",
    RegexOption.IGNORE_CASE
)
private val nextPage = Regex(
    """(?:(?:покажи|show)\s+)?(?:ещ[её]|далее|дальше|more|next(?:\s+page)?)[.!?]*""",
    RegexOption.IGNORE_CASE
)
private val updateRequest = Regex(
    """^(?:а\s+)?(?:теперь|only|now|только|остав[а-я]*|убери|сбрось|очисти|remove|clear|reset)(?!\p{L})""",
    RegexOption.IGNORE_CASE
)
private val locationQuestion =
    Regex("""(?<!\p{L})(?:где|where)(?!\p{L})""", RegexOption.IGNORE_CASE)
private val galleryWords = Regex(
    """(?<!\p{L})(?:пожалуйста|please|все|all|най[дт][а-я]*|(?:по)?ищ[а-я]*|покаж[а-я]*|показать|остав[а-я]*|find|search|show|look|for|where|is|are|photos?|pictures?|images?|files?|фото|фотографи[а-я]*|изображени[а-я]*|сним[а-я]*|где|а|и|в|на|из|с|со|за|по|до|к|которые|которых|этих|эти|них|нем|нём|ней|среди|найденных|он|она|они|его|ее|её|их|вместе|только|теперь|год[а-я]*|of|these|those|from|among|found|and|together|only|now|he|she|they|him|her|them|it|the|a|an|at|in|on|with|to|year)(?!\p{L})""",
    RegexOption.IGNORE_CASE
)
internal val galleryReference = Regex(
    """^(?:(?:а|and)\s+)?(?:(?:теперь|now)\s+)?(?:из\s+(?:этих|них)|среди\s+найденных|где\s+(?:он|она|они)|of\s+these|from\s+these|among\s+(?:these|found)|where\s+is\s+(?:he|she))(?!\p{L})""",
    RegexOption.IGNORE_CASE
)
internal val galleryVerb = Regex(
    """(?<!\p{L})(?:най[дт][а-я]*|(?:по)?ищ[а-я]*|покаж[а-я]*|показать|find|search|show|look\s+for)(?!\p{L})""",
    RegexOption.IGNORE_CASE
)

private class GalleryInterpretationException(val reason: AssistantGalleryCommand.Clarification) :
    IllegalArgumentException()

private fun rejectGalleryCondition(reason: AssistantGalleryCommand.Clarification): Nothing =
    throw GalleryInterpretationException(reason)

private val unsupportedFilter = Regex(
    """(?<!\p{L})(?:GPS|OCR|координат[а-я]*|разрешени[а-я]*|мегабайт[а-я]*|file\s+size|с\s+текстом|with\s+(?:the\s+)?text)(?!\p{L})|\d+\s+(?:фотографи[а-я]*|фото|сним[а-я]*|photos?|pictures?|images?)(?!\p{L})""",
    RegexOption.IGNORE_CASE
)

private val trailingWords = Regex(
    """(?:а|и|в|на|из|с|со|за|по|до|от|к|этих|эти|них|среди|найденных|вместе|только|год[а-я]*|and|of|from|to|in|on|at|with|these|those|among|found|together|only|year)""",
    RegexOption.IGNORE_CASE
)

private val mediaNoun = Regex(
    """(?<!\p{L})(?:фото|фотографи[а-я]*|сним[а-я]*|изображени[а-я]*|photos?|pictures?|images?)(?!\p{L})""",
    RegexOption.IGNORE_CASE
)

internal val galleryAddition =
    Regex("""^(?:а\s+)?(?:и|ещ[её]|также|добавь|also|add|and)(?!\p{L})""", RegexOption.IGNORE_CASE)

private val galleryCommandPrefix = Regex(
    """^(?:(?:пожалуйста|please)[,\s]+)?(?:${galleryVerb.pattern})[,\s]+(?:(?:пожалуйста|please)[,\s]+)?(?:(?:мне|нам|me|us|для\s+меня|для\s+нас|for\s+(?:me|us))(?=[,\s]|$)[,\s]*)?""",
    RegexOption.IGNORE_CASE
)

internal fun needsGalleryRouting(question: String, current: AssistantGalleryQuery?): Boolean =
    !galleryHelpQuestion.containsMatchIn(question) && (galleryVerb.containsMatchIn(question) || locationQuestion.containsMatchIn(
        question
    ) ||
            current != null && (galleryReference.containsMatchIn(question) || galleryAddition.containsMatchIn(
        question
    ) || galleryFollowUp.containsMatchIn(question)))

private val galleryFollowUp = Regex(
    """^(?:а\s+)?(?:ещ[её]|далее|дальше|только|теперь|за|в|с|убери|сбрось|очисти|more|next|only|now|in|from|remove|clear|reset)(?!\p{L})""",
    RegexOption.IGNORE_CASE
)
