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

@UiFilterInject(group = UiFilterInject.Groups.ENHANCEMENT)
class UiDcpDehazeFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf("5", "1", "0.2", "1", "0", "0", "0", "false")
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_dcp_dehaze,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Number(R.string.scale, 1f..20f, isInteger = true),
        GmicParameterInfo.Number(R.string.strength, 0f..2f, isInteger = false),
        GmicParameterInfo.Number(R.string.min, 0f..1f, isInteger = false),
        GmicParameterInfo.Number(R.string.max, 0f..1f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_brightness, -100f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.contrast, -100f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.gamma, -100f..100f, isInteger = false),
        GmicParameterInfo.Toggle(R.string.gmic_param_transmittance_map)
    )
), Filter.DcpDehaze
