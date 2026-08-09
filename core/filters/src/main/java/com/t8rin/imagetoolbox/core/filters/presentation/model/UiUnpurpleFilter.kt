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

package com.t8rin.imagetoolbox.core.filters.presentation.model

import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GmicFilterParams
import com.t8rin.imagetoolbox.core.ksp.annotations.UiFilterInject
import com.t8rin.imagetoolbox.core.resources.R

@UiFilterInject(group = UiFilterInject.Groups.COLOR)
class UiUnpurpleFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf("1", "0", "0", "0.33", "5", "false", "0")
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_unpurple,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Number(
            R.string.gmic_param_intensity_of_purple_fringe,
            0f..1f,
            isInteger = false
        ),
        GmicParameterInfo.Number(R.string.gmic_param_minimum_brightness, 0f..1f, isInteger = false),
        GmicParameterInfo.Number(
            R.string.gmic_param_minimum_red_blue_ratio_in_the_fringe,
            0f..1f,
            isInteger = false
        ),
        GmicParameterInfo.Number(
            R.string.gmic_param_maximum_red_blue_ratio_in_the_fringe,
            0f..1f,
            isInteger = false
        ),
        GmicParameterInfo.Number(
            R.string.gmic_param_blur_standard_deviation,
            1f..10f,
            isInteger = true
        ),
        GmicParameterInfo.Toggle(R.string.gmic_param_gentle_mode_overrides_minimum_brightness_and_minimun_red_blue_ratio),
        GmicParameterInfo.Number(R.string.bit_depth, 0f..32f, isInteger = true)
    )
), Filter.Unpurple
