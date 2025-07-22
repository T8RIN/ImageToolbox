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
import com.t8rin.imagetoolbox.core.filters.domain.model.params.ChannelMixParams
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import kotlin.math.roundToInt

@Composable
internal fun ChannelMixParamsItem(
    value: ChannelMixParams,
    filter: UiFilter<ChannelMixParams>,
    onFilterChange: (value: ChannelMixParams) -> Unit,
    previewOnly: Boolean
) {
    val blueGreen: MutableState<Float> =
        remember(value) { mutableFloatStateOf(value.blueGreen.toFloat()) }
    val redBlue: MutableState<Float> =
        remember(value) { mutableFloatStateOf(value.redBlue.toFloat()) }
    val greenRed: MutableState<Float> =
        remember(value) { mutableFloatStateOf(value.greenRed.toFloat()) }
    val intoR: MutableState<Float> =
        remember(value) { mutableFloatStateOf(value.intoR.toFloat()) }
    val intoG: MutableState<Float> =
        remember(value) { mutableFloatStateOf(value.intoG.toFloat()) }
    val intoB: MutableState<Float> =
        remember(value) { mutableFloatStateOf(value.intoB.toFloat()) }

    LaunchedEffect(
        blueGreen.value,
        redBlue.value,
        greenRed.value,
        intoR.value,
        intoG.value,
        intoB.value
    ) {
        onFilterChange(
            ChannelMixParams(
                blueGreen = blueGreen.value.roundToInt(),
                redBlue = redBlue.value.roundToInt(),
                greenRed = greenRed.value.roundToInt(),
                intoR = intoR.value.roundToInt(),
                intoG = intoG.value.roundToInt(),
                intoB = intoB.value.roundToInt(),
            )
        )
    }

    val paramsInfo by remember(filter) {
        derivedStateOf {
            filter.paramsInfo.mapIndexedNotNull { index, filterParam ->
                if (filterParam.title == null) return@mapIndexedNotNull null
                when (index) {
                    0 -> blueGreen
                    1 -> redBlue
                    2 -> greenRed
                    3 -> intoR
                    4 -> intoG
                    else -> intoB
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