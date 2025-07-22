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

package com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.utils.roundTo
import com.t8rin.imagetoolbox.core.filters.domain.model.params.WaterParams
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem

@Composable
internal fun WaterParamsItem(
    value: WaterParams,
    filter: UiFilter<WaterParams>,
    onFilterChange: (value: WaterParams) -> Unit,
    previewOnly: Boolean
) {
    val fractionSize: MutableState<Float> =
        remember(value) { mutableFloatStateOf(value.fractionSize) }
    val frequencyX: MutableState<Float> =
        remember(value) { mutableFloatStateOf(value.frequencyX) }
    val frequencyY: MutableState<Float> =
        remember(value) { mutableFloatStateOf(value.frequencyY) }
    val amplitudeX: MutableState<Float> =
        remember(value) { mutableFloatStateOf(value.amplitudeX) }
    val amplitudeY: MutableState<Float> =
        remember(value) { mutableFloatStateOf(value.amplitudeY) }

    LaunchedEffect(
        fractionSize.value,
        frequencyX.value,
        frequencyY.value,
        amplitudeX.value,
        amplitudeY.value
    ) {
        onFilterChange(
            WaterParams(
                fractionSize = fractionSize.value,
                frequencyX = frequencyX.value,
                frequencyY = frequencyY.value,
                amplitudeX = amplitudeX.value,
                amplitudeY = amplitudeY.value
            )
        )
    }

    val paramsInfo by remember(filter) {
        derivedStateOf {
            filter.paramsInfo.mapIndexedNotNull { index, filterParam ->
                if (filterParam.title == null) return@mapIndexedNotNull null
                when (index) {
                    0 -> fractionSize
                    1 -> frequencyX
                    2 -> frequencyY
                    3 -> amplitudeX
                    else -> amplitudeY
                } to filterParam
            }
        }
    }

    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        paramsInfo.forEach { (state, info) ->
            val (title, valueRange, roundTo) = info
            EnhancedSliderItem(
                enabled = !previewOnly,
                value = state.value,
                title = stringResource(title!!),
                valueRange = valueRange,
                onValueChange = {
                    state.value = it
                },
                internalStateTransformation = {
                    it.roundTo(roundTo)
                },
                behaveAsContainer = false
            )
        }
    }
}