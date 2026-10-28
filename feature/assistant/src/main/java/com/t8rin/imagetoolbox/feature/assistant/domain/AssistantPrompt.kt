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

internal fun assistantPrompt(
    grounding: String,
    history: List<AssistantMessage>,
    question: String,
    hasImage: Boolean,
    thinking: Boolean = false
): String = buildString {
    append("<|im_start|>system\n${grounding.escapeTemplate()}<|im_end|>\n")
    history.filter { it.error == null && it.answer.text.isNotBlank() }
        .takeLast(2)
        .forEach { message ->
            append("<|im_start|>user\n")
            if (message.context.selectedUris.isNotEmpty()) {
                append("Files were attached to this earlier message; their contents are not included here.\n")
            }
            append(message.question.take(200).escapeTemplate())
            append("<|im_end|>\n<|im_start|>assistant\n")
            append(message.answer.text.take(240).escapeTemplate())
            append("<|im_end|>\n")
        }
    append("<|im_start|>user\n")
    if (hasImage) append("<|vision_start|><|image_pad|><|vision_end|>\n")
    append(question.escapeTemplate())
    append("<|im_end|>\n<|im_start|>assistant\n")
    if (!thinking) append("<think>\n\n</think>\n\n")
}

private fun String.escapeTemplate(): String = replace("<|", "＜|")

internal fun assistantInterpretationPrompt(
    question: String,
    history: List<AssistantMessage>,
    simple: Boolean = false
): String = assistantPrompt(
    grounding = if (simple) "Translate the latest request into English and resolve references to the previous question. Keep every condition, name, number and filename. Return only the complete request, without answering."
    else "Rephrase the meaning of the latest request in concise English, without answering it. Preserve whether it asks for editing instructions, a personal gallery lookup, or general knowledge. Keep numbers, names and filenames unchanged. For an explicitly requested relative date, resolve it against ${java.time.LocalDate.now()} and write ISO dates. Otherwise keep the request undated. Return only the rephrased request.",
    history = history,
    question = question,
    hasImage = false
)

internal fun assistantReplyPrompt(
    request: AssistantRequest,
    grounding: String,
    history: List<AssistantMessage>,
    language: String? = request.language,
    hasImage: Boolean = false
): String = assistantPrompt(
    grounding = buildString {
        appendLine(grounding)
        appendLine("General knowledge and conversation are supported. Answer these normally without redirecting the user to app editing.")
        appendLine("Answer the latest user question directly. Use fewer than 60 words. Give workflows as at most three short steps. If a term is unfamiliar or the question is ambiguous, ask one specific clarification. Do not guess a different word, definition or spelling correction without supporting context.")
        appendLine("When asked for a capability list, use the complete supplied list rather than only examples.")
        appendLine("Include only operations needed for the request. Tool names refer to screens inside ImageToolbox, not separate apps.")
        appendLine("Use ordinary answer text. Never expose tool calls or internal routing data.")
        if (!language.isNullOrBlank()) appendLine("Response language (BCP-47): $language. Use this language even when documentation is in another language.")
        else appendLine("Respond in the language of the latest user question, even when documentation is in another language.")
    },
    history = history,
    question = request.query,
    hasImage = hasImage
)
