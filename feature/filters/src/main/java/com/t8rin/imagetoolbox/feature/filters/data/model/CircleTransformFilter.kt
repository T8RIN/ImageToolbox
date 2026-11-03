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
import com.t8rin.gmic.filters.CircleTransform
import com.t8rin.gmic.filters.CircleTransformSymmetry
import com.t8rin.gmic.filters.GmicBoundary
import com.t8rin.gmic.filters.GmicInterpolation
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.CircleTransform.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.GEOMETRY)
internal class CircleTransformFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.CircleTransform {

    override fun createFilter(): GmicFilter = CircleTransform(
        centerX = value.settings[0].toFloat(),
        centerY = value.settings[1].toFloat(),
        radiusPointX = value.settings[2].toFloat(),
        radiusPointY = value.settings[3].toFloat(),
        xScale = value.settings[4].toFloat(),
        yScale = value.settings[5].toFloat(),
        symmetry = CircleTransformSymmetry.valueOf(value.settings[6]),
        interpolation = GmicInterpolation.valueOf(value.settings[7]),
        boundary = GmicBoundary.valueOf(value.settings[8])
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_circle_transform,
            R.string.filter_help_gmic_filter_circle_transform
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicNumber(R.string.center_x, 50, 0f..100f),
                gmicNumber(R.string.center_y, 50, 0f..100f),
                gmicNumber(R.string.gmic_param_radius_point_x, 75, 0f..100f),
                gmicNumber(R.string.gmic_param_radius_point_y, 50, 0f..100f),
                gmicNumber(R.string.gmic_param_x_scale, -2, -16f..16f),
                gmicNumber(R.string.gmic_param_y_scale, -2, -16f..16f),
                gmicSelection(
                    R.string.texture_symmetry,
                    "None",
                    listOf("None", "Inside", "Outside")
                ),
                gmicSelection(
                    R.string.gmic_param_interpolation,
                    "Linear",
                    listOf("NearestNeighbor", "Linear")
                ),
                gmicSelection(
                    R.string.gmic_param_boundary,
                    "Mirror",
                    listOf("Transparent", "Nearest", "Periodic", "Mirror")
                )
            )
        }
    }
}