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

package com.t8rin.imagetoolbox.feature.ai_tools.data

import ai.onnxruntime.OrtSession
import android.graphics.Bitmap
import androidx.core.graphics.scale
import com.t8rin.imagetoolbox.feature.ai_tools.data.utils.createInputTensor
import com.t8rin.imagetoolbox.feature.ai_tools.domain.model.AiDetectionContract
import com.t8rin.imagetoolbox.feature.ai_tools.domain.model.AiDetectionResult
import com.t8rin.imagetoolbox.feature.ai_tools.domain.model.NeuralModel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import javax.inject.Inject
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.roundToInt

internal class AiImageDetectorProcessor @Inject constructor() {

    suspend fun detect(
        session: OrtSession,
        source: Bitmap,
        model: NeuralModel
    ): AiDetectionResult {
        val contract = requireNotNull(model.aiDetectionContract)
        val prepared = source.prepareForDetection(contract)

        return try {
            currentCoroutineContext().ensureActive()
            val tensorData = prepared.bitmap.toNormalizedNchw(contract)
            createInputTensor(
                data = tensorData,
                shape = longArrayOf(
                    1,
                    3,
                    contract.inputSize.toLong(),
                    contract.inputSize.toLong()
                ),
                fp16 = false
            ).use { input ->
                session.run(mapOf(session.inputNames.first() to input)).use { output ->
                    val logits = flattenFloatOutput(output[0].value)
                    AiDetectionResult(
                        aiProbability = contract.output.toAiProbability(logits)
                    )
                }
            }
        } finally {
            if (prepared.recycleAfterUse) prepared.bitmap.recycle()
        }
    }
}

private data class PreparedBitmap(
    val bitmap: Bitmap,
    val recycleAfterUse: Boolean
)

private fun Bitmap.prepareForDetection(
    contract: AiDetectionContract
): PreparedBitmap = when (contract.resizeMode) {
    AiDetectionContract.ResizeMode.Stretch -> {
        val resized = this.scale(contract.inputSize, contract.inputSize)
        PreparedBitmap(
            bitmap = resized,
            recycleAfterUse = resized !== this
        )
    }

    AiDetectionContract.ResizeMode.ShortestSideCenterCrop -> {
        val scale = contract.resizeSize.toFloat() / minOf(width, height)
        val scaledWidth = (width * scale).roundToInt().coerceAtLeast(contract.inputSize)
        val scaledHeight = (height * scale).roundToInt().coerceAtLeast(contract.inputSize)
        val resized = this.scale(scaledWidth, scaledHeight)
        val cropped = Bitmap.createBitmap(
            resized,
            (scaledWidth - contract.inputSize) / 2,
            (scaledHeight - contract.inputSize) / 2,
            contract.inputSize,
            contract.inputSize
        )

        if (resized !== this && resized !== cropped) resized.recycle()

        PreparedBitmap(
            bitmap = cropped,
            recycleAfterUse = cropped !== this
        )
    }
}

private suspend fun Bitmap.toNormalizedNchw(
    contract: AiDetectionContract
): FloatArray {
    val size = contract.inputSize
    val pixelCount = size * size
    val pixels = IntArray(pixelCount)
    getPixels(pixels, 0, size, 0, 0, size, size)

    val output = FloatArray(pixelCount * 3)
    pixels.forEachIndexed { index, pixel ->
        if (index % size == 0) currentCoroutineContext().ensureActive()

        val red = ((pixel shr 16) and 0xFF) / 255f
        val green = ((pixel shr 8) and 0xFF) / 255f
        val blue = (pixel and 0xFF) / 255f

        output[index] = (red - contract.mean.red) / contract.std.red
        output[pixelCount + index] = (green - contract.mean.green) / contract.std.green
        output[pixelCount * 2 + index] = (blue - contract.mean.blue) / contract.std.blue
    }
    return output
}

internal fun flattenFloatOutput(value: Any?): FloatArray = buildList {
    fun append(item: Any?) {
        when (item) {
            is Float -> add(item)
            is FloatArray -> item.forEach(::add)
            is Array<*> -> item.forEach(::append)
            else -> error("Unsupported AI detector output: ${item?.javaClass}")
        }
    }
    append(value)
}.toFloatArray()

internal fun AiDetectionContract.Output.toAiProbability(logits: FloatArray): Float {
    require(logits.isNotEmpty()) { "AI detector returned an empty output" }

    return when (this) {
        is AiDetectionContract.Output.Sigmoid -> {
            (1f / (1f + exp(-logits.first()))).coerceIn(0f, 1f)
        }

        is AiDetectionContract.Output.Softmax -> {
            require(aiClassIndices.isNotEmpty()) {
                "At least one AI class index is required"
            }
            require(aiClassIndices.all { it in logits.indices }) {
                "AI class indices $aiClassIndices are outside of ${logits.size} output classes"
            }
            val maximum = logits.max()
            val denominator = logits.sumOf { exp((it - maximum).toDouble()) }
            val aiProbability = aiClassIndices.sumOf { index ->
                exp((logits[index] - maximum).toDouble())
            }
            (aiProbability / max(denominator, 1e-12))
                .toFloat()
                .coerceIn(0f, 1f)
        }
    }
}
