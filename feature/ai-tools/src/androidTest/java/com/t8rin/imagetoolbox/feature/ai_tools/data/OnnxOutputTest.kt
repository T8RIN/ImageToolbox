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

import ai.onnxruntime.OnnxTensor
import android.util.Log
import com.t8rin.imagetoolbox.feature.ai_tools.data.utils.createInputTensor
import com.t8rin.imagetoolbox.feature.ai_tools.data.utils.extractOutputArray
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.system.measureNanoTime

class OnnxOutputTest {

    @Test
    fun outputMatchesNativeValuesForFloatAndHalfPrecision() = runBlocking {
        for (channels in listOf(1, 3, 4)) {
            for (fp16 in listOf(false, true)) {
                val input = FloatArray(channels * 7 * 11) { (it % 101 - 50) / 64f }
                createInputTensor(
                    input,
                    longArrayOf(1, channels.toLong(), 7, 11),
                    fp16
                ).use { tensor ->
                    val (actual, count) = extractOutputArray(tensor, channels, 7, 11)
                    assertEquals(channels, count)
                    assertArrayEquals(input, actual, 0f)
                    assertArrayEquals(referenceOutput(tensor, channels, 7, 11), actual, 0f)
                }
            }
        }
    }

    @Test
    fun paddedOutputKeepsTheOriginalCropAndFirstBatch(): Unit = runBlocking {
        val input = FloatArray(2 * 3 * 9 * 13) { it / 64f }
        createInputTensor(input, longArrayOf(2, 3, 9, 13), false).use { tensor ->
            val actual = extractOutputArray(tensor, 3, 7, 11).first
            assertArrayEquals(referenceOutput(tensor, 3, 7, 11), actual, 0f)
        }
        createInputTensor(FloatArray(77) { it.toFloat() }, longArrayOf(77), false).use { tensor ->
            assertArrayEquals(
                FloatArray(77) { it.toFloat() },
                extractOutputArray(tensor, 1, 7, 11).first,
                0f
            )
        }
    }

    @Test
    fun outputCopyBenchmark(): Unit = runBlocking {
        val width = 512
        val height = 512
        val input = FloatArray(3 * width * height) { (it % 251) / 255f }
        createInputTensor(
            input,
            longArrayOf(1, 3, height.toLong(), width.toLong()),
            false
        ).use { tensor ->
            val before = mutableListOf<Long>()
            val after = mutableListOf<Long>()
            repeat(10) { pass ->
                lateinit var expected: FloatArray
                lateinit var actual: FloatArray
                val oldTime =
                    measureNanoTime { expected = referenceOutput(tensor, 3, height, width) }
                val newTime =
                    measureNanoTime { actual = extractOutputArray(tensor, 3, height, width).first }
                assertArrayEquals(input, expected, 0f)
                assertArrayEquals(expected, actual, 0f)
                if (pass >= 3) {
                    before += oldTime
                    after += newTime
                }
            }
            Log.i(
                "OutputCopyBenchmark",
                "nestedMedianMs=${before.sorted()[before.size / 2] / 1e6} " +
                        "bufferMedianMs=${after.sorted()[after.size / 2] / 1e6}"
            )
        }
    }

    @Suppress("UNCHECKED_CAST")
    private suspend fun referenceOutput(
        tensor: OnnxTensor,
        channels: Int,
        height: Int,
        width: Int
    ): FloatArray = coroutineScope {
        val nested = tensor.value as Array<Array<Array<FloatArray>>>
        val output = FloatArray(channels * height * width)
        for (channel in 0 until channels) {
            for (y in 0 until height) {
                for (x in 0 until width) {
                    ensureActive()
                    output[channel * height * width + y * width + x] = nested[0][channel][y][x]
                }
            }
        }
        output
    }
}