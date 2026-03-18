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

import android.graphics.PointF

internal abstract class Handle {
    open fun getAngle(): Float? {
        return 0f
    }

    abstract fun draggablePoint(manager: ParamsManager): PointF
    abstract fun tryDrag(point: PointF, manager: ParamsManager): PointF?
}

internal abstract class LinearHandle(
    private val managedParam: ParamT,
    private val direction: PointF
) : Handle() {
    override fun getAngle(): Float? {
        return Math.toDegrees(kotlin.math.atan2(direction.y, direction.x).toDouble()).toFloat()
    }

    protected abstract fun computeDraggablePoint(values: FloatArray): PointF
    protected abstract fun pointToValue(point: PointF): Float

    override fun draggablePoint(manager: ParamsManager): PointF =
        computeDraggablePoint(manager.valuesRef())

    override fun tryDrag(point: PointF, manager: ParamsManager): PointF? {
        val values = manager.valuesRef()
        val initialPoint = computeDraggablePoint(values)

        val dx = point.x - initialPoint.x
        val dy = point.y - initialPoint.y

        val norm = direction.x * dx + direction.y * dy
        val clippedPoint = PointF(
            initialPoint.x + direction.x * norm,
            initialPoint.y + direction.y * norm
        )

        val newValue = pointToValue(clippedPoint)

        return try {
            manager.updateParams(listOf(managedParam), floatArrayOf(newValue))
            clippedPoint
        } catch (e: ParamsManager.InvalidValues) {
            null
        }
    }
}

internal class XHandle(
    private val managedParam: ParamT,
    private val yProvider: (values: FloatArray) -> Float
) : LinearHandle(managedParam, PointF(1f, 0f)) {
    override fun computeDraggablePoint(values: FloatArray): PointF =
        PointF(values[managedParam], yProvider(values))

    override fun pointToValue(point: PointF): Float = point.x
}

internal class YHandle(
    private val xProvider: (values: FloatArray) -> Float,
    private val managedParam: ParamT
) : LinearHandle(managedParam, PointF(0f, 1f)) {
    override fun computeDraggablePoint(values: FloatArray): PointF =
        PointF(xProvider(values), values[managedParam])

    override fun pointToValue(point: PointF): Float = point.y
}
