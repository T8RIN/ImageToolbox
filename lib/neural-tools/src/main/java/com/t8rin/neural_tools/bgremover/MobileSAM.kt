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

package com.t8rin.neural_tools.bgremover

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.graphics.Bitmap
import android.graphics.RectF
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import com.t8rin.neural_tools.ModelFiles
import com.t8rin.neural_tools.NeuralTool
import com.t8rin.neural_tools.runWithOptions
import java.io.Closeable
import java.nio.FloatBuffer
import kotlin.math.exp
import kotlin.math.roundToInt

object MobileSAM : NeuralTool() {
    private val models = ModelFiles(
        "mobile_sam",
        ModelFiles.Model(
            name = "mobile_sam_image_encoder.onnx",
            size = 28_157_093L,
            sha256 = "580f5fb648ea1062c0aabc26217aed56921985f03f0cbbd852bba81d760cc749"
        ),
        ModelFiles.Model(
            name = "sam_mask_decoder_single.onnx",
            size = 16_501_323L,
            sha256 = "93915fc7c993ab9d59ab8c9ccd3bce37f7509c81ab4150a74abd4d2abbd8570d"
        )
    )

    data class Point(
        val x: Float,
        val y: Float,
        val positive: Boolean
    )

    fun isDownloaded() = models.isDownloaded()
    suspend fun download(onProgress: suspend (Float) -> Unit) = models.download(onProgress)
    fun deleteModels() = models.delete()

    fun openSession(image: Bitmap, runOptions: OrtSession.RunOptions? = null): Session {
        check(isDownloaded()) { "Download MobileSAM models first" }
        return Session(image, runOptions)
    }

    internal fun autoRemove(image: Bitmap, runOptions: OrtSession.RunOptions?): Bitmap {
        val proxy = image.scale(320, 320)
        val pixels = IntArray(320 * 320)
        try {
            val foreground = U2NetPortableBackgroundRemover.removeBackground(
                image = proxy,
                runOptions = runOptions
            )
            try {
                foreground.getPixels(pixels, 0, 320, 0, 0, 320, 320)
            } finally {
                foreground.recycle()
            }
        } finally {
            if (proxy !== image) proxy.recycle()
        }
        var left = 320
        var top = 320
        var right = -1
        var bottom = -1
        for (i in pixels.indices) {
            if ((pixels[i] ushr 24) > 128) {
                left = minOf(left, i % 320)
                top = minOf(top, i / 320)
                right = maxOf(right, i % 320)
                bottom = maxOf(bottom, i / 320)
            }
        }
        check(right >= left && bottom >= top) { "No foreground object found" }
        val box = RectF(left / 320f, top / 320f, (right + 1) / 320f, (bottom + 1) / 320f)
        return openSession(image, runOptions).use { it.segment(emptyList(), runOptions, box) }
    }

