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

class DecorativeCollagesTest {
    @Test
    fun everyTemplateHasDistinctUsableGeometry() {
        val templates = DecorativeCollages.templates
        assertEquals(176, templates.size)
        assertEquals(templates.size, templates.map { it.id }.toSet().size)
        assertEquals(templates.size, templates.map { it.design }.toSet().size)
        templates.forEach { template ->
            assertTrue(template.id, template.design.isValid())
            assertTrue(template.id, template.design.slots.all { it.area > .001f })
        }
    }

    @Test
    fun newLayoutsAreLimitedToFifteenPhotos() {
        val counts = DecorativeCollages.templates.groupingBy { it.design.slots.size }.eachCount()
        assertEquals((1..15).toSet(), counts.keys)
        assertEquals(8, counts[1])
        (2..15).forEach { assertEquals(12, counts[it]) }
    }
}