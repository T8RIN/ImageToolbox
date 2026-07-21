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
class UiReliefLightFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf("0.3", "0.2", "0.2", "0", "1", "50", "50", "5", "0.5", "false", "0")
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_relief_light,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Number(R.string.gmic_param_ambient_lightness, 0f..5f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_specular_lightness, 0f..2f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_specular_size, 0f..1f, isInteger = false),
        GmicParameterInfo.Number(R.string.texture_darkness, 0f..1f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_light_smoothness, 0f..5f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_light_x, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_light_y, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_light_z, 0f..20f, isInteger = false),
        GmicParameterInfo.Number(R.string.scale, 0f..3f, isInteger = false),
        GmicParameterInfo.Toggle(R.string.gmic_param_opacity_as_heightmap),
        GmicParameterInfo.Number(R.string.gmic_param_image_smoothness, 0f..10f, isInteger = false)
    )
), Filter.ReliefLight
