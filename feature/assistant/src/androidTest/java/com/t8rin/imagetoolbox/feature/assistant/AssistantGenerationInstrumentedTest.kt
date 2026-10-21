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

import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.genai.GenAI
import ai.onnxruntime.genai.Generator
import ai.onnxruntime.genai.GeneratorParams
import ai.onnxruntime.genai.Model
import ai.onnxruntime.genai.Tokenizer
import android.os.Debug
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.t8rin.imagetoolbox.core.di.entryPoint
import com.t8rin.imagetoolbox.core.domain.model.AssistantModel
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.presentation.model.Setting
import com.t8rin.imagetoolbox.core.settings.presentation.model.key
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.feature.assistant.di.AssistantEntryPoint
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantAnswer
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantRequest
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantScreenContext
import com.t8rin.neural_tools.assistant.AssistantModels
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.Json
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/** Generation requires installed packages; real network downloads are explicitly opt-in. */
@RunWith(AndroidJUnit4::class)
class AssistantGenerationInstrumentedTest {
    private val app = InstrumentationRegistry.getInstrumentation().targetContext
    private val services = app.entryPoint<AssistantEntryPoint>()
    private val knowledge = services.knowledge
    private val standardModels = listOf(AssistantModel.Lite, AssistantModel.Advanced)

    @Test
    fun liteGeneratesGroundedApplicationRepliesAndRecoversAfterCancellation() = runBlocking {
        verify(AssistantModel.Lite)
    }

    @Test
    fun advancedGeneratesGroundedApplicationRepliesAndRecoversAfterCancellation() = runBlocking {
        verify(AssistantModel.Advanced)
    }

    @Test
    fun modelsExplainPhotoCompressionUsingActualToolsAndFormatFacts() = runBlocking {
        val models = AssistantModels()
        assumeTrue(
            "Install both packages before testing compression",
            models.installations().filter { it.variant in standardModels }.all { it.installed })
        val report = JSONArray()
        try {
            standardModels.forEach { variant ->
                AssistantApplicationFixtures.requests.filter { it.first.endsWith("compress_question") }
                    .forEach { fixture ->
                        val answer = knowledge.retrieve(fixture.second)
                        val prompt = AssistantApplicationFixtures.prompt(
                            knowledge.grounding(fixture.second, answer, variant),
                            knowledge.userPrompt(fixture.second, answer)
                        )
                        val record =
                            JSONObject().put("variant", variant.id).put("case", fixture.first)
                        report.put(record)
                        verifyGeneration(models, variant, prompt, fixture, answer, record)
                    }
            }
        } finally {
            models.release()
            File(app.cacheDir, "assistant-compression-quality.json").writeText(report.toString(2))
        }
    }

    @Test
    fun modelsUseSelectedFilesCurrentToolAndSettings() = runBlocking {
        val models = AssistantModels()
        assumeTrue(models.installations().filter { it.variant in standardModels }
            .all { it.installed })
        val requests = listOf(
            "selected_photo" to AssistantRequest(
                "Что можно сделать с этим фото?",
                AssistantScreenContext(
                    selectedUris = listOf("content://assistant-test/photo"),
                    parameters = mapOf("selectedImageInfo" to "image/jpeg, 4032 × 3024, 5000000 bytes")
                )
            ),
            "current_tool" to AssistantRequest(
                "Как лучше настроить этот инструмент?",
                AssistantScreenContext(
                    toolId = Screen.WeightResize().id,
                    selectedUris = listOf("content://assistant-test/photo"),
                    parameters = mapOf(
                        "toolKey" to Screen.WeightResize().simpleName,
                        "maxBytes" to "512000", "imageFormat" to "PNG", "keepExif" to "true"
                    )
                )
            ),
            "setting" to AssistantRequest("Где выключить удаление оригиналов после сохранения?")
        )
        val report = JSONArray()
        try {
            standardModels.forEach { variant ->
                requests.forEach { fixture ->
                    println("AssistantContext ${variant.id}/${fixture.first} started")
                    val answer = knowledge.retrieve(fixture.second)
                    val prompt = AssistantApplicationFixtures.prompt(
                        knowledge.grounding(fixture.second, answer, variant),
                        knowledge.userPrompt(fixture.second, answer)
                    )
                    val record = JSONObject().put("variant", variant.id).put("case", fixture.first)
                    report.put(record)
                    verifyGeneration(models, variant, prompt, fixture, answer, record)
                    when (fixture.first) {
                        "selected_photo" -> assertTrue(
                            "No useful image tool was offered",
                            answer.tools.isNotEmpty()
                        )

                        "current_tool" -> assertTrue(answer.tools.any { it.key == Screen.WeightResize().simpleName })
                        "setting" -> assertTrue(answer.settings.any { it.key == Setting.DeleteOriginalsAfterSave.key() })
                    }
                    assertFalse(
                        "The assistant pretended to inspect image contents",
                        Regex(
                            "на (?:фото|изображении) (?:вижу|изображен|изображён)|I (?:can )?see",
                            RegexOption.IGNORE_CASE
                        )
                            .containsMatchIn(record.getString("rawOutput"))
                    )
                    if (fixture.first == "setting") {
                        assertFalse(
                            "The assistant invented the setting's default value",
                            Regex(
                                "включ[её]н[а-я]*\\s*\\(?по умолчанию|enabled\\s+by default",
                                RegexOption.IGNORE_CASE
                            )
                                .containsMatchIn(record.getString("rawOutput").replace("*", ""))
                        )
                    }
                    println("AssistantContext ${variant.id}/${fixture.first} passed")
                }
            }
        } finally {
            models.release()
            File(app.cacheDir, "assistant-context-quality.json").writeText(report.toString(2))
        }
    }

