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

package com.t8rin.neural_tools.search

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.core.graphics.createBitmap
import com.t8rin.neural_tools.NeuralTool
import com.t8rin.neural_tools.runCancellable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.Closeable
import java.io.File
import java.nio.FloatBuffer
import java.nio.LongBuffer
import java.security.MessageDigest

/**
 * OpenAI CLIP ViT-B/32 visual encoder (MIT), with multilingual DistilBERT text embeddings (Apache-2.0).
 * https://github.com/openai/CLIP
 * https://huggingface.co/Xenova/clip-vit-base-patch32/tree/d15189d7028b43f1d3e65039190477f6af591c2a
 * https://huggingface.co/sentence-transformers/clip-ViT-B-32-multilingual-v1/tree/58edf8cada9e398793dca955574a48cbb7f18be2
 *
 * Sessions are owned by the caller and must be opened off the main thread and closed after use.
 */
object ClipImageSearch : NeuralTool() {

    private val models = listOf(
        ModelFile(
            name = "visual_quant.onnx",
            size = 89_117_001L,
            sha256 = "583fd1110a514667812fee7d684952aaf82a99b959760c8d7dca7e0ab9839299"
        ),
        ModelFile(
            name = "textual_multilingual_quant.onnx",
            size = 136_909_921L,
            sha256 = "73499ba0518a41c555d0762511988896db51ae3250d56ff816f34d436c36459d"
        ),
        ModelFile(
            name = "vocab.txt",
            size = 995_526L,
            sha256 = "fe0fda7c425b48c516fc8f160d594c8022a0808447475c1a7c6d6479763f310c"
        )
    )

    private val environment by lazy { OrtEnvironment.getEnvironment() }

    private val directory: File
        get() = File(context.filesDir, "ai_models/clip_search")

    private val verifiedFiles = mutableMapOf<String, Pair<Long, Long>>()

    fun isDownloaded(): Boolean = models.all { isValid(it, File(directory, it.name)) }

    suspend fun download(
        onProgress: suspend (Float) -> Unit
    ) = withContext(Dispatchers.IO) {
        check(directory.mkdirs() || directory.isDirectory)
        val totalSize = models.sumOf(ModelFile::size)
        var completedSize = 0L
        onProgress(0f)

        models.forEach { model ->
            currentCoroutineContext().ensureActive()
            val destination = File(directory, model.name)
            if (!isValid(model, destination)) {
                val temporary = File.createTempFile(model.name, ".download", directory)
                try {
                    downloader.download(
                        url = baseUrl.replace("*", "clip/${model.name}?download=true"),
                        destinationPath = temporary.absolutePath,
                        onProgress = { progress ->
                            currentCoroutineContext().ensureActive()
                            val downloaded = progress.currentPercent.coerceIn(0f, 1f) * model.size
                            onProgress((completedSize + downloaded) / totalSize)
                        }
                    )
                    currentCoroutineContext().ensureActive()
                    check(
                        isValid(
                            model,
                            temporary
                        )
                    ) { "Invalid downloaded CLIP file: ${model.name}" }
                    currentCoroutineContext().ensureActive()
                    check(temporary.renameTo(destination)) { "Cannot install CLIP file: ${model.name}" }
                } finally {
                    synchronized(verifiedFiles) { verifiedFiles.remove(temporary.absolutePath) }
                    temporary.delete()
                }
            }
            completedSize += model.size
            onProgress(completedSize.toFloat() / totalSize)
        }
    }

    fun deleteModels() {
        check(!directory.exists() || directory.deleteRecursively()) { "Cannot delete CLIP models" }
        synchronized(verifiedFiles) { verifiedFiles.clear() }
    }

    fun openImageEncoder(): ImageEncoder {
        check(isDownloaded()) { "CLIP models are not downloaded" }
        return ImageEncoder(openSession("visual_quant.onnx"))
    }

    fun openTextEncoder(): TextEncoder {
        check(isDownloaded()) { "CLIP models are not downloaded" }
        val vocabulary = File(directory, "vocab.txt").useLines { lines ->
            lines.withIndex().associate { it.value to it.index }
        }
        val tokenizer = ClipTokenizer(vocabulary)
        return TextEncoder(openSession("textual_multilingual_quant.onnx"), tokenizer)
    }

    private fun openSession(name: String): OrtSession = OrtSession.SessionOptions().use { options ->
        options.setOptimizationLevel(OrtSession.SessionOptions.OptLevel.ALL_OPT)
        // KleidiAI can select unsupported SME instructions on Android ARM64 emulators.
        options.addConfigEntry("mlas.disable_kleidiai", "1")
        options.setIntraOpNumThreads(Runtime.getRuntime().availableProcessors().coerceIn(1, 4))
        environment.createSession(File(directory, name).absolutePath, options)
    }

