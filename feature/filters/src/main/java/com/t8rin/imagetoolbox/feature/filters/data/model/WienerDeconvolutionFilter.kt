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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.WienerDeconvolution.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.opencv_tools.photo_filters.SpectralFilters

@FilterDefinition(group = FilterGroups.ENHANCEMENT)
internal class WienerDeconvolutionFilter(
    override val value: Value
) : FilterTransformation, Filter.WienerDeconvolution {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = SpectralFilters.wienerDeconvolution(
        bitmap = input,
        psf = value.psf,
        radius = value.radius,
        angle = value.angle,
        regularization = value.regularization,
        strength = value.strength
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.wiener_deconvolution, R.string.filter_help_wiener_deconvolution)

        override val parameters = structuredParameters {
            Value::psf.choice(
                R.string.blur_shape, 0,
                R.string.motion_blur,
                R.string.defocus,
                R.string.gaussian
            )
            Value::radius.float(R.string.radius, 5f, 1f..35f, roundTo = 1)
            Value::angle.float(R.string.angle, 0f, -90f..90f, roundTo = 1)
            Value::regularization.float(
                R.string.regularization,
                0.026f,
                0.0001f..0.25f,
                roundTo = 4
            )
            Value::strength.float(R.string.strength, 0.8f, 0f..1f)
        }
    }
}