    @Test
    fun modelsExplainExpandedHelp() = runBlocking {
        val models = AssistantModels()
        val selectedCases = InstrumentationRegistry.getArguments()
            .getString("assistantCases")?.split(',')
        val selectedModels = InstrumentationRegistry.getArguments()
            .getString("assistantModels")?.split(',')
        val variants = AssistantModel.entries.filter {
            if (selectedModels == null) !it.supportsVision else it.id in selectedModels
        }
        assertTrue("No requested models were found", variants.isNotEmpty())
        val installationStarted = System.nanoTime()
        assumeTrue(models.installations().filter { it.variant in variants }.all { it.installed })
        val installationSeconds = secondsSince(installationStarted)
        val requests = listOf(
            "gif_frames" to AssistantRequest("Как собрать GIF из нескольких фотографий и настроить задержку кадров?"),
            "flatten_pdf" to AssistantRequest("What does Flatten PDF change? Will the text stay selectable?"),
            "exif_date" to AssistantRequest("Почему при очистке EXIF нельзя сохранить дату?"),
            "cache_limit" to AssistantRequest("How do I clear the cache automatically when it gets too large?"),
            "photo_opacity" to AssistantRequest("как изменить прозрачность у фото?"),
            "interface_shadows" to AssistantRequest("как выключить тени"),
            "vignette" to AssistantRequest("Как затемнить края фото и оставить центр светлым?"),
            "gaussian_blur" to AssistantRequest("What do radius and sigma do in Gaussian blur?"),
            "selected_filter" to AssistantRequest(
                "Что делает этот фильтр?",
                AssistantScreenContext(
                    toolId = Screen.Filter().id,
                    parameters = mapOf(
                        "toolKey" to Screen.Filter().simpleName,
                        "filterKey" to "white_balance"
                    )
                )
            )
        ).filter { selectedCases == null || it.first in selectedCases }
        assertTrue("No requested generation cases were found", requests.isNotEmpty())
        val report = JSONArray()
        try {
            variants.forEach { variant ->
                requests.forEach { fixture ->
                    val record = JSONObject().put("variant", variant.id).put("case", fixture.first)
                    report.put(record)
                    println("AssistantHelp ${variant.id}/${fixture.first} started")
                    record.put("installationSeconds", installationSeconds)
                    val started = System.nanoTime()
                    val answer = knowledge.retrieve(fixture.second)
                    record.put("retrievalSeconds", secondsSince(started))
                        .put(
                            "retrieved",
                            JSONObject(Json.encodeToString(AssistantAnswer.serializer(), answer))
                        )
                    assertTrue(
                        "The answer has no supporting documentation",
                        answer.references.isNotEmpty()
                    )
                    when (fixture.first) {
                        "gif_frames" -> assertTrue(answer.tools.any {
                            (knowledge.resolveTool(it) as? Screen.GifTools)?.type is Screen.GifTools.Type.ImageToGif
                        })

                        "flatten_pdf" -> assertTrue(answer.tools.any {
                            knowledge.resolveTool(it) is Screen.PdfTools.Flatten
                        })

                        "exif_date" -> assertTrue(answer.settings.any {
                            it.key == Setting.AlwaysClearExif.key() || it.key == Setting.KeepDateTime.key()
                        })

                        "cache_limit" -> assertTrue(answer.settings.any {
                            it.key == Setting.CacheAutoClearLimit.key() || it.key == Setting.AutoCacheClear.key()
                        })

                        "photo_opacity" -> {
                            assertTrue(answer.tools.any {
                                knowledge.resolveTool(it) is Screen.Filter
                            })
                            assertTrue(answer.settings.isEmpty())
                        }

                        "interface_shadows" -> assertTrue(answer.settings.any {
                            it.key.endsWith("Shadows")
                        })

                        "vignette", "gaussian_blur", "selected_filter" -> {
                            val key = when (fixture.first) {
                                "selected_filter" -> "white_balance"
                                else -> fixture.first
                            }
                            assertTrue(answer.tools.any {
                                ((knowledge.resolveTool(it) as? Screen.Filter)?.type
                                        as? Screen.Filter.Type.Basic)?.filterKey == key
                            })
                            assertTrue(answer.settings.isEmpty())
                        }
                    }
                    val prompt = AssistantApplicationFixtures.prompt(
                        knowledge.grounding(fixture.second, answer, variant),
                        knowledge.userPrompt(fixture.second, answer)
                    )
                    verifyGeneration(models, variant, prompt, fixture, answer, record)
                    println("AssistantHelp ${variant.id}/${fixture.first} passed")
                }
            }
        } finally {
            models.release()
            File(app.cacheDir, "assistant-expanded-help-quality.json").writeText(report.toString(2))
        }
    }

