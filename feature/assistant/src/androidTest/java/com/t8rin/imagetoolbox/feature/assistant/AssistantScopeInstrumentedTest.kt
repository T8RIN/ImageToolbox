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
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantRequest
import com.t8rin.neural_tools.assistant.AssistantModels
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class AssistantScopeInstrumentedTest {
    @Test
    fun modelsAskAboutTheAppInsteadOfWritingRecipesOrPoems() = runBlocking {
        val app = InstrumentationRegistry.getInstrumentation().targetContext
        val knowledge = app.entryPoint<AssistantEntryPoint>().knowledge
        val models = AssistantModels()
        val variants = AssistantModel.entries.filterNot { it.supportsVision }
        assumeTrue(models.installations().filter { it.variant in variants }.all { it.installed })
        val report = JSONArray()
        val failures = mutableListOf<String>()
        try {
            variants.forEach { variant ->
                listOf(
                    AssistantRequest("Write a recipe for chocolate cake.", language = "en"),
                    AssistantRequest("Напиши стихотворение про осень.", language = "ru")
                ).forEach { request ->
                    val retrieved = knowledge.retrieve(request)
                    assertTrue(
                        request.query, retrieved.references.isEmpty() &&
                                retrieved.tools.isEmpty() && retrieved.settings.isEmpty()
                    )
                    val prompt = AssistantApplicationFixtures.prompt(
                        knowledge.grounding(request, retrieved, variant),
                        knowledge.userPrompt(request, retrieved)
                    )
                    val output = StringBuilder()
                    val started = System.nanoTime()
                    var firstToken = 0L
                    withTimeout(90_000) {
                        models.generate(variant, prompt) {
                            if (firstToken == 0L) firstToken = System.nanoTime()
                            output.append(it)
                        }
                    }
                    val reply = output.toString().trim()
                    report.put(
                        JSONObject().put("model", variant.id).put("question", request.query)
                            .put("prompt", prompt).put("rawOutput", reply)
                            .put("ttftMs", (firstToken - started) / 1e6)
                            .put("totalMs", (System.nanoTime() - started) / 1e6)
                    )
                    val plain = reply.replace(Regex("[*_`#]"), "").trim()
                    val clarification = if (request.language == "ru") {
                        Regex(
                            "(?:какую|какой|какая|какие|что|чем|как).*(?:ImageToolbox|задач|помоч|инструмент|настрой|файл|фото|изображ)",
                            setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)
                        )
                    } else {
                        Regex(
                            "(?:what|which|how).*(?:ImageToolbox|tool|setting|file|photo|image|help)",
                            setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)
                        )
                    }
                    val recipe = Regex(
                        "\\b(?:ingredients?|preheat|bake|oven|flour|cocoa|butter|sugar|eggs?|tbsp|tsp)\\b",
                        RegexOption.IGNORE_CASE
                    )
                    val appQuestion = plain.contains("ImageToolbox", ignoreCase = true) &&
                            clarification.containsMatchIn(plain) && plain.endsWith('?') &&
                            plain.count { it == '?' } == 1 && plain.lines()
                        .count { it.isNotBlank() } <= 2
                    if (!appQuestion || recipe.containsMatchIn(plain)) {
                        failures.add("${variant.id}/${request.language}: $reply")
                    }
                }
            }
            assertTrue(failures.joinToString("\n"), failures.isEmpty())
        } finally {
            models.release()
            File(app.cacheDir, "assistant-scope-quality.json").writeText(report.toString(2))
        }
    }
}
