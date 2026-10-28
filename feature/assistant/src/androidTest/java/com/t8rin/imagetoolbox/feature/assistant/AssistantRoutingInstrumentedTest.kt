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
import com.t8rin.imagetoolbox.feature.assistant.di.AssistantEntryPoint
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantCommand
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantGalleryCatalog
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantGalleryQuery
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantPhotoPlan
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantRequest
import com.t8rin.imagetoolbox.feature.assistant.domain.assistantInterpretationPrompt
import com.t8rin.imagetoolbox.feature.assistant.domain.assistantPrompt
import com.t8rin.neural_tools.assistant.AssistantModels
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
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
            listOf("APPLE", "ORANGE", "PEAR", "LEMON").forEachIndexed { index, expected ->
                val prompt = assistantPrompt(
                    "Repeat only the word given by the user.", emptyList(), expected, false
                )
                val kind =
                    if (index % 2 == 0) AssistantModels.GenerationKind.Command else AssistantModels.GenerationKind.Reply
                val output = runtime.generate(
                    requireNotNull(model),
                    prompt,
                    maxOutputTokens = 16,
                    kind = kind
                ) {}
                assertEquals(output, expected, output.trim().trimEnd('.'))
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
        ): AssistantPhotoPlan {
            val catalog = AssistantGalleryCatalog(listOf("Marie", "Alex"), listOf("Trips"))
            val language =
                instrumentation.targetContext.entryPoint<AssistantEntryPoint>().knowledge.detectLanguage(
                    AssistantRequest(question)
                )
            val scope = if (AssistantCommand.hasNamedRequest(
                    catalog,
                    question
                )
            ) AssistantCommand.Scope.Photos else AssistantCommand.Scope.Auto
            val reasoning =
                selected != "lite" && (scope != AssistantCommand.Scope.Photos || selected == "max")
            val interpretation = runtime.generate(
                variant,
                assistantInterpretationPrompt(question, emptyList(), simple = selected == "lite"),
                maxOutputTokens = 128,
                repetitionPenalty = 1.0,
                kind = AssistantModels.GenerationKind.SearchQuery
            ) {}.trim()
            val instructions = AssistantCommand.instructions(
                current,
                catalog,
                question,
                language,
                scope = scope
            ) + "\nRequest meaning in English: $interpretation"
            val routePrompt =
                assistantPrompt(instructions, emptyList(), question, false, thinking = reasoning)
            val started = System.nanoTime()
            val routed = withTimeout(125_000) {
                runtime.generate(
                    variant,
                    routePrompt,
                    maxOutputTokens = if (reasoning) 512 else 256,
                    repetitionPenalty = 1.0,
                    stopOnRepetition = false,
                    kind = AssistantModels.GenerationKind.Command,
                    responseGrammar = AssistantCommand.grammar(
                        current,
                        catalog = catalog,
                        question = question,
                        reasoning = reasoning,
                        scope = scope
                    )
                ) {}
            }
            report.put(
                JSONObject().put("question", question).put("interpretation", interpretation)
                    .put("route", routed)
            )
            File(instrumentation.targetContext.cacheDir, "assistant-gallery-tools.json").writeText(
                report.toString(2)
            )
            val route = AssistantCommand.parse(routed)
            report.getJSONObject(report.length() - 1)
                .put("totalMs", (System.nanoTime() - started) / 1_000_000)
            check(route is AssistantCommand.Action.SearchPhotos) { report.toString() }
            report.getJSONObject(report.length() - 1).put("scene", route.plan.scene)
            File(instrumentation.targetContext.cacheDir, "assistant-gallery-tools.json").writeText(
                report.toString(2)
            )
            return route.plan
        }
        try {
            val scene = "Busca fotos de gatos en mi galería."
            val sceneCall = call(scene)
            assertEquals(
                scene,
                sceneCall.gallery(null, scene).semantic
            )
            assertEquals(emptyMap<String, AssistantPhotoPlan.PersonRole>(), sceneCall.people)
            assertEquals(null, sceneCall.album)
            assertEquals(null, sceneCall.from)
            assertEquals(null, sceneCall.to)
            val compound = "Montre les photos de Marie prises en juillet 2026 dans l’album Trips."
            val compoundCall = call(compound)
            val query = compoundCall.gallery(null, compound)
            assertEquals(report.toString(), listOf("Marie"), query.people)
            assertEquals(report.toString(), "2026-07-01", query.from)
            assertEquals(report.toString(), "2026-07-31", query.to)
            assertEquals(report.toString(), "Trips", query.album)
            assertEquals(null, query.semantic)
            val followUp = call("Und ohne Alex.", query)
            val refined =
                followUp.gallery(query, "Und ohne Alex.")
            assertEquals(query.copy(excludedPeople = listOf("Alex")), refined)
            val visual = "Montre les photos de Marie avec un parapluie rouge dans l’album Trips."
            val visualPlan = call(visual, refined)
            assertEquals(refined.copy(semantic = visual), visualPlan.gallery(refined, visual))
        } finally {
            runtime.release()
        }
    }
}
