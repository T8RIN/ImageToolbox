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

import ru.tech.imageresizershrinker.core.filters.domain.model.Filter
import ru.tech.imageresizershrinker.core.filters.domain.model.FilterParam
import ru.tech.imageresizershrinker.core.resources.R

class UiGrayscaleFilter(
    override val value: Triple<Float, Float, Float> = Triple(0.299f, 0.587f, 0.114f)
) : UiFilter<Triple<Float, Float, Float>>(
    title = R.string.gray_scale,
    value = value,
    paramsInfo = listOf(
        FilterParam(
            title = R.string.color_red,
            valueRange = 0f..1f
        ),
        FilterParam(
            title = R.string.color_green,
            valueRange = 0f..1f
        ),
        FilterParam(
            title = R.string.color_blue,
            valueRange = 0f..1f
        )
    )
), Filter.Grayscale