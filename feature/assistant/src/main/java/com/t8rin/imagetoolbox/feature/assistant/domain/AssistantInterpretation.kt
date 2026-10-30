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
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.util.Locale

internal sealed interface AssistantInterpretation {
    data object Answer : AssistantInterpretation
    data class Help(val query: String) : AssistantInterpretation
    data class Translate(val language: String) : AssistantInterpretation
    data class Formats(val alpha: Boolean) : AssistantInterpretation
    data object Clarify : AssistantInterpretation
    data object Search : AssistantInterpretation
    data object More : AssistantInterpretation

    companion object {
        val grammar = buildString {
            appendLine("start: help | translate | clarify | search | answer | more | supported_formats | transparency_formats")
            appendLine("help: %json {\"type\":\"object\",\"properties\":{\"action\":{\"const\":\"help\"},\"query\":{\"type\":\"string\",\"minLength\":1,\"maxLength\":160}},\"required\":[\"action\",\"query\"],\"additionalProperties\":false}")
            appendLine("translate: %json {\"type\":\"object\",\"properties\":{\"action\":{\"const\":\"translate\"},\"language\":{\"type\":\"string\",\"minLength\":2,\"maxLength\":35}},\"required\":[\"action\",\"language\"],\"additionalProperties\":false}")
            listOf(
                "clarify",
                "search",
                "answer",
                "more",
                "supported_formats",
                "transparency_formats"
            ).forEach { action ->
                appendLine("${action.lowercase(Locale.ROOT)}: %json {\"type\":\"object\",\"properties\":{\"action\":{\"const\":\"$action\"}},\"required\":[\"action\"],\"additionalProperties\":false}")
            }
        }

        fun parse(text: String): AssistantInterpretation {
            require(text.length <= 1000)
            val value = Json.parseToJsonElement(text).jsonObject
            val action = value.getValue("action").jsonPrimitive.content
            if (action == "help") {
                require(value.keys == setOf("action", "query"))
                return Help(value.getValue("query").jsonPrimitive.content.also {
                    require(it.isNotBlank() && it.length <= 160)
                })
            }
            if (action == "translate") {
                require(value.keys == setOf("action", "language"))
                val language =
                    Locale.forLanguageTag(value.getValue("language").jsonPrimitive.content)
                require(language.language.isNotBlank())
                return Translate(language.toLanguageTag())
            }
            require(value.keys == setOf("action"))
            return when (action) {
                "answer" -> Answer
                "search" -> Search
                "more" -> More
                "clarify" -> Clarify
                "supported_formats" -> Formats(false)
                "transparency_formats" -> Formats(true)
                else -> error("Invalid assistant action")
            }
        }
    }
}
