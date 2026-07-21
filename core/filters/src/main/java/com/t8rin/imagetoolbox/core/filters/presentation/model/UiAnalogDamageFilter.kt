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
class UiAnalogDamageFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf("18", "true", "3", "1", "0.6", "38")
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_analog_damage,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Number(R.string.texture_dirt, 0f..100f, isInteger = false),
        GmicParameterInfo.Toggle(R.string.gmic_param_monochrome_dirt),
        GmicParameterInfo.Number(R.string.gmic_param_aberration_x, -32f..32f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_aberration_y, -32f..32f, isInteger = false),
        GmicParameterInfo.Number(R.string.softness, 0f..10f, isInteger = false),
        GmicParameterInfo.Number(R.string.quality, 1f..100f, isInteger = true)
    )
), Filter.AnalogDamage
