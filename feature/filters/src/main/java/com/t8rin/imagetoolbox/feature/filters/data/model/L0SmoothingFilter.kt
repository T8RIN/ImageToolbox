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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.L0Smoothing.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.opencv_tools.photo_filters.SpectralFilters
import kotlin.math.roundToInt

@FilterDefinition(group = FilterGroups.ENHANCEMENT)
internal class L0SmoothingFilter(
    override val value: Value
) : FilterTransformation, Filter.L0Smoothing {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = SpectralFilters.l0Smoothing(
        bitmap = input,
        lambda = value.lambda,
        penaltyGrowth = value.penaltyGrowth,
        iterations = value.iterations.roundToInt(),
        strength = value.strength
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.l0_smoothing, R.string.filter_help_l0_smoothing)

        override val parameters = structuredParameters {
            Value::lambda.float(R.string.lambda, 0.016f, 0.0001f..0.2f, roundTo = 4)
            Value::penaltyGrowth.float(R.string.penalty_growth, 2.2f, 1.3f..4f, roundTo = 1)
            Value::iterations.float(R.string.iterations, 9f, 1f..14f, roundTo = 0)
            Value::strength.float(R.string.strength, 1f, 0f..1f)
        }
    }
}