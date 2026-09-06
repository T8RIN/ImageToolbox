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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.GradientPalette
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Palette
import com.t8rin.imagetoolbox.core.ui.widget.color_picker.GradientColorItem
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.palette_selection.GradientPaletteSelector
import com.t8rin.imagetoolbox.core.ui.widget.saver.GradientPaletteSaver
import kotlin.math.roundToInt

@Composable
fun ColorAndGradientSelector(
    value: Color?,
    gradientPalette: GradientPalette?,
    onValueChange: (Color) -> Unit,
    onGradientPaletteChange: (GradientPalette) -> Unit,
    title: String = stringResource(R.string.background_color),
    modifier: Modifier = Modifier,
    selectionKey: Any? = Unit,
    icon: ImageVector? = Icons.Outlined.Palette,
    onNullClick: (() -> Unit)? = null,
    allowAlpha: Boolean = true,
    gradientAngle: Float = 0f,
    onGradientAngleChange: ((Float) -> Unit)? = null
) {
    var lastPalette by rememberSaveable(selectionKey, stateSaver = GradientPaletteSaver) {
        mutableStateOf(gradientPalette ?: GradientPalette.SoftRainbow)
    }
    LaunchedEffect(gradientPalette) {
        gradientPalette?.let { lastPalette = it }
    }

    Column(modifier = modifier) {
        ColorRowSelector(
            value = value,
            onValueChange = onValueChange,
            title = title,
            icon = icon,
            onNullClick = onNullClick,
            allowAlpha = allowAlpha,
            isColorSelectionVisible = gradientPalette == null,
            isAdditionalItemSelected = gradientPalette != null,
            additionalItem = {
                GradientColorItem(
                    palette = gradientPalette ?: lastPalette,
                    selected = gradientPalette != null,
                    onClick = { onGradientPaletteChange(lastPalette) }
                )
            }
        )
        AnimatedVisibility(visible = gradientPalette != null) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(
                    start = 8.dp, end = 8.dp, bottom = 8.dp
                )
            ) {
                GradientPaletteSelector(
                    value = gradientPalette ?: lastPalette,
                    onValueChange = onGradientPaletteChange,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    shape = ShapeDefaults.top
                )
                if (onGradientAngleChange != null) {
                    EnhancedSliderItem(
                        value = gradientAngle,
                        onValueChange = onGradientAngleChange,
                        valueRange = 0f..360f,
                        title = stringResource(R.string.angle),
                        internalStateTransformation = { it.roundToInt().toFloat() },
                        containerColor = MaterialTheme.colorScheme.surface,
                        shape = ShapeDefaults.bottom
                    )
                }
            }
        }
    }
}