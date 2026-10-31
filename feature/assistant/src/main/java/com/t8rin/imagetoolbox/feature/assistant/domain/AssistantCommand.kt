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

import com.t8rin.imagetoolbox.core.domain.model.AssistantModel
import com.t8rin.neural_tools.search.ClipImageSearch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters

internal object AssistantCommand {
    private const val MAX_SCENE_LENGTH = ClipImageSearch.TEXT_CONTEXT_LENGTH - 2

    sealed interface Action {
        data class SearchPhotos(
            val query: AssistantGalleryQuery,
            val mode: AssistantGallerySearch.Mode
        ) : Action

        data class Answer(val text: String) : Action
        data object MorePhotos : Action
    }

    fun parse(
        text: String,
        current: AssistantGalleryQuery? = null,
        question: String = "",
        referenceDate: LocalDate = LocalDate.now()
    ): Action {
        require(text.length <= 6000)
        val value = Json.parseToJsonElement(text).jsonObject
        return when (value.getValue("action").jsonPrimitive.content) {
            "more" -> {
                require(value.keys == setOf("action")); Action.MorePhotos
            }

            "ask" -> {
                require(value.keys == setOf("action", "question"))
                Action.Answer(value.getValue("question").jsonPrimitive.content.also { require(it.isNotBlank()) })
            }

            "all" -> {
                require(value.keys == setOf("action"))
                Action.SearchPhotos(AssistantGalleryQuery(), AssistantGallerySearch.Mode.New)
            }

            "search" -> {
                require(value.keys == setOf("action", "mode", "query"))
                val mode = when (value.getValue("mode").jsonPrimitive.content) {
                    "new" -> AssistantGallerySearch.Mode.New
                    "update" -> AssistantGallerySearch.Mode.Update
                    "narrow" -> AssistantGallerySearch.Mode.Narrow
                    else -> error("Unknown photo search mode")
                }
                val raw = value.getValue("query").jsonObject
                require(raw.keys.all {
                    it in setOf(
                        "scene",
                        "people",
                        "clear",
                        "date",
                        "album",
                        "filename"
                    )
                })
                val personChanges = raw["people"]?.takeUnless { it == JsonNull }
                require(personChanges == null || personChanges is JsonObject)
                require(mode == AssistantGallerySearch.Mode.New || current != null)
                val (from, to) = dates(raw["date"], referenceDate)
                val sceneValue = raw["scene"]?.jsonPrimitive
                require(sceneValue == null || sceneValue == JsonNull || sceneValue.isString || sceneValue.booleanOrNull != null)
                val filename = raw["filename"]?.jsonPrimitive?.contentOrNull?.takeIf {
                    question.isEmpty() || question.contains(
                        it,
                        true
                    ) || mode != AssistantGallerySearch.Mode.New && it == current?.filename
                }
                val rawAlbum = raw["album"]?.takeUnless { it == JsonNull }
                val albumChanges = (rawAlbum as? JsonObject)?.let {
                    Json.decodeFromString<Map<String, AssistantPhotoPlan.PersonRole>>(it.toString())
                }.orEmpty()
                require(albumChanges.values.none { it == AssistantPhotoPlan.PersonRole.Exclude })
                val includedAlbums =
                    albumChanges.filterValues { it == AssistantPhotoPlan.PersonRole.Include }.keys
                require(includedAlbums.size <= 1)
                val album =
                    (rawAlbum as? JsonPrimitive)?.contentOrNull ?: includedAlbums.singleOrNull()
                val removedAlbum = albumChanges.any { (name, role) ->
                    role == AssistantPhotoPlan.PersonRole.Remove && name.equals(
                        current?.album,
                        true
                    )
                }
                require(
                    mode != AssistantGallerySearch.Mode.Narrow ||
                            sceneValue?.booleanOrNull == true || sceneValue?.isString == true ||
                            personChanges?.jsonObject?.values?.any { it.jsonPrimitive.content != "remove" } == true ||
                            from != null || to != null || album != null || filename != null)
                val query = AssistantPhotoPlan(
                    resetFilters = mode == AssistantGallerySearch.Mode.New,
                    withinResults = mode == AssistantGallerySearch.Mode.Narrow,
                    scene = when (sceneValue?.booleanOrNull) {
                        true -> question.also { require(it.isNotBlank() && it.length <= MAX_SCENE_LENGTH) }
                        false -> null
                        null -> sceneValue?.contentOrNull
                    },
                    people = personChanges?.let { Json.decodeFromString(it.toString()) }
                        ?: emptyMap(),
                    from = from,
                    to = to,
                    album = album,
                    filename = filename,
                    clear = raw["clear"]?.takeUnless { it == JsonNull }
                        ?.let { Json.decodeFromString<Set<AssistantPhotoPlan.Filter>>(it.toString()) }
                        .orEmpty() +
                            buildSet {
                                if (sceneValue?.isString == true) add(AssistantPhotoPlan.Filter.Scene)
                                if (removedAlbum) add(AssistantPhotoPlan.Filter.Album)
                            }
                ).normalized().gallery(current)
                val normalized = query.normalized()
                val scene = normalized.semantic?.let { description ->
                    (normalized.people + normalized.excludedPeople + listOfNotNull(
                        normalized.album,
                        normalized.filename
                    ))
                        .fold(description) { text, literal ->
                            text.replace(
                                Regex(
                                    "(?<![\\p{L}\\p{N}])${Regex.escape(literal)}(?![\\p{L}\\p{N}])",
                                    RegexOption.IGNORE_CASE
                                ), " "
                            )
                        }.replace(Regex("\\s+"), " ").trim().takeIf(String::isNotEmpty)
                }
                val desired = normalized.copy(semantic = scene)
                require(mode != AssistantGallerySearch.Mode.New || desired != AssistantGalleryQuery())
                Action.SearchPhotos(desired, mode)
            }

            else -> error("Unknown assistant action")
        }
    }

