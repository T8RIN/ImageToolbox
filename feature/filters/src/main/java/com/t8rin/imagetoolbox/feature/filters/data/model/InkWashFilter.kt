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
import com.t8rin.gmic.filters.InkWash
import com.t8rin.gmic.filters.InkWashContrast
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.InkWash.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.ARTISTIC)
internal class InkWashFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.InkWash {

    override fun createFilter(): GmicFilter = InkWash(
        size = value.settings[0].toFloat(),
        amplitude = value.settings[1].toFloat(),
        skipOtherSteps = value.settings[2].toBoolean(),
        smootherSharpness = value.settings[3].toFloat(),
        edgeProtection = value.settings[4].toFloat(),
        softness = value.settings[5].toFloat(),
        stretchContrast = InkWashContrast.valueOf(value.settings[6]),
        localNormalizationAmplitude = value.settings[7].toFloat(),
        localNormalizationSize = value.settings[8].toFloat(),
        neighborhoodSmoothness = value.settings[9].toFloat(),
        averageSmoothness = value.settings[10].toFloat()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.gmic_filter_ink_wash, R.string.filter_help_gmic_filter_ink_wash)

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicNumber(R.string.just_size, 0.14f, 0f..4f),
                gmicNumber(R.string.amplitude, 23, 0f..200f),
                gmicToggle(R.string.gmic_param_skip_other_steps, false),
                gmicNumber(R.string.gmic_param_smoother_sharpness, 0.5f, 0f..2f),
                gmicNumber(R.string.gmic_param_edge_protection, 0.54f, 0f..1f),
                gmicNumber(R.string.softness, 2.25f, 0f..10f),
                gmicSelection(
                    R.string.contrast,
                    "None",
                    listOf("None", "Automatic", "AutomaticWithContrastMask", "Manual")
                ),
                gmicNumber(R.string.gmic_param_local_normalization_amplitude, 2, 0f..60f),
                gmicNumber(R.string.gmic_param_local_normalization_size, 6, 0f..64f),
                gmicNumber(R.string.gmic_param_neighborhood_smoothness, 5, 0f..40f),
                gmicNumber(R.string.gmic_param_average_smoothness, 20, 0f..40f)
            )
        }
    }
}