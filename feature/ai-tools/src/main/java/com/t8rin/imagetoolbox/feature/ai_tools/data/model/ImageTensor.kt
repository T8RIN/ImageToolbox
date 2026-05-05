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
import ai.onnxruntime.TensorInfo
import kotlin.math.abs

internal data class ImageTensor(
    val name: String,
    val channels: Int,
    val isFloat16: Boolean,
    val height: Int?,
    val width: Int?,
    private val rawChannels: Long
) {
    fun outputChannels(keepSignedValue: Boolean): Int {
        val channelsValue = if (keepSignedValue) rawChannels else abs(rawChannels)
        return if (channelsValue == 1L) 1 else 3
    }

    companion object {
        fun Map<String, NodeInfo>.firstImageTensor(): ImageTensor? {
            fun TensorInfo.isImageFloatTensor(shape: LongArray): Boolean {
                val isFloatTensor = type == OnnxJavaType.FLOAT || type == OnnxJavaType.FLOAT16
                return isFloatTensor && shape.size == 4
            }

            fun Long.positiveDimension(): Int? = takeIf { it > 0L }?.toInt()

            for ((name, nodeInfo) in this) {
                val tensorInfo = nodeInfo.info as? TensorInfo ?: continue
                val shape = tensorInfo.shape
                if (!tensorInfo.isImageFloatTensor(shape)) continue

                return ImageTensor(
                    name = name,
                    channels = if (shape[1] == 1L) 1 else 3,
                    isFloat16 = tensorInfo.type == OnnxJavaType.FLOAT16,
                    height = shape[2].positiveDimension(),
                    width = shape[3].positiveDimension(),
                    rawChannels = shape[1]
                )
            }

            return null
        }
    }
}
