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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.LinearFastGaussianBlurNext.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.BlurEdgeMode
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.TransferFunc
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.imagetoolbox.feature.filters.data.utils.toEdgeMode
import com.t8rin.imagetoolbox.feature.filters.data.utils.toFunc

@FilterDefinition(group = FilterGroups.BLUR)
internal class LinearFastGaussianBlurNextFilter(
    override val value: Value
) : FilterTransformation, Filter.LinearFastGaussianBlurNext {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = Aire.linearFastGaussianNext(
        bitmap = input,
        verticalRadius = value.radius,
        horizontalRadius = value.radius,
        transferFunction = value.transferFunction.toFunc(),
        edgeMode = value.edgeMode.toEdgeMode()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.linear_fast_gaussian_blur_next,
            R.string.filter_help_linear_fast_gaussian_blur_next
        )

        override val parameters = structuredParameters {
            Value::radius.int(R.string.radius, 10, 1..300)
            Value::transferFunction.enum(
                R.string.tag_transfer_function,
                label = { it.label }) { TransferFunc.SRGB }
            Value::edgeMode.enum(
                R.string.edge_mode,
                label = { it.label }) { BlurEdgeMode.Reflect101 }
        }
    }
}