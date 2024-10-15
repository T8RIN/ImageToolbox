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

package ru.tech.imageresizershrinker.core.filters.presentation.model

import ru.tech.imageresizershrinker.core.domain.utils.NEAREST_ODD_ROUNDING
import ru.tech.imageresizershrinker.core.filters.domain.model.Filter
import ru.tech.imageresizershrinker.core.filters.domain.model.FilterParam
import ru.tech.imageresizershrinker.core.resources.R

class UiFastBilaterialBlurFilter(
    override val value: Triple<Int, Float, Float> = Triple(11, 10f, 3f),
) : UiFilter<Triple<Int, Float, Float>>(
    title = R.string.fast_bilaterial_blur,
    value = value,
    paramsInfo = listOf(
        FilterParam(
            title = R.string.just_size,
            valueRange = 1f..200f,
            roundTo = NEAREST_ODD_ROUNDING
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
        )
    )
), Filter.FastBilaterialBlur