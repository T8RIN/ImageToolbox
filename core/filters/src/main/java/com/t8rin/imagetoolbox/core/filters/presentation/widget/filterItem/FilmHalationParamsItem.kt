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
import com.t8rin.imagetoolbox.core.filters.domain.model.params.FilmHalationParams
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter

@Composable
internal fun FilmHalationParamsItem(
    value: FilmHalationParams,
    filter: UiFilter<FilmHalationParams>,
    onFilterChange: (value: FilmHalationParams) -> Unit,
    previewOnly: Boolean
) {
    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        FilterParamItem(
            value = value.threshold,
            info = filter.paramsInfo[0],
            previewOnly = previewOnly
        ) { onFilterChange(value.copy(threshold = it)) }
        FilterParamItem(
            value = value.knee,
            info = filter.paramsInfo[1],
            previewOnly = previewOnly
        ) { onFilterChange(value.copy(knee = it)) }
        FilterParamItem(
            value = value.radius,
            info = filter.paramsInfo[2],
            previewOnly = previewOnly
        ) { onFilterChange(value.copy(radius = it)) }
        FilterParamItem(
            value = value.warmth,
            info = filter.paramsInfo[3],
            previewOnly = previewOnly
        ) { onFilterChange(value.copy(warmth = it)) }
        FilterParamItem(
            value = value.intensity,
            info = filter.paramsInfo[4],
            previewOnly = previewOnly
        ) { onFilterChange(value.copy(intensity = it)) }
    }
}