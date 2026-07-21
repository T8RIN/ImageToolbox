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
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GmicFilterParams
import com.t8rin.imagetoolbox.core.ksp.annotations.UiFilterInject
import com.t8rin.imagetoolbox.core.resources.R

@UiFilterInject(group = UiFilterInject.Groups.DISTORTION)
class UiSquareToCircleFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf(
            "SquareToCircle",
            "50",
            "50",
            "100",
            "0",
            "0",
            "Linear",
            "Transparent",
            "true"
        )
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_square_to_circle,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Selection(
            R.string.gmic_param_mode,
            listOf("SquareToCircle", "CircleToSquare")
        ),
        GmicParameterInfo.Number(R.string.center_x, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.center_y, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.strength, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.zoom, -100f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.angle, -180f..180f, isInteger = false),
        GmicParameterInfo.Selection(
            R.string.gmic_param_interpolation,
            listOf("NearestNeighbor", "Linear")
        ),
        GmicParameterInfo.Selection(
            R.string.gmic_param_boundary,
            listOf("Transparent", "Nearest", "Periodic", "Mirror")
        ),
        GmicParameterInfo.Toggle(R.string.gmic_param_adapt_to_image_ratio)
    )
), Filter.SquareToCircle
