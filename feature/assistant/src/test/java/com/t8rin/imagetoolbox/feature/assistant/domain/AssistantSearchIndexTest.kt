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
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class AssistantSearchIndexTest {
    @Test
    fun searchesRealEnglishAndRussianHelpResources() {
        listOf(
            "values" to "remove background",
            "values-ru" to "удаление фона"
        ).forEach { (directory, query) ->
            val root =
                generateSequence(File(checkNotNull(System.getProperty("user.dir")))) { it.parentFile }
                    .first { File(it, "core/resources/src/main/res").isDirectory }
            val xml = File(root, "core/resources/src/main/res/$directory/strings.xml")
            val nodes = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xml)
                .getElementsByTagName("string")
            val documents = (0 until nodes.length).map { nodes.item(it) }
                .filter { it.attributes.getNamedItem("name").nodeValue.startsWith("help_tip_") }
                .map { node ->
                    val key = node.attributes.getNamedItem("name").nodeValue
                    AssistantSearchIndex.Document(key, node.textContent, node.textContent)
                }
            val index = AssistantSearchIndex(documents)
            val matches = index.search(query)
            assertTrue("No help results in $directory", matches.isNotEmpty())
            assertTrue(matches.any { it.value.contains("background") })
            assertTrue("Greeting matched help in $directory", index.search("Привет").isEmpty())
        }
    }

    @Test
    fun ranksTitleAndCombinedTermCoverageAboveIncidentalMentions() {
        val index = AssistantSearchIndex(
            listOf(
                AssistantSearchIndex.Document(
                    "resize",
                    "Resize by file size",
                    "Compress photos to a target file size"
                ),
                AssistantSearchIndex.Document("font", "Font size", "Change text size"),
                AssistantSearchIndex.Document(
                    "save",
                    "Save files",
                    "Select an image and save the file"
                )
            )
        )
        assertEquals("resize", index.search("target file size").first().value)
        assertTrue(index.search("neuralgalaxyfoobar").isEmpty())
        assertTrue(index.search("teleport photos").isEmpty())
        assertTrue(index.search("как это сделать").isEmpty())
        assertTrue(index.search("How does file teleportation between galaxies work?").isEmpty())
    }

    @Test
    fun normalizesRussianYoAndMatchesWordPrefixes() {
        val index = AssistantSearchIndex(
            listOf(
                AssistantSearchIndex.Document(
                    "colour",
                    "Цвет фотографии",
                    "Сохранение фотографий и цвета"
                )
            )
        )
        assertEquals("елка", AssistantSearchIndex.normalize("ЁЛКА!"))
        assertEquals("colour", index.search("сохран").single().value)
    }

    @Test
    fun commonTermsDoNotOutweighAnUnmatchedSpecificTerm() {
        val index = AssistantSearchIndex(
            listOf(
                AssistantSearchIndex.Document(
                    "scan",
                    "Document scanner",
                    "The first scan can take time"
                ),
                AssistantSearchIndex.Document("preview", "Preview", "The first preview takes time")
            )
        )
        assertTrue(index.search("time-to-first-token").isEmpty())
        assertEquals("scan", index.search("document scanner").single().value)
    }

    @Test
    fun russianGreetingDoesNotMatchWordsWithDifferentEndings() {
        val index = AssistantSearchIndex(
            listOf(
                AssistantSearchIndex.Document(
                    "pdf",
                    "Приведение страниц PDF к формату бумаги",
                    "Размер страниц"
                ),
                AssistantSearchIndex.Document(
                    "resize",
                    "Сжатие по весу",
                    "Может привести к появлению артефактов"
                )
            )
        )
        assertTrue(index.search("Привет").isEmpty())
        assertEquals("pdf", index.search("приведение").single().value)
    }

    @Test
    fun matchesRussianCasesWithoutDroppingTheSubjectOfAQuestion() {
        val index = AssistantSearchIndex(
            listOf(
                AssistantSearchIndex.Document(
                    "dates", "Сохраняйте дату и время", "Очистка EXIF позволяет сохранять даты"
                ),
                AssistantSearchIndex.Document(
                    "hash", "Контрольная сумма", "Рассчитать хэш файла"
                )
            )
        )
        assertEquals("dates", index.search("Можно сохранить дату при очистке EXIF?").first().value)
        assertEquals("hash", index.search("Как рассчитать контрольную сумму файла?").first().value)
    }

    @Test
    fun englishWordPrefixesDoNotTurnChecksumsIntoChecks() {
        val index = AssistantSearchIndex(
            listOf(
                AssistantSearchIndex.Document(
                    "pdf",
                    "Compare PDF",
                    "Check the pages and compare versions"
                ),
                AssistantSearchIndex.Document(
                    "hash",
                    "Checksum tools",
                    "Compare a checksum to a known value"
                ),
                AssistantSearchIndex.Document("compress", "Compression", "Compress photos")
            )
        )
        assertEquals("hash", index.search("How do I compare checksums?").first().value)
        assertTrue(index.search("checksums") { it == "pdf" }.isEmpty())
        assertEquals("compress", index.search("compressed").first().value)
        assertTrue(index.search("Why are there ions?").isEmpty())
    }

    @Test
    fun matchesCommonEnglishEditingForms() {
        listOf(
            "crop" to "cropping",
            "resize" to "resizing",
            "rotate" to "rotation",
            "rotate" to "rotating",
            "convert" to "conversion",
            "date" to "dates",
            "save" to "saving"
        ).forEach { (title, query) ->
            val index =
                AssistantSearchIndex(listOf(AssistantSearchIndex.Document(title, title, title)))
            assertEquals(query, title, index.search(query).single().value)
        }
    }

    @Test
    fun aLongerRussianWordDoesNotMatchAnUnrelatedShorterRoot() {
        val index = AssistantSearchIndex(
            listOf(
                AssistantSearchIndex.Document(
                    "search", "Поиск по фото", "Подготовка модели занимает время. Выберите язык."
                )
            )
        )
        assertTrue(
            index.search("От чего зависит время до первого токена языковой модели?").isEmpty()
        )
        assertTrue(index.search("языковой").isEmpty())
        assertEquals("search", index.search("язык").single().value)
    }

    @Test
    fun pluralFrameQuestionsMatchTimingRatherThanUnrelatedConversionControls() {
        val index = AssistantSearchIndex(
            listOf(
                AssistantSearchIndex.Document(
                    "create", "Изображения в GIF",
                    "Измените FPS, чтобы поменять задержку между кадрами. GIF использует ограниченную палитру."
                ),
                AssistantSearchIndex.Document(
                    "convert", "GIF в WebP",
                    "Настройте качество конвертации GIF в WebP. Проверьте несколько кадров."
                )
            )
        )
        assertEquals(
            "create",
            index.search("Задержка кадров GIF")
                .first().value
        )
        assertEquals(AssistantSearchIndex.tokens("кадры"), AssistantSearchIndex.tokens("кадров"))
    }

    @Test
    fun ranksTheRequestedSubsetBeforeApplyingTheResultLimit() {
        val documents = listOf(
            AssistantSearchIndex.Document("general", "Remove background", "Remove background"),
            AssistantSearchIndex.Document(
                "related",
                "Background settings",
                "Configure how to remove background"
            ),
            AssistantSearchIndex.Document("other", "Background colour", "Change background colour")
        )
        val index = AssistantSearchIndex(documents)
        val query = "remove background"
        assertEquals("general", index.search(query, 1).single().value)
        assertEquals("related", index.search(query, 1) { it == "related" }.single().value)
        assertEquals(
            AssistantSearchIndex(documents.filter { it.value != "general" }).search(query),
            index.search(query) { it != "general" }
        )
    }
}
