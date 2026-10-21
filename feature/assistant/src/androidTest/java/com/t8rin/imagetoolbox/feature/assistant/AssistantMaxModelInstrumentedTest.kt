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
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.feature.assistant.di.AssistantEntryPoint
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantRequest
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantScreenContext
import com.t8rin.neural_tools.assistant.AssistantModels
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class AssistantMaxModelInstrumentedTest {
    private val app = InstrumentationRegistry.getInstrumentation().targetContext
    private val variant = AssistantModel.Max

    @Test
    fun downloadsPublishedMaxPackage() = runBlocking {
        assumeTrue(
            InstrumentationRegistry.getArguments().getString("downloadAssistantMaxModel") == "true"
        )
        val models = AssistantModels()
        val started = System.nanoTime()
        var progress = 0f
        try {
            assumeTrue(
                "Remove Max before testing a fresh download",
                !models.installations().single { it.variant == variant }.installed
            )
            val directory = File(app.filesDir, "ai_models/assistant")
            val staging = directory.resolve(".${variant.id}.download")
            check(staging.mkdirs() || staging.isDirectory)
            val orphanNames = listOf(
                "model.onnx.data.interrupted.download",
                ".download-model.onnx.data.interrupted.tmp"
            )
            orphanNames.forEach { staging.resolve(it).writeText("interrupted") }
            models.download(variant) {
                progress = it.currentPercent
                assertTrue(orphanNames.none { name -> staging.resolve(name).exists() })
            }
            val state = models.installations().single { it.variant == variant }
            assertTrue(state.installed)
            assertTrue(progress >= 1f)
            assertTrue(!staging.exists())
            assertTrue(orphanNames.none { directory.resolve(variant.id).resolve(it).exists() })
            File(app.cacheDir, "assistant-max-download.json").writeText(
                JSONObject()
                    .put("bytes", state.installedSize)
                    .put("seconds", (System.nanoTime() - started) / 1e9)
                    .toString(2)
            )
        } finally {
            models.release()
        }
    }

    @Test
    fun maxStreamsEnglishAndRussianThenRecoversAfterCancellation(): Unit = runBlocking {
        val models = AssistantModels()
        val report = JSONObject().put("variant", variant.id)
        val records = JSONArray()
        report.put("requests", records)
        val verificationStart = System.nanoTime()
        assumeTrue(models.installations().single { it.variant == variant }.installed)
        report.put("packageVerificationSeconds", (System.nanoTime() - verificationStart) / 1e9)
        try {
            val request = AssistantRequest(
                query = "Как сжать выбранное фото до 500 КБ?",
                context = AssistantScreenContext(
                    selectedUris = listOf("content://assistant-test/photo"),
                    parameters = mapOf(
                        "selectedImageInfo" to "image/jpeg, 4032 × 3024, 5000000 bytes"
                    )
                )
            )
            val knowledge = app.entryPoint<AssistantEntryPoint>().knowledge
            val retrievalStart = System.nanoTime()
            val answer = knowledge.retrieve(request)
            report.put("retrievalSeconds", (System.nanoTime() - retrievalStart) / 1e9)
            assertTrue(answer.tools.any { it.key == Screen.WeightResize().simpleName })
            val groundedPrompt = AssistantApplicationFixtures.prompt(
                knowledge.grounding(request, answer, variant), knowledge.userPrompt(request, answer)
            )
            val russianQuestion = "Объясни одним предложением, зачем сжимают фотографии."
            val recoveryQuestion = "Say hello in one short sentence."
            val unrelated = AssistantRequest("Напиши стихотворение про осень.")
            val unrelatedContext = knowledge.retrieve(unrelated)
            val clarificationPrompt = AssistantApplicationFixtures.prompt(
                knowledge.grounding(unrelated, unrelatedContext, variant),
                knowledge.userPrompt(unrelated, unrelatedContext)
            )
            val profile = JSONObject()
            report.put("nativeProfile", profile).put(
                "coldDefinition",
                "Fresh AssistantModels session after native profile; model file pages may be cached"
            )
            val counts = profileNative(
                profile,
                linkedMapOf(
                    "hi" to prompt("Hi"),
                    "russian" to prompt(russianQuestion),
                    "grounded_selected_photo" to groundedPrompt,
                    "out_of_domain" to clarificationPrompt,
                    "after_cancel" to prompt(recoveryQuestion)
                )
            )
            generate(models, "hi_cold", "Hi", records, promptTokens = counts.getValue("hi"))
            generate(models, "hi_warm", "Hi", records, promptTokens = counts.getValue("hi"))
            val russian = generate(
                models, "russian", russianQuestion, records,
                promptTokens = counts.getValue("russian")
            )
            assertTrue(russian.any { it.lowercaseChar() in 'а'..'я' })
            val groundedReply = generate(
                models = models,
                name = "grounded_selected_photo",
                question = request.query,
                records = records,
                input = groundedPrompt,
                promptTokens = counts.getValue("grounded_selected_photo")
            )
            assertTrue("Missing target file size: $groundedReply", groundedReply.contains("500"))
            assertTrue("Missing retrieved tool: $groundedReply", answer.tools.any {
                groundedReply.contains(it.title, ignoreCase = true)
            })
            assertTrue(groundedReply.any { it.lowercaseChar() in 'а'..'я' })
            val clarification = generate(
                models, "out_of_domain", unrelated.query, records,
                input = clarificationPrompt,
                promptTokens = counts.getValue("out_of_domain")
            )
            assertTrue(
                clarification, clarification.contains("ImageToolbox") &&
                    clarification.trim().endsWith('?') && clarification.lines()
                .count { it.isNotBlank() } <= 2)
            println("AssistantMax case=cancel start")
            coroutineScope {
                val firstToken = CompletableDeferred<Unit>()
                val job = launch {
                    models.generate(
                        variant,
                        prompt("Write a detailed numbered list of fifty different photography tips.")
                    ) { firstToken.complete(Unit) }
                }
                withTimeout(600_000) { firstToken.await() }
                val cancelStarted = System.nanoTime()
                models.cancel()
                withTimeout(10_000) { job.join() }
                report.put("cancelSeconds", (System.nanoTime() - cancelStarted) / 1e9)
                assertTrue(job.isCancelled)
            }
            println("AssistantMax case=cancel complete")
            generate(
                models, "after_cancel", recoveryQuestion, records,
                promptTokens = counts.getValue("after_cancel")
            )
        } finally {
            models.release()
            report.put("pssAfterReleaseKb", Debug.getPss())
            File(app.cacheDir, "assistant-max-generation.json").writeText(report.toString(2))
        }
    }

    private suspend fun generate(
        models: AssistantModels,
        name: String,
        question: String,
        records: JSONArray,
        input: String = prompt(question),
        promptTokens: Int
    ): String {
        val record = JSONObject().put("name", name)
            .put("promptCharacters", input.length).put("promptTokens", promptTokens)
            .put("completed", false)
        records.put(record)
        val text = StringBuilder()
        var firstTokenNanos: Long? = null
        val start = System.nanoTime()
        println("AssistantMax case=$name start promptTokens=$promptTokens")
        try {
            withTimeout(600_000) {
                models.generate(variant, input) { token ->
                    if (firstTokenNanos == null) {
                        firstTokenNanos = System.nanoTime()
                        println("AssistantMax case=$name firstTokenSeconds=${secondsSince(start)}")
                    }
                    text.append(token)
                }
            }
            assertTrue(text.isNotBlank())
            assertTrue(!text.contains("<think>"))
            record.put("completed", true)
            return text.toString()
        } catch (failure: Throwable) {
            record.put("error", failure.toString())
            throw failure
        } finally {
            record.put(
                "ttftSeconds",
                firstTokenNanos?.let { (it - start) / 1e9 } ?: JSONObject.NULL)
                .put("totalSeconds", secondsSince(start))
                .put("pssKb", Debug.getPss())
                .put("reply", text.toString())
            println("AssistantMax case=$name finished $record")
        }
    }

    private suspend fun profileNative(
        report: JSONObject,
        prompts: Map<String, String>
    ): Map<String, Int> = withTimeout(600_000) {
        withContext(Dispatchers.Default) {
            val counts = linkedMapOf<String, Int>()
            val started = System.nanoTime()
            report.put("pssBeforeKb", Debug.getPss())
            println("AssistantMax native=model start")
            try {
                OrtEnvironment.getEnvironment().setTelemetry(false)
                GenAI.setTelemetry(false)
                report.put("environmentSeconds", secondsSince(started))
                val modelStarted = System.nanoTime()
                Model(
                    File(
                        app.filesDir,
                        "ai_models/assistant/${variant.id}"
                    ).absolutePath
                ).use { model ->
                    report.put("modelInitSeconds", secondsSince(modelStarted))
                    println("AssistantMax native=model ready seconds=${secondsSince(modelStarted)}")
                    currentCoroutineContext().ensureActive()
                    val tokenizerStarted = System.nanoTime()
                    Tokenizer(model).use { tokenizer ->
                        report.put("tokenizerInitSeconds", secondsSince(tokenizerStarted))
                        val tokenCounts = JSONObject()
                        report.put("promptTokens", tokenCounts)
                        prompts.forEach { (name, input) ->
                            tokenizer.encode(input).use { tokens ->
                                counts[name] = tokens.getSequence(0).size
                                tokenCounts.put(name, counts.getValue(name))
                            }
                        }
                        println("AssistantMax native=tokenizer ready promptTokens=$tokenCounts")
                        val generationStarted = System.nanoTime()
                        tokenizer.encode(prompts.getValue("hi")).use { tokens ->
                            report.put("encodeSeconds", secondsSince(generationStarted))
                            val paramsStarted = System.nanoTime()
                            GeneratorParams(model).use { params ->
                                params.setSearchOption(
                                    "max_length",
                                    (counts.getValue("hi") + 16).toDouble()
                                )
                                params.setSearchOption("do_sample", false)
                                report.put("paramsSeconds", secondsSince(paramsStarted))
                                val generatorStarted = System.nanoTime()
                                Generator(model, params).use { generator ->
                                    report.put(
                                        "generatorInitSeconds",
                                        secondsSince(generatorStarted)
                                    )
                                    println("AssistantMax native=prefill start")
                                    val prefillStarted = System.nanoTime()
                                    generator.appendTokenSequences(tokens)
                                    report.put("prefillSeconds", secondsSince(prefillStarted))
                                    tokenizer.createStream().use { stream ->
                                        val eos = tokenizer.eosTokenIds.toSet()
                                        val output = StringBuilder()
                                        var emittedTokens = generator.tokenCount()
                                        var outputTokens = 0
                                        var firstTokenNanos = 0L
                                        var lastTokenNanos = 0L
                                        while (!generator.isDone && outputTokens < 16) {
                                            currentCoroutineContext().ensureActive()
                                            generator.generateNextToken()
                                            val count = generator.tokenCount()
                                            if (count == emittedTokens) continue
                                            emittedTokens = count
                                            val token = generator.getLastTokenInSequence(0)
                                            if (token in eos) continue
                                            val now = System.nanoTime()
                                            if (firstTokenNanos == 0L) {
                                                firstTokenNanos = now
                                                report.put(
                                                    "firstTokenSeconds",
                                                    (now - generationStarted) / 1e9
                                                )
                                            }
                                            lastTokenNanos = now
                                            outputTokens++
                                            output.append(stream.decode(token))
                                        }
                                        val decodeSeconds = (lastTokenNanos - firstTokenNanos) / 1e9
                                        report.put("outputTokens", outputTokens)
                                            .put("decodeSeconds", decodeSeconds)
                                            .put(
                                                "decodeTokensPerSecond",
                                                if (decodeSeconds > 0) (outputTokens - 1) / decodeSeconds else 0.0
                                            )
                                            .put("reply", output.toString())
                                            .put("pssDuringKb", Debug.getPss())
                                    }
                                }
                            }
                        }
                    }
                }
                counts
            } catch (failure: Throwable) {
                report.put("error", failure.toString())
                throw failure
            } finally {
                report.put("totalSeconds", secondsSince(started))
                    .put("pssAfterReleaseKb", Debug.getPss())
                println("AssistantMax native=finished $report")
            }
        }
    }

    private fun secondsSince(started: Long): Double = (System.nanoTime() - started) / 1e9

    private fun prompt(question: String): String =
        "<|im_start|>system\nReply briefly in the language of the question.<|im_end|>\n" +
                "<|im_start|>user\n$question<|im_end|>\n" +
                "<|im_start|>assistant\n<think>\n\n</think>\n\n"
}
