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
import com.t8rin.imagetoolbox.feature.assistant.di.AssistantEntryPoint
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantCommand
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantGalleryCatalog
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantGalleryQuery
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantRequest
import com.t8rin.imagetoolbox.feature.assistant.domain.assistantCommandPrompt
import com.t8rin.imagetoolbox.feature.assistant.domain.assistantPrompt
import com.t8rin.neural_tools.assistant.AssistantModels
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class AssistantRoutingInstrumentedTest {
    @Test
    fun recordsSystemLanguages() = runBlocking {
        val app = InstrumentationRegistry.getInstrumentation().targetContext
        val name =
            InstrumentationRegistry.getArguments().getString("manualCases") ?: return@runBlocking
        val cases = JSONArray(File(app.cacheDir, name).readText())
        val knowledge = app.entryPoint<AssistantEntryPoint>().knowledge
        val report = JSONArray()
        for (index in 0 until cases.length()) {
            val query = cases.getJSONObject(index).getString("question")
            val language = knowledge.detectLanguage(AssistantRequest(query))
            val record = JSONObject().put("question", query).put("language", language)
            report.put(record)
        }
        File(app.cacheDir, "assistant-system-languages.json").writeText(report.toString(2))
    }

    @Test
    fun nativeGenerationKeepsSystemInstructionsWhenReusingContext() = runBlocking {
        val runtime = AssistantModels()
        val model = runtime.installations(false)
            .firstOrNull { it.installed && it.variant.id == "advanced" }?.variant
        assumeTrue(model != null)
        try {
            listOf("PINEAPPLE", "MANGO").forEach { expected ->
                val prompt = assistantPrompt(
                    "For every question, reply with the single word $expected.",
                    emptyList(),
                    "What is two plus two?",
                    false
                )
                val output =
                    runtime.generate(requireNotNull(model), prompt, maxOutputTokens = 32) {}
                assertEquals(output, expected, output.trim().trimEnd('.'))
            }
            listOf("APPLE", "APPLE", "ORANGE", "PEAR", "LEMON").forEach { expected ->
                val prompt = assistantPrompt(
                    "Repeat only the word given by the user.", emptyList(), expected, false
                )
                val output = runtime.generate(
                    requireNotNull(model),
                    prompt,
                    maxOutputTokens = 16
                ) {}
                assertEquals(output, expected, output.trim().trimEnd('.'))
            }
        } finally {
            runtime.release()
        }
    }

    @Test
    fun nativeGuidanceResetsWhenReusingTheGenerator() = runBlocking {
        val runtime = AssistantModels()
        assumeTrue(
            runtime.installations(false).any { it.installed && it.variant == AssistantModel.Lite })
        val grammar =
            """start: %json {"type":"object","properties":{"value":{"enum":["APPLE","PEAR"]}},"required":["value"],"additionalProperties":false}"""
        try {
            listOf("APPLE", "PEAR", "APPLE", "PEAR").forEach { value ->
                val output = runtime.generate(
                    AssistantModel.Lite,
                    assistantPrompt(
                        "Return JSON with value equal to the word in the question.",
                        emptyList(),
                        value,
                        false
                    ),
                    maxOutputTokens = 32,
                    responseGrammar = grammar
                ) {}
                assertEquals(output, value, JSONObject(output).getString("value"))
            }
        } finally {
            runtime.release()
        }
    }

    @Test
    fun interpretationGrammarCompilesInTheInstalledRuntime() = runBlocking {
        val runtime = AssistantModels()
        assumeTrue(
            runtime.installations(false).any { it.installed && it.variant == AssistantModel.Lite })
        try {
            listOf("supported_formats", "transparency_formats").forEach { action ->
                val output = runtime.generate(
                    AssistantModel.Lite,
                    assistantPrompt(
                        "Return JSON with action exactly equal to the user's identifier.",
                        emptyList(),
                        action,
                        false
                    ),
                    maxOutputTokens = 32,
                    responseGrammar = com.t8rin.imagetoolbox.feature.assistant.domain.AssistantInterpretation.grammar
                ) {}
                val expected =
                    com.t8rin.imagetoolbox.feature.assistant.domain.AssistantInterpretation.Formats(
                        action == "transparency_formats"
                    )
                assertEquals(
                    output,
                    expected,
                    com.t8rin.imagetoolbox.feature.assistant.domain.AssistantInterpretation.parse(
                        output
                    )
                )
            }
        } finally {
            runtime.release()
        }
    }

    @Test
    fun nativeCallsFindPhotosAndKeepCompoundFiltersAcrossLanguages() = runBlocking {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val selected = InstrumentationRegistry.getArguments().getString("helpModels") ?: "advanced"
        val runtime = AssistantModels()
        val model = runtime.installations(false)
            .firstOrNull { it.installed && it.variant.id == selected }?.variant
        assumeTrue(model != null)
        val variant = requireNotNull(model)
        val report = JSONArray()
        suspend fun call(
            question: String,
            current: AssistantGalleryQuery? = null
        ): AssistantCommand.Action {
            val catalog = AssistantGalleryCatalog(
                listOf("Marie", "Alex"),
                listOf("Trips"),
                listOf("IMG_20260712.jpg").filter { question.contains(it) })
            val started = System.nanoTime()
            val request = AssistantRequest(question)
            val knowledge =
                instrumentation.targetContext.entryPoint<AssistantEntryPoint>().knowledge
            val english = if (knowledge.detectLanguage(request)
                    ?.substringBefore('-') == "en"
            ) question else runtime.generate(
                variant,
                com.t8rin.imagetoolbox.feature.assistant.domain.assistantTranslationPrompt(
                    request,
                    variant
                ),
                maxOutputTokens = 160
            ) {}
            val resolved =
                request.copy(query = english, language = knowledge.detectLanguage(request))
            val interpretation =
                com.t8rin.imagetoolbox.feature.assistant.domain.AssistantInterpretation.parse(
                    runtime.generate(
                        variant,
                        com.t8rin.imagetoolbox.feature.assistant.domain.assistantInterpretationPrompt(
                            resolved,
                            current,
                            model = variant,
                            originalQuestion = question
                        ),
                        maxOutputTokens = 96,
                        responseGrammar = com.t8rin.imagetoolbox.feature.assistant.domain.AssistantInterpretation.grammar
                    ) {})
            check(interpretation == com.t8rin.imagetoolbox.feature.assistant.domain.AssistantInterpretation.Search || interpretation == com.t8rin.imagetoolbox.feature.assistant.domain.AssistantInterpretation.More) { "Photo request was routed to an answer: $question" }
            val thinking =
                InstrumentationRegistry.getArguments().getString("planThinking") == "true"
            val routePrompt = assistantCommandPrompt(
                resolved,
                current,
                catalog,
                model = variant,
                thinking = thinking,
                originalQuestion = question
            )
            val routed =
                if (interpretation == com.t8rin.imagetoolbox.feature.assistant.domain.AssistantInterpretation.More) """{"action":"more"}""" else withTimeout(
                    125_000
                ) {
                runtime.generate(
                    variant,
                    routePrompt,
                    maxOutputTokens = if (thinking) 768 else 256,
                    thinking = thinking,
                    sample = thinking,
                    repetitionPenalty = 1.0,
                    stopOnRepetition = false,
                    responseGrammar = AssistantCommand.grammar(
                        current,
                        catalog = catalog,
                        question = question,
                        language = resolved.language
                    ).takeUnless {
                        InstrumentationRegistry.getArguments().getString("unconstrained") == "true"
                    }
                ) {}
            }
            report.put(
                JSONObject().put("question", question).put("interpretation", english)
                    .put("route", routed)
            )
            File(instrumentation.targetContext.cacheDir, "assistant-gallery-tools.json").writeText(
                report.toString(2)
            )
            val route = AssistantCommand.parse(routed)
            report.getJSONObject(report.length() - 1)
                .put("totalMs", (System.nanoTime() - started) / 1_000_000)
            if (route is AssistantCommand.Action.SearchPhotos) report.getJSONObject(report.length() - 1)
                .put("scene", route.query.semantic)
            File(instrumentation.targetContext.cacheDir, "assistant-gallery-tools.json").writeText(
                report.toString(2)
            )
            return route
        }

        val failures = mutableListOf<String>()
        val selectedCases = InstrumentationRegistry.getArguments().getString("photoCases")
            ?.split(',')?.map(String::toInt)?.toSet()
        var caseIndex = 0
        suspend fun verify(
            question: String,
            current: AssistantGalleryQuery? = null,
            check: (AssistantCommand.Action) -> Unit
        ) {
            val index = caseIndex++
            if (selectedCases != null && index !in selectedCases) return
            try {
                check(call(question, current))
            } catch (failure: Exception) {
                failures.add("$question: $failure")
            } catch (failure: AssertionError) {
                failures.add("$question: $failure")
            }
        }

        fun AssistantCommand.Action.query() = (this as AssistantCommand.Action.SearchPhotos).query
        fun AssistantCommand.Action.mode() = (this as AssistantCommand.Action.SearchPhotos).mode
        val compound = AssistantGalleryQuery(
            people = listOf("Marie"),
            from = "2026-07-01",
            to = "2026-07-31",
            album = "Trips"
        )
        val excluded = compound.copy(excludedPeople = listOf("Alex"))
        val visual = excluded.copy(semantic = "red umbrella")
        val filename = visual.copy(filename = "IMG_20260712.jpg")
        try {
            verify("Busca fotos de gatos en mi galería.") {
                val query = it.query()
                assertEquals(
                    com.t8rin.imagetoolbox.feature.assistant.domain.AssistantGallerySearch.Mode.New,
                    it.mode()
                )
                assertTrue(query.semantic.orEmpty().contains("cat", true))
                assertEquals(AssistantGalleryQuery(semantic = query.semantic), query)
            }
            verify("Montre les photos de Marie prises en juillet 2026 dans l’album Trips.") {
                assertEquals(compound, it.query())
            }
            verify("Und ohne Alex.", compound) {
                assertEquals(
                    com.t8rin.imagetoolbox.feature.assistant.domain.AssistantGallerySearch.Mode.Update,
                    it.mode()
                )
                assertEquals(excluded, it.query())
            }
            verify(
                "Montre les photos de Marie avec un parapluie rouge dans l’album Trips.",
                excluded
            ) {
                val query = it.query()
                assertEquals(excluded.copy(semantic = query.semantic), query)
                assertTrue(query.semantic.orEmpty().contains("umbrella", true))
                assertTrue(query.semantic.orEmpty().contains("red", true))
                assertTrue(!query.semantic.orEmpty().contains("Marie", true))
                assertTrue(!query.semantic.orEmpty().contains("Trips", true))
            }
            verify("Keep the conditions, but only files named IMG_20260712.jpg.", visual) {
                assertEquals(
                    com.t8rin.imagetoolbox.feature.assistant.domain.AssistantGallerySearch.Mode.Update,
                    it.mode()
                )
                assertEquals(filename, it.query())
            }
            verify("Remove the date restriction.", filename) {
                assertEquals(
                    com.t8rin.imagetoolbox.feature.assistant.domain.AssistantGallerySearch.Mode.Update,
                    it.mode()
                )
                assertEquals(filename.copy(from = null, to = null), it.query())
            }
            verify("Start a new independent search: cats.", filename) {
                val query = it.query()
                assertEquals(
                    com.t8rin.imagetoolbox.feature.assistant.domain.AssistantGallerySearch.Mode.New,
                    it.mode()
                )
                assertTrue(query.semantic.orEmpty().contains("cat", true))
                assertEquals(AssistantGalleryQuery(semantic = query.semantic), query)
            }
            verify("Only search within these results for bicycles.", compound) {
                assertEquals(
                    com.t8rin.imagetoolbox.feature.assistant.domain.AssistantGallerySearch.Mode.Narrow,
                    it.mode()
                )
                assertTrue(it.query().semantic.orEmpty().contains("bicycl", true))
                assertEquals(compound.copy(semantic = it.query().semantic), it.query())
            }
            verify("Remove Alex's person condition, keeping the other conditions.", excluded) {
                assertEquals(compound, it.query())
            }
            verify("Show photos taken yesterday.") {
                val yesterday = java.time.LocalDate.now().minusDays(1).toString()
                assertEquals(AssistantGalleryQuery(from = yesterday, to = yesterday), it.query())
            }
            verify("Show more photos with the same conditions.", compound) {
                assertEquals(AssistantCommand.Action.MorePhotos, it)
            }
            assertTrue(failures.joinToString("\n"), failures.isEmpty())
        } finally {
            runtime.release()
        }
    }
}
