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

package ru.tech.imageresizershrinker.core.domain.image.model

sealed class ImageScaleMode(val value: Int) {
    data object NotPresent : ImageScaleMode(-2)
    data object Bilinear : ImageScaleMode(0)
    data object Nearest : ImageScaleMode(1)
    data object Spline : ImageScaleMode(2)
    data object Mitchell : ImageScaleMode(3)
    data object Lanczos : ImageScaleMode(4)
    data object Catmull : ImageScaleMode(5)
    data object Hermite : ImageScaleMode(6)
    data object BSpline : ImageScaleMode(7)
    data object Hann : ImageScaleMode(8)
    data object Bicubic : ImageScaleMode(9)
    data object LanczosBessel : ImageScaleMode(10)

    companion object {
        val Default = Bilinear

        val entries by lazy {
            listOf(
                Bilinear,
                Nearest,
                Spline,
                Mitchell,
                Lanczos,
                Catmull,
                Hermite,
                BSpline,
                Hann,
                Bicubic,
                LanczosBessel
            )
        }

        fun fromInt(value: Int): ImageScaleMode = when (value) {
            0 -> Bilinear
            1 -> Nearest
            2 -> Spline
            3 -> Mitchell
            4 -> Lanczos
            5 -> Catmull
            6 -> Hermite
            7 -> BSpline
            8 -> Hann
            9 -> Bicubic
            10 -> LanczosBessel

            else -> NotPresent
        }
    }
}