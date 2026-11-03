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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.CapillaryInkBleed.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.opencv_tools.photo_filters.ArtisticEffects

@FilterDefinition(group = FilterGroups.ARTISTIC)
internal class CapillaryInkBleedFilter(
    override val value: Value
) : FilterTransformation, Filter.CapillaryInkBleed {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = ArtisticEffects.capillaryInkBleed(
        bitmap = input,
        wetness = value.wetness,
        direction = value.direction,
        spread = value.spread,
        roughness = value.roughness,
        steps = value.steps,
        seed = value.seed,
        strength = value.strength
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.capillary_ink_bleed, R.string.filter_help_capillary_ink_bleed)

        override val parameters = structuredParameters {
            Value::wetness.float(R.string.wetness, 0.77f, 0f..1f)
            Value::direction.float(R.string.fiber_direction, 40f, 0f..180f, roundTo = 1)
            Value::spread.float(R.string.ink_spread, 4f, 1f..16f, roundTo = 1)
            Value::roughness.float(R.string.texture_roughness, 0.65f, 0f..1f)
            Value::steps.int(R.string.steps, 5, 1..12)
            Value::seed.seed(R.string.seed, 0, 0..10000)
            Value::strength.float(R.string.strength, 1f, 0f..1f)
        }
    }
}