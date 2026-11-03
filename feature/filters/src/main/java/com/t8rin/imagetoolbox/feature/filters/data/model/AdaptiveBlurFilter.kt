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
import com.jhlabs.SmartBlurFilter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.AdaptiveBlur.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.JhFilterTransformation
import kotlin.math.roundToInt

@FilterDefinition(group = FilterGroups.BLUR)
internal class AdaptiveBlurFilter(
    override val value: Value
) : JhFilterTransformation(), Filter.AdaptiveBlur {

    override fun createFilter(): JhFilter = SmartBlurFilter().apply {
        radius = value.radius.roundToInt()
        threshold = value.threshold.roundToInt()
    }

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.adaptive_blur, R.string.filter_help_adaptive_blur)

        override val parameters = structuredParameters {
            Value::radius.float(R.string.radius, 15f, 1f..100f, roundTo = 0)
            Value::threshold.float(R.string.threshold, 35f, 0f..255f, roundTo = 0)
        }
    }
}