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
import com.t8rin.gmic.filters.DetailBand
import com.t8rin.gmic.filters.DetailSmoothing
import com.t8rin.gmic.filters.DetailsEqualizer
import com.t8rin.gmic.filters.GmicChannel
import com.t8rin.gmic.filters.GmicParallelism
import com.t8rin.gmic.filters.GmicValueAction
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.DetailsEqualizer.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.ENHANCEMENT)
internal class DetailsEqualizerFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.DetailsEqualizer {

    override fun createFilter(): GmicFilter = DetailsEqualizer(
        baseScale = value.settings[0].toFloat(),
        detailScale = value.settings[1].toFloat(),
        coarse = DetailBand(
            threshold = value.settings[2].toFloat(),
            smoothness = value.settings[3].toFloat(),
            smoothing = DetailSmoothing.valueOf(value.settings[4]),
            gain = value.settings[5].toFloat()
        ),
        medium = DetailBand(
            threshold = value.settings[6].toFloat(),
            smoothness = value.settings[7].toFloat(),
            smoothing = DetailSmoothing.valueOf(value.settings[8]),
            gain = value.settings[9].toFloat()
        ),
        small = DetailBand(
            threshold = value.settings[10].toFloat(),
            smoothness = value.settings[11].toFloat(),
            smoothing = DetailSmoothing.valueOf(value.settings[12]),
            gain = value.settings[13].toFloat()
        ),
        fine = DetailBand(
            threshold = value.settings[14].toFloat(),
            smoothness = value.settings[15].toFloat(),
            smoothing = DetailSmoothing.valueOf(value.settings[16]),
            gain = value.settings[17].toFloat()
        ),
        channel = GmicChannel.valueOf(value.settings[18]),
        valueAction = GmicValueAction.valueOf(value.settings[19]),
        parallelism = GmicParallelism.valueOf(value.settings[20]),
        spatialOverlap = value.settings[21].toInt()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_details_equalizer,
            R.string.filter_help_gmic_filter_details_equalizer
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicNumber(R.string.gmic_param_base_scale, 5, 0f..15f),
                gmicNumber(R.string.gmic_param_detail_scale, 0.5f, 0f..5f),
                gmicNumber(R.string.gmic_param_coarse_threshold, 0, 0f..10f),
                gmicNumber(R.string.gmic_param_coarse_smoothness, 0, 0f..10f),
                gmicSelection(
                    R.string.gmic_param_coarse_smoothing,
                    "Diffusion",
                    listOf("Gaussian", "Bilateral", "Diffusion")
                ),
                gmicNumber(R.string.gmic_param_coarse_gain, 0, -4f..4f),
                gmicNumber(R.string.gmic_param_medium_threshold, 0, 0f..10f),
                gmicNumber(R.string.gmic_param_medium_smoothness, 0, 0f..10f),
                gmicSelection(
                    R.string.gmic_param_medium_smoothing,
                    "Diffusion",
                    listOf("Gaussian", "Bilateral", "Diffusion")
                ),
                gmicNumber(R.string.gmic_param_medium_gain, 0, -4f..4f),
                gmicNumber(R.string.gmic_param_small_threshold, 0, 0f..10f),
                gmicNumber(R.string.gmic_param_small_smoothness, 0, 0f..10f),
                gmicSelection(
                    R.string.gmic_param_small_smoothing,
                    "Diffusion",
                    listOf("Gaussian", "Bilateral", "Diffusion")
                ),
                gmicNumber(R.string.gmic_param_small_gain, 0, -4f..4f),
                gmicNumber(R.string.gmic_param_fine_threshold, 0, 0f..10f),
                gmicNumber(R.string.gmic_param_fine_smoothness, 0, 0f..10f),
                gmicSelection(
                    R.string.gmic_param_fine_smoothing,
                    "Diffusion",
                    listOf("Gaussian", "Bilateral", "Diffusion")
                ),
                gmicNumber(R.string.gmic_param_fine_gain, 0, -4f..4f),
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
                gmicInteger(R.string.gmic_param_spatial_overlap, 32, 0f..256f)
            )
        }
    }
}