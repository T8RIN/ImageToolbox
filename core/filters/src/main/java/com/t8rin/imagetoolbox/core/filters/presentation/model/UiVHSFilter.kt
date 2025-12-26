/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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
import com.t8rin.imagetoolbox.core.resources.R
import kotlin.math.PI

class UiVHSFilter(
    override val value: Pair<Float, Float> = 2f to 3f,
) : UiFilter<Pair<Float, Float>>(
    title = R.string.vhs,
    value = value,
    paramsInfo = listOf(
        FilterParam(
            title = R.string.seed,
            valueRange = 0f..PI.toFloat(),
            roundTo = 3
        ),
        FilterParam(
            title = R.string.strength,
            valueRange = 0f..10f,
            roundTo = 3
        ),
    )
), Filter.VHS