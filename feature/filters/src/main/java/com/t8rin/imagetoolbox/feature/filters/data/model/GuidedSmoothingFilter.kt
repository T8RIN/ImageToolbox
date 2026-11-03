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

import android.graphics.Bitmap
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.GuidedSmoothing.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.opencv_tools.photo_filters.RestorationFilters

@FilterDefinition(group = FilterGroups.ENHANCEMENT)
internal class GuidedSmoothingFilter(
    override val value: Value
) : FilterTransformation, Filter.GuidedSmoothing {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = RestorationFilters.guidedFilter(
        bitmap = input,
        radius = value.radius,
        epsilon = value.epsilon,
        guide = value.guide,
        strength = value.strength
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.guided_smoothing, R.string.filter_help_guided_smoothing)

        override val parameters = structuredParameters {
            Value::radius.float(R.string.radius, 13f, 1f..80f, roundTo = 1)
            Value::epsilon.float(R.string.epsilon, 0.045f, 0.001f..0.2f, roundTo = 3)
            Value::guide.choice(
                R.string.guide, 0,
                R.string.luminance,
                R.string.per_channel
            )
            Value::strength.float(R.string.strength, 1f, 0f..1f)
        }
    }
}