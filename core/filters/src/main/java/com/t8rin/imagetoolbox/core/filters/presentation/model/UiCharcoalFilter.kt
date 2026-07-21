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

@UiFilterInject(group = UiFilterInject.Groups.EFFECTS)
class UiCharcoalFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf(
            "65",
            "70",
            "170",
            "false",
            "true",
            "false",
            "50",
            "70",
            "-1",
            "-16777216",
            "false"
        )
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_charcoal,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Number(R.string.gmic_param_granularity, 0f..800f, isInteger = true),
        GmicParameterInfo.Number(
            R.string.gmic_param_lowlight_crossover,
            0f..255f,
            isInteger = true
        ),
        GmicParameterInfo.Number(
            R.string.gmic_param_highlight_crossover,
            0f..255f,
            isInteger = true
        ),
        GmicParameterInfo.Toggle(R.string.contrast),
        GmicParameterInfo.Toggle(R.string.gmic_param_optimize_size),
        GmicParameterInfo.Toggle(R.string.gmic_param_chalk_highlights),
        GmicParameterInfo.Number(R.string.gmic_param_minimum_highlight, 0f..255f, isInteger = true),
        GmicParameterInfo.Number(R.string.gmic_param_maximum_highlight, 0f..255f, isInteger = true),
        GmicParameterInfo.Color(R.string.background_color),
        GmicParameterInfo.Color(R.string.gmic_param_foreground_color),
        GmicParameterInfo.Toggle(R.string.invert_colors)
    )
), Filter.Charcoal
