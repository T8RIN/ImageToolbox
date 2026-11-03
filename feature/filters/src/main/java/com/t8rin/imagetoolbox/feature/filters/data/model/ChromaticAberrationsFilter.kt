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

package com.t8rin.imagetoolbox.feature.filters.data.model

import com.t8rin.gmic.GmicFilter
import com.t8rin.gmic.filters.AberrationType
import com.t8rin.gmic.filters.ChromaticAberrations
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.ChromaticAberrations.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.GLITCH)
internal class ChromaticAberrationsFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.ChromaticAberrations {

    override fun createFilter(): GmicFilter = ChromaticAberrations(
        primaryColor = value.settings[0].toInt(),
        primaryType = AberrationType.valueOf(value.settings[1]),
        primaryXAmplitude = value.settings[2].toFloat(),
        primaryYAmplitude = value.settings[3].toFloat(),
        primarySmoothness = value.settings[4].toFloat(),
        primaryCenterAttenuation = value.settings[5].toFloat(),
        primaryAttenuationDecay = value.settings[6].toFloat(),
        secondaryColor = value.settings[7].toInt(),
        secondaryType = AberrationType.valueOf(value.settings[8]),
        secondaryXAmplitude = value.settings[9].toFloat(),
        secondaryYAmplitude = value.settings[10].toFloat(),
        secondarySmoothness = value.settings[11].toFloat(),
        secondaryCenterAttenuation = value.settings[12].toFloat(),
        secondaryAttenuationDecay = value.settings[13].toFloat()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_chromatic_aberrations,
            R.string.filter_help_gmic_filter_chromatic_aberrations
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicColor(R.string.texture_primary_color, -65536),
                gmicSelection(
                    R.string.gmic_param_primary_type,
                    "Shift",
                    listOf("Shift", "Radial", "Angular", "Random")
                ),
                gmicNumber(R.string.gmic_param_primary_x_amplitude, 2, -32f..32f),
                gmicNumber(R.string.gmic_param_primary_y_amplitude, 2, -32f..32f),
                gmicNumber(R.string.gmic_param_primary_smoothness, 0, 0f..10f),
                gmicNumber(R.string.gmic_param_primary_center_attenuation, 50, -100f..100f),
                gmicNumber(R.string.gmic_param_primary_attenuation_decay, 1, 0f..8f),
                gmicColor(R.string.texture_secondary_color, -16711936),
                gmicSelection(
                    R.string.gmic_param_secondary_type,
                    "Shift",
                    listOf("Shift", "Radial", "Angular", "Random")
                ),
                gmicNumber(R.string.gmic_param_secondary_x_amplitude, 0, -32f..32f),
                gmicNumber(R.string.gmic_param_secondary_y_amplitude, 0, -32f..32f),
                gmicNumber(R.string.gmic_param_secondary_smoothness, 0, 0f..10f),
                gmicNumber(R.string.gmic_param_secondary_center_attenuation, 0, -100f..100f),
                gmicNumber(R.string.gmic_param_secondary_attenuation_decay, 1, 0f..8f)
            )
        }
    }
}