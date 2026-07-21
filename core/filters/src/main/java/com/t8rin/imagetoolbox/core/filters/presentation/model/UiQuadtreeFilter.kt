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
class UiQuadtreeFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf("Squares", "1024", "0.5", "0", "3", "1.5", "1", "true")
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_quadtree,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Selection(
            R.string.gmic_param_mode,
            listOf("Squares", "Sierpinski", "EllipsePainting")
        ),
        GmicParameterInfo.Number(R.string.gmic_param_precision, 2f..4096f, isInteger = true),
        GmicParameterInfo.Number(R.string.gmic_param_homogeneity, 0f..2f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_outline, 0f..4f, isInteger = true),
        GmicParameterInfo.Number(R.string.gmic_param_primary_radius, 0f..5f, isInteger = false),
        GmicParameterInfo.Number(R.string.second_radius, 0f..5f, isInteger = false),
        GmicParameterInfo.Number(R.string.texture_anisotropy, 0f..4f, isInteger = false),
        GmicParameterInfo.Toggle(R.string.gmic_param_only_leafs)
    )
), Filter.Quadtree
