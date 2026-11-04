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
import org.junit.Assert.assertEquals
import org.junit.Test

class ScalarMapSamplerTest {

    @Test
    fun singletonAndConstantMapsRemainConstant() {
        for ((width, height) in listOf(1 to 1, 1 to 7, 9 to 1, 7 to 11)) {
            val sampler = ScalarMapSampler(FloatArray(width * height) { 0.375f }, width, height)
            val row = FloatArray(17)
            for (y in 0 until 23) {
                sampler.readRow(y, 23, row)
                assertArrayEquals(FloatArray(row.size) { 0.375f }, row, 1e-7f)
            }
        }
    }

    @Test
    fun oneDimensionalMapsUsePixelCentersAndClampAtEdges() {
        val expected = floatArrayOf(0f, 0.1f, 0.5f, 0.9f, 1f)
        val horizontal = ScalarMapSampler(floatArrayOf(0f, 1f), 2, 1)
        val row = FloatArray(5)
        horizontal.readRow(0, 1, row)
        assertArrayEquals(expected, row, 1e-7f)
        val vertical = ScalarMapSampler(floatArrayOf(0f, 1f), 1, 2)
        for (y in expected.indices) {
            vertical.readRow(y, 5, row)
            assertArrayEquals(FloatArray(5) { expected[y] }, row, 1e-7f)
        }
    }

    @Test
    fun oddOutputInterpolatesBothAxes() {
        val sampler = ScalarMapSampler(floatArrayOf(0f, 1f, 2f, 3f), 2, 2)
        val row = FloatArray(3)
        for (y in 0 until 3) {
            sampler.readRow(y, 3, row)
            assertArrayEquals(floatArrayOf(y.toFloat(), y + 0.5f, y + 1f), row, 1e-7f)
        }
    }

    @Test
    fun sameSizeAndSinglePixelOutputPreserveTheirSamples() {
        val values = FloatArray(15) { it.toFloat() }
        val sampler = ScalarMapSampler(values, 3, 5)
        val row = FloatArray(3)
        for (y in 0 until 5) {
            sampler.readRow(y, 5, row)
            assertArrayEquals(values.copyOfRange(y * 3, y * 3 + 3), row, 0f)
        }
        val center = FloatArray(1)
        sampler.readRow(0, 1, center)
        assertEquals(values[7], center[0], 0f)
    }
}