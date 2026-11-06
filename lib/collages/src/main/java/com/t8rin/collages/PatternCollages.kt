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
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

internal object PatternCollages {
    const val count = 200

    data class Template(val id: String, val design: CollageDesign)

    private enum class Family {
        Aperture, Sunflower, Dunes, Scales, Zipper, Vault, Pebbles,
        Bridges, Courtyard, Rosette, Filmstrip, Waterfall, StainedGlass, Bookends
    }

    val templates: List<Template> by lazy {
        buildList {
            listOf("ginkgo", "pennant", "quatrefoil", "lantern").forEachIndexed { index, name ->
                add(Template("pattern_${name}_1", CollageDesign(listOf(single(index)))))
            }
            Family.entries.forEach { family ->
                for (count in 2..15) {
                    val slots = when (family) {
                        Family.Aperture -> aperture(count)
                        Family.Sunflower -> voronoi(count, false)
                        Family.StainedGlass -> voronoi(count, true)
                        Family.Dunes -> dunes(count)
                        Family.Courtyard -> courtyard(count)
                        Family.Rosette -> rosette(count)
                        Family.Filmstrip -> filmstrip(count)
                        Family.Bookends -> bookends(count)
                        else -> tiles(count, family)
                    }
                    add(Template("pattern_${family.name.lowercase()}_$count", CollageDesign(slots)))
                }
            }
        }
    }

    private fun rect(left: Float, top: Float, right: Float, bottom: Float) = CollageSlot(
        listOf(
            CollagePoint(left, top), CollagePoint(right, top),
            CollagePoint(right, bottom), CollagePoint(left, bottom)
        )
    )

    private fun polar(angle: Double, radius: Double, x: Float = .5f, y: Float = .5f) =
        CollagePoint(x + (cos(angle) * radius).toFloat(), y + (sin(angle) * radius).toFloat())

    private fun aperture(count: Int): List<CollageSlot> {
        fun edge(angle: Double, radius: Double): CollagePoint {
            val turn = angle + .65 * sin(PI * radius)
            val reach = .5 / max(abs(cos(turn)), abs(sin(turn)))
            return polar(turn, radius * reach).let {
                CollagePoint(it.x.coerceIn(0f, 1f), it.y.coerceIn(0f, 1f))
            }
        }
        return List(count) { index ->
            val start = 2 * PI * index / count - PI / 2
            val end = 2 * PI * (index + 1) / count - PI / 2
            val corners = (0..3).map { PI / 4 + it * PI / 2 }.map {
                if (it > 3 * PI / 2) it - 2 * PI else it
            }.filter { it > start && it < end }
            val rim = ((0..32).map { start + (end - start) * it / 32 } + corners).sorted()
            CollageSlot(
                (0..32).map { edge(start, it / 32.0) } +
                        rim.map { edge(it, 1.0) } +
                        (32 downTo 1).map { edge(end, it / 32.0) }
            )
        }
    }

    private fun voronoi(count: Int, stained: Boolean): List<CollageSlot> {
        val centers = List(count) { index ->
            if (stained) {
                val angle = index * 2 * PI / count + .32
                polar(angle, if (index % 3 == 0) .23 else .44, .47f, .52f)
            } else {
                polar(index * PI * (3 - sqrt(5.0)) + .24, .46 * sqrt((index + .5) / count))
            }
        }
        return centers.mapIndexed { index, p ->
            centers.foldIndexed(rect(0f, 0f, 1f, 1f)) { other, slot, q ->
                if (index == other) slot else slot.clipped(
                    q.x - p.x, q.y - p.y,
                    (q.x * q.x + q.y * q.y - p.x * p.x - p.y * p.y) / 2
                )
            }
        }
    }

    private fun dunes(count: Int): List<CollageSlot> {
        val rows = max(2, sqrt(count.toFloat()).toInt())
        fun edge(row: Int, x: Float): CollagePoint {
            val y = row.toFloat() / rows
            return CollagePoint(
                x,
                y + (.18 * sin(PI * y) * sin(PI * x) * cos(2 * PI * x + y)).toFloat()
            )
        }
        return buildList {
            repeat(rows) { row ->
                val columns = count / rows + if (row < count % rows) 1 else 0
                repeat(columns) { column ->
                    fun line(y: Int) =
                        (0..48).map { step -> edge(y, (column + step / 48f) / columns) }
                    add(CollageSlot(line(row) + line(row + 1).asReversed()))
                }
            }
        }
    }

    private fun courtyard(count: Int): List<CollageSlot> {
        val center = rect(.28f, .24f, .72f, .76f)
        if (count == 2) return listOf(
            center,
            CollageSlot(
                listOf(
                    CollagePoint(0f, 0f), CollagePoint(1f, 0f), CollagePoint(1f, 1f),
                    CollagePoint(.76f, 1f), CollagePoint(.76f, .2f), CollagePoint(.24f, .2f),
                    CollagePoint(.24f, 1f), CollagePoint(0f, 1f)
                )
            )
        )
        fun perimeter(t: Double, inner: Boolean): CollagePoint {
            val angle = t * 2 * PI - PI / 2
            val radius = if (inner) minOf(.22 / abs(cos(angle)), .26 / abs(sin(angle)))
            else .5 / max(abs(cos(angle)), abs(sin(angle)))
            return polar(angle, radius)
        }

        val corners = listOf(.125, .375, .625, .875) + listOf(
            atan2(.26, .22) / (2 * PI) + .25,
            atan2(.26, -.22) / (2 * PI) + .25,
            atan2(-.26, -.22) / (2 * PI) + 1.25,
            atan2(-.26, .22) / (2 * PI) + .25
        )
        return listOf(center) + List(count - 1) { index ->
            val start = index.toDouble() / (count - 1)
            val end = (index + 1.0) / (count - 1)
            val steps = ((0..32).map { start + (end - start) * it / 32 } +
                    corners.filter { it > start && it < end }).sorted()
            CollageSlot(steps.map { perimeter(it, false) } + steps.asReversed()
                .map { perimeter(it, true) })
        }
    }

