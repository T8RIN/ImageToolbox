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

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

object MazeSolver {
    suspend fun solve(result: MazeResult, recipe: MazeRecipe): MazeSolution {
        val g = result.graph
        require(g.cells.isNotEmpty()) { "Maze graph is empty" }
        require(result.passages.size == g.edges.size) {
            "Maze passage data does not match the graph"
        }

        suspend fun bfs(start: Int): Pair<IntArray, IntArray> {
            require(start in g.cells.indices) { "Maze endpoint is outside the graph" }
            val distances = IntArray(g.cells.size) { -1 }
            val parent = IntArray(g.cells.size) { -1 }
            val queue = IntArray(g.cells.size)
            var head = 0
            var tail = 1
            queue[0] = start
            distances[start] = 0
            while (head < tail) {
                currentCoroutineContext().ensureActive()
                val cell = queue[head++]
                for (edge in g.adjacency[cell]) {
                    if (!result.passages[edge]) continue
                    val next = g.other(edge, cell)
                    if (distances[next] < 0) {
                        distances[next] = distances[cell] + 1
                        parent[next] = cell
                        queue[tail++] = next
                    }
                }
            }
            return distances to parent
        }

        val boundary = if (g.boundary.isEmpty()) intArrayOf(0) else g.boundary
        fun farthest(distances: IntArray): Int =
            boundary
                .filter { distances[it] >= 0 }
                .maxByOrNull { distances[it] }
                ?: error("No reachable boundary cell")

        val random = MazeRandom(recipe.seed xor 0x735a2d)
        var start = boundary.first()
        var end = boundary.last()
        when (recipe.entrance) {
            MazeEntrance.Farthest -> {
                start = farthest(bfs(start).first)
                end = farthest(bfs(start).first)
            }
            MazeEntrance.Opposite -> {
                var greatestDistance = -1.0
                for (i in boundary.indices) {
                    currentCoroutineContext().ensureActive()
                    val first = g.cells[boundary[i]].center
                    for (j in i + 1 until boundary.size) {
                        val second = g.cells[boundary[j]].center
                        val dx = first.x.toDouble() - second.x
                        val dy = first.y.toDouble() - second.y
                        val distance = dx * dx + dy * dy
                        if (distance > greatestDistance) {
                            greatestDistance = distance
                            start = boundary[i]
                            end = boundary[j]
                        }
                    }
                }
            }
            MazeEntrance.Random -> {
                start = boundary[random.next(boundary.size)]
                val candidates = boundary.filter { it != start }
                end = if (candidates.isEmpty()) start else random.pick(candidates)
            }
            MazeEntrance.Center -> {
                val cx = g.cells.map { it.center.x }.average()
                val cy = g.cells.map { it.center.y }.average()
                start =
                    g.cells.indices.minBy { i ->
                        val center = g.cells[i].center
                        (center.x - cx) * (center.x - cx) + (center.y - cy) * (center.y - cy)
                    }
                end = farthest(bfs(start).first)
            }
            MazeEntrance.Manual -> {
                start = recipe.startCell.coerceIn(g.cells.indices)
                end = recipe.endCell.coerceIn(g.cells.indices)
            }
        }

        val (distances, parent) = bfs(start)
        check(distances.all { it >= 0 }) { "Generated maze is disconnected" }
        check(distances[end] >= 0) { "Selected maze endpoints are not connected" }

        val reversedPath = ArrayList<Int>(distances[end] + 1)
        var current = end
        while (current != start) {
            reversedPath.add(current)
            current = parent[current]
            check(current >= 0) { "Could not reconstruct the maze solution" }
        }
        reversedPath.add(start)
        reversedPath.reverse()

        return MazeSolution(start, end, reversedPath.toIntArray(), distances)
    }

    fun statistics(result: MazeResult, solution: MazeSolution): MazeStatistics {
        val degrees =
            result.graph.adjacency.map { adjacent -> adjacent.count { result.passages[it] } }
        return MazeStatistics(
            degrees.size,
            result.passages.count { it },
            degrees.count { it == 1 },
            degrees.count { it >= 3 },
            (solution.path.size - 1).coerceAtLeast(0),
        )
    }
}
