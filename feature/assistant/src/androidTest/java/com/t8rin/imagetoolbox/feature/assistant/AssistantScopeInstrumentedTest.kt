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

import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.t8rin.imagetoolbox.core.di.entryPoint
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
    fun modelsExplainTheirScopeInsteadOfWritingRecipesOrPoems() = runBlocking {
        val app = InstrumentationRegistry.getInstrumentation().targetContext
        app.startActivity(
            checkNotNull(app.packageManager.getLaunchIntentForPackage(app.packageName))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        val knowledge = app.entryPoint<AssistantEntryPoint>().knowledge
        val models = AssistantModels()
        val variants = models.installations(verifyFiles = false)
            .filter { it.installed && !it.variant.supportsVision }.map { it.variant }
        assumeTrue("Install a text model before testing scope", variants.isNotEmpty())
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
                    val recipe = Regex(
                        "\\b(?:ingredients?|preheat|bake|oven|flour|cocoa|butter|sugar|eggs?|tbsp|tsp)\\b",
                        RegexOption.IGNORE_CASE
                    )
                    val explainsScope = Regex(
                        "ImageToolbox|image|photo|изображ|фото|обработк|настройк",
                        RegexOption.IGNORE_CASE
                    ).containsMatchIn(plain) &&
                            !plain.contains(
                                "Что вы хотите сделать с изображением",
                                ignoreCase = true
                            ) &&
                            plain.lines().count { it.isNotBlank() } <= 6
                    if (!explainsScope || recipe.containsMatchIn(plain)) {
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
