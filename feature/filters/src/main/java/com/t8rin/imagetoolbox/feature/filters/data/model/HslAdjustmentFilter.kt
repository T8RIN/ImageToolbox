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
import com.t8rin.gmic.filters.HslAdjustment
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.HslAdjustment.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.COLOR)
internal class HslAdjustmentFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.HslAdjustment {

    override fun createFilter(): GmicFilter = HslAdjustment(
        contrast = value.settings[0].toFloat(),
        level = value.settings[1].toFloat(),
        autoReduceLevel = value.settings[2].toBoolean(),
        hue = value.settings[3].toFloat(),
        amount = value.settings[4].toFloat(),
        autoSetInverseHue = value.settings[5].toBoolean(),
        gamma = value.settings[6].toFloat(),
        outputContrast = value.settings[7].toFloat()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_hsl_adjustment,
            R.string.filter_help_gmic_filter_hsl_adjustment
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicNumber(R.string.contrast, 1.1f, 0f..2f),
                gmicNumber(R.string.gmic_param_level, 0.05f, -1f..1f),
                gmicToggle(R.string.gmic_param_auto_reduce_level, false),
                gmicNumber(R.string.hue, 35, 0f..360f),
                gmicNumber(R.string.amount, 0.35f, 0.2f..1f),
                gmicToggle(R.string.gmic_param_auto_set_inverse_hue, false),
                gmicNumber(R.string.gamma, 1, 0f..2f),
                gmicNumber(R.string.gmic_param_output_contrast, 1.05f, 0f..2f)
            )
        }
    }
}