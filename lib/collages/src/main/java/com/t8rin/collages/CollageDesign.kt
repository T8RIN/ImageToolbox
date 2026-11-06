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

import kotlin.math.abs

/** Coordinates are normalized to the canvas, independent of preview and export size. */
data class CollagePoint(
    val x: Float,
    val y: Float
)

data class CollageSlot(
    val points: List<CollagePoint>
) {
    val left get() = points.minOf { it.x }
    val top get() = points.minOf { it.y }
    val right get() = points.maxOf { it.x }
    val bottom get() = points.maxOf { it.y }
    val area
        get() = abs(points.indices.sumOf { i ->
            val a = points[i]
            val b = points[(i + 1) % points.size]
            (a.x * b.y - b.x * a.y).toDouble()
        }.toFloat()) / 2f

    fun clipped(a: Float, b: Float, c: Float): CollageSlot {
        val result = mutableListOf<CollagePoint>()
        points.indices.forEach { i ->
            val p = points[i]
            val q = points[(i + 1) % points.size]
            val d = a * p.x + b * p.y - c
            val e = a * q.x + b * q.y - c
            if (d <= 0f) result.add(p)
            if ((d < 0f && e > 0f) || (d > 0f && e < 0f)) {
                val t = d / (d - e)
                result.add(
                    CollagePoint(
                        if (b == 0f) c / a else p.x + (q.x - p.x) * t,
                        if (a == 0f) c / b else p.y + (q.y - p.y) * t
                    )
                )
            }
        }
        return CollageSlot(result)
    }
}

data class CollageDesign(
    val slots: List<CollageSlot>
) {
    fun isValid(): Boolean = slots.size in 1..20 && slots.all { slot ->
        slot.points.size in 3..256 && slot.points.all {
            it.x.isFinite() && it.y.isFinite() && it.x in 0f..1f && it.y in 0f..1f
        } && slot.area > 0.00001f && slot.right - slot.left > 0.001f && slot.bottom - slot.top > 0.001f
    }

    fun split(index: Int, vertical: Boolean, fraction: Float = 0.5f): CollageDesign? {
        val slot = slots.getOrNull(index) ?: return null
        if (slots.size >= 20 || fraction !in .2f.. .8f) return null
        val a = if (vertical) 1f else 0f
        val b = if (vertical) 0f else 1f
        val start = if (vertical) slot.left else slot.top
        val end = if (vertical) slot.right else slot.bottom
        val edge = start + (end - start) * fraction
        val pieces = listOf(slot.clipped(a, b, edge), slot.clipped(-a, -b, -edge))
        return copy(slots = slots.take(index) + pieces + slots.drop(index + 1)).takeIf { it.isValid() }
    }
}

data class CollageSlotStyle(
    val cornerRadius: Float? = null,
    val borderWidth: Float = 0f,
    val borderColor: Int = -1,
    val fit: Boolean = false,
    val shadowRadius: Float = 0f,
    val shadowColor: Int = 0x55000000
)