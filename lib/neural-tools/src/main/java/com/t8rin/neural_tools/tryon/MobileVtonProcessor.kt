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

package com.t8rin.neural_tools.tryon

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.graphics.Bitmap
import com.t8rin.imagetoolbox.core.domain.model.AssistantModel
import com.t8rin.neural_tools.assistant.AssistantModels
import com.t8rin.neural_tools.runCancellable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.Closeable
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.util.Random
import kotlin.math.exp

class MobileVtonProcessor(
    private val steps: Int = 12,
    private val seed: Long? = null
) : Closeable {

    init {
        require(steps > 0)
    }

    private val lifecycle = Any()
    private val models = MobileVtonModels.files
    private val captioner = AssistantModels(standaloneFiles = models)
    private val environment = OrtEnvironment.getEnvironment()
    private var closed = false
    private val jobs = mutableSetOf<Job>()

    suspend fun isDownloaded(): Boolean = withContext(Dispatchers.IO) {
        withModelLock { models.isDownloaded(currentCoroutineContext()::ensureActive) }
    }

    suspend fun download(onProgress: suspend (Float) -> Unit) = withContext(Dispatchers.IO) {
        withModelLock { models.download(onProgress) }
    }

    suspend fun process(person: Bitmap, clothing: Bitmap): Bitmap {
        var result: Bitmap? = null
        return try {
            withContext(Dispatchers.Default) {
                require(!person.isRecycled && !clothing.isRecycled) { "The input image has been recycled" }
                withModelLock {
                    check(withContext(Dispatchers.IO) {
                        models.isDownloaded(currentCoroutineContext()::ensureActive)
                    }) {
                        "Download Mobile-VTON models before processing"
                    }
                    val caption = describe(clothing)
                    currentCoroutineContext().ensureActive()
                    Tensors(environment).use { tensors ->
                        val text = encodeText(caption, tensors)
                        val image = encodeImage(clothing, tensors)
                        val random = seed?.let(::Random) ?: Random()
                        val conditions = encodeConditions(person, clothing, random, tensors)
                        val latent = denoise(text, image, conditions, random, tensors)
                        val output = tensors.float(1, 3, HEIGHT, WIDTH)
                        session("vae_decoder").use { decoder ->
                            decoder.runCancellable(
                                mapOf("latent" to latent.tensor),
                                mapOf("image" to output.tensor)
                            ).close()
                        }
                        MobileVtonImages.bitmap(output.buffer, WIDTH, HEIGHT).also { result = it }
                    }
                }
            }
        } catch (error: Throwable) {
            result?.recycle()
            throw error
        }
    }

    suspend fun deleteModels() = withContext(Dispatchers.IO) {
        withModelLock { models.delete() }
    }

    override fun close() {
        synchronized(lifecycle) {
            closed = true
            jobs.toList().forEach { it.cancel() }
        }
    }

    private suspend fun <T> withModelLock(action: suspend () -> T): T {
        val job = currentCoroutineContext()[Job]!!
        synchronized(lifecycle) {
            check(!closed) { "Mobile-VTON is closed" }
            jobs.add(job)
        }
        return try {
            lock.withLock { action() }
        } finally {
            synchronized(lifecycle) { jobs.remove(job) }
        }
    }

    private suspend fun describe(clothing: Bitmap): Pair<String, String> {
        val response = try {
            captioner.generate(
                variant = AssistantModel.VisionLite,
                prompt = CAPTION_PROMPT,
                images = listOf(clothing),
                maxOutputTokens = 160,
                stopOnRepetition = false,
                responseGrammar = CAPTION_GRAMMAR,
                onToken = {}
            )
        } finally {
            captioner.release()
        }
        val json = JSONObject(response)
        val description = json.getString("description").trim()
        val region = json.getString("region")
        check(region in setOf("upper body", "lower body", "whole body"))
        check(description.startsWith("The ") && description.length > 4) {
            "The local model did not produce a garment description"
        }
        return "Replace the $region with ${description.substringAfter(' ')}" to description
    }

    private suspend fun encodeText(
        caption: Pair<String, String>,
        tensors: Tensors
    ): List<Tensor> {
        val prompts = listOf(NEGATIVE_PROMPT, caption.first, caption.second)
        // The original pipeline appends 256 zero T5 tokens and pads CLIP channels to 4096.
        val result = List(3) { tensors.float(1, 333, 4096) }
        var channelOffset = 0
        listOf("text_encoder", "text_encoder_2").forEachIndexed { index, name ->
            currentCoroutineContext().ensureActive()
            val tokenizerName = if (index == 0) "tokenizer" else "tokenizer_2"
            val vocabulary = JSONObject(models.file("${tokenizerName}_vocab.json").readText())
            val tokenizer = MobileVtonTokenizer(
                vocabulary = vocabulary.keys().asSequence().associateWith(vocabulary::getInt),
                merges = models.file("${tokenizerName}_merges.txt").readLines(),
                padToken = if (index == 0) 49407 else 0
            )
            val channels = if (index == 0) 768 else 1280
            val hidden = tensors.float(1, 77, channels)
            session(name).use { encoder ->
                prompts.forEachIndexed { promptIndex, prompt ->
                    currentCoroutineContext().ensureActive()
                    tensors.long(tokenizer.encode(prompt)).use { input ->
                        encoder.runCancellable(
                            mapOf("input_ids" to input),
                            mapOf("hidden_states" to hidden.tensor)
                        ).close()
                    }
                    val destination = result[promptIndex].buffer
                    repeat(77) { token ->
                        repeat(channels) { channel ->
                            destination.put(
                                token * 4096 + channelOffset + channel,
                                hidden.buffer.get(token * channels + channel)
                            )
                        }
                    }
                }
            }
            channelOffset += channels
        }
        return result
    }

    private suspend fun encodeImage(clothing: Bitmap, tensors: Tensors): List<Tensor> {
        val pixels = tensors.float(1, 3, 224, 224)
        val hidden = List(2) { tensors.float(1, 257, 768) }
        session("image_encoder").use { encoder ->
            repeat(2) { index ->
                if (index == 1) MobileVtonImages.write(clothing, pixels.buffer, 224, 224, true)
                encoder.runCancellable(
                    mapOf("pixel_values" to pixels.tensor),
                    mapOf("hidden_states" to hidden[index].tensor)
                ).close()
            }
        }
        val result = List(2) { tensors.float(1, 16, 4096) }
        session("image_projection").use { projection ->
            repeat(2) { index ->
                projection.runCancellable(
                    mapOf("hidden_states" to hidden[index].tensor),
                    mapOf("image_embeds" to result[index].tensor)
                ).close()
            }
        }
        return result
    }

    private suspend fun encodeConditions(
        person: Bitmap,
        clothing: Bitmap,
        random: Random,
        tensors: Tensors
    ): List<Tensor> {
        val input = tensors.float(1, 3, HEIGHT, WIDTH)
        val mean = tensors.float(1, 16, HEIGHT / 8, WIDTH / 8)
        val logvar = tensors.float(1, 16, HEIGHT / 8, WIDTH / 8)
        val result = List(2) { tensors.float(1, 16, HEIGHT / 8, WIDTH / 8) }
        session("vae_encoder").use { encoder ->
            listOf(person, clothing).forEachIndexed { index, bitmap ->
                MobileVtonImages.write(bitmap, input.buffer, WIDTH, HEIGHT)
                encoder.runCancellable(
                    mapOf("image" to input.tensor),
                    mapOf("mean" to mean.tensor, "logvar" to logvar.tensor)
                ).close()
                repeat(mean.buffer.capacity()) { offset ->
                    val deviation = exp(0.5f * logvar.buffer.get(offset).coerceIn(-30f, 20f))
                    val sample =
                        mean.buffer.get(offset) + deviation * random.nextGaussian().toFloat()
                    result[index].buffer.put(offset, (sample - 0.0609f) * 1.5305f)
                }
                currentCoroutineContext().ensureActive()
            }
        }
        return result
    }

    private suspend fun denoise(
        text: List<Tensor>,
        image: List<Tensor>,
        conditions: List<Tensor>,
        random: Random,
        tensors: Tensors
    ): Tensor {
        val plane = HEIGHT / 8 * WIDTH / 8
        val latent = tensors.float(1, 16, HEIGHT / 4, WIDTH / 8)
        val sample = tensors.float(1, 32, HEIGHT / 4, WIDTH / 8)
        repeat(16) { channel ->
            repeat(plane) { index ->
                val noise = random.nextGaussian().toFloat()
                val offset = channel * plane * 2 + index
                latent.buffer.put(offset, noise)
                latent.buffer.put(offset + plane, noise)
                sample.buffer.put(
                    latent.buffer.capacity() + offset,
                    conditions[0].buffer.get(channel * plane + index)
                )
                sample.buffer.put(
                    latent.buffer.capacity() + offset + plane,
                    conditions[1].buffer.get(channel * plane + index)
                )
            }
        }
        val timestep = tensors.float(1)
        val featureTokens = HEIGHT / 32 * (WIDTH / 32)
        val doubled = List(24) { tensors.float(1, featureTokens * 2, 896) }
        val features = doubled.map { tensors.view(it, 1, featureTokens, 896) }
        val emptyFeature = tensors.float(1, featureTokens * 2, 896)
        val prediction = List(2) { tensors.float(1, 16, HEIGHT / 4, WIDTH / 8) }
        val garmentOutputs =
            features.mapIndexed { index, tensor -> "garment_$index" to tensor.tensor }.toMap()
        val garmentInputs = mapOf(
            "sample" to conditions[1].tensor,
            "timestep" to timestep.tensor,
            "encoder_hidden_states" to text[2].tensor
        )
        val inputs = List(2) { index ->
            buildMap {
                put("sample", sample.tensor)
                put("timestep", timestep.tensor)
                put("encoder_hidden_states", text[index].tensor)
                put("image_embeds", image[index].tensor)
                repeat(24) { feature ->
                    put(
                        "garment_$feature",
                        if (index == 0) emptyFeature.tensor else doubled[feature].tensor
                    )
                }
            }
        }
        val scheduler = MobileVtonScheduler(steps)
        session("garment").use { garment ->
            session("denoiser").use { denoiser ->
                repeat(steps) { index ->
                    currentCoroutineContext().ensureActive()
                    timestep.buffer.put(0, scheduler.timestep(index))
                    garment.runCancellable(garmentInputs, garmentOutputs).close()
                    features.forEachIndexed { featureIndex, feature ->
                        val destination = doubled[featureIndex].buffer
                        repeat(feature.buffer.capacity()) { offset ->
                            val value = feature.buffer.get(offset)
                            destination.put(feature.buffer.capacity() + offset, value)
                        }
                    }
                    repeat(latent.buffer.capacity()) {
                        sample.buffer.put(
                            it,
                            latent.buffer.get(it)
                        )
                    }
                    repeat(2) { guidance ->
                        currentCoroutineContext().ensureActive()
                        denoiser.runCancellable(
                            inputs[guidance],
                            mapOf("noise" to prediction[guidance].tensor)
                        ).close()
                    }
                    scheduler.step(
                        index,
                        latent.buffer,
                        prediction[1].buffer,
                        prediction[0].buffer,
                        2.5f
                    )
                }
            }
        }
        val decoded = tensors.float(1, 16, HEIGHT / 8, WIDTH / 8)
        repeat(16) { channel ->
            repeat(plane) { index ->
                decoded.buffer.put(
                    channel * plane + index,
                    latent.buffer.get(channel * 2 * plane + index) / 1.5305f + 0.0609f
                )
            }
        }
        return decoded
    }

    private fun session(name: String): OrtSession = OrtSession.SessionOptions().use { options ->
        options.addConfigEntry("mlas.disable_kleidiai", "1")
        options.setIntraOpNumThreads(Runtime.getRuntime().availableProcessors().coerceIn(1, 4))
        options.setInterOpNumThreads(1)
        options.setCPUArenaAllocator(false)
        options.setMemoryPatternOptimization(false)
        environment.createSession(models.file("$name.onnx").absolutePath, options)
    }

    private class Tensor(val buffer: FloatBuffer, val tensor: OnnxTensor)

    private class Tensors(private val environment: OrtEnvironment) : Closeable {
        private val tensors = mutableListOf<OnnxTensor>()

        fun float(vararg shape: Int): Tensor {
            val count = shape.fold(1, Math::multiplyExact)
            val buffer = ByteBuffer.allocateDirect(Math.multiplyExact(count, 4))
                .order(ByteOrder.nativeOrder()).asFloatBuffer()
            val tensor =
                OnnxTensor.createTensor(environment, buffer, shape.map(Int::toLong).toLongArray())
            tensors.add(tensor)
            return Tensor(buffer, tensor)
        }

        fun view(source: Tensor, vararg shape: Int): Tensor {
            val count = shape.fold(1, Math::multiplyExact)
            require(count <= source.buffer.capacity())
            val buffer = source.buffer.duplicate().apply { limit(count) }.slice()
            val tensor =
                OnnxTensor.createTensor(environment, buffer, shape.map(Int::toLong).toLongArray())
            tensors.add(tensor)
            return Tensor(buffer, tensor)
        }

        fun long(values: LongArray): OnnxTensor {
            val buffer = ByteBuffer.allocateDirect(values.size * 8).order(ByteOrder.nativeOrder())
                .asLongBuffer()
            buffer.put(values).rewind()
            return OnnxTensor.createTensor(
                environment,
                buffer,
                longArrayOf(1, values.size.toLong())
            )
        }

        override fun close() = tensors.asReversed().forEach(OnnxTensor::close)
    }

    companion object {
        private val lock = Mutex()
        private const val WIDTH = 768
        private const val HEIGHT = 1024
        private const val NEGATIVE_PROMPT =
            "deformed, distorted, disfigured, poorly drawn, bad anatomy, wrong anatomy, extra limb, " +
                    "missing limb, floating limbs, mutated hands and fingers, disconnected limbs, mutation, " +
                    "mutated, ugly, disgusting, blurry, amputation, NSFW"
        private val CAPTION_PROMPT = """
            <|im_start|>system
            Describe only the garment visible in the image. Answer in English as JSON.<|im_end|>
            <|im_start|>user
            <|vision_start|><|image_pad|><|vision_end|>
            Set region to upper body for tops, lower body for trousers or skirts, whole body for dresses. Describe the garment's type, color, sleeves, neckline, pattern and visible text in at most 55 words. Begin the description with "The". Do not describe the background.<|im_end|>
            <|im_start|>assistant
            <think>

            </think>

        """.trimIndent() + "\n\n"
        private val CAPTION_GRAMMAR = """
            start: %json {"type":"object","properties":{"region":{"enum":["upper body","lower body","whole body"]},"description":{"type":"string","minLength":5,"maxLength":400,"pattern":"^The "}},"required":["region","description"],"additionalProperties":false}
        """.trimIndent()
    }
}