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
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GuidedSmoothingParams
import com.t8rin.imagetoolbox.core.ksp.annotations.UiFilterInject
import com.t8rin.imagetoolbox.core.resources.R

@UiFilterInject(group = UiFilterInject.Groups.ENHANCEMENT)
class UiGuidedSmoothingFilter(
    override val value: GuidedSmoothingParams = GuidedSmoothingParams.Default
) : UiFilter<GuidedSmoothingParams>(
    title = R.string.guided_smoothing,
    paramsInfo = listOf(
        FilterParam(R.string.radius, 1f..80f, roundTo = 1),
        FilterParam(R.string.epsilon, 0.001f..0.2f, roundTo = 3),
        FilterParam(
            title = R.string.guide,
            valueRange = 0f..1f,
            roundTo = 0,
            options = listOf(
                R.string.luminance,
                R.string.per_channel
            )
        ),
        FilterParam(R.string.strength, 0f..1f)
    ),
    value = value
), Filter.GuidedSmoothing