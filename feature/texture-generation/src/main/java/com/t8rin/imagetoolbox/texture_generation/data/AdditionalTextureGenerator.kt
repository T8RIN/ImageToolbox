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
import com.t8rin.fast_noise.texture.AncientRunesTextureGenerator
import com.t8rin.fast_noise.texture.AncientRunesTextureParameters
import com.t8rin.fast_noise.texture.ApollonianGasketTextureGenerator
import com.t8rin.fast_noise.texture.ApollonianGasketTextureParameters
import com.t8rin.fast_noise.texture.BacterialCultureTextureGenerator
import com.t8rin.fast_noise.texture.BacterialCultureTextureParameters
import com.t8rin.fast_noise.texture.BiomechanicalTissueTextureGenerator
import com.t8rin.fast_noise.texture.BiomechanicalTissueTextureParameters
import com.t8rin.fast_noise.texture.BurningShipTextureGenerator
import com.t8rin.fast_noise.texture.BurningShipTextureParameters
import com.t8rin.fast_noise.texture.CarbonFiberTextureGenerator
import com.t8rin.fast_noise.texture.CarbonFiberTextureParameters
import com.t8rin.fast_noise.texture.CellularEmbryoTextureGenerator
import com.t8rin.fast_noise.texture.CellularEmbryoTextureParameters
import com.t8rin.fast_noise.texture.ChladniPlateTextureGenerator
import com.t8rin.fast_noise.texture.ChladniPlateTextureParameters
import com.t8rin.fast_noise.texture.ChromatophoreTextureGenerator
import com.t8rin.fast_noise.texture.ChromatophoreTextureParameters
import com.t8rin.fast_noise.texture.CircuitBoardTextureGenerator
import com.t8rin.fast_noise.texture.CircuitBoardTextureParameters
import com.t8rin.fast_noise.texture.CloudChamberTextureGenerator
import com.t8rin.fast_noise.texture.CloudChamberTextureParameters
import com.t8rin.fast_noise.texture.CoralGrowthTextureGenerator
import com.t8rin.fast_noise.texture.CoralGrowthTextureParameters
import com.t8rin.fast_noise.texture.CrystalGrowthTextureGenerator
import com.t8rin.fast_noise.texture.CrystalGrowthTextureParameters
import com.t8rin.fast_noise.texture.CymaticRosetteTextureGenerator
import com.t8rin.fast_noise.texture.CymaticRosetteTextureParameters
import com.t8rin.fast_noise.texture.DendriticCrystalTextureGenerator
import com.t8rin.fast_noise.texture.DendriticCrystalTextureParameters
import com.t8rin.fast_noise.texture.DragonScalesTextureGenerator
import com.t8rin.fast_noise.texture.DragonScalesTextureParameters
import com.t8rin.fast_noise.texture.ElectricArcFieldTextureGenerator
import com.t8rin.fast_noise.texture.ElectricArcFieldTextureParameters
import com.t8rin.fast_noise.texture.EmberFieldTextureGenerator
import com.t8rin.fast_noise.texture.EmberFieldTextureParameters
import com.t8rin.fast_noise.texture.FiberOpticBundleTextureGenerator
import com.t8rin.fast_noise.texture.FiberOpticBundleTextureParameters
import com.t8rin.fast_noise.texture.FireflySwarmTextureGenerator
import com.t8rin.fast_noise.texture.FireflySwarmTextureParameters
import com.t8rin.fast_noise.texture.FluidVorticityTextureGenerator
import com.t8rin.fast_noise.texture.FluidVorticityTextureParameters
import com.t8rin.fast_noise.texture.FrostFernTextureGenerator
import com.t8rin.fast_noise.texture.FrostFernTextureParameters
import com.t8rin.fast_noise.texture.GalacticWebTextureGenerator
import com.t8rin.fast_noise.texture.GalacticWebTextureParameters
import com.t8rin.fast_noise.texture.GalaxyFilamentsTextureGenerator
import com.t8rin.fast_noise.texture.GalaxyFilamentsTextureParameters
import com.t8rin.fast_noise.texture.GeodeTextureGenerator
import com.t8rin.fast_noise.texture.GeodeTextureParameters
import com.t8rin.fast_noise.texture.GildedFiligreeTextureGenerator
import com.t8rin.fast_noise.texture.GildedFiligreeTextureParameters
import com.t8rin.fast_noise.texture.HyperbolicTilingTextureGenerator
import com.t8rin.fast_noise.texture.HyperbolicTilingTextureParameters
import com.t8rin.fast_noise.texture.InkWashMountainsTextureGenerator
import com.t8rin.fast_noise.texture.InkWashMountainsTextureParameters
import com.t8rin.fast_noise.texture.JuliaSetTextureGenerator
import com.t8rin.fast_noise.texture.JuliaSetTextureParameters
import com.t8rin.fast_noise.texture.KaleidoscopeCrystalTextureGenerator
import com.t8rin.fast_noise.texture.KaleidoscopeCrystalTextureParameters
import com.t8rin.fast_noise.texture.KelpForestTextureGenerator
import com.t8rin.fast_noise.texture.KelpForestTextureParameters
import com.t8rin.fast_noise.texture.KintsugiTextureGenerator
import com.t8rin.fast_noise.texture.KintsugiTextureParameters
import com.t8rin.fast_noise.texture.LichenColonyTextureGenerator
import com.t8rin.fast_noise.texture.LichenColonyTextureParameters
import com.t8rin.fast_noise.texture.LichtenbergFigureTextureGenerator
import com.t8rin.fast_noise.texture.LichtenbergFigureTextureParameters
import com.t8rin.fast_noise.texture.LiquidCrystalTextureGenerator
import com.t8rin.fast_noise.texture.LiquidCrystalTextureParameters
import com.t8rin.fast_noise.texture.LunarEjectaTextureGenerator
import com.t8rin.fast_noise.texture.LunarEjectaTextureParameters
import com.t8rin.fast_noise.texture.MagneticFieldTextureGenerator
import com.t8rin.fast_noise.texture.MagneticFieldTextureParameters
import com.t8rin.fast_noise.texture.MandelbrotTextureGenerator
import com.t8rin.fast_noise.texture.MandelbrotTextureParameters
import com.t8rin.fast_noise.texture.MicroscopicDiatomsTextureGenerator
import com.t8rin.fast_noise.texture.MicroscopicDiatomsTextureParameters
import com.t8rin.fast_noise.texture.MoebiusWeaveTextureGenerator
import com.t8rin.fast_noise.texture.MoebiusWeaveTextureParameters
import com.t8rin.fast_noise.texture.MoireGuillocheTextureGenerator
import com.t8rin.fast_noise.texture.MoireGuillocheTextureParameters
import com.t8rin.fast_noise.texture.MotherboardHeatmapTextureGenerator
import com.t8rin.fast_noise.texture.MotherboardHeatmapTextureParameters
import com.t8rin.fast_noise.texture.MyceliumTextureGenerator
import com.t8rin.fast_noise.texture.MyceliumTextureParameters
import com.t8rin.fast_noise.texture.NeonCityTextureGenerator
import com.t8rin.fast_noise.texture.NeonCityTextureParameters
import com.t8rin.fast_noise.texture.NeuralGardenTextureGenerator
import com.t8rin.fast_noise.texture.NeuralGardenTextureParameters
import com.t8rin.fast_noise.texture.OceanCurrentsTextureGenerator
import com.t8rin.fast_noise.texture.OceanCurrentsTextureParameters
import com.t8rin.fast_noise.texture.OrigamiFacetsTextureGenerator
import com.t8rin.fast_noise.texture.OrigamiFacetsTextureParameters
import com.t8rin.fast_noise.texture.PhyllotaxisBloomTextureGenerator
import com.t8rin.fast_noise.texture.PhyllotaxisBloomTextureParameters
import com.t8rin.fast_noise.texture.PorousSpongeTextureGenerator
import com.t8rin.fast_noise.texture.PorousSpongeTextureParameters
import com.t8rin.fast_noise.texture.PrismaticLightTextureGenerator
import com.t8rin.fast_noise.texture.PrismaticLightTextureParameters
import com.t8rin.fast_noise.texture.QuantumFoamTextureGenerator
import com.t8rin.fast_noise.texture.QuantumFoamTextureParameters
import com.t8rin.fast_noise.texture.QuasicrystalTextureGenerator
import com.t8rin.fast_noise.texture.QuasicrystalTextureParameters
import com.t8rin.fast_noise.texture.RainOnGlassTextureGenerator
import com.t8rin.fast_noise.texture.RainOnGlassTextureParameters
import com.t8rin.fast_noise.texture.RayleighBenardTextureGenerator
import com.t8rin.fast_noise.texture.RayleighBenardTextureParameters
import com.t8rin.fast_noise.texture.ReactionDiffusionTextureGenerator
import com.t8rin.fast_noise.texture.ReactionDiffusionTextureParameters
import com.t8rin.fast_noise.texture.RiverDeltaTextureGenerator
import com.t8rin.fast_noise.texture.RiverDeltaTextureParameters
import com.t8rin.fast_noise.texture.RorschachInkblotTextureGenerator
import com.t8rin.fast_noise.texture.RorschachInkblotTextureParameters
import com.t8rin.fast_noise.texture.SeismicInterferenceTextureGenerator
import com.t8rin.fast_noise.texture.SeismicInterferenceTextureParameters
import com.t8rin.fast_noise.texture.SierpinskiTriangleTextureGenerator
import com.t8rin.fast_noise.texture.SierpinskiTriangleTextureParameters
import com.t8rin.fast_noise.texture.SlimeMoldTextureGenerator
import com.t8rin.fast_noise.texture.SlimeMoldTextureParameters
import com.t8rin.fast_noise.texture.SnakeSkinTextureGenerator
import com.t8rin.fast_noise.texture.SnakeSkinTextureParameters
import com.t8rin.fast_noise.texture.SoapFilmTextureGenerator
import com.t8rin.fast_noise.texture.SoapFilmTextureParameters
import com.t8rin.fast_noise.texture.SolarGranulationTextureGenerator
import com.t8rin.fast_noise.texture.SolarGranulationTextureParameters
import com.t8rin.fast_noise.texture.SpectralPrismTextureGenerator
import com.t8rin.fast_noise.texture.SpectralPrismTextureParameters
import com.t8rin.fast_noise.texture.StainedGlassTextureGenerator
import com.t8rin.fast_noise.texture.StainedGlassTextureParameters
import com.t8rin.fast_noise.texture.TerrazzoTextureGenerator
import com.t8rin.fast_noise.texture.TerrazzoTextureParameters
import com.t8rin.fast_noise.texture.TopologicalKnotTextureGenerator
import com.t8rin.fast_noise.texture.TopologicalKnotTextureParameters
import com.t8rin.fast_noise.texture.TurbulentInkTextureGenerator
import com.t8rin.fast_noise.texture.TurbulentInkTextureParameters
import com.t8rin.fast_noise.texture.VeinedLeafTextureGenerator
import com.t8rin.fast_noise.texture.VeinedLeafTextureParameters
import com.t8rin.fast_noise.texture.VolcanicObsidianTextureGenerator
import com.t8rin.fast_noise.texture.VolcanicObsidianTextureParameters
import com.t8rin.fast_noise.texture.XRayBotanicalTextureGenerator
import com.t8rin.fast_noise.texture.XRayBotanicalTextureParameters
import com.t8rin.imagetoolbox.texture_generation.domain.model.FastNoiseTextureParams
import com.t8rin.imagetoolbox.texture_generation.domain.model.TextureFilterType

