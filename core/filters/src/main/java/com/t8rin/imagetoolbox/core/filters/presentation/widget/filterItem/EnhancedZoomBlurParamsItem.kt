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
import com.t8rin.imagetoolbox.core.filters.domain.model.params.EnhancedZoomBlurParams
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem

@Composable
internal fun EnhancedZoomBlurParamsItem(
    value: EnhancedZoomBlurParams,
    filter: UiFilter<EnhancedZoomBlurParams>,
    onFilterChange: (value: EnhancedZoomBlurParams) -> Unit,
    previewOnly: Boolean
) {
    val radius: MutableState<Float> =
        remember(value) { mutableFloatStateOf((value.radius as Number).toFloat()) }
    val sigma: MutableState<Float> =
        remember(value) { mutableFloatStateOf((value.sigma as Number).toFloat()) }
    val anchorX: MutableState<Float> =
        remember(value) { mutableFloatStateOf((value.centerX as Number).toFloat()) }
    val anchorY: MutableState<Float> =
        remember(value) { mutableFloatStateOf((value.centerY as Number).toFloat()) }
    val strength: MutableState<Float> =
        remember(value) { mutableFloatStateOf((value.strength as Number).toFloat()) }
    val angle: MutableState<Float> =
        remember(value) { mutableFloatStateOf((value.angle as Number).toFloat()) }

    LaunchedEffect(
        radius.value,
        sigma.value,
        anchorX.value,
        anchorY.value,
        strength.value,
        angle.value
    ) {
        onFilterChange(
            EnhancedZoomBlurParams(
                radius = radius.value.toInt(),
                sigma = sigma.value,
                centerX = anchorX.value,
                centerY = anchorY.value,
                strength = strength.value,
                angle = angle.value
            )
        )
    }

    val paramsInfo by remember(filter) {
        derivedStateOf {
            filter.paramsInfo.mapIndexedNotNull { index, filterParam ->
                if (filterParam.title == null) return@mapIndexedNotNull null
                when (index) {
                    0 -> radius
                    1 -> sigma
                    2 -> anchorX
                    3 -> anchorY
                    4 -> strength
                    else -> angle
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