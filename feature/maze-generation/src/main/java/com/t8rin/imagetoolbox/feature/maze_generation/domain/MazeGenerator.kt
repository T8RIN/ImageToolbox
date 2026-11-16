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

import kotlin.time.TimeSource
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

/** Algorithms operate only on edge indices; geometry never changes their adjacency contract. */
object MazeGenerator {
    suspend fun generate(recipe: MazeRecipe, captureEvents: Boolean = false): MazeResult {
        val time = TimeSource.Monotonic.markNow()
        val graph = MazeTopologyBuilder.build(recipe)
        require(recipe.algorithm != MazeAlgorithm.ReverseDelete || graph.cells.size <= 1600) {
            "Reverse Delete is limited to 1600 cells"
        }
        val worker = Worker(graph, recipe, captureEvents)
        worker.generate()
        worker.braid()
        return MazeResult(
            graph,
            worker.links,
            worker.events.toIntArray(),
            worker.initial,
            time.elapsedNow().inWholeMilliseconds,
        )
    }

    private class Worker(val g: MazeGraph, val p: MazeRecipe, val captureEvents: Boolean) {
        val random = MazeRandom(p.seed)
        val links = BooleanArray(g.edges.size)
        var initial = BooleanArray(g.edges.size)
        val events = MazeEvents()
        val n = g.cells.size

        suspend fun checkpoint() = currentCoroutineContext().ensureActive()

        fun set(e: Int, open: Boolean = true) {
            if (links[e] != open) {
                links[e] = open
                if (captureEvents) events.add(if (open) e + 1 else -e - 1)
            }
        }

        fun allOpen() {
            g.edges.indices.forEach { links[it] = g.edges[it].b >= 0 }
            initial = links.copyOf()
        }

        fun unvisited(cell: Int, seen: BooleanArray) =
            g.adjacency[cell].filter { !seen[g.other(it, cell)] }

        fun edge(a: Int, b: Int) = g.adjacency[a].first { g.other(it, a) == b }

        suspend fun generate() {
            if (n == 1) return
            when (p.algorithm) {
                MazeAlgorithm.Backtracker -> growing(0)
                MazeAlgorithm.Prim -> prim()
                MazeAlgorithm.Kruskal -> kruskal()
                MazeAlgorithm.AldousBroder -> uniformWalk(false)
                MazeAlgorithm.Wilson -> wilson(BooleanArray(n).also { it[random.next(n)] = true })
                MazeAlgorithm.Houston -> uniformWalk(true)
                MazeAlgorithm.HuntAndKill -> hunt()
                MazeAlgorithm.GrowingTree -> growing(1)
                MazeAlgorithm.BinaryTree -> binary()
                MazeAlgorithm.Sidewinder -> sidewinder()
                MazeAlgorithm.Eller -> eller()
                MazeAlgorithm.RecursiveDivision -> division()
                MazeAlgorithm.BlobbyDivision -> blobby()
                MazeAlgorithm.ReverseDelete -> reverseDelete()
                MazeAlgorithm.GrowingBinaryTree -> growingBinary()
                MazeAlgorithm.OriginShift -> originShift()
            }
        }

        suspend fun growing(mode: Int) {
            val seen = BooleanArray(n)
            val active = mutableListOf(random.next(n))
            seen[active[0]] = true
            while (active.isNotEmpty()) {
                checkpoint()
                val index =
                    if (mode == 0) active.lastIndex
                    else {
                        val sum = p.newestWeight + p.randomWeight + p.oldestWeight + p.middleWeight
                        val x = random.next(sum.coerceAtLeast(1))
                        when {
                            sum == 0 || x < p.newestWeight -> active.lastIndex
                            x < p.newestWeight + p.randomWeight -> random.next(active.size)
                            x < p.newestWeight + p.randomWeight + p.oldestWeight -> 0
                            else -> active.size / 2
                        }
                    }
                val cell = active[index]
                val candidates = unvisited(cell, seen)
                if (candidates.isEmpty()) active.removeAt(index)
                else {
                    val e = random.pick(candidates)
                    val to = g.other(e, cell)
                    set(e)
                    seen[to] = true
                    active.add(to)
                }
            }
        }

