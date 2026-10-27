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

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

internal object AssistantTools {
    private const val CALL_START = "<tool_call>"
    private const val CALL_END = "</tool_call>"

    @Serializable
    private data class Call(val name: String, val arguments: JsonObject)

    @Serializable
    private data class Help(val query: String)

    sealed interface Result {
        data class Answer(val text: String) : Result
        data class Help(val query: String) : Result
        data class Photos(val plan: AssistantPhotoPlan) : Result
        data object More : Result
    }

    fun parse(completion: String): Result {
        val text = completion.trim()
        if (!text.startsWith(CALL_START)) return Result.Answer(text)
        val call = Json.decodeFromString<Call>(
            text.removePrefix(CALL_START).substringBefore(CALL_END).trim()
        )
        return when (call.name) {
            "get_help" -> Result.Help(
                Json.decodeFromJsonElement(
                    Help.serializer(),
                    call.arguments
                ).query
            )

            "find_photos" -> Result.Photos(AssistantPhotoPlan.parse(call.arguments.toString()))
            "more_photos" -> Result.More
            else -> error("Unknown assistant tool: ${call.name}")
        }
    }

    fun replyText(completion: String): String {
        val text = completion.trimStart()
        return if (text.startsWith(CALL_START) || CALL_START.startsWith(text)) "" else text
    }

    fun prompt(
        grounding: String,
        request: AssistantRequest,
        history: List<AssistantMessage>,
        catalog: AssistantGalleryCatalog,
        current: AssistantGalleryQuery?,
        today: java.time.LocalDate = java.time.LocalDate.now()
    ): String = assistantPrompt(
        grounding = buildString {
            appendLine(instructions())
            appendLine(grounding)
            appendLine("Today: $today")
            current?.let { appendLine("Active photo filters: ${Json.encodeToString(it)}") }
            if (catalog.people.isNotEmpty()) appendLine("People: ${Json.encodeToString(catalog.people)}")
            if (catalog.albums.isNotEmpty()) appendLine("Albums: ${Json.encodeToString(catalog.albums)}")
        },
        history = history,
        question = request.query,
        hasImage = false
    )

    fun instructions(): String = """
You are the ImageToolbox assistant. Reply in the user's language unless another is requested. Use supplied help for app instructions; do not invent controls or claim you changed files.
For questions about ImageToolbox tools, settings or editing, call get_help first with a concise English search query preserving the task, numbers and formats. For unrelated conversation answer directly.
For finding existing library photos or changing an active photo search, call find_photos instead of explaining how to search. For answers or conversation respond normally. For the next photo page call more_photos.
Photo fields: resetFilters only for a fresh independent search; withinResults only for restricting already found photos; sceneChanged only for new visible content, false for metadata-only changes. people lists required identities; excludedPeople lists excluded identities. Use exact catalog names and literal filenames. Dates are inclusive ISO dates. Unspecified fields are null/empty. clear is only for explicit removals. Unsupported OCR/GPS, OR or object counts need supported=false and a clarification.
# Tools
<tools>
{"type":"function","function":{"name":"get_help","description":"Look up ImageToolbox instructions and capabilities. Supply the meaning of the question in English; keep all constraints and file formats.","parameters":{"type":"object","properties":{"query":{"type":"string"}},"required":["query"]}}}
{"type":"function","function":{"name":"find_photos","description":"Find existing library photos, or modify the active search. Preserve every filter. Null/empty preserves previous filters; clear explicitly removes them.","parameters":{"type":"object","properties":{"supported":{"type":"boolean"},"clarification":{"type":"string"},"resetFilters":{"type":"boolean"},"withinResults":{"type":"boolean"},"sceneChanged":{"type":"boolean"},"people":{"type":"array","items":{"type":"string"}},"excludedPeople":{"type":"array","items":{"type":"string"}},"from":{"type":["string","null"],"format":"date"},"to":{"type":["string","null"],"format":"date"},"album":{"type":["string","null"]},"filename":{"type":["string","null"]},"clear":{"type":"array","items":{"type":"string","enum":["Scene","People","Date","Album","Filename"]}}},"required":["supported"],"additionalProperties":false}}}
{"type":"function","function":{"name":"more_photos","description":"Next page of the active photo search","parameters":{"type":"object","properties":{}}}}
</tools>
When calling a tool, return one JSON function call inside <tool_call></tool_call>. Otherwise answer directly in plain text.
""".trimIndent()
}
