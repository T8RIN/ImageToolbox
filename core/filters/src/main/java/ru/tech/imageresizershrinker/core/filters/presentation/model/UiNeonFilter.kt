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

import androidx.compose.ui.graphics.Color
import ru.tech.imageresizershrinker.core.domain.model.ColorModel
import ru.tech.imageresizershrinker.core.filters.domain.model.Filter
import ru.tech.imageresizershrinker.core.filters.domain.model.FilterParam
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.utils.helper.toModel

class UiNeonFilter(
    override val value: Triple<Float, Float, ColorModel> = Triple(
        first = 1f,
        second = 0.26f,
        third = Color.Magenta.toModel()
    )
) : UiFilter<Triple<Float, Float, ColorModel>>(
    title = R.string.neon,
    value = value,
    paramsInfo = listOf(
        FilterParam(
            title = R.string.amount,
            valueRange = 1f..25f,
            roundTo = 0
        ),
        FilterParam(
            title = R.string.strength,
            valueRange = 0f..1f
        ),
        FilterParam(
            title = R.string.color,
            valueRange = 0f..0f
        )
    )
), Filter.Neon