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

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class AssistantGalleryInterpreterTest {
    private val today = LocalDate.of(2026, 10, 27)
    private val interpreter = AssistantGalleryInterpreter(today)
    private val catalog = AssistantGalleryCatalog(
        listOf("Саша", "Маша", "Анна Мария", "Аня", "Мая", "Сергей Иванов"),
        listOf("Vacation", "Family trips")
    )

    @Test
    fun combinedSearchKeepsEveryLiteralCondition() {
        val command = interpreter.interpret(
            "Покажи Сашу и Машу на пляже за август 2026 из альбома Vacation файл IMG*",
            catalog
        )
        assertEquals(AssistantGalleryCommand.Action.Search, command.action)
        assertEquals(listOf("Саша", "Маша"), command.query!!.people)
        assertEquals("2026-08-01", command.query.from)
        assertEquals("2026-08-31", command.query.to)
        assertEquals("Vacation", command.query.album)
        assertEquals("IMG*", command.query.filename)
        assertEquals("пляже", command.query.semantic)
    }

    @Test
    fun peopleOnlyAndLiteralSearchesNeverNeedGeneration() {
        listOf(
            "Покажи мне Сашу, пожалуйста" to AssistantGalleryQuery(people = listOf("Саша")),
            "Find me photos from album Vacation" to AssistantGalleryQuery(album = "Vacation"),
            "Где Саша и Маша вместе?" to AssistantGalleryQuery(people = listOf("Саша", "Маша")),
            "Покажи Анну Марию" to AssistantGalleryQuery(people = listOf("Анна Мария")),
            "Покажи Аню" to AssistantGalleryQuery(people = listOf("Аня")),
            "Найди Маю" to AssistantGalleryQuery(people = listOf("Мая")),
            "Покажи Сергея Иванова" to AssistantGalleryQuery(people = listOf("Сергей Иванов")),
            "Найди файл IMG_123.png" to AssistantGalleryQuery(filename = "IMG_123.png"),
            "Найди IMG_123.png" to AssistantGalleryQuery(filename = "IMG_123.png"),
            "Покажи фото из альбома Vacation" to AssistantGalleryQuery(album = "Vacation"),
            "Find photos from album \"Family trips\"" to AssistantGalleryQuery(album = "Family trips")
        ).forEach { (question, expected) ->
            val command = interpreter.interpret(question, catalog)
            assertEquals(question, expected, command.query)
            assertNull(question, command.query?.semantic)
        }
    }

    @Test
    fun calendarUsesRealMonthBoundariesAndRelativeDates() {
        listOf(
            "Найди фото за февраль 2024" to ("2024-02-01" to "2024-02-29"),
            "Find photos in February 2025" to ("2025-02-01" to "2025-02-28"),
            "Найди июльские фото" to ("2026-07-01" to "2026-07-31"),
            "Найди августовские фото" to ("2026-08-01" to "2026-08-31"),
            "Найди фото в июле 2026" to ("2026-07-01" to "2026-07-31"),
            "Найди фото за 15 августа 2026" to ("2026-08-15" to "2026-08-15"),
            "Find photos from 2026-07-01 to 2026-08-31" to ("2026-07-01" to "2026-08-31"),
            "Найди фото с 01.07.2026 по 31.08.2026" to ("2026-07-01" to "2026-08-31"),
            "Покажи фото вчера" to ("2026-10-26" to "2026-10-26"),
            "Покажи фото за прошлый месяц" to ("2026-09-01" to "2026-09-30"),
            "Find photos last week" to ("2026-10-19" to "2026-10-25"),
            "Покажи фото за 2025 год" to ("2025-01-01" to "2025-12-31")
        ).forEach { (question, dates) ->
            val command = interpreter.interpret(question, catalog)
            assertEquals(question, dates.first, command.query?.from)
            assertEquals(question, dates.second, command.query?.to)
            assertNull(question, command.query?.semantic)
        }
    }

    @Test
    fun narrowingAndChangingAFilterPreserveTheOtherConditions() {
        val previous = AssistantGalleryQuery(
            "a cat",
            listOf("Саша"),
            "2026-08-01",
            "2026-08-31",
            "Vacation",
            "IMG*"
        )
        val narrower = interpreter.interpret("А из этих только за июль", catalog, previous)
        assertEquals(AssistantGallerySearch.Mode.Narrow, narrower.mode)
        assertEquals(previous.copy(from = "2026-07-01", to = "2026-07-31"), narrower.query)
        assertEquals(previous.semantic, narrower.query?.semantic)
        val updated = interpreter.interpret("А теперь за июль", catalog, previous)
        assertEquals(AssistantGallerySearch.Mode.Update, updated.mode)
        assertEquals(narrower.query, updated.query)
        val outside = interpreter.interpret("А где он на улице?", catalog, previous)
        assertEquals(previous.copy(semantic = "a cat, улице"), outside.query)
        val added = interpreter.interpret("И Машу", catalog, previous)
        assertEquals(AssistantGallerySearch.Mode.Narrow, added.mode)
        assertEquals(listOf("Саша", "Маша"), added.query!!.people)
        val cleared = interpreter.interpret("Убери фильтр по дате", catalog, previous)
        assertEquals(previous.copy(from = null, to = null), cleared.query)
    }

    @Test
    fun openDateBoundsDoNotKeepAnOldOppositeBound() {
        val previous =
            AssistantGalleryQuery(people = listOf("Саша"), from = "2026-08-01", to = "2026-08-31")
        val since = interpreter.interpret("А теперь с июля", catalog, previous)
        assertEquals("2026-07-01", since.query!!.from)
        assertNull(since.query.to)
        val before = interpreter.interpret("А теперь до июля", catalog, previous)
        assertNull(before.query!!.from)
        assertEquals("2026-06-30", before.query.to)
        val after = interpreter.interpret("Найди фото после 2026-07-15", catalog)
        assertEquals("2026-07-16", after.query!!.from)
        assertNull(after.query.to)
    }

    @Test
    fun spatialRelationsInsideTheDescriptionAreKept() {
        val command = interpreter.interpret("Найди фото кота под столом рядом с собакой", catalog)
        assertEquals("кота под столом рядом с собакой", command.query?.semantic)
        val english = interpreter.interpret("Find photos of a cat between two trees", catalog)
        assertEquals("cat between two trees", english.query?.semantic)
    }

    @Test
    fun freshSearchAndPaginationDoNotInheritUnrequestedFilters() {
        val previous = AssistantGalleryQuery("a beach", listOf("Саша"), album = "Vacation")
        val fresh = interpreter.interpret("Найди фото кота", catalog, previous)
        assertEquals(AssistantGallerySearch.Mode.New, fresh.mode)
        assertEquals(AssistantGalleryQuery("кота"), fresh.query)
        assertEquals(
            AssistantGalleryCommand.Action.More,
            interpreter.interpret("Покажи ещё", catalog, previous).action
        )
        assertEquals(
            AssistantGalleryCommand.Action.More,
            interpreter.interpret("Покажи мне ещё", catalog, previous).action
        )
        assertEquals(
            AssistantGalleryCommand.Action.Clarify,
            interpreter.interpret("Покажи ещё", catalog).action
        )
        listOf(
            "Как сжать фото?",
            "Покажи инструменты сжатия",
            "Покажи мне инструменты сжатия",
            "Покажи поддерживаемые форматы",
            "Show settings"
        ).forEach { question ->
            assertEquals(
                AssistantGalleryCommand.Action.Help,
                interpreter.interpret(question, catalog).action
            )
            assertFalse(needsGalleryRouting(question, previous))
        }
    }

    @Test
    fun invalidOrUnsupportedConditionsAreNeverSilentlyDropped() {
        listOf(
            "Найди фото за 2026-02-30", "Найди фото с 2026-09-01 по 2026-08-01",
            "Найди фото без Саши", "Найди фото из альбома", "Найди фото за июль или сентябрь",
            "Покажи Сашу или Машу"
        ).forEach { question ->
            assertEquals(
                question,
                AssistantGalleryCommand.Action.Clarify,
                interpreter.interpret(question, catalog).action
            )
        }
        val filename = interpreter.interpret("Найди файл IMG_2025-01-01.png", catalog)
        assertNull(filename.query!!.from)
        assertNull(filename.query.to)
        assertEquals("IMG_2025-01-01.png", filename.query.filename)
    }
}
