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
import com.t8rin.gmic.filters.RandomDeformations
import com.t8rin.gmic.filters.RandomDeformationsBoundary
import com.t8rin.gmic.filters.RandomDeformationsInterpolation
import com.t8rin.gmic.filters.RandomDeformationsMatrixInterpolation
import com.t8rin.gmic.filters.RandomDeformationsMode
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.RandomDeformations.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.DISTORTION)
internal class RandomDeformationsFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.RandomDeformations {

    override fun createFilter(): GmicFilter = RandomDeformations(
        recompute = value.settings[0].toBoolean(),
        amplitude = value.settings[1].toFloat(),
        interpolation = RandomDeformationsInterpolation.valueOf(value.settings[2]),
        matrixDensity = value.settings[3].toFloat(),
        matrixInterpolation = RandomDeformationsMatrixInterpolation.valueOf(value.settings[4]),
        mode = RandomDeformationsMode.valueOf(value.settings[5]),
        character = value.settings[6].toFloat(),
        boundary = RandomDeformationsBoundary.valueOf(value.settings[7])
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_random_deformations,
            R.string.filter_help_gmic_filter_random_deformations
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicToggle(R.string.gmic_param_recompute, false),
                gmicNumber(R.string.amplitude, 5, 0f..50f),
                gmicSelection(
                    R.string.gmic_param_interpolation,
                    "Linear",
                    listOf("NoneOption", "Linear", "Bicubic")
                ),
                gmicNumber(R.string.gmic_param_matrix_density, 10, 1f..100f),
                gmicSelection(
                    R.string.gmic_param_matrix_interpolation,
                    "Bicubic",
                    listOf("Linear", "Bicubic")
                ),
                gmicSelection(
                    R.string.gmic_param_mode,
                    "Noise",
                    listOf("Noise", "SpreadNoise")
                ),
                gmicNumber(R.string.gmic_param_character, 0, -100f..100f),
                gmicSelection(
                    R.string.gmic_param_boundary,
                    "Neumann",
                    listOf("Dirichlet", "Neumann", "Periodic", "Mirror")
                )
            )
        }
    }
}