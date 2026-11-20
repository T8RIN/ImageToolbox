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

package com.t8rin.imagetoolbox.texture_generation.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.utils.roundTo
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.provider.ProvideContainerDefaults
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ColorRowSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.DataSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.texture_generation.domain.model.CellularGridType
import com.t8rin.imagetoolbox.texture_generation.domain.model.TextureFilterType
import kotlin.math.roundToInt

@Composable
internal fun ParamColumn(
    content: @Composable () -> Unit
) {
    ProvideContainerDefaults(
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            content()
        }
    }
}

@Composable
internal fun FloatParam(
    value: Float,
    title: String,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    roundTo: Int = 2,
    shape: Shape = ShapeDefaults.center
) {
    EnhancedSliderItem(
        value = value,
        title = title,
        valueRange = range,
        internalStateTransformation = {
            it.roundTo(roundTo)
        },
        onValueChange = onValueChange,
        shape = shape
    )
}

@Composable
internal fun IntParam(
    value: Int,
    title: String,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Int) -> Unit,
    shape: Shape = ShapeDefaults.center
) {
    EnhancedSliderItem(
        value = value,
        title = title,
        valueRange = range,
        internalStateTransformation = {
            it.roundToInt()
        },
        onValueChange = {
            onValueChange(it.roundToInt())
        },
        shape = shape
    )
}

@Composable
internal fun ColorParam(
    title: String,
    value: Color,
    onValueChange: (Color) -> Unit,
    shape: Shape = ShapeDefaults.center
) {
    ColorRowSelector(
        value = value,
        onValueChange = onValueChange,
        title = title,
        modifier = Modifier.container(
            shape = shape
        )
    )
}

@Composable
internal fun <T : Enum<T>> EnumParam(
    value: T,
    entries: List<T>,
    title: String,
    onValueChange: (T) -> Unit,
    shape: Shape = ShapeDefaults.center
) {
    DataSelector(
        value = value,
        onValueChange = onValueChange,
        entries = entries,
        title = title,
        titleIcon = null,
        itemContentText = { it.name.toReadableName() },
        spanCount = 1,
        containerColor = Color.Unspecified,
        shape = shape
    )
}

private fun String.toReadableName(): String = replace(Regex("([a-z])([A-Z])"), "$1 $2")

