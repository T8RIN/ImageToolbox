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
import com.awxkee.aire.EdgeMode
import com.awxkee.aire.MorphKernels
import com.awxkee.aire.MorphOp
import com.awxkee.aire.MorphOpMode
import com.awxkee.aire.Scalar
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.Dilation.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.trickle.TrickleUtils.checkHasAlpha

@FilterDefinition(group = FilterGroups.EFFECTS)
internal class DilationFilter(
    override val value: Value
) : FilterTransformation, Filter.Dilation {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = Aire.morphology(
        bitmap = input,
        kernel = if (value.useCircleKernel) {
            MorphKernels.circle(value.justSize.toInt())
        } else {
            MorphKernels.box(value.justSize.toInt())
        },
        morphOp = MorphOp.DILATE,
        morphOpMode = if (input.checkHasAlpha()) MorphOpMode.RGBA
        else MorphOpMode.RGB,
        borderMode = EdgeMode.REFLECT_101,
        kernelHeight = value.justSize.toInt(),
        kernelWidth = value.justSize.toInt(),
        borderScalar = Scalar.ZEROS
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.dilation, R.string.filter_help_dilation)

        override val parameters = structuredParameters {
            Value::justSize.float(R.string.just_size, 25f, 1f..150f, roundTo = 0)
            Value::useCircleKernel.boolean(R.string.use_circle_kernel, true)
        }
    }
}