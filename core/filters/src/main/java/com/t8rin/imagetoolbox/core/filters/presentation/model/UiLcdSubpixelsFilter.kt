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
import com.t8rin.imagetoolbox.core.filters.domain.model.params.LcdSubpixelsParams
import com.t8rin.imagetoolbox.core.ksp.annotations.UiFilterInject
import com.t8rin.imagetoolbox.core.resources.R

@UiFilterInject(group = UiFilterInject.Groups.PIXELATION)
class UiLcdSubpixelsFilter(
    override val value: LcdSubpixelsParams = LcdSubpixelsParams.Default
) : UiFilter<LcdSubpixelsParams>(
    title = R.string.lcd_subpixels,
    paramsInfo = listOf(
        FilterParam(R.string.just_size, 3f..36f, roundTo = 0),
        FilterParam(R.string.black_matrix, 0.02f..0.6f),
        FilterParam(
            title = R.string.subpixel_order,
            valueRange = 0f..1f,
            roundTo = 0,
            options = listOf(
                R.string.rgb,
                R.string.bgr
            )
        ),
        FilterParam(R.string.viewing_angle, -70f..70f, roundTo = 0),
        FilterParam(R.string.strength, 0f..1f)
    ),
    value = value
), Filter.LcdSubpixels