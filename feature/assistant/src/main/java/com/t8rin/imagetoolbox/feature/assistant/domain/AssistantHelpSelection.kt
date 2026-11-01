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
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive

internal object AssistantHelpSelection {
    fun prompt(
        request: AssistantRequest,
        task: String,
        references: List<AssistantReference>,
        model: AssistantModel
    ): String = assistantPrompt(
        grounding = "Select the documentation that satisfies the request. Check the requested operation, input and output. A document saying an operation is unsupported is not a match. Return only the document number.\n" +
                references.mapIndexed { index, reference ->
                    "$index. ${reference.title}\n${reference.text.take(1200)}"
                }.joinToString("\n\n"),
        history = emptyList(),
        question = "Resolved task (context only): $task\nRequest: ${request.query}",
        hasImage = false,
        model = model
    )

    fun grammar(count: Int): String {
        require(count in 2..3)
        return """start: %json {"type":"integer","enum":[${(0 until count).joinToString(",")}]}"""
    }

    fun parse(text: String, references: List<AssistantReference>): AssistantReference =
        references[Json.parseToJsonElement(text).jsonPrimitive.int]
}
