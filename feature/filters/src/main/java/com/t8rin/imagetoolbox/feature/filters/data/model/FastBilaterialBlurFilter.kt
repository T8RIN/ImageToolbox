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
import com.awxkee.aire.Aire
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.utils.NEAREST_ODD_ROUNDING
import com.t8rin.imagetoolbox.core.domain.utils.roundTo
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.FastBilaterialBlur.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation

@FilterDefinition(group = FilterGroups.BLUR)
internal class FastBilaterialBlurFilter(
    override val value: Value,
) : FilterTransformation, Filter.FastBilaterialBlur {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize,
    ): Bitmap = Aire.fastBilateralBlur(
        bitmap = input,
        spatialSigma = value.sigma,
        rangeSigma = value.spatialSigma,
        kernelSize = value.justSize.roundTo(NEAREST_ODD_ROUNDING).toInt()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.fast_bilaterial_blur, R.string.filter_help_fast_bilaterial_blur)

        override val parameters = structuredParameters {
            Value::justSize.float(R.string.just_size, 11f, 1f..200f, roundTo = NEAREST_ODD_ROUNDING)
            Value::sigma.float(R.string.sigma, 10f, 1f..100f, roundTo = 1)
            Value::spatialSigma.float(R.string.spatial_sigma, 3f, 1f..100f, roundTo = 1)
        }
    }
}