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
import com.t8rin.gmic.filters.Cubism
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.Cubism.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.ARTISTIC)
internal class CubismFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.Cubism {

    override fun createFilter(): GmicFilter = Cubism(
        iterations = value.settings[0].toInt(),
        density = value.settings[1].toFloat(),
        thickness = value.settings[2].toFloat(),
        angle = value.settings[3].toFloat(),
        opacity = value.settings[4].toFloat(),
        smoothness = value.settings[5].toFloat()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.gmic_filter_cubism, R.string.filter_help_gmic_filter_cubism)

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicInteger(R.string.iterations, 2, 0f..10f),
                gmicNumber(R.string.density, 50, 0f..200f),
                gmicNumber(R.string.texture_thickness, 10, 0f..50f),
                gmicNumber(R.string.angle, 90, 0f..360f),
                gmicNumber(R.string.opacity, 0.7f, 0.01f..1f),
                gmicNumber(R.string.gmic_param_smoothness, 0, 0f..5f)
            )
        }
    }
}