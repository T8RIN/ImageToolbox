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
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

internal object ArtisticCollages {
    const val count = 4 * 99 + 5 * 80

    data class Template(val id: String, val design: CollageDesign)

    val templates: List<Template> by lazy {
        buildList {
            for (count in 2..100) {
                add(Template("artistic_honeycomb_$count", CollageDesign(honeycomb(count))))
                add(Template("artistic_terrace_$count", CollageDesign(cells(count, ::terrace))))
                add(Template("artistic_leaves_$count", CollageDesign(cells(count, ::leaf))))
                add(
                    Template(
                        "artistic_ribbon_$count",
                        CollageDesign(
                            cells(
                                count,
                                ::ribbon,
                                maxOf(2, sqrt(count.toFloat()).roundToInt())
                            )
                        )
                    )
                )
                if (count > 20) {
                    add(Template("artistic_pinwheel_$count", CollageDesign(pinwheel(count))))
                    add(Template("artistic_gallery_$count", CollageDesign(gallery(count))))
                    add(Template("artistic_chevron_$count", CollageDesign(cells(count, ::chevron))))
                    add(Template("artistic_bricks_$count", CollageDesign(cells(count, ::bricks))))
                    add(Template("artistic_mosaic_$count", CollageDesign(mosaic(count))))
                }
            }
        }
    }

    private fun cells(
        count: Int,
        shape: (Int, Int, Int, Int) -> List<CollagePoint>,
        rows: Int = sqrt(count.toFloat()).roundToInt()
    ): List<CollageSlot> = buildList {
        repeat(rows) { row ->
            val columns = count / rows + if (row < count % rows) 1 else 0
            repeat(columns) { column -> add(CollageSlot(shape(column, row, columns, rows))) }
        }
    }

    private fun terrace(column: Int, row: Int, columns: Int, rows: Int): List<CollagePoint> {
        fun edge(index: Int): List<CollagePoint> {
            val x = index.toFloat() / columns
            val shift = if (index == 0 || index == columns) 0f
            else (if ((index + row) % 2 == 0) .3f else -.3f) / columns
            return listOf(0f, .3f, .3f, .7f, .7f, 1f).mapIndexed { step, y ->
                CollagePoint(x + if (step in 2..3) shift else 0f, (row + y) / rows)
            }
        }
        return edge(column) + edge(column + 1).asReversed()
    }

    private fun ribbon(column: Int, row: Int, columns: Int, rows: Int): List<CollagePoint> {
        fun edge(index: Int) = (0..24).map { step ->
            val x = (column + step / 24f) / columns
            val wave = if (index == 0 || index == rows) 0f
            else .18f * sin(x * 2f * PI.toFloat()) / rows
            CollagePoint(x, index.toFloat() / rows + wave)
        }
        return edge(row) + edge(row + 1).asReversed()
    }

    private fun leaf(column: Int, row: Int, columns: Int, rows: Int): List<CollagePoint> {
        return (0..64).map { step ->
            val t = step / 32f
            val x: Float
            val y: Float
            if (t <= 1f) {
                x = 1f - (1f - t) * (1f - t)
                y = t * t
            } else {
                val u = 2f - t
                x = u * u
                y = 1f - (1f - u) * (1f - u)
            }
            CollagePoint(
                (column + if ((column + row) % 2 == 0) x else 1f - x) / columns,
                (row + y) / rows
            )
        }
    }

    private fun honeycomb(count: Int): List<CollageSlot> {
        val rows = sqrt(count.toFloat()).roundToInt()
        val polygons = buildList {
            repeat(rows) { row ->
                val columns = count / rows + if (row < count % rows) 1 else 0
                repeat(columns) { column ->
                    add((0..5).map { vertex ->
                        val angle = (vertex * PI / 3 + PI / 6).toFloat()
                        CollagePoint(
                            (column + .5f * (row % 2)) * sqrt(3f) + cos(angle),
                            row * 1.5f + sin(angle)
                        )
                    })
                }
            }
        }
        val left = polygons.minOf { it.minOf(CollagePoint::x) }
        val right = polygons.maxOf { it.maxOf(CollagePoint::x) }
        val top = polygons.minOf { it.minOf(CollagePoint::y) }
        val bottom = polygons.maxOf { it.maxOf(CollagePoint::y) }
        return polygons.map { points ->
            CollageSlot(points.map {
                CollagePoint(
                    (it.x - left) / (right - left),
                    (it.y - top) / (bottom - top)
                )
            })
        }
    }