        suspend fun prim() {
            val seen = BooleanArray(n)
            val frontier = mutableListOf<Int>()
            fun visit(c: Int) {
                seen[c] = true
                frontier.addAll(g.adjacency[c].toList())
            }
            visit(random.next(n))
            while (frontier.isNotEmpty()) {
                checkpoint()
                val i = random.next(frontier.size)
                val e = frontier[i]
                frontier[i] = frontier.last()
                frontier.removeAt(frontier.lastIndex)
                val (a, b) = g.edges[e]
                if (seen[a] != seen[b]) {
                    set(e)
                    visit(if (seen[a]) b else a)
                }
            }
        }

        suspend fun kruskal() {
            val ds = MazeDisjointSet(n)
            val order = g.edges.indices.filter { g.edges[it].b >= 0 }.toIntArray()
            random.shuffle(order)
            for (e in order) {
                checkpoint()
                val (a, b) = g.edges[e]
                if (ds.union(a, b)) set(e)
            }
        }

        suspend fun uniformWalk(hybrid: Boolean) {
            val seen = BooleanArray(n)
            var cell = random.next(n)
            seen[cell] = true
            var count = 1
            val target = if (hybrid) (n * p.houstonSwitch / 100).coerceAtLeast(1) else n
            while (count < target) {
                checkpoint()
                val adjacent = g.adjacency[cell]
                val e = adjacent[random.next(adjacent.size)]
                val next = g.other(e, cell)
                if (!seen[next]) {
                    set(e)
                    seen[next] = true
                    count++
                }
                cell = next
            }
            if (hybrid) wilson(seen)
        }

        suspend fun wilson(seen: BooleanArray) {
            val position = IntArray(n) { -1 }
            val order = IntArray(n) { it }
            random.shuffle(order)
            for (start in order) if (!seen[start]) {
                val path = mutableListOf(start)
                val pathEdges = mutableListOf<Int>()
                position[start] = 0
                var cell = start
                while (!seen[cell]) {
                    checkpoint()
                    val adjacent = g.adjacency[cell]
                    val e = adjacent[random.next(adjacent.size)]
                    val next = g.other(e, cell)
                    val old = position[next]
                    if (old >= 0) {
                        while (path.lastIndex > old) {
                            position[path.removeAt(path.lastIndex)] = -1
                            pathEdges.removeAt(pathEdges.lastIndex)
                        }
                    } else {
                        position[next] = path.size
                        path.add(next)
                        pathEdges.add(e)
                    }
                    cell = next
                }
                for (i in 0 until path.lastIndex) {
                    set(pathEdges[i])
                    seen[path[i]] = true
                }
                path.forEach { position[it] = -1 }
            }
        }

        suspend fun hunt() {
            val seen = BooleanArray(n)
            var cell = random.next(n)
            seen[cell] = true
            val frontier = linkedSetOf<Int>()
            fun add(c: Int) {
                g.adjacency[c].forEach {
                    val next = g.other(it, c)
                    if (!seen[next]) frontier.add(next)
                }
            }
            add(cell)
            while (true) {
                checkpoint()
                val candidates = unvisited(cell, seen)
                if (candidates.isNotEmpty()) {
                    val e = random.pick(candidates)
                    cell = g.other(e, cell)
                    set(e)
                    seen[cell] = true
                    frontier.remove(cell)
                    add(cell)
                } else {
                    if (frontier.isEmpty()) return
                    cell = frontier.first()
                    frontier.remove(cell)
                    val visited = g.adjacency[cell].filter { seen[g.other(it, cell)] }
                    set(random.pick(visited))
                    seen[cell] = true
                    add(cell)
                }
            }
        }

        suspend fun binary() {
            val w = p.columns
            val h = p.rows
            val north =
                p.direction == MazeDirection.NorthEast || p.direction == MazeDirection.NorthWest
            val east =
                p.direction == MazeDirection.NorthEast || p.direction == MazeDirection.SouthEast
            for (y in 0 until h) for (x in 0 until w) {
                checkpoint()
                val c = y * w + x
                val horizontal =
                    if (east) if (x < w - 1) c + 1 else -1 else if (x > 0) c - 1 else -1
                val vertical = if (north) if (y > 0) c - w else -1 else if (y < h - 1) c + w else -1
                val to =
                    when {
                        horizontal < 0 -> vertical
                        vertical < 0 -> horizontal
                        random.next(100) < p.horizontalBias -> horizontal
                        else -> vertical
                    }
                if (to >= 0) set(edge(c, to))
            }
        }

