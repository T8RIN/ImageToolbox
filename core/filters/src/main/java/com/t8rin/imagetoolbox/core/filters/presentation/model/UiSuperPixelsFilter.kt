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
class UiSuperPixelsFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf("16", "10", "5", "Average", "1", "1", "-16777216")
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_super_pixels,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Number(R.string.just_size, 4f..64f, isInteger = true),
        GmicParameterInfo.Number(R.string.gmic_param_regularity, 0f..128f, isInteger = false),
        GmicParameterInfo.Number(R.string.iterations, 1f..16f, isInteger = true),
        GmicParameterInfo.Selection(R.string.color, listOf("Random", "Average")),
        GmicParameterInfo.Number(R.string.gmic_param_border_opacity, 0f..1f, isInteger = false),
        GmicParameterInfo.Number(R.string.border_thickness, 1f..16f, isInteger = true),
        GmicParameterInfo.Color(R.string.border_color)
    )
), Filter.SuperPixels
