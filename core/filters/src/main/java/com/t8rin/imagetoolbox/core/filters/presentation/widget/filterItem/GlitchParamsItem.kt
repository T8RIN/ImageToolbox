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
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GlitchParams
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem

@Composable
internal fun GlitchParamsItem(
    value: GlitchParams,
    filter: UiFilter<GlitchParams>,
    onFilterChange: (value: GlitchParams) -> Unit,
    previewOnly: Boolean
) {
    val channelsShiftX: MutableState<Float> =
        remember(value) { mutableFloatStateOf((value.channelsShiftX as Number).toFloat()) }
    val channelsShiftY: MutableState<Float> =
        remember(value) { mutableFloatStateOf((value.channelsShiftY as Number).toFloat()) }
    val corruptionSize: MutableState<Float> =
        remember(value) { mutableFloatStateOf((value.corruptionSize as Number).toFloat()) }
    val corruptionCount: MutableState<Float> =
        remember(value) { mutableFloatStateOf((value.corruptionCount as Number).toFloat()) }
    val corruptionShiftX: MutableState<Float> =
        remember(value) { mutableFloatStateOf((value.corruptionShiftX as Number).toFloat()) }
    val corruptionShiftY: MutableState<Float> =
        remember(value) { mutableFloatStateOf((value.corruptionShiftY as Number).toFloat()) }

    LaunchedEffect(
        channelsShiftX.value,
        channelsShiftY.value,
        corruptionSize.value,
        corruptionCount.value,
        corruptionShiftX.value,
        corruptionShiftY.value
    ) {
        onFilterChange(
            GlitchParams(
                channelsShiftX = channelsShiftX.value,
                channelsShiftY = channelsShiftY.value,
                corruptionSize = corruptionSize.value,
                corruptionCount = corruptionCount.value.toInt(),
                corruptionShiftX = corruptionShiftX.value,
                corruptionShiftY = corruptionShiftY.value
            )
        )
    }

    val paramsInfo by remember(filter) {
        derivedStateOf {
            filter.paramsInfo.mapIndexedNotNull { index, filterParam ->
                if (filterParam.title == null) return@mapIndexedNotNull null
                when (index) {
                    0 -> channelsShiftX
                    1 -> channelsShiftY
                    2 -> corruptionSize
                    3 -> corruptionCount
                    4 -> corruptionShiftX
                    else -> corruptionShiftY
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