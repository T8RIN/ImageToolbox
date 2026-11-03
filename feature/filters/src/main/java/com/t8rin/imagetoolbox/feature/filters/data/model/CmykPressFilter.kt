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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.CmykPress.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.trickle.Trickle

@FilterDefinition(group = FilterGroups.ARTISTIC)
internal class CmykPressFilter(
    override val value: Value
) : FilterTransformation, Filter.CmykPress {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = Trickle.cmykPress(
        input = input,
        spacing = value.spacing,
        blackGeneration = value.blackGeneration,
        dotGain = value.dotGain,
        misregistration = value.misregistration,
        inkLimit = value.inkLimit,
        paperGrain = value.paperGrain,
        seed = value.seed,
        strength = value.strength
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.cmyk_press, R.string.filter_help_cmyk_press)

        override val parameters = structuredParameters<Value> {
            field(Value::spacing, intParam(R.string.spacing, 8, 2..30))
            field(Value::blackGeneration, floatParam(R.string.black_generation, 0.68f, 0f..1f))
            field(Value::dotGain, floatParam(R.string.dot_gain, 0.22f, 0f..1f))
            field(
                Value::misregistration,
                floatParam(R.string.misregistration, 1.2f, 0f..18f, roundTo = 1)
            )
            field(Value::inkLimit, floatParam(R.string.ink_limit, 2.6f, 0.75f..4f))
            field(Value::paperGrain, floatParam(R.string.paper_grain, 0.4f, 0f..1f))
            field(Value::seed, intParam(R.string.seed, 0, 0..10000))
            field(Value::strength, floatParam(R.string.strength, 1f, 0f..1f))
        }
    }
}