    @Test
    fun rejectsLossyPngClaimsAcrossGeneratedWordingAndMarkdown() {
        listOf(
            "Prefer lossy formats like PNG.",
            "For photos, prefer **lossy formats** (PNG) to preserve quality.",
            "PNG is a lossy format.",
            "PNG, a lossy format, saves space.",
            "PNG (lossy)",
            "PNG — формат с потерями."
        ).forEach { assertTrue(it, describesPngAsLossy(it)) }
        listOf(
            "JPEG or lossy WebP are suitable. PNG is lossless.",
            "Use JPEG (lossy) or PNG (lossless).",
            "PNG is not a lossy format.",
            "Используйте JPEG или WebP с потерями. PNG — формат без потерь.",
            "Выберите формат (PNG, JPEG, WebP), например JPEG для фотографий с потерями, PNG для беспотерьных изображений."
        ).forEach { assertFalse(it, describesPngAsLossy(it)) }
    }

    @Test
    fun rejectsInventedCompressionLevelPresets() {
        listOf(
            "Choose a compression level (e.g., low, medium, high).",
            "Select a **high** quality preset.",
            "Выберите уровень сжатия: низкий, средний или высокий."
        ).forEach { assertTrue(it, inventsCompressionPresets(it)) }
        listOf(
            "Enter the maximum file size in the Max size in KB field.",
            "Choose a percentage preset or enter a maximum file size.",
            "Введите максимальный вес в КБ.",
            "Very small files may have low image quality."
        ).forEach { assertFalse(it, inventsCompressionPresets(it)) }
    }

    @Test
    fun switchesModelsAndRejectsOversizedPrompts(): Unit = runBlocking {
        val models = AssistantModels()
        assumeTrue(
            "Install all three packages before testing switching",
            models.installations().filterNot { it.variant.supportsVision }.all { it.installed }
        )
        val report = JSONObject()
        val replies = JSONArray()
        try {
            listOf(
                AssistantModel.Lite,
                AssistantModel.Advanced,
                AssistantModel.Max,
                AssistantModel.Lite
            )
                .forEach { variant ->
                    repeat(2) { run ->
                        val text = StringBuilder()
                        val started = System.nanoTime()
                        var firstToken = 0L
                        withTimeout(180_000) {
                            models.generate(
                                variant,
                                AssistantApplicationFixtures.prompt("", "Hi")
                            ) {
                                if (firstToken == 0L) firstToken = System.nanoTime()
                                text.append(it)
                            }
                        }
                        assertTrue(
                            "Switching to ${variant.id} produced an empty reply",
                            text.isNotBlank()
                        )
                        replies.put(
                            JSONObject().put("model", variant.id).put("run", run)
                                .put("ttftSeconds", (firstToken - started) / 1e9)
                                .put("totalSeconds", (System.nanoTime() - started) / 1e9)
                                .put("reply", text.toString())
                        )
                    }
                }
            var rejected = false
            try {
                models.generate(
                    AssistantModel.Lite,
                    "test ".repeat(AssistantModels.MAX_PROMPT_TOKENS + 1)
                ) {
                    error("An oversized prompt emitted output")
                }
            } catch (expected: AssistantModels.InputTooLongException) {
                rejected = true
                assertTrue(expected.tokenCount > AssistantModels.MAX_PROMPT_TOKENS)
            }
            assertTrue("The oversized prompt was not rejected", rejected)
            val recovered = StringBuilder()
            models.generate(
                AssistantModel.Lite,
                AssistantApplicationFixtures.prompt("", "Hi")
            ) {
                recovered.append(it)
            }
            assertTrue("Generation did not recover after input validation", recovered.isNotBlank())
            report.put("passed", true).put("replies", replies)
        } finally {
            models.release()
            File(app.cacheDir, "assistant-model-switching.json").writeText(report.toString(2))
        }
    }

