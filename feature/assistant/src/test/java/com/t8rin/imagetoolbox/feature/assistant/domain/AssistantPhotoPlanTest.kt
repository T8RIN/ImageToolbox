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

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import org.junit.Assert.*
import org.junit.Test

class AssistantPhotoPlanTest {
    private fun photos(fields: String): AssistantPhotoPlan {
        val defaults = Json.parseToJsonElement("""{"supported":true}""").jsonObject
        return AssistantPhotoPlan.parse(
            kotlinx.serialization.json.JsonObject(
                defaults + Json.parseToJsonElement(
                    fields
                ).jsonObject
            ).toString()
        )
    }

    @Test
    fun narrowKeepsEveryUnchangedConditionAndUsesTheOriginalSceneRequest() {
        val previous = AssistantGalleryQuery(
            "cat",
            listOf("Alex"),
            "2026-07-01",
            "2026-07-31",
            "Trips",
            "IMG*"
        )
        val plan = photos("""{"withinResults":true,"sceneChanged":true,"people":["Marie"]}""")
        assertEquals(
            previous.copy(semantic = "au soleil", people = listOf("Alex", "Marie")),
            plan.gallery(previous, "au soleil")
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
            plan.gallery(previous, "since August")
        )
    }

    @Test
    fun emptyFieldsDoNotAccidentallyClearFilters() {
        val previous =
            AssistantGalleryQuery("cat", listOf("Alex"), album = "Trips", filename = "IMG*")
        assertEquals(previous, photos("""{"resetFilters":false}""").gallery(previous, "continue"))
        val plan = photos("""{"resetFilters":false,"clear":["People","Album"]}""")
        assertEquals(
            AssistantGalleryQuery("cat", filename = "IMG*"),
            plan.gallery(previous, "clear people and album")
        )
    }

    @Test
    fun newSearchDoesNotInheritOldConditionsAndNeverTranslatesTheRequest() {
        val plan = photos("""{"resetFilters":true,"sceneChanged":true,"album":"Путешествия"}""")
        assertEquals(
            AssistantGalleryQuery(semantic = "夕焼けの海岸", album = "Путешествия"),
            plan.gallery(AssistantGalleryQuery(people = listOf("Alex")), "夕焼けの海岸")
        )
    }

    @Test
    fun invalidAndIncompleteToolCallsCannotTurnIntoPhotoResults() {
        listOf(
            """{"from":"2026-02-30","to":"2026-03-01"}""",
            """{"from":"2026-09-01","to":"2026-08-01"}""",
            """{"exclude":["Alex"]}"""
        ).forEach { value -> assertTrue(value, runCatching { photos(value) }.isFailure) }
        assertTrue(runCatching { AssistantPhotoPlan.parse("""{"resetFilters":true}""") }.isFailure)
        assertTrue(runCatching {
            photos("""{"withinResults":true}""").gallery(
                null,
                "more"
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
            photos("""{"from":"2026-08-01","to":"2026-08-31"}""").gallery(previous, "August")
        assertEquals(listOf("Alex"), changed.excludedPeople)
        assertEquals(previous.people, changed.people)
        assertTrue(
            photos("""{"clear":["People"]}""").gallery(
                previous,
                "clear people"
            ).excludedPeople.isEmpty()
        )
    }

    @Test
    fun parsesTheNativeToolCallCompletion() {
        val plan =
            (AssistantTools.parse("""<tool_call>{"name":"find_photos","arguments":{"supported":true,"excludedPeople":["Alex"]}}</tool_call>""") as AssistantTools.Result.Photos).plan
        assertEquals(listOf("Alex"), plan.gallery(null, "sans Alex").excludedPeople)
    }

}
