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
}

internal fun CellularGridType.titleRes(): Int = when (this) {
    CellularGridType.Random -> R.string.grid_random
    CellularGridType.Square -> R.string.grid_square
    CellularGridType.Hexagonal -> R.string.grid_hexagonal
    CellularGridType.Octagonal -> R.string.grid_octagonal
    CellularGridType.Triangular -> R.string.grid_triangular
}