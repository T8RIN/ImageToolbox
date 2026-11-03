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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.Bloom.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.trickle.Trickle

@FilterDefinition(group = FilterGroups.LIGHT)
internal class BloomFilter(
    override val value: Value
) : FilterTransformation, Filter.Bloom {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = Trickle.bloom(
        src = input,
        threshold = value.threshold,
        intensity = value.intensity,
        radius = value.radius,
        softKnee = value.softKnee,
        exposure = value.exposure,
        gamma = value.gamma
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.bloom, R.string.filter_help_bloom)

        override val parameters = structuredParameters {
            Value::threshold.float(R.string.threshold, 0.6f, 0f..1f)
            Value::intensity.float(R.string.strength, 1.5f, 0f..3f)
            Value::radius.int(R.string.radius, 25, 1..100)
            Value::softKnee.float(R.string.soft_knee, 0.5f, 0f..1f)
            Value::exposure.float(R.string.exposure, 0.02f, 0f..1f)
            Value::gamma.float(R.string.gamma, 1.1f, 0f..2f)
        }
    }
}