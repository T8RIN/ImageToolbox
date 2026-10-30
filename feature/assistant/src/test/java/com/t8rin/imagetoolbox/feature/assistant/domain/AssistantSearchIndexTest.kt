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

class AssistantSearchIndexTest {
    @Test
    fun aNormalizedTaskDoesNotMatchOnlyOneFamiliarWord() {
        val index = AssistantSearchIndex(
            listOf(
                AssistantSearchIndex.Document("picker", "Color picker", "Choose a color"),
                AssistantSearchIndex.Document("crop", "Crop image", "Remove image edges")
            ), "en"
        )
        assertTrue(index.search("color flortation", minimumCoverage = 0.6).isEmpty())
        assertEquals("picker", index.search("choose color", minimumCoverage = 0.6).first().value)
    }

    @Test
    fun citedToolTitlesKeepTheirLinksAfterInflection() {
        val spanish = AssistantSearchIndex<String>(emptyList(), "es")
        assertTrue(spanish.containsTitle("Abre Dibujo y selecciona una foto", "Dibujar"))
        val russian = AssistantSearchIndex<String>(emptyList(), "ru")
        assertTrue(russian.containsTitle("Воспользуйтесь Сжатием по весу", "Сжатие по весу"))
        assertFalse(russian.containsTitle("Измените размер текста", "Сжатие по весу"))
    }

    @Test
    fun inflectionsUseTheCorpusLanguageWithoutChangingEntityMatching() {
        val russian = AssistantSearchIndex(
            listOf(
                AssistantSearchIndex.Document(
                    "background",
                    "Удаление фона",
                    "Удаляет фон изображения"
                ),
                AssistantSearchIndex.Document(
                    "join",
                    "Склеивание изображений",
                    "Соединяет изображения"
                )
            ), "ru"
        )
        assertEquals("background", russian.search("удалить фон").first().value)
        val english = AssistantSearchIndex(
            listOf(
                AssistantSearchIndex.Document(
                    "alpha",
                    "Formats preserving transparency",
                    "Preserve transparent pixels"
                ),
                AssistantSearchIndex.Document("files", "File names", "Name saved files")
            ), "en"
        )
        assertEquals("alpha", english.search("formats preserve transparency").first().value)
        assertFalse(AssistantSearchIndex.matches("are", "area"))
    }

    @Test
    fun wordMatchingHandlesTyposWithoutConfusingUnrelatedPrefixes() {
        assertTrue(AssistantSearchIndex.matches("уменьшть", "уменьшить"))
        assertTrue(AssistantSearchIndex.matches("backgroumd", "background"))
        assertTrue(AssistantSearchIndex.matches("photoss", "photos"))
        assertFalse(AssistantSearchIndex.matches("animals", "animated"))
        assertFalse(AssistantSearchIndex.matches("are", "area"))
        assertFalse(AssistantSearchIndex.matches("blue", "blur"))
        assertFalse(AssistantSearchIndex.matches("500", "700"))
    }

    @Test
    fun recognizedWordsDoNotAcquireAnUnrelatedMeaningThroughTypoMatching() {
        val index = AssistantSearchIndex(
            listOf(
                AssistantSearchIndex.Document("time", "Time", "Repeat an operation several times"),
                AssistantSearchIndex.Document(
                    "tiles",
                    "Glass tiles",
                    "Arrange rectangular tiles in an image"
                )
            )
        )
        assertEquals(listOf("time"), index.search("times").map { it.value })
        assertEquals(listOf("tiles"), index.search("tiles").map { it.value })
    }

    @Test
    fun stemmingKeepsTypoMatchingForUnknownWords() {
        val index = AssistantSearchIndex(
            listOf(
                AssistantSearchIndex.Document(
                    "background",
                    "Remove background",
                    "Make the background transparent"
                ),
                AssistantSearchIndex.Document(
                    "metadata",
                    "Remove metadata",
                    "Delete location information"
                )
            ), "en"
        )
        assertEquals("background", index.search("remove backgroumd").first().value)
    }

