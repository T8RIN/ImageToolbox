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
import com.t8rin.gmic.filters.BrushLight
import com.t8rin.gmic.filters.BrushShape
import com.t8rin.gmic.filters.Brushify
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.Brushify.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.ARTISTIC)
internal class BrushifyFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.Brushify {

    override fun createFilter(): GmicFilter = Brushify(
        shape = BrushShape.valueOf(value.settings[0]),
        ratio = value.settings[1].toFloat(),
        numberOfSizes = value.settings[2].toInt(),
        maximalSize = value.settings[3].toInt(),
        minimalSize = value.settings[4].toFloat(),
        orientations = value.settings[5].toInt(),
        fuzziness = value.settings[6].toFloat(),
        smoothness = value.settings[7].toFloat(),
        light = BrushLight.valueOf(value.settings[8]),
        lightStrength = value.settings[9].toFloat(),
        opacity = value.settings[10].toFloat(),
        density = value.settings[11].toFloat(),
        contourCoherence = value.settings[12].toFloat(),
        orientationCoherence = value.settings[13].toFloat(),
        gradientSmoothness = value.settings[14].toFloat(),
        structureSmoothness = value.settings[15].toFloat(),
        primaryAngle = value.settings[16].toFloat(),
        angleDispersion = value.settings[17].toFloat()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.gmic_filter_brushify, R.string.filter_help_gmic_filter_brushify)

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicSelection(
                    R.string.shape,
                    "Ellipse",
                    listOf(
                        "Rectangle",
                        "Diamond",
                        "Pentagon",
                        "Hexagon",
                        "Octagon",
                        "Ellipse",
                        "Gaussian",
                        "Star",
                        "Heart"
                    )
                ),
                gmicNumber(R.string.gmic_param_ratio, 0.25f, 0f..1f),
                gmicInteger(R.string.gmic_param_number_of_sizes, 4, 1f..16f),
                gmicInteger(R.string.gmic_param_maximal_size, 64, 1f..128f),
                gmicNumber(R.string.gmic_param_minimal_size, 25, 0f..100f),
                gmicInteger(R.string.orientation, 12, 1f..24f),
                gmicNumber(R.string.fuzziness, 0, 0f..10f),
                gmicNumber(R.string.gmic_param_smoothness, 2, 0f..10f),
                gmicSelection(
                    R.string.light,
                    "Full",
                    listOf("None", "Flat", "Darken", "Lighten", "Full")
                ),
                gmicNumber(R.string.strength, 0.2f, 0f..1f),
                gmicNumber(R.string.opacity, 0.5f, 0f..1f),
                gmicNumber(R.string.density, 30, 0f..100f),
                gmicNumber(R.string.gmic_param_contour_coherence, 1, 0f..1f),
                gmicNumber(R.string.gmic_param_orientation_coherence, 1, 0f..1f),
                gmicNumber(R.string.gmic_param_gradient_smoothness, 1, 0f..10f),
                gmicNumber(R.string.gmic_param_structure_smoothness, 5, 0f..10f),
                gmicNumber(R.string.gmic_param_primary_angle, 0, -180f..180f),
                gmicNumber(R.string.dispersion, 0.2f, 0f..1f)
            )
        }
    }
}