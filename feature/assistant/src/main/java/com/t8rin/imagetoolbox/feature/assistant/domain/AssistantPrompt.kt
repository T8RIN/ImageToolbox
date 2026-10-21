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
    hasImage: Boolean
): String = buildString {
    append("<|im_start|>system\n${grounding.escapeTemplate()}<|im_end|>\n")
    history.filter { it.error == null && it.answer.text.isNotBlank() }.takeLast(2)
        .forEach { message ->
            append("<|im_start|>user\n")
            if (message.context.selectedUris.isNotEmpty()) {
                append("Files were attached to this earlier message; their contents are not included here.\n")
            }
            append(message.question.take(300).escapeTemplate())
            append("<|im_end|>\n<|im_start|>assistant\n")
            append(message.answer.text.take(400).escapeTemplate())
            append("<|im_end|>\n")
        }
    append("<|im_start|>user\n")
    if (hasImage) append("<|vision_start|><|image_pad|><|vision_end|>\n")
    append(question.escapeTemplate())
    append("<|im_end|>\n<|im_start|>assistant\n<think>\n\n</think>\n\n")
}

private fun String.escapeTemplate(): String = replace("<|", "＜|")