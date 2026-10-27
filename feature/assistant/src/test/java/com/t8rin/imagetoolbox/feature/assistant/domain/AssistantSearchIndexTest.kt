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

class AssistantSearchIndexTest {
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
}
