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

package com.t8rin.imagetoolbox.core.ui.widget.palette_selection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.GradientPalette
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.helper.toColor
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedChip
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.fadingEdges
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem

@Composable
fun GradientPaletteSelector(
    value: GradientPalette?,
    onValueChange: (GradientPalette) -> Unit,
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.palette),
    shape: Shape = ShapeDefaults.default
) {
    Column(
        modifier = modifier.container(
            shape = shape,
            resultPadding = 8.dp
        )
    ) {
        TitleItem(
            text = title,
            modifier = Modifier.padding(
                top = 8.dp,
                start = 8.dp,
                end = 8.dp
            )
        )
        val state = rememberLazyListState()
        LazyRow(
            state = state,
            modifier = Modifier
                .fillMaxWidth()
                .fadingEdges(state),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            contentPadding = PaddingValues(8.dp),
            flingBehavior = enhancedFlingBehavior()
        ) {
            itemsIndexed(
                items = GradientPalette.entries,
                key = { _, palette -> palette.name }
            ) { index, palette ->
                EnhancedChip(
                    selected = palette == value,
                    onClick = { onValueChange(palette) },
                    selectedColor = MaterialTheme.colorScheme.tertiaryContainer,
                    selectedContentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    shape = ShapeDefaults.byIndex(
                        index = index,
                        size = GradientPalette.entries.size,
                        vertical = false,
                        roundedCorner = 12.dp
                    ),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val colors = remember(palette) {
                            palette.colors.map { it.toColor() }
                        }
                        Box(
                            modifier = Modifier
                                .width(64.dp)
                                .height(18.dp)
                                .clip(ShapeDefaults.extraSmall)
                                .background(Brush.horizontalGradient(colors))
                        )
                        Text(
                            text = palette.label(),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GradientPalette.label(): String = stringResource(
    when (this) {
        GradientPalette.RGB -> R.string.fractal_palette_rgb
        GradientPalette.Classic -> R.string.fractal_palette_classic
        GradientPalette.Fire -> R.string.fractal_palette_fire
        GradientPalette.Ocean -> R.string.fractal_palette_ocean
        GradientPalette.Viridis -> R.string.fractal_palette_viridis
        GradientPalette.Magma -> R.string.fractal_palette_magma
        GradientPalette.Inferno -> R.string.fractal_palette_inferno
        GradientPalette.Plasma -> R.string.fractal_palette_plasma
        GradientPalette.Turbo -> R.string.fractal_palette_turbo
        GradientPalette.Twilight -> R.string.fractal_palette_twilight
        GradientPalette.Ice -> R.string.fractal_palette_ice
        GradientPalette.Forest -> R.string.fractal_palette_forest
        GradientPalette.Neon -> R.string.fractal_palette_neon
        GradientPalette.Cividis -> R.string.fractal_palette_cividis
        GradientPalette.Cubehelix -> R.string.fractal_palette_cubehelix
        GradientPalette.Spectral -> R.string.fractal_palette_spectral
        GradientPalette.Aurora -> R.string.fractal_palette_aurora
        GradientPalette.Sunset -> R.string.fractal_palette_sunset
        GradientPalette.Copper -> R.string.fractal_palette_copper
        GradientPalette.Rocket -> R.string.fractal_palette_rocket
        GradientPalette.Mako -> R.string.fractal_palette_mako
        GradientPalette.Amethyst -> R.string.fractal_palette_amethyst
        GradientPalette.Vaporwave -> R.string.fractal_palette_vaporwave
        GradientPalette.Earth -> R.string.fractal_palette_earth
        GradientPalette.Rainbow -> R.string.fractal_palette_rainbow
        GradientPalette.Cool -> R.string.fractal_palette_cool
        GradientPalette.Hot -> R.string.fractal_palette_hot
        GradientPalette.PurpleDream -> R.string.fractal_palette_purple_dream
        GradientPalette.Lava -> R.string.fractal_palette_lava
        GradientPalette.Galaxy -> R.string.fractal_palette_galaxy
        GradientPalette.Mint -> R.string.fractal_palette_mint
        GradientPalette.Cherry -> R.string.fractal_palette_cherry
        GradientPalette.XfAlternatingGrey -> R.string.fractal_palette_xf_alternating_grey
        GradientPalette.XfBlues -> R.string.fractal_palette_xf_blues
        GradientPalette.XfChromatic -> R.string.fractal_palette_xf_chromatic
        GradientPalette.XfDefault -> R.string.fractal_palette_xf_default
        GradientPalette.XfDefaultWhite -> R.string.fractal_palette_xf_default_white
        GradientPalette.XfFireStorm -> R.string.fractal_palette_xf_fire_storm
        GradientPalette.XfFroth3 -> R.string.fractal_palette_xf_froth_3
        GradientPalette.XfFroth316 -> R.string.fractal_palette_xf_froth_3_16
        GradientPalette.XfFroth6 -> R.string.fractal_palette_xf_froth_6
        GradientPalette.XfFroth616 -> R.string.fractal_palette_xf_froth_6_16
        GradientPalette.XfGamma1 -> R.string.fractal_palette_xf_gamma_1
        GradientPalette.XfGamma2 -> R.string.fractal_palette_xf_gamma_2
        GradientPalette.XfGlasses1 -> R.string.fractal_palette_xf_3d_glasses_1
        GradientPalette.XfGlasses2 -> R.string.fractal_palette_xf_3d_glasses_2
        GradientPalette.XfGoodEga -> R.string.fractal_palette_xf_good_ega
        GradientPalette.XfGreen -> R.string.fractal_palette_xf_green
        GradientPalette.XfGrey -> R.string.fractal_palette_xf_grey
        GradientPalette.XfGrid -> R.string.fractal_palette_xf_grid
        GradientPalette.XfHeadache2 -> R.string.fractal_palette_xf_headache_2
        GradientPalette.XfHeadache -> R.string.fractal_palette_xf_headache
        GradientPalette.XfLandscape -> R.string.fractal_palette_xf_landscape
        GradientPalette.XfLyapunov -> R.string.fractal_palette_xf_lyapunov
        GradientPalette.XfNeon -> R.string.fractal_palette_xf_neon
        GradientPalette.XfPaintJet -> R.string.fractal_palette_xf_paintjet
        GradientPalette.XfRoyal -> R.string.fractal_palette_xf_royal
        GradientPalette.XfTopo -> R.string.fractal_palette_xf_topo
        GradientPalette.XfVolcano -> R.string.fractal_palette_xf_volcano
        GradientPalette.Grayscale -> R.string.fractal_palette_grayscale
    }
)
