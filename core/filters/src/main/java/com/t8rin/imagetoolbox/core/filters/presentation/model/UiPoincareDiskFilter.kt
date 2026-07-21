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

@UiFilterInject(group = UiFilterInject.Groups.DISTORTION)
class UiPoincareDiskFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf(
            "5",
            "6",
            "100",
            "20",
            "0",
            "Polygonal",
            "true",
            "Image",
            "50",
            "50",
            "0",
            "0",
            "2",
            "-16777216",
            "-16777216",
            "-1"
        )
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_poincare_disk,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Number(R.string.gmic_param_p_value, 3f..16f, isInteger = true),
        GmicParameterInfo.Number(R.string.gmic_param_q_value, 3f..32f, isInteger = true),
        GmicParameterInfo.Number(R.string.just_size, 0f..200f, isInteger = false),
        GmicParameterInfo.Number(R.string.iterations, 0f..40f, isInteger = true),
        GmicParameterInfo.Number(R.string.angle, -180f..180f, isInteger = false),
        GmicParameterInfo.Selection(R.string.gmic_param_tiling, listOf("Triangular", "Polygonal")),
        GmicParameterInfo.Toggle(R.string.antialias),
        GmicParameterInfo.Selection(R.string.fill, listOf("Binary", "Image")),
        GmicParameterInfo.Number(R.string.gmic_param_x_shift, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_y_shift, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.zoom, -10f..10f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_image_angle, -180f..180f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_outline, 0f..8f, isInteger = true),
        GmicParameterInfo.Color(R.string.outline_color),
        GmicParameterInfo.Color(R.string.first_color),
        GmicParameterInfo.Color(R.string.second_color)
    )
), Filter.PoincareDisk
