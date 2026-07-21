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
class UiCubismFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf("2", "50", "10", "90", "0.7", "0")
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_cubism,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Number(R.string.iterations, 0f..10f, isInteger = true),
        GmicParameterInfo.Number(R.string.density, 0f..200f, isInteger = false),
        GmicParameterInfo.Number(R.string.texture_thickness, 0f..50f, isInteger = false),
        GmicParameterInfo.Number(R.string.angle, 0f..360f, isInteger = false),
        GmicParameterInfo.Number(R.string.opacity, 0.01f..1f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_smoothness, 0f..5f, isInteger = false)
    )
), Filter.Cubism
