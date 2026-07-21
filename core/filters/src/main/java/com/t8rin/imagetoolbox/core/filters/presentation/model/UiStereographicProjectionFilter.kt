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
class UiStereographicProjectionFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf("Direct", "50", "50", "50", "75", "0", "0", "0", "None", "Transparent")
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_stereographic_projection,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Selection(R.string.gmic_param_transform, listOf("Direct", "Inverse")),
        GmicParameterInfo.Number(R.string.center_x, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.center_y, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_radius_point_x, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_radius_point_y, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(
            R.string.gmic_param_horizon_leveling,
            -10f..10f,
            isInteger = false
        ),
        GmicParameterInfo.Number(R.string.gmic_param_side_blur, 0f..20f, isInteger = false),
        GmicParameterInfo.Number(R.string.dilation, -2f..2f, isInteger = false),
        GmicParameterInfo.Selection(
            R.string.tile_mode_mirror,
            listOf("None", "XAxis", "YAxis", "BothAxes")
        ),
        GmicParameterInfo.Selection(
            R.string.gmic_param_boundary,
            listOf("Transparent", "Nearest", "Periodic", "Mirror")
        )
    )
), Filter.StereographicProjection
