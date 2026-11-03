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
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterParam
import com.t8rin.imagetoolbox.core.filters.domain.model.params.DiffractionPsfParams
import com.t8rin.imagetoolbox.core.ksp.annotations.UiFilterInject
import com.t8rin.imagetoolbox.core.resources.R

@UiFilterInject(group = UiFilterInject.Groups.LIGHT)
class UiDiffractionPsfFilter(
    override val value: DiffractionPsfParams = DiffractionPsfParams.Default
) : UiFilter<DiffractionPsfParams>(
    title = R.string.diffraction_psf,
    paramsInfo = listOf(
        FilterParam(R.string.procedural_blades, 3f..12f, roundTo = 0),
        FilterParam(R.string.rotation, 0f..180f, roundTo = 1),
        FilterParam(R.string.radius, 3f..65f, roundTo = 1),
        FilterParam(R.string.spectral_spread, 0f..1f),
        FilterParam(R.string.threshold, 0.08f..1f),
        FilterParam(R.string.texture_intensity, 0f..5f)
    ),
    value = value
), Filter.DiffractionPsf