        suspend fun sidewinder() {
            val w = p.columns
            val h = p.rows
            for (y in 0 until h) {
                var runStart = 0
                for (x in 0 until w) {
                    checkpoint()
                    val c = y * w + x
                    if (x == w - 1 || (y > 0 && random.next(100) >= p.horizontalBias)) {
                        if (y > 0) {
                            val up = y * w + runStart + random.next(x - runStart + 1)
                            set(edge(up, up - w))
                        }
                        runStart = x + 1
                    } else set(edge(c, c + 1))
                }
            }
        }

        suspend fun eller() {
            val w = p.columns
            val h = p.rows
            val ds = MazeDisjointSet(n)
            for (y in 0 until h) {
                checkpoint()
                for (x in 0 until w - 1) {
                    val a = y * w + x
                    val b = a + 1
                    if (
                        ds.find(a) != ds.find(b) && (y == h - 1 || random.next(100) < p.ellerJoin)
                    ) {
                        ds.union(a, b)
                        set(edge(a, b))
                    }
                }
                if (y < h - 1) {
                    val groups = (0 until w).groupBy { ds.find(y * w + it) }
                    groups.values.forEach { group ->
                        val forced = random.pick(group)
                        group.forEach { x ->
                            if (x == forced || random.next(100) < p.ellerDrop) {
                                val a = y * w + x
                                ds.union(a, a + w)
                                set(edge(a, a + w))
                            }
                        }
                    }
                }
            }
        }

        suspend fun division() {
            allOpen()
            val w = p.columns
            val regions = ArrayDeque<IntArray>()
            regions.add(intArrayOf(0, 0, w, p.rows))
            while (regions.isNotEmpty()) {
                checkpoint()
                val (x, y, rw, rh) = regions.removeLast()
                if (rw == 1 || rh == 1) continue
                if (rw * rh <= p.divisionRoom) {
                    // Rooms remain perfect: replace their open interior with a random spanning
                    // tree.
                    val ds = MazeDisjointSet(n)
                    val inner =
                        g.edges.indices
                            .filter { e ->
                                val (a, b) = g.edges[e]
                                b >= 0 &&
                                    a % w in x until x + rw &&
                                    b % w in x until x + rw &&
                                    a / w in y until y + rh &&
                                    b / w in y until y + rh
                            }
                            .toIntArray()
                    random.shuffle(inner)
                    inner.forEach { e ->
                        val (a, b) = g.edges[e]
                        if (!ds.union(a, b)) set(e, false)
                    }
                    continue
                }
                val horizontal = if (rw == rh) random.next(100) >= p.horizontalBias else rh > rw
                if (horizontal) {
                    val cut = y + 1 + random.next(rh - 1)
                    val gap = x + random.next(rw)
                    for (xx in x until x + rw) if (xx != gap)
                        set(edge((cut - 1) * w + xx, cut * w + xx), false)
                    regions.add(intArrayOf(x, y, rw, cut - y))
                    regions.add(intArrayOf(x, cut, rw, y + rh - cut))
                } else {
                    val cut = x + 1 + random.next(rw - 1)
                    val gap = y + random.next(rh)
                    for (yy in y until y + rh) if (yy != gap)
                        set(edge(yy * w + cut - 1, yy * w + cut), false)
                    regions.add(intArrayOf(x, y, cut - x, rh))
                    regions.add(intArrayOf(cut, y, x + rw - cut, rh))
                }
            }
        }

