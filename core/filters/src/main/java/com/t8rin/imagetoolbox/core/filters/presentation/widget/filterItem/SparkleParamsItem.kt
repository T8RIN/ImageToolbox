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
import com.t8rin.imagetoolbox.core.filters.domain.model.params.SparkleParams
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.ui.theme.toColor
import com.t8rin.imagetoolbox.core.ui.widget.color_picker.ColorSelectionRowDefaults
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ColorRowSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import kotlin.math.roundToInt

@Composable
internal fun SparkleParamsItem(
    value: SparkleParams,
    filter: UiFilter<SparkleParams>,
    onFilterChange: (value: SparkleParams) -> Unit,
    previewOnly: Boolean
) {
    val amount: MutableState<Float> =
        remember(value) { mutableFloatStateOf(value.amount.toFloat()) }
    val rays: MutableState<Float> =
        remember(value) { mutableFloatStateOf(value.rays.toFloat()) }
    val radius: MutableState<Float> =
        remember(value) { mutableFloatStateOf(value.radius) }
    val randomness: MutableState<Float> =
        remember(value) { mutableFloatStateOf(value.randomness.toFloat()) }
    val centreX: MutableState<Float> =
        remember(value) { mutableFloatStateOf(value.centreX) }
    val centreY: MutableState<Float> =
        remember(value) { mutableFloatStateOf(value.centreY) }
    val color: MutableState<Float> =
        remember(value) { mutableFloatStateOf(value.color.colorInt.toFloat()) }

    LaunchedEffect(
        amount.value,
        rays.value,
        radius.value,
        randomness.value,
        centreX.value,
        centreY.value,
        color.value
    ) {
        onFilterChange(
            SparkleParams(
                amount = amount.value.toInt(),
                rays = rays.value.toInt(),
                radius = radius.value,
                randomness = randomness.value.toInt(),
                centreX = centreX.value,
                centreY = centreY.value,
                color = color.value.roundToInt().toColorModel()
            )
        )
    }

    val paramsInfo by remember(filter) {
        derivedStateOf {
            filter.paramsInfo.mapIndexedNotNull { index, filterParam ->
                if (filterParam.title == null) return@mapIndexedNotNull null
                when (index) {
                    0 -> amount
                    1 -> rays
                    2 -> radius
                    3 -> randomness
                    4 -> centreX
                    5 -> centreY
                    else -> color
                } to filterParam
            }
        }
    }

    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        paramsInfo.take(6).forEach { (state, info) ->
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
        paramsInfo[6].let { (state, info) ->
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