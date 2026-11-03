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
import com.t8rin.gmic.filters.Droste
import com.t8rin.gmic.filters.DrosteDrawingMode
import com.t8rin.gmic.filters.DrosteMirror
import com.t8rin.gmic.filters.GmicBoundary
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.Droste.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.GEOMETRY)
internal class DrosteFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.Droste {

    override fun createFilter(): GmicFilter = Droste(
        upperLeftX = value.settings[0].toFloat(),
        upperLeftY = value.settings[1].toFloat(),
        upperRightX = value.settings[2].toFloat(),
        upperRightY = value.settings[3].toFloat(),
        lowerRightX = value.settings[4].toFloat(),
        lowerRightY = value.settings[5].toFloat(),
        lowerLeftX = value.settings[6].toFloat(),
        lowerLeftY = value.settings[7].toFloat(),
        iterations = value.settings[8].toInt(),
        xShift = value.settings[9].toFloat(),
        yShift = value.settings[10].toFloat(),
        angle = value.settings[11].toFloat(),
        zoom = value.settings[12].toFloat(),
        mirror = DrosteMirror.valueOf(value.settings[13]),
        boundary = GmicBoundary.valueOf(value.settings[14]),
        drawingMode = DrosteDrawingMode.valueOf(value.settings[15])
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.gmic_filter_droste, R.string.filter_help_gmic_filter_droste)

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicNumber(R.string.gmic_param_upper_left_x, 20, 0f..100f),
                gmicNumber(R.string.gmic_param_upper_left_y, 20, 0f..100f),
                gmicNumber(R.string.gmic_param_upper_right_x, 80, 0f..100f),
                gmicNumber(R.string.gmic_param_upper_right_y, 20, 0f..100f),
                gmicNumber(R.string.gmic_param_lower_right_x, 80, 0f..100f),
                gmicNumber(R.string.gmic_param_lower_right_y, 80, 0f..100f),
                gmicNumber(R.string.gmic_param_lower_left_x, 20, 0f..100f),
                gmicNumber(R.string.gmic_param_lower_left_y, 80, 0f..100f),
                gmicInteger(R.string.iterations, 1, 1f..10f),
                gmicNumber(R.string.gmic_param_x_shift, 0, -100f..100f),
                gmicNumber(R.string.gmic_param_y_shift, 0, -100f..100f),
                gmicNumber(R.string.angle, 0, 0f..360f),
                gmicNumber(R.string.zoom, 1, 0.1f..5f),
                gmicSelection(
                    R.string.tile_mode_mirror,
                    "None",
                    listOf("None", "XAxis", "YAxis", "BothAxes")
                ),
                gmicSelection(
                    R.string.gmic_param_boundary,
                    "Nearest",
                    listOf("Transparent", "Nearest", "Periodic", "Mirror")
                ),
                gmicSelection(
                    R.string.draw_mode,
                    "Replace",
                    listOf("Replace", "ReplaceSharpest", "Behind", "Below")
                )
            )
        }
    }
}