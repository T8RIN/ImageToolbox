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

import kotlin.math.*
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

object MazeTopologyBuilder {
    suspend fun build(recipe: MazeRecipe): MazeGraph {
        recipe.validated()
        if (recipe.topology == MazeTopology.Polar) return polar(recipe.rows)
        val cells = mutableListOf<MazeCell>()
        val edges = mutableListOf<MazeEdge>()
        val shared = mutableMapOf<String, Int>()
        fun polygon(points: List<MazePoint>) {
            val id = cells.size
            cells.add(
                MazeCell(
                    MazePoint(
                        points.sumOf { it.x.toDouble() }.toFloat() / points.size,
                        points.sumOf { it.y.toDouble() }.toFloat() / points.size,
                    ),
                    points,
                )
            )
            fun key(p: MazePoint) = "${(p.x * 10000).roundToInt()},${(p.y * 10000).roundToInt()}"
            points.indices.forEach { i ->
                val a = points[i]
                val b = points[(i + 1) % points.size]
                val ka = key(a)
                val kb = key(b)
                val k = if (ka < kb) "$ka:$kb" else "$kb:$ka"
                val old = shared.remove(k)
                if (old == null) {
                    shared[k] = edges.size
                    edges.add(MazeEdge(id, -1, MazeWall.Line(a, b)))
                } else edges[old] = edges[old].copy(b = id)
            }
        }
        val retained = if (recipe.topology == MazeTopology.Mask) mask(recipe) else null
        for (r in 0 until recipe.rows) {
            currentCoroutineContext().ensureActive()
            for (c in 0 until recipe.columns) {
                val x = c.toFloat()
                val y = r.toFloat()
                fun p(a: Float, b: Float) = MazePoint(a, b)
                when (recipe.topology) {
                    MazeTopology.Square,
                    MazeTopology.Mask ->
                        if (retained == null || retained[r * recipe.columns + c]) {
                            polygon(listOf(p(x, y), p(x + 1, y), p(x + 1, y + 1), p(x, y + 1)))
                        }
                    MazeTopology.Rhombic -> {
                        val xx = x + y * 0.5f
                        val yy = y * 0.8660254f
                        val bottom = (r + 1) * 0.8660254f
                        polygon(
                            listOf(
                                p(xx, yy),
                                p(xx + 1, yy),
                                p(xx + 1.5f, bottom),
                                p(xx + 0.5f, bottom),
                            )
                        )
                    }
                    MazeTopology.Triangular -> {
                        val xx = x * 0.5f
                        val yy = y * 0.8660254f
                        val bottom = (r + 1) * 0.8660254f
                        polygon(
                            if ((r + c) % 2 == 0)
                                listOf(p(xx, bottom), p(xx + 0.5f, yy), p(xx + 1, bottom))
                            else listOf(p(xx, yy), p(xx + 1, yy), p(xx + 0.5f, bottom))
                        )
                    }
                    MazeTopology.Hexagonal -> {
                        val xx = x * 1.5f
                        val unit = sqrt(3f) / 2
                        val yy = 2 * r + c % 2
                        polygon(
                            listOf(
                                p(xx + 1, yy * unit),
                                p(xx + 0.5f, (yy + 1) * unit),
                                p(xx - 0.5f, (yy + 1) * unit),
                                p(xx - 1, yy * unit),
                                p(xx - 0.5f, (yy - 1) * unit),
                                p(xx + 0.5f, (yy - 1) * unit),
                            )
                        )
                    }
                    MazeTopology.Upsilon -> {
                        // Truncated square tiling: octagons at lattice vertices, diamonds in gaps.
                        val t = 0.29289323f
                        polygon(
                            listOf(
                                p(x + t, y),
                                p(x + 1 - t, y),
                                p(x + 1, y + t),
                                p(x + 1, y + 1 - t),
                                p(x + 1 - t, y + 1),
                                p(x + t, y + 1),
                                p(x, y + 1 - t),
                                p(x, y + t),
                            )
                        )
                        if (r < recipe.rows - 1 && c < recipe.columns - 1) {
                            polygon(
                                listOf(
                                    p(x + 1, y + 1 - t),
                                    p(x + 1 + t, y + 1),
                                    p(x + 1, y + 1 + t),
                                    p(x + 1 - t, y + 1),
                                )
                            )
                        }
                    }
                    MazeTopology.Polar -> Unit
                }
            }
        }
        require(cells.isNotEmpty()) { "Empty maze mask" }
        return MazeGraph(cells, edges)
    }