    private fun dates(value: JsonElement?, reference: LocalDate): Pair<String?, String?> {
        if (value == null || value == JsonNull) return null to null
        if (value is JsonPrimitive) return value.contentOrNull.let { it to it }
        val fields = value.jsonObject
        return when (fields.keys) {
            setOf("unit", "value") -> {
                val raw = fields.getValue("value").jsonPrimitive.content
                val period = when (fields.getValue("unit").jsonPrimitive.content) {
                    "month" -> YearMonth.parse(raw.take(7)).toString()
                    "year" -> Year.parse(raw.take(4)).toString()
                    "day" -> raw
                    else -> error("Unknown date precision")
                }
                period to period
            }

            setOf("unit", "offset") -> {
                val offset = fields.getValue("offset").jsonPrimitive.content.toLong()
                when (fields.getValue("unit").jsonPrimitive.content) {
                    "day" -> reference.plusDays(offset).toString().let { it to it }
                    "week" -> reference.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                        .plusWeeks(offset).let { it.toString() to it.plusDays(6).toString() }

                    "month" -> YearMonth.from(reference).plusMonths(offset).toString()
                        .let { it to it }

                    "year" -> Year.from(reference).plusYears(offset).toString().let { it to it }
                    else -> error("Unknown relative date precision")
                }
            }

            setOf("fromOffset", "toOffset") -> {
                fun boundary(key: String) = fields.getValue(key).jsonPrimitive.contentOrNull
                    ?.toLong()?.let { reference.plusDays(it).toString() }
                boundary("fromOffset") to boundary("toOffset")
            }

            setOf("from", "to") -> fields.getValue("from").jsonPrimitive.contentOrNull to
                    fields.getValue("to").jsonPrimitive.contentOrNull

            else -> error("Unknown date interval")
        }
    }

    fun visibleText(text: String): String {
        val value = text.trimStart()
        if (value.contains("</think>")) return visibleText(
            value.substringAfterLast("</think>").trimStart()
        )
        if (value.startsWith("<think>")) return ""
        val tool = text.indexOf("<tool_call>")
        return if (tool >= 0) text.take(tool).trimEnd() else text
    }

    fun isInternalReply(text: String): Boolean {
        return text == "search" || text.startsWith("search\n") || text.startsWith("ask ") || text == "more" || runCatching {
            val value = Json.parseToJsonElement(text.trim()).jsonObject
            value.containsKey("action") || value.containsKey("mode") ||
                    value.containsKey("name") && value.containsKey("arguments") ||
                    (value.containsKey("query") || value.containsKey("english_request")) && (value.containsKey(
                "kind"
            ) || value.containsKey("search_photos") || value.containsKey("search")) ||
                    value.containsKey("findPhotos") || value.containsKey("appHelp")
        }.getOrDefault(false)
    }