    @Test
    fun downloadsPublishedModelsAndRecoversAfterCancellation(): Unit = runBlocking {
        assumeTrue(
            "Pass assistantDownloadModels=true to download the published HF packages",
            InstrumentationRegistry.getArguments().getString("assistantDownloadModels") == "true"
        )
        val models = AssistantModels()
        val report = JSONObject().put("passed", false)
        val downloads = JSONArray()
        report.put("downloads", downloads)
        try {
            standardModels.forEach { models.delete(it) }
            standardModels.forEach { variant ->
                val record = JSONObject().put("variant", variant.id)
                downloads.put(record)
                verifyDownloadCancellation(models, variant, record)
                downloadAndVerify(models, variant, record)
            }
            val advancedDirectory = File(app.filesDir, "ai_models/assistant/advanced")
            val advancedSnapshot = advancedDirectory.listFiles()!!.associate {
                it.name to (it.length() to it.lastModified())
            }
            models.delete(AssistantModel.Lite)
            val afterDelete = models.installations()
            assertFalse(afterDelete.single { it.variant == AssistantModel.Lite }.installed)
            assertTrue(afterDelete.single { it.variant == AssistantModel.Advanced }.installed)
            assertEquals(
                advancedSnapshot,
                advancedDirectory.listFiles()!!.associate {
                    it.name to (it.length() to it.lastModified())
                }
            )
            report.put("independentDeletionPassed", true)
            downloadAndVerify(
                models,
                AssistantModel.Lite,
                JSONObject().also {
                    report.put("liteReinstallation", it)
                }
            )
            assertTrue(models.installations().filter { it.variant in standardModels }
                .all { it.installed })
            report.put("passed", true)
        } catch (failure: Throwable) {
            report.put("error", failure.toString())
            throw failure
        } finally {
            withContext(NonCancellable + Dispatchers.IO) {
                models.release()
                File(app.cacheDir, "assistant-hf-download.json").writeText(report.toString(2))
            }
        }
    }

    private suspend fun verifyDownloadCancellation(
        models: AssistantModels,
        variant: AssistantModel,
        report: JSONObject
    ) = coroutineScope {
        val staging = File(app.filesDir, "ai_models/assistant/.${variant.id}.download")
        val observer = AssistantModels()
        val partial = CompletableDeferred<Pair<Float, Long>>()
        var cancelled = false
        val download = launch(Dispatchers.IO) {
            try {
                models.download(variant) { progress ->
                    if (progress.currentPercent > 0.001f && progress.currentPercent < 0.9f) {
                        val written = staging.walkTopDown().filter {
                            it.isFile && (it.name.endsWith(".tmp") || it.name.endsWith(".download"))
                        }.sumOf(File::length)
                        if (written > 0L) {
                            partial.complete(progress.currentPercent to written)
                            awaitCancellation()
                        }
                    }
                }
            } catch (expected: CancellationException) {
                cancelled = true
            }
        }
        try {
            val (fraction, bytes) = withTimeout(180_000) { partial.await() }
            report.put("cancelledAtFraction", fraction).put("partialTemporaryBytes", bytes)
            val observed = observer.states.value.single { it.variant == variant }
            assertEquals(AssistantModels.Phase.Downloading, observed.phase)
            assertEquals(fraction, checkNotNull(observed.progress), 0f)
            val snapshot = withTimeout(30_000) { observer.installations() }
            assertFalse(snapshot.single { it.variant == variant }.installed)
            assertTrue(download.isActive)
            val retrieved = knowledge.retrieve(AssistantApplicationFixtures.requests.first().second)
            assertTrue(
                "RAG retrieval was blocked by the other screen's download",
                retrieved.references.isNotEmpty()
            )
            if (variant == AssistantModel.Advanced) {
                withTimeout(60_000) { observer.download(AssistantModel.Lite) {} }
                assertTrue(observer.states.value.single { it.variant == AssistantModel.Lite }.installed)
                assertTrue(
                    "The other model's operation did not complete independently",
                    download.isActive
                )
                report.put("otherVariantCompletesWhileDownloading", true)
            }
            report.put("sharedProgressAndRetrievalReadinessPassed", true)
            observer.cancelDownload(variant)
            withTimeout(30_000) { download.join() }
        } finally {
            withContext(NonCancellable) { download.cancelAndJoin() }
        }
        assertTrue("Cancellation was not observed for ${variant.id}", cancelled)
        assertFalse("Partial staging survived cancellation", staging.exists())
        val state = models.installations().single { it.variant == variant }
        assertFalse(state.installed)
        assertEquals(AssistantModels.Phase.Missing, state.phase)
        assertEquals(null, state.error)
        report.put("partialCancellationPassed", true)
    }

