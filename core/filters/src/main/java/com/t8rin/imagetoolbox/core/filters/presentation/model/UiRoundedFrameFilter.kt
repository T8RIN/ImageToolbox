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
class UiRoundedFrameFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf("10", "10", "20", "0.1", "-16777216", "true")
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_rounded_frame,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Number(R.string.gmic_param_x_size, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_y_size, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.radius, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_smoothness, 0f..15f, isInteger = false),
        GmicParameterInfo.Color(R.string.color),
        GmicParameterInfo.Toggle(R.string.antialias)
    )
), Filter.RoundedFrame
