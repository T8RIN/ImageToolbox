/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.feature.media_picker.domain

import kotlinx.coroutines.flow.Flow
import ru.tech.imageresizershrinker.feature.media_picker.domain.model.Album
import ru.tech.imageresizershrinker.feature.media_picker.domain.model.AllowedMedia
import ru.tech.imageresizershrinker.feature.media_picker.domain.model.Media

interface MediaRetriever {

    fun getAlbumsWithType(
        allowedMedia: AllowedMedia
    ): Flow<Result<List<Album>>>

    fun mediaFlowWithType(
        albumId: Long,
        allowedMedia: AllowedMedia
    ): Flow<Result<List<Media>>>

    fun getMediaByAlbumIdWithType(
        albumId: Long,
        allowedMedia: AllowedMedia
    ): Flow<Result<List<Media>>>

    fun getMediaByType(
        allowedMedia: AllowedMedia
    ): Flow<Result<List<Media>>>

}