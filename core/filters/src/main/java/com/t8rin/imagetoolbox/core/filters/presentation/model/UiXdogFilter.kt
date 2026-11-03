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
import com.t8rin.imagetoolbox.core.filters.domain.model.params.XdogParams
import com.t8rin.imagetoolbox.core.ksp.annotations.UiFilterInject
import com.t8rin.imagetoolbox.core.resources.R

@UiFilterInject(group = UiFilterInject.Groups.ARTISTIC)
class UiXdogFilter(
    override val value: XdogParams = XdogParams.Default
) : UiFilter<XdogParams>(
    title = R.string.xdog,
    paramsInfo = listOf(
        FilterParam(R.string.sigma, 0.5f..12f, roundTo = 1),
        FilterParam(R.string.gmic_param_ratio, 1.1f..4f),
        FilterParam(R.string.weight, 1f..30f, roundTo = 1),
        FilterParam(R.string.threshold, -0.5f..1f),
        FilterParam(R.string.tag_sharpness, 1f..60f, roundTo = 1),
        FilterParam(R.string.ink_color, 0f..0f),
        FilterParam(R.string.texture_paper_color, 0f..0f),
        FilterParam(R.string.strength, 0f..1f)
    ),
    value = value
), Filter.Xdog