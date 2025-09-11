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

package com.t8rin.imagetoolbox.core.domain.image.model

import com.t8rin.imagetoolbox.core.resources.R

sealed interface ScaleColorSpace {
    data object SRGB : ScaleColorSpace
    data object LAB : ScaleColorSpace
    data object LUV : ScaleColorSpace
    data object Linear : ScaleColorSpace
    data object Sigmoidal : ScaleColorSpace
    data object XYZ : ScaleColorSpace
    data object F32sRGB : ScaleColorSpace
    data object F32Rec709 : ScaleColorSpace
    data object F32Gamma22 : ScaleColorSpace
    data object F32Gamma28 : ScaleColorSpace
    data object LCH : ScaleColorSpace
    data object OklabSRGB : ScaleColorSpace
    data object OklabRec709 : ScaleColorSpace
    data object OklabGamma22 : ScaleColorSpace
    data object OklabGamma28 : ScaleColorSpace
    data object JzazbzSRGB : ScaleColorSpace
    data object JzazbzRec709 : ScaleColorSpace
    data object JzazbzGamma22 : ScaleColorSpace
    data object JzazbzGamma28 : ScaleColorSpace

    val ordinal: Int
        get() = entries.indexOf(this)

    companion object {
        val Default = Linear

        val entries by lazy {
            listOf(
                Linear,
                SRGB,
                F32sRGB,
                XYZ,
                LAB,
                LUV,
                Sigmoidal,
                F32Rec709,
                F32Gamma22,
                F32Gamma28,
                LCH,
                OklabSRGB,
                OklabRec709,
                OklabGamma22,
                OklabGamma28,
                JzazbzSRGB,
                JzazbzRec709,
                JzazbzGamma22,
                JzazbzGamma28
            )
        }

        fun fromOrdinal(ordinal: Int) = entries.getOrNull(ordinal) ?: Default
    }
}

sealed class ImageScaleMode(val value: Int) {

    abstract val scaleColorSpace: ScaleColorSpace

    abstract fun copy(
        scaleColorSpace: ScaleColorSpace = this.scaleColorSpace
    ): ImageScaleMode

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ImageScaleMode) return false

        if (value != other.value) return false
        if (scaleColorSpace != other.scaleColorSpace) return false

        return true
    }

    override fun hashCode(): Int {
        var result = value
        result = 31 * result + scaleColorSpace.hashCode()
        return result
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

    class EwaHanning(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(32) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = EwaHanning(scaleColorSpace)
    }

    class EwaRobidoux(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(33) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = EwaRobidoux(scaleColorSpace)
    }

    class EwaBlackman(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(34) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = EwaBlackman(scaleColorSpace)
    }

    class EwaQuadric(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(35) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = EwaQuadric(scaleColorSpace)
    }

    class EwaRobidouxSharp(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(36) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = EwaRobidouxSharp(scaleColorSpace)
    }

    class EwaLanczos3Jinc(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(37) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = EwaLanczos3Jinc(scaleColorSpace)
    }

    class Ginseng(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(38) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = Ginseng(scaleColorSpace)
    }

    class EwaGinseng(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(39) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = EwaGinseng(scaleColorSpace)
    }

    class EwaLanczosSharp(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(40) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = EwaLanczosSharp(scaleColorSpace)
    }

    class EwaLanczos4Sharpest(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(41) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = EwaLanczos4Sharpest(scaleColorSpace)
    }

    class EwaLanczosSoft(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(42) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = EwaLanczosSoft(scaleColorSpace)
    }

    class HaasnSoft(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(43) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = HaasnSoft(scaleColorSpace)
    }

    class Lagrange2(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(44) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = Lagrange2(scaleColorSpace)
    }

    class Lagrange3(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(45) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = Lagrange3(scaleColorSpace)
    }

    class Lanczos6(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(46) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = Lanczos6(scaleColorSpace)
    }

    class Lanczos6Jinc(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(47) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = Lanczos6Jinc(scaleColorSpace)
    }

    class Area(
        override val scaleColorSpace: ScaleColorSpace = ScaleColorSpace.Default
    ) : ImageScaleMode(48) {
        override fun copy(
            scaleColorSpace: ScaleColorSpace
        ): ImageScaleMode = Area(scaleColorSpace)
    }

    companion object {
        val Default = Bilinear()

        val entries by lazy {
            simpleEntries + complexEntries
        }

        val simpleEntries by lazy {
            listOf(
                EwaQuadric(),
                Quadric(),
                Bilinear(),
                Nearest(),
                Cubic(),
                Mitchell(),
                Catmull(),
                Hermite(),
                BSpline(),
                Bicubic(),
                Box(),
                Lanczos2(),
                Lanczos3(),
                Lanczos4(),
                Lanczos2Jinc(),
                Lanczos3Jinc(),
                Lanczos4Jinc(),
                Hamming(),
                Hanning(),
                EwaHanning(),
                EwaRobidoux(),
                Robidoux(),
                RobidouxSharp(),
                EwaLanczosSharp(),
                EwaLanczos4Sharpest(),
                EwaLanczosSoft(),
                EwaRobidouxSharp(),
                EwaLanczos3Jinc(),
                Lagrange2(),
                Lagrange3(),
                Lanczos6(),
                Lanczos6Jinc(),
                Area()
            )
        }

        val complexEntries by lazy {
            listOf(
                Blackman(),
                Welch(),
                Gaussian(),
                Sphinx(),
                Bartlett(),
                Spline16(),
                Spline36(),
                Spline64(),
                Kaiser(),
                BartlettHann(),
                Bohman(),
                EwaBlackman(),
                Ginseng(),
                EwaGinseng(),
                HaasnSoft(),
                Hann(),
            )
        }

        fun fromInt(value: Int): ImageScaleMode = when {
            value == -3 -> Base
            value >= 0 -> entries.associateBy { it.value }[value] ?: NotPresent
            else -> NotPresent
        }
    }
}

