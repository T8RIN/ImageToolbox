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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal object AssistantCommand {
    enum class Scope { Auto, Help, Photos }

    private const val START = "<tool_call>"
    private const val END = "</tool_call>"
    private val json = Json { ignoreUnknownKeys = true }

    @Serializable
    private data class Call(val name: String, val arguments: JsonObject = JsonObject(emptyMap()))

    @Serializable
    private enum class Operation {
        @SerialName("update")
        Update,
        @SerialName("narrow")
        Narrow
    }

    @Serializable
    private sealed interface PhotoFilter {
        @Serializable
        @SerialName("visual_details")
        data class Scene(@SerialName("details") val query: String) : PhotoFilter

        @Serializable
        @SerialName("person")
        data class Person(val name: String, val role: AssistantPhotoPlan.PersonRole) : PhotoFilter

        @Serializable
        @SerialName("date")
        data class Date(val from: String, val to: String) : PhotoFilter

        @Serializable
        @SerialName("album")
        data class Album(val name: String) : PhotoFilter

        @Serializable
        @SerialName("filename")
        data class Filename(val pattern: String) : PhotoFilter

        @Serializable
        @SerialName("clear")
        data class Clear(val field: AssistantPhotoPlan.Filter) : PhotoFilter

        @Serializable
        @SerialName("reset")
        data object Reset : PhotoFilter
    }

    @Serializable
    private data class PhotoCall(
        val operation: Operation,
        val filters: List<PhotoFilter>
    ) {
        fun plan(): AssistantPhotoPlan {
            require(filters.size <= 16)
            var plan = AssistantPhotoPlan(
                withinResults = operation == Operation.Narrow
            )
            filters.forEach { filter ->
                plan = when (filter) {
                    is PhotoFilter.Scene -> plan.copy(
                        scene = listOfNotNull(
                            plan.scene,
                            filter.query
                        ).joinToString(", ")
                    )

                    is PhotoFilter.Person -> plan.copy(people = plan.people + (filter.name to filter.role))
                    is PhotoFilter.Date -> {
                        val range =
                            AssistantPhotoPlan(from = filter.from, to = filter.to).normalized()
                        plan.copy(
                            from = listOfNotNull(plan.from, range.from).maxOrNull(),
                            to = listOfNotNull(plan.to, range.to).minOrNull()
                        )
                    }

                    is PhotoFilter.Album -> {
                        require(plan.album == null || plan.album == filter.name)
                        plan.copy(album = filter.name)
                    }

                    is PhotoFilter.Filename -> {
                        require(plan.filename == null || plan.filename == filter.pattern)
                        plan.copy(filename = filter.pattern)
                    }

                    is PhotoFilter.Clear -> plan.copy(clear = plan.clear + filter.field)
                    PhotoFilter.Reset -> plan.copy(resetFilters = true, withinResults = false)
                }
            }
            return plan.normalized()
        }
    }

    sealed interface Action {
        data object Answer : Action
        data class SearchPhotos(val plan: AssistantPhotoPlan) : Action
        data object MorePhotos : Action
        data class SearchHelp(val query: String) : Action
    }

    fun parse(text: String): Action? {
        val reply =
            if (text.trimStart().startsWith("<think>")) text.substringAfter("</think>") else text
        val start = reply.indexOf(START)
        val body = if (start >= 0) reply.substring(start + START.length).substringBefore(END).trim()
        else reply.trim().takeIf { it.startsWith('{') } ?: return null
        val call = json.decodeFromString<Call>(body)
        return when (call.name) {
            "answer_general_question" -> Action.Answer
            "search_photos" -> Action.SearchPhotos(
                Json.decodeFromString<PhotoCall>(call.arguments.toString()).plan()
            )

            "more_photos" -> Action.MorePhotos
            "search_help" -> Action.SearchHelp(
                requireNotNull(call.arguments["query"]?.jsonPrimitive?.content).also {
                    require(it.isNotBlank() && it.length <= 500)
                }
            )

            else -> error("Unknown assistant action: ${call.name}")
        }
    }

    fun visibleText(text: String): String {
        val start = text.indexOf(START)
        if (start >= 0) return text.take(start).trimEnd()
        val partial = text.lastIndexOf('<')
        return if (partial >= 0 && START.startsWith(text.substring(partial))) text.take(partial)
            .trimEnd() else text
    }

    fun isInternalReply(text: String): Boolean = runCatching {
        val value = Json.parseToJsonElement(text.trim()).jsonObject
        value.containsKey("mode") || value.containsKey("action") || value.containsKey("findPhotos") || value.containsKey(
            "appHelp"
        )
    }.getOrDefault(false)

    private val functions = listOf(
        """
        {
            "type": "function",
            "function": {
                "name": "search_help",
                "description": "Find ImageToolbox instructions for an image task or app setting before explaining an image task, file operation or app setting.",
                "parameters": {
                    "type": "object",
                    "properties": {
                        "query": {
                            "type": "string",
                            "maxLength": 300,
                            "description": "2-6 precise English keywords describing the requested task, without question phrasing."
                        }
                    },
                    "required": [
                        "query"
                    ],
                    "additionalProperties": false
                }
            }
        }
        """.trimIndent(),
        """
        {
            "type": "function",
            "function": {
                "name": "search_photos",
                "description": "Find existing personal photos. Represent EVERY requested condition as a separate filter. Never invent filters. Does not edit files or find duplicates.",
                "parameters": {
                    "type": "object",
                    "properties": {
                        "operation": {
                            "type": "string",
                            "enum": ["update", "narrow"],
                            "description": "update preserves all old conditions and applies the supplied changes; narrow also restricts to earlier results. Starting an independent search requires an explicit reset filter."
                        },
                        "filters": {
                            "type": "array",
                            "maxItems": 16,
                            "description": "One filter for each condition. Include ALL mentioned people, dates, albums, filenames and visual contents. Omit unrequested or unchanged conditions.",
                            "items": {
                                "oneOf": [
                                    {
                                        "type": "object",
                                        "properties": {"type": {"const": "visual_details"}, "details": {"type": "string", "minLength": 1, "maxLength": 500, "description": "Only additional visible details beyond the other filters: depicted objects, appearance, scenery or activities. Never restate people, dates, albums or filenames. Omit this filter for metadata-only requests."}},
                                        "required": ["type", "details"], "additionalProperties": false
                                    },
                                    {
                                        "type": "object",
                                        "properties": {"type": {"const": "person"}, "name": {"type": "string"}, "role": {"type": "string", "enum": ["include", "exclude", "remove"], "description": "include requires this person; exclude means photos without this person; remove drops that person's previous condition."}},
                                        "required": ["type", "name", "role"], "additionalProperties": false
                                    },
                                    {
                                        "type": "object",
                                        "properties": {"type": {"const": "date"}, "from": {"type": "string", "pattern": "^([0-9]{4}(-[0-9]{2}(-[0-9]{2})?)?)?$"}, "to": {"type": "string", "pattern": "^([0-9]{4}(-[0-9]{2}(-[0-9]{2})?)?)?$"}},
                                        "required": ["type", "from", "to"], "additionalProperties": false,
                                        "description": "Capture-date interval. YYYY-MM-DD; for a whole month BOTH from and to are YYYY-MM; for a year BOTH are YYYY. Leave one bound empty for an open interval."
                                    },
                                    {
                                        "type": "object", "properties": {"type": {"const": "album"}, "name": {"type": "string"}},
                                        "required": ["type", "name"], "additionalProperties": false
                                    },
                                    {
                                        "type": "object", "properties": {"type": {"const": "filename"}, "pattern": {"type": "string"}},
                                        "required": ["type", "pattern"], "additionalProperties": false
                                    },
                                    {
                                        "type": "object", "properties": {"type": {"const": "clear"}, "field": {"type": "string", "enum": ["Scene", "People", "Date", "Album", "Filename"]}},
                                        "required": ["type", "field"], "additionalProperties": false,
                                        "description": "A condition the user explicitly removed."
                                    },
                                    {
                                        "type": "object", "properties": {"type": {"const": "reset"}},
                                        "required": ["type"], "additionalProperties": false,
                                        "description": "Clear the entire previous search ONLY when the user explicitly requests an independent new search. Never use for a refinement."
                                    }
                                ]
                            }
                        }
                    },
                    "required": ["operation", "filters"],
                    "additionalProperties": false
                }
            }
        }
        """.trimIndent(),
        """
        {
            "type": "function",
            "function": {
                "name": "more_photos",
                "description": "Show the next page of the active photo search",
                "parameters": {
                    "type": "object",
                    "properties": {},
                    "additionalProperties": false
                }
            }
        }
        """.trimIndent(),
        """
        {
            "type": "function",
            "function": {
                "name": "answer_general_question",
                "description": "Only for unrelated topics such as arithmetic, science or creative writing, for describing attached-image pixels, or for a clarification. Editing workflows and app settings require search_help.",
                "parameters": {
                    "type": "object",
                    "properties": {},
                    "additionalProperties": false
                }
            }
        }
        """.trimIndent()
    ).map { json.parseToJsonElement(it).jsonObject }

    private fun matching(names: List<String>, question: String): List<String> {
        data class Mention(val name: String, val start: Int, val size: Int, val exact: Boolean)

        val query = AssistantSearchIndex.tokens(question)
        val candidates = names.flatMap { name ->
            val terms = AssistantSearchIndex.tokens(name)
            if (terms.isEmpty()) return@flatMap emptyList()
            query.windowed(terms.size).mapIndexedNotNull { start, words ->
                val exact = words == terms
                if (exact || words.zip(terms).all { (word, term) ->
                        word.length >= term.length && AssistantSearchIndex.matches(
                            word,
                            term,
                            minimumPrefix = 4,
                            minimumCoverage = 0.7
                        )
                    }) Mention(name, start, terms.size, exact) else null
            }
        }.sortedWith(compareByDescending<Mention> { it.size }.thenByDescending { it.exact })
        val selected = mutableListOf<Mention>()
        candidates.forEach { candidate ->
            if (selected.none { it.start < candidate.start + candidate.size && candidate.start < it.start + it.size }) selected.add(
                candidate
            )
        }
        return selected.sortedBy { it.start }.map { it.name }.distinct().take(10)
    }

    private fun tools(
        current: AssistantGalleryQuery?,
        catalog: AssistantGalleryCatalog,
        question: String,
        scope: Scope
    ) = functions.mapNotNull { tool ->
        val function = tool.getValue("function").jsonObject
        val name = function.getValue("name").jsonPrimitive.content
        if (scope == Scope.Help && name != "search_help" ||
            scope == Scope.Photos && name != "search_photos"
        ) return@mapNotNull null
        if (current == null && name == "more_photos") return@mapNotNull null
        if (name != "search_photos") return@mapNotNull tool
        val parameters = function.getValue("parameters").jsonObject
        val properties = parameters.getValue("properties").jsonObject.toMutableMap()
        val people = (matching(catalog.people, question).ifEmpty {
            current?.people.orEmpty() + current?.excludedPeople.orEmpty()
        }).distinct()
        val albums = (matching(catalog.albums, question) + listOfNotNull(current?.album)).distinct()
        val patterns = question.split(' ', '\n', '\t').map {
            it.trim('"', '\'', '«', '»', ',', '.', ';')
        }.filter { it.length <= 300 && ('*' in it || '?' in it && '.' in it) }
        val files = (matching(catalog.files, question) + patterns).distinct()
        val filterSchema = properties.getValue("filters").jsonObject
        val alternatives = filterSchema.getValue("items").jsonObject.getValue("oneOf") as JsonArray
        val available = alternatives.mapNotNull { alternative ->
            val schema = alternative.jsonObject
            val fields = schema.getValue("properties").jsonObject.toMutableMap()
            val type = fields.getValue("type").jsonObject.getValue("const").jsonPrimitive.content
            val values = when (type) {
                "person" -> people
                "album" -> albums
                "filename" -> files
                "clear", "reset" -> if (current == null) return@mapNotNull null else null
                else -> null
            }
            if (values != null) {
                if (values.isEmpty()) return@mapNotNull null
                val key = if (type == "filename") "pattern" else "name"
                fields[key] = JsonObject(
                    fields.getValue(key).jsonObject +
                            ("enum" to JsonArray(values.map(::JsonPrimitive)))
                )
            }
            JsonObject(schema + ("properties" to JsonObject(fields)))
        }
        properties["filters"] = JsonObject(
            filterSchema + ("items" to JsonObject(
                mapOf("oneOf" to JsonArray(available))
            ))
        )
        if (current == null) properties["operation"] =
            JsonObject(mapOf("const" to JsonPrimitive("update")))
        val constraints = parameters.toMutableMap()
        constraints["properties"] = JsonObject(properties)
        val namedParameters = JsonObject(constraints)
        val namedFunction = JsonObject(function + ("parameters" to namedParameters))
        JsonObject(tool + ("function" to namedFunction))
    }

    fun hasNamedRequest(catalog: AssistantGalleryCatalog, question: String): Boolean =
        matching(catalog.people, question).isNotEmpty() || matching(
            catalog.albums,
            question
        ).isNotEmpty() || matching(catalog.files, question).isNotEmpty()

    fun grammar(
        current: AssistantGalleryQuery?,
        catalog: AssistantGalleryCatalog = AssistantGalleryCatalog(),
        question: String = "",
        reasoning: Boolean = false,
        scope: Scope = Scope.Auto
    ): String = buildString {
        val functions = tools(current, catalog, question, scope)
        if (reasoning) {
            val hint =
                if (scope == Scope.Photos) "Filters to change:" else "Intent and explicit constraints:"
            val limit = if (scope == Scope.Photos) 256 else 512
            appendLine("start: \"<think>$hint\" /[^<]{1,$limit}/? \"</think>\" command")
        } else appendLine("start: command")
        appendLine("command: " + functions.indices.joinToString(" | ") { "call_$it" })
        functions.forEachIndexed { index, tool ->
            val function = tool.getValue("function").jsonObject
            val prefix = "{\"name\":${function.getValue("name")},\"arguments\":"
            appendLine("call_$index: ${Json.encodeToString(prefix)} args_$index \"}\"")
            appendLine("args_$index: %json ${function.getValue("parameters")}")
        }
    }

    fun instructions(
        current: AssistantGalleryQuery?,
        catalog: AssistantGalleryCatalog = AssistantGalleryCatalog(),
        question: String = "",
        language: String? = null,
        scope: Scope = Scope.Auto
    ): String = buildString {
        appendLine("Image and file editing questions refer to ImageToolbox even when the app is not named. Look up app instructions instead of giving generic editing advice.")
        appendLine("Select one function. For tools, settings and image/file editing instructions use search_help. For existing personal photo lookup or its refinements use search_photos. Never invent function names.")
        appendLine("For general knowledge, conversation, attached-image descriptions or a clarification use answer_general_question.")
        if (!language.isNullOrBlank()) appendLine("Interpret the latest request in language (BCP-47): $language.")
        appendLine("Honor negation. A named identity has exactly one role: include, exclude or remove. Negated identities use exclude. Reason briefly about only the requested constraints, without repeating the question or tool descriptions.")
        appendLine("# Tools\nFunction signatures:")
        appendLine("<tools>")
        tools(current, catalog, question, scope).forEach { appendLine(it.toString()) }
        appendLine("</tools>")
        matching(catalog.people, question).takeIf { it.isNotEmpty() }?.let {
            appendLine("Named people in the request: ${Json.encodeToString(it)}")
        }
        matching(catalog.albums, question).takeIf { it.isNotEmpty() }?.let {
            appendLine("Named albums in the request: ${Json.encodeToString(it)}")
        }
        matching(catalog.files, question).takeIf { it.isNotEmpty() }?.let {
            appendLine(
                "Literal filenames in the request, not scene contents or dates: ${
                    Json.encodeToString(
                        it
                    )
                }"
            )
        }
        appendLine("Person and album filters accept only the available named values in their schemas. If a requested name is unavailable, ask for clarification rather than replacing it with scene contents.")
        appendLine("Emit one filter for EACH requested condition. Capture dates use date filters, names use person, albums use album and filenames use filename. A visual_details filter describes only additional visible contents. Metadata must never appear as visual details. Unmentioned conditions are preserved automatically.")
        current?.let {
            appendLine("Active photo search: ${Json.encodeToString(it)}")
            appendLine("A photo search is active. Interpret short follow-ups as updates to it. Use operation update or narrow for earlier results. Include only changed filters; every old condition is preserved. Starting an independent new search requires a reset filter explicitly requested by the user. Person exclusions and metadata-only refinements do not add visual_details filters.")
            if (it.from != null || it.to != null) appendLine("Active capture-date bounds, for resolving relative date changes only: ${it.from.orEmpty()} to ${it.to.orEmpty()}")
        }

        appendLine("Return one JSON object with name and arguments, matching the selected function. No XML or ordinary prose.")
    }
}
