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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.DctGlitch.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.opencv_tools.photo_filters.SpectralFilters

@FilterDefinition(group = FilterGroups.GLITCH)
internal class DctGlitchFilter(
    override val value: Value
) : FilterTransformation, Filter.DctGlitch {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = SpectralFilters.dctGlitch(
        bitmap = input,
        blockSize = value.blockSize,
        minimumFrequency = value.minimumFrequency,
        maximumFrequency = value.maximumFrequency,
        operation = value.operation,
        probability = value.probability,
        seed = value.seed,
        strength = value.strength
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.dct_glitch, R.string.filter_help_dct_glitch)

        override val parameters = structuredParameters<Value> {
            field(
                Value::blockSize,
                choiceParam(
                    R.string.block_size, 1,
                    R.string.dct_block_8_8,
                    R.string.dct_block_16_16
                )
            )
            field(Value::minimumFrequency, floatParam(R.string.min_frequency, 0.07f, 0f..1f))
            field(Value::maximumFrequency, floatParam(R.string.max_frequency, 0.55f, 0f..1f))
            field(
                Value::operation,
                choiceParam(
                    R.string.operation, 2,
                    R.string.zero_ac,
                    R.string.flip_sign,
                    R.string.chroma_distortion
                )
            )
            field(Value::probability, floatParam(R.string.probability, 0.58f, 0f..1f))
            field(Value::seed, intParam(R.string.seed, 0, 0..10000))
            field(Value::strength, floatParam(R.string.strength, 1f, 0f..1f))
        }
    }
}