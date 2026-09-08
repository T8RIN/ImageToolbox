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

package com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.utils.roundTo
import com.t8rin.imagetoolbox.core.filters.domain.model.params.ProceduralParams
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiProceduralFilter
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.color_picker.ColorSelectionRowDefaults
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ColorRowSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem

@Composable
internal fun ProceduralParamsItem(
    value: ProceduralParams,
    filter: UiProceduralFilter,
    onFilterChange: (ProceduralParams) -> Unit,
    previewOnly: Boolean
) {
    val values = filter.effect.resolve(value)

    Column(modifier = Modifier.padding(8.dp)) {
        filter.effect.colors.forEach { param ->
            ColorRowSelector(
                title = stringResource(R.string.color),
                value = Color(filter.effect.resolveColors(value).getValue(param.name)),
                onValueChange = {
                    onFilterChange(value.copy(colors = value.colors + (param.name to it.toArgb())))
                },
                allowScroll = !previewOnly,
                icon = null,
                defaultColors = ColorSelectionRowDefaults.colorList,
                contentHorizontalPadding = 0.dp,
                allowAlpha = true
            )
        }
        filter.effect.parameters.forEachIndexed { index, param ->
            val (title, range, decimals) = filter.paramsInfo[index]
            EnhancedSliderItem(
                enabled = !previewOnly,
                value = values.getValue(param.name),
                title = stringResource(title!!),
                valueRange = range,
                onValueChange = {
                    onFilterChange(
                        value.copy(values = values + (param.name to it.roundTo(decimals)))
                    )
                },
                internalStateTransformation = { it.roundTo(decimals) },
                behaveAsContainer = false
            )
        }
    }
}