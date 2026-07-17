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

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.helper.toColor
import com.t8rin.imagetoolbox.core.ui.utils.helper.toModel
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.texture_generation.domain.model.TextureFilterType
import com.t8rin.imagetoolbox.texture_generation.domain.model.TextureParams

@Composable
internal fun FastNoiseParams(
    value: TextureParams,
    onValueChange: (TextureParams) -> Unit
) {
    val params = value.fastNoiseParams ?: return
    val config = value.textureFilterType.fastNoiseConfig()

    ParamColumn {
        val showScale = value.textureFilterType != TextureFilterType.RingedPlanet

        if (showScale) {
            FloatParam(
                value = params.scale,
                title = stringResource(R.string.scale),
                range = 0.001f..0.05f,
                roundTo = 4,
                onValueChange = { scale ->
                    onValueChange(value.copy(fastNoiseParams = params.copy(scale = scale)))
                },
                shape = ShapeDefaults.top
            )
        }
        config.values.forEachIndexed { index, info ->
            FloatParam(
                value = params.values[index],
                title = stringResource(info.title),
                range = info.range,
                roundTo = info.roundTo,
                onValueChange = { newValue ->
                    onValueChange(
                        value.copy(
                            fastNoiseParams = params.copy(
                                values = params.values.updated(index, newValue)
                            )
                        )
                    )
                },
                shape = if (!showScale && index == 0) ShapeDefaults.top else ShapeDefaults.center
            )
        }
        config.colors.forEachIndexed { index, title ->
            ColorParam(
                title = stringResource(title),
                value = params.colors[index].toColor(),
                onValueChange = { color ->
                    onValueChange(
                        value.copy(
                            fastNoiseParams = params.copy(
                                colors = params.colors.updated(index, color.toModel())
                            )
                        )
                    )
                },
                shape = ShapeDefaults.center
            )
        }
        IntParam(
            value = params.seed,
            title = stringResource(R.string.seed),
            range = -10000f..10000f,
            onValueChange = { seed ->
                onValueChange(value.copy(fastNoiseParams = params.copy(seed = seed)))
            },
            shape = ShapeDefaults.bottom
        )
    }
}

private data class FastNoiseConfig(
    val values: List<ParamInfo>,
    val colors: List<Int>
)

private data class ParamInfo(
    @StringRes val title: Int,
    val range: ClosedFloatingPointRange<Float>,
    val roundTo: Int = 2
)

