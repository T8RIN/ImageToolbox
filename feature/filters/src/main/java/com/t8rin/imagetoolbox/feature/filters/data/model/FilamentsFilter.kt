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
import com.t8rin.gmic.filters.FilamentColorMode
import com.t8rin.gmic.filters.Filaments
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.Filaments.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.EFFECTS)
internal class FilamentsFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.Filaments {

    override fun createFilter(): GmicFilter = Filaments(
        density = value.settings[0].toFloat(),
        length = value.settings[1].toFloat(),
        contour = value.settings[2].toFloat(),
        distortion = value.settings[3].toFloat(),
        smoothness = value.settings[4].toFloat(),
        rotation = value.settings[5].toFloat(),
        localNormalization = value.settings[6].toBoolean(),
        throwFromLeft = value.settings[7].toBoolean(),
        throwFromRight = value.settings[8].toBoolean(),
        throwFromAbove = value.settings[9].toBoolean(),
        throwFromBelow = value.settings[10].toBoolean(),
        opacity = value.settings[11].toFloat(),
        colorMode = FilamentColorMode.valueOf(value.settings[12])
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.gmic_filter_filaments, R.string.filter_help_gmic_filter_filaments)

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicNumber(R.string.density, 50, 0f..100f),
                gmicNumber(R.string.gmic_param_length, 50, 0f..100f),
                gmicNumber(R.string.contour, 75, 0f..100f),
                gmicNumber(R.string.distortion, 30, 0f..100f),
                gmicNumber(R.string.gmic_param_smoothness, 10, 0f..100f),
                gmicNumber(R.string.rotation, 0, -180f..180f),
                gmicToggle(R.string.gmic_param_local_normalization, true),
                gmicToggle(R.string.gmic_param_throw_from_left, true),
                gmicToggle(R.string.gmic_param_throw_from_right, true),
                gmicToggle(R.string.gmic_param_throw_from_above, true),
                gmicToggle(R.string.gmic_param_throw_from_below, true),
                gmicNumber(R.string.opacity, 50, 0f..100f),
                gmicSelection(
                    R.string.gmic_param_color_mode,
                    "WhiteOnBlack",
                    listOf(
                        "WhiteOnBlack",
                        "BlackOnWhite",
                        "WhiteOnTransparent",
                        "BlackOnTransparent"
                    )
                )
            )
        }
    }
}