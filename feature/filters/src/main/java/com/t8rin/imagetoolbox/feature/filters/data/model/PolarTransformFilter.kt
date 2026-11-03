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
import com.t8rin.gmic.filters.GmicBoundary
import com.t8rin.gmic.filters.PolarTransform
import com.t8rin.gmic.filters.PolarTransformPreset
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.PolarTransform.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.GEOMETRY)
internal class PolarTransformFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.PolarTransform {

    override fun createFilter(): GmicFilter = PolarTransform(
        preset = PolarTransformPreset.valueOf(value.settings[0]),
        centerXPercent = value.settings[1].toFloat(),
        centerYPercent = value.settings[2].toFloat(),
        radiusExpression = value.settings[3],
        angleExpression = value.settings[4],
        boundary = GmicBoundary.valueOf(value.settings[5])
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_polar_transform,
            R.string.filter_help_gmic_filter_polar_transform
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicSelection(
                    R.string.gmic_param_preset,
                    "CustomTransform",
                    listOf("CustomTransform", "InverseRadius", "SwapRadiusAngle")
                ),
                gmicNumber(R.string.center_x, 50, 0f..100f),
                gmicNumber(R.string.center_y, 50, 0f..100f),
                gmicSelection(
                    R.string.gmic_param_radius_expression,
                    "r + R/10*cos(a*5)",
                    listOf("r + R/10*cos(a*5)", "r", "R-r", "a*R/(2*pi)")
                ),
                gmicSelection(
                    R.string.gmic_param_angle_expression,
                    "a",
                    listOf("a", "-a", "r*2*pi/R", "a + r/R")
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