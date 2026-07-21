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
class UiDrosteFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf(
            "20",
            "20",
            "80",
            "20",
            "80",
            "80",
            "20",
            "80",
            "1",
            "0",
            "0",
            "0",
            "1",
            "None",
            "Nearest",
            "Replace"
        )
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_droste,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Number(R.string.gmic_param_upper_left_x, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_upper_left_y, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_upper_right_x, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_upper_right_y, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_lower_right_x, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_lower_right_y, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_lower_left_x, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_lower_left_y, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.iterations, 1f..10f, isInteger = true),
        GmicParameterInfo.Number(R.string.gmic_param_x_shift, -100f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_y_shift, -100f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.angle, 0f..360f, isInteger = false),
        GmicParameterInfo.Number(R.string.zoom, 0.1f..5f, isInteger = false),
        GmicParameterInfo.Selection(
            R.string.tile_mode_mirror,
            listOf("None", "XAxis", "YAxis", "BothAxes")
        ),
        GmicParameterInfo.Selection(
            R.string.gmic_param_boundary,
            listOf("Transparent", "Nearest", "Periodic", "Mirror")
        ),
        GmicParameterInfo.Selection(
            R.string.draw_mode,
            listOf("Replace", "ReplaceSharpest", "Behind", "Below")
        )
    )
), Filter.Droste
