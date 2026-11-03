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
import com.t8rin.gmic.filters.AbstractRegularization
import com.t8rin.gmic.filters.SmoothAbstract
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.SmoothAbstract.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.ARTISTIC)
internal class SmoothAbstractFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.SmoothAbstract {

    override fun createFilter(): GmicFilter = SmoothAbstract(
        smoothness = value.settings[0].toFloat(),
        regularization = AbstractRegularization.valueOf(value.settings[1]),
        regularizationIterations = value.settings[2].toInt(),
        geometry = value.settings[3].toFloat(),
        details = value.settings[4].toFloat()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_smooth_abstract,
            R.string.filter_help_gmic_filter_smooth_abstract
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicNumber(R.string.gmic_param_smoothness, 75, 0f..100f),
                gmicSelection(
                    R.string.gmic_param_regularization,
                    "Isotropic",
                    listOf("Isotropic", "DelaunayGuided", "EdgeOriented")
                ),
                gmicInteger(R.string.iterations, 20, 0f..100f),
                gmicNumber(R.string.gmic_param_geometry, 1, 0f..5f),
                gmicNumber(R.string.texture_detail, 30, 0f..50f)
            )
        }
    }
}