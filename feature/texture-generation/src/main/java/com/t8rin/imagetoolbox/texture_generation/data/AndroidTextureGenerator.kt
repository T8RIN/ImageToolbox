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
import com.t8rin.fast_noise.texture.AsphaltTextureGenerator
import com.t8rin.fast_noise.texture.AsphaltTextureParameters
import com.t8rin.fast_noise.texture.AuroraTextureGenerator
import com.t8rin.fast_noise.texture.AuroraTextureParameters
import com.t8rin.fast_noise.texture.BioluminescenceTextureGenerator
import com.t8rin.fast_noise.texture.BioluminescenceTextureParameters
import com.t8rin.fast_noise.texture.BrickTextureGenerator
import com.t8rin.fast_noise.texture.BrickTextureParameters
import com.t8rin.fast_noise.texture.CamouflageTextureGenerator
import com.t8rin.fast_noise.texture.CamouflageTextureParameters
import com.t8rin.fast_noise.texture.CellTextureGenerator
import com.t8rin.fast_noise.texture.CellTextureParameters
import com.t8rin.fast_noise.texture.ChromaticTunnelTextureGenerator
import com.t8rin.fast_noise.texture.ChromaticTunnelTextureParameters
import com.t8rin.fast_noise.texture.CloudTextureGenerator
import com.t8rin.fast_noise.texture.CloudTextureParameters
import com.t8rin.fast_noise.texture.ConcreteTextureGenerator
import com.t8rin.fast_noise.texture.ConcreteTextureParameters
import com.t8rin.fast_noise.texture.CosmicVortexTextureGenerator
import com.t8rin.fast_noise.texture.CosmicVortexTextureParameters
import com.t8rin.fast_noise.texture.CrackTextureGenerator
import com.t8rin.fast_noise.texture.CrackTextureParameters
import com.t8rin.fast_noise.texture.DamascusTextureGenerator
import com.t8rin.fast_noise.texture.DamascusTextureParameters
import com.t8rin.fast_noise.texture.DirtTextureGenerator
import com.t8rin.fast_noise.texture.DirtTextureParameters
import com.t8rin.fast_noise.texture.EclipseCoronaTextureGenerator
import com.t8rin.fast_noise.texture.EclipseCoronaTextureParameters
import com.t8rin.fast_noise.texture.EventHorizonTextureGenerator
import com.t8rin.fast_noise.texture.EventHorizonTextureParameters
import com.t8rin.fast_noise.texture.FabricTextureGenerator
import com.t8rin.fast_noise.texture.FabricTextureParameters
import com.t8rin.fast_noise.texture.FerrofluidCrownTextureGenerator
import com.t8rin.fast_noise.texture.FerrofluidCrownTextureParameters
import com.t8rin.fast_noise.texture.FireTextureGenerator
import com.t8rin.fast_noise.texture.FireTextureParameters
import com.t8rin.fast_noise.texture.FlowTextureGenerator
import com.t8rin.fast_noise.texture.FlowTextureParameters
import com.t8rin.fast_noise.texture.FoliageTextureGenerator
import com.t8rin.fast_noise.texture.FoliageTextureParameters
import com.t8rin.fast_noise.texture.FractalBloomTextureGenerator
import com.t8rin.fast_noise.texture.FractalBloomTextureParameters
import com.t8rin.fast_noise.texture.GrassTextureGenerator
import com.t8rin.fast_noise.texture.GrassTextureParameters
import com.t8rin.fast_noise.texture.HolographicTextureGenerator
import com.t8rin.fast_noise.texture.HolographicTextureParameters
import com.t8rin.fast_noise.texture.HoneycombTextureGenerator
import com.t8rin.fast_noise.texture.HoneycombTextureParameters
import com.t8rin.fast_noise.texture.IceTextureGenerator
import com.t8rin.fast_noise.texture.IceTextureParameters
import com.t8rin.fast_noise.texture.InkMarblingTextureGenerator
import com.t8rin.fast_noise.texture.InkMarblingTextureParameters
import com.t8rin.fast_noise.texture.IrisTextureGenerator
import com.t8rin.fast_noise.texture.IrisTextureParameters
import com.t8rin.fast_noise.texture.LavaLampTextureGenerator
import com.t8rin.fast_noise.texture.LavaLampTextureParameters
import com.t8rin.fast_noise.texture.LavaTextureGenerator
import com.t8rin.fast_noise.texture.LavaTextureParameters
import com.t8rin.fast_noise.texture.LeatherTextureGenerator
import com.t8rin.fast_noise.texture.LeatherTextureParameters
import com.t8rin.fast_noise.texture.LightningTextureGenerator
import com.t8rin.fast_noise.texture.LightningTextureParameters
import com.t8rin.fast_noise.texture.MossTextureGenerator
import com.t8rin.fast_noise.texture.MossTextureParameters
import com.t8rin.fast_noise.texture.NautilusShellTextureGenerator
import com.t8rin.fast_noise.texture.NautilusShellTextureParameters
import com.t8rin.fast_noise.texture.NebulaTextureGenerator
import com.t8rin.fast_noise.texture.NebulaTextureParameters
import com.t8rin.fast_noise.texture.OilSlickTextureGenerator
import com.t8rin.fast_noise.texture.OilSlickTextureParameters
import com.t8rin.fast_noise.texture.OpalTextureGenerator
import com.t8rin.fast_noise.texture.OpalTextureParameters
import com.t8rin.fast_noise.texture.PaperTextureGenerator
import com.t8rin.fast_noise.texture.PaperTextureParameters
import com.t8rin.fast_noise.texture.PeacockFeatherTextureGenerator
import com.t8rin.fast_noise.texture.PeacockFeatherTextureParameters
import com.t8rin.fast_noise.texture.RingedPlanetTextureGenerator
import com.t8rin.fast_noise.texture.RingedPlanetTextureParameters
import com.t8rin.fast_noise.texture.RustTextureGenerator
import com.t8rin.fast_noise.texture.RustTextureParameters
import com.t8rin.fast_noise.texture.SandTextureGenerator
import com.t8rin.fast_noise.texture.SandTextureParameters
import com.t8rin.fast_noise.texture.SmokeTextureGenerator
import com.t8rin.fast_noise.texture.SmokeTextureParameters
import com.t8rin.fast_noise.texture.StoneTextureGenerator
import com.t8rin.fast_noise.texture.StoneTextureParameters
import com.t8rin.fast_noise.texture.StrangeAttractorTextureGenerator
import com.t8rin.fast_noise.texture.StrangeAttractorTextureParameters
import com.t8rin.fast_noise.texture.SupernovaTextureGenerator
import com.t8rin.fast_noise.texture.SupernovaTextureParameters
import com.t8rin.fast_noise.texture.TerrainTextureGenerator
import com.t8rin.fast_noise.texture.TerrainTextureParameters
import com.t8rin.fast_noise.texture.TopographyTextureGenerator
import com.t8rin.fast_noise.texture.TopographyTextureParameters
import com.t8rin.fast_noise.texture.VelvetTextureGenerator
import com.t8rin.fast_noise.texture.VelvetTextureParameters
import com.t8rin.fast_noise.texture.WaterRippleTextureGenerator
import com.t8rin.fast_noise.texture.WaterRippleTextureParameters
import com.t8rin.fast_noise.texture.WatercolorTextureGenerator
import com.t8rin.fast_noise.texture.WatercolorTextureParameters
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
                    lightColor = colors[1],
                    poreColor = colors[2]
                )
            )

            TextureFilterType.Grass -> GrassTextureGenerator().generate(
                width = width,
                height = height,
                parameters = GrassTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    bladeDensity = values[0],
                    bladeLength = values[1],
                    wind = values[2],
                    patchiness = values[3],
                    highlights = values[4],
                    dirtColor = colors[0],
                    darkGrassColor = colors[1],
                    grassColor = colors[2],
                    tipColor = colors[3]
                )
            )

            TextureFilterType.Dirt -> DirtTextureGenerator().generate(
                width = width,
                height = height,
                parameters = DirtTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    clumps = values[0],
                    moisture = values[1],
                    pebbles = values[2],
                    roughness = values[3],
                    variation = values[4],
                    darkEarthColor = colors[0],
                    earthColor = colors[1],
                    dryColor = colors[2],
                    pebbleColor = colors[3]
                )
            )

            TextureFilterType.Leather -> LeatherTextureGenerator().generate(
                width = width,
                height = height,
                parameters = LeatherTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    wrinkles = values[0],
                    pores = values[1],
                    grain = values[2],
                    softness = values[3],
                    shine = values[4],
                    shadowColor = colors[0],
                    leatherColor = colors[1],
                    lightColor = colors[2],
                    poreColor = colors[3]
                )
            )

            TextureFilterType.Concrete -> ConcreteTextureGenerator().generate(
                width = width,
                height = height,
                parameters = ConcreteTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    aggregate = values[0],
                    stains = values[1],
                    roughness = values[2],
                    cracks = values[3],
                    contrast = values[4],
                    darkColor = colors[0],
                    concreteColor = colors[1],
                    lightColor = colors[2],
                    crackColor = colors[3]
                )
            )

            TextureFilterType.Asphalt -> AsphaltTextureGenerator().generate(
                width = width,
                height = height,
                parameters = AsphaltTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    aggregate = values[0],
                    tar = values[1],
                    wear = values[2],
                    speckles = values[3],
                    contrast = values[4],
                    tarColor = colors[0],
                    asphaltColor = colors[1],
                    stoneColor = colors[2],
                    dustColor = colors[3]
                )
            )

            TextureFilterType.Moss -> MossTextureGenerator().generate(
                width = width,
                height = height,
                parameters = MossTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    density = values[0],
                    fibers = values[1],
                    moisture = values[2],
                    variation = values[3],
                    clumps = values[4],
                    soilColor = colors[0],
                    darkMossColor = colors[1],
                    mossColor = colors[2],
                    tipColor = colors[3]
                )
            )

            TextureFilterType.Fire -> FireTextureGenerator().generate(
                width = width,
                height = height,
                parameters = FireTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    flameFrequency = values[0],
                    turbulence = values[1],
                    intensity = values[2],
                    smoke = values[3],
                    detail = values[4],
                    backgroundColor = colors[0],
                    redColor = colors[1],
                    orangeColor = colors[2],
                    coreColor = colors[3]
                )
            )

            TextureFilterType.Aurora -> AuroraTextureGenerator().generate(
                width = width,
                height = height,
                parameters = AuroraTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    ribbons = values[0],
                    distortion = values[1],
                    glow = values[2],
                    stars = values[3],
                    contrast = values[4],
                    skyColor = colors[0],
                    greenColor = colors[1],
                    cyanColor = colors[2],
                    violetColor = colors[3]
                )
            )

            TextureFilterType.OilSlick -> OilSlickTextureGenerator().generate(
                width = width,
                height = height,
                parameters = OilSlickTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    bands = values[0],
                    distortion = values[1],
                    iridescence = values[2],
                    darkness = values[3],
                    contrast = values[4],
                    darkColor = colors[0],
                    magentaColor = colors[1],
                    cyanColor = colors[2],
                    goldColor = colors[3]
                )
            )

            TextureFilterType.Watercolor -> WatercolorTextureGenerator().generate(
                width = width,
                height = height,
                parameters = WatercolorTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    blooms = values[0],
                    pigment = values[1],
                    edges = values[2],
                    paper = values[3],
                    diffusion = values[4],
                    paperColor = colors[0],
                    pigmentColor = colors[1],
                    secondaryColor = colors[2],
                    edgeColor = colors[3]
                )
            )

            TextureFilterType.AbstractFlow -> FlowTextureGenerator().generate(
                width = width,
                height = height,
                parameters = FlowTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    frequency = values[0],
                    distortion = values[1],
                    symmetry = values[2],
                    sharpness = values[3],
                    glow = values[4],
                    backgroundColor = colors[0],
                    firstColor = colors[1],
                    secondColor = colors[2],
                    glowColor = colors[3]
                )
            )

            TextureFilterType.Opal -> OpalTextureGenerator().generate(
                width = width,
                height = height,
                parameters = OpalTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    colorPlay = values[0],
                    milkiness = values[1],
                    bands = values[2],
                    distortion = values[3],
                    glow = values[4],
                    baseColor = colors[0],
                    cyanColor = colors[1],
                    pinkColor = colors[2],
                    goldColor = colors[3]
                )
            )

            TextureFilterType.DamascusSteel -> DamascusTextureGenerator().generate(
                width = width,
                height = height,
                parameters = DamascusTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    layers = values[0],
                    folding = values[1],
                    distortion = values[2],
                    polish = values[3],
                    contrast = values[4],
                    darkSteelColor = colors[0],
                    steelColor = colors[1],
                    lightSteelColor = colors[2],
                    oxideColor = colors[3]
                )
            )

            TextureFilterType.Lightning -> LightningTextureGenerator().generate(
                width = width,
                height = height,
                parameters = LightningTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    branches = values[0],
                    turbulence = values[1],
                    width = values[2],
                    glow = values[3],
                    intensity = values[4],
                    backgroundColor = colors[0],
                    haloColor = colors[1],
                    boltColor = colors[2],
                    coreColor = colors[3]
                )
            )

            TextureFilterType.Velvet -> VelvetTextureGenerator().generate(
                width = width,
                height = height,
                parameters = VelvetTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    fibers = values[0],
                    direction = values[1],
                    softness = values[2],
                    sheen = values[3],
                    folds = values[4],
                    shadowColor = colors[0],
                    velvetColor = colors[1],
                    sheenColor = colors[2],
                    highlightColor = colors[3]
                )
            )

            TextureFilterType.InkMarbling -> InkMarblingTextureGenerator().generate(
                width = width,
                height = height,
                parameters = InkMarblingTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    ribbons = values[0],
                    turbulence = values[1],
                    feathering = values[2],
                    inkBalance = values[3],
                    contrast = values[4],
                    paperColor = colors[0],
                    blueInkColor = colors[1],
                    redInkColor = colors[2],
                    darkInkColor = colors[3]
                )
            )

            TextureFilterType.HolographicFoil -> HolographicTextureGenerator().generate(
                width = width,
                height = height,
                parameters = HolographicTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    spectrum = values[0],
                    crinkles = values[1],
                    diffraction = values[2],
                    angle = values[3],
                    shine = values[4],
                    silverColor = colors[0],
                    cyanColor = colors[1],
                    magentaColor = colors[2],
                    yellowColor = colors[3]
                )
            )

            TextureFilterType.Bioluminescence -> BioluminescenceTextureGenerator().generate(
                width = width,
                height = height,
                parameters = BioluminescenceTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    veins = values[0],
                    branching = values[1],
                    turbulence = values[2],
                    glow = values[3],
                    depth = values[4],
                    backgroundColor = colors[0],
                    tissueColor = colors[1],
                    glowColor = colors[2],
                    coreColor = colors[3]
                )
            )

            TextureFilterType.CosmicVortex -> CosmicVortexTextureGenerator().generate(
                width = width,
                height = height,
                parameters = CosmicVortexTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    arms = values[0],
                    twist = values[1],
                    turbulence = values[2],
                    stars = values[3],
                    coreGlow = values[4],
                    spaceColor = colors[0],
                    blueColor = colors[1],
                    violetColor = colors[2],
                    coreColor = colors[3]
                )
            )

            TextureFilterType.LavaLamp -> LavaLampTextureGenerator().generate(
                width = width,
                height = height,
                parameters = LavaLampTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    blobs = values[0],
                    softness = values[1],
                    distortion = values[2],
                    glow = values[3],
                    contrast = values[4],
                    backgroundColor = colors[0],
                    firstColor = colors[1],
                    secondColor = colors[2],
                    glowColor = colors[3]
                )
            )

            TextureFilterType.EventHorizon -> EventHorizonTextureGenerator().generate(
                width = width,
                height = height,
                parameters = EventHorizonTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    diskTilt = values[0],
                    horizonSize = values[1],
                    diskWidth = values[2],
                    lensing = values[3],
                    stars = values[4],
                    spaceColor = colors[0],
                    diskColor = colors[1],
                    hotColor = colors[2],
                    lensColor = colors[3]
                )
            )

            TextureFilterType.FractalBloom -> FractalBloomTextureGenerator().generate(
                width = width,
                height = height,
                parameters = FractalBloomTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    petals = values[0],
                    layers = values[1],
                    curl = values[2],
                    filigree = values[3],
                    glow = values[4],
                    backgroundColor = colors[0],
                    outerColor = colors[1],
                    innerColor = colors[2],
                    coreColor = colors[3]
                )
            )

            TextureFilterType.ChromaticTunnel -> ChromaticTunnelTextureGenerator().generate(
                width = width,
                height = height,
                parameters = ChromaticTunnelTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    depth = values[0],
                    twist = values[1],
                    facets = values[2],
                    curvature = values[3],
                    glow = values[4],
                    deepColor = colors[0],
                    cyanColor = colors[1],
                    magentaColor = colors[2],
                    lightColor = colors[3]
                )
            )

            TextureFilterType.EclipseCorona -> EclipseCoronaTextureGenerator().generate(
                width = width,
                height = height,
                parameters = EclipseCoronaTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    moonSize = values[0],
                    coronaSize = values[1],
                    rays = values[2],
                    turbulence = values[3],
                    diamondRing = values[4],
                    spaceColor = colors[0],
                    coronaColor = colors[1],
                    hotColor = colors[2],
                    lightColor = colors[3]
                )
            )

            TextureFilterType.StrangeAttractor -> StrangeAttractorTextureGenerator().generate(
                width = width,
                height = height,
                parameters = StrangeAttractorTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    lobes = values[0],
                    orbitDensity = values[1],
                    curvature = values[2],
                    thickness = values[3],
                    glow = values[4],
                    backgroundColor = colors[0],
                    coldColor = colors[1],
                    warmColor = colors[2],
                    coreColor = colors[3]
                )
            )

            TextureFilterType.FerrofluidCrown -> FerrofluidCrownTextureGenerator().generate(
                width = width,
                height = height,
                parameters = FerrofluidCrownTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    spikes = values[0],
                    spikeLength = values[1],
                    bodySize = values[2],
                    metallic = values[3],
                    distortion = values[4],
                    backgroundColor = colors[0],
                    shadowColor = colors[1],
                    metalColor = colors[2],
                    highlightColor = colors[3]
                )
            )

            TextureFilterType.Supernova -> SupernovaTextureGenerator().generate(
                width = width,
                height = height,
                parameters = SupernovaTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    shockRadius = values[0],
                    shellWidth = values[1],
                    ejecta = values[2],
                    turbulence = values[3],
                    stars = values[4],
                    spaceColor = colors[0],
                    cloudColor = colors[1],
                    flameColor = colors[2],
                    coreColor = colors[3]
                )
            )

            TextureFilterType.Iris -> IrisTextureGenerator().generate(
                width = width,
                height = height,
                parameters = IrisTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    pupilSize = values[0],
                    irisSize = values[1],
                    fibers = values[2],
                    colorVariation = values[3],
                    catchlight = values[4],
                    backgroundColor = colors[0],
                    outerColor = colors[1],
                    innerColor = colors[2],
                    goldColor = colors[3]
                )
            )

            TextureFilterType.PeacockFeather -> PeacockFeatherTextureGenerator().generate(
                width = width,
                height = height,
                parameters = PeacockFeatherTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    eyeSize = values[0],
                    barbDensity = values[1],
                    curvature = values[2],
                    iridescence = values[3],
                    softness = values[4],
                    backgroundColor = colors[0],
                    featherColor = colors[1],
                    blueColor = colors[2],
                    goldColor = colors[3]
                )
            )

            TextureFilterType.NautilusShell -> NautilusShellTextureGenerator().generate(
                width = width,
                height = height,
                parameters = NautilusShellTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    turns = values[0],
                    chambers = values[1],
                    opening = values[2],
                    ridges = values[3],
                    pearlescence = values[4],
                    backgroundColor = colors[0],
                    shadowColor = colors[1],
                    shellColor = colors[2],
                    pearlColor = colors[3]
                )
            )

            TextureFilterType.RingedPlanet -> RingedPlanetTextureGenerator().generate(
                width = width,
                height = height,
                parameters = RingedPlanetTextureParameters(
                    seed = params.seed,
                    scale = params.scale,
                    planetSize = values[0],
                    ringTilt = values[1],
                    ringWidth = values[2],
                    atmosphere = values[3],
                    stars = values[4],
                    spaceColor = colors[0],
                    shadowColor = colors[1],
                    planetColor = colors[2],
                    ringColor = colors[3]
                )
            )

            else -> generateAdditionalFastNoiseTexture(
                width = width,
                height = height,
                type = type,
                params = params
            )
        }
    }
}
