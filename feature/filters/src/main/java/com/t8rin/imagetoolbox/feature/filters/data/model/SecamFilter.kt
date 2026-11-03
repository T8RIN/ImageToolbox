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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.Secam.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.trickle.Trickle

@FilterDefinition(group = FilterGroups.GLITCH)
internal class SecamFilter(
    override val value: Value
) : FilterTransformation, Filter.Secam {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = Trickle.secam(
        input = input,
        chromaRadius = value.chromaRadius,
        frequencyDeviation = value.frequencyDeviation,
        noise = value.noise,
        preEmphasis = value.preEmphasis,
        lineCrosstalk = value.lineCrosstalk,
        seed = value.seed,
        strength = value.strength
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.secam, R.string.filter_help_secam)

        override val parameters = structuredParameters {
            Value::chromaRadius.int(R.string.chroma_radius, 4, 1..16)
            Value::frequencyDeviation.float(R.string.frequency_deviation, 0.8f, 0.2f..1f)
            Value::noise.float(R.string.noise, 0.12f, 0f..0.65f)
            Value::preEmphasis.float(R.string.pre_emphasis, 0.55f, 0f..1f)
            Value::lineCrosstalk.float(R.string.line_crosstalk, 0.07f, 0f..0.5f)
            Value::seed.seed(R.string.seed, 0, 0..10000)
            Value::strength.float(R.string.strength, 1f, 0f..1f)
        }
    }
}