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

package com.t8rin.neural_tools.search

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class ClipEmbeddingTest {

    @Test
    fun normalizesToUnitLengthWithoutChangingDirection() {
        val vector = FloatArray(512).apply {
            this[0] = 3f
            this[1] = -4f
        }.normalizeClipEmbedding()
        assertEquals(0.6f, vector[0], 0.000001f)
        assertEquals(-0.8f, vector[1], 0.000001f)
        assertEquals(1.0, vector.sumOf { it.toDouble() * it }, 0.000001)
    }

    @Test
    fun largeFiniteValuesDoNotOverflowTheNorm() {
        val vector = FloatArray(512) { Float.MAX_VALUE }.normalizeClipEmbedding()
        assertTrue(vector.all { it.isFinite() && it > 0f })
        assertEquals(1.0, vector.sumOf { it.toDouble() * it }, 0.000001)
    }

    @Test
    fun rejectsInvalidOutputsInsteadOfStoringUnusableEmbeddings() {
        listOf(
            FloatArray(512),
            FloatArray(511) { 1f },
            FloatArray(512) { Float.NaN },
            FloatArray(512) { Float.POSITIVE_INFINITY }
        ).forEach { vector ->
            assertThrows(IllegalStateException::class.java) { vector.normalizeClipEmbedding() }
        }
    }
}