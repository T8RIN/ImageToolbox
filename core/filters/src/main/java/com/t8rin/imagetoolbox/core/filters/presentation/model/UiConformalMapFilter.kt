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
class UiConformalMapFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf("Dipole", "1", "0", "0", "0", "0", "0", "0", "Mirror", "0")
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_conformal_map,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Selection(
            R.string.gmic_param_mapping,
            listOf(
                "Identity",
                "Mobius",
                "Cosine",
                "Sine",
                "Tangent",
                "Exponential",
                "Logarithm",
                "Dipole",
                "Star"
            )
        ),
        GmicParameterInfo.Number(R.string.gmic_param_exponent_real, -16f..16f, isInteger = false),
        GmicParameterInfo.Number(
            R.string.gmic_param_exponent_imaginary,
            -16f..16f,
            isInteger = false
        ),
        GmicParameterInfo.Number(R.string.zoom, -4f..4f, isInteger = false),
        GmicParameterInfo.Number(R.string.angle, -180f..180f, isInteger = false),
        GmicParameterInfo.Number(R.string.aspect_ratio, -1f..1f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_x_shift, -5f..5f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_y_shift, -5f..5f, isInteger = false),
        GmicParameterInfo.Selection(
            R.string.gmic_param_boundary,
            listOf("Transparent", "Nearest", "Periodic", "Mirror")
        ),
        GmicParameterInfo.Number(R.string.antialias, 0f..3f, isInteger = true)
    )
), Filter.ConformalMap
