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

import com.t8rin.imagetoolbox.feature.maze_generation.rendering.MazeSceneBuilder
import kotlinx.coroutines.*
import org.junit.Assert.*
import org.junit.Test

class MazeGeneratorTest {
    private fun reachable(g: MazeGraph, passages: BooleanArray): Set<Int> {
        val seen = mutableSetOf(0)
        val q = ArrayDeque<Int>()
        q.add(0)
        while (q.isNotEmpty()) {
            val c = q.removeFirst()
            for (e in g.adjacency[c]) if (passages[e]) {
                val next = g.other(e, c)
                if (seen.add(next)) q.add(next)
            }
        }
        return seen
    }

    @Test(timeout = 120000)
    fun everySupportedAlgorithmAndTopologyIsAPerfectReproducibleMaze() = runBlocking {
        for (topology in MazeTopology.entries) for (algorithm in MazeAlgorithm.entries) {
            if (!algorithm.supports(topology)) continue
            for (seed in listOf(0, 42, -7919)) {
                val recipe =
                    MazeRecipe(
                        topology = topology,
                        algorithm = algorithm,
                        rows = 5,
                        columns = 6,
                        seed = seed,
                    )
                val result = MazeGenerator.generate(recipe, captureEvents = true)
                val label = "$topology / $algorithm / $seed"
                assertEquals(
                    label,
                    result.graph.cells.size,
                    reachable(result.graph, result.passages).size,
                )
                assertEquals(label, result.graph.cells.size - 1, result.passages.count { it })
                val again = MazeGenerator.generate(recipe, captureEvents = true)
                assertArrayEquals(label, result.passages, again.passages)
                assertArrayEquals(label, result.events, again.events)
                val solution = MazeSolver.solve(result, recipe)
                assertEquals(solution.start, solution.path.first())
                assertEquals(solution.end, solution.path.last())
                assertTrue(solution.distances.all { it >= 0 })
                solution.path.toList().zipWithNext().forEach { (a, b) ->
                    assertTrue(
                        result.graph.adjacency[a].any {
                            result.passages[it] && result.graph.other(it, a) == b
                        }
                    )
                }
                val replay = result.initialPassages.copyOf()
                result.events.forEach { e -> replay[kotlin.math.abs(e) - 1] = e > 0 }
                assertArrayEquals(label, result.passages, replay)
            }
        }
    }

    @Test(timeout = 60000)
    fun braidNeverDisconnectsOrClosesPassages() = runBlocking {
        for (topology in MazeTopology.entries) {
            val recipe = MazeRecipe(topology = topology, rows = 8, columns = 9)
            val tree = MazeGenerator.generate(recipe, captureEvents = true)
            val braided = MazeGenerator.generate(recipe.copy(braid = 100))
            tree.passages.indices.forEach {
                if (tree.passages[it]) assertTrue(braided.passages[it])
            }
            assertEquals(braided.graph.cells.size, reachable(braided.graph, braided.passages).size)
            assertTrue(braided.passages.count { it } >= tree.passages.count { it })
        }
    }

    @Test(timeout = 60000)
    fun algorithmParameterExtremesPreserveTheTreeInvariant() = runBlocking {
        val recipes = buildList {
            for (bias in listOf(0, 100)) {
                for (direction in MazeDirection.entries) add(
                    MazeRecipe(
                        algorithm = MazeAlgorithm.BinaryTree,
                        direction = direction,
                        horizontalBias = bias,
                    )
                )
                add(MazeRecipe(algorithm = MazeAlgorithm.Sidewinder, horizontalBias = bias))
                add(MazeRecipe(algorithm = MazeAlgorithm.Eller, ellerJoin = bias, ellerDrop = bias))
                add(MazeRecipe(algorithm = MazeAlgorithm.Houston, houstonSwitch = bias))
                add(
                    MazeRecipe(
                        algorithm = MazeAlgorithm.RecursiveDivision,
                        divisionRoom = 20,
                        horizontalBias = bias,
                    )
                )
            }
            for (weights in
                listOf(
                    listOf(0, 0, 0, 0),
                    listOf(100, 0, 0, 0),
                    listOf(0, 100, 0, 0),
                    listOf(0, 0, 100, 0),
                    listOf(0, 0, 0, 100),
                )) {
                add(
                    MazeRecipe(
                        algorithm = MazeAlgorithm.GrowingTree,
                        newestWeight = weights[0],
                        randomWeight = weights[1],
                        oldestWeight = weights[2],
                        middleWeight = weights[3],
                    )
                )
            }
        }
        for (recipe in recipes) {
            val result = MazeGenerator.generate(recipe, captureEvents = true)
            assertEquals(result.graph.cells.size, reachable(result.graph, result.passages).size)
            assertEquals(result.graph.cells.size - 1, result.passages.count { it })
        }
    }

