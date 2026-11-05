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

package com.t8rin.imagetoolbox.feature.ai_tools.data.utils

import ai.onnxruntime.OnnxTensor
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

internal suspend fun extractOutputArray(
    output: OnnxTensor,
    channels: Int,
    h: Int,
    w: Int
): Pair<FloatArray, Int> {
    val context = currentCoroutineContext()
    context.ensureActive()
    val shape = output.info.shape
    val actualChannels = if (shape.size == 4) {
        require(shape[0] >= 1L && shape[2] >= h && shape[3] >= w) {
            "Unexpected ONNX output shape: ${shape.contentToString()}"
        }
        shape[1].toInt()
    } else channels
    val buffer = requireNotNull(output.floatBuffer) {
        "Unsupported ONNX output: ${output.info.type}"
    }
    if (shape.size == 4 && (shape[0] > 1 || shape[2] != h.toLong() || shape[3] != w.toLong())) {
        val height = shape[2].toInt()
        val width = shape[3].toInt()
        val values = FloatArray(actualChannels * h * w)
        for (channel in 0 until actualChannels) {
            for (y in 0 until h) {
                context.ensureActive()
                buffer.position((channel * height + y) * width)
                buffer.get(values, (channel * h + y) * w, w)
            }
        }
        return values to actualChannels
    }
    require(actualChannels > 0 && buffer.remaining().toLong() == actualChannels.toLong() * h * w) {
        "Unexpected ONNX output size: ${buffer.remaining()}"
    }
    val values = if (buffer.hasArray() && buffer.arrayOffset() == 0 &&
        buffer.position() == 0 && buffer.remaining() == buffer.array().size
    ) {
        buffer.array()
    } else FloatArray(buffer.remaining()).also(buffer::get)
    context.ensureActive()
    return values to actualChannels
}