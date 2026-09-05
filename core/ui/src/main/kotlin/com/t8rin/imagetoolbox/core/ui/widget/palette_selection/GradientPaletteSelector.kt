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

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.GradientPalette
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Check
import com.t8rin.imagetoolbox.core.resources.icons.Done
import com.t8rin.imagetoolbox.core.resources.icons.Gradient
import com.t8rin.imagetoolbox.core.ui.theme.blend
import com.t8rin.imagetoolbox.core.ui.utils.helper.toColor
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedChip
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.AutoCornersShape
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.fadingEdges
import com.t8rin.imagetoolbox.core.ui.widget.modifier.shapeByInteraction
import com.t8rin.imagetoolbox.core.ui.widget.modifier.transparencyChecker
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem

@Composable
fun GradientPaletteSelector(
    value: GradientPalette?,
    onValueChange: (GradientPalette) -> Unit,
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.palette),
    shape: Shape = ShapeDefaults.default,
    color: Color = Color.Unspecified,
    allowCustom: Boolean = true
) {
    var showPicker by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = modifier.container(
            shape = shape,
            color = color,
            resultPadding = 8.dp
        )
    ) {
        TitleItem(
            text = title,
            modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp)
        )
        GradientPaletteRow(
            value = value,
            onValueChange = onValueChange,
            onCustomClick = { showPicker = true }.takeIf { allowCustom }
        )
    }
    if (allowCustom) {
        GradientPaletteSheet(
            visible = showPicker,
            onDismiss = { showPicker = false },
            value = value,
            onValueChange = onValueChange
        )
    }
}

@Composable
internal fun GradientPaletteRow(
    value: GradientPalette?,
    onValueChange: (GradientPalette) -> Unit,
    palettes: List<GradientPalette> = GradientPalette.entries,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(8.dp),
    onCustomClick: (() -> Unit)? = null,
    showLabels: Boolean = true
) {
    val state = rememberLazyListState()
    val isCustom = value is GradientPalette.Custom
    LaunchedEffect(isCustom) {
        if (isCustom && onCustomClick != null) state.animateScrollToItem(0)
    }
    LazyRow(
        state = state,
        modifier = modifier
            .fillMaxWidth()
            .then(if (showLabels) Modifier else Modifier.height(64.dp))
            .fadingEdges(state),
        horizontalArrangement = Arrangement.spacedBy(if (showLabels) 2.dp else 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        contentPadding = contentPadding,
        flingBehavior = enhancedFlingBehavior()
    ) {
        if (onCustomClick != null) {
            item(key = "custom_picker") {
                EnhancedChip(
                    selected = isCustom,
                    onClick = onCustomClick,
                    modifier = Modifier.padding(end = 6.dp),
                    selectedColor = MaterialTheme.colorScheme.tertiaryContainer,
                    selectedContentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    shape = ShapeDefaults.small,
                    contentPadding = PaddingValues(8.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(28.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Gradient,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        GradientPalettePreview(
                            palette = value ?: GradientPalette.SoftRainbow,
                            modifier = Modifier
                                .width(32.dp)
                                .height(2.dp)
                                .clip(ShapeDefaults.circle)
                        )
                    }
                }
            }
        }
        itemsIndexed(
            items = palettes,
            key = { _, palette -> palette.toSerializedString() }
        ) { index, palette ->
            if (showLabels) {
                GradientPaletteChip(
                    palette = palette,
                    selected = palette == value,
                    onClick = { onValueChange(palette) },
                    shape = ShapeDefaults.byIndex(
                        index = index,
                        size = palettes.size,
                        vertical = false,
                        roundedCorner = 12.dp
                    )
                )
            } else {
                GradientPaletteItem(
                    palette = palette,
                    selected = palette == value,
                    onClick = { onValueChange(palette) }
                )
            }
        }
    }
}

@Composable
private fun GradientPaletteChip(
    palette: GradientPalette,
    selected: Boolean,
    onClick: () -> Unit,
    shape: Shape
) {
    EnhancedChip(
        selected = selected,
        onClick = onClick,
        selectedColor = MaterialTheme.colorScheme.tertiaryContainer,
        selectedContentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        shape = shape,
        contentPadding = PaddingValues(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            GradientPalettePreview(
                palette = palette,
                modifier = Modifier
                    .width(64.dp)
                    .height(18.dp)
                    .clip(ShapeDefaults.extraSmall)
            )
            Text(text = palette.label(), style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
internal fun GradientPaletteItem(
    palette: GradientPalette,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val shape = shapeByInteraction(
        shape = if (selected) ShapeDefaults.small else AutoCornersShape(21.dp),
        pressedShape = ShapeDefaults.pressed,
        interactionSource = interactionSource
    )
    val colors = remember(palette) { palette.colors.map { it.toColor() } }
    val accent = colors[colors.size / 2].blend(MaterialTheme.colorScheme.primary, 0.25f)
    val light = accent.blend(Color.White, 0.85f)
    val dark = accent.blend(Color.Black, 0.75f)
    val contentColor = if (accent.luminance() < 0.3f) light else dark
    val fillColor = if (accent.luminance() < 0.3f) dark else light

    Box(
        modifier = modifier
            .height(42.dp)
            .aspectRatio(
                ratio = animateFloatAsState(
                    targetValue = if (selected) 1.5f else 1f,
                    animationSpec = tween(400)
                ).value,
                matchHeightConstraintsFirst = true
            )
            .container(shape = shape, color = Color.Transparent, resultPadding = 0.dp)
            .clip(shape)
            .transparencyChecker()
            .background(Brush.horizontalGradient(colors))
            .hapticsClickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                tint = fillColor,
                modifier = Modifier.size(24.dp)
            )
            Icon(
                imageVector = Icons.Rounded.Done,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
internal fun GradientPalettePreview(
    palette: GradientPalette,
    modifier: Modifier = Modifier
) {
    val brush = remember(palette) {
        Brush.horizontalGradient(palette.colors.map { it.toColor() })
    }
    Box(
        modifier
            .transparencyChecker()
            .background(brush)
    )
}

@Composable
fun GradientPalette.label(): String = stringResource(
    when (this) {
        is GradientPalette.Custom -> R.string.custom
        GradientPalette.SoftRainbow -> R.string.gradient_palette_soft_rainbow
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
        GradientPalette.Rgb -> R.string.fractal_palette_rgb
        GradientPalette.Ryb -> R.string.fractal_palette_ryb
        GradientPalette.Cmyk -> R.string.fractal_palette_cmyk
        GradientPalette.HsvWheel -> R.string.fractal_palette_hsv_wheel
        GradientPalette.RedChannel -> R.string.fractal_palette_red_channel
        GradientPalette.GreenChannel -> R.string.fractal_palette_green_channel
        GradientPalette.BlueChannel -> R.string.fractal_palette_blue_channel
        GradientPalette.Heatmap -> R.string.fractal_palette_heatmap
        GradientPalette.ColdFire -> R.string.fractal_palette_cold_fire
        GradientPalette.Ultraviolet -> R.string.fractal_palette_ultraviolet
        GradientPalette.ToxicWaste -> R.string.fractal_palette_toxic_waste
        GradientPalette.BloodMoon -> R.string.fractal_palette_blood_moon
        GradientPalette.Abyss -> R.string.fractal_palette_abyss
        GradientPalette.ElectricCandy -> R.string.fractal_palette_electric_candy
        GradientPalette.BlackGold -> R.string.fractal_palette_black_gold
        GradientPalette.Ghost -> R.string.fractal_palette_ghost
        GradientPalette.Grayscale -> R.string.fractal_palette_grayscale
    }
)
