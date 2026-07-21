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
class UiWaterReflectionFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf("50", "1", "1080991934", "0", "1.5", "0", "-3.3", "7", "1.5")
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_water_reflection,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Number(R.string.gmic_param_height, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_attenuation, 0.1f..4f, isInteger = false),
        GmicParameterInfo.Color(R.string.color),
        GmicParameterInfo.Number(R.string.amplitude, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.softness, 0f..4f, isInteger = false),
        GmicParameterInfo.Number(R.string.x_angle, -10f..10f, isInteger = false),
        GmicParameterInfo.Number(R.string.y_angle, -10f..10f, isInteger = false),
        GmicParameterInfo.Number(R.string.tag_focal_length, 0f..10f, isInteger = false),
        GmicParameterInfo.Number(R.string.zoom, 1f..5f, isInteger = false)
    )
), Filter.WaterReflection
