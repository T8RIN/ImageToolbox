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

package com.t8rin.imagetoolbox.texture_generation.domain.model

data class GmicTextureParams(
    val organicFibers: OrganicFibersParams = OrganicFibersParams(),
    val reactionDiffusion: ReactionDiffusionParams = ReactionDiffusionParams(),
    val rorschach: RorschachParams = RorschachParams(),
    val truchet: TruchetParams = TruchetParams()
)

data class OrganicFibersParams(
    val agentDensity: Float = 20f,
    val iterations: Int = 20,
    val orientations: Int = 3,
    val sensorDistance: Int = 15,
    val sensorAngle: Float = 15f,
    val motionDistance: Int = 3,
    val motionAngle: Float = 15f,
    val motionMoment: Float = 30f,
    val trailBlur: Float = 1f,
    val particleSize: Int = 19,
    val particleThickness: Float = 10f,
    val quantizedOrientations: Int = 24,
    val opacity: Float = 50f,
    val sharpening: Float = 0f,
    val palette: OrganicFibersPalette = OrganicFibersPalette.Solar
)

enum class OrganicFibersPalette {
    Default, Hsv, Lines, Hot, Cool, Jet, Flag, Cube, Rainbow, Parula, Spring, Summer,
    Autumn, Winter, Bone, Copper, Pink, Vga, Algae, Amp, Balance, Curl, Deep, Delta,
    Dense, Diff, Gray, Haline, Ice, Matter, Oxy, Phase, Rain, Solar, Speed, Tarn, Tempo,
    Thermal, Topo, Turbid, Aurora, Hocuspocus, Srb2, Uzebox, Amiga7800, Amiga7800Mess,
    FornaxVoid1
}

data class ReactionDiffusionParams(
    val iterations: Int = 5,
    val size: Float = 5f,
    val mode: ReactionDiffusionMode = ReactionDiffusionMode.Monochrome
)

enum class ReactionDiffusionMode {
    Monochrome, MonochromeWithAlpha, Rgb, RgbWithAlpha
}

data class RorschachParams(
    val scale: Float = 3f,
    val mirror: RorschachMirror = RorschachMirror.XAxis,
    val stencil: RorschachStencil = RorschachStencil.Color
)

enum class RorschachMirror {
    None, XAxis, YAxis, BothAxes
}

enum class RorschachStencil {
    BlackAndWhite, Rgb, Color
}

data class TruchetParams(
    val scale: Int = 32,
    val radius: Int = 5,
    val smoothness: Float = 1f,
    val type: TruchetType = TruchetType.Curved,
    val colorMode: TruchetColorMode = TruchetColorMode.WhiteOnBlack
)

enum class TruchetType {
    Straight, Curved
}

enum class TruchetColorMode {
    WhiteOnBlack, BlackOnWhite, WhiteOnTransparent, BlackOnTransparent,
    TransparentOnWhite, TransparentOnBlack, Random
}
