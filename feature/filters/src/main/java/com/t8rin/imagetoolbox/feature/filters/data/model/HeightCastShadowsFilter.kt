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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.HeightCastShadows.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.trickle.Trickle

@FilterDefinition(group = FilterGroups.LIGHT)
internal class HeightCastShadowsFilter(
    override val value: Value
) : FilterTransformation, Filter.HeightCastShadows {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = Trickle.heightCastShadows(
        input = input,
        azimuth = value.azimuth,
        elevation = value.elevation,
        depth = value.depth,
        softness = value.softness,
        strength = value.strength
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.height_cast_shadows,
            R.string.filter_help_height_cast_shadows
        )

        override val parameters = structuredParameters {
            Value::azimuth.float(R.string.light_azimuth, 135f, 0f..360f)
            Value::elevation.float(R.string.light_elevation, 35f, 5f..85f)
            Value::depth.float(R.string.relief_depth, 0.35f, 0f..1f)
            Value::softness.float(R.string.softness, 0.15f, 0f..1f)
            Value::strength.float(R.string.strength, 1f, 0f..1f)
        }
    }
}