    private suspend fun mask(recipe: MazeRecipe): BooleanArray {
        val w = recipe.columns
        val h = recipe.rows
        val allowed =
            BooleanArray(w * h) { i ->
                val x = (i % w + 0.5) / w * 2 - 1
                val y = (i / w + 0.5) / h * 2 - 1
                when (recipe.mask) {
                    MazeMask.Ellipse -> x * x + y * y <= 1
                    MazeMask.Diamond -> abs(x) + abs(y) <= 1
                    MazeMask.Cross -> abs(x) < 0.35 || abs(y) < 0.35
                    MazeMask.Heart -> {
                        val yy = -y * 1.2 + 0.15
                        (x * x + yy * yy - 0.8).pow(3) - x * x * yy.pow(3) <= 0
                    }
                    MazeMask.Image -> recipe.imageMask[i]
                }
            }
        // Keep one connected component so every advertised algorithm has a connected input graph.
        val seen = BooleanArray(w * h)
        var largest = emptyList<Int>()
        for (start in allowed.indices) if (allowed[start] && !seen[start]) {
            currentCoroutineContext().ensureActive()
            val queue = ArrayDeque<Int>()
            val component = mutableListOf<Int>()
            queue.add(start)
            seen[start] = true
            while (queue.isNotEmpty()) {
                val i = queue.removeFirst()
                component.add(i)
                val candidates =
                    intArrayOf(
                        if (i % w > 0) i - 1 else -1,
                        if (i % w < w - 1) i + 1 else -1,
                        i - w,
                        i + w,
                    )
                candidates.forEach { n ->
                    if (n in allowed.indices && allowed[n] && !seen[n]) {
                        seen[n] = true
                        queue.add(n)
                    }
                }
            }
            if (component.size > largest.size) largest = component
        }
        require(largest.isNotEmpty()) { "Empty maze mask" }
        return BooleanArray(w * h).also { result -> largest.forEach { result[it] = true } }
    }

    private suspend fun polar(rings: Int): MazeGraph {
        val cells = mutableListOf<MazeCell>()
        val edges = mutableListOf<MazeEdge>()
        val counts = IntArray(rings)
        counts[0] = 1
        for (r in 1 until rings) {
            val ratio = ((2 * PI * r) / counts[r - 1]).roundToInt().coerceIn(1, 2)
            counts[r] = counts[r - 1] * ratio
        }
        val offsets = IntArray(rings)
        for (r in 1 until rings) offsets[r] = offsets[r - 1] + counts[r - 1]
        val center = MazePoint(rings.toFloat(), rings.toFloat())
        fun point(radius: Float, angle: Float): MazePoint {
            val a = Math.toRadians(angle.toDouble())
            return MazePoint(
                center.x + radius * cos(a).toFloat(),
                center.y + radius * sin(a).toFloat(),
            )
        }
        for (r in 0 until rings) {
            currentCoroutineContext().ensureActive()
            val n = counts[r]
            val step = 360f / n
            for (c in 0 until n) {
                val a = c * step
                val id = offsets[r] + c
                val outline = buildList {
                    val pieces = ceil(step / 8).toInt().coerceAtLeast(1)
                    for (i in 0..pieces) add(point(r + 1f, a + step * i / pieces))
                    if (r > 0)
                        for (i in pieces downTo 0) add(point(r.toFloat(), a + step * i / pieces))
                    else add(center)
                }
                cells.add(MazeCell(if (r == 0) center else point(r + 0.5f, a + step / 2), outline))
                if (r > 0) {
                    edges.add(
                        MazeEdge(
                            id,
                            offsets[r] + (c + 1) % n,
                            MazeWall.Line(point(r.toFloat(), a + step), point(r + 1f, a + step)),
                        )
                    )
                    val parent = offsets[r - 1] + c / (n / counts[r - 1])
                    edges.add(MazeEdge(id, parent, MazeWall.Arc(center, r.toFloat(), a, step)))
                }
                if (r == rings - 1)
                    edges.add(MazeEdge(id, -1, MazeWall.Arc(center, r + 1f, a, step)))
            }
        }
        return MazeGraph(cells, edges)
    }
}
