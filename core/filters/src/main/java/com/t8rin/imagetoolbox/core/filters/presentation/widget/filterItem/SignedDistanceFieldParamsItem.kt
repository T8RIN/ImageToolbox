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
import com.t8rin.imagetoolbox.core.filters.domain.model.params.SignedDistanceFieldParams
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import kotlin.math.roundToInt

@Composable
internal fun SignedDistanceFieldParamsItem(
    value: SignedDistanceFieldParams,
    filter: UiFilter<SignedDistanceFieldParams>,
    onFilterChange: (value: SignedDistanceFieldParams) -> Unit,
    previewOnly: Boolean
) {
    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        FilterParamItem(
            value = if (value.alphaMask) 1f else 0f,
            info = filter.paramsInfo[0],
            previewOnly = previewOnly
        ) { onFilterChange(value.copy(alphaMask = it >= 0.5f)) }
        FilterParamItem(
            value = value.threshold,
            info = filter.paramsInfo[1],
            previewOnly = previewOnly
        ) { onFilterChange(value.copy(threshold = it)) }
        FilterParamItem(
            value = value.distance,
            info = filter.paramsInfo[2],
            previewOnly = previewOnly
        ) { onFilterChange(value.copy(distance = it)) }
        FilterParamItem(
            value = value.mode.toFloat(),
            info = filter.paramsInfo[3],
            previewOnly = previewOnly
        ) { onFilterChange(value.copy(mode = it.roundToInt())) }
        FilterParamItem(
            value = value.spacing,
            info = filter.paramsInfo[4],
            previewOnly = previewOnly
        ) { onFilterChange(value.copy(spacing = it)) }
        FilterColorParamItem(
            value = value.insideColor,
            info = filter.paramsInfo[5],
            previewOnly = previewOnly
        ) { onFilterChange(value.copy(insideColor = it)) }
        FilterColorParamItem(
            value = value.outsideColor,
            info = filter.paramsInfo[6],
            previewOnly = previewOnly
        ) { onFilterChange(value.copy(outsideColor = it)) }
        FilterParamItem(
            value = if (value.invert) 1f else 0f,
            info = filter.paramsInfo[7],
            previewOnly = previewOnly
        ) { onFilterChange(value.copy(invert = it >= 0.5f)) }
    }
}