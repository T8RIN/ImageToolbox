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

package com.t8rin.collages.handles

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.hypot

class CollageHandleBoundsTest {
    @Test
    fun controlsAndTouchAreasDoNotOverlapOnSmallSlots() {
        for (scale in listOf(1f, 1.5f, 4f, 10f)) {
            val handles =
                collageHandleBounds(listOf(0f to 35f, 35f to 0f, 35f to 70f), 126f, 72f, scale)
            handles.forEachIndexed { index, handle ->
                handles.drop(index + 1).forEach { other ->
                    val distance =
                        hypot(handle.centerX - other.centerX, handle.centerY - other.centerY)
                    assertTrue((handle.diameter + other.diameter) / 2 < distance)
                    assertTrue(handle.touchRadius + other.touchRadius < distance)
                }
            }
        }
    }

    @Test
    fun zoomDoesNotEnlargeControlsBeyondTheirScreenSize() {
        for (scale in listOf(1f, 2f, 10f)) {
            val handle = collageHandleBounds(listOf(500f to 500f), 126f, 72f, scale).single()
            assertEquals(126f, handle.diameter * scale, .001f)
            assertEquals(72f, handle.touchRadius * scale, .001f)
        }
    }
}