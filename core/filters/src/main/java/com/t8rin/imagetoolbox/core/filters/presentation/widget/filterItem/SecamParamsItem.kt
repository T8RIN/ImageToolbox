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
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.filters.domain.model.params.SecamParams
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import kotlin.math.roundToInt

@Composable
internal fun SecamParamsItem(
    value: SecamParams,
    filter: UiFilter<SecamParams>,
    onFilterChange: (value: SecamParams) -> Unit,
    previewOnly: Boolean
) {
    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        FilterParamItem(
            value = value.chromaRadius.toFloat(),
            info = filter.paramsInfo[0],
            previewOnly = previewOnly
        ) { onFilterChange(value.copy(chromaRadius = it.roundToInt())) }
        FilterParamItem(
            value = value.frequencyDeviation,
            info = filter.paramsInfo[1],
            previewOnly = previewOnly
        ) { onFilterChange(value.copy(frequencyDeviation = it)) }
        FilterParamItem(
            value = value.noise,
            info = filter.paramsInfo[2],
            previewOnly = previewOnly
        ) { onFilterChange(value.copy(noise = it)) }
        FilterParamItem(
            value = value.preEmphasis,
            info = filter.paramsInfo[3],
            previewOnly = previewOnly
        ) { onFilterChange(value.copy(preEmphasis = it)) }
        FilterParamItem(
            value = value.lineCrosstalk,
            info = filter.paramsInfo[4],
            previewOnly = previewOnly
        ) { onFilterChange(value.copy(lineCrosstalk = it)) }
        FilterParamItem(
            value = value.seed.toFloat(),
            info = filter.paramsInfo[5],
            previewOnly = previewOnly
        ) { onFilterChange(value.copy(seed = it.roundToInt())) }
        FilterParamItem(
            value = value.strength,
            info = filter.paramsInfo[6],
            previewOnly = previewOnly
        ) { onFilterChange(value.copy(strength = it)) }
    }
}