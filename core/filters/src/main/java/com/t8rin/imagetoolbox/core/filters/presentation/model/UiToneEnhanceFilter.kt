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

@UiFilterInject(group = UiFilterInject.Groups.LIGHT)
class UiToneEnhanceFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf(
            "0.6",
            "0.9",
            "0.5",
            "1.1",
            "128",
            "0.4",
            "1",
            "0.6",
            "0.2",
            "3",
            "YCbCr",
            "Cut",
            "false"
        )
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_tone_enhance,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Number(R.string.gmic_param_shadows_detail, 0f..2f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_shadows_gamma, 0.2f..1.8f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_highlights_detail, 0f..2f, isInteger = false),
        GmicParameterInfo.Number(
            R.string.gmic_param_highlights_gamma,
            0.2f..1.8f,
            isInteger = false
        ),
        GmicParameterInfo.Number(R.string.center, 0f..255f, isInteger = true),
        GmicParameterInfo.Number(R.string.gmic_param_midpoint_detail, 0f..2f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_midpoint_gamma, 0.2f..1.8f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_recovery_boost, 0f..1f, isInteger = false),
        GmicParameterInfo.Number(
            R.string.gmic_param_recovery_smoothness,
            0f..2f,
            isInteger = false
        ),
        GmicParameterInfo.Number(
            R.string.gmic_param_detail_mask_smoothness,
            0f..20f,
            isInteger = true
        ),
        GmicParameterInfo.Selection(
            R.string.gmic_param_channel,
            listOf("Hsi", "Hsv", "Lab", "LinearRgb", "Rgb", "YCbCr")
        ),
        GmicParameterInfo.Selection(R.string.values, listOf("Cut", "Normalize")),
        GmicParameterInfo.Toggle(R.string.gmic_param_color_median)
    )
), Filter.ToneEnhance
