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

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.filters.presentation.model.UiFilter
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceRowSwitch

@Composable
internal fun BooleanItem(
    value: Boolean,
    filter: UiFilter<Boolean>,
    onFilterChange: (value: Boolean) -> Unit,
    previewOnly: Boolean
) {
    filter.paramsInfo[0].takeIf { it.title != null }
        ?.let { (title, _, _) ->
            PreferenceRowSwitch(
                title = stringResource(id = title!!),
                checked = value,
                onClick = {
                    onFilterChange(value)
                },
                modifier = Modifier.padding(
                    top = 16.dp,
                    start = 12.dp,
                    end = 12.dp,
                    bottom = 12.dp
                ),
                applyHorizontalPadding = false,
                startContent = {},
                resultModifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                ),
                enabled = !previewOnly
            )
        }
}