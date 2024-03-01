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

package ru.tech.imageresizershrinker.core.filters.domain.model

data class LinearTiltShiftParams(
    val blurRadius: Float,
    val sigma: Float,
    val anchorX: Float,
    val anchorY: Float,
    val holeRadius: Float,
    val angle: Float
) {
    companion object {
        val Default by lazy {
            LinearTiltShiftParams(
                blurRadius = 25f,
                sigma = 10f,
                anchorX = 0.5f,
                anchorY = 0.5f,
                holeRadius = 0.2f,
                angle = 45f
            )
        }
    }
}