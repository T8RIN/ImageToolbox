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
import com.t8rin.imagetoolbox.core.filters.domain.model.params.VoronoiCrystallizeParams
import com.t8rin.imagetoolbox.core.resources.R

class UiVoronoiCrystallizeFilter(
    override val value: VoronoiCrystallizeParams = VoronoiCrystallizeParams.Default
) : UiFilter<VoronoiCrystallizeParams>(
    title = R.string.voronoi_crystallize,
    paramsInfo = listOf(
        FilterParam(R.string.border_thickness, 0f..5f, 2),
        FilterParam(R.string.scale, 1f..300f, 2),
        FilterParam(R.string.randomness, 0f..10f, 2),
        FilterParam(R.string.shape, 0f..4f, 0),
        FilterParam(R.string.turbulence, 0f..1f, 2),
        FilterParam(R.string.angle, 0f..360f, 0),
        FilterParam(R.string.stretch, 1f..6f, 2),
        FilterParam(R.string.amount, 0f..1f, 2),
        FilterParam(R.string.border_color, 0f..0f, 0),
    ),
    value = value
), Filter.VoronoiCrystallize