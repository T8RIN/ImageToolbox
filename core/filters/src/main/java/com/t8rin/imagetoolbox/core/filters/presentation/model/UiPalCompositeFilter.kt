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
import com.t8rin.imagetoolbox.core.filters.domain.model.params.PalCompositeParams
import com.t8rin.imagetoolbox.core.ksp.annotations.UiFilterInject
import com.t8rin.imagetoolbox.core.resources.R

@UiFilterInject(group = UiFilterInject.Groups.GLITCH)
class UiPalCompositeFilter(
    override val value: PalCompositeParams = PalCompositeParams.Default
) : UiFilter<PalCompositeParams>(
    title = R.string.pal_composite,
    paramsInfo = listOf(
        FilterParam(R.string.luminance_filtering, 0f..1f),
        FilterParam(R.string.chroma_radius, 1f..6f, roundTo = 0),
        FilterParam(R.string.noise, 0f..0.3f, roundTo = 3),
        FilterParam(R.string.phase_error, -45f..45f, roundTo = 0),
        FilterParam(R.string.samples_per_line, 160f..960f, roundTo = 0),
        FilterParam(R.string.line_compensation, 0f..1f),
        FilterParam(R.string.seed, 0f..10000f, roundTo = 0),
        FilterParam(R.string.strength, 0f..1f)
    ),
    value = value
), Filter.PalComposite