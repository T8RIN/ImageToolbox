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
import com.t8rin.gmic.filters.GmicValueAction
import com.t8rin.gmic.filters.Rodilius
import com.t8rin.gmic.filters.RodiliusColorMode
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.Rodilius.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.ARTISTIC)
internal class RodiliusFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.Rodilius {

    override fun createFilter(): GmicFilter = Rodilius(
        amplitude = value.settings[0].toFloat(),
        thickness = value.settings[1].toFloat(),
        sharpness = value.settings[2].toFloat(),
        orientations = value.settings[3].toInt(),
        offset = value.settings[4].toFloat(),
        smoothness = value.settings[5].toInt(),
        colorMode = RodiliusColorMode.valueOf(value.settings[6]),
        channel = GmicChannel.valueOf(value.settings[7]),
        valueAction = GmicValueAction.valueOf(value.settings[8])
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.gmic_filter_rodilius, R.string.filter_help_gmic_filter_rodilius)

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicNumber(R.string.amplitude, 10, 0f..30f),
                gmicNumber(R.string.texture_thickness, 10, 0f..100f),
                gmicNumber(R.string.tag_sharpness, 300, 0f..1000f),
                gmicInteger(R.string.orientation, 5, 2f..36f),
                gmicNumber(R.string.offset, 30, 0f..180f),
                gmicInteger(R.string.gmic_param_smoothness, 0, 0f..5f),
                gmicSelection(
                    R.string.gmic_param_color_mode,
                    "Lighter",
                    listOf("Darker", "Lighter")
                ),
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
                ),
                gmicSelection(
                    R.string.gmic_param_value_action,
                    "None",
                    listOf("None", "Cut", "Normalize")
                )
            )
        }
    }
}