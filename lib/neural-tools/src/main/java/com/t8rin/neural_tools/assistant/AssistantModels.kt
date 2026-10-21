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

package com.t8rin.neural_tools.assistant

import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.genai.GenAI
import ai.onnxruntime.genai.Generator
import ai.onnxruntime.genai.GeneratorParams
import ai.onnxruntime.genai.Images
import ai.onnxruntime.genai.Model
import ai.onnxruntime.genai.MultiModalProcessor
import ai.onnxruntime.genai.Tokenizer
import android.graphics.Bitmap
import android.os.Build
import android.os.Process
import androidx.core.graphics.scale
import com.t8rin.imagetoolbox.core.domain.model.AssistantModel
import com.t8rin.neural_tools.DownloadProgress
import com.t8rin.neural_tools.ModelFiles
import com.t8rin.neural_tools.NeuralTool
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.security.MessageDigest
import java.text.Normalizer
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AssistantModels : NeuralTool() {

    class InputTooLongException(val tokenCount: Int) : IllegalArgumentException(
        "Assistant input exceeds $MAX_PROMPT_TOKENS tokens: $tokenCount"
    )

    enum class Phase {
        Missing, Downloading, Verifying, Installed, Error
    }

    data class ModelState(
        val variant: AssistantModel,
        val installed: Boolean,
        val downloadSize: Long?,
        val installedSize: Long,
        val phase: Phase = if (installed) Phase.Installed else Phase.Missing,
        val error: String? = null,
        val progress: Float? = null
    )

    val states: StateFlow<List<ModelState>> = _states.asStateFlow()

    private val runtimeLock = Mutex()
    private val nativeLock = Any()

    @Volatile
    private var generationJob: Job? = null

    private var activeGenerator: Generator? = null
    private var activeModel: Model? = null
    private var activeTokenizer: Tokenizer? = null

    @Volatile
    private var activeVariant: AssistantModel? = null

    private val directory: File
        get() = File(context.filesDir, "ai_models/assistant")

    suspend fun installations(): List<ModelState> = withContext(Dispatchers.IO) {
        AssistantModel.entries.forEach { variant ->
            val lock = packageLocks.getValue(variant)
            while (!lock.tryLock()) {
                val state = states.value.single { it.variant == variant }
                if (state.phase != Phase.Verifying || state.progress != null) return@forEach
                states.first { items ->
                    items.single { it.variant == variant }.let {
                        it.phase != Phase.Verifying || it.progress != null
                    }
                }
            }
            var result = states.value.single { it.variant == variant }
            try {
                val modelPackage = AssistantModelFiles[variant]
                updateState(variant) { it.copy(phase = Phase.Verifying) }
                val installed = recover(variant, modelPackage)
                result = ModelState(
                    variant,
                    installed,
                    modelPackage.size,
                    if (installed) modelPackage.size else 0L
                )
            } finally {
                updateState(variant) { result }
                lock.unlock()
            }
        }
        states.value
    }

    suspend fun download(
        variant: AssistantModel,
        onProgress: suspend (DownloadProgress) -> Unit
    ) = withContext(Dispatchers.IO) {
        val job = checkNotNull(currentCoroutineContext()[Job])
        val previous = downloadJobs.putIfAbsent(variant, job)
        if (previous != null) {
            previous.join()
            val state = states.value.single { it.variant == variant }
            check(state.installed) { "Assistant model download did not finish" }
            onProgress(DownloadProgress(1f, checkNotNull(state.downloadSize)))
            return@withContext
        }
        try {
            packageLocks.getValue(variant).withLock {
                downloadPackage(variant, onProgress)
            }
        } finally {
            downloadJobs.remove(variant, job)
        }
    }

    fun cancelDownload(variant: AssistantModel) {
        downloadJobs[variant]?.cancel(CancellationException("Assistant model download cancelled"))
    }

    private suspend fun downloadPackage(
        variant: AssistantModel,
        onProgress: suspend (DownloadProgress) -> Unit
    ) {
        val modelPackage = AssistantModelFiles[variant]
        val staging = directory.resolve(".${variant.id}.download")
        val target = directory.resolve(variant.id)
        try {
            updateState(variant) { it.copy(phase = Phase.Verifying, progress = 0f) }
            if (recover(variant, modelPackage)) {
                updateState(variant) {
                    ModelState(
                        variant,
                        true,
                        modelPackage.size,
                        modelPackage.size
                    )
                }
                onProgress(DownloadProgress(1f, modelPackage.size))
                return
            }
            check(staging.mkdirs() || staging.isDirectory)
            val filenames = modelPackage.files.map { it.name }.toSet()
            checkNotNull(staging.listFiles()).forEach { file ->
                currentCoroutineContext().ensureActive()
                if (file.name !in filenames) check(file.deleteRecursively())
            }
            updateState(variant) {
                ModelState(variant, false, modelPackage.size, 0L, Phase.Downloading, progress = 0f)
            }
            files(variant, staging, modelPackage).download { fraction ->
                updateState(variant) { it.copy(progress = fraction) }
                onProgress(DownloadProgress(fraction, modelPackage.size))
            }
            updateState(variant) { it.copy(phase = Phase.Verifying) }
            verify(staging, modelPackage)
            currentCoroutineContext().ensureActive()
            runtimeLock.withLock {
                if (activeVariant == variant) closeModel()
                install(staging, target)
            }
            updateState(variant) { ModelState(variant, true, modelPackage.size, modelPackage.size) }
        } catch (cancelled: CancellationException) {
            updateState(variant) {
                it.copy(
                    phase = if (it.installed) Phase.Installed else Phase.Missing,
                    error = null,
                    progress = null
                )
            }
            throw cancelled
        } catch (error: Exception) {
            updateState(variant) {
                it.copy(
                    phase = Phase.Error,
                    error = error.message,
                    progress = null
                )
            }
            throw error
        } finally {
            staging.deleteRecursively()
        }
    }

    suspend fun delete(variant: AssistantModel) {
        if (activeVariant == variant) cancel()
        withContext(Dispatchers.IO) {
            packageLocks.getValue(variant).withLock {
                runtimeLock.withLock {
                    if (activeVariant == variant) closeModel()
                    listOf(".${variant.id}.previous", ".${variant.id}.download", variant.id)
                        .forEach { name ->
                            val target = directory.resolve(name)
                            check(!target.exists() || target.deleteRecursively())
                        }
                    verified.clear()
                    updateState(variant) {
                        it.copy(
                            installed = false,
                            installedSize = 0L,
                            phase = Phase.Missing,
                            error = null
                        )
                    }
                }
            }
        }
    }

    suspend fun generate(
        variant: AssistantModel,
        prompt: String,
        images: List<Bitmap> = emptyList(),
        onToken: suspend (String) -> Unit
    ) = withContext(Dispatchers.Default) {
        check(isSupported()) { unsupportedReason.orEmpty() }
        require(prompt.isNotBlank())
        require(images.size <= MAX_IMAGES) { "Assistant accepts one image per request" }
        require(images.isEmpty() || variant.supportsVision) { "This assistant model does not support images" }
        require(images.none { it.isRecycled }) { "Assistant image has already been recycled" }
        val job = currentCoroutineContext()[Job]
        synchronized(nativeLock) {
            check(generationJob == null) { "Assistant generation is already running" }
            generationJob = job
        }
        try {
            packageLocks.getValue(variant).withLock {
                runtimeLock.withLock {
                    try {
                        withContext(Dispatchers.IO) {
                            verify(directory.resolve(variant.id), AssistantModelFiles[variant])
                        }
                        currentCoroutineContext().ensureActive()
                        if (activeVariant != variant) {
                            closeModel()
                            OrtEnvironment.getEnvironment().setTelemetry(false)
                            GenAI.setTelemetry(false)
                            val model = Model(directory.resolve(variant.id).absolutePath)
                            try {
                                currentCoroutineContext().ensureActive()
                                activeTokenizer = Tokenizer(model)
                                activeModel = model
                                activeVariant = variant
                            } catch (error: Throwable) {
                                model.close()
                                throw error
                            }
                        }
                        currentCoroutineContext().ensureActive()
                        if (variant.supportsVision) {
                            generateVisionTokens(
                                Normalizer.normalize(prompt, Normalizer.Form.NFC),
                                images.singleOrNull(),
                                onToken
                            )
                        } else {
                            generateTokens(prompt, onToken)
                        }
                    } catch (error: Throwable) {
                        if (error !is InputTooLongException) closeModel()
                        throw error
                    }
                }
            }
        } finally {
            synchronized(nativeLock) { generationJob = null }
        }
    }

    private suspend fun generateTokens(
        prompt: String,
        onToken: suspend (String) -> Unit
    ) {
        val tokenizer = checkNotNull(activeTokenizer)
        tokenizer.encode(prompt).use { tokens ->
            val inputSize = tokens.getSequence(0).size
            generateTokens(inputSize, onToken) { generator ->
                generator.appendTokenSequences(tokens)
            }
        }
    }

    private suspend fun generateVisionTokens(
        prompt: String,
        image: Bitmap?,
        onToken: suspend (String) -> Unit
    ) {
        val inputSize = checkNotNull(activeTokenizer).encode(prompt).use { tokens ->
            tokens.getSequence(0).size + if (image != null) MAX_IMAGE_TOKENS - 1 else 0
        }
        if (inputSize > MAX_PROMPT_TOKENS) throw InputTooLongException(inputSize)
        MultiModalProcessor(checkNotNull(activeModel)).use { processor ->
            withPreparedImage(image) { file ->
                file?.let { Images(it.absolutePath) }.use { images ->
                    processor.processImages(prompt, images).use { inputs ->
                        currentCoroutineContext().ensureActive()
                        generateTokens(inputSize, onToken) { generator ->
                            generator.setInputs(inputs)
                            val actualSize = generator.tokenCount().toInt()
                            if (actualSize > MAX_PROMPT_TOKENS) {
                                throw InputTooLongException(actualSize)
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun generateTokens(
        inputSize: Int,
        onToken: suspend (String) -> Unit,
        prepare: (Generator) -> Unit
    ) {
        if (inputSize > MAX_PROMPT_TOKENS) throw InputTooLongException(inputSize)
        GeneratorParams(checkNotNull(activeModel)).use { params ->
            params.setSearchOption("max_length", (inputSize + MAX_OUTPUT_TOKENS).toDouble())
            params.setSearchOption("do_sample", false)
            if (activeVariant?.supportsVision == true) {
                params.setSearchOption("repetition_penalty", 1.1)
            }
            Generator(checkNotNull(activeModel), params).use { generator ->
                synchronized(nativeLock) { activeGenerator = generator }
                try {
                    runCancellable { prepare(generator) }
                    streamTokens(generator, checkNotNull(activeTokenizer), onToken)
                } finally {
                    synchronized(nativeLock) { activeGenerator = null }
                }
            }
        }
    }

    private suspend fun withPreparedImage(
        image: Bitmap?,
        action: suspend (File?) -> Unit
    ) {
        if (image == null) return action(null)
        withContext(Dispatchers.IO) {
            val file = File.createTempFile("assistant-vision-", ".png", context.cacheDir)
            var scaled = image
            try {
                currentCoroutineContext().ensureActive()
                val longestSide = maxOf(image.width, image.height)
                if (longestSide > MAX_IMAGE_SIZE) {
                    val scale = MAX_IMAGE_SIZE.toFloat() / longestSide
                    scaled = image.scale(
                        (image.width * scale).toInt().coerceAtLeast(1),
                        (image.height * scale).toInt().coerceAtLeast(1)
                    )
                }
                file.outputStream().use { output ->
                    check(scaled.compress(Bitmap.CompressFormat.PNG, 100, output)) {
                        "Cannot prepare image for assistant"
                    }
                }
                currentCoroutineContext().ensureActive()
                withContext(Dispatchers.Default) { action(file) }
            } finally {
                if (scaled !== image) scaled.recycle()
                file.delete()
            }
        }
    }

    private suspend fun streamTokens(
        generator: Generator,
        tokenizer: Tokenizer,
        onToken: suspend (String) -> Unit
    ) {
        tokenizer.createStream().use { stream ->
            val eos = tokenizer.eosTokenIds.toSet()
            var emittedTokens = generator.tokenCount()
            val maxTokens = emittedTokens + MAX_OUTPUT_TOKENS
            while (emittedTokens < maxTokens) {
                currentCoroutineContext().ensureActive()
                if (generator.isDone) break
                runCancellable { generator.generateNextToken() }
                val count = generator.tokenCount()
                if (count == emittedTokens) continue
                emittedTokens = count
                val token = generator.getLastTokenInSequence(0)
                if (token !in eos) {
                    val delta = stream.decode(token)
                    if (delta.isNotEmpty()) onToken(delta)
                }
            }
        }
    }

    fun cancel() {
        generationJob?.cancel(CancellationException("Assistant generation cancelled"))
        synchronized(nativeLock) {
            runCatching { activeGenerator?.setRuntimeOption("terminate_session", "1") }
        }
    }

    suspend fun release() {
        cancel()
        withContext(NonCancellable + Dispatchers.Default) {
            runtimeLock.withLock { closeModel() }
        }
    }

    private fun closeModel() {
        val tokenizer = activeTokenizer
        val model = activeModel
        activeTokenizer = null
        activeModel = null
        activeVariant = null
        try {
            tokenizer?.close()
        } finally {
            model?.close()
        }
    }

    private fun install(staging: File, target: File) {
        val previous = directory.resolve(".${target.name}.previous")
        check(!previous.exists() || previous.deleteRecursively())
        val hadPrevious = target.exists()
        if (hadPrevious) check(target.renameTo(previous))
        if (!staging.renameTo(target)) {
            if (hadPrevious) check(previous.renameTo(target))
            error("Cannot install assistant model")
        }
        previous.deleteRecursively()
        verified.clear()
    }

    private suspend fun runCancellable(action: () -> Unit) =
        suspendCancellableCoroutine { continuation ->
            continuation.invokeOnCancellation {
                synchronized(nativeLock) {
                    runCatching { activeGenerator?.setRuntimeOption("terminate_session", "1") }
                }
            }
            if (!continuation.isActive) return@suspendCancellableCoroutine
            runCatching(action).onSuccess {
                continuation.resume(Unit)
            }.onFailure {
                continuation.resumeWithException(it)
            }
        }

    private fun files(
        variant: AssistantModel,
        directory: File,
        modelPackage: AssistantModelFiles
    ): ModelFiles = ModelFiles(
        folder = "assistant/v1/${variant.id}",
        models = modelPackage.files.toTypedArray(),
        directoryOverride = directory
    )

    private suspend fun isInstalled(directory: File, modelPackage: AssistantModelFiles): Boolean {
        return try {
            verify(directory, modelPackage)
            true
        } catch (error: CancellationException) {
            throw error
        } catch (_: Exception) {
            false
        }
    }

    private suspend fun recover(
        variant: AssistantModel,
        modelPackage: AssistantModelFiles
    ): Boolean {
        val target = directory.resolve(variant.id)
        val previous = directory.resolve(".${variant.id}.previous")
        if (isInstalled(target, modelPackage)) {
            previous.deleteRecursively()
            return true
        }
        if (!isInstalled(previous, modelPackage)) return false
        runtimeLock.withLock {
            if (activeVariant == variant) closeModel()
            check(!target.exists() || target.deleteRecursively())
            check(previous.renameTo(target)) { "Cannot recover assistant model" }
            verified.clear()
        }
        return true
    }

    private suspend fun verify(directory: File, modelPackage: AssistantModelFiles) {
        val key = directory.absoluteFile to modelPackage
        val signature = modelPackage.files.map {
            val file = directory.resolve(it.name)
            file.length() to file.lastModified()
        }
        if (verified[key] == signature) return
        modelPackage.files.forEach { item ->
            currentCoroutineContext().ensureActive()
            val file = directory.resolve(item.name)
            check(file.isFile && file.length() == item.size) { "Invalid assistant model file: ${item.name}" }
            val digest = MessageDigest.getInstance("SHA-256")
            file.inputStream().buffered().use { input ->
                val buffer = ByteArray(VERIFY_BUFFER_SIZE)
                while (true) {
                    currentCoroutineContext().ensureActive()
                    val count = input.read(buffer)
                    if (count < 0) break
                    digest.update(buffer, 0, count)
                }
            }
            check(digest.digest().joinToString("") { "%02x".format(it) } == item.sha256) {
                "Assistant model checksum mismatch: ${item.name}"
            }
        }
        verified[key] = signature
    }

    private fun updateState(variant: AssistantModel, update: (ModelState) -> ModelState) {
        _states.update { states -> states.map { if (it.variant == variant) update(it) else it } }
    }

    companion object {
        const val RUNTIME_VERSION = AssistantModelFiles.RUNTIME_VERSION
        const val MAX_PROMPT_TOKENS = 3072
        const val MAX_OUTPUT_TOKENS = 1024
        const val MAX_IMAGES = 1
        const val MAX_IMAGE_SIZE = 768
        private const val MAX_IMAGE_TOKENS = MAX_IMAGE_SIZE * MAX_IMAGE_SIZE / (32 * 32)
        private const val VERIFY_BUFFER_SIZE = 1024 * 1024
        private val packageLocks = AssistantModel.entries.associateWith { Mutex() }
        private val downloadJobs = ConcurrentHashMap<AssistantModel, Job>()
        private val verified =
            ConcurrentHashMap<Pair<File, AssistantModelFiles>, List<Pair<Long, Long>>>()
        private val _states = MutableStateFlow(
            AssistantModel.entries.map { ModelState(it, false, AssistantModelFiles[it].size, 0L) }
        )

        fun isSupported(): Boolean = Process.is64Bit() &&
                Build.SUPPORTED_64_BIT_ABIS.any { it == "arm64-v8a" || it == "x86_64" }

        val unsupportedReason: String?
            get() = if (isSupported()) null else "Local generation requires arm64-v8a or x86_64"
    }
}
