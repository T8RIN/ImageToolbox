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

package com.t8rin.imagetoolbox.core.domain.model

import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

data class GradientFill(
    val palette: GradientPalette = GradientPalette.SoftRainbow,
    val angle: Float = 0f
) {
    fun lineFor(width: Float, height: Float, left: Float = 0f, top: Float = 0f): GradientLine {
        val radians = (angle.takeIf { it.isFinite() } ?: 0f) * Math.PI / 180.0
        val dx = cos(radians).toFloat()
        val dy = sin(radians).toFloat()
        val halfLength = (abs(dx) * width + abs(dy) * height).coerceAtLeast(1f) / 2f
        val centerX = left + width / 2f
        val centerY = top + height / 2f
        return GradientLine(
            startX = centerX - dx * halfLength,
            startY = centerY - dy * halfLength,
            endX = centerX + dx * halfLength,
            endY = centerY + dy * halfLength
        )
    }
}

data class GradientLine(
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float
)