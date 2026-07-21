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
class UiMinisteckFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf("8", "64", "8", "2", "100", "0.3", "false")
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_ministeck,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Number(R.string.color, 2f..24f, isInteger = true),
        GmicParameterInfo.Number(R.string.resolution, 16f..256f, isInteger = true),
        GmicParameterInfo.Number(R.string.gmic_param_piece_size, 1f..64f, isInteger = true),
        GmicParameterInfo.Number(R.string.gmic_param_piece_complexity, 1f..10f, isInteger = true),
        GmicParameterInfo.Number(R.string.amplitude, 0f..256f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_relief_size, 0f..1f, isInteger = false),
        GmicParameterInfo.Toggle(R.string.gmic_param_outline)
    )
), Filter.Ministeck
