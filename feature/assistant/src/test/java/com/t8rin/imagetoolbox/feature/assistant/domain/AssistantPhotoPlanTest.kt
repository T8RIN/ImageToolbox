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

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AssistantPhotoPlanTest {
    private fun photos(fields: String): AssistantPhotoPlan = AssistantPhotoPlan.parse(fields)

    @Test
    fun narrowKeepsEveryUnchangedConditionAndUsesOnlyVisualDetails() {
        val previous = AssistantGalleryQuery(
            "cat",
            listOf("Alex"),
            "2026-07-01",
            "2026-07-31",
            "Trips",
            "IMG*"
        )
        val plan =
            photos("""{"withinResults":true,"scene":"au soleil","people":{"Marie":"include"}}""")
        assertEquals(
            previous.copy(semantic = "cat, au soleil", people = listOf("Alex", "Marie")),
            plan.gallery(previous)
        )
    }

    @Test
    fun updatingDatesClearsTheOppositeBoundWithoutLosingOtherFilters() {
        val previous = AssistantGalleryQuery(
            people = listOf("Alex"),
            from = "2026-07-01",
            to = "2026-07-31",
            album = "Trips"
        )
        val plan = photos("""{"resetFilters":false,"from":"2026-08-01"}""")
        assertEquals(
            previous.copy(from = "2026-08-01", to = null),
            plan.gallery(previous)
        )
    }

    @Test
    fun emptyFieldsDoNotAccidentallyClearFilters() {
        val previous =
            AssistantGalleryQuery(
                "cat",
                listOf("Alex"),
                from = "2026-07-01",
                to = "2026-07-31",
                album = "Trips",
                filename = "IMG*"
            )
        assertEquals(previous, photos("""{"resetFilters":false}""").gallery(previous))
        assertEquals(
            previous,
            photos("""{"scene":"","from":"","to":""}""").gallery(previous)
        )
        val plan = photos("""{"resetFilters":false,"clear":["people","album"]}""")
        assertEquals(
            previous.copy(people = emptyList(), album = null),
            plan.gallery(previous)
        )
    }

    @Test
    fun newSearchDoesNotInheritOldConditionsOrUseMetadataAsVisualDetails() {
        val plan =
            photos("""{"resetFilters":true,"scene":"夕焼けの海岸","album":"Путешествия"}""")
        assertEquals(
            AssistantGalleryQuery(semantic = "夕焼けの海岸", album = "Путешествия"),
            plan.gallery(AssistantGalleryQuery(people = listOf("Alex")))
        )
    }

    @Test
    fun invalidAndIncompleteToolCallsCannotTurnIntoPhotoResults() {
        listOf(
            """{"from":"2026-02-30","to":"2026-03-01"}""",
            """{"from":"2026-09-01","to":"2026-08-01"}""",
            """{"exclude":["Alex"]}"""
        ).forEach { value -> assertTrue(value, runCatching { photos(value) }.isFailure) }
        assertTrue(runCatching { AssistantPhotoPlan.parse("""{"supported":false}""") }.isFailure)
        assertTrue(runCatching {
            photos("""{"withinResults":true}""").gallery(
                null
            )
        }.isFailure)
    }

    @Test
    fun excludedPeopleSurviveRefinementsAndCanBeExplicitlyCleared() {
        val previous = AssistantGalleryQuery(
            people = listOf("Marie"),
            excludedPeople = listOf("Alex"),
            album = "Trips"
        )
        val changed =
            photos("""{"from":"2026-08-01","to":"2026-08-31"}""").gallery(previous)
        assertEquals(listOf("Alex"), changed.excludedPeople)
        assertEquals(previous.people, changed.people)
        assertTrue(
            photos("""{"clear":["people"]}""").gallery(
                previous
            ).excludedPeople.isEmpty()
        )
    }

    @Test
    fun explicitInclusionAndExclusionReplaceTheOppositeIdentityCondition() {
        val previous = AssistantGalleryQuery(people = listOf("Marie", "Alex"), album = "Trips")
        val without = photos("""{"people":{"Alex":"exclude"}}""").gallery(previous)
        assertEquals(listOf("Marie"), without.people)
        assertEquals(listOf("Alex"), without.excludedPeople)
        val with = photos("""{"withinResults":false,"people":{"Alex":"include"}}""").gallery(
            without
        )
        assertEquals(listOf("Marie", "Alex"), with.people)
        assertTrue(with.excludedPeople.isEmpty())
        assertEquals("Trips", with.album)
    }

    @Test
    fun clearingOldFiltersCanApplyReplacementValuesInTheSameRequest() {
        val previous = AssistantGalleryQuery(people = listOf("Alex"), album = "Trips")
        val plan =
            photos("""{"clear":["people","album"],"album":"Family","people":{"Marie":"include"}}""")
        assertEquals(
            AssistantGalleryQuery(people = listOf("Marie"), album = "Family"),
            plan.gallery(previous)
        )
    }

    @Test
    fun nullAlbumDoesNotAccidentallyClearAnExistingCondition() {
        val previous = AssistantGalleryQuery(people = listOf("Marie"), album = "Trips")
        assertEquals(previous, photos("""{"album":null}""").gallery(previous))
    }

}
