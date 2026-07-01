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
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterParam
import com.t8rin.imagetoolbox.core.filters.domain.model.params.FlareParams
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.ui.utils.helper.toColor
import com.t8rin.imagetoolbox.core.ui.utils.helper.toModel
import com.t8rin.imagetoolbox.core.ui.widget.color_picker.ColorSelectionRow
import com.t8rin.imagetoolbox.core.ui.widget.color_picker.ColorSelectionRowDefaults
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem

@Composable
internal fun FlareParamsItem(
    value: FlareParams,
    filter: UiFilter<FlareParams>,
    onFilterChange: (value: FlareParams) -> Unit,
    previewOnly: Boolean
) {
    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        FlareSlider(value.radius, filter.paramsInfo[0], !previewOnly) {
            onFilterChange(value.copy(radius = it))
        }
        FlareSlider(value.baseAmount, filter.paramsInfo[1], !previewOnly) {
            onFilterChange(value.copy(baseAmount = it))
        }
        FlareSlider(value.ringAmount, filter.paramsInfo[2], !previewOnly) {
            onFilterChange(value.copy(ringAmount = it))
        }
        FlareSlider(value.rayAmount, filter.paramsInfo[3], !previewOnly) {
            onFilterChange(value.copy(rayAmount = it))
        }
        FlareSlider(value.ringWidth, filter.paramsInfo[4], !previewOnly) {
            onFilterChange(value.copy(ringWidth = it))
        }
        FlareSlider(value.centreX, filter.paramsInfo[5], !previewOnly) {
            onFilterChange(value.copy(centreX = it))
        }
        FlareSlider(value.centreY, filter.paramsInfo[6], !previewOnly) {
            onFilterChange(value.copy(centreY = it))
        }
        ColorSelectionRow(
            value = value.color.toColor(),
            defaultColors = ColorSelectionRowDefaults.colorList,
            allowScroll = !previewOnly,
            onValueChange = {
                onFilterChange(value.copy(color = it.toModel()))
            },
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

@Composable
private fun FlareSlider(
    value: Float,
    info: FilterParam,
    enabled: Boolean,
    onValueChange: (Float) -> Unit
) {
    EnhancedSliderItem(
        enabled = enabled,
        value = value,
        title = stringResource(info.title!!),
        valueRange = info.valueRange,
        onValueChange = onValueChange,
        internalStateTransformation = { it.roundTo(info.roundTo) },
        behaveAsContainer = false
    )
}
