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

import kotlin.math.ln
import kotlin.math.roundToInt

enum class FractalIterationPolicy(
    val stableKey: String,
    val displayName: String
) {
    Fixed(stableKey = "fixed", displayName = "Fixed"),
    ScaleWithZoom(stableKey = "scale_with_zoom", displayName = "Scale with zoom"),
    Adaptive(stableKey = "adaptive", displayName = "Adaptive");

    fun resolve(
        baseIterations: Int,
        viewport: FractalViewport,
        width: Int,
        height: Int
    ): Int {
        val base = baseIterations.coerceIn(MIN_ITERATIONS, MAX_ITERATIONS)
        if (this == Fixed) return base

        val depthBonus = viewport.decimalZoomDepth * ITERATIONS_PER_DECIMAL_ZOOM
        val resolutionRatio = (
                width.coerceAtLeast(1).toDouble() * height.coerceAtLeast(1) / BASE_PIXEL_COUNT
                ).coerceAtLeast(1.0)
        val resolutionBonus = if (this == Adaptive) {
            ln(resolutionRatio) * ITERATIONS_PER_RESOLUTION_E_FOLD
        } else {
            0.0
        }

        return (base + depthBonus + resolutionBonus)
            .roundToInt()
            .coerceIn(MIN_ITERATIONS, MAX_EFFECTIVE_ITERATIONS)
    }

    companion object {
        const val MIN_ITERATIONS = 16
        const val MAX_ITERATIONS = 8_192
        const val MAX_EFFECTIVE_ITERATIONS = 12_000

        private const val ITERATIONS_PER_DECIMAL_ZOOM = 24.0
        private const val ITERATIONS_PER_RESOLUTION_E_FOLD = 48.0
        private const val BASE_PIXEL_COUNT = 640.0 * 480.0
    }
}
