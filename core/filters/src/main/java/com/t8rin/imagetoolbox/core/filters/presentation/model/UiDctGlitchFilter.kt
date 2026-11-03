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

package com.t8rin.imagetoolbox.core.filters.presentation.model

import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterParam
import com.t8rin.imagetoolbox.core.filters.domain.model.params.DctGlitchParams
import com.t8rin.imagetoolbox.core.ksp.annotations.UiFilterInject
import com.t8rin.imagetoolbox.core.resources.R

@UiFilterInject(group = UiFilterInject.Groups.GLITCH)
class UiDctGlitchFilter(
    override val value: DctGlitchParams = DctGlitchParams.Default
) : UiFilter<DctGlitchParams>(
    title = R.string.dct_glitch,
    paramsInfo = listOf(
        FilterParam(
            title = R.string.block_size,
            valueRange = 0f..1f,
            roundTo = 0,
            options = listOf(
                R.string.dct_block_8_8,
                R.string.dct_block_16_16
            )
        ),
        FilterParam(R.string.min_frequency, 0f..1f),
        FilterParam(R.string.max_frequency, 0f..1f),
        FilterParam(
            title = R.string.operation,
            valueRange = 0f..2f,
            roundTo = 0,
            options = listOf(
                R.string.zero_ac,
                R.string.flip_sign,
                R.string.chroma_distortion
            )
        ),
        FilterParam(R.string.probability, 0f..1f),
        FilterParam(R.string.seed, 0f..10000f, roundTo = 0),
        FilterParam(R.string.strength, 0f..1f)
    ),
    value = value
), Filter.DctGlitch