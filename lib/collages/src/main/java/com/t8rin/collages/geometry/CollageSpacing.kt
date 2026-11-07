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

package com.t8rin.collages.geometry

import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.PointF
import android.graphics.RectF
import android.os.Build
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.hypot

internal fun isCollageEdge(start: PointF, end: PointF, bounds: RectF): Boolean {
    val epsilonX = bounds.width() * .00001f
    val epsilonY = bounds.height() * .00001f
    return (abs(start.x - bounds.left) <= epsilonX && abs(end.x - bounds.left) <= epsilonX) ||
            (abs(start.x - bounds.right) <= epsilonX && abs(end.x - bounds.right) <= epsilonX) ||
            (abs(start.y - bounds.top) <= epsilonY && abs(end.y - bounds.top) <= epsilonY) ||
            (abs(start.y - bounds.bottom) <= epsilonY && abs(end.y - bounds.bottom) <= epsilonY)
}

internal fun collageSpacingEdges(points: List<PointF>, bounds: RectF): Path = Path().apply {
    if (points.isEmpty()) return@apply
    val outerEdge = points.indices.firstOrNull {
        isCollageEdge(points[it], points[(it + 1) % points.size], bounds)
    }

    fun extended(start: PointF, end: PointF): PointF {
        val dx = end.x - start.x
        val dy = end.y - start.y
        val scale = maxOf(bounds.width(), bounds.height()) * 2 / hypot(dx, dy).coerceAtLeast(.0001f)
        return PointF(end.x + dx * scale, end.y + dy * scale)
    }

    var connected = false
    repeat(points.size) { offset ->
        val index = ((outerEdge?.plus(1) ?: 0) + offset) % points.size
        val start = points[index]
        val end = points[(index + 1) % points.size]
        if (isCollageEdge(start, end, bounds)) {
            if (connected) {
                val previous = points[(index + points.size - 1) % points.size]
                val last = extended(previous, start)
                lineTo(last.x, last.y)
            }
            connected = false
        } else {
            if (!connected) {
                val first = if (outerEdge == null) start else extended(end, start)
                moveTo(first.x, first.y)
                lineTo(start.x, start.y)
            }
            lineTo(end.x, end.y)
            connected = true
        }
    }
    if (outerEdge == null) close()
}

internal fun Path.collageSpacingEdges(bounds: RectF): Path {
    val pathBounds = RectF().also { computeBounds(it, true) }
    val epsilonX = bounds.width() * .00001f
    val epsilonY = bounds.height() * .00001f
    if (pathBounds.left > bounds.left + epsilonX && pathBounds.top > bounds.top + epsilonY &&
        pathBounds.right < bounds.right - epsilonX && pathBounds.bottom < bounds.bottom - epsilonY
    ) return this
    val result = Path()
    val contour = mutableListOf<PointF>()
    fun addContour() {
        if (contour.size > 1 && contour.first() == contour.last()) contour.removeAt(contour.lastIndex)
        result.addPath(collageSpacingEdges(contour, bounds))
        contour.clear()
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val points = approximate(.1f)
        var fraction = -1f
        for (index in points.indices step 3) {
            if (points[index] == fraction) addContour()
            contour.add(PointF(points[index + 1], points[index + 2]))
            fraction = points[index]
        }
        addContour()
    } else {
        val measure = PathMeasure(this, true)
        val point = FloatArray(2)
        do {
            val steps = ceil(measure.length * 2).toInt().coerceAtLeast(1)
            for (step in 0..steps) {
                if (measure.getPosTan(measure.length * step / steps, point, null)) {
                    contour.add(PointF(point[0], point[1]))
                }
            }
            addContour()
        } while (measure.nextContour())
    }
    return result
}