    @Test
    fun singleCellImageMaskIsValidForEveryGraphAlgorithm() = runBlocking {
        for (algorithm in MazeAlgorithm.entries.filterNot { it.squareOnly }) {
            val recipe =
                MazeRecipe(
                    topology = MazeTopology.Mask,
                    algorithm = algorithm,
                    mask = MazeMask.Image,
                    rows = 2,
                    columns = 2,
                    imageMask = listOf(true, false, false, false),
                )
            val maze = MazeGenerator.generate(recipe, captureEvents = true)
            assertEquals(1, maze.graph.cells.size)
            assertEquals(0, maze.passages.count { it })
            assertArrayEquals(intArrayOf(0), MazeSolver.solve(maze, recipe).path)
        }
    }

    @Test
    fun solutionRouteIsOptionalAndOppositeEndpointsAreGeometricallySeparated() = runBlocking {
        val recipe =
            MazeRecipe(
                rows = 6,
                columns = 6,
                entrance = MazeEntrance.Opposite,
                style = MazeStyle(showSolution = false),
            )
        val maze = MazeGenerator.generate(recipe, captureEvents = true)
        val solution = MazeSolver.solve(maze, recipe)
        val hidden = MazeSceneBuilder.build(maze, solution, recipe)
        assertTrue(hidden.solution.isEmpty())

        val visibleRecipe = recipe.copy(style = recipe.style.copy(showSolution = true))
        val visible = MazeSceneBuilder.build(maze, solution, visibleRecipe)
        assertTrue(visible.solution.size >= solution.path.size)

        val start = maze.graph.cells[solution.start].center
        val end = maze.graph.cells[solution.end].center
        val dx = start.x - end.x
        val dy = start.y - end.y
        assertTrue(dx * dx + dy * dy >= 25f)
    }

    @Test
    fun solverRejectsDisconnectedPassageGraphsInsteadOfReturningAFakeOneCellPath() = runBlocking {
        val cells =
            listOf(
                MazeCell(MazePoint(0f, 0f), listOf(MazePoint(-0.5f, -0.5f))),
                MazeCell(MazePoint(5f, 0f), listOf(MazePoint(4.5f, -0.5f))),
            )
        val graph =
            MazeGraph(
                cells,
                listOf(
                    MazeEdge(0, -1, MazeWall.Line(MazePoint(-0.5f, -0.5f), MazePoint(0f, -0.5f))),
                    MazeEdge(1, -1, MazeWall.Line(MazePoint(4.5f, -0.5f), MazePoint(5f, -0.5f))),
                ),
            )
        val result =
            MazeResult(
                graph,
                BooleanArray(graph.edges.size),
                intArrayOf(),
                BooleanArray(graph.edges.size),
                0L,
            )
        try {
            MazeSolver.solve(result, MazeRecipe())
            fail("Disconnected graph was accepted")
        } catch (expected: IllegalStateException) {
            assertTrue(expected.message!!.contains("disconnected"))
        }
    }

    @Test
    fun productionGenerationDoesNotRetainAnimationDeltas() = runBlocking {
        val recipe = MazeRecipe(algorithm = MazeAlgorithm.OriginShift, rows = 5, columns = 5)
        val normal = MazeGenerator.generate(recipe)
        val captured = MazeGenerator.generate(recipe, captureEvents = true)
        assertTrue(normal.events.isEmpty())
        assertTrue(captured.events.isNotEmpty())
        assertArrayEquals(normal.passages, captured.passages)
    }

    @Test
    fun xorshiftStreamHasAFrozenGoldenSequence() {
        val random = MazeRandom(1)
        assertArrayEquals(
            intArrayOf(270369, 634689, 435461, 599695, 689233),
            IntArray(5) { random.next(1000000) },
        )
    }

    @Test
    fun cancellationIsCooperative() = runBlocking {
        val job = Job()
        job.cancel()
        try {
            withContext(job) {
                MazeGenerator.generate(
                    MazeRecipe(algorithm = MazeAlgorithm.AldousBroder, rows = 100, columns = 100)
                )
            }
            fail("Cancelled generation returned a result")
        } catch (expected: CancellationException) {
            assertTrue(job.isCancelled)
        }
    }

    @Test
    fun reverseDeleteRejectsOversizedInputs() = runBlocking {
        try {
            MazeGenerator.generate(
                MazeRecipe(algorithm = MazeAlgorithm.ReverseDelete, rows = 41, columns = 40)
            )
            fail("Oversized Reverse Delete was accepted")
        } catch (expected: IllegalArgumentException) {
            assertTrue(expected.message!!.contains("1600"))
        }
    }
}
