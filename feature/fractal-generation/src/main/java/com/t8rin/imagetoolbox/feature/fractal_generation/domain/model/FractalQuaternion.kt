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

data class FractalQuaternion(
    val x: Double = 0.0,
    val y: Double = 0.0,
    val z: Double = 0.0,
    val w: Double = 0.0
) {

    fun normalized(fallback: FractalQuaternion = Zero): FractalQuaternion = copy(
        x = x.normalizedComponent(fallback.x),
        y = y.normalizedComponent(fallback.y),
        z = z.normalizedComponent(fallback.z),
        w = w.normalizedComponent(fallback.w)
    )

    companion object {
        val Zero = FractalQuaternion()
        val JuliaDefault = FractalQuaternion(x = -0.2, y = 0.8)
        val CubicDefault = FractalQuaternion(x = -0.2, y = 0.6, z = 0.3)

        const val MIN_COMPONENT = -2.0
        const val MAX_COMPONENT = 2.0
    }
}

private fun Double.normalizedComponent(fallback: Double): Double =
    takeIf(Double::isFinite)?.coerceIn(
        FractalQuaternion.MIN_COMPONENT,
        FractalQuaternion.MAX_COMPONENT
    ) ?: fallback
