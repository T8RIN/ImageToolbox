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

package com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.utils.roundTo
import com.t8rin.imagetoolbox.core.filters.domain.model.params.TornEdgeParams
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import kotlin.math.roundToInt

@Composable
internal fun TornEdgeParamsItem(
    value: TornEdgeParams,
    filter: UiFilter<TornEdgeParams>,
    onFilterChange: (value: TornEdgeParams) -> Unit,
    previewOnly: Boolean
) {
    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        TornEdgeSlider(
            value = value.toothHeight.toFloat(),
            title = filter.paramsInfo[0].title!!,
            valueRange = filter.paramsInfo[0].valueRange,
            enabled = !previewOnly,
            onValueChange = {
                onFilterChange(value.copy(toothHeight = it.roundToInt()))
            }
        )
        TornEdgeSlider(
            value = value.horizontalToothRange.toFloat(),
            title = filter.paramsInfo[1].title!!,
            valueRange = filter.paramsInfo[1].valueRange,
            enabled = !previewOnly,
            onValueChange = {
                onFilterChange(value.copy(horizontalToothRange = it.roundToInt()))
            }
        )
        TornEdgeSlider(
            value = value.verticalToothRange.toFloat(),
            title = filter.paramsInfo[2].title!!,
            valueRange = filter.paramsInfo[2].valueRange,
            enabled = !previewOnly,
            onValueChange = {
                onFilterChange(value.copy(verticalToothRange = it.roundToInt()))
            }
        )
        TornEdgeSwitch(
            title = R.string.top_edge,
            checked = value.top,
            enabled = !previewOnly,
            onCheckedChange = {
                onFilterChange(value.copy(top = it))
            }
        )
        TornEdgeSwitch(
            title = R.string.right_edge,
            checked = value.right,
            enabled = !previewOnly,
            onCheckedChange = {
                onFilterChange(value.copy(right = it))
            }
        )
        TornEdgeSwitch(
            title = R.string.bottom_edge,
            checked = value.bottom,
            enabled = !previewOnly,
            onCheckedChange = {
                onFilterChange(value.copy(bottom = it))
            }
        )
        TornEdgeSwitch(
            title = R.string.left_edge,
            checked = value.left,
            enabled = !previewOnly,
            onCheckedChange = {
                onFilterChange(value.copy(left = it))
            }
        )
    }
}

@Composable
private fun TornEdgeSlider(
    value: Float,
    title: Int,
    valueRange: ClosedFloatingPointRange<Float>,
    enabled: Boolean,
    onValueChange: (Float) -> Unit
) {
    EnhancedSliderItem(
        enabled = enabled,
        value = value,
        title = stringResource(title),
        valueRange = valueRange,
        onValueChange = onValueChange,
        internalStateTransformation = { it.roundTo(0) },
        behaveAsContainer = false
    )
}

@Composable
private fun TornEdgeSwitch(
    title: Int,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    PreferenceRowSwitch(
        title = stringResource(title),
        checked = checked,
        onClick = onCheckedChange,
        modifier = Modifier.padding(
            top = 8.dp,
            start = 4.dp,
            end = 4.dp
        ),
        applyHorizontalPadding = false,
        startContent = {},
        resultModifier = Modifier.padding(16.dp),
        enabled = enabled
    )
}
