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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.SstvReception.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.trickle.Trickle

@FilterDefinition(group = FilterGroups.GLITCH)
internal class SstvReceptionFilter(
    override val value: Value
) : FilterTransformation, Filter.SstvReception {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = Trickle.sstvReception(
        input = input,
        transmissionMode = value.transmissionMode,
        frequencyDrift = value.frequencyDrift,
        syncError = value.syncError,
        noise = value.noise,
        dropout = value.dropout,
        seed = value.seed,
        strength = value.strength
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.sstv_reception, R.string.filter_help_sstv_reception)

        override val parameters = structuredParameters {
            Value::transmissionMode.choice(
                R.string.transmission_mode, 0,
                R.string.sstv_martin,
                R.string.sstv_scottie
            )
            Value::frequencyDrift.float(R.string.frequency_drift, 0.06f, 0f..0.25f, roundTo = 3)
            Value::syncError.float(R.string.sync_error, 0.014f, 0f..0.08f, roundTo = 3)
            Value::noise.float(R.string.noise, 0.08f, 0f..0.4f)
            Value::dropout.float(R.string.dropped_lines, 0.035f, 0f..0.2f, roundTo = 3)
            Value::seed.seed(R.string.seed, 0, 0..10000)
            Value::strength.float(R.string.strength, 1f, 0f..1f)
        }
    }
}