    fun matching(names: List<String>, question: String, language: String? = null): List<String> {
        val inflections = language?.let { AssistantSearchIndex<Unit>(emptyList(), it) }
        val words = AssistantSearchIndex.tokens(question)
        fun match(name: String, exact: Boolean): Boolean {
            val terms = AssistantSearchIndex.tokens(name)
            return terms.isNotEmpty() && words.windowed(terms.size).any { phrase ->
                phrase.zip(terms).all { (word, term) ->
                    word == term || !exact && word.length >= term.length && AssistantSearchIndex.matches(
                        word,
                        term,
                        4,
                        0.7
                    )
                }
            }
        }
        return names.filter {
            match(it, true) || inflections?.containsTitle(
                question,
                it
            ) == true || match(it, false)
        }.distinct().take(10)
    }

    private fun albums(
        current: AssistantGalleryQuery?,
        catalog: AssistantGalleryCatalog,
        question: String,
        language: String?
    ) =
        (matching(catalog.albums, question, language) + listOfNotNull(current?.album)).distinct()

    fun encodeQuery(query: AssistantGalleryQuery): String {
        val fields =
            Json.parseToJsonElement(Json { encodeDefaults = true }.encodeToString(query)).jsonObject
        return JsonObject(
            listOf("scene", "people", "excludedPeople", "from", "to", "album", "filename")
                .associateWith { fields.getValue(if (it == "scene") "semantic" else it) }
        ).toString()
    }

    fun encodePlan(
        query: AssistantGalleryQuery,
        current: AssistantGalleryQuery?,
        question: String,
        copySource: Boolean = false
    ): String {
        val named = matching(
            query.people + query.excludedPeople + current?.people.orEmpty() + current?.excludedPeople.orEmpty(),
            question,
            "en"
        )
        val people = JsonObject(named.associateWith { name ->
            JsonPrimitive(
                when (name) {
                    in query.people -> "include"
                    in query.excludedPeople -> "exclude"
                    else -> "remove"
                }
            )
        })
        val clear = buildSet {
            if (current != null) {
                if (current.semantic != null && current.semantic != query.semantic) add(
                    AssistantPhotoPlan.Filter.Scene
                )
                if (named.isEmpty() && (current.people.isNotEmpty() || current.excludedPeople.isNotEmpty()) &&
                    query.people.isEmpty() && query.excludedPeople.isEmpty()
                ) add(AssistantPhotoPlan.Filter.People)
                if ((current.from != null || current.to != null) && query.from == null && query.to == null) add(
                    AssistantPhotoPlan.Filter.Date
                )
                if (current.album != null && query.album == null) add(AssistantPhotoPlan.Filter.Album)
                if (current.filename != null && query.filename == null) add(AssistantPhotoPlan.Filter.Filename)
            }
        }
        val fields = Json.parseToJsonElement(encodeQuery(query)).jsonObject.toMutableMap()
        val start = query.from?.let(LocalDate::parse)
        val end = query.to?.let(LocalDate::parse)
        if (start != null && start.dayOfYear == 1 && end == Year.from(start).atMonth(12)
                .atEndOfMonth()
        ) {
            val year = JsonPrimitive(Year.from(start).toString())
            fields["from"] = year
            fields["to"] = year
        } else if (start != null && start.dayOfMonth == 1 && end == YearMonth.from(start)
                .atEndOfMonth()
        ) {
            val month = JsonPrimitive(YearMonth.from(start).toString())
            fields["from"] = month
            fields["to"] = month
        }
        if (current != null) {
            val previous = Json.parseToJsonElement(encodeQuery(current)).jsonObject
            listOf("scene", "from", "to", "album", "filename").forEach { field ->
                if (fields[field] == previous[field]) fields[field] = JsonNull
            }
            if (query.from == current.from && query.to == current.to) {
                fields["from"] = JsonNull
                fields["to"] = JsonNull
            }
        }
        val date = if (fields["from"] == JsonNull && fields["to"] == JsonNull) null
        else if (fields["from"] == fields["to"]) {
            val value = fields.getValue("from").jsonPrimitive.content
            when (value.length) {
                4 -> JsonObject(
                    mapOf(
                        "unit" to JsonPrimitive("year"),
                        "value" to JsonPrimitive(value)
                    )
                )

                7 -> JsonObject(
                    mapOf(
                        "unit" to JsonPrimitive("month"),
                        "value" to JsonPrimitive(value)
                    )
                )

                else -> fields["from"]
            }
        } else JsonObject(mapOf("from" to fields.getValue("from"), "to" to fields.getValue("to")))
        val album = when {
            query.album != current?.album && query.album != null ->
                JsonObject(mapOf(query.album to JsonPrimitive("include")))

            query.album == null && current?.album != null ->
                JsonObject(mapOf(current.album to JsonPrimitive("remove")))

            else -> null
        }
        return mapOf(
            "people" to people.takeIf { it.isNotEmpty() },
            "date" to date,
            "album" to album,
            "filename" to fields["filename"],
            "clear" to (clear - AssistantPhotoPlan.Filter.Album).takeIf { it.isNotEmpty() }
                ?.let { Json.parseToJsonElement(Json.encodeToString(it)) },
            "scene" to (fields["scene"]?.takeUnless { it == JsonNull }?.let {
                if (copySource && AssistantPhotoPlan.Filter.Scene !in clear) JsonPrimitive(true) else it
            } ?: if (copySource) JsonPrimitive(false) else JsonNull)
        ).filter { (key, value) -> value != null && (key == "scene" || value != JsonNull) }
            .mapValues { requireNotNull(it.value) }.let(::JsonObject).toString()
    }

