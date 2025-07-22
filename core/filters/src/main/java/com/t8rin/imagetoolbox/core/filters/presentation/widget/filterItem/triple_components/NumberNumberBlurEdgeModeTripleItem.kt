/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.triple_components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.utils.roundTo
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.BlurEdgeMode
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.EdgeModeSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem

@Composable
internal fun NumberNumberBlurEdgeModeTripleItem(
    value: Triple<Number, Number, BlurEdgeMode>,
    filter: UiFilter<Triple<*, *, *>>,
    onFilterChange: (value: Triple<Number, Number, BlurEdgeMode>) -> Unit,
    previewOnly: Boolean
) {
    val sliderState1: MutableState<Float> =
        remember(value) { mutableFloatStateOf(value.first.toFloat()) }
    val sliderState2: MutableState<Float> =
        remember(value) { mutableFloatStateOf(value.second.toFloat()) }
    var edgeMode by remember(value) { mutableStateOf(value.third) }

    LaunchedEffect(
        sliderState1.value,
        sliderState2.value,
        edgeMode
    ) {
        onFilterChange(
            Triple(sliderState1.value, sliderState2.value, edgeMode)
        )
    }

    val paramsInfo by remember(filter) {
        derivedStateOf {
            filter.paramsInfo.mapIndexedNotNull { index, filterParam ->
                if (filterParam.title == null || index > 1) return@mapIndexedNotNull null
                when (index) {
                    0 -> sliderState1
                    else -> sliderState2
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
    filter.paramsInfo[2].title?.let { title ->
        EdgeModeSelector(
            title = title,
            value = edgeMode,
            onValueChange = { edgeMode = it }
        )
    }
}