    private fun rosette(count: Int): List<CollageSlot> = List(count) { index ->
        val start = index * 2 * PI / count - PI / 2
        val end = (index + 1) * 2 * PI / count - PI / 2
        fun side(angle: Double) = (0..32).map { step ->
            val t = step / 32.0
            polar(angle + .45 * sin(PI * t), .43 * t)
        }

        val rim = (0..48).map { step ->
            val t = step / 48.0
            polar(start + (end - start) * t, .43 + .06 * sin(PI * t).pow(2))
        }
        CollageSlot(side(start) + rim + side(end).asReversed())
    }

    private fun filmstrip(count: Int): List<CollageSlot> {
        val hero = rect(0f, .05f, .68f, .95f)
        return listOf(hero) + List(count - 1) { index ->
            val y = index.toFloat() / (count - 1)
            rect(.72f, y, 1f, y + 1f / (count - 1))
        }
    }

    private fun bookends(count: Int): List<CollageSlot> {
        if (count == 2) return listOf(rect(0f, .18f, .42f, 1f), rect(.42f, 0f, 1f, .82f))
        val left = (count - 1) / 2
        val right = count - 1 - left
        return listOf(rect(.27f, 0f, .73f, 1f)) +
                List(left) { rect(0f, (it + .15f) / left, .27f, (it + .85f) / left) } +
                List(right) { rect(.73f, (it + .15f) / right, 1f, (it + .85f) / right) }
    }

    private fun tiles(count: Int, family: Family): List<CollageSlot> = buildList {
        val rows = sqrt(count.toFloat()).toInt().coerceAtLeast(1)
        repeat(rows) { row ->
            val columns = count / rows + if (row < count % rows) 1 else 0
            repeat(columns) { column ->
                val points = when (family) {
                    Family.Vault -> listOf(CollagePoint(0f, 1f), CollagePoint(0f, .5f)) +
                            (0..48).map { step ->
                                val t = step / 48f
                                CollagePoint(t, .5f * abs(2 * t - 1).pow(2))
                            } + CollagePoint(1f, 1f)

                    Family.Pebbles -> (0 until 96).map { step ->
                        val angle = step * 2 * PI / 96
                        val radius = .43 + .045 * sin(3 * angle + row + column)
                        polar(angle, radius)
                    }

                    Family.Bridges -> {
                        fun arc(y: Float, depth: Float) = (0..48).map { step ->
                            val x = step / 48f
                            CollagePoint(x, y + depth * sin(PI * x).toFloat())
                        }
                        arc(0f, .22f) + arc(.68f, .32f).asReversed()
                    }

                    Family.Waterfall -> {
                        val shift = if ((column + row) % 2 == 0) .16f else 0f
                        rect(.03f, shift, .97f, .84f + shift).points
                    }

                    Family.Zipper -> {
                        fun edge(x: Float) =
                            listOf(0f, .28f, .42f, .58f, .72f, 1f).mapIndexed { step, y ->
                                val offset = if (step in 2..3) .22f else 0f
                                CollagePoint(x + offset, y)
                            }
                        edge(0f) + edge(.76f).asReversed()
                    }

                    else -> {
                        listOf(CollagePoint(0f, 0f), CollagePoint(1f, 0f)) +
                                (0..48).map { polar(it * PI / 48, .5) }
                    }
                }
                add(CollageSlot(points.map {
                    CollagePoint(
                        (column + it.x) / columns,
                        (row + it.y) / rows
                    )
                }))
            }
        }
    }

    private fun single(index: Int): CollageSlot = when (index) {
        0 -> CollageSlot(
            listOf(CollagePoint(.46f, .98f), CollagePoint(.54f, .98f)) +
                    (0..96).map { step ->
                        val angle = -.12 * PI - .76 * PI * step / 96
                        val radius = .85 - .12 * cos((angle + PI / 2) * 2).pow(12)
                        CollagePoint(
                            .5f + (.54 * radius * cos(angle)).toFloat(),
                            .94f + (radius * sin(angle)).toFloat()
                        )
                    })

        1 -> CollageSlot(
            listOf(
                CollagePoint(.05f, .05f), CollagePoint(.95f, .05f), CollagePoint(.95f, .95f),
                CollagePoint(.5f, .73f), CollagePoint(.05f, .95f)
            )
        )

        2 -> CollageSlot((0 until 128).map { step ->
            val angle = step * 2 * PI / 128
            polar(angle, .4 + .09 * cos(4 * angle))
        })

        else -> CollageSlot(
            listOf(
                CollagePoint(.3f, .02f), CollagePoint(.7f, .02f), CollagePoint(.96f, .3f),
                CollagePoint(.96f, .7f), CollagePoint(.7f, .98f), CollagePoint(.3f, .98f),
                CollagePoint(.04f, .7f), CollagePoint(.04f, .3f)
            )
        )
    }
}