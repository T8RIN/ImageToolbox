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

package com.t8rin.imagetoolbox.image_cutting.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BorderHorizontal
import androidx.compose.material.icons.rounded.BorderVertical
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.utils.roundTo
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.SelectInverse
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedRangeSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.image_cutting.domain.CutParams
import com.t8rin.imagetoolbox.image_cutting.domain.PivotPair

@Composable
internal fun CutParamsSelector(
    value: CutParams,
    onValueChange: (CutParams) -> Unit
) {
    val params by rememberUpdatedState(value)
    val layoutDirection = LocalLayoutDirection.current

    Column {
        EnhancedRangeSliderItem(
            value = params.vertical?.let { it.start..it.end } ?: 0f..1f,
            valueRange = 0f..1f,
            icon = Icons.Rounded.BorderVertical,
            title = stringResource(R.string.vertical_pivot_line),
            internalStateTransformation = {
                it.start.roundTo(3)..it.endInclusive.roundTo(3)
            },
            onValueChange = {
                onValueChange(
                    params.copy(
                        vertical = PivotPair(
                            start = it.start,
                            end = it.endInclusive,
                            isRtl = layoutDirection == LayoutDirection.Rtl
                        )
                    )
                )
            },
            additionalContent = {
                PreferenceRowSwitch(
                    title = stringResource(R.string.inverse_selection),
                    subtitle = stringResource(R.string.inverse_vertical_selection_sub),
                    startIcon = Icons.Rounded.SelectInverse,
                    checked = params.inverseVertical,
                    onClick = {
                        onValueChange(
                            params.copy(
                                inverseVertical = it
                            )
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.surface,
                    shape = ShapeDefaults.small,
                    modifier = Modifier.padding(
                        start = 4.dp,
                        end = 4.dp,
                        bottom = 4.dp
                    )
                )
            }
        )
        Spacer(Modifier.height(8.dp))
        EnhancedRangeSliderItem(
            value = params.horizontal?.let { it.start..it.end } ?: 0f..1f,
            valueRange = 0f..1f,
            icon = Icons.Rounded.BorderHorizontal,
            title = stringResource(R.string.horizontal_pivot_line),
            internalStateTransformation = {
                it.start.roundTo(3)..it.endInclusive.roundTo(3)
            },
            onValueChange = {
                onValueChange(
                    params.copy(
                        horizontal = PivotPair(
                            start = it.start,
                            end = it.endInclusive,
                            isRtl = layoutDirection == LayoutDirection.Rtl
                        )
                    )
                )
            },
            additionalContent = {
                PreferenceRowSwitch(
                    title = stringResource(R.string.inverse_selection),
                    subtitle = stringResource(R.string.inverse_horizontal_selection_sub),
                    startIcon = Icons.Rounded.SelectInverse,
                    checked = params.inverseHorizontal,
                    onClick = {
                        onValueChange(
                            params.copy(
                                inverseHorizontal = it
                            )
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.surface,
                    shape = ShapeDefaults.small,
                    modifier = Modifier.padding(
                        start = 4.dp,
                        end = 4.dp,
                        bottom = 4.dp
                    )
                )
            }
        )
    }
}