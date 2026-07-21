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

@UiFilterInject(group = UiFilterInject.Groups.PIXELATION)
class UiShapeismFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf(
            "Circle",
            "7",
            "0.38",
            "0",
            "true",
            "5",
            "32",
            "8",
            "Any",
            "0",
            "5",
            "0.5",
            "1",
            "-16777216"
        )
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_shapeism,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Selection(
            R.string.shape,
            listOf(
                "Square",
                "Triangle",
                "Circle",
                "Diamond",
                "Hexagon",
                "Octagon",
                "Star",
                "Heart",
                "Custom"
            )
        ),
        GmicParameterInfo.Number(R.string.texture_branches, 3f..16f, isInteger = true),
        GmicParameterInfo.Number(R.string.texture_thickness, 0f..1f, isInteger = false),
        GmicParameterInfo.Number(R.string.angle, 0f..360f, isInteger = false),
        GmicParameterInfo.Toggle(R.string.antialias),
        GmicParameterInfo.Number(R.string.scale, 1f..16f, isInteger = true),
        GmicParameterInfo.Number(R.string.gmic_param_maximal_size, 1f..256f, isInteger = true),
        GmicParameterInfo.Number(R.string.gmic_param_minimal_size, 1f..256f, isInteger = true),
        GmicParameterInfo.Selection(
            R.string.gmic_param_allowed_angles,
            listOf("Zero", "ZeroOr180", "RightAngles", "Any")
        ),
        GmicParameterInfo.Number(R.string.spacing, -5f..5f, isInteger = true),
        GmicParameterInfo.Number(R.string.gmic_param_precision, 1f..10f, isInteger = true),
        GmicParameterInfo.Number(R.string.texture_edges, 0f..2f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_smoothness, 0f..10f, isInteger = false),
        GmicParameterInfo.Color(R.string.background_color)
    )
), Filter.Shapeism
