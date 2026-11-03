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
import com.jhlabs.RaysFilter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.Rays.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.JhFilterTransformation

@FilterDefinition(group = FilterGroups.LIGHT)
internal class RaysFilter(
    override val value: Value
) : JhFilterTransformation(), Filter.Rays {

    override fun createFilter(): JhFilter = RaysFilter().apply {
        opacity = value.opacity
        threshold = value.threshold
        strength = value.strength
    }

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.rays, R.string.filter_help_rays)

        override val parameters = structuredParameters {
            Value::opacity.float(R.string.opacity, 0.75f, 0f..1f)
            Value::threshold.float(R.string.threshold, 0.25f, 0f..1f)
            Value::strength.float(R.string.strength, 1f, 0f..5f)
        }
    }
}