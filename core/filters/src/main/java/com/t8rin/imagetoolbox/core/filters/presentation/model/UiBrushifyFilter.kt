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

@UiFilterInject(group = UiFilterInject.Groups.EFFECTS)
class UiBrushifyFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf(
            "Ellipse",
            "0.25",
            "4",
            "64",
            "25",
            "12",
            "0",
            "2",
            "Full",
            "0.2",
            "0.5",
            "30",
            "1",
            "1",
            "1",
            "5",
            "0",
            "0.2"
        )
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_brushify,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Selection(
            R.string.shape,
            listOf(
                "Rectangle",
                "Diamond",
                "Pentagon",
                "Hexagon",
                "Octagon",
                "Ellipse",
                "Gaussian",
                "Star",
                "Heart"
            )
        ),
        GmicParameterInfo.Number(R.string.gmic_param_ratio, 0f..1f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_number_of_sizes, 1f..16f, isInteger = true),
        GmicParameterInfo.Number(R.string.gmic_param_maximal_size, 1f..128f, isInteger = true),
        GmicParameterInfo.Number(R.string.gmic_param_minimal_size, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.orientation, 1f..24f, isInteger = true),
        GmicParameterInfo.Number(R.string.fuzziness, 0f..10f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_smoothness, 0f..10f, isInteger = false),
        GmicParameterInfo.Selection(
            R.string.light,
            listOf("None", "Flat", "Darken", "Lighten", "Full")
        ),
        GmicParameterInfo.Number(R.string.strength, 0f..1f, isInteger = false),
        GmicParameterInfo.Number(R.string.opacity, 0f..1f, isInteger = false),
        GmicParameterInfo.Number(R.string.density, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_contour_coherence, 0f..1f, isInteger = false),
        GmicParameterInfo.Number(
            R.string.gmic_param_orientation_coherence,
            0f..1f,
            isInteger = false
        ),
        GmicParameterInfo.Number(
            R.string.gmic_param_gradient_smoothness,
            0f..10f,
            isInteger = false
        ),
        GmicParameterInfo.Number(
            R.string.gmic_param_structure_smoothness,
            0f..10f,
            isInteger = false
        ),
        GmicParameterInfo.Number(R.string.gmic_param_primary_angle, -180f..180f, isInteger = false),
        GmicParameterInfo.Number(R.string.dispersion, 0f..1f, isInteger = false)
    )
), Filter.Brushify