    private suspend fun downloadAndVerify(
        models: AssistantModels,
        variant: AssistantModel,
        report: JSONObject
    ) {
        val started = System.nanoTime()
        var lastProgress = 0f
        withTimeout(1_800_000) {
            coroutineScope {
                val startedDownload = CompletableDeferred<Unit>()
                val owner = async {
                    models.download(variant) { progress ->
                        assertTrue(progress.currentPercent in lastProgress..1f)
                        lastProgress = progress.currentPercent
                        startedDownload.complete(Unit)
                    }
                }
                startedDownload.await()
                val observer = AssistantModels()
                val waiter = async { observer.download(variant) {} }
                owner.await()
                waiter.await()
                assertTrue(observer.states.value.single { it.variant == variant }.installed)
            }
        }
        report.put("concurrentWaiterCompletionPassed", true)
        assertEquals(1f, lastProgress, 0f)
        val state = models.installations().single { it.variant == variant }
        assertTrue(
            "The downloaded ${variant.id} package failed SHA-256 verification",
            state.installed
        )
        assertEquals(AssistantModels.Phase.Installed, state.phase)
        val files = File(app.filesDir, "ai_models/assistant/${variant.id}").listFiles()!!.toList()
        assertEquals(
            setOf(
                "config.json",
                "genai_config.json",
                "generation_config.json",
                "model.onnx",
                "model.onnx.data",
                "tokenizer.json",
                "tokenizer_config.json"
            ),
            files.map(File::getName).toSet()
        )
        assertTrue(files.all(File::isFile))
        assertEquals(state.downloadSize, files.sumOf(File::length))
        report.put("runtimeFiles", JSONArray(files.map(File::getName).sorted()))
            .put("runtimeBytes", state.installedSize)
            .put("seconds", (System.nanoTime() - started) / 1_000_000_000.0)
            .put("sha256InstallationPassed", true)
        var repeatedProgress = 0f
        models.download(variant) { repeatedProgress = it.currentPercent }
        assertEquals(1f, repeatedProgress, 0f)
        report.put("alreadyInstalledRetryPassed", true)
    }

    @Test
    fun managerRejectsMissingOrCorruptedFiles() = runBlocking {
        val directories = standardModels.associateWith {
            File(app.filesDir, "ai_models/assistant/${it.id}")
        }
        assumeTrue(
            "Install both real packages before running integrity tests",
            directories.values.all {
                it.resolve("tokenizer_config.json").isFile
            }
        )
        val models = AssistantModels()
        val otherModels = AssistantModels()
        try {
            assertTrue(
                "An installed package failed initial integrity verification",
                models.installations().filter { it.variant in standardModels }.all { it.installed }
            )
            assertTrue(otherModels.installations().filter { it.variant in standardModels }
                .all { it.installed })
            directories.forEach { (variant, directory) ->
                val target = directory.resolve("tokenizer_config.json")
                val original = target.readBytes()
                val originalModified = target.lastModified()
                val backup = File(
                    app.cacheDir,
                    "assistant-integrity-${variant.id}-${System.nanoTime()}.backup"
                )
                try {
                    check(target.renameTo(backup))
                    val missing = otherModels.installations()
                    assertFalse(
                        "The missing ${variant.id} file was accepted",
                        missing.single { it.variant == variant }.installed
                    )
                    assertTrue(
                        "The other package was affected",
                        missing.filter { it.variant in standardModels && it.variant != variant }
                            .all { it.installed }
                    )
                    assertFalse(models.installations().single { it.variant == variant }.installed)
                    check(backup.renameTo(target))
                    assertTrue(models.installations().single { it.variant == variant }.installed)

                    val corrupted = original.copyOf()
                    corrupted[corrupted.lastIndex] = (corrupted.last().toInt() xor 1).toByte()
                    target.writeBytes(corrupted)
                    check(target.setLastModified(originalModified + 5000))
                    assertEquals(original.size.toLong(), target.length())
                    assertTrue(target.lastModified() != originalModified)
                    val invalid = models.installations()
                    assertFalse(
                        "The same-size corrupted ${variant.id} file was accepted",
                        invalid.single { it.variant == variant }.installed
                    )
                    assertTrue(
                        "The other package was affected",
                        invalid.filter { it.variant in standardModels && it.variant != variant }
                            .all { it.installed }
                    )
                    assertFalse(
                        otherModels.installations().single { it.variant == variant }.installed
                    )
                } finally {
                    withContext(NonCancellable + Dispatchers.IO) {
                        target.writeBytes(original)
                        check(target.setLastModified(originalModified))
                        check(target.readBytes().contentEquals(original))
                        check(!backup.exists() || backup.delete())
                        assertTrue(
                            "The restored ${variant.id} package failed verification",
                            models.installations().single { it.variant == variant }.installed
                        )
                        assertTrue(
                            otherModels.installations().single { it.variant == variant }.installed
                        )
                    }
                }
            }
        } finally {
            models.release()
            otherModels.release()
        }
    }

