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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.AnisotropicDiffusion.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import kotlin.math.roundToInt

@FilterDefinition(group = FilterGroups.ENHANCEMENT)
internal class AnisotropicDiffusionFilter(
    override val value: Value
) : FilterTransformation, Filter.AnisotropicDiffusion {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = Aire.anisotropicDiffusion(
        bitmap = input,
        numOfSteps = value.repeatCount.roundToInt(),
        conduction = value.conduction,
        diffusion = value.diffusion
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.anisotropic_diffusion, R.string.filter_help_anisotropic_diffusion)

        override val parameters = structuredParameters {
            Value::repeatCount.float(R.string.repeat_count, 20f, 1f..100f, roundTo = 0)
            Value::conduction.float(R.string.conduction, 0.6f, 0.1f..1f)
            Value::diffusion.float(R.string.diffusion, 0.5f, 0.01f..1f)
        }
    }
}