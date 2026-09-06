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

package com.t8rin.imagetoolbox.feature.draw.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.GradientPalette
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.BrushColor
import com.t8rin.imagetoolbox.core.ui.widget.color_picker.ColorSelectionRowDefaults
import com.t8rin.imagetoolbox.core.ui.widget.color_picker.GradientColorItem
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ColorRowSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.palette_selection.GradientPaletteSelector
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import kotlin.math.roundToInt

@Composable
fun DrawColorSelector(
    modifier: Modifier = Modifier
        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
    value: Color,
    onValueChange: (Color) -> Unit,
    allowGradient: Boolean = false,
    gradientPalette: GradientPalette = GradientPalette.SoftRainbow,
    onGradientPaletteChange: (GradientPalette) -> Unit = {},
    gradientLength: Float = 1f,
    onGradientLengthChange: (Float) -> Unit = {},
    isGradientMirrored: Boolean = false,
    onGradientMirroredChange: (Boolean) -> Unit = {},
    isGradientEnabled: Boolean = false,
    onGradientEnabledChange: (Boolean) -> Unit = {},
    color: Color = Color.Unspecified,
    titleText: String = stringResource(R.string.paint_color),
    defaultColors: List<Color> = ColorSelectionRowDefaults.colorList,
) {
    Column(
        modifier = modifier
            .container(
                shape = ShapeDefaults.extraLarge,
                color = color
            ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ColorRowSelector(
            value = value,
            onValueChange = {
                onGradientEnabledChange(false)
                onValueChange(it)
            },
            modifier = Modifier.fillMaxWidth(),
            title = titleText,
            allowAlpha = false,
            icon = Icons.Outlined.BrushColor,
            defaultColors = defaultColors,
            isColorSelectionVisible = !allowGradient || !isGradientEnabled,
            isAdditionalItemSelected = allowGradient && isGradientEnabled,
            additionalItem = if (allowGradient) {
                {
                    GradientColorItem(
                        palette = gradientPalette,
                        selected = isGradientEnabled,
                        onClick = { onGradientEnabledChange(true) }
                    )
                }
            } else null
        )
        AnimatedVisibility(
            visible = allowGradient && isGradientEnabled,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(
                    start = 8.dp, end = 8.dp, bottom = 8.dp
                )
            ) {
                GradientPaletteSelector(
                    value = gradientPalette,
                    onValueChange = onGradientPaletteChange,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    shape = ShapeDefaults.top
                )
                EnhancedSliderItem(
                    value = gradientLength * 100f,
                    onValueChange = { onGradientLengthChange(it.roundToInt() / 100f) },
                    title = stringResource(R.string.gradient_length),
                    valueRange = 10f..400f,
                    valueSuffix = "%",
                    shape = ShapeDefaults.center,
                    internalStateTransformation = { it.roundToInt() },
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                )
                PreferenceRowSwitch(
                    title = stringResource(R.string.gradient_mirror),
                    subtitle = stringResource(R.string.gradient_mirror_sub),
                    checked = isGradientMirrored,
                    onClick = onGradientMirroredChange,
                    containerColor = MaterialTheme.colorScheme.surface,
                    shape = ShapeDefaults.bottom,
                    modifier = Modifier.fillMaxWidth(),
                    resultModifier = Modifier.padding(16.dp),
                    applyHorizontalPadding = false
                )
            }
        }
    }
}