    private suspend fun verify(variant: AssistantModel): Unit = coroutineScope {
        assumeTrue(AssistantModels.unsupportedReason, AssistantModels.isSupported())
        val directory = File(app.filesDir, "ai_models/assistant/${variant.id}")
        assumeTrue(
            "Install the real ${variant.id} package before running generation tests",
            listOf(
                "genai_config.json",
                "tokenizer.json",
                "model.onnx",
                "model.onnx.data"
            ).all { directory.resolve(it).isFile }
        )
        val models = AssistantModels()
        val report = JSONObject().put("variant", variant.id).put("androidRuntimeVerified", false)
        val probes = JSONArray()
        report.put("probes", probes)
        report.put("initialPssKb", Debug.getPss())
        try {
            val verificationStarted = System.nanoTime()
            assertTrue(
                "The installed package failed integrity verification",
                models.installations().any {
                    it.variant == variant && it.installed
                }
            )
            report.put("packageIntegrityVerified", true)
                .put("packageVerificationSeconds", secondsSince(verificationStarted))
            val requests = AssistantApplicationFixtures.requests
            val retrieval = JSONArray()
            report.put("requestPreparation", retrieval)
            val answers = requests.map { (name, request) ->
                val started = System.nanoTime()
                knowledge.retrieve(request).also {
                    retrieval.put(
                        JSONObject().put("case", name)
                            .put("retrievalSeconds", secondsSince(started))
                    )
                }
            }
            val prompts = requests.mapIndexed { index, (_, request) ->
                val started = System.nanoTime()
                AssistantApplicationFixtures.prompt(
                    knowledge.grounding(request, answers[index], variant),
                    knowledge.userPrompt(request, answers[index])
                ).also {
                    retrieval.getJSONObject(index)
                        .put("promptConstructionSeconds", secondsSince(started))
                }
            }
            val profile = JSONObject()
            report.put("nativeSetupProfile", profile)
                .put(
                    "coldDefinition",
                    "Fresh manager native model; model file pages may already be cached"
                )
            val setupStarted = System.nanoTime()
            OrtEnvironment.getEnvironment().setTelemetry(false)
            GenAI.setTelemetry(false)
            profile.put(
                "runtimeEnvironmentInitSeconds",
                (System.nanoTime() - setupStarted) / 1_000_000_000.0
            )
            val modelStarted = System.nanoTime()
            val counts = Model(directory.absolutePath).use { model ->
                profile.put(
                    "modelInitSeconds",
                    (System.nanoTime() - modelStarted) / 1_000_000_000.0
                )
                val tokenizerStarted = System.nanoTime()
                Tokenizer(model).use { tokenizer ->
                    profile.put(
                        "tokenizerInitSeconds",
                        (System.nanoTime() - tokenizerStarted) / 1_000_000_000.0
                    )
                    profileGeneration(model, tokenizer, prompts.first(), profile)
                    profile.put(
                        "setupAndDecodeSampleSeconds",
                        (System.nanoTime() - setupStarted) / 1_000_000_000.0
                    )
                    prompts.map { prompt ->
                        tokenizer.encode(prompt).use { it.getSequence(0).size }
                    }
                }
            }
            counts.forEach { count ->
                assertTrue(
                    "Actual prompt exceeds ${AssistantModels.MAX_PROMPT_TOKENS} tokens: $count",
                    count <= AssistantModels.MAX_PROMPT_TOKENS
                )
            }
            val minimalPrompt = AssistantApplicationFixtures.prompt("", "Hi")
            val minimal = JSONArray()
            report.put("minimalNoHistoryOrRetrieval", minimal)
            repeat(2) { index ->
                val record = JSONObject().put("run", index)
                minimal.put(record)
                verifyGeneration(
                    models, variant, minimalPrompt, "minimal" to AssistantRequest("Hi"),
                    AssistantAnswer(""), record
                )
            }
            requests.forEachIndexed { index, (name, request) ->
                when (index) {
                    2 -> {
                        verifyCancellation(models, variant, prompts[index], native = true)
                        report.put("nativeCancellationPassed", true)
                    }

                    3 -> {
                        verifyCancellation(models, variant, prompts[index], native = false)
                        report.put("coroutineCancellationPassed", true)
                    }
                }
                val record = JSONObject().put("case", name).put("promptTokens", counts[index])
                probes.put(record)
                verifyGeneration(
                    models,
                    variant,
                    prompts[index],
                    name to request,
                    answers[index],
                    record
                )
                if (index == 0) {
                    val warm = JSONObject().put("case", name).put("promptTokens", counts[index])
                    report.put("warmRepeat", warm)
                    verifyGeneration(
                        models,
                        variant,
                        prompts[index],
                        name to request,
                        answers[index],
                        warm
                    )
                    report.put("sameManagerWarmRepeatPassed", true)
                }
                if (index == 2) report.put("generationAfterNativeCancellationPassed", true)
                if (index == 3) report.put("generationAfterCoroutineCancellationPassed", true)
            }
            val outOfDomain = AssistantRequest("Write a recipe for chocolate cake.")
            val outOfDomainAnswer = knowledge.retrieve(outOfDomain)
            val outOfDomainPrompt = AssistantApplicationFixtures.prompt(
                knowledge.grounding(outOfDomain, outOfDomainAnswer, variant),
                knowledge.userPrompt(outOfDomain, outOfDomainAnswer)
            )
            val outOfDomainRecord = JSONObject().put("case", "out_of_domain")
            report.put("outOfDomain", outOfDomainRecord)
            verifyGeneration(
                models,
                variant,
                outOfDomainPrompt,
                "out_of_domain" to outOfDomain,
                outOfDomainAnswer,
                outOfDomainRecord
            )
            val russianQuestion =
                AssistantRequest("Напиши стихотворение про осень.")
            val russianContext = knowledge.retrieve(russianQuestion)
            val russian = JSONObject().put("case", "russian_general_question")
            report.put("russianGeneralQuestion", russian)
            verifyGeneration(
                models,
                variant,
                AssistantApplicationFixtures.prompt(
                    knowledge.grounding(russianQuestion, russianContext, variant),
                    knowledge.userPrompt(russianQuestion, russianContext)
                ),
                "out_of_domain" to russianQuestion,
                russianContext,
                russian
            )
            assertTrue(
                "The assistant did not redirect an unrelated request in Russian",
                russian.getString("rawOutput").any { it in 'А'..'я' }
            )
            report.put("generationPssKb", Debug.getPss())
            report.put("androidRuntimeVerified", true)
        } catch (failure: Throwable) {
            report.put("error", failure.toString())
            throw failure
        } finally {
            models.release()
            report.put("releasedPssKb", Debug.getPss())
            File(
                app.cacheDir,
                "assistant-android-generation-${variant.id}.json"
            ).writeText(report.toString(2))
        }
    }

