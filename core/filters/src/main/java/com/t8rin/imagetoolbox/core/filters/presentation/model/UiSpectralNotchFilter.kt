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

@UiFilterInject(group = UiFilterInject.Groups.ENHANCEMENT)
class UiSpectralNotchFilter(
    override val value: Quad<Float, Float, Float, Float> = Quad(0.12f, 0f, 0.018f, 1f)
) : UiFilter<Quad<Float, Float, Float, Float>>(
    title = R.string.spectral_notch,
    value = value,
    paramsInfo = listOf(
        FilterParam(R.string.frequency_x, -0.5f..0.5f, roundTo = 3),
        FilterParam(R.string.frequency_y, -0.5f..0.5f, roundTo = 3),
        FilterParam(R.string.bandwidth, 0.0005f..0.12f, roundTo = 4),
        FilterParam(R.string.strength, 0f..1f)
    )
), Filter.SpectralNotch