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

package com.t8rin.collages.utils

import android.graphics.RectF
import com.t8rin.collages.CollagePoint
import com.t8rin.collages.model.CollageLayout
import kotlin.math.abs
import kotlin.math.roundToInt

internal fun CollageLayout.withRectangularHandles(): CollageLayout {
    if (paramsManager != null || photoItemList.size < 2) return this
    val items = photoItemList
    fun coordinate(value: Float) = (value * 100_000).roundToInt() / 100_000f
    if (items.any { item ->
            val points = item.shape?.map { coordinate(it.x) to coordinate(it.y) }
                ?: item.pointList.map { coordinate(it.x) to coordinate(it.y) }
            val left = if (item.shape == null) 0f else coordinate(item.bound.left)
            val top = if (item.shape == null) 0f else coordinate(item.bound.top)
            val right = if (item.shape == null) 1f else coordinate(item.bound.right)
            val bottom = if (item.shape == null) 1f else coordinate(item.bound.bottom)
            item.normalizedPath != null || item.path != null || item.clearPath != null || !item.clearAreaPoints.isNullOrEmpty() ||
                    points.toSet() != setOf(
                left to top,
                right to top,
                right to bottom,
                left to bottom
            ) ||
                    item.bound.width() < ParamsManager.minSize - .0001f ||
                    item.bound.height() < ParamsManager.minSize - .0001f
        }) return this
    val area = items.sumOf { (it.bound.width() * it.bound.height()).toDouble() }
    if (abs(area - 1.0) > .0001) return this
    val builder = ParamsManagerBuilder()
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