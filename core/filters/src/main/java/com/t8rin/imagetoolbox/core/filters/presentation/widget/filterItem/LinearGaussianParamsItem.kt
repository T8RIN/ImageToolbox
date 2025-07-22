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
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.BlurEdgeMode
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.TransferFunc
import com.t8rin.imagetoolbox.core.filters.domain.model.params.LinearGaussianParams
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem

@Composable
internal fun LinearGaussianParamsItem(
    value: LinearGaussianParams,
    filter: UiFilter<LinearGaussianParams>,
    onFilterChange: (value: LinearGaussianParams) -> Unit,
    previewOnly: Boolean
) {
    val kernelSize: MutableState<Float> =
        remember(value) { mutableFloatStateOf(value.kernelSize.toFloat()) }
    val sigma: MutableState<Float> =
        remember(value) { mutableFloatStateOf(value.sigma) }
    val edgeMode: MutableState<Float> =
        remember(value) { mutableFloatStateOf(value.edgeMode.ordinal.toFloat()) }
    val transferFunction: MutableState<Float> =
        remember(value) { mutableFloatStateOf(value.transferFunction.ordinal.toFloat()) }

    LaunchedEffect(
        kernelSize.value,
        sigma.value,
        edgeMode.value,
        transferFunction.value
    ) {
        onFilterChange(
            LinearGaussianParams(
                kernelSize = kernelSize.value.toInt(),
                sigma = sigma.value,
                edgeMode = BlurEdgeMode.entries[edgeMode.value.toInt()],
                transferFunction = TransferFunc.entries[transferFunction.value.toInt()]
            )
        )
    }

    val paramsInfo by remember(filter) {
        derivedStateOf {
            filter.paramsInfo.mapIndexedNotNull { index, filterParam ->
                if (filterParam.title == null) return@mapIndexedNotNull null
                when (index) {
                    0 -> kernelSize
                    1 -> sigma
                    2 -> edgeMode
                    else -> transferFunction
                } to filterParam
            }
        }
    }

    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        paramsInfo.take(2).forEach { (state, info) ->
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
        paramsInfo[2].let { (state, info) ->
            EdgeModeSelector(
                title = info.title!!,
                value = BlurEdgeMode.entries[state.value.toInt()],
                onValueChange = { state.value = it.ordinal.toFloat() }
            )
        }
        paramsInfo[3].let { (state, info) ->
            TransferFuncSelector(
                title = info.title!!,
                value = TransferFunc.entries[state.value.toInt()],
                onValueChange = { state.value = it.ordinal.toFloat() }
            )
        }
    }
}