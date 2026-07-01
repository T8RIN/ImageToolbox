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

import com.t8rin.imagetoolbox.core.domain.model.ColorModel

data class TextureParams(
    val textureFilterType: TextureFilterType,
    val color: ColorModel,
    val radius: Int,
    val monochrome: Boolean,
    val shine: Float,
    val scale: Float,
    val brightness: Int,
    val amount: Float,
    val turbulence: Float,
    val dispersion: Float,
    val time: Float,
    val samples: Int,
    val backgroundColor: ColorModel,
    val foregroundColor: ColorModel,
    val xScale: Int,
    val yScale: Int,
    val fuzziness: Int,
    val stretch: Float,
    val angle: Float,
    val angleCoefficient: Float,
    val gradientCoefficient: Float,
    val f1: Float,
    val f2: Float,
    val f3: Float,
    val f4: Float,
    val randomness: Float,
    val gridType: CellularGridType,
    val distancePower: Float,
    val octaves: Float,
    val h: Float,
    val lacunarity: Float,
    val gain: Float,
    val bias: Float,
    val basisType: FbmBasisType,
    val turbulenceFactor: Float,
    val scaling: Float,
    val iterations: Int,
    val a: Float,
    val b: Float,
    val c: Float,
    val d: Float,
    val k: Int,
    val rings: Float
) {
    companion object {
        val Default by lazy {
            TextureParams(
                textureFilterType = TextureFilterType.BrushedMetal,
                color = ColorModel(-7829368),
                radius = 10,
                monochrome = true,
                shine = 0.1f,
                scale = 32f,
                brightness = 10,
                amount = 1f,
                turbulence = 1f,
                dispersion = 0f,
                time = 0f,
                samples = 2,
                backgroundColor = ColorModel(-16777216),
                foregroundColor = ColorModel(-1),
                xScale = 8,
                yScale = 8,
                fuzziness = 0,
                stretch = 1f,
                angle = 0f,
                angleCoefficient = 0f,
                gradientCoefficient = 0f,
                f1 = 1f,
                f2 = 0f,
                f3 = 0f,
                f4 = 0f,
                randomness = 0f,
                gridType = CellularGridType.Hexagonal,
                distancePower = 2f,
                octaves = 4f,
                h = 1f,
                lacunarity = 2f,
                gain = 0.5f,
                bias = 0.5f,
                basisType = FbmBasisType.Noise,
                turbulenceFactor = 0.4f,
                scaling = 0f,
                iterations = 25000,
                a = -0.59f,
                b = 0.2f,
                c = 0.1f,
                d = 0f,
                k = 0,
                rings = 0.5f
            )
        }
    }
}

fun TextureParams.withDefaultsFor(textureFilterType: TextureFilterType): TextureParams =
    when (textureFilterType) {
        TextureFilterType.BrushedMetal -> copy(
            textureFilterType = textureFilterType,
            color = ColorModel(-7829368),
            radius = 10,
            amount = 0.1f,
            monochrome = true,
            shine = 0.1f
        )

        TextureFilterType.Caustics -> copy(
            textureFilterType = textureFilterType,
            scale = 32f,
            brightness = 10,
            amount = 1f,
            turbulence = 1f,
            dispersion = 0f,
            time = 0f,
            samples = 2,
            backgroundColor = ColorModel(-8806401)
        )

        TextureFilterType.Cellular -> copy(
            textureFilterType = textureFilterType,
            color = ColorModel(-1),
            scale = 32f,
            stretch = 1f,
            angle = 0f,
            angleCoefficient = 0f,
            gradientCoefficient = 0f,
            f1 = 1f,
            f2 = 0f,
            f3 = 0f,
            f4 = 0f,
            randomness = 0f,
            gridType = CellularGridType.Hexagonal,
            distancePower = 2f,
            turbulence = 1f,
            amount = 1f,
            gain = 0.5f,
            bias = 0.5f
        )

        TextureFilterType.Check -> copy(
            textureFilterType = textureFilterType,
            foregroundColor = ColorModel(-1),
            backgroundColor = ColorModel(-16777216),
            xScale = 8,
            yScale = 8,
            fuzziness = 0,
            angle = 0f
        )

        TextureFilterType.FBM -> copy(
            textureFilterType = textureFilterType,
            amount = 1f,
            scale = 32f,
            stretch = 1f,
            angle = 0f,
            octaves = 4f,
            h = 1f,
            lacunarity = 2f,
            gain = 0.5f,
            bias = 0.5f,
            basisType = FbmBasisType.Noise
        )

        TextureFilterType.Marble -> copy(
            textureFilterType = textureFilterType,
            scale = 32f,
            stretch = 1f,
            angle = 0f,
            turbulence = 1f,
            turbulenceFactor = 0.4f
        )

        TextureFilterType.Plasma -> copy(
            textureFilterType = textureFilterType,
            color = ColorModel(-16777216),
            turbulence = 1f,
            scaling = 0f
        )

        TextureFilterType.Quilt -> copy(
            textureFilterType = textureFilterType,
            iterations = 25000,
            a = -0.59f,
            b = 0.2f,
            c = 0.1f,
            d = 0f,
            k = 0
        )

        TextureFilterType.Wood -> copy(
            textureFilterType = textureFilterType,
            rings = 0.5f,
            turbulence = 1f,
            gain = 0.8f,
            bias = 0.1f,
            scale = 200f,
            stretch = 10f,
            angle = 0f
        )
    }
