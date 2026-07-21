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
class UiCrtPhosphorsFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf(
            "Stripes",
            "4",
            "Low",
            "0",
            "4",
            "50",
            "true",
            "false",
            "false",
            "false",
            "true"
        )
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_crt_phosphors,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Selection(R.string.type, listOf("Stripes", "Triad", "SlotMask")),
        GmicParameterInfo.Number(R.string.gmic_param_upscale_factor, 1f..8f, isInteger = true),
        GmicParameterInfo.Selection(R.string.gmic_param_precision, listOf("Low", "High")),
        GmicParameterInfo.Number(R.string.gmic_param_smoothness, 0f..10f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_neighborhood_size, 1f..16f, isInteger = true),
        GmicParameterInfo.Number(R.string.gmic_param_stride, 0f..100f, isInteger = false),
        GmicParameterInfo.Toggle(R.string.gmic_param_adaptive_pattern),
        GmicParameterInfo.Toggle(R.string.gmic_param_use_luma),
        GmicParameterInfo.Toggle(R.string.gmic_param_transpose_pattern),
        GmicParameterInfo.Toggle(R.string.gmic_param_average_over_pattern),
        GmicParameterInfo.Toggle(R.string.gmic_param_normalize_image)
    )
), Filter.CrtPhosphors
