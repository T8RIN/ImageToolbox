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
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.util.Locale

internal sealed interface AssistantInterpretation {
    data object Answer : AssistantInterpretation
    data object Capabilities : AssistantInterpretation
    data class Help(val query: String, val subject: String? = null) : AssistantInterpretation
    data class Translate(val language: String) : AssistantInterpretation
    data class Formats(val alpha: Boolean) : AssistantInterpretation
    data object Clarify : AssistantInterpretation
    data object NextPage : AssistantInterpretation
    data object Search : AssistantInterpretation

    companion object {
        fun grammar(model: AssistantModel): String = buildString {
            val routingGoal = model != AssistantModel.Advanced
            val deferPagination = model == AssistantModel.Lite || model == AssistantModel.VisionLite
            val actions = listOf(
                "editing_instructions",
                "app_term_definition",
                "translate_previous_help",
                "need_context",
                "photo_filters",
                "general_knowledge_or_creative_writing",
                "assistant_capabilities",
                "list_image_export_formats",
                "list_transparent_export_formats",
                "next_photo_page_unchanged"
            ).filterNot { deferPagination && it == "next_photo_page_unchanged" }
            appendLine("start: ${actions.joinToString(" | ")}")
            actions.forEach { action ->
                val fields = buildMap {
                    val goal = """{"type":"string","minLength":1,"maxLength":160}"""
                    if (routingGoal) put("goal", goal)
                    put("action", """{"const":${Json.encodeToString(action)}}""")
                    if (!routingGoal && (action == "editing_instructions" || action == "app_term_definition")) {
                        put("goal", goal)
                    }
                    if (action == "app_term_definition") {
                        put("subject", """{"type":"string","minLength":1,"maxLength":160}""")
                    }
                    if (action == "translate_previous_help") {
                        put("language", """{"type":"string","minLength":2,"maxLength":35}""")
                    }
                }
                val properties = fields.entries.joinToString(",") { (name, schema) ->
                    "${Json.encodeToString(name)}:$schema"
                }
                appendLine(
                    "$action: %json {\"type\":\"object\",\"properties\":{$properties},\"required\":${
                        Json.encodeToString(
                            fields.keys.toList()
                        )
                    },\"additionalProperties\":false}"
                )
            }
        }

        fun parse(text: String): AssistantInterpretation {
            require(text.length <= 1000)
            val decoded = Json.parseToJsonElement(text).jsonObject
            decoded["goal"]?.jsonPrimitive?.content?.let { require(it.isNotBlank() && it.length <= 160) }
            val action = decoded.getValue("action").jsonPrimitive.content
            val value =
                if (action == "editing_instructions" || action == "app_term_definition") decoded
                else JsonObject(decoded - "goal")
            if (action == "editing_instructions" || action == "app_term_definition") {
                require(
                    value.keys == if (action == "app_term_definition") {
                        setOf("action", "goal", "subject")
                    } else setOf("action", "goal")
                )
                val goal = value.getValue("goal").jsonPrimitive.content
                require(goal.isNotBlank() && goal.length <= 160)
                val subject = value["subject"]?.jsonPrimitive?.content
                require(subject == null || subject.isNotBlank() && subject.length <= 160)
                return Help(goal, subject)
            }
            if (action == "translate_previous_help") {
                require(value.keys == setOf("action", "language"))
                val language =
                    Locale.forLanguageTag(value.getValue("language").jsonPrimitive.content)
                require(language.language.isNotBlank())
                return Translate(language.toLanguageTag())
            }
            require(value.keys == setOf("action"))
            return when (action) {
                "photo_filters" -> Search
                "general_knowledge_or_creative_writing" -> Answer
                "assistant_capabilities" -> Capabilities
                "next_photo_page_unchanged" -> NextPage
                "need_context" -> Clarify
                "list_image_export_formats" -> Formats(false)
                "list_transparent_export_formats" -> Formats(true)
                else -> error("Invalid assistant action")
            }
        }
    }
}
