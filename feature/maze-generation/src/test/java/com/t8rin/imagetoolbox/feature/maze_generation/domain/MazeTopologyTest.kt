/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.feature.maze_generation.domain

import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class MazeTopologyTest {
    @Test(timeout = 60000)
    fun allGridsHaveSymmetricFiniteConnectedGeometryAtLargeCoordinates() = runBlocking {
        for (topology in MazeTopology.entries) {
            val graph =
                MazeTopologyBuilder.build(
                    MazeRecipe(topology = topology, rows = 100, columns = 100)
                )
            graph.adjacency.forEachIndexed { c, edges ->
                edges.forEach { e ->
                    val other = graph.other(e, c)
                    assertTrue(other != c && other in graph.cells.indices)
                    assertTrue(e in graph.adjacency[other])
                }
            }
            graph.cells.forEach { c ->
                assertTrue(c.center.x.isFinite() && c.center.y.isFinite())
                assertTrue(c.outline.all { it.x.isFinite() && it.y.isFinite() })
            }
            val seen = mutableSetOf(0)
            val queue = ArrayDeque<Int>()
            queue.add(0)
            while (queue.isNotEmpty()) {
                val c = queue.removeFirst()
                graph.adjacency[c].forEach { e ->
                    val next = graph.other(e, c)
                    if (seen.add(next)) queue.add(next)
                }
            }
            assertEquals(topology.name, graph.cells.size, seen.size)
            assertTrue(graph.boundary.isNotEmpty())
            if (topology == MazeTopology.Triangular)
                assertTrue(graph.adjacency.all { it.size <= 3 })
            if (topology == MazeTopology.Hexagonal) assertTrue(graph.adjacency.all { it.size <= 6 })
        }
    }

    @Test
    fun disconnectedMasksRetainOnlyTheLargestIsland() = runBlocking {
        val graph =
            MazeTopologyBuilder.build(
                MazeRecipe(
                    topology = MazeTopology.Mask,
                    mask = MazeMask.Image,
                    rows = 3,
                    columns = 3,
                    imageMask = listOf(true, true, false, true, false, false, false, false, true),
                )
            )
        assertEquals(3, graph.cells.size)
    }

    @Test
    fun incompatibleRecipesAreRejected() {
        MazeAlgorithm.entries
            .filter { it.squareOnly }
            .forEach { algorithm ->
                try {
                    MazeRecipe(topology = MazeTopology.Hexagonal, algorithm = algorithm).validated()
                    fail(algorithm.name)
                } catch (expected: IllegalArgumentException) {
                    assertTrue(expected.message!!.contains("square"))
                }
            }
    }

    @Test
    fun invalidAndFutureRecipesCannotReachTheRenderer() {
        listOf(
                MazeRecipe(version = 2),
                MazeRecipe(rows = 0),
                MazeRecipe(braid = 101),
                MazeRecipe(style = MazeStyle(wallWidth = Float.NaN)),
                MazeRecipe(topology = MazeTopology.Mask, mask = MazeMask.Image),
            )
            .forEach {
                try {
                    it.validated()
                    fail("Invalid recipe accepted")
                } catch (expected: IllegalArgumentException) {}
            }
    }
}
