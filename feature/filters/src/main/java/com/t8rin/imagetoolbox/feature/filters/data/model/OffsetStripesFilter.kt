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
import com.t8rin.gmic.filters.GmicBoundary
import com.t8rin.gmic.filters.GmicChannel
import com.t8rin.gmic.filters.OffsetStripes
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.OffsetStripes.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.GLITCH)
internal class OffsetStripesFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.OffsetStripes {

    override fun createFilter(): GmicFilter = OffsetStripes(
        iterations = value.settings[0].toInt(),
        randomSeed = value.settings[1].toInt(),
        perChannelShift = value.settings[2].toBoolean(),
        horizontalHeight = value.settings[3].toFloat(),
        horizontalAmplitude = value.settings[4].toFloat(),
        verticalWidth = value.settings[5].toFloat(),
        verticalAmplitude = value.settings[6].toFloat(),
        boundary = GmicBoundary.valueOf(value.settings[7]),
        channel = GmicChannel.valueOf(value.settings[8])
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_offset_stripes,
            R.string.filter_help_gmic_filter_offset_stripes
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicInteger(R.string.iterations, 1, 0f..256f),
                gmicSeed(R.string.seed, 0, 0f..65535f),
                gmicToggle(R.string.channel_shift, false),
                gmicNumber(R.string.gmic_param_horizontal_height, 10, 0f..100f),
                gmicNumber(R.string.gmic_param_horizontal_amplitude, 20, 0f..100f),
                gmicNumber(R.string.gmic_param_vertical_width, 10, 0f..100f),
                gmicNumber(R.string.gmic_param_vertical_amplitude, 20, 0f..100f),
                gmicSelection(
                    R.string.gmic_param_boundary,
                    "Mirror",
                    listOf("Transparent", "Nearest", "Periodic", "Mirror")
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
                )
            )
        }
    }
}