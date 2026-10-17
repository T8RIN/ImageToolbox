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

package com.t8rin.imagetoolbox.feature.media_picker.domain

import com.t8rin.imagetoolbox.core.settings.domain.model.SemanticSearchSettings
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.Media
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.SemanticSearchCategoryResult
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.SemanticSearchState
import kotlinx.coroutines.flow.StateFlow

interface SemanticSearchRepository {

    val state: StateFlow<SemanticSearchState>

    fun updateWorkState(isQueued: Boolean, isWaitingForPermission: Boolean, error: String? = null)

    suspend fun refresh()

    suspend fun downloadModels()

    suspend fun index(media: List<Media>)

    suspend fun search(
        query: String,
        media: List<Media>,
        minimumSimilarity: Float = 0.26f
    ): List<Media>

    suspend fun searchByImage(
        uri: String,
        media: List<Media>,
        minimumSimilarity: Float = 0.7f
    ): List<Media>

    suspend fun categories(
        media: List<Media>,
        settings: SemanticSearchSettings = SemanticSearchSettings()
    ): List<SemanticSearchCategoryResult>

    suspend fun previewCategory(
        media: List<Media>,
        settings: SemanticSearchSettings,
        categoryId: String
    ): SemanticSearchCategoryResult?

    suspend fun prepareCategoryReferences(
        settings: SemanticSearchSettings
    ): SemanticSearchSettings

    suspend fun cleanupCategoryReferences(settings: SemanticSearchSettings)

    suspend fun clear()

}