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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AssistantCommandTest {
    @Test
    fun toolsAreNeverRenderedInStreamingTextEvenAfterAnIntroduction() {
        val text =
            "Searching… <tool_call>{\"name\":\"search_photos\",\"arguments\":{\"operation\":\"update\",\"filters\":[]}}</tool_call>"
        assertEquals(
            AssistantCommand.Action.SearchPhotos(AssistantPhotoPlan()),
            AssistantCommand.parse(text)
        )
        for (size in text.indexOf('<') + 1..text.length) assertEquals(
            "Searching…",
            AssistantCommand.visibleText(text.take(size))
        )
        assertEquals("普通の回答", AssistantCommand.visibleText("普通の回答"))
    }

    @Test
    fun helpLookupKeepsItsQueryAndDoesNotMistakeOrdinaryProseForATool() {
        val text =
            "<tool_call>{\"name\":\"search_help\",\"arguments\":{\"query\":\"remove GPS metadata\"}}</tool_call>"
        assertEquals(
            AssistantCommand.Action.SearchHelp("remove GPS metadata"),
            AssistantCommand.parse(text)
        )
        assertNull(AssistantCommand.parse("Use the crop tool to remove the edges."))
    }

    @Test
    fun structuredPhotoCallsKeepEveryFilterAndExpandTheWholeMonth() {
        val command =
            AssistantCommand.parse("""{"name":"search_photos","arguments":{"operation":"update","filters":[{"type":"date","from":"2026-07","to":"2026-07"},{"type":"album","name":"Trips"},{"type":"person","name":"Marie","role":"include"},{"type":"person","name":"Alex","role":"exclude"}]}}""") as AssistantCommand.Action.SearchPhotos
        assertEquals(
            AssistantGalleryQuery(
                people = listOf("Marie"),
                excludedPeople = listOf("Alex"),
                from = "2026-07-01",
                to = "2026-07-31",
                album = "Trips"
            ),
            command.plan.gallery(null, "photos")
        )
    }

    @Test
    fun independentDateConditionsAreIntersectedAndMalformedArgumentsAreRejected() {
        val command =
            AssistantCommand.parse("""{"name":"search_photos","arguments":{"operation":"update","filters":[{"type":"date","from":"2026-07-01","to":""},{"type":"date","from":"","to":"2026-07-31"}]}}""") as AssistantCommand.Action.SearchPhotos
        val previous = AssistantGalleryQuery(people = listOf("Marie"), album = "Trips")
        assertEquals(
            previous.copy(from = "2026-07-01", to = "2026-07-31"),
            command.plan.gallery(previous, "July")
        )
        assertTrue(runCatching { AssistantCommand.parse("""{"name":"search_photos","arguments":{}}""") }.isFailure)
        assertTrue(runCatching { AssistantCommand.parse("""{"name":"search_photos","arguments":{"operation":"update","filters":[{"type":"unknown"}]}}""") }.isFailure)
    }

    @Test
    fun anAlbumRefinementRetainsTheFilenameUntilAnExplicitReset() {
        val previous = AssistantGalleryQuery(filename = "IMG_2026.png")
        val update =
            AssistantCommand.parse("""{"name":"search_photos","arguments":{"operation":"update","filters":[{"type":"album","name":"Trips"}]}}""") as AssistantCommand.Action.SearchPhotos
        assertEquals(previous.copy(album = "Trips"), update.plan.gallery(previous, "Trips"))
        val reset =
            AssistantCommand.parse("""{"name":"search_photos","arguments":{"operation":"update","filters":[{"type":"reset"},{"type":"album","name":"Trips"}]}}""") as AssistantCommand.Action.SearchPhotos
        assertEquals(
            AssistantGalleryQuery(album = "Trips"),
            reset.plan.gallery(previous, "new search")
        )
    }

}
