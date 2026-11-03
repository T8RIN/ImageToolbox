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
import com.t8rin.gmic.filters.DeconvolutionBlur
import com.t8rin.gmic.filters.GmicChannel
import com.t8rin.gmic.filters.GmicParallelism
import com.t8rin.gmic.filters.GoldMeinelSharpen
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.GoldMeinelSharpen.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.ENHANCEMENT)
internal class GoldMeinelSharpenFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.GoldMeinelSharpen {

    override fun createFilter(): GmicFilter = GoldMeinelSharpen(
        sigma = value.settings[0].toFloat(),
        iterations = value.settings[1].toInt(),
        acceleration = value.settings[2].toFloat(),
        blur = DeconvolutionBlur.valueOf(value.settings[3]),
        cutValues = value.settings[4].toBoolean(),
        channel = GmicChannel.valueOf(value.settings[5]),
        parallelism = GmicParallelism.valueOf(value.settings[6]),
        spatialOverlap = value.settings[7].toInt()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_gold_meinel_sharpen,
            R.string.filter_help_gmic_filter_gold_meinel_sharpen
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicNumber(R.string.sigma, 1, 0.5f..10f),
                gmicInteger(R.string.iterations, 5, 1f..15f),
                gmicNumber(R.string.gmic_param_acceleration, 1, 1f..3f),
                gmicSelection(R.string.blur, "Gaussian", listOf("Exponential", "Gaussian")),
                gmicToggle(R.string.gmic_param_cut_values, true),
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
                    R.string.gmic_param_parallelism,
                    "Auto",
                    listOf(
                        "Auto",
                        "OneThread",
                        "TwoThreads",
                        "FourThreads",
                        "EightThreads",
                        "SixteenThreads"
                    )
                ),
                gmicInteger(R.string.gmic_param_spatial_overlap, 24, 0f..256f)
            )
        }
    }
}