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
import com.t8rin.gmic.filters.DodgeSketch
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.DodgeSketch.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.ARTISTIC)
internal class DodgeSketchFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.DodgeSketch {

    override fun createFilter(): GmicFilter = DodgeSketch(
        power = value.settings[0].toInt(),
        spatialVariance = value.settings[1].toFloat(),
        valueVariance = value.settings[2].toFloat(),
        iterations = value.settings[3].toInt(),
        colored = value.settings[4].toBoolean()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_dodge_sketch,
            R.string.filter_help_gmic_filter_dodge_sketch
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicInteger(R.string.gmic_param_power, 3, 0f..10f),
                gmicNumber(R.string.gmic_param_spatial_variance, 10, 0f..100f),
                gmicNumber(R.string.gmic_param_value_variance, 7, 0f..100f),
                gmicInteger(R.string.iterations, 2, 1f..10f),
                gmicToggle(R.string.gmic_param_colored, false)
            )
        }
    }
}