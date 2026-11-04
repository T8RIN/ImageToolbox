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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.CeramicGlaze.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.trickle.Trickle

@FilterDefinition(group = FilterGroups.PIXELATION)
internal class CeramicGlazeFilter(
    override val value: Value
) : FilterTransformation, Filter.CeramicGlaze {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = Trickle.ceramicGlaze(
        input = input,
        size = value.size,
        pooling = value.pooling,
        gloss = value.gloss,
        crackle = value.crackle,
        seed = value.seed,
        strength = value.strength
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.ceramic_glaze,
            R.string.filter_help_ceramic_glaze
        )

        override val parameters = structuredParameters {
            Value::size.float(R.string.cell_size, 32f, 12f..96f)
            Value::pooling.float(R.string.pooling, 0.55f, 0f..1f)
            Value::gloss.float(R.string.gloss, 0.7f, 0f..1f)
            Value::crackle.float(R.string.crackle, 0.5f, 0f..1f)
            Value::seed.seed(R.string.seed, 0, 0..10000)
            Value::strength.float(R.string.strength, 1f, 0f..1f)
        }
    }
}