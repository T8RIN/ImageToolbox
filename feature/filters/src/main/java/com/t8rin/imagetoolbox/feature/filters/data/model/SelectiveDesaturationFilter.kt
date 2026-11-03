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
import com.t8rin.gmic.filters.SelectiveDesaturation
import com.t8rin.gmic.filters.SelectiveDesaturationMaximumSaturation
import com.t8rin.gmic.filters.SelectiveDesaturationMode
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.SelectiveDesaturation.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.COLOR)
internal class SelectiveDesaturationFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.SelectiveDesaturation {

    override fun createFilter(): GmicFilter = SelectiveDesaturation(
        referenceColor = value.settings[0].toInt(),
        mode = SelectiveDesaturationMode.valueOf(value.settings[1]),
        strength = value.settings[2].toFloat(),
        regularization = value.settings[3].toInt(),
        maximumSaturation = SelectiveDesaturationMaximumSaturation.valueOf(value.settings[4])
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_selective_desaturation,
            R.string.filter_help_gmic_filter_selective_desaturation
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicColor(R.string.gmic_param_reference_color, -1754827),
                gmicSelection(
                    R.string.gmic_param_desaturate,
                    "AllButReferenceColor",
                    listOf("ReferenceColor", "AllButReferenceColor")
                ),
                gmicNumber(R.string.strength, 5, 0f..10f),
                gmicInteger(R.string.gmic_param_regularization, 4, 0f..20f),
                gmicSelection(
                    R.string.gmic_param_maximum_saturation,
                    "FromInput",
                    listOf("FromInput", "FromReferenceColor", "MaximumValue")
                )
            )
        }
    }
}