        suspend fun blobby() {
            allOpen()
            val regions = ArrayDeque<IntArray>()
            regions.add(IntArray(n) { it })
            val labels = IntArray(n) { -1 }
            while (regions.isNotEmpty()) {
                checkpoint()
                val region = regions.removeLast()
                if (region.size < 2) continue
                region.forEach { labels[it] = -2 }
                val a = region[random.next(region.size)]
                var b = region[random.next(region.size)]
                while (a == b) b = region[random.next(region.size)]
                val active = mutableListOf(a, b)
                labels[a] = 0
                labels[b] = 1
                while (active.isNotEmpty()) {
                    checkpoint()
                    val i = random.next(active.size)
                    val c = active[i]
                    val candidates =
                        g.adjacency[c].map { g.other(it, c) }.filter { labels[it] == -2 }
                    if (candidates.isEmpty()) {
                        active[i] = active.last()
                        active.removeAt(active.lastIndex)
                    } else {
                        val next = random.pick(candidates)
                        labels[next] = labels[c]
                        active.add(next)
                    }
                }
                val membership = region.toHashSet()
                val crossing =
                    region.flatMap { c ->
                        g.adjacency[c].filter { e ->
                            val other = g.other(e, c)
                            other in membership && c < other && labels[c] != labels[other]
                        }
                    }
                val gap = random.pick(crossing)
                crossing.forEach { if (it != gap) set(it, false) }
                regions.add(region.filter { labels[it] == 0 }.toIntArray())
                regions.add(region.filter { labels[it] == 1 }.toIntArray())
                region.forEach { labels[it] = -1 }
            }
        }

        suspend fun reverseDelete() {
            allOpen()
            val order = g.edges.indices.filter { links[it] }.toIntArray()
            random.shuffle(order)
            val seen = IntArray(n)
            var stamp = 0
            val queue = IntArray(n)
            for (e in order) {
                checkpoint()
                links[e] = false
                stamp++
                val (a, b) = g.edges[e]
                var head = 0
                var tail = 1
                queue[0] = a
                seen[a] = stamp
                while (head < tail && seen[b] != stamp) {
                    checkpoint()
                    val c = queue[head++]
                    for (nextEdge in g.adjacency[c]) if (links[nextEdge]) {
                        val next = g.other(nextEdge, c)
                        if (seen[next] != stamp) {
                            seen[next] = stamp
                            queue[tail++] = next
                        }
                    }
                }
                links[e] = true
                if (seen[b] == stamp) set(e, false)
            }
        }

        suspend fun growingBinary() {
            // Choose one parent in the preceding BFS layer; every edge descends toward the root.
            val root = random.next(n)
            val distance = IntArray(n) { -1 }
            val q = ArrayDeque<Int>()
            distance[root] = 0
            q.add(root)
            while (q.isNotEmpty()) {
                checkpoint()
                val c = q.removeFirst()
                g.adjacency[c].forEach { e ->
                    val next = g.other(e, c)
                    if (distance[next] < 0) {
                        distance[next] = distance[c] + 1
                        q.add(next)
                    }
                }
            }
            for (c in 0 until n) if (c != root) {
                checkpoint()
                set(random.pick(g.adjacency[c].filter { distance[g.other(it, c)] < distance[c] }))
            }
        }

        suspend fun originShift() {
            growing(0)
            val parent = IntArray(n) { -1 }
            var root = random.next(n)
            val q = ArrayDeque<Int>()
            q.add(root)
            val seen = BooleanArray(n)
            seen[root] = true
            while (q.isNotEmpty()) {
                val c = q.removeFirst()
                for (e in g.adjacency[c]) if (links[e]) {
                    val next = g.other(e, c)
                    if (!seen[next]) {
                        seen[next] = true
                        parent[next] = e
                        q.add(next)
                    }
                }
            }
            repeat(n * p.originIterations) {
                checkpoint()
                val adjacent = g.adjacency[root]
                val e = adjacent[random.next(adjacent.size)]
                val next = g.other(e, root)
                val old = parent[next]
                if (old != e) {
                    set(old, false)
                    set(e)
                }
                parent[root] = e
                parent[next] = -1
                root = next
            }
        }

        suspend fun braid() {
            if (p.braid == 0) return
            val cells = IntArray(n) { it }
            random.shuffle(cells)
            for (c in cells) {
                checkpoint()
                if (g.adjacency[c].count { links[it] } == 1 && random.next(100) < p.braid) {
                    val closed = g.adjacency[c].filter { !links[it] }
                    val dead =
                        closed.filter { e -> g.adjacency[g.other(e, c)].count { links[it] } == 1 }
                    if (closed.isNotEmpty()) set(random.pick(dead.ifEmpty { closed }))
                }
            }
        }
    }
}
