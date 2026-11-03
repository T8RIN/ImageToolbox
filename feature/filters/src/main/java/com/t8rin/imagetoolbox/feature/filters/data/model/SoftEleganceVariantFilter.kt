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
import com.t8rin.imagetoolbox.core.domain.model.ImageModel
import com.t8rin.imagetoolbox.core.domain.transformation.ChainTransformation
import com.t8rin.imagetoolbox.core.domain.transformation.Transformation
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterValues
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.SoftEleganceVariant.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation

@FilterDefinition(group = FilterGroups.LUT)
internal class SoftEleganceVariantFilter(
    override val value: Value
) : ChainTransformation<Bitmap>, FilterTransformation, Filter.SoftEleganceVariant {

    override fun getTransformations(): List<Transformation<Bitmap>> = listOf(
        LUT512x512Filter(
            FilterValues.lut512x512(
                intensity = value.strength,
                image = ImageModel(R.drawable.lookup_soft_elegance_2)
            )
        )
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.soft_elegance_variant, R.string.filter_help_soft_elegance_variant)

        override val parameters = structuredParameters {
            Value::strength.float(R.string.strength, 1f, 0f..1f)
        }
    }
}