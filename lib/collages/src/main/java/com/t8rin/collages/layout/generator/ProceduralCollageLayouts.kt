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

@file:Suppress("SameParameterValue")

package com.t8rin.collages.layout.generator

import com.t8rin.collages.model.CollageDesign
import com.t8rin.collages.model.CollagePoint
import com.t8rin.collages.model.CollageSlot
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sign
import kotlin.math.sin
import kotlin.math.sqrt

internal object ProceduralCollageLayouts {
    val layoutCount = (1..100).sumOf(::count)

    fun count(imageCount: Int): Int = when (imageCount) {
        in 1..20 -> 5
        in 21..50 -> 10
        in 51..100 -> 12
        else -> 0
    }

    fun generate(imageCount: Int): Map<String, CollageDesign> =
        (0 until count(imageCount)).associate { variant ->
            "procedural_${imageCount}_$variant" to create(imageCount, variant)
        }

    fun create(imageCount: Int, variant: Int): CollageDesign {
        require(variant in 0 until count(imageCount))
        val seed = LayoutRandom(imageCount * 104729 + variant * 7919 + 17)
        val edition = variant / 6
        val slots = if (imageCount == 1) {
            listOf(silhouette(Bounds(0f, 0f, 1f, 1f), variant))
        } else when (variant) {
            0 -> if (imageCount < 5) interlock(imageCount) else mosaic(imageCount, edition, seed)
            1 -> waves(imageCount, edition)
            2 -> if (imageCount == 2) waves(imageCount, 3) else facets(imageCount, edition, seed)
            3 -> windows(imageCount)
            4 -> pinwheel(imageCount)
            5, 11 -> rings(imageCount, edition)
            6 -> spotlight(imageCount, seed)
            7 -> leaves(imageCount)
            8 -> facets(imageCount, edition, seed)
            9 -> pebbles(imageCount)
            else -> waves(imageCount, 2)
        }
        return CollageDesign(slots)
    }

    private fun mosaic(
        count: Int,
        edition: Int,
        seed: LayoutRandom,
        bounds: Bounds = Bounds(0f, 0f, 1f, 1f)
    ): List<CollageSlot> {
        val slots = mutableListOf<CollageSlot>()
        fun split(box: Bounds, size: Int, depth: Int) {
            if (size == 1) {
                slots.add(box.slot())
                return
            }
            val first = (size * if ((depth + edition) % 3 == 0) .38f else .57f)
                .roundToInt().coerceIn(1, size - 1)
            val fraction = first.toFloat() / size
            val cut = fraction + (seed.next() - .5f) * .32f * minOf(fraction, 1f - fraction)
            val vertical = when {
                box.width > box.height * 1.25f -> true
                box.height > box.width * 1.25f -> false
                else -> (depth + edition) % 2 == 0
            }
            if (vertical) {
                val x = box.left + box.width * cut
                split(box.copy(right = x), first, depth + 1)
                split(box.copy(left = x), size - first, depth + 1)
            } else {
                val y = box.top + box.height * cut
                split(box.copy(bottom = y), first, depth + 1)
                split(box.copy(top = y), size - first, depth + 1)
            }
        }
        split(bounds, count, 0)
        return slots
    }

    private fun terraces(count: Int, edition: Int): List<Bounds> {
        val rows = sqrt(count * if (edition == 0) .7f else 1.45f).roundToInt()
            .coerceIn(1, count)
        val sizes = distribute(count, List(rows) { 1f + .28f * sin(it * 1.9f + edition) })
        val heights = divisions(sizes.mapIndexed { row, size ->
            size * (1f + .18f * cos(row * 2.1f + edition))
        })
        return sizes.flatMapIndexed { row, size ->
            val widths = divisions(List(size) { column ->
                1f + .35f * sin(column * 2.3f + row * .9f + edition * 1.7f)
            })
            List(size) { column ->
                Bounds(widths[column], heights[row], widths[column + 1], heights[row + 1])
            }
        }
    }

