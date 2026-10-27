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
import com.t8rin.imagetoolbox.feature.assistant.domain.*
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
class AssistantToolsInstrumentedTest {
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
        val catalog = AssistantGalleryCatalog(listOf("Marie", "Alex"), listOf("Trips"))
        suspend fun call(
            question: String,
            current: AssistantGalleryQuery? = null
        ): AssistantTools.Result {
            val prompt = AssistantTools.prompt(
                "Answer briefly in the user's language.",
                AssistantRequest(question),
                emptyList(),
                catalog,
                current
            )
            val started = System.nanoTime()
            val output = withTimeout(125_000) {
                runtime.generate(
                    variant,
                    prompt,
                    maxOutputTokens = 224,
                    repetitionPenalty = 1.1
                ) {}
            }
            report.put(
                JSONObject().put("question", question).put("output", output)
                    .put("totalMs", (System.nanoTime() - started) / 1_000_000)
            )
            File(instrumentation.targetContext.cacheDir, "assistant-gallery-tools.json").writeText(
                report.toString(2)
            )
            return AssistantTools.parse(output)
        }
        try {
            val scene = "Busca fotos de gatos en mi galería."
            val sceneCall = call(scene)
            assertTrue(report.toString(), sceneCall is AssistantTools.Result.Photos)
            assertEquals(
                scene,
                (sceneCall as AssistantTools.Result.Photos).plan.gallery(null, scene).semantic
            )
            val compound = "Montre les photos de Marie prises en juillet 2026 dans l’album Trips."
            val compoundCall = call(compound)
            assertTrue(report.toString(), compoundCall is AssistantTools.Result.Photos)
            val query = (compoundCall as AssistantTools.Result.Photos).plan.gallery(null, compound)
            assertEquals(report.toString(), listOf("Marie"), query.people)
            assertEquals(report.toString(), "2026-07-01", query.from)
            assertEquals(report.toString(), "2026-07-31", query.to)
            assertEquals(report.toString(), "Trips", query.album)
            val followUp = call("Und ohne Alex.", query)
            assertTrue(report.toString(), followUp is AssistantTools.Result.Photos)
            val refined =
                (followUp as AssistantTools.Result.Photos).plan.gallery(query, "Und ohne Alex.")
            assertEquals(query.copy(excludedPeople = listOf("Alex")), refined)
        } finally {
            runtime.release()
        }
    }
}