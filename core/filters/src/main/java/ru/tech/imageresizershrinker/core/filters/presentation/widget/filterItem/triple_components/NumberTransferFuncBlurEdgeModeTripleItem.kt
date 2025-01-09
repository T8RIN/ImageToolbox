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

package ru.tech.imageresizershrinker.core.filters.presentation.widget.filterItem.triple_components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.domain.utils.roundTo
import ru.tech.imageresizershrinker.core.filters.domain.model.BlurEdgeMode
import ru.tech.imageresizershrinker.core.filters.domain.model.TransferFunc
import ru.tech.imageresizershrinker.core.filters.presentation.model.UiFilter
import ru.tech.imageresizershrinker.core.filters.presentation.widget.filterItem.EdgeModeSelector
import ru.tech.imageresizershrinker.core.filters.presentation.widget.filterItem.TransferFuncSelector
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedSliderItem

@Composable
internal fun NumberTransferFuncBlurEdgeModeTripleItem(
    value: Triple<Number, TransferFunc, BlurEdgeMode>,
    filter: UiFilter<Triple<*, *, *>>,
    onFilterChange: (value: Triple<Number, TransferFunc, BlurEdgeMode>) -> Unit,
    previewOnly: Boolean
) {
    var sliderState1 by remember(value) { mutableFloatStateOf(value.first.toFloat()) }
    var transferFunction by remember(value) { mutableStateOf(value.second) }
    var edgeMode by remember(value) { mutableStateOf(value.third) }

    LaunchedEffect(
        sliderState1,
        transferFunction,
        edgeMode
    ) {
        onFilterChange(
            Triple(sliderState1, transferFunction, edgeMode)
        )
    }

    EnhancedSliderItem(
        modifier = Modifier
            .padding(
                top = 8.dp,
                start = 8.dp,
                end = 8.dp
            ),
        enabled = !previewOnly,
        value = sliderState1,
        title = filter.paramsInfo[0].title?.let {
            stringResource(it)
        } ?: "",
        onValueChange = {
            sliderState1 = it
        },
        internalStateTransformation = {
            it.roundTo(filter.paramsInfo[0].roundTo)
        },
        valueRange = filter.paramsInfo[0].valueRange,
        behaveAsContainer = false
    )
    filter.paramsInfo[1].title?.let { title ->
        TransferFuncSelector(
            title = title,
            value = transferFunction,
            onValueChange = { transferFunction = it }
        )
    }
    filter.paramsInfo[2].title?.let { title ->
        EdgeModeSelector(
            title = title,
            value = edgeMode,
            onValueChange = { edgeMode = it }
        )
    }
}