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
import com.awxkee.aire.Scalar
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.utils.NEAREST_ODD_ROUNDING
import com.t8rin.imagetoolbox.core.domain.utils.roundTo
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.MotionBlur.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.BlurEdgeMode
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.imagetoolbox.feature.filters.data.utils.toEdgeMode

@FilterDefinition(group = FilterGroups.BLUR)
internal class MotionBlurFilter(
    override val value: Value,
) : FilterTransformation, Filter.MotionBlur {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize,
    ): Bitmap = Aire.motionBlur(
        bitmap = input,
        kernelSize = value.kernelSize.toFloat().roundTo(NEAREST_ODD_ROUNDING).toInt(),
        angle = value.angle,
        borderMode = value.edgeMode.toEdgeMode(),
        borderScalar = Scalar.ZEROS
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.motion_blur, R.string.filter_help_motion_blur)

        override val parameters = structuredParameters {
            Value::kernelSize.int(R.string.just_size, 25, 0..201, roundTo = NEAREST_ODD_ROUNDING)
            Value::angle.float(R.string.angle, 45f, 0f..360f)
            Value::edgeMode.enum(
                R.string.edge_mode,
                label = { it.label }) { BlurEdgeMode.Reflect101 }
        }
    }
}