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
class UiDiffusionTensorsFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf("10", "5", "Color", "1", "0.15", "1", "0", "3")
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_diffusion_tensors,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Number(R.string.resolution, 0f..20f, isInteger = false),
        GmicParameterInfo.Number(R.string.just_size, 0f..16f, isInteger = false),
        GmicParameterInfo.Selection(
            R.string.gmic_param_color_mode,
            listOf("Monochrome", "Grayscale", "Orientation", "Color")
        ),
        GmicParameterInfo.Number(R.string.gmic_param_outline, 0f..16f, isInteger = true),
        GmicParameterInfo.Number(R.string.tag_sharpness, 0f..1f, isInteger = false),
        GmicParameterInfo.Number(R.string.texture_anisotropy, 0f..1f, isInteger = false),
        GmicParameterInfo.Number(
            R.string.gmic_param_gradient_smoothness,
            0f..10f,
            isInteger = false
        ),
        GmicParameterInfo.Number(R.string.gmic_param_tensor_smoothness, 0f..10f, isInteger = false)
    )
), Filter.DiffusionTensors
