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
class UiHsvEqualizerFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf(
            "25",
            "55",
            "8",
            "0.2",
            "0.04",
            "200",
            "70",
            "-6",
            "0.14",
            "-0.03",
            "330",
            "45",
            "4",
            "0.1",
            "0.06"
        )
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_hsv_equalizer,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Number(R.string.gmic_param_first_hue_band, 0f..360f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_first_band_width, 1f..360f, isInteger = false),
        GmicParameterInfo.Number(
            R.string.gmic_param_first_hue_shift,
            -180f..180f,
            isInteger = false
        ),
        GmicParameterInfo.Number(
            R.string.gmic_param_first_saturation_correction,
            -0.99f..0.99f,
            isInteger = false
        ),
        GmicParameterInfo.Number(
            R.string.gmic_param_first_value_correction,
            -0.99f..0.99f,
            isInteger = false
        ),
        GmicParameterInfo.Number(R.string.gmic_param_second_hue_band, 0f..360f, isInteger = false),
        GmicParameterInfo.Number(
            R.string.gmic_param_second_band_width,
            1f..360f,
            isInteger = false
        ),
        GmicParameterInfo.Number(
            R.string.gmic_param_second_hue_shift,
            -180f..180f,
            isInteger = false
        ),
        GmicParameterInfo.Number(
            R.string.gmic_param_second_saturation_correction,
            -0.99f..0.99f,
            isInteger = false
        ),
        GmicParameterInfo.Number(
            R.string.gmic_param_second_value_correction,
            -0.99f..0.99f,
            isInteger = false
        ),
        GmicParameterInfo.Number(R.string.gmic_param_third_hue_band, 0f..360f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_third_band_width, 1f..360f, isInteger = false),
        GmicParameterInfo.Number(
            R.string.gmic_param_third_hue_shift,
            -180f..180f,
            isInteger = false
        ),
        GmicParameterInfo.Number(
            R.string.gmic_param_third_saturation_correction,
            -0.99f..0.99f,
            isInteger = false
        ),
        GmicParameterInfo.Number(
            R.string.gmic_param_third_value_correction,
            -0.99f..0.99f,
            isInteger = false
        )
    )
), Filter.HsvEqualizer
