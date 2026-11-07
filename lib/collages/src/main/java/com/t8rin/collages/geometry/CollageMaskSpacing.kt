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

import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.os.Build
import kotlin.math.abs
import kotlin.math.ceil

internal fun Path.flattenMask(): Path = Path().also { result ->
    result.fillType = fillType
    val measure = PathMeasure(this, true)
    val contour = Path()
    val point = FloatArray(2)
    do {
        if (measure.length <= 0f) continue
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            contour.reset()
            if (!measure.getSegment(0f, measure.length, contour, true)) continue
            val points = contour.approximate(.2f)
            if (points.isEmpty()) continue
            result.moveTo(points[1], points[2])
            for (index in 3 until points.size step 3) {
                result.lineTo(points[index + 1], points[index + 2])
            }
        } else {
            val steps = ceil(measure.length * 2).toInt().coerceAtLeast(1)
            for (step in 0..steps) {
                measure.getPosTan(measure.length * step / steps, point, null)
                if (step == 0) result.moveTo(point[0], point[1])
                else result.lineTo(point[0], point[1])
            }
        }
        result.close()
    } while (measure.nextContour())
}

internal fun Path.insetMask(
    distance: Float,
    spacingEdges: Path = this,
    keepLargest: Boolean = false
) {
    if (distance <= 0f) return
    var minimumArea = Double.POSITIVE_INFINITY
    var originalContours = 0
    if (!keepLargest) {
        forEachContour { _, area ->
            originalContours++
            if (area > .0001) minimumArea = minOf(minimumArea, area)
        }
    }
    val border = Path()
    Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = distance * 2
        strokeCap = Paint.Cap.SQUARE
        strokeJoin = Paint.Join.MITER
        strokeMiter = 2f
    }.getFillPath(spacingEdges, border)
    op(border, Path.Op.DIFFERENCE)
    if (!PathMeasure(this, true).nextContour()) return
    val singleOutline = keepLargest || originalContours == 1 && fillType == Path.FillType.WINDING
    if (singleOutline || minimumArea.isFinite()) {
        var largestArea = 0.0
        val clean = Path().also { it.fillType = fillType }
        forEachContour { contour, area ->
            // Path operations can leave tiny closed loops at nearly tangent edges.
            if (singleOutline) {
                if (area > largestArea) {
                    largestArea = area
                    clean.set(contour)
                }
            } else if (area >= minimumArea * .0001) clean.addPath(contour)
        }
        set(clean)
    }
}

private inline fun Path.forEachContour(block: (Path, Double) -> Unit) {
    val measure = PathMeasure(this, true)
    val point = FloatArray(2)
    do {
        val length = measure.length
        if (length <= 0f) continue
        measure.getPosTan(0f, point, null)
        val firstX = point[0].toDouble()
        val firstY = point[1].toDouble()
        var x = firstX
        var y = firstY
        var area = 0.0
        for (step in 1..256) {
            measure.getPosTan(length * step / 256, point, null)
            area += x * point[1] - point[0] * y
            x = point[0].toDouble()
            y = point[1].toDouble()
        }
        area += x * firstY - firstX * y
        val contour = Path()
        measure.getSegment(0f, length, contour, true)
        contour.close()
        block(contour, abs(area) / 2)
    } while (measure.nextContour())
}