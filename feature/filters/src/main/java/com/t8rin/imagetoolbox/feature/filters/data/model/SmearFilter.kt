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

import com.jhlabs.JhFilter
import com.jhlabs.SmearFilter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.Smear.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.JhFilterTransformation

@FilterDefinition(group = FilterGroups.PIXELATION)
internal class SmearFilter(
    override val value: Value
) : JhFilterTransformation(), Filter.Smear {

    override fun createFilter(): JhFilter = SmearFilter().apply {
        angle = Math.toRadians(value.angle.toDouble()).toFloat()
        density = value.density
        mix = value.mix
        distance = value.distance
        shape = value.shape
    }

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.smear, R.string.filter_help_smear)

        override val parameters = structuredParameters {
            Value::angle.float(R.string.angle, 45f, 0f..360f, 0)
            Value::density.float(R.string.density, 1f, 0f..1f)
            Value::mix.float(R.string.mix, 1f, 0f..1f)
            Value::distance.int(R.string.distance, 100, 0..200)
            Value::shape.int(R.string.shape, 0, 0..3)
        }
    }
}