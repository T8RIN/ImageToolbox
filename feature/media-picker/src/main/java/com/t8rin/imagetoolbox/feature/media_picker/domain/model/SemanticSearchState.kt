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

package com.t8rin.imagetoolbox.feature.media_picker.domain.model

data class SemanticSearchState(
    val modelsReady: Boolean = false,
    val isQueued: Boolean = false,
    val isWaitingForPermission: Boolean = false,
    val isDownloading: Boolean = false,
    val downloadProgress: Float = 0f,
    val isIndexing: Boolean = false,
    val processedImages: Int = 0,
    val totalImages: Int = 0,
    val failedImages: Int = 0,
    val indexedImages: Int = 0,
    val indexRevision: Long = 0L,
    val faceModelsReady: Boolean = false,
    val isFacesIndexing: Boolean = false,
    val hiddenPeopleCount: Int = 0,
    val isFacesDownloading: Boolean = false,
    val facesDownloadProgress: Float = 0f,
    val error: String? = null
)