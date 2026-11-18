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

import com.t8rin.imagetoolbox.feature.media_picker.domain.model.GalleryOcrState
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.Media
import kotlinx.coroutines.flow.StateFlow

interface GalleryOcrRepository {

    val state: StateFlow<GalleryOcrState>

    fun updateWorkState(isQueued: Boolean? = null, isWaitingForPermission: Boolean? = null)

    suspend fun downloadModels()

    suspend fun index(
        media: List<Media>,
        pruneMissing: Boolean,
        shouldYield: () -> Boolean
    ): Boolean

    /** A null query matches any recognized text. */
    suspend fun search(query: String?, media: List<Media>): List<Media>

    suspend fun hasPending(media: List<Media>): Boolean

}
