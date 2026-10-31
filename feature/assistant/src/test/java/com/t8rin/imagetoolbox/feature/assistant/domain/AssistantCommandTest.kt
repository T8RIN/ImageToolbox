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
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AssistantCommandTest {
    @Test
    fun relativeCalendarPeriodsAreCalculatedAtTheRequestedPrecision() {
        fun period(unit: String, offset: Int) = (
                AssistantCommand.parse(
                    """{"action":"search","mode":"new","query":{"date":{"unit":"$unit","offset":$offset}}}""",
                    referenceDate = java.time.LocalDate.of(2024, 3, 1)
                ) as AssistantCommand.Action.SearchPhotos).query
        assertEquals("2024-02-29", period("day", -1).from)
        assertEquals("2024-02-29", period("day", -1).to)
        assertEquals("2024-02-01", period("month", -1).from)
        assertEquals("2024-02-29", period("month", -1).to)
        assertEquals("2023-01-01", period("year", -1).from)
        assertEquals("2023-12-31", period("year", -1).to)
        assertEquals("2024-03-02", period("day", 1).from)
        assertEquals("2024-02-19", period("week", -1).from)
        assertEquals("2024-02-25", period("week", -1).to)
        val range = AssistantCommand.parse(
            """{"action":"search","mode":"new","query":{"date":{"fromOffset":-1,"toOffset":1}}}""",
            referenceDate = java.time.LocalDate.of(2024, 3, 1)
        ) as AssistantCommand.Action.SearchPhotos
        assertEquals("2024-02-29", range.query.from)
        assertEquals("2024-03-02", range.query.to)
    }

    @Test
    fun nullPlaceholdersPreserveConditionsUntilExplicitlyCleared() {
        val current = AssistantGalleryQuery(
            semantic = "red umbrella", people = listOf("Marie"),
            from = "2026-07-01", to = "2026-07-31", album = "Trips", filename = "scan.png"
        )
        val unchanged = AssistantCommand.parse(
            """{"action":"search","mode":"update","query":{"scene":null,"people":null,"date":null,"album":null,"filename":null,"clear":null}}""",
            current
        ) as AssistantCommand.Action.SearchPhotos
        assertEquals(current, unchanged.query)
        val cleared = AssistantCommand.parse(
            """{"action":"search","mode":"update","query":{"date":null,"clear":["date"]}}""",
            current
        ) as AssistantCommand.Action.SearchPhotos
        assertEquals(current.copy(from = null, to = null), cleared.query)
    }

    @Test
    fun aMonthOrYearCoversTheWholeRequestedPeriod() {
        val month =
            AssistantCommand.parse("""{"action":"search","mode":"new","query":{"date":{"unit":"month","value":"2024-02-29"}}}""") as AssistantCommand.Action.SearchPhotos
        assertEquals("2024-02-01", month.query.from)
        assertEquals("2024-02-29", month.query.to)
        val year =
            AssistantCommand.parse("""{"action":"search","mode":"new","query":{"date":{"unit":"year","value":"2024"}}}""") as AssistantCommand.Action.SearchPhotos
        assertEquals("2024-01-01", year.query.from)
        assertEquals("2024-12-31", year.query.to)
    }

    @Test
    fun catalogNamesIncludeInflectedAndExactNamesInTheSameRequest() {
        assertEquals(
            listOf("Саша", "Анна"),
            AssistantCommand.matching(listOf("Саша", "Анна", "Марина"), "Саша без Анны", "ru")
        )
        assertEquals(
            listOf("Дом"),
            AssistantCommand.matching(listOf("Дом", "Море"), "в альбоме Дома", "ru")
        )
    }

    @Test
    fun internalPlansAreNeverRenderedDuringStreaming() {
        val call = """{"action":"search","scene":"a beach"}"""
        assertEquals(call, AssistantCommand.visibleText(call))
        assertTrue(AssistantCommand.isInternalReply(call))
        assertTrue(AssistantCommand.isInternalReply("""{"query":"a beach","search_photos":true}"""))
        assertTrue(AssistantCommand.isInternalReply("""{"name":"search_photos","arguments":{}}"""))
        assertEquals("普通の回答", AssistantCommand.visibleText("普通の回答"))
    }

    @Test
    fun reasoningRemainsHiddenUntilTheActualAnswerStarts() {
        assertEquals("", AssistantCommand.visibleText("<think>checking the request"))
        assertEquals("Answer", AssistantCommand.visibleText("<think>checking</think>\nAnswer"))
        assertEquals("Answer", AssistantCommand.visibleText("checking</think>\nAnswer"))
        assertEquals(
            "Answer",
            AssistantCommand.visibleText("<think>first</think>checking</think>\nAnswer")
        )
    }

    @Test
    fun onlyMentionedPeopleAreExposedToTheModelForChanges() {
        val grammar = AssistantCommand.grammar(
            AssistantGalleryQuery(people = listOf("Marie")),
            AssistantGalleryCatalog(people = listOf("Marie", "Alex")),
            "Without Alex"
        )
        assertTrue(grammar.contains("Alex"))
        assertFalse(grammar.contains("Marie"))
    }

    @Test
    fun peopleConditionsCanOnlyBeRemovedWhenTheyExist() {
        val catalog = AssistantGalleryCatalog(people = listOf("Marie", "Alex"))
        fun roles(current: AssistantGalleryQuery): String = AssistantCommand.grammar(
            current, catalog, "Without Alex"
        ).lineSequence().first { it.startsWith("search_update:") }
        assertFalse(roles(AssistantGalleryQuery(people = listOf("Marie"))).contains("remove"))
        assertTrue(roles(AssistantGalleryQuery(excludedPeople = listOf("Alex"))).contains("remove"))
    }

    @Test
    fun anIndependentSearchDoesNotInheritUnmentionedPeopleAndAlbums() {
        val grammar = AssistantCommand.grammar(
            AssistantGalleryQuery(people = listOf("Marie"), album = "Trips"),
            AssistantGalleryCatalog(people = listOf("Marie"), albums = listOf("Trips")),
            "Start an independent search for cats"
        )
        val independent = grammar.lineSequence().first { it.startsWith("search_new:") }
        val update = grammar.lineSequence().first { it.startsWith("search_update:") }
        assertFalse(independent.contains("Marie"))
        assertFalse(independent.contains("Trips"))
        assertFalse(update.contains("Marie"))
        assertTrue(update.contains("Trips"))
    }

    private fun search(
        query: String,
        mode: String = "new",
        current: AssistantGalleryQuery? = null
    ): AssistantCommand.Action.SearchPhotos {
        val defaults = Json.parseToJsonElement(
            """{"people":{},"clear":[],"album":null,"scene":null,"filename":null}"""
        ).jsonObject
        val fields = JsonObject(defaults + Json.parseToJsonElement(query).jsonObject)
        return AssistantCommand.parse(
            """{"action":"search","mode":"$mode","query":$fields}""", current
        ) as AssistantCommand.Action.SearchPhotos
    }

    @Test
    fun completeSearchesKeepEveryFilter() {
        val command =
            search("""{"album":"Trips","people":{"Marie":"include","Alex":"exclude"},"date":{"from":"2026-07-01","to":"2026-07-31"}}""")
        assertEquals(
            AssistantGalleryQuery(
                people = listOf("Marie"),
                excludedPeople = listOf("Alex"),
                from = "2026-07-01",
                to = "2026-07-31",
                album = "Trips"
            ), command.query
        )
        assertEquals(AssistantGallerySearch.Mode.New, command.mode)
    }

    @Test
    fun openIntervalsAndInvalidPlansAreHandledExplicitly() {
        assertEquals(
            "2026-07-01",
            search("""{"date":{"from":"2026-07-01","to":null}}""").query.from
        )
        listOf(
            "{}",
            """{"action":"unknown"}""",
            """{"action":"search","mode":"new","query":{"unknown":true}}""",
            """{"action":"ask","question":""}""",
            """{"action":"search","mode":"new","query":{"date":{"from":"2026-99-99","to":null}}}"""
        ).forEach {
            assertTrue(it, runCatching { AssistantCommand.parse(it) }.isFailure)
        }
    }

    @Test
    fun narrowingRequiresAnActualFilterInsteadOfAnEmptyChange() {
        val current = AssistantGalleryQuery(album = "Trips")
        assertTrue(runCatching {
            AssistantCommand.parse(
                """{"action":"search","mode":"narrow","query":{"scene":null}}""", current
            )
        }.isFailure)
        val query = search("""{"scene":"bicycles"}""", "narrow", current).query
        assertEquals(current.copy(semantic = "bicycles"), query)
    }

    @Test
    fun removalAndNarrowingHaveAnExplicitRepresentation() {
        val command = search(
            """{"scene":"red umbrella","people":{"Marie":"include"},"album":"Trips"}""",
            "narrow",
            current = AssistantGalleryQuery()
        )
        assertEquals(
            AssistantGalleryQuery(
                semantic = "red umbrella",
                people = listOf("Marie"),
                album = "Trips"
            ), command.query
        )
        assertEquals(AssistantGallerySearch.Mode.Narrow, command.mode)
    }

    @Test
    fun personActionsPreserveUnmentionedIdentitiesAndRemoveBothKindsOfCondition() {
        val current = AssistantGalleryQuery(
            people = listOf("Marie"), excludedPeople = listOf("Alex"), album = "Trips"
        )
        assertEquals(
            current.copy(excludedPeople = emptyList()),
            search("""{"people":{"Alex":"remove"},"album":"Trips"}""", "update", current).query
        )
        assertEquals(
            current,
            search("""{"album":"Trips"}""", "update", current).query
        )
        assertEquals(
            current.copy(people = listOf("Marie", "Alex"), excludedPeople = emptyList()),
            search("""{"people":{"Alex":"include"},"album":"Trips"}""", "update", current).query
        )
        assertEquals(
            AssistantGalleryQuery(album = "Trips"),
            search("""{"clear":["people"],"album":"Trips"}""", "update", current).query
        )
        assertEquals(
            AssistantGalleryQuery(semantic = "cats"),
            search("""{"scene":"cats"}""", "new", current).query
        )
    }

    @Test
    fun calendarBoundariesDoNotDependOnTheModelCountingDays() {
        assertEquals(
            AssistantGalleryQuery(from = "2026-10-30", to = "2026-10-30"),
            search("""{"date":"2026-10-30"}""").query
        )
        assertEquals(
            AssistantGalleryQuery(from = "2024-02-01", to = "2024-02-29"),
            search("""{"date":"2024-02"}""").query
        )
        assertEquals(
            AssistantGalleryQuery(from = "2026-01-01", to = "2026-12-31"),
            search("""{"date":"2026"}""").query
        )
    }

    @Test
    fun anEmptyPatchPreservesEveryCondition() {
        val current = AssistantGalleryQuery(
            people = listOf("Marie"),
            excludedPeople = listOf("Alex"),
            album = "Trips"
        )
        assertEquals(current, search("{}", "update", current).query)
    }

    @Test
    fun paginationAndClarificationAreNotSearchFilters() {
        assertEquals(
            AssistantCommand.Action.MorePhotos,
            AssistantCommand.parse("""{"action":"more"}""")
        )
        assertEquals(
            AssistantCommand.Action.Answer("Which album?"),
            AssistantCommand.parse("""{"action":"ask","question":"Which album?"}""")
        )
        assertTrue(runCatching { AssistantCommand.parse("""{"action":"more","scene":"cats"}""") }.isFailure)
    }
}
