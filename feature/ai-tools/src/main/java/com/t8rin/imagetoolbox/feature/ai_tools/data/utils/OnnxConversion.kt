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

import com.t8rin.imagetoolbox.core.utils.makeLog
import com.t8rin.imagetoolbox.feature.ai_tools.data.utils.AiExtensions.LOG_TAG
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.ensureActive

internal suspend fun extractOutputArray(
    outputValue: Any,
    channels: Int,
    h: Int,
    w: Int
): Pair<FloatArray, Int> = coroutineScope {
    ensureActive()
    "ONNX output payload: ${outputValue.javaClass.name}".makeLog(LOG_TAG)

    when (outputValue) {
        is FloatArray -> outputValue to channels
        is ShortArray -> FloatArray(outputValue.size) { index ->
            float16ToFloat(outputValue[index])
        } to channels

        is Array<*> -> readNestedOutput(
            outputValue = outputValue,
            channels = channels,
            height = h,
            width = w
        )

        else -> throw IllegalArgumentException(
            "Unsupported ONNX output: ${outputValue.javaClass}"
        )
    }
}

@Suppress("UNCHECKED_CAST")
private suspend fun readNestedOutput(
    outputValue: Array<*>,
    channels: Int,
    height: Int,
    width: Int
): Pair<FloatArray, Int> {
    val firstError = runCatching {
        copyNestedFloatOutput(
            tensor = outputValue as Array<Array<Array<FloatArray>>>,
            requestedChannels = channels,
            height = height,
            width = width
        )
    }

    if (firstError.isSuccess) {
        return firstError.getOrThrow()
    }

    return runCatching {
        copyNestedHalfOutput(
            tensor = outputValue as Array<Array<Array<ShortArray>>>,
            requestedChannels = channels,
            height = height,
            width = width
        )
    }.getOrElse { secondError ->
        throw IllegalStateException(
            "Unable to unpack ONNX result: " +
                    "${firstError.exceptionOrNull()?.message}, ${secondError.message}"
        )
    }
}

private suspend fun copyNestedFloatOutput(
    tensor: Array<Array<Array<FloatArray>>>,
    requestedChannels: Int,
    height: Int,
    width: Int
): Pair<FloatArray, Int> = coroutineScope {
    val batchCount = tensor.size
    val actualChannels = tensor[0].size
    val actualHeight = tensor[0][0].size
    val actualWidth = tensor[0][0][0].size
    val channelsToCopy = maxOf(requestedChannels, actualChannels)

    logNestedShape(
        precision = "FP32",
        batchCount = batchCount,
        channels = actualChannels,
        height = actualHeight,
        width = actualWidth,
        copiedChannels = channelsToCopy
    )

    val flattened = FloatArray(channelsToCopy * height * width)
    for (channel in 0 until channelsToCopy) {
        for (y in 0 until height) {
            for (x in 0 until width) {
                ensureActive()
                flattened[channel * height * width + y * width + x] = tensor[0][channel][y][x]
            }
        }
    }

    flattened to channelsToCopy
}

private suspend fun copyNestedHalfOutput(
    tensor: Array<Array<Array<ShortArray>>>,
    requestedChannels: Int,
    height: Int,
    width: Int
): Pair<FloatArray, Int> = coroutineScope {
    val batchCount = tensor.size
    val actualChannels = tensor[0].size
    val actualHeight = tensor[0][0].size
    val actualWidth = tensor[0][0][0].size
    val channelsToCopy = maxOf(requestedChannels, actualChannels)

    logNestedShape(
        precision = "FP16",
        batchCount = batchCount,
        channels = actualChannels,
        height = actualHeight,
        width = actualWidth,
        copiedChannels = channelsToCopy
    )

    val flattened = FloatArray(channelsToCopy * height * width)
    for (channel in 0 until channelsToCopy) {
        for (y in 0 until height) {
            for (x in 0 until width) {
                ensureActive()
                flattened[channel * height * width + y * width + x] =
                    float16ToFloat(tensor[0][channel][y][x])
            }
        }
    }

    flattened to channelsToCopy
}