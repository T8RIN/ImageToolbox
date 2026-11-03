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
import com.t8rin.gmic.filters.ConstrainedSharpen
import com.t8rin.gmic.filters.ConstrainedSharpenChannel
import com.t8rin.gmic.filters.ConstrainedSharpenValueAction
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.ConstrainedSharpen.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.ENHANCEMENT)
internal class ConstrainedSharpenFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.ConstrainedSharpen {

    override fun createFilter(): GmicFilter = ConstrainedSharpen(
        sharpenRadius = value.settings[0].toFloat(),
        amount = value.settings[1].toFloat(),
        threshold = value.settings[2].toFloat(),
        constraintRadius = value.settings[3].toInt(),
        overshoot = value.settings[4].toFloat(),
        channelS = ConstrainedSharpenChannel.valueOf(value.settings[5]),
        valueAction = ConstrainedSharpenValueAction.valueOf(value.settings[6])
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_constrained_sharpen,
            R.string.filter_help_gmic_filter_constrained_sharpen
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicNumber(R.string.gmic_param_sharpen_radius, 0.75f, 0f..10f),
                gmicNumber(R.string.amount, 2.5f, 0f..10f),
                gmicNumber(R.string.threshold, 1, 0f..50f),
                gmicInteger(R.string.gmic_param_constraint_radius, 5, 0f..10f),
                gmicNumber(R.string.gmic_param_overshoot, 0, 0f..50f),
                gmicSelection(
                    R.string.gmic_param_channel,
                    "Luminance",
                    listOf(
                        "All",
                        "Rgba",
                        "Rgb",
                        "Red",
                        "Green",
                        "Blue",
                        "Alpha",
                        "LinearRgb",
                        "LinearRed",
                        "LinearGreen",
                        "LinearBlue",
                        "Luminance",
                        "Chrominance",
                        "BlueChrominance",
                        "RedChrominance",
                        "GreenChrominance",
                        "LabLightness",
                        "LabChrominance",
                        "LabA",
                        "LabB",
                        "LchChrominance",
                        "LchChroma",
                        "LchHue",
                        "HsvHue",
                        "HsvSaturation",
                        "HsvValue",
                        "HsiIntensity",
                        "HslLightness",
                        "Cyan",
                        "Magenta",
                        "Yellow",
                        "Key"
                    )
                ),
                gmicSelection(
                    R.string.gmic_param_value_action,
                    "Cut",
                    listOf("NoneOption", "Cut", "Normalize")
                )
            )
        }
    }
}