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
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GuidedSmoothingParams
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import kotlin.math.roundToInt

@Composable
internal fun GuidedSmoothingParamsItem(
    value: GuidedSmoothingParams,
    filter: UiFilter<GuidedSmoothingParams>,
    onFilterChange: (value: GuidedSmoothingParams) -> Unit,
    previewOnly: Boolean
) {
    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        FilterParamItem(
            value = value.radius,
            info = filter.paramsInfo[0],
            previewOnly = previewOnly
        ) { onFilterChange(value.copy(radius = it)) }
        FilterParamItem(
            value = value.epsilon,
            info = filter.paramsInfo[1],
            previewOnly = previewOnly
        ) { onFilterChange(value.copy(epsilon = it)) }
        FilterParamItem(
            value = value.guide.toFloat(),
            info = filter.paramsInfo[2],
            previewOnly = previewOnly
        ) { onFilterChange(value.copy(guide = it.roundToInt())) }
        FilterParamItem(
            value = value.strength,
            info = filter.paramsInfo[3],
            previewOnly = previewOnly
        ) { onFilterChange(value.copy(strength = it)) }
    }
}