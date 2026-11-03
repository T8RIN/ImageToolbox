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
import com.t8rin.gmic.filters.GmicInterpolation
import com.t8rin.gmic.filters.WarpByIntensity
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.WarpByIntensity.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.DISTORTION)
internal class WarpByIntensityFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.WarpByIntensity {

    override fun createFilter(): GmicFilter = WarpByIntensity(
        xFactor = value.settings[0].toFloat(),
        yFactor = value.settings[1].toFloat(),
        xOffset = value.settings[2].toFloat(),
        yOffset = value.settings[3].toFloat(),
        correlatedChannels = value.settings[4].toBoolean(),
        interpolation = GmicInterpolation.valueOf(value.settings[5]),
        boundary = GmicBoundary.valueOf(value.settings[6]),
        channel = GmicChannel.valueOf(value.settings[7])
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_warp_by_intensity,
            R.string.filter_help_gmic_filter_warp_by_intensity
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicNumber(R.string.gmic_param_x_factor, 0.04f, -6f..6f),
                gmicNumber(R.string.gmic_param_y_factor, 0.04f, -6f..6f),
                gmicNumber(R.string.gmic_param_x_offset, 128, 0f..255f),
                gmicNumber(R.string.gmic_param_y_offset, 128, 0f..255f),
                gmicToggle(R.string.gmic_param_correlated_channels, false),
                gmicSelection(
                    R.string.gmic_param_interpolation,
                    "Linear",
                    listOf("NearestNeighbor", "Linear")
                ),
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