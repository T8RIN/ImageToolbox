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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.AgxToneMapping.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.trickle.Trickle

@FilterDefinition(group = FilterGroups.LIGHT)
internal class AgxToneMappingFilter(
    override val value: Value
) : FilterTransformation, Filter.AgxToneMapping {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = Trickle.agxToneMapping(
        input = input,
        exposure = value.exposure,
        look = value.look,
        saturation = value.saturation,
        strength = value.strength
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.agx_tone_mapping, R.string.filter_help_agx_tone_mapping)

        override val parameters = structuredParameters {
            Value::exposure.float(R.string.exposure, 0f, -4f..4f)
            Value::look.choice(
                R.string.look, 0,
                R.string.neutral,
                R.string.golden,
                R.string.punchy
            )
            Value::saturation.float(R.string.saturation, 1f, 0f..1.75f)
            Value::strength.float(R.string.strength, 1f, 0f..1f)
        }
    }
}