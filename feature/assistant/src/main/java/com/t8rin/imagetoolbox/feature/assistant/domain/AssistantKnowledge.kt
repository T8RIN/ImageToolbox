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
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen

interface AssistantKnowledge {
    suspend fun detectLanguage(request: AssistantRequest): String? = request.language

    suspend fun retrieve(
        request: AssistantRequest,
        queries: List<String> = emptyList(),
        sources: List<String> = emptyList(),
        minimumCoverage: Double? = null,
        definition: Boolean = false
    ): AssistantAnswer

    suspend fun documentation(
        request: AssistantRequest,
        reference: AssistantReference,
        workflow: Boolean = false
    ): AssistantAnswer

    fun formats(request: AssistantRequest, alpha: Boolean): AssistantAnswer

    fun capabilities(request: AssistantRequest, model: AssistantModel): AssistantAnswer

    fun clarification(request: AssistantRequest): AssistantAnswer

    fun fileSizeFits(
        request: AssistantRequest,
        actualBytes: Long,
        limitBytes: Long,
        limitLabel: String
    ): AssistantAnswer

    fun resolveTool(tool: AssistantTool): Screen?

    fun context(request: AssistantRequest): String = ""

    fun userPrompt(request: AssistantRequest, answer: AssistantAnswer): String

    fun grounding(
        request: AssistantRequest,
        answer: AssistantAnswer,
        model: AssistantModel = AssistantModel.Lite,
        hasImage: Boolean = false,
        includeToolGuide: Boolean = false
    ): String
}

internal fun AssistantAnswer.documentsSubject(request: AssistantRequest, subject: String): Boolean {
    val analyzer = AssistantSearchIndex<Unit>(emptyList(), request.language)
    if (!analyzer.containsTitle(request.query, subject)) return false
    val index = AssistantSearchIndex(
        references.map { AssistantSearchIndex.Document(it.tipId, it.title, it.text) },
        request.language
    )
    return index.search(subject, minimumCoverage = 1.0).isNotEmpty()
}
