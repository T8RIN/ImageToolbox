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

@UiFilterInject(group = UiFilterInject.Groups.PIXELATION)
class UiCircleAbstractionFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf("8", "5", "0.8", "0", "true", "true", "true")
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_circle_abstraction,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Number(R.string.gmic_param_number_of_colors, 2f..16f, isInteger = true),
        GmicParameterInfo.Number(R.string.density, 1f..100f, isInteger = true),
        GmicParameterInfo.Number(R.string.opacity, 0f..1f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_smoothness, 0f..4f, isInteger = false),
        GmicParameterInfo.Toggle(R.string.gmic_param_filled_circles),
        GmicParameterInfo.Toggle(R.string.gmic_param_fill_transparent_holes),
        GmicParameterInfo.Toggle(R.string.gmic_param_normalize_colors)
    )
), Filter.CircleAbstraction
