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
import com.t8rin.imagetoolbox.core.filters.domain.model.params.BloomParams
import com.t8rin.imagetoolbox.core.resources.R

class UiBloomFilter(
    override val value: BloomParams = BloomParams.Default
) : UiFilter<BloomParams>(
    title = R.string.bloom,
    paramsInfo = listOf(
        FilterParam(R.string.threshold, 0f..1f),
        FilterParam(R.string.strength, 0f..3f),
        FilterParam(R.string.radius, 1f..100f, roundTo = 0),
        FilterParam(R.string.soft_knee, 0f..1f),
        FilterParam(R.string.exposure, 0f..1f),
        FilterParam(R.string.gamma, 0f..2f)
    ),
    value = value
), Filter.Bloom