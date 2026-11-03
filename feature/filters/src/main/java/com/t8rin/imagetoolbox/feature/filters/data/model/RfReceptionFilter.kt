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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.RfReception.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.trickle.Trickle

@FilterDefinition(group = FilterGroups.GLITCH)
internal class RfReceptionFilter(
    override val value: Value
) : FilterTransformation, Filter.RfReception {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = Trickle.rfReception(
        input = input,
        delay = value.delay,
        reflection = value.reflection,
        noise = value.noise,
        frequency = value.frequency,
        seed = value.seed,
        strength = value.strength
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.rf_reception, R.string.filter_help_rf_reception)

        override val parameters = structuredParameters<Value> {
            field(Value::delay, intParam(R.string.delay, 19, 1..85))
            field(Value::reflection, floatParam(R.string.reflection, 0.42f, 0f..0.9f))
            field(Value::noise, floatParam(R.string.noise, 0.09f, 0f..0.4f))
            field(Value::frequency, intParam(R.string.frequency, 21, 1..120))
            field(Value::seed, intParam(R.string.seed, 0, 0..10000))
            field(Value::strength, floatParam(R.string.strength, 1f, 0f..1f))
        }
    }
}