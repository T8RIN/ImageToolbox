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

internal object AssistantResponse {
    const val APP = "[APP]"
    const val OTHER = "[OTHER]"

    fun answer(output: String, source: AssistantAnswer, scopeNotice: String): AssistantAnswer {
        val value = output.trimStart()
        return when {
            value.startsWith(OTHER) -> AssistantAnswer(scopeNotice, isScopeNotice = true)
            APP.startsWith(value) || OTHER.startsWith(value) -> AssistantAnswer("")
            else -> source.copy(text = text(value, scopeNotice).trim())
        }
    }

    fun text(output: String, scopeNotice: String): String {
        val text = output.trimStart()
        return when {
            text.startsWith(APP) -> text.removePrefix(APP).trimStart()
            text.startsWith(OTHER) -> scopeNotice
            APP.startsWith(text) || OTHER.startsWith(text) -> ""
            else -> text
        }
    }
}
