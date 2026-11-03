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
import com.t8rin.gmic.filters.GmicChannel
import com.t8rin.gmic.filters.LocalOrientation
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.LocalOrientation.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.EFFECTS)
internal class LocalOrientationFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.LocalOrientation {

    override fun createFilter(): GmicFilter = LocalOrientation(
        smoothness = value.settings[0].toFloat(),
        minimumThreshold = value.settings[1].toFloat(),
        maximumThreshold = value.settings[2].toFloat(),
        negativeColors = value.settings[3].toBoolean(),
        channel = GmicChannel.valueOf(value.settings[4])
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_local_orientation,
            R.string.filter_help_gmic_filter_local_orientation
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicNumber(R.string.gmic_param_smoothness, 0, 0f..5f),
                gmicNumber(R.string.gmic_param_minimum_threshold, 0, 0f..100f),
                gmicNumber(R.string.gmic_param_maximum_threshold, 100, 0f..100f),
                gmicToggle(R.string.gmic_param_negative_colors, false),
                gmicSelection(
                    R.string.gmic_param_channel,
                    "All",
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
                        "YCbCr",
                        "Luminance",
                        "Chrominance",
                        "BlueChrominance",
                        "RedChrominance",
                        "GreenChrominance",
                        "Lab",
                        "LabLightness",
                        "LabChrominance",
                        "LabA",
                        "LabB",
                        "Lch",
                        "LchChrominance",
                        "LchChroma",
                        "LchHue",
                        "Hsv",
                        "HsvHue",
                        "HsvSaturation",
                        "HsvValue",
                        "Hsi",
                        "HsiIntensity",
                        "Hsl",
                        "HslLightness",
                        "Cmyk",
                        "Cmy",
                        "Cyan",
                        "Magenta",
                        "Yellow",
                        "Key",
                        "Yiq",
                        "YiqLuma",
                        "YiqChrominance",
                        "Ryb",
                        "RybRed",
                        "RybYellow",
                        "RybBlue"
                    )
                )
            )
        }
    }
}