    private suspend fun verifyGeneration(
        models: AssistantModels,
        variant: AssistantModel,
        prompt: String,
        fixture: Pair<String, AssistantRequest>,
        retrieved: AssistantAnswer,
        record: JSONObject
    ) {
        val output = StringBuilder()
        record.put("prompt", prompt)
        val started = System.nanoTime()
        var firstTokenNanos = 0L
        try {
            withTimeout(300_000) {
                models.generate(variant, prompt) { token ->
                    output.append(token)
                    val elapsed = System.nanoTime() - started
                    if (firstTokenNanos == 0L) firstTokenNanos = elapsed
                }
            }
            record.put("rawOutput", output.toString())
                .put("firstTokenSeconds", firstTokenNanos / 1_000_000_000.0)
                .put("firstVisibleTextSeconds", firstTokenNanos / 1_000_000_000.0)
                .put("generationSeconds", (System.nanoTime() - started) / 1_000_000_000.0)
            val reply = retrieved.copy(text = output.toString().trim())
            record.put(
                "reply",
                JSONObject(Json.encodeToString(AssistantAnswer.serializer(), reply))
            )
            verifyReply(fixture.first, reply)
            assertTrue("No streamed token was observed", firstTokenNanos > 0L)
            record.put("passed", true)
        } catch (failure: Throwable) {
            record.put("rawOutput", output.toString()).put("error", failure.toString())
            throw failure
        }
    }

    private fun profileGeneration(
        model: Model,
        tokenizer: Tokenizer,
        prompt: String,
        report: JSONObject
    ) {
        val started = System.nanoTime()
        tokenizer.encode(prompt).use { tokens ->
            val inputSize = tokens.getSequence(0).size
            report.put("encodeSeconds", secondsSince(started)).put("promptTokens", inputSize)
            val paramsStarted = System.nanoTime()
            GeneratorParams(model).use { params ->
                params.setSearchOption(
                    "max_length",
                    (inputSize + AssistantModels.MAX_OUTPUT_TOKENS).toDouble()
                )
                params.setSearchOption("do_sample", false)
                report.put("paramsAndSearchSeconds", secondsSince(paramsStarted))
                val generatorStarted = System.nanoTime()
                Generator(model, params).use { generator ->
                    report.put("generatorInitSeconds", secondsSince(generatorStarted))
                    val appendStarted = System.nanoTime()
                    generator.appendTokenSequences(tokens)
                    report.put("appendPromptSeconds", secondsSince(appendStarted))
                    tokenizer.createStream().use { stream ->
                        val firstTokenStarted = System.nanoTime()
                        generator.generateNextToken()
                        report.put("firstDecodeStepSeconds", secondsSince(firstTokenStarted))
                        report.put("firstTokenSeconds", secondsSince(started))
                        val firstToken = generator.getLastTokenInSequence(0)
                        report.put("firstDelta", stream.decode(firstToken))
                        val decodeStarted = System.nanoTime()
                        var count = 0
                        val output = StringBuilder()
                        var emittedTokens = generator.tokenCount()
                        while (!generator.isDone && count < 32) {
                            generator.generateNextToken()
                            val tokenCount = generator.tokenCount()
                            if (tokenCount == emittedTokens) continue
                            emittedTokens = tokenCount
                            output.append(stream.decode(generator.getLastTokenInSequence(0)))
                            count++
                        }
                        val decodeSeconds = secondsSince(decodeStarted)
                        report.put("decodeSampleTokens", count)
                            .put("decodeSampleSeconds", decodeSeconds)
                            .put("decodeTokensPerSecond", count / decodeSeconds)
                            .put("decodeSample", output.toString())
                            .put("generationPssKb", Debug.getPss())
                    }
                }
            }
        }
    }

