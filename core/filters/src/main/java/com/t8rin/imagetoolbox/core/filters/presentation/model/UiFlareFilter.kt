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
import com.t8rin.imagetoolbox.core.filters.domain.model.params.FlareParams
import com.t8rin.imagetoolbox.core.ksp.annotations.UiFilterInject
import com.t8rin.imagetoolbox.core.resources.R

@UiFilterInject(group = UiFilterInject.Groups.LIGHT)
class UiFlareFilter(
    override val value: FlareParams = FlareParams.Default
) : UiFilter<FlareParams>(
    title = R.string.flare,
    paramsInfo = listOf(
        R.string.radius paramTo 0.01f..1f,
        R.string.base_amount paramTo 0f..2f,
        R.string.ring_amount paramTo 0f..2f,
        R.string.ray_amount paramTo 0f..2f,
        R.string.ring_width paramTo 0.01f..5f,
        R.string.center_x paramTo 0f..1f,
        R.string.center_y paramTo 0f..1f,
        R.string.color paramTo 0f..0f
    ),
    value = value
), Filter.Flare
