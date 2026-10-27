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
import com.t8rin.neural_tools.assistant.AssistantModels
import kotlinx.coroutines.flow.StateFlow

interface AssistantRepository {
    val messages: StateFlow<List<AssistantMessage>>
    val installations: StateFlow<List<AssistantModels.ModelState>>
    val isLoading: StateFlow<Boolean>
    val isCheckingModels: StateFlow<Boolean>
    val isGenerating: StateFlow<Boolean>
    val error: StateFlow<String?>

    fun send(request: AssistantRequest): Boolean

    fun regenerate()

    fun newConversation()

    fun stop()

    fun releaseWhenIdle()

    fun canContinueGallery(message: AssistantMessage): Boolean

    fun chooseGalleryPerson(message: AssistantMessage, personId: Long)

    suspend fun downloadModel(model: AssistantModel)

    fun cancelDownload(model: AssistantModel)

    fun deleteModel(model: AssistantModel)

    companion object {
        const val MAX_QUERY_LENGTH = 4_000
        const val MAX_SELECTED_IMAGES = 500
    }
}