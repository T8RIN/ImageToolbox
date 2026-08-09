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
class UiSaturationEqualizerFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf(
            "-12",
            "-6",
            "4",
            "8",
            "12",
            "8",
            "4",
            "-2",
            "-8",
            "8",
            "14",
            "6",
            "-4",
            "-8",
            "4",
            "10",
            "12",
            "8",
            "0"
        )
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_saturation_equalizer,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Number(R.string.gmic_param_black, -128f..128f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_near_black, -128f..128f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_dark_gray, -128f..128f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_mid_dark_gray, -128f..128f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_middle_gray, -128f..128f, isInteger = false),
        GmicParameterInfo.Number(
            R.string.gmic_param_mid_light_gray,
            -128f..128f,
            isInteger = false
        ),
        GmicParameterInfo.Number(R.string.gmic_param_light_gray, -128f..128f, isInteger = false),
        GmicParameterInfo.Number(R.string.highlights, -128f..128f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_white, -128f..128f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_hue_0, -128f..128f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_hue_45, -128f..128f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_hue_90, -128f..128f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_hue_135, -128f..128f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_hue_180, -128f..128f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_hue_225, -128f..128f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_hue_270, -128f..128f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_hue_315, -128f..128f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_hue_360, -128f..128f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_rotate_hue_bands, -45f..45f, isInteger = false)
    )
), Filter.SaturationEqualizer
