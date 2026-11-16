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

package com.t8rin.imagetoolbox.feature.maze_generation.rendering

import com.t8rin.imagetoolbox.feature.maze_generation.domain.*

/** One scene drives the preview, raster export and SVG export. */
data class MazeScene(
    val minX: Float,
    val minY: Float,
    val width: Float,
    val height: Float,
    val walls: List<MazeWall>,
    val solution: List<MazePoint>,
    val fills: List<Pair<List<MazePoint>, Float>>,
    val start: MazePoint?,
    val end: MazePoint?,
    val style: MazeStyle,
)

object MazeSceneBuilder {
    fun build(
        result: MazeResult,
        solution: MazeSolution,
        recipe: MazeRecipe,
        passages: BooleanArray = result.passages,
        solutionProgress: Float = 1f,
    ): MazeScene {
        val g = result.graph
        val style = recipe.style
        require(g.cells.isNotEmpty()) { "Maze graph is empty" }
        require(result.passages.size == g.edges.size) {
            "Generated passage data does not match the graph"
        }
        require(passages.size == g.edges.size) { "Maze passage data does not match the graph" }
        require(solution.distances.size == g.cells.size) {
            "Maze solution data does not match the graph"
        }
        require(solution.distances.all { it >= 0 }) {
            "Maze solution distances contain unreachable cells"
        }
        require(solution.start in g.cells.indices && solution.end in g.cells.indices) {
            "Maze solution endpoint is outside the graph"
        }
        require(solution.path.isNotEmpty()) { "Maze solution path is empty" }
        require(solution.path.first() == solution.start && solution.path.last() == solution.end) {
            "Maze solution path endpoints do not match the solution"
        }
        require(solution.path.all { it in g.cells.indices }) {
            "Maze solution path is outside the graph"
        }
        val pathEdges = IntArray((solution.path.size - 1).coerceAtLeast(0)) { index ->
            val from = solution.path[index]
            val to = solution.path[index + 1]
            g.adjacency[from].firstOrNull {
                result.passages[it] && g.other(it, from) == to
            } ?: error("Maze solution contains a closed or missing passage")
        }
        style.validated()
        val points = g.cells.flatMap { it.outline }
        require(points.isNotEmpty() && points.all { it.x.isFinite() && it.y.isFinite() }) {
            "Maze geometry contains invalid coordinates"
        }
        val padding =
            style.margin +
                maxOf(
                    style.wallWidth / 2,
                    if (style.showSolution) style.solutionWidth / 2 else 0f,
                    if (style.showMarkers) style.markerSize else 0f,
                )
        val minX = points.minOf { it.x } - padding
        val minY = points.minOf { it.y } - padding
        val maxX = points.maxOf { it.x } + padding
        val maxY = points.maxOf { it.y } + padding
        val openingIds = mutableSetOf<Int>()
        fun opening(cell: Int, opposite: Int): MazePoint? {
            if (!recipe.openEntrances) return null
            // Prefer the boundary away from the other endpoint. Distinct openings for a single
            // cell.
            val target = g.cells[opposite].center
            val e =
                g.edges.indices
                    .filter { g.edges[it].a == cell && g.edges[it].b < 0 && it !in openingIds }
                    .maxByOrNull { i ->
                        val m = g.edges[i].wall.midpoint()
                        (m.x - target.x) * (m.x - target.x) + (m.y - target.y) * (m.y - target.y)
                    } ?: return null
            openingIds.add(e)
            return g.edges[e].wall.midpoint()
        }
        val entrance = opening(solution.start, solution.end)
        val exit = opening(solution.end, solution.start)
        val progress = solutionProgress.takeIf { it.isFinite() }?.coerceIn(0f, 1f) ?: 1f
        val route =
            if (style.showSolution)
                buildList {
                    entrance?.let(::add)
                    solution.path.forEachIndexed { index, cell ->
                        if (index > 0) add(g.edges[pathEdges[index - 1]].wall.midpoint())
                        add(g.cells[cell].center)
                    }
                    exit?.let(::add)
                }
            else emptyList()
        val maxDistance = solution.distances.maxOrNull()?.coerceAtLeast(1) ?: 1
        return MazeScene(
            minX,
            minY,
            maxX - minX,
            maxY - minY,
            g.edges.indices.filter { !passages[it] && it !in openingIds }.map { g.edges[it].wall },
            route.take((route.size * progress).toInt().coerceIn(0, route.size)),
            if (style.heatmap)
                g.cells.indices.map {
                    g.cells[it].outline to solution.distances[it].toFloat() / maxDistance
                }
            else emptyList(),
            if (style.showMarkers) g.cells[solution.start].center else null,
            if (style.showMarkers) g.cells[solution.end].center else null,
            style,
        )
    }
}
