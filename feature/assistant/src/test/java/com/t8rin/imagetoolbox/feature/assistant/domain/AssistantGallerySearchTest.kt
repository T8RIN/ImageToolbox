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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class AssistantGallerySearchTest {
    @Test
    fun metadataFiltersWorkWithEveryAiCapabilityDisabled() {
        val query =
            AssistantGalleryQuery(from = "2026-07-01", album = "Vacation", filename = "*.jpg")
        assertNull(gallerySearchBlocker(query, false, false, SemanticSearchState()))
        assertNull(
            gallerySearchBlocker(
                query,
                true,
                false,
                SemanticSearchState(isDownloading = true)
            )
        )
    }

    @Test
    fun disabledFunctionsAreCheckedBeforeModelsAndCannotDropPeople() {
        val query = AssistantGalleryQuery(semantic = "a beach", people = listOf("Саша"))
        val ready =
            SemanticSearchState(modelsReady = true, faceModelsReady = true, indexedImages = 50)
        assertEquals(
            R.string.assistant_gallery_disabled_people,
            gallerySearchBlocker(query, false, false, ready)
        )
        assertEquals(
            R.string.assistant_gallery_disabled_people,
            gallerySearchBlocker(query, false, true, ready)
        )
        assertEquals(
            R.string.assistant_gallery_people_disabled,
            gallerySearchBlocker(query, true, false, ready)
        )
        assertNull(gallerySearchBlocker(query, true, true, ready))
        assertEquals(
            R.string.assistant_gallery_disabled,
            gallerySearchBlocker(query.copy(people = emptyList()), false, false, ready)
        )
    }

    @Test
    fun distinguishesModelDownloadMissingModelsPermissionAndIndexing() {
        val query = AssistantGalleryQuery(semantic = "a cat")
        val ready = SemanticSearchState(modelsReady = true, indexedImages = 50)
        assertEquals(
            R.string.assistant_gallery_downloading,
            gallerySearchBlocker(query, true, false, ready.copy(isDownloading = true))
        )
        assertEquals(
            R.string.assistant_gallery_models_missing,
            gallerySearchBlocker(query, true, false, ready.copy(modelsReady = false))
        )
        assertEquals(
            R.string.assistant_gallery_permission,
            gallerySearchBlocker(query, true, false, ready.copy(isWaitingForPermission = true))
        )
        assertNull(gallerySearchBlocker(query, true, false, ready.copy(isIndexing = true)))
        val person = query.copy(people = listOf("Саша"))
        assertEquals(
            R.string.assistant_gallery_models_missing,
            gallerySearchBlocker(person, true, true, ready)
        )
        assertEquals(
            R.string.assistant_gallery_downloading,
            gallerySearchBlocker(person, true, true, ready.copy(isFacesDownloading = true))
        )
    }

    @Test
    fun datesAlbumFilenameAndWholePreviousSelectionAreIntersectedBeforeRanking() {
        val media = (1..30).map { photo(it) }
        val query = AssistantGalleryQuery(
            semantic = "a beach", from = "2026-07-01",
            to = "2026-07-31", album = "Vacation", filename = "IMG*"
        )
        // Photo 25 is outside the first page of twelve thumbnails, but inside the search.
        val allPreviousMatches = media.take(25).mapTo(hashSetOf(), Media::uri)
        val result = filterGalleryMedia(media, query, allPreviousMatches, ZoneId.of("UTC"))
        assertEquals((1..25).map { "content://media/$it" }, result.map(Media::uri))
        assertTrue(filterGalleryMedia(media, query.copy(album = "Other")).isEmpty())
        assertTrue(filterGalleryMedia(media, query, emptySet()).isEmpty())
    }

    @Test
    fun dateTakenUsesMillisecondsAndFallbackUsesModifiedSecondsInLocalTimezone() {
        val photo = photo(1)
        val query = AssistantGalleryQuery(from = "2026-08-01", to = "2026-08-01")
        val zone = ZoneId.of("Europe/Moscow")
        val midnight = LocalDate.parse("2026-08-01").atStartOfDay(zone).toInstant().toEpochMilli()
        assertEquals(
            1,
            filterGalleryMedia(
                listOf(photo.copy(takenTimestamp = midnight)),
                query,
                zone = zone
            ).size
        )
        assertEquals(
            1,
            filterGalleryMedia(
                listOf(photo.copy(takenTimestamp = 0, timestamp = midnight / 1000)),
                query,
                zone = zone
            ).size
        )
        assertTrue(
            filterGalleryMedia(
                listOf(photo.copy(takenTimestamp = midnight - 1)),
                query,
                zone = zone
            ).isEmpty()
        )
    }

    private fun photo(id: Int) = Media(
        id = id.toLong(), label = "IMG_$id.jpg", uri = "content://media/$id",
        path = "/Pictures/IMG_$id.jpg", relativePath = "Pictures/", albumID = 1,
        albumLabel = "Vacation", timestamp = 0,
        takenTimestamp = LocalDate.parse("2026-07-15").atStartOfDay(ZoneId.of("UTC")).toInstant()
            .toEpochMilli(),
        mimeType = "image/jpeg"
    )
}
