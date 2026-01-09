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
import ai.onnxruntime.OnnxJavaType
import ai.onnxruntime.OrtSession
import ai.onnxruntime.TensorInfo
import kotlin.math.abs

internal class ModelInfo(
    val strength: Float,
    session: OrtSession,
    val chunkSize: Int,
    val overlap: Int
) {
    val inputName: String
    val inputInfoMap: Map<String, NodeInfo> = session.inputInfo
    val inputChannels: Int
    val outputChannels: Int
    val isFp16: Boolean
    val expectedWidth: Int?
    val expectedHeight: Int?

    init {
        var foundInputName: String? = null
        var foundInputChannels = 3
        var foundOutputChannels = 3
        var foundIsFp16 = false
        var foundExpectedWidth: Int? = null
        var foundExpectedHeight: Int? = null

        for ((key, nodeInfo) in inputInfoMap) {
            val tensorInfo = nodeInfo.info as? TensorInfo ?: continue
            val shape = tensorInfo.shape
            if ((tensorInfo.type == OnnxJavaType.FLOAT || tensorInfo.type == OnnxJavaType.FLOAT16) && shape.size == 4) {
                foundInputName = key
                foundInputChannels = if (shape[1] == 1L) 1 else 3
                foundIsFp16 = (tensorInfo.type == OnnxJavaType.FLOAT16)
                if (shape[2] > 0) foundExpectedHeight = shape[2].toInt()
                if (shape[3] > 0) foundExpectedWidth = shape[3].toInt()
                break
            }
        }

        val outputInfoMap = session.outputInfo
        for ((_, nodeInfo) in outputInfoMap) {
            val tensorInfo = nodeInfo.info as? TensorInfo ?: continue
            val shape = tensorInfo.shape
            if ((tensorInfo.type == OnnxJavaType.FLOAT || tensorInfo.type == OnnxJavaType.FLOAT16) && shape.size == 4) {
                foundOutputChannels = if (abs(shape[1]) == 1L) 1 else 3
                break
            }
        }

        inputName =
            foundInputName ?: throw RuntimeException("Could not find valid input tensor")
        inputChannels = foundInputChannels
        outputChannels = foundOutputChannels
        isFp16 = foundIsFp16
        expectedWidth = foundExpectedWidth
        expectedHeight = foundExpectedHeight
    }
}