    private fun waves(count: Int, edition: Int): List<CollageSlot> {
        val rows = sqrt(count.toFloat()).roundToInt().coerceAtLeast(1)
        val sizes = distribute(count, List(rows) { 1f })
        val boxes = tiles(count)
        val xs = (List(65) { it / 64f } + boxes.flatMap { listOf(it.left, it.right) })
            .distinct().sorted()
        val ys = (List(65) { it / 64f } + boxes.flatMap { listOf(it.top, it.bottom) })
            .distinct().sorted()

        fun warp(x: Float, y: Float): CollagePoint {
            val phase = .4
            val wave = when (edition) {
                2 -> {
                    val position = y * rows
                    val section = position.toInt()
                    ((if (section % 2 == 0) position - section else 1 - (position - section)) * 2 - 1).toDouble()
                }

                3 -> sin(2 * PI * y) + .45 * sin(4 * PI * y + PI / 3)
                else -> sin(2 * PI * y + phase)
            }
            val dx = (if (edition == 2) .30 else .22) / rows * sin(PI * x) * wave
            val dy = (if (edition == 0) .08 else 0.0) / sizes.max() *
                    sin(PI * y) * sin(2 * PI * x - phase)
            return point(x + dx, y + dy)
        }
        return boxes.map { box ->
            val x = xs.filter { it in box.left..box.right }
            val y = ys.filter { it > box.top && it < box.bottom }
            CollageSlot(
                x.map { warp(it, box.top) } + y.map { warp(box.right, it) } +
                        x.asReversed().map { warp(it, box.bottom) } +
                        y.asReversed().map { warp(box.left, it) }
            )
        }
    }

    private fun facets(count: Int, edition: Int, seed: LayoutRandom): List<CollageSlot> {
        val rows = sqrt(count * if (edition == 0) 1f else .65f).roundToInt().coerceAtLeast(1)
        val sizes = distribute(count, List(rows) { 1f })
        val centers = if (edition == 1) {
            List(count) { index ->
                val angle = index * PI * (3 - sqrt(5.0))
                val radius = .47 * sqrt((index + .5) / count)
                point(.5 + radius * cos(angle), .5 + radius * sin(angle))
            }
        } else sizes.flatMapIndexed { row, size ->
            List(size) { column ->
                point(
                    (column + .5 + (seed.next() - .5) * .56) / size,
                    (row + .5 + (seed.next() - .5) * .56) / rows
                )
            }
        }
        return centers.map { center ->
            var polygon = Bounds(0f, 0f, 1f, 1f).slot().points
            centers.forEach { other ->
                if (center != other) {
                    val x = (other.x - center.x).toDouble()
                    val y = (other.y - center.y).toDouble()
                    val limit = (other.x.toDouble().pow(2) + other.y.toDouble().pow(2) -
                            center.x.toDouble().pow(2) - center.y.toDouble().pow(2)) / 2
                    polygon = buildList {
                        polygon.indices.forEach { i ->
                            val a = polygon[i]
                            val b = polygon[(i + 1) % polygon.size]
                            val da = a.x * x + a.y * y - limit
                            val db = b.x * x + b.y * y - limit
                            if (da <= 0) add(a)
                            if ((da <= 0) != (db <= 0)) {
                                val t = da / (da - db)
                                add(point(a.x + t * (b.x - a.x), a.y + t * (b.y - a.y)))
                            }
                        }
                    }
                }
            }
            CollageSlot(polygon)
        }
    }

    private fun windows(count: Int): List<CollageSlot> =
        terraces(count, 0).mapIndexed { index, box ->
            val points = List(129) { step ->
                val angle = PI + PI * step / 128
                point(.5 + .5 * cos(angle), .44 + .44 * sin(angle))
            } + listOf(CollagePoint(1f, 1f), CollagePoint(0f, 1f))
            CollageSlot(
                points.map {
                    val y = if (index % 3 == 1) 1f - it.y else it.y
                    CollagePoint(box.left + box.width * it.x, box.top + box.height * y)
                },
                preserveShape = true
            )
        }

    private fun interlock(count: Int): List<CollageSlot> = List(count) { index ->
        fun edge(x: Float) = listOf(
            CollagePoint(x, 0f), CollagePoint(x, .32f),
            CollagePoint(x + .52f / count, .32f), CollagePoint(x + .52f / count, .68f),
            CollagePoint(x, .68f), CollagePoint(x, 1f)
        )

        val left = if (index == 0) listOf(CollagePoint(0f, 0f), CollagePoint(0f, 1f))
        else edge(index.toFloat() / count)
        val right = if (index == count - 1) listOf(CollagePoint(1f, 0f), CollagePoint(1f, 1f))
        else edge((index + 1f) / count)
        CollageSlot(left + right.asReversed())
    }

