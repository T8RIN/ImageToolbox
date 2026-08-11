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

package com.t8rin.imagetoolbox.feature.multi_frame_fusion.domain

data class FusionParams(
    val mode: FusionMode = FusionMode.Exposure,
    val alignImages: Boolean = true,
    val cropToOverlap: Boolean = true,
    val contrastWeight: Float = 1f,
    val saturationWeight: Float = 1f,
    val exposureWeight: Float = 1f,
    val focusRadius: Int = 7,
    val focusStrength: Float = 2f
) {
    fun normalized(): FusionParams = copy(
        contrastWeight = contrastWeight.coerceIn(MIN_WEIGHT, MAX_WEIGHT),
        saturationWeight = saturationWeight.coerceIn(MIN_WEIGHT, MAX_WEIGHT),
        exposureWeight = exposureWeight.coerceIn(MIN_WEIGHT, MAX_WEIGHT),
        focusRadius = focusRadius
            .coerceIn(MIN_FOCUS_RADIUS, MAX_FOCUS_RADIUS)
            .let { if (it % 2 == 0) it + 1 else it }
            .coerceAtMost(MAX_FOCUS_RADIUS),
        focusStrength = focusStrength.coerceIn(MIN_FOCUS_STRENGTH, MAX_FOCUS_STRENGTH)
    )

    companion object {
        const val MIN_IMAGES = 2
        const val MAX_IMAGES = 12

        const val MIN_WEIGHT = 0f
        const val MAX_WEIGHT = 2f

        const val MIN_FOCUS_RADIUS = 1
        const val MAX_FOCUS_RADIUS = 21
        const val MIN_FOCUS_STRENGTH = 1f
        const val MAX_FOCUS_STRENGTH = 6f
    }
}
