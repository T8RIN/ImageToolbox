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

import com.t8rin.collages.public.CollageConstants
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

}

data class CollageDesign(
    val slots: List<CollageSlot>
) {
    fun isValid(): Boolean =
        slots.size in 1..CollageConstants.MAX_IMAGE_COUNT && slots.all { slot ->
            slot.points.size in 3..256 && slot.points.all {
                it.x.isFinite() && it.y.isFinite() && it.x in 0f..1f && it.y in 0f..1f
            } && slot.area > 0.00001f && slot.right - slot.left > 0.001f && slot.bottom - slot.top > 0.001f
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