    private fun rectangle(left: Float, top: Float, right: Float, bottom: Float) = CollageSlot(
        listOf(
            CollagePoint(left, top),
            CollagePoint(right, top),
            CollagePoint(right, bottom),
            CollagePoint(left, bottom)
        )
    )

    private fun grid(count: Int, rows: Int) = cells(count, { column, row, columns, totalRows ->
        rectangle(
            column.toFloat() / columns, row.toFloat() / totalRows,
            (column + 1f) / columns, (row + 1f) / totalRows
        ).points
    }, rows)

    private fun pinwheel(count: Int): List<CollageSlot> = buildList {
        add(rectangle(.3f, .3f, .7f, .7f))
        repeat(4) { side ->
            val slots = (count - 1) / 4 + if (side < (count - 1) % 4) 1 else 0
            grid(slots, sqrt(slots * .3f / .7f).roundToInt().coerceAtLeast(1)).forEach { slot ->
                add(CollageSlot(slot.points.map { point ->
                    var x = point.x * .7f
                    var y = point.y * .3f
                    repeat(side) {
                        val nextX = 1f - y
                        y = x
                        x = nextX
                    }
                    CollagePoint(x, y)
                }))
            }
        }
    }

    private fun gallery(count: Int): List<CollageSlot> = listOf(
        rectangle(0f, 0f, .42f, .6f), rectangle(0f, .6f, .42f, 1f)
    ) + grid(count - 2, sqrt((count - 2) / .58f).roundToInt()).map { slot ->
        CollageSlot(slot.points.map { CollagePoint(.42f + it.x * .58f, it.y) })
    }

    private fun chevron(column: Int, row: Int, columns: Int, rows: Int): List<CollagePoint> {
        val left = column.toFloat() / columns
        val right = (column + 1f) / columns
        val positions = (listOf(left, right) + (1..5).map { it / 6f }
            .filter { it > left && it < right }).sorted()

        fun edge(index: Int) = positions.map { x ->
            val shift = if (index == 0 || index == rows) 0f
            else (abs((x * 3f) % 1f - .5f) * 4f - 1f) * .16f / rows
            CollagePoint(x, index.toFloat() / rows + shift)
        }
        return edge(row) + edge(row + 1).asReversed()
    }

    private fun bricks(column: Int, row: Int, columns: Int, rows: Int): List<CollagePoint> {
        fun edge(index: Int): Float = when {
            index == 0 -> 0f
            index == columns -> 1f
            row % 2 == 0 -> index.toFloat() / columns
            else -> (index - .3f) / (columns - .6f)
        }
        return rectangle(
            edge(column),
            row.toFloat() / rows,
            edge(column + 1),
            (row + 1f) / rows
        ).points
    }

    private fun mosaic(count: Int): List<CollageSlot> {
        val centers = cells(count, { column, row, columns, rows ->
            val index = row * columns + column
            listOf(
                CollagePoint(
                    (column + .5f + .24f * sin(index * 2.2f + .7f)) / columns,
                    (row + .5f + .24f * cos(index * 1.8f + .3f)) / rows
                )
            )
        }).map { it.points.single() }
        return centers.mapIndexed { index, center ->
            centers.foldIndexed(rectangle(0f, 0f, 1f, 1f)) { otherIndex, slot, other ->
                if (index == otherIndex) slot else slot.clipped(
                    other.x - center.x, other.y - center.y,
                    (other.x * other.x + other.y * other.y - center.x * center.x - center.y * center.y) / 2f
                )
            }
        }
    }
}