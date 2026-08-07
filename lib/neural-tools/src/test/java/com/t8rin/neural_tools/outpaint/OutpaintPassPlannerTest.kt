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

package com.t8rin.neural_tools.outpaint

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OutpaintPassPlannerTest {
    private val planner = OutpaintPassPlanner(maxDepth = 96)

    @Test
    fun `no expansion returns no passes`() {
        assertTrue(planner.plan(320, 240, 320, 240, 0, 0).isEmpty())
    }

    @Test
    fun `right expansion is split into bounded layers`() {
        val passes = planner.plan(500, 200, 200, 200, 0, 0)
        assertEquals(4, passes.size)
        assertTrue(passes.all { it.left == null && it.top == null && it.bottom == null })
        assertEquals(500, passes.last().knownAfter.right)
    }

    @Test
    fun `asymmetric expansion reaches every target edge`() {
        val passes = planner.plan(701, 509, 213, 127, 73, 41)
        val finalRect = passes.last().knownAfter
        assertEquals(OutpaintRect(0, 0, 701, 509), finalRect)
        assertTrue(passes.flatMap(OutpaintPass::regions).all { it.width > 0 && it.height > 0 })
    }

    @Test
    fun `all four sides are planned`() {
        val first = planner.plan(400, 400, 200, 200, 100, 100).first()
        assertTrue(first.left != null)
        assertTrue(first.right != null)
        assertTrue(first.top != null)
        assertTrue(first.bottom != null)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `source outside target is rejected`() {
        planner.plan(200, 200, 150, 150, 100, 0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `negative offset is rejected`() {
        planner.plan(200, 200, 100, 100, -1, 0)
    }
}
