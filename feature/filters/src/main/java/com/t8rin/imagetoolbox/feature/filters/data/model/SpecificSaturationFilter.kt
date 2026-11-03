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
import com.t8rin.gmic.filters.SpecificSaturation
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.SpecificSaturation.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.COLOR)
internal class SpecificSaturationFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.SpecificSaturation {

    override fun createFilter(): GmicFilter = SpecificSaturation(
        input = value.settings[0].toFloat(),
        opposing = value.settings[1].toFloat(),
        hue = value.settings[2].toInt(),
        level = value.settings[3].toFloat(),
        gamma = value.settings[4].toFloat(),
        contrast = value.settings[5].toFloat(),
        srgbConversion = value.settings[6].toBoolean(),
        processTopLayerOnly = value.settings[7].toBoolean()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_specific_saturation,
            R.string.filter_help_gmic_filter_specific_saturation
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicNumber(R.string.gmic_param_input, 1.2f, 0f..2f),
                gmicNumber(R.string.gmic_param_opposing, 0.9f, 0f..2f),
                gmicInteger(R.string.hue, 35, 0f..360f),
                gmicNumber(R.string.gmic_param_level, 0.08f, -0.5f..0.5f),
                gmicNumber(R.string.gamma, 1, 0.2f..1.8f),
                gmicNumber(R.string.contrast, 1.1f, 0.2f..1.8f),
                gmicToggle(R.string.gmic_param_srgb_conversion, true),
                gmicToggle(R.string.gmic_param_process_top_layer_only, false)
            )
        }
    }
}