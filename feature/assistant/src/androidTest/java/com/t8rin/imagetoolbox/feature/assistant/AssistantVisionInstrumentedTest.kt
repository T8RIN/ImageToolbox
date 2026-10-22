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

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.t8rin.imagetoolbox.core.di.entryPoint
import com.t8rin.imagetoolbox.core.domain.model.AssistantModel
import com.t8rin.imagetoolbox.feature.assistant.di.AssistantEntryPoint
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantRequest
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantScreenContext
import com.t8rin.imagetoolbox.feature.assistant.domain.assistantPrompt
import com.t8rin.neural_tools.assistant.AssistantModels
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class AssistantVisionInstrumentedTest {
    private val app = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun downloadsPublishedVisionPackages() = runBlocking {
        assumeTrue(
            InstrumentationRegistry.getArguments()
                .getString("downloadAssistantVisionModels") == "true"
        )
        val models = AssistantModels()
        try {
            AssistantModel.entries.filter { it.supportsVision }.forEach { variant ->
                var progress = 0f
                withTimeout(30 * 60_000L) {
                    models.download(variant) { progress = it.currentPercent }
                }
                assertTrue(models.installations().single { it.variant == variant }.installed)
                assertTrue(progress >= 1f)
                assertFalse(
                    File(
                        app.filesDir,
                        "ai_models/assistant/.${variant.id}.download"
                    ).exists()
                )
            }
        } finally {
            models.release()
        }
    }

    @Test
    fun visionLiteReadsPixelsAndRecoversAfterCancellation() = runBlocking {
        verify(AssistantModel.VisionLite)
    }

    @Test
    fun visionAdvancedReadsPixelsAndRecoversAfterCancellation() = runBlocking {
        verify(AssistantModel.VisionAdvanced)
    }

    @Test
    fun visionMaxReadsPixelsAndRecoversAfterCancellation() = runBlocking {
        verify(AssistantModel.VisionMax)
    }

    private suspend fun verify(variant: AssistantModel) = coroutineScope {
        val models = AssistantModels()
        val records = JSONArray()
        val images = listOf(fixture(Color.RED, circle = true), fixture(Color.BLUE, circle = false))
        try {
            assumeTrue(
                "Install ${variant.id} before running image generation tests",
                models.installations().single { it.variant == variant }.installed
            )
            val replies = images.mapIndexed { index, bitmap ->
                val started = System.nanoTime()
                val output = StringBuilder()
                withTimeout(5 * 60_000L) {
                    models.generate(
                        variant = variant,
                        prompt = prompt("Name the color and shape in this image in one short sentence."),
                        images = listOf(bitmap)
                    ) { output.append(it) }
                }
                output.toString().also {
                    records.put(
                        JSONObject().put("image", index).put("answer", it)
                            .put("seconds", (System.nanoTime() - started) / 1e9)
                    )
                }
            }
            assertTrue(replies[0], replies[0].contains("red", ignoreCase = true))
            assertTrue(replies[1], replies[1].contains("blue", ignoreCase = true))
            assertTrue("Both images produced the same answer", replies[0] != replies[1])

            val knowledge = app.entryPoint<AssistantEntryPoint>().knowledge
            val request = AssistantRequest(
                query = "Опиши изображение одним предложением.",
                context = AssistantScreenContext(selectedUris = listOf("content://assistant-test/image")),
                language = "ru"
            )
            val answer = knowledge.retrieve(request)
            val description = StringBuilder()
            withTimeout(5 * 60_000L) {
                models.generate(
                    variant = variant,
                    prompt = assistantPrompt(
                        grounding = knowledge.grounding(request, answer, variant, hasImage = true),
                        history = emptyList(),
                        question = knowledge.userPrompt(request, answer),
                        hasImage = true
                    ),
                    images = listOf(images[0])
                ) { description.append(it) }
            }
            records.put(JSONObject().put("groundedRussian", description.toString()))
            assertTrue(
                description.toString(),
                description.contains(Regex("красн", RegexOption.IGNORE_CASE))
            )

            val started = CompletableDeferred<Unit>()
            val prefill = launch(Dispatchers.Default) {
                started.complete(Unit)
                models.generate(
                    variant = variant,
                    prompt = prompt("Describe all the details in this image."),
                    images = listOf(images[1])
                ) {}
            }
            started.await()
            delay(100)
            val cancelStarted = System.nanoTime()
            models.cancel()
            withTimeout(30_000) { prefill.join() }
            records.put(
                JSONObject().put(
                    "prefillCancelSeconds",
                    (System.nanoTime() - cancelStarted) / 1e9
                )
            )
            assertTrue("Early vision generation did not cancel", prefill.isCancelled)

            val firstToken = CompletableDeferred<Unit>()
            val generation = launch {
                models.generate(
                    variant = variant,
                    prompt = prompt("Describe this image in detail."),
                    images = listOf(images[0])
                ) {
                    firstToken.complete(Unit)
                    models.cancel()
                }
            }
            withTimeout(5 * 60_000L) {
                firstToken.await()
                generation.join()
            }
            assertTrue("Vision generation did not cancel", generation.isCancelled)
            models.release()

            val recovered = StringBuilder()
            withTimeout(5 * 60_000L) {
                models.generate(
                    variant,
                    prompt("Say hello in one short sentence.", image = false)
                ) {
                    recovered.append(it)
                }
            }
            records.put(JSONObject().put("textAfterCancel", recovered.toString()))
            assertTrue(
                "Text generation after vision cancellation was empty",
                recovered.isNotBlank()
            )
            assertTrue(images.none(Bitmap::isRecycled))
            assertTrue(
                "Temporary assistant images were not removed",
                app.cacheDir.listFiles().orEmpty().none { it.name.startsWith("assistant-vision-") }
            )
        } finally {
            models.release()
            images.forEach(Bitmap::recycle)
            File(app.cacheDir, "assistant-${variant.id}-pixels.json").writeText(records.toString(2))
        }
    }

    private fun fixture(color: Int, circle: Boolean): Bitmap =
        Bitmap.createBitmap(384, 384, Bitmap.Config.ARGB_8888).apply {
            Canvas(this).apply {
                drawColor(Color.WHITE)
                val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color }
                if (circle) drawCircle(192f, 192f, 128f, paint)
                else drawRect(64f, 64f, 320f, 320f, paint)
            }
        }

    private fun prompt(question: String, image: Boolean = true): String =
        "<|im_start|>system\nAnswer in English. Be brief.<|im_end|>\n" +
                "<|im_start|>user\n" +
                (if (image) "<|vision_start|><|image_pad|><|vision_end|>\n" else "") +
                "$question<|im_end|>\n<|im_start|>assistant\n<think>\n\n</think>\n\n"
}
