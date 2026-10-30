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

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal object AssistantCommand {
    sealed interface Action {
        data class SearchPhotos(
            val query: AssistantGalleryQuery,
            val mode: AssistantGallerySearch.Mode
        ) : Action

        data class Answer(val text: String) : Action
        data object MorePhotos : Action
    }

    fun parse(text: String): Action {
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

            "search" -> {
                require(value.keys == setOf("action", "mode", "query"))
                val mode = when (value.getValue("mode").jsonPrimitive.content) {
                    "new" -> AssistantGallerySearch.Mode.New
                    "update" -> AssistantGallerySearch.Mode.Update
                    "narrow" -> AssistantGallerySearch.Mode.Narrow
                    else -> error("Unknown photo search mode")
                }
                Action.SearchPhotos(
                    Json.decodeFromString<AssistantGalleryQuery>(
                        value.getValue("query").toString()
                    ).normalized(), mode
                )
            }

            else -> error("Unknown assistant action")
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
            listOf("people", "excludedPeople", "from", "to", "album", "semantic", "filename")
                .associateWith(fields::getValue)
        ).toString()
    }

    private fun schema(
        current: AssistantGalleryQuery?,
        catalog: AssistantGalleryCatalog,
        question: String,
        language: String?
    ): String {
        val named = matching(
            catalog.people + current?.people.orEmpty() + current?.excludedPeople.orEmpty(),
            question,
            language
        )
        val albums = Json.encodeToString(
            listOf<String?>(null) + albums(
                current,
                catalog,
                question,
                language
            )
        )
        val text = """{"type":["string","null"],"minLength":1,"maxLength":200}"""
        val date = """{"type":["string","null"],"format":"date"}"""
        fun persons(previous: List<String>): String {
            val names = Json.encodeToString((previous + named).distinct())
            return if (names == "[]") """{"const":[]}""" else """{"type":"array","items":{"enum":$names},"maxItems":10}"""
        }
        return """{"type":"object","properties":{
            "people":${persons(current?.people.orEmpty())},"excludedPeople":${persons(current?.excludedPeople.orEmpty())},
            "from":$date,"to":$date,"album":{"enum":$albums},"semantic":$text,"filename":$text
        },"required":["semantic","people","excludedPeople","from","to","album","filename"],"additionalProperties":false}""".replace(
            "\n",
            ""
        )
    }

    fun grammar(
        current: AssistantGalleryQuery?,
        catalog: AssistantGalleryCatalog = AssistantGalleryCatalog(),
        question: String = "",
        language: String? = null
    ): String = buildString {
        val modes = if (current == null) listOf("new") else listOf("update", "new", "narrow")
        appendLine("start: ${modes.joinToString(" | ") { "search_$it" }} | ask")
        modes.forEach { mode ->
            val previous = current.takeUnless { mode == "new" }
            appendLine(
                "search_$mode: %json {\"type\":\"object\",\"properties\":{\"action\":{\"const\":\"search\"},\"mode\":{\"const\":\"$mode\"},\"query\":${
                    schema(
                        previous,
                        catalog,
                        question,
                        language
                    )
                }},\"required\":[\"action\",\"mode\",\"query\"],\"additionalProperties\":false}"
            )
        }
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

    fun instructions(): String = buildString {
        appendLine("Convert each photo request to JSON with action, mode, query. Return the COMPLETE desired filters. Keep unmentioned current conditions. Null means no condition. Empty lists mean no people.")
        appendLine("mode: update normally, new for an independent search with old conditions cleared, narrow for searching only previous results. semantic describes additional depicted content in English. Use semantic=null when all requested content is already represented by other query fields. Never repeat metadata there. people lists identities required in each result; excludedPeople lists identities forbidden in each result. Removing a person's condition removes their name from both lists. from/to are inclusive ISO dates or null; a single day uses that same date at both ends. album/filename are literal names or null. action=ask with question clarifies an unavailable identity.")
    }
}
