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

package com.t8rin.opencv_tools.photo_filters

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.CancellationException
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class ShockSharpeningTest {

    @Test
    fun upwindingPreservesLocalBoundsEvenWithExcessiveTimesteps() {
        val random = Random(18)
        for (width in 1..17) {
            val current = FloatArray(width * 13) { random.nextFloat() }
            val curvature = FloatArray(current.size) { random.nextFloat() - 0.5f }
            val output = FloatArray(current.size)
            ShockSharpening.evolve(width, current, curvature, output, timeStep = 100f) {}
            for (i in current.indices) {
                val x = i % width
                val y = i / width
                val neighbors = floatArrayOf(
                    current[i], current[y * width + max(0, x - 1)],
                    current[y * width + min(width - 1, x + 1)], current[max(0, y - 1) * width + x],
                    current[min(12, y + 1) * width + x]
                )
                assertTrue(output[i].isFinite())
                assertTrue(output[i] in neighbors.min()..neighbors.max())
            }
        }
    }

    @Test
    fun constantSignalAndZeroTimeStepAreUnchanged() {
        val constant = FloatArray(99) { 0.47f }
        val curvature = FloatArray(99) { if (it % 2 == 0) -1f else 1f }
        val result = FloatArray(99)
        ShockSharpening.evolve(11, constant, curvature, result) {}
        assertArrayEquals(constant, result, 0f)
        val signal = FloatArray(99) { it / 100f }
        ShockSharpening.evolve(11, signal, curvature, result, timeStep = -1f) {}
        assertArrayEquals(signal, result, 0f)
    }

    @Test
    fun zeroCurvatureDoesNotInventEdges() {
        val input = FloatArray(31) { it / 32f }
        val output = FloatArray(input.size)
        ShockSharpening.evolve(1, input, FloatArray(input.size), output) {}
        assertArrayEquals(input, output, 0f)
    }

    @Test(expected = CancellationException::class)
    fun evolutionChecksCancellationBeforeEachRow() {
        ShockSharpening.evolve(11, FloatArray(99), FloatArray(99), FloatArray(99)) {
            throw CancellationException()
        }
    }
}