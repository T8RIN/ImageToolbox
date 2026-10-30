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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AssistantCommandTest {
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
    fun aCompleteSearchCanRetainPreviousPeopleAlongsideANewCondition() {
        val grammar = AssistantCommand.grammar(
            AssistantGalleryQuery(people = listOf("Marie")),
            AssistantGalleryCatalog(people = listOf("Marie", "Alex")),
            "Without Alex"
        )
        assertTrue(grammar.contains("Alex"))
        assertTrue(grammar.contains("Marie"))
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
        assertTrue(update.contains("Marie"))
        assertTrue(update.contains("Trips"))
    }

    private fun search(query: String, mode: String = "new") = AssistantCommand.parse(
        """{"action":"search","mode":"$mode","query":$query}"""
    ) as AssistantCommand.Action.SearchPhotos

    @Test
    fun completeSearchesKeepEveryFilter() {
        val command =
            search("""{"from":"2026-07-01","to":"2026-07-31","album":"Trips","people":["Marie"],"excludedPeople":["Alex"]}""")
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
        assertEquals("2026-07-01", search("""{"from":"2026-07-01","to":null}""").query.from)
        listOf(
            "{}",
            """{"action":"unknown"}""",
            """{"action":"search","mode":"new","query":{"unknown":true}}""",
            """{"action":"ask","question":""}""",
            """{"action":"search","mode":"new","query":{"from":"2026-99-99"}}"""
        ).forEach {
            assertTrue(it, runCatching { AssistantCommand.parse(it) }.isFailure)
        }
    }

    @Test
    fun removalAndNarrowingHaveAnExplicitRepresentation() {
        val command = search(
            """{"semantic":"red umbrella","people":["Marie"],"from":null,"to":null,"album":"Trips"}""",
            "narrow"
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
    fun paginationAndClarificationAreNotSearchFilters() {
        assertEquals(
            AssistantCommand.Action.MorePhotos,
            AssistantCommand.parse("""{"action":"more"}""")
        )
        assertEquals(
            AssistantCommand.Action.Answer("Which album?"),
            AssistantCommand.parse("""{"action":"ask","question":"Which album?"}""")
        )
        assertTrue(runCatching { AssistantCommand.parse("""{"action":"more","semantic":"cats"}""") }.isFailure)
    }
}
