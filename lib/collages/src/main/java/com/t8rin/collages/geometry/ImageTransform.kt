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

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

internal object ImageTransform {

    fun resize(
        values: FloatArray,
        oldWidth: Float,
        oldHeight: Float,
        width: Float,
        height: Float,
        scaleImage: Boolean = true
    ): FloatArray {
        if (oldWidth <= 0f || oldHeight <= 0f || width <= 0f || height <= 0f) return values.copyOf()
        val scale = if (scaleImage) sqrt((width / oldWidth) * (height / oldHeight)) else 1f
        return values.copyOf().apply {
            this[0] *= scale
            this[1] *= scale
            this[2] = (values[2] - oldWidth / 2f) * scale + width / 2f
            this[3] *= scale
            this[4] *= scale
            this[5] = (values[5] - oldHeight / 2f) * scale + height / 2f
        }
    }

    fun fit(
        values: FloatArray,
        imageWidth: Float,
        imageHeight: Float,
        bounds: FloatArray
    ): FloatArray {
        if (imageWidth <= 0f || imageHeight <= 0f) return values.copyOf()
        val result = values.copyOf()
        var local = inverseBounds(result, bounds) ?: return result
        val scale =
            max(1f, max((local[2] - local[0]) / imageWidth, (local[3] - local[1]) / imageHeight))
        if (scale > 1f) {
            val cx = (bounds[0] + bounds[2]) / 2f
            val cy = (bounds[1] + bounds[3]) / 2f
            result[0] *= scale
            result[1] *= scale
            result[2] = (result[2] - cx) * scale + cx
            result[3] *= scale
            result[4] *= scale
            result[5] = (result[5] - cy) * scale + cy
            local = inverseBounds(result, bounds) ?: return result
        }
        val dx = when {
            local[0] < 0f -> local[0]
            local[2] > imageWidth -> local[2] - imageWidth
            else -> 0f
        }
        val dy = when {
            local[1] < 0f -> local[1]
            local[3] > imageHeight -> local[3] - imageHeight
            else -> 0f
        }
        result[2] += result[0] * dx + result[1] * dy
        result[5] += result[3] * dx + result[4] * dy
        return result
    }

    fun covers(
        values: FloatArray,
        imageWidth: Float,
        imageHeight: Float,
        bounds: FloatArray
    ): Boolean {
        val local = inverseBounds(values, bounds) ?: return false
        return local[0] >= -0.01f && local[1] >= -0.01f &&
                local[2] <= imageWidth + 0.01f && local[3] <= imageHeight + 0.01f
    }

    private fun inverseBounds(values: FloatArray, bounds: FloatArray): FloatArray? {
        val determinant = values[0] * values[4] - values[1] * values[3]
        if (!determinant.isFinite() || abs(determinant) < 0.00000001f) return null
        var minX = Float.POSITIVE_INFINITY
        var minY = Float.POSITIVE_INFINITY
        var maxX = Float.NEGATIVE_INFINITY
        var maxY = Float.NEGATIVE_INFINITY
        for (x in floatArrayOf(bounds[0], bounds[2])) {
            for (y in floatArrayOf(bounds[1], bounds[3])) {
                val px = x - values[2]
                val py = y - values[5]
                val localX = (values[4] * px - values[1] * py) / determinant
                val localY = (values[0] * py - values[3] * px) / determinant
                minX = minOf(minX, localX)
                minY = minOf(minY, localY)
                maxX = maxOf(maxX, localX)
                maxY = maxOf(maxY, localY)
            }
        }
        return floatArrayOf(minX, minY, maxX, maxY)
    }
}