    private fun isValid(model: ModelFile, file: File): Boolean {
        if (!file.isFile || file.length() != model.size) return false
        val stamp = file.length() to file.lastModified()
        synchronized(verifiedFiles) {
            if (verifiedFiles[file.absolutePath] == stamp) return true
        }
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().buffered().use { input ->
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            var size = input.read(buffer)
            while (size >= 0) {
                digest.update(buffer, 0, size)
                size = input.read(buffer)
            }
        }
        val valid = digest.digest().joinToString("") { "%02x".format(it) } == model.sha256
        if (valid) synchronized(verifiedFiles) { verifiedFiles[file.absolutePath] = stamp }
        return valid
    }

    class ImageEncoder internal constructor(
        private val session: OrtSession
    ) : Closeable {

        suspend fun encode(bitmap: Bitmap): FloatArray {
            currentCoroutineContext().ensureActive()
            val input = preprocess(bitmap)
            return OnnxTensor.createTensor(
                environment,
                FloatBuffer.wrap(input),
                longArrayOf(1, 3, IMAGE_SIZE.toLong(), IMAGE_SIZE.toLong())
            ).use { tensor ->
                session.runCancellable(mapOf("pixel_values" to tensor)).use {
                    it.embedding("image_embeds")
                }
            }
        }

        override fun close() = session.close()

        private fun preprocess(bitmap: Bitmap): FloatArray {
            val result = createBitmap(IMAGE_SIZE, IMAGE_SIZE)
            try {
                // Resize the shorter side to 224, then take the centered 224 x 224 crop.
                val scale = IMAGE_SIZE.toFloat() / minOf(bitmap.width, bitmap.height)
                val width = (bitmap.width * scale).toInt().coerceAtLeast(IMAGE_SIZE)
                val height = (bitmap.height * scale).toInt().coerceAtLeast(IMAGE_SIZE)
                val left = (IMAGE_SIZE - width) / 2f
                val top = (IMAGE_SIZE - height) / 2f
                Canvas(result).drawBitmap(
                    bitmap,
                    null,
                    RectF(left, top, left + width, top + height),
                    Paint(Paint.FILTER_BITMAP_FLAG)
                )
                val pixels = IntArray(IMAGE_SIZE * IMAGE_SIZE)
                result.getPixels(pixels, 0, IMAGE_SIZE, 0, 0, IMAGE_SIZE, IMAGE_SIZE)
                return FloatArray(pixels.size * 3).apply {
                    pixels.forEachIndexed { index, pixel ->
                        this[index] = ((pixel shr 16 and 0xff) / 255f - 0.48145467f) / 0.26862955f
                        this[index + pixels.size] =
                            ((pixel shr 8 and 0xff) / 255f - 0.4578275f) / 0.2613026f
                        this[index + pixels.size * 2] =
                            ((pixel and 0xff) / 255f - 0.40821072f) / 0.2757771f
                    }
                }
            } finally {
                result.recycle()
            }
        }

        private companion object {
            const val IMAGE_SIZE = 224
        }
    }

    class TextEncoder internal constructor(
        private val session: OrtSession,
        private val tokenizer: ClipTokenizer
    ) : Closeable {

        suspend fun encode(text: String): FloatArray {
            currentCoroutineContext().ensureActive()
            val tokens = tokenizer.encode(text)
            val shape = longArrayOf(1, tokens.inputIds.size.toLong())
            return OnnxTensor.createTensor(
                environment,
                LongBuffer.wrap(tokens.inputIds),
                shape
            ).use { inputIds ->
                OnnxTensor.createTensor(
                    environment,
                    LongBuffer.wrap(tokens.attentionMask),
                    shape
                ).use { attentionMask ->
                    session.runCancellable(
                        mapOf("input_ids" to inputIds, "attention_mask" to attentionMask)
                    ).use { it.embedding("sentence_embedding") }
                }
            }
        }

        override fun close() = session.close()
    }

    private fun OrtSession.Result.embedding(name: String): FloatArray {
        val output = get(name).get() as OnnxTensor
        val buffer = output.floatBuffer
        return FloatArray(buffer.remaining()).also(buffer::get).normalizeClipEmbedding()
    }

    private data class ModelFile(
        val name: String,
        val size: Long,
        val sha256: String
    )
}