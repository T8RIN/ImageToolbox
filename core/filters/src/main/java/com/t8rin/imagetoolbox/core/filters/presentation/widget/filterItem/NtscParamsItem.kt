/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.utils.roundTo
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterParam
import com.t8rin.imagetoolbox.core.filters.domain.model.params.NtscParams
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Tune
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.other.ExpandableItem
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import kotlin.math.roundToInt

@Composable
internal fun NtscParamsItem(
    value: NtscParams,
    filter: UiFilter<NtscParams>,
    onFilterChange: (value: NtscParams) -> Unit,
    previewOnly: Boolean
) {
    val amount = remember(value) { mutableFloatStateOf(value.amount) }
    val chromaBleed = remember(value) { mutableFloatStateOf(value.chromaBleed) }
    val tapeWear = remember(value) { mutableFloatStateOf(value.tapeWear) }
    val noise = remember(value) { mutableFloatStateOf(value.noise) }
    val tracking = remember(value) { mutableFloatStateOf(value.tracking) }
    val seed = remember(value) { mutableFloatStateOf(value.seed.toFloat()) }
    val lumaSmear = remember(value) { mutableFloatStateOf(value.lumaSmear) }
    val compositeSharpening = remember(value) { mutableFloatStateOf(value.compositeSharpening) }
    val ringing = remember(value) { mutableFloatStateOf(value.ringing) }
    val snow = remember(value) { mutableFloatStateOf(value.snow) }
    val processingDownscale = remember(value) {
        mutableFloatStateOf(value.processingDownscale.toFloat())
    }

    val states = listOf(
        amount,
        chromaBleed,
        tapeWear,
        noise,
        tracking,
        seed,
        lumaSmear,
        compositeSharpening,
        ringing,
        snow,
        processingDownscale
    )

    LaunchedEffect(
        amount.floatValue,
        chromaBleed.floatValue,
        tapeWear.floatValue,
        noise.floatValue,
        tracking.floatValue,
        seed.floatValue,
        lumaSmear.floatValue,
        compositeSharpening.floatValue,
        ringing.floatValue,
        snow.floatValue,
        processingDownscale.floatValue
    ) {
        onFilterChange(
            NtscParams(
                amount = amount.floatValue,
                chromaBleed = chromaBleed.floatValue,
                tapeWear = tapeWear.floatValue,
                noise = noise.floatValue,
                tracking = tracking.floatValue,
                seed = seed.floatValue.roundToInt(),
                lumaSmear = lumaSmear.floatValue,
                compositeSharpening = compositeSharpening.floatValue,
                ringing = ringing.floatValue,
                snow = snow.floatValue,
                processingDownscale = processingDownscale.floatValue.roundToInt()
            )
        )
    }

    Column(
        modifier = Modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        filter.paramsInfo.take(BASIC_PARAMS_COUNT).forEachIndexed { index, info ->
            NtscParamSlider(
                state = states[index],
                info = info,
                previewOnly = previewOnly
            )
        }

        ExpandableItem(
            modifier = Modifier.padding(top = 4.dp),
            visibleContent = {
                TitleItem(
                    text = stringResource(R.string.ntsc_advanced_settings),
                    subtitle = stringResource(R.string.ntsc_advanced_settings_sub),
                    icon = Icons.Rounded.Tune,
                    iconEndPadding = 16.dp,
                    modifier = Modifier.padding(8.dp)
                )
            },
            expandableContent = {
                Column(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    filter.paramsInfo.drop(BASIC_PARAMS_COUNT).forEachIndexed { index, info ->
                        NtscParamSlider(
                            state = states[index + BASIC_PARAMS_COUNT],
                            info = info,
                            previewOnly = previewOnly
                        )
                    }
                }
            }
        )
    }
}

@Composable
private fun NtscParamSlider(
    state: MutableState<Float>,
    info: FilterParam,
    previewOnly: Boolean
) {
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

private const val BASIC_PARAMS_COUNT = 5