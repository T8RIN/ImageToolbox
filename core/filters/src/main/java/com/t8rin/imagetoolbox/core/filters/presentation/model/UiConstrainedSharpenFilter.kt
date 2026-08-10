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

@UiFilterInject(group = UiFilterInject.Groups.ENHANCEMENT)
class UiConstrainedSharpenFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf("0.75", "2.5", "1", "5", "0", "Luminance", "Cut")
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_constrained_sharpen,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Number(R.string.gmic_param_sharpen_radius, 0f..10f, isInteger = false),
        GmicParameterInfo.Number(R.string.amount, 0f..10f, isInteger = false),
        GmicParameterInfo.Number(R.string.threshold, 0f..50f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_constraint_radius, 0f..10f, isInteger = true),
        GmicParameterInfo.Number(R.string.gmic_param_overshoot, 0f..50f, isInteger = false),
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
                "Luminance",
                "Chrominance",
                "BlueChrominance",
                "RedChrominance",
                "GreenChrominance",
                "LabLightness",
                "LabChrominance",
                "LabA",
                "LabB",
                "LchChrominance",
                "LchChroma",
                "LchHue",
                "HsvHue",
                "HsvSaturation",
                "HsvValue",
                "HsiIntensity",
                "HslLightness",
                "Cyan",
                "Magenta",
                "Yellow",
                "Key"
            )
        ),
        GmicParameterInfo.Selection(
            R.string.gmic_param_value_action,
            listOf("NoneOption", "Cut", "Normalize")
        )
    )
), Filter.ConstrainedSharpen
