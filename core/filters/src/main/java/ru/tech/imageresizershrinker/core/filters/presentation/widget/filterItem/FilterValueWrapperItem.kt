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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.domain.model.ColorModel
import ru.tech.imageresizershrinker.core.filters.domain.model.FilterValueWrapper
import ru.tech.imageresizershrinker.core.filters.domain.model.wrap
import ru.tech.imageresizershrinker.core.filters.presentation.model.UiColorFilter
import ru.tech.imageresizershrinker.core.filters.presentation.model.UiFilter
import ru.tech.imageresizershrinker.core.filters.presentation.model.UiRGBFilter
import ru.tech.imageresizershrinker.core.ui.utils.helper.toColor
import ru.tech.imageresizershrinker.core.ui.utils.helper.toModel
import ru.tech.imageresizershrinker.core.ui.widget.color_picker.ColorSelectionRow
import ru.tech.imageresizershrinker.core.ui.widget.color_picker.ColorSelectionRowDefaults

@Composable
internal fun FilterValueWrapperItem(
    value: FilterValueWrapper<*>,
    filter: UiFilter<FilterValueWrapper<*>>,
    onFilterChange: (value: FilterValueWrapper<*>) -> Unit,
    previewOnly: Boolean
) {
    when (val wrapped = value.wrapped) {
        is ColorModel -> {
            Box(
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp
                )
            ) {
                ColorSelectionRow(
                    value = remember(wrapped) {
                        wrapped.toColor()
                    },
                    defaultColors = remember(filter) {
                        derivedStateOf {
                            ColorSelectionRowDefaults.colorList.map {
                                if (filter is UiColorFilter) it.copy(0.5f)
                                else it
                            }
                        }
                    }.value,
                    allowAlpha = filter !is UiRGBFilter,
                    allowScroll = !previewOnly,
                    onValueChange = {
                        onFilterChange(it.toModel().wrap())
                    }
                )
            }
        }
    }
}