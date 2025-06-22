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

package com.t8rin.imagetoolbox.core.ui.widget.controls.selection

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.TableRows
import androidx.compose.material.icons.outlined.ViewColumn
import androidx.compose.material.icons.rounded.GridOn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.extendedcolors.util.roundToTwoDigits
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.theme.toColor
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.modifier.HelperGridParams
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch

@Composable
fun HelperGridParamsSelector(
    value: HelperGridParams,
    onValueChange: (HelperGridParams) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = ShapeDefaults.extraLarge,
) {
    Column(
        modifier = modifier.container(
            shape = shape,
            resultPadding = 0.dp
        )
    ) {
        PreferenceRowSwitch(
            modifier = Modifier.clip(shape),
            startIcon = Icons.Rounded.GridOn,
            drawContainer = false,
            checked = value.enabled,
            onClick = {
                onValueChange(value.copy(enabled = it))
            },
            title = stringResource(R.string.helper_grid),
            subtitle = stringResource(R.string.helper_grid_sub)
        )
        AnimatedVisibility(value.enabled) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Spacer(Modifier.height(4.dp))
                ColorRowSelector(
                    value = value.color.toColor(),
                    onValueChange = {
                        onValueChange(value.copy(color = it.toArgb()))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .container(
                            shape = ShapeDefaults.top,
                            color = MaterialTheme.colorScheme.surface,
                            resultPadding = 0.dp
                        )
                        .padding(start = 4.dp),
                    icon = Icons.Outlined.ColorLens,
                    title = stringResource(R.string.grid_color)
                )
                EnhancedSliderItem(
                    value = value.cellWidth,
                    title = stringResource(R.string.cell_width),
                    icon = Icons.Outlined.ViewColumn,
                    internalStateTransformation = {
                        it.roundToTwoDigits()
                    },
                    valueSuffix = " Pt",
                    shape = ShapeDefaults.center,
                    onValueChange = {
                        onValueChange(value.copy(cellWidth = it))
                    },
                    valueRange = 1f..100f,
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                EnhancedSliderItem(
                    value = value.cellHeight,
                    title = stringResource(R.string.cell_height),
                    icon = Icons.Outlined.TableRows,
                    internalStateTransformation = {
                        it.roundToTwoDigits()
                    },
                    valueSuffix = " Pt",
                    shape = ShapeDefaults.bottom,
                    onValueChange = {
                        onValueChange(value.copy(cellHeight = it))
                    },
                    valueRange = 1f..100f,
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}