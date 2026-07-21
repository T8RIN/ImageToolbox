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

package com.t8rin.imagetoolbox.core.filters.presentation.model

import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GmicFilterParams
import com.t8rin.imagetoolbox.core.ksp.annotations.UiFilterInject
import com.t8rin.imagetoolbox.core.resources.R

@UiFilterInject(group = UiFilterInject.Groups.LIGHT)
class UiDetailsEqualizerFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf(
            "5",
            "0.5",
            "0",
            "0",
            "Diffusion",
            "0",
            "0",
            "0",
            "Diffusion",
            "0",
            "0",
            "0",
            "Diffusion",
            "0",
            "0",
            "0",
            "Diffusion",
            "0",
            "All",
            "None",
            "Auto",
            "32"
        )
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_details_equalizer,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Number(R.string.gmic_param_base_scale, 0f..15f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_detail_scale, 0f..5f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_coarse_threshold, 0f..10f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_coarse_smoothness, 0f..10f, isInteger = false),
        GmicParameterInfo.Selection(
            R.string.gmic_param_coarse_smoothing,
            listOf("Gaussian", "Bilateral", "Diffusion")
        ),
        GmicParameterInfo.Number(R.string.gmic_param_coarse_gain, -4f..4f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_medium_threshold, 0f..10f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_medium_smoothness, 0f..10f, isInteger = false),
        GmicParameterInfo.Selection(
            R.string.gmic_param_medium_smoothing,
            listOf("Gaussian", "Bilateral", "Diffusion")
        ),
        GmicParameterInfo.Number(R.string.gmic_param_medium_gain, -4f..4f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_small_threshold, 0f..10f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_small_smoothness, 0f..10f, isInteger = false),
        GmicParameterInfo.Selection(
            R.string.gmic_param_small_smoothing,
            listOf("Gaussian", "Bilateral", "Diffusion")
        ),
        GmicParameterInfo.Number(R.string.gmic_param_small_gain, -4f..4f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_fine_threshold, 0f..10f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_fine_smoothness, 0f..10f, isInteger = false),
        GmicParameterInfo.Selection(
            R.string.gmic_param_fine_smoothing,
            listOf("Gaussian", "Bilateral", "Diffusion")
        ),
        GmicParameterInfo.Number(R.string.gmic_param_fine_gain, -4f..4f, isInteger = false),
        GmicParameterInfo.Selection(
            R.string.gmic_param_channel,
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
        GmicParameterInfo.Selection(
            R.string.gmic_param_value_action,
            listOf("None", "Cut", "Normalize")
        ),
        GmicParameterInfo.Selection(
            R.string.gmic_param_parallelism,
            listOf(
                "Auto",
                "OneThread",
                "TwoThreads",
                "FourThreads",
                "EightThreads",
                "SixteenThreads"
            )
        ),
        GmicParameterInfo.Number(R.string.gmic_param_spatial_overlap, 0f..256f, isInteger = true)
    )
), Filter.DetailsEqualizer
