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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.SierraDithering.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.trickle.DitheringType
import com.t8rin.trickle.Trickle

@FilterDefinition(group = FilterGroups.DITHERING)
internal class SierraDitheringFilter(
    override val value: Value,
) : FilterTransformation, Filter.SierraDithering {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = Trickle.dithering(
        input = input,
        type = DitheringType.Sierra,
        threshold = value.threshold.toInt(),
        isGrayScale = value.grayscale
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.sierra_dithering, R.string.filter_help_sierra_dithering)

        override val parameters = structuredParameters {
            Value::threshold.float(R.string.threshold, 200f, 1f..255f, roundTo = 0)
            Value::grayscale.boolean(R.string.gray_scale, false)
        }
    }
}