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
import com.t8rin.gmic.filters.FillHoles
import com.t8rin.gmic.filters.FillHolesChannelS
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.FillHoles.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.EFFECTS)
internal class FillHolesFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.FillHoles {

    override fun createFilter(): GmicFilter = FillHoles(
        morphRadius = value.settings[0].toInt(),
        edgeRadius = value.settings[1].toInt(),
        closeRadius = value.settings[2].toInt(),
        channelS = FillHolesChannelS.valueOf(value.settings[3]),
        fillLightColours = value.settings[4].toBoolean(),
        fast = value.settings[5].toBoolean()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.gmic_filter_fill_holes, R.string.filter_help_gmic_filter_fill_holes)

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicInteger(R.string.gmic_param_morph_radius, 11, 3f..50f),
                gmicInteger(R.string.gmic_param_edge_radius, 21, 0f..50f),
                gmicInteger(R.string.gmic_param_close_radius, 5, 0f..10f),
                gmicSelection(
                    R.string.gmic_param_channel,
                    "All",
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
                gmicToggle(R.string.gmic_param_fill_light_colours, false),
                gmicToggle(R.string.fast, true)
            )
        }
    }
}