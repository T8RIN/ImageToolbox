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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.FlowTexture.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.trickle.Trickle

@FilterDefinition(group = FilterGroups.ARTISTIC)
internal class FlowTextureFilter(
    override val value: Value
) : FilterTransformation, Filter.FlowTexture {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = Trickle.flowTexture(
        input = input,
        length = value.length,
        detail = value.detail,
        contrast = value.contrast,
        seed = value.seed,
        strength = value.strength
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.flow_texture,
            R.string.filter_help_flow_texture
        )

        override val parameters = structuredParameters {
            Value::length.float(R.string.flow_length, 0.035f, 0.005f..0.1f, roundTo = 3)
            Value::detail.float(R.string.detail, 1.4f, 0.3f..3f)
            Value::contrast.float(R.string.contrast, 0.7f, 0f..2f)
            Value::seed.seed(R.string.seed, 0, 0..10000)
            Value::strength.float(R.string.strength, 1f, 0f..1f)
        }
    }
}