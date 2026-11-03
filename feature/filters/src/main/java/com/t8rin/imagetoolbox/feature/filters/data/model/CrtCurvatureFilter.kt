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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.CrtCurvature.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.trickle.Trickle

@FilterDefinition(group = FilterGroups.GLITCH)
internal class CrtCurvatureFilter(
    override val value: Value,
) : FilterTransformation, Filter.CrtCurvature {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = Trickle.crtCurvature(
        src = input,
        curvature = value.curvature,
        vignette = value.vignette,
        chroma = value.chroma
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.crt_curvature, R.string.filter_help_crt_curvature)

        override val parameters = structuredParameters {
            Value::curvature.float(R.string.curvature, 0.25f, -1f..1f, roundTo = 3)
            Value::vignette.float(R.string.vignette, 0.65f, 0f..1f, roundTo = 3)
            Value::chroma.float(R.string.chroma, 0.015f, 0f..1f, roundTo = 3)
        }
    }
}