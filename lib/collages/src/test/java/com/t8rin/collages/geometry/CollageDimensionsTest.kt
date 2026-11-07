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

package com.t8rin.collages.geometry

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CollageDimensionsTest {

    @Test
    fun outputUsesSourceSizeAndAspectRatio() {
        assertEquals(3000 to 2000, collageOutputDimensions(6000, 1.5f, 0.5f, 100_000_000))
        assertEquals(2000 to 3000, collageOutputDimensions(6000, 2f / 3f, 0.5f, 100_000_000))
    }

    @Test
    fun outputRespectsMemoryAndEdgeLimits() {
        val (width, height) = collageOutputDimensions(20000, 2f, 1f, 8_000_000)
        assertTrue(width <= 8192 && height <= 8192)
        assertTrue(width.toLong() * height <= 8_000_000)
        assertEquals(2f, width.toFloat() / height, 0.001f)
    }

    @Test
    fun largeCollagesGiveMoreDetailToLargerSlotsWithinABoundedBudget() {
        val areas = listOf(.25f) + List(99) { .75f / 99 }
        val sizes = areas.map { collagePreviewSize(100, it, false) }
        assertTrue(sizes.first() > sizes.last())
        assertTrue(sizes.sumOf { it.toLong() * it } <= 20L * 1024 * 1024)
        assertTrue(sizes.all { it in 256..1600 })
    }
}