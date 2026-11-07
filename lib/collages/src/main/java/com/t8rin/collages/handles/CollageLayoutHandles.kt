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

package com.t8rin.collages.handles

import android.graphics.RectF
import com.t8rin.collages.model.CollageLayout
import com.t8rin.collages.model.CollagePoint
import kotlin.math.roundToInt

internal fun CollageLayout.withRectangularHandles(): CollageLayout {
    if (paramsManager != null || photoItemList.size < 2) return this
    val items = photoItemList
    fun coordinate(value: Float) = (value * 100_000).roundToInt() / 100_000f
    if (items.any { item ->
            if (item.path != null || item.clearPath != null || !item.clearAreaPoints.isNullOrEmpty()) {
                return@any true
            }
            item.normalizedPath?.let { path ->
                val bounds = RectF()
                return@any !path.isRect(bounds) || coordinate(bounds.left) != 0f ||
                        coordinate(bounds.top) != 0f || coordinate(bounds.right) != 1f ||
                        coordinate(bounds.bottom) != 1f
            }
            val points = item.shape?.map { coordinate(it.x) to coordinate(it.y) }
                ?: item.pointList.map { coordinate(it.x) to coordinate(it.y) }
            if (points.isEmpty()) return@any item.shape != null
            val left = if (item.shape == null) 0f else coordinate(item.bound.left)
            val top = if (item.shape == null) 0f else coordinate(item.bound.top)
            val right = if (item.shape == null) 1f else coordinate(item.bound.right)
            val bottom = if (item.shape == null) 1f else coordinate(item.bound.bottom)
            val corners = setOf(left to top, right to top, right to bottom, left to bottom)
            !points.containsAll(corners) || points.any { (x, y) ->
                (x != left && x != right) && (y != top && y != bottom)
            } || (points + points.first()).zipWithNext().any { (a, b) ->
                a.first != b.first && a.second != b.second
            }
        }) return this
    fun overlaps(): Boolean = items.indices.any { index ->
        val bounds = items[index].bound
        (index + 1 until items.size).any { otherIndex ->
            val other = items[otherIndex].bound
            minOf(bounds.right, other.right) - maxOf(bounds.left, other.left) > .00001f &&
                    minOf(bounds.bottom, other.bottom) - maxOf(bounds.top, other.top) > .00001f
        }
    }
    if (overlaps()) return this
    val builder = ParamsManagerBuilder {
        if (overlaps()) throw ParamsManager.InvalidValues()
    }

    fun parameters(values: List<Float>) = values.map(::coordinate).distinct().sorted()
        .filter { it > 0f && it < 1f }.associateWith(builder::param)

    val xParams = parameters(items.flatMap { listOf(it.bound.left, it.bound.right) })
    val yParams = parameters(items.flatMap { listOf(it.bound.top, it.bound.bottom) })
    items.forEach { item ->
        val original = RectF(
            coordinate(item.bound.left), coordinate(item.bound.top),
            coordinate(item.bound.right), coordinate(item.bound.bottom)
        )
        val left = xParams[original.left]
        val right = xParams[original.right]
        val top = yParams[original.top]
        val bottom = yParams[original.bottom]
        builder.addBoxedItem(
            photoItem = item,
            xParams = listOfNotNull(left, right),
            yParams = listOfNotNull(top, bottom),
            boxParams = { values ->
                RectF(
                    left?.let { values[it] } ?: original.left,
                    top?.let { values[it] } ?: original.top,
                    right?.let { values[it] } ?: original.right,
                    bottom?.let { values[it] } ?: original.bottom
                )
            },
            action = { photo, _, bounds ->
                if (photo.shape != null) photo.shape = listOf(
                    CollagePoint(bounds.left, bounds.top),
                    CollagePoint(bounds.right, bounds.top),
                    CollagePoint(bounds.right, bounds.bottom),
                    CollagePoint(bounds.left, bounds.bottom)
                )
            }
        )
    }
    val (manager, photoItems) = builder.build()
    return copy(paramsManager = manager, photoItemList = photoItems)
}