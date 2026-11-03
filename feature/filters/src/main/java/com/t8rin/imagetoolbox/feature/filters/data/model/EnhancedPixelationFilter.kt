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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.EnhancedPixelation.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.imagetoolbox.feature.filters.data.utils.pixelation.PixelationTool

@FilterDefinition(group = FilterGroups.PIXELATION)
internal class EnhancedPixelationFilter(
    override val value: Value,
) : FilterTransformation, Filter.EnhancedPixelation {
    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = PixelationTool.pixelate(
        input = input,
        layers = { enhancedSquare(value.layers) }
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.enhanced_pixelation, R.string.filter_help_enhanced_pixelation)

        override val parameters = structuredParameters {
            Value::layers.float(default = 48f, range = 10f..200f)
        }
    }
}