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
import com.t8rin.gmic.filters.ToneEnhance
import com.t8rin.gmic.filters.ToneEnhanceChannel
import com.t8rin.gmic.filters.ToneEnhanceValues
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.ToneEnhance.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.LIGHT)
internal class ToneEnhanceFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.ToneEnhance {

    override fun createFilter(): GmicFilter = ToneEnhance(
        shadowsDetail = value.settings[0].toFloat(),
        shadowsGamma = value.settings[1].toFloat(),
        highlightsDetail = value.settings[2].toFloat(),
        highlightsGamma = value.settings[3].toFloat(),
        center = value.settings[4].toInt(),
        midpointDetail = value.settings[5].toFloat(),
        midpointGamma = value.settings[6].toFloat(),
        recoveryBoost = value.settings[7].toFloat(),
        recoverySmoothness = value.settings[8].toFloat(),
        detailMaskSmoothness = value.settings[9].toInt(),
        channel = ToneEnhanceChannel.valueOf(value.settings[10]),
        values = ToneEnhanceValues.valueOf(value.settings[11]),
        colorMedian = value.settings[12].toBoolean()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_tone_enhance,
            R.string.filter_help_gmic_filter_tone_enhance
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicNumber(R.string.gmic_param_shadows_detail, 0.6f, 0f..2f),
                gmicNumber(R.string.gmic_param_shadows_gamma, 0.9f, 0.2f..1.8f),
                gmicNumber(R.string.gmic_param_highlights_detail, 0.5f, 0f..2f),
                gmicNumber(R.string.gmic_param_highlights_gamma, 1.1f, 0.2f..1.8f),
                gmicInteger(R.string.center, 128, 0f..255f),
                gmicNumber(R.string.gmic_param_midpoint_detail, 0.4f, 0f..2f),
                gmicNumber(R.string.gmic_param_midpoint_gamma, 1, 0.2f..1.8f),
                gmicNumber(R.string.gmic_param_recovery_boost, 0.6f, 0f..1f),
                gmicNumber(R.string.gmic_param_recovery_smoothness, 0.2f, 0f..2f),
                gmicInteger(R.string.gmic_param_detail_mask_smoothness, 3, 0f..20f),
                gmicSelection(
                    R.string.gmic_param_channel,
                    "YCbCr",
                    listOf("Hsi", "Hsv", "Lab", "LinearRgb", "Rgb", "YCbCr")
                ),
                gmicSelection(R.string.values, "Cut", listOf("Cut", "Normalize")),
                gmicToggle(R.string.gmic_param_color_median, false)
            )
        }
    }
}