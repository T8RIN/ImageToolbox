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
class UiTaquinFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf("7", "7", "None", "50", "5", "0", "-16777216", "0")
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_taquin,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Number(R.string.gmic_param_x_tiles, 1f..20f, isInteger = true),
        GmicParameterInfo.Number(R.string.gmic_param_y_tiles, 1f..20f, isInteger = true),
        GmicParameterInfo.Selection(
            R.string.gmic_param_removed_tile,
            listOf("None", "First", "Last", "Random")
        ),
        GmicParameterInfo.Number(R.string.gmic_param_relief, 0f..255f, isInteger = false),
        GmicParameterInfo.Number(R.string.border_thickness, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_border_outline, 0f..16f, isInteger = true),
        GmicParameterInfo.Color(R.string.outline_color),
        GmicParameterInfo.Number(R.string.seed, 0f..65535f, isInteger = true)
    )
), Filter.Taquin
