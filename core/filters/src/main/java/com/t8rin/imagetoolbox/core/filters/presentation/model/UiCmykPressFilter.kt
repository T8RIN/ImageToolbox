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
import com.t8rin.imagetoolbox.core.filters.domain.model.params.CmykPressParams
import com.t8rin.imagetoolbox.core.ksp.annotations.UiFilterInject
import com.t8rin.imagetoolbox.core.resources.R

@UiFilterInject(group = UiFilterInject.Groups.ARTISTIC)
class UiCmykPressFilter(
    override val value: CmykPressParams = CmykPressParams.Default
) : UiFilter<CmykPressParams>(
    title = R.string.cmyk_press,
    paramsInfo = listOf(
        FilterParam(R.string.spacing, 2f..30f, roundTo = 0),
        FilterParam(R.string.black_generation, 0f..1f),
        FilterParam(R.string.dot_gain, 0f..1f),
        FilterParam(R.string.misregistration, 0f..18f, roundTo = 1),
        FilterParam(R.string.ink_limit, 0.75f..4f),
        FilterParam(R.string.paper_grain, 0f..1f),
        FilterParam(R.string.seed, 0f..10000f, roundTo = 0),
        FilterParam(R.string.strength, 0f..1f)
    ),
    value = value
), Filter.CmykPress