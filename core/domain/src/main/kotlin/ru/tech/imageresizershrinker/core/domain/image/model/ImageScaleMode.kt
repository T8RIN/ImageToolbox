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

    abstract val isAntialiasingEnabled: Boolean

    abstract fun copy(
        isAntialiasingEnabled: Boolean = this.isAntialiasingEnabled
    ): ImageScaleMode

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageScaleMode

        return isAntialiasingEnabled == other.isAntialiasingEnabled
    }

    override fun hashCode(): Int {
        return isAntialiasingEnabled.hashCode()
    }

    object NotPresent : ImageScaleMode(-2) {
        override val isAntialiasingEnabled: Boolean
            get() = false

        override fun copy(
            isAntialiasingEnabled: Boolean
        ): ImageScaleMode = NotPresent

        override fun toString(): String = "NotPresent"
    }

    class Bilinear(
        override val isAntialiasingEnabled: Boolean = true
    ) : ImageScaleMode(0) {
        override fun copy(
            isAntialiasingEnabled: Boolean
        ): ImageScaleMode = Bilinear(isAntialiasingEnabled)
    }

    class Nearest(
        override val isAntialiasingEnabled: Boolean = true
    ) : ImageScaleMode(1) {
        override fun copy(
            isAntialiasingEnabled: Boolean
        ): ImageScaleMode = Nearest(isAntialiasingEnabled)
    }

    class Spline(
        override val isAntialiasingEnabled: Boolean = true
    ) : ImageScaleMode(2) {
        override fun copy(
            isAntialiasingEnabled: Boolean
        ): ImageScaleMode = Spline(isAntialiasingEnabled)
    }

    class Mitchell(
        override val isAntialiasingEnabled: Boolean = true
    ) : ImageScaleMode(3) {
        override fun copy(
            isAntialiasingEnabled: Boolean
        ): ImageScaleMode = Mitchell(isAntialiasingEnabled)
    }

    class Lanczos(
        override val isAntialiasingEnabled: Boolean = true
    ) : ImageScaleMode(4) {
        override fun copy(
            isAntialiasingEnabled: Boolean
        ): ImageScaleMode = Lanczos(isAntialiasingEnabled)
    }

    class Catmull(
        override val isAntialiasingEnabled: Boolean = true
    ) : ImageScaleMode(5) {
        override fun copy(
            isAntialiasingEnabled: Boolean
        ): ImageScaleMode = Catmull(isAntialiasingEnabled)
    }

    class Hermite(
        override val isAntialiasingEnabled: Boolean = true
    ) : ImageScaleMode(6) {
        override fun copy(
            isAntialiasingEnabled: Boolean
        ): ImageScaleMode = Hermite(isAntialiasingEnabled)
    }

    class BSpline(
        override val isAntialiasingEnabled: Boolean = true
    ) : ImageScaleMode(7) {
        override fun copy(
            isAntialiasingEnabled: Boolean
        ): ImageScaleMode = BSpline(isAntialiasingEnabled)
    }

    class Hann(
        override val isAntialiasingEnabled: Boolean = true
    ) : ImageScaleMode(8) {
        override fun copy(
            isAntialiasingEnabled: Boolean
        ): ImageScaleMode = Hann(isAntialiasingEnabled)
    }

    class Bicubic(
        override val isAntialiasingEnabled: Boolean = true
    ) : ImageScaleMode(9) {
        override fun copy(
            isAntialiasingEnabled: Boolean
        ): ImageScaleMode = Bicubic(isAntialiasingEnabled)
    }

    class LanczosBessel(
        override val isAntialiasingEnabled: Boolean = true
    ) : ImageScaleMode(10) {
        override fun copy(
            isAntialiasingEnabled: Boolean
        ): ImageScaleMode = LanczosBessel(isAntialiasingEnabled)
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