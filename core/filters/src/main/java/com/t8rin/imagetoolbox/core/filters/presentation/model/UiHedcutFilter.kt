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
class UiHedcutFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf("0.5", "0.5", "0.5", "0.0", "0.5", "false", "true")
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_hedcut,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Number(R.string.contrast, 0f..1f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_luminance_level, 0f..1f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_smoothing, 0f..1f, isInteger = false),
        GmicParameterInfo.Number(R.string.just_size, 0f..1f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_step, 0f..1f, isInteger = false),
        GmicParameterInfo.Toggle(R.string.quality),
        GmicParameterInfo.Toggle(R.string.gmic_param_force_gray)
    )
), Filter.Hedcut
