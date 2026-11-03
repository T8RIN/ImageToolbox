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
import com.t8rin.imagetoolbox.core.filters.domain.model.params.RfReceptionParams
import com.t8rin.imagetoolbox.core.ksp.annotations.UiFilterInject
import com.t8rin.imagetoolbox.core.resources.R

@UiFilterInject(group = UiFilterInject.Groups.GLITCH)
class UiRfReceptionFilter(
    override val value: RfReceptionParams = RfReceptionParams.Default
) : UiFilter<RfReceptionParams>(
    title = R.string.rf_reception,
    paramsInfo = listOf(
        FilterParam(R.string.delay, 1f..85f, roundTo = 0),
        FilterParam(R.string.reflection, 0f..0.9f),
        FilterParam(R.string.noise, 0f..0.4f),
        FilterParam(R.string.frequency, 1f..120f, roundTo = 0),
        FilterParam(R.string.seed, 0f..10000f, roundTo = 0),
        FilterParam(R.string.strength, 0f..1f)
    ),
    value = value
), Filter.RfReception