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

import kotlin.math.cos
import kotlin.math.sin

/** Coordinates use cell units. A shared wall has exactly one edge, in both renderers. */
data class MazePoint(val x: Float, val y: Float)

sealed interface MazeWall {
    data class Line(val from: MazePoint, val to: MazePoint) : MazeWall

    data class Arc(val center: MazePoint, val radius: Float, val start: Float, val sweep: Float) :
        MazeWall
}

data class MazeEdge(val a: Int, val b: Int, val wall: MazeWall)

data class MazeCell(val center: MazePoint, val outline: List<MazePoint>)

class MazeGraph(val cells: List<MazeCell>, val edges: List<MazeEdge>) {
    val adjacency: Array<IntArray> = run {
        val lists = Array(cells.size) { mutableListOf<Int>() }
        edges.forEachIndexed { index, edge ->
            if (edge.b >= 0) {
                lists[edge.a].add(index)
                lists[edge.b].add(index)
            }
        }
        Array(cells.size) { lists[it].toIntArray() }
    }
    val boundary: IntArray = edges.filter { it.b < 0 }.map { it.a }.distinct().toIntArray()

    fun other(edge: Int, cell: Int): Int = edges[edge].let { if (it.a == cell) it.b else it.a }
}

data class MazeResult(
    val graph: MazeGraph,
    val passages: BooleanArray,
    /** Signed edge index + 1: carve positive, close negative. No full graph snapshots. */
    val events: IntArray,
    val initialPassages: BooleanArray,
    val elapsedMillis: Long,
)

data class MazeSolution(val start: Int, val end: Int, val path: IntArray, val distances: IntArray)

data class MazeStatistics(
    val cells: Int,
    val passages: Int,
    val deadEnds: Int,
    val junctions: Int,
    val solutionLength: Int,
)

fun MazeWall.midpoint(): MazePoint =
    when (this) {
        is MazeWall.Line -> MazePoint((from.x + to.x) / 2, (from.y + to.y) / 2)
        is MazeWall.Arc -> {
            val angle = Math.toRadians((start + sweep / 2).toDouble())
            MazePoint(
                center.x + radius * cos(angle).toFloat(),
                center.y + radius * sin(angle).toFloat(),
            )
        }
    }
