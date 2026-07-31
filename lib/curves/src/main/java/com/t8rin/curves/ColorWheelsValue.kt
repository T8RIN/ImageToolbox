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

package com.t8rin.curves

import kotlin.math.abs
import kotlin.math.sqrt

internal data class ColorWheelPoint(
    val x: Float = 0f,
    val y: Float = 0f
) {
    fun normalized(): ColorWheelPoint {
        val length = sqrt(x * x + y * y)
        return if (length <= 1f) {
            copy(x = x.coerceIn(-1f, 1f), y = y.coerceIn(-1f, 1f))
        } else {
            ColorWheelPoint(x / length, y / length)
        }
    }

    val isDefault: Boolean
        get() = x * x + y * y < DefaultEpsilon

    private companion object {
        const val DefaultEpsilon = 0.000001f
    }
}

internal data class ColorWheelsValue(
    val shadows: ColorWheelPoint = ColorWheelPoint(),
    val midtones: ColorWheelPoint = ColorWheelPoint(),
    val highlights: ColorWheelPoint = ColorWheelPoint(),
    val edges: Float = DefaultEdges
) {
    val isDefault: Boolean
        get() = shadows.isDefault &&
                midtones.isDefault &&
                highlights.isDefault &&
                abs(edges - DefaultEdges) < DefaultEpsilon

    fun normalized(): ColorWheelsValue = copy(
        shadows = shadows.normalized(),
        midtones = midtones.normalized(),
        highlights = highlights.normalized(),
        edges = edges.coerceIn(MinEdges, MaxEdges)
    )

    companion object {
        const val MinEdges = 0f
        const val MaxEdges = 1f
        const val DefaultEdges = 0.5f

        private const val DefaultEpsilon = 0.000001f
    }
}
