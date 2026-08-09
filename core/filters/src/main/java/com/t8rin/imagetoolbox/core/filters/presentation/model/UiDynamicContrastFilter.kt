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

@UiFilterInject(group = UiFilterInject.Groups.LIGHT)
class UiDynamicContrastFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf("12", "128", "220")
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_dynamic_contrast,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Number(R.string.amount, -64f..64f, isInteger = false),
        GmicParameterInfo.Number(R.string.threshold, 0f..255f, isInteger = false),
        GmicParameterInfo.Number(R.string.texture_intensity, 0f..255f, isInteger = false)
    )
), Filter.DynamicContrast
