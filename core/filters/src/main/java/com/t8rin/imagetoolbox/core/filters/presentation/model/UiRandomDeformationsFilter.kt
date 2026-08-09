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
class UiRandomDeformationsFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf("false", "5", "Linear", "10", "Bicubic", "Noise", "0", "Neumann")
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_random_deformations,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Toggle(R.string.gmic_param_recompute),
        GmicParameterInfo.Number(R.string.amplitude, 0f..50f, isInteger = false),
        GmicParameterInfo.Selection(
            R.string.gmic_param_interpolation,
            listOf("NoneOption", "Linear", "Bicubic")
        ),
        GmicParameterInfo.Number(R.string.gmic_param_matrix_density, 1f..100f, isInteger = false),
        GmicParameterInfo.Selection(
            R.string.gmic_param_matrix_interpolation,
            listOf("Linear", "Bicubic")
        ),
        GmicParameterInfo.Selection(R.string.gmic_param_mode, listOf("Noise", "SpreadNoise")),
        GmicParameterInfo.Number(R.string.gmic_param_character, -100f..100f, isInteger = false),
        GmicParameterInfo.Selection(
            R.string.gmic_param_boundary,
            listOf("Dirichlet", "Neumann", "Periodic", "Mirror")
        )
    )
), Filter.RandomDeformations
