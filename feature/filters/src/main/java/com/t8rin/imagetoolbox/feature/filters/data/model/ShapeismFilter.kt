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
import com.t8rin.gmic.filters.Shapeism
import com.t8rin.gmic.filters.ShapeismAngle
import com.t8rin.gmic.filters.ShapeismShape
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.Shapeism.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.PIXELATION)
internal class ShapeismFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.Shapeism {

    override fun createFilter(): GmicFilter = Shapeism(
        shape = ShapeismShape.valueOf(value.settings[0]),
        branches = value.settings[1].toInt(),
        thickness = value.settings[2].toFloat(),
        angle = value.settings[3].toFloat(),
        antialiasing = value.settings[4].toBoolean(),
        scales = value.settings[5].toInt(),
        maximalSize = value.settings[6].toInt(),
        minimalSize = value.settings[7].toInt(),
        allowedAngles = ShapeismAngle.valueOf(value.settings[8]),
        spacing = value.settings[9].toInt(),
        precision = value.settings[10].toInt(),
        edges = value.settings[11].toFloat(),
        smoothness = value.settings[12].toFloat(),
        backgroundColor = value.settings[13].toInt()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.gmic_filter_shapeism, R.string.filter_help_gmic_filter_shapeism)

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicSelection(
                    R.string.shape,
                    "Circle",
                    listOf(
                        "Square",
                        "Triangle",
                        "Circle",
                        "Diamond",
                        "Hexagon",
                        "Octagon",
                        "Star",
                        "Heart",
                        "Custom"
                    )
                ),
                gmicInteger(R.string.texture_branches, 7, 3f..16f),
                gmicNumber(R.string.texture_thickness, 0.38f, 0f..1f),
                gmicNumber(R.string.angle, 0, 0f..360f),
                gmicToggle(R.string.antialias, true),
                gmicInteger(R.string.scale, 5, 1f..16f),
                gmicInteger(R.string.gmic_param_maximal_size, 32, 1f..256f),
                gmicInteger(R.string.gmic_param_minimal_size, 8, 1f..256f),
                gmicSelection(
                    R.string.gmic_param_allowed_angles,
                    "Any",
                    listOf("Zero", "ZeroOr180", "RightAngles", "Any")
                ),
                gmicInteger(R.string.spacing, 0, -5f..5f),
                gmicInteger(R.string.gmic_param_precision, 5, 1f..10f),
                gmicNumber(R.string.texture_edges, 0.5f, 0f..2f),
                gmicNumber(R.string.gmic_param_smoothness, 1, 0f..10f),
                gmicColor(R.string.background_color, -16777216)
            )
        }
    }
}