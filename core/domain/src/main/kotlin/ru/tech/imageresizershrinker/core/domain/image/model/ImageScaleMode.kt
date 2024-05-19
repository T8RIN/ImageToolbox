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

sealed interface ScaleColorSpace {
    data object SRGB : ScaleColorSpace
    data object LAB : ScaleColorSpace
    data object Linear : ScaleColorSpace

    companion object {
        val Default = LAB

        val entries by lazy {
            listOf(
                LAB, SRGB, Linear
            )
        }
    }
}

sealed class ImageScaleMode(val value: Int) {

    abstract val scaleColorSpace: ScaleColorSpace

    abstract fun copy(
        scaleColorSpace: ScaleColorSpace = this.scaleColorSpace
    ): ImageScaleMode

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageScaleMode

        return scaleColorSpace == other.scaleColorSpace
    }

    override fun hashCode(): Int {
        return scaleColorSpace.hashCode()
    }

    object NotPresent : ImageScaleMode(-2) {
        override val scaleColorSpace: ScaleColorSpace
            get() = ScaleColorSpace.Default

        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = NotPresent

        override fun toString(): String = "NotPresent"
    }

    class Bilinear(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(0) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = Bilinear(scaleColorSpace)
    }

    class Nearest(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(1) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = Nearest(scaleColorSpace)
    }

    class Spline(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(2) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = Spline(scaleColorSpace)
    }

    class Mitchell(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(3) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = Mitchell(scaleColorSpace)
    }

    class Lanczos(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(4) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = Lanczos(scaleColorSpace)
    }

    class Catmull(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(5) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = Catmull(scaleColorSpace)
    }

    class Hermite(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(6) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = Hermite(scaleColorSpace)
    }

    class BSpline(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(7) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = BSpline(scaleColorSpace)
    }

    class Hann(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(8) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = Hann(scaleColorSpace)
    }

    class Bicubic(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(9) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = Bicubic(scaleColorSpace)
    }

    class LanczosBessel(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(10) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = LanczosBessel(scaleColorSpace)
    }

    companion object {
        val Default = Bilinear()

        val entries by lazy {
            listOf(
                Bilinear(),
                Nearest(),
                Spline(),
                Mitchell(),
                Lanczos(),
                Catmull(),
                Hermite(),
                BSpline(),
                Hann(),
                Bicubic(),
                LanczosBessel()
            )
        }

        fun fromInt(value: Int): ImageScaleMode = when (value) {
            0 -> Bilinear()
            1 -> Nearest()
            2 -> Spline()
            3 -> Mitchell()
            4 -> Lanczos()
            5 -> Catmull()
            6 -> Hermite()
            7 -> BSpline()
            8 -> Hann()
            9 -> Bicubic()
            10 -> LanczosBessel()

            else -> NotPresent
        }
    }
}