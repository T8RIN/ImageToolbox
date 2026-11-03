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
import com.t8rin.gmic.filters.CrtPhosphorType
import com.t8rin.gmic.filters.CrtPhosphors
import com.t8rin.gmic.filters.CrtPrecision
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.CrtPhosphors.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.EFFECTS)
internal class CrtPhosphorsFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.CrtPhosphors {

    override val upscaleFactor: Int
        get() = value.settings[1].toInt()

    override fun createFilter(): GmicFilter = CrtPhosphors(
        type = CrtPhosphorType.valueOf(value.settings[0]),
        upscaleFactor = upscaleFactor,
        precision = CrtPrecision.valueOf(value.settings[2]),
        smoothness = value.settings[3].toFloat(),
        neighborhoodSize = value.settings[4].toInt(),
        stride = value.settings[5].toFloat(),
        adaptivePattern = value.settings[6].toBoolean(),
        useLuma = value.settings[7].toBoolean(),
        transposePattern = value.settings[8].toBoolean(),
        averageOverPattern = value.settings[9].toBoolean(),
        normalizeImage = value.settings[10].toBoolean()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_crt_phosphors,
            R.string.filter_help_gmic_filter_crt_phosphors
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicSelection(R.string.type, "Stripes", listOf("Stripes", "Triad", "SlotMask")),
                gmicInteger(R.string.gmic_param_upscale_factor, 4, 1f..8f),
                gmicSelection(R.string.gmic_param_precision, "Low", listOf("Low", "High")),
                gmicNumber(R.string.gmic_param_smoothness, 0, 0f..10f),
                gmicInteger(R.string.gmic_param_neighborhood_size, 4, 1f..16f),
                gmicNumber(R.string.gmic_param_stride, 50, 0f..100f),
                gmicToggle(R.string.gmic_param_adaptive_pattern, true),
                gmicToggle(R.string.gmic_param_use_luma, false),
                gmicToggle(R.string.gmic_param_transpose_pattern, false),
                gmicToggle(R.string.gmic_param_average_over_pattern, false),
                gmicToggle(R.string.gmic_param_normalize_image, true)
            )
        }
    }
}