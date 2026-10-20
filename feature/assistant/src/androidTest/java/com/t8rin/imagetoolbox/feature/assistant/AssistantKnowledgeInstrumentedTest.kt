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

package com.t8rin.imagetoolbox.feature.assistant

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.t8rin.imagetoolbox.core.di.entryPoint
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.presentation.model.Setting
import com.t8rin.imagetoolbox.core.settings.presentation.model.key
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.feature.assistant.di.AssistantEntryPoint
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantRequest
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantScreenContext
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AssistantKnowledgeInstrumentedTest {
    private val services = InstrumentationRegistry.getInstrumentation().targetContext
        .entryPoint<AssistantEntryPoint>()
    private val knowledge = services.knowledge

    @Test
    fun retrievesInstalledHelpInRussianAndEnglish() = runBlocking {
        listOf(
            "Как удалить фон изображения?",
            "How do I remove an image background?"
        ).forEach { query ->
            val answer = knowledge.retrieve(AssistantRequest(query))
            assertTrue(
                answer.references.toString(),
                answer.references.any { "background" in it.tipId })
            assertTrue(answer.tools.any { it.id == Screen.EraseBackground().id })
            if (query.first() == 'К') {
                assertTrue(
                    answer.references.first().text,
                    answer.references.first().text.any { it in 'А'..'я' })
            } else {
                assertTrue(
                    answer.references.first().text,
                    answer.references.first().text.contains("background", ignoreCase = true)
                )
            }
        }
    }

    @Test
    fun retrievesRelevantDocumentationForMultipleTopics() = runBlocking {
        val pdf = knowledge.retrieve(AssistantRequest("Как сделать PDF с OCR?"))
        assertEquals("searchable-pdf", pdf.references.first().tipId)
        val compression = knowledge.retrieve(AssistantRequest("Сожми фотографии до 500 КБ"))
        assertEquals("resize-by-file-size", compression.references.first().tipId)
        val exif = knowledge.retrieve(AssistantRequest("Удали EXIF из выбранных фотографий"))
        assertTrue(exif.references.toString(), exif.references.any { it.tipId == "exif-removal" })
        val combined = knowledge.retrieve(
            AssistantRequest("Удали фон, затем сожми фотографии до 500 КБ.")
        )
        assertTrue(
            combined.references.toString(),
            combined.references.any { it.tipId == "background-remover" })
        assertTrue(
            combined.references.toString(),
            combined.references.any { it.tipId == "resize-by-file-size" })
    }

    @Test
    fun compressionQuestionsFindTheActualToolAndHelpWithoutRequiringASize() = runBlocking {
        val context = AssistantScreenContext(
            selectedUris = listOf(
                "content://media/external/images/media/1",
                "content://media/external/images/media/2"
            )
        )
        listOf(
            "как сжать фото", "Как сжимать фотографии?", "Сожми фотографию", "Сжми фото",
            "Ужать изображение", "Как уменьшить вес фотографии?", "Как уменьшить размер файла?",
            "How can I compress a photo?"
        ).forEach { query ->
            val request = AssistantRequest(query, context)
            val answer = knowledge.retrieve(request)
            assertEquals(query, "resize-by-file-size", answer.references.first().tipId)
            assertEquals(query, listOf("resize-by-file-size"), answer.references.map { it.tipId })
            assertEquals(query, listOf(Screen.WeightResize().id), answer.tools.map { it.id })
            val language = if (query.startsWith("How")) "en" else "ru"
            val grounding = knowledge.grounding(request, answer)
            assertTrue(
                grounding,
                grounding.contains(
                    services.resources.getStringLocalized(
                        R.string.by_bytes_resize,
                        language
                    )
                )
            )
        }
    }

    @Test
    fun specificCompressionToolsAreNotReplacedByPhotoCompressionAliases() = runBlocking {
        val lab = knowledge.retrieve(AssistantRequest("Compression Lab"))
        assertTrue(lab.tools.any { it.id == Screen.CompressionLab().id })
        listOf("Compression Lab", "Compress PDF", "Сжать PDF").forEach { query ->
            val answer = knowledge.retrieve(AssistantRequest(query))
            assertFalse(query, answer.references.any { it.tipId == "resize-by-file-size" })
            assertFalse(query, answer.tools.any { it.id == Screen.WeightResize().id })
        }
    }

    @Test
    fun explainsActualSettingAndExcludesImageUrisFromGrounding() = runBlocking {
        val setting = Setting.DeleteOriginalsAfterSave
        val uri = "content://media/external/images/media/private_assistant_fixture"
        val request = AssistantRequest(
            "Где находится эта настройка?",
            AssistantScreenContext(
                toolId = Screen.WeightResize().id,
                settingKey = setting.key(),
                selectedUris = listOf(uri),
                parameters = mapOf("maxBytes" to "512000", "privateMetadata" to "PRIVATE_DATA")
            )
        )
        val answer = knowledge.retrieve(request)
        assertTrue(
            knowledge.grounding(request, answer)
                .contains(services.resources.getStringLocalized(setting.title, "ru"))
        )
        val grounding = knowledge.grounding(request, knowledge.retrieve(request))
        assertFalse(grounding.contains(uri))
        assertFalse(grounding.contains("PRIVATE_DATA"))
        assertFalse(grounding.contains("known-tip-id"))
        assertFalse(grounding.contains("\"text\":\"answer\""))
        assertTrue(grounding.contains("512000"))
    }

    @Test
    fun ordinaryQuestionsDoNotReceiveDocumentationOrACannedAnswer() = runBlocking {
        val results = listOf(
            "Hi", "Привет", "Write a haiku about autumn", "Расскажи короткую сказку про кота",
            "time-to-first-token", "What determines time-to-first-token in a language model?",
            "От чего зависит время до первого токена языковой модели?"
        ).map { query ->
            val request = AssistantRequest(query)
            request to knowledge.retrieve(request)
        }
        val unexpected = results.filter { (_, answer) ->
            answer.references.isNotEmpty() || answer.tools.isNotEmpty()
        }
        assertTrue(unexpected.joinToString("\n") { (request, answer) ->
            "${request.query}: references=${answer.references.map { it.tipId }}"
        }, unexpected.isEmpty())
        results.forEach { (request, retrieved) ->
            assertTrue(request.query, retrieved.text.isEmpty())
            val prompt = knowledge.grounding(request, retrieved)
            assertFalse(prompt, prompt.contains("DOCS"))
            assertFalse(prompt, prompt.contains("JSON"))
            assertTrue(prompt, prompt.length < 200)
            assertTrue(
                prompt,
                prompt.contains(if (request.query.any { it in 'А'..'я' }) "Russian" else "English")
            )
        }
    }

    @Test
    fun groundingKeepsTheSourceFactsForKnownGenerationRequests() = runBlocking {
        AssistantApplicationFixtures.requests.forEach { (name, request) ->
            val facts = when (name) {
                "ru_open_background", "en_open_background" -> listOf(
                    R.string.help_tip_background_remover_description,
                    R.string.help_tip_background_remover_step_1,
                    R.string.help_tip_background_remover_step_2,
                    R.string.help_tip_background_remover_step_3
                )

                "ru_compress_500kb", "ru_compress_question", "en_compress_question" -> listOf(
                    R.string.help_tip_resize_by_file_size_description,
                    R.string.help_tip_resize_by_file_size_step_1,
                    R.string.help_tip_resize_by_file_size_step_2,
                    R.string.help_tip_resize_by_file_size_step_3
                )

                "en_searchable_pdf" -> listOf(
                    R.string.help_tip_searchable_pdf_description,
                    R.string.help_tip_searchable_pdf_step_1,
                    R.string.help_tip_searchable_pdf_step_2,
                    R.string.help_tip_searchable_pdf_step_3
                )

                "en_remove_exif" -> listOf(
                    R.string.help_tip_exif_removal_description,
                    R.string.help_tip_exif_removal_step_1,
                    R.string.help_tip_exif_removal_step_2,
                    R.string.help_tip_exif_removal_step_3
                )

                "ru_background_then_compress" -> listOf(
                    R.string.help_tip_background_remover_description,
                    R.string.help_tip_background_remover_step_1,
                    R.string.help_tip_background_remover_step_2,
                    R.string.help_tip_background_remover_step_3,
                    R.string.help_tip_resize_by_file_size_description,
                    R.string.help_tip_resize_by_file_size_step_1,
                    R.string.help_tip_resize_by_file_size_step_2,
                    R.string.help_tip_resize_by_file_size_step_3
                )

                else -> error("Unknown generation fixture")
            }
            val language = if (name.startsWith("ru_")) "ru" else "en"
            val grounding = knowledge.grounding(request, knowledge.retrieve(request))
            facts.forEach { resource ->
                val text = services.resources.getStringLocalized(resource, language)
                assertTrue("$name omitted source facts: $text", grounding.contains(text))
            }
        }
    }

    @Test
    fun groundingDoesNotPrefillEveryRelatedTipForASingleTool() = runBlocking {
        val request = AssistantRequest("Открой удаление фона.")
        val retrieved = knowledge.retrieve(request)
        val grounding = knowledge.grounding(request, retrieved)
        assertEquals(listOf("background-remover"), retrieved.references.map { it.tipId })
        assertEquals(listOf(Screen.EraseBackground().id), retrieved.tools.map { it.id })
        assertTrue(grounding, grounding.length < 1200)
        assertTrue(
            grounding,
            grounding.contains(
                services.resources.getStringLocalized(
                    Screen.EraseBackground().title,
                    "ru"
                )
            )
        )
        retrieved.references.drop(1).forEach { reference ->
            assertFalse(grounding, grounding.contains(reference.title))
        }
    }

    @Test
    fun groundingNamesActualToolsWithoutPromisingActionButtons() = runBlocking {
        listOf(
            "как сжать фото" to "ru",
            "How can I compress a photo?" to "en"
        ).forEach { (query, language) ->
            val request = AssistantRequest(query)
            val answer = knowledge.retrieve(request)
            val grounding = knowledge.grounding(request, answer)
            val title = services.resources.getStringLocalized(R.string.by_bytes_resize, language)
            assertTrue(grounding, grounding.contains("Help:\n$title\n"))
            assertFalse(grounding, grounding.contains("\n${answer.references.first().title}\n"))
            assertFalse(grounding, grounding.contains("Available action"))
            assertFalse(grounding, grounding.contains("buttons"))
            assertFalse(grounding, grounding.contains("open_tool"))
        }
    }
}
