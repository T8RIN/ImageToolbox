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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.PalComposite.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.trickle.Trickle

@FilterDefinition(group = FilterGroups.GLITCH)
internal class PalCompositeFilter(
    override val value: Value
) : FilterTransformation, Filter.PalComposite {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = Trickle.palComposite(
        input = input,
        lumaFilter = value.lumaFilter,
        chromaRadius = value.chromaRadius,
        noise = value.noise,
        phaseError = value.phaseError,
        scanSamples = value.scanSamples,
        lineCompensation = value.lineCompensation,
        seed = value.seed,
        strength = value.strength
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.pal_composite, R.string.filter_help_pal_composite)

        override val parameters = structuredParameters {
            Value::lumaFilter.float(R.string.luminance_filtering, 0.88f, 0f..1f)
            Value::chromaRadius.int(R.string.chroma_radius, 2, 1..6)
            Value::noise.float(R.string.noise, 0.035f, 0f..0.3f, roundTo = 3)
            Value::phaseError.int(R.string.phase_error, 12, -45..45)
            Value::scanSamples.int(R.string.samples_per_line, 720, 160..960)
            Value::lineCompensation.float(R.string.line_compensation, 0.7f, 0f..1f)
            Value::seed.seed(R.string.seed, 0, 0..10000)
            Value::strength.float(R.string.strength, 1f, 0f..1f)
        }
    }
}