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
    TextureFilterType.OrganicFibers -> R.string.texture_organic_fibers
    TextureFilterType.GmicReactionDiffusion -> R.string.texture_reaction_diffusion
    TextureFilterType.Truchet -> R.string.texture_truchet
}

internal fun CellularGridType.titleRes(): Int = when (this) {
    CellularGridType.Random -> R.string.grid_random
    CellularGridType.Square -> R.string.grid_square
    CellularGridType.Hexagonal -> R.string.grid_hexagonal
    CellularGridType.Octagonal -> R.string.grid_octagonal
    CellularGridType.Triangular -> R.string.grid_triangular
}
