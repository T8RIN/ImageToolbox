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

import com.t8rin.imagetoolbox.core.ksp.annotations.FastNoiseTextureMappings
import com.t8rin.imagetoolbox.core.ksp.annotations.FastNoiseTextureName

@FastNoiseTextureMappings
enum class TextureFilterType(val isFastNoise: Boolean = false) {
    BrushedMetal,
    Brick(true),
    Camouflage(true),
    Cell(true),
    Cloud(true),
    Crack(true),
    Fabric(true),
    Foliage(true),
    Honeycomb(true),
    Ice(true),
    Lava(true),
    Nebula(true),
    Paper(true),
    Rust(true),
    Sand(true),
    Smoke(true),
    Stone(true),
    Terrain(true),
    Topography(true),
    WaterRipple(true),

    @FastNoiseTextureName("Wood")
    AdvancedWood(true),

    Grass(true),
    Dirt(true),
    Leather(true),
    Concrete(true),
    Asphalt(true),
    Moss(true),
    Fire(true),
    Aurora(true),
    OilSlick(true),
    Watercolor(true),

    @FastNoiseTextureName("Flow")
    AbstractFlow(true),

    Opal(true),

    @FastNoiseTextureName("Damascus")
    DamascusSteel(true),

    Lightning(true),
    Velvet(true),
    InkMarbling(true),

    @FastNoiseTextureName("Holographic")
    HolographicFoil(true),

    Bioluminescence(true),
    CosmicVortex(true),
    LavaLamp(true),
    EventHorizon(true),
    FractalBloom(true),
    ChromaticTunnel(true),
    EclipseCorona(true),
    StrangeAttractor(true),
    FerrofluidCrown(true),
    Supernova(true),
    Iris(true),
    PeacockFeather(true),
    NautilusShell(true),
    RingedPlanet(true),
    Geode(true),
    PrismaticLight(true),
    StainedGlass(true),
    KelpForest(true),
    FrostFern(true),
    LiquidCrystal(true),
    DragonScales(true),
    FireflySwarm(true),
    Mycelium(true),
    Kintsugi(true),
    CarbonFiber(true),
    CircuitBoard(true),
    SoapFilm(true),
    MoireGuilloche(true),
    SnakeSkin(true),
    Terrazzo(true),
    GalaxyFilaments(true),
    VolcanicObsidian(true),
    MotherboardHeatmap(true),
    MicroscopicDiatoms(true),
    ReactionDiffusion(true),
    CoralGrowth(true),
    SlimeMold(true),
    DendriticCrystal(true),
    ElectricArcField(true),
    CloudChamber(true),
    TurbulentInk(true),
    CellularEmbryo(true),
    NeuralGarden(true),
    MagneticField(true),
    RiverDelta(true),
    LichenColony(true),
    BacterialCulture(true),
    FluidVorticity(true),
    CrystalGrowth(true),
    GalacticWeb(true),
    VeinedLeaf(true),
    PorousSponge(true),
    RainOnGlass(true),
    EmberField(true),
    QuantumFoam(true),
    ChladniPlate(true),
    CymaticRosette(true),
    LichtenbergFigure(true),
    Quasicrystal(true),
    Mandelbrot(true),
    BurningShip(true),
    JuliaSet(true),
    KaleidoscopeCrystal(true),
    SpectralPrism(true),
    TopologicalKnot(true),
    XRayBotanical(true),
    Chromatophore(true),
    BiomechanicalTissue(true),
    GildedFiligree(true),
    AncientRunes(true),
    SolarGranulation(true),
    LunarEjecta(true),
    OceanCurrents(true),
    InkWashMountains(true),
    NeonCity(true),
    PhyllotaxisBloom(true),
    SierpinskiTriangle(true),
    ApollonianGasket(true),
    HyperbolicTiling(true),
    MoebiusWeave(true),
    RorschachInkblot(true),
    SeismicInterference(true),
    RayleighBenard(true),
    OrigamiFacets(true),
    FiberOpticBundle(true),
    OrganicFibers,
    GmicReactionDiffusion,
    Truchet,
    Caustics,
    Cellular,
    Check,
    FBM,
    Marble,
    Plasma,
    Quilt,
    Wood,
}
