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
import com.t8rin.imagetoolbox.core.filters.domain.model.params.KaleidoscopeParams
import com.t8rin.imagetoolbox.core.resources.R

class UiKaleidoscopeFilter(
    override val value: KaleidoscopeParams = KaleidoscopeParams.Default
) : UiFilter<KaleidoscopeParams>(
    title = R.string.kaleidoscope,
    paramsInfo = listOf(
        FilterParam(R.string.angle, 0f..360f, 0),
        FilterParam(R.string.secondary_angle, 0f..360f, 0),
        FilterParam(R.string.center_x, 0f..1f, 2),
        FilterParam(R.string.center_y, 0f..1f, 2),
        FilterParam(R.string.sides, 2f..12f, 0),
        FilterParam(R.string.radius, 0f..100f, 2),
    ),
    value = value
), Filter.Kaleidoscope