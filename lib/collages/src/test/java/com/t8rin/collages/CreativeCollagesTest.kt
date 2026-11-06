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

package com.t8rin.collages

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CreativeCollagesTest {
    @Test
    fun catalogContainsTwoHundredDistinctValidCompositions() {
        val templates = CreativeCollages.templates
        assertEquals(200, templates.size)
        assertEquals(200, templates.map { it.id }.distinct().size)
        assertEquals(200, templates.map { it.design }.distinct().size)
        templates.forEach { assertTrue(it.id, it.design.isValid()) }
    }

    @Test
    fun everyFamilySupportsTwoThroughTwentyPhotos() {
        CreativeCollages.Family.entries.forEach { family ->
            val counts = CreativeCollages.templates.filter { it.family == family }
                .map { it.design.slots.size }.toSet()
            assertEquals(family.name, (2..20).toSet(), counts)
        }
    }

    @Test
    fun splittingPreservesAreaAndOtherSlots() {
        CreativeCollages.templates.filter { it.design.slots.size < 20 }.forEach { template ->
            val original = template.design
            listOf(true, false).forEach { vertical ->
                val split = original.split(0, vertical, .5f) ?: error(template.id)
                assertTrue(split.isValid())
                assertEquals(original.slots.size + 1, split.slots.size)
                assertEquals(original.slots.drop(1), split.slots.drop(2))
                assertEquals(
                    template.id,
                    original.slots[0].area,
                    split.slots[0].area + split.slots[1].area,
                    .0001f
                )
            }
        }
    }

    @Test
    fun rectangularSplitsKeepExactSharedEdges() {
        CreativeCollages.templates.filter { it.family == CreativeCollages.Family.Weave }
            .forEach { template ->
                template.design.slots.forEach { slot ->
                    assertEquals(
                        template.id,
                        setOf(
                            CollagePoint(slot.left, slot.top),
                            CollagePoint(slot.right, slot.top),
                            CollagePoint(slot.right, slot.bottom),
                            CollagePoint(slot.left, slot.bottom)
                        ),
                        slot.points.toSet()
                    )
                }
            }
    }

    @Test
    fun rectangularLayoutsFillTheCanvas() {
        CreativeCollages.templates.filter {
            it.family == CreativeCollages.Family.Weave ||
                    it.family == CreativeCollages.Family.Editorial
        }.forEach { template ->
            assertEquals(
                template.id,
                1.0,
                template.design.slots.sumOf { it.area.toDouble() },
                .00001
            )
        }
    }

}