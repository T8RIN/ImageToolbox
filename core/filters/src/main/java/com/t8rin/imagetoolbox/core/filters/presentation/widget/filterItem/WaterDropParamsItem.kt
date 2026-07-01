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
import com.t8rin.imagetoolbox.core.filters.domain.model.params.WaterDropParams
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem

@Composable
internal fun WaterDropParamsItem(
    value: WaterDropParams,
    filter: UiFilter<WaterDropParams>,
    onFilterChange: (value: WaterDropParams) -> Unit,
    previewOnly: Boolean
) {
    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        WaterDropSlider(value.wavelength, filter.paramsInfo[0], !previewOnly) {
            onFilterChange(value.copy(wavelength = it))
        }
        WaterDropSlider(value.amplitude, filter.paramsInfo[1], !previewOnly) {
            onFilterChange(value.copy(amplitude = it))
        }
        WaterDropSlider(value.phase, filter.paramsInfo[2], !previewOnly) {
            onFilterChange(value.copy(phase = it))
        }
        WaterDropSlider(value.centreX, filter.paramsInfo[3], !previewOnly) {
            onFilterChange(value.copy(centreX = it))
        }
        WaterDropSlider(value.centreY, filter.paramsInfo[4], !previewOnly) {
            onFilterChange(value.copy(centreY = it))
        }
        WaterDropSlider(value.radius, filter.paramsInfo[5], !previewOnly) {
            onFilterChange(value.copy(radius = it))
        }
    }
}

@Composable
private fun WaterDropSlider(
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
