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

import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.Media
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.SemanticSearchState
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

internal fun gallerySearchBlocker(
    query: AssistantGalleryQuery,
    enabled: Boolean,
    groupPeople: Boolean,
    state: SemanticSearchState
): Int? {
    val people = query.people.isNotEmpty() || query.excludedPeople.isNotEmpty()
    if (query.semantic == null && !people) return null
    return when {
        !enabled -> if (people) R.string.assistant_gallery_disabled_people else R.string.assistant_gallery_disabled
        people && !groupPeople -> R.string.assistant_gallery_people_disabled
        state.isDownloading || people && state.isFacesDownloading -> R.string.assistant_gallery_downloading
        !state.modelsReady || people && !state.faceModelsReady -> R.string.assistant_gallery_models_missing
        state.isWaitingForPermission -> R.string.assistant_gallery_permission
        else -> null
    }
}

internal fun filterGalleryMedia(
    media: List<Media>,
    query: AssistantGalleryQuery,
    source: Set<String>? = null,
    zone: ZoneId = ZoneId.systemDefault()
): List<Media> {
    val from = query.from?.let(LocalDate::parse)
    val to = query.to?.let(LocalDate::parse)
    val filenamePattern = query.filename?.takeIf { '*' in it || '?' in it }?.let { pattern ->
        Regex(pattern.map { character ->
            when (character) {
                '*' -> ".*"
                '?' -> "."
                else -> Regex.escape(character.toString())
            }
        }.joinToString(""), RegexOption.IGNORE_CASE)
    }
    return media.filter { photo ->
        val date = Instant.ofEpochMilli(photo.takenTimestamp?.takeIf { it > 0 }
            ?: (photo.timestamp * 1000))
            .atZone(zone).toLocalDate()
        (source == null || photo.uri in source) &&
                (from == null || date >= from) && (to == null || date <= to) &&
                (query.album == null || photo.albumLabel.equals(query.album, true)) &&
                (query.filename == null || if (filenamePattern != null) filenamePattern.matches(
                    photo.label
                ) else photo.label.contains(query.filename, true))
    }
}
