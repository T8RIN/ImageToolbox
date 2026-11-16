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

/** Frozen xorshift32 stream. Changing this requires a recipe algorithmVersion bump. */
internal class MazeRandom(seed: Int) {
    private var state = if (seed == 0) 0x6d2b79f5 else seed

    fun next(bound: Int): Int {
        require(bound > 0)
        // Rejection avoids modulo bias, important for the uniform spanning-tree algorithms.
        val limit = (1L shl 32) - ((1L shl 32) % bound)
        var n: Long
        do {
            var x = state
            x = x xor (x shl 13)
            x = x xor (x ushr 17)
            x = x xor (x shl 5)
            state = x
            n = x.toLong() and 0xffffffffL
        } while (n >= limit)
        return (n % bound).toInt()
    }

    fun <T> pick(values: List<T>): T = values[next(values.size)]

    fun shuffle(values: IntArray) {
        for (i in values.lastIndex downTo 1) {
            val j = next(i + 1)
            val t = values[i]
            values[i] = values[j]
            values[j] = t
        }
    }
}

internal class MazeEvents {
    private var data = IntArray(1024)
    private var size = 0

    fun add(event: Int) {
        if (size == data.size) data = data.copyOf(data.size * 2)
        data[size++] = event
    }

    fun toIntArray(): IntArray = data.copyOf(size)
}

internal class MazeDisjointSet(size: Int) {
    private val parent = IntArray(size) { it }
    private val rank = IntArray(size)

    fun find(value: Int): Int {
        var x = value
        while (parent[x] != x) {
            parent[x] = parent[parent[x]]
            x = parent[x]
        }
        return x
    }

    fun union(a: Int, b: Int): Boolean {
        var x = find(a)
        var y = find(b)
        if (x == y) return false
        if (rank[x] < rank[y]) {
            val t = x
            x = y
            y = t
        }
        parent[y] = x
        if (rank[x] == rank[y]) rank[x]++
        return true
    }
}
