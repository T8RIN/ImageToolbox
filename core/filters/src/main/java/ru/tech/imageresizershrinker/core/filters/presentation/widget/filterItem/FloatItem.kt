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

package ru.tech.imageresizershrinker.core.filters.presentation.widget.filterItem

import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.domain.utils.roundTo
import ru.tech.imageresizershrinker.core.filters.presentation.model.UiFilter
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedSlider

@Composable
internal fun FloatItem(
    value: Float,
    filter: UiFilter<Float>,
    onFilterChange: (value: Float) -> Unit,
    previewOnly: Boolean
) {
    EnhancedSlider(
        modifier = Modifier
            .padding(top = 16.dp, start = 12.dp, end = 12.dp, bottom = 8.dp)
            .offset(y = (-2).dp),
        enabled = !previewOnly,
        value = value,
        onValueChange = {
            onFilterChange(it.roundTo(filter.paramsInfo.first().roundTo))
        },
        valueRange = filter.paramsInfo.first().valueRange
    )
}