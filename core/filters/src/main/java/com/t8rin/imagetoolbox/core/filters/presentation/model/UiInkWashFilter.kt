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
class UiInkWashFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf("0.14", "23", "false", "0.5", "0.54", "2.25", "None", "2", "6", "5", "20")
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_ink_wash,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Number(R.string.just_size, 0f..4f, isInteger = false),
        GmicParameterInfo.Number(R.string.amplitude, 0f..200f, isInteger = false),
        GmicParameterInfo.Toggle(R.string.gmic_param_skip_other_steps),
        GmicParameterInfo.Number(R.string.gmic_param_smoother_sharpness, 0f..2f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_edge_protection, 0f..1f, isInteger = false),
        GmicParameterInfo.Number(R.string.softness, 0f..10f, isInteger = false),
        GmicParameterInfo.Selection(
            R.string.contrast,
            listOf("None", "Automatic", "AutomaticWithContrastMask", "Manual")
        ),
        GmicParameterInfo.Number(
            R.string.gmic_param_local_normalization_amplitude,
            0f..60f,
            isInteger = false
        ),
        GmicParameterInfo.Number(
            R.string.gmic_param_local_normalization_size,
            0f..64f,
            isInteger = false
        ),
        GmicParameterInfo.Number(
            R.string.gmic_param_neighborhood_smoothness,
            0f..40f,
            isInteger = false
        ),
        GmicParameterInfo.Number(R.string.gmic_param_average_smoothness, 0f..40f, isInteger = false)
    )
), Filter.InkWash
