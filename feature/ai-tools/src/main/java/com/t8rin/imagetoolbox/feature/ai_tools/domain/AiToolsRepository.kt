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

package com.t8rin.imagetoolbox.feature.ai_tools.domain

import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.feature.ai_tools.domain.model.NeuralDownloadProgress
import com.t8rin.imagetoolbox.feature.ai_tools.domain.model.NeuralModel
import com.t8rin.imagetoolbox.feature.ai_tools.domain.model.NeuralParams
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface AiToolsRepository<Image> {
    val occupiedStorageSize: StateFlow<Long>

    val downloadedModels: StateFlow<List<NeuralModel>>

    val selectedModel: StateFlow<NeuralModel?>

    suspend fun selectModel(
        model: NeuralModel?,
        forced: Boolean = false
    ): Boolean

    fun downloadModel(
        model: NeuralModel
    ): Flow<NeuralDownloadProgress>

    suspend fun importModel(
        uri: String
    ): SaveResult

    suspend fun processImage(
        image: Image,
        listener: AiProgressListener,
        params: NeuralParams
    ): Image?

    suspend fun deleteModel(model: NeuralModel)

    fun cleanup()

}