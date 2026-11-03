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
import com.jhlabs.LensBlurFilter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.LensBlur.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.JhFilterTransformation
import kotlin.math.roundToInt

@FilterDefinition(group = FilterGroups.BLUR)
internal class LensBlurFilter(
    override val value: Value
) : JhFilterTransformation(), Filter.LensBlur {

    override fun createFilter(): JhFilter = LensBlurFilter().apply {
        radius = value.radius
        sides = value.sides.roundToInt()
        bloom = value.bloom
        bloomThreshold = value.threshold
    }

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.lens_blur, R.string.filter_help_lens_blur)

        override val parameters = structuredParameters {
            Value::radius.float(R.string.radius, 10f, 0f..50f, roundTo = 0)
            Value::sides.float(R.string.sides, 5f, 3f..12f, roundTo = 0)
            Value::bloom.float(R.string.bloom, 2f, 0f..10f)
            Value::threshold.float(R.string.threshold, 255f, 0f..255f, roundTo = 0)
        }
    }
}