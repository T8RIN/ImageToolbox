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

package com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.quad_components

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
import com.t8rin.imagetoolbox.core.domain.utils.Quad
import com.t8rin.imagetoolbox.core.domain.utils.roundTo
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem

@Composable
internal fun NumberQuadItem(
    value: Quad<Number, Number, Number, Number>,
    filter: UiFilter<Quad<*, *, *, *>>,
    onFilterChange: (Quad<Number, Number, Number, Number>) -> Unit,
    previewOnly: Boolean
) {
    val sliderState1: MutableState<Float> =
        remember(value) { mutableFloatStateOf(value.first.toFloat()) }
    val sliderState2: MutableState<Float> =
        remember(value) { mutableFloatStateOf(value.second.toFloat()) }
    val sliderState3: MutableState<Float> =
        remember(value) { mutableFloatStateOf(value.third.toFloat()) }
    val sliderState4: MutableState<Float> =
        remember(value) { mutableFloatStateOf(value.fourth.toFloat()) }

    LaunchedEffect(
        sliderState1.value,
        sliderState2.value,
        sliderState3.value,
        sliderState4.value
    ) {
        onFilterChange(
            Quad(
                sliderState1.value,
                sliderState2.value,
                sliderState3.value,
                sliderState4.value
            )
        )
    }

    val paramsInfo by remember(filter) {
        derivedStateOf {
            filter.paramsInfo.mapIndexedNotNull { index, filterParam ->
                if (filterParam.title == null) return@mapIndexedNotNull null
                when (index) {
                    0 -> sliderState1
                    1 -> sliderState2
                    2 -> sliderState3
                    else -> sliderState4
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