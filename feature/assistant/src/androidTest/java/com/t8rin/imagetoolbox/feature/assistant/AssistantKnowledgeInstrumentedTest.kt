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
import com.t8rin.imagetoolbox.core.domain.model.AssistantModel
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.presentation.model.Setting
import com.t8rin.imagetoolbox.core.settings.presentation.model.SettingsGroup
import com.t8rin.imagetoolbox.core.settings.presentation.model.key
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.feature.assistant.di.AssistantEntryPoint
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantAnswer
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantReference
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantRequest
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantScreenContext
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantTool
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import kotlin.system.measureNanoTime

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
            assertTrue(answer.tools.any { it.key == Screen.EraseBackground().simpleName })
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
            assertEquals(
                query,
                listOf(Screen.WeightResize().simpleName),
                answer.tools.map { it.key })
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
        assertTrue(lab.tools.any { it.key == Screen.CompressionLab().simpleName })
        listOf("Compression Lab", "Compress PDF", "Сжать PDF").forEach { query ->
            val answer = knowledge.retrieve(AssistantRequest(query))
            assertFalse(query, answer.references.any { it.tipId == "resize-by-file-size" })
            assertFalse(query, answer.tools.any { it.key == Screen.WeightResize().simpleName })
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
            answer.references.isNotEmpty() || answer.tools.isNotEmpty() || answer.settings.isNotEmpty()
        }
        assertTrue(unexpected.joinToString("\n") { (request, answer) ->
            "${request.query}: references=${answer.references.map { it.tipId }}, settings=${answer.settings.map { it.key }}"
        }, unexpected.isEmpty())
        results.forEach { (request, retrieved) ->
            assertTrue(request.query, retrieved.text.isEmpty())
            val prompt = knowledge.grounding(request, retrieved)
            assertFalse(prompt, prompt.contains("DOCS"))
            assertFalse(prompt, prompt.contains("JSON"))
            assertTrue(prompt, prompt.length < 450)
            val russian = request.query.any { it in 'А'..'я' }
            assertTrue(
                prompt,
                prompt.contains(if (russian) "Задай только короткий вопрос" else "Ask only a short question")
            )
            assertTrue(
                prompt,
                prompt.contains(if (russian) "Отвечай по-русски" else "Reply in English")
            )
            val userPrompt = knowledge.userPrompt(request, retrieved)
            assertTrue(userPrompt, userPrompt.contains(request.query))
            assertTrue(
                userPrompt,
                userPrompt.contains(if (russian) "Пример подходящего вопроса" else "An appropriate question")
            )
        }
    }

    @Test
    fun missingDocumentationDoesNotDiscardCurrentEditingOrSettingContext() {
        listOf(
            AssistantScreenContext(toolId = Screen.WeightResize().id),
            AssistantScreenContext(settingKey = Setting.DeleteOriginalsAfterSave.key()),
            AssistantScreenContext(selectedUris = listOf("content://assistant-test/photo"))
        ).forEach { context ->
            val prompt = knowledge.grounding(
                AssistantRequest("What should I change here?", context),
                AssistantAnswer(text = "")
            )
            assertFalse(prompt, prompt.contains("No matching app information"))
            assertTrue(prompt, prompt.contains("CONTEXT:"))
            assertTrue(prompt, prompt.contains("using the supplied facts"))
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
        assertEquals(listOf(Screen.EraseBackground().simpleName), retrieved.tools.map { it.key })
        assertTrue(grounding, grounding.length < 1500)
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
            assertEquals(query, knowledge.userPrompt(request, answer))
            assertTrue(grounding, grounding.contains("Help:\n$title\n"))
            assertFalse(grounding, grounding.contains("\n${answer.references.first().title}\n"))
            assertFalse(grounding, grounding.contains("Available action"))
            assertFalse(grounding, grounding.contains("buttons"))
            assertFalse(grounding, grounding.contains("open_tool"))
        }
    }

    @Test
    fun everyCatalogToolIsDiscoverableInBothLanguagesIncludingCollidingIds() = runBlocking {
        val catalog = (Screen.typedEntries.flatMap { it.entries } + Screen.PdfTools.options)
            .distinctBy { it.simpleName }
        val failures = mutableListOf<String>()
        listOf("en", "ru").forEach { language ->
            catalog.forEach { screen ->
                val title = services.resources.getStringLocalized(screen.title, language)
                val answer = knowledge.retrieve(AssistantRequest(title, language = language))
                val destinations = answer.tools.mapNotNull(knowledge::resolveTool)
                if (destinations.none { it.simpleName == screen.simpleName }) {
                    failures.add("$language/$title (${screen.simpleName}): ${destinations.map { it.simpleName }}")
                }
            }
        }
        assertTrue(failures.joinToString("\n"), failures.isEmpty())
    }

    @Test
    fun everyAvailableSettingHasAStableLinkAndCatalogTitle() = runBlocking {
        val settings = SettingsGroup.entries.flatMap { it.settingsList }.distinct()
        val failures = mutableListOf<String>()
        listOf("en", "ru").forEach { language ->
            settings.forEach { setting ->
                val title = services.resources.getStringLocalized(setting.title, language)
                val answer = knowledge.retrieve(AssistantRequest(title, language = language))
                if (answer.settings.none { it.key == setting.key() }) {
                    failures.add("$language/$title (${setting.key()}): ${answer.settings.map { it.key }}")
                }
                assertTrue(
                    title,
                    answer.settings.any { it.key == setting.key() && it.title == title })
            }
        }
        assertTrue(failures.joinToString("\n"), failures.isEmpty())
    }

    @Test
    fun preciseSettingNamesAreNotReplacedByShorterTitles() = runBlocking {
        listOf("Cache limit", "Where is Cache limit?", "Лимит кэша").forEach { query ->
            val answer = knowledge.retrieve(AssistantRequest(query))
            assertEquals(query, Setting.CacheAutoClearLimit.key(), answer.settings.first().key)
        }
        listOf("Where can I change the language?", "Где изменить язык?").forEach { query ->
            val answer = knowledge.retrieve(AssistantRequest(query))
            assertEquals(query, Setting.ChangeLanguage.key(), answer.settings.first().key)
        }
    }

    @Test
    fun naturalToggleQuestionsFindTheOriginalFileDeletionSetting() = runBlocking {
        val setting = Setting.DeleteOriginalsAfterSave
        listOf(
            "Где выключить удаление оригиналов после сохранения?" to "ru",
            "Как отключить удаление исходных файлов после сохранения?" to "ru",
            "Where can I turn off deleting original files after saving?" to "en",
            "How do I disable deleting original files after saving?" to "en"
        ).forEach { (query, language) ->
            val request = AssistantRequest(query, language = language)
            val answer = knowledge.retrieve(request)
            assertEquals(query, listOf(setting.key()), answer.settings.map { it.key })
            assertTrue(query, answer.tools.isEmpty())
            val prompt = knowledge.grounding(request, answer)
            assertTrue(
                prompt,
                prompt.contains(services.resources.getStringLocalized(setting.title, language))
            )
            assertTrue(
                prompt,
                prompt.contains(
                    services.resources.getStringLocalized(
                        checkNotNull(setting.subtitle),
                        language
                    )
                )
            )
            val path = listOf(R.string.settings, R.string.folder, setting.title)
                .joinToString(" > ") { services.resources.getStringLocalized(it, language) }
            assertTrue(prompt, prompt.contains(path))
            assertTrue(prompt, prompt.contains("Current and default values are unknown"))
            assertTrue(knowledge.userPrompt(request, answer).startsWith(query))
        }
    }

    @Test
    fun documentedModesRetainTheirActualDestinationAndTitle() = runBlocking {
        val request = AssistantRequest("Create a searchable PDF with OCR")
        val answer = knowledge.retrieve(request)
        val tool = answer.tools.first { it.key == Screen.RecognizeText().simpleName }
        val destination =
            services.helpRepository.getTip(checkNotNull(tool.helpTipId))?.deepLink as Screen.RecognizeText
        assertTrue(destination.type is Screen.RecognizeText.Type.WriteToSearchablePdf)
        assertEquals(
            services.resources.getStringLocalized(
                checkNotNull(destination.type).title,
                "en"
            ), tool.title
        )
    }

    @Test
    fun helpForServiceScreensUsesTheDocumentTitleInGrounding() {
        listOf(
            "choose-tool" to Screen.Main,
            "logs-feedback" to Screen.AppLogs
        ).forEach { (id, screen) ->
            val tip = checkNotNull(services.helpRepository.getTip(id))
            assertEquals(screen, tip.deepLink)
            val page = tip.pages.first()
            val title = services.resources.getStringLocalized(tip.title, "en")
            val description = services.resources.getStringLocalized(page.description, "en")
            val answer = AssistantAnswer(
                text = "",
                references = listOf(
                    AssistantReference(
                        title = title,
                        text = services.resources.getStringLocalized(
                            page.title,
                            "en"
                        ) + "\n\n" + description,
                        tipId = id,
                        categoryKey = tip.category.key
                    )
                )
            )
            val prompt = knowledge.grounding(AssistantRequest(title), answer)
            assertTrue(prompt, prompt.contains("Help:\n$title\n"))
            assertTrue(prompt, prompt.contains(description))
        }
    }

    @Test
    fun photoOnlyRequestsReceiveUsefulEditsWithoutPretendingToSeePixels() = runBlocking {
        val request = AssistantRequest(
            "What can I do with these files?",
            AssistantScreenContext(
                selectedUris = listOf("content://media/private-photo"),
                parameters = mapOf("selectedImageInfo" to "1: 4000×3000px, image/jpeg, 4.2 MB")
            )
        )
        val answer = knowledge.retrieve(request)
        assertTrue(answer.tools.toString(), answer.tools.any { it.key == Screen.Crop().simpleName })
        assertTrue(
            answer.tools.toString(),
            answer.tools.any { it.key == Screen.WeightResize().simpleName })
        AssistantModel.entries.forEach { model ->
            val prompt = knowledge.grounding(request, answer, model)
            assertTrue(prompt, prompt.contains("4000×3000px"))
            assertTrue(prompt, prompt.contains("not image pixels"))
            assertTrue(prompt, prompt.contains("Suggest useful edits"))
            assertFalse(prompt, prompt.contains("content://"))
        }
    }

    @Test
    fun visionGroundingUsesPixelsOnlyWhenAnImageWasPrepared() = runBlocking {
        val request = AssistantRequest(
            "Придумай подпись к фото",
            AssistantScreenContext(selectedUris = listOf("content://private/photo"))
        )
        val answer = knowledge.retrieve(request)
        AssistantModel.entries.filter { it.supportsVision }.forEach { model ->
            val prompt = knowledge.grounding(request, answer, model, hasImage = true)
            assertTrue(prompt, prompt.contains("предлагай подписи"))
            assertTrue(prompt, prompt.contains("visible details"))
            assertTrue(prompt, prompt.contains("Earlier attachments are not available"))
            assertTrue(prompt, prompt.contains("Отвечай по-русски"))
            assertFalse(prompt, prompt.contains("not image pixels"))
            assertFalse(
                prompt,
                prompt.contains("Name the matching ImageToolbox tool or setting first")
            )
            assertFalse(prompt, prompt.contains("content://private/photo"))
            val withoutImage = knowledge.grounding(request, answer, model)
            assertTrue(withoutImage, withoutImage.contains("not image pixels"))
        }
        val textOnly = knowledge.grounding(request, answer, AssistantModel.Lite, hasImage = true)
        assertTrue(textOnly, textOnly.contains("not image pixels"))
    }

    @Test
    fun visionPhotoPromptStillProvidesActualEditingTools() = runBlocking {
        listOf("en", "ru").forEach { language ->
            val request = AssistantRequest(
                query = services.resources.getStringLocalized(
                    R.string.assistant_vision_photo_prompt,
                    language
                ),
                context = AssistantScreenContext(selectedUris = listOf("content://private/photo")),
                language = language
            )
            val answer = knowledge.retrieve(request)
            assertTrue(answer.tools.any { it.key == Screen.Crop().simpleName })
            assertTrue(answer.tools.any { it.key == Screen.SingleEdit().simpleName })
            assertEquals(request.query, knowledge.userPrompt(request, answer))
        }
    }

    @Test
    fun fileOnlyRequestsSuggestToolsForTheMeasuredMimeType() = runBlocking {
        listOf(
            "application/pdf" to listOf(
                Screen.PdfTools.Compress(),
                Screen.PdfTools.ExtractPages(),
                Screen.PdfTools.Metadata()
            ),
            "audio/mpeg" to listOf(Screen.AudioCoverExtractor()),
            "application/zip" to listOf(Screen.ArchiveTools())
        ).forEach { (mime, expected) ->
            listOf("en", "ru").forEach { language ->
                val request = AssistantRequest(
                    query = services.resources.getStringLocalized(
                        R.string.assistant_photo_prompt,
                        language
                    ),
                    context = AssistantScreenContext(
                        selectedUris = listOf("content://media/private-file"),
                        parameters = mapOf("selectedImageInfo" to "1: $mime, 4000000 bytes")
                    ),
                    language = language
                )
                val answer = knowledge.retrieve(request)
                assertEquals(
                    mime,
                    expected.map { it.simpleName }.toSet(),
                    answer.tools.map { it.key }.toSet()
                )
                val prompt = knowledge.grounding(request, answer)
                assertTrue(prompt, prompt.contains("Files: 1"))
                assertFalse(
                    prompt,
                    Regex("^Images:", RegexOption.MULTILINE).containsMatchIn(prompt)
                )
                assertFalse(prompt, prompt.contains("content://"))
            }
        }
    }

    @Test
    fun detailedHelpPagesMatchRealControlsWithoutAddingCatalogCards() {
        listOf(
            "depth-effects",
            "pdf-compression",
            "pdf-annotation-cleanup",
            "metadata-editing",
            "cache-cleanup"
        )
            .forEach { id ->
                val tip = checkNotNull(services.helpRepository.getTip(id))
                assertEquals(id, 3, tip.pages.first().steps.size)
                assertTrue(id, tip.deepLink != null)
            }
        val summaries = services.helpRepository.tips.filter { tip ->
            tip.pages.all { it.steps.isEmpty() && it.description == tip.subtitle }
        }
        assertTrue(summaries.map { it.id }.toString(), summaries.isEmpty())
        val report = JSONObject()
            .put(
                "detailedTips",
                services.helpRepository.tips.count { tip -> tip.pages.any { it.steps.isNotEmpty() } })
            .put("newDetailedTips", 5)
            .put("catalogToolSummaries", summaries.count { it.deepLink !is Screen.Settings })
            .put("catalogSettingSummaries", summaries.count { it.deepLink is Screen.Settings })
        File(
            InstrumentationRegistry.getInstrumentation().targetContext.cacheDir,
            "assistant-knowledge-coverage.json"
        )
            .writeText(report.toString(2))
    }

    @Test
    fun aiHelpDescribesAllAssistantModelsAndRecentFunctions() {
        val models = checkNotNull(services.helpRepository.getTip("assistant-models"))
        val guides = listOf(
            "assistant-workflow", "assistant-models", "assistant-images", "assistant-downloads",
            "ai-upscaling", "ai-cleanup", "ai-colorization", "ai-face-restoration", "ai-dehaze",
            "ai-document-cleanup", "ai-style-transfer", "ai-detection", "ai-background-models",
            "ai-inpainting", "ai-ocr", "ai-gallery", "depth-effects", "ai-parameters"
        ).map { checkNotNull(services.helpRepository.getTip(it)) }
        listOf("en", "ru").forEach { language ->
            fun text(ids: List<Int>) = ids.joinToString(" ") {
                services.resources.getStringLocalized(it, language)
            }

            val modelText = text(models.pages.flatMap { listOf(it.description) + it.steps })
            AssistantModel.entries.forEach { model ->
                assertTrue("$language: ${model.displayName}", modelText.contains(model.displayName))
            }
            val content =
                text(guides.flatMap { it.pages }.flatMap { listOf(it.description) + it.steps })
            listOf(
                "GFPGAN", "DehazeFormer-T", "DocShadow", "UVDOC", "LaMa", "DDColor",
                "SCUNet", "FBCNN", "MicroAST", "AesFA", "VGG19", "Depth Anything 3", "Mirage",
                "PaddleOCR v6"
            ).forEach { name -> assertTrue("$language: $name", content.contains(name)) }
            guides.forEach { guide ->
                assertEquals(guide.id, "ai-tools", guide.category.key)
                assertTrue(guide.id, guide.deepLink != null)
                guide.pages.forEach { page ->
                    assertTrue(guide.id, page.steps.isNotEmpty())
                    assertTrue(
                        guide.id,
                        text(listOf(page.title, page.description) + page.steps).isNotBlank()
                    )
                }
            }
        }
    }

    @Test
    fun assistantFindsGuidesForItsModelsAndRecentAiTools() = runBlocking {
        listOf(
            "Which Assistant model uses Qwen3.5 9B?" to "assistant-models",
            "Как восстановить лицо через GFPGAN?" to "ai-face-restoration",
            "How do I remove haze with DehazeFormer-T?" to "ai-dehaze",
            "Как убрать тени с документа через DocShadow?" to "ai-document-cleanup",
            "How do I use VGG19 style transfer?" to "ai-style-transfer"
        ).forEach { (query, tipId) ->
            val answer = knowledge.retrieve(AssistantRequest(query))
            assertTrue(
                "$query: ${answer.references.map { it.tipId }}",
                answer.references.any { it.tipId == tipId }
            )
        }
    }

    @Test
    fun newerToolsRetrieveTheirDocumentedControls() = runBlocking {
        listOf(
            "How do I create a depth mask?" to "depth-effects",
            "How do I remove links from a PDF?" to "pdf-annotation-cleanup",
            "Как изменить отдельный тег EXIF?" to "metadata-editing"
        ).forEach { (query, tipId) ->
            val answer = knowledge.retrieve(AssistantRequest(query))
            assertTrue(
                "$query: ${answer.references.map { it.tipId }}",
                answer.references.any { it.tipId == tipId })
        }
    }

    @Test
    fun everyToolModeAndSettingHasSubstantiveLinkedHelp() {
        val catalog = (Screen.typedEntries.flatMap { it.entries } + Screen.PdfTools.options +
                listOf(Screen.Assistant(), Screen.UsageStatistics, Screen.LibrariesInfo))
            .distinctBy { it.simpleName }
        val modes = Screen.Filter.Type.entries.map { Screen.Filter(it) } +
                Screen.RecognizeText.Type.entries.map { Screen.RecognizeText(it) } +
                Screen.GifTools.Type.entries.map { Screen.GifTools(it) } +
                Screen.ApngTools.Type.entries.map { Screen.ApngTools(it) } +
                Screen.WebpTools.Type.entries.map { Screen.WebpTools(it) } +
                Screen.JxlTools.Type.entries.map { Screen.JxlTools(it) }
        val settings = SettingsGroup.entries.flatMap { it.settingsList }.distinct()
        val tips = services.helpRepository.tips.filter { tip ->
            tip.pages.any { page ->
                page.steps.isNotEmpty() &&
                        listOf("ru", "en").all { language ->
                            (listOf(page.description) + page.steps).all {
                                services.resources.getStringLocalized(it, language).isNotBlank()
                            }
                        }
            }
        }
        val missingTools = catalog.filter { screen ->
            tips.none { it.deepLink?.simpleName == screen.simpleName }
        }.map { it.simpleName }
        val missingModes = modes.filter { screen -> tips.none { it.deepLink == screen } }
            .map { it.toString() }
        val missingSettings = settings.filter { setting ->
            tips.none { (it.deepLink as? Screen.Settings)?.targetSetting == setting }
        }.map { it.key() }
        val report = JSONObject()
            .put("detailedTips", tips.size)
            .put("catalogTools", catalog.size)
            .put("toolModes", modes.size)
            .put("settings", settings.size)
            .put("missingTools", JSONArray(missingTools))
            .put("missingModes", JSONArray(missingModes))
            .put("missingSettings", JSONArray(missingSettings))
        File(
            InstrumentationRegistry.getInstrumentation().targetContext.cacheDir,
            "assistant-linked-help-coverage.json"
        ).writeText(report.toString(2))
        assertTrue(
            report.toString(2),
            missingTools.isEmpty() && missingModes.isEmpty() && missingSettings.isEmpty()
        )
    }

    @Test
    fun practicalQuestionsRetrieveDocumentedToolsAndSettings() = runBlocking {
        val toolQuestions = listOf(
            "Как сделать GIF из фото?" to Screen.GifTools(),
            "How do I make a GIF from photos?" to Screen.GifTools(),
            "Зачем Flatten PDF?" to Screen.PdfTools.Flatten(),
            "Why should I flatten a PDF?" to Screen.PdfTools.Flatten(),
            "Как рассчитать контрольную сумму файла?" to Screen.ChecksumTools(),
            "How do I compare file checksums?" to Screen.ChecksumTools()
        )
        val settingQuestions = listOf(
            "Можно сохранить дату при очистке EXIF?" to Setting.KeepDateTime,
            "Can I keep dates when clearing EXIF?" to Setting.KeepDateTime,
            "Почему неактивен лимит кэша?" to Setting.CacheAutoClearLimit,
            "Why is the cache limit disabled?" to Setting.CacheAutoClearLimit
        )
        val failures = mutableListOf<String>()
        toolQuestions.forEach { (query, screen) ->
            val answer = knowledge.retrieve(AssistantRequest(query))
            if (answer.tools.none { it.key == screen.simpleName } || answer.references.none {
                    services.helpRepository.getTip(it.tipId)?.deepLink?.simpleName == screen.simpleName
                }) {
                failures.add("$query: ${answer.tools}, ${answer.references.map { it.tipId }}")
            }
        }
        settingQuestions.forEach { (query, setting) ->
            val request = AssistantRequest(query)
            val answer = knowledge.retrieve(request)
            if (answer.settings.none { it.key == setting.key() } || answer.references.none {
                    (services.helpRepository.getTip(it.tipId)?.deepLink as? Screen.Settings)?.targetSetting == setting
                }) {
                failures.add("$query: ${answer.settings}, ${answer.references.map { it.tipId }}")
            }
            val prompt = knowledge.grounding(request, answer)
            if ("Help:" !in prompt) failures.add("$query: no documented instructions in grounding")
        }
        assertTrue(failures.joinToString("\n"), failures.isEmpty())
    }

    @Test
    fun followUpQuestionsStayInTheCurrentDocumentedMode() = runBlocking {
        listOf("ru", "en").forEach { language ->
            listOf(
                Triple(
                    Screen.GifTools(Screen.GifTools.Type.ImageToGif()),
                    Screen.GifTools.Type.ImageToGif().title,
                    if (language == "ru") "Как поменять задержку между кадрами?" else "How do I change the delay between frames?"
                ),
                Triple(
                    Screen.RecognizeText(Screen.RecognizeText.Type.WriteToSearchablePdf()),
                    Screen.RecognizeText.Type.WriteToSearchablePdf().title,
                    if (language == "ru") "Как выбрать язык распознавания?" else "How do I choose the recognition language?"
                )
            ).forEach { (screen, modeTitle, query) ->
                val mode = services.resources.getStringLocalized(modeTitle, language)
                val request = AssistantRequest(
                    query,
                    AssistantScreenContext(
                        toolId = screen.id,
                        parameters = mapOf("toolKey" to screen.simpleName, "mode" to mode)
                    ),
                    language
                )
                val answer = knowledge.retrieve(request)
                val linkedModes =
                    answer.references.map { services.helpRepository.getTip(it.tipId)?.deepLink }
                assertTrue("$query: $linkedModes", linkedModes.any { it == screen })
                assertTrue(query, answer.settings.isEmpty())
                assertTrue(query, answer.tools.all { it.key == screen.simpleName })
                assertTrue(query, knowledge.grounding(request, answer).contains(mode))
            }
        }
    }

    @Test
    fun aCurrentToolDoesNotHideExplicitApplicationSettings() = runBlocking {
        val screen = Screen.GifTools(Screen.GifTools.Type.ImageToGif())
        val context = AssistantScreenContext(
            toolId = screen.id,
            parameters = mapOf(
                "toolKey" to screen.simpleName,
                "mode" to services.resources.getStringLocalized(
                    Screen.GifTools.Type.ImageToGif().title,
                    "en"
                )
            )
        )
        listOf(
            "Где выключить удаление оригиналов после сохранения?" to Setting.DeleteOriginalsAfterSave,
            "How do I clear cache automatically when too large?" to Setting.AutoCacheClear
        ).forEach { (query, setting) ->
            val answer = knowledge.retrieve(AssistantRequest(query, context))
            assertTrue("$query: $answer", answer.settings.any { it.key == setting.key() })
            assertTrue("$query: $answer", answer.tools.none { it.key == screen.simpleName })
            if (setting == Setting.AutoCacheClear) {
                assertTrue(query, answer.references.any { it.text.contains("Cache limit") })
            }
        }
    }

    @Test
    fun recordsColdAndWarmRetrievalForTheInstalledHelpCorpus() = runBlocking {
        val report = JSONArray()
        listOf("ru", "en").forEach { language ->
            val instance = services.knowledge
            val request = AssistantRequest(
                if (language == "ru") "Как сделать GIF из фото?" else "How do I make a GIF from photos?",
                language = language
            )
            val cold = measureNanoTime { instance.retrieve(request) } / 1_000_000.0
            val warm = List(5) { measureNanoTime { instance.retrieve(request) } / 1_000_000.0 }
            val answer = instance.retrieve(request)
            assertTrue(answer.toString(), answer.references.isNotEmpty())
            report.put(
                JSONObject()
                    .put("language", language)
                    .put("tips", services.helpRepository.tips.size)
                    .put("coldMs", cold)
                    .put("warmMs", JSONArray(warm))
                    .put("promptCharacters", knowledge.grounding(request, answer).length)
            )
        }
        File(
            InstrumentationRegistry.getInstrumentation().targetContext.cacheDir,
            "assistant-help-retrieval-timing.json"
        ).writeText(report.toString(2))
    }

    @Test
    fun disablingShadowsFindsTheActualAppearanceSwitches() = runBlocking {
        val shadows = setOf(
            Setting.ContainerShadows, Setting.AppBarShadows, Setting.SliderShadows,
            Setting.SwitchShadows, Setting.FABShadows, Setting.ButtonShadows
        ).map { it.key() }
        listOf("Как выключить тени?", "How do I turn off shadows?").forEach { query ->
            val answer = knowledge.retrieve(AssistantRequest(query))
            assertTrue("$query: $answer", answer.settings.isNotEmpty())
            assertTrue("$query: $answer", answer.settings.all { it.key in shadows })
            assertTrue("$query: $answer", answer.references.isNotEmpty())
            assertTrue("$query: $answer", answer.tools.isEmpty())
        }
    }

    @Test
    fun photoOpacityUsesTheDocumentedFilterInsteadOfAnApplicationSetting() = runBlocking {
        listOf(
            "ru" to "Как изменить прозрачность у фото?",
            "en" to "How do I change a photo's opacity?"
        ).forEach { (language, query) ->
            val request = AssistantRequest(query, language = language)
            val answer = knowledge.retrieve(request)
            assertTrue("$query: $answer", answer.settings.isEmpty())
            assertTrue("$query: $answer", answer.references.any {
                services.helpRepository.getTip(it.tipId)?.deepLink ==
                        Screen.Filter(Screen.Filter.Type.Basic(filterKey = "opacity"))
            })
            assertTrue("$query: $answer", answer.tools.any {
                knowledge.resolveTool(it) == Screen.Filter(Screen.Filter.Type.Basic(filterKey = "opacity"))
            })
            val prompt = knowledge.grounding(request, answer)
            assertTrue(
                query,
                prompt.contains(services.resources.getStringLocalized(R.string.opacity, language))
            )
        }
    }

    @Test
    fun namedFiltersResolveToValidatedConfiguredTools() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val filters = UiFilter.groups.flatMap { it.filters(canAddTemplates = true) }
            .filter {
                context.resources.getResourceEntryName(it.title) in setOf(
                    "opacity",
                    "contrast",
                    "brightness"
                )
            }
        assertTrue(filters.toString(), filters.size >= 3)
        listOf("ru", "en").forEach { language ->
            filters.forEach { filter ->
                val title = services.resources.getStringLocalized(filter.title, language)
                val key = context.resources.getResourceEntryName(filter.title)
                val query =
                    if (language == "ru") "Добавить фильтр $title" else "Add the $title filter"
                val answer = knowledge.retrieve(AssistantRequest(query, language = language))
                val tool = answer.tools.firstOrNull { it.filterKey == key }
                assertTrue("$query: $answer", tool != null)
                assertEquals(
                    Screen.Filter(Screen.Filter.Type.Basic(filterKey = key)),
                    knowledge.resolveTool(checkNotNull(tool))
                )
                assertTrue("$query: $answer", answer.references.isNotEmpty())
                assertTrue("$query: $answer", answer.references.all {
                    services.helpRepository.getTip(it.tipId)?.deepLink ==
                            Screen.Filter(Screen.Filter.Type.Basic(filterKey = key))
                })
                assertTrue(
                    query,
                    knowledge.grounding(AssistantRequest(query, language = language), answer)
                        .contains("Help:\n$title\n")
                )
            }
        }
        assertEquals(
            null,
            knowledge.resolveTool(
                AssistantTool(
                    "Filter",
                    "Unknown",
                    "",
                    filterKey = "made_up_filter"
                )
            )
        )
    }

    @Test
    fun everyFilterHasLocalizedHelpForItsConfiguredDestination() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val filters = UiFilter.groups.flatMap { it.filters(canAddTemplates = true) }
            .associateBy { context.resources.getResourceEntryName(it.title) }
        filters.forEach { (key, filter) ->
            val tip = services.helpRepository.getTip("filter-$key")
            assertTrue("Missing filter help: $key", tip != null)
            assertEquals(
                key,
                Screen.Filter(Screen.Filter.Type.Basic(filterKey = key)),
                checkNotNull(tip).deepLink
            )
            listOf("ru", "en").forEach { language ->
                val title = services.resources.getStringLocalized(filter.title, language)
                assertTrue("$key/$language", tip.pages.any { page ->
                    val description =
                        services.resources.getStringLocalized(page.description, language)
                    description.isNotBlank() && description != title
                })
            }
        }
    }

    @Test
    fun exactToolAndSettingTitlesDoNotSelectAFilter() = runBlocking {
        listOf("ru", "en").forEach { language ->
            val filterToolTitle =
                services.resources.getStringLocalized(Screen.Filter().title, language)
            val toolAnswer =
                knowledge.retrieve(AssistantRequest(filterToolTitle, language = language))
            assertTrue(
                toolAnswer.toString(),
                toolAnswer.tools.any { it.key == Screen.Filter().simpleName })
            assertTrue(toolAnswer.toString(), toolAnswer.tools.all { it.filterKey == null })
            listOf(Setting.ContainerShadows, Setting.ColorBlindScheme).forEach { setting ->
                val title = services.resources.getStringLocalized(setting.title, language)
                val answer = knowledge.retrieve(AssistantRequest(title, language = language))
                assertTrue("$title: $answer", answer.settings.any { it.key == setting.key() })
                assertTrue("$title: $answer", answer.tools.isEmpty())
            }
        }
    }

    @Test
    fun aFollowUpAboutTheSelectedFilterUsesItsOwnDescription() = runBlocking {
        listOf(
            "ru" to "Что делает этот фильтр?",
            "en" to "What does this filter do?"
        ).forEach { (language, query) ->
            val request = AssistantRequest(
                query,
                AssistantScreenContext(
                    toolId = Screen.Filter().id,
                    parameters = mapOf(
                        "toolKey" to Screen.Filter().simpleName,
                        "mode" to services.resources.getStringLocalized(
                            R.string.full_filter,
                            language
                        ),
                        "filterKey" to "contrast"
                    )
                ),
                language
            )
            val answer = knowledge.retrieve(request)
            assertTrue("$query: $answer", answer.tools.any { it.filterKey == "contrast" })
            assertTrue("$query: $answer", answer.references.isNotEmpty())
            assertTrue("$query: $answer", answer.references.all {
                services.helpRepository.getTip(it.tipId)?.deepLink ==
                        Screen.Filter(Screen.Filter.Type.Basic(filterKey = "contrast"))
            })
            assertTrue("$query: $answer", answer.settings.isEmpty())
        }
    }

    @Test
    fun describedEffectsFindFiltersWithoutKnowingTheirNames() = runBlocking {
        listOf(
            "Как убрать пурпурные ореолы?" to setOf("gmic_filter_unpurple"),
            "How can I remove purple fringes?" to setOf("gmic_filter_unpurple"),
            "Какой из фильтров убирает пурпурные ореолы?" to setOf("gmic_filter_unpurple"),
            "Which filters reduce purple fringes?" to setOf("gmic_filter_unpurple"),
            "Как перенести цвета с другого фото?" to setOf(
                "palette_transfer",
                "palette_transfer_variant"
            ),
            "How can I transfer colors from another photo?" to setOf(
                "palette_transfer",
                "palette_transfer_variant"
            )
        ).forEach { (query, expectedKeys) ->
            val request = AssistantRequest(query)
            val answer = knowledge.retrieve(request)
            val tool = answer.tools.firstOrNull { it.filterKey in expectedKeys }
            assertTrue("$query: $answer", tool != null)
            val destination = knowledge.resolveTool(checkNotNull(tool))
            assertEquals(
                query,
                Screen.Filter(Screen.Filter.Type.Basic(filterKey = tool.filterKey)),
                destination
            )
            assertTrue("$query: $answer", answer.settings.isEmpty())
            val reference = answer.references.firstOrNull {
                services.helpRepository.getTip(it.tipId)?.deepLink == destination
            }
            assertTrue("$query: $answer", reference != null)
            assertTrue(
                query,
                knowledge.grounding(request, answer).contains("Help:\n${tool.title}\n")
            )
        }
    }

    @Test
    fun practicalGenerationQuestionsRetrieveTheExactDocumentedModeAndFilters() = runBlocking {
        listOf(
            "Как собрать GIF из нескольких фотографий и настроить задержку кадров?" to
                    Screen.GifTools(Screen.GifTools.Type.ImageToGif()),
            "Как затемнить края фото и оставить центр светлым?" to
                    Screen.Filter(Screen.Filter.Type.Basic(filterKey = "vignette")),
            "What do radius and sigma do in Gaussian blur?" to
                    Screen.Filter(Screen.Filter.Type.Basic(filterKey = "gaussian_blur"))
        ).forEach { (query, destination) ->
            val request = AssistantRequest(query)
            val answer = knowledge.retrieve(request)
            assertTrue(
                "$query: $answer",
                answer.tools.any { knowledge.resolveTool(it) == destination })
            assertTrue("$query: $answer", answer.references.any {
                services.helpRepository.getTip(it.tipId)?.deepLink == destination
            })
            assertTrue("$query: $answer", answer.settings.isEmpty())
            if (destination is Screen.GifTools) {
                assertTrue(query, knowledge.grounding(request, answer).contains("FPS"))
            }
        }
    }

}
