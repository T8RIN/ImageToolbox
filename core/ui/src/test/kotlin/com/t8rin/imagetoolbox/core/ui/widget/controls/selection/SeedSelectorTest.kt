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

package com.t8rin.imagetoolbox.core.ui.widget.controls.selection

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.roundToInt
import kotlin.random.Random

class SeedSelectorTest {

    @Test
    fun randomIntegerSeedStaysInRangeAndChangesValue() {
        val range = -10f..10f
        var seed = 0f
        val random = Random(42)

        repeat(100) {
            val next = random.seed(seed, range, roundTo = 0)

            assertTrue(next in range)
            assertEquals(next.toInt().toFloat(), next, 0f)
            assertNotEquals(seed, next)
            seed = next
        }
    }

    @Test
    fun randomDecimalSeedKeepsRequestedPrecision() {
        val range = 0f..3.1415927f
        var seed = 2f
        val random = Random(42)

        repeat(100) {
            val next = random.seed(seed, range, roundTo = 3)

            assertTrue(next in range)
            assertEquals((next * 1000).roundToInt().toFloat(), next * 1000, 0.001f)
            assertNotEquals(seed, next)
            seed = next
        }
    }

    @Test
    fun singleValueRangeReturnsThatValue() {
        assertEquals(
            5f,
            Random.seed(5f, 5f..5f, roundTo = 0),
            0f
        )
    }
}
