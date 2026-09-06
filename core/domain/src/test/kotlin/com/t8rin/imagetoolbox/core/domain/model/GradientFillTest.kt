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

package com.t8rin.imagetoolbox.core.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GradientFillTest {
    @Test
    fun directionUsesTheFullNonSquareCanvas() {
        val expected = listOf(
            GradientLine(0f, 50f, 200f, 50f),
            GradientLine(100f, 0f, 100f, 100f),
            GradientLine(200f, 50f, 0f, 50f),
            GradientLine(100f, 100f, 100f, 0f)
        )
        for ((index, line) in expected.withIndex()) {
            val actual = GradientFill(angle = index * 90f).lineFor(200f, 100f)
            assertEquals(line.startX, actual.startX, 0.001f)
            assertEquals(line.startY, actual.startY, 0.001f)
            assertEquals(line.endX, actual.endX, 0.001f)
            assertEquals(line.endY, actual.endY, 0.001f)
        }
    }

    @Test
    fun diagonalSpansCornerProjectionsAndHonorsOrigin() {
        val line = GradientFill(angle = 45f).lineFor(200f, 100f, 10f, 20f)
        assertEquals(30f, line.startX + line.startY, 0.001f)
        assertEquals(330f, line.endX + line.endY, 0.001f)
    }

    @Test
    fun invalidAngleAndEmptyBoundsStillProduceFiniteNonzeroLine() {
        val line = GradientFill(angle = Float.NaN).lineFor(0f, 0f)
        assertTrue(line.startX.isFinite() && line.endX.isFinite())
        assertTrue(line.startX < line.endX)
    }
}