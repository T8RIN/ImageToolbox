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

package com.t8rin.collages

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object CreativeCollages {
    enum class Family { Glass, Comic, Orbit, Fan, Wave, Weave, Arch, Editorial, Spiral, Cards }
    data class Template(val id: String, val family: Family, val design: CollageDesign)

    val templates: List<Template> by lazy {
        buildList {
            Family.entries.forEach { family ->
                for (count in 2..20) add(create(family, count, 0))
                add(create(family, family.ordinal + 2, 1))
            }
        }
    }

    private fun rect(l: Float, t: Float, r: Float, b: Float) = CollageSlot(
        listOf(
            CollagePoint(l, t), CollagePoint(r, t), CollagePoint(r, b), CollagePoint(l, b)
        )
    )

    private fun create(family: Family, count: Int, variant: Int): Template {
        val slots = when (family) {
            Family.Glass -> glass(count, variant)
            Family.Comic -> strips(count, variant, diagonal = true)
            Family.Orbit -> orbit(count, variant)
            Family.Fan -> fan(count, variant)
            Family.Wave -> waves(count, variant)
            Family.Weave -> weave(count, variant)
            Family.Arch -> arches(count, variant)
            Family.Editorial -> editorial(count, variant)
            Family.Spiral -> spiral(count, variant)
            Family.Cards -> cards(count, variant)
        }
        return Template(
            "creative_${family.name.lowercase()}_${count}_$variant",
            family,
            CollageDesign(slots)
        )
    }

    private fun glass(count: Int, variant: Int): List<CollageSlot> {
        val columns = kotlin.math.ceil(sqrt(count.toDouble())).toInt()
        val rows = (count + columns - 1) / columns
        val centers = (0 until count).map { i ->
            val row = i / columns
            val rowCount = minOf(columns, count - row * columns)
            CollagePoint(
                ((i % columns + 0.5f + 0.2f * sin(i * 2.4f + variant)) / rowCount).coerceIn(
                    .05f,
                    .95f
                ),
                ((row + 0.5f + 0.22f * cos(i * 1.7f + variant)) / rows).coerceIn(.05f, .95f)
            )
        }
        return centers.mapIndexed { i, p ->
            centers.foldIndexed(rect(0f, 0f, 1f, 1f)) { j, cell, q ->
                if (i == j) cell else cell.clipped(
                    q.x - p.x, q.y - p.y,
                    (q.x * q.x + q.y * q.y - p.x * p.x - p.y * p.y) / 2f
                )
            }
        }
    }

    private fun strips(count: Int, variant: Int, diagonal: Boolean): List<CollageSlot> {
        val rows = maxOf(1, sqrt(count.toDouble()).toInt())
        return buildList {
            var remaining = count
            for (row in 0 until rows) {
                val n = (remaining + rows - row - 1) / (rows - row)
                val t = row.toFloat() / rows
                val b = (row + 1f) / rows
                val shift =
                    if (diagonal) (if ((row + variant) % 2 == 0) .28f else -.28f) / n else 0f
                for (col in 0 until n) {
                    val l = col.toFloat() / n
                    val r = (col + 1f) / n
                    add(
                        CollageSlot(
                            listOf(
                                CollagePoint(l, t), CollagePoint(r, t),
                                CollagePoint(if (col == n - 1) 1f else r + shift, b),
                                CollagePoint(if (col == 0) 0f else l + shift, b)
                            )
                        )
                    )
                }
                remaining -= n
            }
        }
    }

    private fun fan(count: Int, variant: Int): List<CollageSlot> {
        val center =
            CollagePoint(if (variant == 0) .37f else .66f, if (variant == 0) .43f else .31f)
        return (0 until count).map { i ->
            val a = (2 * PI * i / count + .17).toFloat()
            val b = (2 * PI * (i + 1) / count + .17).toFloat()
            rect(0f, 0f, 1f, 1f)
                .clipped(sin(a), -cos(a), sin(a) * center.x - cos(a) * center.y)
                .clipped(-sin(b), cos(b), -sin(b) * center.x + cos(b) * center.y)
        }
    }

    private fun orbit(count: Int, variant: Int): List<CollageSlot> {
        if (count == 2) {
            val circle = ellipse(.03f, .03f, .97f, .97f)
            return listOf(circle.clipped(1f, 0f, .6f), circle.clipped(-1f, 0f, -.6f))
        }
        if (variant != 0) {
            val circle = ellipse(.03f, .03f, .97f, .97f)
            return (0 until count).map { index ->
                val start = (2 * PI * index / count + PI / 4).toFloat()
                val end = (2 * PI * (index + 1) / count + PI / 4).toFloat()
                circle.clipped(sin(start), -cos(start), (sin(start) - cos(start)) / 2)
                    .clipped(-sin(end), cos(end), (cos(end) - sin(end)) / 2)
            }
        }
        val n = count - 1
        val cx = .5f
        val cy = .5f
        val inner = .20f
        val radiusX = minOf(cx, 1f - cx) - .03f
        val steps = (96 + n - 1) / n
        val ring = (0 until n).map { i ->
            val points = (0..steps).map { step ->
                val angle = 2 * PI * (i * steps + step) / (n * steps) - PI / 2
                CollagePoint(
                    (cx + radiusX * cos(angle)).toFloat(),
                    (cy + .47 * sin(angle)).toFloat()
                )
            } + (steps downTo 0).map { step ->
                val angle = 2 * PI * (i * steps + step) / (n * steps) - PI / 2
                CollagePoint(
                    (cx + inner * cos(angle)).toFloat(),
                    (cy + inner * sin(angle)).toFloat()
                )
            }
            CollageSlot(points)
        }
        val center = CollageSlot((0 until n * steps).map { step ->
            val angle = 2 * PI * step / (n * steps) - PI / 2
            CollagePoint(
                (cx + inner * cos(angle)).toFloat(),
                (cy + inner * sin(angle)).toFloat()
            )
        })
        return listOf(center) + ring
    }

    private fun waves(count: Int, variant: Int): List<CollageSlot> {
        val rows = maxOf(1, sqrt(count.toDouble()).toInt())
        return strips(count, variant, false).map { cell ->
            fun edge(x: Float, y: Float): CollagePoint {
                val offset = if (x == 0f || x == 1f) 0f else
                    sin(y * PI.toFloat() * (2 + variant) + x * 2f) * .12f / rows
                return CollagePoint((x + offset).coerceIn(0f, 1f), y)
            }
            CollageSlot((0..64).map {
                edge(
                    cell.left,
                    cell.top + (cell.bottom - cell.top) * it / 64
                )
            } +
                    (64 downTo 0).map {
                        edge(
                            cell.right,
                            cell.top + (cell.bottom - cell.top) * it / 64
                        )
                    })
        }
    }

    private fun weave(count: Int, variant: Int): List<CollageSlot> {
        var design = CollageDesign(listOf(rect(0f, 0f, 1f, 1f)))
        repeat(count - 1) { i ->
            val index = design.slots.indices.maxBy { design.slots[it].area }
            val slot = design.slots[index]
            val width = slot.right - slot.left
            val height = slot.bottom - slot.top
            val vertical = if (width == height) (i + variant) % 2 == 0 else width > height
            val fraction = .38f + .03f * variant
            design = design.split(index, vertical, if (i % 2 == 0) fraction else 1f - fraction)!!
        }
        return design.slots
    }

    private fun arches(count: Int, variant: Int): List<CollageSlot> =
        strips(count, variant, false).map { box ->
            val radius = (box.right - box.left) / 2f
            val height = minOf(radius, (box.bottom - box.top) * (if (variant == 0) .45f else .18f))
            val cx = (box.left + box.right) / 2f
            val arc = (0..48).map {
                val angle = PI + PI * it / 48
                CollagePoint(
                    (cx + radius * cos(angle)).toFloat(),
                    (box.top + height + height * sin(angle)).toFloat()
                )
            }
            CollageSlot(
                arc + listOf(
                    CollagePoint(box.right, box.bottom),
                    CollagePoint(box.left, box.bottom)
                )
            )
        }

    private fun editorial(count: Int, variant: Int): List<CollageSlot> {
        val split = if (variant == 0) .64f else .43f
        val side = if (count <= 5) count - 1 else (count - 1) / 2
        if (count > 10) {
            fun grid(
                size: Int,
                left: Float,
                top: Float,
                right: Float,
                bottom: Float
            ): List<CollageSlot> {
                val columns = kotlin.math.round(sqrt(size * (right - left) / (bottom - top)))
                    .toInt().coerceAtLeast(1)
                val rows = (size + columns - 1) / columns
                return buildList {
                    var remaining = size
                    repeat(rows) { row ->
                        val cells = (remaining + rows - row - 1) / (rows - row)
                        repeat(cells) { column ->
                            add(
                                rect(
                                    left + (right - left) * column / cells,
                                    top + (bottom - top) * row / rows,
                                    left + (right - left) * (column + 1) / cells,
                                    top + (bottom - top) * (row + 1) / rows
                                )
                            )
                        }
                        remaining -= cells
                    }
                }
            }
            return listOf(rect(0f, 0f, split, .67f)) +
                    grid(side, split, 0f, 1f, 1f) +
                    grid(count - side - 1, 0f, .67f, split, 1f)
        }
        return listOf(rect(0f, 0f, split, if (count <= 5) 1f else .67f)) +
                (0 until side).map { rect(split, it.toFloat() / side, 1f, (it + 1f) / side) } +
                if (count <= 5) emptyList() else (0 until count - 1 - side).map {
                    val n = count - 1 - side
                    rect(split * it / n, .67f, split * (it + 1) / n, 1f)
                }
    }

    private fun spiral(count: Int, variant: Int): List<CollageSlot> {
        if (count < 5) return weave(count, variant + 2)
        val innerStart = if (variant == 0) .32f else .25f
        val innerEnd = if (variant == 0) .68f else .65f
        fun polygon(vararg coordinates: Float) = CollageSlot(
            coordinates.toList().chunked(2).map { CollagePoint(it[0], it[1]) }
        )

        val arms = listOf(
            polygon(0f, 0f, 1f, 0f, innerEnd, innerStart, 0f, innerStart),
            polygon(1f, 0f, 1f, 1f, innerEnd, innerEnd, innerEnd, innerStart),
            polygon(1f, 1f, 0f, 1f, innerStart, innerEnd, innerEnd, innerEnd),
            polygon(0f, 1f, 0f, innerStart, innerStart, innerStart, innerStart, innerEnd)
        )
        return buildList {
            arms.forEachIndexed { index, arm ->
                val pieces = (count - 1) / 4 + if (index < (count - 1) % 4) 1 else 0
                val vertical = index % 2 == 0
                val start = if (vertical) arm.left else arm.top
                val end = if (vertical) arm.right else arm.bottom
                repeat(pieces) { piece ->
                    val lower = start + (end - start) * piece / pieces
                    val upper = start + (end - start) * (piece + 1) / pieces
                    add(
                        if (vertical) arm.clipped(-1f, 0f, -lower).clipped(1f, 0f, upper)
                        else arm.clipped(0f, -1f, -lower).clipped(0f, 1f, upper)
                    )
                }
            }
            add(rect(innerStart, innerStart, innerEnd, innerEnd))
        }
    }

    private fun cards(count: Int, variant: Int): List<CollageSlot> =
        strips(count, variant, false).mapIndexed { i, box ->
            val cx = (box.left + box.right) / 2f
            val cy = (box.top + box.bottom) / 2f
            val angle = (if (i % 2 == 0) .055f else -.055f) * (variant + 1)
            CollageSlot(box.points.map { p ->
                val x = (p.x - cx) * .9f
                val y = (p.y - cy) * .9f
                CollagePoint(
                    (cx + x * cos(angle) - y * sin(angle)).coerceIn(.015f, .985f),
                    (cy + x * sin(angle) + y * cos(angle)).coerceIn(.015f, .985f)
                )
            })
        }

    private fun ellipse(l: Float, t: Float, r: Float, b: Float) = CollageSlot((0 until 96).map {
        val angle = 2 * PI * it / 96
        CollagePoint(
            ((l + r) / 2 + (r - l) / 2 * cos(angle)).toFloat(),
            ((t + b) / 2 + (b - t) / 2 * sin(angle)).toFloat()
        )
    })
}