internal fun TextureFilterType.titleRes(): Int = when (this) {
    TextureFilterType.RandomTriangles -> R.string.texture_pattern_random_triangles
    TextureFilterType.HexCubePattern -> R.string.texture_pattern_hex_cube_pattern
    TextureFilterType.SquareLooper -> R.string.texture_pattern_square_looper
    TextureFilterType.MoireInterference -> R.string.texture_pattern_moire_interference
    TextureFilterType.TruchetArcs -> R.string.texture_pattern_truchet_arcs
    TextureFilterType.CircuitPattern -> R.string.texture_pattern_circuit_pattern
    TextureFilterType.RosacePattern -> R.string.texture_pattern_rosace_pattern
    TextureFilterType.RosettePattern -> R.string.texture_pattern_rosette_pattern
    TextureFilterType.LightArray -> R.string.texture_pattern_light_array
    TextureFilterType.VoronoiGems -> R.string.texture_pattern_voronoi_gems
    TextureFilterType.CitySkyline -> R.string.texture_pattern_city_skyline
    TextureFilterType.WinterLandscape -> R.string.texture_pattern_winter_landscape
    TextureFilterType.CircleRipplePattern -> R.string.texture_pattern_circle_ripple_pattern
    TextureFilterType.SquareRipplePattern -> R.string.texture_pattern_square_ripple_pattern
    TextureFilterType.HatchPattern -> R.string.texture_pattern_hatch_pattern
    TextureFilterType.SmoothHatch -> R.string.texture_pattern_smooth_hatch
    TextureFilterType.XorPattern -> R.string.texture_pattern_xor_pattern
    TextureFilterType.SquareSpiralPattern -> R.string.texture_pattern_square_spiral_pattern
    TextureFilterType.CrossStitchPattern -> R.string.texture_pattern_cross_stitch_pattern
    TextureFilterType.PlaidPattern -> R.string.texture_pattern_plaid_pattern
    TextureFilterType.ChromaticPlaidPattern -> R.string.texture_chromatic_plaid
    TextureFilterType.GeneratedWaves -> R.string.texture_pattern_generated_waves
    TextureFilterType.VoronoiHatch -> R.string.texture_pattern_voronoi_hatch
    TextureFilterType.LoopPattern -> R.string.texture_pattern_loop_pattern
    TextureFilterType.ScintillatingGrid -> R.string.texture_pattern_scintillating_grid
    TextureFilterType.CircleSpiralPattern -> R.string.texture_pattern_circle_spiral_pattern
    TextureFilterType.TestChart -> R.string.texture_pattern_test_chart
    TextureFilterType.BentRows -> R.string.texture_pattern_bent_rows
    TextureFilterType.DiamondsIllusion -> R.string.texture_pattern_diamonds_illusion
    TextureFilterType.Sphere3D -> R.string.texture_sphere_3d
    TextureFilterType.Cube3D -> R.string.texture_cube_3d
    TextureFilterType.CubeStair3D -> R.string.texture_cube_stair_3d
    TextureFilterType.CubeSpheres3D -> R.string.texture_cube_spheres_3d
    TextureFilterType.EmptiedCube3D -> R.string.texture_emptied_cube_3d
    TextureFilterType.Torus3D -> R.string.texture_torus_3d
    TextureFilterType.MobiusTorus3D -> R.string.texture_mobius_torus_3d
    TextureFilterType.GyroRings3D -> R.string.texture_gyro_rings_3d
    TextureFilterType.TorusGem3D -> R.string.texture_torus_gem_3d
    TextureFilterType.CubeOctahedron3D -> R.string.texture_cube_octahedron_3d
    TextureFilterType.InfiniteSpheroids3D -> R.string.texture_infinite_spheroids_3d
    TextureFilterType.InfiniteStructure3D -> R.string.texture_infinite_structure_3d
    TextureFilterType.InfiniteCylinders3D -> R.string.texture_infinite_cylinders_3d
    TextureFilterType.TruchetRings3D -> R.string.texture_truchet_rings_3d
    TextureFilterType.TruchetPipes3D -> R.string.texture_truchet_pipes_3d
    TextureFilterType.BrushedMetal -> R.string.texture_brushed_metal
    TextureFilterType.Caustics -> R.string.texture_caustics
    TextureFilterType.Cellular -> R.string.texture_cellular
    TextureFilterType.Check -> R.string.texture_checkerboard
    TextureFilterType.FBM -> R.string.texture_fbm
    TextureFilterType.Marble -> R.string.texture_marble
    TextureFilterType.Plasma -> R.string.texture_plasma
    TextureFilterType.Quilt -> R.string.texture_quilt
    TextureFilterType.Wood -> R.string.texture_wood
    TextureFilterType.Brick -> R.string.texture_brick
    TextureFilterType.Camouflage -> R.string.texture_camouflage
    TextureFilterType.Cell -> R.string.texture_cell
    TextureFilterType.Cloud -> R.string.texture_cloud
    TextureFilterType.Crack -> R.string.texture_crack
    TextureFilterType.Fabric -> R.string.texture_fabric
    TextureFilterType.Foliage -> R.string.texture_foliage
    TextureFilterType.Honeycomb -> R.string.texture_honeycomb
    TextureFilterType.Ice -> R.string.texture_ice
    TextureFilterType.Lava -> R.string.texture_lava
    TextureFilterType.Nebula -> R.string.texture_nebula
    TextureFilterType.Paper -> R.string.texture_paper
    TextureFilterType.Rust -> R.string.texture_rust
    TextureFilterType.Sand -> R.string.texture_sand
    TextureFilterType.Smoke -> R.string.texture_smoke
    TextureFilterType.Stone -> R.string.texture_stone
    TextureFilterType.Terrain -> R.string.texture_terrain
    TextureFilterType.Topography -> R.string.texture_topography
    TextureFilterType.WaterRipple -> R.string.texture_water_ripple
    TextureFilterType.AdvancedWood -> R.string.texture_advanced_wood
    TextureFilterType.Grass -> R.string.texture_grass
    TextureFilterType.Dirt -> R.string.texture_dirt
    TextureFilterType.Leather -> R.string.texture_leather
    TextureFilterType.Concrete -> R.string.texture_concrete
    TextureFilterType.Asphalt -> R.string.texture_asphalt
    TextureFilterType.Moss -> R.string.texture_moss
    TextureFilterType.Fire -> R.string.texture_fire
    TextureFilterType.Aurora -> R.string.texture_aurora
    TextureFilterType.OilSlick -> R.string.texture_oil_slick
    TextureFilterType.Watercolor -> R.string.texture_watercolor
    TextureFilterType.AbstractFlow -> R.string.texture_abstract_flow
    TextureFilterType.Opal -> R.string.texture_opal
    TextureFilterType.DamascusSteel -> R.string.texture_damascus_steel
    TextureFilterType.Lightning -> R.string.texture_lightning
    TextureFilterType.Velvet -> R.string.texture_velvet
    TextureFilterType.InkMarbling -> R.string.texture_ink_marbling
    TextureFilterType.HolographicFoil -> R.string.texture_holographic_foil
    TextureFilterType.Bioluminescence -> R.string.texture_bioluminescence
    TextureFilterType.CosmicVortex -> R.string.texture_cosmic_vortex
    TextureFilterType.LavaLamp -> R.string.texture_lava_lamp
    TextureFilterType.EventHorizon -> R.string.texture_event_horizon
    TextureFilterType.FractalBloom -> R.string.texture_fractal_bloom
    TextureFilterType.ChromaticTunnel -> R.string.texture_chromatic_tunnel
    TextureFilterType.EclipseCorona -> R.string.texture_eclipse_corona
    TextureFilterType.StrangeAttractor -> R.string.texture_strange_attractor
    TextureFilterType.FerrofluidCrown -> R.string.texture_ferrofluid_crown
    TextureFilterType.Supernova -> R.string.texture_supernova
    TextureFilterType.Iris -> R.string.texture_iris
    TextureFilterType.PeacockFeather -> R.string.texture_peacock_feather
    TextureFilterType.NautilusShell -> R.string.texture_nautilus_shell
    TextureFilterType.RingedPlanet -> R.string.texture_ringed_planet
    TextureFilterType.Geode -> R.string.texture_geode
    TextureFilterType.PrismaticLight -> R.string.texture_prismatic_light
    TextureFilterType.StainedGlass -> R.string.texture_stained_glass
    TextureFilterType.KelpForest -> R.string.texture_kelp_forest
    TextureFilterType.FrostFern -> R.string.texture_frost_fern
    TextureFilterType.LiquidCrystal -> R.string.texture_liquid_crystal
    TextureFilterType.DragonScales -> R.string.texture_dragon_scales
    TextureFilterType.FireflySwarm -> R.string.texture_firefly_swarm
    TextureFilterType.Mycelium -> R.string.texture_mycelium
    TextureFilterType.Kintsugi -> R.string.texture_kintsugi
    TextureFilterType.CarbonFiber -> R.string.texture_carbon_fiber
    TextureFilterType.CircuitBoard -> R.string.texture_circuit_board
    TextureFilterType.SoapFilm -> R.string.texture_soap_film
    TextureFilterType.MoireGuilloche -> R.string.texture_moire_guilloche
    TextureFilterType.SnakeSkin -> R.string.texture_snake_skin
    TextureFilterType.Terrazzo -> R.string.texture_terrazzo
    TextureFilterType.GalaxyFilaments -> R.string.texture_galaxy_filaments
    TextureFilterType.VolcanicObsidian -> R.string.texture_volcanic_obsidian
    TextureFilterType.MotherboardHeatmap -> R.string.texture_motherboard_heatmap
    TextureFilterType.MicroscopicDiatoms -> R.string.texture_microscopic_diatoms
    TextureFilterType.ReactionDiffusion -> R.string.texture_reaction_diffusion
    TextureFilterType.CoralGrowth -> R.string.texture_coral_growth
    TextureFilterType.SlimeMold -> R.string.texture_slime_mold
    TextureFilterType.DendriticCrystal -> R.string.texture_dendritic_crystal
    TextureFilterType.ElectricArcField -> R.string.texture_electric_arc_field
    TextureFilterType.CloudChamber -> R.string.texture_cloud_chamber
    TextureFilterType.TurbulentInk -> R.string.texture_turbulent_ink
    TextureFilterType.CellularEmbryo -> R.string.texture_cellular_embryo
    TextureFilterType.NeuralGarden -> R.string.texture_neural_garden
    TextureFilterType.MagneticField -> R.string.texture_magnetic_field
    TextureFilterType.RiverDelta -> R.string.texture_river_delta
    TextureFilterType.LichenColony -> R.string.texture_lichen_colony
    TextureFilterType.BacterialCulture -> R.string.texture_bacterial_culture
    TextureFilterType.FluidVorticity -> R.string.texture_fluid_vorticity
    TextureFilterType.CrystalGrowth -> R.string.texture_crystal_growth
    TextureFilterType.GalacticWeb -> R.string.texture_galactic_web
    TextureFilterType.VeinedLeaf -> R.string.texture_veined_leaf
    TextureFilterType.PorousSponge -> R.string.texture_porous_sponge
    TextureFilterType.RainOnGlass -> R.string.texture_rain_on_glass
    TextureFilterType.EmberField -> R.string.texture_ember_field
    TextureFilterType.QuantumFoam -> R.string.texture_quantum_foam
    TextureFilterType.ChladniPlate -> R.string.texture_chladni_plate
    TextureFilterType.CymaticRosette -> R.string.texture_cymatic_rosette
    TextureFilterType.LichtenbergFigure -> R.string.texture_lichtenberg_figure
    TextureFilterType.Quasicrystal -> R.string.texture_quasicrystal
    TextureFilterType.Mandelbrot -> R.string.texture_mandelbrot
    TextureFilterType.BurningShip -> R.string.texture_burning_ship
    TextureFilterType.JuliaSet -> R.string.texture_julia_set
    TextureFilterType.KaleidoscopeCrystal -> R.string.texture_kaleidoscope_crystal
    TextureFilterType.SpectralPrism -> R.string.texture_spectral_prism
    TextureFilterType.TopologicalKnot -> R.string.texture_topological_knot
    TextureFilterType.XRayBotanical -> R.string.texture_x_ray_botanical
    TextureFilterType.Chromatophore -> R.string.texture_chromatophore
    TextureFilterType.BiomechanicalTissue -> R.string.texture_biomechanical_tissue
    TextureFilterType.GildedFiligree -> R.string.texture_gilded_filigree
    TextureFilterType.AncientRunes -> R.string.texture_ancient_runes
    TextureFilterType.SolarGranulation -> R.string.texture_solar_granulation
    TextureFilterType.LunarEjecta -> R.string.texture_lunar_ejecta
    TextureFilterType.OceanCurrents -> R.string.texture_ocean_currents
    TextureFilterType.InkWashMountains -> R.string.texture_ink_wash_mountains
    TextureFilterType.NeonCity -> R.string.texture_neon_city
    TextureFilterType.PhyllotaxisBloom -> R.string.texture_phyllotaxis_bloom
    TextureFilterType.SierpinskiTriangle -> R.string.texture_sierpinski_triangle
    TextureFilterType.ApollonianGasket -> R.string.texture_apollonian_gasket
    TextureFilterType.HyperbolicTiling -> R.string.texture_hyperbolic_tiling
    TextureFilterType.MoebiusWeave -> R.string.texture_moebius_weave
    TextureFilterType.RorschachInkblot -> R.string.texture_rorschach_inkblot
    TextureFilterType.SeismicInterference -> R.string.texture_seismic_interference
    TextureFilterType.RayleighBenard -> R.string.texture_rayleigh_benard
    TextureFilterType.OrigamiFacets -> R.string.texture_origami_facets
    TextureFilterType.FiberOpticBundle -> R.string.texture_fiber_optic_bundle
    TextureFilterType.KnittedFabric -> R.string.texture_knitted_fabric
    TextureFilterType.WickerWeave -> R.string.texture_wicker_weave
    TextureFilterType.Chainmail -> R.string.texture_chainmail
    TextureFilterType.HerringboneParquet -> R.string.texture_herringbone_parquet
    TextureFilterType.RoofTiles -> R.string.texture_roof_tiles
    TextureFilterType.HammeredMetal -> R.string.texture_hammered_metal
    TextureFilterType.CorrugatedMetal -> R.string.texture_corrugated_metal
    TextureFilterType.BubbleWrap -> R.string.texture_bubble_wrap
    TextureFilterType.PenroseTiling -> R.string.texture_penrose_tiling
    TextureFilterType.CairoTiling -> R.string.texture_cairo_tiling
    TextureFilterType.IslamicStarTiling -> R.string.texture_islamic_star_tiling
    TextureFilterType.CelticKnotwork -> R.string.texture_celtic_knotwork
    TextureFilterType.Seigaiha -> R.string.texture_seigaiha
    TextureFilterType.Asanoha -> R.string.texture_asanoha
    TextureFilterType.Houndstooth -> R.string.texture_houndstooth
    TextureFilterType.Jigsaw -> R.string.texture_jigsaw
    TextureFilterType.TreeBark -> R.string.texture_tree_bark
    TextureFilterType.PineCone -> R.string.texture_pine_cone
    TextureFilterType.ZebraStripes -> R.string.texture_zebra_stripes
    TextureFilterType.LeopardSpots -> R.string.texture_leopard_spots
    TextureFilterType.LissajousRibbons -> R.string.texture_lissajous_ribbons
    TextureFilterType.BismuthCrystal -> R.string.texture_bismuth_crystal
    TextureFilterType.HilbertRibbons -> R.string.texture_hilbert_ribbons
    TextureFilterType.Malachite -> R.string.texture_malachite
    TextureFilterType.Labradorite -> R.string.texture_labradorite
    TextureFilterType.Seraphinite -> R.string.texture_seraphinite
    TextureFilterType.PorcelainCrackle -> R.string.texture_porcelain_crackle
    TextureFilterType.RakuGlaze -> R.string.texture_raku_glaze
    TextureFilterType.MotherOfPearl -> R.string.texture_mother_of_pearl
    TextureFilterType.TemperedTitanium -> R.string.texture_tempered_titanium
    TextureFilterType.EndGrainMosaic -> R.string.texture_end_grain_mosaic
    TextureFilterType.Cork -> R.string.texture_cork
    TextureFilterType.CutCrystalGlass -> R.string.texture_cut_crystal_glass
    TextureFilterType.Sashiko -> R.string.texture_sashiko
    TextureFilterType.Bargello -> R.string.texture_bargello
    TextureFilterType.Kilim -> R.string.texture_kilim
    TextureFilterType.Brocade -> R.string.texture_brocade
    TextureFilterType.Batik -> R.string.texture_batik
    TextureFilterType.Ikat -> R.string.texture_ikat
    TextureFilterType.EmbossedPaisley -> R.string.texture_embossed_paisley
    TextureFilterType.ArtDecoFans -> R.string.texture_art_deco_fans
    TextureFilterType.CloverLattice -> R.string.texture_clover_lattice
    TextureFilterType.GuillocheRosettes -> R.string.texture_guilloche_rosettes
    TextureFilterType.GinkgoLeaves -> R.string.texture_ginkgo_leaves
    TextureFilterType.MonsteraCanopy -> R.string.texture_monstera_canopy
    TextureFilterType.LotusPond -> R.string.texture_lotus_pond
    TextureFilterType.SeaAnemone -> R.string.texture_sea_anemone
    TextureFilterType.Jellyfish -> R.string.texture_jellyfish
    TextureFilterType.ButterflyWings -> R.string.texture_butterfly_wings
    TextureFilterType.FungalGills -> R.string.texture_fungal_gills
    TextureFilterType.Radiolaria -> R.string.texture_radiolaria
    TextureFilterType.SlotCanyon -> R.string.texture_slot_canyon
    TextureFilterType.GlacialCrevasses -> R.string.texture_glacial_crevasses
    TextureFilterType.SpiralPearls -> R.string.texture_spiral_pearls
    TextureFilterType.HopfFibration -> R.string.texture_hopf_fibration
    TextureFilterType.Spirograph -> R.string.texture_spirograph
    TextureFilterType.ChiralPinwheels -> R.string.texture_chiral_pinwheels
    TextureFilterType.TruchetBlocks -> R.string.texture_truchet_blocks
    TextureFilterType.ImpossibleStairs -> R.string.texture_impossible_stairs
    TextureFilterType.WovenHexagons -> R.string.texture_woven_hexagons
    TextureFilterType.Gyroid -> R.string.texture_gyroid
    TextureFilterType.Hypercube -> R.string.texture_hypercube
    TextureFilterType.LenticularWaves -> R.string.texture_lenticular_waves
    TextureFilterType.StellarNursery -> R.string.texture_stellar_nursery
    TextureFilterType.PulsarJets -> R.string.texture_pulsar_jets
    TextureFilterType.GravitationalLensing -> R.string.texture_gravitational_lensing
    TextureFilterType.NacreClouds -> R.string.texture_nacre_clouds
    TextureFilterType.MercuryDroplets -> R.string.texture_mercury_droplets
    TextureFilterType.SonicBoom -> R.string.texture_sonic_boom
    TextureFilterType.RibbonVortex -> R.string.texture_ribbon_vortex
    TextureFilterType.IridescentFoam -> R.string.texture_iridescent_foam
    TextureFilterType.LavaCrust -> R.string.texture_lava_crust
    TextureFilterType.AbyssalVent -> R.string.texture_abyssal_vent
    TextureFilterType.EmberVeil -> R.string.texture_ember_veil
    TextureFilterType.PolarMist -> R.string.texture_polar_mist
    TextureFilterType.RibbedSatin -> R.string.texture_ribbed_satin
    TextureFilterType.DiffractionGrating -> R.string.texture_diffraction_grating
    TextureFilterType.SpiralBeacon -> R.string.texture_spiral_beacon
    TextureFilterType.MoltenWax -> R.string.texture_molten_wax
    TextureFilterType.PrismLights -> R.string.texture_prism_lights
    TextureFilterType.FossilCrossSection -> R.string.texture_fossil_cross_section
    TextureFilterType.AgateEye -> R.string.texture_agate_eye
    TextureFilterType.JadeCurrent -> R.string.texture_jade_current
    TextureFilterType.FrostSpindle -> R.string.texture_frost_spindle
    TextureFilterType.PrismaticFaults -> R.string.texture_prismatic_faults
    TextureFilterType.RootLace -> R.string.texture_root_lace
    TextureFilterType.OliveMosaic -> R.string.texture_olive_mosaic
    TextureFilterType.MarbledPatina -> R.string.texture_marbled_patina
    TextureFilterType.CoralSprigs -> R.string.texture_coral_sprigs
    TextureFilterType.CrystalSpokes -> R.string.texture_crystal_spokes
    TextureFilterType.PlasmaTendril -> R.string.texture_plasma_tendril
    TextureFilterType.PearlButtons -> R.string.texture_pearl_buttons
    TextureFilterType.LinkedNodes -> R.string.texture_linked_nodes
    TextureFilterType.LeafSkeleton -> R.string.texture_leaf_skeleton
    TextureFilterType.BeadedCurtain -> R.string.texture_beaded_curtain
    TextureFilterType.SpiralCobweb -> R.string.texture_spiral_cobweb
    TextureFilterType.GoldenTurbine -> R.string.texture_golden_turbine
    TextureFilterType.RunicTrellis -> R.string.texture_runic_trellis
    TextureFilterType.CraterRays -> R.string.texture_crater_rays
    TextureFilterType.PixelFountain -> R.string.texture_pixel_fountain
    TextureFilterType.CircleLace -> R.string.texture_circle_lace
    TextureFilterType.HexagonalLace -> R.string.texture_hexagonal_lace
    TextureFilterType.PerforatedRosette -> R.string.texture_perforated_rosette
    TextureFilterType.LunariaPods -> R.string.texture_lunaria_pods
    TextureFilterType.HorsetailStems -> R.string.texture_horsetail_stems
    TextureFilterType.DandelionClocks -> R.string.texture_dandelion_clocks
    TextureFilterType.CalicoKoi -> R.string.texture_calico_koi
    TextureFilterType.ValleyBells -> R.string.texture_valley_bells
    TextureFilterType.SilkPoppies -> R.string.texture_silk_poppies
    TextureFilterType.FlyAgaricGrove -> R.string.texture_fly_agaric_grove
    TextureFilterType.BeachPebbles -> R.string.texture_beach_pebbles
    TextureFilterType.TravertinePools -> R.string.texture_travertine_pools
    TextureFilterType.TidepoolStars -> R.string.texture_tidepool_stars
    TextureFilterType.WaveCutCliffs -> R.string.texture_wave_cut_cliffs
    TextureFilterType.SailingRegatta -> R.string.texture_sailing_regatta
    TextureFilterType.BarnacleColony -> R.string.texture_barnacle_colony
    TextureFilterType.FlyingPetrels -> R.string.texture_flying_petrels
    TextureFilterType.MoonPhases -> R.string.texture_moon_phases
    TextureFilterType.SiderealTrails -> R.string.texture_sidereal_trails
    TextureFilterType.OrionChart -> R.string.texture_orion_chart
    TextureFilterType.MeteorShower -> R.string.texture_meteor_shower
    TextureFilterType.ClockworkOrrery -> R.string.texture_clockwork_orrery
    TextureFilterType.CometPlume -> R.string.texture_comet_plume
    TextureFilterType.BinarySunset -> R.string.texture_binary_sunset
    TextureFilterType.PivotFields -> R.string.texture_pivot_fields
    TextureFilterType.AlluvialBends -> R.string.texture_alluvial_bends
    TextureFilterType.RakedSandGarden -> R.string.texture_raked_sand_garden
    TextureFilterType.BalloonFestival -> R.string.texture_balloon_festival
    TextureFilterType.HarborLight -> R.string.texture_harbor_light
    TextureFilterType.PaperLanterns -> R.string.texture_paper_lanterns
    TextureFilterType.ParasolMarket -> R.string.texture_parasol_market
    TextureFilterType.BackgammonPoints -> R.string.texture_backgammon_points
    TextureFilterType.DominoTable -> R.string.texture_domino_table
    TextureFilterType.PlayingCardSuits -> R.string.texture_playing_card_suits
    TextureFilterType.VinylCrate -> R.string.texture_vinyl_crate
    TextureFilterType.MusicBoxRoll -> R.string.texture_music_box_roll
    TextureFilterType.ZipperTape -> R.string.texture_zipper_tape
    TextureFilterType.BobbinLace -> R.string.texture_bobbin_lace
    TextureFilterType.MarquetryCompass -> R.string.texture_marquetry_compass
    TextureFilterType.WaffleIron -> R.string.texture_waffle_iron
    TextureFilterType.CitrusWheels -> R.string.texture_citrus_wheels
    TextureFilterType.SaltedPretzels -> R.string.texture_salted_pretzels
    TextureFilterType.LiquoriceAllsorts -> R.string.texture_liquorice_allsorts
    TextureFilterType.RavioliStamp -> R.string.texture_ravioli_stamp
    TextureFilterType.CameoBrocade -> R.string.texture_cameo_brocade
    TextureFilterType.BowTiePatchwork -> R.string.texture_bow_tie_patchwork
    TextureFilterType.TasselTrim -> R.string.texture_tassel_trim
    TextureFilterType.MillefioriCanes -> R.string.texture_millefiori_canes
    TextureFilterType.GreekMeander -> R.string.texture_greek_meander
    TextureFilterType.PaperChainGarland -> R.string.texture_paper_chain_garland
    TextureFilterType.RibbonCockade -> R.string.texture_ribbon_cockade
    TextureFilterType.VenetianShutters -> R.string.texture_venetian_shutters
    TextureFilterType.SandstoneArcade -> R.string.texture_sandstone_arcade
    TextureFilterType.GearBlueprint -> R.string.texture_gear_blueprint
    TextureFilterType.AdmissionTickets -> R.string.texture_admission_tickets
    TextureFilterType.SawtoothRoofscape -> R.string.texture_sawtooth_roofscape
    TextureFilterType.FanVault -> R.string.texture_fan_vault
    TextureFilterType.WoodcutSwells -> R.string.texture_woodcut_swells
    TextureFilterType.ApartmentPlan -> R.string.texture_apartment_plan
    TextureFilterType.RooftopTanks -> R.string.texture_rooftop_tanks
    TextureFilterType.BookclothSpines -> R.string.texture_bookcloth_spines
    TextureFilterType.HarbourBunting -> R.string.texture_harbour_bunting
    TextureFilterType.PantographLinkage -> R.string.texture_pantograph_linkage
    TextureFilterType.DualTrace -> R.string.texture_dual_trace
    TextureFilterType.StressFringes -> R.string.texture_stress_fringes
    TextureFilterType.WindBarbs -> R.string.texture_wind_barbs
    TextureFilterType.AlignmentNomogram -> R.string.texture_alignment_nomogram
    TextureFilterType.VacuumTriodes -> R.string.texture_vacuum_triodes
    TextureFilterType.BasePairs -> R.string.texture_base_pairs
    TextureFilterType.HarmonicSpectrogram -> R.string.texture_harmonic_spectrogram
    TextureFilterType.GelBands -> R.string.texture_gel_bands
    TextureFilterType.Stereonet -> R.string.texture_stereonet
    TextureFilterType.PressureDials -> R.string.texture_pressure_dials
    TextureFilterType.AirfoilStreamlines -> R.string.texture_airfoil_streamlines
    TextureFilterType.MolecularModel -> R.string.texture_molecular_model
    TextureFilterType.MarshHeron -> R.string.texture_marsh_heron
    TextureFilterType.SwitchbackPass -> R.string.texture_switchback_pass
    TextureFilterType.CicadaAmber -> R.string.texture_cicada_amber
    TextureFilterType.Passionflower -> R.string.texture_passionflower
    TextureFilterType.IcebergKeel -> R.string.texture_iceberg_keel
    TextureFilterType.SoundingFlukes -> R.string.texture_sounding_flukes
    TextureFilterType.SamaraFlight -> R.string.texture_samara_flight
    TextureFilterType.OctopusArm -> R.string.texture_octopus_arm
    TextureFilterType.WardianCase -> R.string.texture_wardian_case
    TextureFilterType.AntGalleries -> R.string.texture_ant_galleries
    TextureFilterType.RainShafts -> R.string.texture_rain_shafts
    TextureFilterType.PitcherPlants -> R.string.texture_pitcher_plants
    TextureFilterType.PoleFoldShibori -> R.string.texture_pole_fold_shibori
    TextureFilterType.PeacockMarbling -> R.string.texture_peacock_marbling
    TextureFilterType.LogCabinQuilt -> R.string.texture_log_cabin_quilt
    TextureFilterType.CoiledRagRug -> R.string.texture_coiled_rag_rug
    TextureFilterType.ButtonTufting -> R.string.texture_button_tufting
    TextureFilterType.TornPosterWall -> R.string.texture_torn_poster_wall
    TextureFilterType.LatteRosetta -> R.string.texture_latte_rosetta
    TextureFilterType.SalmonMarbling -> R.string.texture_salmon_marbling
    TextureFilterType.PomegranateCut -> R.string.texture_pomegranate_cut
    TextureFilterType.LetterpressOverprint -> R.string.texture_letterpress_overprint
    TextureFilterType.PaperQuilling -> R.string.texture_paper_quilling
    TextureFilterType.GoldLeafTransfer -> R.string.texture_gold_leaf_transfer
    TextureFilterType.CaneWebbing -> R.string.texture_cane_webbing
    TextureFilterType.PleatedHandFan -> R.string.texture_pleated_hand_fan
    TextureFilterType.TailorTape -> R.string.texture_tailor_tape
    TextureFilterType.PalmetteFrieze -> R.string.texture_palmette_frieze
    TextureFilterType.Switchyard -> R.string.texture_switchyard
    TextureFilterType.SuspensionSpan -> R.string.texture_suspension_span
    TextureFilterType.RainCrosswalk -> R.string.texture_rain_crosswalk
    TextureFilterType.CofferedRotunda -> R.string.texture_coffered_rotunda
    TextureFilterType.StationCanopy -> R.string.texture_station_canopy
    TextureFilterType.PendulumCase -> R.string.texture_pendulum_case
    TextureFilterType.ScrewBookpress -> R.string.texture_screw_bookpress
    TextureFilterType.TheatreWings -> R.string.texture_theatre_wings
    TextureFilterType.HornGramophone -> R.string.texture_horn_gramophone
    TextureFilterType.CanalLock -> R.string.texture_canal_lock
    TextureFilterType.FunicularIncline -> R.string.texture_funicular_incline
    TextureFilterType.CinemaMarquee -> R.string.texture_cinema_marquee
    TextureFilterType.SolarProminences -> R.string.texture_solar_prominences
    TextureFilterType.StellarBowShock -> R.string.texture_stellar_bow_shock
    TextureFilterType.HourglassNebula -> R.string.texture_hourglass_nebula
    TextureFilterType.RocheLobes -> R.string.texture_roche_lobes
    TextureFilterType.BasaltColonnade -> R.string.texture_basalt_colonnade
    TextureFilterType.FaultedStrata -> R.string.texture_faulted_strata
    TextureFilterType.GarnetSchist -> R.string.texture_garnet_schist
    TextureFilterType.OoliticLimestone -> R.string.texture_oolitic_limestone
    TextureFilterType.TrilobiteImpression -> R.string.texture_trilobite_impression
    TextureFilterType.DoubleSlit -> R.string.texture_double_slit
    TextureFilterType.LensCrossSection -> R.string.texture_lens_cross_section
    TextureFilterType.IrisDiaphragm -> R.string.texture_iris_diaphragm
    TextureFilterType.EmissionSpectrum -> R.string.texture_emission_spectrum
    TextureFilterType.SchlierenPlume -> R.string.texture_schlieren_plume
    TextureFilterType.HandRadiograph -> R.string.texture_hand_radiograph
    TextureFilterType.LogisticBifurcation -> R.string.texture_logistic_bifurcation
    TextureFilterType.SunstoneFeldspar -> R.string.texture_sunstone_feldspar
    TextureFilterType.RingPunchedSuede -> R.string.texture_ring_punched_suede
    TextureFilterType.SpindleMoulding -> R.string.texture_spindle_moulding
    TextureFilterType.LenoGauze -> R.string.texture_leno_gauze
    TextureFilterType.SoumakBraid -> R.string.texture_soumak_braid
    TextureFilterType.CouchingLoops -> R.string.texture_couching_loops
    TextureFilterType.CrazyQuiltSeams -> R.string.texture_crazy_quilt_seams
    TextureFilterType.PassementerieFrogs -> R.string.texture_passementerie_frogs
    TextureFilterType.RyaPile -> R.string.texture_rya_pile
    TextureFilterType.OttomanCord -> R.string.texture_ottoman_cord
    TextureFilterType.BattenbergLace -> R.string.texture_battenberg_lace
    TextureFilterType.TatamiInlay -> R.string.texture_tatami_inlay
    TextureFilterType.DarningSampler -> R.string.texture_darning_sampler
    TextureFilterType.ChenilleRows -> R.string.texture_chenille_rows
    TextureFilterType.SpanishBlackwork -> R.string.texture_spanish_blackwork
    TextureFilterType.ShingleFishscale -> R.string.texture_shingle_fishscale
    TextureFilterType.TwistedPaperCord -> R.string.texture_twisted_paper_cord
    TextureFilterType.DrypointBurr -> R.string.texture_drypoint_burr
    TextureFilterType.MezzotintRocker -> R.string.texture_mezzotint_rocker
    TextureFilterType.BenDayDots -> R.string.texture_ben_day_dots
    TextureFilterType.SplitFountainInk -> R.string.texture_split_fountain_ink
    TextureFilterType.StippleAquatint -> R.string.texture_stipple_aquatint
    TextureFilterType.ChineColle -> R.string.texture_chine_colle
    TextureFilterType.DeckledPulp -> R.string.texture_deckled_pulp
    TextureFilterType.LeatherBasketStamp -> R.string.texture_leather_basket_stamp
    TextureFilterType.AdzeHewnOak -> R.string.texture_adze_hewn_oak
    TextureFilterType.CloisonneCells -> R.string.texture_cloisonne_cells
    TextureFilterType.LaidPaperWatermark -> R.string.texture_laid_paper_watermark
    TextureFilterType.ShellacCheckering -> R.string.texture_shellac_checkering
    TextureFilterType.CombedSlip -> R.string.texture_combed_slip
    TextureFilterType.ParquetDeVersailles -> R.string.texture_parquet_de_versailles
    TextureFilterType.ScallopValves -> R.string.texture_scallop_valves
    TextureFilterType.UrchinTest -> R.string.texture_urchin_test
    TextureFilterType.NummuliteBank -> R.string.texture_nummulite_bank
    TextureFilterType.CrinoidColumnals -> R.string.texture_crinoid_columnals
    TextureFilterType.GraptoliteShale -> R.string.texture_graptolite_shale
    TextureFilterType.StromatoliteDomes -> R.string.texture_stromatolite_domes
    TextureFilterType.SeptarianNodules -> R.string.texture_septarian_nodules
    TextureFilterType.TourmalinePrisms -> R.string.texture_tourmaline_prisms
    TextureFilterType.SeleniteBlades -> R.string.texture_selenite_blades
    TextureFilterType.GraphicGranite -> R.string.texture_graphic_granite
    TextureFilterType.PillowBasalt -> R.string.texture_pillow_basalt
    TextureFilterType.ChevronFolds -> R.string.texture_chevron_folds
    TextureFilterType.OrbicularDiorite -> R.string.texture_orbicular_diorite
    TextureFilterType.DesertRoses -> R.string.texture_desert_roses
    TextureFilterType.RippleCrossbeds -> R.string.texture_ripple_crossbeds
    TextureFilterType.MangroveKnees -> R.string.texture_mangrove_knees
    TextureFilterType.CottonBolls -> R.string.texture_cotton_bolls
    TextureFilterType.BirchLenticels -> R.string.texture_birch_lenticels
    TextureFilterType.PlaneBark -> R.string.texture_plane_bark
    TextureFilterType.CattailFloss -> R.string.texture_cattail_floss
    TextureFilterType.FernSori -> R.string.texture_fern_sori
    TextureFilterType.EquisetumCrosscuts -> R.string.texture_equisetum_crosscuts
    TextureFilterType.LotusSeedheads -> R.string.texture_lotus_seedheads
    TextureFilterType.DuckweedRafts -> R.string.texture_duckweed_rafts
    TextureFilterType.MapleBurl -> R.string.texture_maple_burl
    TextureFilterType.MusselByssus -> R.string.texture_mussel_byssus
    TextureFilterType.SandDollarBed -> R.string.texture_sand_dollar_bed
    TextureFilterType.ScuteGrowthRings -> R.string.texture_scute_growth_rings
    TextureFilterType.CuttlefishMantle -> R.string.texture_cuttlefish_mantle
    TextureFilterType.LimpetRadula -> R.string.texture_limpet_radula
    TextureFilterType.CornerCubeReflector -> R.string.texture_corner_cube_reflector
    TextureFilterType.MicroficheFrames -> R.string.texture_microfiche_frames
    TextureFilterType.AcousticSlotPanel -> R.string.texture_acoustic_slot_panel
    TextureFilterType.BearingRaceway -> R.string.texture_bearing_raceway
    TextureFilterType.PinFinHeatsink -> R.string.texture_pin_fin_heatsink
    TextureFilterType.TreadPlate -> R.string.texture_tread_plate
    TextureFilterType.SplitFlapMatrix -> R.string.texture_split_flap_matrix
    TextureFilterType.WireRopeSection -> R.string.texture_wire_rope_section
    TextureFilterType.KnurledInsert -> R.string.texture_knurled_insert
    TextureFilterType.BallGridArray -> R.string.texture_ball_grid_array
    TextureFilterType.PiezoComb -> R.string.texture_piezo_comb
    TextureFilterType.PolarizerMosaic -> R.string.texture_polarizer_mosaic
    TextureFilterType.PhosphorTriads -> R.string.texture_phosphor_triads
    TextureFilterType.AnamorphicStreaks -> R.string.texture_anamorphic_streaks
    TextureFilterType.DichroicSplitter -> R.string.texture_dichroic_splitter
    TextureFilterType.TalbotCarpet -> R.string.texture_talbot_carpet
    TextureFilterType.ConoscopicCrosses -> R.string.texture_conoscopic_crosses
    TextureFilterType.SpeckleShear -> R.string.texture_speckle_shear
    TextureFilterType.PythagoreanTiling -> R.string.texture_pythagorean_tiling
    TextureFilterType.MiuraFolds -> R.string.texture_miura_folds
    TextureFilterType.DovetailMesh -> R.string.texture_dovetail_mesh
    TextureFilterType.ReuleauxTripods -> R.string.texture_reuleaux_tripods
    TextureFilterType.HingedSquares -> R.string.texture_hinged_squares
    TextureFilterType.OctagonCabochons -> R.string.texture_octagon_cabochons
    TextureFilterType.PentominoMosaic -> R.string.texture_pentomino_mosaic
    TextureFilterType.VacuumChuck -> R.string.texture_vacuum_chuck
    TextureFilterType.TorsionSprings -> R.string.texture_torsion_springs
    TextureFilterType.CuneiformImpressions -> R.string.texture_cuneiform_impressions
    TextureFilterType.AnechoicWedges -> R.string.texture_anechoic_wedges
    TextureFilterType.Maze -> R.string.texture_maze
    TextureFilterType.OrganicFibers -> R.string.texture_organic_fibers
    TextureFilterType.GmicReactionDiffusion -> R.string.texture_gmic_reaction_diffusion
    TextureFilterType.Truchet -> R.string.texture_truchet
}

internal fun CellularGridType.titleRes(): Int = when (this) {
    CellularGridType.Random -> R.string.grid_random
    CellularGridType.Square -> R.string.grid_square
    CellularGridType.Hexagonal -> R.string.grid_hexagonal
    CellularGridType.Octagonal -> R.string.grid_octagonal
    CellularGridType.Triangular -> R.string.grid_triangular
}
