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

package com.t8rin.imagetoolbox.feature.fractal_generation.domain.model

data class FractalCamera(
    val yaw: Double = DEFAULT_YAW,
    val pitch: Double = DEFAULT_PITCH,
    val distance: Double = DEFAULT_DISTANCE
) {

    fun normalized(fallback: FractalCamera = Default): FractalCamera = copy(
        yaw = yaw
            .takeIf(Double::isFinite)
            ?.wrappedDegrees()
            ?: fallback.yaw,
        pitch = pitch
            .takeIf(Double::isFinite)
            ?.coerceIn(MIN_PITCH, MAX_PITCH)
            ?: fallback.pitch,
        distance = distance
            .takeIf(Double::isFinite)
            ?.coerceIn(MIN_DISTANCE, MAX_DISTANCE)
            ?: fallback.distance
    )

    fun orbit(
        yawDelta: Double,
        pitchDelta: Double
    ): FractalCamera = copy(
        yaw = yaw + yawDelta,
        pitch = pitch + pitchDelta
    ).normalized(this)

    fun zoomBy(factor: Double): FractalCamera {
        val safeFactor = factor.takeIf { it.isFinite() && it > 0.0 } ?: 1.0
        return copy(distance = distance / safeFactor).normalized(this)
    }

    companion object {
        val Default = FractalCamera()

        const val DEFAULT_YAW = -35.0
        const val DEFAULT_PITCH = 20.0
        const val DEFAULT_DISTANCE = 3.5
        const val MIN_YAW = -180.0
        const val MAX_YAW = 180.0
        const val MIN_PITCH = -89.0
        const val MAX_PITCH = 89.0
        const val MIN_DISTANCE = 0.5
        const val MAX_DISTANCE = 20.0
    }
}

private fun Double.wrappedDegrees(): Double {
    val wrapped = ((this + HALF_ROTATION) % FULL_ROTATION + FULL_ROTATION) %
            FULL_ROTATION - HALF_ROTATION
    return if (wrapped == -HALF_ROTATION && this > 0.0) HALF_ROTATION else wrapped
}

private const val HALF_ROTATION = 180.0
private const val FULL_ROTATION = 360.0
