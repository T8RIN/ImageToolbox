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

package com.t8rin.collages.layout.generator

import com.t8rin.collages.layout.CollageLayoutStore
import com.t8rin.collages.model.CollageSlot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProceduralCollageLayoutsTest {
    private val layouts by lazy {
        (1..100).associateWith(ProceduralCollageLayouts::generate)
    }

    @Test
    fun addsOneThousandStableLayoutsWithMoreChoicesForSparseCounts() {
        assertEquals(1000, layouts.values.sumOf { it.size })
        assertEquals(6000, CollageLayoutStore.layoutCount)
        assertEquals(0, CollageLayoutStore.getLayoutCount(0))
        assertEquals(0, CollageLayoutStore.getLayoutCount(101))
        layouts.forEach { (count, entries) ->
            assertEquals(entries, ProceduralCollageLayouts.generate(count))
            assertEquals(
                CollageLayoutStore.counts[count - 1] + entries.size,
                CollageLayoutStore.getLayoutCount(count)
            )
            assertTrue(entries.values.all { it.slots.size == count })
        }
        assertTrue(layouts.getValue(51).size > layouts.getValue(1).size)
        assertEquals(1000, layouts.values.flatMap { it.keys }.toSet().size)
    }

    @Test
    fun everySlotHasUsableAreaAndStaysInsideTheCanvas() {
        layouts.forEach { (count, entries) ->
            entries.forEach { (id, design) ->
                assertTrue(id, design.isValid())
                design.slots.forEach { slot ->
                    assertTrue("$id area=${slot.area}", slot.area > .12f / count)
                    val ratio = (slot.right - slot.left) / (slot.bottom - slot.top)
                    assertTrue("$id ratio=$ratio", ratio in .15f..6.67f)
                }
            }
        }
    }

    @Test
    fun partitionsCoverTheCanvasAndSlotsDoNotOverlap() {
        layouts.forEach { (count, entries) ->
            entries.values.forEachIndexed { variant, design ->
                val fillsCanvas = count > 1 && variant in listOf(0, 1, 2, 4, 6, 8, 10)
                if (fillsCanvas) {
                    assertEquals(
                        "$count:$variant", 1.0,
                        design.slots.sumOf { it.area.toDouble() }, .00003
                    )
                }
                for (row in 0 until 31) for (column in 0 until 31) {
                    val x = (column + .371f) / 31
                    val y = (row + .613f) / 31
                    val hits = design.slots.count { it.contains(x, y) }
                    assertTrue("$count:$variant overlap at $x,$y", hits <= 1)
                    if (fillsCanvas) assertEquals("$count:$variant gap at $x,$y", 1, hits)
                }
            }
        }
    }

    @Test
    fun noTwoVariantsDescribeTheSameGeometry() {
        layouts.values.forEach { entries ->
            val signatures = entries.values.map { design ->
                design.slots.map { slot ->
                    slot.points.map { it.x to it.y }.toSet()
                }.toSet()
            }
            assertEquals(entries.size, signatures.toSet().size)
        }
    }

    private fun CollageSlot.contains(x: Float, y: Float): Boolean {
        if (x < left || x > right || y < top || y > bottom) return false
        var inside = false
        points.indices.forEach { index ->
            val a = points[index]
            val b = points[(index + 1) % points.size]
            if ((a.y > y) != (b.y > y) &&
                x < (b.x - a.x) * (y - a.y) / (b.y - a.y) + a.x
            ) {
                inside = !inside
            }
        }
        return inside
    }
}