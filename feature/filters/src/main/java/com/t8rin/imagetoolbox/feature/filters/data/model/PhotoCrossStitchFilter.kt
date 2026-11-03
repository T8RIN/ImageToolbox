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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.PhotoCrossStitch.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.trickle.Trickle

@FilterDefinition(group = FilterGroups.ARTISTIC)
internal class PhotoCrossStitchFilter(
    override val value: Value
) : FilterTransformation, Filter.PhotoCrossStitch {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = Trickle.photoCrossStitch(
        input = input,
        size = value.size,
        threadColors = value.threadColors,
        threadWidth = value.threadWidth,
        lightAngle = value.lightAngle,
        relief = value.relief,
        seed = value.seed,
        strength = value.strength
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.photo_cross_stitch, R.string.filter_help_photo_cross_stitch)

        override val parameters = structuredParameters<Value> {
            field(Value::size, intParam(R.string.just_size, 11, 4..38))
            field(Value::threadColors, intParam(R.string.thread_colors, 24, 2..64))
            field(Value::threadWidth, floatParam(R.string.thread_width, 0.12f, 0.05f..0.25f))
            field(Value::lightAngle, intParam(R.string.light_angle, 35, 0..360))
            field(Value::relief, floatParam(R.string.relief_strength, 0.7f, 0f..1f))
            field(Value::seed, intParam(R.string.seed, 0, 0..10000))
            field(Value::strength, floatParam(R.string.strength, 1f, 0f..1f))
        }
    }
}