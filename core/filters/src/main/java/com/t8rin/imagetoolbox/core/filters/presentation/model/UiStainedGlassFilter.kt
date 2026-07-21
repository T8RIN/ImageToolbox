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
class UiStainedGlassFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf("40", "0.1", "false", "true", "1", "0", "0", "0")
    )
) : GmicUiFilter(
    title = R.string.texture_stained_glass,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Number(R.string.texture_edges, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.texture_shading, 0f..0.5f, isInteger = false),
        GmicParameterInfo.Toggle(R.string.gmic_param_thin_separators),
        GmicParameterInfo.Toggle(R.string.equalize),
        GmicParameterInfo.Number(R.string.color, 0f..3f, isInteger = false),
        GmicParameterInfo.Number(R.string.brightness, -100f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.contrast, -100f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.gamma, -100f..100f, isInteger = false)
    )
), Filter.StainedGlass
