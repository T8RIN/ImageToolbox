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
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

internal object DecorativeCollages {
    const val count = 176

    data class Template(val id: String, val design: CollageDesign)

    private enum class Family { Petals, Canopy, Ripple, Focus, Constellation, Facets, Stamp, Ticket, Shield, Banner, Kite, Blossom }

    val templates: List<Template> by lazy {
        buildList {
            listOf(
                "crescent",
                "cloud",
                "arch",
                "shield",
                "hexagon",
                "squircle",
                "flower",
                "diamond"
            ).forEachIndexed { index, name ->
                add(
                    Template(
                        "decorative_${name}_1",
                        CollageDesign(listOf(CollageSlot(single(index))))
                    )
                )
            }
            Family.entries.forEach { family ->
                for (count in 2..15) {
                    val slots = when (family) {
                        Family.Petals -> petals(count)
                        Family.Canopy -> canopy(count)
                        Family.Ripple -> cells(count).map { box ->
                            CollageSlot(edges(box).map { p ->
                                CollagePoint(
                                    p.x + .055f * sin(p.y * 2 * PI).toFloat() * sin(p.x * PI).toFloat(),
                                    p.y + .045f * sin(p.x * 4 * PI).toFloat() * sin(p.y * PI).toFloat()
                                )
                            })
                        }

                        Family.Focus -> focus(count)
                        Family.Constellation -> constellation(count)
                        Family.Facets -> facets(count)
                        else -> cells(count).map { box ->
                            CollageSlot(silhouette(family).map { p ->
                                CollagePoint(
                                    box.left + p.x * (box.right - box.left),
                                    box.top + p.y * (box.bottom - box.top)
                                )
                            })
                        }
                    }
                    add(
                        Template(
                            "decorative_${family.name.lowercase()}_$count",
                            CollageDesign(slots)
                        )
                    )
                }
            }
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

    private fun cells(count: Int): List<CollageSlot> = buildList {
        val rows = sqrt(count.toFloat()).toInt().coerceAtLeast(1)
        repeat(rows) { row ->
            val columns = count / rows + if (row < count % rows) 1 else 0
            repeat(columns) { column ->
                add(
                    rectangle(
                        column.toFloat() / columns,
                        row.toFloat() / rows,
                        (column + 1f) / columns,
                        (row + 1f) / rows
                    )
                )
            }
        }
    }

    private fun edges(box: CollageSlot): List<CollagePoint> =
        box.points.flatMapIndexed { index, p ->
            val q = box.points[(index + 1) % box.points.size]
            (0 until 24).map { step ->
                val t = step / 24f
                CollagePoint(p.x + (q.x - p.x) * t, p.y + (q.y - p.y) * t)
            }
        }

    private fun polar(angle: Double, radius: Double, x: Float = .5f, y: Float = .5f) =
        CollagePoint(x + (radius * cos(angle)).toFloat(), y + (radius * sin(angle)).toFloat())

    private fun circle(x: Float, y: Float, radius: Float) = CollageSlot((0 until 64).map {
        polar(it * 2 * PI / 64, radius.toDouble(), x, y)
    })

    private fun petals(count: Int) = List(count) { index ->
        CollageSlot((0..64).map { step ->
            val t = step / 64.0
            polar(
                (index + t) * 2 * PI / count - PI / 2,
                .49 * sin(PI * t).coerceAtLeast(0.0).pow(.3)
            )
        })
    }

    private fun canopy(count: Int) = List(count) { index ->
        fun arc(radius: Double) = (0..64).map { step ->
            val angle = PI + PI * step / 64
            CollagePoint(
                .5f + (.49 * radius * cos(angle)).toFloat(),
                .98f + (.96 * radius * sin(angle)).toFloat()
            )
        }
        CollageSlot(
            arc((index + 1.0) / count) + if (index == 0) listOf(
                CollagePoint(
                    .5f,
                    .98f
                )
            ) else arc(index.toDouble() / count).asReversed()
        )
    }

    private fun constellation(count: Int): List<CollageSlot> =
        listOf(circle(.5f, .5f, .24f)) + List(count - 1) { index ->
            val angle = index * 2 * PI / (count - 1) - PI / 2
            val radius =
                if (count <= 3) .13f else min(.13f, (.32 * sin(PI / (count - 1))).toFloat())
            val center = polar(angle, .37)
            circle(center.x, center.y, radius)
        }

    private fun focus(count: Int): List<CollageSlot> {
        fun inner(angle: Double) = polar(angle, .22 + .035 * cos(5 * angle + PI / 2))
        return listOf(CollageSlot((0 until 100).map { inner(it * 2 * PI / 100) })) + List(count - 1) { index ->
            val start = index * 2 * PI / (count - 1) - PI / 2
            val end = (index + 1) * 2 * PI / (count - 1) - PI / 2
            val angles =
                ((0..48).map { start + (end - start) * it / 48 } + (-2..8).map { PI / 4 + it * PI / 2 }
                    .filter { it > start && it < end }).sorted()
            CollageSlot(angles.map { angle ->
                polar(
                    angle,
                    .5 / max(abs(cos(angle)), abs(sin(angle)))
                )
            } + angles.asReversed().map(::inner))
        }
    }

    private fun facets(count: Int): List<CollageSlot> {
        if (count == 2) {
            val seam = listOf(
                CollagePoint(.35f, 0f),
                CollagePoint(.65f, .28f),
                CollagePoint(.35f, .72f),
                CollagePoint(.65f, 1f)
            )
            return listOf(
                CollageSlot(listOf(CollagePoint(0f, 0f)) + seam + CollagePoint(0f, 1f)),
                CollageSlot(listOf(CollagePoint(1f, 1f)) + seam.asReversed() + CollagePoint(1f, 0f))
            )
        }
        val slots = mutableListOf(rectangle(0f, 0f, 1f, 1f))
        repeat(count - 1) { index ->
            val largest = slots.indices.maxBy { slots[it].area }
            val slot = slots.removeAt(largest)
            val angle = .39 + index * .91
            val a = cos(angle).toFloat()
            val b = sin(angle).toFloat()
            val c =
                slot.points.sumOf { (it.x * a + it.y * b).toDouble() }.toFloat() / slot.points.size
            slots.add(slot.clipped(a, b, c))
            slots.add(slot.clipped(-a, -b, -c))
        }
        return slots
    }

    private fun silhouette(family: Family): List<CollagePoint> = when (family) {
        Family.Shield -> listOf(
            CollagePoint(.04f, .04f),
            CollagePoint(.96f, .04f),
            CollagePoint(.96f, .63f),
            CollagePoint(.5f, .96f),
            CollagePoint(.04f, .63f)
        )

        Family.Banner -> listOf(
            CollagePoint(.04f, .04f),
            CollagePoint(.96f, .04f),
            CollagePoint(.96f, .96f),
            CollagePoint(.5f, .77f),
            CollagePoint(.04f, .96f)
        )

        Family.Kite -> listOf(
            CollagePoint(.5f, .02f),
            CollagePoint(.96f, .4f),
            CollagePoint(.5f, .98f),
            CollagePoint(.04f, .4f)
        )

        Family.Blossom -> (0 until 120).map { step ->
            val angle = step * 2 * PI / 120
            polar(angle, .41 + .065 * cos(angle * 6))
        }

        Family.Ticket -> edges(rectangle(.04f, .04f, .96f, .96f)).map { p ->
            val distance = abs(p.y - .5f)
            val notch = if (distance < .12f) sqrt(.12f * .12f - distance * distance) else 0f
            p.copy(
                x = p.x + when (p.x) {
                    .04f -> notch; .96f -> -notch; else -> 0f
                }
            )
        }

        Family.Stamp -> (0 until 4).flatMap { side ->
            (0 until 60).map { step ->
                val t = step / 60f
                var x = .04f + .92f * t
                var y = .04f + .035f * (1f - cos(t * 10 * PI).toFloat())
                repeat(side) { val next = 1f - y; y = x; x = next }
                CollagePoint(x, y)
            }
        }

        else -> error("Unsupported silhouette: $family")
    }

    private fun single(index: Int): List<CollagePoint> = when (index) {
        0 -> (0..64).map { polar(PI / 3 + it * 4 * PI / 3 / 64, .47) } + (0..64).map { step ->
            val t = step / 64f
            val y = .5f - (.47 * sin(PI / 3)).toFloat() + t * (.94 * sin(PI / 3)).toFloat()
            CollagePoint(.735f - .46f * sin(PI * t).toFloat(), y)
        }

        1 -> (0 until 128).map { step ->
            val angle = step * 2 * PI / 128
            val radius = .39 + .065 * cos(5 * angle - PI / 2)
            CollagePoint(
                .5f + (radius * cos(angle)).toFloat(),
                .55f + (radius * .75 * sin(angle)).toFloat()
            )
        }

        2 -> (0..64).map { step ->
            val angle = PI + PI * step / 64
            CollagePoint(.5f + (.46 * cos(angle)).toFloat(), .5f + (.46 * sin(angle)).toFloat())
        } + listOf(CollagePoint(.96f, .96f), CollagePoint(.04f, .96f))

        3 -> silhouette(Family.Shield)
        4 -> (0 until 6).map { polar(it * PI / 3, .47) }
        5 -> (0 until 128).map { step ->
            val angle = step * 2 * PI / 128
            val x = cos(angle)
            val y = sin(angle)
            CollagePoint(
                .5f + (.46 * kotlin.math.sign(x) * abs(x).pow(.5)).toFloat(),
                .5f + (.46 * kotlin.math.sign(y) * abs(y).pow(.5)).toFloat()
            )
        }

        6 -> (0 until 128).map { step ->
            val angle = step * 2 * PI / 128
            polar(angle, .4 + .07 * cos(8 * angle))
        }

        else -> silhouette(Family.Kite)
    }
}