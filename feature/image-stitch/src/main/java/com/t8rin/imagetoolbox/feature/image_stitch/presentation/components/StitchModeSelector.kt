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

package com.t8rin.imagetoolbox.feature.image_stitch.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.TableRows
import androidx.compose.material.icons.rounded.ViewColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.feature.image_stitch.domain.StitchMode
import kotlin.math.roundToInt

@Composable
fun StitchModeSelector(
    modifier: Modifier = Modifier,
    value: StitchMode,
    onValueChange: (StitchMode) -> Unit
) {
    Column(
        modifier = modifier
            .container(shape = ShapeDefaults.extraLarge)
    ) {
        EnhancedButtonGroup(
            modifier = Modifier.padding(start = 3.dp, end = 2.dp),
            enabled = true,
            title = {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(stringResource(id = R.string.stitch_mode))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            },
            items = listOf(
                stringResource(R.string.horizontal),
                stringResource(R.string.vertical),
                stringResource(R.string.horizontal_grid),
                stringResource(R.string.vertical_grid)
            ),
            selectedIndex = when (value) {
                StitchMode.Horizontal -> 0
                StitchMode.Vertical -> 1
                is StitchMode.Grid.Horizontal -> 2
                is StitchMode.Grid.Vertical -> 3
            },
            onIndexChange = {
                onValueChange(
                    when (it) {
                        0 -> StitchMode.Horizontal
                        1 -> StitchMode.Vertical
                        2 -> StitchMode.Grid.Horizontal()
                        3 -> StitchMode.Grid.Vertical()
                        else -> StitchMode.Horizontal
                    }
                )
            }
        )
        AnimatedVisibility(
            visible = value is StitchMode.Grid.Horizontal,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            EnhancedSliderItem(
                modifier = Modifier.padding(8.dp),
                value = value.gridCellsCount(),
                title = stringResource(R.string.rows_count),
                sliderModifier = Modifier
                    .padding(
                        top = 14.dp,
                        start = 12.dp,
                        end = 12.dp,
                        bottom = 10.dp
                    ),
                icon = Icons.Rounded.TableRows,
                valueRange = 2f..6f,
                steps = 3,
                internalStateTransformation = {
                    it.roundToInt()
                },
                onValueChangeFinished = {
                    onValueChange(
                        StitchMode.Grid.Horizontal(it.roundToInt())
                    )
                },
                onValueChange = {},
                shape = ShapeDefaults.default,
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        }
        AnimatedVisibility(
            visible = value is StitchMode.Grid.Vertical,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            EnhancedSliderItem(
                modifier = Modifier.padding(8.dp),
                value = value.gridCellsCount(),
                title = stringResource(R.string.columns_count),
                sliderModifier = Modifier
                    .padding(
                        top = 14.dp,
                        start = 12.dp,
                        end = 12.dp,
                        bottom = 10.dp
                    ),
                icon = Icons.Rounded.ViewColumn,
                valueRange = 2f..6f,
                steps = 3,
                internalStateTransformation = {
                    it.roundToInt()
                },
                onValueChangeFinished = {
                    onValueChange(
                        StitchMode.Grid.Vertical(it.roundToInt())
                    )
                },
                onValueChange = {},
                shape = ShapeDefaults.default,
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        }
    }
}