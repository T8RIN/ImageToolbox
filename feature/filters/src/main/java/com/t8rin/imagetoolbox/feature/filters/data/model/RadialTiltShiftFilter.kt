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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.RadialTiltShift.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import kotlin.math.roundToInt

@FilterDefinition(group = FilterGroups.BLUR)
internal class RadialTiltShiftFilter(
    override val value: Value
) : FilterTransformation, Filter.RadialTiltShift {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = Aire.tiltShift(
        bitmap = input,
        radius = value.blurRadius.roundToInt(),
        sigma = value.sigma,
        anchorX = value.anchorX,
        anchorY = value.anchorY,
        tiltRadius = value.holeRadius
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.tilt_shift, R.string.filter_help_tilt_shift)

        override val parameters = structuredParameters {
            Value::blurRadius.float(R.string.blur_radius, 25f, 1f..100f, roundTo = 0)
            Value::sigma.float(R.string.sigma, 10f, 1f..50f)
            Value::anchorX.float(R.string.center_x, 0.5f, 0f..1f)
            Value::anchorY.float(R.string.center_y, 0.5f, 0f..1f)
            Value::holeRadius.float(R.string.radius, 0.2f, 0f..1f)
        }
    }
}