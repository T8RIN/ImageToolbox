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
class UiFadedArrayFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf("2", "2", "0", "0", "80", "90", "None", "Shrink")
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_faded_array,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Number(R.string.gmic_param_x_tiles, 1f..10f, isInteger = true),
        GmicParameterInfo.Number(R.string.gmic_param_y_tiles, 1f..10f, isInteger = true),
        GmicParameterInfo.Number(R.string.gmic_param_x_offset, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_y_offset, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_fade_start, 1f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_fade_end, 1f..100f, isInteger = false),
        GmicParameterInfo.Selection(
            R.string.tile_mode_mirror,
            listOf("None", "XAxis", "YAxis", "BothAxes")
        ),
        GmicParameterInfo.Selection(
            R.string.gmic_param_sizing,
            listOf("Shrink", "Expand", "Repeat")
        )
    )
), Filter.FadedArray