internal fun generateAdditionalFastNoiseTexture(
    width: Int,
    height: Int,
    type: TextureFilterType,
    params: FastNoiseTextureParams
): Bitmap? {
    val values = params.values
    val colors = params.colors.map { it.colorInt }

    return when (type) {
        TextureFilterType.Geode -> GeodeTextureGenerator().generate(
            width = width,
            height = height,
            parameters = GeodeTextureParameters(
                seed = params.seed,
                scale = params.scale,
                bands = values[0],
                distortion = values[1],
                crystalSharpness = values[2],
                sparkle = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.PrismaticLight -> PrismaticLightTextureGenerator().generate(
            width = width,
            height = height,
            parameters = PrismaticLightTextureParameters(
                seed = params.seed,
                scale = params.scale,
                facets = values[0],
                dispersion = values[1],
                beamSharpness = values[2],
                bloom = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.StainedGlass -> StainedGlassTextureGenerator().generate(
            width = width,
            height = height,
            parameters = StainedGlassTextureParameters(
                seed = params.seed,
                scale = params.scale,
                cells = values[0],
                irregularity = values[1],
                leadSharpness = values[2],
                glow = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.KelpForest -> KelpForestTextureGenerator().generate(
            width = width,
            height = height,
            parameters = KelpForestTextureParameters(
                seed = params.seed,
                scale = params.scale,
                density = values[0],
                current = values[1],
                depthSharpness = values[2],
                lightRays = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.FrostFern -> FrostFernTextureGenerator().generate(
            width = width,
            height = height,
            parameters = FrostFernTextureParameters(
                seed = params.seed,
                scale = params.scale,
                branches = values[0],
                branching = values[1],
                crystalSharpness = values[2],
                frostGlow = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.LiquidCrystal -> LiquidCrystalTextureGenerator().generate(
            width = width,
            height = height,
            parameters = LiquidCrystalTextureParameters(
                seed = params.seed,
                scale = params.scale,
                domains = values[0],
                dispersion = values[1],
                boundarySharpness = values[2],
                shine = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.DragonScales -> DragonScalesTextureGenerator().generate(
            width = width,
            height = height,
            parameters = DragonScalesTextureParameters(
                seed = params.seed,
                scale = params.scale,
                scaleDensity = values[0],
                curvature = values[1],
                rimSharpness = values[2],
                iridescence = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.FireflySwarm -> FireflySwarmTextureGenerator().generate(
            width = width,
            height = height,
            parameters = FireflySwarmTextureParameters(
                seed = params.seed,
                scale = params.scale,
                density = values[0],
                drift = values[1],
                trailSharpness = values[2],
                glow = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.Mycelium -> MyceliumTextureGenerator().generate(
            width = width,
            height = height,
            parameters = MyceliumTextureParameters(
                seed = params.seed,
                scale = params.scale,
                networkDensity = values[0],
                branching = values[1],
                fiberSharpness = values[2],
                glow = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.Kintsugi -> KintsugiTextureGenerator().generate(
            width = width,
            height = height,
            parameters = KintsugiTextureParameters(
                seed = params.seed,
                scale = params.scale,
                crackDensity = values[0],
                irregularity = values[1],
                goldSharpness = values[2],
                metallic = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.CarbonFiber -> CarbonFiberTextureGenerator().generate(
            width = width,
            height = height,
            parameters = CarbonFiberTextureParameters(
                seed = params.seed,
                scale = params.scale,
                weaveDensity = values[0],
                twill = values[1],
                fiberSharpness = values[2],
                sheen = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.CircuitBoard -> CircuitBoardTextureGenerator().generate(
            width = width,
            height = height,
            parameters = CircuitBoardTextureParameters(
                seed = params.seed,
                scale = params.scale,
                traceDensity = values[0],
                routing = values[1],
                traceSharpness = values[2],
                emission = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.SoapFilm -> SoapFilmTextureGenerator().generate(
            width = width,
            height = height,
            parameters = SoapFilmTextureParameters(
                seed = params.seed,
                scale = params.scale,
                bands = values[0],
                flow = values[1],
                filmSharpness = values[2],
                shine = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.MoireGuilloche -> MoireGuillocheTextureGenerator().generate(
            width = width,
            height = height,
            parameters = MoireGuillocheTextureParameters(
                seed = params.seed,
                scale = params.scale,
                frequency = values[0],
                offset = values[1],
                lineSharpness = values[2],
                glow = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.SnakeSkin -> SnakeSkinTextureGenerator().generate(
            width = width,
            height = height,
            parameters = SnakeSkinTextureParameters(
                seed = params.seed,
                scale = params.scale,
                scaleDensity = values[0],
                irregularity = values[1],
                rimSharpness = values[2],
                sheen = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.Terrazzo -> TerrazzoTextureGenerator().generate(
            width = width,
            height = height,
            parameters = TerrazzoTextureParameters(
                seed = params.seed,
                scale = params.scale,
                chipDensity = values[0],
                variation = values[1],
                chipSharpness = values[2],
                polish = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.GalaxyFilaments -> GalaxyFilamentsTextureGenerator().generate(
            width = width,
            height = height,
            parameters = GalaxyFilamentsTextureParameters(
                seed = params.seed,
                scale = params.scale,
                filamentDensity = values[0],
                turbulence = values[1],
                filamentSharpness = values[2],
                starGlow = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.VolcanicObsidian -> VolcanicObsidianTextureGenerator().generate(
            width = width,
            height = height,
            parameters = VolcanicObsidianTextureParameters(
                seed = params.seed,
                scale = params.scale,
                facets = values[0],
                fracturing = values[1],
                edgeSharpness = values[2],
                lavaGlow = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.MotherboardHeatmap -> MotherboardHeatmapTextureGenerator().generate(
            width = width,
            height = height,
            parameters = MotherboardHeatmapTextureParameters(
                seed = params.seed,
                scale = params.scale,
                traceDensity = values[0],
                heatSpread = values[1],
                traceSharpness = values[2],
                heatGlow = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.MicroscopicDiatoms -> MicroscopicDiatomsTextureGenerator().generate(
            width = width,
            height = height,
            parameters = MicroscopicDiatomsTextureParameters(
                seed = params.seed,
                scale = params.scale,
                colonyDensity = values[0],
                shellVariation = values[1],
                poreSharpness = values[2],
                luminescence = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.ReactionDiffusion -> ReactionDiffusionTextureGenerator().generate(
            width = width,
            height = height,
            parameters = ReactionDiffusionTextureParameters(
                seed = params.seed,
                scale = params.scale,
                patternScale = values[0],
                feedback = values[1],
                edgeSharpness = values[2],
                glow = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.CoralGrowth -> CoralGrowthTextureGenerator().generate(
            width = width,
            height = height,
            parameters = CoralGrowthTextureParameters(
                seed = params.seed,
                scale = params.scale,
                branchDensity = values[0],
                growthBias = values[1],
                edgeSharpness = values[2],
                glow = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.SlimeMold -> SlimeMoldTextureGenerator().generate(
            width = width,
            height = height,
            parameters = SlimeMoldTextureParameters(
                seed = params.seed,
                scale = params.scale,
                trailDensity = values[0],
                chemotaxis = values[1],
                trailSharpness = values[2],
                glow = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.DendriticCrystal -> DendriticCrystalTextureGenerator().generate(
            width = width,
            height = height,
            parameters = DendriticCrystalTextureParameters(
                seed = params.seed,
                scale = params.scale,
                branchCount = values[0],
                branching = values[1],
                crystalSharpness = values[2],
                glow = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.ElectricArcField -> ElectricArcFieldTextureGenerator().generate(
            width = width,
            height = height,
            parameters = ElectricArcFieldTextureParameters(
                seed = params.seed,
                scale = params.scale,
                arcCount = values[0],
                turbulence = values[1],
                arcSharpness = values[2],
                glow = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.CloudChamber -> CloudChamberTextureGenerator().generate(
            width = width,
            height = height,
            parameters = CloudChamberTextureParameters(
                seed = params.seed,
                scale = params.scale,
                trackDensity = values[0],
                deflection = values[1],
                trackSharpness = values[2],
                glow = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.TurbulentInk -> TurbulentInkTextureGenerator().generate(
            width = width,
            height = height,
            parameters = TurbulentInkTextureParameters(
                seed = params.seed,
                scale = params.scale,
                ribbonDensity = values[0],
                turbulence = values[1],
                edgeSharpness = values[2],
                bleed = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.CellularEmbryo -> CellularEmbryoTextureGenerator().generate(
            width = width,
            height = height,
            parameters = CellularEmbryoTextureParameters(
                seed = params.seed,
                scale = params.scale,
                embryoDensity = values[0],
                morphology = values[1],
                membraneSharpness = values[2],
                glow = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.NeuralGarden -> NeuralGardenTextureGenerator().generate(
            width = width,
            height = height,
            parameters = NeuralGardenTextureParameters(
                seed = params.seed,
                scale = params.scale,
                neuronDensity = values[0],
                branching = values[1],
                fiberSharpness = values[2],
                glow = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.MagneticField -> MagneticFieldTextureGenerator().generate(
            width = width,
            height = height,
            parameters = MagneticFieldTextureParameters(
                seed = params.seed,
                scale = params.scale,
                lineDensity = values[0],
                poleDistortion = values[1],
                lineSharpness = values[2],
                glow = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.RiverDelta -> RiverDeltaTextureGenerator().generate(
            width = width,
            height = height,
            parameters = RiverDeltaTextureParameters(
                seed = params.seed,
                scale = params.scale,
                channelDensity = values[0],
                erosion = values[1],
                channelSharpness = values[2],
                waterGlow = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.LichenColony -> LichenColonyTextureGenerator().generate(
            width = width,
            height = height,
            parameters = LichenColonyTextureParameters(
                seed = params.seed,
                scale = params.scale,
                colonyDensity = values[0],
                spread = values[1],
                edgeSharpness = values[2],
                moisture = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.BacterialCulture -> BacterialCultureTextureGenerator().generate(
            width = width,
            height = height,
            parameters = BacterialCultureTextureParameters(
                seed = params.seed,
                scale = params.scale,
                colonyDensity = values[0],
                growthVariation = values[1],
                membraneSharpness = values[2],
                glow = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.FluidVorticity -> FluidVorticityTextureGenerator().generate(
            width = width,
            height = height,
            parameters = FluidVorticityTextureParameters(
                seed = params.seed,
                scale = params.scale,
                vortexDensity = values[0],
                turbulence = values[1],
                ridgeSharpness = values[2],
                glow = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.CrystalGrowth -> CrystalGrowthTextureGenerator().generate(
            width = width,
            height = height,
            parameters = CrystalGrowthTextureParameters(
                seed = params.seed,
                scale = params.scale,
                crystalDensity = values[0],
                anisotropy = values[1],
                facetSharpness = values[2],
                glow = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.GalacticWeb -> GalacticWebTextureGenerator().generate(
            width = width,
            height = height,
            parameters = GalacticWebTextureParameters(
                seed = params.seed,
                scale = params.scale,
                nodeDensity = values[0],
                gravityWarp = values[1],
                filamentSharpness = values[2],
                glow = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.VeinedLeaf -> VeinedLeafTextureGenerator().generate(
            width = width,
            height = height,
            parameters = VeinedLeafTextureParameters(
                seed = params.seed,
                scale = params.scale,
                veinDensity = values[0],
                curvature = values[1],
                veinSharpness = values[2],
                translucency = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.PorousSponge -> PorousSpongeTextureGenerator().generate(
            width = width,
            height = height,
            parameters = PorousSpongeTextureParameters(
                seed = params.seed,
                scale = params.scale,
                poreDensity = values[0],
                irregularity = values[1],
                poreSharpness = values[2],
                moisture = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.RainOnGlass -> RainOnGlassTextureGenerator().generate(
            width = width,
            height = height,
            parameters = RainOnGlassTextureParameters(
                seed = params.seed,
                scale = params.scale,
                dropDensity = values[0],
                streaking = values[1],
                dropSharpness = values[2],
                refraction = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.EmberField -> EmberFieldTextureGenerator().generate(
            width = width,
            height = height,
            parameters = EmberFieldTextureParameters(
                seed = params.seed,
                scale = params.scale,
                emberDensity = values[0],
                updraft = values[1],
                emberSharpness = values[2],
                glow = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.QuantumFoam -> QuantumFoamTextureGenerator().generate(
            width = width,
            height = height,
            parameters = QuantumFoamTextureParameters(
                seed = params.seed,
                scale = params.scale,
                bubbleDensity = values[0],
                uncertainty = values[1],
                boundarySharpness = values[2],
                glow = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.ChladniPlate -> ChladniPlateTextureGenerator().generate(
            width = width,
            height = height,
            parameters = ChladniPlateTextureParameters(
                seed = params.seed,
                scale = params.scale,
                modeCount = values[0],
                asymmetry = values[1],
                lineSharpness = values[2],
                metallic = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.CymaticRosette -> CymaticRosetteTextureGenerator().generate(
            width = width,
            height = height,
            parameters = CymaticRosetteTextureParameters(
                seed = params.seed,
                scale = params.scale,
                petalCount = values[0],
                resonance = values[1],
                ridgeSharpness = values[2],
                glow = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.LichtenbergFigure -> LichtenbergFigureTextureGenerator().generate(
            width = width,
            height = height,
            parameters = LichtenbergFigureTextureParameters(
                seed = params.seed,
                scale = params.scale,
                branchCount = values[0],
                branching = values[1],
                arcSharpness = values[2],
                glow = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.Quasicrystal -> QuasicrystalTextureGenerator().generate(
            width = width,
            height = height,
            parameters = QuasicrystalTextureParameters(
                seed = params.seed,
                scale = params.scale,
                symmetry = values[0],
                phaseWarp = values[1],
                ridgeSharpness = values[2],
                glow = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.Mandelbrot -> MandelbrotTextureGenerator().generate(
            width = width,
            height = height,
            parameters = MandelbrotTextureParameters(
                seed = params.seed,
                scale = params.scale,
                iterationDetail = values[0],
                centerOffset = values[1],
                boundarySharpness = values[2],
                glow = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.BurningShip -> BurningShipTextureGenerator().generate(
            width = width,
            height = height,
            parameters = BurningShipTextureParameters(
                seed = params.seed,
                scale = params.scale,
                iterationDetail = values[0],
                centerOffset = values[1],
                boundarySharpness = values[2],
                glow = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.JuliaSet -> JuliaSetTextureGenerator().generate(
            width = width,
            height = height,
            parameters = JuliaSetTextureParameters(
                seed = params.seed,
                scale = params.scale,
                iterations = values[0],
                constantPhase = values[1],
                filamentSharpness = values[2],
                glow = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.KaleidoscopeCrystal -> KaleidoscopeCrystalTextureGenerator().generate(
            width = width,
            height = height,
            parameters = KaleidoscopeCrystalTextureParameters(
                seed = params.seed,
                scale = params.scale,
                segments = values[0],
                foldWarp = values[1],
                facetSharpness = values[2],
                glow = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.SpectralPrism -> SpectralPrismTextureGenerator().generate(
            width = width,
            height = height,
            parameters = SpectralPrismTextureParameters(
                seed = params.seed,
                scale = params.scale,
                facetCount = values[0],
                refraction = values[1],
                edgeSharpness = values[2],
                bloom = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.TopologicalKnot -> TopologicalKnotTextureGenerator().generate(
            width = width,
            height = height,
            parameters = TopologicalKnotTextureParameters(
                seed = params.seed,
                scale = params.scale,
                turns = values[0],
                twist = values[1],
                tubeSharpness = values[2],
                glow = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.XRayBotanical -> XRayBotanicalTextureGenerator().generate(
            width = width,
            height = height,
            parameters = XRayBotanicalTextureParameters(
                seed = params.seed,
                scale = params.scale,
                leafDensity = values[0],
                curvature = values[1],
                veinSharpness = values[2],
                luminescence = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.Chromatophore -> ChromatophoreTextureGenerator().generate(
            width = width,
            height = height,
            parameters = ChromatophoreTextureParameters(
                seed = params.seed,
                scale = params.scale,
                cellDensity = values[0],
                pulsePhase = values[1],
                cellSharpness = values[2],
                iridescence = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.BiomechanicalTissue -> BiomechanicalTissueTextureGenerator().generate(
            width = width,
            height = height,
            parameters = BiomechanicalTissueTextureParameters(
                seed = params.seed,
                scale = params.scale,
                ribDensity = values[0],
                tension = values[1],
                ridgeSharpness = values[2],
                wetSheen = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.GildedFiligree -> GildedFiligreeTextureGenerator().generate(
            width = width,
            height = height,
            parameters = GildedFiligreeTextureParameters(
                seed = params.seed,
                scale = params.scale,
                curlDensity = values[0],
                ornament = values[1],
                lineSharpness = values[2],
                metallic = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.AncientRunes -> AncientRunesTextureGenerator().generate(
            width = width,
            height = height,
            parameters = AncientRunesTextureParameters(
                seed = params.seed,
                scale = params.scale,
                glyphDensity = values[0],
                erosion = values[1],
                strokeSharpness = values[2],
                glow = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.SolarGranulation -> SolarGranulationTextureGenerator().generate(
            width = width,
            height = height,
            parameters = SolarGranulationTextureParameters(
                seed = params.seed,
                scale = params.scale,
                granuleDensity = values[0],
                convection = values[1],
                edgeSharpness = values[2],
                heatGlow = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.LunarEjecta -> LunarEjectaTextureGenerator().generate(
            width = width,
            height = height,
            parameters = LunarEjectaTextureParameters(
                seed = params.seed,
                scale = params.scale,
                craterDensity = values[0],
                rayLength = values[1],
                rimSharpness = values[2],
                albedo = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.OceanCurrents -> OceanCurrentsTextureGenerator().generate(
            width = width,
            height = height,
            parameters = OceanCurrentsTextureParameters(
                seed = params.seed,
                scale = params.scale,
                currentDensity = values[0],
                curl = values[1],
                streamSharpness = values[2],
                glow = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.InkWashMountains -> InkWashMountainsTextureGenerator().generate(
            width = width,
            height = height,
            parameters = InkWashMountainsTextureParameters(
                seed = params.seed,
                scale = params.scale,
                ridgeCount = values[0],
                mist = values[1],
                ridgeSharpness = values[2],
                paperGlow = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.NeonCity -> NeonCityTextureGenerator().generate(
            width = width,
            height = height,
            parameters = NeonCityTextureParameters(
                seed = params.seed,
                scale = params.scale,
                buildingDensity = values[0],
                perspective = values[1],
                edgeSharpness = values[2],
                neonGlow = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.PhyllotaxisBloom -> PhyllotaxisBloomTextureGenerator().generate(
            width = width,
            height = height,
            parameters = PhyllotaxisBloomTextureParameters(
                seed = params.seed,
                scale = params.scale,
                seedDensity = values[0],
                spiralAngle = values[1],
                seedSharpness = values[2],
                glow = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.SierpinskiTriangle -> SierpinskiTriangleTextureGenerator().generate(
            width = width,
            height = height,
            parameters = SierpinskiTriangleTextureParameters(
                seed = params.seed,
                scale = params.scale,
                depth = values[0],
                rotation = values[1],
                edgeSharpness = values[2],
                glow = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.ApollonianGasket -> ApollonianGasketTextureGenerator().generate(
            width = width,
            height = height,
            parameters = ApollonianGasketTextureParameters(
                seed = params.seed,
                scale = params.scale,
                recursion = values[0],
                curvature = values[1],
                boundarySharpness = values[2],
                glow = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.HyperbolicTiling -> HyperbolicTilingTextureGenerator().generate(
            width = width,
            height = height,
            parameters = HyperbolicTilingTextureParameters(
                seed = params.seed,
                scale = params.scale,
                polygonSides = values[0],
                curvature = values[1],
                edgeSharpness = values[2],
                glow = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.MoebiusWeave -> MoebiusWeaveTextureGenerator().generate(
            width = width,
            height = height,
            parameters = MoebiusWeaveTextureParameters(
                seed = params.seed,
                scale = params.scale,
                bandCount = values[0],
                twist = values[1],
                edgeSharpness = values[2],
                sheen = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.RorschachInkblot -> RorschachInkblotTextureGenerator().generate(
            width = width,
            height = height,
            parameters = RorschachInkblotTextureParameters(
                seed = params.seed,
                scale = params.scale,
                lobeCount = values[0],
                symmetry = values[1],
                edgeSharpness = values[2],
                bleed = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.SeismicInterference -> SeismicInterferenceTextureGenerator().generate(
            width = width,
            height = height,
            parameters = SeismicInterferenceTextureParameters(
                seed = params.seed,
                scale = params.scale,
                sourceCount = values[0],
                phase = values[1],
                ridgeSharpness = values[2],
                glow = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.RayleighBenard -> RayleighBenardTextureGenerator().generate(
            width = width,
            height = height,
            parameters = RayleighBenardTextureParameters(
                seed = params.seed,
                scale = params.scale,
                cellDensity = values[0],
                convection = values[1],
                edgeSharpness = values[2],
                heatGlow = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.OrigamiFacets -> OrigamiFacetsTextureGenerator().generate(
            width = width,
            height = height,
            parameters = OrigamiFacetsTextureParameters(
                seed = params.seed,
                scale = params.scale,
                foldDensity = values[0],
                asymmetry = values[1],
                creaseSharpness = values[2],
                sheen = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        TextureFilterType.FiberOpticBundle -> FiberOpticBundleTextureGenerator().generate(
            width = width,
            height = height,
            parameters = FiberOpticBundleTextureParameters(
                seed = params.seed,
                scale = params.scale,
                fiberDensity = values[0],
                bend = values[1],
                coreSharpness = values[2],
                glow = values[3],
                contrast = values[4],
                backgroundColor = colors[0],
                primaryColor = colors[1],
                secondaryColor = colors[2],
                highlightColor = colors[3]
            )
        )

        else -> error("Unsupported additional fast-noise texture type: $type")
    }
}
