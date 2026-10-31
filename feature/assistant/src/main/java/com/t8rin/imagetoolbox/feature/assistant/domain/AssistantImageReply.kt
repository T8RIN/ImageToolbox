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
import java.util.Locale

internal object AssistantImageReply {
    fun prompt(request: AssistantRequest, grounding: String, model: AssistantModel): String =
        assistantPrompt(
            grounding = "Answer the image question directly in one short sentence. Use visible facts only. Do not read lettering or identifiers unless asked. Reply in ${
                request.language?.let {
                    Locale.forLanguageTag(
                        it
                    ).getDisplayLanguage(Locale.ENGLISH)
                } ?: "the language of the question"
            } unless the user explicitly requests another language.",
            history = emptyList(),
            question = "$grounding\nQuestion: ${request.query}",
            hasImage = true,
            model = model
        )

    fun parse(text: String): String = AssistantCommand.visibleText(text).trim().also {
        require(it.isNotEmpty() && !AssistantCommand.isInternalReply(it))
    }
}
