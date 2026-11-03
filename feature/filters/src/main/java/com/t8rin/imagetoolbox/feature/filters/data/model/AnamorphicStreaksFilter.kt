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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.AnamorphicStreaks.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.opencv_tools.photo_filters.OpticalEffects

@FilterDefinition(group = FilterGroups.LIGHT)
internal class AnamorphicStreaksFilter(
    override val value: Value
) : FilterTransformation, Filter.AnamorphicStreaks {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = OpticalEffects.anamorphicStreaks(
        bitmap = input,
        threshold = value.threshold,
        angle = value.angle,
        length = value.length,
        width = value.width,
        falloff = value.falloff,
        intensity = value.intensity
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.anamorphic_streaks, R.string.filter_help_anamorphic_streaks)

        override val parameters = structuredParameters {
            Value::threshold.float(R.string.threshold, 0.58f, 0.08f..1f)
            Value::angle.float(R.string.angle, 0f, -90f..90f, roundTo = 1)
            Value::length.float(R.string.gmic_param_length, 85f, 2f..180f, roundTo = 1)
            Value::width.float(R.string.gmic_param_width, 1.4f, 0.5f..16f, roundTo = 1)
            Value::falloff.float(R.string.falloff, 2.4f, 0.5f..8f, roundTo = 1)
            Value::intensity.float(R.string.texture_intensity, 2.4f, 0f..5f)
        }
    }
}