    private fun pinwheel(count: Int): List<CollageSlot> {
        if (count < 5) return interlock(count).map { slot ->
            slot.copy(points = slot.points.map { CollagePoint(it.y, 1f - it.x) })
        }
        val rings = (sqrt(count.toFloat()) / 3).roundToInt().coerceAtLeast(1)
        val sizes = distribute(count - 1, List(rings * 4) { rings - it / 4f + 1 })
        val slots = mutableListOf<CollageSlot>()
        val step = .39f / rings
        repeat(rings) { ring ->
            val start = ring * step
            val end = 1f - start
            val a = start + step
            val b = end - step
            val arms = listOf(
                Bounds(start, start, b, a), Bounds(b, start, end, b),
                Bounds(a, b, end, end), Bounds(start, a, a, end)
            )
            arms.forEachIndexed { arm, box ->
                val size = sizes[ring * 4 + arm]
                val cuts = divisions(List(size) { 1f + .2f * sin(it * 2f + ring + arm) })
                repeat(size) { index ->
                    slots.add(
                        if (arm % 2 == 0) box.copy(
                            left = box.left + box.width * cuts[index],
                            right = box.left + box.width * cuts[index + 1]
                        ).slot() else box.copy(
                            top = box.top + box.height * cuts[index],
                            bottom = box.top + box.height * cuts[index + 1]
                        ).slot()
                    )
                }
            }
        }
        slots.add(Bounds(.39f, .39f, .61f, .61f).slot())
        return slots
    }

    private fun spotlight(count: Int, seed: LayoutRandom): List<CollageSlot> {
        val hero = Bounds(.29f, .25f, .71f, .75f)
        val boxes = listOf(
            Bounds(0f, 0f, .71f, .25f), Bounds(.71f, 0f, 1f, .75f),
            Bounds(.29f, .75f, 1f, 1f), Bounds(0f, .25f, .29f, 1f)
        )
        val sizes = distribute(count - 1, boxes.map { it.width * it.height })
        return listOf(hero.slot()) + boxes.flatMapIndexed { index, box ->
            mosaic(sizes[index], index % 2, seed, box)
        }
    }

    private fun leaves(count: Int): List<CollageSlot> =
        tiles(count).mapIndexed { index, box ->
            CollageSlot(List(64) { i ->
                val t = i / 32.0
                val x = if (i <= 32) t else 2 - t
                val y = if (i <= 32) 1 - sqrt(1 - (1 - x).pow(2)) else sqrt(1 - x * x)
                point(
                    box.left + (.04 + .92 * x) * box.width,
                    box.top + (.04 + .92 * if (index % 2 == 0) y else 1 - y) * box.height
                )
            }, preserveShape = true)
        }

    private fun tiles(count: Int): List<Bounds> {
        val rows = sqrt(count.toFloat()).roundToInt()
        val sizes = distribute(count, List(rows) { 1f })
        return sizes.flatMapIndexed { row, size ->
            List(size) { column ->
                Bounds(
                    column.toFloat() / size, row.toFloat() / rows,
                    (column + 1f) / size, (row + 1f) / rows
                )
            }
        }
    }

    private fun pebbles(count: Int): List<CollageSlot> =
        tiles(count).mapIndexed { index, box ->
            CollageSlot(List(64) { i ->
                val angle = 2 * PI * i / 64
                val radius = .46 * (1 - .07 * sin(3 * angle + index * 1.3))
                val rotation = if (index % 2 == 0) .18 else -.18
                val x = radius * sign(cos(angle)) * abs(cos(angle)).pow(.8)
                val y = radius * sign(sin(angle)) * abs(sin(angle)).pow(.8)
                point(
                    box.left + (.5 + x * cos(rotation) - y * sin(rotation)) * box.width,
                    box.top + (.5 + x * sin(rotation) + y * cos(rotation)) * box.height
                )
            }, preserveShape = true)
        }

