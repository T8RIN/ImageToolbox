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
import com.t8rin.imagetoolbox.core.filters.domain.model.params.AdaptiveBinarizationParams
import com.t8rin.imagetoolbox.core.ksp.annotations.UiFilterInject
import com.t8rin.imagetoolbox.core.resources.R

@UiFilterInject(group = UiFilterInject.Groups.ENHANCEMENT)
class UiAdaptiveBinarizationFilter(
    override val value: AdaptiveBinarizationParams = AdaptiveBinarizationParams.Default
) : UiFilter<AdaptiveBinarizationParams>(
    title = R.string.adaptive_binarization,
    paramsInfo = listOf(
        FilterParam(
            title = R.string.algorithm,
            valueRange = 0f..3f,
            roundTo = 0,
            options = listOf(
                R.string.sauvola,
                R.string.niblack,
                R.string.wolf,
                R.string.nick
            )
        ),
        FilterParam(R.string.radius, 1f..80f, roundTo = 1),
        FilterParam(R.string.threshold_coefficient, -1f..1f),
        FilterParam(R.string.dynamic_range, 0.02f..1f),
        FilterParam(R.string.ink_color, 0f..0f),
        FilterParam(R.string.texture_paper_color, 0f..0f),
        FilterParam(
            title = R.string.gmic_param_invert,
            valueRange = 0f..1f,
            roundTo = 0,
            options = listOf(
                R.string.normal,
                R.string.inverted
            )
        )
    ),
    value = value
), Filter.AdaptiveBinarization