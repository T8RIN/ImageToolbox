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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.utils.roundTo
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GmicFilterParams
import com.t8rin.imagetoolbox.core.filters.presentation.model.GmicParameterInfo
import com.t8rin.imagetoolbox.core.filters.presentation.model.GmicUiFilter
import com.t8rin.imagetoolbox.core.ui.widget.color_picker.ColorSelectionRowDefaults
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ColorRowSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.DataSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch

@Composable
internal fun GmicFilterParamsItem(
    value: GmicFilterParams,
    filter: GmicUiFilter,
    onFilterChange: (GmicFilterParams) -> Unit,
    previewOnly: Boolean
) {
    Column(
        modifier = Modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        filter.gmicParamsInfo.forEachIndexed { index, info ->
            val currentValue = value[index]

            when (info) {
                is GmicParameterInfo.Number -> EnhancedSliderItem(
                    value = currentValue.toFloat(),
                    title = stringResource(info.title),
                    valueRange = info.range,
                    internalStateTransformation = {
                        if (info.isInteger) it.toInt().toFloat() else it.roundTo(info.roundTo)
                    },
                    onValueChange = {
                        val updated = if (info.isInteger) it.toInt().toString() else it.toString()
                        onFilterChange(value.withValue(index, updated))
                    },
                    enabled = !previewOnly,
                    behaveAsContainer = false
                )

                is GmicParameterInfo.Toggle -> PreferenceRowSwitch(
                    title = stringResource(info.title),
                    checked = currentValue.toBoolean(),
                    onClick = {
                        onFilterChange(value.withValue(index, it.toString()))
                    },
                    modifier = Modifier.padding(
                        top = 8.dp,
                        start = 4.dp,
                        end = 4.dp
                    ),
                    enabled = !previewOnly,
                    applyHorizontalPadding = false,
                    startContent = {},
                    resultModifier = Modifier.padding(16.dp)
                )

                is GmicParameterInfo.Selection -> DataSelector(
                    value = currentValue,
                    onValueChange = {
                        onFilterChange(value.withValue(index, it))
                    },
                    entries = info.entries,
                    title = stringResource(info.title),
                    titleIcon = null,
                    itemContentText = { it.toReadableName() },
                    spanCount = 1,
                    containerColor = Color.Unspecified,
                    behaveAsContainer = false
                )

                is GmicParameterInfo.Color -> ColorRowSelector(
                    value = Color(currentValue.toInt()),
                    onValueChange = {
                        onFilterChange(value.withValue(index, it.toArgb().toString()))
                    },
                    title = stringResource(info.title),
                    allowScroll = !previewOnly,
                    icon = null,
                    defaultColors = ColorSelectionRowDefaults.colorList,
                    contentHorizontalPadding = 0.dp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}

private fun GmicFilterParams.withValue(index: Int, value: String): GmicFilterParams = copy(
    values = values.toMutableList().apply { this[index] = value }
)

private fun String.toReadableName(): String = replace(Regex("([a-z])([A-Z])"), "$1 $2")
