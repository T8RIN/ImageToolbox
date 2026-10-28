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

package com.t8rin.neural_tools.faces

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.graphics.Bitmap
import androidx.core.graphics.scale
import com.t8rin.neural_tools.ModelFiles
import com.t8rin.neural_tools.runCancellable
import java.io.Closeable
import java.nio.FloatBuffer
import kotlin.math.sqrt

object ArcFace {
    private val models = ModelFiles(
        "faces",
        ModelFiles.Model(
            name = "arcface.onnx",
            size = 136_619_444L,
            sha256 = "ffe014a45c9488506719d37fd578ece6661bb385535b36e8039975fa5d4683db"
        )
    )

    fun isDownloaded() = models.isDownloaded()
    suspend fun download(onProgress: suspend (Float) -> Unit) = models.download(onProgress)
    fun deleteModels() = models.delete()
    fun openEncoder() = Encoder()

    class Encoder internal constructor() : Closeable {
        private val environment = OrtEnvironment.getEnvironment()
        private val sessionDelegate = lazy {
            OrtSession.SessionOptions().use { options ->
                options.addConfigEntry("mlas.disable_kleidiai", "1")
                options.setIntraOpNumThreads(
                    Runtime.getRuntime().availableProcessors().coerceIn(1, 4)
                )
                environment.createSession(models.file("arcface.onnx").absolutePath, options)
            }
        }
        private val session by sessionDelegate

        suspend fun encode(image: Bitmap, face: DetectedFace): FloatArray {
            val box = face.bounds
            val marginX = box.width() * 0.2f
            val marginY = box.height() * 0.2f
            val left = ((box.left - marginX) * image.width).toInt().coerceIn(0, image.width - 1)
            val top = ((box.top - marginY) * image.height).toInt().coerceIn(0, image.height - 1)
            val right =
                ((box.right + marginX) * image.width).toInt().coerceIn(left + 1, image.width)
            val bottom =
                ((box.bottom + marginY) * image.height).toInt().coerceIn(top + 1, image.height)
            val crop = Bitmap.createBitmap(image, left, top, right - left, bottom - top)
            val resized = crop.scale(112, 112)
            val pixels = IntArray(112 * 112)
            resized.getPixels(pixels, 0, 112, 0, 0, 112, 112)
            if (resized !== crop) resized.recycle()
            if (crop !== image) crop.recycle()
            val buffer = FloatBuffer.allocate(pixels.size * 3)
            for (pixel in pixels) {
                buffer.put(((pixel shr 16 and 255) - 127.5f) / 127.5f)
                buffer.put(((pixel shr 8 and 255) - 127.5f) / 127.5f)
                buffer.put(((pixel and 255) - 127.5f) / 127.5f)
            }
            buffer.rewind()
            return OnnxTensor.createTensor(environment, buffer, longArrayOf(1, 112, 112, 3))
                .use { input ->
                    session.runCancellable(mapOf(session.inputNames.first() to input))
                        .use { output ->
                            val vector = (output[0] as OnnxTensor).floatBuffer.let { values ->
                                FloatArray(values.remaining()).also(values::get)
                            }
                            check(vector.size == 512 && vector.all(Float::isFinite)) { "Invalid face embedding" }
                            val norm = sqrt(vector.sumOf { it.toDouble() * it }).toFloat()
                            check(norm > 0f) { "Empty face embedding" }
                            FloatArray(vector.size) { vector[it] / norm }
                        }
                }
        }

        override fun close() {
            if (sessionDelegate.isInitialized()) session.close()
        }
    }
}