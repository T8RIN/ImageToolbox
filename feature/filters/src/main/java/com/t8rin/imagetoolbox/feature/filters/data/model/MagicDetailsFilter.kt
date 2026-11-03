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
import com.t8rin.gmic.filters.MagicDetails
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.MagicDetails.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.ENHANCEMENT)
internal class MagicDetailsFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.MagicDetails {

    override fun createFilter(): GmicFilter = MagicDetails(
        amplitude = value.settings[0].toFloat(),
        spatialScale = value.settings[1].toFloat(),
        valueScale = value.settings[2].toFloat(),
        edges = value.settings[3].toFloat(),
        smoothness = value.settings[4].toFloat(),
        channel = GmicChannel.valueOf(value.settings[5])
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_magic_details,
            R.string.filter_help_gmic_filter_magic_details
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicNumber(R.string.amplitude, 6, 0f..30f),
                gmicNumber(R.string.gmic_param_spatial_scale, 3, 0f..10f),
                gmicNumber(R.string.gmic_param_value_scale, 15, 0f..20f),
                gmicNumber(R.string.texture_edges, -0.5f, -3f..3f),
                gmicNumber(R.string.gmic_param_smoothness, 2, 0f..20f),
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