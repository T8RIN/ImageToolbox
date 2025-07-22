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
import com.t8rin.imagetoolbox.core.filters.domain.model.params.ClaheParams
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem

@Composable
internal fun ClaheParamsItem(
    value: ClaheParams,
    filter: UiFilter<ClaheParams>,
    onFilterChange: (value: ClaheParams) -> Unit,
    previewOnly: Boolean
) {
    val threshold: MutableState<Float> =
        remember(value) { mutableFloatStateOf(value.threshold) }
    val gridSizeHorizontal: MutableState<Float> =
        remember(value) { mutableFloatStateOf(value.gridSizeHorizontal.toFloat()) }
    val gridSizeVertical: MutableState<Float> =
        remember(value) { mutableFloatStateOf(value.gridSizeVertical.toFloat()) }
    val binsCount: MutableState<Float> =
        remember(value) { mutableFloatStateOf(value.binsCount.toFloat()) }

    LaunchedEffect(
        threshold.value,
        gridSizeHorizontal.value,
        gridSizeVertical.value,
        binsCount.value
    ) {
        onFilterChange(
            ClaheParams(
                threshold = threshold.value,
                gridSizeHorizontal = gridSizeHorizontal.value.toInt(),
                gridSizeVertical = gridSizeVertical.value.toInt(),
                binsCount = binsCount.value.toInt()
            )
        )
    }

    val paramsInfo by remember(filter) {
        derivedStateOf {
            filter.paramsInfo.mapIndexedNotNull { index, filterParam ->
                if (filterParam.title == null) return@mapIndexedNotNull null
                when (index) {
                    0 -> threshold
                    1 -> gridSizeHorizontal
                    2 -> gridSizeVertical
                    else -> binsCount
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