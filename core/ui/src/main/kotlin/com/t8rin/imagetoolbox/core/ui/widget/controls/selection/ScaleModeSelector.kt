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

package com.t8rin.imagetoolbox.core.ui.widget.controls.selection

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.domain.image.model.ImageScaleMode
import com.t8rin.imagetoolbox.core.domain.image.model.ScaleColorSpace
import com.t8rin.imagetoolbox.core.domain.image.model.title
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.outlineVariant
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.utils.confetti.LocalConfettiHostState
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalResourceManager
import com.t8rin.imagetoolbox.core.ui.utils.provider.SafeLocalContainerColor
import com.t8rin.imagetoolbox.core.ui.widget.buttons.SupportingButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBadge
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedChip
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateShape
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.fadingEdges
import com.t8rin.imagetoolbox.core.ui.widget.modifier.scaleOnTap
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import kotlinx.coroutines.launch

@Composable
fun ScaleModeSelector(
    value: ImageScaleMode,
    onValueChange: (ImageScaleMode) -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Unspecified,
    shape: Shape = ShapeDefaults.extraLarge,
    enableItemsCardBackground: Boolean = true,
    titlePadding: PaddingValues = PaddingValues(top = 8.dp),
    titleArrangement: Arrangement.Horizontal = Arrangement.Center,
    entries: List<ImageScaleMode> = ImageScaleMode.defaultEntries(),
    title: @Composable RowScope.() -> Unit = {
        val scope = rememberCoroutineScope()
        val confettiHostState = LocalConfettiHostState.current
        val showConfetti: () -> Unit = {
            scope.launch {
                confettiHostState.showConfetti()
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.scale_mode),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f, false)
            )
            EnhancedBadge(
                content = {
                    Text(entries.size.toString())
                },
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onTertiary,
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .padding(bottom = 12.dp)
                    .scaleOnTap {
                        showConfetti()
                    }
            )
        }
    }
) {
    val isColorSpaceSelectionVisible = enableItemsCardBackground && value !is ImageScaleMode.Base
    var showInfoSheet by rememberSaveable { mutableStateOf(false) }
    val settingsState = LocalSettingsState.current

    LaunchedEffect(settingsState) {
        if (value != settingsState.defaultImageScaleMode) {
            onValueChange(settingsState.defaultImageScaleMode)
        }
    }

    Column(
        modifier = modifier
            .container(
                shape = shape,
                color = backgroundColor
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(titlePadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = titleArrangement
        ) {
            title()
            Spacer(modifier = Modifier.width(8.dp))
            SupportingButton(
                onClick = {
                    showInfoSheet = true
                }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        val chipsModifier = Modifier
            .fillMaxWidth()
            .then(
                if (enableItemsCardBackground) {
                    Modifier
                        .padding(horizontal = 8.dp)
                        .container(
                            color = MaterialTheme.colorScheme.surface,
                            shape = animateShape(
                                if (isColorSpaceSelectionVisible) {
                                    ShapeDefaults.top
                                } else ShapeDefaults.default
                            )
                        )
                        .padding(horizontal = 8.dp, vertical = 12.dp)
                } else Modifier.padding(8.dp)
            )

        val state = rememberLazyStaggeredGridState()
        LazyHorizontalStaggeredGrid(
            verticalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.CenterVertically
            ),
            state = state,
            horizontalItemSpacing = 8.dp,
            rows = StaggeredGridCells.Adaptive(30.dp),
            modifier = Modifier
                .heightIn(max = if (enableItemsCardBackground) 160.dp else 140.dp)
                .then(chipsModifier)
                .fadingEdges(
                    scrollableState = state,
                    isVertical = false,
                    spanCount = 3
                ),
            contentPadding = PaddingValues(2.dp),
            flingBehavior = enhancedFlingBehavior()
        ) {
            items(entries) {
                val selected by remember(value, it) {
                    derivedStateOf {
                        value::class.isInstance(it)
                    }
                }
                EnhancedChip(
                    onClick = {
                        onValueChange(it.copy(value.scaleColorSpace))
                    },
                    selected = selected,
                    label = {
                        Text(text = stringResource(id = it.title))
                    },
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    selectedColor = MaterialTheme.colorScheme.outlineVariant(
                        0.2f,
                        MaterialTheme.colorScheme.tertiary
                    ),
                    selectedContentColor = MaterialTheme.colorScheme.onTertiary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        AnimatedVisibility(
            visible = isColorSpaceSelectionVisible,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            val items = remember {
                ScaleColorSpace.entries
            }
            DataSelector(
                value = value.scaleColorSpace,
                onValueChange = {
                    onValueChange(
                        value.copy(it)
                    )
                },
                spanCount = 2,
                entries = items,
                title = stringResource(R.string.tag_color_space),
                titleIcon = Icons.Outlined.ColorLens,
                itemContentText = {
                    it.title
                },
                containerColor = MaterialTheme.colorScheme.surface,
                shape = ShapeDefaults.bottom,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .padding(horizontal = 8.dp),
                selectedItemColor = MaterialTheme.colorScheme.secondary
            )
        }

        AnimatedVisibility(isColorSpaceSelectionVisible || enableItemsCardBackground) {
            Spacer(Modifier.height(8.dp))
        }
    }

    EnhancedModalBottomSheet(
        sheetContent = {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                flingBehavior = enhancedFlingBehavior()
            ) {
                itemsIndexed(entries) { index, item ->
                    val selected by remember(value, item) {
                        derivedStateOf {
                            value::class.isInstance(item)
                        }
                    }
                    val containerColor = takeColorFromScheme {
                        if (selected) secondaryContainer
                        else SafeLocalContainerColor
                    }
                    val contentColor = takeColorFromScheme {
                        if (selected) onSecondaryContainer
                        else onSurface
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .container(
                                color = containerColor,
                                shape = ShapeDefaults.byIndex(
                                    index = index,
                                    size = entries.size
                                ),
                                resultPadding = 0.dp
                            )
                            .hapticsClickable {
                                onValueChange(item)
                            }
                    ) {
                        TitleItem(text = stringResource(id = item.title))
                        Text(
                            text = stringResource(id = item.subtitle),
                            modifier = Modifier.padding(
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 16.dp
                            ),
                            fontSize = 14.sp,
                            lineHeight = 18.sp,
                            color = contentColor
                        )
                    }
                }
            }
        },
        visible = showInfoSheet,
        onDismiss = {
            showInfoSheet = it
        },
        title = {
            TitleItem(text = stringResource(R.string.scale_mode))
        },
        confirmButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = { showInfoSheet = false }
            ) {
                AutoSizeText(stringResource(R.string.close))
            }
        }
    )
}

@Composable
fun ImageScaleMode.Companion.defaultEntries(): List<ImageScaleMode> {
    val context = LocalResourceManager.current
    return remember {
        listOf(ImageScaleMode.Base) + simpleEntries.sortedBy {
            context.getString(it.title)
        } + complexEntries.sortedBy {
            context.getString(it.title)
        }
    }
}

val ScaleColorSpace.title: String
    @Composable
    get() = when (this) {
        is ScaleColorSpace.Linear -> stringResource(R.string.linear)
        is ScaleColorSpace.SRGB -> "sRGB"
        is ScaleColorSpace.LAB -> "LAB"
        is ScaleColorSpace.LUV -> "LUV"
        is ScaleColorSpace.Sigmoidal -> stringResource(R.string.sigmoidal)
        is ScaleColorSpace.XYZ -> "XYZ"
        is ScaleColorSpace.F32Gamma22 -> "${stringResource(R.string.gamma)} 2.2"
        is ScaleColorSpace.F32Gamma28 -> "${stringResource(R.string.gamma)} 2.8"
        is ScaleColorSpace.F32Rec709 -> "Rec.709"
        is ScaleColorSpace.F32sRGB -> "F32 sRGB"
        is ScaleColorSpace.LCH -> "LCH"
        is ScaleColorSpace.OklabGamma22 -> "Oklab G2.2"
        is ScaleColorSpace.OklabGamma28 -> "Oklab G2.8"
        is ScaleColorSpace.OklabRec709 -> "Oklab Rec.709"
        is ScaleColorSpace.OklabSRGB -> "Oklab sRGB"
        is ScaleColorSpace.JzazbzGamma22 -> "Jzazbz ${stringResource(R.string.gamma)} 2.2"
        is ScaleColorSpace.JzazbzGamma28 -> "Jzazbz ${stringResource(R.string.gamma)} 2.8"
        is ScaleColorSpace.JzazbzRec709 -> "Jzazbz Rec.709"
        is ScaleColorSpace.JzazbzSRGB -> "Jzazbz sRGB"
    }

private val ImageScaleMode.subtitle: Int
    get() = when (this) {
        ImageScaleMode.Base,
        ImageScaleMode.NotPresent -> R.string.basic_sub

        is ImageScaleMode.Bilinear -> R.string.bilinear_sub
        is ImageScaleMode.Nearest -> R.string.nearest_sub
        is ImageScaleMode.Cubic -> R.string.cubic_sub
        is ImageScaleMode.Mitchell -> R.string.mitchell_sub
        is ImageScaleMode.Catmull -> R.string.catmull_sub
        is ImageScaleMode.Hermite -> R.string.hermite_sub
        is ImageScaleMode.BSpline -> R.string.bspline_sub
        is ImageScaleMode.Hann -> R.string.hann_sub
        is ImageScaleMode.Bicubic -> R.string.bicubic_sub
        is ImageScaleMode.Hamming -> R.string.hamming_sub
        is ImageScaleMode.Hanning -> R.string.hanning_sub
        is ImageScaleMode.Blackman -> R.string.blackman_sub
        is ImageScaleMode.Welch -> R.string.welch_sub
        is ImageScaleMode.Quadric -> R.string.quadric_sub
        is ImageScaleMode.Gaussian -> R.string.gaussian_sub
        is ImageScaleMode.Sphinx -> R.string.sphinx_sub
        is ImageScaleMode.Bartlett -> R.string.bartlett_sub
        is ImageScaleMode.Robidoux -> R.string.robidoux_sub
        is ImageScaleMode.RobidouxSharp -> R.string.robidoux_sharp_sub
        is ImageScaleMode.Spline16 -> R.string.spline16_sub
        is ImageScaleMode.Spline36 -> R.string.spline36_sub
        is ImageScaleMode.Spline64 -> R.string.spline64_sub
        is ImageScaleMode.Kaiser -> R.string.kaiser_sub
        is ImageScaleMode.BartlettHann -> R.string.bartlett_hann_sub
        is ImageScaleMode.Box -> R.string.box_sub
        is ImageScaleMode.Bohman -> R.string.bohman_sub
        is ImageScaleMode.Lanczos2 -> R.string.lanczos2_sub
        is ImageScaleMode.Lanczos3 -> R.string.lanczos3_sub
        is ImageScaleMode.Lanczos4 -> R.string.lanczos4_sub
        is ImageScaleMode.Lanczos2Jinc -> R.string.lanczos2_jinc_sub
        is ImageScaleMode.Lanczos3Jinc -> R.string.lanczos3_jinc_sub
        is ImageScaleMode.Lanczos4Jinc -> R.string.lanczos4_jinc_sub
        is ImageScaleMode.EwaHanning -> R.string.ewa_hanning_sub
        is ImageScaleMode.EwaRobidoux -> R.string.ewa_robidoux_sub
        is ImageScaleMode.EwaBlackman -> R.string.ewa_blackman_sub
        is ImageScaleMode.EwaQuadric -> R.string.ewa_quadric_sub
        is ImageScaleMode.EwaRobidouxSharp -> R.string.ewa_robidoux_sharp_sub
        is ImageScaleMode.EwaLanczos3Jinc -> R.string.ewa_lanczos3_jinc_sub
        is ImageScaleMode.Ginseng -> R.string.ginseng_sub
        is ImageScaleMode.EwaGinseng -> R.string.ewa_ginseng_sub
        is ImageScaleMode.EwaLanczosSharp -> R.string.ewa_lanczos_sharp_sub
        is ImageScaleMode.EwaLanczos4Sharpest -> R.string.ewa_lanczos_4_sharpest_sub
        is ImageScaleMode.EwaLanczosSoft -> R.string.ewa_lanczos_soft_sub
        is ImageScaleMode.HaasnSoft -> R.string.haasn_soft_sub
        is ImageScaleMode.Lagrange2 -> R.string.lagrange_2_sub
        is ImageScaleMode.Lagrange3 -> R.string.lagrange_3_sub
        is ImageScaleMode.Lanczos6 -> R.string.lanczos_6_sub
        is ImageScaleMode.Lanczos6Jinc -> R.string.lanczos_6_jinc_sub
        is ImageScaleMode.Area -> R.string.area_sub
    }