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

import kotlin.math.hypot
import kotlin.math.min

internal data class CollageHandleBounds(
    val centerX: Float,
    val centerY: Float,
    val diameter: Float,
    val touchRadius: Float
)

internal fun collageHandleBounds(
    positions: List<Pair<Float, Float>>,
    diameter: Float,
    touchRadius: Float,
    scale: Float
): List<CollageHandleBounds> = positions.mapIndexed { index, (x, y) ->
    val nearest = positions.indices.filter { it != index }.minOfOrNull {
        hypot(positions[it].first - x, positions[it].second - y)
    } ?: Float.POSITIVE_INFINITY
    val size = min(diameter / scale, nearest * .7f)
    CollageHandleBounds(
        centerX = x,
        centerY = y,
        diameter = size,
        touchRadius = min(touchRadius / scale, nearest * .48f).coerceAtLeast(size / 2f)
    )
}