    @Test
    fun ranksTitlesAndTheRequestedSubjectAheadOfIncidentalWords() {
        val index = AssistantSearchIndex(
            listOf(
                AssistantSearchIndex.Document(
                    "photo",
                    "Compress photos",
                    "Resize a photo to a target file size"
                ),
                AssistantSearchIndex.Document(
                    "pdf",
                    "Compress PDF",
                    "Reduce PDF document file size"
                ),
                AssistantSearchIndex.Document("font", "Font size", "Change text size")
            )
        )
        assertEquals("photo", index.search("compress photos").first().value)
        assertEquals("pdf", index.search("compress PDF").first().value)
        assertTrue(index.search("qzxxvkyy").isEmpty())
    }

    @Test
    fun aRequestedActionMatchesItsDocumentedNounFormInTitles() {
        val index = AssistantSearchIndex(
            listOf(
                AssistantSearchIndex.Document("formats", "Format conversion", "Export a picture"),
                AssistantSearchIndex.Document("psd", "PSD", "Convert a PSD file into a picture")
            )
        )
        assertEquals("formats", index.search("convert format").first().value)
    }

    @Test
    fun normalizationIsUnicodeBasedAndHasNoStopWordOrLanguageLists() {
        assertEquals("cafe", AssistantSearchIndex.normalize("CAFÉ!"))
        listOf("расскажи", "explain", "cómo", "warum", "写真", "صور", "तस्वीर").forEach {
            assertTrue(it, AssistantSearchIndex.tokens(it).isNotEmpty())
        }
    }

    @Test
    fun findsLocalizedTitlesWithoutChoosingAnApplicationLanguage() {
        listOf(
            "Удалить фон",
            "Fondo transparente",
            "Hintergrund entfernen",
            "إزالة الخلفية",
            "背景を削除"
        ).forEach { title ->
            val index = AssistantSearchIndex(
                listOf(
                    AssistantSearchIndex.Document("target", title, title),
                    AssistantSearchIndex.Document("other", "Checksum", "File integrity")
                )
            )
            assertEquals(title, "target", index.search(title).first().value)
        }
    }

    @Test
    fun appliesRestrictionsBeforeRankingAndLimiting() {
        val documents = listOf(
            AssistantSearchIndex.Document("photo", "Compress photos", "Reduce file size"),
            AssistantSearchIndex.Document("pdf", "Compress PDF", "Reduce file size")
        )
        val index = AssistantSearchIndex(documents)
        assertEquals("photo", index.search("compress", 1) { it == "photo" }.single().value)
        assertEquals(
            AssistantSearchIndex(documents.take(1)).search("compress"),
            index.search("compress") { it == "photo" })
    }

    @Test
    fun anUnknownSubjectCannotMatchOnlyTheQuestionIntroducer() {
        val index = AssistantSearchIndex(
            listOf(
                AssistantSearchIndex.Document(
                    "crop",
                    "Crop",
                    "What is cropping? Cropping removes image edges."
                ),
                AssistantSearchIndex.Document(
                    "resize",
                    "Resize",
                    "What is resizing? Change image dimensions."
                )
            )
        )
        assertTrue(index.search("What is xyzzqx?").isEmpty())
        assertEquals("crop", index.search("What is cropping?").first().value)
    }

    @Test
    fun variableFileSizeDoesNotHideTheRelevantInstructions() {
        val index = AssistantSearchIndex(
            listOf(
                AssistantSearchIndex.Document(
                    "weight",
                    "Resize by file size",
                    "Reduce an image to a target file size in KB or MB"
                ),
                AssistantSearchIndex.Document("font", "Font size", "Change the size of letters")
            )
        )
        assertEquals("weight", index.search("reduce image to 900KB").first().value)
        assertTrue(index.search("17 times 6").isEmpty())
    }

    @Test
    fun conversionDirectionIsPreservedWhenDocumentsShareTheSameWords() {
        val index = AssistantSearchIndex(
            listOf(
                AssistantSearchIndex.Document("extract", "PDF to images", "Convert PDF to images"),
                AssistantSearchIndex.Document("create", "Images to PDF", "Convert images to PDF"),
                AssistantSearchIndex.Document("colors", "Colors", "Combine and render colors")
            )
        )
        assertEquals("create", index.search("combine images to PDF").first().value)
        assertEquals("extract", index.search("render PDF to images").first().value)
    }

}
