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
class UiFillHolesFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf("11", "21", "5", "All", "false", "true")
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_fill_holes,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Number(R.string.gmic_param_morph_radius, 3f..50f, isInteger = true),
        GmicParameterInfo.Number(R.string.gmic_param_edge_radius, 0f..50f, isInteger = true),
        GmicParameterInfo.Number(R.string.gmic_param_close_radius, 0f..10f, isInteger = true),
        GmicParameterInfo.Selection(
            R.string.gmic_param_channel,
            listOf(
                "All",
                "RGBAAll",
                "RGBAll",
                "RGBRed",
                "RGBGreen",
                "RGBBlue",
                "RGBAAlpha",
                "YCbCrLuminance",
                "YCbCrBlueRedChrominances",
                "YCbCrBlueChrominance",
                "YCbCrRedChrominance",
                "YCbCrGreenChrominance",
                "LabLightness",
                "LabAbChrominances",
                "LabAChrominance",
                "LabBChrominance",
                "LchChChrominances",
                "LchCChrominance",
                "LchHChrominance",
                "HSVHue",
                "HSVSaturation",
                "HSVValue",
                "HSIIntensity",
                "HSLLightness",
                "CMYKCyan",
                "CMYKMagenta",
                "CMYKYellow",
                "CMYKKey"
            )
        ),
        GmicParameterInfo.Toggle(R.string.gmic_param_fill_light_colours),
        GmicParameterInfo.Toggle(R.string.fast)
    )
), Filter.FillHoles