val ImageScaleMode.title: Int
    get() = when (this) {
        ImageScaleMode.Base,
        ImageScaleMode.NotPresent -> R.string.basic

        is ImageScaleMode.Bilinear -> R.string.bilinear
        is ImageScaleMode.Nearest -> R.string.nearest
        is ImageScaleMode.Cubic -> R.string.cubic
        is ImageScaleMode.Mitchell -> R.string.mitchell
        is ImageScaleMode.Catmull -> R.string.catmull
        is ImageScaleMode.Hermite -> R.string.hermite
        is ImageScaleMode.BSpline -> R.string.bspline
        is ImageScaleMode.Hann -> R.string.hann
        is ImageScaleMode.Bicubic -> R.string.bicubic
        is ImageScaleMode.Hamming -> R.string.hamming
        is ImageScaleMode.Hanning -> R.string.hanning
        is ImageScaleMode.Blackman -> R.string.blackman
        is ImageScaleMode.Welch -> R.string.welch
        is ImageScaleMode.Quadric -> R.string.quadric
        is ImageScaleMode.Gaussian -> R.string.gaussian
        is ImageScaleMode.Sphinx -> R.string.sphinx
        is ImageScaleMode.Bartlett -> R.string.bartlett
        is ImageScaleMode.Robidoux -> R.string.robidoux
        is ImageScaleMode.RobidouxSharp -> R.string.robidoux_sharp
        is ImageScaleMode.Spline16 -> R.string.spline16
        is ImageScaleMode.Spline36 -> R.string.spline36
        is ImageScaleMode.Spline64 -> R.string.spline64
        is ImageScaleMode.Kaiser -> R.string.kaiser
        is ImageScaleMode.BartlettHann -> R.string.bartlett_hann
        is ImageScaleMode.Box -> R.string.box
        is ImageScaleMode.Bohman -> R.string.bohman
        is ImageScaleMode.Lanczos2 -> R.string.lanczos2
        is ImageScaleMode.Lanczos3 -> R.string.lanczos3
        is ImageScaleMode.Lanczos4 -> R.string.lanczos4
        is ImageScaleMode.Lanczos2Jinc -> R.string.lanczos2_jinc
        is ImageScaleMode.Lanczos3Jinc -> R.string.lanczos3_jinc
        is ImageScaleMode.Lanczos4Jinc -> R.string.lanczos4_jinc
        is ImageScaleMode.EwaHanning -> R.string.ewa_hanning
        is ImageScaleMode.EwaRobidoux -> R.string.ewa_robidoux
        is ImageScaleMode.EwaBlackman -> R.string.ewa_blackman
        is ImageScaleMode.EwaQuadric -> R.string.ewa_quadric
        is ImageScaleMode.EwaRobidouxSharp -> R.string.ewa_robidoux_sharp
        is ImageScaleMode.EwaLanczos3Jinc -> R.string.ewa_lanczos3_jinc
        is ImageScaleMode.Ginseng -> R.string.ginseng
        is ImageScaleMode.EwaGinseng -> R.string.ewa_ginseng
        is ImageScaleMode.EwaLanczosSharp -> R.string.ewa_lanczos_sharp
        is ImageScaleMode.EwaLanczos4Sharpest -> R.string.ewa_lanczos_4_sharpest
        is ImageScaleMode.EwaLanczosSoft -> R.string.ewa_lanczos_soft
        is ImageScaleMode.HaasnSoft -> R.string.haasn_soft
        is ImageScaleMode.Lagrange2 -> R.string.lagrange_2
        is ImageScaleMode.Lagrange3 -> R.string.lagrange_3
        is ImageScaleMode.Lanczos6 -> R.string.lanczos_6
        is ImageScaleMode.Lanczos6Jinc -> R.string.lanczos_6_jinc
        is ImageScaleMode.Area -> R.string.area
    }