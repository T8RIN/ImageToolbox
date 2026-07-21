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
class UiBlurredFrameFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf(
            "30",
            "30",
            "0",
            "5",
            "0",
            "false",
            "-8355712",
            "None",
            "5",
            "-1",
            "2",
            "2",
            "1",
            "0",
            "0.5",
            "0.5",
            "0"
        )
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_blurred_frame,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Number(R.string.gmic_param_horizontal_size, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_vertical_size, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.crop, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.blur, 0f..10f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_roundness, 0f..1f, isInteger = false),
        GmicParameterInfo.Toggle(R.string.color_balance),
        GmicParameterInfo.Color(R.string.gmic_param_balance_color),
        GmicParameterInfo.Selection(
            R.string.gmic_param_normalization,
            listOf("None", "Stretch", "Equalize")
        ),
        GmicParameterInfo.Number(R.string.outline_size, 0f..50f, isInteger = false),
        GmicParameterInfo.Color(R.string.outline_color),
        GmicParameterInfo.Number(R.string.gmic_param_x_shadow, -10f..10f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_y_shadow, -10f..10f, isInteger = false),
        GmicParameterInfo.Number(R.string.softness, 0f..5f, isInteger = false),
        GmicParameterInfo.Number(R.string.contrast, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_x_centering, 0f..1f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_y_centering, 0f..1f, isInteger = false),
        GmicParameterInfo.Number(R.string.angle, -180f..180f, isInteger = false)
    )
), Filter.BlurredFrame
