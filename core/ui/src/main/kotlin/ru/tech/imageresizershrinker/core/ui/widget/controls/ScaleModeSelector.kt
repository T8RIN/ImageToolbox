/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.core.ui.widget.controls

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.core.domain.image.model.ImageScaleMode
import ru.tech.imageresizershrinker.core.domain.image.model.ScaleColorSpace
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedChip
import ru.tech.imageresizershrinker.core.ui.widget.buttons.SupportingButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ToggleGroupButton
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ScaleModeSelector(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Unspecified,
    shape: Shape = RoundedCornerShape(24.dp),
    enableItemsCardBackground: Boolean = true,
    value: ImageScaleMode,
    titlePadding: PaddingValues = PaddingValues(top = 8.dp),
    titleArrangement: Arrangement.Horizontal = Arrangement.Center,
    onValueChange: (ImageScaleMode) -> Unit,
    entries: List<ImageScaleMode> = remember {
        ImageScaleMode.entries
    },
    title: @Composable RowScope.() -> Unit = {
        Text(
            text = stringResource(R.string.scale_mode),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )
    }
) {
    val isColorSpaceSelectionVisible = enableItemsCardBackground && value !is ImageScaleMode.Base
    val showInfoSheet = rememberSaveable { mutableStateOf(false) }
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
                    showInfoSheet.value = true
                }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            verticalArrangement = Arrangement.spacedBy(
                8.dp,
                Alignment.CenterVertically
            ),
            horizontalArrangement = Arrangement.spacedBy(
                8.dp,
                Alignment.CenterHorizontally
            ),
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (enableItemsCardBackground) {
                        Modifier
                            .padding(horizontal = 8.dp)
                            .container(
                                color = MaterialTheme.colorScheme.surface,
                                shape = if (isColorSpaceSelectionVisible) ContainerShapeDefaults.topShape
                                else ContainerShapeDefaults.defaultShape
                            )
                            .padding(horizontal = 8.dp, vertical = 12.dp)
                    } else Modifier.padding(8.dp)
                )
        ) {
            entries.forEach {
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
                        Text(text = it.title)
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

        if (isColorSpaceSelectionVisible) {
            Spacer(modifier = Modifier.height(4.dp))
            val items = remember {
                ScaleColorSpace.entries
            }
            ToggleGroupButton(
                enabled = true,
                itemCount = items.size,
                inactiveButtonColor = MaterialTheme.colorScheme.surfaceContainer,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .container(
                        color = MaterialTheme.colorScheme.surface,
                        shape = ContainerShapeDefaults.bottomShape
                    ),
                title = {
                    Text(
                        modifier = Modifier.padding(top = 8.dp),
                        text = stringResource(R.string.scale_color_space),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                },
                selectedIndex = items.indexOfFirst {
                    value.scaleColorSpace::class.isInstance(it)
                },
                buttonIcon = {},
                itemContent = {
                    Text(items[it].title)
                },
                indexChanged = {
                    onValueChange(
                        value.copy(items[it])
                    )
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    SimpleSheet(
        sheetContent = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                entries.forEachIndexed { index, item ->
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .container(
                                shape = ContainerShapeDefaults.shapeForIndex(
                                    index = index,
                                    size = entries.size
                                ),
                                resultPadding = 0.dp
                            )
                    ) {
                        TitleItem(text = item.title)
                        Text(
                            text = item.subtitle,
                            modifier = Modifier.padding(
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 16.dp
                            ),
                            fontSize = 14.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        },
        visible = showInfoSheet,
        title = {
            TitleItem(text = stringResource(R.string.scale_mode))
        },
        confirmButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = { showInfoSheet.value = false }
            ) {
                AutoSizeText(stringResource(R.string.close))
            }
        }
    )
}

private val ScaleColorSpace.title: String
    @Composable
    get() = when (this) {
        is ScaleColorSpace.Linear -> stringResource(R.string.linear)
        is ScaleColorSpace.SRGB -> "SRGB"
        is ScaleColorSpace.LAB -> "LAB"
        is ScaleColorSpace.LUV -> "LUV"
    }

private val ImageScaleMode.title: String
    @Composable
    get() = when (this) {
        ImageScaleMode.Base,
        ImageScaleMode.NotPresent -> stringResource(id = R.string.basic)

        is ImageScaleMode.Bilinear -> stringResource(id = R.string.bilinear)
        is ImageScaleMode.Nearest -> stringResource(id = R.string.nearest)
        is ImageScaleMode.Cubic -> stringResource(id = R.string.cubic)
        is ImageScaleMode.Mitchell -> stringResource(id = R.string.mitchell)
        is ImageScaleMode.Catmull -> stringResource(id = R.string.catmull)
        is ImageScaleMode.Hermite -> stringResource(id = R.string.hermite)
        is ImageScaleMode.BSpline -> stringResource(id = R.string.bspline)
        is ImageScaleMode.Hann -> stringResource(id = R.string.hann)
        is ImageScaleMode.Bicubic -> stringResource(id = R.string.bicubic)
        is ImageScaleMode.Hamming -> stringResource(id = R.string.hamming)
        is ImageScaleMode.Hanning -> stringResource(id = R.string.hanning)
        is ImageScaleMode.Blackman -> stringResource(id = R.string.blackman)
        is ImageScaleMode.Welch -> stringResource(id = R.string.welch)
        is ImageScaleMode.Quadric -> stringResource(id = R.string.quadric)
        is ImageScaleMode.Gaussian -> stringResource(id = R.string.gaussian)
        is ImageScaleMode.Sphinx -> stringResource(id = R.string.sphinx)
        is ImageScaleMode.Bartlett -> stringResource(id = R.string.bartlett)
        is ImageScaleMode.Robidoux -> stringResource(id = R.string.robidoux)
        is ImageScaleMode.RobidouxSharp -> stringResource(id = R.string.robidoux_sharp)
        is ImageScaleMode.Spline16 -> stringResource(id = R.string.spline16)
        is ImageScaleMode.Spline36 -> stringResource(id = R.string.spline36)
        is ImageScaleMode.Spline64 -> stringResource(id = R.string.spline64)
        is ImageScaleMode.Kaiser -> stringResource(id = R.string.kaiser)
        is ImageScaleMode.BartlettHann -> stringResource(id = R.string.bartlett_hann)
        is ImageScaleMode.Box -> stringResource(id = R.string.box)
        is ImageScaleMode.Bohman -> stringResource(id = R.string.bohman)
        is ImageScaleMode.Lanczos2 -> stringResource(id = R.string.lanczos2)
        is ImageScaleMode.Lanczos3 -> stringResource(id = R.string.lanczos3)
        is ImageScaleMode.Lanczos4 -> stringResource(id = R.string.lanczos4)
        is ImageScaleMode.Lanczos2Jinc -> stringResource(id = R.string.lanczos2_jinc)
        is ImageScaleMode.Lanczos3Jinc -> stringResource(id = R.string.lanczos3_jinc)
        is ImageScaleMode.Lanczos4Jinc -> stringResource(id = R.string.lanczos4_jinc)
    }

private val ImageScaleMode.subtitle: String
    @Composable
    get() = when (this) {
        ImageScaleMode.Base,
        ImageScaleMode.NotPresent -> stringResource(id = R.string.basic_sub)

        is ImageScaleMode.Bilinear -> stringResource(id = R.string.bilinear_sub)
        is ImageScaleMode.Nearest -> stringResource(id = R.string.nearest_sub)
        is ImageScaleMode.Cubic -> stringResource(id = R.string.cubic_sub)
        is ImageScaleMode.Mitchell -> stringResource(id = R.string.mitchell_sub)
        is ImageScaleMode.Catmull -> stringResource(id = R.string.catmull_sub)
        is ImageScaleMode.Hermite -> stringResource(id = R.string.hermite_sub)
        is ImageScaleMode.BSpline -> stringResource(id = R.string.bspline_sub)
        is ImageScaleMode.Hann -> stringResource(id = R.string.hann_sub)
        is ImageScaleMode.Bicubic -> stringResource(id = R.string.bicubic_sub)
        is ImageScaleMode.Hamming -> stringResource(id = R.string.hamming_sub)
        is ImageScaleMode.Hanning -> stringResource(id = R.string.hanning_sub)
        is ImageScaleMode.Blackman -> stringResource(id = R.string.blackman_sub)
        is ImageScaleMode.Welch -> stringResource(id = R.string.welch_sub)
        is ImageScaleMode.Quadric -> stringResource(id = R.string.quadric_sub)
        is ImageScaleMode.Gaussian -> stringResource(id = R.string.gaussian_sub)
        is ImageScaleMode.Sphinx -> stringResource(id = R.string.sphinx_sub)
        is ImageScaleMode.Bartlett -> stringResource(id = R.string.bartlett_sub)
        is ImageScaleMode.Robidoux -> stringResource(id = R.string.robidoux_sub)
        is ImageScaleMode.RobidouxSharp -> stringResource(id = R.string.robidoux_sharp_sub)
        is ImageScaleMode.Spline16 -> stringResource(id = R.string.spline16_sub)
        is ImageScaleMode.Spline36 -> stringResource(id = R.string.spline36_sub)
        is ImageScaleMode.Spline64 -> stringResource(id = R.string.spline64_sub)
        is ImageScaleMode.Kaiser -> stringResource(id = R.string.kaiser_sub)
        is ImageScaleMode.BartlettHann -> stringResource(id = R.string.bartlett_hann_sub)
        is ImageScaleMode.Box -> stringResource(id = R.string.box_sub)
        is ImageScaleMode.Bohman -> stringResource(id = R.string.bohman_sub)
        is ImageScaleMode.Lanczos2 -> stringResource(id = R.string.lanczos2_sub)
        is ImageScaleMode.Lanczos3 -> stringResource(id = R.string.lanczos3_sub)
        is ImageScaleMode.Lanczos4 -> stringResource(id = R.string.lanczos4_sub)
        is ImageScaleMode.Lanczos2Jinc -> stringResource(id = R.string.lanczos2_jinc_sub)
        is ImageScaleMode.Lanczos3Jinc -> stringResource(id = R.string.lanczos3_jinc_sub)
        is ImageScaleMode.Lanczos4Jinc -> stringResource(id = R.string.lanczos4_jinc_sub)
    }