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

import com.t8rin.imagetoolbox.core.domain.utils.Quad
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterParam
import com.t8rin.imagetoolbox.core.ksp.annotations.UiFilterInject
import com.t8rin.imagetoolbox.core.resources.R

@UiFilterInject(group = UiFilterInject.Groups.ARTISTIC)
class UiAnisotropicKuwaharaFilter(
    override val value: Quad<Float, Float, Float, Float> = Quad(5.5f, 0.82f, 10f, 1f)
) : UiFilter<Quad<Float, Float, Float, Float>>(
    title = R.string.anisotropic_kuwahara,
    value = value,
    paramsInfo = listOf(
        FilterParam(R.string.radius, 1f..24f, roundTo = 1),
        FilterParam(R.string.anisotropy, 0f..1f),
        FilterParam(R.string.selectivity, 1f..24f, roundTo = 1),
        FilterParam(R.string.strength, 0f..1f)
    )
), Filter.AnisotropicKuwahara