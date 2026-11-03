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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.DirectionalEmbroidery.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.opencv_tools.photo_filters.DrawingEffects

@FilterDefinition(group = FilterGroups.ARTISTIC)
internal class DirectionalEmbroideryFilter(
    override val value: Value
) : FilterTransformation, Filter.DirectionalEmbroidery {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = DrawingEffects.directionalEmbroidery(
        bitmap = input,
        stitchLength = value.stitchLength,
        density = value.density,
        thickness = value.thickness,
        relief = value.relief,
        colorLevels = value.colorLevels,
        seed = value.seed,
        strength = value.strength
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.directional_embroidery, R.string.filter_help_directional_embroidery)

        override val parameters = structuredParameters {
            Value::stitchLength.float(R.string.stitch_length, 9f, 3f..45f, roundTo = 1)
            Value::density.float(R.string.density, 0.8f, 0.1f..1f)
            Value::thickness.float(R.string.texture_thickness, 0.29f, 0.08f..0.55f)
            Value::relief.float(R.string.relief_strength, 0.62f, 0f..1f)
            Value::colorLevels.int(R.string.color_levels, 9, 2..16)
            Value::seed.seed(R.string.seed, 0, 0..10000)
            Value::strength.float(R.string.strength, 1f, 0f..1f)
        }
    }
}