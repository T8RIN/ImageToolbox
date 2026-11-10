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

package com.t8rin.neural_tools.ocr

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import com.t8rin.neural_tools.runCancellable
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import java.nio.FloatBuffer
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.round

internal class GalleryOcrRouter(
    private val session: OrtSession,
    private val labels: List<String>,
    private val config: GalleryOcrModels.Config
) {
    suspend fun classify(crops: List<Mat>): List<Route> {
        val windows = crops.map {
            currentCoroutineContext().ensureActive()
            windows(it)
        }
        val probabilities = windows.flatten().chunked(64).flatMap { batch ->
            currentCoroutineContext().ensureActive()
            val input = FloatArray(batch.size * WIDTH * HEIGHT)
            batch.forEachIndexed { index, window -> window.copyInto(input, index * WIDTH * HEIGHT) }
            OnnxTensor.createTensor(
                OrtEnvironment.getEnvironment(), FloatBuffer.wrap(input),
                longArrayOf(batch.size.toLong(), 1, HEIGHT.toLong(), WIDTH.toLong())
            ).use { tensor ->
                session.runCancellable(mapOf("ink" to tensor)).use { result ->
                    val output = (result.get("logits").get() as OnnxTensor).floatBuffer
                    List(batch.size) {
                        val row = FloatArray(labels.size)
                        output.get(row)
                        val max = row.max()
                        val weights = row.map { exp((it - max).toDouble()) }
                        val sum = weights.sum()
                        weights.map { it / sum }
                    }
                }
            }
        }

        var offset = 0
        val classifications = windows.map { parts ->
            val mean = labels.indices.map { label ->
                (offset until offset + parts.size).sumOf { probabilities[it][label] } / parts.size
            }
            offset += parts.size
            val (best, second) = labels.indices.sortedByDescending(mean::get)
            Classification(labels[best], mean[best], mean[best] - mean[second])
        }
        val hint = classifications.filter { it.canRoute() }
            .groupingBy { it.label }.eachCount().maxByOrNull { it.value }?.key

        return classifications.map {
            val script = when {
                it.canRoute() -> it.label
                it.isLatinOrCyrillic -> null
                else -> hint
            } ?: "Latn".takeIf { crops.size >= 8 }
            Route(config.routes[script] ?: config.fallback, script)
        }
    }

    // Similar Latin and Cyrillic capitals can give several misleading votes on the same image.
    private fun Classification.canRoute() =
        isConfident() && (!isLatinOrCyrillic || probability >= .88)

    private fun Classification.isConfident() =
        label != "unknown" && probability >= config.accept && margin >= config.margin

    private fun windows(gray: Mat): List<FloatArray> {
        val height = gray.rows()
        val width = gray.cols()
        if (minOf(width, height) < 2) return listOf(FloatArray(WIDTH * HEIGHT))

        val pixels = ByteArray(height * width)
        gray.get(0, 0, pixels)
        val band = (height / 10).coerceAtLeast(1) * width
        val edges = IntArray(256)
        for (i in 0 until band) {
            edges[pixels[i].toInt() and 255]++
            edges[pixels[pixels.size - 1 - i].toInt() and 255]++
        }
        val invert = edges.percentile(2 * band, .5) < 115
        val histogram = IntArray(256)
        val intensities = IntArray(pixels.size) {
            val value = pixels[it].toInt() and 255
            (if (invert) 255 - value else value).also { grayValue -> histogram[grayValue]++ }
        }
        val low = histogram.percentile(pixels.size, .05)
        val high = histogram.percentile(pixels.size, .85)
        val contrast = maxOf(35f, high - low)
        val values =
            FloatArray(pixels.size) { ((high - intensities[it]) / contrast).coerceIn(0f, 1f) }

        val targetHeight = 26
        val targetWidth =
            round(width.toDouble() * targetHeight / height).toInt().coerceIn(2, WIDTH * 5)
        val ink = Mat(height, width, CvType.CV_32FC1)
        val resized = Mat()
        val scaled = FloatArray(targetWidth * targetHeight)
        try {
            ink.put(0, 0, values)
            Imgproc.resize(
                ink, resized, Size(targetWidth.toDouble(), targetHeight.toDouble()), 0.0, 0.0,
                if (height > targetHeight) Imgproc.INTER_AREA else Imgproc.INTER_LINEAR
            )
            resized.get(0, 0, scaled)
        } finally {
            ink.release()
            resized.release()
        }

        val count = ((targetWidth - 32 + 95) / 96).coerceIn(1, 4)
        val last = (targetWidth - WIDTH).coerceAtLeast(0)
        val starts =
            if (count == 1) listOf(0) else List(count) { it * last / (count - 1) }.distinct()
        val partWidth = minOf(WIDTH, targetWidth)
        val left = (WIDTH - partWidth) / 2
        return starts.map { start ->
            FloatArray(WIDTH * HEIGHT).also { window ->
                for (y in 0 until targetHeight) {
                    val row = y * targetWidth + start
                    scaled.copyInto(window, (y + 3) * WIDTH + left, row, row + partWidth)
                }
            }
        }
    }

    private fun IntArray.percentile(size: Int, fraction: Double): Float {
        val rank = (size - 1) * fraction
        val lower = floor(rank).toInt()
        var total = 0
        var low: Int? = null
        for (value in indices) {
            total += this[value]
            if (total > lower && low == null) low = value
            if (total > lower + 1 || (total > lower && rank == lower.toDouble())) {
                return (checkNotNull(low) + (value - low) * (rank - lower)).toFloat()
            }
        }
        return lastIndex.toFloat()
    }

    data class Route(val profile: GalleryOcrModels.Profile, val script: String?)

    private data class Classification(
        val label: String,
        val probability: Double,
        val margin: Double
    ) {
        val isLatinOrCyrillic get() = label == "Latn" || label == "Cyrl"
    }

    private companion object {
        const val WIDTH = 160
        const val HEIGHT = 32
    }
}