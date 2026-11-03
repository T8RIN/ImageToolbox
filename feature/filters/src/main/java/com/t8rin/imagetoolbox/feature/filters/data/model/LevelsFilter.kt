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
import com.jhlabs.LevelsFilter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.Levels.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.JhFilterTransformation

@FilterDefinition(group = FilterGroups.LIGHT)
internal class LevelsFilter(
    override val value: Value
) : JhFilterTransformation(), Filter.Levels {

    override fun createFilter(): JhFilter = LevelsFilter().apply {
        lowLevel = value.lowInput
        highLevel = value.highInput
        lowOutputLevel = value.lowOutput
        highOutputLevel = value.highOutput
    }

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.levels, R.string.filter_help_levels)

        override val parameters = structuredParameters {
            Value::lowInput.float(R.string.low_input, 0f, 0f..1f)
            Value::highInput.float(R.string.high_input, 1f, 0f..1f)
            Value::lowOutput.float(R.string.low_output, 0f, 0f..1f)
            Value::highOutput.float(R.string.high_output, 1f, 0f..1f)
        }
    }
}