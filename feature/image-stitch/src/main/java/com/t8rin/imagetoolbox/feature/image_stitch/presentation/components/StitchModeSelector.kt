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

package com.t8rin.imagetoolbox.feature.image_stitch.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BorderBottom
import androidx.compose.material.icons.rounded.BorderLeft
import androidx.compose.material.icons.rounded.BorderRight
import androidx.compose.material.icons.rounded.BorderTop
import androidx.compose.material.icons.rounded.TableRows
import androidx.compose.material.icons.rounded.ViewColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.utils.safeCast
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
            title = stringResource(id = R.string.stitch_mode),
            entries = StitchMode.entries,
            value = value,
            onValueChange = onValueChange,
            itemContent = {
                Text(it.title())
            },
            useClassFinding = true
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
                containerColor = MaterialTheme.colorScheme.surface
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
                containerColor = MaterialTheme.colorScheme.surface
            )
        }
        AnimatedVisibility(
            visible = value is StitchMode.Auto,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column {
                Spacer(Modifier.height(8.dp))
                EnhancedSliderItem(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    value = value.safeCast<StitchMode.Auto>()?.topDrop ?: 0,
                    title = stringResource(R.string.top_drop),
                    sliderModifier = Modifier
                        .padding(
                            top = 14.dp,
                            start = 12.dp,
                            end = 12.dp,
                            bottom = 10.dp
                        ),
                    icon = Icons.Rounded.BorderTop,
                    valueRange = 0f..512f,
                    internalStateTransformation = {
                        it.roundToInt()
                    },
                    onValueChangeFinished = {
                        onValueChange(
                            value.safeCast<StitchMode.Auto>()?.copy(
                                topDrop = it.roundToInt()
                            ) ?: value
                        )
                    },
                    onValueChange = {},
                    shape = ShapeDefaults.top,
                    containerColor = MaterialTheme.colorScheme.surface
                )
                Spacer(Modifier.height(4.dp))
                EnhancedSliderItem(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    value = value.safeCast<StitchMode.Auto>()?.bottomDrop ?: 0,
                    title = stringResource(R.string.bottom_drop),
                    sliderModifier = Modifier
                        .padding(
                            top = 14.dp,
                            start = 12.dp,
                            end = 12.dp,
                            bottom = 10.dp
                        ),
                    icon = Icons.Rounded.BorderBottom,
                    valueRange = 0f..512f,
                    internalStateTransformation = {
                        it.roundToInt()
                    },
                    onValueChangeFinished = {
                        onValueChange(
                            value.safeCast<StitchMode.Auto>()?.copy(
                                bottomDrop = it.roundToInt()
                            ) ?: value
                        )
                    },
                    onValueChange = {},
                    shape = ShapeDefaults.center,
                    containerColor = MaterialTheme.colorScheme.surface
                )
                Spacer(Modifier.height(4.dp))
                EnhancedSliderItem(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    value = value.safeCast<StitchMode.Auto>()?.startDrop ?: 0,
                    title = stringResource(R.string.start_drop),
                    sliderModifier = Modifier
                        .padding(
                            top = 14.dp,
                            start = 12.dp,
                            end = 12.dp,
                            bottom = 10.dp
                        ),
                    icon = Icons.Rounded.BorderLeft,
                    valueRange = 0f..512f,
                    internalStateTransformation = {
                        it.roundToInt()
                    },
                    onValueChangeFinished = {
                        onValueChange(
                            value.safeCast<StitchMode.Auto>()?.copy(
                                startDrop = it.roundToInt()
                            ) ?: value
                        )
                    },
                    onValueChange = {},
                    shape = ShapeDefaults.center,
                    containerColor = MaterialTheme.colorScheme.surface
                )
                Spacer(Modifier.height(4.dp))
                EnhancedSliderItem(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    value = value.safeCast<StitchMode.Auto>()?.endDrop ?: 0,
                    title = stringResource(R.string.end_drop),
                    sliderModifier = Modifier
                        .padding(
                            top = 14.dp,
                            start = 12.dp,
                            end = 12.dp,
                            bottom = 10.dp
                        ),
                    icon = Icons.Rounded.BorderRight,
                    valueRange = 0f..512f,
                    internalStateTransformation = {
                        it.roundToInt()
                    },
                    onValueChangeFinished = {
                        onValueChange(
                            value.safeCast<StitchMode.Auto>()?.copy(
                                endDrop = it.roundToInt()
                            ) ?: value
                        )
                    },
                    onValueChange = {},
                    shape = ShapeDefaults.bottom,
                    containerColor = MaterialTheme.colorScheme.surface
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun StitchMode.title(): String = when (this) {
    is StitchMode.Auto -> stringResource(R.string.auto)
    is StitchMode.Grid.Horizontal -> stringResource(R.string.horizontal_grid)
    is StitchMode.Grid.Vertical -> stringResource(R.string.vertical_grid)
    StitchMode.Horizontal -> stringResource(R.string.horizontal)
    StitchMode.Vertical -> stringResource(R.string.vertical)
}