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

@UiFilterInject(group = UiFilterInject.Groups.EFFECTS)
class UiDeblurFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf("2", "10", "20", "0.1", "MeanCurvature", "All", "Auto", "24")
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_deblur,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Number(R.string.radius, 0f..20f, isInteger = false),
        GmicParameterInfo.Number(R.string.iterations, 0f..100f, isInteger = true),
        GmicParameterInfo.Number(R.string.gmic_param_time_step, 0f..50f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_smoothness, 0f..10f, isInteger = false),
        GmicParameterInfo.Selection(
            R.string.gmic_param_regularization,
            listOf("Tikhonov", "MeanCurvature", "TotalVariation")
        ),
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
), Filter.Deblur
