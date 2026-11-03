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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.Autostereogram.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.trickle.Trickle

@FilterDefinition(group = FilterGroups.ARTISTIC)
internal class AutostereogramFilter(
    override val value: Value
) : FilterTransformation, Filter.Autostereogram {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = Trickle.autostereogram(
        input = input,
        repeatDistance = value.repeatDistance,
        depth = value.depth,
        gamma = value.gamma,
        size = value.size,
        softness = value.softness,
        invert = if (value.invert) 1 else 0,
        seed = value.seed
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.autostereogram, R.string.filter_help_autostereogram)

        override val parameters = structuredParameters {
            Value::repeatDistance.float(R.string.repeat_distance, 0.15f, 0.06f..0.3f)
            Value::depth.float(R.string.texture_depth, 0.34f, 0.02f..0.65f)
            Value::gamma.float(R.string.gamma, 1f, 0.4f..2.5f)
            Value::size.int(R.string.just_size, 2, 1..7)
            Value::softness.float(R.string.softness, 3f, 0f..16f, roundTo = 1)
            Value::invert.toggle(
                R.string.gmic_param_invert,
                false,
                R.string.normal,
                R.string.inverted
            )
            Value::seed.seed(R.string.seed, 0, 0..10000)
        }
    }
}