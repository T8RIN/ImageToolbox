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
    data object LUV : ScaleColorSpace
    data object Linear : ScaleColorSpace

    companion object {
        val Default = Linear

        val entries by lazy {
            listOf(
                Linear, LAB, SRGB, LUV
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

    object Base : ImageScaleMode(-3) {
        override val scaleColorSpace: ScaleColorSpace
            get() = ScaleColorSpace.Default

        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = Base

        override fun toString(): String = "Base"
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

    class Cubic(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(2) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = Cubic(scaleColorSpace)
    }

    class Mitchell(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(3) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = Mitchell(scaleColorSpace)
    }

    class Catmull(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(4) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = Catmull(scaleColorSpace)
    }

    class Hermite(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(5) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = Hermite(scaleColorSpace)
    }

    class BSpline(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(6) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = BSpline(scaleColorSpace)
    }

    class Hann(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(7) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = Hann(scaleColorSpace)
    }

    class Bicubic(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(8) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = Bicubic(scaleColorSpace)
    }

    class Hamming(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(9) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = Hamming(scaleColorSpace)
    }

    class Hanning(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(10) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = Hanning(scaleColorSpace)
    }

    class Blackman(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(11) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = Blackman(scaleColorSpace)
    }

    class Welch(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(12) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = Welch(scaleColorSpace)
    }

    class Quadric(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(13) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = Quadric(scaleColorSpace)
    }

    class Gaussian(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(14) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = Gaussian(scaleColorSpace)
    }

    class Sphinx(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(15) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = Sphinx(scaleColorSpace)
    }

    class Bartlett(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(16) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = Bartlett(scaleColorSpace)
    }

    class Robidoux(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(17) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = Robidoux(scaleColorSpace)
    }

    class RobidouxSharp(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(18) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = RobidouxSharp(scaleColorSpace)
    }

    class Spline16(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(19) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = Spline16(scaleColorSpace)
    }

    class Spline36(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(20) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = Spline36(scaleColorSpace)
    }

    class Spline64(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(21) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = Spline64(scaleColorSpace)
    }

    class Kaiser(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(22) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = Kaiser(scaleColorSpace)
    }

    class BartlettHann(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(23) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = BartlettHann(scaleColorSpace)
    }

    class Box(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(24) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = Box(scaleColorSpace)
    }

    class Bohman(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(25) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = Bohman(scaleColorSpace)
    }

    class Lanczos2(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(26) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = Lanczos2(scaleColorSpace)
    }

    class Lanczos3(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(27) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = Lanczos3(scaleColorSpace)
    }

    class Lanczos4(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(28) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = Lanczos4(scaleColorSpace)
    }

    class Lanczos2Jinc(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(29) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = Lanczos2Jinc(scaleColorSpace)
    }

    class Lanczos3Jinc(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(30) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = Lanczos3Jinc(scaleColorSpace)
    }

    class Lanczos4Jinc(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(31) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = Lanczos4Jinc(scaleColorSpace)
    }

    companion object {
        val Default = Bilinear()

        val entries by lazy {
            listOf(
                Bilinear(),
                Nearest(),
                Cubic(),
                Mitchell(),
                Catmull(),
                Hermite(),
                BSpline(),
                Hann(),
                Bicubic(),
                Hamming(),
                Hanning(),
                Blackman(),
                Welch(),
                Quadric(),
                Gaussian(),
                Sphinx(),
                Bartlett(),
                Robidoux(),
                RobidouxSharp(),
                Spline16(),
                Spline36(),
                Spline64(),
                Kaiser(),
                BartlettHann(),
                Box(),
                Bohman(),
                Lanczos2(),
                Lanczos3(),
                Lanczos4(),
                Lanczos2Jinc(),
                Lanczos3Jinc(),
                Lanczos4Jinc(),
            )
        }

        fun fromInt(value: Int): ImageScaleMode = when (value) {
            -3 -> Base
            0 -> Bilinear()
            1 -> Nearest()
            2 -> Cubic()
            3 -> Mitchell()
            4 -> Catmull()
            5 -> Hermite()
            6 -> BSpline()
            7 -> Hann()
            8 -> Bicubic()
            9 -> Hamming()
            10 -> Hanning()
            11 -> Blackman()
            12 -> Welch()
            13 -> Quadric()
            14 -> Gaussian()
            15 -> Sphinx()
            16 -> Bartlett()
            17 -> Robidoux()
            18 -> RobidouxSharp()
            19 -> Spline16()
            20 -> Spline36()
            21 -> Spline64()
            22 -> Kaiser()
            23 -> BartlettHann()
            24 -> Box()
            25 -> Bohman()
            26 -> Lanczos2()
            27 -> Lanczos3()
            28 -> Lanczos4()
            29 -> Lanczos2Jinc()
            30 -> Lanczos3Jinc()
            31 -> Lanczos4Jinc()

            else -> NotPresent
        }
    }
}