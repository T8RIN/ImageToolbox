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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.WaterEffect.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation

@FilterDefinition(group = FilterGroups.DISTORTION)
internal class WaterEffectFilter(
    override val value: Value
) : FilterTransformation, Filter.WaterEffect {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = Aire.waterEffect(
        bitmap = input,
        fractionSize = 0.2f * value.fractionSize,
        frequencyX = value.frequencyX,
        frequencyY = value.frequencyY,
        amplitudeX = value.amplitudeX,
        amplitudeY = value.amplitudeY
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.water_effect, R.string.filter_help_water_effect)

        override val parameters = structuredParameters {
            Value::fractionSize.float(R.string.just_size, 0.2f, 0f..1f)
            Value::frequencyX.float(R.string.frequency_x, 2f, -4f..4f)
            Value::frequencyY.float(R.string.frequency_y, 2f, -4f..4f)
            Value::amplitudeX.float(R.string.amplitude_x, 0.5f, -4f..4f)
            Value::amplitudeY.float(R.string.amplitude_y, 0.5f, -4f..4f)
        }
    }
}