private fun TextureFilterType.fastNoiseConfig(): FastNoiseConfig = when (this) {
    TextureFilterType.Brick -> config(
        param(R.string.aspect_ratio, 0.5f..4f),
        unit(R.string.texture_mortar_width),
        unit(R.string.texture_irregularity),
        unit(R.string.texture_roughness),
        unit(R.string.texture_bevel),
        colors = intArrayOf(
            R.string.texture_mortar_color,
            R.string.texture_dark_brick_color,
            R.string.texture_brick_color,
            R.string.texture_highlight_color
        )
    )

    TextureFilterType.Camouflage -> config(
        unit(R.string.texture_first_threshold),
        unit(R.string.texture_second_threshold),
        unit(R.string.texture_third_threshold),
        distortion(),
        unit(R.string.texture_edge_softness, 3),
        colors = intArrayOf(
            R.string.texture_dark_color,
            R.string.texture_forest_color,
            R.string.texture_earth_color,
            R.string.texture_sand_color
        )
    )

    TextureFilterType.Cell -> config(
        unit(R.string.jitter),
        unit(R.string.texture_border_width),
        unit(R.string.glow),
        distortion(),
        unit(R.string.variation),
        colors = intArrayOf(
            R.string.texture_background_color,
            R.string.texture_cell_color,
            R.string.texture_edge_color,
            R.string.texture_highlight_color
        )
    )

    TextureFilterType.Cloud -> config(
        unit(R.string.texture_coverage),
        unit(R.string.softness),
        unit(R.string.texture_detail),
        distortion(),
        unit(R.string.density),
        colors = intArrayOf(
            R.string.texture_sky_color,
            R.string.texture_shadow_color,
            R.string.texture_light_color
        )
    )

    TextureFilterType.Crack -> config(
        unit(R.string.width, 3),
        unit(R.string.density),
        distortion(),
        unit(R.string.texture_depth),
        unit(R.string.texture_branching),
        colors = intArrayOf(
            R.string.texture_surface_color,
            R.string.texture_variation_color,
            R.string.texture_crack_color,
            R.string.texture_edge_color
        )
    )

    TextureFilterType.Fabric -> config(
        param(R.string.texture_horizontal_threads, 1f..64f),
        param(R.string.texture_vertical_threads, 1f..64f),
        unit(R.string.texture_irregularity),
        unit(R.string.texture_depth),
        unit(R.string.texture_fuzz),
        colors = intArrayOf(
            R.string.texture_warp_color,
            R.string.texture_weft_color,
            R.string.texture_shadow_color,
            R.string.texture_highlight_color
        )
    )

    TextureFilterType.Foliage -> config(
        unit(R.string.density),
        unit(R.string.texture_edge_softness),
        unit(R.string.texture_veins),
        unit(R.string.texture_lighting),
        unit(R.string.variation),
        colors = intArrayOf(
            R.string.texture_shadow_color,
            R.string.texture_dark_leaf_color,
            R.string.texture_leaf_color,
            R.string.texture_highlight_color
        )
    )

    TextureFilterType.Honeycomb -> config(
        unit(R.string.texture_border_width, 3),
        unit(R.string.texture_bevel),
        unit(R.string.texture_irregularity),
        unit(R.string.fill),
        unit(R.string.glow),
        colors = intArrayOf(
            R.string.texture_background_color,
            R.string.texture_border_color,
            R.string.texture_honey_color,
            R.string.texture_highlight_color
        )
    )

    TextureFilterType.Ice -> config(
        unit(R.string.texture_crack_width, 3),
        unit(R.string.texture_frost),
        unit(R.string.texture_depth),
        distortion(),
        unit(R.string.sparkle),
        colors = intArrayOf(
            R.string.texture_deep_color,
            R.string.texture_ice_color,
            R.string.texture_frost_color,
            R.string.texture_crack_color
        )
    )

    TextureFilterType.Lava -> config(
        distortion(),
        param(R.string.texture_flow, 0f..4f),
        unit(R.string.texture_detail),
        unit(R.string.texture_crust),
        unit(R.string.glow),
        colors = intArrayOf(
            R.string.texture_crust_color,
            R.string.texture_lava_color,
            R.string.texture_glow_color
        )
    )

    TextureFilterType.Nebula -> config(
        param(R.string.turbulence, 0f..64f),
        unit(R.string.texture_cloud_density),
        unit(R.string.texture_stars),
        unit(R.string.glow),
        contrast(),
        colors = intArrayOf(
            R.string.texture_space_color,
            R.string.texture_violet_color,
            R.string.texture_blue_color,
            R.string.texture_glow_color
        )
    )

    TextureFilterType.Paper -> config(
        param(R.string.texture_fiber_density, 1f..128f),
        unit(R.string.texture_fiber_strength),
        unit(R.string.grain),
        unit(R.string.texture_stains),
        unit(R.string.texture_roughness),
        colors = intArrayOf(
            R.string.texture_base_color,
            R.string.texture_light_color,
            R.string.texture_fiber_color,
            R.string.texture_stain_color
        )
    )

    TextureFilterType.Rust -> config(
        unit(R.string.texture_corrosion),
        unit(R.string.texture_pitting),
        unit(R.string.texture_flakes),
        distortion(),
        contrast(),
        colors = intArrayOf(
            R.string.texture_metal_color,
            R.string.texture_dark_rust_color,
            R.string.texture_rust_color,
            R.string.texture_orange_color
        )
    )

    TextureFilterType.Sand -> config(
        param(R.string.texture_dune_frequency, 1f..32f),
        param(R.string.texture_wind_angle, 0f..6.28f),
        unit(R.string.texture_ripples),
        unit(R.string.grain),
        contrast(),
        colors = intArrayOf(
            R.string.texture_shadow_color,
            R.string.texture_sand_color,
            R.string.texture_light_color
        )
    )

    TextureFilterType.Smoke -> config(
        param(R.string.turbulence, 0f..64f),
        unit(R.string.density),
        param(R.string.texture_wisps, 0f..5f),
        contrast(),
        unit(R.string.texture_detail),
        colors = intArrayOf(
            R.string.texture_background_color,
            R.string.texture_shadow_color,
            R.string.texture_smoke_color
        )
    )

    TextureFilterType.Stone -> config(
        unit(R.string.grain),
        unit(R.string.texture_veins),
        param(R.string.texture_vein_scale, 0.001f..0.1f, 3),
        distortion(),
        contrast(),
        colors = intArrayOf(
            R.string.texture_dark_color,
            R.string.texture_light_color,
            R.string.texture_vein_color
        )
    )

    TextureFilterType.Terrain -> config(
        unit(R.string.texture_water_level),
        unit(R.string.texture_mountain_level),
        unit(R.string.texture_erosion),
        unit(R.string.texture_detail),
        unit(R.string.texture_snow_level),
        colors = intArrayOf(
            R.string.texture_water_color,
            R.string.texture_lowland_color,
            R.string.texture_rock_color,
            R.string.texture_snow_color
        )
    )

    TextureFilterType.Topography -> config(
        param(R.string.texture_line_count, 1f..32f),
        unit(R.string.texture_line_thickness),
        unit(R.string.texture_shading),
        distortion(),
        contrast(),
        colors = intArrayOf(
            R.string.texture_low_color,
            R.string.texture_high_color,
            R.string.texture_line_color
        )
    )

    TextureFilterType.WaterRipple -> config(
        param(R.string.frequency, 1f..48f),
        distortion(),
        unit(R.string.texture_caustics),
        unit(R.string.texture_depth),
        unit(R.string.highlights),
        colors = intArrayOf(
            R.string.texture_deep_color,
            R.string.texture_shallow_color,
            R.string.texture_highlight_color
        )
    )

    TextureFilterType.AdvancedWood -> config(
        param(R.string.rings, 1f..32f),
        unit(R.string.grain),
        distortion(),
        param(R.string.stretch, 1f..16f),
        unit(R.string.contrast),
        colors = intArrayOf(
            R.string.texture_dark_color,
            R.string.texture_light_color,
            R.string.texture_pore_color
        )
    )

    TextureFilterType.Grass -> config(
        param(R.string.texture_blade_density, 0f..2f),
        param(R.string.texture_blade_length, 1f..100f),
        param(R.string.texture_wind, 0f..2f),
        unit(R.string.texture_patchiness),
        unit(R.string.highlights),
        colors = intArrayOf(
            R.string.texture_dirt_color,
            R.string.texture_dark_grass_color,
            R.string.texture_grass_color,
            R.string.texture_tip_color
        )
    )

    TextureFilterType.Dirt -> config(
        unit(R.string.texture_clumps),
        unit(R.string.texture_moisture),
        unit(R.string.texture_pebbles),
        unit(R.string.texture_roughness),
        unit(R.string.variation),
        colors = intArrayOf(
            R.string.texture_dark_earth_color,
            R.string.texture_earth_color,
            R.string.texture_dry_color,
            R.string.texture_pebble_color
        )
    )

    TextureFilterType.Leather -> config(
        unit(R.string.texture_wrinkles),
        unit(R.string.texture_pores),
        unit(R.string.grain),
        unit(R.string.softness),
        unit(R.string.shine, 3),
        colors = intArrayOf(
            R.string.texture_shadow_color,
            R.string.texture_leather_color,
            R.string.texture_light_color,
            R.string.texture_pore_color
        )
    )

    TextureFilterType.Concrete -> config(
        unit(R.string.texture_aggregate),
        unit(R.string.texture_stains),
        unit(R.string.texture_roughness),
        unit(R.string.texture_cracks),
        contrast(),
        colors = intArrayOf(
            R.string.texture_dark_color,
            R.string.texture_concrete_color,
            R.string.texture_light_color,
            R.string.texture_crack_color
        )
    )

    TextureFilterType.Asphalt -> config(
        unit(R.string.texture_aggregate),
        unit(R.string.texture_tar),
        unit(R.string.texture_wear),
        unit(R.string.texture_speckles),
        contrast(),
        colors = intArrayOf(
            R.string.texture_tar_color,
            R.string.texture_asphalt_color,
            R.string.texture_stone_color,
            R.string.texture_dust_color
        )
    )

    TextureFilterType.Moss -> config(
        unit(R.string.density),
        unit(R.string.texture_fibers),
        unit(R.string.texture_moisture),
        unit(R.string.variation),
        unit(R.string.texture_clumps),
        colors = intArrayOf(
            R.string.texture_soil_color,
            R.string.texture_dark_moss_color,
            R.string.texture_moss_color,
            R.string.texture_tip_color
        )
    )

    TextureFilterType.Fire -> config(
        param(R.string.texture_flame_frequency, 1f..32f),
        param(R.string.turbulence, 0f..64f),
        unit(R.string.texture_intensity),
        unit(R.string.texture_smoke_amount),
        unit(R.string.texture_detail),
        colors = intArrayOf(
            R.string.texture_background_color,
            R.string.texture_red_color,
            R.string.texture_orange_color,
            R.string.texture_core_color
        )
    )

    TextureFilterType.Aurora -> config(
        param(R.string.texture_ribbons, 1f..32f),
        distortion(),
        unit(R.string.glow),
        unit(R.string.texture_stars),
        contrast(),
        colors = intArrayOf(
            R.string.texture_sky_color,
            R.string.texture_green_color,
            R.string.texture_cyan_color,
            R.string.texture_violet_color
        )
    )

    TextureFilterType.OilSlick -> config(
        param(R.string.texture_bands, 1f..32f),
        distortion(),
        unit(R.string.texture_iridescence),
        unit(R.string.texture_darkness),
        contrast(),
        colors = intArrayOf(
            R.string.texture_dark_color,
            R.string.texture_magenta_color,
            R.string.texture_cyan_color,
            R.string.texture_gold_color
        )
    )

    TextureFilterType.Watercolor -> config(
        unit(R.string.texture_blooms),
        unit(R.string.texture_pigment),
        unit(R.string.texture_edges),
        unit(R.string.texture_paper_amount),
        unit(R.string.texture_diffusion),
        colors = intArrayOf(
            R.string.texture_paper_color,
            R.string.texture_pigment_color,
            R.string.texture_secondary_color,
            R.string.texture_edge_color
        )
    )

    TextureFilterType.AbstractFlow -> config(
        param(R.string.frequency, 1f..32f),
        distortion(),
        unit(R.string.texture_symmetry),
        param(R.string.texture_sharpness, 0f..3f),
        unit(R.string.glow),
        colors = intArrayOf(
            R.string.texture_background_color,
            R.string.texture_first_color,
            R.string.texture_second_color,
            R.string.texture_glow_color
        )
    )

    TextureFilterType.Opal -> config(
        unit(R.string.texture_color_play),
        unit(R.string.texture_milkiness),
        param(R.string.texture_bands, 1f..32f),
        distortion(),
        unit(R.string.glow),
        colors = intArrayOf(
            R.string.texture_base_color,
            R.string.texture_cyan_color,
            R.string.texture_pink_color,
            R.string.texture_gold_color
        )
    )

    TextureFilterType.DamascusSteel -> config(
        param(R.string.texture_layers, 1f..64f),
        unit(R.string.texture_folding),
        distortion(),
        unit(R.string.texture_polish),
        contrast(),
        colors = intArrayOf(
            R.string.texture_dark_steel_color,
            R.string.texture_steel_color,
            R.string.texture_light_steel_color,
            R.string.texture_oxide_color
        )
    )

    TextureFilterType.Lightning -> config(
        param(R.string.texture_branches, 1f..32f),
        param(R.string.turbulence, 0f..64f),
        unit(R.string.width, 3),
        unit(R.string.glow),
        unit(R.string.texture_intensity),
        colors = intArrayOf(
            R.string.texture_background_color,
            R.string.texture_halo_color,
            R.string.texture_bolt_color,
            R.string.texture_core_color
        )
    )

    TextureFilterType.Velvet -> config(
        unit(R.string.texture_fibers),
        param(R.string.texture_direction, 0f..6.28f),
        unit(R.string.softness),
        unit(R.string.texture_sheen),
        unit(R.string.texture_folds),
        colors = intArrayOf(
            R.string.texture_shadow_color,
            R.string.texture_velvet_color,
            R.string.texture_sheen_color,
            R.string.texture_highlight_color
        )
    )

    TextureFilterType.InkMarbling -> config(
        param(R.string.texture_ribbons, 1f..32f),
        param(R.string.turbulence, 0f..64f),
        unit(R.string.texture_feathering),
        unit(R.string.texture_ink_balance),
        contrast(),
        colors = intArrayOf(
            R.string.texture_paper_color,
            R.string.texture_blue_ink_color,
            R.string.texture_red_ink_color,
            R.string.texture_dark_ink_color
        )
    )

    TextureFilterType.HolographicFoil -> config(
        param(R.string.texture_spectrum, 1f..32f),
        unit(R.string.texture_crinkles),
        unit(R.string.texture_diffraction),
        param(R.string.angle, 0f..6.28f),
        unit(R.string.shine),
        colors = intArrayOf(
            R.string.texture_silver_color,
            R.string.texture_cyan_color,
            R.string.texture_magenta_color,
            R.string.texture_yellow_color
        )
    )

    TextureFilterType.Bioluminescence -> config(
        unit(R.string.texture_veins),
        unit(R.string.texture_branching),
        param(R.string.turbulence, 0f..64f),
        unit(R.string.glow),
        unit(R.string.texture_depth),
        colors = intArrayOf(
            R.string.texture_background_color,
            R.string.texture_tissue_color,
            R.string.texture_glow_color,
            R.string.texture_core_color
        )
    )

    TextureFilterType.CosmicVortex -> config(
        param(R.string.texture_arms, 1f..16f),
        param(R.string.texture_twist, 0f..32f),
        unit(R.string.turbulence),
        unit(R.string.texture_stars),
        unit(R.string.texture_core_glow),
        colors = intArrayOf(
            R.string.texture_space_color,
            R.string.texture_blue_color,
            R.string.texture_violet_color,
            R.string.texture_core_color
        )
    )

    TextureFilterType.LavaLamp -> config(
        param(R.string.texture_blobs, 1f..16f),
        unit(R.string.softness),
        distortion(),
        unit(R.string.glow),
        contrast(),
        colors = intArrayOf(
            R.string.texture_background_color,
            R.string.texture_first_color,
            R.string.texture_second_color,
            R.string.texture_glow_color
        )
    )

    TextureFilterType.EventHorizon -> config(
        unit(R.string.texture_disk_tilt),
        unit(R.string.texture_horizon_size),
        unit(R.string.texture_disk_width, 3),
        unit(R.string.texture_lensing),
        unit(R.string.texture_stars),
        colors = intArrayOf(
            R.string.texture_space_color,
            R.string.texture_disk_color,
            R.string.texture_hot_color,
            R.string.texture_lens_color
        )
    )

    TextureFilterType.FractalBloom -> config(
        param(R.string.texture_petals, 1f..16f),
        param(R.string.texture_layers, 1f..16f),
        param(R.string.texture_curl, 0f..12f),
        unit(R.string.texture_filigree),
        unit(R.string.glow),
        colors = intArrayOf(
            R.string.texture_background_color,
            R.string.texture_outer_color,
            R.string.texture_inner_color,
            R.string.texture_core_color
        )
    )

    TextureFilterType.ChromaticTunnel -> config(
        param(R.string.texture_depth, 1f..32f),
        param(R.string.texture_twist, 0f..16f),
        param(R.string.texture_facets, 1f..16f),
        unit(R.string.texture_curvature),
        unit(R.string.glow),
        colors = intArrayOf(
            R.string.texture_deep_color,
            R.string.texture_cyan_color,
            R.string.texture_magenta_color,
            R.string.texture_light_color
        )
    )

    TextureFilterType.EclipseCorona -> config(
        unit(R.string.texture_moon_size),
        unit(R.string.texture_corona_size),
        param(R.string.texture_rays, 1f..64f),
        unit(R.string.turbulence),
        unit(R.string.texture_diamond_ring),
        colors = intArrayOf(
            R.string.texture_space_color,
            R.string.texture_corona_color,
            R.string.texture_hot_color,
            R.string.texture_light_color
        )
    )

    TextureFilterType.StrangeAttractor -> config(
        param(R.string.texture_lobes, 1f..8f),
        param(R.string.texture_orbit_density, 1f..64f),
        param(R.string.texture_curvature, 0f..12f),
        unit(R.string.texture_thickness, 3),
        unit(R.string.glow),
        colors = intArrayOf(
            R.string.texture_background_color,
            R.string.texture_cold_color,
            R.string.texture_warm_color,
            R.string.texture_core_color
        )
    )

    TextureFilterType.FerrofluidCrown -> config(
        param(R.string.texture_spikes, 1f..32f),
        unit(R.string.texture_spike_length),
        unit(R.string.texture_body_size),
        unit(R.string.texture_metallic),
        unit(R.string.distortion),
        colors = intArrayOf(
            R.string.texture_background_color,
            R.string.texture_shadow_color,
            R.string.texture_metal_color,
            R.string.texture_highlight_color
        )
    )

    TextureFilterType.Supernova -> config(
        unit(R.string.texture_shock_radius),
        unit(R.string.texture_shell_width, 3),
        unit(R.string.texture_ejecta),
        unit(R.string.turbulence),
        unit(R.string.texture_stars),
        colors = intArrayOf(
            R.string.texture_space_color,
            R.string.texture_cloud_color,
            R.string.texture_flame_color,
            R.string.texture_core_color
        )
    )

    TextureFilterType.Iris -> config(
        unit(R.string.texture_pupil_size),
        unit(R.string.texture_iris_size),
        param(R.string.texture_fibers, 1f..96f),
        unit(R.string.texture_color_variation),
        unit(R.string.texture_catchlight),
        colors = intArrayOf(
            R.string.texture_background_color,
            R.string.texture_outer_color,
            R.string.texture_inner_color,
            R.string.texture_gold_color
        )
    )

    TextureFilterType.PeacockFeather -> config(
        unit(R.string.texture_eye_size),
        param(R.string.texture_barb_density, 1f..96f),
        unit(R.string.texture_curvature),
        unit(R.string.texture_iridescence),
        unit(R.string.softness),
        colors = intArrayOf(
            R.string.texture_background_color,
            R.string.texture_feather_color,
            R.string.texture_blue_color,
            R.string.texture_gold_color
        )
    )

    TextureFilterType.NautilusShell -> config(
        param(R.string.texture_turns, 1f..8f),
        param(R.string.texture_chambers, 1f..32f),
        unit(R.string.texture_opening),
        unit(R.string.texture_ridges),
        unit(R.string.texture_pearlescence),
        colors = intArrayOf(
            R.string.texture_background_color,
            R.string.texture_shadow_color,
            R.string.texture_shell_color,
            R.string.texture_pearl_color
        )
    )

    TextureFilterType.RingedPlanet -> config(
        unit(R.string.texture_planet_size),
        unit(R.string.texture_ring_tilt),
        unit(R.string.texture_ring_width),
        unit(R.string.texture_atmosphere),
        unit(R.string.texture_stars),
        colors = intArrayOf(
            R.string.texture_space_color,
            R.string.texture_shadow_color,
            R.string.texture_planet_color,
            R.string.texture_ring_color
        )
    )

    TextureFilterType.Geode -> normalizedConfig(
        R.string.texture_bands,
        R.string.distortion,
        R.string.texture_crystal_sharpness,
        R.string.sparkle
    )

    TextureFilterType.PrismaticLight -> normalizedConfig(
        R.string.texture_facets,
        R.string.dispersion,
        R.string.texture_beam_sharpness,
        R.string.bloom
    )

    TextureFilterType.StainedGlass -> normalizedConfig(
        R.string.texture_cells,
        R.string.texture_irregularity,
        R.string.texture_lead_sharpness,
        R.string.glow
    )

    TextureFilterType.KelpForest -> normalizedConfig(
        R.string.density,
        R.string.current,
        R.string.texture_depth_sharpness,
        R.string.texture_light_rays
    )

    TextureFilterType.FrostFern -> normalizedConfig(
        R.string.texture_branches,
        R.string.texture_branching,
        R.string.texture_crystal_sharpness,
        R.string.texture_frost_glow
    )

    TextureFilterType.LiquidCrystal -> normalizedConfig(
        R.string.texture_domains,
        R.string.dispersion,
        R.string.texture_boundary_sharpness,
        R.string.shine
    )

    TextureFilterType.DragonScales -> normalizedConfig(
        R.string.texture_scale_density,
        R.string.texture_curvature,
        R.string.texture_rim_sharpness,
        R.string.texture_iridescence
    )

    TextureFilterType.FireflySwarm -> normalizedConfig(
        R.string.density,
        R.string.texture_drift,
        R.string.texture_trail_sharpness,
        R.string.glow
    )

    TextureFilterType.Mycelium -> normalizedConfig(
        R.string.texture_network_density,
        R.string.texture_branching,
        R.string.texture_fiber_sharpness,
        R.string.glow
    )

    TextureFilterType.Kintsugi -> normalizedConfig(
        R.string.texture_crack_density,
        R.string.texture_irregularity,
        R.string.texture_gold_sharpness,
        R.string.texture_metallic
    )

    TextureFilterType.CarbonFiber -> normalizedConfig(
        R.string.texture_weave_density,
        R.string.texture_twill,
        R.string.texture_fiber_sharpness,
        R.string.texture_sheen
    )

    TextureFilterType.CircuitBoard -> normalizedConfig(
        R.string.texture_trace_density,
        R.string.texture_routing,
        R.string.texture_trace_sharpness,
        R.string.texture_emission
    )

    TextureFilterType.SoapFilm -> normalizedConfig(
        R.string.texture_bands,
        R.string.texture_flow,
        R.string.texture_film_sharpness,
        R.string.shine
    )

    TextureFilterType.MoireGuilloche -> normalizedConfig(
        R.string.frequency,
        R.string.offset,
        R.string.texture_line_sharpness,
        R.string.glow
    )

    TextureFilterType.SnakeSkin -> normalizedConfig(
        R.string.texture_scale_density,
        R.string.texture_irregularity,
        R.string.texture_rim_sharpness,
        R.string.texture_sheen
    )

    TextureFilterType.Terrazzo -> normalizedConfig(
        R.string.texture_chip_density,
        R.string.variation,
        R.string.texture_chip_sharpness,
        R.string.texture_polish
    )

    TextureFilterType.GalaxyFilaments -> normalizedConfig(
        R.string.texture_filament_density,
        R.string.turbulence,
        R.string.texture_filament_sharpness,
        R.string.texture_star_glow
    )

    TextureFilterType.VolcanicObsidian -> normalizedConfig(
        R.string.texture_facets,
        R.string.texture_fracturing,
        R.string.texture_edge_sharpness,
        R.string.texture_lava_glow
    )

    TextureFilterType.MotherboardHeatmap -> normalizedConfig(
        R.string.texture_trace_density,
        R.string.texture_heat_spread,
        R.string.texture_trace_sharpness,
        R.string.texture_heat_glow
    )

    TextureFilterType.MicroscopicDiatoms -> normalizedConfig(
        R.string.texture_colony_density,
        R.string.texture_shell_variation,
        R.string.texture_pore_sharpness,
        R.string.texture_luminescence
    )

    TextureFilterType.ReactionDiffusion -> normalizedConfig(
        R.string.texture_pattern_scale,
        R.string.feedback,
        R.string.texture_edge_sharpness,
        R.string.glow
    )

    TextureFilterType.CoralGrowth -> normalizedConfig(
        R.string.texture_branch_density,
        R.string.texture_growth_bias,
        R.string.texture_edge_sharpness,
        R.string.glow
    )

    TextureFilterType.SlimeMold -> normalizedConfig(
        R.string.texture_trail_density,
        R.string.texture_chemotaxis,
        R.string.texture_trail_sharpness,
        R.string.glow
    )

    TextureFilterType.DendriticCrystal -> normalizedConfig(
        R.string.texture_branch_count,
        R.string.texture_branching,
        R.string.texture_crystal_sharpness,
        R.string.glow
    )

    TextureFilterType.ElectricArcField -> normalizedConfig(
        R.string.texture_arc_count,
        R.string.turbulence,
        R.string.texture_arc_sharpness,
        R.string.glow
    )

    TextureFilterType.CloudChamber -> normalizedConfig(
        R.string.texture_track_density,
        R.string.texture_deflection,
        R.string.texture_track_sharpness,
        R.string.glow
    )

    TextureFilterType.TurbulentInk -> normalizedConfig(
        R.string.texture_ribbon_density,
        R.string.turbulence,
        R.string.texture_edge_sharpness,
        R.string.texture_bleed
    )

    TextureFilterType.CellularEmbryo -> normalizedConfig(
        R.string.texture_embryo_density,
        R.string.texture_morphology,
        R.string.texture_membrane_sharpness,
        R.string.glow
    )

    TextureFilterType.NeuralGarden -> normalizedConfig(
        R.string.texture_neuron_density,
        R.string.texture_branching,
        R.string.texture_fiber_sharpness,
        R.string.glow
    )

    TextureFilterType.MagneticField -> normalizedConfig(
        R.string.texture_line_density,
        R.string.texture_pole_distortion,
        R.string.texture_line_sharpness,
        R.string.glow
    )

    TextureFilterType.RiverDelta -> normalizedConfig(
        R.string.texture_channel_density,
        R.string.texture_erosion,
        R.string.texture_channel_sharpness,
        R.string.texture_water_glow
    )

    TextureFilterType.LichenColony -> normalizedConfig(
        R.string.texture_colony_density,
        R.string.texture_spread,
        R.string.texture_edge_sharpness,
        R.string.texture_moisture
    )

    TextureFilterType.BacterialCulture -> normalizedConfig(
        R.string.texture_colony_density,
        R.string.texture_growth_variation,
        R.string.texture_membrane_sharpness,
        R.string.glow
    )

    TextureFilterType.FluidVorticity -> normalizedConfig(
        R.string.texture_vortex_density,
        R.string.turbulence,
        R.string.texture_ridge_sharpness,
        R.string.glow
    )

    TextureFilterType.CrystalGrowth -> normalizedConfig(
        R.string.texture_crystal_density,
        R.string.texture_anisotropy,
        R.string.texture_facet_sharpness,
        R.string.glow
    )

    TextureFilterType.GalacticWeb -> normalizedConfig(
        R.string.texture_node_density,
        R.string.texture_gravity_warp,
        R.string.texture_filament_sharpness,
        R.string.glow
    )

    TextureFilterType.VeinedLeaf -> normalizedConfig(
        R.string.texture_vein_density,
        R.string.texture_curvature,
        R.string.texture_vein_sharpness,
        R.string.texture_translucency
    )

    TextureFilterType.PorousSponge -> normalizedConfig(
        R.string.texture_pore_density,
        R.string.texture_irregularity,
        R.string.texture_pore_sharpness,
        R.string.texture_moisture
    )

    TextureFilterType.RainOnGlass -> normalizedConfig(
        R.string.texture_drop_density,
        R.string.texture_streaking,
        R.string.texture_drop_sharpness,
        R.string.texture_refraction
    )

    TextureFilterType.EmberField -> normalizedConfig(
        R.string.texture_ember_density,
        R.string.texture_updraft,
        R.string.texture_ember_sharpness,
        R.string.glow
    )

    TextureFilterType.QuantumFoam -> normalizedConfig(
        R.string.texture_bubble_density,
        R.string.texture_uncertainty,
        R.string.texture_boundary_sharpness,
        R.string.glow
    )

    TextureFilterType.ChladniPlate -> normalizedConfig(
        R.string.texture_mode_count,
        R.string.texture_asymmetry,
        R.string.texture_line_sharpness,
        R.string.texture_metallic
    )

    TextureFilterType.CymaticRosette -> normalizedConfig(
        R.string.texture_petal_count,
        R.string.texture_resonance,
        R.string.texture_ridge_sharpness,
        R.string.glow
    )

    TextureFilterType.LichtenbergFigure -> normalizedConfig(
        R.string.texture_branch_count,
        R.string.texture_branching,
        R.string.texture_arc_sharpness,
        R.string.glow
    )

    TextureFilterType.Quasicrystal -> normalizedConfig(
        R.string.texture_symmetry,
        R.string.texture_phase_warp,
        R.string.texture_ridge_sharpness,
        R.string.glow
    )

    TextureFilterType.Mandelbrot -> normalizedConfig(
        R.string.texture_iteration_detail,
        R.string.texture_center_offset,
        R.string.texture_boundary_sharpness,
        R.string.glow
    )

    TextureFilterType.BurningShip -> normalizedConfig(
        R.string.texture_iteration_detail,
        R.string.texture_center_offset,
        R.string.texture_boundary_sharpness,
        R.string.glow
    )

    TextureFilterType.JuliaSet -> normalizedConfig(
        R.string.iterations,
        R.string.texture_constant_phase,
        R.string.texture_filament_sharpness,
        R.string.glow
    )

    TextureFilterType.KaleidoscopeCrystal -> normalizedConfig(
        R.string.texture_segments,
        R.string.texture_fold_warp,
        R.string.texture_facet_sharpness,
        R.string.glow
    )

    TextureFilterType.SpectralPrism -> normalizedConfig(
        R.string.texture_facet_count,
        R.string.texture_refraction,
        R.string.texture_edge_sharpness,
        R.string.bloom
    )

    TextureFilterType.TopologicalKnot -> normalizedConfig(
        R.string.texture_turns,
        R.string.texture_twist,
        R.string.texture_tube_sharpness,
        R.string.glow
    )

    TextureFilterType.XRayBotanical -> normalizedConfig(
        R.string.texture_leaf_density,
        R.string.texture_curvature,
        R.string.texture_vein_sharpness,
        R.string.texture_luminescence
    )

    TextureFilterType.Chromatophore -> normalizedConfig(
        R.string.texture_cell_density,
        R.string.texture_pulse_phase,
        R.string.texture_cell_sharpness,
        R.string.texture_iridescence
    )

    TextureFilterType.BiomechanicalTissue -> normalizedConfig(
        R.string.texture_rib_density,
        R.string.texture_tension,
        R.string.texture_ridge_sharpness,
        R.string.texture_wet_sheen
    )

    TextureFilterType.GildedFiligree -> normalizedConfig(
        R.string.texture_curl_density,
        R.string.texture_ornament,
        R.string.texture_line_sharpness,
        R.string.texture_metallic
    )

    TextureFilterType.AncientRunes -> normalizedConfig(
        R.string.texture_glyph_density,
        R.string.texture_erosion,
        R.string.texture_stroke_sharpness,
        R.string.glow
    )

    TextureFilterType.SolarGranulation -> normalizedConfig(
        R.string.texture_granule_density,
        R.string.texture_convection,
        R.string.texture_edge_sharpness,
        R.string.texture_heat_glow
    )

    TextureFilterType.LunarEjecta -> normalizedConfig(
        R.string.texture_crater_density,
        R.string.texture_ray_length,
        R.string.texture_rim_sharpness,
        R.string.texture_albedo
    )

    TextureFilterType.OceanCurrents -> normalizedConfig(
        R.string.texture_current_density,
        R.string.texture_curl,
        R.string.texture_stream_sharpness,
        R.string.glow
    )

    TextureFilterType.InkWashMountains -> normalizedConfig(
        R.string.texture_ridge_count,
        R.string.texture_mist,
        R.string.texture_ridge_sharpness,
        R.string.texture_paper_glow
    )

    TextureFilterType.NeonCity -> normalizedConfig(
        R.string.texture_building_density,
        R.string.texture_perspective,
        R.string.texture_edge_sharpness,
        R.string.texture_neon_glow
    )

    TextureFilterType.PhyllotaxisBloom -> normalizedConfig(
        R.string.texture_seed_density,
        R.string.texture_spiral_angle,
        R.string.texture_seed_sharpness,
        R.string.glow
    )

    TextureFilterType.SierpinskiTriangle -> normalizedConfig(
        R.string.texture_depth,
        R.string.rotation,
        R.string.texture_edge_sharpness,
        R.string.glow
    )

    TextureFilterType.ApollonianGasket -> normalizedConfig(
        R.string.texture_recursion,
        R.string.texture_curvature,
        R.string.texture_boundary_sharpness,
        R.string.glow
    )

    TextureFilterType.HyperbolicTiling -> normalizedConfig(
        R.string.texture_polygon_sides,
        R.string.texture_curvature,
        R.string.texture_edge_sharpness,
        R.string.glow
    )

    TextureFilterType.MoebiusWeave -> normalizedConfig(
        R.string.texture_band_count,
        R.string.texture_twist,
        R.string.texture_edge_sharpness,
        R.string.texture_sheen
    )

    TextureFilterType.RorschachInkblot -> normalizedConfig(
        R.string.texture_lobe_count,
        R.string.texture_symmetry,
        R.string.texture_edge_sharpness,
        R.string.texture_bleed
    )

    TextureFilterType.SeismicInterference -> normalizedConfig(
        R.string.texture_source_count,
        R.string.phase,
        R.string.texture_ridge_sharpness,
        R.string.glow
    )

    TextureFilterType.RayleighBenard -> normalizedConfig(
        R.string.texture_cell_density,
        R.string.texture_convection,
        R.string.texture_edge_sharpness,
        R.string.texture_heat_glow
    )

    TextureFilterType.OrigamiFacets -> normalizedConfig(
        R.string.texture_fold_density,
        R.string.texture_asymmetry,
        R.string.texture_crease_sharpness,
        R.string.texture_sheen
    )

    TextureFilterType.FiberOpticBundle -> normalizedConfig(
        R.string.texture_fiber_density,
        R.string.texture_bend,
        R.string.texture_core_sharpness,
        R.string.glow
    )

    else -> error("Unsupported fast-noise texture type: $this")
}

private fun normalizedConfig(
    @StringRes value1: Int,
    @StringRes value2: Int,
    @StringRes value3: Int,
    @StringRes value4: Int
) = config(
    unit(value1),
    unit(value2),
    unit(value3),
    unit(value4),
    contrast(),
    colors = intArrayOf(
        R.string.texture_background_color,
        R.string.texture_primary_color,
        R.string.texture_secondary_color,
        R.string.texture_highlight_color
    )
)

private fun config(
    vararg values: ParamInfo,
    colors: IntArray
) = FastNoiseConfig(
    values = values.toList(),
    colors = colors.toList()
)

private fun unit(
    @StringRes title: Int,
    roundTo: Int = 2
) = param(title, 0f..1f, roundTo)

private fun distortion() = param(R.string.distortion, 0f..64f)

private fun contrast() = param(R.string.contrast, 0f..3f)

private fun param(
    @StringRes title: Int,
    range: ClosedFloatingPointRange<Float>,
    roundTo: Int = 2
) = ParamInfo(title, range, roundTo)

private fun <T> List<T>.updated(index: Int, value: T): List<T> =
    toMutableList().apply { this[index] = value }