    class Session internal constructor(
        private val image: Bitmap,
        runOptions: OrtSession.RunOptions?
    ) : Closeable {
        private val environment = OrtEnvironment.getEnvironment()
        private val width: Int
        private val height: Int
        private val embeddings: OnnxTensor
        private val decoder: OrtSession

        init {
            val scale = 1024f / maxOf(image.width, image.height)
            width = (image.width * scale).roundToInt().coerceIn(1, 1024)
            height = (image.height * scale).roundToInt().coerceIn(1, 1024)
            val resized = image.scale(width, height)
            val pixels = IntArray(width * height)
            resized.getPixels(pixels, 0, width, 0, 0, width, height)
            if (resized !== image) resized.recycle()
            val buffer = FloatBuffer.allocate(1024 * 1024 * 3)
            for (y in 0 until height) {
                for (x in 0 until width) {
                    val pixel = pixels[y * width + x]
                    val offset = (y * 1024 + x) * 3
                    buffer.put(offset, (pixel shr 16 and 255).toFloat())
                    buffer.put(offset + 1, (pixel shr 8 and 255).toFloat())
                    buffer.put(offset + 2, (pixel and 255).toFloat())
                }
            }
            embeddings = options().use { options ->
                val file = models.file("mobile_sam_image_encoder.onnx")
                environment.createSession(file.absolutePath, options).use { encoder ->
                    OnnxTensor.createTensor(environment, buffer, longArrayOf(1024, 1024, 3)).use { input ->
                        encoder.runWithOptions(mapOf("input_image" to input), runOptions).use { result ->
                            val tensor = result[0] as OnnxTensor
                            val values = tensor.floatBuffer
                            val copy = FloatArray(values.remaining()).also(values::get)
                            OnnxTensor.createTensor(environment, FloatBuffer.wrap(copy), tensor.info.shape)
                        }
                    }
                }
            }
            decoder = runCatching {
                options().use {
                    environment.createSession(models.file("sam_mask_decoder_single.onnx").absolutePath, it)
                }
            }.onFailure {
                embeddings.close()
            }.getOrThrow()
        }

        fun segment(
            points: List<Point>,
            runOptions: OrtSession.RunOptions? = null,
            box: RectF? = null,
            outputImage: Bitmap = image
        ): Bitmap {
            require(points.any { it.positive } || box != null) { "Select a foreground point first" }
            val outputWidth = outputImage.width
            val outputHeight = outputImage.height
            val count = points.size + if (box != null) 2 else 1
            val coords = FloatArray(count * 2)
            val labels = FloatArray(count) { -1f }
            points.forEachIndexed { index, point ->
                coords[index * 2] = point.x.coerceIn(0f, 1f) * width
                coords[index * 2 + 1] = point.y.coerceIn(0f, 1f) * height
                labels[index] = if (point.positive) 1f else 0f
            }
            if (box != null) {
                val index = points.size
                coords[index * 2] = box.left * width
                coords[index * 2 + 1] = box.top * height
                coords[index * 2 + 2] = box.right * width
                coords[index * 2 + 3] = box.bottom * height
                labels[index] = 2f
                labels[index + 1] = 3f
            }
            val tensors = mutableMapOf<String, OnnxTensor>()
            try {
                tensors["point_coords"] = OnnxTensor.createTensor(
                    environment,
                    FloatBuffer.wrap(coords),
                    longArrayOf(1, count.toLong(), 2)
                )
                tensors["point_labels"] = OnnxTensor.createTensor(
                    environment,
                    FloatBuffer.wrap(labels),
                    longArrayOf(1, count.toLong())
                )
                tensors["mask_input"] = OnnxTensor.createTensor(
                    environment,
                    FloatBuffer.allocate(256 * 256),
                    longArrayOf(1, 1, 256, 256)
                )
                tensors["has_mask_input"] = OnnxTensor.createTensor(environment, floatArrayOf(0f))
                tensors["orig_im_size"] = OnnxTensor.createTensor(
                    environment,
                    floatArrayOf(outputHeight.toFloat(), outputWidth.toFloat())
                )
                val inputs = tensors + ("image_embeddings" to embeddings)
                return decoder.runWithOptions(inputs, runOptions).use { output ->
                    val values = (output[0] as OnnxTensor).floatBuffer
                    check(values.remaining() == outputWidth * outputHeight) {
                        "Unexpected MobileSAM mask dimensions"
                    }
                    val pixels = IntArray(outputWidth * outputHeight)
                    outputImage.getPixels(pixels, 0, outputWidth, 0, 0, outputWidth, outputHeight)
                    for (i in pixels.indices) {
                        val opacity = 1f / (1f + exp(-values.get(i).coerceIn(-20f, 20f)))
                        val alpha = ((pixels[i] ushr 24) * opacity).roundToInt().coerceIn(0, 255)
                        pixels[i] = (alpha shl 24) or (pixels[i] and 0xFFFFFF)
                    }
                    createBitmap(outputWidth, outputHeight).apply {
                        setPixels(pixels, 0, outputWidth, 0, 0, outputWidth, outputHeight)
                        setHasAlpha(true)
                    }
                }
            } finally {
                tensors.values.forEach(OnnxTensor::close)
            }
        }

        override fun close() {
            try {
                decoder.close()
            } finally {
                embeddings.close()
            }
        }

        private fun options() = OrtSession.SessionOptions().apply {
            addConfigEntry("mlas.disable_kleidiai", "1")
            setIntraOpNumThreads(Runtime.getRuntime().availableProcessors().coerceIn(1, 4))
        }
    }
}