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
import com.jhlabs.WeaveFilter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.Weave.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.JhFilterTransformation

@FilterDefinition(group = FilterGroups.PIXELATION)
internal class WeaveFilter(
    override val value: Value
) : JhFilterTransformation(), Filter.Weave {

    override fun createFilter(): JhFilter = WeaveFilter().apply {
        xWidth = value.xWidth
        yWidth = value.yWdth
        xGap = value.xGap
        yGap = value.yGap
    }

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.weave, R.string.filter_help_weave)

        override val parameters = structuredParameters {
            Value::xWidth.float(R.string.x_width, 16f, 0f..100f)
            Value::yWdth.float(R.string.y_wdth, 16f, 0f..100f)
            Value::xGap.float(R.string.x_gap, 6f, 0f..100f)
            Value::yGap.float(R.string.y_gap, 6f, 0f..100f)
        }
    }
}