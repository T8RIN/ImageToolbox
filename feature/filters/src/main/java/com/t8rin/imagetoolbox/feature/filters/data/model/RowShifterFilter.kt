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
import com.t8rin.gmic.filters.RowShifter
import com.t8rin.gmic.filters.RowShifterInterpolation
import com.t8rin.gmic.filters.RowShifterOrder
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.RowShifter.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.GLITCH)
internal class RowShifterFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.RowShifter {

    override fun createFilter(): GmicFilter = RowShifter(
        order = RowShifterOrder.valueOf(value.settings[0]),
        xShift = value.settings[1].toFloat(),
        xCentre = value.settings[2].toFloat(),
        yShift = value.settings[3].toFloat(),
        yCentre = value.settings[4].toFloat(),
        interpolation = RowShifterInterpolation.valueOf(value.settings[5]),
        invertShifts = value.settings[6].toBoolean()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_row_shifter,
            R.string.filter_help_gmic_filter_row_shifter
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicSelection(
                    R.string.order,
                    "XThenY",
                    listOf("XThenY", "Simultaneous", "YThenX")
                ),
                gmicNumber(R.string.gmic_param_x_shift, 0, -5f..5f),
                gmicNumber(R.string.gmic_param_x_centre, 0.5f, 0f..1f),
                gmicNumber(R.string.gmic_param_y_shift, 0, -5f..5f),
                gmicNumber(R.string.gmic_param_y_centre, 0.5f, 0f..1f),
                gmicSelection(
                    R.string.gmic_param_interpolation,
                    "Bilinear",
                    listOf(
                        "NoneOption",
                        "Nearest",
                        "Average",
                        "Bilinear",
                        "Grid",
                        "Bicubic",
                        "Lanczos"
                    )
                ),
                gmicToggle(R.string.gmic_param_invert_shifts, false)
            )
        }
    }
}