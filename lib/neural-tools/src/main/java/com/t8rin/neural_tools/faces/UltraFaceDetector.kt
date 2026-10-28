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
import android.graphics.RectF
import androidx.core.graphics.scale
import com.t8rin.neural_tools.NeuralTool
import com.t8rin.neural_tools.runCancellable
import java.io.Closeable
import java.nio.FloatBuffer

class UltraFaceDetector : NeuralTool(), Closeable {
    private val environment = OrtEnvironment.getEnvironment()
    private val sessionDelegate = lazy {
        OrtSession.SessionOptions().use { options ->
            options.addConfigEntry("mlas.disable_kleidiai", "1")
            options.setIntraOpNumThreads(Runtime.getRuntime().availableProcessors().coerceIn(1, 4))
            context.assets.open("version-RFB-320.onnx").use {
                environment.createSession(it.readBytes(), options)
            }
        }
    }

    private val session by sessionDelegate

    suspend fun detect(image: Bitmap, minimumConfidence: Float = 0.7f): List<DetectedFace> {
        val resized = image.scale(320, 240)
        val pixels = IntArray(320 * 240)
        resized.getPixels(pixels, 0, 320, 0, 0, 320, 240)
        if (resized !== image) resized.recycle()
        val values = FloatBuffer.allocate(pixels.size * 3)
        for (channel in 0..2) {
            for (pixel in pixels) values.put(((pixel shr (16 - channel * 8) and 255) - 127f) / 128f)
        }
        values.rewind()
        return OnnxTensor.createTensor(environment, values, longArrayOf(1, 3, 240, 320)).use { input ->
            session.runCancellable(mapOf(session.inputNames.first() to input)).use { output ->
                val tensors = (0 until output.size()).map { output[it] as OnnxTensor }
                val scores = tensors.first { it.info.shape.last() == 2L }.floatBuffer
                val boxes = tensors.first { it.info.shape.last() == 4L }.floatBuffer
                val count = scores.remaining() / 2
                check(boxes.remaining() == count * 4) { "Unexpected UltraFace output dimensions" }
                val candidates = (0 until count).mapNotNull { index ->
                    val bounds = RectF(
                        boxes.get(index * 4).coerceIn(0f, 1f),
                        boxes.get(index * 4 + 1).coerceIn(0f, 1f),
                        boxes.get(index * 4 + 2).coerceIn(0f, 1f),
                        boxes.get(index * 4 + 3).coerceIn(0f, 1f)
                    )
                    DetectedFace(bounds, scores.get(index * 2 + 1)).takeIf {
                        it.confidence >= minimumConfidence && bounds.width() > 0 && bounds.height() > 0
                    }
                }.sortedByDescending { it.confidence }
                buildList {
                    for (candidate in candidates) {
                        if (none { overlap(it.bounds, candidate.bounds) > 0.3f }) add(candidate)
                    }
                }
            }
        }
    }

    private fun overlap(a: RectF, b: RectF): Float {
        val width = (minOf(a.right, b.right) - maxOf(a.left, b.left)).coerceAtLeast(0f)
        val height = (minOf(a.bottom, b.bottom) - maxOf(a.top, b.top)).coerceAtLeast(0f)
        val intersection = width * height
        return intersection / (a.width() * a.height() + b.width() * b.height() - intersection).coerceAtLeast(1e-6f)
    }

    override fun close() {
        if (sessionDelegate.isInitialized()) session.close()
    }
}