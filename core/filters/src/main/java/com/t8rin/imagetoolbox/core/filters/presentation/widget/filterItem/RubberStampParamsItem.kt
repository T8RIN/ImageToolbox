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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.toColorModel
import com.t8rin.imagetoolbox.core.domain.utils.roundTo
import com.t8rin.imagetoolbox.core.filters.domain.model.params.RubberStampParams
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.ui.theme.toColor
import com.t8rin.imagetoolbox.core.ui.widget.color_picker.ColorSelectionRowDefaults
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ColorRowSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import kotlin.math.roundToInt

@Composable
internal fun RubberStampParamsItem(
    value: RubberStampParams,
    filter: UiFilter<RubberStampParams>,
    onFilterChange: (value: RubberStampParams) -> Unit,
    previewOnly: Boolean
) {
    val threshold: MutableState<Float> =
        remember(value) { mutableFloatStateOf(value.threshold) }
    val softness: MutableState<Float> =
        remember(value) { mutableFloatStateOf(value.softness) }
    val radius: MutableState<Float> =
        remember(value) { mutableFloatStateOf(value.radius) }
    val firstColor: MutableState<Float> =
        remember(value) { mutableFloatStateOf(value.firstColor.colorInt.toFloat()) }
    val secondColor: MutableState<Float> =
        remember(value) { mutableFloatStateOf(value.secondColor.colorInt.toFloat()) }

    LaunchedEffect(
        threshold.value,
        softness.value,
        radius.value,
        firstColor.value,
        secondColor.value,
    ) {
        onFilterChange(
            RubberStampParams(
                threshold = threshold.value,
                softness = softness.value,
                radius = radius.value,
                firstColor = firstColor.value.toInt().toColorModel(),
                secondColor = secondColor.value.toInt().toColorModel(),
            )
        )
    }

    val paramsInfo by remember(filter) {
        derivedStateOf {
            filter.paramsInfo.mapIndexedNotNull { index, filterParam ->
                if (filterParam.title == null) return@mapIndexedNotNull null
                when (index) {
                    0 -> threshold
                    1 -> softness
                    2 -> radius
                    3 -> firstColor
                    else -> secondColor
                } to filterParam
            }
        }
    }

    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        paramsInfo.take(3).forEach { (state, info) ->
            val (title, valueRange, roundTo) = info
            EnhancedSliderItem(
                enabled = !previewOnly,
                value = state.value,
                title = stringResource(title!!),
                valueRange = valueRange,
                steps = if (valueRange == 0f..4f) 3 else 0,
                onValueChange = {
                    state.value = it
                },
                internalStateTransformation = {
                    it.roundTo(roundTo)
                },
                behaveAsContainer = false
            )
        }
        paramsInfo[3].let { (state, info) ->
            ColorRowSelector(
                title = stringResource(info.title!!),
                value = state.value.roundToInt().toColor(),
                onValueChange = {
                    state.value = it.toArgb().toFloat()
                },
                allowScroll = !previewOnly,
                icon = null,
                defaultColors = ColorSelectionRowDefaults.colorList,
                contentHorizontalPadding = 16.dp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
        paramsInfo[4].let { (state, info) ->
            ColorRowSelector(
                title = stringResource(info.title!!),
                value = state.value.roundToInt().toColor(),
                onValueChange = {
                    state.value = it.toArgb().toFloat()
                },
                allowScroll = !previewOnly,
                icon = null,
                defaultColors = ColorSelectionRowDefaults.colorList,
                contentHorizontalPadding = 16.dp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}