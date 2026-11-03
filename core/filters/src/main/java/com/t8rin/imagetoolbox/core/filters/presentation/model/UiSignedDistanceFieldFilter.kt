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
import com.t8rin.imagetoolbox.core.filters.domain.model.params.SignedDistanceFieldParams
import com.t8rin.imagetoolbox.core.ksp.annotations.UiFilterInject
import com.t8rin.imagetoolbox.core.resources.R

@UiFilterInject(group = UiFilterInject.Groups.EFFECTS)
class UiSignedDistanceFieldFilter(
    override val value: SignedDistanceFieldParams = SignedDistanceFieldParams.Default
) : UiFilter<SignedDistanceFieldParams>(
    title = R.string.signed_distance_field,
    paramsInfo = listOf(
        FilterParam(
            title = R.string.mask_source,
            valueRange = 0f..1f,
            roundTo = 0,
            options = listOf(R.string.luminance, R.string.alpha)
        ),
        FilterParam(R.string.threshold, 0f..1f),
        FilterParam(R.string.distance, 1f..180f, roundTo = 1),
        FilterParam(
            title = R.string.gmic_param_mode,
            valueRange = 0f..2f,
            roundTo = 0,
            options = listOf(
                R.string.gradient,
                R.string.glow,
                R.string.isolines
            )
        ),
        FilterParam(R.string.spacing, 1f..60f, roundTo = 1),
        FilterParam(R.string.inside_color, 0f..0f),
        FilterParam(R.string.outside_color, 0f..0f),
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
), Filter.SignedDistanceField