    private fun secondsSince(started: Long): Double =
        (System.nanoTime() - started) / 1_000_000_000.0

    private suspend fun verifyCancellation(
        models: AssistantModels,
        variant: AssistantModel,
        prompt: String,
        native: Boolean
    ) = coroutineScope {
        val started = CompletableDeferred<Unit>()
        var observedCancellation = false
        val generation = launch {
            try {
                models.generate(variant, prompt) { started.complete(Unit) }
            } catch (cancelled: CancellationException) {
                observedCancellation = true
            }
        }
        withTimeout(300_000) { started.await() }
        if (native) models.cancel() else generation.cancel()
        withTimeout(30_000) { generation.join() }
        assertTrue(
            "${if (native) "Native" else "Coroutine"} cancellation was not observed",
            observedCancellation
        )
        models.release()
    }

    private fun verifyReply(name: String, reply: AssistantAnswer) {
        if (name == "out_of_domain" || name == "minimal") {
            assertTrue("The model returned no answer", reply.text.isNotBlank())
            val formatInstructions = Regex(
                """\b[12]\s*[-–—]\s*4\s+(?:(?:helpful|complete)\s+)?sentences\b|""" +
                        """Reply\s+with\s+one\s+JSON\s+object|\btext\s*:\s*string\b""",
                RegexOption.IGNORE_CASE
            )
            assertFalse(
                "The model repeated response-format instructions",
                formatInstructions.containsMatchIn(reply.text)
            )
            assertTrue(
                "An unrelated request cited ImageToolbox documentation",
                reply.references.isEmpty()
            )
            assertTrue("An unrelated request produced tool links", reply.tools.isEmpty())
            assertTrue("An unrelated request produced settings links", reply.settings.isEmpty())
            if (name == "out_of_domain") {
                assertTrue(
                    "The assistant did not stay within ImageToolbox: ${reply.text}",
                    reply.text.contains("ImageToolbox", ignoreCase = true)
                )
            }
            return
        }
        assertTrue("The model returned no answer", reply.text.isNotBlank())
        assertTrue(
            "The reply has no retrieved application context",
            reply.references.isNotEmpty() || reply.tools.isNotEmpty() || reply.settings.isNotEmpty()
        )
        assertFalse(
            "The model recommended external editing tools instead of ImageToolbox: ${reply.text}",
            Regex(
                "photoshop|фотошоп|adobe|онлайн.?сервис|online (?:tool|service)",
                RegexOption.IGNORE_CASE
            )
                .containsMatchIn(reply.text)
        )
        assertFalse(
            "The answer incorrectly described PNG as a lossy format: ${reply.text}",
            describesPngAsLossy(reply.text)
        )
        if (name.startsWith("ru_")) assertTrue(reply.text.any { it in 'А'..'я' })
        if ("compress" in name) {
            assertFalse(
                "The answer invented compression level presets: ${reply.text}",
                inventsCompressionPresets(reply.text)
            )
        }
        if (name.endsWith("compress_question")) {
            assertTrue(
                "Compression tool link is missing",
                reply.tools.any { it.key == Screen.WeightResize().simpleName })
            val title = services.resources.getStringLocalized(
                R.string.by_bytes_resize,
                if (name.startsWith("ru_")) "ru" else "en"
            )
            val mentionsTool = if (name.startsWith("ru_")) {
                Regex("сжати[ея] по весу", RegexOption.IGNORE_CASE).containsMatchIn(reply.text)
            } else {
                reply.text.contains(title, ignoreCase = true)
            }
            assertTrue(
                "The answer did not identify the available tool: ${reply.text}",
                mentionsTool
            )
        }
        assertEquals(reply.references.map { it.tipId }.distinct().size, reply.references.size)
    }

    private fun describesPngAsLossy(text: String): Boolean = text.replace(Regex("[*_`]"), "")
        .lowercase().split(Regex("[.!?;\\n]")).any { clause ->
            val directClaim = Regex("png.{0,60}?(?:lossy|с потерями)").findAll(clause).any {
                !Regex("jpe?g|webp").containsMatchIn(it.value)
            }
            val formatList = Regex("(?:lossy|с потерями)\\s+(?:formats?|формат[а-яё]*).{0,40}?png")
                .containsMatchIn(clause)
            (directClaim || formatList) &&
                    "lossless" !in clause && "без потерь" !in clause &&
                    !Regex("(?:not|isn't)\\s+(?:a\\s+)?lossy").containsMatchIn(clause)
        }

    private fun inventsCompressionPresets(text: String): Boolean {
        val normalized = text.replace(Regex("[*_`]"), "").lowercase()
        return Regex("compression (?:level|preset)|quality preset|уров[её]н[а-яё]* сжатия")
            .containsMatchIn(normalized) &&
                Regex("\\b(?:low|medium|high)\\b|низк[а-я]*|средн[а-я]*|высок[а-я]*")
                    .containsMatchIn(normalized)
    }
}
