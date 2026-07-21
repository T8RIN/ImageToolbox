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
class UiEngraveFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf(
            "0.5",
            "50",
            "0",
            "8",
            "40",
            "0",
            "0",
            "true",
            "10",
            "1",
            "0",
            "0",
            "0",
            "X1_5"
        )
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_engrave,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Number(R.string.radius, 0f..2f, isInteger = false),
        GmicParameterInfo.Number(R.string.density, 0f..200f, isInteger = false),
        GmicParameterInfo.Number(R.string.texture_edges, 0f..10f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_coherence, 0f..40f, isInteger = false),
        GmicParameterInfo.Number(R.string.threshold, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_minimal_area, -256f..256f, isInteger = true),
        GmicParameterInfo.Number(
            R.string.gmic_param_flat_regions_removal,
            0f..10f,
            isInteger = false
        ),
        GmicParameterInfo.Toggle(R.string.gmic_param_add_color_background),
        GmicParameterInfo.Number(R.string.gmic_param_quantization, 0f..40f, isInteger = false),
        GmicParameterInfo.Number(R.string.texture_shading, 0f..5f, isInteger = true),
        GmicParameterInfo.Number(R.string.hue, -180f..180f, isInteger = false),
        GmicParameterInfo.Number(R.string.saturation, -100f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_lightness, -100f..100f, isInteger = false),
        GmicParameterInfo.Selection(R.string.antialias, listOf("Disabled", "X1_5", "X2", "X3"))
    )
), Filter.Engrave
