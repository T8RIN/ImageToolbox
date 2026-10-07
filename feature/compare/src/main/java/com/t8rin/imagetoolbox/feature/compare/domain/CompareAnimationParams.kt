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

package com.t8rin.imagetoolbox.feature.compare.domain

import kotlin.math.roundToInt

internal data class CompareAnimationParams(
    val durationSeconds: Int = 4,
    val isVertical: Boolean = false,
    val showLabels: Boolean = true,
    val maxSize: Int = 2048
) {
    val durationMillis: Int
        get() = durationSeconds.coerceIn(2, 8) * 1000

    fun positionAt(timeMillis: Int): Float {
        val time = timeMillis.mod(durationMillis)
        val travelDuration = (durationMillis - 2 * HOLD_MILLIS) / 2
        val position = when {
            time < HOLD_MILLIS -> 0f
            time < HOLD_MILLIS + travelDuration ->
                (time - HOLD_MILLIS).toFloat() / travelDuration

            time < 2 * HOLD_MILLIS + travelDuration -> 1f
            else -> 1f - (time - 2 * HOLD_MILLIS - travelDuration).toFloat() / travelDuration
        }
        return position * position * (3f - 2f * position)
    }

    fun frames(): List<CompareAnimationFrame> = buildList {
        var time = 0
        val travelDuration = (durationMillis - 2 * HOLD_MILLIS) / 2
        repeat(2) {
            add(CompareAnimationFrame(positionAt(time), HOLD_MILLIS))
            time += HOLD_MILLIS
            repeat(travelDuration / FRAME_MILLIS) {
                add(CompareAnimationFrame(positionAt(time), FRAME_MILLIS))
                time += FRAME_MILLIS
            }
        }
    }

    companion object {
        private const val HOLD_MILLIS = 500
        private const val FRAME_MILLIS = 50
        val sizes = listOf(720, 1080, 2048, 4096)

        fun outputSize(width: Int, height: Int, maxSize: Int = 2048): Pair<Int, Int> {
            val scale = (maxSize.coerceAtLeast(1).toFloat() / maxOf(width, height)).coerceAtMost(1f)
            return (width * scale).roundToInt().coerceAtLeast(1) to
                    (height * scale).roundToInt().coerceAtLeast(1)
        }
    }
}

internal data class CompareAnimationFrame(
    val position: Float,
    val durationMillis: Int
)