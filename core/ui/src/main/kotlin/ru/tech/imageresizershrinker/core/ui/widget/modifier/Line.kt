/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.core.ui.widget.modifier

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@Stable
@Immutable
data class Line(
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float
) {

    companion object {
        val CenterVertical = Line(
            startX = 0.5f,
            startY = 0f,
            endX = 0.5f,
            endY = 1f
        )

        val CenterHorizontal = Line(
            startX = 0f,
            startY = 0.5f,
            endX = 1f,
            endY = 0.5f
        )

        @Suppress("FunctionName")
        fun Rotated(angle: Float): Line = if (abs(angle) % 360 != 0f) {
            CenterVertical.rotate(angle)
        } else {
            CenterVertical
        }

        @Suppress("FunctionName")
        fun Bundle(size: Int): List<Line> = if (size > 0) {
            List(size) {
                Rotated(it * (360f / size))
            }
        } else {
            listOf()
        }
    }

    fun rotate(angle: Float): Line {
        val centerX = (startX + endX) / 2
        val centerY = (startY + endY) / 2

        val radians = Math.toRadians(angle.toDouble()).toFloat()
        val cosA = cos(radians)
        val sinA = sin(radians)

        fun rotatePoint(
            x: Float,
            y: Float
        ): Pair<Float, Float> {
            val dx = x - centerX
            val dy = y - centerY
            val newX = centerX + dx * cosA - dy * sinA
            val newY = centerY + dx * sinA + dy * cosA
            return newX to newY
        }

        val (newStartX, newStartY) = rotatePoint(startX, startY)
        val (newEndX, newEndY) = rotatePoint(endX, endY)

        return copy(startX = newStartX, startY = newStartY, endX = newEndX, endY = newEndY)
    }

}