    private fun schema(
        current: AssistantGalleryQuery?,
        catalog: AssistantGalleryCatalog,
        question: String,
        language: String?,
        copySource: Boolean,
        requireFilter: Boolean
    ): String {
        val named = matching(
            catalog.people + current?.people.orEmpty() + current?.excludedPeople.orEmpty(),
            question,
            language
        )
        val availableAlbums = albums(
                current,
                catalog,
                question,
                language
            )
        val namedAlbums = matching(catalog.albums, question, language)
        val albumRoles = availableAlbums.joinToString(",") { name ->
            "${Json.encodeToString(name)}:{\"enum\":${
                Json.encodeToString(
                    if (current?.album == name) listOf(
                        "include",
                        "remove"
                    ) else listOf("include")
                )
            }}"
        }
        val album = ""","album":{"type":["object","null"],"properties":{$albumRoles},"required":${
            Json.encodeToString(namedAlbums)
        },"additionalProperties":false}"""
        val removable = buildList {
            if (current?.semantic != null) add("scene")
            if (current != null && (current.people.isNotEmpty() || current.excludedPeople.isNotEmpty())) add(
                "people"
            )
            if (current?.from != null || current?.to != null) add("date")
            if (current?.album != null) add("album")
            if (current?.filename != null) add("filename")
        }
        val clear =
            if (removable.isEmpty()) ",\"clear\":{\"type\":[\"array\",\"null\"],\"maxItems\":0}"
            else ",\"clear\":{\"type\":[\"array\",\"null\"],\"items\":{\"enum\":${
                Json.encodeToString(
                    removable
                )
            }},\"maxItems\":${removable.size}}"
        val text = """{"type":["string","null"],"minLength":1,"maxLength":200}"""
        val years = Regex("(?<!\\p{Nd})\\p{Nd}{4}(?!\\p{Nd})").findAll(question)
            .map { match -> match.value.map { it.digitToInt() }.joinToString("") }.distinct()
            .toList()
        val year = years.joinToString("|")
        val date = """{"type":["string","null"],"pattern":"^($year)(-[0-9]{2}){0,2}$"}"""
        val absolute = if (years.isEmpty()) emptyList() else buildList {
            add("""{"type":"string","pattern":"^($year)-[0-9]{2}-[0-9]{2}$"}""")
            listOf("month" to "($year)-[0-9]{2}", "year" to "($year)")
                .forEach { (unit, pattern) -> add("""{"type":"object","properties":{"unit":{"const":"$unit"},"value":{"type":"string","pattern":"^$pattern$"}},"required":["unit","value"],"additionalProperties":false}""") }
            add("""{"type":"object","properties":{"from":$date,"to":$date},"required":["from","to"],"additionalProperties":false}""")
        }
        val relative =
            """{"type":"object","properties":{"unit":{"enum":["day","week","month","year"]},"offset":{"type":"integer","minimum":-1200,"maximum":1200}},"required":["unit","offset"],"additionalProperties":false}"""
        val offset = """{"type":["integer","null"],"minimum":-365250,"maximum":365250}"""
        val relativeRange =
            """{"type":"object","properties":{"fromOffset":$offset,"toOffset":$offset},"required":["fromOffset","toOffset"],"additionalProperties":false}"""
        val sourceFits =
            question.length + current?.semantic.orEmpty().length + 2 <= MAX_SCENE_LENGTH
        val description = """{"type":"string","minLength":1,"maxLength":$MAX_SCENE_LENGTH}"""
        val descriptions =
            description.takeIf { !copySource || !sourceFits || current?.semantic != null }
        val flags = if (copySource && sourceFits) """{"enum":[true,false,null]}"""
        else """{"enum":[null]}"""
        val scene = """{"anyOf":[${listOfNotNull(descriptions, flags).joinToString(",")}]}"""
        val roles = named.joinToString(",") { name ->
            "${Json.encodeToString(name)}:{\"enum\":${
                Json.encodeToString(
                    if (name in current?.people.orEmpty() || name in current?.excludedPeople.orEmpty()) listOf(
                        "include",
                        "exclude",
                        "remove"
                    ) else listOf("include", "exclude")
                )
            }}"
        }
        val peopleTypes = if (named.isEmpty()) listOf("object", "null") else listOf("object")
        val people =
            """{"type":${Json.encodeToString(peopleTypes)},"properties":{$roles},"required":${
                Json.encodeToString(named)
            },"additionalProperties":false}"""
        val required = buildList {
            add("scene")
            if (named.isNotEmpty()) add("people")
            if (namedAlbums.isNotEmpty()) add("album")
        }
        val base = """{"type":"object","properties":{
            "people":$people,"date":{"anyOf":[${
            (listOf(
                """{"type":"null"}""",
                relative,
                relativeRange
            ) + absolute).joinToString(",")
        }]}$album,"filename":$text$clear,"scene":$scene
        },"required":${Json.encodeToString(required)},"additionalProperties":false}""".replace(
            "\n",
            ""
        )
        if (!requireFilter) return base
        val root = Json.parseToJsonElement(base).jsonObject
        val properties = root.getValue("properties").jsonObject
        val requiredFields =
            Json.decodeFromString<List<String>>(root.getValue("required").toString())

        fun selected(field: String, constraint: String): JsonObject = JsonObject(
            root + mapOf(
                "properties" to JsonObject(properties + (field to Json.parseToJsonElement(constraint))),
                "required" to Json.parseToJsonElement(Json.encodeToString((requiredFields + field).distinct()))
            )
        )

        val selectors = buildList {
            val content = listOfNotNull(
                descriptions,
                """{"const":true}""".takeIf { copySource && sourceFits })
            add(selected("scene", """{"anyOf":[${content.joinToString(",")}]}"""))
            if (named.isNotEmpty()) add(
                selected(
                    "people", JsonObject(
                        Json.parseToJsonElement(people).jsonObject + mapOf(
                            "type" to JsonPrimitive("object"), "minProperties" to JsonPrimitive(1)
                        )
                    ).toString()
                )
            )
            if (namedAlbums.isNotEmpty()) add(
                selected(
                    "album",
                    """{"type":"object","properties":{$albumRoles},"required":${
                        Json.encodeToString(namedAlbums)
                    },"minProperties":1,"additionalProperties":false}"""
                )
            )
            add(
                selected(
                    "date",
                    """{"anyOf":[${
                        (listOf(
                            relative,
                            relativeRange
                        ) + absolute).joinToString(",")
                    }]}"""
                )
            )
            add(selected("filename", """{"type":"string","minLength":1,"maxLength":200}"""))
        }
        return JsonObject(mapOf("anyOf" to JsonArray(selectors))).toString()
    }

    fun grammar(
        current: AssistantGalleryQuery?,
        catalog: AssistantGalleryCatalog = AssistantGalleryCatalog(),
        question: String = "",
        language: String? = null,
        model: AssistantModel = AssistantModel.Lite
    ): String = buildString {
        val copySource = model == AssistantModel.Lite || model == AssistantModel.VisionLite
        val modes = if (current == null) listOf("new") else listOf("update", "new", "narrow")
        appendLine("start: ${modes.joinToString(" | ") { "search_$it" }} | ask | all${if (current != null) " | more" else ""}")
        modes.forEach { mode ->
            val previous = current.takeUnless { mode == "new" }
            appendLine(
                "search_$mode: %json {\"type\":\"object\",\"properties\":{\"action\":{\"const\":\"search\"},\"mode\":{\"const\":\"$mode\"},\"query\":${
                    schema(
                        previous,
                        catalog,
                        question,
                        language,
                        copySource,
                        requireFilter = mode != "update"
                    )
                }},\"required\":[\"action\",\"mode\",\"query\"],\"additionalProperties\":false}"
            )
        }
        appendLine("all: %json {\"type\":\"object\",\"properties\":{\"action\":{\"const\":\"all\"}},\"required\":[\"action\"],\"additionalProperties\":false}")
        if (current != null) appendLine("more: %json {\"type\":\"object\",\"properties\":{\"action\":{\"const\":\"more\"}},\"required\":[\"action\"],\"additionalProperties\":false}")
        appendLine("ask: %json {\"type\":\"object\",\"properties\":{\"action\":{\"const\":\"ask\"},\"question\":{\"type\":\"string\",\"minLength\":1,\"maxLength\":200}},\"required\":[\"action\",\"question\"],\"additionalProperties\":false}")
    }

    fun catalogContext(
        current: AssistantGalleryQuery?,
        catalog: AssistantGalleryCatalog,
        question: String,
        language: String? = null
    ): String =
        "People named in this request: ${
            Json.encodeToString(
                matching(
                    catalog.people + current?.people.orEmpty() + current?.excludedPeople.orEmpty(),
                    question,
                    language
                )
            )
        }; " +
                "albums named in this request: ${
                    Json.encodeToString(
                        matching(
                            catalog.albums + listOfNotNull(
                                current?.album
                            ), question, language
                        )
                    )
                }; " +
                "filenames named in this request: ${
                    Json.encodeToString(
                        (catalog.files + listOfNotNull(
                            current?.filename
                        )).distinct().filter { question.contains(it, true) })
                }."

    fun instructions(copySource: Boolean = false): String = buildString {
        if (copySource) appendLine("For visible content use scene=true: the app copies the original request into its multilingual encoder without losing colors, objects or actions. For metadata-only changes use scene=false. Only use a scene string when replacing an old visible condition or shortening a request too long for the encoder; include every desired visual detail in that string.")
        appendLine(
            "Convert each filter change to JSON with action, mode and query. ${if (copySource) "" else "scene is a short positive description of the desired visible objects, attributes and actions, or null for metadata-only changes. "}action=more is only for another page with every condition unchanged; any removal or replacement is action=search. Return only changed metadata filters. Omit unchanged metadata; scene=null leaves visible content unchanged. clear lists filters explicitly removed: scene, people, date, album, filename. For a new search the app starts with empty filters. action=search needs at least one actual condition. action=all explicitly clears every condition and shows the whole gallery; never use it when a person, date or depicted content is requested. The app preserves every unmentioned condition; do not copy current filters into changes. people contains an action for each named person in this request only: include, exclude or remove. Unmentioned people are preserved by the app. Add people to clear only when the user explicitly removes every people condition, never when excluding or removing one person."
        )
        appendLine(
            "mode: update normally, new for an independent search with old conditions cleared, narrow for searching only previous results. ${if (copySource) "Never rewrite a short new visible condition: use scene=true. Extract all dates before deciding whether visible content is present." else "Extract scene directly from the original request, preserving colors, objects and actions in its language."} The visual encoder is multilingual. Do not translate it or add search instructions, people names, dates, albums or filenames. A scene string replaces the previous visual condition: when adding or replacing a detail, supply the full desired visual description and preserve every old visual detail that still applies. Use scene=null when depicted content is unchanged. Never copy a scene from the demonstrations. include requires that person in results; exclude forbids them; remove deletes their condition without excluding them. A relative calendar period uses date={unit:day/week/month/year,offset:integer}: 0 is current, negative is past, positive is future. Weeks run Monday through Sunday. For a relative interval use date={fromOffset:days,toOffset:days}, with null for an open boundary. Boundaries are inclusive, so seven days ending today use -6 and 0. Preserve the requested unit and let the app calculate dates. An explicit day is YYYY-MM-DD. For a whole named month use date={unit:month,value:YYYY-MM}; for a whole named year use date={unit:year,value:YYYY}. Never select just the last day of a month or year. The app expands both boundaries. Only a requested interval uses date={from:ISO string or null,to:ISO string or null}; null is an open boundary. Omit date when unchanged. The final Request is authoritative. Never borrow conditions or objects from demonstrations. Relative periods need an offset, not calculated dates. If an absolute year is missing and cannot be resolved from active filters, ask which year. album maps a literal album name to include or remove, using the same operation convention as people. include sets that album; remove deletes its restriction. filename is a literal name or null. action=ask with question clarifies an unavailable identity."
        )
    }
}
