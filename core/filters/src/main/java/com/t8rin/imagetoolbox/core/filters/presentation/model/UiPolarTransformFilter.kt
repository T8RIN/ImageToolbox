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
class UiPolarTransformFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf("CustomTransform", "50", "50", "r + R/10*cos(a*5)", "a", "Mirror")
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_polar_transform,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Selection(
            R.string.gmic_param_preset,
            listOf("CustomTransform", "InverseRadius", "SwapRadiusAngle")
        ),
        GmicParameterInfo.Number(R.string.center_x, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.center_y, 0f..100f, isInteger = false),
        GmicParameterInfo.Selection(
            R.string.gmic_param_radius_expression,
            listOf("r + R/10*cos(a*5)", "r", "R-r", "a*R/(2*pi)")
        ),
        GmicParameterInfo.Selection(
            R.string.gmic_param_angle_expression,
            listOf("a", "-a", "r*2*pi/R", "a + r/R")
        ),
        GmicParameterInfo.Selection(
            R.string.gmic_param_boundary,
            listOf("Transparent", "Nearest", "Periodic", "Mirror")
        )
    )
), Filter.PolarTransform
