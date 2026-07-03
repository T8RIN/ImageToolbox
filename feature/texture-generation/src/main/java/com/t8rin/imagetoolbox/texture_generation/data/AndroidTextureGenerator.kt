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

package com.t8rin.imagetoolbox.texture_generation.data

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.createBitmap
import com.jhlabs.BrushedMetalFilter
import com.jhlabs.CausticsFilter
import com.jhlabs.CellularFilter
import com.jhlabs.CheckFilter
import com.jhlabs.FBMFilter
import com.jhlabs.JhFilter
import com.jhlabs.MarbleTexFilter
import com.jhlabs.PlasmaFilter
import com.jhlabs.QuiltFilter
import com.jhlabs.WoodFilter
import com.t8rin.fast_noise.texture.BrickTextureGenerator
import com.t8rin.fast_noise.texture.BrickTextureParameters
import com.t8rin.fast_noise.texture.CamouflageTextureGenerator
import com.t8rin.fast_noise.texture.CamouflageTextureParameters
import com.t8rin.fast_noise.texture.CellTextureGenerator
import com.t8rin.fast_noise.texture.CellTextureParameters
import com.t8rin.fast_noise.texture.CloudTextureGenerator
import com.t8rin.fast_noise.texture.CloudTextureParameters
import com.t8rin.fast_noise.texture.CrackTextureGenerator
import com.t8rin.fast_noise.texture.CrackTextureParameters
import com.t8rin.fast_noise.texture.FabricTextureGenerator
import com.t8rin.fast_noise.texture.FabricTextureParameters
import com.t8rin.fast_noise.texture.FoliageTextureGenerator
import com.t8rin.fast_noise.texture.FoliageTextureParameters
import com.t8rin.fast_noise.texture.HoneycombTextureGenerator
import com.t8rin.fast_noise.texture.HoneycombTextureParameters
import com.t8rin.fast_noise.texture.IceTextureGenerator
import com.t8rin.fast_noise.texture.IceTextureParameters
import com.t8rin.fast_noise.texture.LavaTextureGenerator
import com.t8rin.fast_noise.texture.LavaTextureParameters
import com.t8rin.fast_noise.texture.NebulaTextureGenerator
import com.t8rin.fast_noise.texture.NebulaTextureParameters
import com.t8rin.fast_noise.texture.PaperTextureGenerator
import com.t8rin.fast_noise.texture.PaperTextureParameters
import com.t8rin.fast_noise.texture.RustTextureGenerator
import com.t8rin.fast_noise.texture.RustTextureParameters
import com.t8rin.fast_noise.texture.SandTextureGenerator
import com.t8rin.fast_noise.texture.SandTextureParameters
import com.t8rin.fast_noise.texture.SmokeTextureGenerator
import com.t8rin.fast_noise.texture.SmokeTextureParameters
import com.t8rin.fast_noise.texture.StoneTextureGenerator
import com.t8rin.fast_noise.texture.StoneTextureParameters
import com.t8rin.fast_noise.texture.TerrainTextureGenerator
import com.t8rin.fast_noise.texture.TerrainTextureParameters
import com.t8rin.fast_noise.texture.TopographyTextureGenerator
import com.t8rin.fast_noise.texture.TopographyTextureParameters
import com.t8rin.fast_noise.texture.WaterRippleTextureGenerator
import com.t8rin.fast_noise.texture.WaterRippleTextureParameters
import com.t8rin.fast_noise.texture.WoodTextureGenerator
import com.t8rin.fast_noise.texture.WoodTextureParameters
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.texture_generation.domain.TextureGenerator
import com.t8rin.imagetoolbox.texture_generation.domain.model.FastNoiseTextureParams
import com.t8rin.imagetoolbox.texture_generation.domain.model.TextureFilterType
import com.t8rin.imagetoolbox.texture_generation.domain.model.TextureParams
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class AndroidTextureGenerator @Inject constructor(
    dispatchersHolder: DispatchersHolder
) : TextureGenerator<Bitmap>, DispatchersHolder by dispatchersHolder {

    override suspend fun generateTexture(
        width: Int,
        height: Int,
        textureParams: TextureParams,
        onFailure: (Throwable) -> Unit
    ): Bitmap? = withContext(defaultDispatcher) {
        runCatching {
            if (textureParams.textureFilterType.isFastNoise) {
                createFastNoiseTexture(
                    width = width,
                    height = height,
                    type = textureParams.textureFilterType,
                    params = requireNotNull(textureParams.fastNoiseParams)
                )
            } else {
                createFilter(textureParams).filter(
                    createBitmap(width, height).apply {
                        val sourceColor = when (textureParams.textureFilterType) {
                            TextureFilterType.Cellular,
                            TextureFilterType.Plasma -> textureParams.color.colorInt

                            else -> Color.TRANSPARENT
                        }
                        eraseColor(sourceColor)
                    }
                )
            }
        }.onFailure(onFailure).getOrNull()
    }

    private fun createFilter(
        textureParams: TextureParams
    ): JhFilter = when (textureParams.textureFilterType) {
        TextureFilterType.BrushedMetal -> BrushedMetalFilter().apply {
            color = textureParams.color.colorInt
            radius = textureParams.radius
            amount = textureParams.amount
            monochrome = textureParams.monochrome
            shine = textureParams.shine
        }

        TextureFilterType.Caustics -> CausticsFilter().apply {
            scale = textureParams.scale
            brightness = textureParams.brightness
            amount = textureParams.amount
            turbulence = textureParams.turbulence
            dispersion = textureParams.dispersion
            time = textureParams.time
            samples = textureParams.samples
            bgColor = textureParams.backgroundColor.colorInt
        }

        TextureFilterType.Cellular -> CellularFilter().apply {
            setScale(textureParams.scale)
            setStretch(textureParams.stretch)
            setAngle(textureParams.angle)
            setAngleCoefficient(textureParams.angleCoefficient)
            gradientCoefficient = textureParams.gradientCoefficient
            f1 = textureParams.f1
            f2 = textureParams.f2
            f3 = textureParams.f3
            f4 = textureParams.f4
            setRandomness(textureParams.randomness)
            setGridType(textureParams.gridType.value)
            setDistancePower(textureParams.distancePower)
            setTurbulence(textureParams.turbulence)
            setAmount(textureParams.amount)
            gain = textureParams.gain
            bias = textureParams.bias
            useColor = true
        }

        TextureFilterType.Check -> CheckFilter().apply {
            foreground = textureParams.foregroundColor.colorInt
            background = textureParams.backgroundColor.colorInt
            xScale = textureParams.xScale
            yScale = textureParams.yScale
            fuzziness = textureParams.fuzziness
            angle = textureParams.angle
        }

        TextureFilterType.FBM -> FBMFilter().apply {
            amount = textureParams.amount
            scale = textureParams.scale
            stretch = textureParams.stretch
            angle = textureParams.angle
            octaves = textureParams.octaves
            h = textureParams.h
            lacunarity = textureParams.lacunarity
            gain = textureParams.gain
            bias = textureParams.bias
            basisType = textureParams.basisType.value
        }

        TextureFilterType.Marble -> MarbleTexFilter().apply {
            scale = textureParams.scale
            stretch = textureParams.stretch
            angle = textureParams.angle
            turbulence = textureParams.turbulence
            turbulenceFactor = textureParams.turbulenceFactor
        }

        TextureFilterType.Plasma -> PlasmaFilter().apply {
            turbulence = textureParams.turbulence
            scaling = textureParams.scaling
            useImageColors = true
        }

        TextureFilterType.Quilt -> QuiltFilter().apply {
            iterations = textureParams.iterations
            a = textureParams.a
            b = textureParams.b
            c = textureParams.c
            d = textureParams.d
            k = textureParams.k
        }

        TextureFilterType.Wood -> WoodFilter().apply {
            rings = textureParams.rings
            turbulence = textureParams.turbulence
            gain = textureParams.gain
            bias = textureParams.bias
            scale = textureParams.scale
            stretch = textureParams.stretch
            angle = textureParams.angle
        }

        else -> error("Unsupported JH Labs texture type: ${textureParams.textureFilterType}")
    }

    private fun createFastNoiseTexture(
        width: Int,
        height: Int,
        type: TextureFilterType,
        params: FastNoiseTextureParams
    ): Bitmap? {
        val values = params.values
        val colors = params.colors.map { it.colorInt }

        return when (type) {
            TextureFilterType.Brick -> BrickTextureGenerator().generate(
                width = width,
                height = height,
                parameters = BrickTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    aspectRatio = values[0],
                    mortarWidth = values[1],
                    irregularity = values[2],
                    roughness = values[3],
                    bevel = values[4],
                    mortarColor = colors[0],
                    darkBrickColor = colors[1],
                    brickColor = colors[2],
                    highlightColor = colors[3]
                )
            )

            TextureFilterType.Camouflage -> CamouflageTextureGenerator().generate(
                width = width,
                height = height,
                parameters = CamouflageTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    firstThreshold = values[0],
                    secondThreshold = values[1],
                    thirdThreshold = values[2],
                    distortion = values[3],
                    edgeSoftness = values[4],
                    darkColor = colors[0],
                    forestColor = colors[1],
                    earthColor = colors[2],
                    sandColor = colors[3]
                )
            )

            TextureFilterType.Cell -> CellTextureGenerator().generate(
                width = width,
                height = height,
                parameters = CellTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    jitter = values[0],
                    borderWidth = values[1],
                    glow = values[2],
                    distortion = values[3],
                    variation = values[4],
                    backgroundColor = colors[0],
                    cellColor = colors[1],
                    edgeColor = colors[2],
                    highlightColor = colors[3]
                )
            )

            TextureFilterType.Cloud -> CloudTextureGenerator().generate(
                width = width,
                height = height,
                parameters = CloudTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    coverage = values[0],
                    softness = values[1],
                    detail = values[2],
                    distortion = values[3],
                    density = values[4],
                    skyColor = colors[0],
                    shadowColor = colors[1],
                    lightColor = colors[2]
                )
            )

            TextureFilterType.Crack -> CrackTextureGenerator().generate(
                width = width,
                height = height,
                parameters = CrackTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    width = values[0],
                    density = values[1],
                    distortion = values[2],
                    depth = values[3],
                    branching = values[4],
                    surfaceColor = colors[0],
                    variationColor = colors[1],
                    crackColor = colors[2],
                    edgeColor = colors[3]
                )
            )

            TextureFilterType.Fabric -> FabricTextureGenerator().generate(
                width = width,
                height = height,
                parameters = FabricTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    horizontalThreads = values[0],
                    verticalThreads = values[1],
                    irregularity = values[2],
                    depth = values[3],
                    fuzz = values[4],
                    warpColor = colors[0],
                    weftColor = colors[1],
                    shadowColor = colors[2],
                    highlightColor = colors[3]
                )
            )

            TextureFilterType.Foliage -> FoliageTextureGenerator().generate(
                width = width,
                height = height,
                parameters = FoliageTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    density = values[0],
                    edgeSoftness = values[1],
                    veins = values[2],
                    lighting = values[3],
                    variation = values[4],
                    shadowColor = colors[0],
                    darkLeafColor = colors[1],
                    leafColor = colors[2],
                    highlightColor = colors[3]
                )
            )

            TextureFilterType.Honeycomb -> HoneycombTextureGenerator().generate(
                width = width,
                height = height,
                parameters = HoneycombTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    borderWidth = values[0],
                    bevel = values[1],
                    irregularity = values[2],
                    fill = values[3],
                    glow = values[4],
                    backgroundColor = colors[0],
                    borderColor = colors[1],
                    honeyColor = colors[2],
                    highlightColor = colors[3]
                )
            )

            TextureFilterType.Ice -> IceTextureGenerator().generate(
                width = width,
                height = height,
                parameters = IceTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    crackWidth = values[0],
                    frost = values[1],
                    depth = values[2],
                    distortion = values[3],
                    sparkle = values[4],
                    deepColor = colors[0],
                    iceColor = colors[1],
                    frostColor = colors[2],
                    crackColor = colors[3]
                )
            )

            TextureFilterType.Lava -> LavaTextureGenerator().generate(
                width = width,
                height = height,
                parameters = LavaTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    distortion = values[0],
                    flow = values[1],
                    detail = values[2],
                    crust = values[3],
                    glow = values[4],
                    crustColor = colors[0],
                    lavaColor = colors[1],
                    glowColor = colors[2]
                )
            )

            TextureFilterType.Nebula -> NebulaTextureGenerator().generate(
                width = width,
                height = height,
                parameters = NebulaTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    turbulence = values[0],
                    cloudDensity = values[1],
                    stars = values[2],
                    glow = values[3],
                    contrast = values[4],
                    spaceColor = colors[0],
                    violetColor = colors[1],
                    blueColor = colors[2],
                    glowColor = colors[3]
                )
            )

            TextureFilterType.Paper -> PaperTextureGenerator().generate(
                width = width,
                height = height,
                parameters = PaperTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    fiberDensity = values[0],
                    fiberStrength = values[1],
                    grain = values[2],
                    stains = values[3],
                    roughness = values[4],
                    baseColor = colors[0],
                    lightColor = colors[1],
                    fiberColor = colors[2],
                    stainColor = colors[3]
                )
            )

            TextureFilterType.Rust -> RustTextureGenerator().generate(
                width = width,
                height = height,
                parameters = RustTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    corrosion = values[0],
                    pitting = values[1],
                    flakes = values[2],
                    distortion = values[3],
                    contrast = values[4],
                    metalColor = colors[0],
                    darkRustColor = colors[1],
                    rustColor = colors[2],
                    orangeColor = colors[3]
                )
            )

            TextureFilterType.Sand -> SandTextureGenerator().generate(
                width = width,
                height = height,
                parameters = SandTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    duneFrequency = values[0],
                    windAngle = values[1],
                    ripples = values[2],
                    grain = values[3],
                    contrast = values[4],
                    shadowColor = colors[0],
                    sandColor = colors[1],
                    lightColor = colors[2]
                )
            )

            TextureFilterType.Smoke -> SmokeTextureGenerator().generate(
                width = width,
                height = height,
                parameters = SmokeTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    turbulence = values[0],
                    density = values[1],
                    wisps = values[2],
                    contrast = values[3],
                    detail = values[4],
                    backgroundColor = colors[0],
                    shadowColor = colors[1],
                    smokeColor = colors[2]
                )
            )

            TextureFilterType.Stone -> StoneTextureGenerator().generate(
                width = width,
                height = height,
                parameters = StoneTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    grain = values[0],
                    veins = values[1],
                    veinScale = values[2],
                    distortion = values[3],
                    contrast = values[4],
                    darkColor = colors[0],
                    lightColor = colors[1],
                    veinColor = colors[2]
                )
            )

            TextureFilterType.Terrain -> TerrainTextureGenerator().generate(
                width = width,
                height = height,
                parameters = TerrainTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    waterLevel = values[0],
                    mountainLevel = values[1],
                    erosion = values[2],
                    detail = values[3],
                    snowLevel = values[4],
                    waterColor = colors[0],
                    lowlandColor = colors[1],
                    rockColor = colors[2],
                    snowColor = colors[3]
                )
            )

            TextureFilterType.Topography -> TopographyTextureGenerator().generate(
                width = width,
                height = height,
                parameters = TopographyTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    lineCount = values[0],
                    lineThickness = values[1],
                    shading = values[2],
                    distortion = values[3],
                    contrast = values[4],
                    lowColor = colors[0],
                    highColor = colors[1],
                    lineColor = colors[2]
                )
            )

            TextureFilterType.WaterRipple -> WaterRippleTextureGenerator().generate(
                width = width,
                height = height,
                parameters = WaterRippleTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    frequency = values[0],
                    distortion = values[1],
                    caustics = values[2],
                    depth = values[3],
                    highlights = values[4],
                    deepColor = colors[0],
                    shallowColor = colors[1],
                    highlightColor = colors[2]
                )
            )

            TextureFilterType.AdvancedWood -> WoodTextureGenerator().generate(
                width = width,
                height = height,
                parameters = WoodTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    rings = values[0],
                    grain = values[1],
                    distortion = values[2],
                    stretch = values[3],
                    contrast = values[4],
                    darkColor = colors[0],
                    lightColor = colors[1]
                )
            )

            else -> error("Unsupported fast-noise texture type: $type")
        }
    }
}
