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

data class MediaState(
    val media: List<Media> = emptyList(),
    val mappedMedia: List<MediaItem> = emptyList(),
    val error: String = "",
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false
)

fun MediaState.updateMedia(uri: String, transform: (Media) -> Media?): MediaState =
    updateMedia(setOf(uri), transform)

fun MediaState.updateMedia(uris: Set<String>, transform: (Media) -> Media?): MediaState {
    fun Media.updated() = if (uri in uris) transform(this) else this

    return copy(
        media = media.mapNotNull { it.updated() },
        mappedMedia = mappedMedia.mapNotNull { item ->
            when (item) {
                is MediaItem.Header -> item.data.mapNotNull { it.updated() }
                    .takeIf { it.isNotEmpty() }?.let { item.copy(data = it) }

                is MediaItem.MediaViewItem -> item.media.updated()?.let { item.copy(media = it) }
            }
        }
    )
}

data class AlbumState(
    val albums: List<Album> = emptyList(),
    val error: String = ""
)