    private fun silhouette(box: Bounds, variant: Int): CollageSlot {
        val powers = listOf(3.4, 1.3, 2.4, 2.0, 4.2)
        val waves = listOf(3, 2, 4, 5, 6)
        val amplitudes = listOf(.10, .18, .28, .22, .14)
        return CollageSlot(
            List(192) { i ->
                val angle = 2 * PI * i / 192
                val radius = .46 * (1 - amplitudes[variant] *
                        (1 + sin(waves[variant] * angle + variant * .7)) / 2)
                val x = .5 + radius * sign(cos(angle)) * abs(cos(angle)).pow(2 / powers[variant])
                val y = .5 + radius * sign(sin(angle)) * abs(sin(angle)).pow(2 / powers[variant])
                point(box.left + x * box.width, box.top + y * box.height)
            },
            preserveShape = true
        )
    }

    private fun rings(count: Int, edition: Int): List<CollageSlot> {
        val levels = ceil(sqrt(count.toDouble()) / 2).toInt().coerceAtLeast(2)
        val sizes = listOf(1) + distribute(count - 1, List(levels - 1) { 3f + it * 2 })
        val divisions = sizes.drop(1).mapIndexed { ring, size ->
            divisions(List(size) { 1f + .18f * sin(it * 1.8f + ring + edition) })
                .map { (it * 4096).roundToInt() / 4096f }
        }
        val angles = (List(129) { (it / 128f * 4096).roundToInt() / 4096f } + divisions.flatten())
            .distinct().sorted()
        val radiusX = .48
        val radiusY = .48
        fun polar(radius: Double, angle: Float): CollagePoint =
            point(
                .5 + radiusX * radius * sign(cos(2 * PI * angle)) *
                        abs(cos(2 * PI * angle)).pow(if (edition == 0) 1.0 else .55),
                .5 + radiusY * radius * sign(sin(2 * PI * angle)) *
                        abs(sin(2 * PI * angle)).pow(if (edition == 0) 1.0 else .55)
            )

        var used = 1
        val slots = mutableListOf(
            CollageSlot(
                angles.dropLast(1).map {
                    polar(sqrt(1.0 / count), it)
                }
            )
        )
        divisions.forEachIndexed { ring, cuts ->
            val inner = sqrt(used.toDouble() / count)
            used += sizes[ring + 1]
            val outer = sqrt(used.toDouble() / count)
            cuts.zipWithNext().forEach { (start, end) ->
                val arc = angles.filter { it in start..end }
                slots.add(CollageSlot(arc.map { polar(outer, it) } +
                        arc.asReversed().map { polar(inner, it) }))
            }
        }
        return slots
    }

    private fun distribute(total: Int, weights: List<Float>): List<Int> {
        val remaining = total - weights.size
        val sum = weights.sum()
        val ideal = weights.map { remaining * it / sum }
        val result = ideal.map { it.toInt() + 1 }.toMutableList()
        ideal.indices.sortedByDescending { ideal[it] - ideal[it].toInt() }
            .take(total - result.sum()).forEach { result[it]++ }
        return result
    }

    private fun divisions(weights: List<Float>): List<Float> {
        val total = weights.sum()
        var offset = 0f
        return listOf(0f) + weights.mapIndexed { index, weight ->
            offset += weight
            if (index == weights.lastIndex) 1f else offset / total
        }
    }

    private fun point(x: Double, y: Double) =
        CollagePoint(x.toFloat().coerceIn(0f, 1f), y.toFloat().coerceIn(0f, 1f))

    private data class Bounds(
        val left: Float,
        val top: Float,
        val right: Float,
        val bottom: Float
    ) {
        val width get() = right - left
        val height get() = bottom - top
        fun slot() = CollageSlot(
            listOf(
                CollagePoint(left, top), CollagePoint(right, top),
                CollagePoint(right, bottom), CollagePoint(left, bottom)
            )
        )
    }

    private class LayoutRandom(private var state: Int) {
        fun next(): Float {
            state = state xor (state shl 13)
            state = state xor (state ushr 17)
            state = state xor (state shl 5)
            return (state ushr 8) / 16777216f
        }
    }
}