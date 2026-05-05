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

package com.t8rin.imagetoolbox.feature.ai_tools.data.model

import ai.onnxruntime.NodeInfo
import ai.onnxruntime.OrtSession
import com.t8rin.imagetoolbox.core.utils.makeLog
import com.t8rin.imagetoolbox.feature.ai_tools.data.model.ImageTensor.Companion.firstImageTensor
import com.t8rin.imagetoolbox.feature.ai_tools.data.utils.AiExtensions.MODEL_ALIGNMENT
import com.t8rin.imagetoolbox.feature.ai_tools.data.utils.roundedUpTo
import com.t8rin.imagetoolbox.feature.ai_tools.domain.model.NeuralModel

internal class ModelInfo(
    val strength: Float,
    val overlap: Int,
    chunkSize: Int,
    disableChunking: Boolean,
    session: OrtSession,
    model: NeuralModel
) {
    val inputInfoMap: Map<String, NodeInfo> = session.inputInfo

    private val inputTensor = inputInfoMap.firstImageTensor()
        ?: error("ONNX session does not expose an image input tensor")

    val inputName: String = inputTensor.name
    val inputChannels: Int = inputTensor.channels
    val isScuNet = model.name.startsWith("scunet_")
    val outputChannels: Int = session.outputInfo
        .firstImageTensor()
        ?.outputChannels(keepSignedValue = isScuNet)
        ?: 3
    val isFp16: Boolean = inputTensor.isFloat16
    val expectedWidth: Int? = inputTensor.width
    val expectedHeight: Int? = inputTensor.height
    val isScuNetColor = model.name.startsWith("scunet_color")
    val isNonChunkable = model.isNonChunkable || disableChunking

    val minSpatialSize = spatialSizeMap.entries.find {
        model.name.contains(it.key)
    }?.value ?: 256

    val scaleFactor: Int = scaleMap.entries.find {
        model.name.contains(it.key)
    }?.value ?: 1

    val tileLimit = run {
        if (isNonChunkable) return@run Int.MAX_VALUE

        val chunkSize = if (isScuNet) {
            minOf(chunkSize, 256)
        } else {
            chunkSize
        }

        if (expectedWidth != null && expectedHeight != null) {
            minOf(chunkSize, expectedWidth, expectedHeight)
        } else {
            chunkSize
        }
    }

    fun tensorSizeFor(source: TensorSize): TensorSize {
        val useMinimumSide = isScuNetColor || minSpatialSize > 256
        val width = expectedWidth?.takeIf { it > 0 } ?: run {
            val aligned = source.width.roundedUpTo(MODEL_ALIGNMENT)
            if (useMinimumSide) maxOf(aligned, minSpatialSize) else aligned
        }
        val height = expectedHeight?.takeIf { it > 0 } ?: run {
            val aligned = source.height.roundedUpTo(MODEL_ALIGNMENT)
            if (useMinimumSide) maxOf(aligned, minSpatialSize) else aligned
        }
        return TensorSize(width, height)
    }

    init {
        "Model chunk=$chunkSize, overlap=$overlap, scale=$scaleFactor".makeLog("ModelInfo")

        val inputType = if (isFp16) "FP16" else "FP32"
        val widthText = expectedWidth ?: "dynamic"
        val heightText = expectedHeight ?: "dynamic"
        val tensorText = "Tensor input=$inputType/$inputChannels channel(s), " +
                "output=$outputChannels channel(s), " +
                "size=${widthText}x$heightText"

        tensorText.makeLog("ModelInfo")
    }
}

private val scaleMap = buildMap {
    repeat(16) {
        val scale = it + 1

        put("x$scale", scale)
        put("${scale}x", scale)
    }
}

private val spatialSizeMap = mapOf(
    "nafnet" to 512
)
