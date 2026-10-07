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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.GradientPalette
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Gradient
import com.t8rin.imagetoolbox.core.ui.utils.helper.toModel
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ColorRowSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.DataSelector
import com.t8rin.imagetoolbox.core.ui.widget.modifier.AutoCornersShape
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container

@Composable
fun GradientBackgroundSelector(
    preset: GradientBackgroundPreset?,
    colors: List<Color>,
    onPresetChange: (GradientBackgroundPreset) -> Unit,
    onPaletteChange: (GradientPalette) -> Unit,
    onStartColorChange: (Color) -> Unit,
    onEndColorChange: (Color) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = ShapeDefaults.center,
    lastShape: Shape = shape
) {
    val entries = remember {
        GradientBackgroundPreset.entries.filterNot { it == GradientBackgroundPreset.Custom }
    }
    val palette = remember(colors) {
        GradientPalette.fromColors(colors.map { it.toModel() })
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        DataSelector(
            value = preset ?: GradientBackgroundPreset.Custom,
            onValueChange = onPresetChange,
            entries = entries,
            title = stringResource(R.string.gradient),
            titleIcon = Icons.Outlined.Gradient,
            itemContentText = { entry ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(
                        modifier = Modifier
                            .size(20.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(entry.startColor, entry.endColor)
                                ),
                                shape = ShapeDefaults.extraSmall
                            )
                            .border(
                                width = 0.5.dp,
                                color = MaterialTheme.colorScheme.outlineVariant,
                                shape = AutoCornersShape(3.5.dp)
                            )
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(stringResource(entry.title))
                }
                null
            },
            spanCount = 3,
            badgeContent = { Text(entries.size.toString()) },
            key = GradientBackgroundPreset::name,
            shape = shape
        )
        GradientPaletteSelector(
            value = palette,
            onValueChange = onPaletteChange,
            shape = if (palette is GradientPalette.Custom) shape else lastShape
        )
        AnimatedVisibility(visible = palette is GradientPalette.Custom) {
            ColorRowSelector(
                value = colors.first(),
                onValueChange = onStartColorChange,
                title = stringResource(R.string.code_preview_gradient_start),
                allowAlpha = false,
                modifier = Modifier.container(shape = shape)
            )
        }
        AnimatedVisibility(visible = palette is GradientPalette.Custom) {
            ColorRowSelector(
                value = colors.last(),
                onValueChange = onEndColorChange,
                title = stringResource(R.string.code_preview_gradient_end),
                allowAlpha = false,
                modifier = Modifier.container(shape = lastShape)
            )
        }
    }
}