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

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.core.filters.domain.model.Filter
import ru.tech.imageresizershrinker.core.filters.domain.model.FilterParam
import ru.tech.imageresizershrinker.core.resources.R

class UiBokehFilter(
    override val value: Triple<Int, Int, Int> = Triple(15, 45, 6)
) : UiFilter<Triple<Int, Int, Int>>(
    title = R.string.bokeh,
    value = value,
    paramsInfo = listOf(
        FilterParam(
            title = R.string.radius,
            valueRange = 0f..50f,
            roundTo = 0
        ),
        FilterParam(
            title = R.string.angle,
            valueRange = 0f..360f,
            roundTo = 0
        ),
        FilterParam(
            title = R.string.amount,
            valueRange = 0f..40f,
            roundTo = 0
        )
    )
), Filter.Bokeh<Bitmap>