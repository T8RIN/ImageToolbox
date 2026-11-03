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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.ShuffleBlur.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.trickle.Trickle
import kotlin.math.roundToInt

@FilterDefinition(group = FilterGroups.BLUR)
internal class ShuffleBlurFilter(
    override val value: Value
) : FilterTransformation, Filter.ShuffleBlur {

    private val radiusMapping = listOf(0f, RAD_1) + List(200) {
        RAD_2 + STEP * it
    }

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = Trickle.shuffleBlur(
        input = input,
        threshold = value.threshold,
        strength = radiusMapping.getOrNull(value.radius.roundToInt()) ?: 0f
    )

    companion object : Filter.Schema.Structured<Value> {
        private const val RAD_1 = 0.001f
        private const val RAD_2 = 0.005f
        private const val STEP = 0.005f

        override val info = FilterInfo(R.string.shuffle_blur, R.string.filter_help_shuffle_blur)

        override val parameters = structuredParameters {
            Value::radius.float(R.string.radius, 35f, 0f..70f, roundTo = 0)
            Value::threshold.float(R.string.threshold, 1f, -1f..1f)
        }
    }
}