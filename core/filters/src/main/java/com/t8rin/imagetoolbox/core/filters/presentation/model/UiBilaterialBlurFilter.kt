/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterParam
import com.t8rin.imagetoolbox.core.filters.domain.model.params.BilaterialBlurParams
import com.t8rin.imagetoolbox.core.resources.R


class UiBilaterialBlurFilter(
    override val value: BilaterialBlurParams = BilaterialBlurParams.Default,
) : UiFilter<BilaterialBlurParams>(
    title = R.string.bilaterial_blur,
    value = value,
    paramsInfo = listOf(
        FilterParam(
            title = R.string.radius,
            valueRange = 1f..50f,
            roundTo = 0
        ),
        FilterParam(
            title = R.string.sigma,
            valueRange = 1f..100f,
            roundTo = 1
        ),
        FilterParam(
            title = R.string.spatial_sigma,
            valueRange = 1f..100f,
            roundTo = 1
        ),
        FilterParam(
            title = R.string.edge_mode,
            valueRange = 0f..0f,
            roundTo = 0
        ),
    )
), Filter.BilaterialBlur