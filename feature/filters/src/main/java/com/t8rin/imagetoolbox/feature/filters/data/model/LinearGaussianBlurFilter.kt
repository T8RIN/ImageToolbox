/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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
import com.t8rin.imagetoolbox.core.domain.transformation.Transformation
import com.t8rin.imagetoolbox.core.domain.utils.NEAREST_ODD_ROUNDING
import com.t8rin.imagetoolbox.core.domain.utils.roundTo
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.params.LinearGaussianParams
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterInject
import com.t8rin.imagetoolbox.feature.filters.data.utils.toEdgeMode
import com.t8rin.imagetoolbox.feature.filters.data.utils.toFunc

@FilterInject
internal class LinearGaussianBlurFilter(
    override val value: LinearGaussianParams = LinearGaussianParams.Default
) : Transformation<Bitmap>, Filter.LinearGaussianBlur {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = Aire.linearGaussianBlur(
        bitmap = input,
        verticalKernelSize = value.kernelSize.toFloat().roundTo(NEAREST_ODD_ROUNDING).toInt(),
        horizontalKernelSize = value.kernelSize.toFloat().roundTo(NEAREST_ODD_ROUNDING).toInt(),
        verticalSigma = value.sigma,
        horizontalSigma = value.sigma,
        edgeMode = value.edgeMode.toEdgeMode(),
        transferFunction = value.transferFunction.toFunc()
    )

}