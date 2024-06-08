/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.feature.svg_maker.domain

data class SvgParams(
    val colorsCount: Int,
    val isPaletteSampled: Boolean,
    val quantizationCyclesCount: Int,
    val blurRadius: Int,
    val blurDelta: Int,
    val pathOmit: Int,
    val linesThreshold: Float,
    val quadraticThreshold: Float,
    val minColorRatio: Float,
    val coordinatesRoundingAmount: Int,
    val svgPathsScale: Float, // 0.01f, 100f
    val isImageSampled: Boolean
) {
    companion object {
        val Default by lazy {
            SvgParams(
                colorsCount = 16,
                isPaletteSampled = true,
                quantizationCyclesCount = 3,
                blurRadius = 0,
                blurDelta = 20,
                pathOmit = 8,
                linesThreshold = 1f,
                quadraticThreshold = 1f,
                minColorRatio = 0.02f,
                coordinatesRoundingAmount = 1,
                svgPathsScale = 1f,
                isImageSampled = true
            )
        }
        val Detailed by lazy {
            Default.copy(
                pathOmit = 0,
                linesThreshold = 0.5f,
                quadraticThreshold = 0.5f,
                coordinatesRoundingAmount = 3,
                colorsCount = 64,
                quantizationCyclesCount = 1
            )
        }
        val Grayscale by lazy {
            Default.copy(
                isPaletteSampled = false,
                quantizationCyclesCount = 1,
                colorsCount = 7
            )
        }

        val presets by lazy {
            listOf(Default, Detailed, Grayscale)
        }
    }
}