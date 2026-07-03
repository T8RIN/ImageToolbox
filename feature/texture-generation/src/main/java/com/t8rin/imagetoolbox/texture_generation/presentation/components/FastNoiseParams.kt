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
        IntParam(
            value = params.seed,
            title = stringResource(R.string.seed),
            range = -10000f..10000f,
            onValueChange = { seed ->
                onValueChange(value.copy(fastNoiseParams = params.copy(seed = seed)))
            },
            shape = ShapeDefaults.top
        )
        FloatParam(
            value = params.scale,
            title = stringResource(R.string.scale),
            range = 0.001f..0.05f,
            roundTo = 4,
            onValueChange = { scale ->
                onValueChange(value.copy(fastNoiseParams = params.copy(scale = scale)))
            }
        )
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
                }
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
                shape = if (index == config.colors.lastIndex) {
                    ShapeDefaults.bottom
                } else ShapeDefaults.center
            )
        }
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

    else -> error("Unsupported fast-noise texture type: $this")
}

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