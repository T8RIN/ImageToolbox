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

class PatternCollagesTest {
    @Test
    fun everyLayoutHasDistinctUsableSlots() {
        val templates = PatternCollages.templates
        assertEquals(200, templates.size)
        assertEquals(200, templates.map { it.id }.toSet().size)
        val existing = (CreativeCollages.templates.map { it.design } +
                ArtisticCollages.templates.map { it.design } +
                DecorativeCollages.templates.map { it.design }).toSet()
        val designs = mutableSetOf<CollageDesign>()
        templates.forEach {
            assertTrue(it.id, it.design.isValid())
            assertTrue(it.id, it.design.slots.all { slot -> slot.area > .001f })
            assertTrue(it.id, designs.add(it.design) && it.design !in existing)
        }
    }

    @Test
    fun newLayoutsCoverOneToFifteenPhotos() {
        val counts = PatternCollages.templates.groupingBy { it.design.slots.size }.eachCount()
        assertEquals((1..15).toSet(), counts.keys)
        assertEquals(4, counts[1])
        (2..15).forEach { assertEquals(14, counts[it]) }
    }

    @Test
    fun tessellationsFillTheCanvas() {
        PatternCollages.templates.filter { template ->
            listOf("aperture", "sunflower", "stainedglass", "dunes").any { it in template.id } ||
                    "courtyard" in template.id && template.design.slots.size > 2
        }.forEach { template ->
            assertEquals(
                template.id,
                1.0,
                template.design.slots.sumOf { it.area.toDouble() },
                .0001
            )
        }
    }
}