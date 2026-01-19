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

package com.t8rin.imagetoolbox.feature.draw.domain

import com.t8rin.imagetoolbox.core.domain.model.IntegerSize

class DisplacementMap(
    val width: Int,
    val height: Int
) {
    val dx = FloatArray(width * height)
    val dy = FloatArray(width * height)

    fun index(x: Int, y: Int) = y * width + x
}

enum class WarpMode {
    MOVE,
    GROW,
    SHRINK,
    SWIRL_CW,
    SWIRL_CCW
}

data class WarpBrush(
    val radius: Float,
    val strength: Float,
    val hardness: Float
)

data class WarpStroke(
    val from: Pair<Float, Float>,
    val to: Pair<Float, Float>
) {
    fun scaleToFitCanvas(
        currentSize: IntegerSize,
        oldSize: IntegerSize
    ): WarpStroke {
        val sx = currentSize.width.toFloat() / oldSize.width
        val sy = currentSize.height.toFloat() / oldSize.height
        return copy(
            from = (from.first * sx) to (from.second * sy),
            to = (to.first * sx) to (to.second * sy)
        )
    }
}