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
import com.t8rin.imagetoolbox.core.filters.domain.model.params.ContourHatchingParams
import com.t8rin.imagetoolbox.core.ksp.annotations.UiFilterInject
import com.t8rin.imagetoolbox.core.resources.R

@UiFilterInject(group = UiFilterInject.Groups.ARTISTIC)
class UiContourHatchingFilter(
    override val value: ContourHatchingParams = ContourHatchingParams.Default
) : UiFilter<ContourHatchingParams>(
    title = R.string.contour_hatching,
    paramsInfo = listOf(
        FilterParam(R.string.spacing, 2f..32f, roundTo = 1),
        FilterParam(R.string.gmic_param_width, 0.1f..0.9f),
        FilterParam(R.string.angle, 0f..180f, roundTo = 1),
        FilterParam(R.string.contrast, 0.5f..3f),
        FilterParam(R.string.ink_color, 0f..0f),
        FilterParam(R.string.texture_paper_color, 0f..0f),
        FilterParam(R.string.strength, 0f..1f)
    ),
    value = value
), Filter.ContourHatching