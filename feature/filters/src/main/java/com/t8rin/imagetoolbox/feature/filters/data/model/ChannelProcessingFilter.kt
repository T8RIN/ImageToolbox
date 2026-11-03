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
import com.t8rin.gmic.filters.ChannelProcessing
import com.t8rin.gmic.filters.ChannelProcessingTonesRange
import com.t8rin.gmic.filters.ChannelProcessingValueAction
import com.t8rin.gmic.filters.GmicChannel
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.ChannelProcessing.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.COLOR)
internal class ChannelProcessingFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.ChannelProcessing {

    override fun createFilter(): GmicFilter = ChannelProcessing(
        brightness = value.settings[0].toFloat(),
        contrast = value.settings[1].toFloat(),
        gamma = value.settings[2].toFloat(),
        smoothness = value.settings[3].toFloat(),
        valueAction = ChannelProcessingValueAction.valueOf(value.settings[4]),
        lowValue = value.settings[5].toFloat(),
        highValue = value.settings[6].toFloat(),
        quantization = value.settings[7].toInt(),
        equalization = value.settings[8].toBoolean(),
        negation = value.settings[9].toBoolean(),
        tonesRange = ChannelProcessingTonesRange.valueOf(value.settings[10]),
        tonesSmoothness = value.settings[11].toFloat(),
        channelS = GmicChannel.valueOf(value.settings[12])
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_channel_processing,
            R.string.filter_help_gmic_filter_channel_processing
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicNumber(R.string.gmic_param_brightness, 5, -100f..100f),
                gmicNumber(R.string.contrast, 12, -100f..100f),
                gmicNumber(R.string.gamma, 0, -100f..100f),
                gmicNumber(R.string.gmic_param_smoothness, 0, 0f..10f),
                gmicSelection(
                    R.string.gmic_param_value_action,
                    "NoneOption",
                    listOf("NoneOption", "Cut", "CutAndNormalize", "Normalize", "Threshold")
                ),
                gmicNumber(R.string.gmic_param_low_value, 0, 0f..100f),
                gmicNumber(R.string.gmic_param_high_value, 100, 0f..100f),
                gmicInteger(R.string.gmic_param_quantization, 256, 1f..256f),
                gmicToggle(R.string.gmic_param_equalization, false),
                gmicToggle(R.string.gmic_param_negation, false),
                gmicSelection(
                    R.string.gmic_param_tones_range,
                    "Midtones",
                    listOf("AllTones", "Shadows", "Midtones", "Highlights")
                ),
                gmicNumber(R.string.gmic_param_tones_smoothness, 2, 0f..10f),
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