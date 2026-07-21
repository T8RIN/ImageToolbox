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
class UiChromaticAberrationsFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf(
            "-65536",
            "Shift",
            "2",
            "2",
            "0",
            "50",
            "1",
            "-16711936",
            "Shift",
            "0",
            "0",
            "0",
            "0",
            "1"
        )
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_chromatic_aberrations,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Color(R.string.texture_primary_color),
        GmicParameterInfo.Selection(
            R.string.gmic_param_primary_type,
            listOf("Shift", "Radial", "Angular", "Random")
        ),
        GmicParameterInfo.Number(
            R.string.gmic_param_primary_x_amplitude,
            -32f..32f,
            isInteger = false
        ),
        GmicParameterInfo.Number(
            R.string.gmic_param_primary_y_amplitude,
            -32f..32f,
            isInteger = false
        ),
        GmicParameterInfo.Number(
            R.string.gmic_param_primary_smoothness,
            0f..10f,
            isInteger = false
        ),
        GmicParameterInfo.Number(
            R.string.gmic_param_primary_center_attenuation,
            -100f..100f,
            isInteger = false
        ),
        GmicParameterInfo.Number(
            R.string.gmic_param_primary_attenuation_decay,
            0f..8f,
            isInteger = false
        ),
        GmicParameterInfo.Color(R.string.texture_secondary_color),
        GmicParameterInfo.Selection(
            R.string.gmic_param_secondary_type,
            listOf("Shift", "Radial", "Angular", "Random")
        ),
        GmicParameterInfo.Number(
            R.string.gmic_param_secondary_x_amplitude,
            -32f..32f,
            isInteger = false
        ),
        GmicParameterInfo.Number(
            R.string.gmic_param_secondary_y_amplitude,
            -32f..32f,
            isInteger = false
        ),
        GmicParameterInfo.Number(
            R.string.gmic_param_secondary_smoothness,
            0f..10f,
            isInteger = false
        ),
        GmicParameterInfo.Number(
            R.string.gmic_param_secondary_center_attenuation,
            -100f..100f,
            isInteger = false
        ),
        GmicParameterInfo.Number(
            R.string.gmic_param_secondary_attenuation_decay,
            0f..8f,
            isInteger = false
        )
    )
), Filter.ChromaticAberrations
