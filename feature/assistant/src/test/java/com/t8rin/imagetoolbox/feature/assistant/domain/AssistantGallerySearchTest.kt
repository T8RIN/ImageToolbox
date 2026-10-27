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
    fun parsesCombinedConditionsAndRejectsInventedFields() {
        val command = AssistantGalleryCommand.parse(
            """
            {"action":"search","semantic":"a beach","people":["Саша","Маша"],
            "from":"2026-08-01","to":"2026-08-31","album":"Vacation","filename":"IMG*"}
        """.trimIndent()
        )
        assertEquals(listOf("Саша", "Маша"), command.query!!.people)
        assertEquals("Vacation", command.query!!.album)
        listOf(
            """{"action":"search","uri":"content://invented"}""",
            """{"action":"search","from":"2026-02-30"}""",
            """{"action":"search","from":"2026-09-01","to":"2026-08-01"}""",
            """{"action":"search","semantic":""}""",
            """{}""",
            """{"action":"search"}""",
            """Some prose {"action":"more"}"""
        ).forEach { assertTrue(it, runCatching { AssistantGalleryCommand.parse(it) }.isFailure) }
        assertEquals(
            AssistantGalleryCommand.Action.More,
            AssistantGalleryCommand.parse("```json\n{\"action\":\"more\"}\n```").action
        )
        assertTrue(runCatching { AssistantGalleryCommand.parse("{\"action\":\"delete\"}") }.isFailure)
        assertTrue(runCatching { AssistantGalleryCommand.parse("{\"action\":\"help\",\"people\":[\"Саша\"]}") }.isFailure)
    }

    @Test
    fun routesOnlySearchRequestsAndGalleryFollowUpsThroughTheGalleryInterpreter() {
        listOf(
            "Как замазать лица?",
            "Хочу прозрачный фон",
            "Напиши стих",
            "Из фото сделать GIF"
        ).forEach {
            assertFalse(it, needsGalleryRouting(it, null))
        }
        listOf("Найди фото кота", "Покажи Сашу", "Where is Sasha?").forEach {
            assertTrue(it, needsGalleryRouting(it, null))
        }
        assertTrue(
            needsGalleryRouting(
                "Из этих только за июль",
                AssistantGalleryQuery(semantic = "a cat")
            )
        )
        assertTrue(needsGalleryRouting("Ещё", AssistantGalleryQuery(semantic = "a cat")))
    }

    @Test
    fun explicitReferencesCannotStartANewSearch() {
        val command =
            AssistantGalleryCommand(AssistantGalleryCommand.Action.Search, album = "Vacation")
        assertTrue(command.isRefinement("Из этих только из альбома Vacation"))
        assertTrue(command.isRefinement("А где он на улице?"))
        assertTrue(command.isRefinement("Of these, only July"))
        assertFalse(command.isRefinement("Покажи фото из альбома Vacation"))
        assertFalse(
            command.copy(action = AssistantGalleryCommand.Action.Help).isRefinement("Из этих")
        )
    }

    @Test
    fun trimsFiltersAndDeduplicatesPeople() {
        val query = AssistantGalleryCommand.parse(
            """
            {"action":"search","semantic":" a beach ","people":[" Саша ","Саша"],
            "album":" Vacation ","from":" 2026-07-01 "}
        """.trimIndent()
        ).query!!
        assertEquals("a beach", query.semantic)
        assertEquals(listOf("Саша"), query.people)
        assertEquals("Vacation", query.album)
        assertEquals("2026-07-01", query.from)
    }

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

    @Test
    fun refinementRetainsPeopleAndOtherConditionsAndPromptDoesNotIncludePhotoUris() {
        val previous = AssistantGalleryQuery(
            semantic = "a beach", people = listOf("Саша"),
            from = "2026-07-01", to = "2026-07-31", album = "Vacation", filename = "IMG*"
        )
        val refined =
            AssistantGalleryQuery(semantic = "outdoors", people = listOf("Маша")).refine(previous)
        assertEquals(listOf("Саша", "Маша"), refined.people)
        assertEquals(previous.album, refined.album)
        assertEquals(previous.from, refined.from)
        assertEquals(previous.filename, refined.filename)
        val prompt = assistantGalleryPrompt("А где он на улице?", "semanticEnabled=false", previous)
        assertTrue(prompt.contains("semanticEnabled=false"))
        assertTrue(prompt.contains("Саша"))
        assertFalse(prompt.contains("content://"))
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
